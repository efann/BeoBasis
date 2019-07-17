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

package com.beowurks.BeoZippin;

import com.beowurks.BeoCommon.BaseFrame;
import com.beowurks.BeoCommon.CancelDialog;
import com.beowurks.BeoCommon.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ThreadCompile extends Thread
{
  private final BaseFrame foFrame;
  private final File foArchiveFile;
  private final SwingProgressComponents foSwingProgressComponents;

  private final boolean flRecurse;
  private final boolean flIncludeHiddenDirectories;
  private final boolean flIncludeHiddenFiles;
  private final boolean flSaveFolderInformation;

  private final int fnCompressionLevel;

  private final String[] faFolders;

  private final File[] faFileMask;

  private BuildFileList foBuildFileList = null;

  private final String fcSaveFolderRoot;

  private String fcComment = "";

  private Object foFinishCallbackObject = null;
  private Method fmFinishCallbackMethod = null;
  private Object[] faFinishParameters = null;

  // ---------------------------------------------------------------------------
  public ThreadCompile(final BaseFrame toFrame, final IZipProgressComponents toZipProgressComponents,
                       final File toArchiveFile, final boolean tlIncludeHiddenDirectories, final boolean tlIncludeHiddenFiles,
                       final int tnCompressionLevel, final String[] taFolders, final String tcComment,
                       final Object toFinishCallbackObject, final Method tmFinishCallbackMethod, final Object[] taFinishParameters)
  {
    this.setPriority(Thread.NORM_PRIORITY);

    this.foFrame = toFrame;
    this.foArchiveFile = toArchiveFile;
    this.foSwingProgressComponents = new SwingProgressComponents(toZipProgressComponents);

    this.flRecurse = true;
    this.flIncludeHiddenDirectories = tlIncludeHiddenDirectories;
    this.flIncludeHiddenFiles = tlIncludeHiddenFiles;
    this.flSaveFolderInformation = true;
    this.faFileMask = null;

    this.fnCompressionLevel = tnCompressionLevel;

    this.faFolders = taFolders;

    this.fcSaveFolderRoot = null;

    this.fcComment = tcComment;

    // It doesn't matter if taParameters is null.
    if ((toFinishCallbackObject != null) && (tmFinishCallbackMethod != null))
    {
      this.foFinishCallbackObject = toFinishCallbackObject;
      this.fmFinishCallbackMethod = tmFinishCallbackMethod;
      this.faFinishParameters = taFinishParameters;
    }
  }

  // ---------------------------------------------------------------------------
  public ThreadCompile(final BaseFrame toFrame, final IZipProgressComponents toZipProgressComponents,
                       final File toArchiveFile, final boolean tlRecurse, final boolean tlIncludeHiddenDirectories,
                       final boolean tlIncludeHiddenFiles, final boolean tlSaveFolderInformation, final int tnCompressionLevel,
                       final File[] taFileMask, final Object toFinishCallbackObject, final Method tmFinishCallbackMethod,
                       final Object[] taFinishParameters)
  {
    this.setPriority(Thread.NORM_PRIORITY);

    this.foFrame = toFrame;
    this.foArchiveFile = toArchiveFile;
    this.foSwingProgressComponents = new SwingProgressComponents(toZipProgressComponents);

    this.flRecurse = tlRecurse;
    this.flIncludeHiddenDirectories = tlIncludeHiddenDirectories;
    this.flIncludeHiddenFiles = tlIncludeHiddenFiles;
    boolean llSaveFolderInformation = tlSaveFolderInformation;
    this.faFileMask = taFileMask;

    this.fnCompressionLevel = tnCompressionLevel;

    this.faFolders = null;

    String lcSaveFolderRoot;
    if ((llSaveFolderInformation) || (this.faFileMask == null))
    {
      lcSaveFolderRoot = null;
    }
    else
    {
      // If the llSaveFolderInformation is false, then it is vital that all
      // of the faFileMask elements have the same parent. Otherwise, it
      // will be impossible to strip the Folder Root.
      lcSaveFolderRoot = this.faFileMask[0].getParent();

      boolean llOkay = true;
      for (final File loFileMask : this.faFileMask)
      {
        final String lcParent = loFileMask.getParent();
        llOkay = Util.isWindows() ? (lcParent.compareTo(lcSaveFolderRoot) == 0) : (lcParent
                .compareToIgnoreCase(lcSaveFolderRoot) == 0);
      }

      if (!llOkay)
      {
        llSaveFolderInformation = true;
        lcSaveFolderRoot = null;
        ZipCommon.errorException(this.foFrame,
                "Unable to determine the Folder Root! The path information will be saved.");
      }
    }

    this.fcSaveFolderRoot = lcSaveFolderRoot;
    this.flSaveFolderInformation = llSaveFolderInformation;

    this.fcComment = "";

    // It doesn't matter if taParameters is null.
    if ((toFinishCallbackObject != null) && (tmFinishCallbackMethod != null))
    {
      this.foFinishCallbackObject = toFinishCallbackObject;
      this.fmFinishCallbackMethod = tmFinishCallbackMethod;
      this.faFinishParameters = taFinishParameters;
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    final Date loDateBegin = new Date();
    boolean llOkay = true;
    final boolean llCanceled;

    this.foFrame.setBusy(true);

    final CancelDialog loCancelDialog = this.foSwingProgressComponents.getCancelDialog();
    loCancelDialog.showCancelDialog();

    final String lcFileName = this.foArchiveFile.getPath();
    this.foSwingProgressComponents.resetAll();

    try
    {
      this.buildList();

      if (!this.foSwingProgressComponents.isCanceled())
      {
        final ZipCompilation loZip = new ZipCompilation(lcFileName, this.foBuildFileList.getFileList(),
                this.fnCompressionLevel, this.flSaveFolderInformation, this.fcSaveFolderRoot, this.fcComment,
                this.foSwingProgressComponents);

        loZip.zipFiles();
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
      ZipCommon.errorExceptionInThread(this.foFrame, "There was an error in creating the archive, <b>" + lcFileName
              + "</b>.", loErr.toString());
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
      Util.errorMessageInThread(
              this.foFrame,
              new JLabel(
                      "<html><font face=\"Arial\">There was an error in creating <i><b>"
                              + lcFileName
                              + "</i></b>!<br><br>- Ensure that the folder exists.<br>- Ensure there is adequate disk space on the backup drive.<br>- If removable media, make sure there is a disk or CD in the targeted drive.<br>- Check that the zipped file itself is not open in some other program.<br>- Try a different encryption method (found on the menu under Tools->Options...).<br>- If using a UDF-formatted CD, the CD might be over-reporting the amount of free space.<br><br><i>("
                              + Util.displayTimeDifference(loDateBegin, new Date(), 1) + ")</i><br></font></html>"));
    }

    this.finishRoutine();
  }

  // ---------------------------------------------------------------------------
  private void buildList()
  {
    this.foBuildFileList = new BuildFileList(this.foSwingProgressComponents, this.flRecurse,
            this.flIncludeHiddenDirectories, this.flIncludeHiddenFiles);

    if (this.faFolders != null)
    {
      this.foBuildFileList.loadFiles(this.faFolders);
    }
    else if (this.faFileMask != null)
    {
      this.foBuildFileList.loadFiles(this.faFileMask);
    }
  }

  // ---------------------------------------------------------------------------
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
          ThreadCompile.this.fmFinishCallbackMethod.invoke(ThreadCompile.this.foFinishCallbackObject,
                  ThreadCompile.this.faFinishParameters);
        }
        catch (final Exception ignored)
        {
        }
      }

    });
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
