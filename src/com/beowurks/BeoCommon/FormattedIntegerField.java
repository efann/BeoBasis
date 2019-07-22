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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import java.awt.Toolkit;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class FormattedIntegerField extends BaseFormattedNumberField
{
  private int fnMinValue = Integer.MIN_VALUE;
  private int fnMaxValue = Integer.MAX_VALUE;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField()
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

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField(final Object toValue)
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

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField(final Format toFormat)
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

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField(final JFormattedTextField.AbstractFormatter toFormatter)
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

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField(final JFormattedTextField.AbstractFormatterFactory toFactory)
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

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedIntegerField(final JFormattedTextField.AbstractFormatterFactory toFactory, final Object toCurrentValue)
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

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected void setupFormattedField() throws Exception
  {
    this.foNumberFormat = NumberFormat.getIntegerInstance();

    this.setInputVerifier(new FormattedIntegerVerifier());

    super.setupFormattedField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setMinValue(final int tnMin)
  {
    this.fnMinValue = tnMin;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setMaxValue(final int tnMax)
  {
    this.fnMaxValue = tnMax;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public int getMinValue()
  {
    return (this.fnMinValue);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public int getMaxValue()
  {
    return (this.fnMaxValue);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// I got this code from the help on JFormattedTextField.
class FormattedIntegerVerifier extends InputVerifier
{
  // ---------------------------------------------------------------------------------------------------------------------
  // By the way, it's best not to call commitEdit or setValue inside this
  // routine as it could cause recursion and stack overflow.
  @Override
  public boolean verify(final JComponent toInput)
  {
    final FormattedIntegerField loFormattedInteger = (FormattedIntegerField) toInput;

    try
    {
      final AbstractFormatter loFormatter = loFormattedInteger.getFormatter();
      final String lcText = loFormattedInteger.getText();
      final Object loValue = loFormatter.stringToValue(lcText);

      final int lnMin = loFormattedInteger.getMinValue();
      final int lnMax = loFormattedInteger.getMaxValue();

      final int lnCurrentValue = ((Number) loValue).intValue();

      if (lnCurrentValue < lnMin)
      {
        loFormattedInteger.setText(Integer.toString(lnMin));
        Util.errorMessage(null, "Must not be less than " + lnMin);
      }
      if (lnCurrentValue > lnMax)
      {
        loFormattedInteger.setText(Integer.toString(lnMax));
        Util.errorMessage(null, "Must not be more than " + lnMax);
      }
    }
    catch (final ParseException loErr)
    {
      Toolkit.getDefaultToolkit().beep();
    }

    return true;
  }

  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
