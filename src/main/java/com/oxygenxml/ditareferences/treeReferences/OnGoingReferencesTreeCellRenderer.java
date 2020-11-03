/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.treeReferences;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.standalone.ui.TreeCellRenderer;
import ro.sync.util.URLUtil;

/**
 * Ongoing tree cell renderer
 * @author mircea_badoi
 *
 */
public class OnGoingReferencesTreeCellRenderer extends TreeCellRenderer{
  
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
        label.setText(referenceInfo.getMessage().split(" ")[0]);
        label.setToolTipText(URLUtil.getDescription(referenceInfo.getSystemID()));
      }
    }
    return label;
  }
}
