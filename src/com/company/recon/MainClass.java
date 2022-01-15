package com.company.recon;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.company.recon.exceptions.ColumnMappingFileCorruptException;
import com.company.recon.exceptions.ConfigFileCorruptException;
import com.company.recon.exceptions.DataCorruptException;
import com.company.recon.operations.Operator;
import com.company.recon.reader.ConfigFileReader;
import com.company.recon.reader.CsvReader;
import com.company.recon.reader.MappingFileReader;
import com.company.recon.writer.ConsoleWriter;
import com.company.recon.writer.CustomCsvWriter;
import com.company.recon.writer.ExcelWriter;
import com.company.recon.writer.LogFormatter;

public class MainClass {
	
	private static final String TAG="MainClass";
	public static final Logger Log = Logger.getLogger(MainClass.class.getName());//MainClass Property
	public static String configFile;
	
	private static List<String[]> listOfSrcMinusTarget;//a-b
	private static List<String[]> listOfTargetMinusSrc;//b-a
	private static List<String[]> listOfSrcIntersectionTarget;//a intersection b
	private static List<String[]> listMismatched;// (a-b) union (b-a) or (a union b)-(a intersection b)
	
	private static List<String[]> listOfColumnMappedTarget;
	private static List<String[]> listOfValueMappedTarget;
	private static List<String[]> listSrcOfDiff;
	private static List<String[]> listTargetOfDiff;
	private static List<String[]> listOfDiff;
	
	public static String errorDVC1="Column Numbers of both Source and Target are NOT equal.Check data in Input Files or Give a 'Yes' for filter in ConfigFile";
	public static String errorDVC21="The data in Source has exceeded the number of Column headers.Please check the Source data! ";
	public static String errorDVC22="The data in Target has exceeded the number of Column headers.Please check the Target data!";
	public static String errorDVC31="There are Duplicates in Source Data.Please check the validity of the compositeKey";
	public static String errorDVC32="There are Duplicates in Target Data.Please check the validity of the compositeKey";
	public static String exceptionCFCE1="Exception in ConfigFile.FilterKey is not subset of source and target headers";
	public static String exceptionCFCE21="Exception in ConfigFile.CompositeKey is not subset of filterKey";
	public static String exceptionCFCE22="Exception in ConfigFile.CompositeKey is not subset of source and target headers";
	public static String exceptionCFCE31="Exception in ConfigFile.DevianceKey is not subset of filterKey";
	public static String exceptionCFCE32="Exception in ConfigFile.DevianceKey is not subset  of source and target headers";
	public static String exceptionCFCE4="Exception in ConfigFile.DevianceKey element count should match(Map them!) deviancePercentagesKey count!";
	//CFCE=ConfigFileCorruptionException.
	public static String exceptionCMFCE1="Filter key must be a subset of ColumnMappingKeySet";
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");//MainClass Property
//	public static final String logFilePathName=".\\log\\log.txt";//For windows file system
	public static final String logFilePathName="."+FILE_SEPARATOR+"log"+FILE_SEPARATOR+"log.txt";
	public static final String outputValueMappedTargetFile="."+FILE_SEPARATOR+"data"+FILE_SEPARATOR+"gen"+MainClass.FILE_SEPARATOR+"valueMappedTarget.csv";//For generic file system
	public static void main(String[] args){
		

		try {
			 Log.setUseParentHandlers(false);//instruct your logger not to send it's messages on up to it's parent logger
			//FileHandler: This handler writes all the logging messages to file in the XML format.
//			 Handler fileHandler = new FileHandler("/Users/pankaj/tmp/logger.log", 2000, 5);
			 Handler fileHandler = new FileHandler(logFilePathName);
			 Handler consoleHandler = new ConsoleHandler();
			 LogFormatter customFormatter= new LogFormatter();
			 fileHandler.setFormatter(customFormatter);
			 consoleHandler.setFormatter(customFormatter);
			 Log.addHandler(fileHandler);
			 Log.addHandler(consoleHandler);
			 Log.setLevel(Level.INFO);
//			 The logs will be generated for all the levels equal to or greater than the logger level, for 
//			 example if logger level is set to INFO, logs will be generated for INFO, WARNING and SEVERE logging messages.
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//-------------------------END OF LOGGING FUNDA----------------------------------
		
		configFile=args[0];
		Log.info("Printing Input Parameters for String[] args----> ");
		for(String s : args)
	    {
			Log.info(s);  
	    }
		Log.info("Recon Tool Has Been Started");
		ConfigFileReader configFileReader= new ConfigFileReader();
		ConfigFileReader.initConfigFileStrings(configFile);
		configFileReader.printConfigFileDetails();
		MappingFileReader mappingFileReader=new MappingFileReader();
		
		CsvReader csvReader=new CsvReader();
		Operator operator=new Operator();
		DataCorruptException dataCorruptException=new DataCorruptException("An Object of Data Corrupt Exception");
		ConfigFileCorruptException configFileCorruptException = new ConfigFileCorruptException("An Object of ConfigFile CorruptException");
		ColumnMappingFileCorruptException columnMappingFileCorruptException=new ColumnMappingFileCorruptException("An Object of ColumnMappingFile CorruptException");
		ConsoleWriter consolewriter=new ConsoleWriter();
		ExcelWriter excelwriter=new ExcelWriter();
		CustomCsvWriter customCsvWriter=new CustomCsvWriter();
		
		String sourceHeader=operator.convertStringArrayToString(csvReader.readCsvFileHeader(ConfigFileReader.sourceFile));
		String origtargetHeader=operator.convertStringArrayToString(csvReader.readCsvFileHeader(ConfigFileReader.targetFile));
		
		List<String[]> listOfSrcRecordObjects=csvReader.readCsvFileIntoObjects(ConfigFileReader.sourceFile);
		final List<String[]> listOfOrigTargetRecordObjects=csvReader.readCsvFileIntoObjects(ConfigFileReader.targetFile);
		final List<String[]> listOfChangebleTargetRecordObjects=csvReader.readCsvFileIntoObjects(ConfigFileReader.targetFile);
		List<String[]> listOfTargetRecordObjects = null;
		String targetHeader="";
		
		
		if(ConfigFileReader.valueMappingFile!=null){//'yes' in ConfigFile
			Log.info("valueMappingFile is NOT null,Creating new valueTarget File; Continuing with tool...");
			
			Map<String, String> valueMapping=mappingFileReader.initMappingFileStrings(ConfigFileReader.valueMappingFile,"valueMappingFile");
			listOfValueMappedTarget=operator.createDataForValueMappedCsvFileOfTarget(valueMapping, listOfChangebleTargetRecordObjects,csvReader.readCsvFileHeader(ConfigFileReader.targetFile));//method changes data in listOfChangebleTargetRecordObjects
			
			
			consolewriter.printListToConsole(listOfValueMappedTarget,"valueMappedTargetlist");//Prints header too
			
			customCsvWriter.writeToCsv_v1(listOfValueMappedTarget,outputValueMappedTargetFile);
			System.out.println("Created File:"+outputValueMappedTargetFile);
			ConfigFileReader.targetFile=outputValueMappedTargetFile;
			
		}
		else{
			Log.info("valueMappingFile is (not present)null,Taking the original target file into consideration; Continuing with tool...");
			listOfTargetRecordObjects=listOfOrigTargetRecordObjects;//check!!
		}
		
		
		//----------------------------------END FUNDA OF VALUE MAPPING FILE--------------------------------------
		
		
		if(ConfigFileReader.columnMappingFile!=null){//'yes' in ConfigFile
			Log.info("columnMappingFile is NOT null,Creating new Target File; Continuing with tool...");
			
			
			Map<String, String> columnMapping=mappingFileReader.initMappingFileStrings(ConfigFileReader.columnMappingFile,"columnMappingFile");
			
			//A check!
			if(ConfigFileReader.filter!=null){
				String keySetConcat="";
				Set<String> keyset = columnMapping.keySet();
				for(String key : keyset){  
//				 String value = columnMapping.get(key);
				 keySetConcat+=key+ConfigFileReader.delimiter;
				}
				boolean isfilterCcolumnMappingKeySet=configFileReader.IsASubsetOfB(ConfigFileReader.filter, keySetConcat);
					if(isfilterCcolumnMappingKeySet==false){
						columnMappingFileCorruptException.setMessage(exceptionCMFCE1);
						Log.severe(exceptionCMFCE1);
						throw columnMappingFileCorruptException;
					}
			}
			
			//Another check!
			if(ConfigFileReader.valueMappingFile==null){
				listOfColumnMappedTarget=operator.createDataForColumnMappedCsvFileOfTarget(columnMapping, listOfOrigTargetRecordObjects, operator.convertStringToStringArray(sourceHeader),csvReader.readCsvFileHeader(ConfigFileReader.targetFile));
			}
			else{
				System.out.println("ConfigFileReader.targetFile now is:"+ConfigFileReader.targetFile);
				listOfColumnMappedTarget=operator.createDataForColumnMappedCsvFileOfTarget(columnMapping, csvReader.readFromCsv(ConfigFileReader.targetFile), operator.convertStringToStringArray(sourceHeader),csvReader.readHeaderFromCsv(ConfigFileReader.targetFile));
			}
			
			consolewriter.printListToConsole(listOfColumnMappedTarget,"columnMappedTargetlist");
			
			customCsvWriter.writeToCsv_v1(listOfColumnMappedTarget);//Writes new target csv file to./data/gen/mappedTarget.csv
			
			
			ConfigFileReader.targetFile=CustomCsvWriter.outputTargetCsvFilePathName;//Changing the target file at Run Time
			listOfTargetRecordObjects=csvReader.readFromCsv(ConfigFileReader.targetFile);//CustomCsvWriter.outputTargetCsvFilePathName
			targetHeader=operator.convertStringArrayToString(csvReader.readHeaderFromCsv(ConfigFileReader.targetFile));//Change this method for header;CustomCsvWriter.outputTargetCsvFilePathName
		}
		else{//'no' in ConfigFile for ConfigFileReader.columnMappingFile
			//Do Nothing continue with tool....
			Log.info("columnMappingFile is null,Taking the given Target File; Continuing with tool...");
			//Another check!
			if(ConfigFileReader.valueMappingFile==null){
			listOfTargetRecordObjects=listOfOrigTargetRecordObjects;
			targetHeader=origtargetHeader;
			}
			else{
				listOfTargetRecordObjects=csvReader.readFromCsv(ConfigFileReader.targetFile);//CustomCsvWriter.outputTargetCsvFilePathName
				targetHeader=operator.convertStringArrayToString(csvReader.readHeaderFromCsv(ConfigFileReader.targetFile));
			}
		}
		
		//----------------------------------END FUNDA OF COLUMN MAPPING FILE--------------------------------------
		
		
		//VALIDITY CHECKS OF DATA AT HAND:1 AND VALIDITY OF CONFIGFILE: 1----->
		if(ConfigFileReader.filter!=null){//'yes' in ConfigFile
			Log.info("Filter is not null, Continuing with tool...");
			boolean isfilterCSourceHeader=configFileReader.IsASubsetOfB(ConfigFileReader.filter, sourceHeader);
			boolean isfilterCTargetHeader=configFileReader.IsASubsetOfB(ConfigFileReader.filter, targetHeader);
			if(isfilterCSourceHeader & isfilterCTargetHeader){
				Log.info("FilterKey is  subset of source and target headers, Continuing with tool...");
			}
			else{
				configFileCorruptException.setMessage(exceptionCFCE1);
				Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
			
		}
		else{
			boolean columnnumValid=csvReader.checkColumnNumberEqualityOfData(ConfigFileReader.sourceFile,ConfigFileReader.targetFile);
			if(columnnumValid){
				Log.info("Column Numbers of both Source and Target are equal.Proceeding with the next data validity check...");
			}
			else{
				dataCorruptException.setMessage(errorDVC1);
				Log.severe(dataCorruptException.getMessage());
				throw dataCorruptException;
			}
		}

		//VALIDITY OF CONFIGFILE 2--------------->
		if(ConfigFileReader.filter!=null){//'yes' in ConfigFile
			boolean isCompositeKeyCFilterKey=configFileReader.IsASubsetOfB(ConfigFileReader.compositeKey, ConfigFileReader.filter);
			if(isCompositeKeyCFilterKey){
				Log.info("CompositeKey is  subset of FilterKey, Continuing with tool...");
			}
			else{
				configFileCorruptException.setMessage(exceptionCFCE21);
				Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
		}
		else{//'no' in ConfigFile for ConfigFileReader.filter
			boolean isCompositeKeyCSourceHeader=configFileReader.IsASubsetOfB(ConfigFileReader.compositeKey, sourceHeader);
			boolean isCompositekeyCTargetHeader=configFileReader.IsASubsetOfB(ConfigFileReader.compositeKey, targetHeader);
			if(isCompositeKeyCSourceHeader & isCompositekeyCTargetHeader){
			Log.info("CompositeKey is  subset of source and target headers, Continuing with tool...");
			}
			else{
				configFileCorruptException.setMessage(exceptionCFCE22);
				Log.info(configFileCorruptException.getMessage());
				Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
		}
		
		
		//VALIDITY OF CONFIGFILE 3--------------->
		if(ConfigFileReader.filter!=null){//'yes' in ConfigFile
			boolean isDevianceKeyCFilterKey=configFileReader.IsASubsetOfB(ConfigFileReader.calculateDevianceOn, ConfigFileReader.filter);
			if(isDevianceKeyCFilterKey){
				Log.info("DevianceKey is  subset of FilterKey, Continuing with tool...");
			}
			else{
				configFileCorruptException.setMessage(exceptionCFCE31);
				Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
		}
		else{//'no' in ConfigFile
			boolean isDevianceKeyCSourceHeader=configFileReader.IsASubsetOfB(ConfigFileReader.calculateDevianceOn, sourceHeader);
			boolean isDeviancekeyCTargetHeader=configFileReader.IsASubsetOfB(ConfigFileReader.calculateDevianceOn, targetHeader);
			if(isDevianceKeyCSourceHeader & isDeviancekeyCTargetHeader){
				Log.info("DevianceKey is  subset of source and target headers, Continuing with tool...");
			}
			else{
				configFileCorruptException.setMessage(exceptionCFCE32);
				Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
		}
		
		//VALIDITY OF CONFIGFILE 4--------------->
		if(configFileReader.areLengthsEqual(ConfigFileReader.calculateDevianceOn, ConfigFileReader.devianceValues)){
			Log.info("DevianceKey  had devianceValues have equal lengths.Continuing with tool....");
		}
		else{
			configFileCorruptException.setMessage(exceptionCFCE4);
			Log.severe(configFileCorruptException.getMessage());
			throw configFileCorruptException;
		}
		
		//VALIDITY CHECKS OF DATA AT HAND:2----->
		boolean sourceValid=csvReader.checkValidityOfSourceOrTargetData(ConfigFileReader.sourceFile);
		if(sourceValid){
			// e=new DataCorruptException("The data in Source has exceeded the number of Column headers.Please check the Source data! ");
			dataCorruptException.setMessage(errorDVC21);
			Log.severe(dataCorruptException.getMessage());
			throw dataCorruptException;
		}
		else{
			Log.info("Passed 'Validity of Data in Source'. Continuing with the tool....");
		}
		
		
		boolean targetValid=csvReader.checkValidityOfSourceOrTargetData(ConfigFileReader.targetFile);
		if(targetValid){
			dataCorruptException.setMessage(errorDVC22);
			Log.severe(dataCorruptException.getMessage());
			throw dataCorruptException;
		}
		else{
			Log.info("Passed 'Validity of Data in Target'. Continuing with the tool....");
		}
		
		
//		List<String[]> listOfSrcRecordObjects=csvReader.readCsvFileIntoObjects(ConfigFileReader.sourceFile);
//		List<String[]> listOfTargetRecordObjects=csvReader.readCsvFileIntoObjects(ConfigFileReader.targetFile);
		
		//VALIDITY CHECKS OF DATA AT HAND:3----->
		boolean sourceDuplicate=operator.checkDuplicateTupleOccuranceonSourceorTarget(listOfSrcRecordObjects, ConfigFileReader.compositeKey);
		if(sourceDuplicate){
			dataCorruptException.setMessage(errorDVC31);
			Log.severe(dataCorruptException.getMessage());
			throw dataCorruptException;
		}
		else{
			Log.info("Passed 'Duplicate Check Validity' in Source. Continuing with the tool....");
		}
		
		boolean targetDuplicate=operator.checkDuplicateTupleOccuranceonSourceorTarget(listOfTargetRecordObjects, ConfigFileReader.compositeKey);
		if(targetDuplicate){
			dataCorruptException.setMessage(errorDVC32);
			Log.severe(dataCorruptException.getMessage());
			throw dataCorruptException;
		}
		else{
			Log.info("Passed 'Duplicate Check Validity' in Target Data. Continuing with the tool...");
		}
//------------------------------ Data Validity Checks are Over--------------------------------		
		
		
		
		consolewriter.printListToConsole(listOfSrcRecordObjects,"sourcelist");
		consolewriter.printListToConsole(listOfTargetRecordObjects,"targetlist");

		
		
		listOfSrcMinusTarget=operator.createSrcMinusTarget(listOfSrcRecordObjects, listOfTargetRecordObjects,ConfigFileReader.compositeKey,ConfigFileReader.filter);
		consolewriter.printListToConsole(listOfSrcMinusTarget,"Source-Target");


		listOfTargetMinusSrc=operator.createSrcMinusTarget(listOfTargetRecordObjects, listOfSrcRecordObjects,ConfigFileReader.compositeKey,ConfigFileReader.filter);//Just replace the order of parameter
		consolewriter.printListToConsole(listOfTargetMinusSrc,"Target-Source");
		
		//For perfectly matching srcIntersectionTarget you don't need compositeKey
		listOfSrcIntersectionTarget=operator.srcIntersectionTarget(listOfSrcRecordObjects,listOfTargetRecordObjects,ConfigFileReader.filter);
		consolewriter.printListToConsole(listOfSrcIntersectionTarget,"Source=Target");
		
		listSrcOfDiff=operator.createSrcOrTargetOfDiff(listOfSrcRecordObjects, listOfTargetRecordObjects, ConfigFileReader.compositeKey,ConfigFileReader.filter);
		consolewriter.printListToConsole(listSrcOfDiff,"Source Of Diff");
		
		listTargetOfDiff=operator.createSrcOrTargetOfDiff(listOfTargetRecordObjects, listOfSrcRecordObjects, ConfigFileReader.compositeKey,ConfigFileReader.filter);
		consolewriter.printListToConsole(listTargetOfDiff,"Target Of Diff");
		
		listOfDiff=operator.createDiff(listSrcOfDiff, listTargetOfDiff, ConfigFileReader.calculateDevianceOn,ConfigFileReader.devianceValues,ConfigFileReader.filter);
		consolewriter.printListToConsole(listOfDiff,"Source!=Target");
		
		
//TODO:		
//		listMismatched=operator.mismatchedList(listOfSrcRecordObjects, listOfTargetRecordObjects);
//		consolewriter.printListToConsole(listMismatched,"Source!=Target");
	
		String summaryHeader="Recon Summary,Count";
		String recordsInSource[]={"Total Number of Records Present in Source",operator.getSizeOfList(listOfSrcRecordObjects)};
		String recordsInTarget[]={"Total Number of Records Present in Target",operator.getSizeOfList(listOfTargetRecordObjects)};
		String srcMinusTarget[]={"Total number of Records Present in Source But not present in Target",operator.getSizeOfList(listOfSrcMinusTarget)};
		String targetMinusSource[]={"Total Number of Records Present in Target But not present in Source",operator.getSizeOfList(listOfTargetMinusSrc)};
		String matchingPerfectly[]={"Total Number of Records Matching Perfectly",operator.getSizeOfList(listOfSrcIntersectionTarget)};
		String recordsMismatched[]={"Total Number of Records Miss Matched",operator.getSizeOfList(listOfDiff)};
		String matchingWithinDeviance[]={"Total Number of Records Matching Within Deviance",Integer.parseInt(operator.getSizeOfList(listOfSrcIntersectionTarget))+operator.noOfRecordMatchingWithinDeviance(listOfDiff)+""};
		String missMatchedOutsideDeviance[]={"Total Number of Records Miss Matched Outside Deviance",operator.noOfRecordMatchingOutsideDeviance(listOfDiff)+""};
		
		List<String[]> summaryList=new LinkedList<String[]>();
		summaryList.add(recordsInSource);
		summaryList.add(recordsInTarget);
		summaryList.add(srcMinusTarget);
		summaryList.add(targetMinusSource);
		summaryList.add(matchingPerfectly);
		summaryList.add(recordsMismatched);
		summaryList.add(matchingWithinDeviance);
		summaryList.add(missMatchedOutsideDeviance);
		
		excelwriter.writeToExcel(summaryList,"Summary",summaryHeader);
		excelwriter.writeToExcel(listOfSrcRecordObjects,"Source",ConfigFileReader.filter);
		excelwriter.writeToExcel(listOfTargetRecordObjects,"Target",ConfigFileReader.filter);
		excelwriter.writeToExcel(listOfSrcMinusTarget,"Source-Target",ConfigFileReader.filter);
		excelwriter.writeToExcel(listOfTargetMinusSrc,"Target-Source",ConfigFileReader.filter);
		excelwriter.writeToExcelForIntersection(listOfSrcIntersectionTarget,"Source=Target",ConfigFileReader.filter);
		excelwriter.writeToExcelForDiff(listSrcOfDiff,listTargetOfDiff,listOfDiff,"Source!=Target",ConfigFileReader.filter);
		
		
		//------------------Operations on Filters----------------------------->
		
		
//		listOfSrcMinusTargetOnFilter=operator.srcMinusTarget(listOfSrcRecordObjects, listOfTargetRecordObjects, filter);
//		consolewriter.printListToConsole(listOfSrcMinusTargetOnFilter,"Source-target on filter: "+filter);
//		excelwriter.writeToExcel(listOfSrcMinusTargetOnFilter,"Source-Target on filter",filter);
//		
//		listOfTargetMinusSourceOnFilter=operator.srcMinusTarget(listOfTargetRecordObjects, listOfSrcRecordObjects, filter);
//		consolewriter.printListToConsole(listOfTargetMinusSourceOnFilter,"Target-Source on filter: "+filter);
//		excelwriter.writeToExcel(listOfTargetMinusSourceOnFilter,"Target-Source on filter",filter);
//		
//		listOfSrcIntersectionTargetOnFilter=operator.srcIntersectionTarget(listOfSrcRecordObjects, listOfTargetRecordObjects, filter);
//		consolewriter.printListToConsole(listOfSrcIntersectionTargetOnFilter,"Source=Target on filter: "+filter);
//		excelwriter.writeToExcel(listOfSrcIntersectionTargetOnFilter,"Source=Target on filter",filter);
	}
	
	
	
}
