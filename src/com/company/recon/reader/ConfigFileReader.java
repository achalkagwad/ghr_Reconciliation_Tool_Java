package com.company.recon.reader;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.company.recon.MainClass;
import com.company.recon.exceptions.ConfigFileCorruptException;



public class ConfigFileReader extends CustomFileReader {

//	private static final Logger Log = Logger.getLogger(ConfigFileReader.class.getName());
	
	public static String sourceFile;//="D:\\Recon_Files\\Student_Source.csv";//TODO take as input from String[] args
	public static String targetFile;//="D:\\Recon_Files\\Student_Target.csv"; //TODO take as input from String[] args
	public static String filter;//="studentid,studentsem,studentsubject"; //TODO take as input from String[] args //StudentSubject,StudentName
	public static String compositeKey;//="studentid,studentsem,studentsubject";
	public static String calculateDevianceOn;//="studentage";
	public static String percentageOrAbsolute;
	public static String devianceValues;//="5";
	public static String configSplitBy = "=";
	public static String delimiter;
	public static String columnMappingFile;
	public static String valueMappingFile;
	
	public static String filterYesorNOException="Error in Config File. Please Check whether filter has a 'yes' or a 'no'as the first element of String";
	public static String columnMappingFileYesorNOException="Error in Config File. Please Check whether columnMappingFile has a 'yes' or a 'no'as the first element of String";
	public static String valueMappingFileYesorNOException="Error in Config File. Please Check whether valueMappingFile has a 'yes' or a 'no'as the first element of String";
	public static String exceptionCFCE2="Please check whether the ConfigFile has no empty strings at the end of the File!";
	public static String exceptionCFCE3="Error in ConfigFile.Check if percentage, or absolute are properly given and spelled";
	public static ConfigFileCorruptException configFileCorruptException;
	
	static{
        MainClass.Log.info("Static Block for 'ConfigFileReader.java' ");
      //To ensure exception object is created only once and thus optimize on performance!
        configFileCorruptException=new ConfigFileCorruptException(filterYesorNOException);
    }
	

	
	/**
	 * This method takes a path to configFile in HardDisk of local pc and than initializes
	 * all the config file parameters necessary to run the Recon Tool. Note:There are
	 *  certain assumptions made regarding the display config File for this method to work correctly.
	 *  This assumptions are mentioned in the documentation that comes with the Recon Tool.
	 *  If the assumptions are not met, Exceptions are thrown by the Recon tool!
	 * @param pathtoconfigfile
	 */
	public static void initConfigFileStrings(String pathtoconfigfile){
//		ConfigFileCorruptException configFileCorruptException=new ConfigFileCorruptException(filterYesorNOException);//TODO Put this code in static initializer block
		List<String[]> configObjects=readFileIntoObjects(pathtoconfigfile);
		 Map<String, String> data = new LinkedHashMap<String, String>();//LinkedHashMap ensures insertion order
		 
		 try{
		 for(int i=0;i<configObjects.size();i++){
	        	data.put(configObjects.get(i)[0], configObjects.get(i)[1]);
	        }
		 }
		 catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
			 configFileCorruptException.setMessage(exceptionCFCE2);
			 throw configFileCorruptException;
		 }
		 
		 Set<String> keyset = data.keySet();
		 
		 for(String key : keyset){
	        
			 String value = data.get(key);
			 if(key.equalsIgnoreCase("SourceFile")){
				 sourceFile="."+MainClass.FILE_SEPARATOR+"data"+MainClass.FILE_SEPARATOR+value;//O.S generic
			 }
			 else if(key.equalsIgnoreCase("targetFile")){
				 targetFile="."+MainClass.FILE_SEPARATOR+"data"+MainClass.FILE_SEPARATOR+value;//O.S generic
			 }
			 else if(key.equalsIgnoreCase("filter")){
				 String[] filterArray=convertStringToStringArray(value);
				 if(filterArray[0].equalsIgnoreCase("Yes")){
					 filter=convertStringArrayToString(filterArray);//This method ignores the first parameter and concats	 
				 }
				 else if(filterArray[0].equalsIgnoreCase("No")){
					 filter=null;
				 }
				 else{
					 configFileCorruptException.setMessage(filterYesorNOException);
					 MainClass.Log.severe(configFileCorruptException.getMessage());
					 throw configFileCorruptException;
				 }
				 
			 }
			 else if(key.equalsIgnoreCase("compositeKey")){
				 compositeKey=value;
			 }
			 else if(key.equalsIgnoreCase("calculateDevianceOn")){
				 calculateDevianceOn=returnStringOfIndexRecord_v1(value,0);
				 percentageOrAbsolute=returnStringOfIndexRecord_v1(value, 1); //
				 devianceValues=returnStringOfIndexRecord_v1(value,2);
				 testIfPercentageOrAbsoluteStringIsCorrect(percentageOrAbsolute);
				 //calculateDevianceOn=value; //earlier code
			 }
//			 else if(key.equalsIgnoreCase("deviancePercentages")){
//				 devianceValues=value;
//			 } //No Longer needed
			 else if(key.equalsIgnoreCase("delimiter")){
				 delimiter=value;
			 }
			 else if(key.equalsIgnoreCase("columnMappingFile")){
//				 columnMappingFile="."+MainClass.FILE_SEPARATOR+"config"+MainClass.FILE_SEPARATOR+value;
				 
				 String[] columnMappingFileArray=convertStringToStringArray(value);
				 if(columnMappingFileArray[0].equalsIgnoreCase("Yes")){
					//This method ignores the first parameter and concats
					 columnMappingFile="."+MainClass.FILE_SEPARATOR+"config"+MainClass.FILE_SEPARATOR+deleteLastChar(convertStringArrayToString(columnMappingFileArray));
				 }
				 else if(columnMappingFileArray[0].equalsIgnoreCase("No")){
					 columnMappingFile=null;
				 }
				 else{
					 configFileCorruptException.setMessage(columnMappingFileYesorNOException);
					 MainClass.Log.severe(configFileCorruptException.getMessage());
					 throw configFileCorruptException;
				 }
			 }
			 else if(key.equalsIgnoreCase("valueMappingFile")){
				 String[] valueMappingFileArray=convertStringToStringArray(value);
				 if(valueMappingFileArray[0].equalsIgnoreCase("Yes")){
					//This method ignores the first parameter and concats
					 valueMappingFile="."+MainClass.FILE_SEPARATOR+"config"+MainClass.FILE_SEPARATOR+deleteLastChar(convertStringArrayToString(valueMappingFileArray));
				 }
				 else if(valueMappingFileArray[0].equalsIgnoreCase("No")){
					 valueMappingFile=null;
				 }
				 else{
					 configFileCorruptException.setMessage(valueMappingFileYesorNOException);
					 MainClass.Log.severe(configFileCorruptException.getMessage());
					 throw configFileCorruptException;
				 }
			 }
			 else{
				 configFileCorruptException.setMessage("Illegal parameter in config file.Check whether attributes are spelled correctly");
				 MainClass.Log.severe(configFileCorruptException.getMessage());
				 throw configFileCorruptException;
			 }
	        }
	}//End method initConfigFileStrings
	
	
	/**
	 * Test method to check whether ConfigFile Parameters are correctly assigned.
	 * To correctly test call this method only after initConfigFileStrings() is called.
	 */
	public  void printConfigFileDetails(){
		MainClass.Log.info("Source File is:"+sourceFile);
		MainClass.Log.info("calculateDevianceOn is:"+calculateDevianceOn);
		MainClass.Log.info("percentageOrAbsolute is:"+percentageOrAbsolute);
		MainClass.Log.info("devianceValues is:"+devianceValues);
		System.out.println("columnMappingFile is:"+columnMappingFile);
	}
	
	
	/**
	 * This method check if String 'key',(which should be a delimited String) is a subset of String 'header'.
	 * (which also should be a delimited String). 
	 * @param key
	 * @param header
	 * @return If key is subset of header , method returns true,else false
	 */
	public boolean IsASubsetOfB(String key,String header){
		
		String keyRecord[]=key.split(delimiter);
		String headerRecord[]=header.split(delimiter);

		int index=0;
		
		for(int i=0;i<keyRecord.length;i++){
			for(int j=0;j<headerRecord.length;j++){
				if(keyRecord[i].equalsIgnoreCase(headerRecord[j])){
					index++;
					break;
				}
			}
		}
		
		if(keyRecord.length==index){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean areLengthsEqual(String configFileKey1,String configFileKey2){
		String keyRecord1[]=configFileKey1.split(delimiter);
		String keyRecord2[]=configFileKey2.split(delimiter);
		
		return keyRecord1.length==keyRecord2.length;
	}
	
	
	
	/**
	 * Helper method to test if the spelling and order of 'percentageOrAbsolute' is correct in ConfigFile
	 * @param percentageOrAbsolute
	 */
	private static void testIfPercentageOrAbsoluteStringIsCorrect(String percentageOrAbsolute){
		String perOrAbs[]=convertStringToStringArray(percentageOrAbsolute);
		
		for(int i=0;i<perOrAbs.length;i++){
			if(perOrAbs[i].equalsIgnoreCase("percentage")){
				//Do Nothing. Continue with tool....
			}	
			else if(perOrAbs[i].equalsIgnoreCase("absolute")){
				//Do Nothing. Continue with tool....
			}	
			else{
				configFileCorruptException.setMessage(exceptionCFCE3);
				MainClass.Log.severe(configFileCorruptException.getMessage());
				throw configFileCorruptException;
			}
		}
	}
	
	/**
	 * This method returns respective values of the String 'calculateDevianceOn'
	 * only if its in the format value,value,value:value,value,value(multiple of 3)
	 * eg:studentmarks,absolute,4:studentage,percentage,10
	 * @param calculateDevianceOn
	 * @param startIndex
	 * @return
	 */
	private static String returnStringOfIndexRecord_v1(String calculateDevianceOn,int startIndex){
		String[] recordArray=calculateDevianceOn.split(":");
		String indexRecord="";
		String[] valueArray={};
		
		for(int i=0;i<recordArray.length;i++){
			valueArray=recordArray[i].split(delimiter);
			indexRecord+=valueArray[startIndex]+ConfigFileReader.delimiter;
//			startIndex=startIndex+3;
		}
		return indexRecord;
	}
	
	
	/**
	 * This method returns respective values of the String 'calculateDevianceOn'
	 * only if its in the format value,value,value,value,value,value(multiple of 3)
	 * eg:studentmarks,absolute,4,studentage,percentage,10
	 * @param calculateDevianceOn
	 * @param startIndex
	 * @return
	 */
	private static String returnStringOfIndexRecord(String calculateDevianceOn,int startIndex){
		String[] calculateDevianceOnArray=convertStringToStringArray(calculateDevianceOn);
		int noOfRecords=(calculateDevianceOnArray.length+1)/3;//Here '3' is hardcoded assuming the string will have a set of 3 values each
		String indexRecord="";
		for(int i=0;i<noOfRecords;i++){
			indexRecord+=calculateDevianceOnArray[startIndex]+ConfigFileReader.delimiter;
			startIndex=startIndex+3;
		}
		return indexRecord;
	}
	
	/**
	 * Helper method
	 * This method deletes the last character of a String and returns the new String
	 * @param phrase
	 * @return
	 */
	private static String deleteLastChar(String phrase) {

	    String rephrase = "";
	    if (phrase != null && phrase.length() > 1) {
	        rephrase = phrase.substring(0, phrase.length() - 1);
	    }
	    	return rephrase;
	}
	
}
