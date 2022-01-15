package com.company.recon.writer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.company.recon.MainClass;
import com.company.recon.operations.Operator;
import com.company.recon.reader.ConfigFileReader;
import com.company.recon.reader.CsvReader;
import com.company.recon.reader.MappingFileReader;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ExcelWriter {
	private XSSFWorkbook workbook;
	FileOutputStream out; //Check this.If not necessary delete
	CsvReader csvReader;
	MappingFileReader mappingFileReader;
	Operator operator;
	CellStyle yellowCellStyle;
	CellStyle greyBoldCellStyle;
	CellStyle redCellStyle;
	CellStyle greenCellStyle;
	CellStyle violetCellStyle;
	
	CellStyle sourceCellStyle;
	CellStyle targetCellStyle;
	
	public static final String outputFilePathName="."+MainClass.FILE_SEPARATOR+"docs"+MainClass.FILE_SEPARATOR+"gen"+MainClass.FILE_SEPARATOR+"Recon_Source_Target.xlsx";//For generic file system
	
	public ExcelWriter(){
		workbook = new XSSFWorkbook();
		csvReader=new CsvReader();
		mappingFileReader=new MappingFileReader();
		operator=new Operator();
		
		
		sourceCellStyle=createColorCellStyle(workbook,IndexedColors.SKY_BLUE.getIndex());//Sky Blue For source
		targetCellStyle=createColorCellStyle(workbook,IndexedColors.GOLD.getIndex());//Gold For Target
		
		greyBoldCellStyle=createColorCellStyle(workbook,IndexedColors.GREY_40_PERCENT.getIndex());
		redCellStyle=createColorCellStyle(workbook,IndexedColors.RED.getIndex());
		greenCellStyle=createColorCellStyle(workbook,IndexedColors.GREEN.getIndex());
		yellowCellStyle=createColorCellStyle(workbook,IndexedColors.YELLOW.getIndex());
		violetCellStyle=createColorCellStyle(workbook,IndexedColors.VIOLET.getIndex());;
	}

	/**
	 * This method takes data i.e a list of String array and a sheet name 
	 *  and puts the data onto the new sheet name which is created on the fly.
	 *  If filter is null, the whole column header is taken,else filter headers are taken.
	 *  This method is only used to write 'Summary','Source-Target' and 'Target-Source' excel sheets.
	 *  For 'Source=Target' and 'Source!=Target' use other custom made methods.
	 * @param list
	 * @param sheetName
	 */
	public void writeToExcel(List<String[]> list,String sheetName,String filter){
		String header[]=null;
		int columnnum=0;
		Map<String, Object[]> columnMappedDataTarget = null;
		Map<String, Object[]> columnValueMappedDataTarget = null;
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);

        //Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        if(filter==null)
        header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
        else{
        	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
        }
        header=convertStringToStringArray(convertStringArrayToString(header));//To put "." at the end of each entry in cell for header
        data.put("headerkey", header);
        
        for(int i=0;i<list.size();i++){
        	data.put(Integer.toString(i), list.get(i));
        }
        
        
        if(sheetName.equalsIgnoreCase("source-target")|| sheetName.equalsIgnoreCase("source") ){
        	columnnum=fillInData(data,sheet,0,"source",sheetName);//First pass '0' as column number
//        	MainClass.Log.info("THE CELL NUM IS:"+columnnum);
        }
        else if(sheetName.equalsIgnoreCase("target-source")|| sheetName.equalsIgnoreCase("target"))
        {
        	//-----------------------------------------Case2------------------------------------------------------
            //create mapped 'header' for target
                    if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
                    	columnMappedDataTarget=createColumnMappedTarget(list,filter);//Data set1-> column:yes,value:no
                    }
                    
            //---------------------------------------------Case3--------------------------------------------------
            //create mapped 'value' for target
            		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile createColumnValueMappedTarget
            			columnValueMappedDataTarget = createColumnValueMappedTarget(list,filter,header);//Data set2-> column:(yes,no),value:yes
            		}//End of creating mapped 'value' for Target		
        	
            		
            		if(ConfigFileReader.valueMappingFile!=null){
            			columnnum=fillInData(columnValueMappedDataTarget,sheet,0,"target",sheetName);//Paste the mapped data again!
            		}
            		else if(ConfigFileReader.columnMappingFile!=null & ConfigFileReader.valueMappingFile==null ){
            			columnnum=fillInData(columnMappedDataTarget,sheet,0,"target",sheetName);//Paste the mapped data again!
            		}
            		else{
            			columnnum=fillInData(data,sheet,0,"target",sheetName);//Paste the same data again! Case1
            		}	
        	
//        	columnnum=fillInData(data,sheet,0,"target",sheetName);//First pass '0' as column number
        }
        else if(sheetName.equalsIgnoreCase("Summary")){
        	columnnum=fillInData(data,sheet,0,"Summary",sheetName);//First pass '0' as column number
        }
        
        writeToOutPutStream(sheetName);//After filling in Data write it to outPutStream of the workbook
                 
	}//End writeToExcel()
	
	/**
	 * This method takes a List<String[]> as the data to be written the given sheet name.
	 * Here the data i.e List<String[]> are intersection records of the Source and Target Files.
	 * Filter may be given to display records in the order of filter.
	 * This method does not work for column Mapping and valueMapping fundas. It just treats target 
	 * as source. So if you want to print target in sync with source use this method.
	 * @param listsrc
	 * @param sheetName
	 * @param filter
	 */
	public void writeToExcelForIntersection_v1(List<String[]> listsrc,String sheetName,String filter){
		String header[]=null;
		XSSFSheet sheet = workbook.createSheet(sheetName);
		
		//Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> datasrc = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        //Adding and displaying of Source Data---->
        if(filter==null)
            header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
            else{
            	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
            }
        header=convertStringToStringArray(convertStringArrayToString(header));//To put "." at the end of each entry in cell for header
        datasrc.put("headerkey", header);
        
        for(int i=0;i<listsrc.size();i++){
        	datasrc.put(Integer.toString(i), listsrc.get(i));
        }
        
        int columnnum=fillInData(datasrc,sheet,0,"source",sheetName);//First pass '0' as column number
//        MainClass.Log.info("THE CELL NUM IS:"+columnnum);
		
        columnnum=fillInData(datasrc,sheet,(columnnum+1),"target",sheetName);//Paste the same data again!
//        MainClass.Log.info("THE CELL NUM NOW IS:"+columnnum);
        
        writeToOutPutStream(sheetName);//After filling in Data, write it to outPutStream of the workbook
       
        
	}
	
	/**
	 * This method is under code cleaning phase!.
	 * This method takes a List<String[]> as the data to be written the given sheet name.
	 * Here the data i.e List<String[]> are intersection records of the Source and Target Files.
	 * Filter may be given to display records in the order of filter. 
	 * This method puts actual 'column values' and 'data values' as in 
	 * columnMappingFile for headers of the target and values of the target.
	 * @param listsrc
	 * @param sheetName
	 * @param filter
	 */
	public void writeToExcelForIntersection(List<String[]> listsrc,String sheetName,String filter){
		String header[]=null;
		String headerTarget[]=null;
		XSSFSheet sheet = workbook.createSheet(sheetName);
		Map<String, Object[]> columnMappedDataTarget = null;
		Map<String, Object[]> columnValueMappedDataTarget = null;
		
		//Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> datasrc = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        //Adding and displaying of Source Data---->
        if(filter==null)
            header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
            else{
            	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
            }
        header=convertStringToStringArray(convertStringArrayToString(header));//To put "." at the end of each entry in cell for header
        datasrc.put("headerkey", header);
        
        for(int i=0;i<listsrc.size();i++){
        	datasrc.put(Integer.toString(i), listsrc.get(i));
        }
        
        int columnnum=fillInData(datasrc,sheet,0,"source",sheetName);//First pass '0' as column number
//-------------------------------------------------------------------------------------------------
//create mapped 'header' for target
        if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
        	columnMappedDataTarget=createColumnMappedTarget(listsrc,filter);//Data set1-> column:yes,value:no
        }
        
//-------------------------------------------------------------------------------------------------
//create mapped 'value' for target
		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile createColumnValueMappedTarget
			columnValueMappedDataTarget = createColumnValueMappedTarget(listsrc,filter,header);//Data set2-> column:(yes,no),value:yes
		}//End of creating mapped 'value' for Target
		
		
//------------------------------------------------------------------------------
		if(ConfigFileReader.valueMappingFile!=null){
			columnnum=fillInData(columnValueMappedDataTarget,sheet,(columnnum+1),"target",sheetName);//Paste the mapped data again!
		}
		else if(ConfigFileReader.columnMappingFile!=null & ConfigFileReader.valueMappingFile==null ){
			columnnum=fillInData(columnMappedDataTarget,sheet,(columnnum+1),"target",sheetName);//Paste the mapped data again!
		}
		else{
			columnnum=fillInData(datasrc,sheet,(columnnum+1),"target",sheetName);//Paste the same data again!
		}	
		
		writeToOutPutStream(sheetName);//After filling in Data write it to outPutStream of the workbook
		    
	}//End writeToExcelForIntersection
	
	
	/**
	 * Helper method for writeToExcel,writeToExcelForIntersection and writeToExcelForDiff.
	 * This creates appropriate mapped header to be put as first record entry into map
	 * @param filter
	 * @return
	 */
	private String[] createColumnMappedTargetHeader(String filter){
		String targetHeader[]=null;
		if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
			Map<String, String> columnMapping=mappingFileReader.initMappingFileStrings(ConfigFileReader.columnMappingFile,"columnMappingFile");
			
		 if(filter==null){
			 targetHeader=csvReader.readCsvFileHeader(ConfigFileReader.targetFile);//This file should be visible to all classes!Thus its static
	        	replacewithColumnMappedHeader(targetHeader,columnMapping);//This changes the headerTarget to mapped values internally
	        }
	        else{
	        	targetHeader=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
	            replacewithColumnMappedHeader(targetHeader,columnMapping);//This changes the headerTarget to mapped values internally
	        }
		 targetHeader=convertStringToStringArray(convertStringArrayToString(targetHeader));//To put "." at the end of each entry in cell for header
		}
		return targetHeader;
	}
	
	/**
	 *  Helper method for writeToExcel,writeToExcelForIntersection and writeToExcelForDiff.
	 *  This method creates appropriate Column Mapped (if given) Data set to show on the Excel sheet.
	 * @param target
	 * @param filter
	 * @return column Mapped Target Map
	 */
	private Map<String, Object[]> createColumnMappedTarget(List<String[]> target,String filter){
		Map<String, Object[]> columnMappedDataTarget= new LinkedHashMap<String, Object[]>();
		
		 columnMappedDataTarget.put("headerkey", createColumnMappedTargetHeader(filter));//Call method which returns appropriate mapped header
	        
	        for(int i=0;i<target.size();i++){
	        	columnMappedDataTarget.put(Integer.toString(i), target.get(i));//Data set1-> column:yes,value:no
	        }
			return columnMappedDataTarget;    
	}
	
	/**
	 *  Helper method for writeToExcel,writeToExcelForIntersection and writeToExcelForDiff.
	 *  This method creates appropriate both Column and Value(if given) Mapped Data set to show on the Excel sheet.
	 * @param target
	 * @param filter
	 * @param header
	 * @return column and value Mapped Target Map
	 */
	private Map<String, Object[]> createColumnValueMappedTarget(List<String[]> target,String filter,String[] header){
		Map<String, Object[]> columnValueMappedDataTarget= new LinkedHashMap<String, Object[]>();
		 //create mapped 'value' for target
		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile
			List<String[]> valueMappedList=null;
			columnValueMappedDataTarget = new LinkedHashMap<String, Object[]>();
			Map<String, String> valueMapping=mappingFileReader.initReverseMappingFileStrings(ConfigFileReader.valueMappingFile,"valueMappingFile");
			if(ConfigFileReader.columnMappingFile!=null){
				valueMappedList=operator.createDataForReverseValueMappedFileOfTarget(valueMapping, target, createColumnMappedTargetHeader(filter));//changes 'target' internally
			}
			else{
				valueMappedList=operator.createDataForReverseValueMappedFileOfTarget(valueMapping, target, header);//changes 'target' internally
			}
			
			 for(int i=0;i<valueMappedList.size();i++){
		        	columnValueMappedDataTarget.put(Integer.toString(i), valueMappedList.get(i));//Data set2-> column:(yes,no),value:yes
		        }    
		}//End of creating mapped 'value' for Target
		return columnValueMappedDataTarget;	
	}
	
	/**
	 * Helper Method.This method replaces source header names with actual target header names.
	 * Should be used only when ConfigFileReader.columnValueMapping!=null. I.e its 'yes' in config File
	 * @param origHeader
	 * @param columnMapping
	 */
	private void replacewithColumnMappedHeader(String origHeader[],Map<String, String> columnMapping){

		Set<String> keyset = columnMapping.keySet();
		for(String key : keyset){  
			for(int i=0;i<origHeader.length;i++){
				if(key.equalsIgnoreCase(origHeader[i])){
					origHeader[i]=columnMapping.get(key);
				}
			}
		
		}//End for of 'key'	
	}
	
	/**
	 * This method is takes three data sets i.e List<String[]> for Source, Target and Difference and these
	 * data sets are printed on the 'Source!=Target' excel sheet.This method does not work for column Mapping and value
	 * Mapping fundas. It just treats target as source. So if you want to print target in sync with source use this
	 * method
	 * @param listsrc
	 * @param listtarget
	 * @param listdiff
	 * @param sheetName
	 * @param filter
	 */
	public void writeToExcelForDiff_v1(List<String[]> listsrc,List<String[]> listtarget,List<String[]> listdiff,String sheetName,String filter){
		 //Create a blank sheet
		String header[]=null;
		String headerdiff[]=null;//Creating header for diff is going to be interesting! Its now implemented
		headerdiff=createHeaderOfDiff(ConfigFileReader.calculateDevianceOn,ConfigFileReader.percentageOrAbsolute,ConfigFileReader.devianceValues,filter);
        XSSFSheet sheet = workbook.createSheet(sheetName);
//        CellStyle lineCellStyle=createLineCellStyle(workbook);

        //Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> datasrc = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        Map<String, Object[]> datatarget = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        Map<String, Object[]> datadiff = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        //Adding and displaying of Source Data---->
        if(filter==null)
            header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
            else{
            	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
            }
        header=convertStringToStringArray(convertStringArrayToString(header));//To put "." at the end of each entry in cell for header
        datasrc.put("headerkey", header);
        
        for(int i=0;i<listsrc.size();i++){
        	datasrc.put(Integer.toString(i), listsrc.get(i));
        }
        
        int columnnum=fillInData(datasrc,sheet,0,"source",sheetName);//First pass '0' as column number
//        MainClass.Log.info("THE CELL NUM IS:"+columnnum);
        
        //-------------- Adding and displaying of Difference Data---->
        headerdiff=convertStringToStringArray(convertStringArrayToString(headerdiff));//To put "." at the end of each entry in cell for header
        datadiff.put("headerkey", headerdiff);
        for(int i=0;i<listdiff.size();i++){
        	datadiff.put(Integer.toString(i), listdiff.get(i));
        }
        
        columnnum=fillInData(datadiff,sheet,(columnnum+1),"difference",sheetName);
//        MainClass.Log.info("THE CELL NUM NOW IS:"+columnnum);
        
        //----------------Adding and displaying of Target Data---->
        datatarget.put("headerkey", header);
        for(int i=0;i<listtarget.size();i++){
        	datatarget.put(Integer.toString(i), listtarget.get(i));
        }
        
        
        columnnum=fillInData(datatarget,sheet,(columnnum+1),"target",sheetName);

        writeToOutPutStream(sheetName);//After filling in Data write it to outPutStream of the workbook
        
	}//End WriteToExcel
	
	
	/**
	 * Method under code cleaning phase! This method is takes three data sets i.e List<String[]> for Source, Target 
	 * and Difference and these data sets are printed on the 'Source!=Target' excel sheet.This method puts  actual
	 *  'column values' and'data values' as in columnMappingFile and valueMappingFile for headers of the target and
	 *  values of the target
	 * @param listsrc
	 * @param listtarget
	 * @param listdiff
	 * @param sheetName
	 * @param filter
	 */
	public void writeToExcelForDiff(List<String[]> listsrc,List<String[]> listtarget,List<String[]> listdiff,String sheetName,String filter){
		 //Create a blank sheet
		String header[]=null;
		String headerdiff[]=null;//Creating header for diff is going to be interesting! Its now implemented
		String headerTarget[]=null;
		Map<String, Object[]> columnMappedDataTarget = null;
		Map<String, Object[]> columnValueMappedDataTarget = null;
		headerdiff=createHeaderOfDiff(ConfigFileReader.calculateDevianceOn,ConfigFileReader.percentageOrAbsolute,ConfigFileReader.devianceValues,filter);
        XSSFSheet sheet = workbook.createSheet(sheetName);


        //Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> datasrc = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        Map<String, Object[]> datatarget = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        Map<String, Object[]> datadiff = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        //Adding and displaying of Source Data---->
        if(filter==null)
            header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
            else{
            	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
            }
        header=convertStringToStringArray(convertStringArrayToString(header));//To put "." at the end of each entry in cell for header
        datasrc.put("headerkey", header);
        
        for(int i=0;i<listsrc.size();i++){
        	datasrc.put(Integer.toString(i), listsrc.get(i));
        }
        
        int columnnum=fillInData(datasrc,sheet,0,"source",sheetName);//First pass '0' as column number
//        MainClass.Log.info("THE CELL NUM IS:"+columnnum);
        
        //-------------- Adding and displaying of Difference Data---->
        headerdiff=convertStringToStringArray(convertStringArrayToString(headerdiff));//To put "." at the end of each entry in cell for header
        datadiff.put("headerkey", headerdiff);
        for(int i=0;i<listdiff.size();i++){
        	datadiff.put(Integer.toString(i), listdiff.get(i));
        }
        
        columnnum=fillInData(datadiff,sheet,(columnnum+1),"difference",sheetName);

        
        //----------------Adding  of Target Data:Case1----> //displaying done at the end
        datatarget.put("headerkey", header);
        for(int i=0;i<listtarget.size();i++){
        	datatarget.put(Integer.toString(i), listtarget.get(i));
        }
        
        
      //-----------------------------------------Case2------------------------------------------------------
      //create mapped 'header' for target
              if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
              	columnMappedDataTarget=createColumnMappedTarget(listtarget,filter);//Data set1-> column:yes,value:no
              }
              
      //---------------------------------------------Case3--------------------------------------------------
      //create mapped 'value' for target
      		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile createColumnValueMappedTarget
      			columnValueMappedDataTarget = createColumnValueMappedTarget(listtarget,filter,header);//Data set2-> column:(yes,no),value:yes
      		}//End of creating mapped 'value' for Target			
        
        
        
        
//  		if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
//  			columnMappedDataTarget = new LinkedHashMap<String, Object[]>();
//  			Map<String, String> columnMapping=mappingFileReader.initMappingFileStrings(ConfigFileReader.columnMappingFile,"columnMappingFile");
//  			
//  			//Adding and displaying of Mapped Target Data---->
//
//  	        if(filter==null){
//  	        	headerTarget=csvReader.readCsvFileHeader(ConfigFileReader.targetFile);//This file should be visible to all classes!Thus its static
//  	        	replacewithColumnMappedHeader(headerTarget,columnMapping);//This changes the headerTarget to mapped values internally
//  	        }
//  	        else{
//  	            headerTarget=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
//  	            replacewithColumnMappedHeader(headerTarget,columnMapping);//This changes the headerTarget to mapped values internally
//  	        }
//  	        headerTarget=convertStringToStringArray(convertStringArrayToString(headerTarget));//To put "." at the end of each entry in cell for header
//  	        columnMappedDataTarget.put("headerkey", headerTarget);
//  	        
//  	        for(int i=0;i<listtarget.size();i++){
//  	        	columnMappedDataTarget.put(Integer.toString(i), listtarget.get(i));//Data set1-> column:yes,value:no
//  	        }    
//  		}//End of creating mappedTargetHeader
        
        
 
//	      //create mapped 'value' for target
//		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile
//			List<String[]> valueMappedList=null;
//			columnValueMappedDataTarget = new LinkedHashMap<String, Object[]>();
//			Map<String, String> valueMapping=mappingFileReader.initReverseMappingFileStrings(ConfigFileReader.valueMappingFile,"valueMappingFile");
//			if(ConfigFileReader.columnMappingFile!=null){
//				valueMappedList=operator.createDataForReverseValueMappedFileOfTarget(valueMapping, listtarget, headerTarget);//changes 'listtarget' internally
//			}
//			else{
//				valueMappedList=operator.createDataForReverseValueMappedFileOfTarget(valueMapping, listtarget, header);//'header' from line no 383
//			}
//			
//			 for(int i=0;i<valueMappedList.size();i++){
//		        	columnValueMappedDataTarget.put(Integer.toString(i), valueMappedList.get(i));//Data set2-> column:(yes,no),value:yes
//		        }    
//		}//End of creating mapped 'value' for Target
//		
		
		//------------------------------------------------------------------------------
		if(ConfigFileReader.valueMappingFile!=null){
			columnnum=fillInData(columnValueMappedDataTarget,sheet,(columnnum+1),"target",sheetName);//Paste the mapped data again!
		}
		else if(ConfigFileReader.columnMappingFile!=null & ConfigFileReader.valueMappingFile==null ){
			columnnum=fillInData(columnMappedDataTarget,sheet,(columnnum+1),"target",sheetName);//Paste the mapped data again!
		}
		else{
			columnnum=fillInData(datatarget,sheet,(columnnum+1),"target",sheetName);//Paste the same data again!
		}	
        
        //columnnum=fillInData(datatarget,sheet,(columnnum+1),"target",sheetName); //orig

		writeToOutPutStream(sheetName);//After filling in Data write it to outPutStream of the workbook
	}//End WriteToExcel
	
	
	/**
	 * This is under implementation!!
	 * 
	 * This method takes data i.e a list of String array and a sheet name 
	 *  and puts the data onto the new sheet name which is created on the fly.
	 *  If filter is null, the whole column header is taken,else filter headers are taken
	 * @param list
	 * @param sheetName
	 */
	public void writeSummaryToExcel(List<String[]> list,String sheetName,String filter){
		String header[]=null;
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);
        CellStyle lineCellStyle=createLineCellStyle(workbook);
        
        //Convert List<String[]> to Map so that its easy to put data in excel sheet
        Map<String, Object[]> data = new LinkedHashMap<String, Object[]>();//LinkedHashMap ensures insertion order
        
        if(filter==null)
        header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
        else{
        	header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
        }
        data.put("headerkey", header);
        
        for(int i=0;i<list.size();i++){		
        	data.put(Integer.toString(i), list.get(i));	
        }
        	
      //Iterate over data and write to sheet
        	Set<String> keyset = data.keySet();
            int rownum = 0;
            for (String key : keyset)
            {
            	
                Row row = sheet.createRow(rownum++);
                if(rownum==0|| rownum==1)
                	continue;
                
                Object [] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr)
                {
                   Cell cell = row.createCell(cellnum++);
                   //--------------------
                   if(cellnum==0|| cellnum==1)
                   	continue;
                   //----------------------
                   if(obj instanceof String)
                        cell.setCellValue((String)obj);
                   		cell.setCellStyle(lineCellStyle);
//                    else if(obj instanceof Integer)
//                        cell.setCellValue((Integer)obj);
                }
            } //End outer for loop
            
            
          //Put data into rows before doing its beautification
            makeHeaderBold(workbook,sheet.getRow(2));
//            Row row = workbook.getSheetAt(0).getRow(2);//Get Header Row of sheet number 1
            Row row = sheet.getRow(2);//Get Header Row of sheet number 1
            for(int colNum = 0; colNum<row.getLastCellNum();colNum++)   
            	sheet.autoSizeColumn(colNum);//Auto size w.r.t header row size
            
            
            writeToOutPutStream(sheetName);//After filling in Data write it to outPutStream of the workbook
           
            
	}//End writeToExcel()
	
//Private methods----------------------------------------------------------------------->	
	
	private  void makeHeaderBold(Workbook wb, Row row){
	    CellStyle headerStyle = wb.createCellStyle();//Create style
	    Font font = wb.createFont();//Create font
	    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
	    headerStyle.setFont(font);//set it to bold
	    headerStyle.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//Just this line does not fill color
	    headerStyle.setFillPattern(CellStyle.SPARSE_DOTS);
//	    style.setWrapText(true);
	    headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
	    headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
	    headerStyle.setBorderRight(CellStyle.BORDER_THIN);
	    headerStyle.setBorderTop(CellStyle.BORDER_THIN);
	   
	    
	    for(int i = 0; i < row.getLastCellNum(); i++){//For each cell in the row 
	        row.getCell(i).setCellStyle(headerStyle);//Set the style
	    }
	}
	
	private  CellStyle createLineCellStyle(Workbook wb){
		CellStyle lineCellStyle = workbook.createCellStyle();
		lineCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		lineCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		lineCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		lineCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		return lineCellStyle;
	}
	
	private  CellStyle createColorCellStyle(Workbook wb,Short bg){
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(bg);//Just this line does not fill color
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);		
		
		if(bg==IndexedColors.GREY_40_PERCENT.getIndex()){
			Font font = wb.createFont();//Create font
		    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
		    cellStyle.setFont(font);//set it to bold
		}
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		return cellStyle;
	}
	
	
	/**
	 * Method which writes given data to given sheet
	 * Returns the column number on which data has stopped writing
	 * @param data
	 * @param sheet
	 * @param lineCellStyle
	 * @return
	 */
	private int fillInData(Map<String, Object[]> data,XSSFSheet sheet,int columnnum,String typeofdata,String sheetname){
		//Iterate over data and write to sheet
		
    	Set<String> keyset = data.keySet();
        int rownum = 0;
        int cellnumber = 0;
        Row row;
        for (String key : keyset)
        {
        	if(columnnum==0)
             row = sheet.createRow(rownum++);
        	else
        	 row = sheet.getRow(rownum++);
            
            Object [] objArr = data.get(key);
            int cellnum = columnnum;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);//Put data into rows before doing its beautification.
               
               
               if(((String)obj).endsWith(".")){//It means the data we are dealing with is header data!
            	   cell.setCellStyle(greyBoldCellStyle);
            	   cell.setCellValue(deleteLastChar((String)obj)); //Again delete the "."at the end of the String to keep the Data Intact
               }
               else{
            	   if(typeofdata.equalsIgnoreCase("source"))
               			cell.setCellStyle(sourceCellStyle);
            	   else if(typeofdata.equalsIgnoreCase("target"))
            		   cell.setCellStyle(targetCellStyle);
            	   else if(typeofdata.equalsIgnoreCase("difference")){
            		   if(((String)obj).equalsIgnoreCase("ok")){
                    	   cell.setCellStyle(greenCellStyle);
                       }
            		   else if(((String)obj).equalsIgnoreCase("yes")){
                    	   cell.setCellStyle(yellowCellStyle);
                       }
            		   else if(((String)obj).equalsIgnoreCase("no") || ((String)obj).equalsIgnoreCase("not ok")) {
            			   cell.setCellStyle(redCellStyle);
            		   }
            		   else if(((String)obj).endsWith("e")){
            			   cell.setCellStyle(greenCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            		   else if(((String)obj).endsWith("d")){
            			   cell.setCellStyle(redCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            		   else if(((String)obj).endsWith("v")){
            			   cell.setCellStyle(violetCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            	   }
            	   else if(typeofdata.equalsIgnoreCase("Summary")){
            		   //Do Nothing with color coding!
            		   Row row1 = sheet.getRow(0);//Get Header Row of sheet number 1
                       for(int colNum = 0; colNum<row1.getLastCellNum();colNum++)   
                       	sheet.autoSizeColumn(colNum);//Auto size
            	   }
//                		else if(obj instanceof Integer)
//                    	cell.setCellValue((Integer)obj);
               }
               
               sheet.autoSizeColumn(cellnum);//Important formatting step here!
            }//End for of 'Object' loop
            cellnumber=cellnum;
        } //End outer for loop
        

//        Row firstrow = sheet.getRow(0);//Get Header Row
//        for(int colNum = 0; colNum<firstrow.getLastCellNum();colNum++)   
//        	sheet.autoSizeColumn(colNum);//Auto size w.r.t no of elements in 'header row'
        
        return cellnumber;
	}
	
	
	
	/**
	 * This is under Implementation. This method has to put keywords on top of data
	 * Method which writes given data to given sheet
	 * Returns the column number on which data has stopped writing
	 * @param data
	 * @param sheet
	 * @param lineCellStyle
	 * @return
	 */
	private int fillInData_v1(Map<String, Object[]> data,XSSFSheet sheet,int columnnum,String typeofdata,String sheetName){
		//Iterate over data and write to sheet
//		data.values();
		String[] header=(String[]) data.get("0");
		 int cellIndexSource=header.length/2;
		
		Row firstRow=sheet.createRow(0);
		Cell firstRowCells=firstRow.createCell(cellIndexSource);
		if(sheetName.equalsIgnoreCase("Source-Target")){
			System.out.println("Inside Source-Target ");
			firstRowCells.setCellValue(sheetName);
			firstRowCells.setCellStyle(sourceCellStyle);
			System.out.println("getCellType is:"+firstRowCells.getCellType());
		}
		else if(sheetName.equalsIgnoreCase("Target-Source")){
			System.out.println("Inside Target-Source ");
			firstRowCells=firstRow.getCell(cellIndexSource);
			firstRowCells.setCellValue(sheetName);
			firstRowCells.setCellStyle(targetCellStyle);
			System.out.println("getCellType is:"+firstRowCells.getCellType());
		}
		else if(sheetName.equalsIgnoreCase("Source=Target")){
			System.out.println("Inside Source=Target ");
			if(typeofdata.equalsIgnoreCase("Source")){
				System.out.println("CellIndexSource is:"+cellIndexSource);
				firstRowCells=firstRow.getCell(cellIndexSource);
				firstRowCells.setCellValue("typeofdataaaaaaaaaaaaaaaaaaaaa");//source
				System.out.println("getStringCellValue is:"+firstRowCells.getStringCellValue());
				firstRowCells.setCellStyle(sourceCellStyle);
				System.out.println("getCellType is:"+firstRowCells.getCellType());
				//setAsActiveCell();
			}
			else if(typeofdata.equalsIgnoreCase("Target")){
				int cellIndexTarget=cellIndexSource+header.length;
				firstRowCells=firstRow.createCell(cellIndexTarget);
				firstRowCells.setCellValue(typeofdata);//target
				firstRowCells.setCellStyle(targetCellStyle);
			}
		}
		else if(sheetName.equalsIgnoreCase("Source!=Target")){
			System.out.println("Inside Source!=Target ");
			String[] Nheader=(String[]) data.get("0");
			int cellIndex = 0;
			cellIndexSource=Nheader.length/2;
			if(typeofdata.equalsIgnoreCase("Source")){
				System.out.println("*******Inside Source!=Target; type of data is:Source ");
				firstRowCells=firstRow.getCell(cellIndexSource);
				firstRowCells.setCellValue(typeofdata);//source
				firstRowCells.setCellStyle(sourceCellStyle);
			}
			else if(typeofdata.equalsIgnoreCase("Difference")){
				System.out.println("*******Inside Source!=Target; type of data is:Difference ");
				cellIndex=cellIndexSource+Nheader.length;
				firstRowCells=firstRow.createCell(cellIndex);
				firstRowCells.setCellValue(typeofdata);//difference
				firstRowCells.setCellStyle(violetCellStyle);
			}
			else if(typeofdata.equalsIgnoreCase("Target")){
				System.out.println("*******Inside Source!=Target; type of data is:Target ");
				cellIndex=cellIndex+Nheader.length;
				firstRowCells=firstRow.createCell(cellIndex);
				firstRowCells.setCellValue(typeofdata);//target
				firstRowCells.setCellStyle(targetCellStyle);
			}
			
		}
		
    	Set<String> keyset = data.keySet();
        int rownum = 1;
        int cellnumber = 0;
        Row row;
        for (String key : keyset)
        {
        	if(columnnum==0)
             row = sheet.createRow(rownum++);
        	else
        	 row = sheet.getRow(rownum++);
            
            Object [] objArr = data.get(key);
            int cellnum = columnnum;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);//Put data into rows before doing its beautification.
               
               
               if(((String)obj).endsWith(".")){//It means the data we are dealing with is header data!
            	   cell.setCellStyle(greyBoldCellStyle);
            	   cell.setCellValue(deleteLastChar((String)obj)); //Again delete the "."at the end of the String to keep the Data Intact
               }
               else{
            	   if(typeofdata.equalsIgnoreCase("source"))
               			cell.setCellStyle(sourceCellStyle);
            	   else if(typeofdata.equalsIgnoreCase("target"))
            		   cell.setCellStyle(targetCellStyle);
            	   else if(typeofdata.equalsIgnoreCase("difference")){
            		   if(((String)obj).equalsIgnoreCase("ok")){
                    	   cell.setCellStyle(greenCellStyle);
                       }
            		   else if(((String)obj).equalsIgnoreCase("yes")){
                    	   cell.setCellStyle(yellowCellStyle);
                       }
            		   else if(((String)obj).equalsIgnoreCase("no") || ((String)obj).equalsIgnoreCase("not ok")) {
            			   cell.setCellStyle(redCellStyle);
            		   }
            		   else if(((String)obj).endsWith("e")){
            			   cell.setCellStyle(greenCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            		   else if(((String)obj).endsWith("d")){
            			   cell.setCellStyle(redCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            		   else if(((String)obj).endsWith("v")){
            			   cell.setCellStyle(violetCellStyle);
            			   cell.setCellValue(deleteLastChar((String)obj));
            		   }
            	   }
            	   else if(typeofdata.equalsIgnoreCase("Summary")){
            		   //Do Nothing with color coding!
            		   Row row1 = sheet.getRow(0);//Get Header Row of sheet number 1
                       for(int colNum = 0; colNum<row1.getLastCellNum();colNum++)   
                       	sheet.autoSizeColumn(colNum);//Auto size
            	   }
//                		else if(obj instanceof Integer)
//                    	cell.setCellValue((Integer)obj);
               }
               
               sheet.autoSizeColumn(cellnum);//Important formatting step here!
            }//End for of 'Object' loop
            cellnumber=cellnum;
        } //End outer for loop
        

//        Row firstrow = sheet.getRow(0);//Get Header Row
//        for(int colNum = 0; colNum<firstrow.getLastCellNum();colNum++)   
//        	sheet.autoSizeColumn(colNum);//Auto size w.r.t no of elements in 'header row'
        
        return cellnumber;
	}
	
	
	private String[] convertStringToStringArray(String str){
		String record[]=str.split(Operator.splitBy);
		return record;
	}
	
	private String convertStringArrayToString(String[] record){
		String str="";
		for(int i=0;i<record.length;i++){
			str+=record[i]+"."+ConfigFileReader.delimiter;
		}
		return str;
	}
	
	
	private  String removeCharAt(String s, int pos) {
	      return s.substring(0, pos) + s.substring(pos + 1);
	}
	
	private String deleteLastChar(String phrase) {

	    String rephrase = "";
	    if (phrase != null && phrase.length() > 1) {
	        rephrase = phrase.substring(0, phrase.length() - 1);
	    }
	    	return rephrase;
	}
	
	private void writeToOutPutStream(String sheetName){
		 try
	        {
	            //Write the workbook in file system
	            FileOutputStream out = new FileOutputStream(new File(outputFilePathName));
	            workbook.write(out);
	            out.close();
	            MainClass.Log.info("'"+sheetName+"' sheet name written successfully on local disk's project path.");
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	}
	
	/**
	 * This method has to be and is in sync with createDiff() method of Operator Class.This method generates the 
	 * column headers w.r.t input of the config files.
	 * @param calculateDevianceOn
	 * @param filter
	 * @return String of Array.Each value containing the unique header columns
	 */
	private String[] createHeaderOfDiff(String calculateDevianceOn,String percentageOrAbsolute,String devianceValues, String filter ){
		String strheaderOfDiff="";
		String[] headerOfDiff=null;
		String header[]=null;
		
		if(filter==null)
	        header=csvReader.readCsvFileHeader(ConfigFileReader.sourceFile);//This file should be visible to all classes!Thus its static
	    else{
	        header=filter.split(Operator.splitBy);//Used Operator.splitBy as delimiter may change in future eg say to ';'
	    }
		
		
		String calculateDevianceOnArray[]=convertStringToStringArray(calculateDevianceOn);
		String perOrAbs[]=convertStringToStringArray(percentageOrAbsolute);
		String[] devianceValuesArray=convertStringToStringArray(devianceValues);
		for(int i=0;i<header.length;i++){
			strheaderOfDiff+=header[i]+ConfigFileReader.delimiter;
			for(int j=0;j<calculateDevianceOnArray.length;j++){
				if(header[i].equalsIgnoreCase(calculateDevianceOnArray[j])){
					if(perOrAbs[j].equalsIgnoreCase("percentage"))
						strheaderOfDiff+="%Deviance"+ConfigFileReader.delimiter+"Within %Range:"+devianceValuesArray[j]+"%"+ConfigFileReader.delimiter;
					else if(perOrAbs[j].equalsIgnoreCase("absolute"))
						strheaderOfDiff+="Within Range:"+devianceValuesArray[j]+ConfigFileReader.delimiter;
					else{
						MainClass.Log.severe("Error in ConfigFile. Check if percentage, or absolute are properly given and spelled");
						//Throw e;
					}
				}
			}
		}
		
		MainClass.Log.info("The header of Diff is:"+strheaderOfDiff);
		headerOfDiff=convertStringToStringArray(strheaderOfDiff);
		return headerOfDiff;
	}//End createHeaderOfDiff()
}
