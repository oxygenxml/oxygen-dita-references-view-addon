package com.oxygenxml.ditareferences.i18n;

import java.io.Serializable;

import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Implement internationalization tags using PluginResourceBundle.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class DITAReferencesTranslator implements Translator, Serializable {

	/**
   * Generated UID
   */
  private static final long serialVersionUID = -5909408625888871637L;

  public DITAReferencesTranslator() {
		// empty constructor
	}
	
	@Override
	public String getTranslation(String key) {
		return ((StandalonePluginWorkspace) PluginWorkspaceProvider.getPluginWorkspace()).getResourceBundle()
				.getMessage(key);
	}

}
