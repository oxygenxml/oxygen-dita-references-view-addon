package com.oxygenxml.sdksamples.workspace;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.oxygenxml.sdksamples.translator.Tags;
import com.oxygenxml.sdksamples.translator.Translator;

import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
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

	private JTextArea currentTextComponent;
	private ReferencesTreeCaretListener currentCaretListener;

	private ReferencesTreeSelectionListener refTreeSelectionListener;
	private ReferencesMouseAdapter refMouseAdapter;

	/**
	 * The constructor including the tree selection listener
	 * 
	 * @param pluginWorkspaceAccess
	 * @param keysProvider
	 * @param translator            The translator
	 */
	public ReferencesTree(StandalonePluginWorkspace pluginWorkspaceAccess, KeysProvider keysProvider,
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
		refMouseAdapter.setEditorAccess(editorAccess);

		try {
			if (editorAccess != null) {
				if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())
						&& editorAccess.getCurrentPage() instanceof WSXMLTextEditorPage) {
					final WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) editorAccess.getCurrentPage();
					// Preliminary refresh
					this.setPreliminaryTextTree(textPage);
				} else {
					// CSS is opened or XML is opened in Grid mode.
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
	 * @param textPage The XML textPage
	 * @throws XPathExpressionException
	 * @throws XPathException
	 */

	private void setPreliminaryTextTree(final WSXMLTextEditorPage textPage)
			throws XPathExpressionException, XPathException {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(Tags.ROOT_REFERENCES);
		DefaultTreeModel referencesTreeModel = new DefaultTreeModel(root);

		addAllReferences(textPage, root);

		referencesTreeModel.setRoot(root);
		this.setModel(referencesTreeModel);

		// expand all Nodes of The Reference Tree
		expandAllNodesInRefTree(this, 0, this.getRowCount());

		// get current Caret Listener and update it 
		if (currentTextComponent != null && currentCaretListener != null) {
			currentTextComponent.removeCaretListener(currentCaretListener);
		}

		this.currentTextComponent = (JTextArea) textPage.getTextComponent();
		this.currentCaretListener = new ReferencesTreeCaretListener(textPage, this, this.refTreeSelectionListener);
		this.refTreeSelectionListener.setCaretSelectionInhibitor(currentCaretListener);
		this.currentTextComponent.addCaretListener(currentCaretListener);
	}

	/**
	 * Add all the category nodes and the references for each of them taking into account the "class" values of the leaf nodes
	 * 
	 * @param textPage The XML textPage
	 * @param root     The rootNode
	 * @throws XPathException
	 */
	private void addAllReferences(WSXMLTextEditorPage textPage, DefaultMutableTreeNode root) throws XPathException {

		DefaultMutableTreeNode imageReferences = new DefaultMutableTreeNode(Tags.IMAGE_REFERENCES);
		DefaultMutableTreeNode crossReferences = new DefaultMutableTreeNode(Tags.CROSS_REFERENCES);
		DefaultMutableTreeNode contentReferences = new DefaultMutableTreeNode(Tags.CONTENT_REFERENCES);
		DefaultMutableTreeNode relatedLinks = new DefaultMutableTreeNode(Tags.RELATED_LINKS);
		DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(Tags.NO_OUTGOING_REFERENCES_FOUND);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(
				Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);

		Object[] referenceNodes = textPage.evaluateXPath(ALL_REFS_XPATH_EXPRESSION);
		WSXMLTextNodeRange[] referenceNodeRanges = textPage.findElementsByXPath(ALL_REFS_XPATH_EXPRESSION);

		// DITA topics with outgoing references
		// The root element is the first in the list of references.
		if (referenceNodes.length >= 1) {
			Element rootElement = (Element) referenceNodes[0];
			NamedNodeMap rootAttributes = rootElement.getAttributes();
			// DITA topic or Composite topic
			if ((rootAttributes.getNamedItem("class") != null
					&& rootAttributes.getNamedItem("class").getNodeValue().contains("topic/topic"))
					|| rootElement.getNodeName().equals("dita")) {

				if (referenceNodes.length == 1) {
					// DITA topic but no reference found
					root.add(noReferencesFound);
				} else {

					// It is an interesting XML document, it's DITA.
					for (int i = 1; i < referenceNodes.length; i++) {
						Element currentElement = (Element) referenceNodes[i];
						NamedNodeMap currentElemAttributes = currentElement.getAttributes();

						NodeRange refRange = new TextPageNodeRange(currentElement, referenceNodeRanges[i]);

						Node classAttribute = currentElemAttributes.getNamedItem("class");
						if (classAttribute != null) {
							if (classAttribute.getNodeValue().contains(" topic/image ")) {
								imageReferences.add(new DefaultMutableTreeNode(refRange));
							} else if (classAttribute.getNodeValue().contains(" topic/xref ")) {
								crossReferences.add(new DefaultMutableTreeNode(refRange));
							} else if (classAttribute.getNodeValue().contains(" topic/link ")) {
								relatedLinks.add(new DefaultMutableTreeNode(refRange));
							} else if (currentElement.getAttributes().getNamedItem("conkeyref") != null
									|| currentElement.getAttributes().getNamedItem("conref") != null) {
								contentReferences.add(new DefaultMutableTreeNode(refRange));
							} else {
								// key references to values defined in the DITAMAP
								contentReferences.add(new DefaultMutableTreeNode(refRange));
							}
						}
					}
					// Do not add empty categories to the referencesTree
					if (imageReferences.getChildCount() != 0) {
						root.add(imageReferences);
					}
					if (crossReferences.getChildCount() != 0) {
						root.add(crossReferences);
					}
					if (contentReferences.getChildCount() != 0) {
						root.add(contentReferences);
					}
					if (relatedLinks.getChildCount() != 0) {
						root.add(relatedLinks);
					}

				}
			} else {
				// an XML file which is not DITA: HTML for example
				root.add(noReferencesAvailable);

			}

		} else {
			LOGGER.error("Invalid situation");
			// DITA topic with NO references
			root.add(noReferencesFound);
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
