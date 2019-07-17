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

import com.beowurks.BeoCommon.CancelDialog;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public interface IZipProgressComponents
{
  // ---------------------------------------------------------------------------
  JLabel getActionDescriptionLabel();

  // ---------------------------------------------------------------------------
  JTextField getCurrentFileTextField();

  // ---------------------------------------------------------------------------
  JProgressBar getCurrentFileProgressBar();

  // ---------------------------------------------------------------------------
  JProgressBar getCurrentOperationProgressBar();

  // ---------------------------------------------------------------------------
  CancelDialog getCancelDialog();
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
