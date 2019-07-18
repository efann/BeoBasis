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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class GridBagLayoutHelper extends GridBagLayout
{
  private final GridBagConstraints foConstraints = new GridBagConstraints();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public GridBagLayoutHelper()
  {
    this.foConstraints.insets = new Insets(4, 4, 4, 4);

    this.foConstraints.ipadx = 2;
    this.foConstraints.ipady = 2;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setInsets(final int tnTop, final int tnLeft, final int tnBottom, final int tnRight)
  {
    this.foConstraints.insets.top = tnTop;
    this.foConstraints.insets.left = tnLeft;
    this.foConstraints.insets.bottom = tnBottom;
    this.foConstraints.insets.right = tnRight;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setInsetDefaults()
  {
    this.foConstraints.insets.top = 4;
    this.foConstraints.insets.left = 4;
    this.foConstraints.insets.bottom = 4;
    this.foConstraints.insets.right = 4;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public GridBagConstraints getConstraint(final int tnCol, final int tnRow, final int tnAnchor, final int tnFill)
  {
    return (this.getConstraint(tnCol, tnRow, 1, 1, tnAnchor, tnFill));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public GridBagConstraints getConstraint(final int tnCol, final int tnRow, final int tnWidth, final int tnHeight,
                                          final int tnAnchor, final int tnFill)
  {
    this.foConstraints.gridx = tnCol;
    this.foConstraints.gridy = tnRow;
    this.foConstraints.gridwidth = tnWidth;
    this.foConstraints.gridheight = tnHeight;
    this.foConstraints.anchor = tnAnchor;

    final double lnWeightX = 1.0;
    final double lnWeightY = 1.0;

    switch (tnFill)
    {
      case GridBagConstraints.HORIZONTAL:
        this.foConstraints.weightx = lnWeightX;
        this.foConstraints.weighty = 0.0;
        break;
      case GridBagConstraints.VERTICAL:
        this.foConstraints.weighty = lnWeightY;
        this.foConstraints.weightx = 0.0;
        break;
      case GridBagConstraints.BOTH:
        this.foConstraints.weightx = lnWeightX;
        this.foConstraints.weighty = lnWeightY;
        break;
      case GridBagConstraints.NONE:
        this.foConstraints.weightx = 0.0;
        this.foConstraints.weighty = 0.0;
        break;
    }
    this.foConstraints.fill = tnFill;

    return (this.foConstraints);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
