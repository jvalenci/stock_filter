package com.jvalenc.stock.filter;

import com.eclipsesource.json.JsonObject;
import com.jvalenc.stock.csv.reader.CsvReader;
import com.jvalenc.stock.csv.reader.ICsvReader;
import com.jvalenc.stock.models.QueryCriteria;
import com.jvalenc.stock.models.SMADataPoint;
import com.jvalenc.stock.models.StockSymbol;
import com.jvalenc.stock.util.enums.Interval;
import com.jvalenc.stock.util.enums.QueryFunction;
import com.jvalenc.stock.util.enums.SeriesType;
import com.jvalenc.stock.util.enums.TimePeriod;
import com.jvalenc.stock.web.rest.AlphaVantageWebClient;
import com.jvalenc.stock.web.rest.IWebClient;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

/**
 * Created by jonat on 11/12/2017.
 */
public class SimpleMovingAverageCrossover {
//todo 1. We could move the response to its own class as well as the email client. Seems to be working now.
    private static Logger LOG = Logger.getLogger(SimpleMovingAverageCrossover.class);
    private static Properties mailServerProperties;
    private static Session getMailSession;
    private static MimeMessage generateMailMessage;
    private static final String USERNAME = "jonathan.valencia716@gmail.com";
    private static final String PASSWORD = "9129669jvad";
    private static final List<String> subscribers = getSubscribers();

    private static List<String> getSubscribers(){
        Set<String> subscribers = new HashSet<>();
        File file = new File("C:\\Users\\jonat\\IdeaProjects\\stock-filter\\src\\main\\resources\\subscribers.csv");
        if(file.exists()){
            try(FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {

                String line;
                while((line =  bufferedReader.readLine()) != null){
                    subscribers.add(line.trim());
                }
            } catch (FileNotFoundException e){
                LOG.error("File " + file.getPath() + " not found", e);
            } catch (IOException e) {
                LOG.error(e);
            }

        }
        return Collections.unmodifiableList(Arrays.asList(subscribers.toArray(new String[]{})));
    }

    /**
     * For the purpose of simple moving average cross over we need two queries.
     * one for the 8 sma the other for 23 sma.
     * @param stockSymbol
     * @return List of query criteria to make the rest calls.
     */
    protected static List<QueryCriteria> queryBuilder(StockSymbol stockSymbol){

        List<QueryCriteria> queries = new ArrayList<>();

        QueryCriteria query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.EIGHT);
        query.setSeriesType(SeriesType.CLOSE);
        queries.add(query);

        query = new QueryCriteria();
        query.setSymbol(stockSymbol.getSymbol());
        query.setQueryFunction(QueryFunction.SMA);
        query.setInterval(Interval.DAILY);
        query.setTimePeriod(TimePeriod.TWENTY_THREE);
        query.setSeriesType(SeriesType.CLOSE);
        queries.add(query);

        return queries;
    }

    /**
     * @param csvReader
     * @param directoryFileName
     * @return
     */
    protected static List<StockSymbol> getStockSymbols(ICsvReader<StockSymbol> csvReader, String directoryFileName){
        return csvReader.readCsvDirectory(directoryFileName);
    }

    /**
     * @param webClient
     * @return response
     */
    protected static List<JsonObject> webService(IWebClient<JsonObject> webClient){
        //send the query to the web client
        try {
            webClient.sendRequest();
        }catch (Exception ex){
            LOG.error(ex);
        }
        return webClient.getResponses();
    }

    private static int getResponseValueSize(final JsonObject response){
        int size = 0;
        if(!response.isEmpty()) {
            JsonObject sizeObject = response.get("Technical Analysis: SMA").asObject();
            size = sizeObject.size();
        }
        return size;
    }

    /**Parsing the response. I'm only interested in the first
     * 5 days. that a potential crossover could have occurred.
     * @param response
     * @return Two data sets each containing 5 days worth of data points
     */
    protected static List< List<SMADataPoint> > parseResponse(List<JsonObject> response){

        LOG.info("parsing the Response \n" + response.toString());

        List< List<SMADataPoint> > dataPoints = new ArrayList<>();

        if(response.size() == 2 && response != null) {
            if( getResponseValueSize(response.get(0)) >= 5 && getResponseValueSize(response.get(1)) >= 5) {
                //parse the response
                response.forEach(
                        jsonObject -> {
                            List<SMADataPoint> data = new ArrayList<SMADataPoint>();
                            LOG.debug(jsonObject.get("Technical Analysis: SMA"));
                            JsonObject jsonTechAna = jsonObject.get("Technical Analysis: SMA").asObject();

                            for (int i = 0; i < 5; i++) {
                                SMADataPoint point = new SMADataPoint();
                                String timeStamp = jsonTechAna.names().get(i);
                                String sma = jsonTechAna.get(timeStamp).asObject().get("SMA").asString();
                                point.setTimeStamp(timeStamp);
                                point.setSimpleMovingAverage(Double.parseDouble(sma));
                                LOG.debug(timeStamp + " : " + sma);
                                data.add(point);
                            }
                            dataPoints.add(data);
                        }
                );
            }
        }
        return dataPoints;
    }

    /**Examine both data sets and try to find the intersection within 0.01
     * @param data
     * @return true if the data sets have an intersection: otherwise return false
     */
    protected static boolean analyseForIntersection(List< List<SMADataPoint> > data){
        LOG.info("Starting the analysis for both set of data");

        if(data != null && data.size() == 2 ) {
            if(data.get(0).size() > 0 && data.get(1).size() > 0) {
                List<SMADataPoint> firstDataPoints = data.get(0);
                List<SMADataPoint> secondDataPoints = data.get(1);
                final double THRESHOLD = 0.01;
                double difference;
                for (int i = 0; i < firstDataPoints.size(); i++) {
                    difference = Math.abs(firstDataPoints.get(i).getSimpleMovingAverage() - secondDataPoints.get(i).getSimpleMovingAverage());
                    if (difference <= THRESHOLD) {
                        LOG.info("Analysis has found an intersection.");
                        return true;
                    }
                }
            }
        }
        LOG.info("Analysis found no intersection.");
        return false;
    }

    /** Sends an email to me with the stocks that have an intersection
     * @param stockSymbols
     * @return true if the method ran all the way through indicating that an email was sent.
     * @throws MessagingException
     */
    public static boolean generateAndSendEmail(Set<StockSymbol> stockSymbols) throws MessagingException {

    //todo be able to send multiple people email from a properties file
        // Step1
        LOG.info("1st ===> setup Mail Server Properties..");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        LOG.info("Mail Server Properties have been setup successfully..");

        // Step2
        LOG.info("2nd ===> get Mail Session..");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);

        for(String email : subscribers){
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        }
//        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));
        generateMailMessage.setSubject("Simple Moving Average Crossover");
        String emailBody = generateEmailBody(stockSymbols);
        generateMailMessage.setContent(emailBody, "text/html");
        LOG.info("Mail Session has been created successfully..");

        // Step3
        LOG.info("\n\n 3rd ===> Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect("smtp.gmail.com", USERNAME, PASSWORD);
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        LOG.info("Mail sent. Closing session.");
        transport.close();
        return true;
    }

    private static String generateEmailBody(Set<StockSymbol> stockSymbols){
        StringBuilder sb = new StringBuilder();

        sb.append("These are the stocks that it's 8 SMA and 23 SMA have intersected.");
        sb.append("<br><br>");
        sb.append("<ol>");
        stockSymbols.forEach(
                symbol -> {
                    sb.append("<li>" + symbol.getSymbol() + "</li>");
                }
        );
        sb.append("</ol>");

        return sb.toString();
    }

    public static void main(String[] args) {

        if( args.length != 1){
            LOG.error("You must provide only the directory where you have your stock list CSVs");
            System.exit(1);
        }

        //List of all the stock symbols
        List<StockSymbol> stockSymbols = getStockSymbols(new CsvReader(), args[0]);
        Set<StockSymbol> stocksToEmail = new HashSet<>();

        for(int i = 0; i < stockSymbols.size(); i++){
            StockSymbol symbol = stockSymbols.get(i);
            try {
                //build query
                List<QueryCriteria> queries = queryBuilder(symbol);

                //make the call and get a response
                List<JsonObject> response = webService(new AlphaVantageWebClient(queries));

                //parse the response to get two List of SMADataPonits
                List<List<SMADataPoint>> parsedResponse = parseResponse(response);

                //find where the cross over occurred
                if (analyseForIntersection(parsedResponse)) {
                    stocksToEmail.add(symbol);
                }

                //This is just a loading symbol
                StringBuilder sb = new StringBuilder();
                sb.append("LOADING:");
                sb.append(" ");
                float percentage = ((i * 100.0f)/stockSymbols.size());
                sb.append(String.format("%.02f", percentage));
                sb.append("% Completed");
                LOG.info(sb.toString());

            } catch (Exception e) {
                LOG.error("Api Might have thrown an error for call frequency. break and try to send stocks to email if there is any: " + e);
                break;
            }
        }
        try {
            if(stocksToEmail.size() > 0) {
                generateAndSendEmail(stocksToEmail);
            }else {
                LOG.info("There were no stocks with an intersection found.");
            }
        }catch (MessagingException ex){
            LOG.error("The email was unsuccessful", ex);
        }
    }
}
