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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.tree.references.ReferenceType;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.ui.Icons;

/**
 * Present incoming references in a JTree
 * 
 * @author mircea_badoi
 *
 */
public class IncomingReferencesPanel extends JPanel {
	/**
	 * For translation.
	 */
	private static final DITAReferencesTranslator TRANSLATOR = new DITAReferencesTranslator();

	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesPanel.class.getName());

	/**
	 * JTree with ongoing references
	 */
	private final IncomingReferencesTree referenceTree;

	/**
	 * References graph
	 */
	private transient Object graph;

	/**
	 * The plugin workspace
	 */
	private final transient PluginWorkspace workspaceAccess;

	/**
	 * Timer for loading panel.
	 */
	private static final transient Timer loadingInProgressTimer = new Timer(false);

	/**
	 * Timer for loading panel.
	 */
	private static final Timer REFRESH_TIMER = new Timer(false);

	/**
	 * TimerTask for loading panel.
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
	private final transient EnumMap<ReferenceCategory, List<IncomingReference>> referencesMapCategory = new EnumMap<>(
			ReferenceCategory.class);

	
	/**
	 * Constructor
	 * 
	 * @param workspaceAccess The pluginworkspace
	 */
	@SuppressWarnings("serial")
	public IncomingReferencesPanel(PluginWorkspace workspaceAccess) {
		this.workspaceAccess = workspaceAccess;
		cards = new CardLayout();
		this.setLayout(cards);

		// add tree
		referenceTree = new IncomingReferencesTree(workspaceAccess, REFRESH_TIMER, graph, referencesMapCategory);
		ToolTipManager.sharedInstance().registerComponent(referenceTree);
		this.add(referenceTree, ReferenceType.INCOMING.toString());

		// creating loading panel
		JPanel loadingPanel = new JPanel(new BorderLayout());
		loadingLabel = new JLabel(Icons.getIconAnimated(Icons.PROGRESS_IMAGE), SwingConstants.LEFT);
		loadingPanel.add(loadingLabel, BorderLayout.NORTH);
		this.add(loadingPanel, LOADING_ID);

		// install listener
		installListeners(workspaceAccess);

		// create refresh action
		Icon icon = (Icon) workspaceAccess.getImageUtilities()
				.loadIcon(ro.sync.exml.Oxygen.class.getResource(Icons.REFRESH));
		refreshAction = new AbstractAction(TRANSLATOR.getTranslation(Tags.REFRESH_INCOMING_REFERENCES), icon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				graph = null;
				refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA));
			}
		};
	}

	
	/**
	 * Notify about the selected tab
	 * 
	 * @param selected true if incoming tab is selected, false otherwise
	 */
	public void setTabSelected(boolean selected) {
		if (selected) {
			refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA));
		}
	}

	
	/**
	 * Refreshes the references in the given editor
	 * 
	 * @param workspaceAccess The WSEditor
	 */
	public synchronized void refresh(WSEditor workspaceAccess) {
		if (workspaceAccess != null) {

			URL editorLocation = workspaceAccess.getEditorLocation();
			refresh(editorLocation);
		} else {
			referenceTree.reset();
		}
	}

	
	/**
	 * Refreshes the references in the given editor
	 * 
	 * @param editorLocation The location of the editor to be refreshed
	 */
	private synchronized void refresh(URL editorLocation) {
		REFRESH_TIMER.schedule(new TimerTask() {

			@SuppressWarnings("serial")
			@Override
			public void run() {
				if (isShowing()) {
					referencesMapCategory.clear();
					List<IncomingReference> temp;
					try {
						updateInProgressStatus(true, 50);
						temp = IncomingReferenceUtil.searchIncomingRef(editorLocation, graph);
						DefaultMutableTreeNode root = new DefaultMutableTreeNode(
								TRANSLATOR.getTranslation(Tags.INCOMING_REFERENCES));
						DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root) {
							@Override
							public boolean isLeaf(Object node) {
								return false;
							} // NOSONAR
						};

						if (temp != null) {
							IncomingReferenceUtil.addReferencesCategoriesToNode(temp, referencesMapCategory, root);
							
							SwingUtilities.invokeLater(() -> {
								if (root.getChildCount() == 0) {
									DefaultTreeModel noRefModel = new DefaultTreeModel(root);
									referenceTree.setModel(noRefModel);
								} else {
									referenceTree.setModel(referencesTreeModel);
								}

								IncomingReferenceUtil.expandFirstLevelOfTree(root, referenceTree);
							});

						}

					} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
							| InvocationTargetException e) {
						LOGGER.error(e, e);
					} finally {
						updateInProgressStatus(false, 0);
					}
				}
			}
		}, 10);

	}


	/**
	 * Install listeners to the tree
	 * 
	 * @param workspaceAccess The plugin workspace
	 */
	private void installListeners(PluginWorkspace workspaceAccess) {
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
	 * @param delay      after which the function to be executed
	 */
	private void updateInProgressStatus(final boolean inProgress, int delay) {
		if (loadingInProgressTask != null) {
			loadingInProgressTask.cancel();
			loadingInProgressTask = null;
		}
		loadingInProgressTask = new TimerTask() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeAndWait(() -> {

						if (inProgress) {
							loadingLabel.setText(TRANSLATOR.getTranslation(Tags.LOADING));
							// Display pending panel.
							cards.show(IncomingReferencesPanel.this, LOADING_ID);
						} else {
							// Display the result
							cards.show(IncomingReferencesPanel.this, ReferenceType.INCOMING.toString());
						}

					});
				} catch (Exception e) {
					LOGGER.error(e, e);
				}
			}
		};
		loadingInProgressTimer.schedule(loadingInProgressTask, delay);
	}
	

	/**
	 * Get the action for the refresh button
	 */
	public AbstractAction getRefereshAction() {
		return refreshAction;
	}

	
}
