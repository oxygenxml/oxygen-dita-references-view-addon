package com.oxygenxml.sdksamples.workspace;

/**
 * Set the inhibitor when needed.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface CaretSelectionInhibitor {
	/**
	 * Sets the inhibitor on false by default.
	 * 
	 * @param inhibitTreeSelectionListener The inhibitor
	 */
	public void setInhibitCaretSelectionListener(boolean inhibitCaretSelectionListener);

}
