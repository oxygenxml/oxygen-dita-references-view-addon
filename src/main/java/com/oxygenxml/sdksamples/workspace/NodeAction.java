package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;

import org.apache.batik.svggen.ImageCacher.External;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import ro.sync.ecss.dita.DITAAccess;
import ro.sync.ecss.dita.reference.keyref.KeyInfo;
import ro.sync.exml.editor.ContentTypes;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.util.UtilAccess;

public class NodeAction extends AbstractAction {
	/**
	 * The OpenNodeAction Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(NodeAction.class);

	private NodeRange nodeRange;
	private WSEditor editorAccess;

	private StandalonePluginWorkspace pluginWorkspaceAccess;

	public NodeAction(NodeRange nodeRange, WSEditor editorAccess, StandalonePluginWorkspace pluginWorkspaceAccess,
			String itemName) {
		super(itemName);

		this.nodeRange = nodeRange;
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
		this.editorAccess = editorAccess;
	}

	public NodeAction(NodeRange nodeRange, WSEditor editorAccess, StandalonePluginWorkspace pluginWorkspaceAccess) {
		this(nodeRange, editorAccess, pluginWorkspaceAccess, "");
	}

	/**
	 * Action for open reference. Case for every attribute value of the leaf node.
	 */
	public void actionPerformed(ActionEvent e) {

		String hrefAttr = nodeRange.getAttributeValue("href");
		String keyrefAttr = nodeRange.getAttributeValue("keyref");
		String conrefAttr = nodeRange.getAttributeValue("conref");
		String conkeyrefAttr = nodeRange.getAttributeValue("conkeyref");
		String formatAttr = nodeRange.getAttributeValue("format");
		URL url = null;
		LinkedHashMap<String, KeyInfo> keys = DITAAccess.getKeys(editorAccess.getEditorLocation());

		try {
			if (hrefAttr != null) {
				URL possibleURL = new URL(editorAccess.getEditorLocation(), hrefAttr);
				url = getURLForHTTPHost(formatAttr, hrefAttr, possibleURL);
				openReferences(url, nodeRange, hrefAttr);
			} else if (conrefAttr != null) {
				url = new URL(editorAccess.getEditorLocation(), nodeRange.getAttributeValue("conref"));
				openReferences(url, nodeRange, "conref");
			} else if (keyrefAttr != null) {
				KeyInfo value = getKeyInfoFromReference(keyrefAttr, keys);
				url = getURLForHTTPHost(formatAttr, value.getHrefValue(), value.getHrefLocation());

				openReferences(url, nodeRange, value.getAttributes().get("format"));

			} else if (conkeyrefAttr != null) {
				KeyInfo value = getKeyInfoFromReference(conkeyrefAttr, keys);
				url = value.getHrefLocation();
				openReferences(url, nodeRange, value.getAttributes().get("format"));

			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			LOGGER.debug(e1, e1);
		}

	}

	/**
	 * Case for no protocol in the attribute value. The HTTP Host should have the
	 * protocol name when new URL created. For example: "https://www.google.com" if
	 * user types "www.google.com".
	 * 
	 * @param formatAttr  Format attribute to verify
	 * @param hrefValue   The href value of the attribute
	 * @param possibleURL The URL if no HTML format available
	 * @return The target URL
	 * @throws MalformedURLException
	 */
	private URL getURLForHTTPHost(String formatAttr, String hrefValue, URL possibleURL) throws MalformedURLException {
		URL url;
		if (formatAttr != null && formatAttr.equals("html") && !hrefValue.startsWith("http")) {
			url = new URL(hrefValue.replace(hrefValue, "http://" + hrefValue));
		} else {
			url = possibleURL;
		}
		return url;
	}

	/**
	 * Get the specific KeyInfo of the given key after removing the "/" if any. in
	 * order to get the filename.
	 * 
	 * @param keyAttr The key reference attribute value
	 * @param keys    The LinkedHashMap with all the keys
	 * @throws MalformedURLException
	 */
	private KeyInfo getKeyInfoFromReference(String keyAttr, LinkedHashMap<String, KeyInfo> keys)
			throws MalformedURLException {
		StringTokenizer st = new StringTokenizer(keyAttr, "/");
		String keyName = null;
		if (st.hasMoreTokens()) {
			keyName = st.nextToken();
		}

		KeyInfo value = keys.get(keyName);
		return value;
	}

	/**
	 * Open references from attribute with either Oxygen or an associated
	 * application depending on its type: images, DITA, HTML, PDF etc.
	 * 
	 * @param url                     Target URL, the URL to open
	 * @param nodeRange               The nodeRange
	 * @param referenceAttributeValue The attribute value
	 * @throws MalformedURLException
	 * @throws DOMException
	 */
	private void openReferences(URL url, NodeRange nodeRange, String referenceAttributeValue)
			throws MalformedURLException {

		String classAttr = nodeRange.getAttributeValue("class");
		String formatAttr = nodeRange.getAttributeValue("format");

		// it's image
		if (classAttr != null && classAttr.contains(" topic/image ")) {
			if (pluginWorkspaceAccess.getUtilAccess().isSupportedImageURL(url)) {
				pluginWorkspaceAccess.open(url, null, ContentTypes.IMAGE_CONTENT_TYPE);
			} else {
				// image extension in @format attribute
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
				//it's binary resource or a HTML format
				if (pluginWorkspaceAccess.getUtilAccess().isUnhandledBinaryResourceURL(url)
						|| referenceAttributeValue.equals("html")) {
					pluginWorkspaceAccess.openInExternalApplication(url, true);
				} else {
					// it's DITA
					pluginWorkspaceAccess.open(url);
				}
			}
		}
	}
}
