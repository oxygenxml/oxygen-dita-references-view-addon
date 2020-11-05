package com.oxygenxml.ditareferences.workspace.text;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.tree.TreePath;

import org.junit.Test;

import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Test the node selection in XML TextPage while selecting the corresponding
 * leaf node in the ReferencesTree.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class SelectLeafNodeForTextPageTest extends TestCase {

	private List<Integer> selectionOffsets = new ArrayList<Integer>();

	/**
	 * Test for element selection in textPage after selecting it in ReferencesTree.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void test_SelectionInTreeTextPage() throws InterruptedException {
		OutgoingReferencesTree tree = new OutgoingReferencesTree(new StandalonePluginWorkspaceAccessForTests(),
				null, new DITAReferencesTranslatorForTests());

		WSEditorAdapterForTests editor = new WSEditorAdapterForTests() {
			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				return new WSXMLTextEditorPageForTests() {
					@Override
					public Object[] evaluateXPath(String xpathExpression) throws XPathException {
						final String ditaContent = "<topic id=\"copyright\" class=\"- topic/topic \">\n"
								+ "    <title>Copyright</title>\n"
								+ "    <shortdesc>Legal-related information.</shortdesc>\n" + "    <body>\n"
								+ "        <p>Most of the information was taken from <xref class='- topic/xref '\n"
								+ "href=\"www.wikipedia.com\"\n"
								+ "                format=\"html\" scope=\"external\">Wikipedia</xref>, the free encyclopedia.</p>\n"
								+ "    </body>\n" + "</topic>";

						return TestUtil.evaluateAllRefsExpression(ditaContent);
					}

					@Override
					public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
						return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests(),
								new WSXMLTextNodeRangeForTests() };
					}

					@Override
					public void select(int startOffset, int endOffset) {
						selectionOffsets.add(startOffset);
						selectionOffsets.add(endOffset);
					}

					@Override
					public int getOffsetOfLineStart(int lineNumber) throws BadLocationException {
						return 5;
					}
				};
			}
		};
		tree.setShowing(true);
		tree.refresh(editor);
		TreePath path = tree.getPathForRow(1);

		tree.setSelectionPath(path);
		tree.setSelectionRow(1);
		Thread.sleep(700);

		assertEquals("[4, 4]", selectionOffsets.toString());

	}

}
