package com.oxygenxml.sdksamples.workspace;

/**
 * Set the inhibiter when needed.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface TreeSelectionInhibitor {
	/**
	 * Set on false by default.
	 * 
	 * @param inhibitTreeSelectionListener The inhibiter set on false by default
	 */
	public void setInhibitTreeSelectionListener(boolean inhibitTreeSelectionListener);
}
