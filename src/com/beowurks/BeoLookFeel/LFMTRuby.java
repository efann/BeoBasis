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
 * This class describes a theme using red colors.
 *
 * @author Jeff Dinkins
 *         <p/>
 *         Modified by Eddie Fann, August, 2005
 *         <p/>
 *         From the file, jdk1.5.0_04/demo/jfc/SwingSet2/src/RubyTheme.java
 * @version 1.9 07/26/04
 */
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class LFMTRuby extends DefaultMetalTheme
{
  private static final ColorUIResource PRIMARY1 = new ColorUIResource(80, 10, 22);
  private static final ColorUIResource PRIMARY2 = new ColorUIResource(193, 10, 44);
  private static final ColorUIResource PRIMARY3 = new ColorUIResource(244, 10, 66);

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return ("Ruby");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary1()
  {
    return (LFMTRuby.PRIMARY1);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary2()
  {
    return (LFMTRuby.PRIMARY2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected ColorUIResource getPrimary3()
  {
    return (LFMTRuby.PRIMARY3);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
