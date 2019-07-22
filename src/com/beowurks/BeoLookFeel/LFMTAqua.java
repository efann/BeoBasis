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
 * This class describes a theme using "blue-green" colors.
 * <p/>
 * 1.9 07/26/04
 *
 * @author Steve Wilson
 *         <p/>
 *         Modified by Eddie Fann, August, 2005
 *         <p/>
 *         From the file, jdk1.5.0_04/demo/jfc/SwingSet2/src/AquaTheme.java
 */
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class LFMTAqua extends DefaultMetalTheme
{
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(102, 153, 153);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(128, 192, 192);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(159, 235, 235);

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Aqua");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFMTAqua.PRIMARY1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFMTAqua.PRIMARY2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFMTAqua.PRIMARY3);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
