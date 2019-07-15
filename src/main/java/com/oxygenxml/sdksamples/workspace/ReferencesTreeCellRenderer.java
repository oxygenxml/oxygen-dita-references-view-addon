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

import com.oxygenxml.sdksamples.translator.DITAReferencesTranslator;
import com.oxygenxml.sdksamples.translator.Tags;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReferencesTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final Logger LOGGER = Logger.getLogger(ReferencesTreeCellRenderer.class);
	/**
	 * The translator of the DITA reference categories.
	 */
	private DITAReferencesTranslator translator = new DITAReferencesTranslator();

	/**
	 * The Image Icons for the reference categories.
	 */
	private ImageIcon imageIcon = null;
	private ImageIcon linkIcon = null;
	private ImageIcon contentIcon = null;
	private ImageIcon crossIcon = null;

	/**
	 * The constructor including the image icon for the categories.
	 * 
	 * @param pluginWorkspaceAccess
	 */
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
			// Attribute nodes
			String toDisplayString = null;
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof NodeRange) {
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

			} else // Reference categories nodes
			if (((DefaultMutableTreeNode) value).getUserObject() instanceof String) {
				label.setForeground(Color.blue);
				if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.IMAGE_REFERENCES)) {
					toDisplayString = translator.getTranslation(Tags.IMAGE_REFERENCES);
					label.setIcon(imageIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CROSS_REFERENCES)) {
					toDisplayString = translator.getTranslation(Tags.CROSS_REFERENCES);
					label.setIcon(crossIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CONTENT_REFERENCES)) {
					toDisplayString = translator.getTranslation(Tags.CONTENT_REFERENCES);
					label.setIcon(contentIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.RELATED_LINKS)) {
					toDisplayString = translator.getTranslation(Tags.RELATED_LINKS);
					label.setIcon(linkIcon);
				} else if (((DefaultMutableTreeNode) value).getUserObject()
						.equals(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE)) {
					toDisplayString = translator.getTranslation(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
				} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.NO_OUTGOING_REFERENCES_FOUND)) {
					toDisplayString = translator.getTranslation(Tags.NO_OUTGOING_REFERENCES_FOUND);
				}
			}
			label.setText(toDisplayString);
		}
		return label;
	}

	/**
	 * Display maximum 20 characters for each node value and set the toolTip with
	 * the full node value.
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
