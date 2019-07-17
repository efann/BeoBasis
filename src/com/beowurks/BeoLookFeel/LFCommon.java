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

import com.beowurks.BeoCommon.Util;

import java.awt.Frame;
import java.awt.Window;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public final class LFCommon
{
  static private MetalThemeInfo[] faThemeInfos = null;
  // Get rid of lazy initialization error from FindBugs.
  static private final Object foTempSynchronized = new Object();

  // ---------------------------------------------------------------------------
  private LFCommon()
  {
  }

  // ---------------------------------------------------------------------------
  static public void setLookFeel(final String tcLookFeelClass, final String tcMetalTheme, final Window toWindow,
                                 final boolean tlAllComponents)
  {
    // When you change Look And Feels (L&F), there are problems when setting
    // the setDefaultLookAndFeelDecorated routine. If you've already
    // set this routine, then when dynamically changing to a L&F that doesn't
    // support the decorations, then the title bar disappears.
    // So for now, I'm leaving out setDefaultLookAndFeelDecorated.
    // boolean llDecorated =
    // UIManager.getLookAndFeel().getSupportsWindowDecorations();
    // JFrame.setDefaultLookAndFeelDecorated(true);
    // JDialog.setDefaultLookAndFeelDecorated(true);

    // From
    // http://developer.apple.com/documentation/Java/Conceptual/Java14Development/04-JavaUIToolkits/JavaUIToolkits.html
    // On the property of "apple.laf.useScreenMenuBar"
    // This property can have a value of true or false. By default it is false,
    // which means
    // menus are in the window instead of the menu bar. When set to true, the
    // Java runtime
    // moves any given JFrame�s JMenuBar to the top of the screen, where
    // Macintosh users expect it.

    // Allow the look & feel for the Mac to remain the default.
    if (Util.isMacintosh())
    {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      return;
    }

    try
    {
      if (tcLookFeelClass.isEmpty())
      {
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        UIManager.setLookAndFeel(new MetalLookAndFeel());
      }
      else
      {
        if (LFCommon.isMetalLookAndFeel(tcLookFeelClass))
        {
          final String lcMetalTheme = (!tcMetalTheme.isEmpty()) ? tcMetalTheme : "DefaultMetalTheme";
          final Class<?> loClass = LFCommon.loadClass(lcMetalTheme);
          MetalLookAndFeel.setCurrentTheme((MetalTheme) (loClass.newInstance()));
        }

        try
        {
          UIManager.setLookAndFeel(tcLookFeelClass);
        }
        catch (final Exception ignore)
        {
        }
      }

      if (tlAllComponents)
      {
        final Frame[] laFrames = Frame.getFrames();

        for (final Frame loFrame : laFrames)
        {
          SwingUtilities.updateComponentTreeUI(loFrame);
        }
      }

      if (toWindow != null)
      {
        SwingUtilities.updateComponentTreeUI(toWindow);
      }
    }
    catch (final Exception loErr)
    {
      Util.errorMessage(toWindow, "There was a problem with resetting the Look & Feel.\n\n" + loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  static public boolean isMetalLookAndFeel(final LookAndFeel toLookAndFeel)
  {
    return (LFCommon.isMetalLookAndFeel(toLookAndFeel.getClass().getName()));
  }

  // ---------------------------------------------------------------------------
  static public boolean isMetalLookAndFeel(final String tcLookAndFeel)
  {
    return (tcLookAndFeel.toLowerCase().contains("metal"));
  }

  // ---------------------------------------------------------------------------
  static private Class<?> loadClass(final String tcClassName) throws ClassNotFoundException
  {
    return (Class.forName(tcClassName, true, Thread.currentThread().getContextClassLoader()));
  }

  // ---------------------------------------------------------------------------
  static public String getCurrentLookAndFeel()
  {
    return (UIManager.getLookAndFeel().getClass().getName());
  }

  // ---------------------------------------------------------------------------
  static public String getCurrentMetalTheme()
  {
    return (MetalLookAndFeel.getCurrentTheme().getClass().getName());
  }

  // ---------------------------------------------------------------------------
  static protected MetalThemeInfo[] getMetalThemes()
  {
    /*
     * Gets rid of the following FindBug problem: Incorrect lazy initialization
     * and update of static field com.beowurks.BeoLookFeel.LFCommon.faThemeInfos
     */
    synchronized (LFCommon.foTempSynchronized)
    {
      if (LFCommon.faThemeInfos == null)
      {
        LFCommon.faThemeInfos = new MetalThemeInfo[8];

        try
        {
          DefaultMetalTheme loTheme;

          LFCommon.faThemeInfos[0] = new MetalThemeInfo();
          loTheme = new LFMTAqua();
          LFCommon.faThemeInfos[0].fcName = loTheme.getName();
          LFCommon.faThemeInfos[0].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[1] = new MetalThemeInfo();
          loTheme = new LFMTCharcoal();
          LFCommon.faThemeInfos[1].fcName = loTheme.getName();
          LFCommon.faThemeInfos[1].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[2] = new MetalThemeInfo();
          loTheme = new LFMTContrast();
          LFCommon.faThemeInfos[2].fcName = loTheme.getName();
          LFCommon.faThemeInfos[2].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[3] = new MetalThemeInfo();
          loTheme = new LFEmerald();
          LFCommon.faThemeInfos[3].fcName = loTheme.getName();
          LFCommon.faThemeInfos[3].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[4] = new MetalThemeInfo();
          loTheme = new OceanTheme();
          LFCommon.faThemeInfos[4].fcName = loTheme.getName();
          LFCommon.faThemeInfos[4].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[5] = new MetalThemeInfo();
          loTheme = new LFMTOceanGreen();
          LFCommon.faThemeInfos[5].fcName = loTheme.getName();
          LFCommon.faThemeInfos[5].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[6] = new MetalThemeInfo();
          loTheme = new LFMTRuby();
          LFCommon.faThemeInfos[6].fcName = loTheme.getName();
          LFCommon.faThemeInfos[6].fcClassName = loTheme.getClass().getName();

          LFCommon.faThemeInfos[7] = new MetalThemeInfo();
          loTheme = new DefaultMetalTheme();
          LFCommon.faThemeInfos[7].fcName = loTheme.getName();
          LFCommon.faThemeInfos[7].fcClassName = loTheme.getClass().getName();
        }
        catch (final Exception loErr)
        {
          Util.errorMessage(
                  null,
                  "There was an error in initializing LFCommon.faThemeInfos. Notify support@beowurks.com.\n\n"
                          + loErr.getMessage());
        }
      }
    }
    return (LFCommon.faThemeInfos);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class MetalThemeInfo
{
  protected String fcName;
  protected String fcClassName;
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
