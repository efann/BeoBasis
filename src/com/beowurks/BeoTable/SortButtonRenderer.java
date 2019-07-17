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

package com.beowurks.BeoTable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class SortButtonRenderer extends JButton implements TableCellRenderer
{
  public static final int STATE_NONE = 0;
  public static final int STATE_UP = 1;
  public static final int STATE_DOWN = 2;

  private int fnSortColumn = -1;
  private int fnSortState = SortButtonRenderer.STATE_NONE;

  private int fnCurrentPressedButton = -1;

  private final JButton btnDown = new JButton();
  private final JButton btnUp = new JButton();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public SortButtonRenderer()
  {
    final Dimension ldButtonSize = new Dimension((int) this.getPreferredSize().getWidth(), 20);
    final Insets loInset = new Insets(4, 4, 4, 4);

    this.setMinimumSize(ldButtonSize);
    this.setPreferredSize(ldButtonSize);
    this.setMargin((Insets) loInset.clone());

    Icon loImage;

    loImage = UIManager.getIcon("Table.ascendingSortIcon");
    this.btnUp.setIcon(loImage);
    this.btnUp.setPressedIcon(loImage);
    this.btnUp.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    this.btnUp.setMinimumSize(ldButtonSize);
    this.btnUp.setPreferredSize(ldButtonSize);
    this.btnUp.setMargin((Insets) loInset.clone());

    loImage = UIManager.getIcon("Table.descendingSortIcon");
    this.btnDown.setIcon(loImage);
    this.btnDown.setPressedIcon(loImage);
    this.btnDown.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    this.btnDown.setMinimumSize(ldButtonSize);
    this.btnDown.setPreferredSize(ldButtonSize);
    this.btnDown.setMargin((Insets) loInset.clone());

    // If you want the buttons to work in Motif you have to do the following:
    // However, there's some other quirky behaviour with Motif, so for now
    // we won't be using it. No one's quite sure why setDefaultCapable fixes
    // the incredible shrinking button problem in Motif.
    // this.setDefaultCapable(false);
    // this.btnUp.setDefaultCapable(false);
    // this.btnDown.setDefaultCapable(false);
  }

  // ---------------------------------------------------------------------------
  @Override
  public Component getTableCellRendererComponent(final JTable toTable, final Object toValue,
                                                 final boolean tlIsSelected, final boolean tlHasFocus, final int tnRow, final int tnColumn)
  {
    final boolean llPressed = (tnColumn == this.fnCurrentPressedButton);

    JButton loButton = this;
    if (tnColumn == this.fnSortColumn)
    {
      if (this.fnSortState == SortButtonRenderer.STATE_DOWN)
      {
        loButton = this.btnDown;
      }
      else if (this.fnSortState == SortButtonRenderer.STATE_UP)
      {
        loButton = this.btnUp;
      }
    }

    loButton.setText((toValue == null) ? "" : toValue.toString());

    loButton.getModel().setPressed(llPressed);
    loButton.getModel().setArmed(llPressed);

    return (loButton);
  }

  // ---------------------------------------------------------------------------
  public void setPressedColumn(final int tnColumn)
  {
    this.fnCurrentPressedButton = tnColumn;
  }

  // ---------------------------------------------------------------------------
  public void setSortColumn(final int tnColumn)
  {
    this.fnSortColumn = tnColumn;
  }

  // ---------------------------------------------------------------------------
  public void setSortColumn(final int tnColumn, final boolean tlToggle, final int tnSortState)
  {
    if (tlToggle)
    {
      if (this.fnSortColumn == tnColumn)
      {
        switch (this.fnSortState)
        {
          case SortButtonRenderer.STATE_NONE:
            this.fnSortState = SortButtonRenderer.STATE_DOWN;
            break;

          case SortButtonRenderer.STATE_DOWN:
            this.fnSortState = SortButtonRenderer.STATE_UP;
            break;

          case SortButtonRenderer.STATE_UP:
            this.fnSortState = SortButtonRenderer.STATE_DOWN;
            break;
        }
      }
      else
      {
        this.fnSortColumn = tnColumn;
        this.fnSortState = SortButtonRenderer.STATE_DOWN;
      }
    }
    else
    {
      this.fnSortColumn = tnColumn;
      this.fnSortState = tnSortState;
    }

  }

  // ---------------------------------------------------------------------------
  public boolean isCurrentColumnAscending()
  {
    return (this.fnSortState == SortButtonRenderer.STATE_UP);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
