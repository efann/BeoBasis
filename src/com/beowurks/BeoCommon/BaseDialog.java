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

package com.beowurks.BeoCommon;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
abstract public class BaseDialog extends JDialog
{

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseDialog(final JFrame toFrame, final String tcTitle)
  {
    this(toFrame, tcTitle, true);

    this.setCallingWindowCursor(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseDialog(final JFrame toFrame, final String tcTitle, final boolean tlModal)
  {
    super(toFrame, tcTitle, tlModal);

    this.setCallingWindowCursor(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void jbInit() throws Exception
  {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Now all of the inherited dialogs will look more like JInternalFrame.
    // By the way, if window decorations is not supported, then you'll
    // end up with a dialog that has no decorative bar, like with
    // the Microsoft Windows' themes.
    if (UIManager.getLookAndFeel().getSupportsWindowDecorations())
    {
      this.setUndecorated(true);
      this.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void centerDialog()
  {
    this.pack();

    this.setLocationRelativeTo(this.getOwner());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I got the following code from
  // http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
  public void makeVisible(final boolean tlCenter)
  {
    class DialogShowFix implements Runnable
    {
      final BaseDialog foBaseDialog;
      final boolean flCenter;

      public DialogShowFix(final BaseDialog toBaseDialog, final boolean tlFixCenter)
      {
        this.foBaseDialog = toBaseDialog;
        this.flCenter = tlFixCenter;
      }

      @Override
      public void run()
      {
        try
        {
          if (this.flCenter)
          {
            this.foBaseDialog.centerDialog();
          }

          this.foBaseDialog.setVisible(true);
        }
        catch (final Exception loErr)
        {
          System.err.println(loErr.getMessage());
        }
      }
    }

    if (this.isModal())
    {
      if (tlCenter)
      {
        this.centerDialog();
      }

      this.setVisible(true);
    }
    else
    {
      final Runnable loRunner = new DialogShowFix(this, tlCenter);
      EventQueue.invokeLater(loRunner);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void setCallingWindowCursor(final boolean tlBusy)
  {
    final Window loWindow = this.getOwner();
    if (loWindow != null)
    {
      if (loWindow instanceof BaseFrame)
      {
        ((BaseFrame) loWindow).setBusy(tlBusy, false);
      }
      else if (loWindow instanceof BaseDialog)
      {
        ((BaseDialog) loWindow).setBusy(tlBusy, false);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setBusy(final boolean tlBusy)
  {
    this.setBusy(tlBusy, true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setBusy(final boolean tlBusy, final boolean tlIncludeFocusRoutines)
  {
    if (tlBusy)
    {
      WaitCursorRoutines.setWaitCursor(this, tlIncludeFocusRoutines);
    }
    else
    {
      WaitCursorRoutines.setDefaultCursor(this, tlIncludeFocusRoutines);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I created this because there isn't a programmatic closing method for a
  // JDialog.
  public void closeWindow()
  {
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Overridden so we can exit when window is closed
  @Override
  protected void processWindowEvent(final WindowEvent e)
  {
    // Remember, you don't have to call dispose as
    // this.setDefaultCloseOperation()
    // is set in jbInit. And it does work: I've tested it.
    switch (e.getID())
    {
      case WindowEvent.WINDOW_OPENED:
        this.setCallingWindowCursor(false);
        break;

      case WindowEvent.WINDOW_CLOSED:
        this.cleanUp();
        break;
    }

    super.processWindowEvent(e);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I created this because there isn't a good reliable destructor procedure in
  // Java.
  public final void cleanUp()
  {
    this.finalOperations();
    this.removeListeners();
    this.releasePointers();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void finalOperations()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void removeListeners()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void releasePointers()
  {
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
