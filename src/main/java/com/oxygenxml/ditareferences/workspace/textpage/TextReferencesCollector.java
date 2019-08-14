/**
 * 
 */
package com.oxygenxml.ditareferences.workspace.textpage;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.oxygenxml.ditareferences.workspace.NodeRange;
import com.oxygenxml.ditareferences.workspace.ReferencesCollector;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Evaluate the XPath expression and get the Nodes and its NodeRanges.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class TextReferencesCollector extends ReferencesCollector {

	@Override
	protected List<NodeRange> collect(WSEditorPage page) throws XPathException {
		WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) page;
		List<NodeRange> ranges = new ArrayList<NodeRange>();

		Object[] referenceNodes = textPage.evaluateXPath(ALL_REFS_XPATH_EXPRESSION);
		WSXMLTextNodeRange[] referenceTextNodeRanges = textPage
				.findElementsByXPath(ALL_REFS_XPATH_EXPRESSION);

		// get all pairs (Element, its corresponding NodeRange)
		if (referenceNodes != null) {
			for (int i = 0; i < referenceNodes.length; i++) {
				Element currentElement = (Element) referenceNodes[i];
				NodeRange refRange = new TextPageNodeRange(currentElement, referenceTextNodeRanges[i]);
				ranges.add(refRange);
			}
		}

		return ranges;
	}
}
