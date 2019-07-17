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

package com.beowurks.BeoCommon;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public final class WaitCursorRoutines
{
  static final private MouseAdapterAbsorber ABSORB_MOUSE = new MouseAdapterAbsorber();
  static final private MouseMotionAdapterAbsorber ABSORB_MOUSE_MOTION = new MouseMotionAdapterAbsorber();
  static final private MouseWheelAdapterAbsorber ABSORB_MOUSE_WHEEL = new MouseWheelAdapterAbsorber();

  static final private KeyAdapterAbsorber ABSORB_KEYS = new KeyAdapterAbsorber();
  static final private FocusAdapterConstant FOCUS_CONSTANT = new FocusAdapterConstant();

  // ---------------------------------------------------------------------------
  private WaitCursorRoutines()
  {
  }

  // ---------------------------------------------------------------------------
  static public void setWaitCursor(final Component toComponent)
  {
    if (toComponent == null)
    {
      System.err.println("toComponent is null in WaitCursorRoutines.setWaitCursor");
      return;
    }

    WaitCursorRoutines.setCursorThread(toComponent, Cursor.WAIT_CURSOR, false);
  }

  // ---------------------------------------------------------------------------
  static public void setWaitCursor(final Component toComponent, final boolean tlIncludeFocusRoutines)
  {
    if (toComponent == null)
    {
      System.err.println("toComponent is null in WaitCursorRoutines.setWaitCursor");
      return;
    }

    WaitCursorRoutines.setCursorThread(toComponent, Cursor.WAIT_CURSOR, tlIncludeFocusRoutines);
  }

  // ---------------------------------------------------------------------------
  static public void setDefaultCursor(final Component toComponent)
  {
    if (toComponent == null)
    {
      System.err.println("toComponent is null in WaitCursorRoutines.setDefaultCursor");
      return;
    }

    WaitCursorRoutines.setCursorThread(toComponent, Cursor.DEFAULT_CURSOR, false);
  }

  // ---------------------------------------------------------------------------
  static public void setDefaultCursor(final Component toComponent, final boolean tlIncludeFocusRoutines)
  {
    if (toComponent == null)
    {
      System.err.println("toComponent is null in WaitCursorRoutines.setDefaultCursor");
      return;
    }

    WaitCursorRoutines.setCursorThread(toComponent, Cursor.DEFAULT_CURSOR, tlIncludeFocusRoutines);
  }

  // ---------------------------------------------------------------------------
  static private void setCursorThread(final Component toComponent, final int tnCursorType,
                                      final boolean tlIncludeFocusRoutines)
  {
    if (EventQueue.isDispatchThread())
    {
      WaitCursorRoutines.changeCursor(toComponent, tnCursorType, tlIncludeFocusRoutines);
    }
    else
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          WaitCursorRoutines.changeCursor(toComponent, tnCursorType, tlIncludeFocusRoutines);
        }
      });
    }
  }

  // ---------------------------------------------------------------------------
  // In some cases, I was using this routine to set wait cursors while creating
  // a new JFrame or JDialog. With a JFrame, the new JFrame would appear and
  // then
  // disappear behind the calling JFrame. With a modal JDialog on Linux, the
  // JDialog
  // would appear, but the calling JFrame would have keyboard focus.
  // By now having the option to use tlIncludeFocusRoutines in the below manner
  // which, when true, only sets the glasspane to visible, the problem has been
  // resolved.
  static private void changeCursor(final Component toComponent, final int tnCursorType,
                                   final boolean tlIncludeFocusRoutines)
  {

    if (!WaitCursorRoutines.isCursorTypeOkay(tnCursorType))
    {
      return;
    }

    try
    {
      final Component loGlassPane = WaitCursorRoutines.getWindowGlassPane(toComponent);

      final Cursor loCursor = Cursor.getPredefinedCursor(tnCursorType);
      final boolean llWaitCursor = (tnCursorType == Cursor.WAIT_CURSOR);

      if (loGlassPane != null)
      {
        WaitCursorRoutines.updateMouseListeners(loGlassPane, llWaitCursor);
        WaitCursorRoutines.updateKeyListeners(loGlassPane, llWaitCursor);
        if (tlIncludeFocusRoutines)
        {
          WaitCursorRoutines.updateFocusListeners(loGlassPane, toComponent, llWaitCursor);
        }
        else
        {
          loGlassPane.setVisible(llWaitCursor);
        }

        loGlassPane.setCursor(loCursor);
      }
      else
      {
        toComponent.setCursor(loCursor);
      }
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  static private boolean isCursorTypeOkay(final int tnCursorType)
  {
    if ((tnCursorType != Cursor.WAIT_CURSOR) && (tnCursorType != Cursor.DEFAULT_CURSOR))
    {
      System.err.println("Unknown cursor type in setCursorThread of CursorRoutines.");
      return (false);
    }

    return (true);
  }

  // ---------------------------------------------------------------------------
  static private void updateFocusListeners(final Component toGlassPane, final Component toOwner,
                                           final boolean tlAddListeners)
  {
    if (tlAddListeners)
    {
      WaitCursorRoutines.FOCUS_CONSTANT.setGlassPane(toGlassPane);
      WaitCursorRoutines.FOCUS_CONSTANT.setGlassPaneOwner(toOwner);
      toGlassPane.addFocusListener(WaitCursorRoutines.FOCUS_CONSTANT);
      WaitCursorRoutines.FOCUS_CONSTANT.setGlassPaneVisibility(true);
    }
    else
    {
      WaitCursorRoutines.FOCUS_CONSTANT.setGlassPaneVisibility(false);
      WaitCursorRoutines.FOCUS_CONSTANT.resetGlassPane();
      WaitCursorRoutines.FOCUS_CONSTANT.resetGlassPaneOwner();
      toGlassPane.removeFocusListener(WaitCursorRoutines.FOCUS_CONSTANT);
    }
  }

  // ---------------------------------------------------------------------------
  static private void updateKeyListeners(final Component toGlassPane, final boolean tlAddListeners)
  {
    if (tlAddListeners)
    {
      toGlassPane.addKeyListener(WaitCursorRoutines.ABSORB_KEYS);
    }
    else
    {
      toGlassPane.removeKeyListener(WaitCursorRoutines.ABSORB_KEYS);
    }
  }

  // ---------------------------------------------------------------------------
  static private void updateMouseListeners(final Component toGlassPane, final boolean tlAddListeners)
  {
    if (tlAddListeners)
    {
      toGlassPane.addMouseListener(WaitCursorRoutines.ABSORB_MOUSE);
      toGlassPane.addMouseMotionListener(WaitCursorRoutines.ABSORB_MOUSE_MOTION);
      toGlassPane.addMouseWheelListener(WaitCursorRoutines.ABSORB_MOUSE_WHEEL);
    }
    else
    {
      toGlassPane.removeMouseListener(WaitCursorRoutines.ABSORB_MOUSE);
      toGlassPane.removeMouseMotionListener(WaitCursorRoutines.ABSORB_MOUSE_MOTION);
      toGlassPane.removeMouseWheelListener(WaitCursorRoutines.ABSORB_MOUSE_WHEEL);
    }
  }

  // ---------------------------------------------------------------------------
  static private Component getWindowGlassPane(final Component toComponent)
  {
    Component loGlassPane = null;
    if (toComponent instanceof JFrame)
    {
      loGlassPane = ((JFrame) toComponent).getGlassPane();
    }
    else if (toComponent instanceof JDialog)
    {
      loGlassPane = ((JDialog) toComponent).getGlassPane();
    }

    return (loGlassPane);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class MouseWheelAdapterAbsorber implements MouseWheelListener
{
  // ---------------------------------------------------------------------------
  @Override
  public void mouseWheelMoved(final MouseWheelEvent e)
  {
    e.consume();
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class MouseMotionAdapterAbsorber implements MouseMotionListener
{
  // ---------------------------------------------------------------------------
  @Override
  public void mouseDragged(final MouseEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseMoved(final MouseEvent e)
  {
    e.consume();
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class MouseAdapterAbsorber implements MouseListener
{
  // ---------------------------------------------------------------------------
  @Override
  public void mouseClicked(final MouseEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mousePressed(final MouseEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseReleased(final MouseEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseEntered(final MouseEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseExited(final MouseEvent e)
  {
    e.consume();
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class FocusAdapterConstant extends FocusAdapter
{
  private Component foGlassPane = null;
  private Window foGlassPaneWindowOwner = null;

  // ---------------------------------------------------------------------------
  public void setGlassPaneVisibility(final boolean tlVisible)
  {
    if (this.foGlassPane == null)
    {
      return;
    }

    this.foGlassPane.setVisible(tlVisible);

    if (tlVisible)
    {
      this.foGlassPane.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------
  public void setGlassPane(final Component toGlassPane)
  {
    this.foGlassPane = toGlassPane;
  }

  // ---------------------------------------------------------------------------
  public void resetGlassPane()
  {
    this.foGlassPane = null;
  }

  // ---------------------------------------------------------------------------
  public void resetGlassPaneOwner()
  {
    this.foGlassPaneWindowOwner = null;
  }

  // ---------------------------------------------------------------------------
  public void setGlassPaneOwner(final Component toOwner)
  {
    if (toOwner instanceof Window)
    {
      this.foGlassPaneWindowOwner = (Window) toOwner;
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  public void focusGained(final FocusEvent e)
  {
    final Dialog loDialog = this.findDialogWindow();
    if (loDialog != null)
    {
      loDialog.requestFocus();
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  public void focusLost(final FocusEvent e)
  {
    if (this.foGlassPane == null)
    {
      return;
    }

    if (this.foGlassPane.isVisible())
    {
      if (this.findDialogWindow() == null)
      {
        this.foGlassPane.requestFocus();
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Just looking for a progress monitor type dialog window. If one exists, then
  // return it.
  private Dialog findDialogWindow()
  {
    Dialog loDialog = null;

    if (this.foGlassPaneWindowOwner != null)
    {
      final Window[] loChildren = this.foGlassPaneWindowOwner.getOwnedWindows();
      final int lnCount = loChildren.length;

      for (int i = 0; ((i < lnCount) && (loDialog == null)); i++)
      {
        if (loChildren[i] instanceof Dialog)
        {
          if (((Dialog) loChildren[i]).isVisible())
          {
            loDialog = (Dialog) loChildren[i];
          }
        }
      }
    }

    return (loDialog);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class KeyAdapterAbsorber implements KeyListener
{
  // ---------------------------------------------------------------------------
  @Override
  public void keyTyped(final KeyEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void keyPressed(final KeyEvent e)
  {
    e.consume();
  }

  // ---------------------------------------------------------------------------
  @Override
  public void keyReleased(final KeyEvent e)
  {
    e.consume();
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
