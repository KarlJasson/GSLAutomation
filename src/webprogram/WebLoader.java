package webprogram;

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.FileReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

//import plantdata.PlantSample;

	public class WebLoader {
		
		private String logInUsername;
		private String logInPassword;
		
		
		public WebLoader(){
		
			
		System.setProperty("phantomjs.binary.path", "/usr/bin/phantomjs.exe");
			
		String log4jConfPath = "/home/karljasson/workspace/Selenium for GSL Form 1/log4j/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		
		DesiredCapabilities dc = new DesiredCapabilities();
		dc.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		
		//WebDriver driver = new FirefoxDriver(dc);
		
		//DesiredCapabilities caps = new DesiredCapabilities();
		
		//caps.setCapability("takesScreenshot", false);
		//caps.setCapability("phantomjs.binary.path", "/usr/bin/phantomjs");
		//caps.setCapability("handlesAlerts", true);
		
		
		DesiredCapabilities capabilities = new DesiredCapabilities();
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		capabilities = DesiredCapabilities.phantomjs();
		cliArgsCap.add("--web-security=false");
		cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--ignore-ssl-errors=true");
		capabilities.setCapability("takesScreenshot", false);
		
		capabilities.setCapability("handlesAlerts", true);
		
		capabilities.setCapability(
		    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		capabilities.setCapability(
		    PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
		        new String[] { "--logLevel=2" });
		capabilities.setCapability("phantomjs.binary.path", "/usr/bin/phantomjs");
		WebDriver driver = new PhantomJSDriver(capabilities);
		
		//WebDriver driver = new PhantomJSDriver(caps);
		//WebDriver driver2 = new PhantomJSDriver(caps);		
		//WebDriver driver = new PhantomJSDriver();
		
		//System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		//WebDriver driver = new ChromeDriver(dc);
		
		//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
			
		String baseUrl =  "http://btree.ocimumbio.com/irri-test/";
		WebElement myDynamicElement;

	
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		try{
		
			driver.get(baseUrl);

			//// BLOCK 0001: OPENING A NEW SERVICE REQUEST ///////////////////////////////////////////////////////
				
				WebElement temp;
				//Scanner sc = new Scanner(System.in);
				
				do{
					//System.out.print("Username: ");
					//logInUsername = sc.nextLine();
					//System.out.print("Password: ");
					//logInPassword = sc.nextLine();
					System.out.println("LOGGING IN");
					logInUsername = "karljasson";
					
					logInPassword = "dakotiyan5`";
				
					driver.findElement(By.id("userId")).clear();
					driver.findElement(By.id("userId")).sendKeys(logInUsername);
					driver.findElement(By.id("password")).clear();
					driver.findElement(By.id("password")).sendKeys(logInPassword);

				    try{
				    	temp = driver.findElement(By.xpath("//img[@src='/irri-test/resources/styles/images/go-button.png']"));
					    temp.click();
				    	if(driver.switchTo().alert() != null){
				    		Alert ale = driver.switchTo().alert();
				    		
				    		ale.accept();
				    	}
				    }catch(Exception eee){}
				
				    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				    
				}while(driver.findElements(By.id("HeaderSearch")).size() == 0);
				
				////////////////////////////// BLOOOOCKK //////////////////////////////////////////////
				
				// ChromDriver Implementation //////////
				Actions action = new Actions(driver);
				
				System.out.println("REDIRECTING");

				driver.get("http://btree.ocimumbio.com/irri-test/app/module/ServiceRequest/create?hasSearch=true&btreeModuleId=ServiceRequest");
				
				driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				
			//// END BLOCK 0001 //////////////////////////////////////////////////////////////////////////////////////
				
			//// BLOCK 0002: TRANSFER SENDER DETAILS /////////////////////////////////////////////////////////////////
				
				HashMap<String, String> mappings = new HashMap<String, String>();
				
				try{
					System.out.println("TRANSFERRING DATA TO LIMS");
					BufferedReader br = new BufferedReader(new FileReader("requestInfo.txt"));
					
					String str = "";
					while((str=br.readLine())!=null){ // br reads lines until it encounters End of File
						String[] valuePair = str.trim().split(","); // The entire line is first trimmed of leading and trailing whitespace, then split
						mappings.put(valuePair[0], valuePair[1]);
					}
					br.close();
				}catch(Exception e){
				}
				
				myDynamicElement = (new WebDriverWait(driver, 10))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("ClassificationOfMaterials")));
				System.out.println("Classification of Materials");
				new Select(driver.findElement(By.id("ClassificationOfMaterials"))).selectByVisibleText(mappings.get("Classification of Materials"));
				//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				System.out.println("Click 'search'");	
				try{
					driver.findElement(By.id("Servicesearch")).click();
				}catch(Exception e0001){
					System.out.println("Click 'search' failed");	
				}
				
				
				myDynamicElement = (new WebDriverWait(driver, 15))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("jqg_177491")));
				// TO DO: Add a switch-case for what service to select
				System.out.println("click infinium");
				driver.findElement(By.id("jqg_177491")).click();
				
				
				
				myDynamicElement = (new WebDriverWait(driver, 10))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("ext-gen37")));
				driver.findElement(By.id("ext-gen37")).click();
				System.out.println("confirm select infinium");
				
				System.out.println("Select recipient Individual");
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("RecepientUser")));
				new Select(driver.findElement(By.id("RecepientUser"))).selectByVisibleText(mappings.get("Recipient Individual"));

				System.out.println("sample type");
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("SampleType")));
				new Select(driver.findElement(By.id("SampleType"))).selectByVisibleText(mappings.get("Sample Type"));

				System.out.println("sender");
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("Sender")));
				new Select(driver.findElement(By.id("Sender"))).selectByVisibleText(mappings.get("Sender"));
				
				System.out.println("adding comments");
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("Comments")));
				driver.findElement(By.id("Comments")).sendKeys("Study: "+mappings.get("Study")+"\n");
				driver.findElement(By.id("Comments")).sendKeys("Purpose of genotyping: "+mappings.get("Purpose of genotyping")+"\n");
				driver.findElement(By.id("Comments")).sendKeys("IRS/Supervisor: "+mappings.get("IRS/Supervisor")+"\n");
				driver.findElement(By.id("Comments")).sendKeys("Division: "+mappings.get("Division")+"\n");
				driver.findElement(By.id("Comments")).sendKeys("Date submitted: "+mappings.get("Date submitted"));
				
				
				driver.findElement(By.className("save")).sendKeys(Keys.RETURN);
				driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				
				
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='#ServiceRequest_RequestPlateDiv']")));
				driver.findElement(By.xpath("//a[@href='#ServiceRequest_RequestPlateDiv']")).click();
				//driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
				
				for(int i=0; i<Integer.parseInt(mappings.get("Files to load")); i++){
				//for(int i=0; i<1; i++){
					
					while(driver.findElements(By.id("ext-gen61")).size() == 0){
						
						
					}
					System.out.println("Uploading file number "+(i+1));
					driver.findElement(By.id("ext-gen61")).click();
					
					
					
					driver.findElement(By.id("file")).sendKeys("/home/karljasson/workspace/Selenium for GSL Form 1/"+mappings.get("FILE"+(i+1)));
					driver.findElement(By.className("next")).sendKeys(Keys.RETURN);
					driver.manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
					
					
					
					driver.findElement(By.className("next")).sendKeys(Keys.RETURN);
					//System.out.println("Next 2");
					driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
					
					if(driver.findElements(By.className("save")).size() != 0){
						try{
							Thread.sleep(5000);
						}catch(Exception eeh){
							
						}
						driver.findElement(By.className("save")).sendKeys(Keys.RETURN);
						driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
					}
					
					
				}
				////////////////////*///////////////////
				
			//// END BLOCK 0002 /////////////////**/////////////////////////////////////////////////////////////////////
				System.out.println("FINAL TOUCHES");
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='#generalTab']")));
				
				driver.findElement(By.xpath("//a[@href='#generalTab']")).click();
				//temp = driver.FindElements(By.className("propertyValue"));
				String sts = driver.findElements(By.className("propertyValue")).get(0).getText();
				//String sts = (String) driver.findElement(By.xpath("//input[@name='.list[15].fieldName']")).getAttribute("id");
				System.out.println("Service Request created. [ID: "+sts+"]");
			///////////////////////////////*///////// END BLOOOOOCK//////////////////////////////////
		}catch(Exception ey){
			System.out.println("Error Encountered");
			System.out.println(ey.getMessage());
			
		}finally{
			System.out.println("LOGGING OUT");
			myDynamicElement = (new WebDriverWait(driver, 10))
					  .until(ExpectedConditions.presenceOfElementLocated(By.id("ext-gen14")));
			driver.findElement(By.id("ext-gen14")).click();
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.findElement(By.id("ext-comp-1010")).click();
			
			driver.close();
			System.out.println("DONE");
		}
	}
}
