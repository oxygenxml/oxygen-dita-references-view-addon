package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;

/**
 * A pair of DOM Element and it's corresponding range in the text page.
 * 
 * @author Alexandra Dinisor
 */
public class TextPageNodeRange implements NodeRange{
	/**
	 * The referencesTree Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(NodeRange.class);
	/**
	 * The XML element.
	 */
	private Element element;

	/**
	 * The range in the XML content
	 */
	private WSXMLTextNodeRange range;

	/**
	 * The node range.
	 * 
	 * 
	 * @param node  The node
	 * @param range The range.
	 */
	public TextPageNodeRange(Element node, WSXMLTextNodeRange range) {
		this.element = node;
		this.range = range;
	}

	/**
	 * Compute the offsets of the node from the XML textPage.
	 * 
	 * @param range       The node ranges
	 * @param xmlTextPage The XML textPage
	 * @return An array with the start and end offsets of the node in XML textPage
	 */
	public int[] getNodeOffsets(WSEditorPage editorPage) {
		WSXMLTextEditorPage xmlTextPage = (WSXMLTextEditorPage) editorPage;
		int[] finalOffsets = new int[2];
		try {
			int startLine = range.getStartLine();
			int startColumn = range.getStartColumn();
			int endLine = range.getEndLine();
			int endColumn = range.getEndColumn();
			// the startOffset
			finalOffsets[0] = xmlTextPage.getOffsetOfLineStart(startLine) + startColumn - 1;
			// the endOffset
			finalOffsets[1] = xmlTextPage.getOffsetOfLineStart(endLine) + endColumn - 1;
			return finalOffsets;

		} catch (BadLocationException e) {
			e.printStackTrace();
			LOGGER.error(e, e);
		}
		return finalOffsets;
	}

	/**
	 * Get the attribute value, returns <code>null</code> if there is no such
	 * attribute.
	 * 
	 * @param attributeName The attribute name
	 * @return the attribute value or <code>null</code>
	 */
	public String getAttributeValue(String attributeName) {
		if (element != null) {
			NamedNodeMap attrs = element.getAttributes();
			if (attrs != null) {
				Node attr = attrs.getNamedItem(attributeName);
				if (attr != null) {
					return attr.getNodeValue();
				}
			}
		}
		return null;
	}

}
