package com.oxygenxml.sdksamples.translator;

/**
 * Translation interface for the keys.
 * 
 * @author Alexandra Dinisor
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
