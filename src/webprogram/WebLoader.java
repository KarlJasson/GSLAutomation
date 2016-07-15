package webprogram;

import java.util.Scanner;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.FileReader;


import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

//import plantdata.PlantSample;

	public class WebLoader {
		
		private String logInUsername;
		private String logInPassword;
		
		
		public WebLoader(String serviceInput){
		
		// replace/correct the absolute path of log4j.properties
		String log4jConfPath = "/home/karljasson/workspace/Selenium for GSL Form 1/log4j/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		
		DesiredCapabilities caps = new DesiredCapabilities();
    	caps.setJavascriptEnabled(true);
    	
    	System.out.println("JavascriptEnabled: "+Boolean.toString(caps.isJavascriptEnabled()));
    	caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/bin/phantomjs");
    	caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {"--web-security=no", "--ignore-ssl-errors=yes"});
    	WebDriver driver = new PhantomJSDriver(caps);
			
		String baseUrl =  "http://btree.ocimumbio.com/irri-test/";
		WebElement myDynamicElement;

		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		try{
		
			driver.get(baseUrl);

			//// BLOCK 0001: OPENING A NEW SERVICE REQUEST ///////////////////////////////////////////////////////
				
				WebElement temp;
				
				do{
					//System.out.print("Username: ");
					//logInUsername = sc.nextLine();
					//System.out.print("Password: ");
					//logInPassword = sc.nextLine();
				
					logInUsername = "karljasson";
					logInPassword = "dakotiyan5`";
				
					driver.findElement(By.id("userId")).clear();
					driver.findElement(By.id("userId")).sendKeys(logInUsername);
					driver.findElement(By.id("password")).clear();
					driver.findElement(By.id("password")).sendKeys(logInPassword);

					driver.findElement(By.xpath("//img[@src='/irri-test/resources/styles/images/go-button.png']")).click();;
				    //temp.click();

				    try{   
				    	((JavascriptExecutor) driver).executeScript("window.confirm = function(msg) { return true; }");
				    }catch(Exception eee){
				    	
				    }
				
				    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				    
				}while(driver.findElements(By.id("HeaderSearch")).size() == 0);

				driver.get("http://btree.ocimumbio.com/irri-test/app/module/ServiceRequest/create?hasSearch=true&btreeModuleId=ServiceRequest");
				
				driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				System.out.println("Current URL: "+driver.getCurrentUrl());
				
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
				new Select(driver.findElement(By.id("ClassificationOfMaterials"))).selectByVisibleText(mappings.get("Classification of Materials"));
				//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				driver.findElement(By.id("Servicesearch")).click();
				
				switch(serviceInput){
				
					case "INF" :
						myDynamicElement = (new WebDriverWait(driver, 15))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("jqg_177491")));
						driver.findElement(By.id("jqg_177491")).click();
						break;
						
					case "PlantTrak" :
						myDynamicElement = (new WebDriverWait(driver, 15))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("jqg_363938")));
						driver.findElement(By.id("jqg_363938")).click();
						break;
						
					case "PlantTrakINF":
						myDynamicElement = (new WebDriverWait(driver, 15))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("jqg_394433")));
						driver.findElement(By.id("jqg_394433")).click();
						break;
				}
				
				myDynamicElement = (new WebDriverWait(driver, 10))
						  .until(ExpectedConditions.presenceOfElementLocated(By.id("ext-gen37")));
				driver.findElement(By.id("ext-gen37")).click();
				
				
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
				
				driver.findElement(By.className("save")).click();
				
			//// END BLOCK 0002 /////////////////**/////////////////////////////////////////////////////////////////////
				
			//// BLOCK 0003: UPLOAD PLATE FILES/////////////////////////////////////////////////////////////////////////
				driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
				
				myDynamicElement = (new WebDriverWait(driver, 25))
						  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='#ServiceRequest_RequestPlateDiv']")));
				driver.findElement(By.xpath("//a[@href='#ServiceRequest_RequestPlateDiv']")).click();
				
				for(int i=0; i<Integer.parseInt(mappings.get("Files to load")); i++){
					
					while(driver.findElements(By.id("ext-gen61")).size() == 0){
						
						
					}
					System.out.println("Uploading file number "+(i+1));
					driver.findElement(By.id("ext-gen61")).click();
					
					
					
					driver.findElement(By.id("file")).sendKeys("/home/karljasson/workspace/Selenium for GSL Form 1/"+mappings.get("FILE"+(i+1)));
				
					driver.findElement(By.className("next")).click();
					
					driver.manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
					
					driver.findElement(By.className("next")).click();

					driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
					
					if(driver.findElements(By.className("save")).size() != 0){
						try{
							Thread.sleep(5000);
						}catch(Exception eeh){
							
						}
						driver.findElement(By.className("save")).click();
						driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
					}
				}
				
			//// END BLOCK 0003 ///////////////////////////////////////////////////////////////////////////////////////

			myDynamicElement = (new WebDriverWait(driver, 25))
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='#generalTab']")));
			driver.findElement(By.xpath("//a[@href='#generalTab']")).click();
			
			String sts = driver.findElements(By.className("propertyValue")).get(0).getText();
			System.out.println("Service Request created. [ID: "+sts+"]");
				
		}catch(Exception ey){
			System.out.println("Error Encountered");
			System.out.println("Stopped at: "+driver.getCurrentUrl());
			System.out.println(ey.getMessage());
			
		}finally{
			myDynamicElement = (new WebDriverWait(driver, 20))
					  .until(ExpectedConditions.presenceOfElementLocated(By.id("ext-gen14")));
			driver.findElement(By.id("ext-gen14")).click();
			driver.findElement(By.id("ext-comp-1010")).click();
			driver.close();
		}
	}
}
