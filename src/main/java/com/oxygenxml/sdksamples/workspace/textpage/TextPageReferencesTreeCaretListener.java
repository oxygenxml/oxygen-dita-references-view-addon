package com.oxygenxml.sdksamples.workspace.textpage;

import java.util.function.Supplier;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.oxygenxml.sdksamples.workspace.ReferencesTree;
import com.oxygenxml.sdksamples.workspace.ReferencesTreeCaretListener;
import com.oxygenxml.sdksamples.workspace.TreeSelectionInhibitor;

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
		if (editorPage != null) {
			return editorPage.get().getCaretOffset();
		}
		return -1;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		caretUpdate();
	}

	/**
	 * Bind the current TextPage with its current Caret Listener.
	 */
	public void bind() {
		JTextArea currentTextComponent = (JTextArea) editorPage.get().getTextComponent();
		currentTextComponent.removeCaretListener(this);		
		currentTextComponent.addCaretListener(this);
	}
}
