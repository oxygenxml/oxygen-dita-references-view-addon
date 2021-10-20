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
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;
import com.oxygenxml.ditareferences.i18n.Translator;
import com.oxygenxml.ditareferences.tree.references.ReferenceType;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.ui.Icons;

/**
 * Present incoming references in a JTree.
 * 
 * @author mircea_badoi
 *
 */
public class IncomingReferencesPanel extends JPanel implements ProgressStatusListener {
	/**
	 * For translation.
	 */
	private static final Translator TRANSLATOR = DITAReferencesTranslator.getInstance();

	/**
	 * Logger for logging.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferencesPanel.class.getName());

	/**
	 * JTree with ongoing references.
	 */
	private final IncomingReferencesTree referenceTree;

	/**
	 * The plugin workspace
	 */
	private final transient PluginWorkspace workspaceAccess;

	/**
	 * Timer for loading panel.
	 */
	private static final transient Timer loadingInProgressTimer = new Timer(false);

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
	 * Refresh action.
	 */
	private final AbstractAction refreshAction;
	
	
	/**
	 * Constructor.
	 * 
	 * @param workspaceAccess The pluginworkspace
	 */
	@SuppressWarnings("serial")
	public IncomingReferencesPanel(PluginWorkspace workspaceAccess) {
		this.workspaceAccess = workspaceAccess;
		cards = new CardLayout();
		this.setLayout(cards);

		// add tree
		referenceTree = new IncomingReferencesTree(workspaceAccess);
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
				refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA), true);
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
			refresh(workspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA), false);
		}
	}

	
	/**
	 * Refreshes the references in the given editor
	 * 
	 * @param workspaceAccess The WSEditor
	 */
	public synchronized void refresh(WSEditor workspaceAccess, boolean clearCache) {
		if (workspaceAccess != null) {
			URL editorLocation = workspaceAccess.getEditorLocation();
			referenceTree.refresh(editorLocation, this, clearCache);
		} else {
			referenceTree.reset();
		}
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
				referenceTree.refresh(editorLocation, IncomingReferencesPanel.this, true);
			}
		}, PluginWorkspace.DITA_MAPS_EDITING_AREA);

	}
	
	@Override
	public void updateInProgressStatus(final boolean inProgress, int delay) {
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
