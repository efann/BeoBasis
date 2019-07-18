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

package com.beowurks.BeoLookFeel;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class describes a theme using gray colors.
 * <p/>
 * 1.9 07/26/04
 *
 * @author Steve Wilson
 *         <p/>
 *         Modified by Eddie Fann, August, 2005
 *         <p/>
 *         From the file, jdk1.5.0_04/demo/jfc/SwingSet2/src/CharcoalTheme.java
 */
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class LFMTCharcoal extends DefaultMetalTheme
{
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(66, 33, 66);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(90, 86, 99);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(99, 99, 99);

  private static final ColorUIResource SECONDARY1 = new ColorUIResource(0, 0, 0);
  private static final ColorUIResource SECONDARY2 = new ColorUIResource(51, 51, 51);
  private static final ColorUIResource SECONDARY3 = new ColorUIResource(102, 102, 102);

  private static final ColorUIResource COLORBLACK = new ColorUIResource(222, 222, 222);
  private static final ColorUIResource COLORWHITE = new ColorUIResource(0, 0, 0);

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Charcoal");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFMTCharcoal.PRIMARY1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFMTCharcoal.PRIMARY2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFMTCharcoal.PRIMARY3);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary1()
  {
    return (LFMTCharcoal.SECONDARY1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary2()
  {
    return (LFMTCharcoal.SECONDARY2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getSecondary3()
  {
    return (LFMTCharcoal.SECONDARY3);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getBlack()
  {
    return (LFMTCharcoal.COLORBLACK);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getWhite()
  {
    return (LFMTCharcoal.COLORWHITE);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
