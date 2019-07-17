/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java programs written by
 *           Beowurks.
 * =============================================================================
 * Copyright(c) 2001-2019, by Beowurks.
 *
 * This application is open-source software; you can redistribute it and/or modify it
 * under the terms of the Eclipse Public License, Version 1.0 (http://opensource.org/licenses/EPL-1.0).
 *
 * Original Author:  Eddie Fann
 * Contributor(s):   -;
 */

package com.beowurks.BeoCommon.Dialogs.Credits;

import com.beowurks.BeoCommon.BaseButton;
import com.beowurks.BeoCommon.BaseDialog;
import com.beowurks.BeoCommon.Dialogs.JEditorPaneFixHTML;
import com.beowurks.BeoCommon.GridBagLayoutHelper;
import com.beowurks.BeoCommon.Util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class DialogCredits extends BaseDialog implements ActionListener
{
  private final BaseButton btnClose1 = new BaseButton(76, 30);

  private final JEditorPaneFixHTML txtCredits1 = new JEditorPaneFixHTML();

  private final Vector<ICredit> foVectorLinks;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public DialogCredits(final JFrame toFrame, final Vector<ICredit> toVectorLinks)
  {
    super(toFrame, "Credits");
    this.foVectorLinks = toVectorLinks;

    try
    {
      this.jbInit();

      this.makeVisible(true);
    }
    catch (final Exception loErr)
    {
      loErr.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------
  // Component initialization
  // By the way, if you call this.setResizable(false), the application
  // icon will not appear in the upper left corner.
  @Override
  protected void jbInit() throws Exception
  {
    super.jbInit();

    this.setupButtons();
    this.setupScrollPanes();
    this.setupListeners();
    this.setupLayouts();

    this.setupCredits();

    Util.addEscapeListener(this);
  }

  // ---------------------------------------------------------------------------
  private void setupCredits()
  {
    final StringBuilder loHtml = new StringBuilder();
    loHtml.append("<p class='header'>We wish to acknowledge the following:</p>");

    for (final ICredit loVectorLink : this.foVectorLinks)
    {
      loHtml.append("<p>").append(loVectorLink.getDescription()).append("<br />");
      loHtml.append("<a href='").append(loVectorLink.getURL()).append("'>").append(loVectorLink.getURL()).append("</a></p>");
    }

    final HTMLDocument loDoc = (HTMLDocument) this.txtCredits1.getDocument();
    final StyleSheet loStyle = loDoc.getStyleSheet();

    loStyle.addRule("body {background-color: #C0D9D9; border: none; padding: 10px; font-family: Arial; }");
    loStyle.addRule("p.header { text-align: center; font-weight: bold; font-family: Arial; }");

    this.txtCredits1.setText(loHtml.toString());

    // Ensure that the Scroll Pane is at the top in case of too many lines of
    // credit text.
    // From http://stackoverflow.com/questions/2442504/jscrollpane-scrolls-down-with-long-text-in-jeditorpane
    // By the way, getScrollPane().getVerticalScrollBar().setValue(0) does not work.
    this.txtCredits1.setCaretPosition(0);
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
  private void setupLayouts()
  {
    // --------------------
    final GridBagLayoutHelper loGrid = new GridBagLayoutHelper();

    this.getContentPane().setLayout(loGrid);

    loGrid.setInsets(10, 4, 10, 4);

    this.getContentPane().add(this.txtCredits1.getScrollPane(),
            loGrid.getConstraint(0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH));
    this.getContentPane().add(this.btnClose1,
            loGrid.getConstraint(0, 1, GridBagConstraints.EAST, GridBagConstraints.NONE));
  }

  // ---------------------------------------------------------------------------
  private void setupScrollPanes()
  {
    final Dimension ldScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

    final int lnWidth = (int) (ldScreenSize.width * 0.3);
    final int lnHeight = (int) (ldScreenSize.height * 0.3);

    final JScrollPane loPane = this.txtCredits1.getScrollPane();
    loPane.setPreferredSize(new Dimension(lnWidth, lnHeight));
    loPane.setMinimumSize(new Dimension(lnWidth, lnHeight));
  }

  // ---------------------------------------------------------------------------
  private void setupListeners()
  {
    this.btnClose1.addActionListener(this);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void removeListeners()
  {
    super.removeListeners();

    this.btnClose1.removeActionListener(this);
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface ActionListener
  @Override
  public void actionPerformed(final ActionEvent toActionEvent)
  {
    final Object loSource = toActionEvent.getSource();

    if (loSource instanceof JButton)
    {
      if (loSource == this.btnClose1)
      {
        this.closeWindow();
      }
    }
  }
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
