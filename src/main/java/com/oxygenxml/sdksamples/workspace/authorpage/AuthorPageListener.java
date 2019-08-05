/**
 * 
 */
package com.oxygenxml.sdksamples.workspace.authorpage;

import javax.swing.Timer;

import ro.sync.ecss.extensions.api.AttributeChangedEvent;
import ro.sync.ecss.extensions.api.AuthorListener;
import ro.sync.ecss.extensions.api.DocumentContentDeletedEvent;
import ro.sync.ecss.extensions.api.DocumentContentInsertedEvent;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorNode;

/**
 * @author Alexandra_Dinisor
 *
 */
public class AuthorPageListener implements AuthorListener {
	/**
	 * Timer for changes in AuthorPage.
	 */
	private Timer updateTreeTimer;

	/**
	 * Construct the AuthorPage Listener.
	 * 
	 * @param updateTreeTimer
	 */
	public AuthorPageListener(Timer updateTreeTimer) {
		super();
		this.updateTreeTimer = updateTreeTimer;
	}

	@Override
	public void documentChanged(AuthorDocument oldDocument, AuthorDocument newDocument) {
		updateTreeTimer.restart();

	}

	@Override
	public void doctypeChanged() {
		updateTreeTimer.restart();

	}

	@Override
	public void contentInserted(DocumentContentInsertedEvent e) {
		updateTreeTimer.restart();

	}

	@Override
	public void contentDeleted(DocumentContentDeletedEvent e) {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeDoctypeChange() {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeContentInsert(DocumentContentInsertedEvent e) {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeContentDelete(DocumentContentDeletedEvent e) {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeAuthorNodeStructureChange(AuthorNode authorNode) {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeAuthorNodeNameChange(AuthorNode authorNode) {
		updateTreeTimer.restart();

	}

	@Override
	public void beforeAttributeChange(AttributeChangedEvent e) {
		updateTreeTimer.restart();

	}

	@Override
	public void authorNodeStructureChanged(AuthorNode node) {
		updateTreeTimer.restart();

	}

	@Override
	public void authorNodeNameChanged(AuthorNode node) {
		updateTreeTimer.restart();

	}

	@Override
	public void attributeChanged(AttributeChangedEvent e) {
		updateTreeTimer.restart();

	}

}
