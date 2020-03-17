package com.oxygenxml.ditareferences.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.oxygenxml.ditareferences.translator.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.translator.Tags;
import com.oxygenxml.ditareferences.workspace.authorpage.AuthorPageListener;
import com.oxygenxml.ditareferences.workspace.textpage.TextPageListener;

import ro.sync.ecss.dita.DITAAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * DITA Outgoing References Plugin extension.
 * 
 * @author Alexandra_Dinisor
 */
public class DITAReferencesWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

	/* View Id; defined in plugin.xml. */
	private static final String DITA_REFERENCES_WORKSPACE_ACCESS_ID = "DITAReferencesView";

	/* Workspace access. */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/* Provider of keys for the current DITA Map. */
	private KeysProvider keysProvider = editorLocation -> DITAAccess.getKeys(editorLocation);

	/* The tree with the outgoing references. */
	private ReferencesTree refTree;

	/* The timer for editor changes. */
	private static final int TIMER_DELAY = 500;
	private ActionListener timerListener = new EditorChangesTimerListener();
	private Timer updateTreeTimer = new Timer(TIMER_DELAY, timerListener);

	/* The DITA references translator for the side-view label. */
	private DITAReferencesTranslator translator = new DITAReferencesTranslator();

	/* Document Listener to update the ReferencesTree for TextPage. */
	private TextPageListener textPageDocumentListener = new TextPageListener(updateTreeTimer);

	/* Author Listener to update the ReferencesTree for AuthorPage. */
	private AuthorPageListener authorPageListener = new AuthorPageListener(updateTreeTimer);

	/**
	 * Let the application start.
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.refTree = new ReferencesTree(pluginWorkspaceAccess, keysProvider, translator);

		pluginWorkspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
						
			@Override
			public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
				return true;
			}

			/**
			 * Update the referencesTree when a DITA editor is opened.
			 */
			@Override
			public void editorOpened(URL editorLocation) {
					updateTreeTimer.setRepeats(false);
					WSEditor editorAccess = pluginWorkspaceAccess.getEditorAccess(editorLocation,
							PluginWorkspace.MAIN_EDITING_AREA);
					if (editorAccess != null) {
						if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
							// update listener for TextPage when editor opened in Text Mode
							updateTreeTimer.restart();
							WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
							textPage.getDocument().addDocumentListener(textPageDocumentListener);

						} else if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
							// update listener for AuthorPage when editor opened in Author Mode
							updateTreeTimer.restart();
							WSAuthorEditorPage authorPage = (WSAuthorEditorPage) editorAccess.getCurrentPage();
							authorPage.getDocumentController().addAuthorListener(authorPageListener);

						} else {
							updateTreeTimer.restart();
						}
					}
			}

			@Override
			public void editorClosed(URL editorLocation) {
				// An edited XML document has been closed.
			}

			@Override
			public boolean editorAboutToBeClosed(URL editorLocation) {
				return true;
			}

			@Override
			public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
				// The editor was relocated (Save as was called).
			}

			/**
			 * Update the RefrencesTree when page changes with coalescing.
			 */
			@Override
			public void editorPageChanged(URL editorLocation) {
				updateTreeTimer.setRepeats(false);
				WSEditor editorAccess = pluginWorkspaceAccess.getEditorAccess(editorLocation,
						PluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						if (refTree.isShowing()) {
							updateTreeTimer.restart();
							WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();

							// update the textPage Listener
							textPage.getDocument().removeDocumentListener(textPageDocumentListener);
							textPage.getDocument().addDocumentListener(textPageDocumentListener);
						}

					} else if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						updateTreeTimer.restart();
						if (refTree.isShowing()) {
							WSAuthorEditorPage authorPage = (WSAuthorEditorPage) editorAccess.getCurrentPage();

							// update the authorPage Listener
							authorPage.getDocumentController().removeAuthorListener(authorPageListener);
							authorPage.getDocumentController().addAuthorListener(authorPageListener);
						}
					} else {
						updateTreeTimer.restart();
					}
				}
			}

			@Override
			public void editorSelected(URL editorLocation) {
				bindTreeWithEditor(editorLocation);
			}

			@Override
			public void editorActivated(URL editorLocation) {
				bindTreeWithEditor(editorLocation);
			}

		}, PluginWorkspace.MAIN_EDITING_AREA);
		
		/**
		 * Add Icon, Title and ScrollPane for side-view. ScrollPane should let the whole
		 * part of node text to be painted in the Layout without adding extra "...".
		 */
		pluginWorkspaceAccess.addViewComponentCustomizer(viewInfo -> {
			if (DITA_REFERENCES_WORKSPACE_ACCESS_ID.equals(viewInfo.getViewID())) {
				
				JScrollPane scrollPane = new JScrollPane(refTree);
				scrollPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						SwingUtilities.invokeLater(() -> {
							// remember the selected path
							TreePath selectionPath = refTree.getSelectionPath();
							((DefaultTreeModel) refTree.getModel())
									.nodeStructureChanged((TreeNode) refTree.getModel().getRoot());

							// expand all rows with same selected path after side-view scrolled
							refTree.expandAllRows();
							refTree.setSelectionPath(selectionPath);
						});

					}
				});
				// set side-view ScrollPane
				viewInfo.setComponent(scrollPane);

				// set side-view Title
				viewInfo.setTitle(translator.getTranslation(Tags.DITA_REFERENCES));

				// set side-view Icon
				URL iconURL = getClass().getClassLoader().getResource(Icons.DITA_REFERENCES);
				if (iconURL != null) {
					ImageUtilities imageUtilities = pluginWorkspaceAccess.getImageUtilities();
					ImageIcon icon = (ImageIcon) imageUtilities.loadIcon(iconURL);
					viewInfo.setIcon(icon);
				}
			}

		});
	}

	/**
	 * Let the application close.
	 */
	@Override
	public boolean applicationClosing() {
		return true;
	}

	/**
	 * Refresh the referencesTree in its own class. Depends on the location the
	 * method is called from. For example: "editorSelected(URL location)" or
	 * <code>null</code> directly from EditorChangesTimerListener.
	 * 
	 * @param editorLocation The editorLocation
	 */
	protected void bindTreeWithEditor(URL editorLocation) {
		WSEditor editorAccess = null;
		if (editorLocation != null) {
			editorAccess = pluginWorkspaceAccess.getEditorAccess(editorLocation,
					PluginWorkspace.MAIN_EDITING_AREA);
		} else {
			editorAccess = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
		}

			refTree.refreshReferenceTree(editorAccess);
	}

	/**
	 * Inner TimerListener for EditorChanges in ReferencesTree. Notify the tree
	 * about change in the textPage.
	 * 
	 * @author Alexandra_Dinisor
	 */
	class EditorChangesTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			bindTreeWithEditor(null);
		}
	}
	
}