package com.oxygenxml.sdksamples.workspace;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import com.oxygenxml.sdksamples.translator.DITAReferencesTranslator;
import com.oxygenxml.sdksamples.translator.Tags;

import ro.sync.ecss.dita.DITAAccess;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.WSEditor;
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

	/**
	 * The DITA references translator for the side-view label.
	 */
	private DITAReferencesTranslator translator = new DITAReferencesTranslator();

	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.refTree = new ReferencesTree(pluginWorkspaceAccess, keysProvider, translator);

		pluginWorkspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
				// You can reject here the opening of an URL if you want
				return true;
			}

			@Override
			public void editorOpened(URL editorLocation) {
			}

			@Override
			public void editorClosed(URL editorLocation) {
				// An edited XML document has been closed.
			}

			@Override
			public boolean editorAboutToBeClosed(URL editorLocation) {
				// You can veto the closing of an XML document.
				// Allow close
				return true;
			}

			/**
			 * The editor was relocated (Save as was called).
			 */
			@Override
			public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
			}

			@Override
			public void editorPageChanged(URL editorLocation) {
				refreshTreeInternal(editorLocation);
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
	 * Refresh the tree in its class
	 * 
	 * @param editorLocation
	 */
	void refreshTreeInternal(URL editorLocation) {
		WSEditor editorAccess = pluginWorkspaceAccess
				.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
		refTree.refreshReferenceTree(editorAccess);
	}

}