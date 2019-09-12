package com.oxygenxml.ditareferences.workspace.text;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.oxygenxml.ditareferences.workspace.DITAReferencesTranslatorForTests;
import com.oxygenxml.ditareferences.workspace.NodeRange;
import com.oxygenxml.ditareferences.workspace.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.StandalonePluginWorkspaceAccessForTests;
import com.oxygenxml.ditareferences.workspace.TestUtil;
import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Test if the ReferencesTree node is selected after caret from textPage.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class CaretForTextPageTest extends TestCase {
	private static final Logger LOGGER = Logger.getLogger(CaretForTextPageTest.class);
	
	private List<Integer> selectionOffsets = new ArrayList<Integer>();

	/**
	 * Test for selection of the corresponding node in the refTree after caret in
	 * textPage.
	 */
	@Test
	public void testCaretListener() {
		ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests(),
				null, new DITAReferencesTranslatorForTests());
		tree.setShowing(true);

		final String ditaContent = "<topic id=\"copyright\" class=\"- topic/topic \">\n"
				+ "    <title>Copyright</title>\n" 
				+ "    <shortdesc>Legal-related information.</shortdesc>\n"
				+ "    <body>\n" 
				+ "        <p>Most of the information was taken from <xref class=\"- topic/xref \" href=\"www.wikipedia.com\"\n"
				+ "                format=\"html\" scope=\"external\">Wikipedia</xref>, the free encyclopedia.</p>\n"
				+ "    </body>\n" 
				+ "</topic>";

		WSEditorAdapterForTests editor = new WSEditorAdapterForTests() {

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
							return TestUtil.evaluateAllRefsExpression(ditaContent);
						}

						@Override
						public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
							WSXMLTextNodeRange interestingNode = new WSXMLTextNodeRangeForTests() {
								@Override
								public int getStartLine() {
									return 5;

								}

								@Override
								public int getEndLine() {
									return 7;

								}

								@Override
								public int getStartColumn() {
									return 40;
								}

								@Override
								public int getEndColumn() {
									return 45;
								}
							};
							return new WSXMLTextNodeRange[] { new WSXMLTextNodeRangeForTests(), interestingNode };
						}

						@Override
						public int getCaretOffset() {
							return textArea.getCaretPosition();

						}

						@Override
						public void setCaretPosition(int offset) {
							textArea.setCaretPosition(offset);
						}

						@Override
						public void select(int startOffset, int endOffset) {
							selectionOffsets.add(startOffset);
							selectionOffsets.add(endOffset);
						}

						@Override
						public int getOffsetOfLineStart(int lineNumber) throws BadLocationException {
							if (lineNumber == 5) {
								// first line of the reference
								return 150;
							} else {
								// last line of the reference
								return 306;
							}
						}

					};
				}
				return currentTextPage;
			}
		};

		WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) editor.getCurrentPage();
		JTextArea textArea = (JTextArea) textPage.getTextComponent();
		textArea.setText(ditaContent);
		tree.refreshReferenceTree(editor);

		// set the caret inside the "xref" element in the XML textPage
		textPage.setCaretPosition(textArea.getText().indexOf("<xref") + 5);
		System.out.println(TestUtil.logTreeNodes(tree));
		try {
			Thread.sleep(1000);
			assertEquals(1, tree.getSelectionModel().getSelectionCount());

			NodeRange selectedNodeRange = (NodeRange) ((DefaultMutableTreeNode) tree.getSelectionPath()
					.getLastPathComponent()).getUserObject();
			assertEquals("www.wikipedia.com", selectedNodeRange.getAttributeValue("href"));
			
		} catch (InterruptedException e) {
			LOGGER.debug(e, e);
		}

	}

}
