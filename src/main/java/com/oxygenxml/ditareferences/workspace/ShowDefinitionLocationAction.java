package com.oxygenxml.ditareferences.workspace;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRange;

import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

@SuppressWarnings("serial")
public class ShowDefinitionLocationAction extends AbstractAction {

	/**
	 * Imposed Content Type for DITA Map definition Location.
	 */
	private static final String TEXT_XML = "text/xml";
	
	private RelLinkNodeRange relLinkNodeRange;
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * Construct the Action for showing the definition location.
	 * 
	 * @param relLinkNodeRange     The relLinkNodeRange
	 * @param pluginWorkspaceAcces The pluginWorkspaceAccess
	 * @param showDefLocationName  The name for show definition location
	 */
	public ShowDefinitionLocationAction(RelLinkNodeRange relLinkNodeRange,
			StandalonePluginWorkspace pluginWorkspaceAcces, String showDefLocationName) {
		super(showDefLocationName);

		this.relLinkNodeRange = relLinkNodeRange;
		this.pluginWorkspaceAccess = pluginWorkspaceAcces;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		URL definitionLocationUrl = relLinkNodeRange.getTargetDefinitionLocation();
		// open in DITA Map directly the corresponding definition location
		pluginWorkspaceAccess.open(definitionLocationUrl, null, TEXT_XML);
	}

}
