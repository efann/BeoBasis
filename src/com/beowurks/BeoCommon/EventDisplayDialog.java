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

package com.beowurks.BeoCommon;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class EventDisplayDialog extends BaseDialog implements ActionListener, Runnable
{
  public final static int IMAGE_CHECK = 0;
  public final static int IMAGE_ERROR = 1;
  public final static int IMAGE_INFO = 2;

  private final static String LINE_SEPARATOR = System.getProperty("line.separator");

  private final static int PREFERRED_WIDTH = 400;

  final private String[] faImageURLs = {"images/Check.gif", "images/Error.gif", "images/Information.gif"};
  private ImageIcon[] faImages;

  final private Box boxButtons1 = Box.createHorizontalBox();

  final private BaseButton btnClose1 = new BaseButton();
  final private JLabel lblTimeLapsed1 = new JLabel();
  final private JLabel lblTitle1 = new JLabel();

  final private EventTableModel foTableModel = new EventTableModel();

  final private JTable grdEvents1 = new JTable(this.foTableModel);
  final private JScrollPane scrEvents1 = new JScrollPane();

  final private JProgressBar barEventProgress1 = new JProgressBar(0, 100);

  final private Date foStartTime = new Date();

  private boolean flAutoClose = false;

  private BufferedWriter foLogWriter = null;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public EventDisplayDialog(final JFrame toFrame, final String tcTitle)
  {
    super(toFrame, tcTitle);

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
  public EventDisplayDialog(final JFrame toFrame, final String tcTitle, final boolean tlAutoClose, final File toLogFile)
  {
    super(toFrame, tcTitle);

    this.flAutoClose = tlAutoClose;

    try
    {
      this.jbInit();
      this.setupLogFile(toLogFile);
      this.makeVisible(true);
    }
    catch (final Exception loErr)
    {
      loErr.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  protected void jbInit() throws Exception
  {
    super.jbInit();

    this.setModal(false);

    this.setupButtons();
    this.setupListeners();
    this.setupTables();
    this.setupProgressBars();
    this.setupImages();
    this.setupLabels();

    this.setupLayouts();
  }

  // ---------------------------------------------------------------------------
  private void setupLogFile(final File toLogFile)
  {
    if (toLogFile == null)
    {
      return;
    }

    final String lcDirectory = Util.extractDirectory(toLogFile.getAbsolutePath());

    if (Util.makeDirectory(lcDirectory))
    {
      try
      {
        this.foLogWriter = new BufferedWriter(new FileWriter(toLogFile));
      }
      catch (final IOException loErr)
      {
        this.foLogWriter = null;
      }
    }
  }

  // ---------------------------------------------------------------------------
  private void setupImages()
  {
    final int lnCount = this.faImageURLs.length;

    this.faImages = new ImageIcon[lnCount];
    URL loURLIcon;

    for (int i = 0; i < lnCount; ++i)
    {
      loURLIcon = this.getClass().getResource(this.faImageURLs[i]);
      if (loURLIcon != null)
      {
        final Image loImage = Toolkit.getDefaultToolkit().createImage(loURLIcon);
        this.faImages[i] = new ImageIcon(loImage.getScaledInstance(16, 16, Image.SCALE_SMOOTH));
      }
      else
      {
        System.err.println("Can't find " + this.faImageURLs[i]);
      }
    }
  }

  // ---------------------------------------------------------------------------
  private void setupLabels()
  {
    this.lblTimeLapsed1.setText("<html><font face=\"Arial\"><i>" + this.getTitle()
            + " is running. . . .</i></font></html>");

    this.lblTitle1.setText("Event Display");
    this.lblTitle1.setFont(this.lblTitle1.getFont().deriveFont(Font.ITALIC));
  }

  // ---------------------------------------------------------------------------
  private void setupButtons()
  {
    this.btnClose1.setText("Close");
    this.btnClose1.setMnemonic('C');
    this.btnClose1.setToolTipText("Close this event display window");

    this.btnClose1.setEnabled(false);
  }

  // ---------------------------------------------------------------------------
  private void setupLayouts()
  {
    this.boxButtons1.add(this.btnClose1, null);

    this.scrEvents1.getViewport().add(this.grdEvents1, null);

    final GridBagLayoutHelper loGrid = new GridBagLayoutHelper();

    this.getContentPane().setLayout(loGrid);

    loGrid.setInsets(4, 4, 0, 4);
    this.getContentPane().add(this.lblTitle1,
            loGrid.getConstraint(0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL));
    loGrid.setInsets(0, 4, 4, 4);
    this.getContentPane().add(this.scrEvents1,
            loGrid.getConstraint(0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH));
    loGrid.setInsetDefaults();
    this.getContentPane().add(this.lblTimeLapsed1,
            loGrid.getConstraint(0, 2, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));
    this.getContentPane().add(this.barEventProgress1,
            loGrid.getConstraint(0, 3, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));
    this.getContentPane().add(this.boxButtons1,
            loGrid.getConstraint(0, 4, GridBagConstraints.CENTER, GridBagConstraints.NONE));
  }

  // ---------------------------------------------------------------------------
  private void setupListeners()
  {
    this.btnClose1.addActionListener(this);
  }

  // ---------------------------------------------------------------------------
  private void setupTables()
  {
    this.grdEvents1.setShowHorizontalLines(false);
    this.grdEvents1.setShowVerticalLines(false);
    this.grdEvents1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.grdEvents1.setRowSelectionAllowed(true);
    this.grdEvents1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    this.grdEvents1.setIntercellSpacing(new Dimension(0, 0));

    final Dimension ldSize = new Dimension(EventDisplayDialog.PREFERRED_WIDTH, 300);
    this.scrEvents1.setPreferredSize(ldSize);
    this.scrEvents1.setMinimumSize(ldSize);

    this.grdEvents1.setTableHeader(null);

    this.grdEvents1.setRowHeight(20);
    this.grdEvents1.getColumnModel().getColumn(0).setPreferredWidth(30);
    this.grdEvents1.getColumnModel().getColumn(0).setMaxWidth(30);

    this.grdEvents1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
  }

  // ---------------------------------------------------------------------------
  private void setupProgressBars()
  {
    final Dimension ldSize = new Dimension(EventDisplayDialog.PREFERRED_WIDTH, (int) this.barEventProgress1
            .getPreferredSize().getHeight() + 2);

    this.barEventProgress1.setPreferredSize(ldSize);
    this.barEventProgress1.setMinimumSize(ldSize);

    this.barEventProgress1.setValue(0);
    this.barEventProgress1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    this.barEventProgress1.setStringPainted(true);
  }

  // ---------------------------------------------------------------------------
  public void updateProgressbar(final int tnPercentage)
  {
    this.barEventProgress1.setValue(tnPercentage);

    SwingUtilities.invokeLater(this);
  }

  // ---------------------------------------------------------------------------
  public void updateLines(final String tcMessage, final int tnImageType)
  {
    this.updateLog(tcMessage);

    this.foTableModel.addRow(new Object[]{this.faImages[tnImageType], tcMessage});

    SwingUtilities.invokeLater(this);
  }

  // ---------------------------------------------------------------------------
  private void updateLog(final String tcMessage)
  {
    if (this.foLogWriter == null)
    {
      return;
    }

    try
    {
      this.foLogWriter.write(tcMessage);
      this.foLogWriter.write(EventDisplayDialog.LINE_SEPARATOR);
    }
    catch (final IOException ignored)
    {
    }
  }

  // ---------------------------------------------------------------------------
  private void closeLog()
  {
    if (this.foLogWriter == null)
    {
      return;
    }

    try
    {
      this.foLogWriter.close();
    }
    catch (final IOException ignored)
    {
    }
  }

  // ---------------------------------------------------------------------------
  public void enableCloseButton(final boolean tlEnabled)
  {
    final String lcTimeDifference = Util.displayTimeDifference(this.foStartTime, new Date(), 1);

    this.lblTimeLapsed1.setText("<html><font face=\"Arial\"><i>" + lcTimeDifference + "</i></font></html>");

    this.updateLog("");
    this.updateLog(lcTimeDifference);

    this.closeLog();

    this.btnClose1.setEnabled(tlEnabled);

    if (this.flAutoClose)
    {
      this.btnClose1.doClick();
    }
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface ActionListener
  // ---------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent toEvent)
  {
    final Object loSource = toEvent.getSource();

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
  // Interface ActionListener
  // ---------------------------------------------------------------------------
  @Override
  public void run()
  {
    final int lnRow = this.grdEvents1.getRowCount();

    // Turns out there is no need to call the following routine:
    // this.foTableModel.fireTableDataChanged();
    this.grdEvents1.changeSelection(lnRow - 1, 0, false, false);

    if (!this.isVisible())
    {
      this.setVisible(true);
    }

    this.barEventProgress1.updateUI();
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
class EventTableModel extends DefaultTableModel
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public EventTableModel()
  {

    this.setColumnIdentifiers(new Object[]{new ImageIcon(), "Event"});
    this.setRowCount(0);

  }

  // ---------------------------------------------------------------------------
  @Override
  public boolean isCellEditable(final int row, final int column)
  {
    return (false);
  }

  // ---------------------------------------------------------------------------
  @Override
  public Class<?> getColumnClass(final int tnColumn)
  {
    if (tnColumn == 0)
    {
      return ImageIcon.class;
    }

    final Object loObject = this.getValueAt(0, tnColumn);

    if (loObject == null)
    {
      // Default to the String class
      return (String.class);
    }

    return (loObject.getClass());
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
