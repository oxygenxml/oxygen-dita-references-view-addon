/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.sideview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.tree.references.ReferenceType;
import com.oxygenxml.ditareferences.tree.references.VersionUtil;
import com.oxygenxml.ditareferences.tree.references.incoming.IncomingReferencesPanel;
import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;

import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

@SuppressWarnings("serial")
public class SideViewComponent extends JPanel {
  
  /**
   * The buttons to filter the references
   */
  private TagFilterPanel filterButtons;
  
  /**
   * the main panel with the filters buttons and refresh
   */
  private JPanel mainPanel;
  
  /**
   * The card layout used by the display panel.
   */
  private final CardLayout cards;
  
  /**
   * Constructor
   * @param outgoingRef The references of the dita document
   * @param incomingRef 
   */
  public SideViewComponent(OutgoingReferencesTree outgoingRef, IncomingReferencesPanel incomingRef) {
    //set the layout for this JPanel
    setLayout(new BorderLayout());
    
    //creating the panel which will hold the cards with the incoming and outgoing references
    mainPanel = new JPanel();
    cards = new CardLayout();
    mainPanel.setLayout(cards);
    
    //create and add the panel with outgoing references
    JScrollPane outgoingReferences = new JScrollPane(outgoingRef);
    outgoingReferences.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(() -> {
          // remember the selected path
          TreePath selectionPath = outgoingRef.getSelectionPath();
          ((DefaultTreeModel) outgoingRef.getModel())
              .nodeStructureChanged((TreeNode) outgoingRef.getModel().getRoot());

          // expand all rows with same selected path after side-view scrolled
          outgoingRef.expandAllRows();
          outgoingRef.setSelectionPath(selectionPath);
        });
      }
    });
    mainPanel.add(outgoingReferences, ReferenceType.OUTGOING.toString());
    
    //create and add the incoming references panel
    if(VersionUtil.isOxygenVersionNewer(23, 0)) {
      JScrollPane incomingReferencesScrollPane = new JScrollPane(incomingRef);
      //increment speed of the vertical and horizontal scroll
      incomingReferencesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
      incomingReferencesScrollPane.getHorizontalScrollBar().setUnitIncrement(10);
      mainPanel.add(incomingReferencesScrollPane, ReferenceType.INCOMING.toString());
      JPanel optionPanel = new JPanel(new GridBagLayout());
      GridBagConstraints constr = new GridBagConstraints();
      //create refresh button
      constr.insets = new Insets(10, 0, 0, 0);
      constr.weightx = 1;
      constr.anchor = GridBagConstraints.NORTHEAST;
      constr.fill = GridBagConstraints.NONE;
      constr.gridx++;
      AbstractAction refreshAction = incomingRef.getRefereshAction();
      JButton refreshButton = new ToolbarButton(refreshAction, false);
      refreshButton.setVisible(false);
      optionPanel.add(refreshButton, constr);
      
      //create filter buttons
      constr.insets = new Insets(0, 0, 0, 0);
      constr.gridx = 0;
      constr.gridy = 0;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.anchor = GridBagConstraints.NORTHWEST;
      filterButtons = new TagFilterPanel("References") {
        @Override
        void showPanel(String type) {
          String outgoing = ReferenceType.OUTGOING.toString();
          String incoming = ReferenceType.INCOMING.toString();
          if(type.equals(outgoing)) {
            refreshButton.setVisible(false);
            cards.show(outgoingReferences.getParent(), outgoing);
            incomingRef.setTabSelected(false);
          } else if(type.equals(incoming)){
            refreshButton.setVisible(true);
            cards.show(incomingReferencesScrollPane.getParent(), incoming);
            incomingRef.setTabSelected(true);
          } 
        }
      };
      filterButtons.setTags(ReferenceType.values());
      optionPanel.add(filterButtons, constr);
      
      add(optionPanel, BorderLayout.NORTH);
    }
    
    //add the panel with the cards to the component panel
    add(mainPanel, BorderLayout.CENTER);
  }
}
