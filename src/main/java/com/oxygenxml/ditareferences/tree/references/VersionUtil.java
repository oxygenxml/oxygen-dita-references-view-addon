/*
* Copyright (c) 2020 Syncro Soft SRL - All Rights Reserved.
*
* This file contains proprietary and confidential source code.
* Unauthorized copying of this file, via any medium, is strictly prohibited.
*/

package com.oxygenxml.ditareferences.tree.references;

import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

/**
 * Checks if the current version is greater than another
 * @author mircea_badoi
 *
 */
public final class  VersionUtil {
  /**
   * name of the Oxygen 23 API method that we access through reflection 
   */
  public static final String METHOD_NAME_SEARCH_REFERENCES = "searchReferences";
  
  /**
   * name of the Oxygen 23 API method that we access through reflection 
   */
  public static final String METHOD_NAME_CREATE_REFERENCE_GRAPH = "createReferencesGraph";
  
  /**
   * Constructor
   */
  private VersionUtil() {
    //private to avoid instantiation
  }

 /**
  * Compare versions of Oxygen
  * eg: 22.1
  * @param the major version to compare to (eg: 22)
  * @param the minor version to compare to (eg: 1)
  * @return true if the current version is greater than the one to compare to
  */
  public static boolean isOxygenVersionNewer(int majorVersiontoCompare, int minorVersionToCompare) {
    int majorVersion = getMajorVersion();
    return 
        (majorVersion >= majorVersiontoCompare && getMinorVersion() >= minorVersionToCompare);
  }

  /**
   * @return Current oxygen major version
   */
  private static int getMajorVersion() {
    int majorVersion = -1;
    int[] oxygenVersion = getOxygenVersion();
    if (oxygenVersion.length > 0) {
      majorVersion = oxygenVersion[0];
    }
    return majorVersion;
  }

  /**
   * @return Current oxygen minor version
   */
  private static int getMinorVersion() {
    int minorVersion = -1;
    int[] oxygenVersion = getOxygenVersion();
    if (oxygenVersion != null && oxygenVersion.length > 1) {
      minorVersion = oxygenVersion[1];
    }
    return minorVersion;
  }

  /**
   * @return Current oxygen version
   */
  private static int[] getOxygenVersion() {
    PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();
    if (pluginWorkspace != null) {
      String version = pluginWorkspace.getVersion();
      if (version != null) {
        String[] split = version.split("[.]");
        int [] versionArray = new int[split.length];
        for (int i = 0; i < split.length; i++) {
          try {
            versionArray[i] = Integer.parseInt(split[i]);
          } catch (NumberFormatException e) {
            // nothing
          }
        }

        return versionArray;
      }
    }
    return new int [0];
  }
}

