package com.company.recon.writer;


import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Override
	public String format(LogRecord record) {
		// TODO Auto-generated method stub
		 return record.getLevel()+"|"
		 +record.getSourceClassName()+"|"
//         +record.getSourceMethodName()+"|"
         +record.getMessage()+LINE_SEPARATOR; //Use line.separator instead of "\n". That will use the platform dependant line separator - \n for Unix, \r\n for Windows, etc. 
		 //record.getMillis()
		 // +new Date()+"::"
	}

	
}
