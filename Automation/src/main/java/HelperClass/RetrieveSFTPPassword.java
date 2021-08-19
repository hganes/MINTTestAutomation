package HelperClass;
import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetrieveSFTPPassword {

	private static Logger log = LogManager.getLogger(SFTP_Upload.class.getName());
	
	public String GetSFTPPassword(String SFTPPassword) throws IOException {
		// TODO Auto-generated method stub
		
		Properties prop = new Properties();
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\data.properties");
        prop.load(fis);
        
        String user = prop.getProperty("unixuser");
        String password = prop.getProperty("password");
        String host = prop.getProperty("unixhost");
        int port = Integer.parseInt(prop.getProperty("unixport"));
        String unixusername = prop.getProperty("unixUsername");
        String unixpath = prop.getProperty("unixpath");
        //String SFTPPassword = "rO0ABXQABkRFU2VkZXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAIkjECQ+5UhNBzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAAZXVxAH4AAQAAABAr2eKiHUPfXYzc1ni0iOjo";
        String command = "sh " + unixpath + " " + unixusername + " " + SFTPPassword;
        String SFTPPass = null;
        
		try {
		              JSch jsch = new JSch();
		              Session session = jsch.getSession(user, host, port);
		              session.setPassword(password);
		              session.setConfig("StrictHostKeyChecking", "no");
		              log.info("Establishing Connection...");
		              session.connect();
		              log.info("Connection established.");
		              log.info("Creating SFTP Channel.");
		              //ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		              ChannelExec execChannel = (ChannelExec) session.openChannel("exec");
		              ((ChannelExec) execChannel).setCommand(command);
		     //execChannel.setInputStream(null);
		     //OutputStream out = execChannel.getOutputStream();
		     //((ChannelExec) execChannel).setErrStream(System.err);
		     
		              execChannel.connect();
		              log.info("Exec Channel created.");
		              
		              //String Password = null;
		              //execChannel.toString();
		              //System.out.println(Password);
		              
		              InputStream inputStream = execChannel.getInputStream();
		              int k =0;
		              String line = null;
		              
		              
		            		  Scanner scanner = new Scanner(new InputStreamReader(inputStream));
		                             while (scanner.hasNextLine()) {
		                                           line = scanner.nextLine();
		                                           k++;
		                                           if(k==12) {
		                                           log.info(line);
		                                           SFTPPass = line;
		                                           }
		                                          }
		                           
		              
		              /*InputStream inputStream = execChannel.getInputStream();
		              
		              try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
		                            Password = reader.readLine().toString();
		                            System.out.println(Password);
		                           
		              }*/
		              
		              execChannel.disconnect();
		              session.disconnect();
		             
		              
		              } catch (Exception e) {
		                            
		              }
		
		 System.out.println("Done");
		return SFTPPass;
		
	}

}
