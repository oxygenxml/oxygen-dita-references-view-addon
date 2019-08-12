package com.oxygenxml.sdksamples.translator;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization tags using PluginResourceBundle.
 * 
 * @author Alexandra Dinisor
 *
 */
public class DITAReferencesTranslator implements Translator {

	public DITAReferencesTranslator() {
	}
	
	@Override
	public String getTranslation(String key) {
		return ((StandalonePluginWorkspace) PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle()
				.getMessage(key);
	}

}
