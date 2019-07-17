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

import java.awt.Toolkit;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class FormattedLongField extends BaseFormattedNumberField
{
  private long fnMinValue = Long.MIN_VALUE;
  private long fnMaxValue = Long.MAX_VALUE;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public FormattedLongField()
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
  public FormattedLongField(final Object toValue)
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
  public FormattedLongField(final Format toFormat)
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
  public FormattedLongField(final JFormattedTextField.AbstractFormatter toFormatter)
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
  public FormattedLongField(final JFormattedTextField.AbstractFormatterFactory toFactory)
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
  public FormattedLongField(final JFormattedTextField.AbstractFormatterFactory toFactory, final Object toCurrentValue)
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
    this.foNumberFormat = NumberFormat.getIntegerInstance();

    this.setInputVerifier(new FormattedLongVerifier());

    super.setupFormattedField();
  }

  // ---------------------------------------------------------------------------
  public void setMinValue(final long tnMin)
  {
    this.fnMinValue = tnMin;
  }

  // ---------------------------------------------------------------------------
  public void setMaxValue(final long tnMax)
  {
    this.fnMaxValue = tnMax;
  }

  // ---------------------------------------------------------------------------
  public long getMinValue()
  {
    return (this.fnMinValue);
  }

  // ---------------------------------------------------------------------------
  public long getMaxValue()
  {
    return (this.fnMaxValue);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// I got this code from the help on JFormattedTextField.
class FormattedLongVerifier extends InputVerifier
{
  // ---------------------------------------------------------------------------
  // By the way, it's best not to call commitEdit or setValue inside this
  // routine as it could cause recursion and stack overflow.
  @Override
  public boolean verify(final JComponent toInput)
  {
    final FormattedLongField loFormattedLong = (FormattedLongField) toInput;

    try
    {
      final AbstractFormatter loFormatter = loFormattedLong.getFormatter();
      final String lcText = loFormattedLong.getText();
      final Object loValue = loFormatter.stringToValue(lcText);

      final long lnMin = loFormattedLong.getMinValue();
      final long lnMax = loFormattedLong.getMaxValue();

      final long lnCurrentValue = ((Number) loValue).longValue();

      if (lnCurrentValue < lnMin)
      {
        loFormattedLong.setText(Long.toString(lnMin));
        Util.errorMessage(null, "Must not be less than " + lnMin);
      }
      if (lnCurrentValue > lnMax)
      {
        loFormattedLong.setText(Long.toString(lnMax));
        Util.errorMessage(null, "Must not be more than " + lnMax);
      }
    }
    catch (final ParseException loErr)
    {
      Toolkit.getDefaultToolkit().beep();
    }

    return true;
  }

  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
