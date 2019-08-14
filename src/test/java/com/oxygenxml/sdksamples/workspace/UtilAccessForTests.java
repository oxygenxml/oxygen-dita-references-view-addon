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

	@Override
	public String makeRelative(URL baseURL, URL childURL) {
		
		return null;
	}

	@Override
	public String correctURL(String url) {
		
		return null;
	}

	@Override
	public URL convertFileToURL(File file) throws MalformedURLException {
		
		return null;
	}

	@Override
	public URL removeUserCredentials(URL url) {
		
		return null;
	}

	@Override
	public File locateFile(URL url) {
		
		return null;
	}

	@Override
	public String getExtension(URL url) {
		
		return null;
	}

	@Override
	public String getFileName(String urlPath) {
		
		return null;
	}

	@Override
	public boolean isSupportedImageURL(URL url) {
		if (url.getFile() != null) {
			if (url.getFile().endsWith(".png") || url.getFile().endsWith(".jpg") || url.getFile().endsWith(".jpeg")
					|| url.getFile().endsWith(".gif") || url.getFile().endsWith(".tiff")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUnhandledBinaryResourceURL(URL url) {
		
		return false;
	}

	@Override
	public String expandEditorVariables(String pathWithEditorVariables, URL currentEditedURL) {
		
		return null;
	}

	@Override
	public String encrypt(String toEncrypt) {
		
		return null;
	}

	@Override
	public String decrypt(String toDecrypt) {
		
		return null;
	}

	@Override
	public void addCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		

	}

	@Override
	public void removeCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		

	}

	@Override
	public Reader createReader(URL url, String defaultEncoding) throws IOException {
		
		return null;
	}

	@Override
	public BufferedImage createImage(String imageUrl) throws IOException {
		
		return null;
	}

	@Override
	public ImageHolder optimizeImage(URL imageUrl) throws IOException {
		
		return null;
	}

}
