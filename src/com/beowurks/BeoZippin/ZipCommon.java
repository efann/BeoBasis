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

import com.beowurks.BeoCommon.Util;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Window;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public final class ZipCommon
{
  protected final static int CHOICE_YES = 0;
  protected final static int CHOICE_YES_TO_ALL = 1;
  protected final static int CHOICE_NO = 2;
  protected final static int CHOICE_NO_TO_ALL = 3;
  protected final static int CHOICE_CANCEL = 4;

  protected final static String[] faYesNoAllCancelChoices = {"Yes", "Yes To All", "No", "No To All", "Cancel"};

  private final static StringBuilder fcExceptionError = new StringBuilder(256);

  // ---------------------------------------------------------------------------------------------------------------------
  private ZipCommon()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected void errorExceptionInThread(final Window toWindow, final String tcException)
  {
    ZipCommon.errorExceptionInThread(toWindow, "Please notify support@beowurks.com of the following error:",
        tcException);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected void errorExceptionInThread(final Window toWindow, final String tcMessage, final String tcException)
  {
    ZipCommon.setExceptionError(tcMessage, tcException);

    Util.errorMessageInThread(toWindow, new JLabel(ZipCommon.fcExceptionError.toString()));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected void errorException(final Window toWindow, final String tcMessage, final String tcException)
  {
    ZipCommon.setExceptionError(tcMessage, tcException);

    Util.errorMessage(toWindow, new JLabel(ZipCommon.fcExceptionError.toString()));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected void errorException(final Window toWindow, final String tcException)
  {
    ZipCommon.errorException(toWindow, "Please notify support@beowurks.com of the following error:", tcException);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected boolean yesnoException(final Window toWindow, final String tcMessage, final String tcException)
  {
    ZipCommon.setExceptionError(tcMessage, tcException);

    return (Util.yesNo(toWindow, ZipCommon.fcExceptionError.toString()));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static protected int yesNoAllCancel(final Window toWindow, final String tcTitle, final String tcMessage)
  {
    final int lnResults = JOptionPane.showOptionDialog(toWindow, tcMessage, tcTitle, JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE, null, ZipCommon.faYesNoAllCancelChoices, ZipCommon.faYesNoAllCancelChoices[0]);

    if ((lnResults >= 0) && (lnResults < ZipCommon.faYesNoAllCancelChoices.length))
    {
      return (lnResults);
    }

    return (-1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static private void setExceptionError(final String tcMessage, final String tcException)
  {
    Util.clearStringBuilder(ZipCommon.fcExceptionError);

    ZipCommon.fcExceptionError.append("<html><font face=\"Arial\">");
    ZipCommon.fcExceptionError.append(tcMessage);
    ZipCommon.fcExceptionError.append(" <br><br><i> ");
    ZipCommon.fcExceptionError.append(tcException);
    ZipCommon.fcExceptionError.append(" </i><p></p></font></html>");
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
