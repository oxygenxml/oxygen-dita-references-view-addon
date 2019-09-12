package com.oxygenxml.ditareferences.workspace.author;

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.KeysProvider;
import com.oxygenxml.ditareferences.workspace.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.UtilAccessForTests;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;
import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * Test if OpenReferenceAction opens the reference nodes depending on their
 * attribute type for AuthorPage.
 * 
 * @Alexandra_Dinisor
 *
 */
public class OpenReferenceInAuthorPageTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(OpenReferenceInAuthorPageTest.class);
	
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

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
				LOGGER.debug(e, e);
			}
			return null;
		}
	}, new DITAReferencesTranslatorForTests());
	
	/**
	 * Open image reference from ReferencesTree.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_OpenImageReference() throws InterruptedException {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		assertEquals(10, tree.getRowCount());

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 1, false);
		assertEquals("image2.jpg", label.getText());

		tree.setSelectionRow(1);
		TestUtil.simulateDoubleClick(tree);

		assertTrue("Should have worked " + String.valueOf(urlToImage.get(0)), urlToImage.get(0).toString().endsWith("/image2.jpg"));

	}

	/**
	 * Open conref reference from ReferencesTree.
	 */
	@Test
	public void test_OpenConrefReference() throws InterruptedException {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		TreePath path = tree.getPathForRow(6);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 6, false);
		assertEquals("sample2.dita#sample2/i1", label.getText());

		tree.setSelectionRow(6);
		TestUtil.simulateDoubleClick(tree);

		assertTrue("Should have worked " + String.valueOf(urlToDITA.get(0)), urlToDITA.get(0).toString().endsWith("/sample2.dita#sample2/i1"));

	}

	/**
	 * Open conkeyref reference from ReferencesTree.
	 */
	@Test
	public void test_OpenConkeyrefReference() throws InterruptedException {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		TreePath path = tree.getPathForRow(7);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 7, false);
		assertEquals("sample2/i1", label.getText());

		tree.setSelectionRow(7);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:/C:/Users/test/Documents/sample2.dita", urlToDITA.get(0).toString());

	}

	/**
	 * Open binary resource, not handled by Oxygen.
	 */
	@Test
	public void test_OpenBinaryResourceReference() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("myPDF", label.getText());

		tree.setSelectionRow(9);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:/C:/Users/test/Documents/test.pdf", urlToExternal.get(0).toString());
	}


}
