/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.util.URLUtil;

/**
 * Holds the data for an incoming reference
 * @author mircea_badoi
 *
 */
public class IncomingReference implements Comparable<IncomingReference> {
  
  /**
   * The document position info
   */
  private DocumentPositionedInfo dpi;
  
  /**
   * The file name of the reference
   */
  private String fileName;
  
  /**
   * Additional line number information
   */
  private String additionalInformation;
  
  /**
   * Parametrized constructor
   * @param dpi The DocumentPositionedInfo
   */
  public IncomingReference(DocumentPositionedInfo dpi) {
    this.dpi = dpi;
    this.fileName = URLUtil.extractFileName(dpi.getSystemID());
    if(fileName == null) {
    	fileName = "";
    }
  }
  
  /**
   * Build label with column and line details
   * @param dpi The DocumentPositionedInfo
   */
  public void setShowExtraLineNumberInformation() {
    StringBuilder build = new StringBuilder();
    build.append("[");
    build.append(dpi.getLine());
    build.append(":");
    build.append(dpi.getColumn());
    build.append("]");
    this.additionalInformation = build.toString();
  }
  
  /**
   * @return The DocumentPositionedInfo
   */
  public DocumentPositionedInfo getDPI() {
    return dpi;
  }
  
  /**
   * 
   * @return The formatted text for the label 
   */
  public String getRenderText() {
	  if(additionalInformation == null) {
		  return fileName;
	  } else {
		  return fileName + " " + additionalInformation;
	  }
  }
  
  /**
   * 
   * @return The text for the tooltip
   */
  public String getTooltipText() {
    StringBuilder tooltip = new StringBuilder();
    tooltip.append("<html><p>");
    tooltip.append(URLUtil.getDescription(dpi.getSystemID()));
    tooltip.append("</p>");
    tooltip.append(dpi.getMessage());
    tooltip.append("[");
    tooltip.append("line: ");
    tooltip.append(dpi.getLine());
    tooltip.append(", column: ");
    tooltip.append(dpi.getColumn());
    tooltip.append("]");
    tooltip.append("</p></html>");
    
    return tooltip.toString();
  }
  
  /**
   * 
   * @return The system id of the DocumentPositionInfo
   */
  public String getSystemId() {
    return dpi.getSystemID();
  }

  @Override
  public int compareTo(IncomingReference o) {
    return this.fileName.compareTo(o.fileName);
  }
}
