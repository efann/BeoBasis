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

// As this class references code from the book "Swing",
// it must contain the following paragraph:
//
// *  =================================================== 
// *  This program contains code from the book "Swing" 
// *  2nd Edition by Matthew Robinson and Pavel Vorobiev 
// *  http://www.spindoczine.com/sbe 
// *  ===================================================
//
package com.beowurks.BeoTable;

import com.beowurks.BeoCommon.Util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class SortingTable extends JTable implements MouseListener, MouseMotionListener, KeyListener, ActionListener
{
  public static final int COLUMN_ORDER = 0;
  public static final int COLUMN_WIDTH = 1;
  public static final int COLUMN_PROPERTIES = 2;

  // These properties are protected as they are called by the DisplayISMessage
  // class.
  // By the way, IS stands for Incremental Search
  protected StringBuilder fcIS_String = new StringBuilder(256);
  protected int fnIS_CurrentSearchRow = 0;

  protected Timer tmrIS_Reset = null;
  protected JLabel lblIS_Label = new JLabel();
  protected Popup popIS_TextWindow = null;
  protected JPanel pnlIS_Panel = null;

  // IS stands for Incremental Search
  // Timestamp returns long in milliseconds. So this should be ~3 seconds.
  private static final long IS_RESET_TIME = 3000;
  private static final char IS_BACKSPACE = 8;
  private static final char IS_DELETE = 127;
  private static final char IS_SPACE = ' ';
  private static final int IS_HOME_KEY = 36;
  private static final int IS_END_KEY = 35;

  private final SortButtonRenderer foSortButtonRenderer = new SortButtonRenderer();

  private boolean flMouseBeingDragged = false;

  private int fnPressedColumnIndexToModel = -1;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public SortingTable()
  {
    super(new SortingTableModel());

    this.commonInitiator();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public SortingTable(final SortingTableModel toSortingTableModel)
  {
    super(toSortingTableModel);

    this.commonInitiator();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void commonInitiator()
  {
    this.pnlIS_Panel = new JPanel(new BorderLayout());
    this.pnlIS_Panel.setBackground(UIManager.getColor("ToolTip.background"));
    this.pnlIS_Panel.add(this.lblIS_Label);

    this.setShowHorizontalLines(false);
    this.setShowVerticalLines(false);
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setRowSelectionAllowed(true);
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    this.setIntercellSpacing(new Dimension(0, 0));

    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.addKeyListener(this);

    // Removes the default behavior of HOME and END keys which was interfering
    // somewhat with the behavior
    // established in the below keyReleased routine.
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
        "none");
    this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
        "none");

    this.getTableHeader().addMouseListener(this);
    this.getTableHeader().addMouseMotionListener(this);

    this.tmrIS_Reset = new Timer((int) SortingTable.IS_RESET_TIME, this);
    this.tmrIS_Reset.setRepeats(false);

    this.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public SortButtonRenderer getSortButtonRenderer()
  {
    return (this.foSortButtonRenderer);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public SortingTableModel getSortModel()
  {
    final TableModel loModel = this.getModel();
    if (loModel instanceof SortingTableModel)
    {
      return ((SortingTableModel) this.getModel());
    }

    Util.errorMessage(null, "TableModel is not SortingTableModel");
    return (null);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setupHeaderRenderer()
  {
    final TableColumnModel loModel = this.getColumnModel();

    final int lnCount = this.getColumnCount();
    for (int i = 0; i < lnCount; ++i)
    {
      loModel.getColumn(i).setHeaderRenderer(this.foSortButtonRenderer);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setupColumns(final Object[][] taObjects)
  {
    final SortingTableModel loTableModel = this.getSortModel();
    final TableColumnModel loColumnModel = this.getColumnModel();

    final int lnRows = taObjects.length;

    for (final Object[] laObject : taObjects)
    {
      loTableModel.addColumn(laObject[0]);
    }

    for (int lnRow = 0; lnRow < lnRows; ++lnRow)
    {
      final Object loObject = taObjects[lnRow][1];

      if (loObject instanceof Boolean)
      {
        continue;
      }

      final TableColumn loColumn = loColumnModel.getColumn(lnRow);

      final TableCellPaddedRenderer loRenderer = (loObject instanceof Number) ? new TableCellPaddedRenderer(
          SwingConstants.RIGHT) : new TableCellPaddedRenderer();

      loColumn.setCellRenderer(loRenderer);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setupColumnWidthsThenPositions(final int[][] taColumnProps)
  {
    if (taColumnProps == null)
    {
      return;
    }

    final TableColumnModel loModel = this.getColumnModel();
    final int lnColumns = (taColumnProps.length > loModel.getColumnCount()) ? loModel.getColumnCount()
        : taColumnProps.length;

    // Set the preferred widths.
    for (int i = 0; i < lnColumns; ++i)
    {
      loModel.getColumn(i).setPreferredWidth(taColumnProps[i][SortingTable.COLUMN_WIDTH]);
    }

    // Now set the column order but only if all of the columns are included.
    // Otherwise
    // it just won't work.
    if (taColumnProps.length == loModel.getColumnCount())
    {
      final TableColumn[] laColumns = new TableColumn[lnColumns];
      for (int i = 0; i < lnColumns; ++i)
      {
        laColumns[i] = loModel.getColumn(i);
      }

      for (int i = 0; i < lnColumns; ++i)
      {
        loModel.removeColumn(laColumns[i]);
      }

      for (int i = 0; i < lnColumns; ++i)
      {
        for (int xx = 0; xx < lnColumns; ++xx)
        {
          if (taColumnProps[xx][SortingTable.COLUMN_ORDER] == i)
          {
            loModel.addColumn(laColumns[xx]);
            break;
          }
        }
      }
    }

  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void sortColumn(final int tnColumnIndexToModel, final boolean tlToggleState, final boolean tlAscending)
  {
    // Sometimes this routine is set through the program and not by a mouse
    // click. For instance,
    // when initializing the SortingTable, the sorted column is set from a saved
    // property.
    if (this.fnPressedColumnIndexToModel != tnColumnIndexToModel)
    {
      this.fnPressedColumnIndexToModel = tnColumnIndexToModel;
    }

    final SortingTableModel loSortModel = this.getSortModel();

    int[] laSelectedRows = null;
    if ((this.getSelectedRowCount() != 0) && (this.getSelectedRowCount() != this.getRowCount()))
    {
      laSelectedRows = this.getSelectedRows();
      final int lnLen = laSelectedRows.length;
      for (int i = 0; i < lnLen; ++i)
      {
        laSelectedRows[i] = loSortModel.getAbsoluteRowPosition(laSelectedRows[i]);
      }
    }

    final int lnSortState = tlAscending ? SortButtonRenderer.STATE_UP : SortButtonRenderer.STATE_DOWN;
    this.foSortButtonRenderer.setSortColumn(this.convertColumnIndexToView(tnColumnIndexToModel), tlToggleState,
        tlToggleState ? SortButtonRenderer.STATE_NONE : lnSortState);

    loSortModel.sortColumn(tnColumnIndexToModel, this.foSortButtonRenderer.isCurrentColumnAscending());

    if (laSelectedRows != null)
    {
      this.clearSelection();
      final int lnRows = laSelectedRows.length;
      for (int i = 0; i < lnRows; ++i)
      {
        laSelectedRows[i] = loSortModel.getRelativeRowPosition(laSelectedRows[i]);
        this.addRowSelectionInterval(laSelectedRows[i], laSelectedRows[i]);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void autoFitAllColumns()
  {
    // The majority of the sizing code came from
    // Owen McGovern (http://forums.sun.com/profile.jspa?userID=358507)
    // http://forums.sun.com/thread.jspa?forumID=54&messageID=2532529&threadID=527301
    final int lnColumns = this.getColumnCount();

    for (int lnCol = 0; lnCol < lnColumns; ++lnCol)
    {
      this.autoFitColumn(lnCol);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void autoFitColumn(final int tnColumn)
  {
    final int lnRows = this.getRowCount();
    int lnMaxWidth = this.getColumnHeaderSize(tnColumn);
    int lnMaxBorder = 0;
    final TableColumn loColumn = this.getColumnModel().getColumn(tnColumn);

    Component loCellComponent = null;

    TableCellRenderer loTableCellRenderer;

    for (int lnRow = 0; lnRow < lnRows; ++lnRow)
    {
      final Object loValue = this.getValueAt(lnRow, tnColumn);
      loTableCellRenderer = this.getCellRenderer(lnRow, tnColumn);
      loCellComponent = loTableCellRenderer.getTableCellRendererComponent(this, loValue, false, false, lnRow, tnColumn);

      if (loCellComponent instanceof JLabel)
      {
        final JLabel loLabel = (JLabel) loCellComponent;

        if (loLabel.getBorder() != null)
        {
          try
          {
            final Insets loInsets = loLabel.getBorder().getBorderInsets(loLabel);
            lnMaxBorder = Math.max(lnMaxBorder, loInsets.left + loInsets.right);
          }
          catch (final Exception loException)
          {
            loException.printStackTrace();
          }
        }

        final String lcText = loLabel.getText();
        final Font loFont = loLabel.getFont();
        final FontMetrics loFontMetrics = loLabel.getFontMetrics(loFont);

        final int lnTextWidth = SwingUtilities.computeStringWidth(loFontMetrics, lcText);

        lnMaxWidth = Math.max(lnMaxWidth, lnTextWidth);
      }
      else
      {
        lnMaxWidth = Math.max(lnMaxWidth, loCellComponent.getPreferredSize().width);
      }

    }

    loColumn.setPreferredWidth(lnMaxWidth + lnMaxBorder);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private int getColumnHeaderSize(final int tnColumn)
  {
    int lnWidth = 0;

    final TableColumn loColumn = this.getColumnModel().getColumn(tnColumn);
    Component loCellComponent = null;

    final TableCellRenderer loHeaderRenderer = loColumn.getHeaderRenderer();
    if (loHeaderRenderer != null)
    {
      final String lcHeaderValue = loColumn.getHeaderValue().toString();
      loCellComponent = loHeaderRenderer.getTableCellRendererComponent(this, lcHeaderValue, false, false, 0, tnColumn);

      if (loCellComponent instanceof JComponent)
      {
        final JComponent loComponent = (JComponent) loCellComponent;

        final String lcText = lcHeaderValue;

        final Font loFont = loComponent.getFont();
        final FontMetrics loFontMetrics = loComponent.getFontMetrics(loFont);

        lnWidth = SwingUtilities.computeStringWidth(loFontMetrics, lcText);
        lnWidth += loComponent.getInsets().left + loComponent.getInsets().right;

        if (loCellComponent instanceof JButton)
        {
          final JButton loButton = (JButton) loCellComponent;
          final Icon loIcon = loButton.getIcon();
          if (loIcon != null)
          {
            // It appears that there is about 4 pixels between the edge of the
            // text and the image.
            lnWidth += loIcon.getIconWidth() + 4;
          }
        }
      }
      else
      {
        lnWidth = loCellComponent.getPreferredSize().width;
      }
    }
    else
    {
      try
      {
        final String lcHeaderText = (String) loColumn.getHeaderValue();
        final JLabel loDefaultLabel = new JLabel(lcHeaderText);

        final Font font = loDefaultLabel.getFont();
        final FontMetrics fontMetrics = loDefaultLabel.getFontMetrics(font);

        lnWidth = SwingUtilities.computeStringWidth(fontMetrics, lcHeaderText);
      }
      catch (final ClassCastException loErr)
      {
        // Can't work out the header column width.
        lnWidth = 0;
      }
    }

    return (lnWidth);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface MouseListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseClicked(final MouseEvent toEvent)
  {
    EventQueue.invokeLater(new DisplayISMessage(this, null));

    final Object loObject = toEvent.getSource();

    if (loObject.equals(this.getTableHeader()))
    {
      if (toEvent.getClickCount() == 2)
      {
        final JTableHeader loHeader = this.getTableHeader();
        final int lnType = loHeader.getCursor().getType();

        if ((lnType == Cursor.E_RESIZE_CURSOR) || (lnType == Cursor.W_RESIZE_CURSOR))
        {
          // For some reason, I don't need to use convertColumnIndexToModel.
          int lnColumn = loHeader.columnAtPoint(toEvent.getPoint());

          // With the resize cursor, the center could still be over the right
          // column.
          // So if the cursor is in the left half of a column, subtract one, as
          // you
          // want the column to the left of the cursor.
          final Rectangle loRectangle = loHeader.getHeaderRect(lnColumn);
          if (toEvent.getPoint().x < loRectangle.getCenterX())
          {
            --lnColumn;
          }

          this.autoFitColumn(lnColumn);
        }
      }
    }

    // For some reason, just pressing on the header does not automatically set
    // focus on the table.
    if (!this.hasFocus())
    {
      this.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mousePressed(final MouseEvent toEvent)
  {
    EventQueue.invokeLater(new DisplayISMessage(this, null));

    final Object loObject = toEvent.getSource();

    if (loObject.equals(this.getTableHeader()))
    {
      final JTableHeader loHeader = this.getTableHeader();
      if (loHeader.getCursor().getType() == Cursor.HAND_CURSOR)
      {
        final int lnColumn = loHeader.columnAtPoint(toEvent.getPoint());
        this.fnPressedColumnIndexToModel = this.convertColumnIndexToModel(lnColumn);
        this.foSortButtonRenderer.setPressedColumn(lnColumn);

        loHeader.repaint();
      }
    }

    // For some reason, just pressing on the header does not automatically set
    // focus on the table.
    if (!this.hasFocus())
    {
      this.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseReleased(final MouseEvent toEvent)
  {
    EventQueue.invokeLater(new DisplayISMessage(this, null));

    final Object loObject = toEvent.getSource();

    if (loObject.equals(this.getTableHeader()))
    {
      final JTableHeader loHeader = this.getTableHeader();

      final boolean llSort = ((!this.flMouseBeingDragged) && (loHeader.getCursor().getType() == Cursor.HAND_CURSOR));
      if (this.flMouseBeingDragged)
      {
        this.flMouseBeingDragged = false;
      }

      this.foSortButtonRenderer.setPressedColumn(-1);
      if (llSort)
      {
        this.sortColumn(this.fnPressedColumnIndexToModel, true, false);
      }
      else
      {
        this.foSortButtonRenderer.setSortColumn(this.convertColumnIndexToView(this.getSortModel().getSortColumn()));
      }

      loHeader.repaint();
    }

    // For some reason, just pressing on the header does not automatically set
    // focus on the table.
    if (!this.hasFocus())
    {
      this.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseEntered(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseExited(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface MouseMotionListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseDragged(final MouseEvent toEvent)
  {
    EventQueue.invokeLater(new DisplayISMessage(this, null));

    final Object loObject = toEvent.getSource();

    if (loObject.equals(this.getTableHeader()))
    {
      if (!this.flMouseBeingDragged)
      {
        this.flMouseBeingDragged = true;
      }

      final JTableHeader loHeader = this.getTableHeader();
      if (loHeader.getCursor().getType() == Cursor.DEFAULT_CURSOR)
      {

        // The pressed column and sorted column must be constantly reset as the
        // column
        // and its position index constantly change.
        this.foSortButtonRenderer.setPressedColumn(this.convertColumnIndexToView(this.fnPressedColumnIndexToModel));
        this.foSortButtonRenderer.setSortColumn(this.convertColumnIndexToView(this.getSortModel().getSortColumn()));

        loHeader.repaint();
      }
    }

    // For some reason, just pressing on the header does not automatically set
    // focus on the table.
    if (!this.hasFocus())
    {
      this.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseMoved(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface KeyListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void keyPressed(final KeyEvent toEvent)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void keyReleased(final KeyEvent toEvent)
  {
    if (toEvent.isActionKey())
    {
      EventQueue.invokeLater(new DisplayISMessage(this, null));

      final int lnKeyCode = toEvent.getKeyCode();

      switch (lnKeyCode)
      {
        case SortingTable.IS_HOME_KEY:
          this.changeSelection(0, 0, false, false);
          toEvent.consume();
          break;

        case SortingTable.IS_END_KEY:
          this.changeSelection(this.getRowCount() - 1, 0, false, false);
          toEvent.consume();
          break;
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void keyTyped(final KeyEvent toEvent)
  {
    if (toEvent.isAltDown() || toEvent.isControlDown() || toEvent.isAltGraphDown() || toEvent.isMetaDown())
    {
      return;
    }

    final int lnSortedColumnIndex = this.convertColumnIndexToView(this.fnPressedColumnIndexToModel);
    if (lnSortedColumnIndex < 0)
    {
      return;
    }

    final char lcChar = toEvent.getKeyChar();
    if (((lcChar < SortingTable.IS_SPACE) || (lcChar == SortingTable.IS_DELETE))
        && (lcChar != SortingTable.IS_BACKSPACE))
    {
      EventQueue.invokeLater(new DisplayISMessage(this, null));
      return;
    }

    if (lcChar == SortingTable.IS_BACKSPACE)
    {
      final int lnDelPos = this.fcIS_String.length() - 1;
      if (lnDelPos >= 0)
      {
        this.fcIS_String.deleteCharAt(lnDelPos);
      }
      else
      {
        Toolkit.getDefaultToolkit().beep();
      }
    }
    else
    {
      this.fcIS_String.append(toEvent.getKeyChar());
    }

    final String lcSearch = this.fcIS_String.toString().trim();
    final int lnSearchLen = lcSearch.length();

    if (lnSearchLen == 0)
    {
      EventQueue.invokeLater(new DisplayISMessage(this, null));
      return;
    }

    EventQueue.invokeLater(new DisplayISMessage(this, lcSearch));

    final int lnRows = this.getRowCount();

    boolean llFound = false;
    for (int lnRow = this.fnIS_CurrentSearchRow; lnRow < lnRows; ++lnRow)
    {
      final String lcValue = this.getValueAt(lnRow, lnSortedColumnIndex).toString().trim();
      if (lnSearchLen > lcValue.length())
      {
        continue;
      }

      if (lcValue.substring(0, lnSearchLen).compareToIgnoreCase(lcSearch) == 0)
      {
        this.changeSelection(lnRow, lnSortedColumnIndex, false, false);

        llFound = true;
        break;
      }
    }

    if (!llFound)
    {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ActionListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent toEvent)
  {
    final Object loObject = toEvent.getSource();
    if (loObject.equals(this.tmrIS_Reset))
    {
      EventQueue.invokeLater(new DisplayISMessage(this, null));
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// Since the routines for resetting and displaying the incremental search
// string can be called from keyboard, mouse and timer events, I didn't want
// any collisions. So these routines are thrown to the Event Queue: this way,
// all calls are placed in a queue and therefore no collisions should occur.
// And I'm using the Event Queue 'cause I'm manipulating a Swing object, the
// Popup
// window.
class DisplayISMessage implements Runnable
{
  final private SortingTable foSortingTable;
  final private String fcISMessage;

  // ---------------------------------------------------------------------------------------------------------------------
  public DisplayISMessage(final SortingTable toTable, final String tcISMessage)
  {
    this.foSortingTable = toTable;
    this.fcISMessage = tcISMessage;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void run()
  {
    if (this.fcISMessage == null)
    {
      this.resetIncrementalSearch();
    }
    else
    {
      this.displayIncrementalSearchText(this.fcISMessage);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void displayIncrementalSearchText(final String tcISDisplayText)
  {
    this.foSortingTable.tmrIS_Reset.stop();

    if (this.foSortingTable.popIS_TextWindow != null)
    {
      // This clears and recycles the popup window. Check out the code for
      // PopupFactory;
      this.foSortingTable.popIS_TextWindow.hide();
    }

    if (tcISDisplayText == null)
    {
      return;
    }

    this.foSortingTable.lblIS_Label.setText("<html><i>Incremetal Search Phrase:</i> <b>" + tcISDisplayText
        + "</b></html>");

    if (this.foSortingTable.getParent() instanceof JViewport)
    {
      // This not a mistake: I want the tooltip to be at the header's top and
      // the JViewport's left.
      // If you use the left margin of the header, when scrolling to the right,
      // you could get a
      // negative number.
      this.foSortingTable.popIS_TextWindow = PopupFactory.getSharedInstance().getPopup(null,
          this.foSortingTable.pnlIS_Panel, this.foSortingTable.getParent().getLocationOnScreen().x,
          this.foSortingTable.getTableHeader().getLocationOnScreen().y);

      this.foSortingTable.popIS_TextWindow.show();
    }

    this.foSortingTable.tmrIS_Reset.start();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void resetIncrementalSearch()
  {
    this.displayIncrementalSearchText(null);

    Util.clearStringBuilder(this.foSortingTable.fcIS_String);
    this.foSortingTable.fnIS_CurrentSearchRow = 0;
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
