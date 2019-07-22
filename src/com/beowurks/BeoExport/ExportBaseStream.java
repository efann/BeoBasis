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

package com.beowurks.BeoExport;

import com.beowurks.BeoCommon.Util;

import java.io.IOException;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class ExportBaseStream extends ExportBase
{
  protected String fcEOL;
  protected String fcBOL;
  protected String fcSeparator;
  protected String fcPrefix;
  protected String fcSuffix;

  protected StringBuilder fcStringBuilder = new StringBuilder();

  // ---------------------------------------------------------------------------------------------------------------------
  public ExportBaseStream(final String tcSaveFileName)
  {
    super(tcSaveFileName);

    this.fcBOL = "";
    this.fcEOL = System.getProperty("line.separator");
    this.fcSeparator = ",";
    this.fcPrefix = "";
    this.fcSuffix = "";
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeEOF() throws IOException
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeBOF() throws IOException
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeBOL() throws IOException
  {
    this.writeString(this.fcBOL);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeEOL() throws IOException
  {
    this.writeString(this.fcEOL);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDimensions() throws IOException
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final int tnValue) throws IOException
  {
    this.writeDataField(Integer.toString(tnValue));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final long tnValue) throws IOException
  {
    this.writeDataField(Long.toString(tnValue));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final double tnValue) throws IOException
  {
    this.writeDataField(Double.toString(tnValue));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final boolean tlValue) throws IOException
  {
    this.writeDataField(Boolean.toString(tlValue));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final String tcValue) throws IOException
  {
    Util.clearStringBuilder(this.fcStringBuilder);

    this.fcStringBuilder.append(this.fcPrefix);
    this.fcStringBuilder.append(tcValue);
    this.fcStringBuilder.append(this.fcSuffix);

    this.writeString(this.fcStringBuilder.toString());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeSeparator() throws IOException
  {
    this.writeString(this.fcSeparator);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeLabel(final String tcFieldName) throws IOException
  {
    Util.clearStringBuilder(this.fcStringBuilder);

    this.fcStringBuilder.append(this.fcPrefix);
    this.fcStringBuilder.append(tcFieldName);
    this.fcStringBuilder.append(this.fcSuffix);

    this.writeString(this.fcStringBuilder.toString());
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
