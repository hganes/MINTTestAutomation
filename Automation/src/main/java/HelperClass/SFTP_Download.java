package HelperClass;

//import java.io.IOException;
//import java.sql.ResultSet;
//import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jcraft.jsch.*;

//import net.schmizz.sshj.SSHClient;
//import net.schmizz.sshj.sftp.SFTPClient;
//import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class SFTP_Download {
              
private static Logger log = LogManager.getLogger(SFTP_Download.class.getName());
              
              public void SFTPDownload(String hostname, int portno, String Username, String Password, String LocalFilepath, String RemoteFilepath, String fileName) {
                             try {
                                           JSch jsch = new JSch();
                                           Session session = jsch.getSession(Username, hostname, portno);
                                           session.setPassword(Password);
                                           session.setConfig("StrictHostKeyChecking", "no");
                                           System.out.println("Establishing Connection...");
                                           session.connect();
                                           Channel channel = session.openChannel("sftp");
                                  channel.connect();
                                  System.out.println("SFTP Connection established");
                                           //log.info "Creating SFTP Channel.";
                                           ChannelSftp sftpChannel = (ChannelSftp) channel;
                                           //sftpChannel.cd(remoteFile);
                                           //sftpChannel.put(LocalFilepath + "\\" + fileName, RemoteFilepath + "/" + fileName);
                                           sftpChannel.get(RemoteFilepath + "/" + fileName, LocalFilepath + "\\" + fileName);
                                           Thread.sleep(1000);
                                           channel.disconnect();
                                 session.disconnect();
                                 log.info("File downloaded in SFTP successfully");
                             } catch (Exception e) {
                                           System.out.println("Error: " + e.getMessage());
                                           log.info("Error in downloading file from SFTP: " + e.getMessage());
                             }
              }
              //Commented Deepak's Code
              /*public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
                             // TODO Auto-generated method stub
                                           
                                           String Profilename = "mft_test2_RP";
                                  String REMOTE_HOST = null;
                                  String USERNAME = null;
                                  String PASSWORD = null;
                                  int REMOTE_PORT;
                                  int SESSION_TIMEOUT = 100000;
                                  int CHANNEL_TIMEOUT = 5000;
                                 
                                  DatabaseOperations DB = new DatabaseOperations();
                                  String SQLgetSFTPDetails = "OPEN SYMMETRIC KEY SQLSymmetricKey DECRYPTION BY CERTIFICATE SelfSignedCertificate; select REMOTE_HOST, REMOTE_PORT, REMOTE_USER, CONVERT(varchar,DecryptByKey(REMOTE_PASSWORD)) AS PASSWORD FROM [AdTechHub_Phase1].[dbo].[CONF_SFTP_PROF] WHERE NAME = '" + Profilename + "' ; CLOSE SYMMETRIC KEY SQLSymmetricKey;";
                                  System.out.println(SQLgetSFTPDetails);
                                  ResultSet rsgetSFTPDetails = DB.sqlSelect(SQLgetSFTPDetails);
                                  System.out.println("Query Executed");
                                  
                                  while (rsgetSFTPDetails.next()) {
                                            REMOTE_HOST = rsgetSFTPDetails.getString(1);
                                            System.out.println("REMOTE_HOST is" + REMOTE_HOST);
                                            REMOTE_PORT = Integer.parseInt(rsgetSFTPDetails.getString(2));
                                            System.out.println("REMOTE_PORT is" + REMOTE_PORT);
                                            USERNAME = rsgetSFTPDetails.getString(3);
                                            System.out.println("USERNAME is" + USERNAME);
                                            //PASSWORD = rsgetSFTPDetails.getString(4);
                                            PASSWORD = "P@ssw0rd";
                                            System.out.println("PASSWORD is" + PASSWORD);
                                  }
                                  
                                     // TODO Auto-generated method stub
                                     String localFile = "C:\\Users\\DEM49\\JAVA_PRACTICE\\Automation\\File_Processing\\BREAK\\UKTV\\Source_Files\\UKTBS.30689";
                                     String remoteFile = "/home/mft_test2/TestingAutomation/UKTBS.30689";
                                     SSHClient client = new SSHClient();
                                     client.addHostKeyVerifier(new PromiscuousVerifier());
                                     client.connect(REMOTE_HOST);
                                     client.authPassword(USERNAME, PASSWORD);
                                   //  return client;
                                     
                                     SFTPClient sftpClient = client.newSFTPClient();
                                     
                                     sftpClient.put(localFile, remoteFile);
                                     
                                     System.out.println("upload done");
                                  
                                     sftpClient.close();
                                     client.disconnect();

                             

                                 }*/
}
