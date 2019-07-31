/**
 * 
 */
package com.oxygenxml.sdksamples.workspace;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import ro.sync.exml.workspace.api.util.EditorVariablesResolver;
import ro.sync.exml.workspace.api.util.ImageHolder;
import ro.sync.exml.workspace.api.util.UtilAccess;

/**
 * @author Alexandra_Dinisor
 *
 */
public class UtilAccessForTests implements UtilAccess {

	public String makeRelative(URL baseURL, URL childURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public String correctURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	public URL convertFileToURL(File file) throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL removeUserCredentials(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public File locateFile(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExtension(URL url) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFileName(String urlPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSupportedImageURL(URL url) {
		if (url.getFile() != null) {
			if (url.getFile().endsWith(".png") || url.getFile().endsWith(".jpg") || url.getFile().endsWith(".jpeg")
					|| url.getFile().endsWith(".gif") || url.getFile().endsWith(".tiff")) {
				return true;
			}
		}
		return false;
	}

	public boolean isUnhandledBinaryResourceURL(URL url) {
		// TODO Auto-generated method stub
		return false;
	}

	public String expandEditorVariables(String pathWithEditorVariables, URL currentEditedURL) {
		// TODO Auto-generated method stub
		return null;
	}

	public String encrypt(String toEncrypt) {
		// TODO Auto-generated method stub
		return null;
	}

	public String decrypt(String toDecrypt) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		// TODO Auto-generated method stub

	}

	public void removeCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		// TODO Auto-generated method stub

	}

	public Reader createReader(URL url, String defaultEncoding) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferedImage createImage(String imageUrl) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageHolder optimizeImage(URL imageUrl) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
