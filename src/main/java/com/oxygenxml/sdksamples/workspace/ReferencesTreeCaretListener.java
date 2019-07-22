package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;

public class ReferencesTreeCaretListener implements CaretListener, CaretSelectionInhibitor {

	private static final Logger LOGGER = Logger.getLogger(ReferencesTreeCaretListener.class);

	private WSXMLTextEditorPage textPage;
	private ReferencesTree refTree;

	private TreeSelectionInhibitor treeSelectionInhibitor;

	private static final int TIMER_DELAY = 600;
	private ActionListener timerListener = new TimerListener();
	private Timer updateCaretTimer = new Timer(TIMER_DELAY, timerListener);

	/**
	 * Set the inhibitor on false, by default.
	 */
	private boolean inhibitCaretSelectionListener = false;

	/**
	 * The constructor.
	 * 
	 * @param textPage           The XML textPage
	 * @param refTree            The references Tree
	 * @param selectionInhibitor The boolean for the selection
	 */
	public ReferencesTreeCaretListener(WSXMLTextEditorPage textPage, ReferencesTree refTree,
			TreeSelectionInhibitor selectionInhibitor) {
		this.updateCaretTimer.setRepeats(false);

		this.textPage = textPage;
		this.refTree = refTree;
		this.treeSelectionInhibitor = selectionInhibitor;
	}

	/**
	 * Caret updates with coalescing.
	 */
	public void caretUpdate(CaretEvent e) {
		if(!inhibitCaretSelectionListener) {
			updateCaretTimer.restart();
		} else {
			updateCaretTimer.stop();
		}
	}

	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Notify the caret about the change.
			searchForNodeMatchingCaret();
		}
	}

	/**
	 * Iterate in tree to find matching reference node for caret.
	 */
	private void searchForNodeMatchingCaret() {
		if (textPage.getCaretOffset() != 0) {
			int caretOffset = textPage.getCaretOffset();

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) refTree.getModel().getRoot();
			TreePath pathForSelectionInTree = visitAllNodes(refTree, new TreePath(root), caretOffset,
					textPage);

			if (pathForSelectionInTree != null) {
				treeSelectionInhibitor.setInhibitTreeSelectionListener(true);
				refTree.expandPath(pathForSelectionInTree);
				refTree.setSelectionPath(pathForSelectionInTree);
				treeSelectionInhibitor.setInhibitTreeSelectionListener(false);
			}

		}
	}

	/**
	 * Visit all the nodes recursively to find the matching one for caret.
	 * 
	 * @param tree        The Reference Tree
	 * @param parent      The path from parent
	 * @param caretOffset The caretOffset
	 * @param textPage    The XML textPage
	 * @return The TreePath for the selected Node
	 */
	private TreePath visitAllNodes(JTree tree, TreePath parent, int caretOffset,
			final WSXMLTextEditorPage textPage) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();

		// it must be value node
		if (node.getUserObject() instanceof NodeRange) {

			NodeRange nodeRangeElem = (NodeRange) node.getUserObject();
			int[] nodeOffsets = nodeRangeElem.getNodeOffsets(textPage);
			int startNodeOffset = nodeOffsets[0];
			int endNodeOffset = nodeOffsets[1];

			if (startNodeOffset <= caretOffset && caretOffset <= endNodeOffset) {
				// Found the node at the corresponding caret
				TreePath currentPath = parent;
				return currentPath;
			}
		}

		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) e.nextElement();
				TreePath foundPath = visitAllNodes(tree, parent.pathByAddingChild(nextNode), caretOffset,
						textPage);

				if (foundPath != null) {
					// Found the node in the deeper recursion
					return foundPath;
				}
			}
		}
		return null;
	}
	
	/**
	 * Implement the InhibitorCaretSelectionInhibitor interface method.
	 * 
	 * @param inhibitCaretSelectionListener
	 */
	public void setInhibitCaretSelectionListener(boolean inhibitCaretSelectionListener) {
		this.inhibitCaretSelectionListener = inhibitCaretSelectionListener;

	}

}
