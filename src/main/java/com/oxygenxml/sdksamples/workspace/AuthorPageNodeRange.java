package com.oxygenxml.sdksamples.workspace;

import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * 
 * @author Alexandra_Dinisor
 *
 */
public class AuthorPageNodeRange implements NodeRange {

	/**
	 * The Author element
	 */
	private AuthorElement element;

	public String getAttributeValue(String attributeName) {
		AttrValue attributeValue = element.getAttribute(attributeName);
		return attributeValue != null ? attributeValue.getValue() : null;
	}

	public int[] getNodeOffsets(WSEditorPage editorPage) {
		return new int[] { element.getStartOffset(), element.getEndOffset() };
	}
}
