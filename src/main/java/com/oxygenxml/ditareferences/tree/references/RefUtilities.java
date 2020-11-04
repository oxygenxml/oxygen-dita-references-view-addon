package com.oxygenxml.ditareferences.tree.references;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import com.oxygenxml.ditareferences.workspace.DITAConstants;

import ro.sync.ecss.dita.reference.keyref.KeyInfo;

public class RefUtilities {

	/**
	 * Get the specific KeyInfo of the given key after removing the "/" if any. in
	 * order to get the filename.
	 * 
	 * @param keyAttrValue The key reference attribute value
	 * @param keys         The LinkedHashMap with all the keys
	 */
	public static KeyInfo getKeyInfoFromReference(String keyAttrValue, LinkedHashMap<String, KeyInfo> keys) {
		if(keys != null) {
			StringTokenizer st = new StringTokenizer(keyAttrValue, "/");
			String keyName = null;
			if (st.hasMoreTokens()) {
				keyName = st.nextToken();
			}
			return keys.get(keyName);
		}
		return null;
	}
	
	/**
	 * Get URL in case of no protocol in the attribute value. The HTTP Host should
	 * have the protocol name when new URL created. For example:
	 * "http://www.google.com" if user types "www.google.com".
	 * 
	 * @param formatAttrValue Format attribute value to verify
	 * @param hrefAttrValue   The HREF value of the attribute
	 * @param possibleURL     The URL if no HTML format available
	 * @return The target URL
	 * @throws MalformedURLException
	 */
	public static URL getURLForHTTPHost(String formatAttrValue, String hrefAttrValue, URL possibleURL)
			throws MalformedURLException {
		URL url;
		if (formatAttrValue != null && formatAttrValue.equals(DITAConstants.FORMAT_HTML)
				&& !hrefAttrValue.startsWith(DITAConstants.PROTOCOL_HTTP)) {
			url = new URL("http://" + hrefAttrValue);
		} else {
			url = possibleURL;
		}
		return url;
	}
}
