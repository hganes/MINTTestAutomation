package PageObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FileinformationScreen {
	public WebDriver driver;
	
	By ConfigLink = By.xpath("//*[@id='liconfig']");
	By linkTextCode = By.linkText("Code");
	
	public FileinformationScreen(WebDriver fdriver) {
		driver = fdriver;
	}
	public WebElement mConfigLink() {
		WebElement configlink = driver.findElement(ConfigLink);
		return configlink;
	}
	
	public WebElement mlinkTextCode() {
		WebElement linktextCode = driver.findElement(linkTextCode);
		return linktextCode;
	}
	
}
