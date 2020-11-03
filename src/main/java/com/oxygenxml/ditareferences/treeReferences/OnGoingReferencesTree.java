/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.treeReferences;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.ui.Tree;
import ro.sync.ui.Icons;
import ro.sync.util.Equaler;

/**
 * Present ongoing references in a JTree
 * @author mircea_badoi
 *
 */
public class OnGoingReferencesTree extends JPanel {
  /**
   * Constant used for java reflexion
   */
  private static final String RO_SYNC_ECSS_DITA_DITA_ACCESS = "ro.sync.ecss.dita.DITAAccess";

  /**
   * Generated UID
   */
  private static final long serialVersionUID = 6172484593878514367L;
  
  /**
   * Logger for logging.
   */
  private static final Logger logger = Logger.getLogger(OnGoingReferencesTree.class.getName());
  
  /**
   * List with all ongoing references
   */
  private List<DocumentPositionedInfo> listOfOngoingReferences;
  
  /**
   * Last used editor url
   */
  private URL oldEditorUrl;
  
  /**
   * JTree with ongoing references
   */
  private JTree referenceTree;
  
  /**
   * The last used editor
   */
  private WSEditor lastEditor;
  
  /**
   * Timer for loading panel
   */
  private static Timer timer = new Timer(false) ;
  
  /**
   * TimerTask for loading panel
   */
  private TimerTask task;
  
  /**
   * The ID of the panel with references 
   */
  public static final String References_ID = "on_going_references";
  
  /**
   * The ID of the pending panel.
   */
  public static final String LOADING_ID = "loading_panel";
  
  /**
   * The card layout used by the display panel.
   */
  private final CardLayout cardLayout = new CardLayout();
  
  /**
   * The label that displays the pending loading icon with the message.
   */
  private JLabel loadingLabel;
  
  /**
   * Constructor
   * @param workspaceAccess The pluginworkspace
   */
  public OnGoingReferencesTree(PluginWorkspace workspaceAccess){
    this.setLayout(cardLayout);
    JPanel loadingPanel = new JPanel(new BorderLayout());
    loadingLabel = new JLabel(Icons.getIconAnimated(Icons.PROGRESS_IMAGE), SwingConstants.LEFT);
    loadingPanel.add(loadingLabel, BorderLayout.NORTH);
    this.add(loadingPanel, LOADING_ID);
    referenceTree = new Tree();
    ToolTipManager.sharedInstance().registerComponent(referenceTree);
    referenceTree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          e.consume();
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) referenceTree.getLastSelectedPathComponent();
          if (node != null) {
            DocumentPositionedInfo referenceInfo = (DocumentPositionedInfo) ((DefaultMutableTreeNode) node).getUserObject();
            try {
              StringBuilder urlToOpen = new StringBuilder();
              urlToOpen.append(referenceInfo.getSystemID());
              urlToOpen.append("#line=");
              urlToOpen.append(referenceInfo.getLine());
              urlToOpen.append(";column=");
              urlToOpen.append(referenceInfo.getColumn());
              URL url = new URL(urlToOpen.toString());
              if(workspaceAccess.open(url)) {
                SwingUtilities.invokeLater(new Runnable() {

                  @Override
                  public void run() {
                    WSEditorPage currentPage = workspaceAccess.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA).getCurrentPage();
                    selectRange(currentPage, referenceInfo);
                  }
                });
              }
            } catch (MalformedURLException e1) {
              logger.error(e1, e1);
            }
          }
        }
      }
    });
    //add tree
    this.add(referenceTree, References_ID);
  }
  
  /**
   * Refreshes the references in the last used editor
   */
  public void refresh() {
    if (lastEditor instanceof WSEditor) {
      refresh(lastEditor);
    } 
  }
  
  /**
   * Refreshes the references in the given editor
   * @param workspaceAccess the WSEditor
   */
  public void refresh(WSEditor workspaceAccess) {
    lastEditor = workspaceAccess;
    Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          load(true, 200);
          searchAndCreateRefOngoingTree(workspaceAccess);

        } catch (Exception e1) {
          e1.printStackTrace();
        }
        finally {
          load(false, 0);
        }
      }
    });

    thread.start();
  }
  
  /**
   * Search for ongoing references and creates a tree with them
   * @param workspaceAccess
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  private void searchAndCreateRefOngoingTree(WSEditor workspaceAccess)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    if(workspaceAccess != null) {

      URL editorLocation = workspaceAccess.getEditorLocation();
      if(VersionUtil.isOxygenVersionNewer(23, 0)){
        Class<?> ditaAccess = Class.forName(RO_SYNC_ECSS_DITA_DITA_ACCESS);
        Object graph = null;
        if(!Equaler.verifyEquals(oldEditorUrl, editorLocation)) {
          Method createReferencesGraph = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_CREATE_REFERENCE_GRAPH);
          createReferencesGraph.setAccessible(true);
          graph = createReferencesGraph.invoke(null);
        }
        Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class, Object.class);
        searchReferences.setAccessible(true);
        listOfOngoingReferences = (List<DocumentPositionedInfo>) searchReferences.invoke(null,editorLocation, graph);

      } else if(VersionUtil.isOxygenVersionNewer(22, 0)) {
        Class<?> ditaAccess = Class.forName(RO_SYNC_ECSS_DITA_DITA_ACCESS);
        Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class);
        searchReferences.setAccessible(true);
        listOfOngoingReferences = (List<DocumentPositionedInfo>) searchReferences.invoke(null,editorLocation);
      }
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Ongoing References");
      DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root);
      DefaultMutableTreeNode ref ;
      for (DocumentPositionedInfo documentPositionedInfo : listOfOngoingReferences) {
        ref = new DefaultMutableTreeNode(documentPositionedInfo);
        root.add(ref);
      }
      referenceTree.setCellRenderer(new OnGoingReferencesTreeCellRenderer());
      referenceTree.setModel(referencesTreeModel);
      oldEditorUrl = editorLocation;
    }
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
                cardLayout.show(OnGoingReferencesTree.this, LOADING_ID);
              } else {
                // Display the result
                cardLayout.show(referenceTree.getParent(), References_ID);
              }

            }
          });  
        } catch(Exception e) {
          logger.error(e, e);
        }
      }};
      timer.schedule(task, delay);
  }
  
  /**
   * Select the corresponding Element in Editor.
   * 
   * @param page Text / Author Page
   * @param dpi  The document position info
   */
  private void selectRange(WSEditorPage page, DocumentPositionedInfo dpi) {
    if (page instanceof WSTextEditorPage) {
      WSTextEditorPage textPage = (WSTextEditorPage) page;
      try {
        int startOffset = textPage.getOffsetOfLineStart(dpi.getLine()) + dpi.getColumn() -1;
        int endOffset  = startOffset + dpi.getLength();
        textPage.select(startOffset, endOffset);

      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    } else if(page instanceof WSAuthorEditorPage) {
      WSAuthorEditorPage authorPage = (WSAuthorEditorPage)page;
      try {
        int[] startEndOffsets = authorPage.getStartEndOffsets(dpi);
        authorPage.select(startEndOffsets[0], startEndOffsets[1]);
      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    }
  }
}
