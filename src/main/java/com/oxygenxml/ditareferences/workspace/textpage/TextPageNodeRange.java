package com.oxygenxml.ditareferences.workspace.textpage;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.oxygenxml.ditareferences.tree.references.NodeRange;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;

/**
 * A pair of DOM Element and its corresponding range in the Text Page.
 * 
 * @author Alexandra_Dinisor
 */
public class TextPageNodeRange extends NodeRange {

	/* The referencesTree Logger. */
	private static final Logger LOGGER = Logger.getLogger(TextPageNodeRange.class);

	/* The XML element. */
	private Element element;

	/* The range in the XML content */
	private WSXMLTextNodeRange range;

	/**
	 * Construct the TextPage NodeRange.
	 * 
	 * @param node  The node
	 * @param range The range.
	 */
	public TextPageNodeRange(Element node, WSXMLTextNodeRange range) {
		this.element = node;
		this.range = range;
	}

	/**
	 * Compute the offsets of the corresponding reference node in tree for the
	 * element in the textPage.
	 * 
	 * @param editorPage The XML textPage
	 * @return An array with start and end offsets of DOM node in XML TextPage
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
			LOGGER.debug(e, e);
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


	public String getNodeName() {
		return element.getNodeName();
	}

	public URL getEditorLocation() {
		try {
			return new URL(element.getBaseURI());
		} catch (MalformedURLException e) {
			return null;
		}
	}

}
