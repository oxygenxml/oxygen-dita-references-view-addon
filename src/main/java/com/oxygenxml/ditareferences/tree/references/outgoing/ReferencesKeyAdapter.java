package com.oxygenxml.ditareferences.tree.references.outgoing;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;
import com.oxygenxml.ditareferences.workspace.KeysProvider;
import com.oxygenxml.ditareferences.workspace.ShowDefinitionLocationAction;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Key Adapter for Enter when opening reference, Context Menu button to open
 * popUp Menu.
 * 
 * @Alexandra_Dinisor
 *
 */
public class ReferencesKeyAdapter extends KeyAdapter {

	/**
	 * The tree.
	 */
	private OutgoingReferencesTree refTree;
	
	/**
	 * The workspace access.
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	
	/**
	 * EditorAccess.
	 */
	private WSEditor editorAccess;

	/**
	 * Keys Provider.
	 */
	private KeysProvider keysProvider;
	
	/**
	 * For translation.
	 */
	private static final Translator TRANSLATOR = DITAReferencesTranslator.getInstance();

	
	/**
	 * Construct the Key Adapter.
	 * 
	 * @param refTree               The ReferencesTree
	 * @param pluginWorkspaceAccess The PluginWorkspaceAccess
	 * @param keysProvider          The KeysProvider
	 */
	public ReferencesKeyAdapter(OutgoingReferencesTree refTree, StandalonePluginWorkspace pluginWorkspaceAccess,
			KeysProvider keysProvider) {
		super();
		this.refTree = refTree;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.keysProvider = keysProvider;
	}

	/**
	 * Set the editorAccess.
	 * 
	 * @param editorAccess The editorAccess
	 */
	public void setEditorAccess(WSEditor editorAccess) {
		this.editorAccess = editorAccess;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			handleOpenReference();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
			handleContextMenuButton(TRANSLATOR.getTranslation(Tags.OPEN_REFERENCE),
					TRANSLATOR.getTranslation(Tags.SHOW_DEFINITION_LOCATION));
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
						menu.add(new ShowDefinitionLocationAction((RelLinkNodeRange) node.getUserObject(),
								pluginWorkspaceAccess, showDefLocationName));
					}

					int rowForSelectedPath = refTree.getRowForPath(currentSelectedPath);
					if (rowForSelectedPath >= 0) {
						Rectangle rowBounds = refTree.getRowBounds(rowForSelectedPath);
						menu.show(refTree, rowBounds.width, rowBounds.y);
					}
				}
			}
		}

	}

	/**
	 * Enable the reference opening when Enter Key is pressed after a Leaf Node is
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
