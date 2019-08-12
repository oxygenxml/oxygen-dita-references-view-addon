package com.oxygenxml.sdksamples.workspace;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import org.junit.Test;

import junit.framework.TestCase;

public class StringUtilitiesTest extends TestCase {	
	int width;
	int advanceWidth;
	String text;
	String expected;
	String trimmed;
	
	Font font = new Font("Courier New", Font.PLAIN, 14);
	FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
		
	@Test
	public void test_TrimLastComponent() {
		width = 100;
		text = "gears.dita#gears_topic/idler_gear";
		expected = "idler_gear";
		advanceWidth = fontMetrics.charsWidth(text.toCharArray(), 0, text.length());
		System.out.println("advanceWidth for " + text + ": " + advanceWidth);
		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);
	}
	
	@Test
	public void test_TrimAnchor() {
		
		width = 200;
		text = "gears_topic/idler_gear";
		advanceWidth = fontMetrics.charsWidth(text.toCharArray(), 0, text.length());
		System.out.println("advanceWidth for " + text + ": " + advanceWidth);
		expected = "gears_topic/idler_gear";
		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);
	}
		
	@Test
	public void test_TrimALongerText() {
		width = 300;
		text = "abcdefghij.dita#gears_topic/idler_gear";
		expected = "gears_topic/idler_gear";
		advanceWidth = fontMetrics.charsWidth(text.toCharArray(), 0, text.length());
		System.out.println("advanceWidth for " + text + ": " + advanceWidth);

		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		assertEquals(expected, trimmed);	
	}
	
	@Test
	public void test_TrimLongTextWithoutDelim() {
		width = 300;
		text = "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.png";
		expected = text;
		advanceWidth = fontMetrics.charsWidth(text.toCharArray(), 0, text.length());
		trimmed = StringUtilities.trimNodeText(fontMetrics, text, width);
		System.out.println("advanceWidth for " + text + ": " + advanceWidth);
		assertEquals(expected, trimmed);
	}

}
