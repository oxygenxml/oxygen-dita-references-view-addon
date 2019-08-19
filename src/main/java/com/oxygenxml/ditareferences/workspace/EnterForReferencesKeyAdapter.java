package com.oxygenxml.ditareferences.workspace;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class EnterForReferencesKeyAdapter extends KeyAdapter {

	private ReferencesTree refTree;
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private WSEditor editorAccess;

	/**
	 * Set the editorAccess.
	 * 
	 * @param editorAccess The editorAccess
	 */
	public void setEditorAccess(WSEditor editorAccess) {
		this.editorAccess = editorAccess;
	}

	private KeysProvider keysProvider;

	/**
	 * Construct the Key Adapter.
	 * 
	 * @param refTree               The ReferencesTree
	 * @param pluginWorkspaceAccess The pluginWorkspaceAccess
	 * @param keysProvider          The keysProvider
	 * @param editorAccess          The editorAccess
	 */
	public EnterForReferencesKeyAdapter(ReferencesTree refTree, StandalonePluginWorkspace pluginWorkspaceAccess,
			KeysProvider keysProvider) {
		super();
		this.refTree = refTree;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.keysProvider = keysProvider;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			handleEnterKey();
		}
	}

	/**
	 * Enable the reference opening when ENTER Key is pressed after a Leaf Node is
	 * selected.
	 */
	private void handleEnterKey() {
		TreePath currentLocationPath = refTree.getSelectionPath();
		if (currentLocationPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentLocationPath.getLastPathComponent();

			// it must be Leaf Node
			if (node.getUserObject() instanceof NodeRange) {
				new OpenReferenceAction((NodeRange) node.getUserObject(), editorAccess, pluginWorkspaceAccess,
						keysProvider).actionPerformed(null);
			}
		}
	}

}
