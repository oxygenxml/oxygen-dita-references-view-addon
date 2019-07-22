package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;

/**
 * A pair of DOM Element and it's corresponding range in the text page.
 * 
 * @author Alexandra Dinisor
 */
public class NodeRange {
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
	public NodeRange(Element node, WSXMLTextNodeRange range) {
		this.element = node;
		this.range = range;
	}

	public Element getNode() {
		return element;
	}

	/**
	 * Compute the offsets of the node from the XML textPage.
	 * 
	 * @param range       The node ranges
	 * @param xmlTextPage The XML textPage
	 * @return An array with the start and end offsets of the node in XML textPage
	 */
	public int[] getNodeOffsets(WSXMLTextEditorPage xmlTextPage) {
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
			LOGGER.debug(e, e);
		}
		return finalOffsets;
	}
}
