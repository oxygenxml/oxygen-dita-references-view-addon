package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;
import com.oxygenxml.ditareferences.tree.references.VersionUtil;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Tree;

/**
 * A tree to store the incoming references.
 * 
 * @author Alex_Smarandache
 */
@SuppressWarnings("serial")
public class IncomingReferencesTree extends Tree {
	
	/**
	 * Constant used for java reflexion.
	 */
	private static final String DITA_ACCESS_CLASS_NAME = "ro.sync.ecss.dita.DITAAccess";
	/**
	 * For translation.
	 */
	private static final Translator TRANSLATOR = DITAReferencesTranslator.getInstance();
	
	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesTree.class.getName());
	
	/**
	 * The graph.
	 */
	private transient Object graph;
	
	/**
	 * Timer for loading panel.
	 */
	private static final Timer REFRESH_TIMER = new Timer(false);
	

	/**
	 * Constructor.
	 * 
	 * @param workspaceAccess  The Pluginworkspace.
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
		
		// add expansion listener
		this.addTreeWillExpandListener(new IncomingReferenceTreeWillExpandListener(this));
		
		//add action listeners
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
						DefaultMutableTreeNode source = (DefaultMutableTreeNode)selPath.getLastPathComponent();
						boolean menuShouldBeShow = source.getUserObject() instanceof IncomingReference;
						if(menuShouldBeShow) {
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
				}
			}
		});
		
	}

	
	/**
	 * Refreshes the references in the given editor.
	 * 
	 * @param editorLocation The location of the editor to be refreshed.
	 */
	synchronized void refresh(URL editorLocation, ProgressStatusListener progressStatus, boolean clearCache) {
		if(clearCache) {
			graph = null;
		}
		REFRESH_TIMER.schedule(new TimerTask() {

			@Override
			public void run() {
				if (isShowing()) {
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
							IncomingReferenceUtil.addReferencesCategoriesToRoot(temp, root);
							
							SwingUtilities.invokeLater(() -> {
								if (root.getChildCount() == 0) {
									DefaultTreeModel noRefModel = new DefaultTreeModel(root);
									DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(TRANSLATOR.getTranslation(Tags.NO_INCOMING_REFERENCES_FOUND));
					                root.add(noReferencesFound);
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
	 * Search for ongoing references and compute the label for them.
	 * 
	 * @param editorLocation The editor to search location.
	 * @param graph          The graph to be created.
	 * 
	 * @return The list of found ongoing references.
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
    List<IncomingReference> searchIncomingRef(URL editorLocation)
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
