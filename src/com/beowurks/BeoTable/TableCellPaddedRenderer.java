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

package com.beowurks.BeoTable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class TableCellPaddedRenderer extends DefaultTableCellRenderer
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public TableCellPaddedRenderer()
  {

    this.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public TableCellPaddedRenderer(final int tnHorizontalAlignment)
  {

    this.setHorizontalAlignment(tnHorizontalAlignment);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public Component getTableCellRendererComponent(final JTable toTable, final Object toColor,
                                                 final boolean tlIsSelected, final boolean tlHasFocus, final int tnRow, final int tnColumn)
  {
    super.getTableCellRendererComponent(toTable, toColor, tlIsSelected, tlHasFocus, tnRow, tnColumn);

    // By the way, this appears to have no effect on row height.
    // It appears you need to use JTable.setRowHeight.
    this.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

    return (this);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
