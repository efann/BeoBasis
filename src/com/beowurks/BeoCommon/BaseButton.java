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

import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class BaseButton extends JButton
{
  public static final int BASE_WIDTH = 90;
  public static final int BASE_HEIGHT = 24;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public BaseButton()
  {

    this.resetDimensions(BaseButton.BASE_WIDTH, BaseButton.BASE_HEIGHT);

    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  // ---------------------------------------------------------------------------
  public BaseButton(final int tnWidth, final int tnHeight)
  {

    this.resetDimensions(tnWidth, tnHeight);

    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  // ---------------------------------------------------------------------------
  private void resetDimensions(final int tnWidth, final int tnHeight)
  {
    // Otherwise, you'll have a large blank area on the left and right of the button face.
    this.setBorder(new EmptyBorder(2, 2, 2, 2));

    final Dimension ldButtonSize = new Dimension(tnWidth, tnHeight);

    this.setMinimumSize(ldButtonSize);
    this.setPreferredSize(ldButtonSize);
    // Don't get rid of the setMaximumSize call. Otherwise, it appears that
    // the buttons do some sort of autosize.
    this.setMaximumSize(ldButtonSize);

    // If you want the buttons to work in Motif you have to do the following:
    // However, there's some other quirky behaviour with Motif, so for now
    // we won't be using it. No one's quite why setDefaultCapable fixes
    // the incredible shrinking button problem in Motif.
    // this.setDefaultCapable(false);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
