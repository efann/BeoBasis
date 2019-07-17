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

package com.beowurks.BeoCommon;

import java.awt.Container;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class HTMLTableWriter
{
  public static final int ALIGN_LEFT = 0;
  public static final int ALIGN_RIGHT = 1;
  public static final int ALIGN_CENTER = 2;

  public static final int STYLE_REGULAR = 0x0001;
  public static final int STYLE_BOLD = 0x0010;
  public static final int STYLE_ITALIC = 0x0100;

  private final String fcFileName;

  private BufferedWriter foBufferedWriter;

  private final Container foParentContainer;

  // ---------------------------------------------------------------------------
  public HTMLTableWriter(final String tcFileName, final Container toParentContainer)
  {
    this.fcFileName = tcFileName;
    this.foParentContainer = toParentContainer;
  }

  // ---------------------------------------------------------------------------
  public boolean openFile()
  {
    boolean llOkay = true;

    try
    {
      final FileWriter loFileWriter = new FileWriter(this.fcFileName);
      this.foBufferedWriter = new BufferedWriter(loFileWriter);
    }
    catch (final IOException e)
    {
      llOkay = false;
      Util.errorMessage(this.foParentContainer, e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  public boolean closeFile()
  {
    boolean llOkay = true;

    try
    {
      this.foBufferedWriter.close();
    }
    catch (final IOException e)
    {
      llOkay = false;
      Util.errorMessage(this.foParentContainer, e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  public boolean writeString(final String tcString)
  {
    boolean llOkay = true;

    try
    {
      this.foBufferedWriter.write(tcString);
    }
    catch (final IOException e)
    {
      llOkay = false;
      Util.errorMessage(this.foParentContainer, e);
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  public boolean writeTableHeader()
  {
    final String lcHeader = "<html>" + "\n" + "<title>Data Table</title>" + "\n"
            + "<body bgcolor=\"#FFFFFF\" text=\"#000000\">" + "\n" + "<table border=\"1\">" + "\n";

    return (this.writeString(lcHeader));
  }

  // ---------------------------------------------------------------------------
  public boolean writeTableFooter()
  {
    final String lcFooter = "</table>" + "\n" + "</body>" + "\n" + "</html>" + "\n";

    return (this.writeString(lcFooter));
  }

  // ---------------------------------------------------------------------------
  public boolean writeRowStart()
  {
    final String lcString = "<tr>" + "\n";

    return (this.writeString(lcString));
  }

  // ---------------------------------------------------------------------------
  public boolean writeRowEnd()
  {
    final String lcString = "</tr>" + "\n";

    return (this.writeString(lcString));
  }

  // ---------------------------------------------------------------------------
  public boolean writeCol(final double tnValue, final int tnStyle, final int tnAlignment)
  {
    return (this.writeCol(Double.toString(tnValue), tnStyle, tnAlignment));
  }

  // ---------------------------------------------------------------------------
  public boolean writeCol(final int tnValue, final int tnStyle, final int tnAlignment)
  {
    return (this.writeCol(Integer.toString(tnValue), tnStyle, tnAlignment));
  }

  // ---------------------------------------------------------------------------
  public boolean writeCol(final String tcValue, final int tnStyle, final int tnAlignment)
  {
    String lcString = "<td>";

    switch (tnAlignment)
    {
      case HTMLTableWriter.ALIGN_RIGHT:
        lcString += "<div align=\"right\">";
        break;
      case HTMLTableWriter.ALIGN_CENTER:
        lcString += "<div align=\"center\">";
        break;
    }

    if ((tnStyle & HTMLTableWriter.STYLE_BOLD) == HTMLTableWriter.STYLE_BOLD)
    {
      lcString += "<b>";
    }

    if ((tnStyle & HTMLTableWriter.STYLE_ITALIC) == HTMLTableWriter.STYLE_ITALIC)
    {
      lcString += "<i>";
    }

    lcString += tcValue;

    if ((tnStyle & HTMLTableWriter.STYLE_ITALIC) == HTMLTableWriter.STYLE_ITALIC)
    {
      lcString += "</i>";
    }

    if ((tnStyle & HTMLTableWriter.STYLE_BOLD) == HTMLTableWriter.STYLE_BOLD)
    {
      lcString += "</b>";
    }

    switch (tnAlignment)
    {
      case HTMLTableWriter.ALIGN_RIGHT:
      case HTMLTableWriter.ALIGN_CENTER:
        lcString += "</div>";
        break;
    }

    lcString += "</td>\n";

    return (this.writeString(lcString));
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
