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

package com.beowurks.BeoCommon.Dialogs.About;

import com.beowurks.BeoCommon.BaseButton;
import com.beowurks.BeoCommon.BaseDialog;
import com.beowurks.BeoCommon.Dialogs.JEditorPaneFixHTML;
import com.beowurks.BeoCommon.GridBagLayoutHelper;
import com.beowurks.BeoCommon.Util;
import com.beowurks.BeoTable.SortingTable;
import com.beowurks.BeoTable.SortingTableModel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class DialogAbout extends BaseDialog implements ActionListener, MouseListener
{
  private final static int PADDING = 8;

  private final JEditorPaneFixHTML txtTitle1 = new JEditorPaneFixHTML();
  private final JEditorPaneFixHTML txtCopyright1 = new JEditorPaneFixHTML();
  private final JEditorPaneFixHTML txtLicense1 = new JEditorPaneFixHTML();

  private final JLabel lblLogo1 = new JLabel();

  private final BaseButton btnClose1 = new BaseButton(76, 30);

  private final JScrollPane scrSystemInfo1 = new JScrollPane();
  private final SortingTable grdSystemInfo1 = new SortingTable();

  private final Dimension foStandardGridDimension = new Dimension(450, 300);

  private final IAbout foAbout;

  // ---------------------------------------------------------------------------
  public DialogAbout(final JFrame toFrame, final IAbout toAbout)
  {
    super(toFrame, "About");

    this.foAbout = toAbout;

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

  // ---------------------------------------------------------------------------
  @Override
  protected void jbInit() throws Exception
  {
    super.jbInit();

    this.setupLabels();
    this.setupTextBoxes();
    this.setupButtons();

    this.setupGrids();
    this.setupGridDataAndSetColumnWidths();

    this.setupListeners();
    this.setupScrollPanes();
    this.setupLayouts();

    Util.addEscapeListener(this);
  }

  // ---------------------------------------------------------------------------
  private void setupLayouts()
  {
    final GridBagLayoutHelper loHelperNoInset = new GridBagLayoutHelper();
    final GridBagLayoutHelper loHelperInset = new GridBagLayoutHelper();

    loHelperNoInset.setInsets(0, 0, 0, 0);
    loHelperInset.setInsets(DialogAbout.PADDING, DialogAbout.PADDING, DialogAbout.PADDING, DialogAbout.PADDING);

    this.getContentPane().setLayout(new GridBagLayout());

    final JPanel loLogoPanel = new JPanel(new GridBagLayout());
    final JPanel loInfoPanel = new JPanel(new GridBagLayout());

    loLogoPanel.setBorder(null);
    loLogoPanel.add(this.lblLogo1, loHelperInset.getConstraint(0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE));
    loLogoPanel.add(this.txtLicense1.getScrollPane(), loHelperNoInset.getConstraint(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));

    loInfoPanel.add(this.txtTitle1.getScrollPane(), loHelperNoInset.getConstraint(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
    loInfoPanel.add(this.txtCopyright1.getScrollPane(), loHelperNoInset.getConstraint(0, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
    loInfoPanel.add(this.scrSystemInfo1, loHelperInset.getConstraint(0, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH));

    this.getContentPane().add(loLogoPanel, loHelperNoInset.getConstraint(0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE));
    this.getContentPane().add(loInfoPanel, loHelperNoInset.getConstraint(1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH));
    this.getContentPane().add(this.btnClose1, loHelperInset.getConstraint(0, 1, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE));

  }

  // ---------------------------------------------------------------------------
  private void setupScrollPanes()
  {
    this.scrSystemInfo1.setPreferredSize(this.foStandardGridDimension);
    this.scrSystemInfo1.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    this.scrSystemInfo1.getViewport().add(this.grdSystemInfo1, null);

    this.txtTitle1.setNeverScroll();
    this.txtCopyright1.setNeverScroll();
    this.txtLicense1.setNeverScroll();

    this.txtTitle1.getScrollPane().setBorder(null);
    this.txtCopyright1.getScrollPane().setBorder(null);
    this.txtLicense1.getScrollPane().setBorder(null);

    // Trying to control the size of the license text so that it remains directly under
    // the logo. Hopefully it will never need a height greater than 150 which is about 7.5 lines.
    final Dimension loDim = new Dimension(this.foAbout.getLogo().getWidth(), 150);
    final JScrollPane loPane = this.txtLicense1.getScrollPane();
    loPane.setPreferredSize(loDim);
    loPane.setMaximumSize(loDim);
    loPane.setMinimumSize(loDim);

  }

  // ---------------------------------------------------------------------------
  private void setupGrids()
  {
    // -------------------------------
    // Setup System Info grid
    // -------------------------------
    this.grdSystemInfo1.setupColumns(new Object[][]{{"Label", ""}, {"Value", ""},});
    this.grdSystemInfo1.setupHeaderRenderer();

    this.grdSystemInfo1.setBackground(Util.getButtonBackground());
    this.grdSystemInfo1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
  }

  // ---------------------------------------------------------------------------
  private void setupButtons()
  {
    this.btnClose1.setText("Close");
    this.btnClose1.setMnemonic('C');
    this.btnClose1.setToolTipText("Close this dialog");

    this.btnClose1.setIcon(new ImageIcon(this.getClass().getResource("/com/beowurks/BeoCommon/images/exit22.png")));
  }

  // ---------------------------------------------------------------------------
  // JEditorPane, not JLabel, has an addHyperlinkListener.
  // Also, since one can block and copy a JEditorPane, but not a JLabel,
  // I'm making all text JEditorPanes.
  private void setupTextBoxes()
  {
    this.initTextBox(this.txtTitle1, "<div class='title'><a href='" + this.foAbout.getTitleURL() + "'>" + this.foAbout.getTitle() + "</a></div>");
    this.initTextBox(this.txtLicense1, "<div class='license'>Licensed under the <a href='" + this.foAbout.getLicenseURL() + "'>" + this.foAbout.getLicense() + "</a>.</div>");

    final SimpleDateFormat loFormat = new SimpleDateFormat("yyyy");
    final String lcCurrentYear = loFormat.format(new Date());
    final String lcStartYear = Integer.toString(this.foAbout.getCopyrightStartYear());

    final String lcCopyrightYears = (lcCurrentYear.compareTo(lcStartYear) == 0) ? lcStartYear : lcStartYear + "-" + lcCurrentYear;

    this.initTextBox(this.txtCopyright1, "<div class='copyright'>Copyright&#169; " + lcCopyrightYears + ", <a href='" + this.foAbout.getCopyrightCompanyURL() + "'>" + this.foAbout.getCopyrightCompany() + "</a>. All rights reserved.</div>");
  }

  // ---------------------------------------------------------------------------
  private void initTextBox(final JEditorPane toEditorPane, final String tcText)
  {
    final Color loRGB = this.getBackground();
    final String lcColor = "#" + Integer.toHexString(loRGB.getRed()) + Integer.toHexString(loRGB.getGreen()) + Integer.toHexString(loRGB.getBlue());
    final String lcPadding = Integer.toString(DialogAbout.PADDING);

    final HTMLDocument loDoc = (HTMLDocument) toEditorPane.getDocument();
    final StyleSheet loStyle = loDoc.getStyleSheet();
    loStyle.addRule("body { background-color: " + lcColor + "; border: none; font-size: 1.0em; }");
    // If you have padding, then you won't have problems with the HyperlinkListener
    // not being fired when quickly moving the mouse from the outside left to over
    // the JEditorPane.
    loStyle.addRule("div.title { font-size: 1.1em; font-weight: bold; padding: " + lcPadding + "px; font-family: Arial; }");
    loStyle.addRule("div.copyright { padding: " + lcPadding + "px; font-style: italic; font-family: Arial; }");
    loStyle.addRule("div.license { padding:" + lcPadding + "px; font-family: Arial; }");

    loStyle.addRule("a { color: #663300; }");

    toEditorPane.setText(tcText);
  }

  // ---------------------------------------------------------------------------
  private void setupLabels()
  {
    this.lblLogo1.setIcon(new ImageIcon(this.foAbout.getLogo()));
    this.lblLogo1.setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  // ---------------------------------------------------------------------------
  private void setupListeners()
  {
    this.btnClose1.addActionListener(this);
    this.lblLogo1.addMouseListener(this);
  }

  // ---------------------------------------------------------------------------
  private void setupGridDataAndSetColumnWidths()
  {
    // -------------------------------
    // Populate System Info grid
    // -------------------------------
    final TableColumnModel loColumnModel = this.grdSystemInfo1.getColumnModel();
    final SortingTableModel loSortModel = this.grdSystemInfo1.getSortModel();
    loSortModel.clearAll();

    final Enumeration<?> loEnum = System.getProperties().propertyNames();
    final StringBuilder lcValue = new StringBuilder();
    while (loEnum.hasMoreElements())
    {
      final String lcLabel = loEnum.nextElement().toString();

      Util.clearStringBuilder(lcValue);
      lcValue.append(System.getProperty(lcLabel));
      int lnPos;

      if ((lnPos = lcValue.toString().indexOf(0x0A)) != -1)
      {
        lcValue.replace(lnPos, lnPos + 1, "\\n");
      }

      if ((lnPos = lcValue.toString().indexOf(0x0D)) != -1)
      {
        lcValue.replace(lnPos, lnPos + 1, "\\r");
      }

      loSortModel.addRow(new Object[]{lcLabel, lcValue.toString()});
    }

    this.grdSystemInfo1.sortColumn(0, true, false);

    final int lnCol = this.grdSystemInfo1.getColumnCount();
    final int lnWidth = (int) (this.foStandardGridDimension.getWidth() / lnCol);

    for (int i = 0; i < lnCol; ++i)
    {
      loColumnModel.getColumn(i).setPreferredWidth(lnWidth);
    }

  }

  // ---------------------------------------------------------------------------
  @Override
  protected void removeListeners()
  {
    this.btnClose1.removeActionListener(this);
    this.lblLogo1.removeMouseListener(this);
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface ActionListener
  // ---------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    if (e.getSource() == this.btnClose1)
    {
      this.closeWindow();
    }
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface MouseListener
  // ---------------------------------------------------------------------------
  @Override
  public void mouseClicked(final MouseEvent toMouseEvent)
  {
    final Object loSource = toMouseEvent.getSource();
    if (loSource == this.lblLogo1)
    {
      Util.launchBrowser(this.foAbout.getLogoURL());
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mousePressed(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseReleased(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseEntered(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseExited(final MouseEvent toMouseEvent)
  {
  }
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
