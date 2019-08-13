package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

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
	 * @param keysProvider          The name of the contextual menu item
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
		String hrefAttr = nodeRange.getAttributeValue("href");
		String keyrefAttr = nodeRange.getAttributeValue("keyref");
		String conrefAttr = nodeRange.getAttributeValue("conref");
		String conkeyrefAttr = nodeRange.getAttributeValue("conkeyref");
		String formatAttr = nodeRange.getAttributeValue("format");
		URL url = null;
		URL editorLocation = editorAccess.getEditorLocation();
		LinkedHashMap<String, KeyInfo> referencesKeys = keysProvider.getKeys(editorLocation);

		try {
			if (hrefAttr != null) {
				// possible URL if the protocol name is already inserted in the href reference
				// by user, otherwise it needs to be added
				URL possibleURL = new URL(editorLocation, hrefAttr);
				url = getURLForHTTPHost(formatAttr, hrefAttr, possibleURL);
				openReferences(url, nodeRange, formatAttr);

			} else if (conrefAttr != null) {
				url = new URL(editorLocation, conrefAttr);
				// TODO ce pasam ca 3 arg
				openReferences(url, nodeRange, formatAttr);

			} else if (keyrefAttr != null) {
				KeyInfo value = getKeyInfoFromReference(keyrefAttr, referencesKeys);
				if (value != null) {
					url = getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation());
					formatAttr = value.getAttributes().get("format");
					openReferences(url, nodeRange, formatAttr);
				}

			} else if (conkeyrefAttr != null) {
				KeyInfo value = getKeyInfoFromReference(conkeyrefAttr, referencesKeys);
				if (value != null) {
					url = value.getHrefLocation();
					formatAttr = value.getAttributes().get("format");
					openReferences(url, nodeRange, formatAttr);
				}
			}
		} catch (MalformedURLException e1) {
			LOGGER.error(e1, e1);
			e1.printStackTrace();
		}
	}

	/**
	 * Get URL in case of no protocol in the attribute value. The HTTP Host should
	 * have the protocol name when new URL created. For example:
	 * "http://www.google.com" if user types "www.google.com".
	 * 
	 * @param formatAttr  Format attribute to verify
	 * @param hrefValue   The HREF value of the attribute
	 * @param possibleURL The URL if no HTML format available
	 * @return The target URL
	 * @throws MalformedURLException
	 */
	private URL getURLForHTTPHost(String formatAttr, String hrefValue, URL possibleURL) throws MalformedURLException {
		URL url;
		if (formatAttr != null && formatAttr.equals("html") && !hrefValue.startsWith("http")) {
			url = new URL("http://" + hrefValue);
		} else {
			url = possibleURL;
		}
		return url;
	}

	/**
	 * Get the specific KeyInfo of the given key after removing the "/" if any. in
	 * order to get the filename.
	 * 
	 * @param keyAttrValue The key reference attribute value
	 * @param keys         The LinkedHashMap with all the keys
	 * @throws MalformedURLException
	 */
	private KeyInfo getKeyInfoFromReference(String keyAttrValue, LinkedHashMap<String, KeyInfo> keys)
			throws MalformedURLException {
		StringTokenizer st = new StringTokenizer(keyAttrValue, "/");
		String keyName = null;
		if (st.hasMoreTokens()) {
			keyName = st.nextToken();
		}
		return keys.get(keyName);
	}

	/**
	 * Open references from attribute with either Oxygen or an associated
	 * application depending on its type: images, DITA, HTML, PDF etc.
	 * 
	 * @param url                     Target URL, the URL to open
	 * @param nodeRange               The nodeRange
	 * @param referenceAttributeValue The attribute value
	 * @param formatAttr              The format attribute
	 * @throws MalformedURLException
	 * @throws DOMException
	 */
	void openReferences(URL url, NodeRange nodeRange, String formatAttr) throws MalformedURLException {
		String classAttr = nodeRange.getAttributeValue("class");

		// it's image
		if (classAttr != null && classAttr.contains(" topic/image ")) {
			if (pluginWorkspaceAccess.getUtilAccess().isSupportedImageURL(url)) {
				pluginWorkspaceAccess.open(url, null, ContentTypes.IMAGE_CONTENT_TYPE);
			} else {
				// image needs extension for URL if none in attributeValue
				if (formatAttr != null) {
					URL imageUrl = new URL(url.toString() + "." + formatAttr);
					if (pluginWorkspaceAccess.getUtilAccess().isSupportedImageURL(imageUrl)) {
						pluginWorkspaceAccess.open(imageUrl, null, ContentTypes.IMAGE_CONTENT_TYPE);
					}
				}
			}

		} else {
			if (formatAttr != null) {
				// it's DITA
				if (formatAttr.equals("dita") || formatAttr.equals("ditamap")) {
					pluginWorkspaceAccess.open(url);
				} else {
					// it's binary resource, not handled by Oxygen
					pluginWorkspaceAccess.openInExternalApplication(url, true);
				}
			} else {
				// binary resource or a HTML format to be opened in browser
				if (pluginWorkspaceAccess.getUtilAccess().isUnhandledBinaryResourceURL(url)
						|| (formatAttr != null && formatAttr.equals("html"))) {
					pluginWorkspaceAccess.openInExternalApplication(url, true);
				} else {
					// it's DITA
					pluginWorkspaceAccess.open(url);
				}
			}
		}
	}
}
