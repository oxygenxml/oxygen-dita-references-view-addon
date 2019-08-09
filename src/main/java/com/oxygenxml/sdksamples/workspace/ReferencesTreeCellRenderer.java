package com.oxygenxml.sdksamples.workspace;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JViewport;
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
	 * Construct Renderer by including the icons for leaf nodes and reference
	 * category nodes corresponding their status: expanded/collapsed.
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

		int width = tree.getWidth();
		Container treeParent = tree.getParent();
		if (treeParent instanceof JViewport) {
			width = (int) ((JViewport) treeParent).getViewRect().getWidth();
		}

		if (value instanceof DefaultMutableTreeNode) {

			if (((DefaultMutableTreeNode) value).getUserObject() instanceof NodeRange) {
				NodeRange nodeRange = (NodeRange) ((DefaultMutableTreeNode) value).getUserObject();

				// set width for node text without its IconWidth and TextGap
				Rectangle rowBounds = tree.getRowBounds(row);
				if (rowBounds != null) {
					width -= rowBounds.x;
					if (label.getIcon() != null) {
						width -= label.getIcon().getIconWidth();
						width -= label.getIconTextGap();
					}
				}
				setTextAndToolTipForLeafNode(label, width, nodeRange);
				setIconForLeafNode(label, nodeRange);

			} else {
				// decide if expanded/collapsed icons are needed when at least 1 reference is
				// found in tree
				boolean hasReferences = true;

				if (((DefaultMutableTreeNode) value).getUserObject() instanceof String) {
					hasReferences = setTextForCategoryNode(value, label, hasReferences);
					setIconForCategoryNode(expanded, label, hasReferences);
				}
			}
		}
		return label;
	}

	/**
	 * Set text and toolTip for Leaf Node corresponding the attribute value.
	 * 
	 * @param label     The Leaf Node Label
	 * @param width     The Text Width
	 * @param nodeRange The NodeRange
	 */
	private void setTextAndToolTipForLeafNode(JLabel label, int width, NodeRange nodeRange) {
		String hrefAttr = nodeRange.getAttributeValue("href");
		if (hrefAttr != null) {
			this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), hrefAttr, width));
			this.setToolTipText(hrefAttr);
		} else {
			String keyrefAttr = nodeRange.getAttributeValue("keyref");
			if (keyrefAttr != null) {
				this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), keyrefAttr, width));
				this.setToolTipText(hrefAttr);
			} else {
				String conrefAttr = nodeRange.getAttributeValue("conref");
				if (conrefAttr != null) {
					this.setText(
							StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), conrefAttr, width));
					this.setToolTipText(hrefAttr);
				} else {
					String conkeyrefAttr = nodeRange.getAttributeValue("conkeyref");
					if (conkeyrefAttr != null) {
						this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), conkeyrefAttr,
								width));
						this.setToolTipText(hrefAttr);
					}
				}
			}
		}
	}

	/**
	 * Set translated text for reference category and change value for boolean
	 * "hasReferences", if the ReferencesTree has at least 1 reference inside.
	 * 
	 * @param value         The Node Value
	 * @param label         The Node Label
	 * @param hasReferences Boolean hasReferences
	 * @return
	 */
	private boolean setTextForCategoryNode(Object value, JLabel label, boolean hasReferences) {
		String toDisplayCategory = null;

		if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.IMAGE_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.IMAGE_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CROSS_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.CROSS_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CONTENT_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.CONTENT_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.RELATED_LINKS)) {
			toDisplayCategory = translator.getTranslation(Tags.RELATED_LINKS);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE)) {
			toDisplayCategory = translator.getTranslation(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
			hasReferences = false;
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.NO_OUTGOING_REFERENCES_FOUND)) {
			toDisplayCategory = translator.getTranslation(Tags.NO_OUTGOING_REFERENCES_FOUND);
			hasReferences = false;
		}
		label.setText(toDisplayCategory);
		return hasReferences;
	}

	/**
	 * Set icon for category node depending on status: expanded/collapsed.
	 * 
	 * @param expanded      If category node is expanded
	 * @param label         The Node Label
	 * @param hasReferences
	 */
	private void setIconForCategoryNode(boolean expanded, JLabel label, boolean hasReferences) {
		if (hasReferences) {
			if (expanded) {
				label.setIcon(expandedIcon);
			} else {
				label.setIcon(collapsedIcon);
			}
		}
	}

	/**
	 * Set icon for each leaf node depending on reference category.
	 * 
	 * @param label     The Node Label
	 * @param nodeRange The NodeRange
	 */
	private void setIconForLeafNode(JLabel label, NodeRange nodeRange) {
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

}
