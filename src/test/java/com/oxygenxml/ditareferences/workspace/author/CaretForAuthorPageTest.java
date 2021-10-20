package com.oxygenxml.ditareferences.workspace.author;

import java.awt.Rectangle;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Test;

import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;
import com.oxygenxml.ditareferences.workspace.authorpage.AuthorPageNodeRange;

import junit.framework.TestCase;

public class CaretForAuthorPageTest extends TestCase {
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

	OutgoingReferencesTree tree = new OutgoingReferencesTree(new StandalonePluginWorkspaceAccessForTests(), null);

	/**
	 * Set caret inside a references element in AuthorPage and check for
	 * corresponding selection in ReferencesTree.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_SetCaretInAuthorPage() throws InterruptedException {
		tree.setShowing(true);
		tree.setBounds(new Rectangle(0, 0, 1000, 1000));
		WSEditorAdapterForTests editorAdapter = TestUtil.createWSEditorAdapterForAuthorPage(elemArray);
		tree.refresh(editorAdapter);
		WSAuthorEditorPageForTests authorPage = (WSAuthorEditorPageForTests) editorAdapter.getCurrentPage();

		authorPage.setCaretPosition(elemArray[2].getStartOffset());
		assertEquals(125, authorPage.getCaretOffset());
		Thread.sleep(2000); //NOSONAR

		AuthorPageNodeRange range = (AuthorPageNodeRange) ((DefaultMutableTreeNode) tree.getSelectionPath()
				.getLastPathComponent()).getUserObject();
		assertEquals("xref", range.getNodeName());
		assertEquals("http://www.google.com", range.getAttributeValue("href"));

	}

}
