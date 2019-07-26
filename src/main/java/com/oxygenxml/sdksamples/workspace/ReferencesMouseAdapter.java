package com.oxygenxml.sdksamples.workspace;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.sdksamples.translator.Tags;
import com.oxygenxml.sdksamples.translator.Translator;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReferencesMouseAdapter extends MouseAdapter {

	private ReferencesTree refTree;
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private WSEditor editorAccess;

	public void setEditorAccess(WSEditor editorAccess) {
		this.editorAccess = editorAccess;
	}

	/**
	 * The translator of the PopUpMenu item.
	 */
	private Translator translator;

	/**
	 * The Mouse Adapter constructor.
	 * 
	 * @param refTree               The reference Tree
	 * @param pluginWorkspaceAccess
	 * @param editorAccess
	 */
	public ReferencesMouseAdapter(ReferencesTree refTree, StandalonePluginWorkspace pluginWorkspaceAccess,
			Translator translator) {
		super();
		this.refTree = refTree;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.translator = translator;
	}

	@Override
	public void mousePressed(MouseEvent releasedEvent) {
		handleContextMenu(releasedEvent);
	}

	@Override
	public void mouseReleased(MouseEvent releasedEvent) {
		handleContextMenu(releasedEvent);
	}

	@Override
	public void mouseClicked(MouseEvent releasedEvent) {
		if (releasedEvent.isPopupTrigger()) {
			handleContextMenu(releasedEvent);
		} else if (releasedEvent.getClickCount() == 2) {
			processDoubleClick(releasedEvent);
		}
	}

	private void handleContextMenu(MouseEvent releasedEvent) {
		if (releasedEvent.isPopupTrigger()) {
			processRightClick(releasedEvent, translator.getTranslation(Tags.OPEN_REFERENCE));
		}
	}

	/**
	 * Enable "Open Reference" in PopupMenu when right clicked.
	 */
	private void processRightClick(MouseEvent event, String itemName) {
		refTree.requestFocus();
		TreePath currentLocationPath = refTree.getPathForLocation(event.getX(), event.getY());
		if (currentLocationPath != null) {
			refTree.getSelectionModel().setSelectionPath(currentLocationPath);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentLocationPath.getLastPathComponent();

			// it must be Element Node
			if (node.getUserObject() instanceof NodeRange) {
				JPopupMenu menu = new JPopupMenu();
				if (itemName != null) {
					menu.add(new NodeAction((NodeRange) node.getUserObject(), editorAccess, pluginWorkspaceAccess,
							itemName));
					menu.show(refTree, event.getX(), event.getY());
				}
			}
		}
	}

	/**
	 * Enable the reference opening when double clicked.
	 */
	private void processDoubleClick(MouseEvent event) {
		refTree.requestFocus();
		TreePath currentLocationPath = refTree.getPathForLocation(event.getX(), event.getY());
		if (currentLocationPath != null) {
			if (refTree.getSelectionModel().getSelectionPath() != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) currentLocationPath.getLastPathComponent();

				// it must be Element Node
				if (node.getUserObject() instanceof NodeRange) {
					new NodeAction((NodeRange) node.getUserObject(), editorAccess, pluginWorkspaceAccess)
							.actionPerformed(null);
				}
			}
		}
	}

}
