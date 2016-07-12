package plantdata;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @author 	Pios, Karl Jasson 
 * @author 	karljasson@gmail.com
 */
public class GenotypingService {
	
	private String productCode;
	private String serviceKey;
	private int sheetNumber;
	private boolean isTransgenic;
	
	private boolean properInput;
	private String serviceFilename;
	
	// nature of sample = sample type
	// default initial quantity value/unit = 20 microliters
	// default container type = Plate 
	
	public int defaultQuantity = 20;
	public String defaultUnit = "\u00B5"+"L";
	public String defaultContainerType = "Plate";
	
	
	/**
	 * Generates the sample plate data based on Form 1 input
	 * 
	 * @param 	code The product ID as indicated in Form 1
	 * @param 	sNumber An int that indicates the sheet number to be scanned (see Form 1)
	 * @param 	nthInstance An int that indicates plate ordering, as a 
	 * 			service may require multiple plates
	 */
	public GenotypingService(String code, int sNumber, int nthInstance){
		
		
		this.productCode = code;
		
		this.setServiceName(this.productCode);
		
		this.sheetNumber = sNumber;
		properInput = true;
		
		this.serviceFilename = "plate"+Integer.toString(nthInstance)+this.serviceKey+".xls";
		//this.serviceFilename = "plate"+Integer.toString(nthInstance)+this.serviceKey+".csv";
		
		try{
			//String excelFilePath = "GSL SNP Genotyping Request Form Internal Clients Feb2016_dummy.xlsx";
			String excelFilePath = "GSL SNP Genotyping Request Form Internal Clients JUN2016.xlsx";
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
			
			
			Workbook templateWorkbook = new HSSFWorkbook();  // or new XSSFWorkbook();
			Sheet sheet1 = templateWorkbook.createSheet("Samples");
			Row outRow = sheet1.createRow(0);
			outRow.createCell(0).setCellValue("Plate Type");
			outRow.createCell(1).setCellValue("96-Well Plate");
			outRow.createCell(2).setCellValue("External Barcode");
			
			outRow = sheet1.createRow(1);
			outRow.createCell(0).setCellValue("Position");
			outRow.createCell(1).setCellValue("Initial Quantity Value*");
			outRow.createCell(2).setCellValue("Initial Quantity Unit*");
			outRow.createCell(3).setCellValue("Sample Concentration (ng/"+"\u00B5"+"L)*");
			outRow.createCell(4).setCellValue("Generation*");
			outRow.createCell(5).setCellValue("Designation/Variety*");
			outRow.createCell(6).setCellValue("Container Type*");
			outRow.createCell(7).setCellValue("Accession Number");
			outRow.createCell(8).setCellValue("Sample Name");
			outRow.createCell(9).setCellValue("GID");
			
			int templateRow = 2;
			
			Workbook workbook = new XSSFWorkbook(inputStream);
	        Sheet firstSheet = workbook.getSheetAt(sheetNumber);
	        Sheet sheetQC = workbook.getSheetAt(sheetNumber + 2);
	        
	      
	        if(firstSheet.getRow(105).getCell(1).getStringCellValue().equals("") && firstSheet.getRow(106).getCell(1).getStringCellValue().equals("")){
	        
	        	Iterator<Row> iterator = firstSheet.iterator();	
		        while(iterator.hasNext() ){
		        	
		        	Row rowPointer = iterator.next();
		        	
		        	//if(rowPointer.getCell(1).equals("") || rowPointer.getCell(2).equals("") || rowPointer.getCell(3).equals("") || rowPointer.getCell(4).equals("") || rowPointer.getCell(5).equals("") || rowPointer.getCell(6).equals("")){
		        	if(rowPointer.getCell(1).equals("")){
		        		// empty line or missing data
		        	}
		        	else{
		        	//System.out.println("Checking row "+ rowPointer.getRowNum());
		        		
		        	if(rowPointer.getRowNum() < 11){
		        		continue;
		        	}
		        	if(rowPointer.getRowNum() > 104 || rowPointer.getCell(1).getStringCellValue().equals("")) break;
		        		
		        		outRow = sheet1.createRow(templateRow);
		        	
		        		Iterator<Cell> cellIterator = rowPointer.cellIterator();
		            
		        		/*///////////////////////////
		        		PlantSample samplet = new PlantSample();
		        		
		        		samplet.setQuantityValue(defaultQuantity);
		        		samplet.setQuantityUnit(defaultUnit);
		        		samplet.setContainerType("Plate");
						////////////*////////////////////
		        		outRow.createCell(1).setCellValue(defaultQuantity);
		        		outRow.createCell(2).setCellValue(defaultUnit);
		        		outRow.createCell(6).setCellValue("Plate");
		        		
		
		        		for(int columnCount = 0; columnCount < 7; columnCount++){
		        		
		        			Cell cell = cellIterator.next();
		                 
		        			cell.setCellType(Cell.CELL_TYPE_STRING);
		        			
		        		//// BLOCK 0001: SCAN VALUES FROM SAMPLE PLATE SHEET ////////////////////
			                switch(columnCount){
			                	
				                case 0:
			                		//samplet.setPosition(cell.getStringCellValue());
				                	outRow.createCell(0).setCellValue(cell.getStringCellValue());
				                	break;
				                case 1:
			                		//samplet.setID(cell.getStringCellValue());
				                	outRow.createCell(8).setCellValue(cell.getStringCellValue());
				                	break;
				                case 2:
				                	//samplet.setDesignation(cell.getStringCellValue());
				                	outRow.createCell(5).setCellValue(cell.getStringCellValue());
				                	break;
				                case 3:
				                	//samplet.setGID(cell.getStringCellValue());
				                	outRow.createCell(9).setCellValue(cell.getStringCellValue());
				                	break;
				                case 4:
				                	//samplet.setGeneration(cell.getStringCellValue());
				                	outRow.createCell(4).setCellValue(cell.getStringCellValue());
				                	break;
				                case 5:
				                	//samplet.setSampleType(cell.getStringCellValue());
				                	if(cell.getStringCellValue().equals("Y")){
				                		this.isTransgenic = true;
				                	}
				                	//samplet.setSampleType(cell.getStringCellValue());
				                	//outRow.createCell(0).setCellValue(cell.getStringCellValue());
				                	break;
				                case 6:
				                	//samplet.setAccessNumber(cell.getStringCellValue());
				                	outRow.createCell(7).setCellValue(cell.getStringCellValue());
				                	break;
		
			                }
			            /// END BLOCK 0001 ///////////////////////////////////////////////////////
			            }
		        		
		        		//// BLOCK 0002: SCAN VALUES FROM DNA QC SHEET ////////////////////////////
		        		Iterator<Row> iterator2 = sheetQC.iterator();
			        		
			        	while(iterator2.hasNext() ){
			        		Row rowPointer2 = iterator2.next();
			        		
			        		if(rowPointer2.getCell(1).equals("")){
				        		// empty line or missing data
				        	}
				        	else{
				        		
				        	//System.out.println("Checking row "+ rowPointer.getRowNum());
				        	
					        	if(rowPointer2.getRowNum() < 9){
					        		continue;
					        	}
					        	if(rowPointer2.getRowNum() > 104) break;
					        	//System.out.println("Scanning Samples QC");
					        	
					        	Cell cell2 = rowPointer2.getCell(1);
					        	if(cell2.getStringCellValue().equals(outRow.getCell(8).getStringCellValue())){
					        		Cell targetCell = rowPointer2.getCell(3);
					        		targetCell.setCellType(Cell.CELL_TYPE_STRING);
					        		String str = targetCell.getStringCellValue();
					        		str = str.replaceAll("[^\\d.]", "");
					        		//samplet.setConcentration(Double.parseDouble(str));
					        		outRow.createCell(3).setCellValue(Double.parseDouble(str));
					        		//System.out.println(samplet.getID()+" | "+samplet.getConcentrationValue());
					        	}
				        	}
				        }
			        	//// END BLOCK 0002 ////////////////////////////////////////////////////
			        	templateRow++;
			        	 //bw.write(samplet.printToCsv()+"\n");
			        	
		        	}
		        }
			}else{
				System.out.println("(PLATE "+nthInstance+")ERROR: Make sure G12 and H12 are kept empty");
				this.properInput = false;
			}
	        workbook.close();
	        inputStream.close();
	        //bw.close();
	        
	        FileOutputStream fileOut = new FileOutputStream(serviceFilename);
	        templateWorkbook.write(fileOut);
	        fileOut.close();
	        templateWorkbook.close();
		}
		catch(Exception fileNotFound){
			System.out.println("File read failed");
		}
	}
	
	/** Identifies the service requested based on a product ID (see Form 1)
	 * 
	 * @param 	input The product code
	 */
	public void setServiceName(String input){
		
		if(input.equals("P109403")){
			this.serviceKey = "Infinium";
		}
		
		// TO DO: Add more Product Codes
	}
	
	/** Gets the service's name for identification
	 * 
	 * @return 	A string containing the service's name/identification
	 */
	public String getServiceName(){
		return this.serviceKey;
	}
	
	/** Gets the Form 1 sheetnumber that this GenotypingService instance has scanned
	 * 
	 * @return An int representing the worksheet number
	 */
	public int getSheetNumber(){
		return this.sheetNumber;
	}
	
	/** Returns a value indicating if at least one sample is transgenic or not
	 * 
	 * @return	A boolean value indicating if at least one sample in the plate is transgenic
	 */
	public boolean getMaterialClass(){
		return this.isTransgenic;
	}
	
	/** Gets the filename of the generated template
	 * 
	 * @return	 A string representing the filename of the template file
	 */
	public String getFileName(){
		return this.serviceFilename;
	}
	
	/** Returns a boolean value indicating if the data in scanned worksheet has no known errors
	 * 
	 * @return 	A boolean value indicating if the input file was correctly filled in
	 */
	public boolean getCorrectness(){
		return this.properInput;
	}

}
