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

import com.beowurks.BeoCommon.Util;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BuildFileList implements Comparator<FileIndex>
{
  private final Vector<FileIndex> foFileListing = new Vector<>();
  // I use the Hashtable as it doesn't allow duplicates; if the program
  // tries to add a duplicate entry, it is ignored.
  private final Hashtable<String, FileIndex> foTempNames = new Hashtable<>();

  private final File[] foRootFiles;
  private final SwingProgressComponents foSwingProgressComponents;
  private final boolean flRecurse;
  private final boolean flIncludeHiddenDirectories;
  private final boolean flIncludeHiddenFiles;

  private Pattern foPattern;

  // ---------------------------------------------------------------------------------------------------------------------
  public BuildFileList(final SwingProgressComponents toSwingProgressComponents, final boolean tlRecurse,
                       final boolean tlIncludeHiddenDirectories, final boolean tlIncludeHiddenFiles)
  {
    this.foRootFiles = File.listRoots();

    this.foSwingProgressComponents = toSwingProgressComponents;

    this.flRecurse = tlRecurse;
    this.flIncludeHiddenDirectories = tlIncludeHiddenDirectories;
    this.flIncludeHiddenFiles = tlIncludeHiddenFiles;

    this.foSwingProgressComponents.updateDescription("Building file list. . . .");
    this.foSwingProgressComponents.updateOperationBar(0);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static private boolean isAllFilesMask(final String tcFileMask)
  {
    return ((tcFileMask.compareTo("*.*") == 0) || (tcFileMask.compareTo("*") == 0));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public Vector<FileIndex> getFileList()
  {
    return (this.foFileListing);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private boolean isRootFile(final File toFile)
  {
    boolean llRoot = false;

    for (final File loRootFile : this.foRootFiles)
    {
      if (loRootFile.compareTo(toFile) == 0)
      {
        llRoot = true;
        break;
      }
    }

    return (llRoot);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void loadFiles(final File[] taFiles)
  {
    this.foTempNames.clear();

    for (final File loFile : taFiles)
    {
      if (loFile.isDirectory())
      {
        this.searchDirectory(loFile);
      }
      // Remember: a full file name is also a type of mask.
      else
      {
        final String lcFileMask = loFile.getName();

        if (BuildFileList.isAllFilesMask(lcFileMask))
        {
          this.searchDirectory(loFile.getParentFile());
        }
        else
        {
          this.setPattern(lcFileMask);
          this.searchDirectoryWithMask(loFile.getParentFile());
        }
      }
    }

    if (!this.foSwingProgressComponents.isCanceled())
    {
      this.foFileListing.clear();

      final Enumeration<FileIndex> loEnum = this.foTempNames.elements();
      while (loEnum.hasMoreElements())
      {
        this.foFileListing.addElement(loEnum.nextElement());
      }

      Collections.sort(this.foFileListing, this);

      this.foTempNames.clear();
    }

  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void loadFiles(final String[] taStartDirectories)
  {
    this.foFileListing.clear();

    for (final String lcStartDirectory : taStartDirectories)
    {
      if (!this.foSwingProgressComponents.isCanceled())
      {
        this.searchDirectory(new File(lcStartDirectory));
      }
    }

    if (!this.foSwingProgressComponents.isCanceled())
    {
      this.foFileListing.clear();

      final Enumeration<FileIndex> loEnum = this.foTempNames.elements();
      while (loEnum.hasMoreElements())
      {
        this.foFileListing.addElement(loEnum.nextElement());
      }

      Collections.sort(this.foFileListing, this);

      this.foTempNames.clear();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void searchDirectoryWithMask(final File toStartDirectory)
  {
    if (this.foSwingProgressComponents.isCanceled())
    {
      return;
    }

    if ((!this.flIncludeHiddenDirectories) && (toStartDirectory.isHidden()) && (!this.isRootFile(toStartDirectory)))
    {
      return;
    }

    FileIndex loIndex;
    if (!this.isRootFile(toStartDirectory))
    {
      final String lcPath = Util.includeTrailingBackslash(toStartDirectory.getPath());

      // I want to include the directory name because otherwise if the
      // directory is empty, it will not be included.
      loIndex = new FileIndex(lcPath, true, toStartDirectory.isHidden());

      this.foTempNames.put(loIndex.fcFileName, loIndex);

      this.foSwingProgressComponents.updateFileName(lcPath);
    }

    final File[] laSubFiles = toStartDirectory.listFiles();
    if (laSubFiles != null)
    {
      for (final File loSubFile : laSubFiles)
      {
        if (loSubFile.isDirectory())
        {
          if (this.flRecurse)
          {
            this.searchDirectoryWithMask(loSubFile);
          }
          continue;
        }

        if ((!this.flIncludeHiddenFiles) && (loSubFile.isHidden()))
        {
          continue;
        }

        if (!this.accept(loSubFile))
        {
          continue;
        }

        loIndex = new FileIndex(loSubFile.getPath(), false, loSubFile.isHidden());

        this.foTempNames.put(loIndex.fcFileName, loIndex);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void searchDirectory(final File toStartDirectory)
  {
    if (this.foSwingProgressComponents.isCanceled())
    {
      return;
    }

    if ((!this.flIncludeHiddenDirectories) && (toStartDirectory.isHidden()) && (!this.isRootFile(toStartDirectory)))
    {
      return;
    }

    FileIndex loIndex;
    if (!this.isRootFile(toStartDirectory))
    {
      final String lcPath = Util.includeTrailingBackslash(toStartDirectory.getPath());

      // I want to include the directory name because otherwise if the
      // directory is empty, it will not be included.
      loIndex = new FileIndex(lcPath, true, toStartDirectory.isHidden());

      this.foTempNames.put(loIndex.fcFileName, loIndex);
      this.foSwingProgressComponents.updateFileName(lcPath);
    }

    final File[] laSubFiles = toStartDirectory.listFiles();
    if (laSubFiles != null)
    {
      for (final File loSubFile : laSubFiles)
      {
        if (loSubFile.isDirectory())
        {
          if (this.flRecurse)
          {
            this.searchDirectory(loSubFile);
          }

          continue;
        }

        if ((!this.flIncludeHiddenFiles) && (loSubFile.isHidden()))
        {
          continue;
        }

        loIndex = new FileIndex(loSubFile.getPath(), false, loSubFile.isHidden());

        this.foTempNames.put(loIndex.fcFileName, loIndex);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // This section of code is from GlobFilter.setPattern located
  // in javax/swing/plaf/basic/BasicFileChooserUI.java.
  // I've modified it to fit within my coding style.
  public void setPattern(final String tcGlobPattern)
  {
    final char[] laGlobPattern = tcGlobPattern.toCharArray();
    final char[] laRevisedPattern = new char[laGlobPattern.length * 2];
    final boolean llWin32 = (File.separatorChar == '\\');
    boolean llBetweenBrackets = false;

    int j = 0;

    if (llWin32)
    {
      // On windows, a pattern ending with *.* is equal to ending with *
      int lnLength = laGlobPattern.length;
      if (tcGlobPattern.endsWith("*.*"))
      {
        lnLength -= 2;
      }
      for (int i = 0; i < lnLength; i++)
      {
        switch (laGlobPattern[i])
        {
          case '*':
            laRevisedPattern[j++] = '.';
            laRevisedPattern[j++] = '*';
            break;

          case '?':
            laRevisedPattern[j++] = '.';
            break;

          case '\\':
            laRevisedPattern[j++] = '\\';
            laRevisedPattern[j++] = '\\';
            break;

          default:
            if ("+()^$.{}[]".indexOf(laGlobPattern[i]) >= 0)
            {
              laRevisedPattern[j++] = '\\';
            }
            laRevisedPattern[j++] = laGlobPattern[i];
            break;
        }
      }
    }
    else
    {
      for (int i = 0; i < laGlobPattern.length; i++)
      {
        switch (laGlobPattern[i])
        {
          case '*':
            if (!llBetweenBrackets)
            {
              laRevisedPattern[j++] = '.';
            }
            laRevisedPattern[j++] = '*';
            break;

          case '?':
            laRevisedPattern[j++] = llBetweenBrackets ? '?' : '.';
            break;

          case '[':
            llBetweenBrackets = true;
            laRevisedPattern[j++] = laGlobPattern[i];

            if (i < (laGlobPattern.length - 1))
            {
              switch (laGlobPattern[i + 1])
              {
                case '!':
                case '^':
                  laRevisedPattern[j++] = '^';
                  i++;
                  break;

                case ']':
                  laRevisedPattern[j++] = laGlobPattern[++i];
                  break;
              }
            }
            break;

          case ']':
            laRevisedPattern[j++] = laGlobPattern[i];
            llBetweenBrackets = false;
            break;

          case '\\':
            if ((i == 0) && (laGlobPattern.length > 1) && (laGlobPattern[1] == '~'))
            {
              laRevisedPattern[j++] = laGlobPattern[++i];
            }
            else
            {
              laRevisedPattern[j++] = '\\';
              if ((i < (laGlobPattern.length - 1)) && ("*?[]".indexOf(laGlobPattern[i + 1]) >= 0))
              {
                laRevisedPattern[j++] = laGlobPattern[++i];
              }
              else
              {
                laRevisedPattern[j++] = '\\';
              }
            }
            break;

          default:
            if (!Character.isLetterOrDigit(laGlobPattern[i]))
            {
              laRevisedPattern[j++] = '\\';
            }
            laRevisedPattern[j++] = laGlobPattern[i];
            break;
        }
      }
    }

    this.foPattern = Pattern.compile(new String(laRevisedPattern, 0, j), Pattern.CASE_INSENSITIVE);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public boolean accept(final File toFile)
  {
    if (toFile == null)
    {
      return (false);
    }

    if (toFile.isDirectory())
    {
      return (true);
    }

    return (this.foPattern.matcher(toFile.getName()).matches());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface Comparator
  // ---------------------------------------------------------------------------------------------------------------------
  // I use this routine to ensure that the directories and their sub-directories
  // are grouped together.
  @Override
  public int compare(final FileIndex toValue1, final FileIndex toValue2)
  {
    final String lcValue1 = toValue1.fcFileName;
    final String lcValue2 = toValue2.fcFileName;

    if ((lcValue1 == null) && (lcValue2 == null))
    {
      return (0);
    }
    else if (lcValue1 == null)
    {
      return (-1);
    }
    else if (lcValue2 == null)
    {
      return (1);
    }

    final boolean llDirectory1 = toValue1.flDirectory;
    final boolean llDirectory2 = toValue2.flDirectory;

    final String lcFolder1 = llDirectory1 ? lcValue1 : Util.extractDirectory(lcValue1);
    final String lcFolder2 = llDirectory2 ? lcValue2 : Util.extractDirectory(lcValue2);

    final int lnResults = lcFolder1.compareToIgnoreCase(lcFolder2);
    if (lnResults == 0)
    {
      return (lcValue1.compareToIgnoreCase(lcValue2));
    }

    return (lnResults);
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // I've decided not to override the equals routine.
  // From the help documentation:
  // Note that it is always safe NOT to override Object.equals(Object). However,
  // if you do override it, remember that it should return true only if
  // the specified object is also a comparator and it imposes the same ordering
  // as this comparator.
  //
  // For example,
  //
  // return ((obj == this) || (obj instanceof BuildFileList));
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
