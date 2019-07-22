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

package com.beowurks.BeoCommon;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BaseEditTable extends JTable
{
  private final ButtonEnabledRenderer foButtonEnabledRenderer = new ButtonEnabledRenderer();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable()
  {
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(final TableModel toDm)
  {
    super(toDm);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(final TableModel toDm, final TableColumnModel toCm)
  {
    super(toDm, toCm);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(final TableModel toDm, final TableColumnModel toCm, final ListSelectionModel toSm)
  {
    super(toDm, toCm, toSm);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(final int tnNumRows, final int tnNumColumns)
  {
    super(tnNumRows, tnNumColumns);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(Vector<? extends Vector> taRowData, Vector<?> taColumnNames)
  {
    super(taRowData, taColumnNames);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseEditTable(final Object[][] toRowData, final Object[] toColumnNames)
  {
    super(toRowData, toColumnNames);
    this.setupInputMap();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setupHeaderRenderer()
  {
    final TableColumnModel loModel = this.getColumnModel();

    final int lnCount = this.getColumnCount();
    for (int i = 0; i < lnCount; ++i)
    {
      loModel.getColumn(i).setHeaderRenderer(this.foButtonEnabledRenderer);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupInputMap()
  {
    this.getActionMap().put("TabGridCell", new GridCellAction(GridCellAction.MOVE_TAB));
    this.getActionMap().put("ShiftTabGridCell", new GridCellAction(GridCellAction.MOVE_SHIFT_TAB));
    this.getActionMap().put("UpGridCell", new GridCellAction(GridCellAction.MOVE_UP));
    this.getActionMap().put("DownGridCell", new GridCellAction(GridCellAction.MOVE_DOWN));

    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
        "TabGridCell");
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        "TabGridCell");
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
        "UpGridCell");
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
        "DownGridCell");
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("shift TAB"),
        "ShiftTabGridCell");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Over-ridden cause a JTable, when disabled, does not grey out the individual
  // cells.
  @Override
  public Component prepareRenderer(final TableCellRenderer toRenderer, final int tnRow, final int tnColumn)
  {
    final Component loComponent = super.prepareRenderer(toRenderer, tnRow, tnColumn);

    loComponent.setEnabled(this.isEnabled());

    return (loComponent);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class GridCellAction extends AbstractAction
{
  public final static int MOVE_TAB = 0;
  public final static int MOVE_SHIFT_TAB = 1;
  public final static int MOVE_DOWN = 2;
  public final static int MOVE_UP = 3;

  private final int fnDirection;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public GridCellAction(final int tnDirection)
  {
    this.fnDirection = tnDirection;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    final JTable loTable = (JTable) e.getSource();
    if ((loTable.getColumnCount() == 0) || (loTable.getRowCount() == 0))
    {
      return;
    }

    int lnRow, lnCol;

    if (loTable.isEditing())
    {
      lnRow = loTable.getEditingRow();
      lnCol = loTable.getEditingColumn();
    }
    else
    {
      lnRow = loTable.getSelectionModel().getAnchorSelectionIndex();

      if (lnRow == -1)
      {
        lnRow = 0;
      }

      lnCol = loTable.getColumnModel().getSelectionModel().getAnchorSelectionIndex();

      if (lnCol == -1)
      {
        lnCol = 0;
      }
    }

    final int lnRows = loTable.getRowCount();
    final int lnColumns = loTable.getColumnCount();

    int lnIncrement = 0;
    switch (this.fnDirection)
    {
      case GridCellAction.MOVE_TAB:
      case GridCellAction.MOVE_DOWN:
        lnIncrement = 1;
        break;

      case GridCellAction.MOVE_SHIFT_TAB:
      case GridCellAction.MOVE_UP:
        lnIncrement = -1;
        break;
    }

    switch (this.fnDirection)
    {
      case GridCellAction.MOVE_TAB:
      case GridCellAction.MOVE_SHIFT_TAB:
        lnCol += lnIncrement;
        if ((lnCol < 0) || (lnCol >= lnColumns))
        {
          lnCol = (lnCol < 0) ? lnColumns - 1 : 0;
          lnRow += lnIncrement;
          if ((lnRow < 0) || (lnRow >= lnRows))
          {
            // lnCol must come first before lnRow is changed.
            lnCol = (lnRow < 0) ? lnColumns - 1 : 0;
            lnRow = (lnRow < 0) ? lnRows - 1 : 0;
          }
        }
        break;

      case GridCellAction.MOVE_DOWN:
      case GridCellAction.MOVE_UP:
        lnRow += lnIncrement;
        if ((lnRow < 0) || (lnRow >= lnRows))
        {
          lnRow = (lnRow < 0) ? lnRows - 1 : 0;
          lnCol += lnIncrement;
          if ((lnCol < 0) || (lnCol >= lnColumns))
          {
            // lnRow must come first before lnCol is changed.
            lnRow = (lnCol < 0) ? lnRows - 1 : 0;
            lnCol = (lnCol < 0) ? lnColumns - 1 : 0;
          }
        }
        break;
    }

    loTable.changeSelection(lnRow, lnCol, false, false);
    loTable.editCellAt(lnRow, lnCol);
    if (loTable.isEditing())
    {
      loTable.getEditorComponent().requestFocus();
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// Cause when a JTable is disabled, the header cells do not become grey.
class ButtonEnabledRenderer extends JButton implements TableCellRenderer
{
  final Color foOriginalForeColor;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public ButtonEnabledRenderer()
  {
    this.foOriginalForeColor = this.getForeground();

    final Insets loInset = new Insets(4, 4, 4, 4);
    this.setMargin((Insets) loInset.clone());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public Component getTableCellRendererComponent(final JTable toTable, final Object toValue,
                                                 final boolean tlIsSelected, final boolean tlHasFocus, final int tnRow, final int tnColumn)
  {
    final JButton loButton = this;
    loButton.setForeground(toTable.isEnabled() ? this.foOriginalForeColor : UIManager
        .getColor("Label.disabledForeground"));

    loButton.setText((toValue == null) ? "" : toValue.toString());

    return (loButton);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
