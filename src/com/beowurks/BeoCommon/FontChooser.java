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

package com.beowurks.BeoCommon;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class FontChooser implements ChangeListener, ListSelectionListener, ItemListener
{
  private final Window foWindow;
  private final Font foFont;

  private JSpinner spnFontSize1;

  private final JScrollPane scrFontNames1 = new JScrollPane();
  private final JList lstFontNames1 = new JList(GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getAvailableFontFamilyNames());
  private final JPanel pnlComponents = new JPanel();

  private final JCheckBox chkBold1 = new JCheckBox();
  private final JCheckBox chkItalic1 = new JCheckBox();

  private final JTextArea txtPreview1 = new JTextArea("Preview of Font");

  private int fnMaxSpinner = 100;

  // ---------------------------------------------------------------------------------------------------------------------
  public FontChooser(final Window toWindow, final Font toFont)
  {

    this.foWindow = toWindow;
    this.foFont = toFont;

    try
    {
      this.jbInit();
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public FontChooser(final Window toWindow, final Font toFont, final int tnMaxSpinner)
  {

    this.foWindow = toWindow;
    this.foFont = toFont;
    this.fnMaxSpinner = tnMaxSpinner;

    try
    {
      this.jbInit();
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void jbInit() throws Exception
  {
    this.setupScrollPanels();
    this.setupSpinners();
    this.setupTextBoxes();
    this.setupCheckBoxes();
    this.setupListeners();
    this.setupLayouts();

    // This will only be scrolled to the correct display value
    // after the JList is added to the JScollPanel.
    this.lstFontNames1.setSelectedValue(this.foFont.getName(), true);

    this.resetPreviewText();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public String getFontName()
  {
    // If, for some reason, nothing has been selected, then just return
    // the Arial font.
    if (this.lstFontNames1.isSelectionEmpty())
    {
      return ("Arial");
    }

    return (this.lstFontNames1.getSelectedValue().toString());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public int getFontSize()
  {
    final Object loValue = this.spnFontSize1.getValue();

    int lnValue;

    try
    {
      lnValue = Integer.parseInt(loValue.toString());
    }
    catch (final Exception e)
    {
      lnValue = 1;
    }

    return (lnValue);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public int getFontStyle()
  {
    int lnStyle = Font.PLAIN;

    if (this.chkBold1.isSelected())
    {
      lnStyle |= Font.BOLD;
    }

    if (this.chkItalic1.isSelected())
    {
      lnStyle |= Font.ITALIC;
    }

    return (lnStyle);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupCheckBoxes()
  {
    this.chkBold1.setText("Bold");
    this.chkItalic1.setText("Italic");

    this.chkBold1.setSelected(this.foFont.isBold());
    this.chkItalic1.setSelected(this.foFont.isItalic());

    this.chkBold1.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.chkItalic1.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupLayouts()
  {
    this.scrFontNames1.getViewport().add(this.lstFontNames1, null);

    final GridBagLayoutHelper loGrid = new GridBagLayoutHelper();
    loGrid.setInsets(4, 4, 4, 4);
    this.pnlComponents.setLayout(loGrid);

    this.pnlComponents.add(this.scrFontNames1,
        loGrid.getConstraint(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
    this.pnlComponents.add(this.spnFontSize1,
        loGrid.getConstraint(0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));
    this.pnlComponents.add(this.chkBold1, loGrid.getConstraint(0, 2, GridBagConstraints.WEST, GridBagConstraints.NONE));
    this.pnlComponents.add(this.chkItalic1,
        loGrid.getConstraint(0, 3, GridBagConstraints.WEST, GridBagConstraints.NONE));
    this.pnlComponents.add(this.txtPreview1,
        loGrid.getConstraint(0, 4, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupListeners()
  {
    this.chkBold1.addItemListener(this);
    this.chkItalic1.addItemListener(this);

    this.spnFontSize1.addChangeListener(this);
    this.lstFontNames1.addListSelectionListener(this);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupScrollPanels()
  {
    final Dimension ldScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension ldSize = new Dimension((int) (ldScreenSize.width * 0.25), (int) (ldScreenSize.height * 0.25));

    this.scrFontNames1.setPreferredSize(ldSize);
    this.scrFontNames1.setMaximumSize(ldSize);
    this.scrFontNames1.setMinimumSize(ldSize);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupSpinners()
  {
    this.spnFontSize1 = new JSpinner(new SpinnerNumberModel(this.foFont.getSize(), 1, this.fnMaxSpinner, 1));

    final Dimension ldSize = new Dimension(60, (int) (this.spnFontSize1.getPreferredSize().getHeight()));

    this.spnFontSize1.setPreferredSize(ldSize);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void setupTextBoxes()
  {
    final Dimension ldScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final int lnHeight = (int) this.txtPreview1.getPreferredSize().getHeight() * 5;
    final Dimension ldSize = new Dimension((int) (ldScreenSize.width * 0.30), lnHeight);

    this.txtPreview1.setPreferredSize(ldSize);
    this.txtPreview1.setLineWrap(true);
    this.txtPreview1.setEditable(false);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void resetPreviewText()
  {
    // You know, you could use deriveFont. But it also creates a new Font. So,
    // you might as well as use the below technique.
    this.txtPreview1.setFont(new Font(this.getFontName(), this.getFontStyle(), this.getFontSize()));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public boolean showDialog()
  {
    final int lnResults = JOptionPane.showConfirmDialog(this.foWindow, new Object[]{this.pnlComponents},
        "Font Chooser", JOptionPane.OK_CANCEL_OPTION);

    return (lnResults == JOptionPane.OK_OPTION);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ItemListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void itemStateChanged(final ItemEvent e)
  {
    this.resetPreviewText();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ChangeListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void stateChanged(final ChangeEvent e)
  {
    this.resetPreviewText();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface ListSelectionListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void valueChanged(final ListSelectionEvent e)
  {
    this.resetPreviewText();
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
