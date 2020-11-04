/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references;
/**
 * Type of the references
 * @author mircea_badoi
 */
public enum ReferenceType {
  /**
   * The outgoing references
   */
  OUTGOING{
    @Override
    public String toString() {
      return "Outgoing";
    }
  },
  /**
   * The ongoing references
   */
  INCOMING{
    @Override
    public String toString() {
      return "Incoming";
    }
  }

}
