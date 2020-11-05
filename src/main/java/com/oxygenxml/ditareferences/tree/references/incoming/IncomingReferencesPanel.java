/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.tree.references.VersionUtil;
import com.oxygenxml.ditareferences.tree.references.incoming.IncomingReferencesTreeCellRenderer;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.WSTextBasedEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.ui.Tree;

/**
 * Present ongoing references in a JTree
 * @author mircea_badoi
 *
 */
public class IncomingReferencesPanel extends JPanel {
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
  private static final Logger logger = Logger.getLogger(IncomingReferencesPanel.class.getName());
  
  /**
   * List with all ongoing references
   */
  private List<DocumentPositionedInfo> listOfOngoingReferences;
  
  /**
   * JTree with ongoing references
   */
  private JTree referenceTree;
  
  /**
   * References graph
   */
  private Object graph;
  
  /**
   * The plugin workspace
   */
  @SuppressWarnings("unused")
  private PluginWorkspace workspaceAccess;

  /**
   * Constructor
   * @param workspaceAccess The pluginworkspace
   */
  public IncomingReferencesPanel(PluginWorkspace workspaceAccess) {
    this.workspaceAccess = workspaceAccess;
    this.setLayout(new BorderLayout());

    //add tree
    referenceTree = new Tree();
    referenceTree.setRootVisible(false);
    referenceTree.setShowsRootHandles(true);
    referenceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    referenceTree.setCellRenderer(new IncomingReferencesTreeCellRenderer(workspaceAccess.getImageUtilities()));
    ToolTipManager.sharedInstance().registerComponent(referenceTree);
    this.add(referenceTree, BorderLayout.CENTER);
    
    //install listener
    installListeners(workspaceAccess);
  }
  
  /**
   * Refreshes the references in the given editor
   * @param workspaceAccess The WSEditor
   */
  public void refresh(WSEditor workspaceAccess) {
    if(workspaceAccess != null) {

      URL editorLocation = workspaceAccess.getEditorLocation();
      refresh(editorLocation);
    }
  }
  
 /**
  * Refreshes the references in the given editor
  * @param editorLocation The location of the editor to be refreshed
  */
  public void refresh(URL editorLocation) {
    if(isDisplayable()) {
      List<DocumentPositionedInfo> temp;
      try {
        temp = searchOngoingRef(editorLocation);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Ongoing References");
        @SuppressWarnings("serial")
        DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
          public boolean isLeaf(Object node) {return false;};
        };
        DefaultMutableTreeNode ref ;
        if(temp != null) {
          for (DocumentPositionedInfo documentPositionedInfo : temp) {
            ref = new DefaultMutableTreeNode(documentPositionedInfo);
            root.add(ref);
          }
          referenceTree.setModel(referencesTreeModel);
        }
      } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        logger.error(e, e);
      }
    }
  }
  
  
  /**
   * Search for ongoing references and creates a tree with them
   * @param editorLocation The editor to search location
   * @return The list of found ongoing references
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  private List<DocumentPositionedInfo> searchOngoingRef(URL editorLocation)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    if(VersionUtil.isOxygenVersionNewer(23, 0)){
      Class<?> ditaAccess = Class.forName(RO_SYNC_ECSS_DITA_DITA_ACCESS);
      if(graph == null) {
        Method createReferencesGraph = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_CREATE_REFERENCE_GRAPH);
        createReferencesGraph.setAccessible(true);
        graph = createReferencesGraph.invoke(null);
      }
      Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class, Object.class);
      searchReferences.setAccessible(true);
      listOfOngoingReferences = (List<DocumentPositionedInfo>) searchReferences.invoke(null,editorLocation, graph);
    } 
    return listOfOngoingReferences;
  }
  
  /**
   * Select the corresponding Element in Editor.
   * 
   * @param page Text / Author Page
   * @param dpi  The document position info
   */
  private void selectRange(WSEditorPage page, DocumentPositionedInfo dpi) {
    if(page instanceof WSTextBasedEditorPage) {
      WSTextBasedEditorPage authorPage = (WSTextBasedEditorPage)page;
      try {
        int[] startEndOffsets = authorPage.getStartEndOffsets(dpi);
        authorPage.select(startEndOffsets[0], startEndOffsets[1]);
      } catch (BadLocationException e) {
        logger.error(e, e);
      }
    }
  }
  
  /**
   * Refreshes the refrences graph
   */
  public void refreshRefrenceGraph() {
    graph = null;
  }
  
  /**
   * Opens the selected Dita file and selects within it the location of the reference
   * @param workspaceAccess The plugin workspace
   */
  private void openFileAndSelectReference(PluginWorkspace workspaceAccess) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) referenceTree.getLastSelectedPathComponent();
    if (node != null) {
      Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
      if(userObject instanceof DocumentPositionedInfo) {
        DocumentPositionedInfo referenceInfo = (DocumentPositionedInfo) userObject;
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
  
  /**
   * Install listeners to the tree 
   * @param workspaceAccess The plugin workspace
   */
  private void installListeners(PluginWorkspace workspaceAccess) {
    referenceTree.addTreeWillExpandListener(new TreeWillExpandListener() {

      @Override
      public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        if(source != null) {
          if(source.getChildCount() == 0) {
            try {
              DocumentPositionedInfo referenceInfo = (DocumentPositionedInfo)(source.getUserObject());
              List<DocumentPositionedInfo> temp;
              URL editorLocation = new URL(referenceInfo.getSystemID());
              temp = searchOngoingRef(editorLocation);
              for (int i = 0; i < temp.size() ; i++) {
                source.add(new DefaultMutableTreeNode(temp.get(i)));
              }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | MalformedURLException e1) {
              e1.printStackTrace();
            } 
          }
        }
      }

      @Override
      public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        //not needed
      }
    });

    referenceTree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (KeyEvent.VK_ENTER == e.getKeyCode()) {
          e.consume();
          openFileAndSelectReference(workspaceAccess);
        }
      }
    });

    referenceTree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          e.consume();
          openFileAndSelectReference(workspaceAccess);
        }
      }
    });

    workspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
      @Override
      public void editorSelected(URL editorLocation) {
        graph = null;
        refresh(editorLocation);
      }
    }, PluginWorkspace.DITA_MAPS_EDITING_AREA);
  }
}
