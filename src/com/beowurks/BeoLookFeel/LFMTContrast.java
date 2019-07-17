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

package com.beowurks.BeoLookFeel;

import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class describes a higher-contrast Metal Theme.
 *
 * @author Michael C. Albers
 *         <p/>
 *         Modified by Eddie Fann, August, 2005
 *         <p/>
 *         From the file, jdk1.5.0_04/demo/jfc/SwingSet2/src/ContrastTheme.java
 * @version 1.10 07/26/04
 */

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class LFMTContrast extends DefaultMetalTheme
{
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(0, 0, 0);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(204, 204, 204);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(255, 255, 255);

  private final static ColorUIResource SECONDARY2 = new ColorUIResource(204, 204, 204);
  private final static ColorUIResource SECONDARY3 = new ColorUIResource(255, 255, 255);

  private static final ColorUIResource PRIMARYHIGHLIGHT = new ColorUIResource(102, 102, 102);

  // ---------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Contrast");
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFMTContrast.PRIMARY1);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFMTContrast.PRIMARY2);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFMTContrast.PRIMARY3);
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getPrimaryControlHighlight()
  {
    return (LFMTContrast.PRIMARYHIGHLIGHT);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary2()
  {
    return (LFMTContrast.SECONDARY2);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary3()
  {
    return (LFMTContrast.SECONDARY3);
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getControlHighlight()
  {
    return (super.getSecondary3());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getFocusColor()
  {
    return (this.getBlack());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getTextHighlightColor()
  {
    return (this.getBlack());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getHighlightedTextColor()
  {
    return (this.getWhite());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getMenuSelectedBackground()
  {
    return (this.getBlack());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getMenuSelectedForeground()
  {
    return (this.getWhite());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getAcceleratorForeground()
  {
    return (this.getBlack());
  }

  // ---------------------------------------------------------------------------
  @Override
  public ColorUIResource getAcceleratorSelectedForeground()
  {
    return (this.getWhite());
  }

  // ---------------------------------------------------------------------------
  @Override
  public void addCustomEntriesToTable(final UIDefaults toTable)
  {
    super.addCustomEntriesToTable(toTable);

    final Border blackLineBorder = new BorderUIResource(new LineBorder(this.getBlack()));

    final Object textBorder = new BorderUIResource(new CompoundBorder(blackLineBorder, new BasicBorders.MarginBorder()));

    toTable.put("ToolTip.border", blackLineBorder);
    toTable.put("TitledBorder.border", blackLineBorder);

    toTable.put("TextField.border", textBorder);
    toTable.put("PasswordField.border", textBorder);
    toTable.put("TextArea.border", textBorder);
    toTable.put("TextPane.border", textBorder);
    toTable.put("EditorPane.border", textBorder);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
