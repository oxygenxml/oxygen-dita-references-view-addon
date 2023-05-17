package com.oxygenxml.ditareferences.workspace.text;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.oxygenxml.ditareferences.workspace.WSEditorAdapterForTests;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.view.graphics.Point;
import ro.sync.exml.view.graphics.Rectangle;
import ro.sync.exml.workspace.api.editor.ReadOnlyReason;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.Anchor;
import ro.sync.exml.workspace.api.editor.page.text.ExternalContentCompletionProvider;
import ro.sync.exml.workspace.api.editor.page.text.TextPopupMenuCustomizer;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.actions.TextActionsProvider;
import ro.sync.exml.workspace.api.editor.page.text.xml.TextDocumentController;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

public class WSXMLTextEditorPageForTests implements WSXMLTextEditorPage {

	protected JTextArea textArea = new JTextArea();

	public Document getDocument() {

		return null;
	}

	public Object getTextComponent() {
		return textArea;
	}

	public void beginCompoundUndoableEdit() {

	}

	public void endCompoundUndoableEdit() {

	}

	public int getLineOfOffset(int offset) throws BadLocationException {

		return 0;
	}

	public int getColumnOfOffset(int offset) throws BadLocationException {

		return 0;
	}

	public int getOffsetOfLineStart(int lineNumber) throws BadLocationException {

		return 0;
	}

	public int getOffsetOfLineEnd(int lineNumber) throws BadLocationException {

		return 0;
	}

	public void addPopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {

	}

	public void removePopUpMenuCustomizer(TextPopupMenuCustomizer popUpCustomizer) {

	}

	public TextActionsProvider getActionsProvider() {

		return null;
	}

	public int getSelectionStart() {

		return 0;
	}

	public int getSelectionEnd() {

		return 0;
	}

	public String getSelectedText() {

		return textArea.getSelectedText();
	}

	public int getCaretOffset() {

		return 0;
	}

	public void deleteSelection() {

	}

	public boolean hasSelection() {

		return false;
	}

	public void selectWord() {

	}

	public void setCaretPosition(int offset) {

	}

	public void select(int startOffset, int endOffset) {
		textArea.select(startOffset, endOffset);
	}

	public int[] getWordAtCaret() {

		return null;
	}

	public Point getLocationOnScreenAsPoint(int x, int y) {

		return null;
	}

	public Point getLocationRelativeToEditorFromScreen(int x, int y) {

		return null;
	}

	public Rectangle modelToViewRectangle(int offset) {

		return null;
	}

	public int viewToModelOffset(int x, int y) {

		return 0;
	}

	public int[] getStartEndOffsets(DocumentPositionedInfo dpInfo) throws BadLocationException {

		return null;
	}

	public void scrollCaretToVisible() {

	}

	public void setReadOnly(ReadOnlyReason reason) {

	}

	public void setReadOnly(String reason) {

	}

	public void setEditable(boolean editable) {

	}

	public boolean isEditable() {

		return false;
	}

	public WSEditor getParentEditor() {
		return new WSEditorAdapterForTests();
	}

	public WSXMLTextNodeRange[] findElementsByXPath(String xpathExpression) throws XPathException {

		return null;
	}

	public Object[] evaluateXPath(String xpathExpression) throws XPathException {

		return null;
	}

	public WSTextXMLSchemaManager getXMLSchemaManager() {

		return null;
	}

	public TextDocumentController getDocumentController() {

		return null;
	}

  @Override
  public void addExternalContentCompletionProvider(ExternalContentCompletionProvider ccProvider) {
    //  Auto-generated method stub
    
  }

  @Override
  public void removeExternalContentCompletionProvider(ExternalContentCompletionProvider ccProvider) {
    //  Auto-generated method stub
    
  }

  @Override
  public Anchor createAnchor(int offset) throws BadLocationException {
    //  Auto-generated method stub
    return null;
  }

  @Override
  public int getOffsetForAnchor(Anchor anchor) throws BadLocationException {
    //  Auto-generated method stub
    return 0;
  }

  @Override
  public void requestFocus() {
    //  Auto-generated method stub
    
  }

  @Override
  public boolean hasFocus() {
    //  Auto-generated method stub
    return false;
  }

}