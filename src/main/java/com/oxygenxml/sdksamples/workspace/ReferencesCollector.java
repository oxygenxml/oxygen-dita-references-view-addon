/**
 * 
 */
package com.oxygenxml.sdksamples.workspace;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.oxygenxml.sdksamples.translator.Tags;

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
	
	private static final Logger LOGGER = Logger.getLogger(ReferencesCollector.class);

	/**
	 * The XPath expression with all the possible references available in the
	 * current textPage to be evaluated.
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
	 * Add all the category nodes and the references for each of them taking into
	 * account the "class" values of the leaf nodes
	 * 
	 * @param textPage The XML TextPage
	 * @param root     The rootNode
	 * @throws XPathException
	 * @throws AuthorOperationException
	 */
	public void collectReferences(WSEditorPage textPage, DefaultMutableTreeNode root) throws XPathException, AuthorOperationException {
		DefaultMutableTreeNode imageReferences = new DefaultMutableTreeNode(Tags.IMAGE_REFERENCES);
		DefaultMutableTreeNode crossReferences = new DefaultMutableTreeNode(Tags.CROSS_REFERENCES);
		DefaultMutableTreeNode contentReferences = new DefaultMutableTreeNode(Tags.CONTENT_REFERENCES);
		DefaultMutableTreeNode relatedLinks = new DefaultMutableTreeNode(Tags.RELATED_LINKS);
		DefaultMutableTreeNode noReferencesFound = new DefaultMutableTreeNode(Tags.NO_OUTGOING_REFERENCES_FOUND);
		DefaultMutableTreeNode noReferencesAvailable = new DefaultMutableTreeNode(Tags.OUTGOING_REFERENCES_NOT_AVAILABLE);

		// get Reference Nodes and NodeRanges for Text Page or Author Page
		List<NodeRange> ranges = collect(textPage);


		// DITA topics with outgoing references
		// The root element is the first in the list of references
		if (ranges.size() >= 1) {
			// DITA Topic or Composite
			if (isDITARoot(ranges.get(0))) {

				if (ranges.size() == 1) {
					// DITA topic but no reference found
					root.add(noReferencesFound);
				} else {
					// It is an interesting XML document, it's DITA.
					for (int i = 1; i < ranges.size(); i++) {
						NodeRange refRange = ranges.get(i);
						String classAttrValue = refRange.getAttributeValue("class");

						if (classAttrValue != null) {
							if (classAttrValue.contains(" topic/image ")) {

								// add image nodeRanges in "image references" category of tree
								imageReferences.add(new DefaultMutableTreeNode(refRange));
							} else if (classAttrValue.contains(" topic/xref ")) {

								// add xref nodeRanges in "cross references" category of tree
								crossReferences.add(new DefaultMutableTreeNode(refRange));
							} else if (classAttrValue.contains(" topic/link ")) {

								// add link nodeRanges in "related links references" category of tree
								relatedLinks.add(new DefaultMutableTreeNode(refRange));
							} else if (refRange.getAttributeValue("conkeyref") != null
									|| refRange.getAttributeValue("conref") != null) {

								// add conref/conkeyref nodeRanges in "content references" category of tree
								contentReferences.add(new DefaultMutableTreeNode(refRange));
							} else {

								// add key references to values defined in the DITAMAP
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
	 * Check for DITA Topic or Composite.
	 * 
	 * @param range The corresponding nodeRange
	 * @return
	 */
	private boolean isDITARoot(NodeRange range) {
		return (range.getAttributeValue("class") != null && range.getAttributeValue("class").contains("topic/topic"))
				|| range.getNodeName().equals("dita");
	}
	
}
