package HelperClass;

import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.testng.annotations.Test;

public class FTP_Download {

              private static Logger log = LogManager.getLogger(FTP_Download.class.getName());
              
              public void FTPDownload(String hostname, int portno, String Username, String Password, String transferMode, String LocalFilepath, String RemoteFilepath, String fileName) {
              FTPClient ftpClient = new FTPClient();
              
              try {
                             ftpClient.connect(hostname, portno);
                             ftpClient.login(Username, Password);
                             
                             ftpClient.enterLocalPassiveMode();
                    
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        File LocalFile = new File(LocalFilepath);
        String RemoteFile = RemoteFilepath;
        
        OutputStream OS = new FileOutputStream(LocalFile + "\\" + fileName);
        boolean done = ftpClient.retrieveFile(RemoteFile + "/" + fileName, OS);
        
        if(done) {
              log.info("File downloaded successfully");
              log.info("FTP Response: " + ftpClient.getStatus());
        }
        else {
              log.info("File Not downloaded successfully");
              log.info("FTP Response: " + ftpClient.getStatus());
        }
        OS.close();
              } catch (SocketException e) {
                             log.info ("Socket Exception occurred while downloading file to FTP server: " + e.getMessage());
              log.info (e.toString());
              } catch (IOException e) {
                             log.info ("IO exception occurred while downloading file to FTP server: " + e.getMessage());
              log.info (e.toString());
              } catch (Exception e) {
              log.info ("Error occurred while downloading file to FTP server: " + e.getMessage());
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
