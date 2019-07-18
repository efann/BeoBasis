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

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTabbedPane;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BaseTabbedPane extends JTabbedPane implements MouseMotionListener, MouseListener, ComponentListener
{
  private final Cursor foCursorDefault = new Cursor(Cursor.DEFAULT_CURSOR);
  private final Cursor foCursorHand = new Cursor(Cursor.HAND_CURSOR);

  private Rectangle[] faTabRectangle = null;
  private int fnTabCount;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTabbedPane()
  {

    this.setupListeners();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTabbedPane(final int tabPlacement)
  {
    super(tabPlacement);

    this.setupListeners();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTabbedPane(final int tabPlacement, final int tabLayoutPolicy)
  {
    super(tabPlacement, tabLayoutPolicy);

    this.setupListeners();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupListeners()
  {
    this.addMouseMotionListener(this);
    this.addMouseListener(this);
    this.addComponentListener(this);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void initializeTabRectangleArray(final boolean tlForce)
  {
    if (this.faTabRectangle != null)
    {
      if (!tlForce)
      {
        return;
      }
    }

    this.fnTabCount = this.getTabCount();
    final int lnTabs = this.fnTabCount;
    this.faTabRectangle = new Rectangle[lnTabs];

    for (int i = 0; i < lnTabs; ++i)
    {
      this.faTabRectangle[i] = this.getBoundsAt(i);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface MouseMotionListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseDragged(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseMoved(final MouseEvent e)
  {
    if (this.faTabRectangle == null)
    {
      this.initializeTabRectangleArray(false);
    }

    boolean llInTabBounds = false;
    final int lnTabs = this.fnTabCount;
    final int lnX = e.getX();
    final int lnY = e.getY();

    for (int i = 0; i < lnTabs; ++i)
    {
      if (this.faTabRectangle[i].contains(lnX, lnY))
      {
        llInTabBounds = true;
        break;
      }
    }

    if (llInTabBounds)
    {
      if (this.getCursor().getType() != Cursor.HAND_CURSOR)
      {
        this.setCursor(this.foCursorHand);
      }
    }
    else
    {
      if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR)
      {
        this.setCursor(this.foCursorDefault);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface MouseListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseClicked(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mousePressed(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseReleased(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseEntered(final MouseEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void mouseExited(final MouseEvent e)
  {
    // Otherwise, sometimes the cursor will remain a hand over, say, grids in
    // the JTabbedPane.
    if (this.getCursor().getType() != Cursor.DEFAULT_CURSOR)
    {
      this.setCursor(this.foCursorDefault);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ComponentListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void componentHidden(final ComponentEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void componentMoved(final ComponentEvent e)
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // If you don't do this, and the tab pane contains an image and the frame
  // is resized, then the cursor while hovering over the tab will not change
  // to a hand.
  @Override
  public void componentResized(final ComponentEvent e)
  {
    this.initializeTabRectangleArray(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void componentShown(final ComponentEvent e)
  {
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
