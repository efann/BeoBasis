/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java Swing programs.
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
 * This class describes a theme using glowing green colors.
 *
 * @author Jeff Dinkins
 * <p/>
 * Modified by Eddie Fann, August, 2005
 * @version 1.9 07/26/04
 */
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class LFEmerald extends DefaultMetalTheme
{
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(51, 142, 71);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(102, 193, 122);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(153, 244, 173);

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Emerald");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFEmerald.PRIMARY1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFEmerald.PRIMARY2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFEmerald.PRIMARY3);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
