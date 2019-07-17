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

import com.beowurks.BeoCommon.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ZipCompilation
{
  private static final int DATA_BUFFER = 2048;
  private static final String FILESEPARATOR = File.separator;

  private final String fcArchiveName;
  private final Vector<FileIndex> foFileListing;
  private final int fnCompressionLevel;

  private final SwingProgressComponents foSwingProgressComponents;

  private String fcArchiveTempFileName = "";

  private boolean flSaveFolderInformation;
  final private String fcSaveFolderRoot;

  private final String fcComment;

  // ---------------------------------------------------------------------------
  // By the way, the Vector toFileListing can consists of different types of
  // Objects.
  // For instance, for zipping files, it consists of a list of FileIndex,
  // whereas
  // for removing files, it consists of a list of Strings.
  // And I do check in the routines that use toFileListing for the correct
  // class.
  public ZipCompilation(final String tcArchiveName, final Vector<FileIndex> toFileListing,
                        final int tnCompressionLevel, final boolean tlSaveFolderInformation, final String tcSaveFolderRoot,
                        final String tcComment, final SwingProgressComponents toSwingProgressComponents)
  {
    this.fcArchiveName = tcArchiveName;
    this.foFileListing = toFileListing;
    this.fnCompressionLevel = tnCompressionLevel;
    this.fcComment = tcComment;

    this.foSwingProgressComponents = toSwingProgressComponents;

    this.flSaveFolderInformation = tlSaveFolderInformation;

    if ((this.flSaveFolderInformation) || (tcSaveFolderRoot == null))
    {
      this.fcSaveFolderRoot = "";
      // Ensure that flSaveFolderInformation is true;
      this.flSaveFolderInformation = true;
    }
    else
    {
      this.fcSaveFolderRoot = ZipCompilation.convertNameToZipStandard(Util.includeTrailingBackslash(tcSaveFolderRoot));
    }

    this.foSwingProgressComponents.updateDescription("Creating archive file, " + this.fcArchiveName + ". . . .");
    this.foSwingProgressComponents.updateOperationBar(0);

    try
    {
      final File loTempFile = File.createTempFile("Beo", ".zip");
      loTempFile.deleteOnExit();
      this.fcArchiveTempFileName = loTempFile.getPath();
    }
    catch (final Exception ignored)
    {
    }

  }

  // ---------------------------------------------------------------------------
  public void zipFiles() throws Exception
  {
    if (this.fcArchiveTempFileName.isEmpty())
    {
      throw new Exception("Unable to determine the temporary file name for compiling zip files.");
    }

    BufferedInputStream loBufferedInput = null;
    FileOutputStream loArchiveTempFile = null;
    ZipOutputStream loZipOutputTempStream = null;
    Exception loException = null;

    try
    {
      loArchiveTempFile = new FileOutputStream(this.fcArchiveTempFileName);
      loZipOutputTempStream = new ZipOutputStream(new BufferedOutputStream(loArchiveTempFile));

      loZipOutputTempStream.setLevel(this.fnCompressionLevel);

      if (!this.fcComment.isEmpty())
      {
        loZipOutputTempStream.setComment(this.fcComment);
      }

      final byte laData[] = new byte[ZipCompilation.DATA_BUFFER];
      final int lnCount = this.foFileListing.size();
      int lnCurrentOperation;
      for (int i = 0; i < lnCount; ++i)
      {
        if (this.foSwingProgressComponents.isCanceled())
        {
          break;
        }

        final FileIndex loFileIndex = this.foFileListing.elementAt(i);
        // You don't want to include the archive file.
        if (loFileIndex.fcFileName.compareToIgnoreCase(this.fcArchiveName) == 0)
        {
          continue;
        }

        this.foSwingProgressComponents.updateFileName(loFileIndex.fcFileName);

        String lcConvertName = ZipCompilation.convertNameToZipStandard(loFileIndex.fcFileName);
        if (!this.flSaveFolderInformation)
        {
          lcConvertName = this.stripRootFolderFromFileName(lcConvertName);
        }

        if ((!this.flSaveFolderInformation) && (lcConvertName.isEmpty()))
        {
          continue;
        }

        loZipOutputTempStream.setMethod(loFileIndex.flDirectory ? ZipOutputStream.STORED : ZipOutputStream.DEFLATED);
        final File loFileInfo = new File(loFileIndex.fcFileName);

        final ZipEntry loEntry = new ZipEntry(lcConvertName);

        loEntry.setTime(loFileInfo.lastModified());

        if (loFileIndex.flDirectory)
        {
          loEntry.setSize(0L);
          loEntry.setCompressedSize(0L);
          loEntry.setCrc(0L);
        }

        loZipOutputTempStream.putNextEntry(loEntry);

        if (!loFileIndex.flDirectory)
        {
          int lnCurrentFileZip;
          final double lnTotalSize = loFileInfo.length();
          double lnTotalRead = 0;
          int lnBytesRead;

          final FileInputStream loInputStream = new FileInputStream(loFileIndex.fcFileName);
          loBufferedInput = new BufferedInputStream(loInputStream, ZipCompilation.DATA_BUFFER);

          while ((lnBytesRead = loBufferedInput.read(laData, 0, ZipCompilation.DATA_BUFFER)) != -1)
          {
            loZipOutputTempStream.write(laData, 0, lnBytesRead);

            lnTotalRead += lnBytesRead;
            lnCurrentFileZip = (int) ((lnTotalRead / lnTotalSize) * 100.0);
            this.foSwingProgressComponents.updateFileBar(lnCurrentFileZip);
          }

          loBufferedInput.close();
          loZipOutputTempStream.closeEntry();
          this.foSwingProgressComponents.updateFileBar(0);
        }

        lnCurrentOperation = (int) (((i + 1.0) / lnCount) * 100.0);
        this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);
      }

      this.mergeExistingZipFileWithTemporaryZipFile(loZipOutputTempStream);
    }
    catch (final Exception loErr)
    {
      loException = loErr;
    }
    finally
    {
      if (loZipOutputTempStream != null)
      {
        loZipOutputTempStream.close();
      }
    }

    if (loException != null)
    {
      throw (loException);
    }

    if (!this.foSwingProgressComponents.isCanceled())
    {
      this.fileSave();
    }
  }

  // ---------------------------------------------------------------------------
  // Realize that with zip files, when adding or deleting entries, you can't
  // just
  // open the existing file and delete/append entries. To add, you have to
  // create
  // a new temporary zip file, add the new entries, append all of the entries
  // from
  // the current zip file by uncompressing and re-compressing the entries, then
  // save the temporary zip file over the current zip file. To delete, you have
  // to
  // create a new temporary zip file, add the non-selected entries by
  // uncompressing and re-compressing the entries and then save the temporary
  // zip
  // file over the current zip file.
  public void deleteFiles() throws Exception
  {
    if (this.fcArchiveTempFileName.isEmpty())
    {
      throw new Exception("Unable to determine the temporary file name for compiling zip files.");
    }

    this.foSwingProgressComponents.updateDescription("Removing selected files from " + this.fcArchiveName
            + " (and saving all others). . . .");

    FileOutputStream loArchiveTempFile = null;
    ZipOutputStream loZipOutputTempStream = null;
    Exception loException = null;
    ZipEntry loOriginalEntry = null;

    try
    {
      loArchiveTempFile = new FileOutputStream(this.fcArchiveTempFileName);
      loZipOutputTempStream = new ZipOutputStream(new BufferedOutputStream(loArchiveTempFile));

      loZipOutputTempStream.setLevel(this.fnCompressionLevel);

      if (!this.fcComment.isEmpty())
      {
        loZipOutputTempStream.setComment(this.fcComment);
      }

      final ZipFile loZipFile = new ZipFile(this.fcArchiveName);
      final Enumeration<? extends ZipEntry> loEnum = loZipFile.entries();

      final byte laData[] = new byte[ZipCompilation.DATA_BUFFER];
      int lnBytesRead;
      int lnCurrentOperation;
      final int lnCount = loZipFile.size();
      int lnTrack = 0;

      while (loEnum.hasMoreElements())
      {
        if (this.foSwingProgressComponents.isCanceled())
        {
          break;
        }

        loOriginalEntry = loEnum.nextElement();
        final String lcFileName = loOriginalEntry.getName();

        // If the file name is found, then it is being deleted, so
        // do not copy to the temporary zip file.
        if (this.foFileListing.contains(new FileIndex(lcFileName)))
        {
          continue;
        }

        this.foSwingProgressComponents.updateFileName(lcFileName);

        // If a directory, then create only an entry. There is no actual
        // file to also copy.
        if (loOriginalEntry.isDirectory())
        {
          loZipOutputTempStream.putNextEntry(loOriginalEntry);
          loZipOutputTempStream.closeEntry();
          continue;
        }

        final long lnOriginalCRC = loOriginalEntry.getCrc();
        final long lnOriginalSize = loOriginalEntry.getSize();

        final ZipEntry loCopyEntry = new ZipEntry(loOriginalEntry.getName());

        // Do not reset the crc, size or compressed size. They need to be
        // recalculated when they are copied to loZipOutputTempStream. The only
        // way they'll be recalculated is if they are set to -1 which is their
        // default values. You can't set the values to -1 using the set methods
        // cause an error will be thrown.
        //
        // By the way, if loOriginalEntry.getMethod is STORED, this will fail
        // when you try to putNextEntry due to the -1 values in size, crc
        // and compressed size. If I could figure a way to just copy the entry
        // rather than undeflate, then deflate, a lot of problems would be
        // solved.
        loCopyEntry.setTime(loOriginalEntry.getTime());
        loCopyEntry.setMethod(ZipEntry.DEFLATED);
        loCopyEntry.setExtra(loOriginalEntry.getExtra());
        loCopyEntry.setComment(loOriginalEntry.getComment());
        loZipOutputTempStream.putNextEntry(loCopyEntry);

        final InputStream loInputStream = loZipFile.getInputStream(loOriginalEntry);
        while ((lnBytesRead = loInputStream.read(laData)) != -1)
        {
          loZipOutputTempStream.write(laData, 0, lnBytesRead);
        }

        loZipOutputTempStream.closeEntry();

        final long lnCopyCRC = loCopyEntry.getCrc();
        final long lnCopySize = loCopyEntry.getSize();

        if (lnOriginalCRC != lnCopyCRC)
        {
          final String lcMessage = "Incorrect CRC value of "
                  + Long.toHexString(((Long) lnCopyCRC).longValue()).toUpperCase() + ". Instead, it should be "
                  + Long.toHexString(((Long) lnOriginalCRC).longValue()).toUpperCase() + ".";
          throw (new Exception(lcMessage));
        }
        if (lnOriginalSize != lnCopySize)
        {
          final String lcMessage = "Incorrect file size of " + lnCopySize + ". Instead, it should be " + lnOriginalSize
                  + ".";
          throw (new Exception(lcMessage));
        }

        lnCurrentOperation = (int) (((lnTrack + 1.0) / lnCount) * 100.0);
        this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);

        ++lnTrack;
      }
    }
    catch (final Exception loErr)
    {
      if (loOriginalEntry == null)
      {
        loException = loErr;
      }
      else
      {
        final String lcMessage = "The following error occurred with " + loOriginalEntry.getName() + ": "
                + loErr.getMessage();
        loException = new Exception(lcMessage);
      }
    }
    finally
    {
      if (loZipOutputTempStream != null)
      {
        loZipOutputTempStream.close();
      }
    }

    if (loException != null)
    {
      throw (loException);
    }

    if (!this.foSwingProgressComponents.isCanceled())
    {
      this.fileSave();
    }
  }

  // ---------------------------------------------------------------------------
  // Remember: with zip files, when adding or deleting entries, you can't just
  // open the existing file and delete/append entries. To add, you have to
  // create
  // a new temporary zip file, add the entries, and add all of the entries from
  // the current zip file, then save the temporary zip file over the current zip
  // file.
  // So merge all of the files from the current archive to the temporary
  // archive.
  private void mergeExistingZipFileWithTemporaryZipFile(final ZipOutputStream toZipOutputTempStream) throws Exception
  {
    final File loFile = new File(this.fcArchiveName);
    if (!loFile.exists())
    {
      return;
    }

    if (this.foSwingProgressComponents.isCanceled())
    {
      return;
    }

    this.foSwingProgressComponents.updateDescription("Merging existing files into " + this.fcArchiveName + ". . . .");

    Exception loException = null;
    try
    {
      final ZipFile loZipFile = new ZipFile(this.fcArchiveName);
      final Enumeration<? extends ZipEntry> loEnum = loZipFile.entries();

      final byte laData[] = new byte[ZipCompilation.DATA_BUFFER];
      int lnBytesRead;
      int lnCurrentOperation;
      final int lnCount = loZipFile.size();
      int lnTrack = 0;

      while (loEnum.hasMoreElements())
      {
        if (this.foSwingProgressComponents.isCanceled())
        {
          break;
        }

        final ZipEntry loOriginalEntry = loEnum.nextElement();
        this.foSwingProgressComponents.updateFileName(loOriginalEntry.getName());

        // When merging the existing file to the newly created temporary file,
        // you have to be aware of duplicate entries. Since the temporary file
        // contains
        // the most recent files, the duplicates in the original are discarded.
        if (loOriginalEntry.isDirectory())
        {
          final Exception loError = ZipCompilation.addEntry(toZipOutputTempStream, loOriginalEntry);
          if (loError == null)
          {
            toZipOutputTempStream.closeEntry();
          }
          else if (!ZipCompilation.isDuplicateError(loError))
          {
            loException = loError;
            break;
          }

          // It's a directory: no need to do anything else.
          continue;
        }

        // Do not reset the crc, size or compressed size. They need to be
        // recalculated when they are copied to loZipOutputTempStream. The only
        // way they'll be recalculated is if they are set to -1 which is their
        // default values. You can't set the values to -1 using the set methods
        // cause an error will be thrown.
        //
        // By the way, if loOriginalEntry.getMethod is STORED, this will fail
        // when you try to putNextEntry due to the -1 values in size, crc
        // and compressed size. If I could figure a way to just copy the entry
        // rather than undeflate, then deflate, a lot of problems would be
        // solved.
        final long lnOriginalCRC = loOriginalEntry.getCrc();
        final long lnOriginalSize = loOriginalEntry.getSize();

        final ZipEntry loCopyEntry = new ZipEntry(loOriginalEntry.getName());
        loCopyEntry.setTime(loOriginalEntry.getTime());
        loCopyEntry.setMethod(ZipEntry.DEFLATED);
        loCopyEntry.setExtra(loOriginalEntry.getExtra());
        loCopyEntry.setComment(loOriginalEntry.getComment());

        final Exception loError = ZipCompilation.addEntry(toZipOutputTempStream, loCopyEntry);
        if (loError != null)
        {
          // It's a duplicate: just skip over.
          if (ZipCompilation.isDuplicateError(loError))
          {
            continue;
          }
          else
          {
            loException = loError;
            break;
          }
        }

        final InputStream loInputStream = loZipFile.getInputStream(loOriginalEntry);
        while ((lnBytesRead = loInputStream.read(laData)) != -1)
        {
          toZipOutputTempStream.write(laData, 0, lnBytesRead);
        }

        toZipOutputTempStream.closeEntry();

        final long lnCopyCRC = loCopyEntry.getCrc();
        final long lnCopySize = loCopyEntry.getSize();

        if (lnOriginalCRC != lnCopyCRC)
        {
          final String lcMessage = "Incorrect CRC value of "
                  + Long.toHexString(((Long) lnCopyCRC).longValue()).toUpperCase() + ". Instead, it should be "
                  + Long.toHexString(((Long) lnOriginalCRC).longValue()).toUpperCase() + ".";
          throw (new Exception(lcMessage));
        }
        if (lnOriginalSize != lnCopySize)
        {
          final String lcMessage = "Incorrect file size of " + lnCopySize + ". Instead, it should be " + lnOriginalSize
                  + ".";
          throw (new Exception(lcMessage));
        }

        lnCurrentOperation = (int) (((lnTrack + 1.0) / lnCount) * 100.0);
        this.foSwingProgressComponents.updateOperationBar(lnCurrentOperation);

        ++lnTrack;
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
  private static Exception addEntry(final ZipOutputStream toZipOutputTempStream, final ZipEntry toEntry)
  {
    Exception loException = null;

    try
    {
      toZipOutputTempStream.putNextEntry(toEntry);
    }
    catch (final Exception loErr)
    {
      loException = loErr;
    }

    return (loException);
  }

  // ---------------------------------------------------------------------------
  private static boolean isDuplicateError(final Exception toError)
  {
    return ((toError instanceof ZipException) && (toError.getMessage().toLowerCase().contains("duplicate")));
  }

  // ---------------------------------------------------------------------------
  // Strip the root folder from the file name.
  private String stripRootFolderFromFileName(final String tcFileName)
  {
    String lcConvertName = tcFileName;
    if (lcConvertName.indexOf(this.fcSaveFolderRoot) == 0)
    {
      lcConvertName = lcConvertName.substring(this.fcSaveFolderRoot.length());
    }

    return (lcConvertName);
  }

  // ---------------------------------------------------------------------------
  // Change all of the file separators in the file name to '/'.
  private static String convertNameToZipStandard(final String tcFileName)
  {
    String lcTruncated = tcFileName;

    final int lnPos = tcFileName.indexOf(ZipCompilation.FILESEPARATOR);
    if (lnPos >= 0)
    {
      lcTruncated = tcFileName.substring(lnPos + 1);
    }

    if (ZipCompilation.FILESEPARATOR.compareTo("/") != 0)
    {
      return (Util.replaceAll(lcTruncated, ZipCompilation.FILESEPARATOR, "/").toString());
    }

    return (lcTruncated);
  }

  // ---------------------------------------------------------------------------
  private void fileSave() throws Exception
  {
    // I only check here if the cancel dialog has been, uh, canceled. Once
    // the save is in process, I let it finish. This way, a user doesn't have
    // an incomplete file on his or her drive.
    if (this.foSwingProgressComponents.isCanceled())
    {
      return;
    }

    this.foSwingProgressComponents.updateDescription("Saving archive file, " + this.fcArchiveName + ". . . .");
    this.foSwingProgressComponents.updateOperationBar(0);

    FileInputStream loIn = null;
    FileOutputStream loOut = null;
    Exception loError = null;

    try
    {
      loIn = new FileInputStream(this.fcArchiveTempFileName);
      loOut = new FileOutputStream(this.fcArchiveName);
    }
    catch (final Exception loErr)
    {
      loError = loErr;
    }

    if (loError == null)
    {
      final byte[] lcBuffer = new byte[ZipCompilation.DATA_BUFFER];

      final File loFileInfo = new File(this.fcArchiveTempFileName);
      final double lnTotalSize = loFileInfo.length();
      double lnTotalRead = 0;
      int lnCurrentFileZip;

      int lnBytesRead;
      try
      {
        while ((lnBytesRead = loIn.read(lcBuffer, 0, ZipCompilation.DATA_BUFFER)) != -1)
        {
          loOut.write(lcBuffer, 0, lnBytesRead);

          lnTotalRead += lnBytesRead;
          lnCurrentFileZip = (int) ((lnTotalRead / lnTotalSize) * 100.0);
          this.foSwingProgressComponents.updateOperationBar(lnCurrentFileZip);
        }
      }
      catch (final Exception loErr)
      {
        loError = loErr;
      }
    }

    try
    {
      if (loIn != null)
      {
        loIn.close();
      }

      if (loOut != null)
      {
        loOut.close();
      }
    }
    catch (final Exception loErr)
    {
      loError = loErr;
    }

    if (loError != null)
    {
      throw (loError);
    }
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
