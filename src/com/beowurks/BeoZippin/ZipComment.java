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

import com.beowurks.BeoCommon.XMLTextReader;
import com.beowurks.BeoCommon.XMLTextWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.RandomAccessFile;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ZipComment
{
  private final static int BUFREADCOMMENT = 0x400;

  private final static String ROOTNODE = "RescueInfo";
  private final static String CHILDNODE = "Folder";

  private final String fcArchiveFileName;

  // ---------------------------------------------------------------------------
  public ZipComment(final String tcArchiveFileName)
  {
    this.fcArchiveFileName = tcArchiveFileName;
  }

  // ---------------------------------------------------------------------------
  public String[] getFoldersFromComments()
  {
    final String lcComment = this.getComment();
    String[] laFolders = null;

    if (lcComment.isEmpty())
    {
      return (null);
    }

    final XMLTextReader loReader = new XMLTextReader();
    if (!loReader.initializeXMLDocument(lcComment))
    {
      return (null);
    }

    final Document loDoc = loReader.getDocument();

    final NodeList loNodeList = loDoc.getElementsByTagName(ZipComment.CHILDNODE);
    final int lnLen = loNodeList.getLength();

    if (lnLen == 0)
    {
      return (null);
    }

    laFolders = new String[lnLen];

    for (int i = 0; i < lnLen; ++i)
    {
      final Element loElement = (Element) loNodeList.item(i);
      final Node loChild = loElement.getFirstChild();
      laFolders[i] = (loChild != null) ? loChild.getNodeValue() : "";
    }

    return (laFolders);
  }

  // ---------------------------------------------------------------------------
  public String getComment()
  {
    RandomAccessFile loFileStream = null;
    int lnPosition = -1;
    String lcComment = "";

    try
    {
      loFileStream = new RandomAccessFile(this.fcArchiveFileName, "r");
      lnPosition = ZipComment.findCentralDir(loFileStream);

      if (lnPosition != -1)
      {

        loFileStream.seek(lnPosition);
        // I got this from the writeEND method of ZipOutputStream in
        // java.util.zip jar
        loFileStream.readInt(); // END record signature
        loFileStream.readShort(); // number of this disk
        loFileStream.readShort(); // central directory start disk
        loFileStream.readShort(); // number of directory entries on disk
        loFileStream.readShort(); // total number of directory entries
        loFileStream.readInt(); // length of central directory
        loFileStream.readInt(); // offset of central directory
        // You can't use readShort because it reads in the wrong order from what
        // ZipOutputStream
        // uses in writeShort. By the way, you have to use readUnsignedByte.
        // Otherwise,
        // you can get negative numbers.
        final int lnCommentLen = (loFileStream.readUnsignedByte() + (loFileStream.readUnsignedByte() << 8));

        if (lnCommentLen > 0)
        {
          final byte[] laBuffer = new byte[lnCommentLen];
          if (loFileStream.read(laBuffer) > 0)
          {
            lcComment = new String(laBuffer);
          }
        }

      }
    }
    catch (final IOException ignored)
    {
    }

    try
    {
      if (loFileStream != null)
      {
        loFileStream.close();
      }
    }
    catch (final Exception ignored)
    {
    }

    return (lcComment);
  }

  // ---------------------------------------------------------------------------
  static public String buildBeoZipComment(final String[] taFolders)
  {
    final XMLTextWriter loWriter = new XMLTextWriter();
    loWriter.initializeXMLDocument();
    loWriter.createRootNode(ZipComment.ROOTNODE, null);

    for (final String lcFolder : taFolders)
    {
      loWriter.appendNodeToRoot(ZipComment.CHILDNODE, lcFolder, null);
    }

    return (loWriter.generateXMLString(2));
  }

  // ---------------------------------------------------------------------------
  public int getCentralDirectoryPosition()
  {
    RandomAccessFile loFileStream = null;
    int lnPosition = -1;

    try
    {
      loFileStream = new RandomAccessFile(this.fcArchiveFileName, "r");
      lnPosition = ZipComment.findCentralDir(loFileStream);
    }
    catch (final Exception ignored)
    {
    }

    try
    {
      if (loFileStream != null)
      {
        loFileStream.close();
      }
    }
    catch (final Exception ignored)
    {
    }

    return (lnPosition);
  }

  // ---------------------------------------------------------------------------
  // Finds the end-of-central-directory (END) header.
  // This source was translated from the routine unzlocal_SearchCentralDir
  // located in the file unzip.c
  // which was downloaded from http://www.gzip.org/zlib/
  static private int findCentralDir(final RandomAccessFile toFileStream)
  {
    final byte[] laBuffer;
    final int lnSizeFile;
    int lnBackRead;
    int lnMaxBack = 0xFFFF; /* maximum size of global comment */
    int lnPosFound = -1;

    try
    {
      lnSizeFile = (int) toFileStream.length();

      if (lnMaxBack > lnSizeFile)
      {
        lnMaxBack = lnSizeFile;
      }

      laBuffer = new byte[ZipComment.BUFREADCOMMENT + 4];

      lnBackRead = 4;
      while (lnBackRead < lnMaxBack)
      {
        int lnReadSize;
        final int lnReadPos;

        if ((lnBackRead + ZipComment.BUFREADCOMMENT) > lnMaxBack)
        {
          lnBackRead = lnMaxBack;
        }
        else
        {
          lnBackRead += ZipComment.BUFREADCOMMENT;
          lnReadPos = lnSizeFile - lnBackRead;

          lnReadSize = ((ZipComment.BUFREADCOMMENT + 4) < (lnSizeFile - lnReadPos)) ? (ZipComment.BUFREADCOMMENT + 4)
                  : (lnSizeFile - lnReadPos);
          toFileStream.seek(lnReadPos);

          lnReadSize = toFileStream.read(laBuffer);
          if (lnReadSize == -1)
          {
            break;
          }

          for (int i = lnReadSize - 3; i > 0; i--)
          {
            if ((laBuffer[i] == 0x50) && (laBuffer[i + 1] == 0x4b) && (laBuffer[i + 2] == 0x05)
                    && (laBuffer[i + 3] == 0x06))
            {
              lnPosFound = lnReadPos + i;
              break;
            }
          }

          if (lnPosFound != -1)
          {
            break;
          }
        }
      }

    }
    catch (final Exception ignored)
    {
    }

    return (lnPosFound);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
