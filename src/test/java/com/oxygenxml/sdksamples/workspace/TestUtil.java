package com.oxygenxml.sdksamples.workspace;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class TestUtil {	

	/**
	 * Evaluate the xPath expression in case of a DITA topic.
	 * 
	 * @param ditaContent The DITA Content
	 * 
	 * @return The xPath nodes
	 */
	static Object[] evaluateAllRefsExpression(String ditaContent) {
		try {
			XPathExpression xpath = XPathFactory.newInstance().newXPath()
					.compile(ReferencesTree.ALL_REFS_XPATH_EXPRESSION);
			NodeList nodeList = (NodeList) xpath.evaluate(new InputSource(new StringReader(ditaContent)),
					XPathConstants.NODESET);
			Object[] nodes = new Object[nodeList.getLength()];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = nodeList.item(i);
			}
			return nodes;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Simulate double click in tree. This expects a node to be already selected.
	 * 
	 * @param tree The references tree.
	 */
	public static void simulateDoubleClick(JTree tree) {
		Rectangle rowBounds = tree.getRowBounds(tree.getSelectionRows()[0]);
		Point point = new Point(rowBounds.x, rowBounds.y);
		MouseEvent mouseEvent = new MouseEvent(tree, MouseEvent.MOUSE_CLICKED, 0, 0, point.x, point.y, 2, false);
		tree.dispatchEvent(mouseEvent);
	}

	/**
	 * Dumps tree information.
	 *
	 * @param tree The tree to dump info for.
	 */
	@SuppressWarnings("unused")
	public static String dumpTree(ReferencesTree tree) {
		StringBuilder dump = new StringBuilder();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		dumpRecursive((DefaultTreeModel) tree.getModel(), root, dump);

		return dump.toString();
	}

	/**
	 * Builds tree representation recursively.
	 *
	 * @param node Current processed node.
	 */
	private static void dumpRecursive(DefaultTreeModel model, DefaultMutableTreeNode node, StringBuilder dump) {
		dump.append("\n").append(Arrays.toString(model.getPathToRoot(node)));
		Enumeration<DefaultMutableTreeNode> children = node.children();
		while (children != null && children.hasMoreElements()) {
			dumpRecursive(model, children.nextElement(), dump);
		}
	}

	/**
	 * Dumps tree nodes as shown in the Oxygen editor.
	 * 
	 * @param tree The ReferencesTree
	 */
	public static String logTreeNodes(ReferencesTree tree) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath path = tree.getPathForRow(i);
			JLabel label = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(
					tree, path.getLastPathComponent(), false, true, true, i, false);
			stringBuilder.append(label.getText()).append("\n");
		}
		return stringBuilder.toString();
	}
}
