/**
 * 
 */
package com.oxygenxml.ditareferences.workspace;

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
		
		return null;
	}

	public String correctURL(String url) {
		
		return null;
	}

	public URL convertFileToURL(File file) throws MalformedURLException {
		
		return null;
	}

	public URL removeUserCredentials(URL url) {
		
		return null;
	}

	public File locateFile(URL url) {
		
		return null;
	}

	public String getExtension(URL url) {
		
		return null;
	}

	public String getFileName(String urlPath) {
		
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
		
		return false;
	}

	public String expandEditorVariables(String pathWithEditorVariables, URL currentEditedURL) {
		
		return null;
	}

	public String encrypt(String toEncrypt) {
		
		return null;
	}

	public String decrypt(String toDecrypt) {
		
		return null;
	}

	public void addCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		

	}

	public void removeCustomEditorVariablesResolver(EditorVariablesResolver resolver) {
		

	}

	public Reader createReader(URL url, String defaultEncoding) throws IOException {
		
		return null;
	}
	
	public BufferedImage createImage(String imageUrl) throws IOException {
		
		return null;
	}

	public ImageHolder optimizeImage(URL imageUrl) throws IOException {
		
		return null;
	}

  public String uncorrectURL(String url) {
    return null;
  }

  public String expandEditorVariables(String pathWithEditorVariables, URL currentEditedURL,
      boolean expandAskEditorVariables) {
    return null;
  }

  @Override
  public String getContentType(String systemID) {
    return null;
  }

}
