package com.oxygenxml.sdksamples.workspace;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import junit.framework.TestCase;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class CaretTreeTest extends TestCase {

	private List<Integer> selectionOffsets = new ArrayList<Integer>();

	/**
	 * Test for caret in textPage with selection of the corresponding node in the
	 * refTree.
	 */
	@Test
	public void testForCaretListener() {
		ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests(),
				new DITAReferencesTranslatorForTests());

		final String ditaContent = "<topic id=\"copyright\" class=\"- topic/topic \">\n"
				+ "    <title>Copyright</title>\n" + "    <shortdesc>Legal-related information.</shortdesc>\n"
				+ "    <body>\n" + "        <p>Most of the information was taken from <xref class='- topic/xref '\n"
				+ "href=\"www.wikipedia.com\"\n"
				+ "                format=\"html\" scope=\"external\">Wikipedia</xref>, the free encyclopedia.</p>\n"
				+ "    </body>\n" + "</topic>";

		WSEditorAdapterForTests editor = new WSEditorAdapterForTests() {

			private WSXMLTextEditorPageForTests currentTP;

			@Override
			public String getCurrentPageID() {
				return PAGE_TEXT;
			}

			@Override
			public WSEditorPage getCurrentPage() {
				if (currentTP == null) {
					currentTP = new WSXMLTextEditorPageForTests() {
						@Override
						public Object[] evaluateXPath(String xpathExpression) throws XPathException {
							return evaluateAllRefsExpression(ditaContent);
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
				return currentTP;
			}
		};

		WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) editor.getCurrentPage();
		JTextArea textArea = (JTextArea) textPage.getTextComponent();
		textArea.setText(ditaContent);
		tree.refreshReferenceTree(editor);

		textPage.setCaretPosition(textArea.getText().indexOf("<xref") + 5);

		// the "xref" node in the reference tree is selected
		try {
			Thread.sleep(1000);
			System.err.println("THE PATH " + tree.getSelectionModel().getSelectionPath());
			assertEquals(1, tree.getSelectionModel().getSelectionCount());
			
			NodeRange selectedNodeRange = (NodeRange) ((DefaultMutableTreeNode) tree.getSelectionPath()
					.getLastPathComponent()).getUserObject();
			assertEquals("www.wikipedia.com", selectedNodeRange.getAttributeValue("href"));			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Evaluate the xPath expression in case of a DITA topic. Only used in
	 * TestCases.
	 * 
	 * @param ditaContent The DITA content
	 * @return The xPath nodes
	 */
	Object[] evaluateAllRefsExpression(final String ditaContent) {
		try {
			XPathExpression xpath = XPathFactory.newInstance().newXPath()
					.compile(ReferencesTree.ALL_REFS_XPATH_EXPRESSION);
			NodeList nodeList = (NodeList) xpath.evaluate(new InputSource(new StringReader(ditaContent)),
					XPathConstants.NODESET);
			Object[] nodes = new Object[nodeList.getLength()];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = nodeList.item(i);
			}
			return nodes;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
