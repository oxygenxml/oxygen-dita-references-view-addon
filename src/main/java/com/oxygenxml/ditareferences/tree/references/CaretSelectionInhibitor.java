package com.oxygenxml.ditareferences.tree.references;

/**
 * Set the inhibiter when needed. For example, when selection from tree to
 * element in textPage is used, caret with selection in tree is inhibited.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface CaretSelectionInhibitor {
	/**
	 * Sets the inhibiter on false by default.
	 * 
	 * @param inhibitTreeSelectionListener The inhibiter
	 */
	public void setInhibitCaretSelectionListener(boolean inhibitCaretSelectionListener);

}
