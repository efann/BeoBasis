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
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.Color;
import java.util.Arrays;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class LFMTOceanGreen extends OceanTheme
{
  // I got these colors by taking them from OceanTheme and
  // leaving the Red unchanged and making the Green & Blue equal.
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(0x63BFBF);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(0xA3CCCC);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(0xB8E5E5);
  private static final ColorUIResource SECONDARY1 = new ColorUIResource(0x7A9999);
  private static final ColorUIResource SECONDARY2 = new ColorUIResource(0xB8E5E5);
  private static final ColorUIResource SECONDARY3 = new ColorUIResource(0xEEEEEE);

  // ---------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Ocean Green");
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFMTOceanGreen.PRIMARY1);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFMTOceanGreen.PRIMARY2);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFMTOceanGreen.PRIMARY3);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary1()
  {
    return (LFMTOceanGreen.SECONDARY1);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary2()
  {
    return (LFMTOceanGreen.SECONDARY2);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary3()
  {
    return (LFMTOceanGreen.SECONDARY3);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void addCustomEntriesToTable(final UIDefaults toTable)
  {
    super.addCustomEntriesToTable(toTable);

    final java.util.List<Object> laButtonGradient = Arrays.asList(new Object[]{Float.valueOf(.3f), Float.valueOf(0f),
        new ColorUIResource(0xCCFFFF), this.getWhite(), new ColorUIResource(0xAAFFFF)});

    toTable.put("Button.gradient", laButtonGradient);
    toTable.put("CheckBoxMenuItem.gradient", laButtonGradient);
    toTable.put("InternalFrame.activeTitleGradient", laButtonGradient);
    toTable.put("CheckBox.gradient", laButtonGradient);
    toTable.put("RadioButton.gradient", laButtonGradient);
    toTable.put("RadioButtonMenuItem.gradient", laButtonGradient);
    toTable.put("ScrollBar.gradient", laButtonGradient);
    toTable.put("ToggleButton.gradient", laButtonGradient);

    toTable.put("TabbedPane.unselectedBackground", this.getPrimary3());
    toTable.put("TabbedPane.borderHightlightColor", Color.gray);
    toTable.put("TabbedPane.contentAreaColor", this.getControl());
    toTable.put("TabbedPane.selected", this.getControl());
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
