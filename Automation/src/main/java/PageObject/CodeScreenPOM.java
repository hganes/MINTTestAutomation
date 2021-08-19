package PageObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CodeScreenPOM {
	public WebDriver driver;
	
	By createCode = By.xpath("//button[@id='createCode']");
	By domain = By.id("domain");
	By code = By.id("code");
	By description = By.id("description");
	By effectivefrom = By.id("effectiveFrom");
	By effectiveto = By.id("effectiveTo");
	By datepicker = By.xpath("//td[@data-handler='selectDay']/a");
	By saveButton = By.id("saveButton");
	By uiDialogTitle = By.className("ui-dialog-title");
	By popupMessage = By.id("success");
	By okButton = By.xpath("//div[@class='ui-dialog-buttonset']/button");
	By closeButton = By.className("close");
	By domainselection = By.className("styledSelect");
	By submit = By.cssSelector("input.btn.btn-search");
	//By codeTableCodeColumn = By.xpath("//*[@id=\"codeTable\"]/tbody/tr[1]");
	By tabindex = By.xpath("//a[@tabindex='0']");
	By nextlink = By.id("nextPage");
	By gridrecord = By.xpath("//*[@id='codeTable']/tbody/tr");
	By CodeColumn = By.cssSelector("tr[role='row'] td:nth-child(2)");
	//By ActionColumn = By.cssSelector("tr[role='row'] td:nth-child(7)");
	By ActionColumn = By.xpath("//td[@data-dt-column='7']");
	By isActive = By.xpath("//select[@id='isActive']/following-sibling::div");
	By isActiveno = By.cssSelector("li.N");
	By isActiveyes = By.cssSelector("li.Y");
	By update = By.linkText("Update");
	By datepickermonthandyear = By.xpath("//div[@class='ui-datepicker-title']");
	By calenderArrowNext = By.cssSelector("a.ui-datepicker-next.ui-corner-all");
	By calendarArrowPrev = By.cssSelector("a.ui-datepicker-prev.ui-corner-all");
	By fromDateCodeScreen = By.id("ifcFromDate");
	By toDateCodeScreen = By.id("ifcToDate");
	By belowGridText = By.id("codeTable_info");
	
	public CodeScreenPOM(WebDriver fdriver) {
		driver = fdriver;
	}
	public WebElement mcreateCode() {
		WebElement createCodebutton = driver.findElement(createCode);
		return createCodebutton;
	}
	
	public WebElement mdomain() {
		WebElement domaintextbox = driver.findElement(domain);
		return domaintextbox;
	}
	
	public WebElement misActive() {
		WebElement isActivedropdown = driver.findElement(isActive);
		return isActivedropdown;
	}
	
	public WebElement misActiveNo() {
		WebElement isActiveN = driver.findElement(isActiveno);
		return isActiveN;
	}
	
	public WebElement mupdate() {
		WebElement updateLink  = driver.findElement(update);
		return updateLink;
	}
	
	public WebElement misActiveYes() {
		WebElement isActiveY = driver.findElement(isActiveyes);
		return isActiveY;
	}
	
	
	public WebElement mcode() {
		WebElement codetextbox = driver.findElement(code);
		return codetextbox;
	}
	
	public WebElement mdescription() {
		WebElement descriptiontextbox = driver.findElement(description);
		return descriptiontextbox;
	}
	
	public WebElement mdomainselection() {
		WebElement domainselectiondropdown = driver.findElement(domainselection);
		return domainselectiondropdown;
	}
	
	public WebElement msubmit() {
		WebElement submitclick = driver.findElement(submit);
		return submitclick;
	}
	
	
	public WebElement meffectivefrom() {
		WebElement effectiveFromclick = driver.findElement(effectivefrom);
		return effectiveFromclick;
	}
	
	public WebElement meffectiveto() {
		WebElement effectiveToclick = driver.findElement(effectiveto);
		return effectiveToclick;
	}
	
	public WebElement mGridTextBelow() {
		WebElement BelowGridText = driver.findElement(belowGridText);
		return BelowGridText;
	}
	
	public List<WebElement> mtabindex() {
		List<WebElement> tabindexnumber = driver.findElements(tabindex);
		return tabindexnumber;
	}
	
	public WebElement mNexticon() {
		WebElement Nextlink = driver.findElement(nextlink);
		return Nextlink;
	}
	
	public List<WebElement> mCodeColumn() {
		List<WebElement> CodeColumningrid = driver.findElements(CodeColumn);
		return CodeColumningrid;
	}
	
	public List<WebElement> mActionColumn() {
		List<WebElement> ActionColumningrid = driver.findElements(ActionColumn);
		return ActionColumningrid;
	}
	
	public List<WebElement> mrecordgrid() {
		List<WebElement> gridrow = driver.findElements(gridrecord);
		return gridrow;
	}
	
	public List<WebElement> mdatepicker() {
		List<WebElement> datePicker = driver.findElements(datepicker);
		return datePicker;
	}
	public WebElement mSave() {
		WebElement savebutton = driver.findElement(saveButton);
		return savebutton;
	}
	public WebElement muiDialogTitle() {
		WebElement uiDialogTitleText = driver.findElement(uiDialogTitle);
		return uiDialogTitleText;
	}
	
	public void mSelectEffectiveDate(String dateFromexcelsheet) throws ParseException, InterruptedException {
		
		String dateFromUI = driver.findElement(datepickermonthandyear).getText();
		 
		 System.out.println("Date From UI:" + dateFromUI);
			
		 Date datetobeSelected = new SimpleDateFormat("dd-MMM-yyyy").parse(dateFromexcelsheet);
		 Date dateinUI = new SimpleDateFormat("MMMM yyyy").parse(dateFromUI);
		
		 int monthDiff = Months.monthsBetween(new DateTime(dateinUI).withDayOfMonth(1),new DateTime(datetobeSelected).withDayOfMonth(1)).getMonths();
		 System.out.println("The month Difference:" + monthDiff);
		 
		 Boolean isFutureDate = false;
		 if (monthDiff>0) {
			 isFutureDate=true;
		 }else {
			 monthDiff = -1 * monthDiff;
		 }
		 for (int i=0;i<monthDiff;i++) {
			 if(isFutureDate) {
			 driver.findElement(calenderArrowNext).click();
			 }else
			 driver.findElement(calendarArrowPrev).click(); 
		 }
		 
		 int days = driver.findElements(datepicker).size();
		int iStartDateRequired = Integer.parseInt(new SimpleDateFormat("dd").format(datetobeSelected));
		 System.out.println("The date to be selected:" +iStartDateRequired);
			
			for(int i=0;i<days;i++) {
				if(Integer.parseInt(driver.findElements(datepicker).get(i).getText())==iStartDateRequired){
					driver.findElements(datepicker).get(i).click();
					break;
				}
			}
		 Thread.sleep(2000);	
		 
	}
	
	public WebElement meffectiveFromCodeScreen() {
		WebElement effectiveFromCodeScreen = driver.findElement(fromDateCodeScreen);
		return effectiveFromCodeScreen;
	}
	
	public WebElement meffectiveToCodeScreen() {
		WebElement effectiveToCodeScreen = driver.findElement(toDateCodeScreen);
		return effectiveToCodeScreen;
	}
	
	public WebElement mpopupMessage() {
		WebElement uipopupmessage = driver.findElement(popupMessage);
		return uipopupmessage;
	}
	public WebElement mokButton() {
		WebElement uiokButton = driver.findElement(okButton);
		return uiokButton;
	}
	public WebElement mclose() {
		WebElement uiclosebutton = driver.findElement(closeButton);
		return uiclosebutton;
	}
	
	
	
}
