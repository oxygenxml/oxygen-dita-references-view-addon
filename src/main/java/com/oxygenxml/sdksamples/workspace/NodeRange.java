package com.oxygenxml.sdksamples.workspace;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * Interface of text /author reference nodes in ReferencesTree.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface NodeRange {

	String getAttributeValue(String attributeName);

	int[] getNodeOffsets(WSEditorPage editorPage);
	
	String getNodeName();

}
