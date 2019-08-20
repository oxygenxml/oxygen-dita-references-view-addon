package com.oxygenxml.ditareferences.workspace;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.translator.Tags;
import com.oxygenxml.ditareferences.translator.Translator;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Adapter when pressing Enter Key to open the reference.
 * 
 * @Alexandra_Dinisor
 *
 */
public class ReferencesKeyAdapter extends KeyAdapter {

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
	private Translator translator;

	/**
	 * Construct the Key Adapter.
	 * 
	 * @param refTree               The ReferencesTree
	 * @param pluginWorkspaceAccess The pluginWorkspaceAccess
	 * @param keysProvider          The keysProvider
	 * @param editorAccess          The editorAccess
	 */
	public ReferencesKeyAdapter(ReferencesTree refTree, StandalonePluginWorkspace pluginWorkspaceAccess,
			KeysProvider keysProvider, Translator translator) {
		super();
		this.refTree = refTree;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.keysProvider = keysProvider;
		this.translator = translator;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			handleOpenReference();
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
			handleContextMenuButton(translator.getTranslation(Tags.OPEN_REFERENCE),
					translator.getTranslation(Tags.SHOW_DEFINITION_LOCATION));
		}
	}

	/**
	 * Enable the opening of context menu when using the Context Menu button from keyboard.
	 */
	private void handleContextMenuButton(String openRefName, String showDefLocationName) {
		refTree.requestFocus();
		TreePath currentSelectedPath = refTree.getSelectionPath();
		if (currentSelectedPath != null) {			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentSelectedPath.getLastPathComponent();

			// it must be Leaf Node
			if (node.getUserObject() instanceof NodeRange) {
				JPopupMenu menu = new JPopupMenu();
				if (openRefName != null) {
					menu.add(new OpenReferenceAction((NodeRange) node.getUserObject(), editorAccess,
							pluginWorkspaceAccess, keysProvider, openRefName));

					// add "Show Definition Location" item in PopUpMenu
					if (node.getUserObject() instanceof RelLinkNodeRange) {
						menu.add(new ShowDefinitionLocationAction((RelLinkNodeRange) node.getUserObject(), editorAccess,
								pluginWorkspaceAccess, showDefLocationName));
					}

					int rowForSelectedPath = refTree.getRowForPath(currentSelectedPath);
					if (rowForSelectedPath >= 0) {
						Rectangle rowBounds = refTree.getRowBounds(rowForSelectedPath);
						menu.show(refTree, rowBounds.width, rowBounds.height);
					}
				}
			}
		}

	}

	/**
	 * Enable the reference opening when ENTER Key is pressed after a Leaf Node is
	 * selected.
	 */
	private void handleOpenReference() {
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
