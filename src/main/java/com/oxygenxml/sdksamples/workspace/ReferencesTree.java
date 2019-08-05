package com.oxygenxml.sdksamples.workspace;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import com.oxygenxml.sdksamples.translator.Tags;
import com.oxygenxml.sdksamples.translator.Translator;
import com.oxygenxml.sdksamples.workspace.authorpage.AuthorPageReferencesTreeCaretListener;
import com.oxygenxml.sdksamples.workspace.authorpage.AuthorReferencesCollector;
import com.oxygenxml.sdksamples.workspace.textpage.TextPageReferencesTreeCaretListener;
import com.oxygenxml.sdksamples.workspace.textpage.TextReferencesCollector;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Tree;

public class ReferencesTree extends Tree {
	/**
	 * The XPath expression with all the possible references available in the
	 * current textPage to be evaluated.
	 */
	static final String ALL_REFS_XPATH_EXPRESSION = "/* | //*[contains(@class, ' topic/image ')] | //*[contains(@class, ' topic/xref ')]"
			+ " | //*[contains(@class, ' topic/link ')] | //*[@conref] | //*[@conkeyref] | //*[@keyref  and not(contains(@class, ' topic/image ')) "
			+ "and not(contains(@class, ' topic/link '))  and  not(contains(@class, ' topic/xref '))]";

	/**
	 * The ReferencesTree Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ReferencesTree.class);

	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private WSEditor editorAccess;

	public WSEditor getEditorAccess() {
		return editorAccess;
	}

	private ReferencesTreeSelectionListener refTreeSelectionListener;
	private ReferencesMouseAdapter refMouseAdapter;

	private TextPageReferencesTreeCaretListener textPageCaretListener;

	private AuthorPageReferencesTreeCaretListener authorPageCaretListener;
	
	/**
	 * The constructor including the tree selection listener
	 * 
	 * @param pluginWorkspaceAccess
	 * @param keysProvider
	 * @param translator            The translator
	 */
	public ReferencesTree(
			StandalonePluginWorkspace pluginWorkspaceAccess, 
			KeysProvider keysProvider,
			Translator translator) {
		this.setRootVisible(false);
		this.setShowsRootHandles(false);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;

		// set cell renderer for the references tree
		this.setCellRenderer(new ReferencesTreeCellRenderer(pluginWorkspaceAccess.getImageUtilities(), translator));

		// install toolTips on JTree.
		ToolTipManager.sharedInstance().registerComponent(this);

		this.refTreeSelectionListener = new ReferencesTreeSelectionListener(this);
		this.getSelectionModel().addTreeSelectionListener(this.refTreeSelectionListener);
				
		this.textPageCaretListener = new TextPageReferencesTreeCaretListener(
				() -> (WSXMLTextEditorPage)editorAccess.getCurrentPage(), 
				this, 
				this.refTreeSelectionListener);	
		
		this.authorPageCaretListener = new AuthorPageReferencesTreeCaretListener(
				() -> (WSAuthorEditorPage)editorAccess.getCurrentPage(),
				this, 
				this.refTreeSelectionListener);
		
		// popUp Menu for Element Nodes
		this.refMouseAdapter = new ReferencesMouseAdapter(this, this.pluginWorkspaceAccess, keysProvider, translator);
		this.addMouseListener(this.refMouseAdapter);
	}

	/**
	 * Refresh references tree.
	 * 
	 * @throws XPathException
	 */
	void refreshReferenceTree(WSEditor editorAccess) {
		this.editorAccess = editorAccess;	
		this.refMouseAdapter.setEditorAccess(editorAccess);

		try {
			if (editorAccess != null) {
				if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())
						&& editorAccess.getCurrentPage() instanceof WSXMLTextEditorPage
						
					|| EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
					&& editorAccess.getCurrentPage() instanceof WSAuthorEditorPage) {					
					// Preliminary refresh
					this.setPreliminaryTextTree(editorAccess);
				} else {
					// Other content type, like CSS, or an XML opened in Grid mode.
					this.setNoRefsAvailableTree();
				}
			} else {
				this.setNoRefsAvailableTree();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e, e);
			this.setNoRefsAvailableTree();
		}
	}

	/**
	 * Build the collector depending on the Page Mode, Author or Text.
	 * 
	 * @param currentPageID The specific Page ID for Text or Author
	 * @return The specific Page Collector
	 */
	private ReferencesCollector buildCollector(String currentPageID) {
		if (currentPageID.equals(EditorPageConstants.PAGE_TEXT)) {
			return new TextReferencesCollector();
		} else {
			return new AuthorReferencesCollector();
		}
	}

	/**
	 * Set the root for "No available references" when CSS / XML is opened in Grid
	 * mode.
	 */
	private void setNoRefsAvailableTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(Tags.ROOT_REFERENCES);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(
				Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);
		root.add(noReferencesAvailable);
		DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root);
		this.setModel(referencesTreeModel);
	}

	/**
	 * Find out all the outgoing references and show them in the tree.
	 * 
	 * @param editorPage The XML textPage
	 * @throws XPathExpressionException
	 * @throws XPathException
	 * @throws AuthorOperationException 
	 */

	private void setPreliminaryTextTree(WSEditor editorAccess)
			throws XPathExpressionException, XPathException, AuthorOperationException {
		
		// set root for ReferencesTree
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(Tags.ROOT_REFERENCES);
		DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root);

		WSEditorPage editorPage = editorAccess.getCurrentPage();
		ReferencesCollector referencesCollector = buildCollector(editorAccess.getCurrentPageID());
		referencesCollector.collectReferences(editorPage, root);

		referencesTreeModel.setRoot(root);
		this.setModel(referencesTreeModel);

		// expand all Nodes of The Reference Tree
		expandAllNodesInRefTree(this, 0, this.getRowCount());

		// updates for Caret and Selection Listener
		installUpdateListeners(editorPage);
	}

	/**
	 * Install the selection and caret updates for TextPage or AuthorPage.
	 * 
	 * @param page Either TextPage or AuthorPage
	 */
	private void installUpdateListeners(WSEditorPage page) {		
		if (page instanceof WSXMLTextEditorPage) {
			// get current Caret Listener for Text Page and update it
			textPageCaretListener.bind();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(textPageCaretListener);
		} else {
			// get current Caret Listener for Author Page and update it
			authorPageCaretListener.bind();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(authorPageCaretListener);
		}
	}

	/**
	 * Expand all the nodes from the very beginning.
	 * 
	 * @param tree          The references Tree
	 * @param startingIndex The beginning
	 * @param rowCount      All rows
	 */
	private void expandAllNodesInRefTree(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodesInRefTree(tree, rowCount, tree.getRowCount());
		}
	}

}
