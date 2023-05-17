package com.oxygenxml.ditareferences.workspace.author;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorElementBaseInterface;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.ContentIterator;
import ro.sync.ecss.extensions.api.node.NamespaceContext;

public class AuthorElementAdapter implements AuthorElement {

	private int startOffset;
	private int endOffset;
	private String elementName;
	private Map<String, String> attributes;
	
	public AuthorElementAdapter(String elementName, int startOffset, int endOffset, Map<String, String> attributes) {
		this.elementName = elementName;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.attributes = attributes;
	}

	@Override
	public AuthorElementBaseInterface getBeforeElement() {
		 
		return null;
	}

	@Override
	public boolean isFirstChildElement() {
		 
		return false;
	}

	@Override
	public boolean hasPseudoClass(String name) {
		 
		return false;
	}

	@Override
	public void setPseudoClass(String name) {		 

	}

	@Override
	public void removePseudoClass(String name) {		 

	}

	@Override
	public boolean isEmptyCSS3() {
		 
		return false;
	}

	@Override
	public List<AuthorNode> getContentNodes() {
		 
		return null;
	}

	@Override
	public AuthorElementBaseInterface getParentElement() {
		 
		return null;
	}

	@Override
	public AuthorDocument getOwnerDocument() {
		 
		return null;
	}

	@Override
	public boolean isDescendentOf(AuthorNode ancestor) {
		 
		return false;
	}

	@Override
	public int getType() {
		 
		return 0;
	}

	@Override
	public int getStartOffset() {
		
		return startOffset;
	}

	@Override
	public int getEndOffset() {
		
		return endOffset;
	}

	@Override
	public String getName() {
		
		return elementName;
	}

	@Override
	public AuthorNode getParent() {
		 
		return null;
	}

	@Override
	public String getDisplayName() {
		 
		return elementName;
	}

	@Override
	public URL getXMLBaseURL() {
		 
		return null;
	}

	@Override
	public String getTextContent() throws BadLocationException {
		 
		return null;
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		 
		return null;
	}

	@Override
	public AttrValue getAttribute(String name) {
		
		if(attributes.containsKey(name)) {
			return new AttrValue(attributes.get(name));
		}
		return null;
	}

	@Override
	public String getNamespace() {
		 
		return null;
	}

	@Override
	public String getLocalName() {
		 
		return null;
	}

	@Override
	public AuthorNode getChild(String childLocalName) {
		 
		return null;
	}

	@Override
	public AuthorElement[] getElementsByLocalName(String localName) {
		 
		return null;
	}

	@Override
	public int getAttributesCount() {
		 
		return 0;
	}

	@Override
	public String getAttributeAtIndex(int index) throws ArrayIndexOutOfBoundsException {
		 
		return null;
	}

	@Override
	public void setAttribute(String qName, AttrValue attributeValue) {
		 

	}

	@Override
	public void setName(String newName) throws IllegalArgumentException {
		 

	}

	@Override
	public void removeAttribute(String qName) {
		 

	}

	@Override
	public String getAttributeNamespace(String attributePrefix) {
		 
		return null;
	}

  @Override
  public ContentIterator getContentIterator() {
    return null;
  }

  @Override
  public void setAttributesNoNSUpdate(Map<String, AttrValue> attrs) {
  }

  @Override
  public Set<String> getPseudoClassNames() {
    return null;
  }

}
