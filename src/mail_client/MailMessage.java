package mail_client;

import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailMessage {
    private int messagesCount = -1;

    public int send(String from, String password, String to, String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.setSubject(subject);
            message.setFrom(new InternetAddress(from));
            message.setSentDate(new Date());
            message.setText(text);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            Transport transport = session.getTransport("smtps");
            transport.connect("smtp.gmail.com", 465, "name", password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AuthenticationFailedException e) {
            return Constants.LOGGING_ERROR;
        }
        catch (MessagingException e) {
            return Constants.MESSAGING_ERROR;
        }
        catch (Exception e) {
            return Constants.ERROR;
        }

        return Constants.NO_ERROR;
    }

    public String receive(String address, String password){
        String host = "pop.gmail.com";
        Message[] messages;
        Folder folder;

        try {
            Properties props = new Properties();

            props.put("mail.pop3.host", "pop.gmail.com");
            props.put("mail.pop3.port", "995");
            props.put("mail.pop3.starttls.enable", "true");

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(address, password);
                }
            });

            Store store = session.getStore("pop3s");
            store.connect(host, 995, address, password);

            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            messages = folder.getMessages();

            String result = "";
            int i, count;
            for (i = messages.length - 1, count = 0; i > 0 && count <= 10; i--, count++){
                result += "from " + messages[i].getFrom()[0].toString() + "\n" + messages[i].getSentDate() + "\n" + messages[i].getSubject() + "\n\n";
            }

            if (messagesCount == -1) {
                messagesCount = folder.getMessageCount();
                folder.close(false);
                store.close();
                return result;
            }
            else if (folder.getMessageCount() != messagesCount) {
                messagesCount = folder.getMessageCount();
                folder.close(false);
                store.close();
                return result;
            }
            else {
                folder.close(false);
                store.close();
                return Constants.EMPTY;
            }
        }
        catch (NoSuchProviderException e) {
            return Constants.EMPTY;
        }
        catch (MessagingException e) {
            return Constants.EMPTY;
        }
        catch (Exception e) {
            return Constants.EMPTY;
        }
    }
}
