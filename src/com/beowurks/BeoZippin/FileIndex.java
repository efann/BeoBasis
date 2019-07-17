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

package com.beowurks.BeoZippin;

//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
class FileIndex
{
  protected String fcFileName = null;
  protected boolean flDirectory = false;
  protected boolean flHidden = false;

  // ---------------------------------------------------------
  public FileIndex(final String tcFileName)
  {
    this.fcFileName = tcFileName;
  }

  // ---------------------------------------------------------
  public FileIndex(final String tcFileName, final boolean tlDirectory, final boolean tlHidden)
  {
    this.fcFileName = tcFileName;
    this.flDirectory = tlDirectory;
    this.flHidden = tlHidden;
  }

  // ---------------------------------------------------------
  @Override
  /** Since FileIndex is based on a FileName, make toString() return the string value,
   * which mimics the String class, rather than returning the Object address. */
  public String toString()
  {
    return (this.fcFileName);
  }

  // ---------------------------------------------------------
  @Override
  /**  Now should work with classes that use Collection. */
  public boolean equals(final Object toObject)
  {
    return (toObject != null ? (this.fcFileName.compareTo(toObject.toString()) == 0) : false);
  }

  // ---------------------------------------------------------
  @Override
  /**  Now should work with classes that use Map. */
  public int hashCode()
  {
    return (this.fcFileName.hashCode());
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
