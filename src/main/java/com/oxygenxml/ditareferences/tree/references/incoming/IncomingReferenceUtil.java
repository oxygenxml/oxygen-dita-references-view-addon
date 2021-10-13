package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.tree.references.VersionUtil;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.WSTextBasedEditorPage;

/**
 * Contains useful methods for incoming references.
 * 
 * @author Alex_Smarandache
 *
 */
public class IncomingReferenceUtil {
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(IncomingReferenceUtil.class.getName());
	
	/**
	 * Constant used for java reflexion
	 */
	private static final String DITA_ACCESS_CLASS_NAME = "ro.sync.ecss.dita.DITAAccess";
	
	
	
	/**
	 * The hidden constructor.
	 */
	private IncomingReferenceUtil() {
		// Nothing
	}	
	

	/**
	 * Add the reference categories to the node and store the children in the map for each reference category.
	 * 
	 * @param incomingReferences     The incoming references.
	 * @param referenceCategories    The map which stores copies for each reference category.
	 * @param node                   The node where the reference categories are added.
	 */
	public static void addReferencesCategoriesToNode(List<IncomingReference> incomingReferences, 
			Map<ReferenceCategory, List<IncomingReference>> referenceCategories, 
			DefaultMutableTreeNode node) {
		
		for (IncomingReference incomingReference : incomingReferences) {
			if (!referenceCategories
					.containsKey(IncomingReferenceUtil.getReferenceCategory(incomingReference.getDPI()))) {
				referenceCategories.put(IncomingReferenceUtil.getReferenceCategory(incomingReference.getDPI()),
						new ArrayList<>());
			}
			referenceCategories.get(IncomingReferenceUtil.getReferenceCategory(incomingReference.getDPI()))
					.add(incomingReference);

		}
		
		List<ReferenceCategory> refCateg = new ArrayList<>(referenceCategories.keySet());
		if (!refCateg.isEmpty()) {
			refCateg.sort((a, b) -> {
				int aCoef = 0;
				if (a != ReferenceCategory.MAP) {
					aCoef = a == ReferenceCategory.CROSS ? 1 : 2;
				}
				int bCoef = 0;
				if (b != ReferenceCategory.MAP) {
					bCoef = b == ReferenceCategory.CROSS ? 1 : 2;
				}
				return Integer.compare(aCoef, bCoef);
			});
			refCateg.forEach(
					referenceCategory -> node.add(new DefaultMutableTreeNode(referenceCategory)));
		}
	}
	
	
	/**
	 * @param dpi for current incoming reference.
	 * 
	 * @return The reference category.
	 */
	public static ReferenceCategory getReferenceCategory(DocumentPositionedInfo dpi) {
		ReferenceCategory referenceCategory = ReferenceCategory.CROSS;
		if (dpi.getMessage().endsWith("[CONREF]") || dpi.getMessage().endsWith("[CONKEYREF]")) {
			referenceCategory = ReferenceCategory.CONTENT;
		} else if (dpi.getSystemID().endsWith(".ditamap")) {
			referenceCategory = ReferenceCategory.MAP;
		}

		return referenceCategory;
	}

	
	/**
	 * Expands the first level for tree.
	 * 
	 * @param root the root of the tree
	 * @param tree the tree to expand
	 */
	public static void expandFirstLevelOfTree(DefaultMutableTreeNode root, JTree tree) {
		DefaultMutableTreeNode currentNode = root.getNextNode();
		do {
			if (currentNode.getLevel() == 1)
				tree.expandPath(new TreePath(currentNode.getPath()));
			currentNode = currentNode.getNextNode();
		} while (currentNode != null);
	}
	
	
	/**
	 * Copy the file location to clipboard.
	 * 
	 * @param tree the tree.
	 */
	public static void copyFileLocationToClipboard(JTree tree) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node != null) {
			Object userObject = node.getUserObject();
			if (userObject instanceof IncomingReference) {
				IncomingReference referenceInfo = (IncomingReference) userObject;
				StringSelection selection = new StringSelection(referenceInfo.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		}
	}
	
	
	/**
	 * Opens the selected Dita file and selects within it the location of the reference.
	 * 
	 * @param workspaceAccess The plugin workspace
	 * @param tree            The tree.
	 * @param refreshTimer    Timer for loading.                
	 */
	public static void openFileAndSelectReference(PluginWorkspace workspaceAccess, JTree tree, Timer refreshTimer) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node != null) {
			Object userObject = node.getUserObject();
			if (userObject instanceof IncomingReference) {
				IncomingReference referenceInfo = (IncomingReference) userObject;
				try {
					URL url = new URL(referenceInfo.getSystemId());
					if (workspaceAccess.open(url)) {
						WSEditor editorAccess = workspaceAccess.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA);
						if (editorAccess != null) {
							WSEditorPage currentPage = editorAccess.getCurrentPage();
							refreshTimer.schedule(new TimerTask() {
								@Override
								public void run() {
									SwingUtilities.invokeLater(() -> selectRange(currentPage, referenceInfo));
								}
							}, 50);
						}
					}
				} catch (MalformedURLException e1) {
					LOGGER.error(e1, e1);
				}
			}
		}
	}
	
	
	/**
	 * Search for ongoing references and compute the label for them
	 * 
	 * @param editorLocation The editor to search location.
	 * @param graph          The graph to be created.
	 * 
	 * @return The list of found ongoing references
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static List<IncomingReference> searchIncomingRef(URL editorLocation, Object graph)
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		List<IncomingReference> incomingReferences = null;
		
		if (VersionUtil.isOxygenVersionNewer(23, 0)) {
			Class<?> ditaAccess = Class.forName(DITA_ACCESS_CLASS_NAME);
			if (graph == null) {
				Method createReferencesGraph = ditaAccess
						.getDeclaredMethod(VersionUtil.METHOD_NAME_CREATE_REFERENCE_GRAPH);
				graph = createReferencesGraph.invoke(null);
			}
			Method searchReferences = ditaAccess.getDeclaredMethod(VersionUtil.METHOD_NAME_SEARCH_REFERENCES, URL.class,
					Object.class);
			incomingReferences = new ArrayList<>();
			List<DocumentPositionedInfo> result;
			result = (List<DocumentPositionedInfo>) searchReferences.invoke(null, editorLocation, graph);
			for (DocumentPositionedInfo documentPositionedInfo : result) {
				incomingReferences.add(new IncomingReference(documentPositionedInfo));
			}
			Collections.sort(incomingReferences);
			for (int i = 1; i < incomingReferences.size(); i++) {
				IncomingReference ref1 = incomingReferences.get(i - 1);
				IncomingReference ref2 = incomingReferences.get(i);
				if (ref1.getSystemId().equals(ref2.getSystemId())) {
					ref1.setShowExtraLineNumberInformation();
					ref2.setShowExtraLineNumberInformation();
				}
			}
		}
		
		return incomingReferences;
	}
	
	
	/**
	 * Select the corresponding Element in Editor.
	 * 
	 * @param page Text / Author Page
	 * @param dpi  The document position info
	 */
	private static void selectRange(WSEditorPage page, IncomingReference dpi) {
		if (page instanceof WSTextBasedEditorPage) {
			WSTextBasedEditorPage authorPage = (WSTextBasedEditorPage) page;
			try {
				int[] startEndOffsets = authorPage.getStartEndOffsets(dpi.getDPI());
				authorPage.select(startEndOffsets[0], startEndOffsets[1]);
			} catch (BadLocationException e) {
				LOGGER.error(e, e);
			}
		}
	}
	
}
