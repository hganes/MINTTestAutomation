package HelperClass;


import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendEmail {
	
	public String SenderEmail = "deepak.madhanagopal@sky.uk";
	public String RecipentEmail = "shalu.saparia@sky.uk";
	public String Subject = "Sample Mail - MINT UI Sanity Automation Test Report";
	public String BodyEmail = "Hi, Please find the attached Test Report for MINT UI Sanity Automation Test";
	public String Password = "febMachinee@23";
	
	private static Logger log = LogManager.getLogger(FTP_Upload.class.getName());
	
	public static void main(String[] args) throws AddressException, MessagingException, IOException {
		SendEmail SE = new SendEmail();
		SE.sendingEmail();
	}
	public void sendingEmail() throws AddressException, MessagingException, IOException {
		
		Properties propertyEmail = new Properties();
		propertyEmail.put("mail.smtp.auth", "true");
		//propertyEmail.put("mail.smtp.starttls.enable", "true");
		propertyEmail.put("mail.smtp.host", "bridgeheads.bskyb.com");
		propertyEmail.put("mail.smtp.port", "25");
		
		log.info("Initialized the properties");
		
		Session session = Session.getInstance(propertyEmail, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(SenderEmail,Password);
		}
		});
		
		log.info("Session Created");
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(SenderEmail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(RecipentEmail));
		message.setSubject(Subject);
		message.setText(BodyEmail);
		
		Multipart multipartEmail = new MimeMultipart();
		
	    MimeBodyPart emailbodytext = new MimeBodyPart();
	    emailbodytext.setText(BodyEmail);
	    
	    MimeBodyPart attachreport = new MimeBodyPart();
	    attachreport.attachFile(System.getProperty("user.dir")+"\\reports\\MINTUISanityTestExecutionReport.html");
	    
	    multipartEmail.addBodyPart(emailbodytext);
	    multipartEmail.addBodyPart(attachreport);
	    
	    message.setContent(multipartEmail);
	    Transport.send(message);
	    
	    System.out.println("Email Successfully Sent");
	    
	}
}
