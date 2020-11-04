/*
 * Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */

package com.oxygenxml.ditareferences.sideview;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oxygenxml.ditareferences.tree.references.ReferenceType;

/**
 * Tags filter panel with exclusive actions. Every time a filter is pressed it is hidden and the others are unselected.
 *  @author mircea_badoi
 *  
 */
public abstract class TagFilterPanel extends JPanel {
  
  /**
   * Generated UID
   */
  private static final long serialVersionUID = -8395368853129892393L;

  /**
   * List of buttons associated to exclusive tags.
   */
  private final List<TagButton> buttons = new ArrayList<>(0);
  
  /**
   * The label before tags.
   */
  private JLabel label;
  
  /**
   * Constructor
   * @param textLabel The text that describes what buttons do
   */
  public TagFilterPanel(String textLabel) {
    super(new WrapLayout(WrapLayout.LEFT, 1, 7));
    if (textLabel != null) {
      label = new JLabel(textLabel + ": ");
    }
  }
  
  
  /**
   * Set the new tags
   * 
   * @param newTags the new tags
   */
  @SuppressWarnings("serial")
  public void setTags(ReferenceType[] values) {
    removeAll();
    invalidate();
    repaint();

    if (label != null) {
      add(label);
    }

    for (ReferenceType referenceType : values) {
      String reference = referenceType.toString();
      TagButton tagButton = new TagButton(new AbstractAction(reference) {
        @Override
        public void actionPerformed(ActionEvent e) {
          for(TagButton button : buttons) {
            String value = (String) button.getAction().getValue(Action.NAME);
            if(!value.contentEquals(reference) && button.isSelected()) {
              button.setSelected(false);
            } else if (value.contentEquals(reference) && !button.isSelected()){
              button.setSelected(true);
            } else if(button.isSelected()) {
              showPanel(value);
            }
          }
        }
      });
      if(reference.contentEquals(ReferenceType.OUTGOING.toString())) {
        tagButton.doClick();
      }
      tagButton.setOpaque(false);
      StringBuilder tooltip = new StringBuilder();
      tooltip.append("The");
      tooltip.append(" ");
      tooltip.append(reference);
      tooltip.append(" ");
      tooltip.append("references");
      tagButton.setToolTipText(tooltip.toString());
      buttons.add(tagButton);
      add(tagButton);
    }
    revalidate();
  }
  
  /**
   * Shows card layout panel based on the type of reference
   * @param type The type of reference
   */
  abstract void showPanel(String type);
  
}
