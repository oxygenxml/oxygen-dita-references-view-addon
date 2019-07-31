package com.oxygenxml.sdksamples.workspace;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

public interface NodeRange {

	String getAttributeValue(String attributeName);

	int[] getNodeOffsets(WSEditorPage editorPage);

}
