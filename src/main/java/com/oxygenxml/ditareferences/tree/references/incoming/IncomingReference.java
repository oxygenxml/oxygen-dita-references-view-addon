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
   * The label text for the refrence
   */
  private String labelText;
  
  /**
   * Parametrized constructor
   * @param dpi The DocumentPositionedInfo
   */
  public IncomingReference(DocumentPositionedInfo dpi) {
    this.dpi = dpi;
    this.labelText = dpi.getSystemID();
  }
  
  /**
   * Build label with colomn and line details
   * @param dpi The DocumentPositionedInfo
   */
  public void buildLabel() {
    StringBuilder build = new StringBuilder();
    build.append(dpi.getSystemID());
    build.append("[");
    build.append(dpi.getLine());
    build.append(":");
    build.append(dpi.getColumn());
    build.append("]");
    this.labelText = build.toString();
  }
  
  /**
   * 
   * @return The DocumentPositionedInfo
   */
  public DocumentPositionedInfo getDPI() {
    return dpi;
  }
  
  /**
   * Sets the DocumentPositionedInfo
   * @param info DocumentPositionedInfo
   */
  public void setDPI(DocumentPositionedInfo info) {
    this.dpi = info;
  }
  
  /**
   * @return The text used for the label not formated
   */
  public String getLabelText() {
    return labelText;
  }
  
  /**
   * Sets the text used for the label of the reference
   * @param labelText
   */
  public void setLabelText(String labelText) {
    this.labelText = labelText;
  }
  
  /**
   * 
   * @return The formatted text for the label 
   */
  public String getText() {
    if(labelText != null) {
      String[] split = labelText.split("/");
      return split[split.length - 1];
    }
    return null;
  }
  
  /**
   * 
   * @return The text for the tooltip
   */
  public String getTooltipText() {
    return URLUtil.getDescription(dpi.getSystemID());
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
    return this.getText().compareTo(o.getText());
  }
}
