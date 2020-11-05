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
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.tree.references.ReferenceType;
import com.oxygenxml.ditareferences.tree.references.VersionUtil;
import com.oxygenxml.ditareferences.tree.references.incoming.IncomingReferencesPanel;
import com.oxygenxml.ditareferences.tree.references.outgoing.OutgoingReferencesTree;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;
import ro.sync.ui.Icons;

@SuppressWarnings("serial")
public class SideViewComponent extends JPanel {
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(SideViewComponent.class.getName());
  
  /**
   * The buttons to filter the references
   */
  private TagFilterPanel filterButtons;
  
  /**
   * Timer for loading panel
   */
  private static Timer timer = new Timer(false) ;
  
  /**
   * TimerTask for loading panel
   */
  private TimerTask task;
  
  /**
   * The ID of the pending panel.
   */
  public static final String LOADING_ID = "loading_panel";
  
  /**
   * The card layout used by the display panel.
   */
  private final CardLayout cards;
  
  /**
   * The label that displays the pending loading icon with the message.
   */
  private JLabel loadingLabel;
  
  private JPanel mainPanel;
  
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
      JScrollPane incomingReferences = new JScrollPane(incomingRef);
      mainPanel.add(incomingReferences, ReferenceType.INCOMING.toString());
      JPanel optionPanel = new JPanel(new GridBagLayout());
      GridBagConstraints constr = new GridBagConstraints();
      //create refresh button
      constr.insets = new Insets(10, 0, 0, 0);
      constr.weightx = 1;
      constr.anchor = GridBagConstraints.NORTHEAST;
      constr.fill = GridBagConstraints.NONE;
      constr.gridx++;
      PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
      Icon icon = (Icon) pluginWorkspace.getImageUtilities().loadIcon(ro.sync.exml.Oxygen.class.getResource(Icons.REFRESH));
      JButton refreshButton = new ToolbarButton(new AbstractAction() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
          new Thread(new Runnable() {
            
            @Override
            public void run() {
              try {
                load(true, 300);
                PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
                WSEditor currentEditorAccess = pluginWorkspace.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
                incomingRef.refreshRefrenceGraph();
                incomingRef.refresh(currentEditorAccess);
              } finally {
                load(false, 0);
              }
            }
          } ).start();;
        }
      }, false);
      refreshButton.setIcon(icon);
      refreshButton.setVisible(false);
      refreshButton.setToolTipText("Refresh incoming references");
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
          String outcoming = ReferenceType.OUTGOING.toString();
          String incoming = ReferenceType.INCOMING.toString();
          if(type.equals(outcoming)) {
            refreshButton.setVisible(false);
            cards.show(outgoingReferences.getParent(), outcoming);
          } else if(type.equals(incoming)){
            refreshButton.setVisible(true);
            cards.show(incomingReferences.getParent(), incoming);
          } 
        }
      };
      filterButtons.setTags(ReferenceType.values());
      optionPanel.add(filterButtons, constr);
      
      add(optionPanel, BorderLayout.NORTH);
    }
    
    //creating loading panel
    JPanel loadingPanel = new JPanel(new BorderLayout());
    loadingLabel = new JLabel(Icons.getIconAnimated(Icons.PROGRESS_IMAGE), SwingConstants.LEFT);
    loadingPanel.add(loadingLabel, BorderLayout.NORTH);
    mainPanel.add(loadingPanel, LOADING_ID);
    
    //add the panel with the cards to the component panel
    add(mainPanel, BorderLayout.CENTER);
  }
  
  /**
   * If the list is in the loading process, a pending panel is displayed
   * 
   * @param inProgress <code>true</code> if the project is starting to load,
   *                   <code>false</code> if the project was loaded.
   * @param delay after which the function to be executed
   */
  public void load(final boolean inProgress, int delay) {
    if(task != null) {
      task.cancel();
      task = null;
    }
    task = new TimerTask() {

      @Override
      public void run() {
        try {
          SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {

              if (inProgress) {
                loadingLabel.setText("Loading...");
                // Display pending panel.
                cards.show(mainPanel, LOADING_ID);
              } else {
                // Display the result
                cards.show(mainPanel, ReferenceType.INCOMING.toString());
              }

            }
          });  
        } catch(Exception e) {
          logger.error(e, e);
        }
      }};
      timer.schedule(task, delay);
  }
  
}
