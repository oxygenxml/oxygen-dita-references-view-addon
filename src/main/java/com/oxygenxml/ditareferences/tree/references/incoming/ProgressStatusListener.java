package com.oxygenxml.ditareferences.tree.references.incoming;

/**
 * An interface for Progress Status.
 * 
 * @author alex_smarandache
 *
 */
public interface ProgressStatusListener {

	/**
	 * If the list is in the loading process, a pending panel is displayed
	 * 
	 * @param inProgress <code>true</code> if the project is starting to load,
	 *                   <code>false</code> if the project was loaded.
	 * @param delay      after which the function to be executed
	 */
	public void updateInProgressStatus(final boolean inProgress, int delay) ;

}
