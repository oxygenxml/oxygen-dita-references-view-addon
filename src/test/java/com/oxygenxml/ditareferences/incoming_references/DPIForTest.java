package com.oxygenxml.ditareferences.incoming_references;

import ro.sync.document.DocumentPositionedInfo;

/**
 * Adapter for ro.sync.document.DocumentPositionedInfo in test cases.
 * 
 * @author alex_smarandache
 * 
 */
public class DPIForTest extends DocumentPositionedInfo {

	/**
	 * The systemID.
	 */
	private String systemID;
	
	/**
	 * The message.
	 */
	private String message;
	
	/**
	 * The line.
	 */
	private int line = 50;
	
	/**
	 * The column.
	 */
	private int column = 100;
	
	
	/**
	 * Constructor.
	 */
	public DPIForTest() {
		super(0);
	}
	
	
	/**
	 * Set value for system id.
	 */
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
	
	
	/**
	 * Set value for message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	@Override
	public String getSystemID() {
		return systemID;
	}
	
	
	@Override
	public int getLine() {
		return line;
	}

	
	@Override
	public int getColumn() {
		return column;
	}
	
	
	@Override
	public String getMessage() {
		return message;
	}
}
