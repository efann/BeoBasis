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

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Document;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class BaseTextField extends JTextField implements FocusListener
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTextField()
  {
    this.setupTextField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTextField(final Document tdDoc, final String tcText, final int tnColumns)
  {
    super(tdDoc, tcText, tnColumns);
    this.setupTextField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTextField(final int tnColumns)
  {
    super(tnColumns);
    this.setupTextField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTextField(final String tcText)
  {
    super(tcText);
    this.setupTextField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseTextField(final String tcText, final int tnColumns)
  {
    super(tcText, tnColumns);
    this.setupTextField();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void setupTextField()
  {
    this.addFocusListener(this);

    this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "TransferFocus");
    this.getActionMap().put("TransferFocus", new TextFieldAbstractAction());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
  // Interface FocusListener
  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void focusGained(final FocusEvent e)
  {
    this.selectAll();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void focusLost(final FocusEvent e)
  {
  }
  // ---------------------------------------------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------------------------------------------
}

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class TextFieldAbstractAction extends AbstractAction
{
  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public TextFieldAbstractAction()
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
