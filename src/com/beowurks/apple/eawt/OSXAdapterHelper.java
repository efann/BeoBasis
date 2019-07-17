/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java programs written by
 *           Beowurks.
 * =============================================================================
 * Copyright(c) 2001-2015, by Beowurks.
 *
 * This application is open-source software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License, Version 1.0 (http://opensource.org/licenses/EPL-1.0).
 *
 * Original Author:  Eddie Fann
 * Contributor(s):   -;
 */

package com.beowurks.apple.eawt;

import com.beowurks.BeoCommon.Util;

//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
public final class OSXAdapterHelper
{

  // ---------------------------------------------------------------------------
  private OSXAdapterHelper()
  {
  }

  // ---------------------------------------------------------------------------
  static public void setupOSXAdapter(final IOSXAdapter tiOSXAdapter)
  {

    if (Util.isMacintosh())
    {

      try
      {
        // Generate and register the OSXAdapter, passing it a hash of all the
        // methods we wish to
        // use as delegates for various com.apple.eawt.ApplicationListener
        // methods
        OSXAdapter.setQuitHandler(tiOSXAdapter, tiOSXAdapter.getClass()
                .getDeclaredMethod("QuitHandler", (Class[]) null));
        OSXAdapter.setAboutHandler(tiOSXAdapter,
                tiOSXAdapter.getClass().getDeclaredMethod("AboutHandler", (Class[]) null));
        OSXAdapter.setPreferencesHandler(tiOSXAdapter,
                tiOSXAdapter.getClass().getDeclaredMethod("PreferencesHandler", (Class[]) null));
        OSXAdapter.setFileHandler(tiOSXAdapter,
                tiOSXAdapter.getClass().getDeclaredMethod("FileHandler", new Class[]{String.class}));
      }
      catch (final Exception loErr)
      {
        Util.showStackTraceInMessage(null, loErr, "OSXAdapter: Error while loading");
      }

    }

  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
