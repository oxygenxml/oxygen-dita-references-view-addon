package com.oxygenxml.ditareferences.tree.references.outgoing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.oxygenxml.ditareferences.tree.references.RefUtilities;
import com.oxygenxml.ditareferences.workspace.DITAConstants;

import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;

/**
 * Abstract class of text / author reference nodes in ReferencesTree.
 * 
 * @author Alexandra_Dinisor
 *
 */
public abstract class NodeRange {
	
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(NodeRange.class);
	
	/**
	 * Get the attribute value, returns <code>null</code> if there is no such
	 * attribute.
	 * 
	 * @param attributeName The attributeName
	 * @return the attribute Value or <code>null</code>
	 */
	public abstract String getAttributeValue(String attributeName);

	/**
	 * Compute the offsets of the corresponding reference node in tree for the
	 * element in the textPage.
	 * 
	 * @param editorPage The EditorPage: Text/Author
	 * @return An array with start and end offsets of Element in Text/Author Page.
	 *         Can be <code>null</code>.
	 */
	public abstract int[] getNodeOffsets(WSEditorPage editorPage);

	/**
	 * Get the nodeName.
	 * 
	 * @return the nodeName. Never <code>null</code>.
	 */
	public abstract String getNodeName();

	/**
	 * Get the displayed text for the node.
	 * 
	 * @return the displayed text. Can be <code>null</code> if no attribute value
	 *         found.
	 */
	String getDisplayText() {
	  String[] ditaConstants = {DITAConstants.KEYREF, DITAConstants.DATAKEYREF,
	      DITAConstants.CONKEYREF, DITAConstants.HREF, DITAConstants.CONREF, DITAConstants.DATA};
	  for (int i = 0; i < ditaConstants.length; i++) {
	    String attributeValue = this.getAttributeValue(ditaConstants[i]);
	    if(attributeValue != null) {
	      return attributeValue;
	    }
	  }
	  return null;
	}

	/**
	 * Get the toolTip text for the node. On keyref values (keyref, datakeyref),
	 * toolTip is set on the key definition location.
	 * 
	 * @return the toolTip text. Can be <code>null</code> if no attribute value
	 *         found.
	 */
	String getTooltipText(LinkedHashMap<String, KeyInfo> referencesKeys) {
	  String formatAttr = this.getAttributeValue(DITAConstants.FORMAT);
	  String attribute = this.getAttributeValue(DITAConstants.KEYREF);
	  String toReturn = null;

	  if (attribute != null) {
	    KeyInfo value = RefUtilities.getKeyInfoFromReference(attribute, referencesKeys);
	    if (value != null) {
	      try {
	        toReturn = RefUtilities.getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation()).toString();
	      } catch (MalformedURLException e) {
	        LOGGER.debug(e, e);
	      }
	    }
	  } else if((attribute = this.getAttributeValue(DITAConstants.DATAKEYREF)) != null) {
	    KeyInfo value = RefUtilities.getKeyInfoFromReference(attribute, referencesKeys);
	    if (value != null) {
	      try {
	        toReturn =  RefUtilities.getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation()).toString();
	      } catch (MalformedURLException e) {
	        LOGGER.debug(e, e);
	      }
	    }
	  } else if((attribute = this.getAttributeValue(DITAConstants.CONKEYREF)) != null) {
	    KeyInfo value = RefUtilities.getKeyInfoFromReference(attribute, referencesKeys);
	    if (value != null) {
	      toReturn =  value.getHrefLocation().toString();
	    }
	  } else if((attribute = this.getAttributeValue(DITAConstants.HREF)) != null) {
	    toReturn =  attribute;
	  } else if((attribute = this.getAttributeValue(DITAConstants.CONREF)) != null) {
	    toReturn =  attribute;
	  } else if((attribute = this.getAttributeValue(DITAConstants.DATA)) != null){
	    toReturn =  attribute;
	  }

	  return toReturn;
	}

	/**
	 * Get the editor location.
	 * 
	 * @return the editor location.
	 */
	public abstract URL getEditorLocation();

}
