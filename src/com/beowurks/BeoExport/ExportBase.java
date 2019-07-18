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

package com.beowurks.BeoExport;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
abstract public class ExportBase
{
  public final static int DOCTYPE_XLS = 0x10;
  public final static int BIT_BIFF3 = 0x0200;
  public final static int BIT_BIFF4 = 0x0400;
  public final static int BIT_BIFF5 = 0x0800;
  public final static int BOF_BIFF2 = 0x0009;
  public final static int DIMM_BIFF2 = 0x0000;
  public final static int BOF_BIFF3 = ExportBase.BOF_BIFF2 | ExportBase.BIT_BIFF3;
  public final static int DIMM_BIFF3 = ExportBase.DIMM_BIFF2 | ExportBase.BIT_BIFF3;
  public final static int BOF_BIFF4 = ExportBase.BOF_BIFF2 | ExportBase.BIT_BIFF4;
  public final static int DIMM_BIFF4 = ExportBase.DIMM_BIFF2 | ExportBase.BIT_BIFF4;
  public final static int BOF_BIFF5 = ExportBase.BOF_BIFF2 | ExportBase.BIT_BIFF5;
  public final static int EOF_BIFF = 0x000A;

  protected String fcFileName;
  protected short fnRow = 0;
  protected short fnCol = 0;

  protected short fnMinSaveRows = 0;
  protected short fnMaxSaveRows = 0;
  protected short fnMinSaveCols = 0;
  protected short fnMaxSaveCols = 0;

  protected DataOutputStream foOutStream;

  // ---------------------------------------------------------------------------------------------------------------------
  public ExportBase(final String tcSaveFileName)
  {
    this.fcFileName = tcSaveFileName;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setMax(final short tnMaxSaveCol, final short tnMaxSaveRow)
  {
    this.fnMaxSaveCols = tnMaxSaveCol;
    this.fnMaxSaveRows = tnMaxSaveRow;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // By the way, I was using FileWriter with BufferedWriter. It turns out that
  // FileWriter has a character translation. For example, 0x8F gets turned
  // into 0x3F. I could use BufferedWriter with DataOutputStream, but for now. .
  // . .
  // DataOutputStream is more C/C++ like where nothing is translated before
  // being stored to disk.
  // Bottom line is FileWriter is used for text files only, whereas I can use
  // DataOutputStream for text and binary.
  public boolean openFile() throws IOException
  {
    boolean llOkay = true;

    try
    {
      this.foOutStream = new DataOutputStream(new FileOutputStream(new File(this.fcFileName)));
    }
    catch (final IOException e)
    {
      llOkay = false;
      throw (e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public boolean closeFile() throws IOException
  {
    boolean llOkay = true;

    try
    {
      this.foOutStream.flush();
      this.foOutStream.close();
    }
    catch (final IOException e)
    {
      llOkay = false;
      throw (e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public boolean writeString(final String tcString) throws IOException
  {
    boolean llOkay = true;

    try
    {
      this.foOutStream.writeBytes(tcString);
    }
    catch (final IOException e)
    {
      llOkay = false;
      throw (e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public String getFileName()
  {
    return (this.fcFileName);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeBOF() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeEOF() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeBOL() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeEOL() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeSeparator() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDimensions() throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeLabel(String tcFieldName) throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDataField(int tnValue) throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDataField(long tnValue) throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDataField(double tnValue) throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDataField(String tcValue) throws IOException;

  // ---------------------------------------------------------------------------------------------------------------------
  abstract public void writeDataField(boolean tlValue) throws IOException;
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
