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

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class BaseFileFilter extends javax.swing.filechooser.FileFilter
{
  private final String fcFilter;
  private final String fcDescription;

  // ---------------------------------------------------------------------------
  public BaseFileFilter(final String tcFilter, final String tcDescription)
  {
    this.fcFilter = tcFilter.toLowerCase();
    this.fcDescription = tcDescription;
  }

  // ---------------------------------------------------------------------------
  @Override
  public boolean accept(final File toFile)
  {
    if (toFile.isDirectory())
    {
      return true;
    }

    final String lcExt = Util.extractFileExtension(toFile);
    if (lcExt != null)
    {
      return (this.fcFilter.contains(lcExt.toLowerCase()));
    }

    return false;
  }

  // ---------------------------------------------------------------------------
  // The description of this filter
  @Override
  public String getDescription()
  {
    return (this.fcDescription);
  }

  // ---------------------------------------------------------------------------
  // The filter
  public String getFilter()
  {
    return (this.fcFilter);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
