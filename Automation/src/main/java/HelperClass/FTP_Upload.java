package HelperClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.testng.annotations.Test;


public class FTP_Upload {
              
              private static Logger log = LogManager.getLogger(FTP_Upload.class.getName());
              
              public void FTPUpload(String hostname, int portno, String Username, String Password, String transferMode, String LocalFilepath, String RemoteFilepath, String fileName) {
              FTPClient ftpClient = new FTPClient();
              
              try {
                             ftpClient.connect(hostname, portno);
                             ftpClient.login(Username, Password);
                             
                             ftpClient.enterLocalPassiveMode();
                    
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        File LocalFile = new File(LocalFilepath);
        String RemoteFile = RemoteFilepath;
        
        InputStream IS = new FileInputStream(LocalFile + "\\" + fileName);
        boolean done = ftpClient.storeFile(RemoteFile + "/" + fileName, IS);
        
        if(done) {
              log.info("File uploaded successfully");
              log.info("FTP Response: " + ftpClient.getStatus());
        }
        else {
              log.info("File Not Uploaded successfully");
              log.info("FTP Response: " + ftpClient.getStatus());
        }
        IS.close();
              } catch (SocketException e) {
                             log.info ("Socket Exception occurred while uploading file to FTP server: " + e.getMessage());
              log.info (e.toString());
              } catch (IOException e) {
                             log.info ("IO exception occurred while uploading file to FTP server: " + e.getMessage());
              log.info (e.toString());
              } catch (Exception e) {
              log.info ("Error occurred while uploading file to FTP server: " + e.getMessage());
              log.info (e.toString());
    }
    
              finally {
        
        try {
            if (ftpClient.isConnected()) {
               ftpClient.logout();
                ftpClient.disconnect();
            }
             
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         
    } 
    
              }
              
}
