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

package com.beowurks.BeoCommon.Dialogs;

import com.beowurks.BeoCommon.Util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// From http://ww05.com/swing-in-the-jeditorpane-component-in-the-realization-of-the-css-is-a-hyperlink/
// a:hover doesn't work natively like in a browser. Here's a fix. . . .
public class JEditorPaneFixHTML extends JEditorPane implements HyperlinkListener, MouseListener
{

  private final JScrollPane foScrollPane = new JScrollPane();

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------
  public JEditorPaneFixHTML()
  {

    this.initializeAll();
  }

  // ---------------------------------------------------------------------------
  public JEditorPaneFixHTML(final String url) throws IOException
  {
    super(url);

    this.initializeAll();
  }

  // ---------------------------------------------------------------------------
  public JEditorPaneFixHTML(final String type, final String text)
  {
    super(type, text);

    this.initializeAll();
  }

  // ---------------------------------------------------------------------------
  public JEditorPaneFixHTML(final URL initialPage) throws IOException
  {
    super(initialPage);

    this.initializeAll();
  }

  // ---------------------------------------------------------------------------
  public JScrollPane getScrollPane()
  {
    return (this.foScrollPane);
  }

  // ---------------------------------------------------------------------------
  public void setNeverScroll()
  {
    this.foScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    this.foScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
  }

  // ---------------------------------------------------------------------------
  private void initializeAll()
  {
    this.foScrollPane.setViewportView(this);

    // setEnabled makes the text barely readable.
    this.setEditable(false);
    this.setContentType("text/html");
    this.setBorder(null);

    this.setupListeners();

    this.setupStyles();
  }

  // ---------------------------------------------------------------------------
  private void setupListeners()
  {
    this.addMouseListener(this);
    this.addHyperlinkListener(this);
  }

  // ---------------------------------------------------------------------------
  private void setupStyles()
  {
    final HTMLDocument loDoc = (HTMLDocument) this.getDocument();
    final StyleSheet loStyle = loDoc.getStyleSheet();
    loStyle.addRule("a { text-decoration: none; }");
    loStyle.addRule("a:hover { text-decoration: underline; }");
  }

  // ---------------------------------------------------------------------------
  synchronized private void updateHyperlinkStyle(final JEditorPane toEditorPane, final String tcStyleName, final Element toElement)
  {
    final HTMLDocument loDoc = (HTMLDocument) toEditorPane.getDocument();

    if (toElement != null)
    {
      final Style loStyle = loDoc.getStyleSheet().getStyle(tcStyleName);
      final int lnStart = toElement.getStartOffset();
      final int lnEnd = toElement.getEndOffset();
      loDoc.setCharacterAttributes(lnStart, lnEnd - lnStart, loStyle, false);

      return;
    }

    for (final HTMLDocument.Iterator loIterator = loDoc.getIterator(HTML.Tag.A); loIterator.isValid(); loIterator.next())
    {
      final Style loStyle = loDoc.getStyleSheet().getStyle(tcStyleName);
      final int lnStart = loIterator.getStartOffset();
      final int lnEnd = loIterator.getEndOffset();
      loDoc.setCharacterAttributes(lnStart, lnEnd - lnStart, loStyle, false);
    }

  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // Interface HyperlinkListener
  // From http://ww05.com/swing-in-the-jeditorpane-component-in-the-realization-of-the-css-is-a-hyperlink/
  // a:hover doesn't work natively like in a browser. Here's a fix. . . .
  @Override
  public void hyperlinkUpdate(final HyperlinkEvent toHyperlinkEvent)
  {
    if (toHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      Util.launchBrowser(toHyperlinkEvent.getURL().toString());
    }
    else
    {
      String lcStyleName = null;
      if (toHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ENTERED)
      {
        lcStyleName = "a:hover";
      }
      else if (toHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.EXITED)
      {
        lcStyleName = "a";
      }

      if (lcStyleName != null)
      {
        this.updateHyperlinkStyle((JEditorPane) toHyperlinkEvent.getSource(), lcStyleName, toHyperlinkEvent.getSourceElement());
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Interface MouseListener
  // ---------------------------------------------------------------------------
  @Override
  public void mouseClicked(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mousePressed(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseReleased(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseEntered(final MouseEvent toMouseEvent)
  {
  }

  // ---------------------------------------------------------------------------
  @Override
  public void mouseExited(final MouseEvent toMouseEvent)
  {
    final Object loSource = toMouseEvent.getSource();
    if (loSource instanceof JEditorPane)
    {
      this.updateHyperlinkStyle((JEditorPane) loSource, "a", null);
    }
  }

  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
