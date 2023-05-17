package com.oxygenxml.ditareferences.workspace;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import ro.sync.diff.merge.api.MergeFilesException;
import ro.sync.diff.merge.api.MergedFileState;
import ro.sync.ecss.extensions.api.OptionListener;
import ro.sync.ecss.extensions.api.component.AuthorComponentException;
import ro.sync.ecss.extensions.api.component.EditorComponentProvider;
import ro.sync.ecss.extensions.api.node.AuthorDocumentProvider;
import ro.sync.exml.plugin.PluginExtension;
import ro.sync.exml.workspace.api.OperationStatus;
import ro.sync.exml.workspace.api.Platform;
import ro.sync.exml.workspace.api.PluginResourceBundle;
import ro.sync.exml.workspace.api.application.ApplicationType;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.css.AuthorCSSAlternativesCustomizer;
import ro.sync.exml.workspace.api.editor.page.ditamap.keys.KeyDefinitionManager;
import ro.sync.exml.workspace.api.images.ImageUtilities;
import ro.sync.exml.workspace.api.images.handlers.ImageHandler;
import ro.sync.exml.workspace.api.license.LicenseInformationProvider;
import ro.sync.exml.workspace.api.listeners.BatchOperationsListener;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.options.DataSourceAccess;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.process.ProcessController;
import ro.sync.exml.workspace.api.process.ProcessListener;
import ro.sync.exml.workspace.api.results.ResultsManager;
import ro.sync.exml.workspace.api.standalone.InputURLChooserCustomizer;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.actions.ActionsProvider;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ditamap.TopicRefTargetInfoProvider;
import ro.sync.exml.workspace.api.standalone.project.ProjectController;
import ro.sync.exml.workspace.api.standalone.proxy.ProxyDetailsProvider;
import ro.sync.exml.workspace.api.templates.TemplateManager;
import ro.sync.exml.workspace.api.util.ColorTheme;
import ro.sync.exml.workspace.api.util.CompareUtilAccess;
import ro.sync.exml.workspace.api.util.ImageInverter;
import ro.sync.exml.workspace.api.util.RelativeReferenceResolver;
import ro.sync.exml.workspace.api.util.UtilAccess;
import ro.sync.exml.workspace.api.util.XMLUtilAccess;
import ro.sync.exml.workspace.api.util.validation.ValidationUtilAccess;

public class StandalonePluginWorkspaceAccessForTests implements StandalonePluginWorkspace {

	public URL[] getAllEditorLocations(int editingArea) {
		return null;
	}

	public WSEditor getEditorAccess(URL location, int editingArea) {
		return new WSEditorAdapterForTests();
	}

	public WSEditor getCurrentEditorAccess(int editingArea) {
		return new WSEditorAdapterForTests();
	}

	public XMLUtilAccess getXMLUtilAccess() {
		return null;
	}

	public CompareUtilAccess getCompareUtilAccess() {
		return null;
	}

	public UtilAccess getUtilAccess() {
		return null;
	}

	public ResultsManager getResultsManager() {
		return null;
	}

	public void addEditorChangeListener(WSEditorChangeListener editorListener, int editingArea) {

	}

	public void removeEditorChangeListener(WSEditorChangeListener editorListener, int editingArea) {

	}

	public WSEditorChangeListener[] getEditorChangeListeners(int editingArea) {
		return null;
	}

	public WSOptionsStorage getOptionsStorage() {
		return null;
	}

	public void setDITAKeyDefinitionManager(KeyDefinitionManager keyDefitionManager) {

	}

	public void addAuthorCSSAlternativesCustomizer(AuthorCSSAlternativesCustomizer cssAlternativesCustomizer) {

	}

	public void removeAuthorCSSAlternativesCustomizer(AuthorCSSAlternativesCustomizer cssAlternativesCustomizer) {

	}

	public void addBatchOperationsListener(BatchOperationsListener listener) {

	}

	public void removeBatchOperationsListener(BatchOperationsListener listener) {

	}

	public boolean open(URL url) {
		return false;
	}

	public boolean open(URL url, String imposedPage) {
		return false;
	}

	public boolean open(URL url, String imposedPage, String imposedContentType) {
		return false;
	}

	public void saveAll() {

	}

	public boolean close(URL url) {
		return false;
	}

	public boolean closeAll() {
		return false;
	}

	public void delete(URL url) throws IOException {

	}

	public void refreshInProject(URL url) {

	}

	public URL createNewEditor(String extension, String contentType, String content) {
		return null;
	}

	public boolean isStandalone() {
		return false;
	}

	public void setParentFrameTitle(String parentFrameTitle) {

	}

	public Object getParentFrame() {
		return null;
	}

	public File chooseFile(String title, String[] allowedExtensions, String filterDescr, boolean openForSave) {
		return null;
	}

	public File chooseFile(File currentFileContext, String title, String[] allowedExtensions, String filterDescr,
			boolean usedForSave) {
		return null;
	}

	public File chooseFile(String title, String[] allowedExtensions, String filterDescr) {
		return null;
	}

	public File[] chooseFiles(File currentFileContext, String title, String[] allowedExtensions, String filterDescr) {
		return null;
	}

	public File chooseDirectory() {
		return null;
	}

	public URL chooseURL(String title, String[] allowedExtensions, String filterDescr) {
		return null;
	}

	public URL chooseURL(String title, String[] allowedExtensions, String filterDescr, String initialURL) {
		return null;
	}

	public String chooseURLPath(String title, String[] allowedExtensions, String filterDescr) {
		return null;
	}

	public String chooseURLPath(String title, String[] allowedExtensions, String filterDescr, String initialURL) {
		return null;
	}

	public int showConfirmDialog(String title, String message, String[] buttonNames, int[] buttonIds) {
		return 0;
	}

	public int showConfirmDialog(String title, String message, String[] buttonNames, int[] buttonIds,
			int initialSelectedIndex) {
		return 0;
	}

	public void showErrorMessage(String message) {

	}

	public void showErrorMessage(String message, Throwable exception) {
	}

	public void showWarningMessage(String message) {

	}

	public void showInformationMessage(String message) {
		

	}

	public void showStatusMessage(String statusMessage) {
		

	}

	public void openInExternalApplication(URL url, boolean preferAssociatedApplication) {
		

	}

	public void openInExternalApplication(URL url, boolean preferAssociatedApplication, String mediaType) {
		

	}

	public void openInExternalApplication(String url, boolean preferAssociatedApplication, String mediaType) {
		

	}

	public ProcessController createJavaProcess(String additionalJavaArguments, String[] classpath, String mainClass,
			String additionalArguments, Map<String, String> environmentalVariables, File startDirectory,
			ProcessListener processListener) {
		
		return null;
	}

	public void startProcess(String name, File workingDirectory, String cmdLine, boolean showConsole) {
		

	}

	public void clearImageCache() {
		

	}

	public DataSourceAccess getDataSourceAccess() {
		
		return null;
	}

	public ImageUtilities getImageUtilities() {
		
		return new ImageUtilities() {

			public Object loadIcon(URL resource) {
				
				return null;
			}

			public Object getIconDecoration(URL resource) {
				
				return null;
			}

			public void removeImageHandler(ImageHandler imageHandler) {
				

			}

			public ImageHandler getImageHandlerFor(String extension) {
				
				return null;
			}

			public void clearImageCache() {
				

			}

			public void addImageHandler(ImageHandler imageHandler) {
				

			}
		};
	}

	public TemplateManager getTemplateManager() {
		
		return null;
	}

	public ColorTheme getColorTheme() {
		
		return null;
	}

	public ImageInverter getImageInverter() {
		
		return null;
	}

	public LicenseInformationProvider getLicenseInformationProvider() {
		
		return null;
	}

	public String getPreferencesDirectory() {
		
		return null;
	}

	public String getUserInterfaceLanguage() {
		
		return null;
	}

	public String getVersion() {
		
		return null;
	}

	public String getVersionBuildID() {
		
		return null;
	}

	public ApplicationType getApplicationType() {
		
		return null;
	}

	public String getApplicationName() {
		
		return null;
	}

	public Platform getPlatform() {
		
		return null;
	}

	public void addInputURLChooserCustomizer(InputURLChooserCustomizer inputURLChooserCustomizer) {
		

	}

	public void addRelativeReferencesResolver(String protocol, RelativeReferenceResolver resolver) {
		

	}

	public void addGlobalOptionListener(OptionListener listener) {
		

	}

	public void removeGlobalOptionListener(OptionListener listener) {
		

	}

	public Object getGlobalObjectProperty(String key) throws IllegalArgumentException {
		
		return null;
	}

	public void setGlobalObjectProperty(String key, Object value) {
		

	}

	public void importGlobalOptions(File optionsFile) {
		

	}

	public void importGlobalOptions(File optionsFile, boolean preserveExistingOptionKeys) throws IOException {
		

	}

	public void saveGlobalOptions() throws IOException {
		

	}

	public void showPreferencesPages(String[] pagesToShowKeys, String pageToSelectKey, boolean showChildrenOfPages) {
		

	}

	public void setMathFlowFixedLicenseKeyForEditor(String fixedKey) {
		

	}

	public void setMathFlowFixedLicenseKeyForComposer(String fixedKey) {
		

	}

	public void setMathFlowFixedLicenseFile(File licenseFile) {
		

	}

	public void setMathFlowInstallationFolder(File installationFolder) {
		

	}

	public Object openDiffFilesApplication(URL leftURL, URL rightURL) {
		
		return null;
	}

	public Object openDiffFilesApplication(URL leftURL, URL rightURL, URL ancestorURL) {
		
		return null;
	}

	public List<MergedFileState> openMergeApplication(File baseDir, File personalModifiedFilesDir,
			File externalModifiedFilesDir, Map<String, String> mergeOptions) throws MergeFilesException {
		
		return null;
	}

	public void addToolbarComponentsCustomizer(ToolbarComponentsCustomizer componentsCustomizer) {
		

	}

	public void addViewComponentCustomizer(ViewComponentCustomizer viewComponentCustomizer) {
		

	}

	public void addMenuBarCustomizer(MenuBarCustomizer menuBarCustomizer) {
		

	}

	public void addTopicRefTargetInfoProvider(String protocol, TopicRefTargetInfoProvider targetInfoProvider) {
		

	}

	public void showView(String viewID, boolean requestFocus) {
		

	}

	public void hideView(String viewID) {
		

	}

	public boolean isViewShowing(String viewID) {
		
		return false;
	}

	public void hideToolbar(String toolbarID) {
		

	}

	public void showToolbar(String toolbarID) {
		

	}

	public boolean isToolbarShowing(String toolbarID) {
		
		return false;
	}

	public String getOxygenActionID(Action action) {
		
		return null;
	}

	public ActionsProvider getActionsProvider() {
		
		return null;
	}

	public void addMenusAndToolbarsContributorCustomizer(MenusAndToolbarsContributorCustomizer customizer) {
		

	}

	public void removeMenusAndToolbarsContributorCustomizer(MenusAndToolbarsContributorCustomizer customizer) {
		

	}

	public EditorComponentProvider createEditorComponentProvider(String[] allowedPages, String initialPage)
			throws AuthorComponentException {
		
		return null;
	}

	public PluginResourceBundle getResourceBundle() {
		
		return null;
	}

	public ProxyDetailsProvider getProxyDetailsProvider() {
		
		return null;
	}

	public ProjectController getProjectManager() {
		
		return null;
	}

  public URL createNewEditor(URL saveTo, String extension, String contentType, String content) {
    return null;
  }

  public File chooseDirectory(File startingDir) {
    return null;
  }

  public String serializePersistentObject(Object persistentObject) throws IOException {
    return null;
  }

  public Object deserializePersistentObject(String persistentObjectStringRepresentation) throws IOException {
    return null;
  }

  public boolean isViewAvailable(String viewID) {
    return false;
  }

  @Override
  public ValidationUtilAccess getValidationUtilAccess() {
    return null;
  }

  @Override
  public AuthorDocumentProvider createAuthorDocumentProvider(URL systemId, Reader documentReader) throws IOException {
    return null;
  }

  @Override
  public AuthorDocumentProvider createAuthorDocumentProvider(URL systemId, Reader documentReader,
      boolean expandReferences) throws IOException {
    return null;
  }

  @Override
  public URL chooseURL(String title, String[] allowedExtensions, String filterDescr, String initialURL, String urlLabel,
      String okLabel) {
    return null;
  }

  @Override
  public void showStatusMessage(String statusMessage, OperationStatus status) {
  }

  @Override
  public ProcessController createProcess(ProcessListener processListener, String name, File workingDirectory,
      String cmdLine, boolean showConsole) {
    return null;
  }

  @Override
  public Object openDiffFilesApplication(String leftLabelText, URL leftURL, String rightLabelText, URL rightURL) {
    return null;
  }

  @Override
  public Object openDiffFilesApplication(String leftLabelText, URL leftURL, String rightLabelText, URL rightURL,
      URL ancestorURL, boolean showAncestorURLPanel) {
    return null;
  }

  @Override
  public void addPluginExtension(String extensionType, PluginExtension pluginExtension) {
  }

}
