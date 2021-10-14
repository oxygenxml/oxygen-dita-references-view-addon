package com.oxygenxml.ditareferences.tree.references.incoming;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A custom MutableTreeNode for Incoming Reference Category.
 * 
 * @author Alex_Smarandache
 */
public class ReferenceCategoryMutableTreeNode extends DefaultMutableTreeNode {

	/**
	 * Children of current category.
	 */
	private List<IncomingReference> children = new ArrayList<>();
	
	/**
	 * The reference category.
	 */
	private final ReferenceCategory referenceCategory;
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param referenceCategory The reference category.
	 */
	public ReferenceCategoryMutableTreeNode(ReferenceCategory referenceCategory) {
		super(referenceCategory);
		this.referenceCategory = referenceCategory;
	}

	
	/**
	 * @return Children of current category.
	 */
	public List<IncomingReference> getChildren() {
		return children;
	}

	
	/**
	 * @param children Children of current category.
	 */
	public void setChildren(List<IncomingReference> children) {
		this.children = children;
	}

	
	/**
	 * @return The reference category.
	 */
	public ReferenceCategory getReferenceCategory() {
		return referenceCategory;
	}


}
