package com.oxygenxml.ditareferences.treeReferences;

/**
 * Set the inhibiter when needed. For example, when caret with selection in tree
 * is used, selection from tree to caret is inhibited.
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
