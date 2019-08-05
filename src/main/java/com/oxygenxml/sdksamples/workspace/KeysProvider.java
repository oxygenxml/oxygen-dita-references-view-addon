/**
 * 
 */
package com.oxygenxml.sdksamples.workspace;

import java.net.URL;
import java.util.LinkedHashMap;

import ro.sync.ecss.dita.reference.keyref.KeyInfo;

/**
 * @author Alexandra_Dinisor
 *
 */
public interface KeysProvider {

	/**
	 * Get the LinkedHashMap with KeyInfo for key references from the current DITAMAP.
	 */
	LinkedHashMap<String, KeyInfo> getKeys(URL editorLocation);

}
