package serviceRequest;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileWriter;
//import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import plantdata.GenotypingService;
//import plantdata.PlantSample;
import webprogram.WebLoader;

import java.util.Date;
import java.util.ArrayList;
//import java.time.Month;


/**
 * @author Pios, Karl Jasson
 * @author karljasson@gmail.com
 */
public class MainProgram {
	
	//public String defaultOrderType = "Normal";
	//public String defaultRecipientIndividual = "Annalhea Jarana";
	//public String defaultSampleType = "GSLDNA";
	
	public static void main(String [] args){
	
		ArrayList<GenotypingService> serviceList = new ArrayList<GenotypingService>();
		
		// Strings to store data in the header portion of Service Requests
		String requestorID;
		String studyType;
		String supervisor;
		String division;
		String purpose;
		String dateSubmitted;
		
		// default values for required information not found in Form 1
		String defaultOrderType = "Normal";
		String defaultRecipientIndividual = "Annalhea Jarana";
		String defaultSampleType = "GSLDNA";
		
		// String serviceAvailed determine which GSL service is availed
		String serviceAvailed = "NONE";
		
		// indicates the total number of samples
		int totalNumberOfSamples = 0;
		
		// if at least one sample is transgenic, 'matClassification' is automatically true; otherwise false
		boolean matClassification = false; 
		
		
		boolean noError = true;
		
		
		String excelFilePath = "GSL SNP Genotyping Request Form Internal Clients JUN2016.xlsx";
		
		try{
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
			
			/**/
			Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet requestSheet = workbook.getSheetAt(0);
	        Cell cell, cell2;
	        	  
        	//// BLOCK 0001: HEADER VALUES /////////////////////////////////////////////////
	        	/*
	        	Do NOT change the formatting of Form 1(table/cell positions). Doing so will affect these 
	        	methods inside BLOCK 0001 and BLOCK 0002 
	        	*/
	        
	        	// Requestor (LIMS Sender)
	        	cell = requestSheet.getRow(7).getCell(2);
	        	requestorID = cell.getStringCellValue();
	        	
	        	/*
	        	 * add to LIMS "Comments"
	        	 * */
	        	// Study 
	        	cell = requestSheet.getRow(8).getCell(2);
	        	studyType = cell.getStringCellValue();
	        	
	        	// Purpose of Genotyping
	        	cell = requestSheet.getRow(10).getCell(2);
	        	purpose = cell.getStringCellValue();
	        	
	        	// IRS/Supervisor
	        	cell = requestSheet.getRow(7).getCell(7);
	        	supervisor = cell.getStringCellValue();
	        	
	        	// Division
	        	cell = requestSheet.getRow(8).getCell(7);
	        	division = cell.getStringCellValue();
	        	
	        	// Date submitted
	        	cell = requestSheet.getRow(9).getCell(7);
	        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
        			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        			Date submitDate = cell.getDateCellValue();
        			dateSubmitted = df.format(submitDate);
        		
	            }else{
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	dateSubmitted = cell.getStringCellValue();
	            }
	        	
	        	
        	//// END BLOCK 0001 ////////////////////////////////////////////////////////////
	           
        	//// BLOCK 0002: GSL SERVICES //////////////////////////////////////////////////
	        	
	        	// Infinium
	        	cell = requestSheet.getRow(20).getCell(3);
	        	cell2 = requestSheet.getRow(17).getCell(3);
	        	if (cell.getNumericCellValue() != 0 && cell2.getNumericCellValue() == 0){
	        		totalNumberOfSamples += (int)cell.getNumericCellValue();
	        		serviceAvailed = "INF";
            	}
	        	
	        	// PlantTrak
	        	else if(cell.getNumericCellValue() == 0 && cell2.getNumericCellValue() != 0){
	        		totalNumberOfSamples += (int)cell2.getNumericCellValue();
	        		serviceAvailed = "PlantTrak";
	        	}
	        	
	        	// Infinium + PlantTrak
	        	else if(cell.getNumericCellValue() != 0 && cell2.getNumericCellValue() != 0 && cell.getNumericCellValue() == cell2.getNumericCellValue()){
	        		totalNumberOfSamples += (int)cell.getNumericCellValue();
	        		serviceAvailed = "PlantTrakINF";
	        	}
	        	
	        	
	        	/*	ERROR, can be caused by
	        	 	- all fields in question are empty
	        	 	- values are placed for Infinium and PlantTrak, yet values are not equal
	        	 */
	        	else{
	        		noError = false;
	        	}
	        	
	        	// TO DO: Additional Services
	        	
        	//// END BLOCK 0002 ////////////////////////////////////////////////////////////
	        	
	        workbook.close();	
        	inputStream.close();

        	
        	//// BLOCK 0003 : WRITING TO PRIMARY FILE FOR SELENIUM /////////////////////////////////
        		
				if(!(serviceAvailed.equals("NONE"))){
					int excessSamples;
					int counter;
				
					switch(serviceAvailed){
					
						case "INF": 
							excessSamples = (totalNumberOfSamples % 94 == 0) ? 0 : 1;
							counter = (totalNumberOfSamples / 94) + excessSamples;
				
							for(int i=0; i<counter; i++){
								GenotypingService serbisyo = new GenotypingService("P109403", (3*serviceList.size())+1, i+1);
								if(serbisyo.getMaterialClass()){
									matClassification = true;
								}
								serviceList.add(serbisyo);
							}
				
						break;
				
						case "PlantTrak": 
							excessSamples = (totalNumberOfSamples % 94 == 0) ? 0 : 1;
							counter = (totalNumberOfSamples / 94) + excessSamples;
				
							for(int i=0; i<counter; i++){
								GenotypingService serbisyo = new GenotypingService("P109405", (3*serviceList.size())+1, i+1);
								if(serbisyo.getMaterialClass()){
									matClassification = true;
								}
								serviceList.add(serbisyo);
							}
						break;
					
						case "PlantTrakINF": 
							excessSamples = (totalNumberOfSamples % 94 == 0) ? 0 : 1;
							counter = (totalNumberOfSamples / 94) + excessSamples;
				
							for(int i=0; i<counter; i++){
								GenotypingService serbisyo = new GenotypingService("P109403P109405", (3*serviceList.size())+1, i+1);
								if(serbisyo.getMaterialClass()){
									matClassification = true;
								}
								serviceList.add(serbisyo);
							}
						break;
					}
				}
        		
	    		boolean correctInfo = true;
	            for(int i=0; i<serviceList.size(); i++){
	            	if(!(serviceList.get(i).getCorrectness())){
	            		correctInfo = false;
	            	}
	            	
	            }

	            if(correctInfo && noError){
	            
	                FileWriter fwriter = new FileWriter(new File("requestInfo.txt"));
	        		BufferedWriter bw = new BufferedWriter(fwriter);
	        		
	        		String str = (matClassification)? "Transgenic" : "Non-Transgenic";
	        		
	        		bw.write("Classification of Materials,"+str+"\n");
	        		bw.write("Order Type,"+defaultOrderType+"\n");
	        		bw.write("Recipient Individual,"+defaultRecipientIndividual+"\n");
	        		bw.write("Sample Type,"+defaultSampleType+"\n");
	        		bw.write("Sender,"+requestorID+"\n");
	        		bw.write("Study,"+studyType+"\n");
	        		bw.write("IRS/Supervisor,"+supervisor+"\n");
	        		bw.write("Division,"+division+"\n");
	        		bw.write("Purpose of genotyping,"+purpose+"\n");
	        		bw.write("Date submitted,"+dateSubmitted+"\n");
	        		bw.write("Files to load,"+Integer.toString(serviceList.size())+"\n");
	        		
	        		for(int i=0; i<serviceList.size(); i++){
	        			bw.write("FILE"+(i+1)+","+serviceList.get(i).getFileName()+"\n");
	        		}
	        		
	        		bw.close();
	            }else{
	            	System.out.println("Error(s) found in Form 1. Review files before rerunning the program");
	            	System.exit(0);
	            }
        	
        	//// END BLOCK 0003 /////////////////////////////////////////////////////////////////////////
        	
	        
    	
	        		
	        		
		}catch(Exception exc1){
			System.out.println("File read failed");
		}
		
		//Scanner sc = new Scanner(System.in);
		//System.out.print("Username: ");
		//String username = sc.nextLine();
		//System.out.print("Password: ");
		//String password = sc.nextLine();
		WebLoader well = new WebLoader(serviceAvailed);
	}

}
