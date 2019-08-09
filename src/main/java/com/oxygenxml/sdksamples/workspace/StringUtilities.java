package com.oxygenxml.sdksamples.workspace;

import java.awt.FontMetrics;

public class StringUtilities {
	/**
	 * 
	 * @param fontMetrics
	 * @param referenceAttributeValue
	 * @param width
	 * @return
	 */
	public static String trimNodeText(FontMetrics fontMetrics, String referenceAttributeValue, int width) {
		String toDisplayString = null;

		// TODO show file name.
		char[] charArray = referenceAttributeValue.toCharArray();
		
		if (fontMetrics.charsWidth(charArray, 0, charArray.length) <= width) {
			toDisplayString = referenceAttributeValue;
		} else {
			for (int i = referenceAttributeValue.length() - 1; i >= 0; i--) {
				
				if (referenceAttributeValue.charAt(i) == '#' || referenceAttributeValue.charAt(i) == '/'
						|| referenceAttributeValue.charAt(i) == '\\' ) {
					
					int tokenLength = fontMetrics.charsWidth(charArray, i + 1, charArray.length - i - 1);
					if (tokenLength <= width) {
						// We found a candidate.
						toDisplayString = referenceAttributeValue.substring(i + 1);
					} else {
						// Too long.
						if (toDisplayString == null) {
							// It is actually the first token being analyzed. Present it.
							toDisplayString = referenceAttributeValue.substring(i + 1);
						}						
						break;
					}
				}
			}	
		}
		System.err.println("to be displayed: " + toDisplayString);
		return toDisplayString;
	}
}
