/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;

import java.util.Objects;

import com.oxygenxml.ditareferences.i18n.DITAReferencesTranslator;
import com.oxygenxml.ditareferences.i18n.Tags;

import ro.sync.document.DocumentPositionedInfo;
import ro.sync.util.URLUtil;

/**
 * Holds the data for an incoming reference
 * @author mircea_badoi
 *
 */
public class IncomingReference implements Comparable<IncomingReference> {
  
  /**
   * For translation
   */
  private static final DITAReferencesTranslator translator = new DITAReferencesTranslator();
  
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
   * The reference category.
   */
  private ReferenceCategory referenceCategory = ReferenceCategory.CROSS;
  
  
  /**
   * Parametrized constructor.
   * 
   * @param dpi The DocumentPositionedInfo
   */
  public IncomingReference(DocumentPositionedInfo dpi) {
    this.dpi = dpi;
    this.fileName = URLUtil.extractFileName(dpi.getSystemID());
    this.fileName = fileName != null ? fileName : "";
    if(dpi.getMessage().endsWith("[CONREF]") || dpi.getMessage().endsWith("[CONKEYREF]")) {
      referenceCategory = ReferenceCategory.CONTENT;
    } else if (dpi.getSystemID().endsWith(".ditamap")) {
      referenceCategory = ReferenceCategory.MAP;
    }
  }
  
  /**
   * Build label with column and line details
   * 
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
   * @return The text for the tooltip
   */
  public String getTooltipText() {
    StringBuilder tooltip = new StringBuilder();
    tooltip.append("<html><p>");
    tooltip.append(URLUtil.getDescription(dpi.getSystemID()));
    tooltip.append("</p>");
    tooltip.append(dpi.getMessage());
    tooltip.append("[");
    tooltip.append(translator.getTranslation(Tags.LINE));
    tooltip.append(": ");
    tooltip.append(dpi.getLine());
    tooltip.append(", ");
    tooltip.append(translator.getTranslation(Tags.COLUMN));
    tooltip.append(": ");
    tooltip.append(dpi.getColumn());
    tooltip.append("]");
    tooltip.append("</p></html>");
    
    return tooltip.toString();
  }
  
  /**
   * @return The system id of the DocumentPositionInfo
   */
  public String getSystemId() {
    return dpi.getSystemID();
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(additionalInformation, dpi, fileName);
  }

  @Override
  public boolean equals(Object obj) {
    boolean toReturn;
    if (this == obj) {
      toReturn = true;
    } else if (obj == null || getClass() != obj.getClass()) {
      toReturn = false;
    } else {
      IncomingReference other = (IncomingReference) obj;
      toReturn = Objects.equals(additionalInformation, other.additionalInformation) && Objects.equals(dpi, other.dpi)
          && Objects.equals(fileName, other.fileName);
    }
    return toReturn;
  }
  
  @Override
  public int compareTo(IncomingReference o) {
    return this.fileName.compareTo(o.fileName);
  }

  @Override
  public String toString() {
    return URLUtil.getDescription(dpi.getSystemID());
  }
  
  
}
