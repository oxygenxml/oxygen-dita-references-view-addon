package com.oxygenxml.sdksamples.workspace;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReferencesTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final Logger LOGGER = Logger.getLogger(ReferencesTreeCellRenderer.class);

	private ImageIcon imageIcon = null;
	private ImageIcon linkIcon = null;
	private ImageIcon contentIcon = null;
	private ImageIcon crossIcon = null;

	public ReferencesTreeCellRenderer(StandalonePluginWorkspace pluginWorkspaceAccess) {
		super();

		URL imageUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementImage16.png");
		if (imageUrl != null) {
			this.imageIcon = (ImageIcon) pluginWorkspaceAccess.getImageUtilities().loadIcon(imageUrl);
		}

		URL crossUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementXref16.png");
		if (crossUrl != null) {
			this.crossIcon = (ImageIcon) pluginWorkspaceAccess.getImageUtilities().loadIcon(crossUrl);
		}

		URL contentUrl = StandalonePluginWorkspace.class.getResource("/images/Conref16.png");
		if (contentUrl != null) {
			this.contentIcon = (ImageIcon) pluginWorkspaceAccess.getImageUtilities().loadIcon(contentUrl);
		}

		URL linkUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementLink16.png");
		if (linkUrl != null) {
			this.linkIcon = (ImageIcon) pluginWorkspaceAccess.getImageUtilities().loadIcon(linkUrl);
		}

		//////////
//		NodeRange range = null;
//		WSXMLTextEditorPage xmlTextPage;
//		int startLine = range.getRange().getStartLine();
//		int column = range.getRange().getStartColumn();
////		xmlTextPage.geto
//		xmlTextPage.setCaretPosition(offset);
//		xmlTextPage.select(startOffset, endOffset);
		///////////
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		label.setIcon(null);

		if (value instanceof DefaultMutableTreeNode) {
			// attribute nodes
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof NodeRange) {
				String toDisplayString = null;
				Node node = ((NodeRange) ((DefaultMutableTreeNode) value).getUserObject()).getNode();

				if (node.getAttributes().getNamedItem("href") != null) {
					toDisplayString = setDisplayNodeValue(node, "href");
				} else if (node.getAttributes().getNamedItem("keyref") != null) {
					toDisplayString = setDisplayNodeValue(node, "keyref");
				} else if (node.getAttributes().getNamedItem("conref") != null) {
					toDisplayString = setDisplayNodeValue(node, "conref");
				} else if (node.getAttributes().getNamedItem("conkeyref") != null) {
					toDisplayString = setDisplayNodeValue(node, "conkeyref");
				}

				label.setText(toDisplayString);
			} else // Reference type modes
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof String) {
				label.setForeground(Color.blue);

				if (((DefaultMutableTreeNode) value).getUserObject().equals(ReferencesTree.IMAGE_REFERENCES)) {
					label.setIcon(imageIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(ReferencesTree.CROSS_REFERENCES)) {
					label.setIcon(crossIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(ReferencesTree.CONTENT_REFERENCES)) {
					label.setIcon(contentIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(ReferencesTree.RELATED_LINKS)) {
					label.setIcon(linkIcon);
				}

			}

		}

		return label;
	}

	/**
	 * 
	 * @param node The DOM node
	 * @return the displayed node text
	 */
	private String setDisplayNodeValue(Node node, String attributeString) {
		String toDisplayString = null;
		String nodeValue = node.getAttributes().getNamedItem(attributeString).getNodeValue();

		if (nodeValue.length() > 20) {
			toDisplayString = "... " + nodeValue.substring(nodeValue.length() - 20);
		} else {
			toDisplayString = nodeValue;
		}

		this.setToolTipText(nodeValue);
		return toDisplayString;
	}

}
