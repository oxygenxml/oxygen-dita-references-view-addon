package com.oxygenxml.ditareferences.workspace;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ShowDefinitionLocationAction extends AbstractAction {

	private RelLinkNodeRange relLinkNodeRange;
	private WSEditor editorAccess;
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * Construct the Action for showing the definition location.
	 * 
	 * @param relLinkNodeRange     The relLinkNodeRange
	 * @param editorAccess         The current editorAccess
	 * @param pluginWorkspaceAcces The pluginWorkspaceAccess
	 * @param showDefLocationName  The name for show definition location
	 */
	public ShowDefinitionLocationAction(RelLinkNodeRange relLinkNodeRange, WSEditor editorAccess,
			StandalonePluginWorkspace pluginWorkspaceAcces, String showDefLocationName) {
		super(showDefLocationName);

		this.relLinkNodeRange = relLinkNodeRange;
		this.editorAccess = editorAccess;
		this.pluginWorkspaceAccess = pluginWorkspaceAcces;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
