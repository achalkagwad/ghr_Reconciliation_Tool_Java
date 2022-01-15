package com.company.recon.writer;

import java.util.List;
import java.util.ListIterator;

import com.company.recon.MainClass;
import com.company.recon.reader.ConfigFileReader;

public class ConsoleWriter {

	/**
	 * This method takes data i.e a list of String array and the name of the list(as string) 
	 *  and prints the data onto the Console
	 * @param linkedList
	 * @param typeoflist
	 */
	public  void printListToConsole(List<String[]> linkedList,String typeoflist){
		 // ListIterator approach
		MainClass.Log.info("Printing List--->"+typeoflist+" ;Num of Records="+linkedList.size());
       ListIterator<String[]> listIterator = linkedList.listIterator();
       while (listIterator.hasNext()) {
//           MainClass.Log.info(listIterator.next());
           printSourceObjectRecord(listIterator.next());
       }
	}
	
	public static void printListToConsole(List<String> linkedList){
		MainClass.Log.info("Printing List--->;Num of Records="+linkedList.size());
        for(int i=0;i<linkedList.size();i++){
        	MainClass.Log.info(i+":"+linkedList.get(i));
        }
	}

	
	public  void printSourceObjectRecord(String[] sourceObjectRecord){
		String row="";
		for(int i=0;i<sourceObjectRecord.length;i++){
			row+=sourceObjectRecord[i]+ConfigFileReader.delimiter;	
		}
		MainClass.Log.info(row);
	}
}
