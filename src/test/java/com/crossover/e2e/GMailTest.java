package com.crossover.e2e;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GMailTest extends TestCase{
    private WebDriver driver;
    private Properties properties = new Properties();
    String emailSubject=null;
    
    public void setUp() throws Exception {
        
        properties.load(new FileReader(new File("src/test/resources/test.properties")));
        //Dont Change below line. Set this value in test.properties file incase you need to change it..
        System.setProperty("webdriver.chrome.driver",properties.getProperty("webdriver.chrome.driver") );
        driver = new ChromeDriver();
    }
    
    public void tearDown() throws Exception {
        driver.quit();
    }

    /*
     * Please focus on completing the task
     * 
     */
    
    
    @Test
	public void testSendEmail() throws Exception{
		
		driver.manage().window().maximize();
		driver.get("https://mail.google.com/");
		
		WebDriverWait wait = new WebDriverWait(driver, 60);
		
		WebElement userElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("identifierId")));
		userElement.sendKeys(properties.getProperty("username"));
		driver.findElement(By.id("identifierNext")).click();

		WebElement passwordElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("password")));
		passwordElement.sendKeys(properties.getProperty("password"));
		driver.findElement(By.id("passwordNext")).click();
		
		WebElement composeElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and (.)='Compose']")));
		composeElement.click();

		WebElement toElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("to")));
		toElement.click();
		toElement.clear();
		toElement.sendKeys(String.format("%s@gmail.com", properties.getProperty("username")));
		
		// emailSubject and emailbody to be used in this unit test.
		emailSubject = properties.getProperty("email.subject");
		WebElement subjectElement = wait.until(ExpectedConditions.elementToBeClickable(By.name("subjectbox")));                                                                                                                                                                              
		subjectElement.clear();                                                                                                                                        
		subjectElement.sendKeys(emailSubject);                                                     
		
		String emailBody = properties.getProperty("email.body");
		WebElement emailBodyElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role = 'textbox']")));                                         
		emailBodyElement.click();                                                                                                                                      
		emailBodyElement.clear();                                                                                                                                      
		emailBodyElement.sendKeys(emailBody);
		
		//Label email as social
		WebElement moreOptions = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@data-tooltip='More options']")));
		moreOptions.click();
		WebElement label = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label']")));
		label.click();
		WebElement labelName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label as:']/following::input")));
		labelName.sendKeys("Social");
		WebElement selectLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Label as:']/following::input/following::*[text()='Social']")));
		selectLabel.click();
		
		//Send the email
		WebElement sendMailButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='button' and text()='Send']")));
		sendMailButton.click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Message sent')]")));
		
		WebElement socialSection = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@role='tab' and @aria-label='Social']")));
		socialSection.click();
		
		/*
		 * verify email subject and its body
		 */
		List<WebElement> inboxEmails = driver.findElements(By.xpath("//*[@class='zA zE']"));
		   for(WebElement email : inboxEmails){ 
		       if(email.isDisplayed() && email.getText().contains(emailSubject)){                                                                                                                                   
		           email.click();                                                                                                                                                             
		           WebElement subject = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'"+emailSubject+"')]")));          
		           WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'"+emailBody+"')]")));   
		           assertEquals("Email subject is not correct",emailSubject,subject.getText());
		           assertEquals("Email body is not corret",emailBody,body.getText());
		       }                                                                                                                                                          
		   }
		   
		   /*
		    * Verify email is starred
		    */
		   WebElement star=driver.findElement(By.xpath("//h2[text()='"+emailSubject+"']/following::*[@role='checkbox']"));
			star.click();
			Thread.sleep(5000);
			assertEquals("email is not starred","Starred",star.getAttribute("title"));
			
			/*
			 * Verify email is under social label
			 */
			WebElement lbl=driver.findElement(By.xpath("(//*[@role='button' and @title='Labels'])[2]"));
			lbl.click();
			List<WebElement> listofSelectedLabels=driver.findElements(By.xpath("//*[@role='menuitemcheckbox' and @aria-checked='true']"));
			for(WebElement ele:listofSelectedLabels){
				assertEquals("email is not under correct label","Social",ele.getText());		
			}
	}
	
}

