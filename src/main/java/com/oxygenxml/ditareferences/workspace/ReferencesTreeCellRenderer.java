package com.oxygenxml.ditareferences.workspace;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;

import com.oxygenxml.ditareferences.translator.Tags;
import com.oxygenxml.ditareferences.translator.Translator;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;

@SuppressWarnings("serial")
public class ReferencesTreeCellRenderer extends TreeCellRenderer {

	/* The translator of the DITA reference categories. */
	private Translator translator;

	/* Icons for leaf nodes. */
	private ImageIcon imageIcon = null;
	private ImageIcon mediaIcon = null;
	private ImageIcon linkIcon = null;
	private ImageIcon contentIcon = null;
	private ImageIcon crossIcon = null;
	private ImageIcon relLinkIcon = null;

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

		URL mediaUrl = StandalonePluginWorkspace.class.getResource("/images/node-customizer/ElementMedia16.png");
		if (mediaUrl != null) {
			this.mediaIcon = (ImageIcon) imageUtilities.loadIcon(mediaUrl);
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

		URL relLinkUrl = StandalonePluginWorkspace.class.getResource("/images/RelTable16.png");
		if (relLinkUrl != null) {
			this.relLinkIcon = (ImageIcon) imageUtilities.loadIcon(relLinkUrl);
		}

	}

	/* compute width of node text only at first method call */
	private int inProgress = 0;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		inProgress++;
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

				// compute width for node text
				if (inProgress == 1) {
					Rectangle rowBounds = tree.getRowBounds(row);
					width = adjustWidth(label, width, rowBounds);
				}
				setTextAndToolTipForLeafNode(label, width, nodeRange);
				setIconForLeafNode(label, nodeRange);

			} else {
				if (((DefaultMutableTreeNode) value).getUserObject() instanceof String) {
					setTextForCategoryNode(value, label);
				}
			}
		}

		inProgress--;
		return label;
	}

	/**
	 * Adjust the width of the leaf node, removing the icon width and gap between
	 * icon and text of the leaf node.
	 * 
	 * @param label     The label
	 * @param width     The initial width
	 * @param rowBounds The rowBounds
	 * @return the adjusted width
	 */
	private int adjustWidth(JLabel label, int width, Rectangle rowBounds) {
		if (rowBounds != null) {
			width -= rowBounds.x;
			if (label.getIcon() != null) {
				width -= label.getIcon().getIconWidth();
				width -= label.getIconTextGap();
			}
		}
		return width;
	}

	/**
	 * Set text and toolTip for Leaf Node corresponding the attribute value.
	 * 
	 * @param label     The Leaf Node Label
	 * @param width     The Text Width
	 * @param nodeRange The NodeRange
	 */
	private void setTextAndToolTipForLeafNode(JLabel label, int width, NodeRange nodeRange) {

		String keyrefAttr = nodeRange.getAttributeValue("keyref");
		if (keyrefAttr != null) {
			this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), keyrefAttr, width));
			this.setToolTipText(keyrefAttr);
		} else {
			String datakeyrefAttr = nodeRange.getAttributeValue("datakeyref");
			if (datakeyrefAttr != null) {
				this.setText(
						StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), datakeyrefAttr, width));
				this.setToolTipText(datakeyrefAttr);
			} else {
				String conkeyrefAttr = nodeRange.getAttributeValue("conkeyref");
				if (conkeyrefAttr != null) {
					this.setText(
							StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), conkeyrefAttr, width));
					this.setToolTipText(conkeyrefAttr);
				} else {
					String hrefAttr = nodeRange.getAttributeValue("href");
					if (hrefAttr != null) {
						this.setText(
								StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), hrefAttr, width));
						this.setToolTipText(hrefAttr);
					} else {
						String conrefAttr = nodeRange.getAttributeValue("conref");
						if (conrefAttr != null) {
							this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), conrefAttr,
									width));
							this.setToolTipText(conrefAttr);
						} else {
							String dataAttr = nodeRange.getAttributeValue("data");
							if (dataAttr != null) {
								this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()),
										dataAttr, width));
								this.setToolTipText(dataAttr);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set translated text for reference category.
	 * 
	 * @param value The Node Value
	 * @param label The Node Label
	 */
	private void setTextForCategoryNode(Object value, JLabel label) {
		String toDisplayCategory = null;

		if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.MEDIA_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.MEDIA_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CROSS_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.CROSS_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CONTENT_REFERENCES)) {
			toDisplayCategory = translator.getTranslation(Tags.CONTENT_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.RELATED_LINKS)) {
			toDisplayCategory = translator.getTranslation(Tags.RELATED_LINKS);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE)) {
			toDisplayCategory = translator.getTranslation(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.NO_OUTGOING_REFERENCES_FOUND)) {
			toDisplayCategory = translator.getTranslation(Tags.NO_OUTGOING_REFERENCES_FOUND);
		}
		label.setText(toDisplayCategory);
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
			} else if (classAttrValue.contains(" topic/object ")) {
				label.setIcon(mediaIcon);
			} else if (classAttrValue.contains(" topic/xref ")) {
				label.setIcon(crossIcon);
			} else if (classAttrValue.contains(" topic/link ")) {
				// make difference between related links and links from relationship table
				if (nodeRange instanceof RelLinkNodeRange) {
					label.setIcon(relLinkIcon);
				} else {
					label.setIcon(linkIcon);
				}
			} else if (nodeRange.getAttributeValue("conkeyref") != null
					|| nodeRange.getAttributeValue("conref") != null) {
				label.setIcon(contentIcon);
			} else {
				label.setIcon(contentIcon);
			}
		}
	}

}
