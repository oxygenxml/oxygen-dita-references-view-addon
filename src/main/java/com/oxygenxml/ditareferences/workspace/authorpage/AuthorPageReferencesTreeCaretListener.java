package com.oxygenxml.ditareferences.workspace.authorpage;

import java.util.function.Supplier;

import com.oxygenxml.ditareferences.tree.references.OutgoingReferencesTree;
import com.oxygenxml.ditareferences.tree.references.ReferencesTreeCaretListener;
import com.oxygenxml.ditareferences.tree.references.TreeSelectionInhibitor;

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
	public AuthorPageReferencesTreeCaretListener(Supplier<WSAuthorEditorPage> authorPage, OutgoingReferencesTree refTree,
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
	 * Unbind the current AuthorPage with its current Caret Listener.
	 */
	public void unbindAuthorPageWithCaret() {
		if(editorPage != null && editorPage.get() != null) {
			editorPage.get().removeAuthorCaretListener(this);
		}
	}

	/**
	 * Bind the current AuthorPage with its current Caret Listener.
	 */
	public void bindAuthorPageWithCaret() {
		unbindAuthorPageWithCaret();
		editorPage.get().addAuthorCaretListener(this);
	}
}
