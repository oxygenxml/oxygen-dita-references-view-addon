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
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;
import ro.sync.util.URLUtil;

/**
 * Ongoing tree cell renderer
 * @author mircea_badoi
 *
 */
public class IncomingReferencesTreeCellRenderer extends TreeCellRenderer{
  
  /**
   * Image utilities used to get icon for node
   */
  private ImageUtilities imageUtilities;
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(IncomingReferencesPanel.class.getName());

  public IncomingReferencesTreeCellRenderer(ImageUtilities imageUtilities) {
    this.imageUtilities = imageUtilities;
  }
  
  /**
   * Generated UID
   */
  private static final long serialVersionUID = 1063823422174158329L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {
    JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    label.setIcon(null);
    label.setBorder(new EmptyBorder(10, 10, 10 ,10));
    if (value instanceof DefaultMutableTreeNode) {
      if (((DefaultMutableTreeNode) value).getUserObject() instanceof DocumentPositionedInfo) {
        DocumentPositionedInfo referenceInfo = (DocumentPositionedInfo) ((DefaultMutableTreeNode) value).getUserObject();
        String[] split = referenceInfo.getSystemID().split("/");
        label.setText(split[split.length - 1]);
        label.setToolTipText(URLUtil.getDescription(referenceInfo.getSystemID()));
        try {
          Icon iconDecoration = (Icon) imageUtilities.getIconDecoration(new URL(referenceInfo.getSystemID()));
          if(iconDecoration != null) {
            label.setIcon(iconDecoration);
          }
        } catch (MalformedURLException e) {
          logger.error(e, e);
        }
      }
    }
    return label;
  }
}
