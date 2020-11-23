package com.oxygenxml.ditareferences.workspace;

import java.awt.FontMetrics;

/**
 * Utilities for node text.
 * 
 * @Alexandra_Dinisor
 *
 */
public class StringUtilities {

	private StringUtilities() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Trim the node text so that it is shown a part of the name clearly.
	 * 
	 * @param fontMetrics             The FontMetrics
	 * @param referenceAttributeValue The ReferenceAttributeValue
	 * @param width                   The width
	 * @return The displayed part of node text
	 */
	public static String trimNodeText(FontMetrics fontMetrics, String referenceAttributeValue, int width) {
		String toDisplayString = referenceAttributeValue;

		char[] charArray = referenceAttributeValue.toCharArray();
		if (fontMetrics.charsWidth(charArray, 0, charArray.length) > width) {
		// search through delimiters for possible string
      toDisplayString = searchThroughDelimiters(fontMetrics, referenceAttributeValue, width, toDisplayString);
		} 
		return toDisplayString;
	}

	/**
	 * Iterate through node text from backwards to forwards considering the
	 * delimiters "#", "/", "\" and the fontMetrics available.
	 * 
	 * @param fontMetrics             The fontMetrics
	 * @param referenceAttributeValue The reference AttributeValue
	 * @param width                   The width
	 * @param toDisplayString         The string to be displayed for node
	 * @return toDisplayString
	 */
	private static String searchThroughDelimiters(FontMetrics fontMetrics, String referenceAttributeValue, int width,
			String toDisplayString) {
		char[] charArray = referenceAttributeValue.toCharArray();
		
		for (int i = referenceAttributeValue.length() - 1; i >= 0; i--) {
			if (referenceAttributeValue.charAt(i) == '#' || referenceAttributeValue.charAt(i) == '/'
					|| referenceAttributeValue.charAt(i) == '\\') {

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
		return toDisplayString;
	}
}
