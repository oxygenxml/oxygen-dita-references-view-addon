package com.oxygenxml.ditareferences.tree.references.incoming;

import com.oxygenxml.ditareferences.tree.references.incoming.IncomingReference;

import junit.framework.TestCase;
import ro.sync.util.URLUtil;

/**
 * Test the /oxygen-dita-references-view/src/main/java/com/oxygenxml/ditareferences/tree/references/incoming/IncomingReference class.
 * 
 * @author alex_smarandache
 *
 */
public class IncomingReferenceTest extends TestCase {

	/**
	 * Test incoming reference methods.
	 * 
	 * @author alex_smarandache
	 * 
	 * @throws Exception
	 */
	public void testIncomingReference() throws Exception {
		DPIForTest dpi = new DPIForTest();
		String systemID = "file:/C:/Users/alex_smarandache/Documents/GitHub/userguide-private/DITA/maps/chapter-installation.ditamap";
		dpi.setSystemID(systemID);
		IncomingReference incomingReference = new IncomingReference(dpi);
		dpi.setMessage("My message");
		
		String toolTipTextExpected = "<html><p>" + URLUtil.getDescription(systemID) + "</p>My message[Line: 50, Column: 100]</p></html>";
		assertEquals(toolTipTextExpected, incomingReference.getTooltipText());
		
		String expectedRenderText = "chapter-installation.ditamap";
		assertEquals(expectedRenderText, incomingReference.getRenderText());
		
		assertEquals(systemID, incomingReference.getSystemId());
		
		assertSame(dpi, incomingReference.getDPI());
	}
}
