package com.company.recon.reader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.company.recon.MainClass;
import com.company.recon.exceptions.ColumnMappingFileCorruptException;
import com.company.recon.exceptions.ConfigFileCorruptException;
import com.company.recon.exceptions.ValueMappingFileCorruptException;

public class MappingFileReader extends CustomFileReader{

	private static String exceptionCMFCE1="Please check whether the ColumnMappingFile has no empty strings at the end of the File!";
	private static String exceptionVMFCE1="Please check whether the ValueMappingFile has no empty strings at the end of the File!";
//	public static String exceptionCFCE3="Error in ConfigFile.Check if percentage, or absolute are properly given and spelled";
	public ColumnMappingFileCorruptException columnMappingFileCorruptException;
	public ValueMappingFileCorruptException valueMappingFileCorruptException;
	
//	static{
//        System.out.println("Static Block for 'ColumnMappingFileReader.java' ");
//      //To ensure exception object is created only once and thus optimize on performance!
//        columnMappingFileCorruptException=new ColumnMappingFileCorruptException("An Object of ColumnMappingFileCorruptException");
//    }
	
	public  Map<String, String> initMappingFileStrings(String pathtomappingfile,String typeoffile){
		
		List<String[]> configObjects=readFileIntoObjects(pathtomappingfile);
		 Map<String, String> data = new LinkedHashMap<String, String>();//LinkedHashMap ensures insertion order
		 
		 try{
		 for(int i=0;i<configObjects.size();i++){
	        	data.put(configObjects.get(i)[0], configObjects.get(i)[1]);
	        }
		 }
		 catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
			 if(typeoffile.equalsIgnoreCase("columnMappingFile")){
				 columnMappingFileCorruptException=new ColumnMappingFileCorruptException(exceptionCMFCE1);
				 throw columnMappingFileCorruptException;
			 }
			 else{
				 valueMappingFileCorruptException=new ValueMappingFileCorruptException(exceptionVMFCE1);
				 throw valueMappingFileCorruptException;
			 }
		 }
		 
//		 String concatValue="";
//		 Set<String> keyset = data.keySet();
//		 for(String key : keyset){
//		        
//			 String value = data.get(key);
//			 concatValue+=value+ConfigFileReader.delimiter;
//
//		 }//End Outer For Loop
//		 System.out.println("ConcatValue is:"+concatValue);
		 
		 return data;
	}//End Method initColumnMappingFileStrings()
	
	
	public  Map<String, String> initReverseMappingFileStrings(String pathtomappingfile,String typeoffile){
		
		List<String[]> configObjects=readFileIntoObjects(pathtomappingfile);
		 Map<String, String> data = new LinkedHashMap<String, String>();//LinkedHashMap ensures insertion order
		 
		 try{
		 for(int i=0;i<configObjects.size();i++){
	        	data.put(configObjects.get(i)[1], configObjects.get(i)[0]);
	        }
		 }
		 catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
			 if(typeoffile.equalsIgnoreCase("columnMappingFile")){
				 columnMappingFileCorruptException=new ColumnMappingFileCorruptException(exceptionCMFCE1);
				 throw columnMappingFileCorruptException;
			 }
			 else{
				 valueMappingFileCorruptException=new ValueMappingFileCorruptException(exceptionVMFCE1);
				 throw valueMappingFileCorruptException;
			 }
		 }
		 
//		 String concatValue="";
//		 Set<String> keyset = data.keySet();
//		 for(String key : keyset){
//		        
//			 String value = data.get(key);
//			 concatValue+=value+ConfigFileReader.delimiter;
//
//		 }//End Outer For Loop
//		 System.out.println("ConcatValue is:"+concatValue);
		 
		 return data;
	}//End Method initColumnMappingFileStrings()
	
}
