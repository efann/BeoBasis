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

import java.io.BufferedInputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ZipTesting
{
  private static final int DATA_BUFFER = 2048;

  private final ZipTable foZipTable;
  private final String fcZipFile;
  private final SwingProgressComponents foSwingProgressComponents;

  // ---------------------------------------------------------------------------
  public ZipTesting(final String tcZipFile, final ZipTable toZipTable,
                    final SwingProgressComponents toSwingProgressComponents)
  {
    this.fcZipFile = tcZipFile;
    this.foZipTable = toZipTable;
    this.foSwingProgressComponents = toSwingProgressComponents;

    this.foSwingProgressComponents.updateDescription("Testing archive file, " + this.fcZipFile + ". . . .");
  }

  // ---------------------------------------------------------------------------
  public void testFiles() throws Exception
  {
    int lnTotalRows = 0;
    int lnSelectedRows = 0;

    if (this.foZipTable != null)
    {
      lnTotalRows = this.foZipTable.getRowCount();
      lnSelectedRows = this.foZipTable.getSelectedRowCount();
    }

    Exception loException = null;

    try
    {
      if ((lnTotalRows == lnSelectedRows) || (lnSelectedRows == 0) || (this.foZipTable == null))
      {
        this.testAll();
      }
      else
      {
        this.testSelected();
      }
    }
    catch (final Exception e)
    {
      loException = e;
    }
    finally
    {
      System.gc();
    }

    if (loException != null)
    {
      throw (loException);
    }
  }

  // ---------------------------------------------------------------------------
  private void testSelected() throws Exception
  {
    final byte laData[] = new byte[ZipTesting.DATA_BUFFER];
    final CRC32 loCRC32 = new CRC32();
    Exception loException = null;
    final int[] laSelected = this.foZipTable.getSelectedRows();
    final int lnCount = laSelected.length;
    if (lnCount == 0)
    {
      return;
    }

    try
    {
      int lnCurrentOperation;

      final ZipFile loZipFile = new ZipFile(this.fcZipFile);
      ZipEntry loEntry;

      for (int i = 0; i < lnCount; ++i)
      {
        if (this.foSwingProgressComponents.isCanceled())
        {
          break;
        }

        loEntry = loZipFile.getEntry(this.foZipTable.getFullPath(laSelected[i]));

        if (loEntry == null)
        {
          continue;
        }

        if (loEntry.isDirectory())
        {
          continue;
        }

        loCRC32.reset();

        this.foSwingProgressComponents.updateFileName(loEntry.getName());

        final BufferedInputStream loInput = new BufferedInputStream(loZipFile.getInputStream(loEntry));

        final long lnFileSize = loEntry.getSize();
        long lnTotalRead = 0;
        int lnBytesRead;

        while ((lnBytesRead = loInput.read(laData, 0, ZipTesting.DATA_BUFFER)) != -1)
        {
          loCRC32.update(laData, 0, lnBytesRead);

          lnTotalRead += lnBytesRead;
          final int lnCurrentFileZip = (int) (((double) lnTotalRead / (double) lnFileSize) * 100.0);
          this.foSwingProgressComponents.updateFileBar(lnCurrentFileZip);
        }

        loInput.close();

        lnCurrentOperation = (int) (((i + 1.0) / lnCount) * 100.0);
        this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);

        if (loEntry.getCrc() != loCRC32.getValue())
        {
          throw (new Exception("Check sums are corrupt for " + loEntry.getName() + " in the zip file, "
                  + this.fcZipFile + "."));
        }

        if (lnTotalRead != lnFileSize)
        {
          throw (new Exception("Read " + lnTotalRead + " bytes for " + loEntry.getName() + " in the zip file, "
                  + this.fcZipFile + ": should be " + lnFileSize + " bytes."));
        }
      }
    }
    catch (final Exception loErr)
    {
      loException = loErr;
    }

    if (loException != null)
    {
      throw (loException);
    }
  }

  // ---------------------------------------------------------------------------
  private void testAll() throws Exception
  {
    final byte laData[] = new byte[ZipTesting.DATA_BUFFER];
    final CRC32 loCRC32 = new CRC32();
    Exception loException = null;

    try
    {
      int lnCurrentOperation;

      final ZipFile loZipFile = new ZipFile(this.fcZipFile);
      ZipEntry loEntry;

      final Enumeration<? extends ZipEntry> loEnum = loZipFile.entries();
      final int lnCount = loZipFile.size();

      for (int i = 0; i < lnCount; ++i)
      {
        if (this.foSwingProgressComponents.isCanceled())
        {
          break;
        }

        loEntry = loEnum.nextElement();
        if (loEntry.isDirectory())
        {
          continue;
        }

        loCRC32.reset();

        this.foSwingProgressComponents.updateFileName(loEntry.getName());

        final BufferedInputStream loInput = new BufferedInputStream(loZipFile.getInputStream(loEntry));

        final long lnFileSize = loEntry.getSize();
        long lnTotalRead = 0;
        int lnBytesRead;

        while ((lnBytesRead = loInput.read(laData, 0, ZipTesting.DATA_BUFFER)) != -1)
        {
          loCRC32.update(laData, 0, lnBytesRead);

          lnTotalRead += lnBytesRead;
          final int lnCurrentFileZip = (int) (((double) lnTotalRead / (double) lnFileSize) * 100.0);
          this.foSwingProgressComponents.updateFileBar(lnCurrentFileZip);
        }

        loInput.close();

        lnCurrentOperation = (int) (((i + 1.0) / lnCount) * 100.0);
        this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);

        if (loEntry.getCrc() != loCRC32.getValue())
        {
          throw (new Exception("Check sums are corrupt for " + loEntry.getName() + " in the zip file, "
                  + this.fcZipFile + "."));
        }

        if (lnTotalRead != lnFileSize)
        {
          throw (new Exception("Read " + lnTotalRead + " bytes for " + loEntry.getName() + " in the zip file, "
                  + this.fcZipFile + ": should be " + lnFileSize + " bytes."));
        }
      }
    }
    catch (final Exception loErr)
    {
      loException = loErr;
    }

    if (loException != null)
    {
      throw (loException);
    }
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
