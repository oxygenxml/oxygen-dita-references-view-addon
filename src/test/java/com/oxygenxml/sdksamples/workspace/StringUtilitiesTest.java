package com.oxygenxml.sdksamples.workspace;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import junit.framework.TestCase;

public class StringUtilitiesTest extends TestCase {

	public void testTrimText() {
		int width = 100;
		String text = "gears.dita#gears_topic/idler_gear";
		Font font = new Font("Courier New", Font.PLAIN, 14);
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
		
		int advanceWidth = fontMetrics.charsWidth("idler_gear".toCharArray(), 0, "idler_gear".length());
		System.out.println("advancewidth for idler_gear: " + advanceWidth);
		String expected = "idler_gear";
		String trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);
		
		width = 200;
		expected = "gears_topic/idler_gear";
		advanceWidth = fontMetrics.charsWidth(expected.toCharArray(), 0, expected.length());
		System.out.println("advancewidth for gears_topic/idler_gear: " + advanceWidth);

		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);
		
		
		width = 300;
		text = "abcdefghij.dita#gears_topic/idler_gear";
		expected = "gears_topic/idler_gear";
		advanceWidth = fontMetrics.charsWidth(text.toCharArray(), 0, text.length());
		System.out.println("advancewidth for " + text + ": " + advanceWidth);

		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);
	}

}
