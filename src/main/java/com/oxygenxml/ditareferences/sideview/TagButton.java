/*
 * Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */

package com.oxygenxml.ditareferences.sideview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import com.jidesoft.swing.JideToggleButton;

/**
 * Buttons used for tag filters
 *   
 */
public class TagButton extends JideToggleButton {
  
  /**
   * The serial UID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * The default inset.
   */
  private static final int DEFAULT_INSET = 4;
  
  /**
   * Create the tag button with action and shows the text.
   * 
   * @param action The action.
   */
  public TagButton(Action action) {
    super(action);
    
    // customize the button.
    setBorderPainted(true);
    setOpaque(true);
    setFont(getFont().deriveFont(Font.BOLD));
    setButtonStyle(0);
    setFocusPainted(false);
    
    Border outsideBorder = BorderFactory.createLineBorder(new Color(237, 237, 237));
    // Use the default buttons insets
    Insets insets = getInsets();
    Border innerBorder = null;
    if (insets != null) {
      innerBorder = BorderFactory.createEmptyBorder(
          insets.top + 1,
          insets.left + 1,
          insets.bottom + 1,
          insets.right + 1);
    } else {
     innerBorder = BorderFactory.createEmptyBorder(
          DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET, DEFAULT_INSET);
    }
    CompoundBorder border = new CompoundBorder(outsideBorder, innerBorder);
    setBorder(border);
  }
  
}
