package com.oxygenxml.ditareferences.workspace.text;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.ReadOnlyReason;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.TextPopupMenuCustomizer;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.actions.TextActionsProvider;

public class TextComponentForTests implements WSTextEditorPage {

	public int getSelectionStart() {
		// Auto-generated method stub
		return 0;
	}

	public int getSelectionEnd() {
		// Auto-generated method stub
		return 0;
	}

	public String getSelectedText() {
		// Auto-generated method stub
		return new JTextArea().getSelectedText();
	}

	public int getCaretOffset() {
		// Auto-generated method stub
		return 0;
	}

	public void deleteSelection() {
		// Auto-generated method stub

	}

	public boolean hasSelection() {
		// Auto-generated method stub
		return false;
	}

	public void selectWord() {
		// Auto-generated method stub

	}

	public void setCaretPosition(int offset) {
		// Auto-generated method stub

	}

	public void select(int startOffset, int endOffset) {
		 (new JTextArea()).select(startOffset, endOffset);

	}

	public int[] getWordAtCaret() {
		// Auto-generated method stub
		return null;
	}

	public Point getLocationOnScreenAsPoint(int x, int y) {
		// Auto-generated method stub
		return null;
	}

	public Point getLocationRelativeToEditorFromScreen(int x, int y) {
		// Auto-generated method stub
		return null;
	}

	public Rectangle modelToViewRectangle(int offset) {
		// Auto-generated method stub
		return null;
	}

	public int viewToModelOffset(int x, int y) {
		// Auto-generated method stub
		return 0;
	}

	public int[] getStartEndOffsets(DocumentPositionedInfo dpInfo) throws BadLocationException {
		// Auto-generated method stub
		return null;
	}

	public void scrollCaretToVisible() {
		// Auto-generated method stub

	}

	public void setReadOnly(ReadOnlyReason reason) {
		// Auto-generated method stub

	}

	public void setReadOnly(String reason) {
		// Auto-generated method stub

	}

	public void setEditable(boolean editable) {
		// Auto-generated method stub

	}

	public boolean isEditable() {
		// Auto-generated method stub
		return false;
	}

	public WSEditor getParentEditor() {
		// Auto-generated method stub
		return null;
	}

	public Document getDocument() {
		// Auto-generated method stub
		return null;
	}

	public Object getTextComponent() {
		// return a new JTextArea
		return new JTextArea();
	}

	public WSTextXMLSchemaManager getXMLSchemaManager() {
		// Auto-generated method stub
		return null;
	}

	public void beginCompoundUndoableEdit() {
		// Auto-generated method stub

	}

	public void endCompoundUndoableEdit() {
		// Auto-generated method stub

	}

	public int getLineOfOffset(int offset) throws BadLocationException {
		// Auto-generated method stub
		return 0;
	}

	public int getColumnOfOffset(int offset) throws BadLocationException {
		// Auto-generated method stub
		return 0;
	}

	public int getOffsetOfLineStart(int lineNumber) throws BadLocationException {
		// Auto-generated method stub
		return 0;
	}

	public int getOffsetOfLineEnd(int lineNumber) throws BadLocationException {
		// Auto-generated method stub
		return 0;
	}

	public void addPopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {
		// Auto-generated method stub

	}

	public void removePopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {
		// Auto-generated method stub

	}

	public TextActionsProvider getActionsProvider() {
		// Auto-generated method stub
		return null;
	}

}
