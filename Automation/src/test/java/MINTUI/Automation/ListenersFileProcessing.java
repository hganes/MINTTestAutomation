package MINTUI.Automation;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import HelperClass.SendEmail;

public class ListenersFileProcessing implements ITestListener{

	public ExtentTest Tester;
	String FileLogId = null;
	
	private static Logger log = LogManager.getLogger(ListenersFileProcessing.class.getName());
	
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		try {
			Object instance = result.getInstance();
			Tester = FileProcessingScenariosTest.class.cast(instance).Test;
			FileLogId = FileProcessingScenariosTest.class.cast(instance).FileLogid;
			log.info("The File Log id is:" + FileLogId);
			//FileLogid =(String)result.getTestClass().getRealClass().getDeclaredField("FileLogid").get(result.getInstance());
			if (FileLogId!=null) {
				Tester.log(Status.PASS, "File Log Id: " + FileLogId);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	public void onTestFailure(ITestResult result) {
		// TODO Auto-generated method stub
		try {
			Object instance = result.getInstance();
			Tester = FileProcessingScenariosTest.class.cast(instance).Test;
			FileLogId = FileProcessingScenariosTest.class.cast(instance).FileLogid; 
			log.info("The File Log id is:" +FileLogId);
			
			//Tester =(ExtentTest)result.getTestClass().getRealClass().getDeclaredField("Test").get(result.getInstance());
		    Tester.fail("The File Log id is: " + FileLogId + " " + result.getThrowable());
			//Tester.fail(result.getThrowable());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		
	}

	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		SendEmail SE = new SendEmail();
		try {
			SE.sendingEmail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
