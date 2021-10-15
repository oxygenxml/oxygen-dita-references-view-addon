package com.oxygenxml.ditareferences.tree.references.incoming;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
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
	 * Contains the coefficient for each type of incoming reference. 
	 * Depending on this coefficient, they will be sorted into trees.
	 */
	private static final Map<ReferenceCategory, Integer> CATEGORY_COEFFICIENT;
	
	static {
	  CATEGORY_COEFFICIENT = new EnumMap<>(ReferenceCategory.class);
	  CATEGORY_COEFFICIENT.put(ReferenceCategory.MAP, 0);
	  CATEGORY_COEFFICIENT.put(ReferenceCategory.CROSS, 1);
	  CATEGORY_COEFFICIENT.put(ReferenceCategory.CONTENT, 2);
	}
	
	
	
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
	 * @param rootNode                   The node where the reference categories are added.
	 */
	public static void addReferencesCategoriesToRoot(List<IncomingReference> incomingReferences, DefaultMutableTreeNode rootNode) {
		
		Map<ReferenceCategory, List<IncomingReference>> referenceCategories = new EnumMap<>(ReferenceCategory.class);
		
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
			refCateg.sort((a, b) -> Integer.compare(CATEGORY_COEFFICIENT.get(a), CATEGORY_COEFFICIENT.get(b)));
			refCateg.forEach(
					referenceCategory -> {
						DefaultMutableTreeNode referenceToAdd = new DefaultMutableTreeNode(referenceCategory);
						referenceCategories.get(referenceCategory).forEach(ref -> referenceToAdd.add(new DefaultMutableTreeNode(ref)));
						rootNode.add(referenceToAdd);
					});
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
		while (currentNode != null) {
			if (currentNode.getLevel() == 1) {
				tree.expandPath(new TreePath(currentNode.getPath()));
			}	
			currentNode = currentNode.getNextNode();
		}
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
