package com.oxygenxml.ditareferences.treeReferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.function.Supplier;

import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

public abstract class ReferencesTreeCaretListener<T extends WSEditorPage> implements CaretSelectionInhibitor {

	protected Supplier<T> editorPage;
	private ReferencesTree refTree;

	private TreeSelectionInhibitor treeSelectionInhibitor;

	private static final int TIMER_DELAY = 600;
	private ActionListener timerListener = new CaretTimerListener();
	private Timer updateCaretTimer = new Timer(TIMER_DELAY, timerListener);

	/* Set the inhibiter on false by default. */
	private boolean inhibitCaretSelectionListener = false;

	/**
	 * Construct the CaretListener for Text/Author.
	 * 
	 * @param textPage           The XML textPage
	 * @param refTree            The references Tree
	 * @param selectionInhibitor The boolean for the selection
	 */
	public ReferencesTreeCaretListener(Supplier<T> editorPage, ReferencesTree refTree,
			TreeSelectionInhibitor selectionInhibitor) {
		this.updateCaretTimer.setRepeats(false);

		this.editorPage = editorPage;
		this.refTree = refTree;
		this.treeSelectionInhibitor = selectionInhibitor;
	}

	/**
	 * Caret updates with coalescing.
	 */
	public void caretUpdate() {
		if (!inhibitCaretSelectionListener) {
			updateCaretTimer.restart();
		} else {
			updateCaretTimer.stop();
		}
	}

	/**
	 * Listener for Caret Updates in TextPage /AuthorPage.
	 * 
	 * @author Alexandra_Dinisor
	 *
	 */
	private class CaretTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Notify the caret about the change.
			searchForNodeMatchingCaret();
		}

		/**
		 * Search and select the treeNode corresponding the caret in Editor.
		 */
		private void searchForNodeMatchingCaret() {
			if (getCaretOffset() > 0) {
				int caretOffset = getCaretOffset();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) refTree.getModel().getRoot();
				TreePath pathForSelectionInTree = visitAllNodes(refTree, new TreePath(root), caretOffset,
						editorPage.get());

				// select the returned path matching the caret
				if (pathForSelectionInTree != null) {
					treeSelectionInhibitor.setInhibitTreeSelectionListener(true);
					refTree.expandPath(pathForSelectionInTree);
					refTree.setSelectionPath(pathForSelectionInTree);
					treeSelectionInhibitor.setInhibitTreeSelectionListener(false);
				}
			}
		}

	}

	/**
	 * Get the caret offset for TextPage / AuthorPage.
	 * 
	 * @return the Caret Offset
	 */
	protected abstract int getCaretOffset();

	/**
	 * Visit all the reference nodes recursively to find the matching one for caret.
	 * 
	 * @param tree        The Reference Tree
	 * @param parent      The path from parent
	 * @param caretOffset The caretOffset
	 * @param textPage    The XML textPage
	 * @return the TreePath for the selected Node
	 */
	@SuppressWarnings("rawtypes")
  private TreePath visitAllNodes(JTree tree, TreePath parent, int caretOffset, final WSEditorPage page) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();

		// it must be value node
		if (node.getUserObject() instanceof NodeRange) {
			NodeRange nodeRangeElem = (NodeRange) node.getUserObject();

			// get node offsets corresponding the caret
			int[] nodeOffsets = nodeRangeElem.getNodeOffsets(page);
			if(nodeOffsets != null) {
				int startNodeOffset = nodeOffsets[0];
				int endNodeOffset = nodeOffsets[1];

				if (startNodeOffset <= caretOffset && caretOffset <= endNodeOffset) {
					// Found the node at the corresponding caret
					return parent;
				}
			}
		}

		// search for the matching node among current node's children
		if (node.getChildCount() > 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) e.nextElement();
				TreePath foundPath = visitAllNodes(tree, parent.pathByAddingChild(nextNode), caretOffset, page);

				if (foundPath != null) {
					// Found the node in the deeper recursion
					return foundPath;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Set the InhibitorCaretSelectionListener.
	 * 
	 * @param inhibitCaretSelectionListener
	 */
	@Override
	public void setInhibitCaretSelectionListener(boolean inhibitCaretSelectionListener) {
		this.inhibitCaretSelectionListener = inhibitCaretSelectionListener;

	}

}
