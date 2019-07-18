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

import com.beowurks.BeoCommon.BaseButton;
import com.beowurks.BeoCommon.BaseDialog;
import com.beowurks.BeoCommon.BaseTabbedPane;
import com.beowurks.BeoCommon.GridBagLayoutHelper;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class LFDialog extends BaseDialog implements ActionListener
{
  private final BaseButton btnOk1 = new BaseButton();
  private final BaseButton btnCancel1 = new BaseButton();
  private final Box boxButtons1 = Box.createHorizontalBox();

  private final ButtonGroup foButtonGroup1 = new ButtonGroup();
  private final ButtonGroup foButtonGroup2 = new ButtonGroup();

  private final BaseTabbedPane tabPane1 = new BaseTabbedPane();
  private JPanel pnlLookFeels1 = null;
  private JPanel pnlThemes1 = null;

  private final String fcCurrentLookAndFeel = LFCommon.getCurrentLookAndFeel();
  private final String fcCurrentMetalTheme = LFCommon.getCurrentMetalTheme();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public LFDialog(final JFrame toFrame)
  {
    super(toFrame, "Look & Feel Options");

    try
    {
      this.jbInit();
      this.makeVisible(true);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected void jbInit() throws Exception
  {
    super.jbInit();

    this.setupPanels();
    this.setupOptionGroups();
    this.setupButtons();
    this.setupTabPanes();

    this.setupListeners();
    this.setupLayouts();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupButtons()
  {
    this.btnOk1.setToolTipText("Accept any changes to the Look & Feel");
    this.btnOk1.setText("OK");

    this.btnCancel1.setToolTipText("Cancel any changes to the Look & Feel");
    this.btnCancel1.setText("Cancel");
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupOptionGroups() throws Exception
  {
    if ((this.pnlLookFeels1 == null) || (this.pnlThemes1 == null))
    {
      throw new Exception("The panels have not been initialized in the routine setupTabPanes in LookAndFeelDialog.");
    }

    // Set up look & feel options
    final UIManager.LookAndFeelInfo[] laLookFeels = UIManager.getInstalledLookAndFeels();
    int lnCount = laLookFeels.length;
    JRadioButton loRadioDefault = null;
    for (int counter = 0; counter < lnCount; counter++)
    {
      final String lcClassName = laLookFeels[counter].getClassName();
      if (lcClassName.toLowerCase().contains("motif"))
      {
        continue;
      }

      final JRadioButton loRadio = new JRadioButton(laLookFeels[counter].getName());
      loRadio.setActionCommand(laLookFeels[counter].getClassName());
      loRadio.setSelected(this.fcCurrentLookAndFeel.compareToIgnoreCase(lcClassName) == 0);
      loRadio.setCursor(new Cursor(Cursor.HAND_CURSOR));
      if ((loRadioDefault == null) || (lcClassName.toLowerCase().contains("nimbus")))
      {
        loRadioDefault = loRadio;
      }

      this.foButtonGroup1.add(loRadio);
      this.pnlLookFeels1.add(loRadio);
    }

    loRadioDefault.setSelected((this.foButtonGroup1.getSelection() == null));

    // Set up look & feel options
    JRadioButton loRadio;
    final MetalThemeInfo[] laThemes = LFCommon.getMetalThemes();
    lnCount = laThemes.length;

    for (int i = 0; i < lnCount; ++i)
    {
      final String lcClassName = laThemes[i].fcClassName;

      loRadio = new JRadioButton(laThemes[i].fcName);
      loRadio.setActionCommand(lcClassName);
      loRadio.setSelected(this.fcCurrentMetalTheme.compareToIgnoreCase(lcClassName) == 0);
      loRadio.setCursor(new Cursor(Cursor.HAND_CURSOR));

      this.foButtonGroup2.add(loRadio);
      this.pnlThemes1.add(loRadio);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupPanels()
  {
    final Border loBorder = new CompoundBorder(new SoftBevelBorder(BevelBorder.LOWERED), new EmptyBorder(5, 5, 5, 5));

    this.pnlLookFeels1 = new JPanel();
    this.pnlLookFeels1.setLayout(new BoxLayout(this.pnlLookFeels1, BoxLayout.Y_AXIS));
    this.pnlLookFeels1.setAlignmentY(Component.TOP_ALIGNMENT);
    this.pnlLookFeels1.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.pnlLookFeels1.setBorder(loBorder);

    this.pnlThemes1 = new JPanel();
    this.pnlThemes1.setLayout(new BoxLayout(this.pnlThemes1, BoxLayout.Y_AXIS));
    this.pnlThemes1.setAlignmentY(Component.TOP_ALIGNMENT);
    this.pnlThemes1.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.pnlThemes1.setBorder(loBorder);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupTabPanes() throws Exception
  {
    if ((this.pnlLookFeels1 == null) || (this.pnlThemes1 == null))
    {
      throw new Exception("The panels have not been initialized in the routine setupTabPanes in LookAndFeelDialog.");
    }

    if (this.foButtonGroup1.getSelection() == null)
    {
      throw new Exception(
              "The button groups have not been initialized in the routine setupTabPanes in LookAndFeelDialog.");
    }

    this.tabPane1.add("Look & Feels", this.pnlLookFeels1);
    this.tabPane1.add("Themes", this.pnlThemes1);

    this.setMetalTabVisibility();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupListeners()
  {
    this.btnOk1.addActionListener(this);
    this.btnCancel1.addActionListener(this);

    Enumeration<?> loE;

    loE = this.foButtonGroup1.getElements();
    while (loE.hasMoreElements())
    {
      final Object loObject = loE.nextElement();
      if (loObject instanceof JRadioButton)
      {
        ((JRadioButton) loObject).addActionListener(this);
      }
    }

    loE = this.foButtonGroup2.getElements();
    while (loE.hasMoreElements())
    {
      final Object loObject = loE.nextElement();
      if (loObject instanceof JRadioButton)
      {
        ((JRadioButton) loObject).addActionListener(this);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupLayouts()
  {
    this.boxButtons1.add(this.btnOk1, null);
    this.boxButtons1.add(this.btnCancel1, null);

    final GridBagLayoutHelper loGridBag = new GridBagLayoutHelper();

    this.getContentPane().setLayout(loGridBag);

    this.getContentPane().add(this.tabPane1,
            loGridBag.getConstraint(0, 3, GridBagConstraints.CENTER, GridBagConstraints.BOTH));
    this.getContentPane().add(this.boxButtons1,
            loGridBag.getConstraint(0, 5, GridBagConstraints.CENTER, GridBagConstraints.NONE));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Only the Metal Look & Feel has themes.
  private void setMetalTabVisibility()
  {
    final String lcLF = this.foButtonGroup1.getSelection().getActionCommand();

    this.tabPane1.setEnabledAt(1, LFCommon.isMetalLookAndFeel(lcLF));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected void removeListeners()
  {
    super.removeListeners();

    this.btnOk1.removeActionListener(this);
    this.btnCancel1.removeActionListener(this);

    Enumeration<?> loE;

    loE = this.foButtonGroup1.getElements();
    while (loE.hasMoreElements())
    {
      final Object loObject = loE.nextElement();
      if (loObject instanceof JRadioButton)
      {
        ((JRadioButton) loObject).removeActionListener(this);
      }
    }

    loE = this.foButtonGroup2.getElements();
    while (loE.hasMoreElements())
    {
      final Object loObject = loE.nextElement();
      if (loObject instanceof JRadioButton)
      {
        ((JRadioButton) loObject).removeActionListener(this);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ActionListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    if (e.getSource() == this.btnCancel1)
    {
      // If cancelled, make sure that you reset all the settings back to the way
      // it was.
      LFCommon.setLookFeel(this.fcCurrentLookAndFeel, this.fcCurrentMetalTheme, this, false);
      this.closeWindow();
    }
    else if (e.getSource() == this.btnOk1)
    {
      final String lcLF = this.foButtonGroup1.getSelection().getActionCommand();
      final String lcTheme = this.foButtonGroup2.getSelection().getActionCommand();

      LFCommon.setLookFeel(lcLF, lcTheme, this, true);

      this.closeWindow();
    }
    else
    {
      final Object loObject = e.getSource();
      if (loObject instanceof JRadioButton)
      {
        final String lcLF = this.foButtonGroup1.getSelection().getActionCommand();
        final String lcTheme = this.foButtonGroup2.getSelection().getActionCommand();

        LFCommon.setLookFeel(lcLF, lcTheme, this, false);

        if (((JRadioButton) loObject).getParent() == this.pnlLookFeels1)
        {
          this.setMetalTabVisibility();
        }
      }
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
