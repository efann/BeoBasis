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
public class FormattedDoubleField extends BaseFormattedNumberField
{
  // You can't use MIN_VALUE: it's just the smallest positive number.
  private double fnMinValue = -Double.MAX_VALUE;
  private double fnMaxValue = Double.MAX_VALUE;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public FormattedDoubleField()
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
  public FormattedDoubleField(final Object toValue)
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
  public FormattedDoubleField(final Format toFormat)
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
  public FormattedDoubleField(final JFormattedTextField.AbstractFormatter toFormatter)
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
  public FormattedDoubleField(final JFormattedTextField.AbstractFormatterFactory toFactory)
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
  public FormattedDoubleField(final JFormattedTextField.AbstractFormatterFactory toFactory, final Object toCurrentValue)
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
    this.foNumberFormat = NumberFormat.getNumberInstance();

    this.setInputVerifier(new FormattedDoubleVerifier());

    super.setupFormattedField();
  }

  // ---------------------------------------------------------------------------
  public void setMinValue(final double tnMin)
  {
    this.fnMinValue = tnMin;
  }

  // ---------------------------------------------------------------------------
  public void setMaxValue(final double tnMax)
  {
    this.fnMaxValue = tnMax;
  }

  // ---------------------------------------------------------------------------
  public double getMinValue()
  {
    return (this.fnMinValue);
  }

  // ---------------------------------------------------------------------------
  public double getMaxValue()
  {
    return (this.fnMaxValue);
  }
  // ---------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// I got this code from the help on JFormattedTextField.
class FormattedDoubleVerifier extends InputVerifier
{
  // ---------------------------------------------------------------------------
  // By the way, it's best not to call commitEdit or setValue inside this
  // routine as it could cause recursion and stack overflow.
  @Override
  public boolean verify(final JComponent toInput)
  {
    final FormattedDoubleField loFormattedDouble = (FormattedDoubleField) toInput;

    try
    {
      final AbstractFormatter loFormatter = loFormattedDouble.getFormatter();
      final String lcText = loFormattedDouble.getText();
      final Object loValue = loFormatter.stringToValue(lcText);

      final double lnMin = loFormattedDouble.getMinValue();
      final double lnMax = loFormattedDouble.getMaxValue();

      final double lnCurrentValue = ((Number) loValue).doubleValue();

      if (lnCurrentValue < lnMin)
      {
        loFormattedDouble.setText(Double.toString(lnMin));
        Util.errorMessage(null, "Must not be less than " + lnMin);
      }
      if (lnCurrentValue > lnMax)
      {
        loFormattedDouble.setText(Double.toString(lnMax));
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
  @Override
  public boolean shouldYieldFocus(final JComponent toInput)
  {
    return this.verify(toInput);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
