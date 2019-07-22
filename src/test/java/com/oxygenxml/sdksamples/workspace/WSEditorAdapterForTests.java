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
		
		return null;
	}

	public URL getEditorLocation() {
		
		return null;
	}

	public void save() {
		

	}

	public void saveAs(URL location) {
		

	}

	public boolean close(boolean askForSave) {
		
		return false;
	}

	public void setModified(boolean modified) {
		

	}

	public boolean isNewDocument() {
		
		return false;
	}

	public Reader createContentReader() {
		
		return null;
	}

	public InputStream createContentInputStream() throws IOException {
		
		return null;
	}

	public void reloadContent(Reader reader) {
		

	}

	public void reloadContent(Reader reader, boolean discardUndoableEdits) {
		

	}

	public void setEditorTabText(String tabText) {
		

	}

	public void setEditorTabTooltipText(String tabTooltip) {
		

	}

	public DocumentTypeInformation getDocumentTypeInformation() {
		
		return null;
	}

	public boolean isModified() {
		
		return false;
	}

	public void runTransformationScenarios(String[] scenarioNames, TransformationFeedback transformationFeedback)
			throws TransformationScenarioNotFoundException {
		

	}

	public WSEditorPage getCurrentPage() {
		return new TextComponentForTests();
	}

	public String getCurrentPageID() {
		
		return null;
	}

	public void addPageChangedListener(WSEditorPageChangedListener pageChangedListener) {
		

	}

	public void removePageChangedListener(WSEditorPageChangedListener pageChangedListener) {
		

	}

	public void addEditorListener(WSEditorListener editorListener) {
		

	}

	public WSEditorListener[] getEditorListeners() {
		
		return null;
	}

	public void removeEditorListener(WSEditorListener editorListener) {
		

	}

	public void changePage(String pageID) {
		

	}

	public void addValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {
		

	}

	public void removeValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {
		

	}

	public boolean checkValid() {
		
		return false;
	}

	public boolean checkValid(boolean automatic) {
		
		return false;
	}

	public Object getComponent() {
		
		return null;
	}

	public void setEditable(boolean editable) {
		

	}

	public boolean isEditable() {
		
		return false;
	}

}
