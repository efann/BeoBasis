/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java programs written by
 *           Beowurks.
 * =============================================================================
 * Copyright(c) 2001-2019, by Beowurks.
 *
 * This application is open-source software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License, Version 2.0 (https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.html).
 *
 * Original Author:  Eddie Fann
 * Contributor(s):   -;
 */

package com.beowurks.BeoZippin;

import com.beowurks.BeoTable.SortingTableModel;

import java.util.zip.ZipEntry;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class ZipTableModel extends SortingTableModel
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public ZipTableModel()
  {

    this.foRealFormat.setMaximumFractionDigits(1);
    this.foRealFormat.setMinimumFractionDigits(1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public Object formatObject(final Object toValue, final int tnCol) throws Exception
  {
    switch (tnCol)
    {
      // CRC-32
      case 5:
        if (toValue.getClass() != Long.class)
        {
          throw new Exception("The CRC-32 column is not a long!");
        }

        return (Long.toHexString(((Long) toValue).longValue()).toUpperCase());

      // Method
      case 6:
        if (toValue.getClass() != Integer.class)
        {
          throw new Exception("The Method column is not an integer!");
        }

        final int lnMethod = ((Integer) toValue).intValue();
        if (lnMethod == ZipEntry.DEFLATED)
        {
          return ("Deflated");
        }
        else if (lnMethod == ZipEntry.STORED)
        {
          return ("Stored");
        }
        break;
    }

    return (super.formatObject(toValue, tnCol));
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
