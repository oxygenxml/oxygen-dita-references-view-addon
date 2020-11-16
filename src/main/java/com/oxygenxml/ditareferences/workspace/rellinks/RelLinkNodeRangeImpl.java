package com.oxygenxml.ditareferences.workspace.rellinks;

import java.net.URL;

import com.oxygenxml.ditareferences.workspace.DITAConstants;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * Implementation for RelLinkNodeRange.
 * 
 * @Alexandra_Dinisor
 *
 */
public class RelLinkNodeRangeImpl extends RelLinkNodeRange {

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

	public String getAttributeValue(String attributeName) {
		if (DITAConstants.CLASS.equals(attributeName)) {
			return DITAConstants.ONLY_LINK_CLASS;
		} else if (DITAConstants.HREF.equals(attributeName)) {
			return relLink.getTargetURL().toString();
		} else if (DITAConstants.FORMAT.equals(attributeName)) {
			return relLink.getTargetFormat();
		} else if (DITAConstants.SCOPE.equals(attributeName)) {
			return relLink.getTargetScope();
		}
		return null;
	}

	public int[] getNodeOffsets(WSEditorPage editorPage) {
		return new int[0];
	}

	public String getNodeName() {
		return DITAConstants.LINK_NAME;
	}

	@Override
	public URL getTargetDefinitionLocation() {
		return relLink.getTargetDefinitionLocation();
	}

	public URL getEditorLocation() {
		return null;
	}

}
