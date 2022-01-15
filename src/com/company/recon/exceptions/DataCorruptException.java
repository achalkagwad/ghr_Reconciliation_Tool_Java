package com.company.recon.exceptions;

//RuntimeException is unchecked exception. It does not have catch or declare rule.
public class DataCorruptException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	
	public DataCorruptException(String message){
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
