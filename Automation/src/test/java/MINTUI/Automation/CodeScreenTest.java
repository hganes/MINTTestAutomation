package MINTUI.Automation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.checkerframework.checker.units.qual.C;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import HelperClass.Base;
import HelperClass.DatabaseOperations;
import HelperClass.SendEmail;
import HelperClass.TakeScreenShot;
import PageObject.CodeScreenPOM;
import PageObject.FileinformationScreen;
import PageObject.LoginScreen;

public class CodeScreenTest extends Base{

	WebDriver driver;
	ExtentReports extent;
	TakeScreenShot TS = new TakeScreenShot();
	public ExtentTest Test;
	
	@BeforeTest
	public void openCodeScreen() throws IOException, InterruptedException {
		
		driver = initializeDriver();
		Thread.sleep(34000);
		FileinformationScreen lf = new FileinformationScreen(driver);
		lf.mConfigLink().click();
		lf.mlinkTextCode().click();
		Thread.sleep(30000);	
	}
	
	@BeforeTest
	public void Extent_Report() throws IOException, InterruptedException {
		
		String path = System.getProperty("user.dir")+"\\reports\\MINTUISanityTestExecutionReport.html";
		ExtentSparkReporter Rep = new ExtentSparkReporter(path);
		Rep.config().setReportName("MINT UI Sanity Automation Report");
		Rep.config().setDocumentTitle("MINT UI Sanity Test Report");
		
	    extent = new ExtentReports();
		extent.attachReporter(Rep);
		extent.setSystemInfo("Tester", "Deepak");
	}
	
	
	@Test(dataProvider = "getdataCodeScreen")
	public void addCode(String TC_id, String Domain, String Code, String Description, String EffectiveFrom, String EffectiveTo, String isActive) throws InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException {
		
		Test = extent.createTest(TC_id + " Add Code " + Code);
		CodeScreenPOM CS = new CodeScreenPOM(driver);
		CS.mcreateCode().click();
		
		DatabaseOperations DB = new DatabaseOperations();
		String SQLfindChannelCode = "SELECT * FROM [AdTechHub_Phase1].[dbo].[CONF_CODE] WHERE code = '"+Code+"' and domain = '"+Domain+"'";
		System.out.println(SQLfindChannelCode);
		ResultSet rsConfCode = DB.sqlSelect(SQLfindChannelCode);
		
		if (rsConfCode.next())
        {
			String SQLDeleteChannelCode = "DELETE FROM [AdTechHub_Phase1].[dbo].[CONF_CODE] WHERE code = '"+Code+"' and domain = '"+Domain+"'";
        	System.out.println(SQLDeleteChannelCode);
			DB.sqlDelete(SQLDeleteChannelCode);
        } 
        
		System.out.println(Domain);
		System.out.println(Code);
		System.out.println(Description);
		CS.mdomain().clear();
		CS.mdomain().sendKeys(Domain);
		CS.mcode().clear();
		CS.mcode().sendKeys(Code);
		CS.mdescription().clear();
		CS.mdescription().sendKeys(Description);
		CS.meffectivefrom().click();
		CS.mSelectEffectiveDate(EffectiveFrom);
		CS.meffectiveto().click();
		CS.mSelectEffectiveDate(EffectiveTo);
		System.out.println("Is active to be selected");
	    CS.misActive().click();
		if(isActive=="N") 
			CS.misActiveNo().click();
		else {
			CS.misActiveYes().click();
		}
		System.out.println("Is active selected");
		Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Add Code " + Code + "_1" ,driver), TC_id + " Add Code " + Code + "_1");
		CS.mSave().click();
		Thread.sleep(5000);
		
		String sDialogTitle = CS.muiDialogTitle().getText();
		String spopMessage = CS.mpopupMessage().getText();
		Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Add Code " + Code + "_2" ,driver), TC_id + " Add Code " + Code + "_2");
		CS.mokButton().click();
		CS.mclose().click();
		
		Assert.assertEquals(sDialogTitle, "Success");
		Assert.assertEquals(spopMessage, "Record added Successfully!");
		Test.getStatus();
		ResultSet rsAfterInsertConfCode = DB.sqlSelect(SQLfindChannelCode);
		
		if(!rsAfterInsertConfCode.next()) {
			Assert.fail();
		}
		
	}
	
	@Test(dataProvider = "getdataCodeScreenUpdate")
	public void updateCode(String TC_id, String Domain, String Code, String Description, String EffectiveFrom, String EffectiveTo, String isActive, String UpdatingField) throws InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException {
		
		Test = extent.createTest(TC_id + " Update Code " + Code);
		CodeScreenPOM CS = new CodeScreenPOM(driver);
		CS.mdomainselection().click();
		System.out.println("Domain dropdown clicked");
		driver.findElement(By.xpath("//li[@id='CHKEEP']")).click();
		System.out.println("Domain dropdown selected");
		//driver.findElement(By.cssSelector("input.btn.btn-search")).click();
		CS.msubmit().click();
		System.out.println("Button Clicked");
		
		Thread.sleep(10000);
	  Boolean iscodefound = false;
	
	  int nooftabs = CS.mtabindex().size();
	  System.out.println("No.of tabs: " + nooftabs);
	  for (int j = 0;j < nooftabs;j++) {
		  
		  int noofrecords = CS.mrecordgrid().size();
		  System.out.println("Total No. of Records:" + noofrecords);
	  for (int k = 0;k < noofrecords;k++) {
		
		 if (CS.mCodeColumn().get(k).getText().equalsIgnoreCase(Code)) {
		 System.out.println(CS.mCodeColumn().get(k).getText());
		 CS.mActionColumn().get(k).click();
		 Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Update Code " + Code + "_1",driver), TC_id + " Update Code " + Code + "_1");
		 CS.mupdate().click();
		 iscodefound = true;
		 Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Update Code " + Code + "_2",driver), TC_id + " Update Code " + Code + "_2");
		 if (UpdatingField.equalsIgnoreCase("Description")) {
		 CS.mdescription().clear();
		 CS.mdescription().sendKeys(Description);
		 }
		 if (UpdatingField.equalsIgnoreCase("isActive")) {
		 CS.misActive().click();
		 if(isActive=="N") 
		 CS.misActiveNo().click();
		 else
		 CS.misActiveYes().click();
		 }
		 
		 //String EffectiveFromDate = EffectiveFrom;
		 //String EffectiveToDate = EffectiveTo;
		 if (UpdatingField.equalsIgnoreCase("EffectiveFrom")) {
		 CS.meffectivefrom().click();
		 CS.mSelectEffectiveDate(EffectiveFrom);
		 }
		 if(UpdatingField.equalsIgnoreCase("EffectiveTo")) {
		 CS.meffectiveto().click();
		 CS.mSelectEffectiveDate(EffectiveTo);
		 }
		 Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Update Code " + Code + "_3",driver), TC_id + " Update Code " + Code + "_3");
		 CS.mSave().click();
		 Thread.sleep(5000);
			
		 String sDialogTitle = CS.muiDialogTitle().getText();
	     String spopMessage = CS.mpopupMessage().getText();
	     Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Update Code " + Code + "_4",driver), TC_id + " Update Code " + Code + "_4");
		 CS.mokButton().click();
		 CS.mclose().click();
			
		 Assert.assertEquals(sDialogTitle, "Success");
		 Assert.assertEquals(spopMessage, "Record updated Successfully!");
		  }
		  if (iscodefound) {
			  break;
		  }
		  }
	  if (j<nooftabs-1) {
	  CS.mtabindex().get(j+1).click();
	  //driver.findElement(By.linkText("2")).click();
	  }
	  }
	  if (!iscodefound) {
		  Assert.fail();
	  }
	  }
	
	@Test(dataProvider = "getdataCodeScreenVerify")
	public void verifyCode(String TC_id, String Domain, String Code, String Description, String EffectiveFrom, String EffectiveTo, String isActive) throws InterruptedException, ClassNotFoundException, SQLException, IOException, ParseException {
		
		Test = extent.createTest(TC_id + " Verify Code Screen ");
		int Count = 0;
		int totalNoofRecords = 0;
		int startnoofrecords = 0;
		int endnoofrecords = 0;
		String StatementtobeValidated, SQLfindChannelCode;
		FileinformationScreen lf = new FileinformationScreen(driver);
		lf.mConfigLink().click();
		lf.mlinkTextCode().click();
		Thread.sleep(10000);
		CodeScreenPOM CS = new CodeScreenPOM(driver);
		String EffectiveFromDate = CS.meffectiveFromCodeScreen().getAttribute("value").toString();
		System.out.println(EffectiveFromDate);
		String EffectiveToDate = CS.meffectiveToCodeScreen().getAttribute("value").toString();
		System.out.println(EffectiveToDate);
	
		
		DatabaseOperations DB = new DatabaseOperations();
		if (Domain.isEmpty()) {
		SQLfindChannelCode = "select * from [AdTechHub_Phase1].[dbo].[CONF_CODE] where EFFECTIVE_FROM <= '"+ EffectiveFromDate +"' and EFFECTIVE_TO >= '" + EffectiveToDate + "'";
		}
		else {
		SQLfindChannelCode = "select * from [AdTechHub_Phase1].[dbo].[CONF_CODE] where EFFECTIVE_FROM <= '"+ EffectiveFromDate +"' and EFFECTIVE_TO >= '" + EffectiveToDate + "' and DOMAIN = '" + Domain + "'";
		CS.mdomainselection().click();
		System.out.println("Domain dropdown clicked");
		driver.findElement(By.xpath("//li[@id='"+Domain+"']")).click();
		System.out.println("Domain dropdown selected");
		CS.msubmit().click();
		Thread.sleep(2000);
		}
		System.out.println(SQLfindChannelCode);
		ResultSet rsConfCode = DB.sqlSelect(SQLfindChannelCode);
		
		while(rsConfCode.next())
		{
		Count++;
		}
	    System.out.println(Count);
	    
	    int nooftabs = CS.mtabindex().size();
	    int LastTab = Integer.parseInt(CS.mtabindex().get(nooftabs-1).getText());
		System.out.println("No.of tabs: " + nooftabs);
		System.out.println("Last tab is: " + LastTab);
		
		for (int j = 0;j < LastTab;j++) {
			  Test.addScreenCaptureFromPath(TS.ToTakeScreenshot(TC_id + " Verify Code Screen_" + j,driver), TC_id + " Verify Code Screen_" + j);
			  if(j>0){
				 CS.mNexticon().click();
			  }
			  int noofrecords = CS.mrecordgrid().size();
			  startnoofrecords = totalNoofRecords+1;
			  endnoofrecords = totalNoofRecords+noofrecords;
			  StatementtobeValidated = "Showing "+startnoofrecords+" to "+endnoofrecords+" of "+Count+" entries";
			  System.out.println("No. of Records:" + noofrecords);
			  System.out.println(StatementtobeValidated);
			  Assert.assertEquals(CS.mGridTextBelow().getText(), StatementtobeValidated);
			  totalNoofRecords = totalNoofRecords + noofrecords;
		}
		
		System.out.println("Total No. of Records:" + totalNoofRecords);
		Assert.assertEquals(Count, totalNoofRecords);
	}
        
		
	
	@DataProvider
	public Object[][] getdataCodeScreen() throws IOException {
		
		int ArraySize = 0;
		int k =0;
		ArrayList<ArrayList<String>> TestData = new ArrayList<ArrayList<String>>();
		FileInputStream fis = new FileInputStream("C://Users//DEM49//JAVA_PRACTICE//Automation//TestScenarios.xlsx") ;
		
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheet("CodeScreen");
		
		Iterator<Row> rows =  sheet.iterator();
			
		Row firstrow = rows.next();
		
		Iterator<Cell> cells = firstrow.cellIterator();
	
		
		while(cells.hasNext()) 
		{
			Cell cellValue = cells.next();
			if (cellValue.getStringCellValue().equalsIgnoreCase("Behaviour")) {
				System.out.println("The column number is:" + k);
				break;
			}
			k++;
		}
		
		while(rows.hasNext()) {
			Row row = rows.next();
			if(row.getCell(k).getStringCellValue().equalsIgnoreCase("Add")) {
				TestData.add(new ArrayList<String>());
				TestData.get(ArraySize).add(0, row.getCell(0).getStringCellValue());
				TestData.get(ArraySize).add(1, row.getCell(3).getStringCellValue());
				TestData.get(ArraySize).add(2, row.getCell(4).getStringCellValue());
				TestData.get(ArraySize).add(3, row.getCell(5).getStringCellValue());
				TestData.get(ArraySize).add(4, row.getCell(6).getStringCellValue());
				TestData.get(ArraySize).add(5, row.getCell(7).getStringCellValue());
				TestData.get(ArraySize).add(6, row.getCell(8).getStringCellValue());
				ArraySize++;
			}
			
		}
		
		Object[][] data=new Object[ArraySize][7];
	
		
		for(int i=0;i<ArraySize;i++) {
			data [i][0] = TestData.get(i).get(0);
			data [i][1] = TestData.get(i).get(1);
			data [i][2] = TestData.get(i).get(2);
			data [i][3] = TestData.get(i).get(3);
			data [i][4] = TestData.get(i).get(4);
			data [i][5] = TestData.get(i).get(5);
			data [i][6] = TestData.get(i).get(6);
			
		}	
		
		
		return data;
		
	
	}
	
	@DataProvider
	public Object[][] getdataCodeScreenUpdate() throws IOException {
		
		int ArraySize = 0;
		int k =0;
		ArrayList<ArrayList<String>> TestData = new ArrayList<ArrayList<String>>();
		FileInputStream fis = new FileInputStream("C://Users//DEM49//JAVA_PRACTICE//Automation//TestScenarios.xlsx") ;
		
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheet("CodeScreen");
		
		Iterator<Row> rows =  sheet.iterator();
			
		Row firstrow = rows.next();
		
		Iterator<Cell> cells = firstrow.cellIterator();
	
		
		while(cells.hasNext()) 
		{
			Cell cellValue = cells.next();
			if (cellValue.getStringCellValue().equalsIgnoreCase("Behaviour")) {
				System.out.println("The column number is:" + k);
				break;
			}
			k++;
		}
		
		while(rows.hasNext()) {
			Row row = rows.next();
			if(row.getCell(k).getStringCellValue().equalsIgnoreCase("Update")) {
				TestData.add(new ArrayList<String>());
				TestData.get(ArraySize).add(0, row.getCell(0).getStringCellValue());
				TestData.get(ArraySize).add(1, row.getCell(3).getStringCellValue());
				TestData.get(ArraySize).add(2, row.getCell(4).getStringCellValue());
				TestData.get(ArraySize).add(3, row.getCell(5).getStringCellValue());
				TestData.get(ArraySize).add(4, row.getCell(6).getStringCellValue());
				TestData.get(ArraySize).add(5, row.getCell(7).getStringCellValue());
				TestData.get(ArraySize).add(6, row.getCell(8).getStringCellValue());
				TestData.get(ArraySize).add(7, row.getCell(9).getStringCellValue());
				//System.out.println("Loaded" + ArraySize + "Element from the sheet");
				ArraySize++;
			}
			
		}

		Object[][] data=new Object[ArraySize][8];
	
		
		for(int i=0;i<ArraySize;i++) {
			data [i][0] = TestData.get(i).get(0);
			data [i][1] = TestData.get(i).get(1);
			data [i][2] = TestData.get(i).get(2);
			data [i][3] = TestData.get(i).get(3);
			data [i][4] = TestData.get(i).get(4);
			data [i][5] = TestData.get(i).get(5);
			data [i][6] = TestData.get(i).get(6);
			data [i][7] = TestData.get(i).get(7);
			
		}	
		
		/* data[0][0]="CHKEEP";
		data[0][1]="DEFE";
		data[0][2]="Sanity Testing1";
		//1st row
		data[1][0]="BARBCODE";
		data[1][1]="01234";
		data[1][2]= "Sony Crime Channel Testing"; */
		return data;
		
	}
	
	
	@AfterTest
	public void Extent_Report_Flush() throws AddressException, MessagingException, IOException {
		extent.flush();
		
	}
	
	@DataProvider
	public Object[][] getdataCodeScreenVerify() throws IOException {
		
		int ArraySize = 0;
		int k =0;
		ArrayList<ArrayList<String>> TestData = new ArrayList<ArrayList<String>>();
		FileInputStream fis = new FileInputStream("C://Users//DEM49//JAVA_PRACTICE//Automation//TestScenarios.xlsx") ;
		
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheet("CodeScreen");
		
		Iterator<Row> rows =  sheet.iterator();
			
		Row firstrow = rows.next();
		
		Iterator<Cell> cells = firstrow.cellIterator();
	
		
		while(cells.hasNext()) 
		{
			Cell cellValue = cells.next();
			if (cellValue.getStringCellValue().equalsIgnoreCase("Behaviour")) {
				System.out.println("The column number is:" + k);
				break;
			}
			k++;
		}
		
		while(rows.hasNext()) {
			Row row = rows.next();
			if(row.getCell(k).getStringCellValue().equalsIgnoreCase("Verify")) {
				TestData.add(new ArrayList<String>());
				TestData.get(ArraySize).add(0, row.getCell(0).getStringCellValue());
				TestData.get(ArraySize).add(1, row.getCell(3).getStringCellValue());
				TestData.get(ArraySize).add(2, row.getCell(4).getStringCellValue());
				TestData.get(ArraySize).add(3, row.getCell(5).getStringCellValue());
				TestData.get(ArraySize).add(4, row.getCell(6).getStringCellValue());
				TestData.get(ArraySize).add(5, row.getCell(7).getStringCellValue());
				TestData.get(ArraySize).add(6, row.getCell(8).getStringCellValue());
				ArraySize++;
			}
			
		}
		
		Object[][] data=new Object[ArraySize][7];
	
		
		for(int i=0;i<ArraySize;i++) {
			data [i][0] = TestData.get(i).get(0);
			data [i][1] = TestData.get(i).get(1);
			data [i][2] = TestData.get(i).get(2);
			data [i][3] = TestData.get(i).get(3);
			data [i][4] = TestData.get(i).get(4);
			data [i][5] = TestData.get(i).get(5);
			data [i][6] = TestData.get(i).get(6);
			
		}	
		
		
		return data;
		
	
	}
}
		
	