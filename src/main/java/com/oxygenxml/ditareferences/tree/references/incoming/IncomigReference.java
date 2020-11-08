/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references.incoming;

import ro.sync.document.DocumentPositionedInfo;

/**
 * Holds the data for an incoming reference
 * @author mircea_badoi
 *
 */
public class IncomigReference {
  
  /**
   * The document position info
   */
  private DocumentPositionedInfo dpi;
  
  /**
   * The label text for the refrence
   */
  private String labelText = "";
  
  /**
   * Parametrized constructor
   * @param dpi The DocumentPositionedInfo
   * @param labelText The text used for the label
   */
  public IncomigReference(DocumentPositionedInfo dpi, String labelText) {
    this.dpi = dpi;
    this.labelText = labelText;
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
   * @return The text used for the label
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
}
