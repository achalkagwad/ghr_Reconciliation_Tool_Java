package com.company.recon.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;



public class CsvReader {
	
	CSVReader reader;
	String cvsSplitBy = ConfigFileReader.delimiter;

	public  List<String[]> readCsvFileIntoObjects(String pathtofile) {
		 
		List<String[]> sourceObjects=null;
		BufferedReader br = null;
		String line = "";

	 
		try {
	 
			sourceObjects = new LinkedList<String[]>();
			br = new BufferedReader(new FileReader(pathtofile));
		
			for(int i=0;(line = br.readLine()) != null;i++){
				if(i==0)
					continue;//To ignore the first line which is assumed to be the column headers of the database
				// use comma as separator
				String[] studentRecord = line.split(cvsSplitBy);
				sourceObjects.add(studentRecord);
//				studentObjects.add(new StudentObject(studentRecord[0],studentRecord[1],studentRecord[2],studentRecord[3],studentRecord[4]));
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
	 
		System.out.println("Done Reading from file:"+pathtofile);
		return sourceObjects;
	  }//End method readFileIntoObjects
	
	public String[] readCsvFileHeader(String pathToFile){
		BufferedReader br = null;
		String line = "";
		String[] studentHeader={};
		
		try {
			br = new BufferedReader(new FileReader(pathToFile));
			line = br.readLine();
			studentHeader = line.split(cvsSplitBy);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}//End Finally
		return studentHeader;
	}
	
	/**
	 * This method reads data into List<String[]> and returns the list.
	 * Note:This method reads data effectively written by CSVWriter of OpenCsv Third Party Library
	 * @param pathtofile
	 * @return
	 */
	public  List<String[]> readFromCsv(String pathtofile) {
		
		List<String[]> allRows=null;
		char csvSplitBy=cvsSplitBy.charAt(0);
		
		try {
		 //Build reader instance
	      reader = new CSVReader(new FileReader(pathtofile), csvSplitBy, '"', 1);//Ignore first row
	      //Read all rows at once
	      allRows = reader.readAll();
	      reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return allRows;
		
	}
	
	/**
	 * Method to read data written by Writer of OpenCsv APIs
	 * Thus this reads data in which each entry is enclosed by ""  
	 * @param pathtofile
	 * @return the header[] of the csv file
	 */
	public  String[] readHeaderFromCsv(String pathtofile) {
		String header[]={};
		char csvSplitBy=cvsSplitBy.charAt(0);
		
		try {
		 reader = new CSVReader(new FileReader(pathtofile), csvSplitBy , '"' , 0);//(line numbers start from zero)
		 header = reader.readNext();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		return header;
		
	}
	
	public boolean checkColumnNumberEqualityOfData(String source_pathToFile, String target_pathToFile){
		String[] sourceHeader= readCsvFileHeader(source_pathToFile);
		String[] targetHeader= readCsvFileHeader(target_pathToFile);
		return (sourceHeader.length==targetHeader.length);
	}
	
	
	public  boolean checkValidityOfSourceOrTargetData(String pathToFile) {
		 
		BufferedReader br = null;
		String line = "";
		boolean valid=false;
		
		String[] header= readCsvFileHeader(pathToFile);
	 
		try {
	 
			br = new BufferedReader(new FileReader(pathToFile));
		
			for(int i=0;(line = br.readLine()) != null;i++){
				if(i==0)
					continue;//To ignore the first line which is assumed to be the column headers of the database
				// use comma as separator
				String[] record = line.split(cvsSplitBy);

				if(record.length>header.length){
					valid=true;
					break;
				}

			}//End for loop
	 
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
	 
		System.out.println("Done Validating data from file:"+pathToFile);
		return valid;
	  }//End method readFileIntoObjects
}
