package com.oxygenxml.ditareferences.tree.references.outgoing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

public class ReferencesTreeSelectionListener implements TreeSelectionListener, TreeSelectionInhibitor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferencesTreeSelectionListener.class);

	/* The referencesTree. */
	private OutgoingReferencesTree refTree;

	/* Set the inhibiter on false, by default. */
	private boolean inhibitTreeSelectionListener = false;

	private CaretSelectionInhibitor caretSelectionInhibitor;

	/*
	 * Coalescing for selecting the matching element from the referencesTree in
	 * textPage.
	 */
	private static final int TIMER_DELAY = 500;
	private ActionListener selectionTreeTimerListener = new SelectionTreeTimerListener();
	private Timer updateTreeTimer = new Timer(TIMER_DELAY, selectionTreeTimerListener);

	/**
	 * Construct the ReferencesTreeSelectionListener.
	 * 
	 * @param refTree The ReferencesTree
	 */
	public ReferencesTreeSelectionListener(OutgoingReferencesTree refTree) {
		this.updateTreeTimer.setRepeats(false);
		this.refTree = refTree;
	}

	/**
	 * Set the caretSelectionInhibitor.
	 * 
	 * @param caretSelectionInhibitor The caretSelectionInhibitor
	 */
	public void setCaretSelectionInhibitor(CaretSelectionInhibitor caretSelectionInhibitor) {
		this.caretSelectionInhibitor = caretSelectionInhibitor;
	}

	/**
	 * Timer Listener when selecting a node in References Tree.
	 * 
	 * @author Alexandra_Dinisor
	 *
	 */
	private class SelectionTreeTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Notify the tree about selection change of the treeNode
			selectReferenceElementInEditorPage();
		}

		/**
		 * Select the matching Reference Element in the TextPage / AuthorPage.
		 */
		private void selectReferenceElementInEditorPage() {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) refTree.getLastSelectedPathComponent();
			// if nothing is selected
			if (node != null) {
				// retrieve the node that was selected
				if (refTree.getEditorAccess() != null) {
					if (isLeafNode(node)) {
						NodeRange range = (NodeRange) node.getUserObject();
						WSEditorPage editorPage = refTree.getEditorAccess().getCurrentPage();
						int[] nodeOffsets = range.getNodeOffsets(editorPage);
						if(nodeOffsets != null) {
							int startOffset = nodeOffsets[0];
							int endOffset = nodeOffsets[1];

							caretSelectionInhibitor.setInhibitCaretSelectionListener(true);
							// select in editorPage the corresponding reference Element
							selectRange(editorPage, startOffset, endOffset);
							caretSelectionInhibitor.setInhibitCaretSelectionListener(false);
						}
					}
				} else {
					LOGGER.debug("EDITOR NULL");
				}
			}
		}
		
		/**
		 * Check for valid leaf node in the current editorAccess.
		 * 
		 * @param node The leaf node
		 * @return true if node is a leaf node from current editoAccess
		 */
		private boolean isLeafNode(DefaultMutableTreeNode node) {
			return refTree.getEditorAccess().getCurrentPage() != null && node.getUserObject() instanceof NodeRange;
		}

		/**
		 * Select the corresponding Element in Editor.
		 * 
		 * @param page        Text / Author Page
		 * @param startOffset Start Offset to be selected
		 * @param endOffset   End Offset to be selected
		 */
		private void selectRange(WSEditorPage page, int startOffset, int endOffset) {
			if (page instanceof WSTextEditorPage) {
				((WSTextEditorPage) page).select(startOffset, endOffset);
			} else {
				((WSAuthorEditorPage) page).select(startOffset, endOffset);
				((WSAuthorEditorPage) page).scrollCaretToVisible();
			}
		}
	}

	/**
	 * Update Tree with coalescing.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (!inhibitTreeSelectionListener) {
			updateTreeTimer.restart();
		} else {
			updateTreeTimer.stop();
		}
	}

	/**
	 * Implement the TreeSelectionInhibitor interface method.
	 * 
	 * @param inhibitTreeSelectionListener The inhibitTreeSelectionListener
	 */
	@Override
	public void setInhibitTreeSelectionListener(boolean inhibitTreeSelectionListener) {
		this.inhibitTreeSelectionListener = inhibitTreeSelectionListener;

	}

}
