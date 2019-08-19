package com.oxygenxml.ditareferences.translator;

/**
 * Translation interface for the keys.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface Translator {
	/**
	 * Get the translation from the given key.
	 * 
	 * @param key The key.
	 */
	public String getTranslation(String key);
}
