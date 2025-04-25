package us.zonix.api.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Mail {

    public static void sendRegistrationEmail(String email, String confirmationId) {
        new Thread(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.host", "mail.privateemail.com");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("site@zonix.us", "@EQaFEnApr2b");
                        }
                    }
            );

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("site@zonix.us", "Zonix Network"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                message.setSubject("Zonix - Complete Your Registration");
                message.setText("Thanks for registering your account on the Zonix Network.\n" + "To complete your account registration, please click the following link:\n" + "http://www.zonix.us/confirm/" + confirmationId + "\n\n" + "Thanks,\n" + "Zonix Network");

                Transport.send(message);
            }
            catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
