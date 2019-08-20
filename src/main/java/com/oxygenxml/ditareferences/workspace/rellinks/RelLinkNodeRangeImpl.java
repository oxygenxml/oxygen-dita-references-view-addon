package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * Implementation for RelLinkNodeRange.
 * 
 * @Alexandra_Dinisor
 *
 */
public class RelLinkNodeRangeImpl implements RelLinkNodeRange {

	/* The related link */
	private RelLink relLink;

	/**
	 * Construct the relLink nodeRange implementation.
	 * 
	 * @param relLink The relLink
	 */
	public RelLinkNodeRangeImpl(RelLink relLink) {
		this.relLink = relLink;
	}

	@Override
	public String getAttributeValue(String attributeName) {
		if("class".equals(attributeName)) {
			return "- topic/link ";
		} else if ("href".equals(attributeName)) {
			return relLink.getTargetURL().toString();
		} else if ("format".equals(attributeName)) {
			return relLink.getTargetFormat();
		} else if ("scope".equals(attributeName)) {
			return relLink.getTargetScope();
		} 
		return null;
	}

	@Override
	public int[] getNodeOffsets(WSEditorPage editorPage) {
		return null;
	}

	@Override
	public String getNodeName() {
		return "link";
	}

	@Override
	public URL getTargetDefinitionLocation() {
		return relLink.getTargetDefinitionLocation();
	}
}
