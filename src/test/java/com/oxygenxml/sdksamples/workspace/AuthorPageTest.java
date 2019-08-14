package com.oxygenxml.sdksamples.workspace;

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.sdksamples.workspace.authorpage.AuthorPageNodeRange;

import junit.framework.TestCase;
import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.util.URLUtil;

public class AuthorPageTest extends TestCase {
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

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
	 * DITA topic opened in Author Mode with image reference.
	 */
	@Test
	public void test_RenderingImageReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refreshReferenceTree(createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 1, false);
		assertEquals("image2.jpg", label.getText());
	}

	/**
	 * DITA topic opened in Author Mode with cross reference.
	 */
	@Test
	public void test_RenderingCrossReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refreshReferenceTree(createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(4);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 4, false);
		assertEquals("google", label.getText());
	}

	/**
	 * DITA topic opened in Author Mode with content reference.
	 */
	@Test
	public void test_RenderingContentReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refreshReferenceTree(createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(6);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 6, false);
		assertEquals("sample2.dita#sample2/i1", label.getText());
	}

	/**
	 * DITA topic opened in Author Mode with related link reference.
	 */
	@Test
	public void test_RenderingLinkReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refreshReferenceTree(createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("myPDF", label.getText());
	}

	/**
	 * Select element in ReferencesTree and check for corresponding selection in
	 * AuthorPage.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_SelectNodeInTree() throws InterruptedException {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);
		WSAuthorEditorPageForTests authorPage = (WSAuthorEditorPageForTests) editorAdapter.getCurrentPage();

		assertEquals(49, elemArray[1].getStartOffset());
		assertEquals(50, elemArray[1].getEndOffset());

		TreePath path = tree.getPathForRow(1);
		tree.setSelectionPath(path);
		tree.setSelectionRow(1);
		Thread.sleep(2000);

		assertEquals(49, authorPage.getSelectionStart());
		assertEquals(50, authorPage.getSelectionEnd());

	}

	/**
	 * Set caret inside a references element in AuthorPage and check for
	 * corresponding selection in ReferencesTree.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_SetCaretInAuthorPage() throws InterruptedException {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);
		WSAuthorEditorPageForTests authorPage = (WSAuthorEditorPageForTests) editorAdapter.getCurrentPage();

		authorPage.setCaretPosition(elemArray[2].getStartOffset());
		assertEquals(125, authorPage.getCaretOffset());
		Thread.sleep(2000);

		AuthorPageNodeRange range = (AuthorPageNodeRange) ((DefaultMutableTreeNode) tree.getSelectionPath()
				.getLastPathComponent()).getUserObject();
		assertEquals("xref", range.getNodeName());
		assertEquals("http://www.google.com", range.getAttributeValue("href"));

	}

	/**
	 * Open image reference from ReferencesTree.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_OpenImageReference() throws InterruptedException {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		assertEquals(10, tree.getRowCount());

		TreePath path = tree.getPathForRow(1);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 1, false);
		assertEquals("image2.jpg", label.getText());

		tree.setSelectionRow(1);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:////C:/image2.jpg", URLToImage.get(0).toString());

	}

	/**
	 * Open conref reference from ReferencesTree.
	 */
	@Test
	public void test_OpenConrefReference() throws InterruptedException {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		TreePath path = tree.getPathForRow(6);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 6, false);
		assertEquals("sample2.dita#sample2/i1", label.getText());

		tree.setSelectionRow(6);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:////C:/sample2.dita#sample2/i1", URLToDITA.get(0).toString());

	}

	/**
	 * Open conkeyref reference from ReferencesTree.
	 */
	@Test
	public void test_OpenConkeyrefReference() throws InterruptedException {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		TreePath path = tree.getPathForRow(7);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 7, false);
		assertEquals("sample2/i1", label.getText());

		tree.setSelectionRow(7);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:/C:/Users/test/Documents/sample2.dita", URLToDITA.get(0).toString());

	}

	/**
	 * Open binary resource, not handled by Oxygen.
	 */
	@Test
	public void test_OpenBinaryResourceReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = createWSEditorAdapterForAuthorPage(elemArray);
		tree.refreshReferenceTree(editorAdapter);

		System.out.println(TestUtil.logTreeNodes(tree));

		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("myPDF", label.getText());

		tree.setSelectionRow(9);
		TestUtil.simulateDoubleClick(tree);

		assertEquals("file:/C:/Users/test/Documents/test.pdf", URLToExternal.get(0).toString());
	}

	/**
	 * Create the WSEditor for AuthorPage depending on the XPath expression.
	 * 
	 * @param elem The array of AuthorElements with XPath evaluation
	 * @return WSEditor in AuthorMode with XPath evaluation
	 */
	private WSEditorAdapterForTests createWSEditorAdapterForAuthorPage(AuthorElementAdapter[] elem) {
		return new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_AUTHOR;
			}

			private WSAuthorEditorPageForTests authorPageInstance = null;

			@Override
			public WSEditorPage getCurrentPage() {
				if (authorPageInstance == null) {
					authorPageInstance = new WSAuthorEditorPageForTests() {
						@Override
						public AuthorDocumentController getDocumentController() {

							return new AuthorDocumentControllerForTests() {
								@Override
								public AuthorNode[] findNodesByXPath(String xpathExpression, boolean ignoreTexts,
										boolean ignoreCData, boolean ignoreComments) throws AuthorOperationException {
									// return an array of AuthorElement
									return elem;
								}
							};
						}
					};
				}
				return authorPageInstance;
			}

			@Override
			public URL getEditorLocation() {
				return URLUtil.convertToURL("file://C:/editor_location.ext");
			}
		};
	}

}
