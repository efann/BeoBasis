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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
abstract public class BaseFrame extends JFrame
{
  static private final boolean flLinuxOS = (System.getProperty("os.name").toLowerCase().contains("linux"));
  static private final boolean flWindowsOS = (System.getProperty("os.name").toLowerCase().contains("windows"));

  protected JMenuBar foDuplicateMenuBar = new JMenuBar();
  protected JMenu faDupMenus[];

  protected boolean flPackFrame = false;

  private final Frame foCallingFrame;

  // Gets rid of the following error:
  // serializable class has no definition of serialVersionUID
  private static final long serialVersionUID = 1L;

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFrame()
  {

    // Set foCallingFrame at the first line. Otherwise, if the active
    // frame changes (i.e., this new JFrame becomes the active one),
    // you won't be able to reset the calling frame cursor.
    this.foCallingFrame = BaseFrame.getActiveFrame();
    this.setCallingFrameCursor(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public BaseFrame(final String tcTitle, final boolean tlPackFrame)
  {
    super(tcTitle);

    // Set foCallingFrame at the first line. Otherwise, if the active
    // frame changes (i.e., this new JFrame becomes the active one),
    // you won't be able to reset the calling frame cursor.
    this.foCallingFrame = BaseFrame.getActiveFrame();
    this.flPackFrame = tlPackFrame;
    this.setCallingFrameCursor(true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Component initialization
  protected void jbInit() throws Exception
  {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // Overridden so we can exit when window is closed
  @Override
  protected void processWindowEvent(final WindowEvent e)
  {
    // Remember, you don't have to call dispose as
    // this.setDefaultCloseOperation()
    // is set in jbInit. And it does work: I've tested it.
    switch (e.getID())
    {
      case WindowEvent.WINDOW_OPENED:

        // This was set to false in the constructor.
        this.setCallingFrameCursor(false);
        break;

      case WindowEvent.WINDOW_CLOSED:
        this.cleanUp();
        break;

      // I can't use this to make sure that there are not any modal dialogs up
      // and running. If a frame, not containing a modal dialog, is clicked from
      // the task bar, the following happens: in Windows, WINDOW_ACTIVATED does
      // get called; in Linux,
      // no events happen and the frame becomes the front and inactive window.
      case WindowEvent.WINDOW_ACTIVATED:
        if (BaseFrame.flWindowsOS)
        {
          this.switchToTopModalWindow();
        }
        break;
    }

    super.processWindowEvent(e);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void switchToTopModalWindow()
  {
    final Dialog loDialog = Util.getTopModalWindow();
    if (loDialog != null)
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        @Override
        public void run()
        {
          loDialog.requestFocus();
        }
      });
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  static public JFrame getActiveFrame()
  {
    final Frame[] laFrames = Frame.getFrames();

    for (final Frame loXFrame : laFrames)
    {
      if (!loXFrame.isVisible())
      {
        continue;
      }

      if (loXFrame instanceof JFrame)
      {
        final JFrame loFrame = (JFrame) loXFrame;
        if (loFrame.isActive())
        {
          return (loFrame);
        }
      }
    }

    // If here, then just return the first, visible JFrame.
    for (final Frame loXFrame : laFrames)
    {
      if (!loXFrame.isVisible())
      {
        continue;
      }

      if (loXFrame instanceof JFrame)
      {
        return ((JFrame) loXFrame);
      }
    }

    return (null);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void centerFrame()
  {
    if (this.flPackFrame)
    {
      this.pack();
    }
    else
    {
      this.validate();
    }

    // Center the window
    final Dimension ldScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension ldFrameSize = this.getSize();

    if (ldFrameSize.height > ldScreenSize.height)
    {
      ldFrameSize.height = ldScreenSize.height;
    }
    if (ldFrameSize.width > ldScreenSize.width)
    {
      ldFrameSize.width = ldScreenSize.width;
    }

    this.setLocation((ldScreenSize.width - ldFrameSize.width) / 2, (ldScreenSize.height - ldFrameSize.height) / 2);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void setLocation(final Point p)
  {
    this.setLocation((int) p.getX(), (int) p.getY());
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public void setLocation(final int x, final int y)
  {
    // So far this is the case with Linux only.
    // Without the visible trick, for some reason, setPosition well usually set
    // the frame
    // to the correct position and then reset to the old position. Very
    // irritating. When
    // I toggle visibility, everything worked.
    final boolean llVisibleTrick = (this.isVisible() && BaseFrame.flLinuxOS);

    if (llVisibleTrick)
    {
      this.setVisible(false);
    }

    super.setLocation(x, y);

    if (llVisibleTrick)
    {
      this.setVisible(true);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I got the following code from
  // http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
  public void makeVisible(final boolean tlCenter)
  {
    this.makeVisible(tlCenter, true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I got the following code from
  // http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
  // By the way, I added the new parameter of tlUseInvokeLater as sometimes this
  // routine
  // is called from within an EventQueue.invokeLater routine which can throw off
  // the order of
  // GUI items being processed.
  public void makeVisible(final boolean tlCenter, final boolean tlUseInvokeLater)
  {
    class FrameShowFix implements Runnable
    {
      final BaseFrame foBaseFrame;
      final boolean flCenter;

      public FrameShowFix(final BaseFrame toBaseFrame, final boolean tlFixCenter)
      {
        this.foBaseFrame = toBaseFrame;
        this.flCenter = tlFixCenter;
      }

      @Override
      public void run()
      {
        try
        {
          if (this.foBaseFrame.getState() == Frame.ICONIFIED)
          {
            this.foBaseFrame.setState(Frame.NORMAL);
          }

          if (this.flCenter)
          {
            this.foBaseFrame.centerFrame();
          }
          else
          {
            this.foBaseFrame.pack();
          }

          this.foBaseFrame.setVisible(true);
        }
        catch (final Exception loErr)
        {
          System.err.println(loErr.getMessage());
        }
      }
    }

    if (tlUseInvokeLater)
    {
      EventQueue.invokeLater(new FrameShowFix(this, tlCenter));
    }
    else
    {
      final FrameShowFix loFrameShowFix = new FrameShowFix(this, tlCenter);
      loFrameShowFix.run();
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void setCallingFrameCursor(final boolean tlBusy)
  {
    if (this.foCallingFrame != null)
    {
      if (this.foCallingFrame instanceof BaseFrame)
      {
        ((BaseFrame) this.foCallingFrame).setBusy(tlBusy, false);
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setBusy(final boolean tlBusy)
  {
    this.setBusy(tlBusy, true);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  public void setBusy(final boolean tlBusy, final boolean tlIncludeFocusRoutines)
  {
    if (tlBusy)
    {
      WaitCursorRoutines.setWaitCursor(this, tlIncludeFocusRoutines);
    }
    else
    {
      WaitCursorRoutines.setDefaultCursor(this, tlIncludeFocusRoutines);
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I created this because there isn't a programmatic closing method for a
  // JFrame.
  public void closeWindow()
  {
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void releasePointers()
  {
    this.setJMenuBar(null);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I used to share the actual menu between JFrames, and when the particular
  // JFrame was inactive, I would have a duplicate menu bar placed on the
  // frame. Problem was that the drop-down menu (JPopupMenu) of the real menu
  // would have a
  // reference to the last frame on which the drop down menu appeared.
  // The actual property is "transient Frame frame" found in JPopupMenu.
  // And if the JFrame had been disposed, the garbage collecter could not
  // free the memory because of this reference in the drop-down menu. Yes,
  // once you went to the current menu and dropped down all of the JPopupMenus,
  // the reference would be reset. However, it's best create actual duplicate
  // functioning menus for each JFrame.
  protected void createDuplicateTopMenu(final JMenuBar toMenuBarToCopy)
  {
    if (toMenuBarToCopy.getMenuCount() > 0)
    {
      this.copyAllMenuItems(toMenuBarToCopy);
      this.copyAllListeners(toMenuBarToCopy);
      this.addMenuActionCommands(this.foDuplicateMenuBar);
    }
    else
    {
      Util.errorMessage(null, "Unable to setup duplicate menu for " + this.getName() + ".");
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void addMenuActionCommands(final JMenuBar toMenuBar)
  {
    final int lnCount = toMenuBar.getMenuCount();

    for (int i = 0; i < lnCount; ++i)
    {
      final JMenu loMenu = toMenuBar.getMenu(i);
      loMenu.setActionCommand(loMenu.getText());

      final int lnCountComp = loMenu.getMenuComponentCount();
      for (int lnC = 0; lnC < lnCountComp; ++lnC)
      {
        final Component loItem = loMenu.getMenuComponent(lnC);
        if (loItem instanceof JMenuItem)
        {
          final JMenuItem loMenuItem = (JMenuItem) loItem;
          loMenuItem.setActionCommand(loMenuItem.getText());
        }
      }

    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void copyAllListeners(final JMenuBar toMenuBarToCopy)
  {
    final int lnCount = toMenuBarToCopy.getMenuCount();
    ActionListener[] laActions;
    int lnActions;

    for (int i = 0; i < lnCount; ++i)
    {
      final JMenu loMenu = toMenuBarToCopy.getMenu(i);

      laActions = loMenu.getActionListeners();
      lnActions = laActions.length;
      for (int lnA = 0; lnA < lnActions; ++lnA)
      {
        this.faDupMenus[i].addActionListener(laActions[lnA]);
      }

      final int lnCountComp = loMenu.getMenuComponentCount();
      for (int lnC = 0; lnC < lnCountComp; ++lnC)
      {
        final Component loItem = loMenu.getMenuComponent(lnC);
        if (loItem instanceof JMenuItem)
        {
          final JMenuItem loOldItem = (JMenuItem) loItem;
          final JMenuItem loMenuItem = (JMenuItem) this.foDuplicateMenuBar.getMenu(i).getMenuComponent(lnC);

          laActions = loOldItem.getActionListeners();
          lnActions = laActions.length;
          for (int lnA = 0; lnA < lnActions; ++lnA)
          {
            loMenuItem.addActionListener(laActions[lnA]);
          }
        }
      }
    }
  }

  // ---------------------------------------------------------------------------------------------------------------------
  private void copyAllMenuItems(final JMenuBar toMenuBarToCopy)
  {
    final int lnCount = toMenuBarToCopy.getMenuCount();
    this.faDupMenus = new JMenu[lnCount];

    for (int i = 0; i < lnCount; ++i)
    {
      this.faDupMenus[i] = new JMenu();
      final JMenu loMenu = toMenuBarToCopy.getMenu(i);

      this.faDupMenus[i].setText(loMenu.getText());
      this.faDupMenus[i].setMnemonic(loMenu.getMnemonic());

      this.foDuplicateMenuBar.add(this.faDupMenus[i]);

      final int lnCountComp = loMenu.getMenuComponentCount();
      for (int lnC = 0; lnC < lnCountComp; ++lnC)
      {
        final Component loItem = loMenu.getMenuComponent(lnC);
        if (loItem instanceof JMenuItem)
        {
          final JMenuItem loOldItem = (JMenuItem) loItem;
          final JMenuItem loMenuItem = new JMenuItem();

          loMenuItem.setText(loOldItem.getText());
          loMenuItem.setMnemonic(loOldItem.getMnemonic());
          loMenuItem.setToolTipText(loOldItem.getToolTipText());

          this.faDupMenus[i].add(loMenuItem);
        }
        else
        {
          this.faDupMenus[i].addSeparator();
        }
      }

    }

  }

  // ---------------------------------------------------------------------------------------------------------------------
  // I created this because there isn't a good reliable destructor procedure in
  // Java.
  public final void cleanUp()
  {
    this.finalOperations();
    this.removeListeners();
    this.releasePointers();
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void finalOperations()
  {
  }

  // ---------------------------------------------------------------------------------------------------------------------
  protected void removeListeners()
  {
  }
  // ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
