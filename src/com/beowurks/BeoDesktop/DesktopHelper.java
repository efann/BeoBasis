/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java Swing programs.
 * =============================================================================
 * Copyright(c) 2001-2019, by Beowurks.
 *
 * This application is open-source software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License, Version 2.0 (https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html).
 *
 * Original Author:  Eddie Fann
 * Contributor(s):   -;
 */

package com.beowurks.BeoDesktop;

import java.awt.Desktop;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class DesktopHelper
{
  // ---------------------------------------------------------------------------------------------------------------------
  private DesktopHelper()
  {

  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Apparently, running on Windows throws errors for these routines as they are not supported.
  // But, that's okay, as I just need them to function on Apple or OS X. By the way, I tested these routines,
  // and they work.
  static public void setupDesktop(final IDesktopAdapter tiDesktopAdapter)
  {
    final Desktop loDesktop = Desktop.getDesktop();

    try
    {
      loDesktop.setAboutHandler(teEvent ->
          tiDesktopAdapter.AboutHandler()
      );
    }
    catch (Exception loError)
    {
    }

    try
    {
      loDesktop.setPreferencesHandler(teEvent ->
          tiDesktopAdapter.PreferencesHandler()
      );
    }
    catch (Exception loError)
    {
    }

    try
    {
      loDesktop.setQuitHandler((teEvent, teResponse) ->
          tiDesktopAdapter.QuitHandler()
      );
    }
    catch (Exception loError)
    {
    }

  }
  // ---------------------------------------------------------------------------------------------------------------------

}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
