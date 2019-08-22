/**
 * 
 */
package com.oxygenxml.ditareferences.workspace;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.translator.Tags;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLink;
import com.oxygenxml.ditareferences.workspace.rellinks.RelLinkNodeRangeImpl;
import com.oxygenxml.ditareferences.workspace.rellinks.RellinksAccessor;

import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.xml.XPathException;

/**
 * Collector for specific NodeRanges.
 * 
 * @author Alexandra_Dinisor
 *
 */
public abstract class ReferencesCollector {
	
	private static final String CLASS = "class";

	private static final Logger LOGGER = Logger.getLogger(ReferencesCollector.class);

	/**
	 * The XPath expression with all possible references available in current
	 * editor.
	 */
	protected static final String ALL_REFS_XPATH_EXPRESSION = "/* | //*[contains(@class, ' topic/image ')] | //*[contains(@class, ' topic/xref ')]"
			+ " | //*[contains(@class, ' topic/link ')] | //*[@conref] | //*[@conkeyref] | //*[@keyref  and not(contains(@class, ' topic/image ')) "
			+ "and not(contains(@class, ' topic/link '))  and  not(contains(@class, ' topic/xref '))]";

	/**
	 * Collect the NodeRanges from the XPath evaluation.
	 * 
	 * @param page Generic Page for Text / Author
	 * @return The list of NodeRanges matching the XPath Expression.
	 * @throws XPathException
	 */
	protected abstract List<NodeRange> collect(WSEditorPage page) throws XPathException;

	/**
	 * Add all category nodes and references for each of them checking the "class"
	 * values of the leaf nodes.
	 * 
	 * @param editorPage The XML TextPage
	 * @param root     The rootNode
	 * @throws XPathException
	 * @throws AuthorOperationException
	 */
	public void collectReferences(WSEditorPage editorPage, DefaultMutableTreeNode root) throws XPathException{
		DefaultMutableTreeNode imageReferences = new DefaultMutableTreeNode(Tags.IMAGE_REFERENCES);
		DefaultMutableTreeNode crossReferences = new DefaultMutableTreeNode(Tags.CROSS_REFERENCES);
		DefaultMutableTreeNode contentReferences = new DefaultMutableTreeNode(Tags.CONTENT_REFERENCES);
		DefaultMutableTreeNode relatedLinks = new DefaultMutableTreeNode(Tags.RELATED_LINKS);
		DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(Tags.NO_OUTGOING_REFERENCES_FOUND);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);

		// get NodeRanges for TextPage / AuthorPage
		List<NodeRange> ranges = collect(editorPage);

		// The root element is the first in the list of references
		if (!ranges.isEmpty()) {

			// DITA Topic or Composite
			if (isDITARoot(ranges.get(0))) {

				addLinksFromRelTable(editorPage, ranges);

				// DITA topic but no reference found.
				if (ranges.size() == 1) {
					root.add(noReferencesFound);
				} else {
					// It is an interesting XML document, it's DITA.
					addElementsInCategory(imageReferences, crossReferences, contentReferences, relatedLinks, ranges);

					// Do not add empty categories to referencesTree.
					addReferenceCategories(root, imageReferences, crossReferences, contentReferences, relatedLinks);
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
	 * Add links from relationship table if any.
	 * 
	 * @param editorPage The TextPage/AuthorPage
	 * @param ranges     The nodeRanges
	 */
	private void addLinksFromRelTable(WSEditorPage editorPage, List<NodeRange> ranges) {
		if (editorPage != null && editorPage.getParentEditor() != null) {		
			List<RelLink> relLinks = RellinksAccessor
					.getRelationshipTableTargetURLs(editorPage.getParentEditor().getEditorLocation());
			if (!relLinks.isEmpty()) {
				for (int i = 0; i < relLinks.size(); i++) {
					ranges.add(new RelLinkNodeRangeImpl(relLinks.get(i)));
				}
			}
		}
	}

	/**
	 * Add elements in a references category.
	 * 
	 * @param imageReferences   The image references category
	 * @param crossReferences   The cross references category
	 * @param contentReferences The content references category
	 * @param relatedLinks      The related links category
	 * @param ranges            The nodeRanges
	 */
	private void addElementsInCategory(DefaultMutableTreeNode imageReferences, DefaultMutableTreeNode crossReferences,
			DefaultMutableTreeNode contentReferences, DefaultMutableTreeNode relatedLinks, List<NodeRange> ranges) {
		for (int i = 1; i < ranges.size(); i++) {
			NodeRange refRange = ranges.get(i);
			String classAttrValue = refRange.getAttributeValue(CLASS);

			if (classAttrValue != null) {
				// add image nodeRanges in "image references" category of tree
				if (classAttrValue.contains(" topic/image ")) {
					imageReferences.add(new DefaultMutableTreeNode(refRange));
				} else
				// add xref nodeRanges in "cross references" category of tree
				if (classAttrValue.contains(" topic/xref ")) {
					crossReferences.add(new DefaultMutableTreeNode(refRange));
				} else
				// add link nodeRanges in "related links references" category of tree
				if (classAttrValue.contains(" topic/link ")) {
					relatedLinks.add(new DefaultMutableTreeNode(refRange));
				} else
				// add conref/conkeyref nodeRanges in "content references" category of tree
				if (refRange.getAttributeValue("conkeyref") != null || refRange.getAttributeValue("conref") != null) {
					contentReferences.add(new DefaultMutableTreeNode(refRange));
				} else {
					// add key references to values defined in the DITAMAP
					contentReferences.add(new DefaultMutableTreeNode(refRange));
				}
			}
		}
	}

	/**
	 * Add categories in tree which are not empty.
	 * 
	 * @param root              The ReferencesTree root
	 * @param imageReferences   The image references category
	 * @param crossReferences   The cross references category
	 * @param contentReferences The content references category
	 * @param relatedLinks      The related links category
	 */
	private void addReferenceCategories(DefaultMutableTreeNode root, DefaultMutableTreeNode imageReferences,
			DefaultMutableTreeNode crossReferences, DefaultMutableTreeNode contentReferences,
			DefaultMutableTreeNode relatedLinks) {

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

	/**
	 * Check for DITA Topic or Composite.
	 * 
	 * @param range The corresponding nodeRange
	 * @return true if root shows DITA file
	 */
	private boolean isDITARoot(NodeRange range) {
		return (range.getAttributeValue(CLASS) != null && range.getAttributeValue(CLASS).contains("topic/topic"))
				|| range.getNodeName().equals("dita");
	}

}
