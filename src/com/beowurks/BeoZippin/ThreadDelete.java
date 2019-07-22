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
import com.beowurks.BeoCommon.CancelDialog;
import com.beowurks.BeoCommon.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class ThreadDelete extends Thread
{
  private final BaseFrame foFrame;
  private final File foArchiveFile;
  private final SwingProgressComponents foSwingProgressComponents;

  private final Vector<FileIndex> foFileList = new Vector<>();

  private final ZipTable foZipTable;

  private final int fnCompressionLevel;

  private Object foFinishCallbackObject = null;
  private Method fmFinishCallbackMethod = null;
  private Object[] faFinishParameters = null;

  // ---------------------------------------------------------------------------------------------------------------------
  public ThreadDelete(final BaseFrame toFrame, final IZipProgressComponents toZipProgressComponents,
                      final File toArchiveFile, final ZipTable toZipTable, final int tnCompressionLevel,
                      final Object toFinishCallbackObject, final Method tmFinishCallbackMethod, final Object[] taFinishParameters)
  {
    this.setPriority(Thread.NORM_PRIORITY);

    this.foFrame = toFrame;
    this.foArchiveFile = toArchiveFile;
    this.foSwingProgressComponents = new SwingProgressComponents(toZipProgressComponents);

    this.foZipTable = toZipTable;

    this.fnCompressionLevel = tnCompressionLevel;

    // It doesn't matter if taParameters is null.
    if ((toFinishCallbackObject != null) && (tmFinishCallbackMethod != null))
    {
      this.foFinishCallbackObject = toFinishCallbackObject;
      this.fmFinishCallbackMethod = tmFinishCallbackMethod;
      this.faFinishParameters = taFinishParameters;
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void run()
  {
    final Date loDateBegin = new Date();
    boolean llOkay = true;
    final boolean llCanceled;

    this.foFrame.setBusy(true);

    final CancelDialog loCancelDialog = this.foSwingProgressComponents.getCancelDialog();
    loCancelDialog.showCancelDialog();

    this.foSwingProgressComponents.resetAll();

    this.buildList();

    final String lcFileName = this.foArchiveFile.getPath();

    final ZipComment loZipComment = new ZipComment(lcFileName);
    final String lcComment = loZipComment.getComment();

    try
    {
      if (!this.foSwingProgressComponents.isCanceled())
      {
        final ZipCompilation loZip = new ZipCompilation(lcFileName, this.foFileList, this.fnCompressionLevel, true,
                null, lcComment, this.foSwingProgressComponents);

        loZip.deleteFiles();
      }

      if (!this.foSwingProgressComponents.isCanceled())
      {
        final ZipTesting loZipTesting = new ZipTesting(lcFileName, null, this.foSwingProgressComponents);
        loZipTesting.testFiles();
      }

    }
    catch (final Exception loErr)
    {
      llOkay = false;
      ZipCommon.errorExceptionInThread(this.foFrame, "There was an error in removing files from the archive, <b>"
              + lcFileName + "</b>.", loErr.toString());
    }
    finally
    {
      llCanceled = this.foSwingProgressComponents.isCanceled();
      loCancelDialog.closeCancelDialog();
    }

    this.foSwingProgressComponents.resetAll();
    this.setPriority(Thread.MIN_PRIORITY);
    this.foFrame.setBusy(false);

    if (llCanceled)
    {
      Util.errorMessageInThread(this.foFrame, "You canceled the current operation. . . .");
    }
    else if (llOkay)
    {
      Util.infoMessageInThread(
              this.foFrame,
              new JLabel("<html><font face=\"Arial\"><i><b>" + lcFileName
                      + "</i></b> has been successfully created in the popular Zip format!<br><br><i>("
                      + Util.displayTimeDifference(loDateBegin, new Date(), 1) + ")</i><br></font></html>"));
    }
    else
    {
      Util.errorMessageInThread(this.foFrame, new JLabel(
              "<html><font face=\"Arial\">There was an error in removing files from <i><b>" + lcFileName
                      + "</i></b>!<br><br><i>(" + Util.displayTimeDifference(loDateBegin, new Date(), 1)
                      + ")</i><br></font></html>"));
    }

    this.finishRoutine();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void buildList()
  {
    this.foFileList.clear();
    final ZipTable loTable = this.foZipTable;
    final int lnCount = loTable.getSelectedRowCount();
    final int[] laRows = loTable.getSelectedRows();
    for (int i = 0; i < lnCount; ++i)
    {
      this.foFileList.addElement(new FileIndex(loTable.getFullPath(laRows[i])));
    }
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
          ThreadDelete.this.fmFinishCallbackMethod.invoke(ThreadDelete.this.foFinishCallbackObject,
                  ThreadDelete.this.faFinishParameters);
        }
        catch (final Exception ignored)
        {
        }
      }

    });
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
