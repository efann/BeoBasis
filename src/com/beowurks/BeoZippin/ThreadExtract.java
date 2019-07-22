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

package com.beowurks.BeoZippin;

import com.beowurks.BeoCommon.BaseFrame;
import com.beowurks.BeoCommon.CancelDialog;
import com.beowurks.BeoCommon.Util;

import javax.swing.JLabel;
import java.util.Date;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class ThreadExtract extends Thread
{
  private final BaseFrame foFrame;
  private final SwingProgressComponents foSwingProgressComponents;

  private final String fcStartDirectory;
  private final ZipTable foZipTable;
  private final String fcArchiveName;
  private final boolean flUsePath;
  private final boolean flOverwriteExisting;

  // ---------------------------------------------------------------------------------------------------------------------
  public ThreadExtract(final BaseFrame toFrame, final IZipProgressComponents toZipProgressComponents,
                       final String tcArchiveName, final ZipTable toZipTable, final String tcStartDirectory, final boolean tlUsePath,
                       final boolean tlOverwriteExisting)
  {
    this.setPriority(Thread.NORM_PRIORITY);

    this.foFrame = toFrame;
    this.foSwingProgressComponents = new SwingProgressComponents(toZipProgressComponents);

    this.fcArchiveName = tcArchiveName;
    this.foZipTable = toZipTable;
    this.fcStartDirectory = tcStartDirectory;
    this.flUsePath = tlUsePath;
    this.flOverwriteExisting = tlOverwriteExisting;
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

    try
    {
      if (!this.foSwingProgressComponents.isCanceled())
      {
        final ZipExtraction loExtract = new ZipExtraction(this.fcArchiveName, this.foZipTable, this.fcStartDirectory,
            this.flUsePath, this.flOverwriteExisting, this.foFrame, this.foSwingProgressComponents);
        loExtract.extractFiles();
      }
    }
    catch (final Exception loErr)
    {
      llOkay = false;
      ZipCommon.errorExceptionInThread(this.foFrame, "There was an error in extracting files from the archive, <b>"
          + this.fcArchiveName + "</b>.", loErr.toString());
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
      Util.infoMessageInThread(this.foFrame,
          new JLabel("<html><font face=\"Arial\">The files were successfully extracted to <i><b>"
              + this.fcStartDirectory + "</i></b> from the archive of <i><b>" + this.fcArchiveName
              + "</i></b>.<br><br><i>(" + Util.displayTimeDifference(loDateBegin, new Date(), 1)
              + ")</i><br></font></html>"));
    }
    else
    {
      Util.errorMessageInThread(this.foFrame,
          new JLabel("<html><font face=\"Arial\">There was an error in extracting files to <i><b>"
              + this.fcStartDirectory + "</i></b> from the archive of <i><b>" + this.fcArchiveName
              + "</i></b>!<br><br><i>(" + Util.displayTimeDifference(loDateBegin, new Date(), 1)
              + ")</i><br></font></html>"));
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
