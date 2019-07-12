package com.oxygenxml.sdksamples.workspace;

import java.net.URL;
import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ro.sync.ecss.extensions.commons.table.properties.ECPropertiesComposite;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextNodeRange;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReferencesTree extends JTree {

	private static final String OUTGOING_REFERENCES_NOT_AVAILABLE = "Outgoing references not available";

	private static final String NO_OUTGOING_REFERENCES_FOUND = "No outgoing references found";

	static final String ALL_REFS_XPATH_EXPRESSION = "/* | //*[contains(@class, ' topic/image ')] | //*[contains(@class, ' topic/xref ')] | //*[contains(@class, ' topic/link ')] | //*[@conref] | //*[@conkeyref]";

	private static final String ROOT_REFERENCES = "Root References";

	protected static final String RELATED_LINKS = "Related links";

	protected static final String CONTENT_REFERENCES = "Content references";

	protected static final String CROSS_REFERENCES = "Cross references";

	protected static final String IMAGE_REFERENCES = "Image references";

	private static final Logger LOGGER = Logger.getLogger(ReferencesTree.class);

	private StandalonePluginWorkspace pluginWorkspaceAccess;

	public ReferencesTree(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.setRootVisible(false);
		ReferencesTreeCellRenderer refTreeCellRenderer = new ReferencesTreeCellRenderer(pluginWorkspaceAccess);
		this.setCellRenderer(refTreeCellRenderer);
		// Install tooltips on JTree.
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	/**
	 * Refresh references tree.
	 * 
	 * @throws XPathException
	 */
	void refreshReferenceTree(WSEditor editorAccess) {
		try {
			if (editorAccess != null) {
				if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())
						&& editorAccess.getCurrentPage() instanceof WSXMLTextEditorPage) {
					final WSXMLTextEditorPage textPage = (WSXMLTextEditorPage) editorAccess.getCurrentPage();
					LOGGER.info("EDITOR LOCATION " + textPage.getParentEditor().getEditorLocation());
					// Preliminary refresh
					this.setPreliminaryTextTree(textPage);
				} else {
					// TODO cleanup tree.
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
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ROOT_REFERENCES);
		ReferencesTreeModel referencesTreeModel = new ReferencesTreeModel();
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(OUTGOING_REFERENCES_NOT_AVAILABLE);
		root.add(noReferencesAvailable);
		referencesTreeModel.setRoot(root);
		this.setModel(referencesTreeModel);
	}

	/**
	 * Find out all the outgoing references and show them in the tree.
	 * 
	 * @param textPage
	 * @throws XPathExpressionException
	 * @throws XPathException
	 */
	private void setPreliminaryTextTree(WSXMLTextEditorPage textPage) throws XPathExpressionException, XPathException {
		ReferencesTreeModel referencesTreeModel = new ReferencesTreeModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ROOT_REFERENCES);
		addAllReferences(textPage, root);

		referencesTreeModel.setRoot(root);
		this.setModel(referencesTreeModel);

		// expand all Nodes of The Reference Tree
		expandAllNodes(this, 0, this.getRowCount());
	}

	/**
	 * 
	 * @param textPage
	 * @param referenceExpression
	 * @param root
	 * @throws XPathException
	 */
	private void addAllReferences(WSXMLTextEditorPage textPage, DefaultMutableTreeNode root) throws XPathException {

		DefaultMutableTreeNode imageReferences = new DefaultMutableTreeNode(IMAGE_REFERENCES);
		DefaultMutableTreeNode crossReferences = new DefaultMutableTreeNode(CROSS_REFERENCES);
		DefaultMutableTreeNode contentReferences = new DefaultMutableTreeNode(CONTENT_REFERENCES);
		DefaultMutableTreeNode relatedLinks = new DefaultMutableTreeNode(RELATED_LINKS);
		DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(NO_OUTGOING_REFERENCES_FOUND);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(OUTGOING_REFERENCES_NOT_AVAILABLE);

		Object[] referenceNodes = textPage.evaluateXPath(ALL_REFS_XPATH_EXPRESSION);
		WSXMLTextNodeRange[] referenceNodeRanges = textPage.findElementsByXPath(ALL_REFS_XPATH_EXPRESSION);
		System.err.println("REF NODES " + Arrays.toString(referenceNodes));
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

						LOGGER.info(" -------------> NODE NAME: " + currentElement.getNodeName());
						LOGGER.info("startLine: " + referenceNodeRanges[i].getStartLine() + " startColumn: "
								+ referenceNodeRanges[i].getStartColumn());
						LOGGER.info("endLine: " + referenceNodeRanges[i].getEndLine() + " endColumn: "
								+ referenceNodeRanges[i].getEndColumn());

						NodeRange refRange = new NodeRange(currentElement, referenceNodeRanges[i]);

						if (currentElemAttributes.getNamedItem("class") != null
								&& currentElemAttributes.getNamedItem("class").getNodeValue().contains("topic/image")) {
							imageReferences.add(new DefaultMutableTreeNode(refRange));
						} else if (currentElemAttributes.getNamedItem("class") != null
								&& currentElemAttributes.getNamedItem("class").getNodeValue().contains("topic/xref")) {							
							crossReferences.add(new DefaultMutableTreeNode(refRange));
						} else if (currentElement.getAttributes().getNamedItem("conkeyref") != null
								|| currentElement.getAttributes().getNamedItem("conref") != null) {
							contentReferences.add(new DefaultMutableTreeNode(refRange));
						} else if (currentElemAttributes.getNamedItem("class") != null
								&& currentElemAttributes.getNamedItem("class").getNodeValue().contains("topic/link")) {
							relatedLinks.add(new DefaultMutableTreeNode(refRange));
						}

					}
					// Do not add empty categories
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
	 * Expand all the nodes from the very beginning
	 * 
	 * @param tree
	 * @param startingIndex
	 * @param rowCount
	 */
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
}
