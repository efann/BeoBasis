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

package com.beowurks.BeoExport;

import java.io.IOException;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class ExportExcel21 extends ExportBaseStream
{
  protected short fnTypeRec;
  protected short fnDataLength;

  // ---------------------------------------------------------------------------
  public ExportExcel21(final String tcSaveFileName)
  {
    super(tcSaveFileName);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeSeparator() throws IOException
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeEOL() throws IOException
  {
    this.fnRow++;
    this.fnCol = 0;
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeBOF() throws IOException
  {
    final short[] laBuffer = new short[2];
    laBuffer[0] = 0;
    laBuffer[1] = ExportBase.DOCTYPE_XLS;

    this.setBIFFBOF();
    this.writeRecordHeader();
    this.writeArray(laBuffer);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeEOF() throws IOException
  {
    this.fnTypeRec = ExportBase.EOF_BIFF;
    this.fnDataLength = 0;

    this.writeRecordHeader();
  }

  // ---------------------------------------------------------------------------
  protected void setBIFFBOF()
  {
    this.fnTypeRec = ExportBase.BOF_BIFF2;
    this.fnDataLength = 4;
  }

  // ---------------------------------------------------------------------------
  protected void setBIFFDIM()
  {
    this.fnTypeRec = ExportBase.DIMM_BIFF2;
    this.fnDataLength = 8;
  }

  // ---------------------------------------------------------------------------
  protected void writeRecordHeader() throws IOException
  {
    final short[] laBuffer = new short[2];

    laBuffer[0] = this.fnTypeRec;
    laBuffer[1] = this.fnDataLength;

    this.writeArray(laBuffer);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDimensions() throws IOException
  {
    final short[] laBuffer = new short[4];

    laBuffer[0] = this.fnMinSaveRows;
    laBuffer[1] = this.fnMaxSaveRows;
    laBuffer[2] = this.fnMinSaveCols;
    laBuffer[3] = this.fnMaxSaveCols;

    this.setBIFFDIM();
    this.writeRecordHeader();
    this.writeArray(laBuffer);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeLabel(final String tcFieldName) throws IOException
  {
    this.writeDataField(tcFieldName);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDataField(final int tnValue) throws IOException
  {
    this.writeDataField((double) tnValue);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDataField(final long tnValue) throws IOException
  {
    this.writeDataField((double) tnValue);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDataField(final double tnValue) throws IOException
  {
    this.fnTypeRec = 3;
    this.fnDataLength = 15;
    this.writeRecordHeader();

    final char[] laAttributes = new char[3];
    laAttributes[0] = 0;
    laAttributes[1] = 0;
    laAttributes[2] = 0;

    final short[] laBuffer = new short[2];
    laBuffer[0] = this.fnRow;
    laBuffer[1] = this.fnCol;

    this.writeArray(laBuffer);
    this.writeArray(laAttributes);
    this.writeDouble(tnValue);

    this.fnCol++;
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDataField(final String tcValue) throws IOException
  {
    final String lcValue = (tcValue.length() > 250) ? tcValue.substring(0, 250) : tcValue;

    this.fnTypeRec = 4;
    this.fnDataLength = (short) (8 + lcValue.length());
    this.writeRecordHeader();

    final char[] laAttributesAndLength = new char[4];
    laAttributesAndLength[0] = 0;
    laAttributesAndLength[1] = 0;
    laAttributesAndLength[2] = 0;
    laAttributesAndLength[3] = (char) lcValue.length();

    final short[] laBuffer = new short[2];
    laBuffer[0] = this.fnRow;
    laBuffer[1] = this.fnCol;

    this.writeArray(laBuffer);
    this.writeArray(laAttributesAndLength);
    this.foOutStream.writeBytes(lcValue);

    this.fnCol++;
  }

  // ---------------------------------------------------------------------------
  @Override
  public void writeDataField(final boolean tlValue) throws IOException
  {
    this.fnTypeRec = 5;
    this.fnDataLength = 9;
    this.writeRecordHeader();

    final char[] laAttributes = new char[3];
    laAttributes[0] = 0;
    laAttributes[1] = 0;
    laAttributes[2] = 0;

    final short[] laBuffer = new short[2];
    laBuffer[0] = this.fnRow;
    laBuffer[1] = this.fnCol;

    this.writeArray(laBuffer);
    this.writeArray(laAttributes);

    final char[] laValues = new char[2];

    laValues[0] = tlValue ? (char) 1 : (char) 0;
    laValues[1] = 0;

    this.writeArray(laValues);

    this.fnCol++;
  }

  // ---------------------------------------------------------------------------
  // Remember: Excel expects values that are stored in byte-reversed form (less
  // significant byte first).
  // So you can't use DataOutputStream.writeDouble, writeInt, etc. because
  // it stores the value with the most significant byte first. However,
  // using the ideas behind DataOutputStream, we just reversed everything.
  protected void writeDouble(final double tnValue) throws IOException
  {
    final long lnValue = Double.doubleToLongBits(tnValue);
    for (int x = 0; x < 8; ++x)
    {
      this.foOutStream.write((int) (lnValue >> (x * 8)) & 0xFF);
    }
  }

  // ---------------------------------------------------------------------------
  protected void writeArray(final short[] taArray) throws IOException
  {
    for (final short lnValue : taArray)
    {
      for (int x = 0; x < 2; ++x)
      {
        this.foOutStream.write((lnValue >> (x * 8)) & 0xFF);
      }
    }
  }

  // ---------------------------------------------------------------------------
  protected void writeArray(final char[] taArray) throws IOException
  {
    for (final char lnValue : taArray)
    {
      this.foOutStream.write(lnValue);
    }
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
