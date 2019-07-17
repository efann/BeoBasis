/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java programs written by
 *           Beowurks.
 * =============================================================================
 * Copyright(c) 2001-2019, by Beowurks.
 *
 * This application is open-source software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License, Version 1.0 (http://opensource.org/licenses/EPL-1.0).
 *
 * Original Author:  Eddie Fann
 * Contributor(s):   -;
 */

package com.beowurks.BeoZippin;

import com.beowurks.BeoTable.SortingTable;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ZipTable extends SortingTable
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public ZipTable()
  {
    super(new ZipTableModel());
  }

  // ---------------------------------------------------------------------------
  public String getFullPath(final int tnRow)
  {
    return (this.extractDirectory(tnRow) + this.extractFileName(tnRow));
  }

  // ---------------------------------------------------------------------------
  public String extractDirectory(final int tnRow)
  {
    final Object loValue = this.getModel().getValueAt(tnRow, this.getColumnCount() - 1);

    return ((loValue != null) ? loValue.toString() : "");
  }

  // ---------------------------------------------------------------------------
  public String extractFileName(final int tnRow)
  {
    final Object loValue = this.getModel().getValueAt(tnRow, 0);

    return ((loValue != null) ? loValue.toString() : "");
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
