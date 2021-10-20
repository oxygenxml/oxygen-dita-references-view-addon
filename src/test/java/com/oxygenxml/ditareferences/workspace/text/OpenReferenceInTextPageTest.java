package com.oxygenxml.ditareferences.workspace.text;

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.workspace.KeysProvider;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.UtilAccessForTests;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;
import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.util.URLUtil;

/**
 * Test if OpenReferenceAction opens the reference nodes depending on their
 * attribute type for TextPage.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class OpenReferenceInTextPageTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(OpenReferenceInTextPageTest.class);
	
	final String ditaTopicAllRefsContent = "<topic id=\"sample\" class=\"- topic/topic \">\n"
			+ "    <title class=\"- topic/title \">sample</title>\n"
			+ "    <body>\n"
			+ "            <image class=\"- topic/image \" href=\"image.png\"/>\n"
			+ "            <image class=\"- topic/image \" href=\"image\" format=\"png\"/> '\n "
			+ "            <p>Link to external resource <xref keyref=\"google\"/> </p>\n"
			+ "            <p class=\"- topic/p \" conref=\"sample2.dita#sample2/i1\"/>\n"
			+ "            <p class=\"- topic/p \" conkeyref=\"sample2/i1\" conrefend=\"bla.dita#test/i3\"/> \n"
			+ "            <object data=\"http://www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3\" \n" 
			+ "                                    outputclass=\"audio\" class=\"- topic/object \" />  \n" 	
			+ "    </body>\n "
			+ "    <related-links>\n"
			+ "            <link class=\"- topic/link \" href=\"www.google.com\" format=\"html\" scope=\"external\"/>\n"
			+ "            <link class=\"- topic/link \" href=\"sample2.dita\"/>\n"
			+ "            <link class=\"- topic/link \" href=\"test.pdf\" format=\"pdf\"><linktext>binary resource</linktext></link>\n"
			+ "            <link class=\"- topic/link \" keyref=\"google\"/>\n"
			+ "            <link class=\"- topic/link \" keyref=\"sample2\"/>\n"
			+ "            <link class=\"- topic/link \" keyref=\"myPDF\"><linktext>binary resource</linktext></link>\n"
			+ "    </related-links>\n"
			+ "</topic>";
	
	private final WSEditorAdapterForTests editorAccess = new WSEditorAdapterForTests() {

		private WSXMLTextEditorPageForTests currentTextPage;

		@Override
		public String getCurrentPageID() {
			return PAGE_TEXT;
		}

		@Override
		public WSEditorPage getCurrentPage() {
			if (currentTextPage == null) {
				currentTextPage = new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						return TestUtil.evaluateAllRefsExpression(ditaTopicAllRefsContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[15];
					}
				};
			}
			return currentTextPage;
		}

		@Override
		public URL getEditorLocation() {
			return URLUtil.convertToURL("file://C:/editor_location.ext");
		}
	};

	private final ArrayList<URL> urlToExternal = new ArrayList<URL>();
	private final ArrayList<URL> urlToDITA = new ArrayList<URL>();
	private final ArrayList<URL> urlToImage = new ArrayList<URL>();

	final StandalonePluginWorkspaceAccessForTests pluginWorkspaceAccess = new StandalonePluginWorkspaceAccessForTests() {
		@Override
		public void openInExternalApplication(URL url, boolean preferAssociatedApplication) {
			urlToExternal.add(url);
			super.openInExternalApplication(url, preferAssociatedApplication);
		}

		@Override
		public boolean open(URL url) {
			urlToDITA.add(url);
			return super.open(url);
		}

		@Override
		public boolean open(URL url, String imposedPage, String imposedContentType) {
			urlToImage.add(url);
			return super.open(url, imposedPage, imposedContentType);
		}

		@Override
		public UtilAccess getUtilAccess() {
			return new UtilAccessForTests();

		}
	};

	OutgoingReferencesTree tree = new OutgoingReferencesTree(pluginWorkspaceAccess, new KeysProvider() {
		@Override
		public LinkedHashMap<String, KeyInfo> getKeys(URL editorLocation) {
			LinkedHashMap<String, KeyInfo> keyMap = null;
			try {
				URL locationURL = new URL("file:/C:/Users/test/Documents/test.pdf");
				KeyInfo firstValue = new KeyInfo("myPDF", "test.pdf", "", locationURL, null, "", false);
				firstValue.setAttribute("keys", "myPDF");
				firstValue.setAttribute("href", "test.pdf");
				firstValue.setAttribute("format", "pdf");
				firstValue.setAttribute("processing-role", "resource-only");
				firstValue.setAttribute("class", "+ map/topicref mapgroup-d/keydef ");

				locationURL = new URL("file:/C:/Users/test/Documents/sample2.dita");
				KeyInfo secondValue = new KeyInfo("sample2", "sample2.dita", "", locationURL, null, "", false);
				secondValue.setAttribute("keys", "sample2");
				secondValue.setAttribute("href", "sample2.dita");
				secondValue.setAttribute("class", "- map/topicref ");

				keyMap = new LinkedHashMap<String, KeyInfo>();
				keyMap.put("myPDF", firstValue);
				keyMap.put("sample2", secondValue);
				return keyMap;
				
			} catch (MalformedURLException e) {
				LOGGER.debug(e, e);
			}
			return null;
		}
	});

	/**
	 * Open reference in image perspective of image with extension.
	 */
	@Test
	public void testOpenRef_ImageWithExtension() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		assertEquals(14, tree.getRowCount());

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 1, false);
		assertEquals("image.png", label.getText());

		tree.setSelectionRow(1);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToImage.get(0)), urlToImage.get(0).toString().endsWith("/image.png"));
	}

	/**
	 * Open reference in image perspective of image with no extension.
	 */
	@Test
	public void testOpenRef_ImageWithNoExtension() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(2);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 2, false);
		assertEquals("image", label.getToolTipText());

		tree.setSelectionRow(2);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToImage.get(0)), urlToImage.get(0).toString().endsWith("/image.png"));
	}
	
	/**
	 * Open reference in image perspective of image with no extension.
	 */
	@Test
	public void testOpenRef_AudioReference() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		
		TreePath path = tree.getPathForRow(3);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 3, false);
		assertEquals("http://www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3", label.getText());

		tree.setSelectionRow(3);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToExternal.get(0)),
				urlToExternal.get(0).toString().endsWith("www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3"));
	}

	/**
	 * Open DITA Topic from conref reference.
	 */
	@Test
	public void testOpenRef_DITATopicFromConRef() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(5);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 5, false);
		assertEquals("sample2.dita#sample2/i1", label.getText());

		tree.setSelectionRow(5);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToDITA.get(0)), urlToDITA.get(0).toString().endsWith("/sample2.dita#sample2/i1"));
	}
	
	/**
	 * Open DITA Topic from conkeyref reference.
	 */
	@Test
	public void testOpenRef_DITATopicFromConKeyRef() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(6);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 6, false);
		assertEquals("sample2/i1", label.getText());

		tree.setSelectionRow(6);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/sample2.dita", urlToDITA.get(0).toString());
	}

	/**
	 * Open HTML reference without protocol written by user in href attribute.
	 */
	@Test
	public void testOpenRef_HTMLFormat() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(8);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 8, false);
		assertEquals("www.google.com", label.getText());

		tree.setSelectionRow(8);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("http://www.google.com", urlToExternal.get(0).toString());
	}

	/**
	 * Open DITA topic from href reference.
	 */
	@Test
	public void testOpenRef_DITATopic() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("sample2.dita", label.getText());

		tree.setSelectionRow(9);
		TestUtil.simulateDoubleClick(tree);
	}

	
	/**
   * Tests tree node tooltips text.
   * 
   * @author Alex_Smarandache
   */
  @Test
  public void testReferenceToolTipText() {
    tree.setShowing(true);
    tree.setBounds(new Rectangle(0, 0, 1000, 1000));
    tree.refresh(editorAccess);
    
    String[] expectedToolTipText = {
        "file:/C:/Users/test/Documents/test.pdf",
        "image.png",
        "image",
        "http://www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3",
        "http://www.nasa.gov/mp3/590318main_ringtone_135_launch.mp3",
        "sample2.dita#sample2/i1",
        "file:/C:/Users/test/Documents/sample2.dita",
        "file:/C:/Users/test/Documents/sample2.dita",
        "www.google.com",
        "sample2.dita",
        "test.pdf",
        "test.pdf",
        "file:/C:/Users/test/Documents/sample2.dita",
        "file:/C:/Users/test/Documents/test.pdf"
    };
    
    for(int i = 0; i < expectedToolTipText.length; i++) {
      TreePath path = tree.getPathForRow(i);
      JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, 
          path.getLastPathComponent(), false, true, true, i, false);
      assertEquals(expectedToolTipText[i], label.getToolTipText());
    }
     
  }
  
  
	/**
	 * Open binary resource with @format attribute, not handled by Oxygen.
	 */
	@Test
	public void testOpenRef_BinaryResourceWithFormat() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(10);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 10, false);
		assertEquals("test.pdf", label.getText());

		tree.setSelectionRow(10);
		TestUtil.simulateDoubleClick(tree);
		assertTrue("Should have worked " + String.valueOf(urlToExternal.get(0)), urlToExternal.get(0).toString().endsWith("/test.pdf"));
	}

	/**
	 * Open DITA Topic from keyref attribute.
	 */
	@Test
	public void testOpenRef_DITATopicFromKeyref() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(12);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 12, false);
		assertEquals("sample2", label.getText());

		tree.setSelectionRow(12);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/sample2.dita", urlToDITA.get(0).toString());
	}

	/**
	 * Open binary resource from key reference, not handled by Oxygen.
	 */
	@Test
	public void testOpenRef_BinaryResourceFromKeyref() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(editorAccess);
		TreePath path = tree.getPathForRow(13);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 13, false);
		assertEquals("myPDF", label.getText());

		tree.setSelectionRow(13);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/test.pdf", urlToExternal.get(0).toString());
	}

}
