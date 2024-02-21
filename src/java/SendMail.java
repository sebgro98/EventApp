import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SendMail {
   
        private DerbyManager derbyManager;
        
        
        public SendMail() {
        derbyManager = new DerbyManager();
        }
    
    private Properties loadEmailConfig() {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\sushil\\Downloads\\eventApp_2\\eventApp\\src\\java\\config.txt"))) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or log an error message
        }
        return properties;
    }

    // Method for sending email
    public void sendEmail(String to, String subject, String body, int eventID) {
        // Email configuration
     
        Properties emailConfig = loadEmailConfig();
        
        // Extract properties from the loaded configuration
        String from = emailConfig.getProperty("email.from");
        String host = emailConfig.getProperty("email.host");
        final String username = emailConfig.getProperty("email.username");
        final String password = emailConfig.getProperty("email.password");
        //System.out.println(from);
        //System.out.println(host);
        //System.out.println(username);

        // Set properties for JavaMail session
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Set your SMTP port (587 is commonly used for TLS)



    
           Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
      

        try {
            // Create a MimeMessage object
            Message message = new MimeMessage(session);

            // Set sender and recipient
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // Set subject and body
            message.setSubject(subject);
            message.setText(body + " With the eventID: " + eventID );

            // Send the message
            Transport.send(message);
            System.out.println("Email sent successfully...");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    // Method for sending email to a user based on UserID
    public void sendEmailToUser(int userID, String subject, String body, int eventID) {
 
        try (Connection conn = derbyManager.getConnection()) {
            // Retrieve user email based on UserID
            String userEmailQuery = "SELECT Username FROM Users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(userEmailQuery)) {
                pstmt.setInt(1, userID);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        String userEmail = resultSet.getString("Username");
                        // Call the sendEmail method to send the email
                        sendEmail(userEmail, subject, body, eventID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
