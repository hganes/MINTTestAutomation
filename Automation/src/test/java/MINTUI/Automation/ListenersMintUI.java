package MINTUI.Automation;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import HelperClass.SendEmail;

public class ListenersMintUI implements ITestListener{

	public ExtentTest Tester;
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		
	}

	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		try {
			Object instance = result.getInstance();
			Tester = CodeScreenTest.class.cast(instance).Test;
			 
			//Tester =(ExtentTest)result.getTestClass().getRealClass().getDeclaredField("Test").get(result.getInstance());
			Tester.log(Status.PASS, "Test Passed");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

	public void onTestFailure(ITestResult result) {
		// TODO Auto-generated method stub
		try {
			Object instance = result.getInstance();
			Tester = CodeScreenTest.class.cast(instance).Test;
			
			//Tester =(ExtentTest)result.getTestClass().getRealClass().getDeclaredField("Test").get(result.getInstance());
			Tester.fail(result.getThrowable());
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
