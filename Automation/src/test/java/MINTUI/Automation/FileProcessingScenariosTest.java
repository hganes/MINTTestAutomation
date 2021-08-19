package MINTUI.Automation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import HelperClass.DatabaseOperations;
import HelperClass.FTP_Download;
import HelperClass.FTP_Upload;
import HelperClass.RetrieveSFTPPassword;
import HelperClass.SFTP_Download;
import HelperClass.SFTP_Upload;

public class FileProcessingScenariosTest {

              ExtentReports extent;
              public ExtentTest Test;
              Properties prop = new Properties();
              String FileLogid = null;
              String currTestResultsDir = null;
              
              private static Logger log = LogManager.getLogger(FileProcessingScenariosTest.class.getName());
              
              @BeforeTest
              public void Extent_Report() throws IOException, InterruptedException {
                             
                             
                             FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\data.properties");
                             prop.load(fis);
                             
                             String path = System.getProperty("user.dir")+"\\reports\\FileProcessingTestExecutionReport.html";
                             ExtentSparkReporter Rep = new ExtentSparkReporter(path);
                             Rep.config().setReportName("File Processing Sanity Automation Report");
                             Rep.config().setDocumentTitle("File Processing Sanity Test Report");
                             
                  extent = new ExtentReports();
                             extent.attachReporter(Rep);
                             extent.setSystemInfo("Environment", prop.getProperty("Test_Environment"));
                             extent.setSystemInfo("Executed By", System.getProperty("user.name"));
                             extent.setSystemInfo("Executed On", java.net.InetAddress.getLocalHost().getHostName());
                             
                             Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //System.out.println(timestamp.toString().replaceFirst(" ", "T"));
                             
                             currTestResultsDir = System.getProperty("user.dir") + "\\TestResults\\" + StringUtils.substringBefore((timestamp.toString().replaceFirst(" ", "T")).replace(":", ""), ".");
                             
                             //Create Test Result directory for current run
                             File file = new File(currTestResultsDir);
                             file.mkdirs();
              }
              
              @Test(dataProvider="getdataFileProcessing")
              public void PushFileProcessing(String TC_ID, String Interface_Type, String Interface_Format, String Interface_id, String Scenario_Type, String Src_File_Name, String expErrCd, String expRecNo, String expChnCd, String expAddInfo) throws InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException {
                             
                             DatabaseOperations DB = new DatabaseOperations();
                             String GetSourceEndPointDetails = "OPEN SYMMETRIC KEY SQLSymmetricKey DECRYPTION BY CERTIFICATE SelfSignedCertificate; "
                                                          + "select HOSTNAME, PORT_NO, TRANSFER_MODE, PROTOCOL_TYPE, USERNAME, CONVERT(varchar,DecryptByKey(PASSWORD)) AS PASSWORD, "
                                                          + "FILEPATH, FILEMASK, DELETE_FLAG, TRIGGER_FILE_FLAG, TRIGGER_FILE_MASK, SFTP_PROFILE_NAME, ZIP_UNZIP, CHECK_COUNT_FLAG, "
                                                          + "CHECK_COUNT, AWS_ID FROM " + prop.getProperty("DatabaseName")+ ".[dbo].[CONF_SOURCE_ENDPOINT] WHERE INTERFACE_ID = " 
                                                         + Interface_id + " ; CLOSE SYMMETRIC KEY SQLSymmetricKey;";
                             log.info(GetSourceEndPointDetails);
                             ResultSet rsSourceEndpointDetails = DB.sqlSelect(GetSourceEndPointDetails);
                             String hostname, ProtocolType, Username, Password, transferMode=null, Filepath, LocalFilepath, SFTPProfileName=null, SFTPRemote_Host=null, SFTPUsername=null, SFTPPassword=null;
                             int portno = 0, SFTPRemote_Port=0;
                             
                             if (rsSourceEndpointDetails.next()) {
                                           hostname = rsSourceEndpointDetails.getString(1);
                                           log.info("Hostname :" + hostname);
                                           if(rsSourceEndpointDetails.getString(2)==null) {
                                        	   
                                           }else {
                                        	 portno = Integer.parseInt(rsSourceEndpointDetails.getString(2));
                                           }
                                           log.info("port no :" + portno);
                                           if(rsSourceEndpointDetails.getString(3)==null) {
                                           }else {
                                        	 transferMode = rsSourceEndpointDetails.getString(3);
                                           }
                                           log.info("Transfer mode :" + transferMode);
                                           ProtocolType = rsSourceEndpointDetails.getString(4);
                                           log.info(ProtocolType);
                                           Username = rsSourceEndpointDetails.getString(5);
                                           log.info(Username);
                                           Password = rsSourceEndpointDetails.getString(6);
                                           log.info(Password);
                                           Filepath = rsSourceEndpointDetails.getString(7);
                                           log.info(Filepath);
                                           SFTPProfileName = rsSourceEndpointDetails.getString("SFTP_PROFILE_NAME");
                                           log.info(SFTPProfileName);
                                           
                                           LocalFilepath = System.getProperty("user.dir")+"\\File_Processing\\" + Interface_Type + "\\" + Interface_Format + "\\Source_Files\\";
                                           
                                           if(ProtocolType.equalsIgnoreCase("FTP")) {
                                                //log.info("Came inside the FTP");
                                                FTP_Upload Ftpupload = new FTP_Upload();
                                                Ftpupload.FTPUpload(hostname, portno, Username, Password, transferMode, LocalFilepath, Filepath, Src_File_Name);
                                           } else if(ProtocolType.equalsIgnoreCase("SFTP")) {
                                        	   
                                        	   String SQLgetSFTPDetails = "select REMOTE_HOST, REMOTE_PORT, REMOTE_USER, REMOTE_PASSWORD FROM "+ prop.getProperty("DatabaseName")+ ".[dbo].[CONF_SFTP_PROF] WHERE NAME = '" + SFTPProfileName + "'";
                                               log.info(SQLgetSFTPDetails);
                                               ResultSet rsgetSFTPDetails = DB.sqlSelect(SQLgetSFTPDetails);
                                               log.info("Query Executed");
                                               
                                               while (rsgetSFTPDetails.next()) {
                                            	   		 SFTPRemote_Host = rsgetSFTPDetails.getString(1);
                                                         log.info("REMOTE_HOST is" + SFTPRemote_Host);
                                                         SFTPRemote_Port = Integer.parseInt(rsgetSFTPDetails.getString(2));
                                                         log.info("REMOTE_PORT is" + SFTPRemote_Port);
                                                         SFTPUsername = rsgetSFTPDetails.getString(3);
                                                         log.info("USERNAME is" + SFTPUsername);
                                                         SFTPPassword = rsgetSFTPDetails.getString(4);
                                                         log.info("PASSWORD is" + SFTPPassword);
                                               }
                                               
                                        	   //Logic to be arrived for getting the SFTP connectivity details is pending
                                                 RetrieveSFTPPassword RetPassword = new RetrieveSFTPPassword();
                                                 
                                                 SFTPPassword = RetPassword.GetSFTPPassword(SFTPPassword);
                                                 SFTP_Upload sftpUpload = new SFTP_Upload();
                                                 sftpUpload.SFTPUpload(SFTPRemote_Host, SFTPRemote_Port, SFTPUsername, SFTPPassword, LocalFilepath, Filepath, Src_File_Name);
                                           } else if(ProtocolType.equalsIgnoreCase("AWS_S3")) {
                                                          
                                           } else {}
                             }
              }
                             @Test
                             public void waitTime() throws InterruptedException {
                             Thread.sleep(TimeUnit.MINUTES.toMillis(Integer.parseInt(prop.getProperty("fileProcessingWaitTime"))));
                                           //log.info("The sleep time is over");
                             }
                             
                             @Test(dataProvider="getdataFileProcessing")
                             public void FileProcessing(String TC_ID, String Interface_Type, String Interface_Format, String Interface_id, String Scenario_Type, String Src_File_Name, String expErrCd, String expRecNo, String expChnCd, String expAddInfo) throws InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException {
                             
                             String fileSize, srcArcLocation, chCodes, fileStatus, bussStrtDt, bussEndDt, srcProcCnt;
                             String tcName = TC_ID + "_" + Interface_Type + "_" + Interface_Format;
                             String tcRstDir = currTestResultsDir + "\\" + tcName;
                             Test = extent.createTest(tcName);
                             
                             Files.createDirectories(Paths.get(tcRstDir)); //Creates test case specific directory
                             String srcFilePath = System.getProperty("user.dir")+"\\File_Processing\\" + Interface_Type + "\\" 
                                                                                                     + Interface_Format + "\\Source_Files\\" + Src_File_Name;
                             String srcFile = tcRstDir + "\\" + Src_File_Name;
                             FileUtils.copyFile(new File(srcFilePath), new File(srcFile));
                             
                             int Count = 0;
                             DatabaseOperations DB = new DatabaseOperations();
                             ResultSet rsGetFileLogids = null;
                  
                             ResultSet rsGetTargetEndpoint = null;
                  
                             String strFileLog = "select ID, SOURCE_FILENAME, SOURCE_FILESIZE, SOURCE_ARC_LOCATION, "
                                                          + "CH_CODES, STATUS, CONTENT_START_DATE, CONTENT_END_DATE, PROCESS_COUNTER from "
                                                          + prop.getProperty("DatabaseName")+".[dbo].[T_FILE_LOG] where SOURCE_FILENAME = '" 
                                                          + Src_File_Name + "' AND INTERFACE_ID = " + Interface_id +  " order by ID desc";
                             log.info("T_FILE_LOG Query: " + strFileLog);
                             
                             try {
                                           rsGetFileLogids = DB.sqlSelect(strFileLog);
                             } catch (Exception e) {
                                           log.info("Error in executing SQL query for T_FILE_LOG table: " + e.getMessage());
                                           log.info(e.toString());
                             }
                             rsGetFileLogids.next();
                             
                             FileLogid = rsGetFileLogids.getString("ID");
                             fileSize = rsGetFileLogids.getString("SOURCE_FILESIZE");
                             srcArcLocation = rsGetFileLogids.getString("SOURCE_ARC_LOCATION");
                             chCodes = rsGetFileLogids.getString("CH_CODES");
                             fileStatus = rsGetFileLogids.getString("STATUS");
                             bussStrtDt = rsGetFileLogids.getString("CONTENT_START_DATE");
                             bussEndDt = rsGetFileLogids.getString("CONTENT_END_DATE");
                             srcProcCnt = rsGetFileLogids.getString("PROCESS_COUNTER");
                             log.info("***** T_FILE_LOG Table result *****");
                             log.info("File Log ID: " + FileLogid);
                             log.info("Source File Size: " + fileSize);
                             log.info("Source Archival Location: " + srcArcLocation);
                             log.info("Business Info: " + chCodes);
                             log.info("Source File Status: " + fileStatus);
                             log.info("Business Start Date: " + bussStrtDt);
                             log.info("Business End Date: " + bussEndDt);
                             log.info("Reprocess count: " + srcProcCnt);
                             
                             String strTgtLogs = "select ID, TGT_FILENAME, TGT_FILE_LOCATION, TGT_ARC_LOCATION, "
                                                          + "STATUS, TARGET_ID, TGT_ARC_FILENAME, PROCESS_COUNTER from " 
                                                          + prop.getProperty("DatabaseName")+".[dbo].[T_TARGET_LOG] where "
                                                          + "FILE_LOG_ID = " + FileLogid;
                             
                             log.info("T_TARGET_LOG Query: " + strTgtLogs);
                             
                             String tgtFileName, tgtFilePath, tgtArcLocation, tgtStatus, tgtID, tgtArcFileName, tgtProcCnt, strGetTargetEndpoint;
                             String tgtHostname, tgtProtocolType, tgtUsername, tgtPassword, tgtTransferMode, tgtName, tgtFilepath, tgtSFTPProfilename, SFTPRemote_Host = null, SFTPUsername = null, SFTPPassword = null;
                             int tgtPortNo=0, SFTPRemote_Port = 0;
                             
                             if (Scenario_Type.equalsIgnoreCase("Positive")) {
                                        Assert.assertEquals(fileStatus, "COMPLETED");
                                          
                                        String SQLgetCountTargetFiles = "select count(*) from "+prop.getProperty("DatabaseName")+".[dbo].[T_TARGET_LOG] where FILE_LOG_ID = " + FileLogid;
                                   		log.info(SQLgetCountTargetFiles);
                                   		ResultSet rsgetCountTargetFiles = DB.sqlSelect(SQLgetCountTargetFiles);
                                   		
                                   		
                                   		while(rsgetCountTargetFiles.next()) {
                                   			Count  = rsgetCountTargetFiles.getInt(1);
                                   			log.info("Count id: " + Count);
                                   		}
                                        
                                   		rsgetCountTargetFiles.close();
                                   		
                                   	    ResultSet rsGetTargetLogs = null;
                                        try {
                                                    rsGetTargetLogs = DB.sqlSelect(strTgtLogs);
                                        } catch (Exception e) {
                                                    log.info("Error in executing SQL query for T_TARGET_LOG table: " + e.getMessage());
                                                    log.info(e.toString());
                                        }
                                     
                                   		
                                           Object[][] TargetFiles=new Object[Count][7];
                                    	   int i = 0;
                                           
                                    	   while (rsGetTargetLogs.next()) {
                                  			 TargetFiles[i][0] = rsGetTargetLogs.getString("TGT_FILENAME");
                                  			 String TgtFilename = rsGetTargetLogs.getString("TGT_FILENAME");
                                  			 log.info(TgtFilename);
                                  			 TargetFiles[i][1] = rsGetTargetLogs.getString("TGT_FILE_LOCATION");
                                  			 String TgtFileLocation  = rsGetTargetLogs.getString("TGT_FILE_LOCATION");
                                  			 log.info(TgtFileLocation);
                                  			 TargetFiles[i][2] = Integer.parseInt(rsGetTargetLogs.getString("TARGET_ID"));
                                  			 int Targetid = Integer.parseInt(rsGetTargetLogs.getString("TARGET_ID"));
                                  			 log.info(Targetid);
                                  			 TargetFiles[i][3] = rsGetTargetLogs.getString("TGT_ARC_LOCATION");
                                 			 String TargetArchievalLocation = rsGetTargetLogs.getString("TGT_ARC_LOCATION");
                                 			 log.info(TargetArchievalLocation);
                                 			 TargetFiles[i][4] =rsGetTargetLogs.getString("TGT_ARC_FILENAME");
                                 			 String TargetArchievalFileName = rsGetTargetLogs.getString("TGT_ARC_FILENAME");
                                 			 log.info(TargetArchievalFileName);
                                 			 TargetFiles[i][5] = rsGetTargetLogs.getString("PROCESS_COUNTER");
                                 			 String ProcessCounter = rsGetTargetLogs.getString("PROCESS_COUNTER");
                                 			 log.info(ProcessCounter);
                                 			 TargetFiles[i][6] = rsGetTargetLogs.getString("STATUS");
                                 			 String TgtStatus  = rsGetTargetLogs.getString("STATUS");
                                 			 log.info(TgtStatus);
                                  			 Assert.assertEquals(TgtStatus, "SENT");
                                
                                  			VerifyTargetFileName(Interface_Type,Src_File_Name,TgtFilename, Interface_Format,chCodes);
                                  			 i++;
                                  		}
                                    	   rsGetTargetLogs.close();	
                                    	   
                                    	   for (int j=0;j<Count;j++) {
                                  			 String SQLgetTargetEndPointDetails = "OPEN SYMMETRIC KEY SQLSymmetricKey DECRYPTION BY CERTIFICATE SelfSignedCertificate; select HOSTNAME, PORT_NO, PROTOCOL_TYPE, USERNAME, CONVERT(varchar,DecryptByKey(PASSWORD)) AS PASSWORD, FILEPATH, TRANSFER_MODE, TARGET_NAME, SFTP_PROFILE_NAME FROM "+prop.getProperty("DatabaseName")+".[dbo].[CONF_TARGET_ENDPOINT] WHERE ID = " + TargetFiles[j][2] + " order by ID desc ; CLOSE SYMMETRIC KEY SQLSymmetricKey;";
                                  			 log.info(SQLgetTargetEndPointDetails);
                                  			 ResultSet rsTargetEndpointDetails = DB.sqlSelect(SQLgetTargetEndPointDetails);
                                  			 
                                  			 log.info(rsTargetEndpointDetails.getFetchSize());
                                  			 
                                  			 if (rsTargetEndpointDetails.next()) {
                               				 log.info("TargetEndpoint details Fetched");
                               				 
                               				 tgtHostname = rsTargetEndpointDetails.getString("HOSTNAME");
                               				 if(rsTargetEndpointDetails.getString("PORT_NO")==null) { 
                               				 }else {
                                             tgtPortNo = Integer.parseInt(rsTargetEndpointDetails.getString("PORT_NO"));
                               				 }
                                             tgtProtocolType = rsTargetEndpointDetails.getString("PROTOCOL_TYPE");
                                             tgtUsername = rsTargetEndpointDetails.getString("USERNAME");
                                             tgtPassword = rsTargetEndpointDetails.getString("PASSWORD");
                                             tgtFilepath = rsTargetEndpointDetails.getString("FILEPATH");
                                             tgtTransferMode = rsTargetEndpointDetails.getString("TRANSFER_MODE");
                                             tgtName = rsTargetEndpointDetails.getString("TARGET_NAME");
                                             tgtSFTPProfilename = rsTargetEndpointDetails.getString("SFTP_PROFILE_NAME");
                                             log.info("***** CONF_TARGET_ENDPOINT Table result *****");
                                             log.info("Target Hostname: " + tgtHostname);
                                             log.info("Target Port No: " + tgtPortNo);
                                             log.info("Target Protocol: " + tgtProtocolType);
                                             log.info("Target Username: " + tgtUsername);
                                             log.info("Target Password: " + tgtPassword);
                                             log.info("File Path: " + tgtFilepath);
                                             log.info("Transfer Mode: " + tgtTransferMode);
                                             log.info("Target Name: " + tgtName);
                                             log.info("SFTP Profile Name: " + tgtSFTPProfilename);
                               				 
                                             String tgtFile = tcRstDir + "\\" + TargetFiles[j][0];
                                             String tgtfilename = TargetFiles[j][0].toString();
                                             String remotefilepath = TargetFiles[j][1].toString();
                                             if(tgtProtocolType.equalsIgnoreCase("FTP")) {
                                                          FTP_Download Ftpdownload = new FTP_Download();
                                                          Ftpdownload.FTPDownload(tgtHostname, tgtPortNo, tgtUsername, tgtPassword, tgtTransferMode, tcRstDir, remotefilepath, tgtfilename);
                                                          log.info("FTP Download checkpoint!");
                                             } else if(tgtProtocolType.equalsIgnoreCase("SFTP")) {
                                                    
                                            	 String SQLgetSFTPDetailsTarget = "select REMOTE_HOST, REMOTE_PORT, REMOTE_USER, REMOTE_PASSWORD FROM "+ prop.getProperty("DatabaseName")+".[dbo].[CONF_SFTP_PROF] WHERE NAME = '" + tgtSFTPProfilename + "'";
                                                 System.out.println(SQLgetSFTPDetailsTarget);
                                                 ResultSet rsgetSFTPDetailsTarget = DB.sqlSelect(SQLgetSFTPDetailsTarget);
                                                 System.out.println("Query Executed");
                                                 
                                                 while (rsgetSFTPDetailsTarget.next()) {
                                              	   		   SFTPRemote_Host = rsgetSFTPDetailsTarget.getString(1);
                                                           System.out.println("REMOTE_HOST is" + SFTPRemote_Host);
                                                           SFTPRemote_Port = Integer.parseInt(rsgetSFTPDetailsTarget.getString(2));
                                                           System.out.println("REMOTE_PORT is" + SFTPRemote_Port);
                                                           SFTPUsername = rsgetSFTPDetailsTarget.getString(3);
                                                           System.out.println("USERNAME is" + SFTPUsername);
                                                           SFTPPassword = rsgetSFTPDetailsTarget.getString(4);
                                                           System.out.println("PASSWORD is" + SFTPPassword);
                                                 }
                                                 
                                          	   //Logic to be arrived for getting the SFTP connectivity details is pending
                                                   RetrieveSFTPPassword RetPassword = new RetrieveSFTPPassword();
                                                   
                                                   SFTPPassword = RetPassword.GetSFTPPassword(SFTPPassword);
                                                   SFTP_Download sftpDownload = new SFTP_Download();
                                                   sftpDownload.SFTPDownload(SFTPRemote_Host, SFTPRemote_Port, SFTPUsername, SFTPPassword, tcRstDir, remotefilepath, Src_File_Name);
                                                   rsgetSFTPDetailsTarget.close();
                                                   
                                             } else if(tgtProtocolType.equalsIgnoreCase("AWS_S3")) {

                                            } 
                                             
                               			  }
                                  		  rsTargetEndpointDetails.close();
                                    	 }
                             } else {
                                           Assert.assertEquals(fileStatus, "REJECTED");
                                           NegativeFileProcessing(TC_ID, Interface_Type, Interface_Format, Interface_id, Scenario_Type, Src_File_Name, expErrCd, expRecNo, expChnCd, expAddInfo, FileLogid);
                             }
                             rsGetFileLogids.close();
                             log.info("Test Completed!");
              }
                             public void NegativeFileProcessing(String TC_ID, String interface_type, String Interface_Format, String Interface_id, String Scenario_Type, String Src_File_Name, String expErrCd, String expRecNo, String expChnCd, String expAddInfo, String fileLogID) throws ClassNotFoundException, SQLException {
                                           
                                           int noOfTags = 0;
                                           String tstResult = null, expErrDesc = null, tagName = null, actRecNo=null, actErrCd=null, actErrDesc=null, intName=null,srcOrg=null,intTypeID = null,intTypeName=null,orgName=null,chnDesc=null;
                                           
                                           DatabaseOperations DB = new DatabaseOperations();

                                           String sqlGetProcesslog = "select * from "+prop.getProperty("DatabaseName")+".[dbo].[T_PROCESS_LOG] where FILE_LOG_ID = '"
                                                                                                                                                + fileLogID + "' AND EVENT_ID = '" + expErrCd + "'";
                                           ResultSet rsgetProcesslogs = DB.sqlSelect(sqlGetProcesslog);
                                           
                                           while (rsgetProcesslogs.next()) {
                                                          actErrCd = rsgetProcesslogs.getString("EVENT_ID");
                                                          actErrDesc = rsgetProcesslogs.getString("LOG_DETAIL");
                                                          log.info(actErrCd + " " + actErrDesc);
                                           }
                                           String sqlGetInterfaceDetails = "select * from "+prop.getProperty("DatabaseName")+".[dbo].[M_INTERFACE] where ID = '" + Interface_id + "'";
                                           ResultSet rsgetInterfaceDetails = DB.sqlSelect(sqlGetInterfaceDetails);
                                           
                                           while (rsgetInterfaceDetails.next()) {
                                                          intName = rsgetInterfaceDetails.getString("NAME");
                                                          srcOrg = rsgetInterfaceDetails.getString("SOURCE_ORG");
                                                          intTypeID = rsgetInterfaceDetails.getString("INTERFACE_TYPE_ID");
                                                          log.info(intName + " " + srcOrg + " " + intTypeID);
                                           }
                                           
                                           String SqlGetInterfaceTypes = "select * from "+prop.getProperty("DatabaseName")+".[dbo].[M_INTERFACE_TYPE] where ID = '" + intTypeID + "'";
                                           ResultSet rsgetInterfaceTypeDetails = DB.sqlSelect(sqlGetInterfaceDetails);
                                           
                                           while (rsgetInterfaceTypeDetails.next()) {
                                                          intTypeName = rsgetInterfaceTypeDetails.getString("NAME");
                                           }
                                           
                                           String sqlGetEventCodesDetails = "select * from "+prop.getProperty("DatabaseName")+".[dbo].[M_EVENT_CODES] where EVENT_CODE = '" + expErrCd + "'";
                                           ResultSet rsgetEventCodeDetails = DB.sqlSelect(sqlGetEventCodesDetails);
                                           
                                           while (rsgetEventCodeDetails.next()) {
                                                          expErrDesc = rsgetEventCodeDetails.getString("EVENT_DESCRIPTION");
                                                          log.info(expErrDesc);
                                           }
                                           
                                           if(expErrDesc==null) {
                                                          Assert.fail("Error Description not Found");
                                           } else {
                                                          noOfTags = StringUtils.countMatches(expErrDesc, "<");
                                                          log.info(noOfTags);
            }
                                           
            for (int i=0; i < noOfTags; i++) {
                tagName = StringUtils.substringBetween(expErrDesc, "<", ">");
                if (tagName.equalsIgnoreCase("FileLogId")) {
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", fileLogID);
                } else if (tagName.equalsIgnoreCase("ChannelCode")) {
                              log.info(expChnCd);
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", expChnCd);
                } else if (tagName.equalsIgnoreCase("SourceFileName")) {
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", fileLogID + "_" + Src_File_Name);
                } else if (tagName.equalsIgnoreCase("SourceSystem")) {
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", interface_type);
                } else if (tagName.equalsIgnoreCase("RecordNumber")) {
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", expRecNo);
                } else if (tagName.equalsIgnoreCase("InterfaceType")) {
                              expErrDesc = expErrDesc.replace("<" + StringUtils.substringBetween(expErrDesc, "<", ">") + ">", intTypeName);
                } 
            }
            log.info(expErrDesc);

            noOfTags = 0;
            noOfTags = StringUtils.countMatches(expAddInfo, "<");
            
            for (int i=0; i < noOfTags; i++) {
                tagName = StringUtils.substringBetween(expAddInfo, "<", ">");
                if (tagName.equalsIgnoreCase("InterfaceName")) {
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", intName);
                }  else if (tagName.equalsIgnoreCase("Partner"))  {
                             String SqlGetCONFCODEDetails =  "select * from "+prop.getProperty("DatabaseName")+".[dbo].[CONF_CODE] where DOMAIN = 'ORG' AND CODE = '" + srcOrg + "'";
                             ResultSet rsGetConfCodeDetails = DB.sqlSelect(SqlGetCONFCODEDetails);
                             
                             while(rsGetConfCodeDetails.next()) {
                                           orgName = rsGetConfCodeDetails.getString("DESCRIPTION");
                             }
                             
                             String partnerName = "";
                    if (interface_type.equalsIgnoreCase("SPOT")) {
                                  partnerName = "Landmark[LMK]/" + orgName + "[" + srcOrg + "]";
                    } else {
                                  partnerName = orgName + "[" + srcOrg + "]/" + "Landmark[LMK]";
                    }
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", partnerName);
                } else if (tagName.equalsIgnoreCase("Channel Description")) {
                
                             String SqlGetCONFCODEDetails = "select * from "+prop.getProperty("DatabaseName")+".[dbo].[CONF_CODE] where CODE = '" + expChnCd + "'";
                             ResultSet rsGetConfCodeDetails = DB.sqlSelect(SqlGetCONFCODEDetails);
                             
                             while(rsGetConfCodeDetails.next()) {
                                           chnDesc = rsGetConfCodeDetails.getString("DESCRIPTION");
                             }
                             expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", chnDesc);
                }else if (tagName.equalsIgnoreCase("ChannelCode")) {
                             log.info(expChnCd);
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", expChnCd);
                }else if (tagName.equalsIgnoreCase("FileLogId")) {
                        expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", fileLogID);
                } else if (tagName.equalsIgnoreCase("SourceFileName")) {
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", fileLogID + "_" + Src_File_Name);
                } else if (tagName.equalsIgnoreCase("RecordNumber")) {
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", expRecNo);
                } else if (tagName.equalsIgnoreCase("SourceSystem")) {
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", interface_type);
                } else if (tagName.equalsIgnoreCase("InterfaceType")) {
                    expAddInfo = expAddInfo.replace("<" + StringUtils.substringBetween(expAddInfo, "<", ">") + ">", intTypeName);
                }
            }
            
            System.out.println(expAddInfo);
            expErrDesc = expErrDesc + " " + expAddInfo;
            System.out.println(actErrDesc);
            System.out.println(expErrDesc);
            
            if (actErrCd == null) {
                Assert.fail("Error Code not logged");
            } else {
                if (expErrCd.equalsIgnoreCase("REJE001") || expErrCd.equalsIgnoreCase("REJE006") || expErrCd.equalsIgnoreCase("ERRO001") || expErrCd.equalsIgnoreCase("ERRO002") || expErrCd.equalsIgnoreCase("ERRO003") || expErrCd.equalsIgnoreCase("ERRO004") || expErrCd.equalsIgnoreCase("ERRO005")) {
                               if (actErrDesc.contains(expErrDesc)) {
                                            Assert.assertTrue(true);
                               } else {
                                            Assert.fail("The Error Description doesn't match");
                               }
                }else {
                               if (actErrDesc.equalsIgnoreCase(expErrDesc)) {
                                            Assert.assertTrue(true);
                               } else {
                                            Assert.fail("The Error Description doesn't match");
                               }
                }
            }

                             }
                             
                             public void VerifyTargetFileName(String Interface_Type, String SourceFilename, String TgtFilename, String Interface_Format, String CH_CODES) throws ClassNotFoundException, SQLException {
                                    
                            	    log.info("************Entered into Target File Name Validation***********");
                            	    DatabaseOperations DB = new DatabaseOperations();
                            	    
                                    if (Interface_Type.equalsIgnoreCase("BREAK")) {
                                         Assert.assertEquals(TgtFilename.substring(0, 2),"BR"); 
                                    }else if(Interface_Type.equalsIgnoreCase("PROGRAM")) {
                                         Assert.assertEquals(TgtFilename.substring(0, 2),"PR");
                                    }else if(Interface_Type.equalsIgnoreCase("LMKIItoSAP")) {
                                    	 Assert.assertEquals(TgtFilename, SourceFilename);
                                    }else if(Interface_Type.equalsIgnoreCase("UNIVERSES")) {
                                    	 Assert.assertEquals(TgtFilename, SourceFilename.substring(0, 13));
                                    }else if(Interface_Type.equalsIgnoreCase("RECONCILEDASRUN")) {
                                    	
                                    	String SqlGetConfCodeMappingDetails = "select TO_CODE from "+prop.getProperty("DatabaseName")+".[dbo].[CONF_CODE_MAPPING] where FROM_CODE = '" + CH_CODES + "' and DOMAIN = 'ATS_asrun_IRE'";
                                        ResultSet rsGetConfCodeMappingDetails = DB.sqlSelect(SqlGetConfCodeMappingDetails);
                                        
                                        if (rsGetConfCodeMappingDetails.next()) {
                                         String TO_CODE = rsGetConfCodeMappingDetails.getString("TO_CODE");
                                         log.info("To_code is :" + TO_CODE);
                                         String TransmissionDate = SourceFilename.substring(5, 13);
                                         log.info("Transmission Date :" + TransmissionDate);
                                         Assert.assertEquals(TgtFilename, TO_CODE+TransmissionDate.substring(6, 8)+TransmissionDate.substring(4, 6)+TransmissionDate.substring(2, 4)+".COM");
                                        }else {
                                        	Assert.assertEquals(TgtFilename, SourceFilename);
                                        	log.info("Assertion Passed for Reconciled As Run");
                                        }
                                    }else if(Interface_Type.equalsIgnoreCase("ATTRIBUTION")) {
                                    	Assert.assertEquals(TgtFilename, SourceFilename);
                                    }
                                                                    
                             }

                             @AfterTest
                             public void Extent_Report_Flush() throws AddressException, MessagingException, IOException {
                                           extent.flush();
                                           
                             }
                             
              @DataProvider
              public Object[][] getdataFileProcessing() throws IOException {
                             
                             int ArraySize = 0;
                             int k =0;
                             ArrayList<ArrayList<String>> TestData = new ArrayList<ArrayList<String>>();
                             FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"//TestScenarios.xlsx") ;
                             
                             XSSFWorkbook wb = new XSSFWorkbook(fis);
                             XSSFSheet sheet = wb.getSheet("FileProcessing");
                             
                             Iterator<Row> rows =  sheet.iterator();
                                           
                             Row firstrow = rows.next();
                             
                             Iterator<Cell> cells = firstrow.cellIterator();
              
                             
                             while(cells.hasNext()) 
                             {
                                           Cell cellValue = cells.next();
                                           if (cellValue.getStringCellValue().equalsIgnoreCase("Run_Flag")) {
                                                          System.out.println("Test Case No:" + k);
                                                          break;
                                           }
                                           k++;
                             }
                             
                             while(rows.hasNext()) {
                                           Row row = rows.next();
                                           if(row.getCell(k).getStringCellValue().equalsIgnoreCase("Y")) {
                                                          TestData.add(new ArrayList<String>());
                                                          TestData.get(ArraySize).add(0, row.getCell(0).getStringCellValue());
                                                          TestData.get(ArraySize).add(1, row.getCell(2).getStringCellValue());
                                                          TestData.get(ArraySize).add(2, row.getCell(3).getStringCellValue());
                                                          TestData.get(ArraySize).add(3, row.getCell(4).getStringCellValue());
                                                          TestData.get(ArraySize).add(4, row.getCell(5).getStringCellValue());
                                                          TestData.get(ArraySize).add(5, row.getCell(6).getStringCellValue());
                                                          TestData.get(ArraySize).add(6, row.getCell(7).getStringCellValue());
                                                          TestData.get(ArraySize).add(7, row.getCell(8).getStringCellValue());
                                                          TestData.get(ArraySize).add(8, row.getCell(9).getStringCellValue());
                                                          TestData.get(ArraySize).add(9, row.getCell(10).getStringCellValue());
                                                          ArraySize++;
                                           }
                                           
                             }
                             
                             Object[][] data=new Object[ArraySize][10];
              
                             
                             for(int i=0;i<ArraySize;i++) {
                                           data [i][0] = TestData.get(i).get(0);
                                           data [i][1] = TestData.get(i).get(1);
                                           data [i][2] = TestData.get(i).get(2);
                                           data [i][3] = TestData.get(i).get(3);
                                           data [i][4] = TestData.get(i).get(4);
                                           data [i][5] = TestData.get(i).get(5);
                                           data [i][6] = TestData.get(i).get(6);
                                           data [i][7] = TestData.get(i).get(7);
                                           data [i][8] = TestData.get(i).get(8);
                                           data [i][9] = TestData.get(i).get(9);
                                           
                             }             
                             wb.close();
                             return data;
              }
              
}
