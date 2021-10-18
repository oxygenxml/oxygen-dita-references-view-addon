package com.oxygenxml.ditareferences.i18n;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization tags using PluginResourceBundle.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class DITAReferencesTranslator implements Translator {

  public DITAReferencesTranslator() {
		// empty constructor
	}
	
	@Override
	public String getTranslation(String key) {
		String toReturn = key;
		if(PluginWorkspaceProvider.getPluginWorkspace() instanceof StandalonePluginWorkspace) {
			toReturn = ((StandalonePluginWorkspace) PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle()
					.getMessage(key);
		}
		
		return toReturn;
	}

}
