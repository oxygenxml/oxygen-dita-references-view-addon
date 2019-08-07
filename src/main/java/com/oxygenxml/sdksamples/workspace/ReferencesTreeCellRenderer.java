package com.oxygenxml.sdksamples.workspace;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.oxygenxml.sdksamples.translator.Tags;
import com.oxygenxml.sdksamples.translator.Translator;

import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;

@SuppressWarnings("serial")
public class ReferencesTreeCellRenderer extends TreeCellRenderer {

	/**
	 * The translator of the DITA reference categories.
	 */
	private Translator translator;
	
	/**
	 * Icons for leaf nodes.
	 */
	private ImageIcon imageIcon = null;
	private ImageIcon linkIcon = null;
	private ImageIcon contentIcon = null;
	private ImageIcon crossIcon = null;

	/**
	 * Icons for reference categories depending on status: expanded/collapsed.
	 */
	private ImageIcon collapsedIcon = null;
	private ImageIcon expandedIcon = null;

	/**
	 * Construct Renderer by including the icons for leaf nodes and icon for
	 * references categories corresponding their status: expanded/collapsed. A
	 * translator for references categories.
	 * 
	 * @param imageUtilities The imageUtilities
	 * @param translator     The translator for categories
	 */
	public ReferencesTreeCellRenderer(ImageUtilities imageUtilities, Translator translator) {
		super();
		this.translator = translator;

		URL imageUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementImage16.png");
		if (imageUrl != null) {
			this.imageIcon = (ImageIcon) imageUtilities.loadIcon(imageUrl);
		}

		URL crossUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementXref16.png");
		if (crossUrl != null) {
			this.crossIcon = (ImageIcon) imageUtilities.loadIcon(crossUrl);
		}

		URL contentUrl = StandalonePluginWorkspace.class.getResource("/images/Conref16.png");
		if (contentUrl != null) {
			this.contentIcon = (ImageIcon) imageUtilities.loadIcon(contentUrl);
		}

		URL linkUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementLink16.png");
		if (linkUrl != null) {
			this.linkIcon = (ImageIcon) imageUtilities.loadIcon(linkUrl);
		}

		URL collapsedUrl = StandalonePluginWorkspace.class.getResource("/images/Collapsed16_dark.png");
		if (collapsedUrl != null) {
			this.collapsedIcon = (ImageIcon) imageUtilities.loadIcon(collapsedUrl);
		}

		URL expandedUrl = StandalonePluginWorkspace.class.getResource("/images/Expanded16.png");
		if (expandedUrl != null) {
			this.expandedIcon = (ImageIcon) imageUtilities.loadIcon(expandedUrl);
		}

	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		label.setIcon(null);

		if (value instanceof DefaultMutableTreeNode) {

			if (((DefaultMutableTreeNode) value).getUserObject() instanceof NodeRange) {
				NodeRange nodeRange = (NodeRange) ((DefaultMutableTreeNode) value).getUserObject();
				// set icon for leaf nodes
				displayIcon(label, nodeRange);

				// set node text for leaf nodes from attribute values
				String hrefAttr = nodeRange.getAttributeValue("href");
				if (hrefAttr != null) {
					displayNodeValue(hrefAttr);
				} else {
					String keyrefAttr = nodeRange.getAttributeValue("keyref");
					if (keyrefAttr != null) {
						displayNodeValue(keyrefAttr);
					} else {
						String conrefAttr = nodeRange.getAttributeValue("conref");
						if (conrefAttr != null) {
							displayNodeValue(conrefAttr);
						} else {
							String conkeyrefAttr = nodeRange.getAttributeValue("conkeyref");
							if (conkeyrefAttr != null) {
								displayNodeValue(conkeyrefAttr);
							}
						}
					}
				}

			} else {
				// add expanded/collapsed icons only when references found in tree
				boolean hasReferences = true;
				
				// set node translated text for references categories
				String toDisplayCategory = null;
				if (((DefaultMutableTreeNode) value).getUserObject() instanceof String) {
					if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.IMAGE_REFERENCES)) {
						toDisplayCategory = translator.getTranslation(Tags.IMAGE_REFERENCES);
					} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CROSS_REFERENCES)) {
						toDisplayCategory = translator.getTranslation(Tags.CROSS_REFERENCES);
					} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CONTENT_REFERENCES)) {
						toDisplayCategory = translator.getTranslation(Tags.CONTENT_REFERENCES);
					} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.RELATED_LINKS)) {
						toDisplayCategory = translator.getTranslation(Tags.RELATED_LINKS);
					} else if (((DefaultMutableTreeNode) value).getUserObject()
							.equals(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE)) {
						toDisplayCategory = translator.getTranslation(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
						hasReferences = false;
					} else if (((DefaultMutableTreeNode) value).getUserObject()
							.equals(Tags.NO_OUTGOING_REFERENCES_FOUND)) {
						toDisplayCategory = translator.getTranslation(Tags.NO_OUTGOING_REFERENCES_FOUND);
						hasReferences = false;
					}
				}
				label.setText(toDisplayCategory);

				// set icon for references categories if any
				if (hasReferences) {
					if (expanded) {
						label.setIcon(expandedIcon);
					} else {
						label.setIcon(collapsedIcon);
					}
				}
			}
		}
		return label;
	}

	/**
	 * Set icon for each leaf node depending on references category.
	 * 
	 * @param label     NodeLabel
	 * @param nodeRange The nodeRange
	 */
	private void displayIcon(JLabel label, NodeRange nodeRange) {
		String classAttrValue = nodeRange.getAttributeValue("class");

		if (classAttrValue != null) {
			if (classAttrValue.contains(" topic/image ")) {
				label.setIcon(imageIcon);
			} else if (classAttrValue.contains(" topic/xref ")) {
				label.setIcon(crossIcon);
			} else if (classAttrValue.contains(" topic/link ")) {
				label.setIcon(linkIcon);
			} else if (nodeRange.getAttributeValue("conkeyref") != null
					|| nodeRange.getAttributeValue("conref") != null) {
				label.setIcon(contentIcon);
			} else {
				label.setIcon(contentIcon);
			}
		}
	}

	/**
	 * Display maximum 20 characters for each leaf node value and set the toolTip
	 * with the full node value.
	 * 
	 * @param referenceAttributeValue The attribute value
	 * @return the displayed node text
	 */
	private void displayNodeValue(String referenceAttributeValue) {
		String toDisplayString = null;

		// TODO show file name.
//		StringTokenizer st = new StringTokenizer(referenceAttributeValue, "/");
//		String filename = null;
//		while (st.hasMoreTokens()) {
//			filename = st.nextToken();
//		}
//
//		int filenameLength = filename.length();
//		int refLength = referenceAttributeValue.length();
//
//		if (filenameLength == refLength) {
//			toDisplayString = referenceAttributeValue;
//		} else {
//			int diff = refLength - filenameLength;
//			if ((refLength - diff) >= 5) {
//				toDisplayString = "... " + referenceAttributeValue.substring(diff - 5);
//			} else {
//				toDisplayString = "... " + referenceAttributeValue.substring(diff);
//			}
//		}

		if (referenceAttributeValue.length() > 20) {
			toDisplayString = "... " + referenceAttributeValue.substring(referenceAttributeValue.length() - 20);
		} else {
			toDisplayString = referenceAttributeValue;
		}

		this.setText(toDisplayString);
		this.setToolTipText(referenceAttributeValue);
	}

}
