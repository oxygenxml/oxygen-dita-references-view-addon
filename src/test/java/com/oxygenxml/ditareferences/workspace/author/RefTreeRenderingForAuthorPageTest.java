package com.oxygenxml.ditareferences.workspace.author;

import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;

import junit.framework.TestCase;

/**
 * Check rendering, SelectionListener and CaretListener for AuthorPage.
 * 
 * @Alexandra_Dinisor
 *
 */
public class RefTreeRenderingForAuthorPageTest extends TestCase {
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

	OutgoingReferencesTree tree = new OutgoingReferencesTree(new StandalonePluginWorkspaceAccessForTests(), null,
			new DITAReferencesTranslatorForTests());

	/**
	 * DITA topic opened in Author Mode with image reference.
	 */
	@Test
	public void test_RenderingImageReference() {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

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
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(9);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, 9, false);
		assertEquals("myPDF", label.getText());
	}

}
