package com.oxygenxml.sdksamples.workspace;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * Interface of text /author reference nodes in ReferencesTree.
 * 
 * @author Alexandra_Dinisor
 *
 */
public interface NodeRange {

	/**
	 * Get the attribute value, returns <code>null</code> if there is no such
	 * attribute.
	 * 
	 * @param attributeName The attributeName
	 * @return the attribute Value or <code>null</code>
	 */
	String getAttributeValue(String attributeName);


	/**
	 * Compute the offsets of the corresponding reference node in tree for the
	 * element in the textPage.
	 * 
	 * @param editorPage The EditorPage: Text/Author
	 * @return An array with start and end offsets of Element in Text/Author Page
	 */
	int[] getNodeOffsets(WSEditorPage editorPage);

	/**
	 * Get the nodeName.
	 * 
	 * @return the nodeName
	 */
	String getNodeName();

}
