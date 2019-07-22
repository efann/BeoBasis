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
public class ExportHTML extends ExportBaseStream
{

  private final static String LINE_SEPARATOR = System.getProperty("line.separator");

  // ---------------------------------------------------------------------------------------------------------------------
  public ExportHTML(final String tcSaveFileName)
  {
    super(tcSaveFileName);

    this.fcBOL = "<tr>" + ExportHTML.LINE_SEPARATOR;
    this.fcEOL = "</tr>" + ExportHTML.LINE_SEPARATOR;
    this.fcSeparator = "";
    this.fcPrefix = "<td>";
    this.fcSuffix = "</td>";
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeBOF() throws IOException
  {
    final String lcHeader = "<html>" + ExportHTML.LINE_SEPARATOR + "<title>Data Table</title>"
        + ExportHTML.LINE_SEPARATOR + "<body bgcolor=\"#FFFFFF\" text=\"#000000\">" + ExportHTML.LINE_SEPARATOR
        + "<table border=\"1\">" + ExportHTML.LINE_SEPARATOR;

    this.writeString(lcHeader);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeEOF() throws IOException
  {
    final String lcFooter = "</table>" + ExportHTML.LINE_SEPARATOR + "</body>" + ExportHTML.LINE_SEPARATOR + "</html>"
        + ExportHTML.LINE_SEPARATOR;

    this.writeString(lcFooter);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeLabel(final String tcFieldName) throws IOException
  {
    Util.clearStringBuilder(this.fcStringBuilder);

    this.fcStringBuilder.append(this.fcPrefix);
    this.fcStringBuilder.append("<b>");
    this.fcStringBuilder.append(!tcFieldName.isEmpty() ? tcFieldName : "&nbsp;");
    this.fcStringBuilder.append("</b>");
    this.fcStringBuilder.append(this.fcSuffix);

    this.writeString(this.fcStringBuilder.toString());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void writeDataField(final String tcValue) throws IOException
  {
    Util.clearStringBuilder(this.fcStringBuilder);

    this.fcStringBuilder.append(this.fcPrefix);
    this.fcStringBuilder.append((!tcValue.isEmpty()) ? tcValue : "&nbsp;");
    this.fcStringBuilder.append(this.fcSuffix);

    this.writeString(this.fcStringBuilder.toString());
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
