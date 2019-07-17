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

import com.beowurks.BeoCommon.BaseFrame;
import com.beowurks.BeoCommon.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ZipExtraction
{
  private static final int DATA_BUFFER = 2048;

  protected SimpleDateFormat foRegularDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

  private final SwingProgressComponents foSwingProgressComponents;

  private final String fcStartDirectory;
  private final ZipTable foZipTable;
  private ZipFile foZipFile = null;
  private final String fcArchiveName;
  private final boolean flUsePath;
  private boolean flOverwriteExisting;
  private final BaseFrame foFrame;

  private boolean flNoToAll = false;

  private final DecimalFormat foByteDecimalFormat = new DecimalFormat("#,###");

  // ---------------------------------------------------------------------------
  public ZipExtraction(final String tcArchiveName, final ZipTable toZipTable, final String tcStartDirectory,
                       final boolean tlUsePath, final boolean tlOverwriteExisting, final BaseFrame toFrame,
                       final SwingProgressComponents toSwingProgressComponents)
  {
    this.fcArchiveName = tcArchiveName;
    this.foZipTable = toZipTable;
    this.fcStartDirectory = Util.replaceAll(Util.includeTrailingBackslash(tcStartDirectory), "\\", "/").toString();
    this.flUsePath = tlUsePath;
    this.flOverwriteExisting = tlOverwriteExisting;
    this.foSwingProgressComponents = toSwingProgressComponents;
    this.foFrame = toFrame;

    this.foSwingProgressComponents.updateDescription("Extracting from archive file, " + this.fcArchiveName + ". . . .");
  }

  // ---------------------------------------------------------------------------
  public void extractFiles() throws Exception
  {
    if (!ZipExtraction.makeDirectory(this.fcStartDirectory))
    {
      throw (new Exception("Unable to make the directory " + this.fcStartDirectory + "."));
    }

    this.flNoToAll = false;

    final int lnTotalRows = this.foZipTable.getRowCount();
    final int lnSelectedRows = this.foZipTable.getSelectedRowCount();

    Exception loError = null;
    try
    {
      this.foZipFile = new ZipFile(this.fcArchiveName);

      if ((lnTotalRows == lnSelectedRows) || (lnSelectedRows == 0))
      {
        this.extractAll();
      }
      else
      {
        this.extractSelected();
      }
    }
    catch (final Exception loErr1)
    {
      loError = loErr1;
    }

    if (loError != null)
    {
      throw (loError);
    }
  }

  // ---------------------------------------------------------------------------
  private void extractSelected() throws Exception
  {
    Exception loError = null;

    BufferedOutputStream loDest = null;
    BufferedInputStream loInput = null;
    FileOutputStream loFileOutput = null;

    ZipEntry loEntry;
    final byte laData[] = new byte[ZipExtraction.DATA_BUFFER];

    final int[] laSelected = this.foZipTable.getSelectedRows();
    final int lnCount = laSelected.length;
    if (lnCount == 0)
    {
      return;
    }

    int lnCurrentOperation;

    for (int i = 0; i < lnCount; ++i)
    {
      if (this.foSwingProgressComponents.isCanceled())
      {
        break;
      }

      loEntry = this.foZipFile.getEntry(this.foZipTable.getFullPath(laSelected[i]));

      if (loEntry == null)
      {
        continue;
      }

      if (this.flUsePath)
      {
        this.createPath(loEntry);
      }

      if (loEntry.isDirectory())
      {
        continue;
      }

      try
      {
        this.foSwingProgressComponents.updateFileName(loEntry.getName());

        loDest = null;
        loInput = null;
        loFileOutput = null;

        final String lcOutputFullName = this.fcStartDirectory
                + (this.flUsePath ? loEntry.getName() : Util.extractFileName(loEntry.getName(), "/"));

        boolean llWrite = true;
        if (!this.flOverwriteExisting)
        {
          llWrite = this.overwriteFileQuery(loEntry, lcOutputFullName);
        }

        if (llWrite)
        {
          loInput = new BufferedInputStream(this.foZipFile.getInputStream(loEntry));
          loFileOutput = new FileOutputStream(lcOutputFullName);
          loDest = new BufferedOutputStream(loFileOutput, ZipExtraction.DATA_BUFFER);

          final double lnTotalSize = loEntry.getSize();
          double lnTotalRead = 0;
          int lnBytesRead;

          while ((lnBytesRead = loInput.read(laData, 0, ZipExtraction.DATA_BUFFER)) != -1)
          {
            loDest.write(laData, 0, lnBytesRead);

            lnTotalRead += lnBytesRead;
            final int lnCurrentFileZip = (int) ((lnTotalRead / lnTotalSize) * 100.0);
            this.foSwingProgressComponents.updateFileBar(lnCurrentFileZip);
          }

          loDest.flush();
          loDest.close();
          loDest = null;

          loInput.close();
          loInput = null;

          final File loFile = new File(lcOutputFullName);
          try
          {
            loFile.setLastModified(loEntry.getTime());
          }
          catch (final SecurityException | IllegalArgumentException ignored)
          {
          }

          if (loFile.length() != loEntry.getSize())
          {
            throw (new Exception("The file size for " + loFile.getPath() + " does not match the file size of "
                    + loEntry.getName() + " found in the archive file of " + this.fcArchiveName + "."));
          }
        }
      }
      catch (final Exception loErr1)
      {
        loError = loErr1;
      }
      finally
      {
        if (loDest != null)
        {
          loDest.flush();
          loDest.close();
        }

        if (loInput != null)
        {
          loInput.close();
        }
      }

      lnCurrentOperation = (int) (((i + 1.0) / lnCount) * 100.0);
      this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);
    }

    if (loError != null)
    {
      throw (loError);
    }
  }

  // ---------------------------------------------------------------------------
  private void extractAll() throws Exception
  {
    Exception loError = null;

    BufferedOutputStream loDest = null;
    BufferedInputStream loInput = null;
    FileOutputStream loFileOutput = null;

    ZipEntry loEntry;
    final byte laData[] = new byte[ZipExtraction.DATA_BUFFER];

    int lnCurrentOperation;

    final Enumeration<? extends ZipEntry> loEnum = this.foZipFile.entries();
    final int lnCount = this.foZipFile.size();

    for (int i = 0; i < lnCount; ++i)
    {
      if (this.foSwingProgressComponents.isCanceled())
      {
        break;
      }

      loEntry = loEnum.nextElement();

      if (this.flUsePath)
      {
        this.createPath(loEntry);
      }

      if (loEntry.isDirectory())
      {
        continue;
      }

      try
      {
        this.foSwingProgressComponents.updateFileName(loEntry.getName());

        loDest = null;
        loInput = null;
        loFileOutput = null;

        final String lcOutputFullName = this.fcStartDirectory
                + (this.flUsePath ? loEntry.getName() : Util.extractFileName(loEntry.getName(), "/"));

        boolean llWrite = true;
        if (!this.flOverwriteExisting)
        {
          llWrite = this.overwriteFileQuery(loEntry, lcOutputFullName);
        }

        if (llWrite)
        {
          loInput = new BufferedInputStream(this.foZipFile.getInputStream(loEntry));
          loFileOutput = new FileOutputStream(lcOutputFullName);
          loDest = new BufferedOutputStream(loFileOutput, ZipExtraction.DATA_BUFFER);

          final double lnTotalSize = loEntry.getSize();
          double lnTotalRead = 0;
          int lnBytesRead;

          while ((lnBytesRead = loInput.read(laData, 0, ZipExtraction.DATA_BUFFER)) != -1)
          {
            loDest.write(laData, 0, lnBytesRead);

            lnTotalRead += lnBytesRead;
            final int lnCurrentFileZip = (int) ((lnTotalRead / lnTotalSize) * 100.0);
            this.foSwingProgressComponents.updateFileBar(lnCurrentFileZip);
          }

          loDest.flush();
          loDest.close();
          loDest = null;

          loInput.close();
          loInput = null;

          final File loFile = new File(lcOutputFullName);
          try
          {
            loFile.setLastModified(loEntry.getTime());
          }
          catch (final SecurityException | IllegalArgumentException ignored)
          {
          }

          if (loFile.length() != loEntry.getSize())
          {
            throw (new Exception("The file size for " + loFile.getPath() + " does not match the file size of "
                    + loEntry.getName() + " found in the archive file of " + this.fcArchiveName + "."));
          }
        }
      }
      catch (final Exception loErr1)
      {
        loError = loErr1;
      }
      finally
      {
        if (loDest != null)
        {
          loDest.flush();
          loDest.close();
        }

        if (loInput != null)
        {
          loInput.close();
        }
      }

      lnCurrentOperation = (int) (((i + 1.0) / lnCount) * 100.0);
      this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);
    }

    if (loError != null)
    {
      throw (loError);
    }
  }

  // ---------------------------------------------------------------------------
  private boolean overwriteFileQuery(final ZipEntry toEntry, final String tcFileName)
  {
    final File loFile = new File(tcFileName);

    if (loFile.exists())
    {
      if (this.flNoToAll)
      {
        return (false);
      }

      final String lcQuestion = "Replace\n" + tcFileName + "\n" + this.foByteDecimalFormat.format(loFile.length())
              + " bytes\n" + this.foRegularDateFormat.format(new Date(loFile.lastModified())) + "\n\n\twith\n\n"
              + toEntry.getName() + "\n" + this.foByteDecimalFormat.format(toEntry.getSize()) + " bytes\n"
              + this.foRegularDateFormat.format(new Date(toEntry.getTime())) + "\n\n";

      final int lnResults = ZipCommon.yesNoAllCancel(this.foFrame, this.foFrame.getTitle(), lcQuestion);

      if (lnResults == ZipCommon.CHOICE_YES)
      {
        return (true);
      }
      else if (lnResults == ZipCommon.CHOICE_YES_TO_ALL)
      {
        this.flOverwriteExisting = true;
        return (true);
      }
      else if (lnResults == ZipCommon.CHOICE_NO)
      {
        return (false);
      }
      else if (lnResults == ZipCommon.CHOICE_NO_TO_ALL)
      {
        this.flNoToAll = true;
        return (false);
      }
      else if (lnResults == ZipCommon.CHOICE_CANCEL)
      {
        this.cancelDialog();
        return (false);
      }
      else
      {
        this.cancelDialog();
        return (false);
      }
    }

    return (true);
  }

  // ---------------------------------------------------------------------------
  private void createPath(final ZipEntry toEntry) throws Exception
  {
    String lcExtract = Util.extractDirectory(toEntry.getName(), "/");
    if (lcExtract == null)
    {
      lcExtract = "";
    }
    final String lcDirectory = this.fcStartDirectory + (toEntry.isDirectory() ? toEntry.getName() : lcExtract);

    if (!ZipExtraction.makeDirectory(lcDirectory))
    {
      throw (new Exception("Unable to make the directory " + lcDirectory + "."));
    }
  }

  // ---------------------------------------------------------------------------
  static private boolean makeDirectory(final String tcDirectory)
  {
    final File loFile = new File(tcDirectory);

    boolean llOkay = true;

    if (!loFile.exists())
    {
      if (!loFile.mkdirs())
      {
        llOkay = false;
      }
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  private void cancelDialog()
  {
    this.foSwingProgressComponents.getCancelDialog().closeCancelDialog();
    while (!this.foSwingProgressComponents.isCanceled())
    {
      try
      {
        Thread.sleep(250);
      }
      catch (final Exception ignored)
      {
      }
    }
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
