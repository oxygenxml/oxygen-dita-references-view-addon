package com.oxygenxml.ditareferences.workspace.textpage;

import java.util.function.Supplier;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.oxygenxml.ditareferences.workspace.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.ReferencesTreeCaretListener;
import com.oxygenxml.ditareferences.workspace.TreeSelectionInhibitor;

import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;

public class TextPageReferencesTreeCaretListener extends ReferencesTreeCaretListener<WSXMLTextEditorPage> implements CaretListener {

	/**
	 * Construct the CaretListener for TextPage.
	 * 
	 * @param textPage           The XML TextPage
	 * @param refTree            The referencesTree
	 * @param selectionInhibitor The selectionInhibitor
	 */
	public TextPageReferencesTreeCaretListener(Supplier<WSXMLTextEditorPage> textPage, ReferencesTree refTree,
			TreeSelectionInhibitor selectionInhibitor) {
		super(textPage, refTree, selectionInhibitor);
	}

	@Override
	protected int getCaretOffset() {
		if (editorPage.get() != null) {
			return editorPage.get().getCaretOffset();
		}
		return -1;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		caretUpdate();
	}

	/**
	 * Remove the caret listener
	 */
	public void unbindTextPageWithCaret() {
		if(editorPage != null && editorPage.get() != null) {
			JTextArea currentTextComponent = (JTextArea) editorPage.get().getTextComponent();
			currentTextComponent.removeCaretListener(this);
		}
	}
	
	/**
	 * Bind the current TextPage with its current Caret Listener.
	 */
	public void bindTextPageWithCaret() {
		unbindTextPageWithCaret();
		JTextArea currentTextComponent = (JTextArea) editorPage.get().getTextComponent();
		currentTextComponent.addCaretListener(this);
	}
}
