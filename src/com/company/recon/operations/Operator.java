package com.company.recon.operations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.company.recon.MainClass;
import com.company.recon.exceptions.ColumnMappingFileCorruptException;
import com.company.recon.reader.ConfigFileReader;


public class Operator {

	private String line = "";
	public static String splitBy = ConfigFileReader.delimiter;
//	private static final Logger Log = Logger.getLogger(Operator.class.getName());
	private static String exceptionCMFCE1="Please check whether the ColumnMappingFile has values similar to that of Source and Target headers(even in Case Sensitivity!)";
	
	public static ColumnMappingFileCorruptException columnMappingFileCorruptException;
	
	static{
        System.out.println("Static Block for 'Operator.java' ");
      //To ensure exception object is created only once and thus optimize on performance!
        columnMappingFileCorruptException=new ColumnMappingFileCorruptException("An Object of ColumnMappingFileCorruptException");
    }
	
	/**
	 * Method which performs (a intersection b) operation; i.e all the elements in 'a' that are also present in 'b'.
	 * If filter is null whole column headers are taken, else filter headers are taken.
	 * Note:For perfectly matching srcIntersectionTarget you don't need compositeKey as a parameter
	 * @param src
	 * @param target
	 * @return list of String[]
	 */
	public  List<String[]> srcIntersectionTarget(List<String[]> src,List<String[]> target,String filter){
		List<String[]> listIntersection=new LinkedList<String[]>();
//		int maxsize=0;
//		final int noOfColoums=src.get(0).length;
//		String[] emptyStringArray= new String[noOfColoums];
//		if(src.size()>target.size()){
//			maxsize=src.size();
//			MainClass.log.info("Source records > target records and source's size is:"+maxsize);
//			for(int i=target.size();i<maxsize;i++){
//				target.add(emptyStringArray);//Add new null objects till maxsize; so that it becomes easy to compare
//			}
//		}
//		else{
//			maxsize=target.size();
//			MainClass.log.info("Target records > source records and target's size is:"+maxsize);
//			for(int i=src.size();i<maxsize;i++){
//				src.add(emptyStringArray);
//			}
//		}
		MainClass.Log.info("Creating 'Source=Target' Records");
		
		//LOGIC BY BRUTE FORCE APPROACH: but below is simple strategy
//		for(int i=0;i<src.size();i++){
//			for(int j=0;j<target.size();j++){
//				if(src.get(i).toString().equalsIgnoreCase( target.get(j).toString() ) ){
//				
//					try{
//						MainClass.log.info("EQUAL ELEMENT FOUND! for "+i+" and "+j);
//						listintersection.add(src.get(i));
//						
//					}
//					catch(NullPointerException e){
//						MainClass.log.info("Exception caught is:"+e+". There are no perfectly matched records");
//					}	
//				}//End If Block
//			}
//		}
		
		
		List<String> stringListSrc=new LinkedList<String>();
		for(int i=0;i<src.size();i++){
			stringListSrc.add(returnStringOfObjectRecord(src.get(i)));
		}
		
		List<String> stringListTarget=new LinkedList<String>();
		for(int i=0;i<target.size();i++){
			stringListTarget.add(returnStringOfObjectRecord(target.get(i)));
		}
		
//		src.retainAll(target);
		stringListSrc.retainAll(stringListTarget);//Main Operation of 'a' intersection 'b'
	
		//converts List<String> to List<String[]>
		for(int i=0;i<stringListSrc.size();i++){
			line=stringListSrc.get(i);
			String[] record = line.split(splitBy);
			listIntersection.add(record);
		}
		
		if(filter==null)
			return listIntersection;
		else{
			//---New Filter Implementation on 10th Jan 2015------>
			List<String[]> listIntersectionWithFilter=new LinkedList<String[]>();
			int filterIndices[]=returnArrayIndicesofFilter(filter);
			for(int i=0;i<listIntersection.size();i++){
				String str=returnStringofIndices(filterIndices, listIntersection.get(i));
				listIntersectionWithFilter.add(convertStringToStringArray(str));
			}
			return listIntersectionWithFilter;
		}
		
	}
	
	
	/**
	 *  Method which performs (a intersection b) operation; i.e all the elements in 'a' that are also present in 'b'
	 *  but on a particular given filter.Filter can be a particular column header or different column headers of DB Table
	 * @param src
	 * @param target
	 * @param filter
	 * @return
	 */
	private  List<String[]> srcIntersectionTarget_BeforeCompositeKey(List<String[]> src,List<String[]> target,String filter){
		List<String[]> listintersection=new LinkedList<String[]>();
		List<String> stringListSrc=new LinkedList<String>();
		List<String> stringListTarget=new LinkedList<String>();
		int arrayIndices[]=returnArrayIndicesofFilter(filter);
		
		for(int i=0;i<src.size();i++){
//			stringListSrc.add(src.get(i)[index]); 
//			stringListTarget.add(target.get(i)[index]);
			stringListSrc.add(returnStringofIndices(arrayIndices, src.get(i))); 
		}
		
		for(int i=0;i<target.size();i++){ 
			stringListTarget.add(returnStringofIndices(arrayIndices, target.get(i)));
		}
		
		stringListSrc.retainAll(stringListTarget);//Main Operation
		
		//converts List<String> to List<String[]>
		for(int i=0;i<stringListSrc.size();i++){
			line=stringListSrc.get(i);
			String[] studentRecord = line.split(splitBy);
			listintersection.add(studentRecord);
		}
		
		return listintersection;	
	}
	
	
	/**
	 * Method which perform (a-b) Operation on all the column headers of a DB; i.e all the elements in 'a' that are not there in 'b'
	 * Note:This method does not use composite key.There is another method which does the same operation on filter key.
	 * @param src
	 * @param target
	 * @return list of Objects
	 */
	public  List<String[]> srcMinusTarget(List<String[]> src,List<String[]> target,String filter){
	
		List<String[]> listSrcMinusTarget=new LinkedList<String[]>();
		List<String> stringListSrc=new LinkedList<String>();
		for(int i=0;i<src.size();i++){
			stringListSrc.add(returnStringOfObjectRecord(src.get(i)));
		}
		
		List<String> stringListTarget=new LinkedList<String>();
		for(int i=0;i<target.size();i++){
			stringListTarget.add(returnStringOfObjectRecord(target.get(i)));
		}
		
		stringListSrc.removeAll(stringListTarget);//Main Operation of a-b
		
		//converts List<String> to List<String[]>
		for(int i=0;i<stringListSrc.size();i++){
			line=stringListSrc.get(i);
			String[] record = line.split(splitBy);
			listSrcMinusTarget.add(record);
		}
		
		
		//return listSrcMinusTarget;
		if(filter==null)
			return listSrcMinusTarget;
		else{
			//---New Filter Implementation on 10th Jan 2015------>
			List<String[]> listSrcMinusTargetWithFilter=new LinkedList<String[]>();
			int filterIndices[]=returnArrayIndicesofFilter(filter);
			for(int i=0;i<listSrcMinusTarget.size();i++){
				String str=returnStringofIndices(filterIndices, listSrcMinusTarget.get(i));
				listSrcMinusTargetWithFilter.add(convertStringToStringArray(str));
			}
			return listSrcMinusTargetWithFilter;
		}
	}
	
	
	/**
	 * Method which perform (a-b) Operation; i.e all the elements in 'a' that are not there in 'b'
	 * but on a particular given filter.Filter can be a particular column header or different column headers of DB Table
	 * @param src
	 * @param target
	 * @param filter
	 * @return list of StudentObjects
	 */
	private  List<String[]> srcMinusTarget_BeforeCompositeKey(List<String[]> src,List<String[]> target,String filter){
	
		List<String[]> listSrcMinusTarget=new LinkedList<String[]>();
		List<String> stringListSrc=new LinkedList<String>();
		List<String> stringListTarget=new LinkedList<String>();
		int arrayIndices[]=returnArrayIndicesofFilter(filter);
		
		for(int i=0;i<src.size();i++){
			stringListSrc.add(returnStringofIndices(arrayIndices, src.get(i)));
		}
		
		for(int i=0;i<target.size();i++){
			stringListTarget.add(returnStringofIndices(arrayIndices, target.get(i)));
		}
		
		stringListSrc.removeAll(stringListTarget);//Main Operation of a-b
		
		for(int i=0;i<stringListSrc.size();i++){
			line=stringListSrc.get(i);
			String[] record = line.split(splitBy);
			listSrcMinusTarget.add(record);
		}
		return listSrcMinusTarget;	
	}
//-----------------------------------------------------------------------------------------
	
	/**
	 * Method which perform (a-b) Operation; i.e all the elements in 'a' that are not there in 'b'
	 * but on a particular given composite key.'compositekey' can be a particular column header or different column headers of
	 * DB Table depending on how DB schema is implemented.'filter' is the column header which you want to show on UI(For eg:Excel sheet)
	 * If filter is null whole column headers are taken, else filter headers are taken.
	 * @param src
	 * @param target
	 * @param filter
	 * @return list of Objects
	 */
	public  List<String[]> createSrcMinusTarget(List<String[]> src,List<String[]> target,String compositekey,String filter){
		
		List<String[]> listSrcMinusTarget=new LinkedList<String[]>();
		
		MainClass.Log.info("Creating Source Minus Target Records");
		
		List<String> stringListSrc=new LinkedList<String>();
		List<String> stringListTarget=new LinkedList<String>();
		int arrayIndices[]=returnArrayIndicesofFilter(compositekey);
		
		for(int i=0;i<src.size();i++){
			stringListSrc.add(returnStringofIndices(arrayIndices, src.get(i)));
		}
		
		for(int i=0;i<target.size();i++){
			stringListTarget.add(returnStringofIndices(arrayIndices, target.get(i)));
		}
		
		stringListSrc.removeAll(stringListTarget);//Main Operation of a-b
		
		for(int i=0;i<stringListSrc.size();i++){//this list is relatively small in size
			for(int j=0;j<src.size();j++){ //this list is relatively big in size
				if(	(stringListSrc.get(i) ).equalsIgnoreCase(returnStringofIndices(arrayIndices, src.get(j)))){
					listSrcMinusTarget.add(src.get(j));
				}
			}	
		}//End outer for loop
		
		
		if(filter==null)
			return listSrcMinusTarget;
		else{
			//---New Filter Implementation on 10th Jan 2015------>
			List<String[]> listSrcMinusTargetWithFilter=new LinkedList<String[]>();
			int filterIndices[]=returnArrayIndicesofFilter(filter);
			for(int i=0;i<listSrcMinusTarget.size();i++){
				String str=returnStringofIndices(filterIndices, listSrcMinusTarget.get(i));
				listSrcMinusTargetWithFilter.add(convertStringToStringArray(str));
			}
			return listSrcMinusTargetWithFilter;
		}
	}//End method createSrcMinusTarget()
	
	
	/**
	 * This method returns true if duplicates are found in the given data i.e List<String[]>.
	 * If duplicates are found it also prints the index on which the duplicate is found!
	 * @param source
	 * @param compositeKey
	 * @return true if duplicates are found, else false.
	 */
	public  boolean checkDuplicateTupleOccuranceonSourceorTarget_v1(List<String[]> source,String compositeKey){
		Set<String> stringSetSource=new LinkedHashSet<String>();//Ensures Insertion order; the other option HashSet has no order
//		Set<String> stringSetSource1=new HashSet<String>();//
		int arrayIndices[]=returnArrayIndicesofFilter(compositeKey);
		boolean targetDuplicate=false;
		
		for(int i=0;i<source.size();i++){
			if(stringSetSource.add(returnStringofIndices(arrayIndices, source.get(i)))==false){
				MainClass.Log.info("Duplicates on composite key are:"+  returnStringofIndices(arrayIndices, source.get(i))+" For Row Number:"+i );
				targetDuplicate=true;
				break;
			}
		}
		
		return targetDuplicate;	
	}
	
	/**
	 * This method returns true if duplicates are found in the given data i.e List<String[]>.
	 * If duplicates are found it also prints the row numbers on which these are found!
	 * @param source
	 * @param compositeKey
	 * @return true if duplicates are found, else false.
	 */
	public  boolean checkDuplicateTupleOccuranceonSourceorTarget(List<String[]> source,String compositeKey){
		//Set<String> stringSetSource=new LinkedHashSet<String>();//Ensures Insertion order; the other option HashSet has no order
		int arrayIndices[]=returnArrayIndicesofFilter(compositeKey);
		boolean targetDuplicate=false;
		
		for(int i=0;i<source.size();i++){
			for(int j=0;j<source.size();j++){
				if(i==j)
					continue;
				if(returnStringofIndices(arrayIndices, source.get(i)).equalsIgnoreCase(returnStringofIndices(arrayIndices, source.get(j)))){
					MainClass.Log.info("Duplicates on composite key are:"+  returnStringofIndices(arrayIndices, source.get(j))+" For Row Number:"+(i+1)+"and "+(j+1) );
					targetDuplicate=true;
					break;
				}
			}
		}
		
		return targetDuplicate;	
	}
	
	
	
	/**
	 * This method returns the Source Or Target(list) of the 'Source!=Target' Sheet in Excel.
	 * To get Source of 'Source!=Target' sheet first pass source objects and than Target objects. To get Target 
	 * of 'Source!=Target' sheet reverse the order. 
	 * If filter is null whole column headers are taken, else filter headers are taken.
	 * @param src
	 * @param target
	 * @param compositekey
	 * @return List of String Array which holds the 'Source' or 'Target' Data of 'Source!=Target' sheet.
	 */
	public  List<String[]> createSrcOrTargetOfDiff(List<String[]> src,List<String[]> target,String compositekey,String filter){
		List<String[]> listOfSrcMinusTargetAll= srcMinusTarget(src,target,filter);//This method should also have filter
		List<String[]> listOfSrcMinusTargetOnCompositeKey=createSrcMinusTarget(src,target,compositekey,filter);
		List<String[]> listOfSrcOfDiff=srcMinusTarget(listOfSrcMinusTargetAll,listOfSrcMinusTargetOnCompositeKey,null);//Pass null here,not filter!
		
		return listOfSrcOfDiff;
		
	}
	
	
	/**
	 * This is implementation v1 where formatting of only the required data is being done
	 * There is a issue with printing the number of "OKs" here with this logic.
	 * @param src
	 * @param target
	 * @param calculateDevianceOn
	 * @param deviancePercentages
	 * @return
	 */
	public  List<String[]> createDiff_v1(List<String[]> src,List<String[]> target,String calculateDevianceOn,String deviancePercentages){
		List<String[]> listOfDiff=new LinkedList<String[]>();
		List<String[]> listOfSrc=new LinkedList<String[]>();
		List<String[]> listOfTarget=new LinkedList<String[]>();
		final int noOfColoums=src.get(0).length;
		String diffRecord="";
		String constantdiffRecord="";
		
		List<String> stringListSrc=new LinkedList<String>();
		List<String> stringListTarget=new LinkedList<String>();
		int arrayIndices[]=returnArrayIndicesofFilter(calculateDevianceOn);
		final int noOfDeviances=arrayIndices.length;
		
		int noOfOks=noOfColoums-arrayIndices.length;
		for(int i=0;i<noOfOks;i++){//This implementation is wrong; wherever there is no calculateDevianceOn filter, put an "OK"
			diffRecord+="OK"+ConfigFileReader.delimiter;
			constantdiffRecord+="OK"+ConfigFileReader.delimiter;
		}
		MainClass.Log.info(diffRecord);
		
		String[] devianceRecord=deviancePercentages.split(splitBy);
		
		for(int i=0;i<src.size();i++){//Here src.size() has to be equal to target.size().Pass appropriate parameters for src and target
			stringListSrc.add(returnStringofIndices(arrayIndices, src.get(i)));
			stringListTarget.add(returnStringofIndices(arrayIndices, target.get(i)));
		}
		
		//Convert List<String> to List<String[]>
		for(int i=0;i<stringListSrc.size();i++){
			line=stringListSrc.get(i);
			String[] record = line.split(splitBy);
			listOfSrc.add(record);
		}
		
		for(int i=0;i<stringListTarget.size();i++){
			line=stringListTarget.get(i);
			String[] record = line.split(splitBy);
			listOfTarget.add(record);
		}
		//-----------------------Formatting and trimming of data sets is over; Now calculation------------------------>
		for(int i=0;i<listOfSrc.size();i++){
			for(int j=0;j<noOfDeviances;j++){
//						if(src.get(i)[j].equalsIgnoreCase(target.get(i)[j]))
				try{
				 double diff=Double.parseDouble(listOfSrc.get(i)[j])-Double.parseDouble(listOfTarget.get(i)[j]);
				 diffRecord+=diff+" d"+ConfigFileReader.delimiter;
				 String deviance=calculateDeviance(Double.parseDouble(listOfSrc.get(i)[j]),Double.parseDouble(listOfTarget.get(i)[j]));
				 diffRecord+=deviance+ConfigFileReader.delimiter;
				 String result=checkIfWithinDevianceRange(Double.parseDouble(deviance),Double.parseDouble(devianceRecord[j]));
				 diffRecord+=result+ConfigFileReader.delimiter;
				 
				}
				catch(NumberFormatException e){
					MainClass.Log.severe("Exception caught is: "+e.toString());
					MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
				}
				catch(ArrayIndexOutOfBoundsException e){
					MainClass.Log.severe("Exception caught is: "+e.toString());
					MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
					MainClass.Log.severe("Num of elements in 'calculate deviance on' and 'deviancePercentages' has to be equal");
					throw e;//Catch and throw back the same exception as we want the tool to quit
				}
			}//End of 'J'
			String[] singleRowRecord=convertStringToStringArray(diffRecord);
			listOfDiff.add(singleRowRecord);
			diffRecord=constantdiffRecord;
		}//End for of 'i';
		return listOfDiff;
		
	}
	
	/**
	 * Generates data for the difference Table in 'Source!=Target' sheet in the excel.
	 * If filter is null whole column headers are taken, else filter headers are taken.
	 * @param src
	 * @param target
	 * @param calculateDevianceOn
	 * @param deviancePercentages
	 * @return List of String Array which holds the difference Data.
	 */
	public  List<String[]> createDiff(List<String[]> src,List<String[]> target,String calculateDevianceOn,String devianceValues,String filter){
		List<String[]> listOfDiff=new LinkedList<String[]>();
		
		final int noOfColoums=src.get(0).length;
		String diffRecord="";
		String constantdiffRecord="";
		String headerRecord[]={};
		int arrayIndices[]={};
		if(filter==null){
			headerRecord=readCsvFileHeader(ConfigFileReader.sourceFile);
			arrayIndices=returnArrayIndicesofFilter(calculateDevianceOn);
		}
		else{
			headerRecord=filter.split(splitBy);
			arrayIndices=returnArrayIndicesofFilterForHeader(calculateDevianceOn,filter);//Very Important Step!
		}
		String[] devianceRecord=devianceValues.split(splitBy);//rename to devianceValuesArray
		final int noOfDeviances=devianceRecord.length;
		int countofdeviances=0;//After adding returnIndexOfDevianceRecord() this step is useless
		
		double diff=0.0;
		String deviance="";
		String result="";
		int recordIndex=0;
		
		//-----------------------
		String perOrAbs[]=ConfigFileReader.percentageOrAbsolute.split(splitBy);//Later do by taking local string
		
		if(src.size()==target.size()){
			MainClass.Log.info("Dimensions(no of rows) of input List<String[]> are equal. Size is:"+src.size()+" createDiff() should work fine.");
		}
		else{
			MainClass.Log.warning("Dimensions (no of rows) of input List<String[]> are NOT equal.Size is:"+src.size()+" createDiff() will not work fine!");
		}
		
		for(int i=0;i<src.size();i++){
			for(int j=0;j<noOfColoums;j++){	
					if(checkIfItsCalculateDevianceColumn(j,arrayIndices)==true){
//						MainClass.Log.info("J is a deviancecolumn; 'i' is:"+i+" 'j' is:"+j);
						try{
							 diff=Double.parseDouble(src.get(i)[j])-Double.parseDouble(target.get(i)[j]);
							 if(diff==0.0){
								 diffRecord+=diff+"e"+ConfigFileReader.delimiter;//'e' symbolizes equal and helps in front end to have green cell color
							 }
							 else{
								 diffRecord+=diff+"d"+ConfigFileReader.delimiter;//'d' symbolizes difference and helps in front end to have red cell color
							 }
//							 deviance=calculateDeviance(Double.parseDouble(src.get(i)[j]),Double.parseDouble(target.get(i)[j]));
//							 diffRecord+=deviance+ConfigFileReader.delimiter;
							 recordIndex=returnIndexOfDevianceRecord(headerRecord, j, calculateDevianceOn);//Mapping logic on Jan19, 2015
							 if(perOrAbs[recordIndex].equalsIgnoreCase("percentage")){
								 deviance=calculateDeviance(Double.parseDouble(src.get(i)[j]),Double.parseDouble(target.get(i)[j]));
								 diffRecord+=deviance+"v"+ConfigFileReader.delimiter;//'v' symbolizes difference and helps in front end to have red cell color
								 result=checkIfWithinDevianceRange(Double.parseDouble(deviance),Double.parseDouble(devianceRecord[recordIndex]));
							 }
							 else if(perOrAbs[recordIndex].equalsIgnoreCase("absolute")){
								 result=checkIfWithinDevianceRange(diff,Double.parseDouble(devianceRecord[recordIndex]));
							 }
							 else{
								 MainClass.Log.severe("Error in ConfigFile. Check if percentage, or absolute are properly given and spelled");
									//Throw e;
							 }
							 diffRecord+=result+ConfigFileReader.delimiter;
							 
							}
							catch(NumberFormatException e){
								MainClass.Log.severe("Exception caught is: "+e.toString());
								MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
								throw e;//Catch and throw back the same exception as we want the tool to quit
							}
							catch(ArrayIndexOutOfBoundsException e){
								MainClass.Log.severe("Exception caught is: "+e.toString());
								MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
								MainClass.Log.severe("Num of elements in 'calculate deviance on' and 'deviancePercentages' has to be equal");
								throw e;//Catch and throw back the same exception as we want the tool to quit
							}
								++countofdeviances;//After adding returnIndexOfDevianceRecord() this step is useless
								
						}//End if
					else{
						//diffRecord+="OK"+ConfigFileReader.delimiter;//earlier code
							if(src.get(i)[j].equalsIgnoreCase(target.get(i)[j])){
								diffRecord+="OK"+ConfigFileReader.delimiter;
							}
							else{
								diffRecord+="NOT OK"+ConfigFileReader.delimiter;
							}
						}//End else	
	
			//---------------------------------------	
					
			}//End for of 'j'
			countofdeviances=0;////This is an important step here.Setting it back to '0'
			String[] singleRowRecord=convertStringToStringArray(diffRecord);
			listOfDiff.add(singleRowRecord);
			diffRecord=constantdiffRecord;
		}//End for of 'i'
			
		return listOfDiff;
		
		
	}//	End createDiff() method
	
	
	/**
	 * Easy and Lazy way of implementing the '&' logic of 'calculateDevianceOn' headers
	 * in 'source!=target' sheet.
	 * @param diff
	 * @return number of records matching Within deviance.
	 */
	public int noOfRecordMatchingWithinDeviance(List<String[]> diff){
		
		int countOfMatches=0;
		for(int i=0;i<diff.size();i++){
			if(convertStringArrayToString(diff.get(i)).contains("No")){
				//do nothing
			}
			else{
				countOfMatches++;
			}	
		}
		return countOfMatches;
	}
	
	/**
	 * Easy and Lazy way of implementing the '&' logic of 'calculateDevianceOn' headers
	 * in 'source!=target' sheet.
	 * @param diff
	 * @return number of records matching outside deviance.
	 */
	public int noOfRecordMatchingOutsideDeviance(List<String[]> diff){
		
		int countOfMatches=0;
		for(int i=0;i<diff.size();i++){
			if(convertStringArrayToString(diff.get(i)).contains("No")){
				
				countOfMatches++;
			}
			else{
				//do nothing
			}	
		}
		return countOfMatches;
	}
	
	/**
	 * Under implementation! This method is just kept here in this class for logic purposes!
	 * Thus its purposely private.
	 * @param src
	 * @param target
	 * @param calculateDevianceOn
	 * @param deviancePercentages
	 * @return List of String Array which holds the difference Data.
	 */
	private  List<String[]> createDiff_WithFilter(List<String[]> src,List<String[]> target,String calculateDevianceOn,String deviancePercentages,String filter){
		List<String[]> listOfDiff=new LinkedList<String[]>();
		
		final int noOfColoums=src.get(0).length;
		String diffRecord="";
		String constantdiffRecord="";
		int arrayIndices[]=returnArrayIndicesofFilterForHeader(calculateDevianceOn,filter);//Very Important Step!
		String[] devianceRecord=deviancePercentages.split(splitBy);
		final int noOfDeviances=devianceRecord.length;
		int countofdeviances=0;
		
		double diff=0.0;
		String deviance="";
		String result="";
		
		if(src.size()==target.size()){
			MainClass.Log.info("Dimensions(no of rows) of input List<String[]> are equal. Size is:"+src.size()+" createDiff() should work fine.");
		}
		else{
			MainClass.Log.info("Dimensions (no of rows) of input List<String[]> are NOT equal.Size is:"+src.size()+" createDiff() will not work fine!");
		}
		
		for(int i=0;i<src.size();i++){
			for(int j=0;j<noOfColoums;j++){	
				
//				if(checkIfItsCalculateDevianceColumn(j,filterIndices)){
					
					if(checkIfItsCalculateDevianceColumn(j,arrayIndices)==true){
//						Log.info("J is a deviancecolumn; 'i' is:"+i+"'j' is:"+j);
						try{
							 diff=Double.parseDouble(src.get(i)[j])-Double.parseDouble(target.get(i)[j]);
							 diffRecord+=diff+" d"+ConfigFileReader.delimiter;
							 deviance=calculateDeviance(Double.parseDouble(src.get(i)[j]),Double.parseDouble(target.get(i)[j]));
							 diffRecord+=deviance+ConfigFileReader.delimiter;
							 result=checkIfWithinDevianceRange(Double.parseDouble(deviance),Double.parseDouble(devianceRecord[countofdeviances]));
							 diffRecord+=result+ConfigFileReader.delimiter;
							 
							}
							catch(NumberFormatException e){
								MainClass.Log.severe("Exception caught is: "+e.toString());
								MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
								throw e;//Catch and throw back the same exception as we want the tool to quit
							}
							catch(ArrayIndexOutOfBoundsException e){
								MainClass.Log.severe("Exception caught is: "+e.toString());
								MainClass.Log.severe("Illegal parameter passed for 'calculate deviance on'or 'deviancePercentages' in config file");
								MainClass.Log.severe("Num of elements in 'calculate deviance on' and 'deviancePercentages' has to be equal");
								throw e;//Catch and throw back the same exception as we want the tool to quit
							}
								++countofdeviances;//This is an important step here
								
						}//End if
					else{
							diffRecord+="OK"+ConfigFileReader.delimiter;
						}//End else	
				
				
			//---------------------------------------	
					
			}//End for of 'j'
			countofdeviances=0;////This is an important step here.Setting it back to '0'
			String[] singleRowRecord=convertStringToStringArray(diffRecord);
			listOfDiff.add(singleRowRecord);
			diffRecord=constantdiffRecord;
		}//End for of 'i'
			
		return listOfDiff;
		
		//-----------------------------------------
		
//		if(filter==null)
//			return listOfDiff;
//		else{
//			String str="";
//			//---New Filter Implementation on 10th Jan 2015------>
//			List<String[]> listOfDiffWithFilter=new LinkedList<String[]>();
//			int filterIndices[]=returnArrayIndicesofFilter(filter);
//			for(int i=0;i<listOfDiff.size();i++){
//				for(int j=0;j<listOfDiff.get(0).length;j++){	
//					if(checkIfItsCalculateDevianceColumn(j,filterIndices)){
//						if(checkIfItsCalculateDevianceColumn(j,arrayIndices)){
//							 str+=listOfDiff.get(i)[j]+ConfigFileReader.delimiter;
//							 continue;
//						}
//						else{
//							str+="OK"+ConfigFileReader.delimiter;
//						}
//					}
//					
////				String str1=returnStringofIndices(filterIndices, listOfDiff.get(i));
////				listOfDiffWithFilter.add(convertStringToStringArray(str1));
//				
//				}//End 'j'
//				String[] singleRowRecord=convertStringToStringArray(str);
//				listOfDiff.add(singleRowRecord);
//				str=constantdiffRecord;
//			}
//			return listOfDiffWithFilter;
//		}
		
		//-------------------------------------
		
	}//	End createDiff() method
	
	
	
	/**
	 * Method which performs (a-b) union (b-a) operation; or (aUb)-(a intersection b)
	 * @param src
	 * @param target
	 * @return list of StudentObjects
	 */
	public  List<String[]> mismatchedList(List<String[]> src,List<String[]> target,String filter){
		List<String[]> listMismatched=new LinkedList<String[]>();
		List<String[]> listSrcMinusTarget=srcMinusTarget(src,target,filter);
		List<String[]> listTargetMinusSrc=srcMinusTarget(target,src,filter);//Notice swap of parameters
		
		List<String> stringListSrcMinusTarget=new LinkedList<String>();
		for(int i=0;i<listSrcMinusTarget.size();i++){
			stringListSrcMinusTarget.add(returnStringOfObjectRecord(listSrcMinusTarget.get(i)));
		}
		
		List<String> stringListTargetMinusSource=new LinkedList<String>();
		for(int i=0;i<listTargetMinusSrc.size();i++){
			stringListTargetMinusSource.add(returnStringOfObjectRecord(listTargetMinusSrc.get(i)));
		}
		
		stringListSrcMinusTarget.addAll(stringListTargetMinusSource);//Main Operation of 'a' union 'b'
		
		for(int i=0;i<stringListSrcMinusTarget.size();i++){
			line=stringListSrcMinusTarget.get(i);
			String[] record = line.split(splitBy);
			listMismatched.add(record);
		}
		return listMismatched;
	}
	
	/**
	 * 
	 * Method which returns the number of records in a List
	 * @param list
	 * @return number of records in the given List as String(not int)
	 */
	public String getSizeOfList(List<String[]> list){
		return ""+list.size(); //or Interger.toString(list.size());
	}
	
	public List<String[]> createDataForColumnMappedCsvFileOfTarget(Map<String, String> columnMapping,List<String[]> target,String[] sourceHeader,String[] origTargetHeader ){
		//Check if concatValueArray.length and sourceHeader.length are equal;else exception
		System.out.println("Inside createDataForColumnMappedCsvFileOfTarget()");
		//Step 1-->
		Map<String, Integer> origTargetHeaderArrayMap = new LinkedHashMap<String, Integer>();
		System.out.println(origTargetHeaderArrayMap);
		for(int i=0;i<origTargetHeader.length;i++){
			System.out.println("origTargetHeader["+i+"]"+origTargetHeader[i]);
			origTargetHeaderArrayMap.put(origTargetHeader[i],i);
		}
		System.out.println(origTargetHeaderArrayMap);
		
		//Step2-->
		String concatValue="";
		Set<String> keySet=columnMapping.keySet();
		for(String key : keySet){
			concatValue+=key+ConfigFileReader.delimiter;
		}
		System.out.println("ConcatValue again of keySet is:"+concatValue);
		String[] concatValueArray=convertStringToStringArray(concatValue);
		
		 System.out.println("The contents of concatValueArray(keys of the map) are:");
		 for(int i=0;i<concatValueArray.length;i++){
			 System.out.println(i+":"+concatValueArray[i]);
		 }
		
		List<String[]> listOfMappedTarget=new LinkedList<String[]>();//Needed Output
//		int arrayIndices[]=returnArrayIndicesofFilter(concatValue);
		String mappedRecord="";
		String constantmappedRecord="";
		
		int value=0;
		for(int i=0;i<target.size();i++){
			
			for(int j=0;j<sourceHeader.length;j++){	
				for(int k=0;k<concatValueArray.length;k++){
					if(sourceHeader[j].equalsIgnoreCase(concatValueArray[k])){
						String mapValue=columnMapping.get(concatValueArray[k]);
						if(origTargetHeaderArrayMap.containsKey(mapValue)){
//							System.out.println("MapKey:"+mapValue+" Is present in OrigTargetHeaderMap.Continuing with tool...");
						}
						else{
							System.out.println("MapKey:"+mapValue+" Is NOT present in OrigTargetHeaderMap");
							columnMappingFileCorruptException.setMessage(exceptionCMFCE1);
							MainClass.Log.severe(columnMappingFileCorruptException.getMessage());
							throw columnMappingFileCorruptException;
						}
						value=origTargetHeaderArrayMap.get(mapValue);
						mappedRecord+=target.get(i)[value]+ConfigFileReader.delimiter;
					}
				}	
			}//End 'j' for loop
			String[] singleRowRecord=convertStringToStringArray(mappedRecord);
			listOfMappedTarget.add(singleRowRecord);
			mappedRecord=constantmappedRecord;
		}//End 'i' for loop	
		
		listOfMappedTarget.add(0, sourceHeader);
		
		return listOfMappedTarget;
	}
	
	/**
	 * Method used to replace target List<String[]> with mapped values mentioned in Mapping File
	 * @param columnValueMapping
	 * @param target
	 * @param targetHeader
	 * @return
	 */
	public List<String[]> createDataForValueMappedCsvFileOfTarget(Map<String, String> columnValueMapping,List<String[]> target,String[] targetHeader){//String[] sourceHeader,String[] origTargetHeader ){
		
//		String concatValue="";
//		Set<String> keySet=columnValueMapping.keySet();
//		for(String key : keySet){
//			concatValue+=key+ConfigFileReader.delimiter;
//		}
//		System.out.println("ConcatValue again of keySet is:"+concatValue);
//		String[] concatValueArray=convertStringToStringArray(concatValue);
		
		Set<String> keySet=columnValueMapping.keySet();
		String value="";
		for(String key : keySet){
			for(int i=0;i<target.size();i++){
				for(int j=0;j<target.get(i).length;j++){
					if(target.get(i)[j].equalsIgnoreCase(key)){
						value=columnValueMapping.get(key);
						target.get(i)[j]=value;
					}
				}
			}
		}//End for of 'key'
		
		target.add(0, targetHeader);
		
		return target;
	}
	
	
	public List<String[]> createDataForReverseValueMappedFileOfTarget(Map<String, String> columnValueMapping,List<String[]> target,String[] targetHeader){//String[] sourceHeader,String[] origTargetHeader ){
		
//		String concatValue="";
//		Set<String> keySet=columnValueMapping.keySet();
//		for(String key : keySet){
//			concatValue+=key+ConfigFileReader.delimiter;
//		}
//		System.out.println("ConcatValue again of keySet is:"+concatValue);
//		String[] concatValueArray=convertStringToStringArray(concatValue);
		
		int columnNameIndex=0;
		Set<String> keySet=columnValueMapping.keySet();
		String value="";
		for(String key : keySet){
			
			if(key.equalsIgnoreCase("columnName")){
				String columnName=columnValueMapping.get(key);
				System.out.println("The columnName in valueMappingFile is: "+columnName);
				if(ConfigFileReader.filter!=null)
					columnNameIndex=returnIndexofFilterForHeader(columnName,ConfigFileReader.filter);
				else
					columnNameIndex=returnIndexofFilter(columnName);
				
				System.out.println("The index of columnName is: "+columnNameIndex);

			}
			
			for(int i=0;i<target.size();i++){
				for(int j=0;j<target.get(i).length;j++){//No of columns
//					System.out.println("target.get(i)[j] "+target.get(i)[j]);
					if(target.get(i)[j].equalsIgnoreCase(key) & j==columnNameIndex ){
						value=columnValueMapping.get(key);
						target.get(i)[j]=value;
					}
				}
			}
		}//End for of 'key'
		
		target.add(0, targetHeader);
		
		return target;
	}
	
	//Private methods--------------------------------------------------------------------------------------------->
	
//	private boolean checkIfColumnIsFilterIndex(int columnNo,Map<String, String> columnValueMapping,String key){
//		int columnNameIndex=0;
//		if(key.equalsIgnoreCase("columnName")){
//			String columnName=columnValueMapping.get(key);
////			returnArrayIndicesofFilter(columnName);
//			System.out.println("*******The columnName in valueMappingFile is: "+columnName);
//			columnNameIndex=returnIndexofFilter(columnName);
//			System.out.println("******The index of columnName is: "+columnNameIndex);
//		}
//		
//		return columnNo==columnNameIndex;
//	}
	
	private String returnStringOfObjectRecord(String[] sourceObjectRecord){
		String row="";
		for(int i=0;i<sourceObjectRecord.length;i++){
			row+=sourceObjectRecord[i]+ConfigFileReader.delimiter;	
		}
//		MainClass.Log.info(row);
		return row;
	}
	
	private String[] readCsvFileHeader(String pathtofile){
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ConfigFileReader.delimiter;
		String[] studentHeader={};
		
		try {
			br = new BufferedReader(new FileReader(pathtofile));
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
	
	/*
	public List<String> tokenizedStringList(List<String> listoffullrows,String filter){
		List<String> tokenList=null;
		String line="";
		List<String> singleTokenList=new LinkedList<String>();
		for(int i=0;i<listoffullrows.size();i++){
			line=listoffullrows.get(i);
			StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
			 while (stringTokenizer.hasMoreElements()) {
				 
				 stringTokenizer.nextElement().toString();
			 }
		}
		
		return tokenList;
	}
	*/
	
	
	
	
	private int returnIndexofFilter(String filter){
		int i=0,index=0;
		boolean flag=false;
		String studentHeader[]=readCsvFileHeader(ConfigFileReader.sourceFile);
		MainClass.Log.info("Printing studentHeader[]; Stopping when 'filter' is found------>");
		for(i=0;i<studentHeader.length;i++){
			MainClass.Log.info(i+":"+studentHeader[i]);
			if(filter.equalsIgnoreCase(studentHeader[i])){
				index=i;
				flag=true;
				break;
			}
		}
		if(flag==false)
			MainClass.Log.warning("Illegal parameter passed for filter. Please check the config file.Choosing filter as 'first' column header");
		return index;
	}
	
	private int returnIndexofFilterForHeader(String filter,String header){
		int i=0,index=0;
		boolean flag=false;
//		String studentHeader[]=readCsvFileHeader(ConfigFileReader.sourceFile);
		String headerRecord[]=header.split(splitBy);
		MainClass.Log.info("Printing studentHeader[]; Stopping when 'filter' is found------>");
		for(i=0;i<headerRecord.length;i++){
			MainClass.Log.info(i+":"+headerRecord[i]);
			if(filter.equalsIgnoreCase(headerRecord[i])){
				index=i;
				flag=true;
				break;
			}
		}
		if(flag==false)
			MainClass.Log.warning("Illegal parameter passed for filter. Please check the config file.Choosing filter as 'first' column header");
		return index;
	}

	
	/**
	 * This method returns the indices of 'filter string' w.r.t the main header of the source file.
	 * To find indices of filter w.r.t any generic(or modified header) use the other method named
	 * returnArrayIndicesofFilterForHeader()
	 * @param filter
	 * @return
	 */
	private int[] returnArrayIndicesofFilter(String filter){
		int i=0,index=0,count=0;
		
		String[] filterRecord = filter.split(splitBy);
		final int noOfColoums=filterRecord.length;
		int arrayIndices[]=new int[noOfColoums];
		int arrayIndicesUnmatched[]=new int[noOfColoums];
		String header[]=readCsvFileHeader(ConfigFileReader.sourceFile);
//		MainClass.Log.info("Printing studentHeader[]; Stopping when 'filter' is found------>");
		for(i=0;i<filterRecord.length;i++){
//			MainClass.Log.info(i+":"+studentHeader[i]);
			for(int j=0;j<header.length;j++){
				if(filterRecord[i].equalsIgnoreCase(header[j])){
					arrayIndices[index]=j;//Put 'j' Inside the array not 'i'
					index++;
					break;
				}
				else{
					arrayIndicesUnmatched[count]=j;
				}
			}
		}
		//TODO:
		//Figure out a way to say that	"Illegal parameter passed for filter"
//		if(arrayIndicesUnmatched.length<noOfColoums)
//			Log.warning("Illegal parameter passed for filter. Please check the config file.Choosing filter as 'first' column header");
		return arrayIndices;
	}
	
	/**
	 * This method returns the indices of 'filter string' w.r.t the  'header string'
	 * @param filter
	 * @param header
	 * @return
	 */
	private int[] returnArrayIndicesofFilterForHeader(String filter,String header){
		int i=0,index=0,count=0;
		
		String[] filterRecord = filter.split(splitBy);
		final int noOfColoums=filterRecord.length;
		int arrayIndices[]=new int[noOfColoums];
		int arrayIndicesUnmatched[]=new int[noOfColoums];
		String headerRecord[]=header.split(splitBy);

		for(i=0;i<filterRecord.length;i++){
			for(int j=0;j<headerRecord.length;j++){
				if(filterRecord[i].equalsIgnoreCase(headerRecord[j])){
					arrayIndices[index]=j;//Put 'j' Inside the array not 'i'
					index++;
					break;
				}
				else{
					arrayIndicesUnmatched[count]=j;
				}
			}
		}//End outer for loop
		
		return arrayIndices;
	}
	
	
	
	private String returnStringofIndices(int arrayIndices[],String[] list){
		String filteredString="";
			for(int j=0;j<arrayIndices.length;j++){
				filteredString+=list[arrayIndices[j]]+ConfigFileReader.delimiter;
			}
		return filteredString;
	}
	
	private String calculateDeviance(double source, double target){
		double deviance=0.0;
		deviance=((source-target)/source)*100;//deviance formula
		deviance=(deviance>=0)? deviance: (-deviance);//absolute value of deviance
		String strDeviance= String.format( "%.9f", deviance );
		return strDeviance;
	}
	
	private String checkIfWithinDevianceRange(double deviance, double deviancerange){
		deviance=(deviance>=0)? deviance: (-deviance);//absolute value of deviance on jan22
		String result=null;
		if(deviance<=deviancerange){
			result="Yes";
		}
		else{
			result="No";
		}
		return result;
	}
	
	/**
	 * This is a helper method for createDiff() method. This returns the appropriate index of the 'devianceValues' string
	 * to compare whether the deviance calculated for each column falls within the specified range(which is mentioned in 
	 * 'devianceValues' string) or no.
	 * @param headerRecord
	 * @param columnno
	 * @param calculateDevianceOn
	 * @return
	 */
	private int returnIndexOfDevianceRecord(String headerRecord[],int columnno,String calculateDevianceOn){
		String calculateDevianceOnRecord[]=calculateDevianceOn.split(splitBy);
		int index=0;
			for(int j=0;j<calculateDevianceOnRecord.length;j++){
				if(headerRecord[columnno].equalsIgnoreCase(calculateDevianceOnRecord[j])){
					index=j;
					break;
				}
			}
		return index;
	}
	
	public String[] convertStringToStringArray(String str){
		String record[]=str.split(splitBy);
		return record;
	}
	
	public String convertStringArrayToString(String[] record){
		String str="";
		for(int i=0;i<record.length;i++){
			str+=record[i]+ConfigFileReader.delimiter;
		}
		return str;
	}
	
	private boolean checkIfItsCalculateDevianceColumn(int columnno,int[] arrayOfIndicesOfCalculateDeviance){
		boolean check=false;
		for(int i=0;i<arrayOfIndicesOfCalculateDeviance.length;i++){
			if(columnno==arrayOfIndicesOfCalculateDeviance[i]){
				check=true;
				break;
			}
		}
		return check;
	}
	
	private int returnIndexOfDevianceColumn(int columnno,int[] arrayOfIndicesOfCalculateDeviance){
		int index=0;
		for(int i=0;i<arrayOfIndicesOfCalculateDeviance.length;i++){
			if(columnno==arrayOfIndicesOfCalculateDeviance[i]){
				index=arrayOfIndicesOfCalculateDeviance[i];
				break;
			}
		}
		return index;
	}
}
