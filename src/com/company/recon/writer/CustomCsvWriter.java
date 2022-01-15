package com.company.recon.writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.company.recon.MainClass;
import com.opencsv.CSVWriter;

public class CustomCsvWriter {

	CSVWriter writer;
	public static final String outputTargetCsvFilePathName="."+MainClass.FILE_SEPARATOR+"data"+MainClass.FILE_SEPARATOR+"gen"+MainClass.FILE_SEPARATOR+"columnMappedTarget.csv";//For generic file system
	public CustomCsvWriter(){
		
	}
	
	/**
	 * This method takes a List<String[]> as the data to be written to the hard coded csv file path name
	 * which is 'outputTargetCsvFilePathName'
	 * @param datatowrite
	 */
	public  void writeToCsv(List<String[]> datatowrite) //String 'pathToCsvFile' can be another parameter to this method if needed
	{
	      
		try {
			writer = new CSVWriter(new FileWriter(outputTargetCsvFilePathName, false));//Pass true to append data to the current csv file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	      
	      for(int i=0;i<datatowrite.size();i++){
//	    	  String [] record=datatowrite.get(i);
	    	  writer.writeNext(datatowrite.get(i));
	      }
	        
	      try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	}//End method writeToCsv()
	
	/**
	 * This method takes a List<String[]> as the data to be writen to the hardcoded csv file path name
	 * which is 'outputTargetCsvFilePathName'.This method uses OpenCSV Apis to write data to csv and thus
	 * each data entry is enclosed in "".
	 * @param datatowrite
	 */
	public  void writeToCsv_v1(List<String[]> datatowrite){
		
		try {
			writer = new CSVWriter(new FileWriter(outputTargetCsvFilePathName, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Pass true to append data to the current csv file
		
		writer.writeAll(datatowrite);
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method takes a List<String[]> as the data to be writen to the file path(csv file) given as a parameter
	 * which is 'outputTargetCsvFilePathName'.This method uses OpenCSV Apis to write data to csv and thus
	 * each data entry is enclosed in "".
	 * @param datatowrite
	 * @param pathtofile
	 */
	public  void writeToCsv_v1(List<String[]> datatowrite,String pathtofile){
		
		try {
			writer = new CSVWriter(new FileWriter(pathtofile, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Pass true to append data to the current csv file
		
		writer.writeAll(datatowrite);
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
