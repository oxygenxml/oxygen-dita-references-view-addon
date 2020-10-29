/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.sideView;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.treeReferences.ReferenceType;
import com.oxygenxml.ditareferences.treeReferences.ReferencesTree;

@SuppressWarnings("serial")
public class SideViewComponent extends JPanel {
  
  /**
   * Presenter panel key
   */
  private static final String PRESENTER_PANEL_KEY = "presenter_Panel";

  /**
   * Message of the presenter panel
   */
  private static final String PRESENTER_MESSAGE = 
      "<html><p>This plugin let you see the incoming and outcoming references."
      + " Please select one of the prefered reference above.</p></html>";

  /**
   * The buttons to filter the references
   */
  private TagFilterPanel filterButtons;
  
  /**
   * Constructor
   * @param references The references of the dita document
   */
  public SideViewComponent(ReferencesTree references) {
    //set the layout for this JPanel
    setLayout(new BorderLayout());
    
    //creating the panel which will hold the cards with the ongoing and outgoing references
    JPanel mainPanel = new JPanel();
    CardLayout cards = new CardLayout();
    mainPanel.setLayout(cards);
    
    //create and add the panel wit outgoing references
    JScrollPane outcomingReferences = new JScrollPane(references);
    outcomingReferences.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(() -> {
          // remember the selected path
          TreePath selectionPath = references.getSelectionPath();
          ((DefaultTreeModel) references.getModel())
              .nodeStructureChanged((TreeNode) references.getModel().getRoot());

          // expand all rows with same selected path after side-view scrolled
          references.expandAllRows();
          references.setSelectionPath(selectionPath);
        });

      }
    });
    mainPanel.add(outcomingReferences, ReferenceType.OUTCOMING.toString());
    
    //create and add the presenter panel 
    JPanel presenter = new JPanel(new BorderLayout());
    presenter.add(new JLabel(PRESENTER_MESSAGE), BorderLayout.NORTH);
    mainPanel.add(presenter, PRESENTER_PANEL_KEY);  
    
    //create and add the ongoing references panel
    JPanel incomingReferences = new JPanel();
    mainPanel.add(incomingReferences, ReferenceType.INCOMING.toString());
    
    //create filter buttons
    filterButtons = new TagFilterPanel("References") {
      @Override
      void showPanel(String type) {
        String outcoming = ReferenceType.OUTCOMING.toString();
        if(type.contentEquals(outcoming)) {
          cards.show(outcomingReferences.getParent(), outcoming);
        } else {
          String incoming = ReferenceType.INCOMING.toString();
          if(type.contentEquals(incoming)){
            cards.show(incomingReferences.getParent(), incoming);
          }
        } 
      }
    };
    filterButtons.setTags(ReferenceType.values());
    add(filterButtons, BorderLayout.NORTH);
    
    // first time we show the presenter panel
    cards.show(presenter.getParent(), PRESENTER_PANEL_KEY);
    //add the panel with the cards to the component panel
    add(mainPanel, BorderLayout.CENTER);
  }
}
