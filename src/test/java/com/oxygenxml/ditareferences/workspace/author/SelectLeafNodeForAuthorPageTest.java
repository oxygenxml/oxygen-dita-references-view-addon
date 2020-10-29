package com.oxygenxml.ditareferences.workspace.author;

import java.awt.Rectangle;

import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.ditareferences.treeReferences.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;

public class SelectLeafNodeForAuthorPageTest extends TestCase {
	AuthorElementAdapter[] elemArray = TestUtil.createAuthorElementArray();

	ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests(), null, new DITAReferencesTranslatorForTests());
	
	/**
	 * Select element in ReferencesTree and check for corresponding selection in
	 * AuthorPage.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_SelectNodeInTree() throws InterruptedException {
		tree.setShowing(true);
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

}
