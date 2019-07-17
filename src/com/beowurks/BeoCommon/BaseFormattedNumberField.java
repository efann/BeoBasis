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

import java.text.Format;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class BaseFormattedNumberField extends BaseFormattedField
{
  protected NumberFormat foNumberFormat = null;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField()
  {
  }

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField(final Object toValue)
  {
    super(toValue);
  }

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField(final Format toFormat)
  {
    super(toFormat);
  }

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField(final JFormattedTextField.AbstractFormatter toFormatter)
  {
    super(toFormatter);
  }

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField(final JFormattedTextField.AbstractFormatterFactory toFactory)
  {
    super(toFactory);
  }

  // ---------------------------------------------------------------------------
  public BaseFormattedNumberField(final JFormattedTextField.AbstractFormatterFactory toFactory,
                                  final Object toCurrentValue)
  {
    super(toFactory, toCurrentValue);
  }

  // ---------------------------------------------------------------------------
  @Override
  protected void setupFormattedField() throws Exception
  {
    super.setupFormattedField();

    if (this.foNumberFormat == null)
    {
      throw (new Exception("foNumberFormat has not been initialized."));
    }

    // REALIZE: the foNumberFormat, when initialized, has already
    // been set to one that handles integers and/or longs.

    // I got this convoluted code from the JFormattedTextField (Format toFormat)
    // code.
    // And it turns out that I can still reference foNumberFormat to change
    // various formats.
    final NumberFormatter loNumberFormatter = new NumberFormatter(this.foNumberFormat);
    this.setFormatterFactory(new DefaultFormatterFactory(loNumberFormatter));

    this.setHorizontalAlignment(SwingConstants.RIGHT);
  }

  // ---------------------------------------------------------------------------
  public void setMinimumFractionDigits(final int tnNewValue)
  {
    if (this.foNumberFormat == null)
    {
      return;
    }

    // The integer numbers can't have decimals. Duh. . . .
    if (this.foNumberFormat.isParseIntegerOnly())
    {
      return;
    }

    this.foNumberFormat.setMinimumFractionDigits(tnNewValue);
  }

  // ---------------------------------------------------------------------------
  public void setMaximumFractionDigits(final int tnNewValue)
  {
    if (this.foNumberFormat == null)
    {
      return;
    }

    if (this.foNumberFormat.isParseIntegerOnly())
    {
      return;
    }

    this.foNumberFormat.setMaximumFractionDigits(tnNewValue);
  }

  // ---------------------------------------------------------------------------
  public void setMinimumIntegerDigits(final int tnNewValue)
  {
    if (this.foNumberFormat == null)
    {
      return;
    }

    this.foNumberFormat.setMinimumIntegerDigits(tnNewValue);
  }

  // ---------------------------------------------------------------------------
  public void setMaximumIntegerDigits(final int tnNewValue)
  {
    if (this.foNumberFormat == null)
    {
      return;
    }

    this.foNumberFormat.setMaximumIntegerDigits(tnNewValue);
  }

  // ---------------------------------------------------------------------------
  public int getIntegerValue()
  {
    final Object loValue = this.getValue();
    return ((loValue instanceof Number) ? ((Number) loValue).intValue() : 0);
  }

  // ---------------------------------------------------------------------------
  public long getLongValue()
  {
    final Object loValue = this.getValue();
    return ((loValue instanceof Number) ? ((Number) loValue).longValue() : 0);
  }

  // ---------------------------------------------------------------------------
  public double getDoubleValue()
  {
    final Object loValue = this.getValue();
    return ((loValue instanceof Number) ? ((Number) loValue).doubleValue() : 0);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
