package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;

/**
 * Implementation for RelLink.
 * 
 * @Alexandra_Dinisor
 *
 */
public class RelLinkImpl implements RelLink{
	
	private URL sourceURL;
	private URL targetURL;
	private String targetFormat;
	private String targetScope;
	private URL definitionLocation;

	public RelLinkImpl(URL sourceURL, URL targetURL, String targetFormat, String targetScope, URL definitionLocation) {
		this.sourceURL = sourceURL;
		this.targetURL = targetURL;
		this.targetFormat = targetFormat;
		this.targetScope = targetScope;
		this.definitionLocation = definitionLocation;		
	}

	@Override
	public URL getSourceURL() {
		return sourceURL;
	}

	@Override
	public URL getTargetURL() {
		return targetURL;
	}

	@Override
	public String getTargetFormat() {
		return targetFormat;
	}

	@Override
	public String getTargetScope() {
		return targetScope;
	}

	@Override
	public URL getTargetDefinitionLocation() {
		return definitionLocation;
	}

}
