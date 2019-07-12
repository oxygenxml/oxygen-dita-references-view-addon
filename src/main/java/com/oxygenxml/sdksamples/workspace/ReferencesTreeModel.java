package com.oxygenxml.sdksamples.workspace;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


class ReferencesTreeModel implements TreeModel {

	private DefaultMutableTreeNode root;
	private EventListenerList listenerList = new EventListenerList();

	public ReferencesTreeModel() {
		this.root = null;
	}

	public Object getRoot() {
		return root;
	}

	public void setRoot(Object node) {
		this.root = (DefaultMutableTreeNode) node;
	}

	public Object getChild(Object parent, int index) {
		return ((DefaultMutableTreeNode) parent).getChildAt(index);
	}

	public int getChildCount(Object parent) {
		return ((DefaultMutableTreeNode) parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		int n = getChildCount(parent);
		for (int i = 0; i < n; i++) {
			if (getChild(parent, i).equals(child)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isLeaf(Object node) {
		if (getChildCount(node) == 0) {
			return true;
		}
		return false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);

	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

}