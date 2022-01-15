package com.company.recon.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.company.recon.MainClass;

public class CustomFileReader {

	public static String SplitBy = "=";
	
	
	//TODO: Put this method in super class named say 'CustomFileReader.java' and extend this class from CustomFileReader.java
	public static  List<String[]> readFileIntoObjects(String pathtofile) {
		 
		List<String[]> configObjects=null;
		BufferedReader br = null;
		String line = "";
		
	 
		try {
	 
			configObjects = new LinkedList<String[]>();
			br = new BufferedReader(new FileReader(pathtofile));
		
//			MainClass.Log.info("Printing File-------->");
			for(int i=0;(line = br.readLine()) != null;i++){
				// use equal to as separator
				String[] record = line.split(SplitBy);
				
				
				for(int j=0;j<record.length;j++){
//					MainClass.Log.info(record[j]+" ");
				}

				
				configObjects.add(record);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}
	 
		MainClass.Log.info("Done Reading from  file:"+pathtofile);
		return configObjects;
	  }//End method readFileIntoObjects
	
	
	//protected methods------------------------------------->
		protected static String[] convertStringToStringArray(String str){
			String record[]=str.split(ConfigFileReader.delimiter);
			return record;
		}
		
		
		/**
		 * This method ignores the first parameter of the String Array and concats
		 * and returns the string equivalent of the array	 
		 * @param record
		 * @return
		 */
		protected static String convertStringArrayToString(String[] record){
			String str="";
			for(int i=1;i<record.length;i++){
				str+=record[i]+ConfigFileReader.delimiter;
			}
			return str;
		}
}
