package com.oxygenxml.ditareferences.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import com.oxygenxml.ditareferences.workspace.text.TextComponentForTests;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.documenttype.DocumentTypeInformation;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.transformation.TransformationFeedback;
import ro.sync.exml.workspace.api.editor.transformation.TransformationScenarioNotFoundException;
import ro.sync.exml.workspace.api.editor.validation.OperationInProgressException;
import ro.sync.exml.workspace.api.editor.validation.ValidationProblemsFilter;
import ro.sync.exml.workspace.api.editor.validation.ValidationScenarioNotFoundException;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.listeners.WSEditorPageChangedListener;

public class WSEditorAdapterForTests implements WSEditor {

	@Override
	public String getEncodingForSerialization() {
		return null;
	}

	@Override
	public URL getEditorLocation() {
		return null;
	}

	@Override
	public void save() {
		
	}

	@Override
	public void saveAs(URL location) {
		
	}

	@Override
	public boolean close(boolean askForSave) {	
		return false;
	}

	@Override
	public void setModified(boolean modified) {
		
	}

	@Override
	public boolean isNewDocument() {	
		return false;
	}

	@Override
	public Reader createContentReader() {	
		return null;
	}

	@Override
	public InputStream createContentInputStream() throws IOException {	
		return null;
	}

	@Override
	public void reloadContent(Reader reader) {
		
	}

	@Override
	public void reloadContent(Reader reader, boolean discardUndoableEdits) {
		
	}

	@Override
	public void setEditorTabText(String tabText) {
		
	}

	@Override
	public void setEditorTabTooltipText(String tabTooltip) {
		
	}

	@Override
	public DocumentTypeInformation getDocumentTypeInformation() {
		
		return null;
	}

	@Override
	public boolean isModified() {
		
		return false;
	}

	@Override
	public void runTransformationScenarios(String[] scenarioNames, TransformationFeedback transformationFeedback)
			throws TransformationScenarioNotFoundException {
		
	}

	@Override
	public WSEditorPage getCurrentPage() {
		return new TextComponentForTests();
	}

	@Override
	public String getCurrentPageID() {		
		return null;
	}

	@Override
	public void addPageChangedListener(WSEditorPageChangedListener pageChangedListener) {	

	}

	@Override
	public void removePageChangedListener(WSEditorPageChangedListener pageChangedListener) {		

	}

	@Override
	public void addEditorListener(WSEditorListener editorListener) {		

	}

	@Override
	public WSEditorListener[] getEditorListeners() {
		
		return null;
	}

	@Override
	public void removeEditorListener(WSEditorListener editorListener) {		

	}

	@Override
	public void changePage(String pageID) {		

	}

	@Override
	public void addValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {		

	}

	@Override
	public void removeValidationProblemsFilter(ValidationProblemsFilter validationProblemsFilter) {		

	}

	@Override
	public boolean checkValid() {
		
		return false;
	}

	@Override
	public boolean checkValid(boolean automatic) {
		
		return false;
	}

	@Override
	public Object getComponent() {
		
		return null;
	}

	@Override
	public void setEditable(boolean editable) {		

	}

	@Override
	public boolean isEditable() {
		
		return false;
	}

  @Override
  public void runTransformationScenario(String scenarioName, Map<String, String> scenarioParameters,
      TransformationFeedback transformationFeedback) throws TransformationScenarioNotFoundException {
  }

  @Override
  public void stopCurrentTransformationScenario() {
  }

  @Override
  public Thread runValidationScenarios(String[] scenarioNames)
      throws ValidationScenarioNotFoundException, OperationInProgressException {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
  }

}
