/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.sideView;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.treeReferences.OnGoingReferencesTree;
import com.oxygenxml.ditareferences.treeReferences.ReferenceType;
import com.oxygenxml.ditareferences.treeReferences.ReferencesTree;
import com.oxygenxml.ditareferences.treeReferences.VersionUtil;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import ro.sync.ui.Icons;

@SuppressWarnings("serial")
public class SideViewComponent extends JPanel {
  
  /**
   * The buttons to filter the references
   */
  private TagFilterPanel filterButtons;
  
  /**
   * Constructor
   * @param outGoingRef The references of the dita document
   * @param onGoingRef 
   */
  public SideViewComponent(ReferencesTree outGoingRef, OnGoingReferencesTree onGoingRef) {
    //set the layout for this JPanel
    setLayout(new BorderLayout());
    
    //creating the panel which will hold the cards with the ongoing and outgoing references
    JPanel mainPanel = new JPanel();
    CardLayout cards = new CardLayout();
    mainPanel.setLayout(cards);
    
    //create and add the panel wit outgoing references
    JScrollPane outcomingReferences = new JScrollPane(outGoingRef);
    outcomingReferences.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(() -> {
          // remember the selected path
          TreePath selectionPath = outGoingRef.getSelectionPath();
          ((DefaultTreeModel) outGoingRef.getModel())
              .nodeStructureChanged((TreeNode) outGoingRef.getModel().getRoot());

          // expand all rows with same selected path after side-view scrolled
          outGoingRef.expandAllRows();
          outGoingRef.setSelectionPath(selectionPath);
        });

      }
    });
    mainPanel.add(outcomingReferences, ReferenceType.OUTCOMING.toString());
    
    //create and add the ongoing references panel
    if(VersionUtil.isOxygenVersionNewer(22, 0)) {
      JScrollPane incomingReferences = new JScrollPane(onGoingRef);
      JPanel optionPanel = new JPanel(new GridBagLayout());
      GridBagConstraints constr = new GridBagConstraints();
      constr.insets = new Insets(0, 5, 0, 5);
      constr.gridx = 0;
      constr.gridy = 0;
      constr.weightx = 1;
      constr.fill = GridBagConstraints.HORIZONTAL;
      constr.anchor = GridBagConstraints.NORTHWEST;
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
      optionPanel.add(filterButtons, constr);
      constr.anchor = GridBagConstraints.NORTHEAST;
      constr.fill = GridBagConstraints.NONE;
      constr.gridx++;
      PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
      Icon icon = (Icon) pluginWorkspace.getImageUtilities().loadIcon(ro.sync.exml.Oxygen.class.getResource(Icons.REFRESH));
      JButton refreshButton = new ToolbarButton(new AbstractAction() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
          PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
          WSEditor currentEditorAccess = pluginWorkspace.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
          outGoingRef.refresh(currentEditorAccess);
          onGoingRef.refresh(currentEditorAccess);
        }
      }, false);
      refreshButton.setIcon(icon);
      optionPanel.add(refreshButton, constr);
      add(optionPanel, BorderLayout.NORTH);
    }
    
    //add the panel with the cards to the component panel
    add(mainPanel, BorderLayout.CENTER);
  }
}
