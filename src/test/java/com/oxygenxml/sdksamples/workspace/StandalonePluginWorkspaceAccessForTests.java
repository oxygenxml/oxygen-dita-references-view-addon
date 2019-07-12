package com.oxygenxml.sdksamples.workspace;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import ro.sync.diff.merge.api.MergeFilesException;
import ro.sync.diff.merge.api.MergedFileState;
import ro.sync.ecss.extensions.api.OptionListener;
import ro.sync.ecss.extensions.api.component.AuthorComponentException;
import ro.sync.ecss.extensions.api.component.EditorComponentProvider;
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

public class StandalonePluginWorkspaceAccessForTests implements StandalonePluginWorkspace {

	public URL[] getAllEditorLocations(int editingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	public WSEditor getEditorAccess(URL location, int editingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	public WSEditor getCurrentEditorAccess(int editingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	public XMLUtilAccess getXMLUtilAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public CompareUtilAccess getCompareUtilAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public UtilAccess getUtilAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultsManager getResultsManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addEditorChangeListener(WSEditorChangeListener editorListener, int editingArea) {
		// TODO Auto-generated method stub

	}

	public void removeEditorChangeListener(WSEditorChangeListener editorListener, int editingArea) {
		// TODO Auto-generated method stub

	}

	public WSEditorChangeListener[] getEditorChangeListeners(int editingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	public WSOptionsStorage getOptionsStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDITAKeyDefinitionManager(KeyDefinitionManager keyDefitionManager) {
		// TODO Auto-generated method stub

	}

	public void addAuthorCSSAlternativesCustomizer(AuthorCSSAlternativesCustomizer cssAlternativesCustomizer) {
		// TODO Auto-generated method stub

	}

	public void removeAuthorCSSAlternativesCustomizer(AuthorCSSAlternativesCustomizer cssAlternativesCustomizer) {
		// TODO Auto-generated method stub

	}

	public void addBatchOperationsListener(BatchOperationsListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeBatchOperationsListener(BatchOperationsListener listener) {
		// TODO Auto-generated method stub

	}

	public boolean open(URL url) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean open(URL url, String imposedPage) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean open(URL url, String imposedPage, String imposedContentType) {
		// TODO Auto-generated method stub
		return false;
	}

	public void saveAll() {
		// TODO Auto-generated method stub

	}

	public boolean close(URL url) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean closeAll() {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete(URL url) throws IOException {
		// TODO Auto-generated method stub

	}

	public void refreshInProject(URL url) {
		// TODO Auto-generated method stub

	}

	public URL createNewEditor(String extension, String contentType, String content) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setParentFrameTitle(String parentFrameTitle) {
		// TODO Auto-generated method stub

	}

	public Object getParentFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	public File chooseFile(String title, String[] allowedExtensions, String filterDescr, boolean openForSave) {
		// TODO Auto-generated method stub
		return null;
	}

	public File chooseFile(File currentFileContext, String title, String[] allowedExtensions, String filterDescr,
			boolean usedForSave) {
		// TODO Auto-generated method stub
		return null;
	}

	public File chooseFile(String title, String[] allowedExtensions, String filterDescr) {
		// TODO Auto-generated method stub
		return null;
	}

	public File[] chooseFiles(File currentFileContext, String title, String[] allowedExtensions, String filterDescr) {
		// TODO Auto-generated method stub
		return null;
	}

	public File chooseDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	public URL chooseURL(String title, String[] allowedExtensions, String filterDescr) {
		// TODO Auto-generated method stub
		return null;
	}

	public URL chooseURL(String title, String[] allowedExtensions, String filterDescr, String initialURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public String chooseURLPath(String title, String[] allowedExtensions, String filterDescr) {
		// TODO Auto-generated method stub
		return null;
	}

	public String chooseURLPath(String title, String[] allowedExtensions, String filterDescr, String initialURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public int showConfirmDialog(String title, String message, String[] buttonNames, int[] buttonIds) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int showConfirmDialog(String title, String message, String[] buttonNames, int[] buttonIds,
			int initialSelectedIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void showErrorMessage(String message) {
		// TODO Auto-generated method stub

	}

	public void showErrorMessage(String message, Throwable exception) {
		// TODO Auto-generated method stub

	}

	public void showWarningMessage(String message) {
		// TODO Auto-generated method stub

	}

	public void showInformationMessage(String message) {
		// TODO Auto-generated method stub

	}

	public void showStatusMessage(String statusMessage) {
		// TODO Auto-generated method stub

	}

	public void openInExternalApplication(URL url, boolean preferAssociatedApplication) {
		// TODO Auto-generated method stub

	}

	public void openInExternalApplication(URL url, boolean preferAssociatedApplication, String mediaType) {
		// TODO Auto-generated method stub

	}

	public void openInExternalApplication(String url, boolean preferAssociatedApplication, String mediaType) {
		// TODO Auto-generated method stub

	}

	public ProcessController createJavaProcess(String additionalJavaArguments, String[] classpath, String mainClass,
			String additionalArguments, Map<String, String> environmentalVariables, File startDirectory,
			ProcessListener processListener) {
		// TODO Auto-generated method stub
		return null;
	}

	public void startProcess(String name, File workingDirectory, String cmdLine, boolean showConsole) {
		// TODO Auto-generated method stub

	}

	public void clearImageCache() {
		// TODO Auto-generated method stub

	}

	public DataSourceAccess getDataSourceAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageUtilities getImageUtilities() {
		// TODO Auto-generated method stub
		return new ImageUtilities() {
			
			public Object loadIcon(URL resource) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public Object getIconDecoration(URL resource) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public void removeImageHandler(ImageHandler imageHandler) {
				// TODO Auto-generated method stub
				
			}
			
			public ImageHandler getImageHandlerFor(String extension) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public void clearImageCache() {
				// TODO Auto-generated method stub
				
			}
			
			public void addImageHandler(ImageHandler imageHandler) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	public TemplateManager getTemplateManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public ColorTheme getColorTheme() {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageInverter getImageInverter() {
		// TODO Auto-generated method stub
		return null;
	}

	public LicenseInformationProvider getLicenseInformationProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPreferencesDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserInterfaceLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVersionBuildID() {
		// TODO Auto-generated method stub
		return null;
	}

	public ApplicationType getApplicationType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getApplicationName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Platform getPlatform() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addInputURLChooserCustomizer(InputURLChooserCustomizer inputURLChooserCustomizer) {
		// TODO Auto-generated method stub

	}

	public void addRelativeReferencesResolver(String protocol, RelativeReferenceResolver resolver) {
		// TODO Auto-generated method stub

	}

	public void addGlobalOptionListener(OptionListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeGlobalOptionListener(OptionListener listener) {
		// TODO Auto-generated method stub

	}

	public Object getGlobalObjectProperty(String key) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setGlobalObjectProperty(String key, Object value) {
		// TODO Auto-generated method stub

	}

	public void importGlobalOptions(File optionsFile) {
		// TODO Auto-generated method stub

	}

	public void importGlobalOptions(File optionsFile, boolean preserveExistingOptionKeys) throws IOException {
		// TODO Auto-generated method stub

	}

	public void saveGlobalOptions() throws IOException {
		// TODO Auto-generated method stub

	}

	public void showPreferencesPages(String[] pagesToShowKeys, String pageToSelectKey, boolean showChildrenOfPages) {
		// TODO Auto-generated method stub

	}

	public void setMathFlowFixedLicenseKeyForEditor(String fixedKey) {
		// TODO Auto-generated method stub

	}

	public void setMathFlowFixedLicenseKeyForComposer(String fixedKey) {
		// TODO Auto-generated method stub

	}

	public void setMathFlowFixedLicenseFile(File licenseFile) {
		// TODO Auto-generated method stub

	}

	public void setMathFlowInstallationFolder(File installationFolder) {
		// TODO Auto-generated method stub

	}

	public Object openDiffFilesApplication(URL leftURL, URL rightURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object openDiffFilesApplication(URL leftURL, URL rightURL, URL ancestorURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<MergedFileState> openMergeApplication(File baseDir, File personalModifiedFilesDir,
			File externalModifiedFilesDir, Map<String, String> mergeOptions) throws MergeFilesException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addToolbarComponentsCustomizer(ToolbarComponentsCustomizer componentsCustomizer) {
		// TODO Auto-generated method stub

	}

	public void addViewComponentCustomizer(ViewComponentCustomizer viewComponentCustomizer) {
		// TODO Auto-generated method stub

	}

	public void addMenuBarCustomizer(MenuBarCustomizer menuBarCustomizer) {
		// TODO Auto-generated method stub

	}

	public void addTopicRefTargetInfoProvider(String protocol, TopicRefTargetInfoProvider targetInfoProvider) {
		// TODO Auto-generated method stub

	}

	public void showView(String viewID, boolean requestFocus) {
		// TODO Auto-generated method stub

	}

	public void hideView(String viewID) {
		// TODO Auto-generated method stub

	}

	public boolean isViewShowing(String viewID) {
		// TODO Auto-generated method stub
		return false;
	}

	public void hideToolbar(String toolbarID) {
		// TODO Auto-generated method stub

	}

	public void showToolbar(String toolbarID) {
		// TODO Auto-generated method stub

	}

	public boolean isToolbarShowing(String toolbarID) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getOxygenActionID(Action action) {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionsProvider getActionsProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMenusAndToolbarsContributorCustomizer(MenusAndToolbarsContributorCustomizer customizer) {
		// TODO Auto-generated method stub

	}

	public void removeMenusAndToolbarsContributorCustomizer(MenusAndToolbarsContributorCustomizer customizer) {
		// TODO Auto-generated method stub

	}

	public EditorComponentProvider createEditorComponentProvider(String[] allowedPages, String initialPage)
			throws AuthorComponentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PluginResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProxyDetailsProvider getProxyDetailsProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProjectController getProjectManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
