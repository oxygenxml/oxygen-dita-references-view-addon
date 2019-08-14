package com.oxygenxml.ditareferences.workspace.author;

import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;
import com.oxygenxml.ditareferences.workspace.authorpage.AuthorPageNodeRange;

import junit.framework.TestCase;

/**
 * Check rendering, SelectionListener and CaretListener for AuthorPage.
 * 
 * @Alexandra_Dinisor
 *
 */
public class AuthorPageTest extends TestCase {
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

	ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests(), null, new DITAReferencesTranslatorForTests());

	/**
	 * DITA topic opened in Author Mode with image reference.
	 */
	@Test
	public void test_RenderingImageReference() {
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refreshReferenceTree(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.refreshReferenceTree(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.refreshReferenceTree(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.refreshReferenceTree(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
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
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
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




}
