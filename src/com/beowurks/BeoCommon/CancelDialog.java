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

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class CancelDialog implements ComponentListener
{
  static private JOptionPane foCancelPane = null;
  static private JDialog foCancelDialog = null;
  private final Window foParentWindow;
  private final String fcLabel;
  private boolean flCanceled = false;

  // ---------------------------------------------------------------------------
  public CancelDialog(final Window toParentWindow, final String tcLabel)
  {
    this.foParentWindow = toParentWindow;
    this.fcLabel = tcLabel;

    this.setupCancelDialog();
  }

  // ---------------------------------------------------------------------------
  private void setupCancelDialog()
  {
    if (CancelDialog.foCancelPane == null)
    {
      CancelDialog.foCancelPane = new JOptionPane(this.fcLabel, JOptionPane.INFORMATION_MESSAGE,
              JOptionPane.DEFAULT_OPTION, null, new Object[]{UIManager.getString("OptionPane.cancelButtonText")}, null);
    }

    if (CancelDialog.foCancelDialog == null)
    {
      String lcTitle = System.getProperty(Util.TITLE_VALUE);
      if (lcTitle == null)
      {
        lcTitle = Util.DEFAULT_TITLE;
      }

      CancelDialog.foCancelDialog = CancelDialog.foCancelPane.createDialog(this.foParentWindow, lcTitle);
      CancelDialog.foCancelDialog.setModal(false);
      CancelDialog.foCancelDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      // I have to use a ComponentListener rather than a WindowListener because
      // I don't dispose
      // of the dialog: I just hide it.
      CancelDialog.foCancelDialog.addComponentListener(this);
    }
  }

  // ---------------------------------------------------------------------------
  public void setMessage(final String tcMessage)
  {
    CancelDialog.foCancelPane.setMessage(tcMessage);
  }

  // ---------------------------------------------------------------------------
  public void showCancelDialog()
  {
    this.flCanceled = false;

    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        if (CancelDialog.foCancelDialog != null)
        {
          CancelDialog.foCancelDialog.setVisible(true);
        }
      }
    });
  }

  // ---------------------------------------------------------------------------
  public boolean isCanceled()
  {
    return (this.flCanceled);
  }

  // ---------------------------------------------------------------------------
  public void closeCancelDialog()
  {
    // Sometimes, if an operation finished quickly, the dialog box was not yet
    // visible, the close was issued, nothing happened, and then the Cancel
    // Dialog
    // box appeared. Now I just make sure that the box is hidden no matter what.
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        if (CancelDialog.foCancelDialog != null)
        {
          CancelDialog.foCancelDialog.setVisible(false);
        }
      }
    });
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface ComponentListener
  // ---------------------------------------------------------------------------
  @Override
  public void componentResized(final ComponentEvent e)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void componentMoved(final ComponentEvent e)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void componentShown(final ComponentEvent e)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void componentHidden(final ComponentEvent e)
  {
    this.flCanceled = true;
  }
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
