package com.oxygenxml.sdksamples.workspace;

import org.w3c.dom.Element;

import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;

public class NodeRange {

	/**
	 * The node
	 */
	private Element node;
	
	/**
	 * The range in the XML content
	 */
	private WSXMLTextNodeRange range;
	
	/**
	 * The node range.
	 * 
	 * 
	 * @param node The node
	 * @param range The range.
	 */
	public NodeRange(Element node, WSXMLTextNodeRange range) {
		this.node = node;
		this.range = range;
	}
	
	public Element getNode() {
		return node;
	}
	
	public WSXMLTextNodeRange getRange() {
		return range;
	}
}
