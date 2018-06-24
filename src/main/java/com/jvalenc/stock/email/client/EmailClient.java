package com.jvalenc.stock.email.client;

import com.jvalenc.stock.models.StockSymbol;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

public class EmailClient {

    private static final Logger logger = Logger.getLogger(EmailClient.class);

    private static final String USERNAME = "jonathan.valencia716@gmail.com";
    private static final String PASSWORD = "9129669jvad";

    /** Sends an email to me with the stocks that have an intersection
     * @param stockSymbols
     * @return true if the method ran all the way through indicating that an email was sent.
     * @throws MessagingException
     */
    public static boolean generateAndSendEmail(Set<StockSymbol> stockSymbols) throws MessagingException {

        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage generateMailMessage;

        //todo: in the future might have to send individual email to not leak personal emails.

        // Step1
        logger.info("1st ===> setup Mail Server Properties..");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        logger.info("Mail Server Properties have been setup successfully..");

        // Step2
        logger.info("2nd ===> get Mail Session..");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);

        for(String email : getSubscribers()){
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        }
//        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));
        generateMailMessage.setSubject("Simple Moving Average Crossover");
        String emailBody = generateEmailBody(stockSymbols);
        generateMailMessage.setContent(emailBody, "text/html");
        logger.info("Mail Session has been created successfully..");

        // Step3
        logger.info("\n\n 3rd ===> Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect("smtp.gmail.com", USERNAME, PASSWORD);
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        logger.info("Mail sent. Closing session.");
        transport.close();
        return true;
    }

    /**
     * This retrieves the list of subscribers from a csv list.
     * @return
     */
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
                logger.error("File " + file.getPath() + " not found", e);
            } catch (IOException e) {
                logger.error(e);
            }

        }
        return Collections.unmodifiableList(Arrays.asList(subscribers.toArray(new String[]{})));
    }

    /**
     * Generates the email body.
     * @param stockSymbols
     * @return
     */
    private static String generateEmailBody(Set<StockSymbol> stockSymbols){
        StringBuilder sb = new StringBuilder();

        //header body text
        sb.append("These are the stocks that it's 8 SMA and 23 SMA have intersected")
                .append(" or has an indication of -80 or lower on the Williams Percent R.")
                .append("<br><br>");

        //start of table
        sb.append("<table style=\"width: 100%;\">");

        //table headers
        sb.append("<tr>")
                .append("<th align=\"left\">").append("Symbol").append("</th>")
                .append("<th align=\"left\">").append("Trend").append("</th>")
                .append("<th align=\"left\">").append("Has SMA Crossover").append("</th>")
                .append("<th align=\"left\">").append("Has WillR").append("</th>")
                .append("</tr>");

        //table data for each symbol
        stockSymbols.forEach(
                symbol -> {
                    sb.append("<tr>")
                            .append("<td>").append(symbol.getSymbol()).append("</td>")
                            .append("<td>").append(symbol.getTrend().getValue()).append("</td>")
                            .append("<td>").append(Boolean.toString(symbol.isHasSMACrossover())).append("</td>")
                            .append("<td>").append(Boolean.toString(symbol.isHasWillR())).append("</td>")
                            .append("</tr>");
                }
        );

        //end of table
        sb.append("</table>");

        return sb.toString();
    }
}
