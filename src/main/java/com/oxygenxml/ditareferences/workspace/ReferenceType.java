/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.workspace;
/**
 * Type of the references
 * @author mircea_badoi
 */
public enum ReferenceType {
  /**
   * The outgoing references
   */
  OUTCOMING{
    @Override
    public String toString() {
      return "Outcoming";
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
