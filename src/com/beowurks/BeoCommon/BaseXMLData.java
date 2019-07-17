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

package com.beowurks.BeoCommon;

import java.io.File;
import java.io.IOException;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
abstract public class BaseXMLData
{
  protected XMLTextReader foXMLTextReader = new XMLTextReader();
  protected XMLTextWriter foXMLTextWriter = new XMLTextWriter();
  protected int fnIndent;

  private final String fcFileName;
  private final String fcDirectory;
  private final String fcFullName;
  private final String fcLockFullName;

  private boolean flLockActive = false;

  private File foLockFile = null;

  // ---------------------------------------------------------------------------
  abstract public boolean parseXMLData();

  // ---------------------------------------------------------------------------
  abstract public boolean saveXMLData();

  // ---------------------------------------------------------------------------
  public BaseXMLData(final String tcDirectory, final String tcFileName, final int tnIndent)
  {
    if (!Util.makeDirectory(tcDirectory))
    {
      Util.errorMessage(null, "Unable to create the directory of " + tcDirectory + ".");
    }

    this.fcDirectory = tcDirectory;
    this.fcFileName = tcFileName;

    this.fcFullName = Util.includeTrailingBackslash(tcDirectory) + this.fcFileName;
    this.fcLockFullName = this.fcFullName + ".lck";
    this.fnIndent = tnIndent;
  }

  // ---------------------------------------------------------------------------
  public boolean copyToFile(final String tcDirectory, final String tcFileName)
  {
    final String lcCopyFullName = Util.includeTrailingBackslash(tcDirectory) + tcFileName;

    return (Util.fileCopy(this.getFullName(), lcCopyFullName));
  }

  // ---------------------------------------------------------------------------
  public boolean createLock()
  {
    boolean llOkay = true;

    if (!this.flLockActive)
    {
      this.foLockFile = new File(this.fcLockFullName);
      this.foLockFile.deleteOnExit();

      try
      {
        llOkay = this.foLockFile.createNewFile();
      }
      catch (final IOException loErr)
      {
        llOkay = false;
      }

      this.flLockActive = llOkay;
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  public boolean deleteFile()
  {
    final File loFile = new File(this.fcFullName);

    return (loFile.delete());
  }

  // ---------------------------------------------------------------------------
  public String getDirectory()
  {
    return (this.fcDirectory);
  }

  // ---------------------------------------------------------------------------
  public String getFileName()
  {
    return (this.fcFileName);
  }

  // ---------------------------------------------------------------------------
  public String getFullName()
  {
    return (this.fcFullName);
  }

  // ---------------------------------------------------------------------------
  public boolean exists()
  {
    return ((new File(this.fcFullName)).exists());
  }

  // ---------------------------------------------------------------------------
  public boolean releaseLock()
  {
    boolean llOkay = true;

    if (this.flLockActive)
    {
      if (this.foLockFile != null)
      {
        if (this.foLockFile.exists())
        {
          try
          {
            this.foLockFile.delete();
          }
          catch (final SecurityException ignored)
          {
          }
        }

        llOkay = !this.foLockFile.exists();
        if (llOkay)
        {
          this.foLockFile = null;
        }
      }

      this.flLockActive = !llOkay;
    }

    return (llOkay);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
