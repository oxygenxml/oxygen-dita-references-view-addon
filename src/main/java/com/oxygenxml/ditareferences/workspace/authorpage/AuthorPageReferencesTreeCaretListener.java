package com.oxygenxml.ditareferences.workspace.authorpage;

import java.util.function.Supplier;

import com.oxygenxml.ditareferences.workspace.ReferencesTree;
import com.oxygenxml.ditareferences.workspace.ReferencesTreeCaretListener;
import com.oxygenxml.ditareferences.workspace.TreeSelectionInhibitor;

import ro.sync.ecss.extensions.api.AuthorCaretEvent;
import ro.sync.ecss.extensions.api.AuthorCaretListener;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

public class AuthorPageReferencesTreeCaretListener extends ReferencesTreeCaretListener<WSAuthorEditorPage> implements AuthorCaretListener {

	/**
	 * Construct the CaretListener for AuthorPage.
	 * 
	 * @param authorPage         The AuthorPage
	 * @param refTree            The References Tree
	 * @param selectionInhibitor The selectionInhibitor
	 */
	public AuthorPageReferencesTreeCaretListener(Supplier<WSAuthorEditorPage> authorPage, ReferencesTree refTree,
			TreeSelectionInhibitor selectionInhibitor) {
		super(authorPage, refTree, selectionInhibitor);
	}

	@Override
	protected int getCaretOffset() {
		if (editorPage.get() != null) {
			return editorPage.get().getCaretOffset();
		}
		return -1;
	}

	@Override
	public void caretMoved(AuthorCaretEvent caretEvent) {
		caretUpdate();
	}

	/**
	 * Bind the current AuthorPage with its current Caret Listener.
	 */
	public void bindAuthorPageWithCaret() {
		editorPage.get().removeAuthorCaretListener(this);
		editorPage.get().addAuthorCaretListener(this);
	}
}