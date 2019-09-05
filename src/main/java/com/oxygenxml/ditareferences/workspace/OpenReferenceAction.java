package com.oxygenxml.ditareferences.workspace;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;

import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.editor.ContentTypes;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

@SuppressWarnings("serial")
public class OpenReferenceAction extends AbstractAction {

	/* The OpenReferenceAction Logger. */
	private static final Logger LOGGER = Logger.getLogger(OpenReferenceAction.class);

	private NodeRange nodeRange;
	private WSEditor editorAccess;
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private KeysProvider keysProvider;

	/**
	 * Constructor for popUp menu triggered by right click.
	 * 
	 * @param nodeRange             The nodeRange to be clicked
	 * @param editorAccess          The editor access
	 * @param pluginWorkspaceAccess The pluginWorkspace access
	 * @param keysProvider          The HashMap with keys
	 * @param actionName            The name of the action.
	 */
	public OpenReferenceAction(NodeRange nodeRange, WSEditor editorAccess,
			StandalonePluginWorkspace pluginWorkspaceAccess, KeysProvider keysProvider, String actionName) {
		super(actionName);

		this.nodeRange = nodeRange;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.editorAccess = editorAccess;
		this.keysProvider = keysProvider;
	}

	/**
	 * Constructor when opening the reference by double click.
	 * 
	 * @param nodeRange             The nodeRange to be clicked
	 * @param editorAccess          The editor access
	 * @param pluginWorkspaceAccess The pluginWorkspace access
	 * @param keyProvider           The HashMap with keys
	 */
	public OpenReferenceAction(NodeRange nodeRange, WSEditor editorAccess,
			StandalonePluginWorkspace pluginWorkspaceAccess, KeysProvider keysProvider) {
		this(nodeRange, editorAccess, pluginWorkspaceAccess, keysProvider, "");
	}

	/**
	 * Action for open reference either by double clicking or by right clicking with
	 * contextual menu. Case for every attribute value of the leaf node. In case of
	 * key references (keyref, conkeyref) the URL location comes from the
	 * LinkedHashMap with KeyInfo.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// attributes of the leaf nodes
		String hrefAttrValue = nodeRange.getAttributeValue(DITAConstants.HREF);
		String keyrefAttrValue = nodeRange.getAttributeValue(DITAConstants.KEYREF);
		String conrefAttrValue = nodeRange.getAttributeValue(DITAConstants.CONREF);
		String conkeyrefAttrValue = nodeRange.getAttributeValue(DITAConstants.CONKEYREF);
		String dataAttrValue = nodeRange.getAttributeValue(DITAConstants.DATA);
		String datakeyrefAttrValue = nodeRange.getAttributeValue(DITAConstants.DATAKEYREF);
		String formatAttrValue = nodeRange.getAttributeValue(DITAConstants.FORMAT);
		String classAttrValue = nodeRange.getAttributeValue(DITAConstants.CLASS);

		URL editorLocation = editorAccess.getEditorLocation();
		LinkedHashMap<String, KeyInfo> referencesKeys = keysProvider != null ? keysProvider.getKeys(editorLocation) : null;

		try {
			if (keyrefAttrValue != null) {
				if (referencesKeys != null) {
					KeyInfo value = RefUtilities.getKeyInfoFromReference(keyrefAttrValue, referencesKeys);
					if (value != null) {
						URL url = RefUtilities.getURLForHTTPHost(formatAttrValue, value.getHrefValue(), value.getHrefLocation());
						formatAttrValue = value.getAttributes().get(DITAConstants.FORMAT);
						openReferences(url, classAttrValue, formatAttrValue);
					}
				}
			} else if (conkeyrefAttrValue != null) {
				if (referencesKeys != null) {
					KeyInfo value = RefUtilities.getKeyInfoFromReference(conkeyrefAttrValue, referencesKeys);
					if (value != null) {
						URL url = value.getHrefLocation();
						formatAttrValue = value.getAttributes().get(DITAConstants.FORMAT);
						openReferences(url, classAttrValue, formatAttrValue);
					}
				}
			} else if (datakeyrefAttrValue != null) {
				if (referencesKeys != null) {
					KeyInfo value = RefUtilities.getKeyInfoFromReference(datakeyrefAttrValue, referencesKeys);
					if (value != null) {
						URL url = value.getHrefLocation();
						formatAttrValue = value.getAttributes().get(DITAConstants.FORMAT);
						openReferences(url, classAttrValue, formatAttrValue);
					}
				}
			} else if (hrefAttrValue != null) {
				// possible URL if the protocol name is already inserted in the href reference
				// by user, otherwise it needs to be added
				URL possibleURL = new URL(editorLocation, hrefAttrValue);
				URL url = RefUtilities.getURLForHTTPHost(formatAttrValue, hrefAttrValue, possibleURL);
				openReferences(url, classAttrValue, formatAttrValue);

			} else if (conrefAttrValue != null) {
				URL url = new URL(editorLocation, conrefAttrValue);
				openReferences(url, classAttrValue, formatAttrValue);

			} else if (dataAttrValue != null) {
				URL dataUrl = new URL(editorLocation, dataAttrValue);
				openReferences(dataUrl, classAttrValue, formatAttrValue);
			}

		} catch (MalformedURLException e1) {
			LOGGER.debug(e1, e1);
		}
	}




	/**
	 * Open references from attribute with either Oxygen or an associated
	 * application depending on its type: images, audio / video files, DITA topic,
	 * HTML, PDF etc.
	 * 
	 * @param url             Target URL, the URL to open
	 * @param classAttrValue  The class attribute value
	 * @param formatAttrValue The format attribute value
	 * @throws MalformedURLException
	 * @throws DOMException
	 */
	private void openReferences(URL url, String classAttrValue, String formatAttrValue) throws MalformedURLException {
		if (classAttrValue != null) {
			// it's image
			if (classAttrValue.contains(DITAConstants.IMAGE_CLASS)) {
				openImageReference(url, formatAttrValue);
			} else
			// it's object file: audio / video
			if (classAttrValue.contains(DITAConstants.OBJECT_CLASS)) {
				pluginWorkspaceAccess.openInExternalApplication(url, true);
			} else {
				if (formatAttrValue != null) {
					openReferenceWithFormatAttr(url, formatAttrValue);
				} else {
					openReferenceWithoutFormatAttr(url);
				}
			}
		}

	}

	/**
	 * Open image references from format attribute.
	 * 
	 * @param url             The target URL
	 * @param formatAttrValue The format attribute Value
	 * @throws MalformedURLException
	 */
	private void openImageReference(URL url, String formatAttrValue) throws MalformedURLException {
		if (pluginWorkspaceAccess.getUtilAccess().isSupportedImageURL(url)) {
			pluginWorkspaceAccess.open(url, null, ContentTypes.IMAGE_CONTENT_TYPE);
		} else {
			// image needs extension for URL if none in attributeValue
			if (formatAttrValue != null) {
				URL imageUrl = new URL(url.toString() + "." + formatAttrValue);
				if (pluginWorkspaceAccess.getUtilAccess().isSupportedImageURL(imageUrl)) {
					pluginWorkspaceAccess.open(imageUrl, null, ContentTypes.IMAGE_CONTENT_TYPE);
				}
			}
		}
	}

	/**
	 * Open reference with NO format attribute. For example, a resource opened in
	 * external application, a web link or DITA topic.
	 * 
	 * @param url        The target URL, the URL to open
	 * @param formatAttr The format Attribute Value
	 */
	private void openReferenceWithoutFormatAttr(URL url) {
		// binary resource or a HTML format to be opened in browser
		if (pluginWorkspaceAccess.getUtilAccess().isUnhandledBinaryResourceURL(url)) {
			pluginWorkspaceAccess.openInExternalApplication(url, true);
		} else {
			// it's DITA
			pluginWorkspaceAccess.open(url);
		}
	}

	/**
	 * Open references with format attribute. For example, DITA topic or resource
	 * opened in external application.
	 * 
	 * @param url             The target URL, the URL to open
	 * @param formatAttrValue The format Attribute Value
	 */
	private void openReferenceWithFormatAttr(URL url, String formatAttrValue) {
		// it's DITA
		if (formatAttrValue.equals(DITAConstants.FORMAT_DITA) || formatAttrValue.equals(DITAConstants.FORMAT_DITAMAP)) {
			pluginWorkspaceAccess.open(url);
		} else {
			// it's binary resource, not handled by Oxygen
			pluginWorkspaceAccess.openInExternalApplication(url, true);
		}
	}

}
