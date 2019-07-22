package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;

public class ReferencesTreeSelectionListener implements TreeSelectionListener, TreeSelectionInhibitor {
	private static final Logger LOGGER = Logger.getLogger(ReferencesTreeSelectionListener.class);

	private ReferencesTree refTree;

	/**
	 * Set the inhibitor on false, by default.
	 */
	private boolean inhibitTreeSelectionListener = false;

	private CaretSelectionInhibitor caretSelectionInhibitor;

	/**
	 * Coalescing for selecting the matching element in text page
	 */
	private static final int TIMER_DELAY = 500;
	private ActionListener timerTreeListener = new TimerTreeListener();
	private Timer updateTreeTimer = new Timer(TIMER_DELAY, timerTreeListener);

	public ReferencesTreeSelectionListener(ReferencesTree refTree) {
		this.updateTreeTimer.setRepeats(false);
		this.refTree = refTree;
	}

	public void setCaretSelectionInhibitor(CaretSelectionInhibitor caretSelectionInhibitor) {
		this.caretSelectionInhibitor = caretSelectionInhibitor;
	}

	private class TimerTreeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Notify the tree about the change.
			selectReferenceElementInTextPage();
		}
	}

	/**
	 * Tree updates with coalescing.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (!inhibitTreeSelectionListener) {
			updateTreeTimer.restart();
		} else {
			updateTreeTimer.stop();
		}
	}

	/**
	 * Select the matching Reference Element in the TextPage.
	 */
	private void selectReferenceElementInTextPage() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) refTree.getLastSelectedPathComponent();
		// if nothing is selected
		if (node == null) {
			return;
		} else {
			// retrieve the node that was selected
			if (this.refTree.getEditorAccess() != null) {
				if (this.refTree.getEditorAccess().getCurrentPage() != null) {
					WSXMLTextEditorPage xmlTextPage = (WSXMLTextEditorPage) refTree.getEditorAccess().getCurrentPage();

					// if node is a reference element
					if (node.getUserObject() instanceof NodeRange) {
						NodeRange range = (NodeRange) node.getUserObject();
						int[] nodeOffsets = range.getNodeOffsets(xmlTextPage);
						int startOffset = nodeOffsets[0];
						int endOffset = nodeOffsets[1];

						// select in editor the specified node from refTree
						caretSelectionInhibitor.setInhibitCaretSelectionListener(true);
						xmlTextPage.select(startOffset, endOffset);
						caretSelectionInhibitor.setInhibitCaretSelectionListener(false);
					}
				}
			} else {
				System.err.println("EDITOR NULL");
			}
		}
	}

	/**
	 * Implement the TreeSelectionInhibitor interface method.
	 * 
	 * @param inhibitTreeSelectionListener
	 */
	public void setInhibitTreeSelectionListener(boolean inhibitTreeSelectionListener) {
		this.inhibitTreeSelectionListener = inhibitTreeSelectionListener;

	}

}
