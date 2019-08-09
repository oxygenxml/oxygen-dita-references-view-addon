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

@SuppressWarnings("serial")
public class ReferencesTree extends Tree {
	/**
	 * The ReferencesTree Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ReferencesTree.class);

	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private WSEditor editorAccess;

	public WSEditor getEditorAccess() {
		return editorAccess;
	}

	private ReferencesMouseAdapter refMouseAdapter;
	private EnterForReferencesKeyAdapter enterKeyAdapter;
	private ReferencesTreeSelectionListener refTreeSelectionListener;
	private TextPageReferencesTreeCaretListener textPageCaretListener;
	private AuthorPageReferencesTreeCaretListener authorPageCaretListener;

	/**
	 * Construct the ReferencesTree.
	 * 
	 * @param pluginWorkspaceAccess
	 * @param keysProvider          The Map with the current DITAMAP keys
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

		// set cellRenderer for ReferencesTree
		this.setCellRenderer(new ReferencesTreeCellRenderer(pluginWorkspaceAccess.getImageUtilities(), translator));

		// install toolTips on JTree.
		ToolTipManager.sharedInstance().registerComponent(this);

		// install selection listener
		this.refTreeSelectionListener = new ReferencesTreeSelectionListener(this);
		this.getSelectionModel().addTreeSelectionListener(this.refTreeSelectionListener);
				
		// install caret listener for textPage
		this.textPageCaretListener = new TextPageReferencesTreeCaretListener(
				() -> (WSXMLTextEditorPage)editorAccess.getCurrentPage(), 
				this, 
				this.refTreeSelectionListener);	
		
		// install caret listener for authorPage
		this.authorPageCaretListener = new AuthorPageReferencesTreeCaretListener(
				() -> (WSAuthorEditorPage)editorAccess.getCurrentPage(),
				this, 
				this.refTreeSelectionListener);
		
		// popUp Menu for Leaf Nodes
		this.refMouseAdapter = new ReferencesMouseAdapter(this, this.pluginWorkspaceAccess, keysProvider, translator);
		this.addMouseListener(this.refMouseAdapter);

		// Key Adapter for Leaf Nodes when Enter Key is pressed
		this.enterKeyAdapter = new EnterForReferencesKeyAdapter(this, this.pluginWorkspaceAccess, keysProvider);
		this.addKeyListener(this.enterKeyAdapter);
	}

	/**
	 * Refresh referencesTree depending on file type. For example: DITA, CSS or XML
	 * in Grid Mode etc.
	 * 
	 * @param editorAccess The current editorAccess
	 * @throws XPathException
	 */
	void refreshReferenceTree(WSEditor editorAccess) {
		this.editorAccess = editorAccess;
		this.refMouseAdapter.setEditorAccess(editorAccess);
		this.enterKeyAdapter.setEditorAccess(editorAccess);

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
	 * Build the collector for Editor: Author/Text Page
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
	 * Set the root for "No available references". For example: CSS / XML opened in
	 * Grid Mode.
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
	 * Find out all the outgoing references and show them in ReferencesTree.
	 * 
	 * @param editorAccess The editorAccess
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

		// install collector of nodeRanges where the XPath expression is evaluated on
		ReferencesCollector referencesCollector = buildCollector(editorAccess.getCurrentPageID());
		referencesCollector.collectReferences(editorPage, root);

		referencesTreeModel.setRoot(root);
		this.setModel(referencesTreeModel);

		// expand all nodes of ReferencesTree
		expandAllNodesInRefTree(this, 0, this.getRowCount());

		// updates for Caret and Selection Listener
		installUpdateListeners(editorPage);
	}

	/**
	 * Install the selection and caret updates for TextPage/AuthorPage.
	 * 
	 * @param page The Text/Author Page
	 */
	private void installUpdateListeners(WSEditorPage page) {
		if (page instanceof WSXMLTextEditorPage) {
			// get current Caret Listener for TextPage and update it
			textPageCaretListener.bind();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(textPageCaretListener);
		} else {
			// get current Caret Listener for AuthorPage and update it
			authorPageCaretListener.bind();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(authorPageCaretListener);
		}
	}

	/**
	 * Expand all nodes from the very beginning.
	 * 
	 * @param refTree          The ReferencesTree
	 * @param startingIndex
	 * @param rowCount
	 */
	private void expandAllNodesInRefTree(JTree refTree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			refTree.expandRow(i);
		}

		if (refTree.getRowCount() != rowCount) {
			expandAllNodesInRefTree(refTree, rowCount, refTree.getRowCount());
		}
	}
}
