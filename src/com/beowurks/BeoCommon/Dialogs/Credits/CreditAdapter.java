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

package com.beowurks.BeoCommon.Dialogs.Credits;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class CreditAdapter implements ICredit
{
  private final String fcDescription;
  private final String fcURL;

  // ---------------------------------------------------------------------------
  public CreditAdapter(final String tcDescription, final String tcURL)
  {
    this.fcDescription = tcDescription;
    this.fcURL = tcURL;
  }

  // ---------------------------------------------------------------------------
  @Override
  public String getDescription()
  {
    return (this.fcDescription);
  }

  // ---------------------------------------------------------------------------
  @Override
  public String getURL()
  {
    return (this.fcURL);
  }

// ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
