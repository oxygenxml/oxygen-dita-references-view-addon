package com.oxygenxml.ditareferences.tree.references.outgoing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.DefaultMutableTreeNode;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;
import com.oxygenxml.ditareferences.workspace.DITAConstants;
import com.oxygenxml.ditareferences.workspace.Icons;
import com.oxygenxml.ditareferences.workspace.KeysProvider;
import com.oxygenxml.ditareferences.workspace.StringUtilities;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;
/**
 * Tree cell renderer.
 * 
 * @Alexandra_Dinisor
 *
 */
@SuppressWarnings("serial")
public class ReferencesTreeCellRenderer extends TreeCellRenderer {	
	
	/**
	 * For translation.
	 */
	private static final Translator TRANSLATOR = DITAReferencesTranslator.getInstance();

	/* The keysProvider. */
	private transient KeysProvider keysProvider;

	/* Icons for leaf nodes. */
	private ImageIcon imageIcon = null;
	private ImageIcon mediaIcon = null;
	private ImageIcon linkIcon = null;
	private ImageIcon contentIcon = null;
	private ImageIcon crossIcon = null;
	private ImageIcon relLinkIcon = null;
	private ImageIcon externalRefIcon = null;

	/**
	 * Construct Renderer by including the icons for leaf nodes and reference
	 * category nodes corresponding their status: expanded/collapsed.
	 * 
	 * @param imageUtilities The imageUtilities
	 */
	public ReferencesTreeCellRenderer(ImageUtilities imageUtilities, KeysProvider keysProvider) {
		super();
		this.keysProvider = keysProvider;

		URL imageUrl = StandalonePluginWorkspace.class.getResource(Icons.IMAGE_REFERENCE);
		if (imageUrl != null) {
			this.imageIcon = (ImageIcon) imageUtilities.loadIcon(imageUrl);
		}

		URL mediaUrl = StandalonePluginWorkspace.class.getResource(Icons.MEDIA_REFERENCE);
		if (mediaUrl != null) {
			this.mediaIcon = (ImageIcon) imageUtilities.loadIcon(mediaUrl);
		}

		URL crossUrl = StandalonePluginWorkspace.class.getResource(Icons.CROSS_REFERENCE);
		if (crossUrl != null) {
			this.crossIcon = (ImageIcon) imageUtilities.loadIcon(crossUrl);
		}

		URL contentUrl = StandalonePluginWorkspace.class.getResource(Icons.CONTENT_REFERENCE);
		if (contentUrl != null) {
			this.contentIcon = (ImageIcon) imageUtilities.loadIcon(contentUrl);
		}

		URL linkUrl = StandalonePluginWorkspace.class.getResource(Icons.LINK_REFERENCE);
		if (linkUrl != null) {
			this.linkIcon = (ImageIcon) imageUtilities.loadIcon(linkUrl);
		}

		URL relLinkUrl = StandalonePluginWorkspace.class.getResource(Icons.REL_LINK_REFERENCE);
		if (relLinkUrl != null) {
			this.relLinkIcon = (ImageIcon) imageUtilities.loadIcon(relLinkUrl);
		}
		
		URL externalRefUrl = StandalonePluginWorkspace.class.getResource(Icons.EXTERNAL_REFERENCE);
		if (externalRefUrl != null) {
			this.externalRefIcon = (ImageIcon) imageUtilities.loadIcon(externalRefUrl);
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
		LinkedHashMap<String, KeyInfo> referencesKeys = keysProvider != null ? keysProvider.getKeys(nodeRange.getEditorLocation()) : null;
		
		String displayedText = nodeRange.getDisplayText();
		if (displayedText != null) {
			this.setText(StringUtilities.trimNodeText(label.getFontMetrics(label.getFont()), displayedText, width));
		}
		
		String toolTipText = nodeRange.getTooltipText(referencesKeys);
		if (toolTipText != null) {
			this.setToolTipText(toolTipText);
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
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.MEDIA_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CROSS_REFERENCES)) {
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.CROSS_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.CONTENT_REFERENCES)) {
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.CONTENT_REFERENCES);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.RELATED_LINKS)) {
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.RELATED_LINKS);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE)) {
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
		} else if (((DefaultMutableTreeNode) value).getUserObject().equals(Tags.NO_OUTGOING_REFERENCES_FOUND)) {
			toDisplayCategory = TRANSLATOR.getTranslation(Tags.NO_OUTGOING_REFERENCES_FOUND);
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
		String classAttrValue = nodeRange.getAttributeValue(DITAConstants.CLASS);
		String scopeAttribute = nodeRange.getAttributeValue(DITAConstants.SCOPE);

		// set icon for external reference
		if (scopeAttribute != null && scopeAttribute.equals(DITAConstants.SCOPE_EXTERNAL)) {
			label.setIcon(externalRefIcon);
		} else if (classAttrValue != null) {
			if (classAttrValue.contains(DITAConstants.IMAGE_CLASS)) {
				label.setIcon(imageIcon);
			} else if (classAttrValue.contains(DITAConstants.OBJECT_CLASS)) {
				label.setIcon(mediaIcon);
			} else if (classAttrValue.contains(DITAConstants.XREF_CLASS)) {
				label.setIcon(crossIcon);
			} else if (classAttrValue.contains(DITAConstants.LINK_CLASS)) {
				// make difference between related links and links from relationship table
				if (nodeRange instanceof RelLinkNodeRange) {
					label.setIcon(relLinkIcon);
				} else {
					label.setIcon(linkIcon);
				}
			} else if (nodeRange.getAttributeValue(DITAConstants.CONKEYREF) != null
					|| nodeRange.getAttributeValue(DITAConstants.CONREF) != null) {
				label.setIcon(contentIcon);
			} else {
				label.setIcon(contentIcon);
			}
		}
	}

}
