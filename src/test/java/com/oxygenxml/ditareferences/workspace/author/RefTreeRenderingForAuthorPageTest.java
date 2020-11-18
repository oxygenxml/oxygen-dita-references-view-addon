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
	  testBase("image2.jpg", 1);
	}

	/**
	 * DITA topic opened in Author Mode with cross reference.
	 */
	@Test
	public void test_RenderingCrossReference() {
	  testBase("google", 4);
	}

	/**
	 * DITA topic opened in Author Mode with content reference.
	 */
	@Test
	public void test_RenderingContentReference() {
	  testBase("sample2.dita#sample2/i1", 6);
	}

	/**
	 * DITA topic opened in Author Mode with related link reference.
	 */
	@Test
	public void test_RenderingLinkReference() {
		testBase("myPDF", 9);
	}

  private void testBase(String result, int number) {
    tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		tree.refresh(TestUtil.createWSEditorAdapterForAuthorPage(elemArray));

		TreePath path = tree.getPathForRow(number);
		JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(),
				false, true, true, number, false);
		assertEquals(result, label.getText());
  }

}
