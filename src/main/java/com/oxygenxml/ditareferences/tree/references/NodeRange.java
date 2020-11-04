package com.oxygenxml.ditareferences.tree.references;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

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
		String keyrefAttr = this.getAttributeValue(DITAConstants.KEYREF);
		if (keyrefAttr != null) {
			return keyrefAttr;
		} else {
			String datakeyrefAttr = this.getAttributeValue(DITAConstants.DATAKEYREF);
			if (datakeyrefAttr != null) {
				return datakeyrefAttr;
			} else {
				String conkeyrefAttr = this.getAttributeValue(DITAConstants.CONKEYREF);
				if (conkeyrefAttr != null) {
					return conkeyrefAttr;
				} else {
					String hrefAttr = this.getAttributeValue(DITAConstants.HREF);
					if (hrefAttr != null) {
						return hrefAttr;
					} else {
						String conrefAttr = this.getAttributeValue(DITAConstants.CONREF);
						if (conrefAttr != null) {
							return conrefAttr;
						} else {
							String dataAttr = this.getAttributeValue(DITAConstants.DATA);
							if (dataAttr != null) {
								return dataAttr;
							}
						}
					}
				}
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
		String keyrefAttr = this.getAttributeValue(DITAConstants.KEYREF);
		String formatAttr = this.getAttributeValue(DITAConstants.FORMAT);

		if (keyrefAttr != null) {
			KeyInfo value = RefUtilities.getKeyInfoFromReference(keyrefAttr, referencesKeys);
			if (value != null) {
				try {
					return RefUtilities.getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation()).toString();
				} catch (MalformedURLException e) {
					LOGGER.debug(e, e);
				}
			}
		} else {
			String datakeyrefAttr = this.getAttributeValue(DITAConstants.DATAKEYREF);
			if (datakeyrefAttr != null) {
				KeyInfo value = RefUtilities.getKeyInfoFromReference(datakeyrefAttr, referencesKeys);
				if (value != null) {
					try {
						return RefUtilities.getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation()).toString();
					} catch (MalformedURLException e) {
						LOGGER.debug(e, e);
					}
				}
			} else {
				String conkeyrefAttr = this.getAttributeValue(DITAConstants.CONKEYREF);
				if (conkeyrefAttr != null) {
					KeyInfo value = RefUtilities.getKeyInfoFromReference(conkeyrefAttr, referencesKeys);
					if (value != null) {
						return value.getHrefLocation().toString();
					}
				} else {
					String hrefAttr = this.getAttributeValue(DITAConstants.HREF);
					if (hrefAttr != null) {
						return hrefAttr;
					} else {
						String conrefAttr = this.getAttributeValue(DITAConstants.CONREF);
						if (conrefAttr != null) {
							return conrefAttr;
						} else {
							String dataAttr = this.getAttributeValue(DITAConstants.DATA);
							if (dataAttr != null) {
								return dataAttr;
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the editor location.
	 * 
	 * @return the editor location.
	 */
	public abstract URL getEditorLocation();

}
