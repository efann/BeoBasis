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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BaseInternalFrame extends JInternalFrame implements InternalFrameListener
{
  private boolean flResetActiveFrameCursor = true;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseInternalFrame()
  {
    super("", true, true, true, true);

    this.setBusy(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseInternalFrame(final String tcTitle)
  {
    super(tcTitle, true, true, true, true);

    this.setBusy(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void jbInit() throws Exception
  {
    this.addInternalFrameListener(this);

    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static public JInternalFrame getActiveFrame()
  {
    final JFrame loActiveFrame = BaseFrame.getActiveFrame();
    JDesktopPane loDesktopPane = null;

    if (loActiveFrame != null)
    {
      loDesktopPane = (JDesktopPane) Util.findObjectInTree(loActiveFrame, JDesktopPane.class);
    }

    return ((loDesktopPane != null) ? loDesktopPane.getSelectedFrame() : null);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public JFrame getParentFrame()
  {
    final Window loWindow = SwingUtilities.getWindowAncestor(this);

    JFrame loFrame = null;
    if (loWindow instanceof JFrame)
    {
      loFrame = (JFrame) loWindow;
    }
    else
    {
      System.err.println("Error BaseInternalFrame.getParentFrame: Not a JFrame object " + loWindow);
    }

    return (loFrame);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void centerFrame()
  {
    this.pack();

    final JDesktopPane loDesktopPane = this.getDesktopPane();
    if (loDesktopPane != null)
    {
      // Center the window
      final Dimension ldScreenSize = loDesktopPane.getSize();
      final Dimension ldFrameSize = this.getSize();

      final int lnX = (ldScreenSize.width - ldFrameSize.width) / 2;
      final int lnY = (ldScreenSize.height - ldFrameSize.height) / 2;

      this.setLocation((lnX > 0) ? lnX : 0, (lnY > 0) ? lnY : 0);
    }
    else
    {
      System.err.println("loDesktopPane is null in com.beowurks.BeoCommon.BaseInternalFrame.centerFrame().");
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I got the following code from
  // http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
  public void makeVisible(final boolean tlCenter)
  {
    this.makeVisible(tlCenter, true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I got the following code from
  // http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
  // By the way, I added the new parameter of tlUseInvokeLater as sometimes this
  // routine
  // is called from within an EventQueue.invokeLater routine which can throw off
  // the order of
  // GUI items being processed.
  public void makeVisible(final boolean tlCenter, final boolean tlUseInvokeLater)
  {
    class FrameShowFix implements Runnable
    {
      final BaseInternalFrame foBaseInternalFrame;
      final boolean flCenter;

      public FrameShowFix(final BaseInternalFrame toBaseInternalFrame, final boolean tlFrameShowFixCenter)
      {
        this.foBaseInternalFrame = toBaseInternalFrame;
        this.flCenter = tlFrameShowFixCenter;
      }

      @Override
      public void run()
      {
        try
        {
          if (this.foBaseInternalFrame.isIcon())
          {
            this.foBaseInternalFrame.setIcon(false);
          }

          if (this.flCenter)
          {
            this.foBaseInternalFrame.centerFrame();
          }
          else
          {
            this.foBaseInternalFrame.pack();
          }

          this.foBaseInternalFrame.setVisible(true);
          // You would think that
          // this.foBaseInternalFrame.getDesktopPane().setSelectedFrame(this.foBaseInternalFrame)
          // would work, but it doesn't.
          this.foBaseInternalFrame.setSelected(true);
        }
        catch (final Exception loErr)
        {
          System.err.println(loErr.getMessage());
        }
      }
    }

    if (tlUseInvokeLater)
    {
      EventQueue.invokeLater(new FrameShowFix(this, tlCenter));
    }
    else
    {
      final FrameShowFix loFrameShowFix = new FrameShowFix(this, tlCenter);
      loFrameShowFix.run();
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
    // With these JInternalFrames, you should set the cursor on the active
    // JFrame.
    if (tlBusy)
    {
      WaitCursorRoutines.setWaitCursor(BaseFrame.getActiveFrame(), tlIncludeFocusRoutines);
    }
    else
    {
      WaitCursorRoutines.setDefaultCursor(BaseFrame.getActiveFrame(), tlIncludeFocusRoutines);
    }
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
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface InternalFrameListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameOpened(final InternalFrameEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameClosing(final InternalFrameEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameClosed(final InternalFrameEvent e)
  {
    this.cleanUp();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameIconified(final InternalFrameEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameDeiconified(final InternalFrameEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameActivated(final InternalFrameEvent e)
  {
    if (this.flResetActiveFrameCursor)
    {
      this.flResetActiveFrameCursor = false;
      this.setBusy(false);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void internalFrameDeactivated(final InternalFrameEvent e)
  {
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
