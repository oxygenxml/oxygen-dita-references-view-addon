package com.oxygenxml.ditareferences.tree.references.incoming;

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
import java.util.EnumMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
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
import com.oxygenxml.ditareferences.tree.references.VersionUtil;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Tree;

/**
 * A tree to store the incoming references.
 * 
 * @author Alex_Smarandache
 */
public class IncomingReferencesTree extends Tree {
	
	/**
	 * Constant used for java reflexion
	 */
	private static final String DITA_ACCESS_CLASS_NAME = "ro.sync.ecss.dita.DITAAccess";
	/**
	 * For translation.
	 */
	private static final DITAReferencesTranslator TRANSLATOR = new DITAReferencesTranslator();
	
	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesPanel.class.getName());
	
	/**
	 * The graph.
	 */
	private transient Object graph;
	
	/**
	 * Timer for loading panel.
	 */
	private static final Timer REFRESH_TIMER = new Timer(false);

	/**
	 * Contains initial references category and their children.
	 */
	private final transient EnumMap<ReferenceCategory, List<IncomingReference>> referencesMapCategory = new EnumMap<>(
			ReferenceCategory.class);
	
	/**
	 * Constructor.
	 * 
	 * @param workspaceAccess           The Pluginworkspace.
	 */
	public IncomingReferencesTree(PluginWorkspace workspaceAccess) {
		setToggleClickCount(0);
		setRootVisible(false);
	    setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new IncomingReferencesTreeCellRenderer(workspaceAccess.getImageUtilities()));
		
		installListeners(workspaceAccess);
		
		ToolTipManager.sharedInstance().registerComponent(this);
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
		installExpansionListener(workspaceAccess);
		installActionListeners(workspaceAccess);
	}

	
	/**
	 * Installs tree's internal listeners
	 * 
	 * @param workspaceAccess The workspace access
	 */
	private void installExpansionListener(PluginWorkspace workspaceAccess) {
		addTreeWillExpandListener(new TreeWillExpandListener() {

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
				temp = searchIncomingRef(editorLocation);
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
				TreeNode[] pathToRoot = ((DefaultTreeModel) IncomingReferencesTree.this.getModel()).getPathToRoot(source);
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
	private void installActionListeners(PluginWorkspace workspaceAccess) {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_ENTER == e.getKeyCode()) {
					e.consume();
					IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, IncomingReferencesTree.this, REFRESH_TIMER);
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				e.consume();
				if (e.getClickCount() == 2) {
					IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, IncomingReferencesTree.this, REFRESH_TIMER);
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
					int selRow = IncomingReferencesTree.this.getRowForLocation(e1.getX(), e1.getY());
					TreePath selPath = IncomingReferencesTree.this.getPathForLocation(e1.getX(), e1.getY());
					if (selPath != null) {
						IncomingReferencesTree.this.setSelectionPath(selPath);
						if (selRow > -1) {
							IncomingReferencesTree.this.setSelectionRow(selRow);
						}
					}
					JPopupMenu menu = new JPopupMenu();
					menu.add(new AbstractAction(TRANSLATOR.getTranslation(Tags.OPEN_REFERENCE)) {

						@Override
						public void actionPerformed(ActionEvent e) {
							IncomingReferenceUtil.openFileAndSelectReference(workspaceAccess, IncomingReferencesTree.this, REFRESH_TIMER);
						}
					});
					menu.add(new AbstractAction(TRANSLATOR.getTranslation(Tags.COPY_LOCATION)) {

						@Override
						public void actionPerformed(ActionEvent e) {
							IncomingReferenceUtil.copyFileLocationToClipboard(IncomingReferencesTree.this);
						}
					});

					menu.show(e1.getComponent(), e1.getX(), e1.getY());
				}
			}
		});
	}

	/**
	 * Refreshes the references in the given editor
	 * 
	 * @param editorLocation The location of the editor to be refreshed
	 */
	synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		if(clearCache) {
			graph = null;
		}
		REFRESH_TIMER.schedule(new TimerTask() {

			@SuppressWarnings("serial")
			@Override
			public void run() {
				if (isShowing()) {
					referencesMapCategory.clear();
					List<IncomingReference> temp;
					try {
						progressStatus.updateInProgressStatus(true, 50);
						temp = searchIncomingRef(editorLocation);
						DefaultMutableTreeNode root = new DefaultMutableTreeNode(
								TRANSLATOR.getTranslation(Tags.INCOMING_REFERENCES));
						DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
							@Override
							public boolean isLeaf(Object node) {
								return false;
							} // NOSONAR
						};

						if (temp != null) {
							IncomingReferenceUtil.addReferencesCategoriesToRoot(temp, referencesMapCategory, root);
							
							SwingUtilities.invokeLater(() -> {
								if (root.getChildCount() == 0) {
									DefaultTreeModel noRefModel = new DefaultTreeModel(root);
									setModel(noRefModel);
								} else {
									setModel(referencesTreeModel);
								}

								IncomingReferenceUtil.expandFirstLevelOfTree(root, IncomingReferencesTree.this);
							});

						}

					} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
							| InvocationTargetException e) {
						LOGGER.error(e, e);
					} finally {
						progressStatus.updateInProgressStatus(false, 0);
					}
				}
			}
		}, 10);

	}
	
	/**
	 * Search for ongoing references and compute the label for them
	 * 
	 * @param editorLocation The editor to search location.
	 * @param graph          The graph to be created.
	 * 
	 * @return The list of found ongoing references
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	private List<IncomingReference> searchIncomingRef(URL editorLocation)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		List<IncomingReference> incomingReferences = null;
		
		if (VersionUtil.isOxygenVersionNewer(23, 0)) {
			Class<?> ditaAccess = Class.forName(DITA_ACCESS_CLASS_NAME);
			if (graph == null) {
				Method createReferencesGraph = ditaAccess
						.getDeclaredMethod(VersionUtil.METHOD_NAME_CREATE_REFERENCE_GRAPH);
				graph = createReferencesGraph.invoke(null);
			}
			Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class,
					Object.class);
			incomingReferences = new ArrayList<>();
			List<DocumentPositionedInfo> result;
			result = (List<DocumentPositionedInfo>) searchReferences.invoke(null, editorLocation, graph);
			for (DocumentPositionedInfo documentPositionedInfo : result) {
				incomingReferences.add(new IncomingReference(documentPositionedInfo));
			}
			Collections.sort(incomingReferences);
			for (int i = 1; i < incomingReferences.size(); i++) {
				IncomingReference ref1 = incomingReferences.get(i - 1);
				IncomingReference ref2 = incomingReferences.get(i);
				if (ref1.getSystemId().equals(ref2.getSystemId())) {
					ref1.setShowExtraLineNumberInformation();
					ref2.setShowExtraLineNumberInformation();
				}
			}
		}
		
		return incomingReferences;
	}
}
