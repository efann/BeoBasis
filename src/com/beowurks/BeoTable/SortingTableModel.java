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

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class SortingTableModel extends DefaultTableModel
{
  protected NumberFormat foLongFormat;
  protected NumberFormat foRealFormat;
  protected SimpleDateFormat foRegularDateFormat = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");

  private final Vector<SortIndex> foSortIndex = new Vector<>();
  private int fnColumnIndexToModel = -1;

  private boolean flSortInitialized = false;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public SortingTableModel()
  {

    this.foLongFormat = NumberFormat.getInstance();
    this.foLongFormat.setGroupingUsed(true);
    this.foLongFormat.setMaximumFractionDigits(0);

    this.foRealFormat = NumberFormat.getInstance();
    this.foRealFormat.setGroupingUsed(true);
    this.foRealFormat.setMaximumFractionDigits(3);
  }

  // ---------------------------------------------------------------------------
  @Override
  public boolean isCellEditable(final int row, final int column)
  {
    return (false);
  }

  // ---------------------------------------------------------------------------
  @Override
  public Class<?> getColumnClass(final int c)
  {
    final Object loObject = this.getValueAt(0, c);

    if (loObject == null)
    {
      // Default to the String class
      return (String.class);
    }

    return (loObject.getClass());
  }

  // ---------------------------------------------------------------------------
  public int getRelativeRowPosition(final int tnRow)
  {
    final int lnRow = tnRow;
    final int lnLen = this.foSortIndex.size();
    for (int i = 0; i < lnLen; ++i)
    {
      if (lnRow == this.foSortIndex.get(i).fnAbsoluteIndex)
      {
        return (i);
      }
    }

    return (lnRow);
  }

  // ---------------------------------------------------------------------------
  public int getAbsoluteRowPosition(final int tnRow)
  {
    int lnRow = tnRow;
    if (!this.foSortIndex.isEmpty())
    {
      lnRow = this.foSortIndex.get(tnRow).fnAbsoluteIndex;
    }

    return (lnRow);
  }

  // ---------------------------------------------------------------------------
  @Override
  public Object getValueAt(final int tnRow, final int tnCol)
  {
    int lnRow = tnRow;
    if (!this.foSortIndex.isEmpty())
    {
      lnRow = this.foSortIndex.get(tnRow).fnAbsoluteIndex;
    }

    final Object loObject = super.getValueAt(lnRow, tnCol);

    try
    {
      return (this.formatObject(loObject, tnCol));
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }

    return (loObject);
  }

  // ---------------------------------------------------------------------------
  public Object formatObject(final Object toValue, final int tnCol) throws Exception
  {
    if (toValue != null)
    {
      if (toValue.getClass() == Integer.class)
      {
        return (this.foLongFormat.format(toValue));
      }
      else if (toValue.getClass() == Long.class)
      {
        return (this.foLongFormat.format(toValue));
      }
      else if (toValue.getClass() == Double.class)
      {
        return (this.foRealFormat.format(toValue));
      }
      else if (toValue.getClass() == Date.class)
      {
        return (this.foRegularDateFormat.format(toValue));
      }
    }

    return (toValue);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void setValueAt(final Object toValue, final int tnRow, final int tnCol)
  {
    int lnRow = tnRow;
    if (!this.foSortIndex.isEmpty())
    {
      lnRow = this.foSortIndex.get(tnRow).fnAbsoluteIndex;
    }

    super.setValueAt(toValue, lnRow, tnCol);
  }

  // ---------------------------------------------------------------------------
  // You don't need an overwritten addRow routine 'cause the index doesn't
  // matter in that case.
  @Override
  public void removeRow(final int tnRow)
  {
    int lnRow = tnRow;
    if (!this.foSortIndex.isEmpty())
    {
      lnRow = this.foSortIndex.get(tnRow).fnAbsoluteIndex;
    }

    super.removeRow(lnRow);
  }

  // ---------------------------------------------------------------------------
  public void clearAll()
  {
    this.foSortIndex.removeAllElements();

    final int lnRows = this.getRowCount();
    if (lnRows <= 0)
    {
      return;
    }

    this.setRowCount(0);
  }

  // ---------------------------------------------------------------------------
  public void sortColumn(final int tnColumnIndexToModel, final boolean tlAscending)
  {
    if ((tnColumnIndexToModel < 0) || (tnColumnIndexToModel >= this.getColumnCount()))
    {
      return;
    }

    this.setSortColumn(tnColumnIndexToModel);

    final int lnSize = this.dataVector.size();
    Vector<?> loRowVector;
    Object loObject = null;
    // I do this just in case there are null values in the elements.
    for (int i = 0; i < lnSize; ++i)
    {
      loRowVector = (Vector<?>) this.dataVector.elementAt(i);
      if (loRowVector.elementAt(tnColumnIndexToModel) != null)
      {
        loObject = loRowVector.elementAt(tnColumnIndexToModel);
        break;
      }
    }

    if (loObject != null)
    {
      final Class<?> loClass = loObject.getClass();
      if (loClass == Integer.class)
      {
        Collections.sort(this.foSortIndex, new IntegerComparator(tlAscending));
      }
      else if (loClass == Long.class)
      {
        Collections.sort(this.foSortIndex, new LongComparator(tlAscending));
      }
      else if (loClass == Double.class)
      {
        Collections.sort(this.foSortIndex, new DoubleComparator(tlAscending));
      }
      else if ((loClass == Date.class) || (loClass == java.sql.Date.class) || (loClass == java.sql.Timestamp.class))
      {
        Collections.sort(this.foSortIndex, new DateComparator(tlAscending));
      }
      else if (loClass == Boolean.class)
      {
        Collections.sort(this.foSortIndex, new BooleanComparator(tlAscending));
      }
      else
      {
        Collections.sort(this.foSortIndex, new StringComparator(tlAscending));
      }
    }

  }

  // ---------------------------------------------------------------------------
  public int getSortColumn()
  {
    if (this.fnColumnIndexToModel < 0)
    {
      this.fnColumnIndexToModel = 0;
    }
    else if (this.fnColumnIndexToModel >= this.getColumnCount())
    {
      this.fnColumnIndexToModel = this.getColumnCount() - 1;
    }

    return (this.fnColumnIndexToModel);
  }

  // ---------------------------------------------------------------------------
  public void setSortColumn(final int tnColumnIndexToModel)
  {
    final int lnRowCount = this.getRowCount();

    // If the column and the column size hasn't changed, then don't reload the
    // data.
    if ((this.fnColumnIndexToModel == tnColumnIndexToModel) && (this.foSortIndex.size() == lnRowCount))
    {
      return;
    }

    if (tnColumnIndexToModel < 0)
    {
      this.fnColumnIndexToModel = 0;
    }
    else if (tnColumnIndexToModel >= this.getColumnCount())
    {
      this.fnColumnIndexToModel = this.getColumnCount() - 1;
    }
    else
    {
      this.fnColumnIndexToModel = tnColumnIndexToModel;
    }

    if (lnRowCount == 0)
    {
      this.foSortIndex.removeAllElements();
      return;
    }

    this.foSortIndex.ensureCapacity(lnRowCount);
    this.foSortIndex.removeAllElements();

    Vector<?> loRowVector;
    for (int i = 0; i < lnRowCount; ++i)
    {
      final SortIndex loSort = new SortIndex();

      loRowVector = (Vector<?>) this.dataVector.elementAt(i);
      loSort.foSortingValue = loRowVector.elementAt(this.fnColumnIndexToModel);

      loSort.fnAbsoluteIndex = i;
      this.foSortIndex.add(loSort);
    }

    if (!this.flSortInitialized)
    {
      this.flSortInitialized = true;
    }
  }

  // ---------------------------------------------------------------------------
  public boolean isSorted()
  {
    return (this.flSortInitialized);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class SortIndex
{
  protected int fnAbsoluteIndex;
  protected Object foSortingValue;
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class StringComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public StringComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final String loValue1 = (String) toValue1.foSortingValue;
    final String loValue2 = (String) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareToIgnoreCase(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class BooleanComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public BooleanComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final Boolean loValue1 = (Boolean) toValue1.foSortingValue;
    final Boolean loValue2 = (Boolean) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareTo(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class IntegerComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public IntegerComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final Integer loValue1 = (Integer) toValue1.foSortingValue;
    final Integer loValue2 = (Integer) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareTo(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class LongComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public LongComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final Long loValue1 = (Long) toValue1.foSortingValue;
    final Long loValue2 = (Long) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareTo(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class DoubleComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public DoubleComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final Double loValue1 = (Double) toValue1.foSortingValue;
    final Double loValue2 = (Double) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareTo(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class DateComparator implements Comparator<SortIndex>, Serializable
{
  private final boolean flAscending;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public DateComparator(final boolean tlAscending)
  {
    this.flAscending = tlAscending;
  }

  // ---------------------------------------------------------------------------
  @Override
  public int compare(final SortIndex toValue1, final SortIndex toValue2)
  {
    final Date loValue1 = (Date) toValue1.foSortingValue;
    final Date loValue2 = (Date) toValue2.foSortingValue;

    if ((loValue1 == null) && (loValue2 == null))
    {
      return (0);
    }
    else if (loValue1 == null)
    {
      return (this.flAscending ? -1 : 1);
    }
    else if (loValue2 == null)
    {
      return (this.flAscending ? 1 : -1);
    }

    return (loValue1.compareTo(loValue2) * (this.flAscending ? 1 : -1));
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
