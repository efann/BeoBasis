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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// I got the code for this class from the JTextField help.
public class TextUpperCaseField extends BaseTextField
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public TextUpperCaseField()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public TextUpperCaseField(final Document tdDoc, final String tcText, final int tnColumns)
  {
    super(tdDoc, tcText, tnColumns);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public TextUpperCaseField(final int tnColumns)
  {
    super(tnColumns);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public TextUpperCaseField(final String tcText)
  {
    super(tcText);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public TextUpperCaseField(final String tcText, final int tnColumns)
  {
    super(tcText, tnColumns);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  protected Document createDefaultModel()
  {
    return new UpperCaseDocument();
  }
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
final class UpperCaseDocument extends PlainDocument
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void insertString(final int tnOffset, final String tcString, final AttributeSet toAttributeSet)
      throws BadLocationException
  {
    if (tcString == null)
    {
      return;
    }

    final char[] lcUpper = tcString.toCharArray();
    for (int i = 0; i < lcUpper.length; i++)
    {
      lcUpper[i] = Character.toUpperCase(lcUpper[i]);
    }
    super.insertString(tnOffset, new String(lcUpper), toAttributeSet);
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
