package com.trade.service.emailService;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${mail.host}")
    private String host;

    @Value("${mail.smtpserver}")
    private String smtpserver;

    @Value("${mail.auth}")
    private String auth;

    @Value("${mail.port}")
    private String port;

    @Value("${mail.defaultport}")
    private String defaultport;

    @Value("${mail.starttls}")
    private String starttls;

    @Value("${mail.starttlsrequired}")
    private String starttlsrequired;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.subject}")
    private String subject;

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    public void sendOtpMail(String userName, String otpNumber, String email) throws MessagingException {
        Message message = createEmail(userName,otpNumber,email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(protocol);
        smtpTransport.connect(smtpserver,username,password);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
     }

    public Message createEmail(String userName, String otpNumber, String email ) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email,false));
        message.setSubject(subject);
        message.setText("Hello " + userName+ " \n \n please use this opt number to reset your password : " + otpNumber + " \n \n  Support Center");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
    public Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(host, smtpserver);
        properties.put(auth, true);
        properties.put(port,defaultport);
        properties.put(starttls,true);
        properties.put(starttlsrequired, true);
        return Session.getInstance(properties, null);
    }
}
