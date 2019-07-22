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

import com.beowurks.BeoCommon.BaseFrame;
import com.beowurks.BeoCommon.Util;
import com.beowurks.BeoTable.SortingTableModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingUtilities;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class ThreadPopulateZipTable extends Thread
{
  private final BaseFrame foFrame;
  private final String fcFileName;
  private final ZipTable foZipTable;
  private final int fnInitialSort;
  private final boolean flInitialAscend;

  private final boolean flAutoSizeColumns;

  private Object foFinishCallbackObject = null;
  private Method fmFinishCallbackMethod = null;
  private Object[] faFinishParameters = null;

  // ---------------------------------------------------------------------------------------------------------------------
  public ThreadPopulateZipTable(final BaseFrame toFrame, final String tcFileName, final ZipTable toZipTable,
                                final int tnInitialSort, final boolean tlInitialAscend, final Object toFinishCallbackObject,
                                final Method tmFinishCallbackMethod, final Object[] taFinishParameters)
  {
    this.foFrame = toFrame;
    this.fcFileName = tcFileName;
    this.foZipTable = toZipTable;

    this.fnInitialSort = tnInitialSort;
    this.flInitialAscend = tlInitialAscend;

    this.flAutoSizeColumns = false;

    // It doesn't matter if taParameters is null.
    if ((toFinishCallbackObject != null) && (tmFinishCallbackMethod != null))
    {
      this.foFinishCallbackObject = toFinishCallbackObject;
      this.fmFinishCallbackMethod = tmFinishCallbackMethod;
      this.faFinishParameters = taFinishParameters;
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public ThreadPopulateZipTable(final BaseFrame toFrame, final String tcFileName, final ZipTable toZipTable,
                                final int tnInitialSort, final boolean tlInitialAscend, final boolean tlAutoSizeColumns,
                                final Object toFinishCallbackObject, final Method tmFinishCallbackMethod, final Object[] taFinishParameters)
  {
    this.foFrame = toFrame;
    this.fcFileName = tcFileName;
    this.foZipTable = toZipTable;

    this.fnInitialSort = tnInitialSort;
    this.flInitialAscend = tlInitialAscend;

    this.flAutoSizeColumns = tlAutoSizeColumns;

    // It doesn't matter if taParameters is null.
    if ((toFinishCallbackObject != null) && (tmFinishCallbackMethod != null))
    {
      this.foFinishCallbackObject = toFinishCallbackObject;
      this.fmFinishCallbackMethod = tmFinishCallbackMethod;
      this.faFinishParameters = taFinishParameters;
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I can't run this thread in the EventQueue: it takes too long. And if I add
  // rows to the table model, it takes too long because with every row added,
  // a fireTableRowsInserted is issued. So by adding with the data vector,
  // there are no fire events called. I can wait till all of the files are
  // added and then issue one fireTableRowsInserted. However, to prevent any
  // errors with the JTable and table model as the rows are being added, I make
  // the table invisible.
  @Override
  public void run()
  {
    this.foFrame.setBusy(true);
    this.foZipTable.setVisible(false);

    final SortingTableModel loModel = this.foZipTable.getSortModel();
    loModel.clearAll();

    Exception loError = null;

    try
    {
      final ZipFile loZipFile = new ZipFile(this.fcFileName);

      final Enumeration<? extends ZipEntry> loEnum = loZipFile.entries();

      // If you place @SuppressWarnings before the method declaration, it
      // applies to the entire
      // method. However, if you place it before the offending line, it pertains
      // to only that line. Any unchecked warnings following the below line will
      // still be
      // called.
      @SuppressWarnings({"unchecked", "rawtypes"})
      final Vector<Vector> loDataVector = loModel.getDataVector();

      while (loEnum.hasMoreElements())
      {
        final ZipEntry loEntry = loEnum.nextElement();

        final double lnSize = loEntry.getSize();
        final long lnPacked = loEntry.getCompressedSize();
        final double lnRatio = (lnSize > 0.0) ? (((lnSize - lnPacked) / lnSize) * 100.0) : lnSize;
        final long lnCRC = loEntry.getCrc();
        final long lnDate = loEntry.getTime();

        final Vector<Object> loVector = new Vector<>(8);
        loVector.addElement(Util.extractFileName(loEntry.getName(), "/"));
        loVector.addElement(new Date(lnDate));
        loVector.addElement(Long.valueOf(loEntry.getSize()));
        loVector.addElement(Double.valueOf(lnRatio));
        loVector.addElement(Long.valueOf(loEntry.getCompressedSize()));
        loVector.addElement(Long.valueOf(lnCRC));
        loVector.addElement(Integer.valueOf(loEntry.getMethod()));
        loVector.addElement(Util.extractDirectory(loEntry.getName(), "/"));

        loDataVector.addElement(loVector);
      }
      loModel.fireTableRowsInserted(0, loDataVector.size() - 1);
    }
    catch (final IOException loErr)
    {
      loError = loErr;
    }

    if (loError == null)
    {
      final boolean llLoadFromProperties = !loModel.isSorted();
      final int lnColumn = llLoadFromProperties ? this.fnInitialSort : loModel.getSortColumn();

      this.foZipTable.sortColumn(lnColumn, false, llLoadFromProperties ? this.flInitialAscend : this.foZipTable
              .getSortButtonRenderer().isCurrentColumnAscending());
    }
    else
    {
      final File loFile = new File(this.fcFileName);
      final String lcCause = (loFile.length() > Integer.MAX_VALUE) ? "The file is over 2 Gigabytes in size."
              : "The file is an invalid/corrupt zip file";

      ZipCommon.errorExceptionInThread(this.foFrame, "There was an error in opening <b>" + this.fcFileName + "</b>. "
              + lcCause, loError.toString());
    }

    this.finishRoutine();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void finishRoutine()
  {
    // It doesn't matter if faParameters is null.
    if ((this.foFinishCallbackObject == null) || (this.fmFinishCallbackMethod == null))
    {
      return;
    }

    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          ThreadPopulateZipTable.this.fmFinishCallbackMethod.invoke(ThreadPopulateZipTable.this.foFinishCallbackObject,
                  ThreadPopulateZipTable.this.faFinishParameters);

          if (ThreadPopulateZipTable.this.flAutoSizeColumns)
          {
            ThreadPopulateZipTable.this.foZipTable.autoFitAllColumns();
          }
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException loErr)
        {
          Util.errorMessageInThread(null, "Error in ThreadPopulateZipTable.finishRoutine:\n\n" + loErr.getMessage());
        }
        finally
        {
          ThreadPopulateZipTable.this.foZipTable.setVisible(true);
          ThreadPopulateZipTable.this.foFrame.setBusy(false);
        }
      }

    });
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
