/**
 * 
 */
package com.oxygenxml.sdksamples.workspace.textpage;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * DocumentListener for TextPageListener.
 * 
 * @author Alexandra_Dinisor
 *
 */
public class TextPageListener implements DocumentListener {

	/* Timer for changes in TextPage. */
	private Timer updateTreeTimer;

	/**
	 * Construct the TextPage Listener.
	 * 
	 * @param updateTreeTimer The updateTreeTimer
	 */
	public TextPageListener(Timer updateTreeTimer) {
		this.updateTreeTimer = updateTreeTimer;
	}

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

}
