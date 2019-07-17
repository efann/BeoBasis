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

import com.beowurks.BeoCommon.CancelDialog;

import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class SwingProgressComponents
{
  private final CancelDialog foCancelDialog;

  private final OperationBarRun foOperationBarRun;
  private final FileBarRun foFileBarRun;
  private final DescriptionRun foDescriptionRun;
  private final FileNameRun foFileNameRun;

  private int fnCurrentOperationBarValue;
  private int fnCurrentFileBarValue;

  // ---------------------------------------------------------------------------
  public SwingProgressComponents(final IZipProgressComponents toZipProgressComponents)
  {
    this.foCancelDialog = toZipProgressComponents.getCancelDialog();

    this.foOperationBarRun = new OperationBarRun(toZipProgressComponents.getCurrentOperationProgressBar());
    this.foFileBarRun = new FileBarRun(toZipProgressComponents.getCurrentFileProgressBar());
    this.foDescriptionRun = new DescriptionRun(toZipProgressComponents.getActionDescriptionLabel());
    this.foFileNameRun = new FileNameRun(toZipProgressComponents.getCurrentFileTextField());
  }

  // ---------------------------------------------------------------------------
  public void updateOperationBar(final int tnValue)
  {
    if (this.foOperationBarRun.isNull())
    {
      return;
    }

    if (this.fnCurrentOperationBarValue == tnValue)
    {
      return;
    }

    this.fnCurrentOperationBarValue = tnValue;

    this.foOperationBarRun.setValue(tnValue);

    EventQueue.invokeLater(this.foOperationBarRun);
  }

  // ---------------------------------------------------------------------------
  public void updateFileBar(final int tnValue)
  {
    if (this.foFileBarRun.isNull())
    {
      return;
    }

    if (this.fnCurrentFileBarValue == tnValue)
    {
      return;
    }

    this.fnCurrentFileBarValue = tnValue;

    this.foFileBarRun.setValue(tnValue);

    EventQueue.invokeLater(this.foFileBarRun);
  }

  // ---------------------------------------------------------------------------
  public void updateDescription(final String tcDescription)
  {
    if (this.foCancelDialog != null)
    {
      this.foCancelDialog.setMessage(tcDescription);
    }

    if (!this.foDescriptionRun.isNull())
    {
      this.foDescriptionRun.setValue(tcDescription);

      EventQueue.invokeLater(this.foDescriptionRun);
    }
  }

  // ---------------------------------------------------------------------------
  public void updateFileName(final String tcPath)
  {
    if (this.foFileNameRun.isNull())
    {
      return;
    }

    this.foFileNameRun.setValue(tcPath);

    EventQueue.invokeLater(this.foFileNameRun);
  }

  // ---------------------------------------------------------------------------
  public void resetAll()
  {
    this.updateDescription("Idle. . . .");
    this.updateFileName(" ");
    this.updateFileBar(0);
    this.updateOperationBar(0);

    this.fnCurrentFileBarValue = -1;
    this.fnCurrentOperationBarValue = -1;
  }

  // ---------------------------------------------------------------------------
  public CancelDialog getCancelDialog()
  {
    return (this.foCancelDialog);
  }

  // ---------------------------------------------------------------------------
  public boolean isCanceled()
  {
    // If no CancelDialog was defined, then it is not in use and
    // this function should return false so that operations can continue.
    if (this.foCancelDialog == null)
    {
      return (false);
    }

    return (this.foCancelDialog.isCanceled());
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class OperationBarRun implements Runnable
{
  private int fnValue;
  private final JProgressBar barActionCurrentOperation;

  // ---------------------------------------------------------------------------
  public OperationBarRun(final JProgressBar barActionCurrentOperation1)
  {
    this.barActionCurrentOperation = barActionCurrentOperation1;
  }

  // ---------------------------------------------------------------------------
  public void setValue(final int tnValue)
  {
    this.fnValue = tnValue;
  }

  // ---------------------------------------------------------------------------
  public boolean isNull()
  {
    return (this.barActionCurrentOperation == null);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    this.barActionCurrentOperation.setValue(this.fnValue);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class FileBarRun implements Runnable
{
  private int fnValue;
  private final JProgressBar barActionCurrentFile;

  // ---------------------------------------------------------------------------
  public FileBarRun(final JProgressBar barActionCurrentFile1)
  {
    this.barActionCurrentFile = barActionCurrentFile1;
  }

  // ---------------------------------------------------------------------------
  public void setValue(final int tnValue)
  {
    this.fnValue = tnValue;
  }

  // ---------------------------------------------------------------------------
  public boolean isNull()
  {
    return (this.barActionCurrentFile == null);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    this.barActionCurrentFile.setValue(this.fnValue);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class DescriptionRun implements Runnable
{
  private String fcValue;
  private final JLabel lblActionDescription;

  // ---------------------------------------------------------------------------
  public DescriptionRun(final JLabel lblActionDescription1)
  {
    this.lblActionDescription = lblActionDescription1;
  }

  // ---------------------------------------------------------------------------
  public void setValue(final String tcValue)
  {
    this.fcValue = tcValue;
  }

  // ---------------------------------------------------------------------------
  public boolean isNull()
  {
    return (this.lblActionDescription == null);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    this.lblActionDescription.setText(this.fcValue);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class FileNameRun implements Runnable
{
  private String fcValue;
  private final JTextField txtActionCurrentFile;

  // ---------------------------------------------------------------------------
  public FileNameRun(final JTextField txtActionCurrentFile1)
  {
    this.txtActionCurrentFile = txtActionCurrentFile1;
  }

  // ---------------------------------------------------------------------------
  public void setValue(final String tcValue)
  {
    this.fcValue = tcValue;
  }

  // ---------------------------------------------------------------------------
  public boolean isNull()
  {
    return (this.txtActionCurrentFile == null);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    this.txtActionCurrentFile.setText(this.fcValue);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
