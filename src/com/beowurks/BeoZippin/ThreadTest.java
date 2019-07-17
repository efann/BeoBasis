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
import com.beowurks.BeoCommon.CancelDialog;
import com.beowurks.BeoCommon.Util;

import java.util.Date;

import javax.swing.JLabel;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ThreadTest extends Thread
{
  private final BaseFrame foFrame;
  private final String fcFileName;
  private final ZipTable foZipTable;
  private final SwingProgressComponents foSwingProgressComponents;

  // ---------------------------------------------------------------------------
  public ThreadTest(final BaseFrame toFrame, final IZipProgressComponents toZipProgressComponents,
                    final StringBuilder tcFileName, final ZipTable toZipTable)
  {
    this.setPriority(Thread.NORM_PRIORITY);

    this.foFrame = toFrame;
    this.foSwingProgressComponents = new SwingProgressComponents(toZipProgressComponents);

    this.fcFileName = tcFileName.toString();
    this.foZipTable = toZipTable;
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    this.foFrame.setBusy(true);

    final Date loDateBegin = new Date();
    boolean llOkay = true;
    final boolean llCanceled;

    final CancelDialog loCancelDialog = this.foSwingProgressComponents.getCancelDialog();
    loCancelDialog.showCancelDialog();

    this.foSwingProgressComponents.resetAll();

    try
    {
      if (!this.foSwingProgressComponents.isCanceled())
      {
        final ZipTesting loZipTesting = new ZipTesting(this.fcFileName, this.foZipTable, this.foSwingProgressComponents);
        loZipTesting.testFiles();
      }
    }

    catch (final Exception loErr)
    {
      llOkay = false;
      ZipCommon.errorExceptionInThread(this.foFrame, "There was an error in testing the file(s) in the archive, <b>"
              + this.fcFileName + "</b>.", loErr.toString());
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
      Util.infoMessageInThread(this.foFrame, new JLabel("<html><font face=\"Arial\"><i><b>" + this.fcFileName
              + "</i></b> is okay!<br><br><i>(" + Util.displayTimeDifference(loDateBegin, new Date(), 1)
              + ")</i><br></font></html>"));
    }
    else
    {
      Util.errorMessageInThread(this.foFrame, new JLabel(
              "<html><font face=\"Arial\">There was an error in testing files in <i><b>" + this.fcFileName
                      + "</i></b>!<br><br><i>(" + Util.displayTimeDifference(loDateBegin, new Date(), 1)
                      + ")</i><br></font></html>"));
    }
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
