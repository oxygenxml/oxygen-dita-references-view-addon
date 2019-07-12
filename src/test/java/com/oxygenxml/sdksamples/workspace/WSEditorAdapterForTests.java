package com.oxygenxml.sdksamples.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.documenttype.DocumentTypeInformation;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.transformation.TransformationFeedback;
import ro.sync.exml.workspace.api.editor.transformation.TransformationScenarioNotFoundException;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblemsFilter;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.listeners.WSEditorPageChangedListener;

public class WSEditorAdapterForTests implements WSEditor {

	public String getEncodingForSerialization() {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getEditorLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void saveAs(URL location) {
		// TODO Auto-generated method stub

	}

	public boolean close(boolean askForSave) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	public boolean isNewDocument() {
		// TODO Auto-generated method stub
		return false;
	}

	public Reader createContentReader() {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream createContentInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void reloadContent(Reader reader) {
		// TODO Auto-generated method stub

	}

	public void reloadContent(Reader reader, boolean discardUndoableEdits) {
		// TODO Auto-generated method stub

	}

	public void setEditorTabText(String tabText) {
		// TODO Auto-generated method stub

	}

	public void setEditorTabTooltipText(String tabTooltip) {
		// TODO Auto-generated method stub

	}

	public DocumentTypeInformation getDocumentTypeInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public void runTransformationScenarios(String[] scenarioNames, TransformationFeedback transformationFeedback)
			throws TransformationScenarioNotFoundException {
		// TODO Auto-generated method stub

	}

	public WSEditorPage getCurrentPage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentPageID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPageChangedListener(WSEditorPageChangedListener pageChangedListener) {
		// TODO Auto-generated method stub

	}

	public void removePageChangedListener(WSEditorPageChangedListener pageChangedListener) {
		// TODO Auto-generated method stub

	}

	public void addEditorListener(WSEditorListener editorListener) {
		// TODO Auto-generated method stub

	}

	public WSEditorListener[] getEditorListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeEditorListener(WSEditorListener editorListener) {
		// TODO Auto-generated method stub

	}

	public void changePage(String pageID) {
		// TODO Auto-generated method stub

	}

	public void addValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {
		// TODO Auto-generated method stub

	}

	public void removeValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {
		// TODO Auto-generated method stub

	}

	public boolean checkValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkValid(boolean automatic) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEditable(boolean editable) {
		// TODO Auto-generated method stub

	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

}
