package project2.starsApp;
import java.util.Properties; 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends email. Taken from NTU materials
 * */
public class SendEmail {
public static void SendSchEmail(String index, String courseCode, String studentName) {

    final String username = "oodpemail";
    final String password = "00dpemail*";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
        });

    try {

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("oodpemail@gmail.com"));
        message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse("oodprecipient@gmail.com"));
        message.setSubject("Successful Addition of Course " + courseCode);
        message.setText(String.format("Dear Student %s, \n\nCourse %s with index %s has been successfully added!", studentName, courseCode, index));

        Transport.send(message);

        System.out.println(String.format("Email sent to %s", studentName));

    } catch (MessagingException e) {
        throw new RuntimeException(e);
    }
}
}
