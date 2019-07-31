package com.oxygenxml.sdksamples.workspace;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.junit.Test;

import junit.framework.TestCase;
import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.util.URLUtil;

/**
 * Test if OpenReferenceAction opens the reference nodes depending on their
 * attribute type.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class OpenReferenceTest extends TestCase {
	
	final String ditaTopicAllRefsContent = "<topic id=\"sample\" class=\"- topic/topic \">\n"
			+ "    <title class=\"- topic/title \">sample</title>\n"
			+ "    <body>\n"
			+ "            <image class=\"- topic/image \" href=\"image.png\"/>\n"
			+ "            <image class=\"- topic/image \" href=\"image\" format=\"png\"/> '\n "
			+ "            <p>Link to external resource <xref keyref=\"google\"/> </p>\n"
			+ "            <p class=\"- topic/p \" conref=\"sample2.dita#sample2/i1\"/>\n"
			+ "            <p class=\"- topic/p \" conkeyref=\"sample2/i1\" conrefend=\"bla.dita#test/i3\"/> \n"
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
						return new WSXMLTextNodeRange[14];
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

	private final ArrayList<URL> URLToExternal = new ArrayList<URL>();
	private final ArrayList<URL> URLToDITA = new ArrayList<URL>();
	private final ArrayList<URL> URLToImage = new ArrayList<URL>();

	final StandalonePluginWorkspaceAccessForTests pluginWorkspaceAccess = new StandalonePluginWorkspaceAccessForTests() {
		@Override
		public void openInExternalApplication(URL url, boolean preferAssociatedApplication) {
			URLToExternal.add(url);
			super.openInExternalApplication(url, preferAssociatedApplication);
		}

		@Override
		public boolean open(URL url) {
			URLToDITA.add(url);
			return super.open(url);
		}

		@Override
		public boolean open(URL url, String imposedPage, String imposedContentType) {
			URLToImage.add(url);
			return super.open(url, imposedPage, imposedContentType);
		}

		@Override
		public UtilAccess getUtilAccess() {
			return new UtilAccessForTests();

		}
	};

	ReferencesTree tree = new ReferencesTree(pluginWorkspaceAccess, new KeysProvider() {
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
				e.printStackTrace();
			}
			return null;
		}
	}, new DITAReferencesTranslatorForTests());

	/**
	 * Open reference in image perspective of image with extension.
	 * 
	 */
	@Test
	public void testOpenRef_ImageWithExtension() {
		tree.refreshReferenceTree(editorAccess);
		assertEquals(13, tree.getRowCount());

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 1, false);
		assertEquals("image.png", label.getText());

		tree.setSelectionRow(1);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:////C:/image.png", URLToImage.get(0).toString());
	}

	/**
	 * Open reference in image perspective of image with no extension.
	 * 
	 */
	@Test
	public void testOpenRef_ImageWithNoExtension() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(2);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 2, false);
		assertEquals("image", label.getText());

		tree.setSelectionRow(2);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:////C:/image.png", URLToImage.get(0).toString());
	}

	/**
	 * Open DITA Topic from conref reference.
	 * 
	 */
	@Test
	public void testOpenRef_DITATopicFromConRef() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(4);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 4, false);
		assertEquals("... ple2.dita#sample2/i1", label.getText());

		tree.setSelectionRow(4);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:////C:/sample2.dita#sample2/i1", URLToDITA.get(0).toString());
	}
	
	/**
	 * Open DITA Topic from conkeyref reference.
	 * 
	 */
	@Test
	public void testOpenRef_DITATopicFromConKeyRef() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(5);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 5, false);
		assertEquals("sample2/i1", label.getText());

		tree.setSelectionRow(5);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/sample2.dita", URLToDITA.get(0).toString());
	}

	/**
	 * Open HTML reference without protocol written by user in href attribute.
	 * 
	 */
	@Test
	public void testOpenRef_HTMLFormat() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(7);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 7, false);
		assertEquals("www.google.com", label.getText());

		tree.setSelectionRow(7);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("http://www.google.com", URLToExternal.get(0).toString());
	}

	/**
	 * Open DITA topic from href reference.
	 */
	@Test
	public void testOpenRef_DITATopic() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(8);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 8, false);
		assertEquals("sample2.dita", label.getText());

		tree.setSelectionRow(8);
		TestUtil.simulateDoubleClick(tree);
	}

	/**
	 * Open binary resource with @format attribute, not handled by Oxygen.
	 * 
	 */
	@Test
	public void testOpenRef_BinaryRescourceWithFormat() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("test.pdf", label.getText());

		tree.setSelectionRow(9);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:////C:/test.pdf", URLToExternal.get(0).toString());
	}

	/**
	 * Open DITA Topic from keyref attribute.
	 * 
	 */
	@Test
	public void testOpenRef_DITATopicFromKeyref() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(11);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 11, false);
		assertEquals("sample2", label.getText());

		tree.setSelectionRow(11);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/sample2.dita", URLToDITA.get(0).toString());
	}

	/**
	 * Open binary resource from key reference, not handled by Oxygen.
	 * 
	 */
	@Test
	public void testOpenRef_BinaryResourceFromKeyref() {
		tree.refreshReferenceTree(editorAccess);
		TreePath path = tree.getPathForRow(12);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 12, false);
		assertEquals("myPDF", label.getText());

		tree.setSelectionRow(12);
		TestUtil.simulateDoubleClick(tree);
		assertEquals("file:/C:/Users/test/Documents/test.pdf", URLToExternal.get(0).toString());
	}

}
