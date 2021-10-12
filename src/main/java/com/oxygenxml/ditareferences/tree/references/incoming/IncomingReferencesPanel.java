/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Icon;
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
import javax.swing.tree.TreeNode;
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
  private static final DITAReferencesTranslator translator = new DITAReferencesTranslator();
  
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
  private final transient List<IncomingReference> listOfIncomingReferences = new ArrayList<>();
  
  /**
   * JTree with ongoing references
   */
  private final JTree referenceTree;
  
  /**
   * References graph
   */
  private transient Object graph;
  
  /**
   * The plugin workspace
   */
  private final transient PluginWorkspace workspaceAccess;
  
  /**
   * Timer for loading panel
   */
  private static final transient Timer loadingInProgressTimer = new Timer(false) ;
  
  /**
   * Timer for loading panel
   */
  private static final Timer refreshTimer = new Timer(false) ;
  
  /**
   * TimerTask for loading panel
   */
  private transient TimerTask loadingInProgressTask;
  
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
  private final JLabel loadingLabel;
  
  /**
   * Refresh action
   */
  private final AbstractAction refreshAction;
  
  /**
   * Contains initial references category and their children.
   */
  private final Map<ReferenceCategory, List<IncomingReference>> referencesCategory = new HashMap<>();

  /**
   * Constructor
   * @param workspaceAccess The pluginworkspace
   */
  @SuppressWarnings("serial")
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
    
    //create refresh action
    Icon icon = (Icon) workspaceAccess.getImageUtilities().loadIcon(ro.sync.exml.Oxygen.class.getResource(Icons.REFRESH));
    refreshAction = new AbstractAction(translator.getTranslation(Tags.REFRESH_INCOMING_REFERENCES),icon) {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        graph = null;
        refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA));
      }
    };
  }
  
  /**
   * Notify about the selected tab
   * @param selected true if incoming tab is selected, false otherwise
   */
  public void setTabSelected(boolean selected) {
    if(selected) {
      refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA));
    }
  }
  
  /**
   * Refreshes the references in the given editor
   * @param workspaceAccess The WSEditor
   */
  public synchronized void refresh(WSEditor workspaceAccess) {
    if(workspaceAccess != null) {

      URL editorLocation = workspaceAccess.getEditorLocation();
      refresh(editorLocation);
    } else {
      resetTree();
    }
  }
  
 /**
  * Refreshes the references in the given editor
  * @param editorLocation The location of the editor to be refreshed
  */
  private synchronized void refresh(URL editorLocation) {
    refreshTimer.schedule(new TimerTask() {
      
      @SuppressWarnings("serial")
      @Override
      public void run() {
        if(isShowing()) {
          referencesCategory.clear();
          List<IncomingReference> temp;
          try {
            updateInProgressStatus(true, 50);
            temp = searchIncomingRef(editorLocation);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(translator.getTranslation(Tags.INCOMING_REFERENCES));
            DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
              @Override
              public boolean isLeaf(Object node) { return false; } //NOSONAR
            };
            
            if(temp != null) {
              for (IncomingReference incomingReference : temp) {
                if(!referencesCategory.containsKey(getReferenceCategory(incomingReference.getDPI()))) {
                  referencesCategory.put(getReferenceCategory(incomingReference.getDPI()), new ArrayList<>());
                } 
                referencesCategory.get(getReferenceCategory(incomingReference.getDPI())).add(incomingReference);
              
              }
              for(ReferenceCategory referenceCategory : referencesCategory.keySet()) {
                DefaultMutableTreeNode ref = new DefaultMutableTreeNode(referenceCategory);
                root.add(ref);
              }
            } 
           
            SwingUtilities.invokeLater(() -> {
              if (root.getChildCount() == 0) {
                DefaultTreeModel noRefModel = new DefaultTreeModel(root);
                DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(translator.getTranslation(Tags.NO_INCOMING_REFERENCES_FOUND));
                root.add(noReferencesFound);
                referenceTree.setModel(noRefModel);
              } else {
                referenceTree.setModel(referencesTreeModel);
              }
            });
            
          } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e, e);
          } finally {
            updateInProgressStatus(false, 0);
          }
        }
      }
    }, 10);
   
  }
  
  /**
   * Resets the tree to empty model
   */
  private void resetTree() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    DefaultTreeModel noRefModel = new DefaultTreeModel(root);
    DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(translator.getTranslation(Tags.INCOMING_REFERENCES_NOT_AVAILABLE));
    root.add(noReferencesAvailable);
    SwingUtilities.invokeLater(() -> referenceTree.setModel(noRefModel));
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
        graph = createReferencesGraph.invoke(null);
      }
      Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class, Object.class);
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
            ref1.setShowExtraLineNumberInformation();
            ref2.setShowExtraLineNumberInformation();
          } 
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
      Object userObject =  node.getUserObject();
      if(userObject instanceof IncomingReference) {
        IncomingReference referenceInfo = (IncomingReference) userObject;
        try {
          URL url = new URL(referenceInfo.getSystemId());
          if(workspaceAccess.open(url)) {
            WSEditor editorAccess = workspaceAccess.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA);
            if(editorAccess != null) {
              WSEditorPage currentPage = editorAccess.getCurrentPage();
              refreshTimer.schedule(new TimerTask() {
                
                @Override
                public void run() {
                  SwingUtilities.invokeLater(() -> selectRange(currentPage, referenceInfo));
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
   * Copy the file location to clipboard.
   */
  private void copyFileLocationToClipboard() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) referenceTree.getLastSelectedPathComponent();
    if (node != null) {
      Object userObject =  node.getUserObject();
      if(userObject instanceof IncomingReference) {
        IncomingReference referenceInfo = (IncomingReference) userObject;
        StringSelection selection = new StringSelection(referenceInfo.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
      }
    }
  }
  
  
  /**
   * Install listeners to the tree 
   * @param workspaceAccess The plugin workspace
   */
  private void installListeners(PluginWorkspace workspaceAccess) {
    
    installInternalListeners(workspaceAccess);
    installExtrnalListeners(workspaceAccess);
    
  }
  
  /**
   * Installs tree's internal listeners
   * @param workspaceAccess The workspace access
   */
  private void installInternalListeners(PluginWorkspace workspaceAccess) {
    referenceTree.addTreeWillExpandListener(new TreeWillExpandListener() {


      /**
       * Add the children to current source node.
       *
       * @param source            The node source.
       * @param referenceInfo     The current IncomingReference instance.
       *
       * @throws ClassNotFoundException
       * @throws InvocationTargetException
       * @throws NoSuchMethodException
       * @throws IllegalAccessException
       */
      private void addChildren(DefaultMutableTreeNode source, IncomingReference referenceInfo)
              throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, MalformedURLException {
        List<IncomingReference> temp;
        URL editorLocation = new URL(referenceInfo.getSystemId());
        temp = searchIncomingRef(editorLocation);
        for (IncomingReference currentChild : temp) {
          source.add(new DefaultMutableTreeNode(currentChild));
        }

      }


      @Override
      public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
        
        if(source != null && source.getChildCount() == 0) {
          if(source.getUserObject() instanceof IncomingReference) {
            try {
              IncomingReference referenceInfo = (IncomingReference)(source.getUserObject());
              int occurencesCounter = getReferenceOccurences(source, referenceInfo);
              
              if(occurencesCounter < 2) {
                addChildren(source, referenceInfo);
              } else {
                //Avoid expanding the same system id on multiple levels in the same path
              }
              
            } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | IllegalAccessException
                    | InvocationTargetException
                    | MalformedURLException e1) {
              logger.error(e1, e1);
            } 
          } else if (source.getUserObject() instanceof ReferenceCategory) {
            ReferenceCategory referenceCategory = (ReferenceCategory) source.getUserObject();
            referencesCategory.get(referenceCategory).forEach(e -> source.add(new DefaultMutableTreeNode(e)));     
          }
        }
        
      }


      @Override
      public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        //not needed
      }
      
      
      /**
       * 
       * @param source          The source node.
       * @param referenceInfo   The current reference.
       * 
       * @return no of occurences for this reference.
       */
      private int getReferenceOccurences(DefaultMutableTreeNode source, IncomingReference referenceInfo) {
        TreeNode[] pathToRoot = ((DefaultTreeModel)referenceTree.getModel()).getPathToRoot(source);
        int occurencesCounter = 0;
        String currentNodeSystemID = referenceInfo.getSystemId();
       
        for (TreeNode treeNode : pathToRoot) {
          DefaultMutableTreeNode nodeInPath = (DefaultMutableTreeNode) treeNode;
          if(nodeInPath.getUserObject() instanceof IncomingReference) {
            IncomingReference referenceInPath = (IncomingReference) nodeInPath.getUserObject();
            if (currentNodeSystemID != null && currentNodeSystemID.equals(referenceInPath.getSystemId())) {
              occurencesCounter++;
            }
          }
        }
        
        return occurencesCounter;

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
   * Installs tree external listeners
   * @param workspaceAccess The workspace access
   */
  private void installExtrnalListeners(PluginWorkspace workspaceAccess) {
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
          menu.add(new AbstractAction(translator.getTranslation(Tags.COPY_LOCATION)) {

            @Override
            public void actionPerformed(ActionEvent e) {
              copyFileLocationToClipboard();
            }
          });
          
          menu.show(e1.getComponent(), e1.getX(), e1.getY());
        }
      }
    });
  }
  
  /**
   * If the list is in the loading process, a pending panel is displayed
   * 
   * @param inProgress <code>true</code> if the project is starting to load,
   *                   <code>false</code> if the project was loaded.
   * @param delay after which the function to be executed
   */
  private void updateInProgressStatus(final boolean inProgress, int delay) {
    if(loadingInProgressTask != null) {
      loadingInProgressTask.cancel();
      loadingInProgressTask = null;
    }
    loadingInProgressTask = new TimerTask() {
      @Override
      public void run() {
        try {
          SwingUtilities.invokeAndWait(() -> {

            if (inProgress) {
              loadingLabel.setText(translator.getTranslation(Tags.LOADING));
              // Display pending panel.
              cards.show(IncomingReferencesPanel.this, LOADING_ID);
            } else {
              // Display the result
              cards.show(IncomingReferencesPanel.this, ReferenceType.INCOMING.toString());
            }

          });  
        } catch(Exception e) {
          logger.error(e, e);
        }
      }};
      loadingInProgressTimer.schedule(loadingInProgressTask, delay);
  }
  
  /**
   * Get the action for the refresh button
   */
  public AbstractAction getRefereshAction() {
      return refreshAction;
  }
  
  
  /**
   * @param dpi for current incoming reference.
   * 
   * @return The reference category.
   */
  static ReferenceCategory getReferenceCategory(DocumentPositionedInfo dpi) {
    ReferenceCategory referenceCategory = ReferenceCategory.CROSS;
    if(dpi.getMessage().endsWith("[CONREF]") || dpi.getMessage().endsWith("[CONKEYREF]")) {
      referenceCategory = ReferenceCategory.CONTENT;
    } else if (dpi.getSystemID().endsWith(".ditamap")) {
      referenceCategory = ReferenceCategory.MAP;
    }
    
    return referenceCategory;
  }
}
