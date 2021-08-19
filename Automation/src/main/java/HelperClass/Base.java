package HelperClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import PageObject.LoginScreen;

public class Base {

	WebDriver driver;
	Properties prop;
	public WebDriver initializeDriver() throws IOException, InterruptedException
	{
		System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver_win32_New\\chromedriver.exe");
		driver = new ChromeDriver();
		
		prop = new Properties();
		FileInputStream fis = new FileInputStream("C:\\Users\\DEM49\\JAVA_PRACTICE\\Automation\\data.properties");
		prop.load(fis);
		
		driver.get(prop.getProperty("HomeURL"));
		Thread.sleep(20000);
		
		driver.manage().window().maximize();

		LoginScreen ls = new LoginScreen(driver);
		ls.mUsername().sendKeys(prop.getProperty("username"));
		ls.mPassword().sendKeys(prop.getProperty("password"));
	
		ls.mSubmit().click();
		
		System.out.println("Success");
		Thread.sleep(30000);
		
		return driver;
	}
}
