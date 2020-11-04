package com.oxygenxml.ditareferences.workspace.authorpage;

import java.net.URL;

import com.oxygenxml.ditareferences.tree.references.NodeRange;

import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * A pair of Author Element and its corresponding range in the Author Page.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class AuthorPageNodeRange extends NodeRange {

	/* The Author element. */
	private AuthorElement element;

	/**
	 * Construct the AuthorPage NodeRange.
	 * 
	 * @param authorNode The AuthorElement
	 */
	public AuthorPageNodeRange(AuthorElement authorNode) {
		element = authorNode;
	}

	@Override
	public String getAttributeValue(String attributeName) {
		AttrValue attributeValue = element.getAttribute(attributeName);
		return attributeValue != null ? attributeValue.getValue() : null;
	}

	@Override
	public int[] getNodeOffsets(WSEditorPage editorPage) {
		return new int[] { element.getStartOffset(), element.getEndOffset() };
	}

	@Override
	public String getNodeName() {
		return element.getDisplayName();
	}

	@Override
	public URL getEditorLocation() {
		return element.getXMLBaseURL();
	}

}
