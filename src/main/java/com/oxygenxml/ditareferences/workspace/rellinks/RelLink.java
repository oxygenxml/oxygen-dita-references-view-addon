package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;

public interface RelLink {

	/**
	 * @return Returns the source URL.
	 */
	public URL getSourceURL();

	/**
	 * @return Returns the target URL.
	 */
	public URL getTargetURL();

	/**
	 * 
	 * @return Returns the target format.
	 */
	public String getTargetFormat();

	/**
	 * 
	 * @return Returns the target scope.
	 */
	public String getTargetScope();

	/**
	 * @return Returns the target definition location.
	 */
	public URL getTargetDefinitionLocation();
}
