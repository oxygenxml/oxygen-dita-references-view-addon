package com.oxygenxml.sdksamples.workspace;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreePath;
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

public class SelectionTreeTest extends TestCase {

	private List<Integer> selectionOffsets = new ArrayList<Integer>();
	/**
	 * Test for selecting node in textPage after selecting a node in ReferencesTree.
	 */
	@Test
	public void testForSelectionInTree() {
		ReferencesTree tree = new ReferencesTree(new StandalonePluginWorkspaceAccessForTests(), new DITAReferencesTranslatorForTests());
		
		final String ditaContent = "<topic id=\"copyright\" class=\"- topic/topic \">\n" + 
				"    <title>Copyright</title>\n" + 
				"    <shortdesc>Legal-related information.</shortdesc>\n" + 
				"    <body>\n" + 
				"        <p>Most of the information was taken from <xref class='- topic/xref '\n" + "href=\"www.wikipedia.com\"\n" + 
				"                format=\"html\" scope=\"external\">Wikipedia</xref>, the free encyclopedia.</p>\n" + 
				"    </body>\n" + 
				"</topic>";
		
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
						return evaluateAllRefsExpression(ditaContent);
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

	
		tree.refreshReferenceTree(editor);	
		WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) editor.getCurrentPage();
	
		TreePath path = tree.getPathForRow(1);
		JLabel firstRowRenderLabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), true, true, true, 0, true);
		

		tree.setSelectionPath(path);
		tree.setSelectionRow(1);
		assertEquals("[4, 4]", selectionOffsets.toString());
			
	}
	
	/**
	 * Evaluate the xPath expression in case of a DITA topic.
	 * 
	 * @param ditaContent
	 * @return
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
