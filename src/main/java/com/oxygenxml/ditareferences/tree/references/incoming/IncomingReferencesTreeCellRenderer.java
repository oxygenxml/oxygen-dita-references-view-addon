/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;

import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;
import ro.sync.util.URLUtil;

/**
 * Incoming tree cell renderer
 * 
 * @author mircea_badoi
 *
 */
public class IncomingReferencesTreeCellRenderer extends TreeCellRenderer {

	/**
	 * For translation
	 */
	private static final Translator TRANSLATOR = DITAReferencesTranslator.getInstance();

	/**
	 * Image utilities used to get icon for node
	 */
	private transient ImageUtilities imageUtilities;

	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesTreeCellRenderer.class.getName());

	/**
	 * Parametrized constructor
	 * 
	 * @param imageUtilities The image utilities(eg: from PluginWorkspace)
	 */
	public IncomingReferencesTreeCellRenderer(ImageUtilities imageUtilities) {
		this.imageUtilities = imageUtilities;
	}

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 1063823422174158329L;

	/**
	 * Overrided cell renderer component as a JLabel
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		label.setIcon(null);
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() instanceof IncomingReference) {
				IncomingReference referenceInfo = (IncomingReference) (node.getUserObject());
				label.setText(referenceInfo.getRenderText());
				label.setToolTipText(referenceInfo.getTooltipText());
				try {
					Icon iconDecoration = (Icon) imageUtilities.getIconDecoration(new URL(referenceInfo.getSystemId()));
					String fileName = URLUtil.extractFileName(referenceInfo.getSystemId());
					if (fileName.endsWith(".dita") || fileName.endsWith(".xml")) {
						URL resource = getClass().getResource("/images/DitaTopicRef16.png");
						iconDecoration = (Icon) imageUtilities.loadIcon(resource);
					}
					if (iconDecoration != null) {
						label.setIcon(iconDecoration);
					}
				} catch (MalformedURLException e) {
					LOGGER.error(e, e);
				}
			} else if (node.getUserObject() instanceof ReferenceCategory) {
				ReferenceCategory referenceCategory = (ReferenceCategory) node.getUserObject();
				String referenceCategoryString = "";
				if (referenceCategory == ReferenceCategory.CONTENT) {
					referenceCategoryString = Tags.CONTENT_REFERENCES;
				} else if (referenceCategory == ReferenceCategory.MAP) {
					referenceCategoryString = Tags.MAP_REFERENCES;
				} else {
					referenceCategoryString = Tags.CROSS_REFERENCES;
				}

				label.setText(TRANSLATOR.getTranslation(referenceCategoryString));
			}
		}
		return label;
	}

	
}
