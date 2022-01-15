package com.company.recon.exceptions;

//RuntimeException is unchecked exception. It does not have catch or declare rule
public class ConfigFileCorruptException extends FileCorruptException {

	public ConfigFileCorruptException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


}
