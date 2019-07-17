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

package com.beowurks.BeoCommon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class FormattedDateField extends BaseFormattedField implements KeyListener
{
  private final Calendar foCalendar = Calendar.getInstance();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public FormattedDateField()
  {

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  public FormattedDateField(final Object toValue)
  {
    super(toValue);

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  public FormattedDateField(final Format toFormat)
  {
    super(toFormat);

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  public FormattedDateField(final JFormattedTextField.AbstractFormatter toFormatter)
  {
    super(toFormatter);

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  public FormattedDateField(final JFormattedTextField.AbstractFormatterFactory toFactory)
  {
    super(toFactory);

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  public FormattedDateField(final JFormattedTextField.AbstractFormatterFactory toFactory, final Object toCurrentValue)
  {
    super(toFactory, toCurrentValue);

    try
    {
      this.setupFormattedField();
    }
    catch (final Exception loErr)
    {
      System.err.println(loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  @Override
  protected void setupFormattedField() throws Exception
  {
    super.setupFormattedField();

    // Removes the default behavior of UP and DOWN arrow keys which was
    // interfering somewhat with the behavior
    // established in the below keyReleased routine. For instance, Up arrow
    // would increment the year or
    // the month or the day depending on the cursor location.
    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");
    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), "none");
    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), "none");

    this.setToolTipText("Date entry uses Quicken-style keys");

    this.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(new SimpleDateFormat("M-d-yyyy"))));
    this.addKeyListener(this);
  }

  // ---------------------------------------------------------------------------
  @Override
  public void setText(final String tcValue)
  {
    super.setText(tcValue);

    // For some reason, every now and then, if you don't commit
    // this value then the Quicken-like keys use
    // the current date rather than the current text value.
    // This fixes that problem.
    try
    {
      this.commitEdit();
    }
    catch (final ParseException ignored)
    {
    }

  }

  // ---------------------------------------------------------------------------
  public long getTimeValue()
  {
    // Kind of a bug. If you retrieve the value before committing
    // it might not be current. For example, if you initially set
    // the text box value and then immediately try to use the current value of
    // the text box, it will not yet be updated.
    try
    {
      this.commitEdit();
    }
    catch (final ParseException ignored)
    {
    }

    final Object loValue = this.getValue();
    return ((loValue instanceof Date) ? ((Date) loValue).getTime() : 0);
  }

  // ---------------------------------------------------------------------------
  protected boolean alterDateValue(final char tcChar)
  {
    boolean llAltered = false;

    final boolean llEmpty = (this.getValue() == null);

    // By the way, in order for this.getValue() to return the correct value,
    // FormattedDateField
    // must use setValue() rather than setText().
    final Date loDate = (!llEmpty) ? (Date) this.getValue() : new Date();

    this.foCalendar.setTime(loDate);

    switch (tcChar)
    {
      case '+':
      case '=':
        if (!llEmpty)
        {
          this.foCalendar.add(Calendar.DATE, 1);
        }
        llAltered = true;
        break;

      case '-':
      case '_':
        if (!llEmpty)
        {
          this.foCalendar.add(Calendar.DATE, -1);
        }
        llAltered = true;
        break;

      case 'T':
      case 't':
        this.foCalendar.setTime(new Date());
        llAltered = true;
        break;

      case 'Y':
      case 'y':
        this.foCalendar.set(this.foCalendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
        llAltered = true;
        break;

      case 'R':
      case 'r':
        this.foCalendar.set(this.foCalendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        llAltered = true;
        break;

      case 'M':
      case 'm':
        this.foCalendar.set(this.foCalendar.get(Calendar.YEAR), this.foCalendar.get(Calendar.MONTH), 1);
        llAltered = true;
        break;

      case 'H':
      case 'h':
        // Go to the first day of the next month, then subtract 1 day to get the
        // last day of the month.
        this.foCalendar.set(this.foCalendar.get(Calendar.YEAR), this.foCalendar.get(Calendar.MONTH) + 1, 1);
        this.foCalendar.add(Calendar.DATE, -1);
        llAltered = true;
        break;
    }

    // Otherwise, the value will be over-written no matter what key is pressed.
    if (llAltered)
    {
      this.setValue(this.foCalendar.getTime());
    }

    return (llAltered);
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface KeyListener
  // ---------------------------------------------------------------------------
  @Override
  public void keyReleased(final KeyEvent toEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void keyPressed(final KeyEvent toEvent)
  {
    if (toEvent.isAltDown() || toEvent.isControlDown() || toEvent.isAltGraphDown() || toEvent.isMetaDown())
    {
      return;
    }

    final int lnID = toEvent.getID();
    final int lnKeyCode = toEvent.getKeyCode();

    if ((lnID == 401) && (lnKeyCode == 38))
    {
      if (this.alterDateValue('+'))
      {
        toEvent.consume();
      }
    }
    else if ((lnID == 401) && (lnKeyCode == 40))
    {
      if (this.alterDateValue('-'))
      {
        toEvent.consume();
      }
    }
  }
  // ---------------------------------------------------------------------------
  // This method checks the key pressed, allowing the following
  // unique behaviors:
  // +,= next day
  // -,_ previous day
  // T,t Today's date
  // M,m First day of month
  // H,h Last day of month
  // Y,y First day of year
  // R,r Last day of year

  @Override
  public void keyTyped(final KeyEvent toEvent)
  {
    if (toEvent.isAltDown() || toEvent.isControlDown() || toEvent.isAltGraphDown() || toEvent.isMetaDown())
    {
      return;
    }

    if (this.alterDateValue(toEvent.getKeyChar()))
    {
      toEvent.consume();
    }
  }
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
