package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;

import com.oxygenxml.ditareferences.workspace.NodeRange;

public abstract class RelLinkNodeRange extends NodeRange {
	
	/**
	 * Get the URL of the target definition location of a relationship link.
	 * 
	 * @return the definition location URL
	 */
	public abstract URL getTargetDefinitionLocation();
}
