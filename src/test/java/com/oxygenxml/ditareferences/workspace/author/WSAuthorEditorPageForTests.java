/**
 * 
 */
package com.oxygenxml.ditareferences.workspace.author;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.ecss.css.Styles;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorCaretEvent;
import ro.sync.ecss.extensions.api.AuthorCaretListener;
import ro.sync.ecss.extensions.api.AuthorChangeTrackingController;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorMouseListener;
import ro.sync.ecss.extensions.api.AuthorReviewController;
import ro.sync.ecss.extensions.api.AuthorSchemaAwareEditingHandler;
import ro.sync.ecss.extensions.api.AuthorSelectionModel;
import ro.sync.ecss.extensions.api.AuthorViewToModelInfo;
import ro.sync.ecss.extensions.api.OptionsStorage;
import ro.sync.ecss.extensions.api.access.AuthorOutlineAccess;
import ro.sync.ecss.extensions.api.access.AuthorTableAccess;
import ro.sync.ecss.extensions.api.attributes.AuthorAttributesDisplayFilter;
import ro.sync.ecss.extensions.api.highlights.AuthorHighlighter;
import ro.sync.ecss.extensions.api.highlights.AuthorPersistentHighlighter;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.structure.AuthorPopupMenuCustomizer;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.ReadOnlyReason;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;
import ro.sync.exml.workspace.api.editor.page.author.fold.AuthorFoldManager;
import ro.sync.exml.workspace.api.editor.page.author.tooltip.AuthorTooltipCustomizer;

/**
 * @author Alexandra_Dinisor
 *
 */
public class WSAuthorEditorPageForTests implements WSAuthorEditorPage {

	private int startOffset;
	private int endOffset;
	private int caretOffset;
	private AuthorCaretListener caretListener;

	public WSAuthorEditorPageForTests() {

	}

	@Override
	public AuthorViewToModelInfo viewToModel(int x, int y) {

		return null;
	}

	@Override
	public void setPopUpMenuCustomizer(AuthorPopupMenuCustomizer popUpCustomizer) {

	}

	@Override
	public void addPopUpMenuCustomizer(AuthorPopupMenuCustomizer popUpCustomizer) {

	}

	@Override
	public void removePopUpMenuCustomizer(AuthorPopupMenuCustomizer popUpCustomizer) {

	}

	@Override
	public void addAuthorMouseListener(AuthorMouseListener mouseListener) {

	}

	@Override
	public void removeAuthorMouseListener(AuthorMouseListener mouseListener) {

	}

	@Override
	public void addAuthorCaretListener(AuthorCaretListener caretListener) {
		this.caretListener = caretListener;
	}

	@Override
	public void removeAuthorCaretListener(AuthorCaretListener caretListener) {

	}

	@Override
	public void refresh(AuthorNode authorNode) {

	}

	@Override
	public void refresh() {

	}

	@Override
	public AuthorHighlighter getHighlighter() {

		return null;
	}

	@Override
	public AuthorPersistentHighlighter getPersistentHighlighter() {

		return null;
	}

	@Override
	public int getBalancedSelectionStart() {

		return 0;
	}

	@Override
	public int getBalancedSelectionEnd() {

		return 0;
	}

	@Override
	public int[] getBalancedSelection(int selectionStart, int selectionEnd) {

		return null;
	}

	@Override
	public AuthorSchemaAwareEditingHandler getDefaultAuthorSchemaAwareEditingHandler() {

		return null;
	}

	@Override
	public AuthorActionsProvider getActionsProvider() {

		return null;
	}

	@Override
	public Object getAuthorComponent() {

		return null;
	}

	@Override
	public Styles getStyles(AuthorNode node) {

		return null;
	}

	@Override
	public void addAuthorAttributesDisplayFilter(AuthorAttributesDisplayFilter attributesDisplayFilter) {

	}

	@Override
	public void removeAuthorAttributesDisplayFilter(AuthorAttributesDisplayFilter attributesDisplayFilter) {

	}

	@Override
	public AuthorNode getFullySelectedNode() {

		return null;
	}

	@Override
	public AuthorNode getFullySelectedNode(int selectionStart, int selectionEnd) {

		return null;
	}

	@Override
	public AuthorSelectionModel getAuthorSelectionModel() {

		return null;
	}

	@Override
	public int getSelectionStart() {
		
		return startOffset;
	}

	@Override
	public int getSelectionEnd() {
		return endOffset;
	}

	@Override
	public String getSelectedText() {

		return null;
	}

	@Override
	public void deleteSelection() {

	}

	@Override
	public boolean hasSelection() {

		return false;
	}

	@Override
	public void select(int startOffset, int endOffset) {
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	@Override
	public boolean isOffsetInInvisibleBounds(int offset) throws BadLocationException {

		return false;
	}

	@Override
	public int moveOutOfInvisibleBounds(int offset, boolean forward) throws BadLocationException {

		return 0;
	}

	@Override
	public void goToNextEditablePosition(int startOffset, int endOffset) throws BadLocationException {

	}

	@Override
	public void editAttribute(AuthorElement targetElement, String attributeName) {

	}

	@Override
	public void scrollToRectangle(Rectangle rectangle) {

	}

	@Override
	public AuthorFoldManager getAuthorFoldManager() {

		return null;
	}

	@Override
	public void addDNDListener(Object dndListener) {

	}

	@Override
	public void removeDNDListener(Object dndListener) {

	}

	@Override
	public void setTagsDisplayMode(int displayMode) {

	}

	@Override
	public int getTagsDisplayMode() {

		return 0;
	}

	@Override
	public URL buildURLForReferencedContent(int caretOffset, boolean shortAnchor) {

		return null;
	}

	@Override
	public int getCaretOffset() {
		return caretOffset;
	}

	@Override
	public void selectWord() {

	}

	@Override
	public void setCaretPosition(int offset) {
		this.caretOffset = offset;	
		// supposedly only 1 caretListener 
		if(caretListener != null) {
			caretListener.caretMoved(new AuthorCaretEvent(caretOffset, new ArrayList<int[]>(), null));
		}
	}

	@Override
	public int[] getWordAtCaret() {

		return null;
	}

	@Override
	public Point getLocationOnScreenAsPoint(int x, int y) {

		return null;
	}

	@Override
	public Point getLocationRelativeToEditorFromScreen(int x, int y) {

		return null;
	}

	@Override
	public Rectangle modelToViewRectangle(int offset) {

		return null;
	}

	@Override
	public int viewToModelOffset(int x, int y) {

		return 0;
	}

	@Override
	public int[] getStartEndOffsets(DocumentPositionedInfo dpInfo) throws BadLocationException {

		return null;
	}

	@Override
	public void scrollCaretToVisible() {

	}

	@Override
	public void setReadOnly(ReadOnlyReason reason) {

	}

	@Override
	public void setReadOnly(String reason) {

	}

	@Override
	public void setEditable(boolean editable) {

	}

	@Override
	public boolean isEditable() {

		return false;
	}

	@Override
	public WSEditor getParentEditor() {

		return null;
	}

	@Override
	public void addTooltipCustomizer(AuthorTooltipCustomizer tooltipCustomizer) {

	}

	@Override
	public void removeTooltipCustomizer(AuthorTooltipCustomizer tooltipCustomizer) {

	}

	@Override
	public AuthorDocumentController getDocumentController() {

		return null;
	}

	@Override
	public AuthorTableAccess getTableAccess() {

		return null;
	}

	@Override
	public AuthorChangeTrackingController getChangeTrackingController() {

		return null;
	}

	@Override
	public AuthorReviewController getReviewController() {

		return null;
	}

	@Override
	public OptionsStorage getOptionsStorage() {

		return null;
	}

	@Override
	public AuthorOutlineAccess getOutlineAccess() {

		return null;
	}

	@Override
	public AuthorAccess getAuthorAccess() {

		return null;
	}

}
