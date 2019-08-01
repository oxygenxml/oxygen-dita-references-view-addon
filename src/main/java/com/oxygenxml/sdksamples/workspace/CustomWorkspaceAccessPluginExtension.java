package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.oxygenxml.sdksamples.translator.DITAReferencesTranslator;
import com.oxygenxml.sdksamples.translator.Tags;

import ro.sync.ecss.dita.DITAAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;

/**
 * Plugin extension - workspace access extension.
 * 
 * @author Alexandra_Dinisor
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * Provider of keys for the current DITAMAP.
	 */
	private KeysProvider keysProvider = editorLocation -> DITAAccess.getKeys(editorLocation);

	/**
	 * The tree with the outgoing references.
	 */
	private ReferencesTree refTree;
	
	private static final int TIMER_DELAY = 500;
	private ActionListener timerListener = new TimerListener();
	private Timer updateTreeTimer = new Timer(TIMER_DELAY, timerListener);

	/**
	 * The DITA references translator for the side-view label.
	 */
	private DITAReferencesTranslator translator = new DITAReferencesTranslator();

	/**
	 * Document Listener to update the ReferencesTree for Text Page.
	 */
	private DocumentListener textPageDocumentListener = new DocumentListener() {
		@Override
		public void removeUpdate(DocumentEvent e) {
			updateTreeTimer.restart();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateTreeTimer.restart();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateTreeTimer.restart();
		}
	};

	/**
	 * 
	 */
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.refTree = new ReferencesTree(pluginWorkspaceAccess, keysProvider, translator);
		
		pluginWorkspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
						
			@Override
			public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
				return true;
			}

			@Override
			public void editorOpened(URL editorLocation) {
				updateTreeTimer.setRepeats(false);
				WSEditor editorAccess = pluginWorkspaceAccess.getEditorAccess(editorLocation,
						PluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage currentPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						currentPage.getDocument().addDocumentListener(textPageDocumentListener);
					} else if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						updateTreeTimer.restart();
					} else {
						updateTreeTimer.restart();
					}
				}
			}

			@Override
			public void editorClosed(URL editorLocation) {
			}

			@Override
			public boolean editorAboutToBeClosed(URL editorLocation) {
				return true;
			}

			@Override
			public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
			}

			/**
			 * Add Listener when page changes to refresh the ReferencesTree with coalescing.
			 */
			@Override
			public void editorPageChanged(URL editorLocation) {
				updateTreeTimer.setRepeats(false);
				WSEditor editorAccess = pluginWorkspaceAccess.getEditorAccess(editorLocation,
						PluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						updateTreeTimer.restart();
						WSTextEditorPage currentPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						// update the textPage Listener 
						currentPage.getDocument().removeDocumentListener(textPageDocumentListener);
						currentPage.getDocument().addDocumentListener(textPageDocumentListener);

					} else if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						updateTreeTimer.restart();
					} else {
						updateTreeTimer.restart();
					}
				}
			}

			@Override
			public void editorSelected(URL editorLocation) {
				refreshTreeInternal(editorLocation);
			}

			@Override
			public void editorActivated(URL editorLocation) {
				refreshTreeInternal(editorLocation);
			}
			
		}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

		pluginWorkspaceAccess.addViewComponentCustomizer(new ViewComponentCustomizer() {
			public void customizeView(ViewInfo viewInfo) {

				if ("ReferencesWorkspaceAccessID".equals(viewInfo.getViewID())) {
					viewInfo.setComponent(new JScrollPane(refTree));
					viewInfo.setTitle(translator.getTranslation(Tags.DITA_REFERENCES));
					ImageIcon DITARefIcon = new ImageIcon(
							getClass().getClassLoader().getResource("images/RefreshReferences16_dark.png"));
					viewInfo.setIcon(DITARefIcon);
				}
			}
		});
	}

	public boolean applicationClosing() {
		// You can reject the application closing here
		return true;
	}

	/**
	 * Refresh the tree in its own class.
	 * 
	 * @param editorLocation
	 */
	void refreshTreeInternal(URL editorLocation) {
		WSEditor editorAccess = pluginWorkspaceAccess
				.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
		refTree.refreshReferenceTree(editorAccess);
	}

	/**
	 * Inner TimerListener for ReferencesTree. Notify the tree about the change in
	 * the textPage.
	 * 
	 * @author Alexandra_Dinisor
	 */
	class TimerListener implements ActionListener {
		private URL editorLocation;

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshTreeInternal(editorLocation);
		}
	}
	
}