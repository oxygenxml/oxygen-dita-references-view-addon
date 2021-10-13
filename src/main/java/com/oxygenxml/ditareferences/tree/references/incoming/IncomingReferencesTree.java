package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Tree;

/**
 * A tree to store the incoming references.
 * 
 * @author Alex_Smarandache
 */
public class IncomingReferencesTree extends Tree {
    
	/**
	 * For translation.
	 */
	private static final DITAReferencesTranslator TRANSLATOR = new DITAReferencesTranslator();
	
	/**
	 * Timer for loading panel.
	 */
	private final Timer refreshTimer;
	
	/**
	 * Contains initial references category and their children.
	 */
	private Map<ReferenceCategory, List<IncomingReference>> referencesMapCategory;
	
	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesPanel.class.getName());
	
	/**
	 * The graph.
	 */
	private final transient Object graph;
	
	
	/**
	 * Constructor.
	 * 
	 * @param workspaceAccess           The Pluginworkspace.
	 * @param refreshTimer              RefreshTimer.
	 * @param graph                     The graph.
	 * @param referencesMapCategory     Contains initial references category and their children.
	 */
	public IncomingReferencesTree(PluginWorkspace workspaceAccess, 
			Timer refreshTimer,
			Object graph,
			Map<ReferenceCategory, List<IncomingReference>> referencesMapCategory) {
		super();
		
		this.referencesMapCategory = referencesMapCategory;
		this.graph = graph;
		this.refreshTimer = refreshTimer;
		
		setToggleClickCount(0);
		setRootVisible(false);
	    setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new IncomingReferencesTreeCellRenderer(workspaceAccess.getImageUtilities()));
		
		installListeners(workspaceAccess);
	}
	
	
	/**
	 * Resets the tree to empty model.
	 */
	public void reset() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel noRefModel = new DefaultTreeModel(root);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(
				TRANSLATOR.getTranslation(Tags.INCOMING_REFERENCES_NOT_AVAILABLE));
		root.add(noReferencesAvailable);
		SwingUtilities.invokeLater(() -> this.setModel(noRefModel));
	}
	
	
	/**
	 * Install listeners to the tree.
	 * 
	 * @param workspaceAccess The plugin workspace
	 */
	private void installListeners(PluginWorkspace workspaceAccess) {
		installInternalListeners(workspaceAccess);
		installExtrnalListeners(workspaceAccess);
	}

	
	/**
	 * Installs tree's internal listeners
	 * 
	 * @param workspaceAccess The workspace access
	 */
	private void installInternalListeners(PluginWorkspace workspaceAccess) {
		JTree referenceTree = this;
		referenceTree.addTreeWillExpandListener(new TreeWillExpandListener() {

			/**
			 * Add the children to current source node.
			 *
			 * @param source        The node source.
			 * @param referenceInfo The current IncomingReference instance.
			 *
			 * @throws ClassNotFoundException
			 * @throws InvocationTargetException
			 * @throws NoSuchMethodException
			 * @throws IllegalAccessException
			 */
			private void addChildren(DefaultMutableTreeNode source, IncomingReference referenceInfo)
					throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException,
					IllegalAccessException, MalformedURLException {
				List<IncomingReference> temp;
				URL editorLocation = new URL(referenceInfo.getSystemId());
				temp = IncomingReferenceUtil.searchIncomingRef(editorLocation, graph);
				for (IncomingReference currentChild : temp) {
					source.add(new DefaultMutableTreeNode(currentChild));
				}

			}

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

				if (source != null && source.getChildCount() == 0) {
					if (source.getUserObject() instanceof IncomingReference) {
						try {
							IncomingReference referenceInfo = (IncomingReference) (source.getUserObject());
							int occurencesCounter = getReferenceOccurences(source, referenceInfo);

							if (occurencesCounter < 2) {
								addChildren(source, referenceInfo);
							} else {
								// Avoid expanding the same system id on multiple levels in the same path
							}

						} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
								| InvocationTargetException | MalformedURLException e1) {
							LOGGER.error(e1, e1);
						}
					} else if (source.getUserObject() instanceof ReferenceCategory) {
						ReferenceCategory referenceCategory = (ReferenceCategory) source.getUserObject();
						referencesMapCategory.get(referenceCategory)
								.forEach(e -> source.add(new DefaultMutableTreeNode(e)));
					}
				}

			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// not needed
			}

			/**
			 * 
			 * @param source        The source node.
			 * @param referenceInfo The current reference.
			 * 
			 * @return no of occurences for this reference.
			 */
			private int getReferenceOccurences(DefaultMutableTreeNode source, IncomingReference referenceInfo) {
				TreeNode[] pathToRoot = ((DefaultTreeModel) referenceTree.getModel()).getPathToRoot(source);
				int occurencesCounter = 0;
				String currentNodeSystemID = referenceInfo.getSystemId();

				for (TreeNode treeNode : pathToRoot) {
					DefaultMutableTreeNode nodeInPath = (DefaultMutableTreeNode) treeNode;
					if (nodeInPath.getUserObject() instanceof IncomingReference) {
						IncomingReference referenceInPath = (IncomingReference) nodeInPath.getUserObject();
						if (currentNodeSystemID != null && currentNodeSystemID.equals(referenceInPath.getSystemId())) {
							occurencesCounter++;
						}
					}
				}

				return occurencesCounter;
			}

		});

	}

	
	/**
	 * Installs tree external listeners
	 * 
	 * @param workspaceAccess The workspace access
	 */
	private void installExtrnalListeners(PluginWorkspace workspaceAccess) {
		JTree referenceTree = this; 
		referenceTree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					e.consume();
					IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, referenceTree, refreshTimer);
				}
			}
		});

		referenceTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				e.consume();
				if (e.getClickCount() == 2) {
					IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, referenceTree, refreshTimer);
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
				if (e1.isPopupTrigger()) {
					// select the corresponding item from tree
					int selRow = referenceTree.getRowForLocation(e1.getX(), e1.getY());
					TreePath selPath = referenceTree.getPathForLocation(e1.getX(), e1.getY());
					if (selPath != null) {
						referenceTree.setSelectionPath(selPath);
						if (selRow > -1) {
							referenceTree.setSelectionRow(selRow);
						}
					}
					JPopupMenu menu = new JPopupMenu();
					menu.add(new AbstractAction(TRANSLATOR.getTranslation(Tags.OPEN_REFERENCE)) {

						@Override
						public void actionPerformed(ActionEvent e) {
							IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, referenceTree, refreshTimer);
						}
					});
					menu.add(new AbstractAction(TRANSLATOR.getTranslation(Tags.COPY_LOCATION)) {

						@Override
						public void actionPerformed(ActionEvent e) {
							IncomingReferenceUtil.copyFileLocationToClipboard(referenceTree);
						}
					});

					menu.show(e1.getComponent(), e1.getX(), e1.getY());
				}
			}
		});
	}

	
}
