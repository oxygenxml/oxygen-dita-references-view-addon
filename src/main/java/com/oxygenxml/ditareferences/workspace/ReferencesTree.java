package com.oxygenxml.ditareferences.workspace;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.translator.Tags;
import com.oxygenxml.ditareferences.translator.Translator;
import com.oxygenxml.ditareferences.workspace.authorpage.AuthorPageReferencesTreeCaretListener;
import com.oxygenxml.ditareferences.workspace.authorpage.AuthorReferencesCollector;
import com.oxygenxml.ditareferences.workspace.textpage.TextPageReferencesTreeCaretListener;
import com.oxygenxml.ditareferences.workspace.textpage.TextReferencesCollector;

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

	/**
	 * The pluginWorkspaceAccess.
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * The editorAccess.
	 */
	private WSEditor editorAccess;

	/**
	 * Get the editorAccess.
	 * 
	 * @return The editorAccess
	 */
	public WSEditor getEditorAccess() {
		return editorAccess;
	}

	private ReferencesMouseAdapter refMouseAdapter;
	private ReferencesKeyAdapter enterKeyAdapter;
	private ReferencesTreeSelectionListener refTreeSelectionListener;
	private TextPageReferencesTreeCaretListener textPageCaretListener;
	private AuthorPageReferencesTreeCaretListener authorPageCaretListener;

	/**
	 * True if the tree is showing.
	 */
	private boolean isShowing = false;
	
	/**
	 * Setter.
	 * 
	 * @param isShowing
	 */
	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}

	/**
	 * Construct the ReferencesTree.
	 * 
	 * @param pluginWorkspaceAccess The pluginWorkspaceAccess
	 * @param keysProvider          The Map with the current DITA Map keys
	 * @param translator            The translator
	 */
	public ReferencesTree(StandalonePluginWorkspace pluginWorkspaceAccess, KeysProvider keysProvider,
			Translator translator) {

		this.setRootVisible(false);
		this.setShowsRootHandles(true);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;

		// set transferHandler to copy text of references node
		this.setTransferHandler(new RefNodeTransferHandler());

		// set cellRenderer for ReferencesTree
		this.setCellRenderer(new ReferencesTreeCellRenderer(pluginWorkspaceAccess.getImageUtilities(), translator, keysProvider));

		// install toolTips on JTree.
		ToolTipManager.sharedInstance().registerComponent(this);

		// install selection listener
		this.refTreeSelectionListener = new ReferencesTreeSelectionListener(this);
		this.getSelectionModel().addTreeSelectionListener(this.refTreeSelectionListener);
				
		
		// install caret listener for textPage and check if current page corresponds
		this.textPageCaretListener = new TextPageReferencesTreeCaretListener(
				() -> {
					if(editorAccess != null) {
						if (editorAccess.getCurrentPage() instanceof WSXMLTextEditorPage) {
							return (WSXMLTextEditorPage) editorAccess.getCurrentPage();
						}
					}

					return null;
				},
				this, 
				this.refTreeSelectionListener);


		// install caret listener for authorPage and check if current page corresponds
		this.authorPageCaretListener = new AuthorPageReferencesTreeCaretListener(
				() -> {
					if(editorAccess != null) {
						if (editorAccess.getCurrentPage() instanceof WSAuthorEditorPage) {
							return (WSAuthorEditorPage) editorAccess.getCurrentPage();
						}
					}

					return null;
				}, 
				this, 
				this.refTreeSelectionListener);
		

		// popUp Menu for Leaf Nodes
		this.refMouseAdapter = new ReferencesMouseAdapter(this, this.pluginWorkspaceAccess, keysProvider, translator);
		this.addMouseListener(this.refMouseAdapter);

		// Key Adapter for Leaf Nodes when Enter Key is pressed
		this.enterKeyAdapter = new ReferencesKeyAdapter(this, this.pluginWorkspaceAccess, keysProvider, translator);
		this.addKeyListener(this.enterKeyAdapter);
		
		// add Hierarchy Listener when side-view is not hidden
		this.addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
					boolean oldShowing = ReferencesTree.this.isShowing;
					ReferencesTree.this.isShowing = isShowing();
					if (isShowing && !oldShowing) {
						refreshReferenceTreeInternal(editorAccess);
					} else if (!isShowing) {
						refreshReferenceTreeInternal(null);
					}
				}
			}
		});
	}

	/**
	 * Refresh referencesTree depending on file type. For example: DITA, CSS or XML
	 * in Grid Mode etc.
	 * 
	 * @param editorAccess The current editorAccess
	 */
	public void refreshReferenceTree(WSEditor editorAccess) {
		// Remove previous listeners from Author and Text page.
		textPageCaretListener.unbindTextPageWithCaret();
		authorPageCaretListener.unbindAuthorPageWithCaret();

		this.editorAccess = editorAccess;
		if (isShowing) {
			refreshReferenceTreeInternal(editorAccess);
		} else {
			// Do nothing
			refreshReferenceTreeInternal(null);
		}
	}

	/**
	 * Refresh referencesTree internal.
	 * 
	 * @param editorAccess The current editorAccess
	 */
	private void refreshReferenceTreeInternal(WSEditor editorAccess) {
		this.refMouseAdapter.setEditorAccess(editorAccess);
		this.enterKeyAdapter.setEditorAccess(editorAccess);

		try {
			if (editorAccess != null) {
				if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())
						&& editorAccess.getCurrentPage() instanceof WSXMLTextEditorPage

						|| EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
								&& editorAccess.getCurrentPage() instanceof WSAuthorEditorPage) {
					// Preliminary refresh
					this.setPreliminaryTree(editorAccess);
				} else {
					// Other content type, like CSS, or an XML opened in Grid mode.
					this.setNoRefsAvailableTree();
				}

			} else {
				this.setNoRefsAvailableTree();
			}
		} catch (Exception e) {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(e, e);
			}
			this.setNoRefsAvailableTree();
		} 
	}

	/**
	 * Build the collector for Editor: Author/Text Page.
	 * 
	 * @param currentPageID The specific Page ID for Text / Author
	 * @return The specific Text/Author Page Collector
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
	 * @throws XPathException
	 */
	private void setPreliminaryTree(WSEditor editorAccess) throws XPathException {

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
		expandAllRows();

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
			textPageCaretListener.bindTextPageWithCaret();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(textPageCaretListener);
		} else {
			// get current Caret Listener for AuthorPage and update it
			authorPageCaretListener.bindAuthorPageWithCaret();
			this.refTreeSelectionListener.setCaretSelectionInhibitor(authorPageCaretListener);
		}
	}

	/**
	 * Expand all nodes from the very beginning.
	 */
	public void expandAllRows() {
		for (int i = 0; i < this.getRowCount(); i++) {
			this.expandRow(i);
		}
	}
	
}
