/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.tree.references.ReferenceType;
import com.oxygenxml.ditareferences.tree.references.VersionUtil;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.WSTextBasedEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.ui.Tree;
import ro.sync.ui.Icons;

/**
 * Present incoming references in a JTree
 * @author mircea_badoi
 *
 */
public class IncomingReferencesPanel extends JPanel {
  /**
   * For translation
   */
  DITAReferencesTranslator translator = new DITAReferencesTranslator();
  
  /**
   * Constant used for java reflexion
   */
  private static final String DITA_ACCESS_CLASS_NAME = "ro.sync.ecss.dita.DITAAccess";

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
  private List<IncomingReference> listOfIncomingReferences = new ArrayList<IncomingReference>();
  
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
  private PluginWorkspace workspaceAccess;
  
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

  /**
   * Constructor
   * @param workspaceAccess The pluginworkspace
   */
  public IncomingReferencesPanel(PluginWorkspace workspaceAccess) {
    this.workspaceAccess = workspaceAccess;
    cards = new CardLayout();
    this.setLayout(cards);

    //add tree
    referenceTree = new Tree();
    referenceTree.setToggleClickCount(0);
    referenceTree.setRootVisible(false);
    referenceTree.setShowsRootHandles(true);
    referenceTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    referenceTree.setCellRenderer(new IncomingReferencesTreeCellRenderer(workspaceAccess.getImageUtilities()));
    ToolTipManager.sharedInstance().registerComponent(referenceTree);
    this.add(referenceTree, ReferenceType.INCOMING.toString());
    
    //creating loading panel
    JPanel loadingPanel = new JPanel(new BorderLayout());
    loadingLabel = new JLabel(Icons.getIconAnimated(Icons.PROGRESS_IMAGE), SwingConstants.LEFT);
    loadingPanel.add(loadingLabel, BorderLayout.NORTH);
    this.add(loadingPanel, LOADING_ID);
    
    //install listener
    installListeners(workspaceAccess);
  }
  
  /**
   * Refreshes the references in the given editor
   * @param workspaceAccess The WSEditor
   */
  public synchronized void refresh(WSEditor workspaceAccess) {
    if(workspaceAccess != null) {

      URL editorLocation = workspaceAccess.getEditorLocation();
      refresh(editorLocation);
    }
  }
  
 /**
  * Refreshes the references in the given editor
  * @param editorLocation The location of the editor to be refreshed
  */
  public synchronized void refresh(URL editorLocation) {
    timer.schedule(new TimerTask() {
      
      @Override
      public void run() {
        if(isDisplayable()) {
          List<IncomingReference> temp;
          try {
            temp = searchIncomingRef(editorLocation);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Ongoing References");
            @SuppressWarnings("serial")
            DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
              
              public boolean isLeaf(Object node) {return false;};
            };
            DefaultMutableTreeNode ref ;
            if(temp != null) {
              for (IncomingReference incomingReference : temp) {
                ref = new DefaultMutableTreeNode(incomingReference);
                root.add(ref);
              }
              SwingUtilities.invokeLater(new Runnable() {
                
                @Override
                public void run() {
                  referenceTree.setModel(referencesTreeModel);
                }
              });
            }
          } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e, e);
          } 
        }
      }
    }, 10);
   
  }
  
  
  /**
   * Search for ongoing references and compute the label for them
   * @param editorLocation The editor to search location
   * @return The list of found ongoing references
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @SuppressWarnings("unchecked")
  private List<IncomingReference> searchIncomingRef(URL editorLocation)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    if(VersionUtil.isOxygenVersionNewer(23, 0)){
      Class<?> ditaAccess = Class.forName(DITA_ACCESS_CLASS_NAME);
      if(graph == null) {
        Method createReferencesGraph = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_CREATE_REFERENCE_GRAPH);
        createReferencesGraph.setAccessible(true);
        graph = createReferencesGraph.invoke(null);
      }
      Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class, Object.class);
      searchReferences.setAccessible(true);
      listOfIncomingReferences.clear();
      List<DocumentPositionedInfo> result;
      result = (List<DocumentPositionedInfo>) searchReferences.invoke(null,editorLocation, graph);
      for (DocumentPositionedInfo documentPositionedInfo : result) {
        listOfIncomingReferences.add(new IncomingReference(documentPositionedInfo));
      }
      Collections.sort(listOfIncomingReferences);
      for (int i = 0; i < listOfIncomingReferences.size(); i++) {
        if(i>0) {
          IncomingReference ref1 = listOfIncomingReferences.get(i - 1);
          IncomingReference ref2 = listOfIncomingReferences.get(i);
          if(ref1.getSystemId().equals(ref2.getSystemId())) {
            ref1.buildLabel();
            ref2.buildLabel();
          } else {
            ref2.setLabelText(ref2.getSystemId());
          }
        } 
      }
      if(listOfIncomingReferences.size() > 0) {
        IncomingReference incomingReference = listOfIncomingReferences.get(0);
        if(incomingReference.getLabelText() == null) {
          incomingReference.setLabelText(incomingReference.getDPI().getSystemID());
        }
      }
    } 
    return listOfIncomingReferences;
  }
  
  /**
   * Select the corresponding Element in Editor.
   * 
   * @param page Text / Author Page
   * @param dpi  The document position info
   */
  private void selectRange(WSEditorPage page, IncomingReference dpi) {
        if(page instanceof WSTextBasedEditorPage) {
          WSTextBasedEditorPage authorPage = (WSTextBasedEditorPage)page;
          try {
            int[] startEndOffsets = authorPage.getStartEndOffsets(dpi.getDPI());
            authorPage.select(startEndOffsets[0], startEndOffsets[1]);
          } catch (BadLocationException e) {
            logger.error(e, e);
          }
        }
  }
  
  /**
   * Opens the selected Dita file and selects within it the location of the reference
   * @param workspaceAccess The plugin workspace
   */
  private void openFileAndSelectReference(PluginWorkspace workspaceAccess) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) referenceTree.getLastSelectedPathComponent();
    if (node != null) {
      Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
      if(userObject instanceof IncomingReference) {
        IncomingReference referenceInfo = (IncomingReference) userObject;
        try {
          URL url = new URL(referenceInfo.getDPI().getSystemID());
          if(workspaceAccess.open(url)) {
            WSEditor editorAccess = workspaceAccess.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA);
            if(editorAccess != null) {
              WSEditorPage currentPage = editorAccess.getCurrentPage();
              timer.schedule(new TimerTask() {
                
                @Override
                public void run() {
                  SwingUtilities.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                      selectRange(currentPage, referenceInfo);
                    }
                  });
                }
              }, 50);
            }
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
              IncomingReference referenceInfo = (IncomingReference)(source.getUserObject());
              List<IncomingReference> temp;
              URL editorLocation = new URL(referenceInfo.getDPI().getSystemID());
              temp = searchIncomingRef(editorLocation);
              for (int i = 0; i < temp.size() ; i++) {
                source.add(new DefaultMutableTreeNode(temp.get(i)));
              }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | MalformedURLException e1) {
              logger.error(e1, e1);
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
        e.consume();
        if (e.getClickCount() == 2) {
          openFileAndSelectReference(workspaceAccess);
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        e.consume();
        mousePressed(e);
      }
      
      @SuppressWarnings("serial")
      @Override
      public void mousePressed(MouseEvent e1) {
        e1.consume();
        if(e1.isPopupTrigger()) {
          //select the corresponding item from tree
          int selRow = referenceTree.getRowForLocation(e1.getX(), e1.getY());
          TreePath selPath = referenceTree.getPathForLocation(e1.getX(), e1.getY());
          if(selPath != null) {
            referenceTree.setSelectionPath(selPath); 
            if (selRow>-1){
              referenceTree.setSelectionRow(selRow); 
            }
          }
          JPopupMenu menu = new JPopupMenu();
          menu.add(new AbstractAction(translator.getTranslation(Tags.OPEN_REFERENCE)) {

            @Override
            public void actionPerformed(ActionEvent e) {
              openFileAndSelectReference(workspaceAccess);
            }
          });
          menu.show(e1.getComponent(), e1.getX(), e1.getY());
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
                cards.show(IncomingReferencesPanel.this, LOADING_ID);
              } else {
                // Display the result
                cards.show(IncomingReferencesPanel.this, ReferenceType.INCOMING.toString());
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
   * Get the action for the refrsh button
   */
  public void getRefereshAction() {
    try {
      load(true, 300);
      graph = null;
      this.refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA));
    } finally {
      load(false, 0);
    }
  }
}
