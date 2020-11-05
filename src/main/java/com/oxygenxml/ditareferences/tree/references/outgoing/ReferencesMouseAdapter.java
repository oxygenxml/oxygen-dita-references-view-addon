package com.oxygenxml.ditareferences.tree.references.outgoing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;
import com.oxygenxml.ditareferences.workspace.KeysProvider;
import com.oxygenxml.ditareferences.workspace.ShowDefinitionLocationAction;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReferencesMouseAdapter extends MouseAdapter {

	private OutgoingReferencesTree refTree;
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private WSEditor editorAccess;

	public void setEditorAccess(WSEditor editorAccess) {
		this.editorAccess = editorAccess;
	}

	/* The translator of the PopUpMenu item. */
	private Translator translator;

	private KeysProvider keysProvider;

	/**
	 * The Mouse Adapter constructor.
	 * 
	 * @param refTree               The ReferencesTree
	 * @param pluginWorkspaceAccess The pluginWorkspaceAccess
	 * @param keysProvider          The keysProvider
	 * @param editorAccess          The editorAccess
	 */
	public ReferencesMouseAdapter(OutgoingReferencesTree refTree, StandalonePluginWorkspace pluginWorkspaceAccess,
			KeysProvider keysProvider, Translator translator) {
		super();
		this.refTree = refTree;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.keysProvider = keysProvider;
		this.translator = translator;
	}

	@Override
	public void mouseReleased(MouseEvent releasedEvent) {
		handleContextMenu(releasedEvent);
	}
	
	@Override
	public void mousePressed(MouseEvent releasedEvent) {
		handleContextMenu(releasedEvent);
	}

	@Override
	public void mouseClicked(MouseEvent mouseClickedEvent) {
		if (mouseClickedEvent.isPopupTrigger()) {
			handleContextMenu(mouseClickedEvent);
		} else if (mouseClickedEvent.getClickCount() == 2) {
			processDoubleClick(mouseClickedEvent);
		}
	}

	private void handleContextMenu(MouseEvent releasedEvent) {
		if (releasedEvent.isPopupTrigger()) {
			processRightClick(releasedEvent, 
					translator.getTranslation(Tags.OPEN_REFERENCE),
					translator.getTranslation(Tags.SHOW_DEFINITION_LOCATION));
		}
	}

	/**
	 * Enable "Open Reference" in PopupMenu when right clicked.
	 */
	private void processRightClick(MouseEvent event, String openRefName, String showDefLocationName) {
		refTree.requestFocus();
		TreePath currentLocationPath = refTree.getPathForLocation(event.getX(), event.getY());
		if (currentLocationPath != null) {
			refTree.getSelectionModel().setSelectionPath(currentLocationPath);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentLocationPath.getLastPathComponent();

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
					menu.show(refTree, event.getX(), event.getY());
				}
			}
		}
	}

	/**
	 * Enable the reference opening when double clicked.
	 */
	private void processDoubleClick(MouseEvent event) {
		TreePath currentLocationPath = refTree.getPathForLocation(event.getX(), event.getY());
		if (isPathSelected(currentLocationPath)) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentLocationPath.getLastPathComponent();

				// it must be Leaf Node
				if (node.getUserObject() instanceof NodeRange) {
					new OpenReferenceAction((NodeRange) node.getUserObject(), editorAccess, pluginWorkspaceAccess,
							keysProvider).actionPerformed(null);
				}
			}
		}

	
	/**
	 * Check if path is selected in ReferencesTree.
	 * 
	 * @param currentLocationPath The current locationPath
	 * @return true is path is selected
	 */
	private boolean isPathSelected(TreePath currentLocationPath) {
		return (currentLocationPath != null && refTree.getSelectionModel().getSelectionPath() != null);
	}

}
