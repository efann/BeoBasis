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

import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

// Class SmallToggleButton unchanged from chapter 12
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class SmallToggleButton extends JToggleButton implements ItemListener
{
  protected Border foRaised = new SoftBevelBorder(BevelBorder.RAISED);
  protected Border foLowered = new SoftBevelBorder(BevelBorder.LOWERED);
  protected Insets foInset = new Insets(4, 4, 4, 4);

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public SmallToggleButton(final boolean tlSelected, final ImageIcon toImgUnselected, final ImageIcon toImgSelected,
                           final String tcTip)
  {
    super(toImgUnselected, tlSelected);

    this.setHorizontalAlignment(SwingConstants.CENTER);
    this.setBorder(tlSelected ? this.foLowered : this.foRaised);
    this.setMargin(this.foInset);
    this.setToolTipText(tcTip);
    this.setRequestFocusEnabled(false);
    this.setSelectedIcon(toImgSelected);
    this.addItemListener(this);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public float getAlignmentY()
  {
    return (0.5f);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Overridden for 1.4 bug fix
  @Override
  public Insets getInsets()
  {
    return (this.foInset);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public Border getBorder()
  {
    return (this.isSelected() ? this.foLowered : this.foRaised);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void itemStateChanged(final ItemEvent e)
  {
    this.setBorder(this.isSelected() ? this.foLowered : this.foRaised);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
