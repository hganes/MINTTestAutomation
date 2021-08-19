package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginScreen {
	
	public WebDriver driver;
	
	By username = By.id("username");
	By password = By.id("password");
	By submit = By.name("submit");
	
	

	public LoginScreen(WebDriver fdriver) {
		driver = fdriver;
		
	}
	

	public WebElement mUsername() {
		WebElement uname = driver.findElement(username);
		return uname;
	}
	
	public WebElement mPassword() {
		WebElement pword = driver.findElement(password);
		return pword;
	}
	
	public WebElement mSubmit() {
		WebElement smit = driver.findElement(submit);
		return smit;
	}
}
