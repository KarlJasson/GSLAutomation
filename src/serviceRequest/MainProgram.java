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
		
		// boolean values determine which GSL service is availed
		boolean availInfinium = false;
			//boolean availFluidigm96 = false;
			//boolean availFluidigm24 = false;
		
		
		// indicates how many samples are provided for each specified service
		int infiniumSampleCount = 0;
			//int fluidigm96SampleCount = 0;
			//int fluidigm24SampleCount = 0;
		
		// indicates the total number of samples, services and plates
		int totalNumberOfSamples = 0;
		
		// if at least one sample is transgenic, 'isTransgenic' is automatically true; otherwise false
		boolean isTransgenic = false; 
		
		
		String excelFilePath = "GSL SNP Genotyping Request Form Internal Clients JUN2016.xlsx";
		
		try{
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
			
			/**/
			Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet requestSheet = workbook.getSheetAt(0);
	        Cell cell;
	  
        	//// BLOCK 0001: HEADER VALUES /////////////////////////////////////////////////
	        	
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
	        	
	        	// GSL Infinium
	        	cell = requestSheet.getRow(20).getCell(3);
	        	if (cell.getNumericCellValue() != 0){
            		infiniumSampleCount = (int)cell.getNumericCellValue();
            		totalNumberOfSamples = totalNumberOfSamples + infiniumSampleCount;
            		availInfinium = true;
            	}
	        	
	        	// TO DO: Additional Services
	        	
        	//// END BLOCK 0002 ////////////////////////////////////////////////////////////
	        	
	        workbook.close();	
        	inputStream.close();

        	
        	//// BLOCK 0003 : WRITING TO PRIMARY FILE FOR SELENIUM /////////////////////////////////
	    		if(availInfinium){
	            	//System.out.println("Client requests Infinium SNP chip with "+infiniumSampleCount+" samples");
	            	
	            	int sobra = (infiniumSampleCount % 94 == 0) ? 0 : 1;
	            	int counter = (infiniumSampleCount / 94) + sobra;
	            	for(int i=0; i<counter; i++){
	    	        	GenotypingService infinium = new GenotypingService("P109403", (3*serviceList.size())+1, i+1);
	    	        	if(infinium.getMaterialClass()){
	    	        		isTransgenic = true;
	    	        	}
	    	        	serviceList.add(infinium);
	            	}
	            }
	            
	    		int leftovers = (totalNumberOfSamples % 94 == 0) ? 0 : 1;
	    		
	    		boolean correctInfo = true;
	            for(int i=0; i<serviceList.size(); i++){
	            	if(!(serviceList.get(i).getCorrectness())){
	            		correctInfo = false;
	            	}
	            	
	            }

	            if(correctInfo){
	            
	                FileWriter fwriter = new FileWriter(new File("requestInfo.txt"));
	        		BufferedWriter bw = new BufferedWriter(fwriter);
	        		
	        		String str = (isTransgenic)? "Transgenic" : "Non-Transgenic";
	        		
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
		WebLoader well = new WebLoader();
	}

}
