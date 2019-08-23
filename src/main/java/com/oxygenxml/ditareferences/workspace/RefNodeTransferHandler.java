package com.oxygenxml.ditareferences.workspace;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * TransferHandler to copy the text of references node.
 * 
 * @Alexandra_Dinisor
 *
 */
@SuppressWarnings("serial")
public class RefNodeTransferHandler extends TransferHandler {

	/**
	 * Copy the node text shown by its renderer label when the node is selected in
	 * the referencesTree.
	 */
	@Override
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
		if (action == TransferHandler.COPY) {
			if (comp instanceof ReferencesTree) {
				ReferencesTree refTree = (ReferencesTree) comp;
				TreePath selectedPath = refTree.getSelectionPath();
				int selectedRow = refTree.getRowForPath(selectedPath);
				DefaultMutableTreeNode refNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

				// label of selected node in referencesTree
				JLabel label = (JLabel) refTree.getCellRenderer().getTreeCellRendererComponent(refTree, refNode, true,
						true, true, selectedRow, true);

				Transferable t = new StringSelection(label.getText());
				try {
					clip.setContents(t, null);
					exportDone(comp, t, action);					
				} catch (IllegalStateException ise) {
					exportDone(comp, t, NONE);
					throw ise;
				}
			} else {
				super.exportToClipboard(comp, clip, action);
			}
		} else {
			super.exportToClipboard(comp, clip, action);
		}
	}
}
