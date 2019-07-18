/*
 * =============================================================================
 * BeoBasis: a library of common routines for Java programs written by
 *           Beowurks.
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

import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.Format;

import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BaseFormattedField extends JFormattedTextField
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField(final Object toValue)
  {
    super(toValue);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField(final Format toFormat)
  {
    super(toFormat);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField(final JFormattedTextField.AbstractFormatter toFormatter)
  {
    super(toFormatter);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField(final JFormattedTextField.AbstractFormatterFactory toFactory)
  {
    super(toFactory);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFormattedField(final JFormattedTextField.AbstractFormatterFactory toFactory, final Object toCurrentValue)
  {
    super(toFactory, toCurrentValue);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void setupFormattedField() throws Exception
  {
    this.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "TransferFocus");
    this.getActionMap().put("TransferFocus", new FormattedFieldAbstractAction());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Doing selectAll() in a JFormattedTextField on focusGained event doesn't
  // work.
  // The behavior you are reporting is documented in the bug database
  // (#4740914).
  // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4740914
  // Please note that this solution does not work when this component is used as
  // an
  // editor in a JTable with surrender-focus enabled.
  // Below is a recommended fix:
  @Override
  protected void processFocusEvent(final FocusEvent toEvent)
  {
    super.processFocusEvent(toEvent);

    if (toEvent.getID() == FocusEvent.FOCUS_GAINED)
    {
      this.selectAll();
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class FormattedFieldAbstractAction extends AbstractAction
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public FormattedFieldAbstractAction()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    try
    {
      new Robot().keyPress(KeyEvent.VK_TAB);
    }
    catch (final java.awt.AWTException ignored)
    {
    }
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
