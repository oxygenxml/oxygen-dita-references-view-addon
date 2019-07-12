package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.ReadOnlyReason;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.TextPopupMenuCustomizer;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.actions.TextActionsProvider;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class WSXMLTextEditorPageForTests implements WSXMLTextEditorPage {

	public Document getDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getTextComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void beginCompoundUndoableEdit() {
		// TODO Auto-generated method stub

	}

	public void endCompoundUndoableEdit() {
		// TODO Auto-generated method stub

	}

	public int getLineOfOffset(int offset) throws BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getColumnOfOffset(int offset) throws BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getOffsetOfLineStart(int lineNumber) throws BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getOffsetOfLineEnd(int lineNumber) throws BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addPopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {
		// TODO Auto-generated method stub

	}

	public void removePopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {
		// TODO Auto-generated method stub

	}

	public TextActionsProvider getActionsProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSelectionStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSelectionEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSelectedText() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCaretOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void deleteSelection() {
		// TODO Auto-generated method stub

	}

	public boolean hasSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	public void selectWord() {
		// TODO Auto-generated method stub

	}

	public void setCaretPosition(int offset) {
		// TODO Auto-generated method stub

	}

	public void select(int startOffset, int endOffset) {
		// TODO Auto-generated method stub

	}

	public int[] getWordAtCaret() {
		// TODO Auto-generated method stub
		return null;
	}

	public Point getLocationOnScreenAsPoint(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Point getLocationRelativeToEditorFromScreen(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public Rectangle modelToViewRectangle(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public int viewToModelOffset(int x, int y) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getStartEndOffsets(DocumentPositionedInfo dpInfo) throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void scrollCaretToVisible() {
		// TODO Auto-generated method stub

	}

	public void setReadOnly(ReadOnlyReason reason) {
		// TODO Auto-generated method stub

	}

	public void setReadOnly(String reason) {
		// TODO Auto-generated method stub

	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub

	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public WSEditor getParentEditor() {
		return new WSEditorAdapterForTests();
	}

	public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] evaluateXPath(String xpathExpression) throws XPathException {
		// TODO Auto-generated method stub
		return null;
	}

	public WSTextXMLSchemaManager getXMLSchemaManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public TextDocumentController getDocumentController() {
		// TODO Auto-generated method stub
		return null;
	}

}
