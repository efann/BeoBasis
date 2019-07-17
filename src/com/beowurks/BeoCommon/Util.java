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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.FontUIResource;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public final class Util
{
  static public final String CONTACTS_SEPARATOR = ";;";
  static public final String MANI_SEPARATOR = System.getProperty("path.separator");

  static private final String LINE_SEPARATOR_CHAR = System.getProperty("line.separator");

  static protected final String TITLE_VALUE = "com.beowurks.title";
  static protected final String DEFAULT_TITLE = "Software by Beowurks";

  static private final String MANI_SPEC_TITLE = "Specification-Title";
  static private final String MANI_SPEC_VENDOR = "Specification-Vendor";
  static private final String MANI_SPEC_VERSION = "Specification-Version";
  static private final String MANI_IMP_TITLE = "Implementation-Title";
  static private final String MANI_IMP_VENDOR = "Implementation-Vendor";
  static private final String MANI_IMP_VERSION = "Implementation-Version";

  static private final int WORD_WRAP_CONSTANT = 100;

  // Used by the extractDirectory and extractFileName routines
  static private final String FILE_SEPARATOR_CHAR = System.getProperty("file.separator");

  // Used by the isWindows routine
  static private final String OPERATING_SYSTEM = System.getProperty("os.name");

  // Since getMemoryStatus is called multiple times in the AboutAdapter form,
  // I decided to not recreate the variables over and over again and moved
  // them to here.
  // Used in getMemoryStatus.
  static private final DecimalFormat foMemoryDecimalFormat = new DecimalFormat("#,###");
  static private final StringBuilder fcMemoryDisplay = new StringBuilder(80);

  // Used in toProperCase
  static private final StringBuilder fcProperCase = new StringBuilder(256);

  // Used in Replace
  static private final StringBuilder fcReplace = new StringBuilder(256);

  // Used by the Padding routines
  static private final StringBuilder fcPadding = new StringBuilder(256);

  // Used by the wrapWord routine
  static private final StringBuilder fcWrapWord = new StringBuilder(256);
  static private int fnWrapWordLength;

  // ---------------------------------------------------------------------------
  private Util()
  {
  }

  // ---------------------------------------------------------------------------
  static public byte[] binaryFileToBytes(final String tcFileName)
  {
    ByteArrayOutputStream loByteArrayOutputStream = null;

    try
    {
      final FileInputStream loFileInputStream = new FileInputStream(tcFileName);
      loByteArrayOutputStream = new ByteArrayOutputStream();

      final byte[] laBuffer = new byte[4096];
      int lnBytesRead = 0;

      do
      {
        lnBytesRead = loFileInputStream.read(laBuffer);

        if (lnBytesRead != -1)
        {
          loByteArrayOutputStream.write(laBuffer, 0, lnBytesRead);
        }
      }
      while (lnBytesRead != -1);

      loFileInputStream.close();
    }
    catch (final IOException loErr)
    {
      loErr.printStackTrace();
    }

    return (loByteArrayOutputStream != null ? loByteArrayOutputStream.toByteArray() : null);
  }

  // ---------------------------------------------------------------------------
  static public String buildTitleFromManifest(final java.lang.Object toObject)
  {
    final StringBuilder lcTitle = new StringBuilder(60);

    final Package loPackage = toObject.getClass().getPackage();

    final String lcImpTitle = loPackage.getImplementationTitle();
    final String lcImpVersion = loPackage.getImplementationVersion();

    // This should be null & null only if running from the development version.
    // JBuilder
    // runs from the class files rather than from the JAR file.
    if ((lcImpTitle == null) || (lcImpVersion == null))
    {
      try
      {
        final FileInputStream loStream = new FileInputStream(Util.includeTrailingBackslash(System
                .getProperty("user.dir")) + "src" + Util.FILE_SEPARATOR_CHAR + "META-INF" + Util.FILE_SEPARATOR_CHAR + "MANIFEST.MF");
        final Manifest loManifest = new Manifest(loStream);
        final Attributes loMap = loManifest.getMainAttributes();
        lcTitle.append(loMap.getValue(Util.MANI_IMP_TITLE)).append(" ").append(loMap.getValue(Util.MANI_IMP_VERSION));
        loStream.close();
      }
      catch (final java.io.IOException ignored)
      {
      }
    }
    else
    {
      lcTitle.append(lcImpTitle).append(" ").append(lcImpVersion);
    }

    return ((lcTitle.length() > 0) ? lcTitle.toString() : "<empty>");
  }

  // ---------------------------------------------------------------------------
  // Recursively adds to a StringBuilder. I got the basis for this code from
  // BasicOptionPaneUI.burstStringInto.
  // By the way, this is primitive line wrapping.
  static private void burstStringBuilderInto(final String tcString)
  {
    final int lnLength = tcString.length();

    if (lnLength > 0)
    {
      final int lnNewLineOffset = tcString.indexOf('\n');

      if (lnNewLineOffset != -1)
      {
        Util.burstStringBuilderInto(tcString.substring(0, lnNewLineOffset));
        Util.burstStringBuilderInto(tcString.substring(lnNewLineOffset + 1));
        return;
      }
      else
      {
        final int lnWrapWordLength = Util.fnWrapWordLength;
        if (lnLength > lnWrapWordLength)
        {
          int lnSpaceOffset = tcString.lastIndexOf(' ', lnWrapWordLength);

          if (lnSpaceOffset < 0)
          {
            lnSpaceOffset = tcString.indexOf(' ', lnWrapWordLength);
          }

          if ((lnSpaceOffset > 0) && (lnSpaceOffset < lnLength))
          {
            Util.burstStringBuilderInto(tcString.substring(0, lnSpaceOffset));
            Util.burstStringBuilderInto(tcString.substring(lnSpaceOffset + 1));
            return;
          }
        }
      }
    }

    Util.fcWrapWord.append(tcString);
    Util.fcWrapWord.append("\n");
  }

  // ---------------------------------------------------------------------------
  static public void bytesToBinaryFile(final byte[] taBytes, final String tcFileName)
  {
    try
    {
      final DataOutputStream loDataOutputStream = new DataOutputStream(new FileOutputStream(tcFileName));
      loDataOutputStream.write(taBytes);
      loDataOutputStream.close();
    }
    catch (final IOException loErr)
    {
      loErr.printStackTrace();
    }
  }

  // ---------------------------------------------------------------------------
  static public void cascadeWindows()
  {
    final Frame[] laFrames = Frame.getFrames();

    final int lnCount = laFrames.length;

    if (lnCount == 0)
    {
      return;
    }

    int lnStep = Toolkit.getDefaultToolkit().getScreenSize().height / lnCount;
    if (lnStep > 40)
    {
      lnStep = 40;
    }

    int lnX = lnStep;
    int lnY = lnStep;
    for (final Frame loXFrame : laFrames)
    {
      if (loXFrame instanceof JFrame)
      {
        final JFrame loFrame = (JFrame) loXFrame;
        if (loFrame.isVisible())
        {
          loFrame.setLocation(lnX, lnY);
          lnX += lnStep;
          lnY += lnStep;

          if (loFrame instanceof BaseFrame)
          {
            ((BaseFrame) loFrame).makeVisible(false);
          }
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  static public void cascadeWindows(final JDesktopPane toDesktopPane)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        final JInternalFrame[] laFrames = toDesktopPane.getAllFrames();
        final int lnCount = laFrames.length;

        if (lnCount == 0)
        {
          return;
        }

        // Find out how many visible frames there are.
        int lnVisibleFrames = 0;
        for (int i = lnCount - 1; i >= 0; i--)
        {
          if (!laFrames[i].isVisible())
          {
            continue;
          }

          ++lnVisibleFrames;

          try
          {
            if (laFrames[i].isIcon())
            {
              laFrames[i].setIcon(false);
            }
            else if (laFrames[i].isMaximum())
            {
              laFrames[i].setMaximum(false);
            }
          }
          catch (final Exception ignored)
          {
          }
        }

        // If there's only one visible JInternalFrame, then just center it.
        final boolean llCenter = (lnVisibleFrames == 1);

        int lnX = 0;
        int lnY = 0;
        int lnStep = 0;

        if (!llCenter)
        {
          // Use the first JInternalFrame to discover the title bar height
          final Rectangle loBounds = laFrames[0].getContentPane().getBounds();

          lnStep = laFrames[0].getHeight() - loBounds.height;
          if (laFrames[0].getBorder() != null)
          {
            lnStep -= laFrames[0].getBorder().getBorderInsets(laFrames[0]).top;
          }

          lnX = lnStep;
          lnY = lnStep;
        }

        // For some reason, toDesktopPane.getAllFrames puts the top-focused
        // window first.
        // So the top one should be set last.
        for (int i = lnCount - 1; i >= 0; i--)
        {
          if (!laFrames[i].isVisible())
          {
            continue;
          }

          if (!llCenter)
          {
            laFrames[i].setLocation(lnX, lnY);
          }

          if (laFrames[i] instanceof BaseInternalFrame)
          {
            ((BaseInternalFrame) laFrames[i]).makeVisible(llCenter);
          }

          if (!llCenter)
          {
            lnX += lnStep;
            lnY += lnStep;
          }
        }
      }
    });
  }

  // ---------------------------------------------------------------------------
  private static Dialog checkForModal(final Window toWindow)
  {
    final Window[] loChildren = toWindow.getOwnedWindows();
    final int lnCount = loChildren.length;

    Dialog loDialog = null;
    for (int i = 0; ((i < lnCount) && (loDialog == null)); i++)
    {
      if (loChildren[i] instanceof Dialog)
      {
        final Dialog loTemp = (Dialog) loChildren[i];
        if ((loTemp.isVisible()) && (loTemp.isModal()))
        {
          loDialog = Util.checkForModal(loTemp);
          if (loDialog == null)
          {
            loDialog = loTemp;
          }
        }
      }
    }

    return (loDialog);
  }

  // ---------------------------------------------------------------------------
  // Yeah, I know: it should be 0, lnLength-1. But according to the help,
  // Parameters:
  // start - The beginning index, inclusive.
  // end - The ending index, exclusive.
  // str - String that will replace previous contents.
  // end is exclusive. So really, end is the same thing as length of the string.
  static public void clearStringBuilder(final StringBuilder tcString)
  {
    final int lnLength = tcString.length();
    if (lnLength > 0)
    {
      tcString.delete(0, lnLength);
    }
  }

  // ---------------------------------------------------------------------------
  static public void closeAllWindows()
  {
    final Frame[] laFrames = Frame.getFrames();

    BaseFrame loMainFrame = null;

    // The first frame that is an instance of BaseFrame
    // should be the main frame.
    for (final Frame loXFrame : laFrames)
    {
      if (loXFrame instanceof BaseFrame)
      {
        loMainFrame = (BaseFrame) loXFrame;
        break;
      }
    }

    if (loMainFrame == null)
    {
      Util.errorMessage(null, "Unable to find the main window.");
      return;
    }

    for (final Frame loXFrame : laFrames)
    {
      if (!loXFrame.isVisible())
      {
        continue;
      }

      if (!(loXFrame instanceof BaseFrame))
      {
        continue;
      }

      final BaseFrame loFrame = (BaseFrame) loXFrame;

      if (loMainFrame == loFrame)
      {
        continue;
      }

      loFrame.closeWindow();
    }

    loMainFrame.makeVisible(true);
  }

  // ---------------------------------------------------------------------------
  static public void closeAllWindows(final JDesktopPane toDesktopPane)
  {
    final JInternalFrame[] laFrames = toDesktopPane.getAllFrames();

    for (final JInternalFrame loFrame : laFrames)
    {
      try
      {
        loFrame.setClosed(true);
      }
      catch (final Exception ignored)
      {
      }
    }
  }

  // ---------------------------------------------------------------------------
  static public String displayTimeDifference(final Date toBegin, final Date toEnd, final int tnDecimalPlaces)
  {
    final double lnMilliseconds = toEnd.getTime() - toBegin.getTime();
    final double lnConvert = lnMilliseconds / 1000.0;

    final int lnMinutes = (int) Math.floor(lnConvert / 60.0);
    final double lnDecimalPlaces = Math.pow(10, tnDecimalPlaces);
    // This way, lnSeconds will have at most tnDecimalPlaces significant figures
    // to the right
    // of the decimal.
    final double lnSeconds = Math.floor((lnConvert - (lnMinutes * 60.0)) * lnDecimalPlaces) / lnDecimalPlaces;

    // Basically, if the display is going to be anything other than 1.0000...
    // (e.g., 0.98992, 1.00001, etc) then I want the label for second to be
    // plural.
    final double lnCompare = Math.pow(10, -(lnDecimalPlaces + 1));
    final boolean llNotOne = (Math.abs(lnSeconds - 1.0) > lnCompare);

    final String lcString = lnMinutes + " minute" + ((lnMinutes != 1) ? "s" : "") + " and " + lnSeconds + " second"
            + ((llNotOne) ? "s" : "");

    return (lcString);
  }

  // ---------------------------------------------------------------------------
  static public boolean downloadWebFile(final String tcWebFile, final String tcLocalFile)
  {
    boolean llOkay = true;
    // If you don't replace the spaces with %20, you will get
    // "HTTP Error 400: Bad request" error
    final String lcWebFile = Util.replaceAll(tcWebFile, " ", "%20").toString();

    URL loURL = null;
    final String lcJustFileName = Util.extractFileName(lcWebFile, "/");

    try
    {
      loURL = new URL(lcWebFile);
    }
    catch (final MalformedURLException loErr)
    {
      llOkay = false;
      Util.errorMessage(null, "There was a Malformed URL Exception with the file, " + lcJustFileName + ".");
    }

    if (llOkay)
    {
      URLConnection loConnection = null;
      try
      {
        loConnection = loURL.openConnection();
        loConnection.setAllowUserInteraction(true);
      }
      catch (final IOException loErr)
      {
        llOkay = false;
        Util.errorMessage(null, "There was an IO Exception in connecting to " + lcJustFileName + ".");
      }

      if (llOkay)
      {
        InputStream loIn = null;
        FileOutputStream loOut = null;
        try
        {
          final byte[] lcBuffer = new byte[1024];

          loIn = loConnection.getInputStream();

          loOut = new FileOutputStream(tcLocalFile);

          int lnBytesRead;
          do
          {
            lnBytesRead = loIn.read(lcBuffer);

            if (lnBytesRead > 0)
            {
              loOut.write(lcBuffer, 0, lnBytesRead);
            }
          }
          while (lnBytesRead >= 0);
        }
        catch (final IOException loErr)
        {
          llOkay = false;
          Util.errorMessage(null, "There was an IO Exception in reading the file, " + lcJustFileName + ".");
        }

        if (loIn != null)
        {
          try
          {
            loIn.close();
          }
          catch (final IOException loErr)
          {
            llOkay = false;
            Util.errorMessage(null, "There was an IO Exception in closing the file, " + lcJustFileName + ".");
          }
        }

        if (loOut != null)
        {
          try
          {
            loOut.close();
          }
          catch (final IOException loErr)
          {
            llOkay = false;
            Util.errorMessage(null, "There was an IO Exception in closing the file, " + lcJustFileName + ".");
          }
        }

      }
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  public static void showStackTraceInMessage(final Component toParent, final Exception toException,
                                             final String tcTitle)
  {
    final StringBuilder loMessage = new StringBuilder();
    final String lcReturn = System.getProperty("line.separator");

    final StackTraceElement[] laStack = toException.getStackTrace();

    for (final StackTraceElement loStack : laStack)
    {
      loMessage.append(loStack);
      loMessage.append(lcReturn);
    }

    final JTextArea loTextArea = new JTextArea();
    loTextArea.setEditable(false);
    loTextArea.setWrapStyleWord(true);
    loTextArea.setLineWrap(true);

    loTextArea.setText(loMessage.toString());
    loTextArea.setCaretPosition(0);

    final JScrollPane loScrollPane = new JScrollPane();
    loScrollPane.getViewport().add(loTextArea, null);
    final Dimension loScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    loScrollPane.setPreferredSize(new Dimension((int) (loScreenSize.width / 2.5), (int) (loScreenSize.height / 2.5)));

    final JOptionPane loOptionPane = new JOptionPane(new Object[]{"<html><b>An error occurred:</b> " + toException.getMessage() + "</html>", loScrollPane}, JOptionPane.ERROR_MESSAGE);
    Util.setOptionPaneComponents(loOptionPane, null);

    final JDialog loDialog = loOptionPane.createDialog(toParent, tcTitle);

    loDialog.setResizable(true);
    loDialog.setVisible(true);
    loDialog.dispose();
  }

  // ---------------------------------------------------------------------------
  static public void errorMessage(final Component toParent, final Object toMessage)
  {
    Util.displayMessage(toParent, toMessage, JOptionPane.ERROR_MESSAGE);
  }

  // ---------------------------------------------------------------------------
  static public void errorMessageInThread(final Component toParent, final Object toMessage)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        Util.errorMessage(toParent, toMessage);
      }
    });
  }

  // ---------------------------------------------------------------------------
  static public boolean yesNo(final Component toParent, final Object toMessage)
  {
    final String lcTitle = Util.getTitle();

    final JOptionPane loOption = new JOptionPane((toMessage instanceof String) ? Util.wrapWords((String) toMessage, Util.WORD_WRAP_CONSTANT) : toMessage,
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION);

    Util.setOptionPaneComponents(loOption, null);

    final JDialog loDialog = loOption.createDialog(toParent, lcTitle);

    loDialog.setVisible(true);

    final Object loReturn = loOption.getValue();

    if ((loReturn instanceof Integer) && ((Integer) loReturn == JOptionPane.YES_OPTION))
    {
      return (true);
    }

    return (false);
  }

  // ---------------------------------------------------------------------------
  static public void displayMessage(final Component toParent, final Object toMessage, final int tnMessageType)
  {
    final String lcTitle = Util.getTitle();

    Util.displayMessage(toParent, toMessage, lcTitle, tnMessageType);
  }

  // ---------------------------------------------------------------------------
  // The article at http://java.sun.com/developer/JDCTechTips/2004/tt0122.html#1
  // discusses adding Word Wrap. Only works with non-HTML strings.
  // BasicOptionPaneUI.burstStringInto splits the String into multiple
  // labels thus messing up any HTML code.
  static public void displayMessage(final Component toParent, final Object toMessage, final String tcTitle,
                                    final int tnMessageType)
  {
    Component loParent = toParent;
    if (loParent == null)
    {
      loParent = Util.getTopModalWindow();
      if (loParent == null)
      {
        loParent = JOptionPane.getRootFrame();
      }
    }

    if (toMessage instanceof String)
    {
      Util.showMessageDialog(loParent, Util.wrapWords((String) toMessage, Util.WORD_WRAP_CONSTANT), tcTitle, tnMessageType);

      return;
    }

    if (toMessage instanceof JLabel)
    {
      final JLabel loLabel = (JLabel) toMessage;
      final String lcText = loLabel.getText();
      // If the text is not html code, then wrap it.
      if (!lcText.toLowerCase().contains("<html>"))
      {
        loLabel.setText(Util.wrapWords(lcText, Util.WORD_WRAP_CONSTANT));
        Util.showMessageDialog(loParent, loLabel, tcTitle, tnMessageType);

        return;
      }
    }

    Util.showMessageDialog(loParent, toMessage, tcTitle, tnMessageType);
  }

  // -----------------------------------------------------------------------------
  static public void showMessageDialog(final Component toParent, final Object toMessage, final String tcTitle, final int tnMessageType)
  {
    final JOptionPane loOptionPane = new JOptionPane(toMessage, tnMessageType);
    Util.setOptionPaneComponents(loOptionPane, null);

    final JDialog loDialog = loOptionPane.createDialog(toParent, tcTitle);

    loDialog.setVisible(true);
  }

  // -----------------------------------------------------------------------------
  static public Object showInputDialog(final Component toParent, final Object[] taComponents, final JComponent toFocus, final String tcTitle, final int tnType, final int tnOptions)
  {
    final JOptionPane loOptionPane = new JOptionPane(taComponents, tnType, tnOptions);
    Util.setOptionPaneComponents(loOptionPane, toFocus);

    final JDialog loDialog = loOptionPane.createDialog(toParent, tcTitle);

    loDialog.setVisible(true);

    return (loOptionPane.getValue());
  }

  // -----------------------------------------------------------------------------
  static public void setOptionPaneComponents(final Container toContainer, final JComponent toFocusRequest)
  {
    final Component[] laComponents = toContainer.getComponents();
    for (final Component loComponent : laComponents)
    {
      if (loComponent instanceof JPanel)
      {
        Util.setOptionPaneComponents((JPanel) loComponent, toFocusRequest);
      }
      else if (loComponent instanceof JButton)
      {
        loComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      if (loComponent == toFocusRequest)
      {
        // From https://tips4java.wordpress.com/2010/03/14/dialog-focus/
        toFocusRequest.addAncestorListener(new AncestorListener()
        {
          @Override
          public void ancestorAdded(final AncestorEvent toEvent)
          {
            toEvent.getComponent().requestFocusInWindow();
          }

          @Override
          public void ancestorRemoved(final AncestorEvent toEvent)
          {
            toEvent.getComponent().requestFocusInWindow();
          }

          @Override
          public void ancestorMoved(final AncestorEvent toEvent)
          {
            toEvent.getComponent().requestFocusInWindow();
          }
        });

      }

    }
  }

  // ---------------------------------------------------------------------------
  // Recursively adds to a StringBuilder. I got this code from
  // BasicOptionPaneUI.burstStringInto.
  static public String wrapWords(final String tcString, final int tnWrapWordLength)
  {
    Util.clearStringBuilder(Util.fcWrapWord);

    // There are a lot of hidden characters where HTML code is
    // involved. So add some characters to cover.
    Util.fnWrapWordLength = tnWrapWordLength;

    Util.burstStringBuilderInto(tcString);

    return (Util.fcWrapWord.toString());
  }

  // ---------------------------------------------------------------------------
  // Get the directory of a file.
  static public String extractDirectory(final String tcFileName)
  {
    return (Util.extractDirectory(tcFileName, Util.FILE_SEPARATOR_CHAR));
  }

  // ---------------------------------------------------------------------------
  // Get the directory of a file.
  static public String extractDirectory(final String tcFileName, final String tcFileSeparatorChar)
  {
    final int lnIndex = tcFileName.lastIndexOf(tcFileSeparatorChar);

    // Return the backslash.
    if (lnIndex >= 0)
    {
      return (tcFileName.substring(0, lnIndex + 1));
    }

    return (null);
  }

  // ---------------------------------------------------------------------------
  // Get the extension of a file.
  static public String extractFileExtension(final File toFile)
  {
    return (Util.extractFileExtension(toFile.getName()));
  }

  // ---------------------------------------------------------------------------
  // Get the extension of a file.
  static public String extractFileExtension(final String tcFileName)
  {
    String lcExt = null;
    final String lcName = tcFileName;

    final int lnPos = lcName.lastIndexOf('.');

    if ((lnPos > 0) && (lnPos < (lcName.length() - 1)))
    {
      lcExt = lcName.substring(lnPos + 1);
    }

    return (lcExt);
  }

  // ---------------------------------------------------------------------------
  // Get the file name from a path.
  static public String extractFileName(final String tcFileName)
  {
    return (Util.extractFileName(tcFileName, Util.FILE_SEPARATOR_CHAR));
  }

  // ---------------------------------------------------------------------------
  // Get the file name from a path.
  static public String extractFileName(final String tcFileName, final String tcFileSeparatorChar)
  {
    final int lnIndex = tcFileName.lastIndexOf(tcFileSeparatorChar);

    // Return the backslash.
    if (lnIndex >= 0)
    {
      return (tcFileName.substring(lnIndex + 1));
    }

    return (tcFileName);
  }

  // ---------------------------------------------------------------------------
  // Get the file name without the extension from a path.
  static public String extractFileStem(final String tcFileName)
  {
    return (Util.extractFileStem(tcFileName, Util.FILE_SEPARATOR_CHAR));
  }

  // ---------------------------------------------------------------------------
  // Get the file name without the extension from a path.
  static public String extractFileStem(final String tcFileName, final String tcFileSeparatorChar)
  {
    final String lcFileName = Util.extractFileName(tcFileName, tcFileSeparatorChar);

    final int lnIndex = lcFileName.lastIndexOf('.');

    // Return only the file name without the extension or period.
    if (lnIndex >= 0)
    {
      return (lcFileName.substring(0, lnIndex));
    }

    return (lcFileName);
  }

  // ---------------------------------------------------------------------------
  static public boolean fileCopy(final String tcSource, final String tcDestination)
  {
    FileInputStream loIn = null;
    FileOutputStream loOut = null;

    boolean llOkay = true;

    try
    {
      loIn = new FileInputStream(tcSource);
      loOut = new FileOutputStream(tcDestination);
    }
    catch (final FileNotFoundException loErr)
    {
      llOkay = false;
    }

    if (llOkay)
    {
      final byte[] lcBuffer = new byte[8192];

      int lnBytesRead;
      do
      {
        try
        {
          lnBytesRead = loIn.read(lcBuffer);
          if (lnBytesRead > 0)
          {
            loOut.write(lcBuffer, 0, lnBytesRead);
          }
        }
        catch (final IOException loErr)
        {
          llOkay = false;
          break;
        }
      }
      while (lnBytesRead >= 0);
    }

    if (loIn != null)
    {
      try
      {
        loIn.close();
      }
      catch (final IOException loErr)
      {
        llOkay = false;
      }
    }

    if (loOut != null)
    {
      try
      {
        loOut.close();
      }
      catch (final IOException loErr)
      {
        llOkay = false;
      }
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  // This only works with text files . . . as the name implies.
  static public String fileTextToString(final String tcFileName)
  {
    final StringBuilder lcFileString = new StringBuilder();

    final File loFile = new File(tcFileName);

    if (loFile.exists())
    {
      BufferedReader loBufferReader = null;

      try
      {
        loBufferReader = new BufferedReader(new FileReader(loFile));

        String lcLine = null;

        while ((lcLine = loBufferReader.readLine()) != null)
        {
          lcFileString.append(lcLine);
          lcFileString.append(Util.LINE_SEPARATOR_CHAR);
        }
      }
      catch (final IOException loErr)
      {
        loErr.printStackTrace();
      }
      finally
      {
        if (loBufferReader != null)
        {
          try
          {
            loBufferReader.close();
          }
          catch (final IOException loErr)
          {
            loErr.printStackTrace();
          }
        }
      }
    }

    return (lcFileString.toString());
  }

  // ---------------------------------------------------------------------------
  static public Component findObjectInTree(final Component toComp, final Class<?> toClass)
  {
    Component loComp = null;
    Component[] laChildren = null;

    if (toClass.isInstance(toComp))
    {
      loComp = toComp;
    }
    else if (toComp instanceof Container)
    {
      laChildren = ((Container) toComp).getComponents();

      for (final Component loChildren : laChildren)
      {
        loComp = Util.findObjectInTree(loChildren, toClass);
        if (loComp != null)
        {
          break;
        }
      }
    }

    return (loComp);
  }

  // ---------------------------------------------------------------------------
  public static byte[] getKeyBytes(final String tcKey, final int tnFinalLength)
  {
    final byte[] laResults = new byte[tnFinalLength];
    for (int i = 0; i < tnFinalLength; ++i)
    {
      laResults[i] = ' ';
    }

    try
    {
      final byte[] laKey = tcKey.getBytes("UTF-8");
      final int lnLength = laKey.length;
      for (int i = 0; i < lnLength; ++i)
      {
        // Should keep wrapping around and XORing the values.
        final int lnMod = i % tnFinalLength;
        laResults[lnMod] = (i < tnFinalLength) ? laKey[i] : (byte) (laResults[lnMod] ^ laKey[i]);
      }

    }

    catch (final UnsupportedEncodingException ignored)
    {
    }

    return (laResults);
  }

  // ---------------------------------------------------------------------------
  // This routine returns the background color of a button which I use
  // for readonly grids, textboxes, etc.
  // Turns out that if you use this.getBackground before the frame or
  // dialog actually appear, the Background is that of the calling frame or
  // dialog. Which is fine, except when called from the mainframe which is
  // usually a different background.
  static public Color getButtonBackground()
  {
    final Color loCurrent = UIManager.getColor("Button.background");
    if (loCurrent != null)
    {
      return (loCurrent);
    }

    return (Color.gray);
  }

  // ---------------------------------------------------------------------------
  // Get the memory status
  static public String getMemoryStatus()
  {
    final long lnFree = Runtime.getRuntime().freeMemory();
    final long lnTotal = Runtime.getRuntime().totalMemory();
    final long lnUsed = lnTotal - lnFree;

    Util.clearStringBuilder(Util.fcMemoryDisplay);

    Util.fcMemoryDisplay.append(Util.foMemoryDecimalFormat.format(lnUsed)).append(" bytes used out of ")
            .append(Util.foMemoryDecimalFormat.format(lnTotal)).append(" (")
            .append(Util.foMemoryDecimalFormat.format(lnFree)).append(" bytes free)");

    return (Util.fcMemoryDisplay.toString());
  }

  // ---------------------------------------------------------------------------
  static public String getPackageVersion(final String tcFile)
  {
    final StringBuilder lcFullVersion = new StringBuilder(200);
    final StringBuilder lcFullFileName = new StringBuilder(200);

    // Here are examples on how to use URL:
    // URL manifURL = new
    // URL("jar:file:D:/java/PolyJenUpgrade/PolyJenUpgrade.jar!/META-INF/MANIFEST.MF");
    // URL manifURL = new
    // URL("jar:http://www.beowurks.com/Software/Upgrade/PolyJenUpgrade.jar!/META-INF/MANIFEST.MF");
    // URL manifURL = new URL("file:d:/temp/10.jpg");

    final File loFile = new File(tcFile);
    if (loFile.exists())
    {
      lcFullFileName.append("jar:file:").append(loFile.getAbsolutePath()).append("!/META-INF/MANIFEST.MF");
    }
    else
    {
      lcFullFileName.append("jar:");
      final String lcWebHeader = "http://";
      if (!tcFile.startsWith(lcWebHeader))
      {
        lcFullFileName.append(lcWebHeader);
      }

      lcFullFileName.append(tcFile).append("!/META-INF/MANIFEST.MF");
    }

    try
    {
      final URL loManiURL = new URL(lcFullFileName.toString());
      try
      {
        final InputStream loInput = loManiURL.openStream();
        final Manifest loManifest = new Manifest(loInput);
        final Attributes loMap = loManifest.getMainAttributes();
        lcFullVersion.append(loMap.getValue(Util.MANI_IMP_TITLE)).append(Util.MANI_SEPARATOR)
                .append(loMap.getValue(Util.MANI_IMP_VENDOR)).append(Util.MANI_SEPARATOR)
                .append(loMap.getValue(Util.MANI_IMP_VERSION)).append(Util.MANI_SEPARATOR)
                .append(loMap.getValue(Util.MANI_SPEC_TITLE)).append(Util.MANI_SEPARATOR)
                .append(loMap.getValue(Util.MANI_SPEC_VENDOR)).append(Util.MANI_SEPARATOR)
                .append(loMap.getValue(Util.MANI_SPEC_VERSION));

        loInput.close();
      }
      catch (final java.io.IOException ignored)
      {
      }
    }
    catch (final java.net.MalformedURLException ignored)
    {
    }

    return ((lcFullVersion.length() > 0) ? lcFullVersion.toString() : "<empty>");
  }

  // ---------------------------------------------------------------------------
  static public String getTitle()
  {
    String lcTitle = System.getProperty(Util.TITLE_VALUE);
    if (lcTitle == null)
    {
      lcTitle = Util.DEFAULT_TITLE;
    }

    return (lcTitle);
  }

  // ---------------------------------------------------------------------------
  public static Dialog getTopModalWindow()
  {
    final Frame[] loFrames = Frame.getFrames();
    final int lnFrameCount = loFrames.length;

    Dialog loDialog = null;
    for (int f = 0; ((f < lnFrameCount) && (loDialog == null)); ++f)
    {
      loDialog = Util.checkForModal(loFrames[f]);
    }

    return (loDialog);
  }

  // ---------------------------------------------------------------------------
  static public String includeTrailingBackslash(final String tcFileName)
  {
    final String lcSeparator = File.separator;
    final String lcFileName = tcFileName + (tcFileName.endsWith(lcSeparator) ? "" : lcSeparator);

    return (lcFileName);
  }

  // ---------------------------------------------------------------------------
  static public void infoMessage(final Component toComponent, final Object toMessage)
  {
    Util.displayMessage(toComponent, toMessage, JOptionPane.INFORMATION_MESSAGE);
  }

  // ---------------------------------------------------------------------------
  static public void infoMessageInThread(final Component toComponent, final Object toMessage)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        Util.infoMessage(toComponent, toMessage);
      }
    });
  }

  // ---------------------------------------------------------------------------
  // This function now returns a Container as that is highest order
  // component that JFrame and JInternalFrame have in common.
  static public Container isFrameObject(final Class<?> toClass)
  {
    final Frame[] loFrames = Frame.getFrames();
    Container loFoundContainer = null;

    final int lnFCount = loFrames.length;
    for (int lnF = 0; ((lnF < lnFCount) && (loFoundContainer == null)); ++lnF)
    {
      if ((loFrames[lnF].isVisible()) && (loFrames[lnF].getClass() == toClass))
      {
        loFoundContainer = loFrames[lnF];
      }
      else
      {
        // Look for a JDesktopPane that would contain JInternalFrames.
        final Component loDesktopPane = Util.findObjectInTree(loFrames[lnF], JDesktopPane.class);
        if (loDesktopPane != null)
        {
          final JInternalFrame[] laInternalFrames = ((JDesktopPane) loDesktopPane).getAllFrames();
          final int lnICount = laInternalFrames.length;
          for (int lnI = 0; ((lnI < lnICount) && (loFoundContainer == null)); ++lnI)
          {
            if ((laInternalFrames[lnI].isVisible()) && (laInternalFrames[lnI].getClass() == toClass))
            {
              loFoundContainer = laInternalFrames[lnI];
            }
          }
        }
      }
    }

    return (loFoundContainer);
  }

  // ---------------------------------------------------------------------------
  // From
  // http://developer.apple.com/library/mac/#documentation/Java/Conceptual/Java14Development/00-Intro/JavaDevelopment.html
  static public boolean isMacintosh()
  {
    return (Util.OPERATING_SYSTEM.toLowerCase().startsWith("mac"));
  }

  // ---------------------------------------------------------------------------
  static public boolean isWindows()
  {
    return (Util.OPERATING_SYSTEM.toLowerCase().contains("windows"));
  }

  // ---------------------------------------------------------------------------
  static public void launchBrowser(final String tcFile)
  {
    if (Desktop.isDesktopSupported())
    {
      String lcFile = tcFile.replaceAll("\\\\", "/");
      lcFile = lcFile.replaceAll(" ", "%20");

      try
      {
        Desktop.getDesktop().browse(new URI(lcFile));
      }
      catch (final IOException | URISyntaxException loErr)
      {
        Util.errorMessage(null,
                "Unfortunately, " + lcFile + " could not load for the following reason:\n\n" + loErr.getMessage());
      }
    }
    else
    {
      Util.errorMessage(null, "Unfortunately, this operating system cannot launch a browser.");
    }

  }

  // ---------------------------------------------------------------------------
  static public void launchEMail(final String tcAddress)
  {
    try
    {
      Desktop.getDesktop().mail(new URI("mailto:" + tcAddress));
    }
    catch (final Exception loErr)
    {
      Util.errorMessage(null, "Unable to launch Mail client.\n\n" + loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  // I got this from examining the jnlp file for the Application Manager on
  // Java Web Start Demos.
  static public void launchJavaApplicationManager()
  {
    final String lcPlayerFile = Util.includeTrailingBackslash(System.getProperty("user.home")) + "JavaApp.jnlp";
    final String lcNewLine = System.getProperty("line.separator");

    try
    {
      final FileWriter loWriter = new FileWriter(lcPlayerFile);
      loWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
      loWriter.write(lcNewLine);
      loWriter.write("<!-- Jump specific JNLP file for launching the player -->");
      loWriter.write(lcNewLine);
      loWriter.write("<!-- Apparently, this is an undocumented feature of Java Web Start -->");
      loWriter.write(lcNewLine);
      loWriter.write("<player/>");
      loWriter.close();

      Desktop.getDesktop().open(new File(lcPlayerFile));
    }
    catch (final Exception loErr)
    {
      Util.errorMessage(null, "Unable to launch the Java Application Manager\n\n" + loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  static public void listDefaultSettings()
  {
    final UIDefaults defaults = UIManager.getDefaults();
    final Enumeration<?> loEnum = defaults.keys();

    int lnCount = 0;
    for (lnCount = 0; loEnum.hasMoreElements(); ++lnCount)
    {
      loEnum.nextElement();
    }

    final String laSort[] = new String[lnCount];

    final Enumeration<?> enumlist = defaults.keys();
    for (int i = 0; i < lnCount; ++i)
    {
      final Object key = enumlist.nextElement();
      laSort[i] = key + ": " + defaults.get(key);
    }

    Arrays.sort(laSort);

    for (int i = 0; i < lnCount; ++i)
    {
      System.out.println(laSort[i]);
    }
  }

  // ---------------------------------------------------------------------------
  static public boolean makeDirectory(final String tcDirectory)
  {
    final File loFile = new File(tcDirectory);
    if (!loFile.exists())
    {
      try
      {
        loFile.mkdirs();
      }
      catch (final SecurityException ignored)
      {
      }
    }

    return (loFile.exists());
  }

  // ---------------------------------------------------------------------------
  static public boolean okCancel(final Component toComponent, final Object toMessage)
  {
    final String lcTitle = Util.getTitle();

    final int lnResults = JOptionPane.showConfirmDialog(toComponent,
            toMessage instanceof String ? Util.wrapWords((String) toMessage, Util.WORD_WRAP_CONSTANT) : toMessage, lcTitle,
            JOptionPane.OK_CANCEL_OPTION);

    return (lnResults == JOptionPane.OK_OPTION);
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder padLeft(final String tcString, final int tnNewLength)
  {
    return (Util.padLeft(tcString, tnNewLength, ' '));
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder padLeft(final String tcString, final int tnNewLength, final char tcChar)
  {
    Util.clearStringBuilder(Util.fcPadding);

    final int lnCurrentLength = tcString.length();
    if (lnCurrentLength > tnNewLength)
    {
      Util.fcPadding.append(tcString.substring(0, tnNewLength));
    }
    else if (lnCurrentLength < tnNewLength)
    {
      for (int i = lnCurrentLength; i < tnNewLength; ++i)
      {
        Util.fcPadding.append(tcChar);
      }

      Util.fcPadding.append(tcString);
    }
    else
    {
      Util.fcPadding.append(tcString);
    }

    return (Util.fcPadding);
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder padRight(final String tcString, final int tnNewLength)
  {
    return (Util.padRight(tcString, tnNewLength, ' '));
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder padRight(final String tcString, final int tnNewLength, final char tcChar)
  {
    Util.clearStringBuilder(Util.fcPadding);

    Util.fcPadding.append(tcString);

    final int lnCurrentLength = Util.fcPadding.length();
    if (lnCurrentLength > tnNewLength)
    {
      Util.fcPadding.setLength(tnNewLength);
    }
    else if (lnCurrentLength < tnNewLength)
    {
      for (int i = lnCurrentLength; i < tnNewLength; ++i)
      {
        Util.fcPadding.append(tcChar);
      }
    }

    return (Util.fcPadding);
  }

  // ---------------------------------------------------------------------------
  static public String readWebFile(final String tcWebFile)
  {
    return (Util.readWebFile(tcWebFile, null, null));
  }

  // ---------------------------------------------------------------------------
  static public String readWebFile(final String tcWebFile, final String tcPostData, final String tcContentType)
  {
    boolean llOkay = true;
    // If you don't replace the spaces with %20, you will get
    // "HTTP Error 400: Bad request" error
    final String lcWebFile = Util.replaceAll(tcWebFile, " ", "%20").toString();

    URL loURL = null;
    final String lcJustFileName = Util.extractFileName(lcWebFile, "/");
    final StringBuilder loContents = new StringBuilder();

    try
    {
      loURL = new URL(lcWebFile);
    }
    catch (final MalformedURLException loErr)
    {
      llOkay = false;
      Util.errorMessage(null, "There was a Malformed URL Exception with the file, " + lcJustFileName + ".");
    }

    if (llOkay)
    {
      URLConnection loConnection = null;
      try
      {
        loConnection = loURL.openConnection();

        loConnection.setDoInput(true);
        loConnection.setUseCaches(false);

        if ((tcPostData != null) && (tcContentType != null))
        {
          loConnection.setDoOutput(true);
          loConnection.setRequestProperty("Content-Type", tcContentType);
          loConnection.setRequestProperty("Content-Length", Integer.toString(tcPostData.length()));

          final OutputStreamWriter loWriter = new OutputStreamWriter(loConnection.getOutputStream());
          loWriter.write(tcPostData);
          loWriter.flush();
          loWriter.close();
        }
      }
      catch (final IOException loErr)
      {
        llOkay = false;
        Util.errorMessage(null, "There was an IO Exception in connecting to " + lcJustFileName + ".");
      }

      if (llOkay)
      {
        InputStream loIn = null;

        try
        {
          final byte[] lbBuffer = new byte[1024];
          loIn = loConnection.getInputStream();

          int lnBytesRead;
          do
          {
            lnBytesRead = loIn.read(lbBuffer);
            if (lnBytesRead > 0)
            {
              // It stuffs the entire length of lbBuffer into the String which
              // could be
              // longer than what was read.
              final String lcTempBuffer = new String(lbBuffer);
              loContents.append(lcTempBuffer.substring(0, lnBytesRead));
            }
          }
          while (lnBytesRead >= 0);
        }
        catch (final IOException loErr)
        {
          llOkay = false;
          Util.errorMessage(null, "There was an IO Exception in reading the file, " + lcJustFileName + ".");
        }

        if (loIn != null)
        {
          try
          {
            loIn.close();
          }
          catch (final IOException loErr)
          {
            llOkay = false;
            Util.errorMessage(null, "There was an IO Exception in closing the file, " + lcJustFileName + ".");
          }
        }
      }
    }

    return (loContents.toString());
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder replaceAll(final String tcString, final String tcSearch, final String tcReplace)
  {
    if (tcSearch.isEmpty())
    {
      throw new IndexOutOfBoundsException("tcSearch is empty in BeoCommon.Util.replaceAll.");
    }

    // Avoid the infinite that occurs with replacing, say, 'a' with 'aa'.
    final boolean llReduction = (tcSearch.length() > tcReplace.length());

    Util.clearStringBuilder(Util.fcReplace);

    Util.fcReplace.append(tcString);

    int lnIndex;
    int lnIndexFrom = 0;
    while ((lnIndex = Util.fcReplace.indexOf(tcSearch, lnIndexFrom)) != -1)
    {
      Util.fcReplace.replace(lnIndex, lnIndex + tcSearch.length(), tcReplace);

      lnIndexFrom = (llReduction) ? lnIndex : lnIndex + tcReplace.length();
    }

    return (Util.fcReplace);
  }

  // ---------------------------------------------------------------------------
  static public void replaceAll(final StringBuilder toString, final String tcSearch, final String tcReplace)
  {
    if (tcSearch.isEmpty())
    {
      throw new IndexOutOfBoundsException("tcSearch is empty in BeoCommon.Util.replaceAll.");
    }

    // Avoid the infinite that occurs with replacing, say, 'a' with 'aa'.
    final boolean llReduction = (tcSearch.length() > tcReplace.length());

    int lnIndex;
    int lnIndexFrom = 0;

    while ((lnIndex = toString.indexOf(tcSearch, lnIndexFrom)) != -1)
    {
      toString.replace(lnIndex, lnIndex + tcSearch.length(), tcReplace);

      lnIndexFrom = (llReduction) ? lnIndex : lnIndex + tcReplace.length();
    }
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder replaceAllIgnoreCase(final String tcString, final String tcSearch, final String tcReplace)
  {
    if (tcSearch.isEmpty())
    {
      throw new IndexOutOfBoundsException("tcSearch is empty in BeoCommon.Util.replaceAllIgnoreCase.");
    }

    // Avoid the infinite that occurs with replacing, say, 'a' with 'aa'.
    final boolean llReduction = (tcSearch.length() > tcReplace.length());

    Util.clearStringBuilder(Util.fcReplace);

    Util.fcReplace.append(tcString);

    int lnIndex;
    int lnIndexFrom = 0;
    while ((lnIndex = Util.fcReplace.toString().toLowerCase().indexOf(tcSearch.toLowerCase(), lnIndexFrom)) != -1)
    {
      Util.fcReplace.replace(lnIndex, lnIndex + tcSearch.length(), tcReplace);

      lnIndexFrom = (llReduction) ? lnIndex : lnIndex + tcReplace.length();
    }

    return (Util.fcReplace);
  }

  // ---------------------------------------------------------------------------
  static public void setTitle(final String tcValue)
  {
    System.setProperty(Util.TITLE_VALUE, tcValue);
  }

  // ---------------------------------------------------------------------------
  static public void setUIManagerAppearances()
  {
    // Don't change any defaults for Macintosh.
    if (Util.isMacintosh())
    {
      return;
    }

    Util.setUIManagerFontToPlain("MenuBar.font");
    Util.setUIManagerFontToPlain("MenuItem.font");
    Util.setUIManagerFontToPlain("RadioButtonMenuItem.font");
    Util.setUIManagerFontToPlain("CheckBoxMenuItem.font");
    Util.setUIManagerFontToPlain("Menu.font");
    Util.setUIManagerFontToPlain("PopupMenu.font");

    Util.setUIManagerFontToPlain("Button.font");
    Util.setUIManagerFontToPlain("Label.font");
    Util.setUIManagerFontToPlain("ComboBox.font");
    Util.setUIManagerFontToPlain("CheckBox.font");
    Util.setUIManagerFontToPlain("TextField.font");
    Util.setUIManagerFontToPlain("TextPane.font");
    Util.setUIManagerFontToPlain("TextArea.font");
    Util.setUIManagerFontToPlain("EditorPane.font");
    Util.setUIManagerFontToPlain("RadioButton.font");
    Util.setUIManagerFontToPlain("TabbedPane.font");

    Util.setUIManagerFontToPlain("CheckBoxMenuItem.acceleratorFont");
    Util.setUIManagerFontToPlain("ColorChooser.font");
    Util.setUIManagerFontToPlain("FileChooser.listFont");
    Util.setUIManagerFontToPlain("FormattedTextField.font");
    Util.setUIManagerFontToPlain("InternalFrame.titleFont");
    Util.setUIManagerFontToPlain("List.font");
    Util.setUIManagerFontToPlain("Menu.acceleratorFont");
    Util.setUIManagerFontToPlain("MenuItem.acceleratorFont");
    Util.setUIManagerFontToPlain("OptionPane.buttonFont");
    Util.setUIManagerFontToPlain("OptionPane.font");
    Util.setUIManagerFontToPlain("OptionPane.messageFont");
    Util.setUIManagerFontToPlain("Panel.font");
    Util.setUIManagerFontToPlain("PasswordField.font");
    Util.setUIManagerFontToPlain("ProgressBar.font");
    Util.setUIManagerFontToPlain("RadioButtonMenuItem.acceleratorFont");
    Util.setUIManagerFontToPlain("ScrollPane.font");
    Util.setUIManagerFontToPlain("Slider.font");
    Util.setUIManagerFontToPlain("Spinner.font");
    Util.setUIManagerFontToPlain("Table.font");
    Util.setUIManagerFontToPlain("TableHeader.font");
    Util.setUIManagerFontToPlain("TitledBorder.font");
    Util.setUIManagerFontToPlain("ToggleButton.font");
    Util.setUIManagerFontToPlain("ToolBar.font");
    Util.setUIManagerFontToPlain("ToolTip.font");
    Util.setUIManagerFontToPlain("Tree.font");
    Util.setUIManagerFontToPlain("Viewport.font");

    UIManager.put("Label.foreground", Color.black);
  }

  // ---------------------------------------------------------------------------
  // This routine sets all of the system fonts to plain rather than bold
  // as well as making Arial the default style.
  private static void setUIManagerFontToPlain(final String tcObjectKey)
  {
    try
    {
      final Font loCurrent = UIManager.getFont(tcObjectKey);

      final Font loFont = new Font("Arial", Font.PLAIN, loCurrent.getSize());
      // From https://tips4java.wordpress.com/2008/10/09/uimanager-defaults/
      UIManager.put(tcObjectKey, new FontUIResource(loFont));
    }
    catch (final NullPointerException ignored)
    {
    }
  }

  // ---------------------------------------------------------------------------
  // This only works with text files . . . as the name implies.
  static public void stringToFileText(final String tcExpression, final String tcFileName)
  {
    PrintWriter loPrintWriter = null;
    try
    {
      final FileWriter loFileWriter = new FileWriter(tcFileName);
      loPrintWriter = new PrintWriter(loFileWriter);

      loPrintWriter.print(tcExpression);
    }
    catch (final IOException loErr)
    {
      loErr.printStackTrace();
    }
    finally
    {
      if (loPrintWriter != null)
      {
        loPrintWriter.close();
      }
    }
  }

  // ---------------------------------------------------------------------------
  static public StringBuilder toProperCase(final String tcString)
  {
    Util.clearStringBuilder(Util.fcProperCase);

    final int lnLength = tcString.length();
    Util.fcProperCase.append(tcString.substring(0, 1).toUpperCase());
    boolean llUpper = false;
    for (int i = 1; i < lnLength; ++i)
    {
      final char lcChar = tcString.charAt(i);
      if (Character.isLetter(lcChar))
      {
        if (llUpper)
        {
          Util.fcProperCase.append(tcString.substring(i, i + 1).toUpperCase());
        }
        else
        {
          Util.fcProperCase.append(tcString.substring(i, i + 1).toLowerCase());
        }

        llUpper = false;
      }
      else
      {
        llUpper = true;
        Util.fcProperCase.append(lcChar);
      }
    }

    return (Util.fcProperCase);
  }

  // ---------------------------------------------------------------------------
// From http://stackoverflow.com/questions/642925/swing-how-do-i-close-a-dialog-when-the-esc-key-is-pressed
  public static void addEscapeListener(final JDialog toDialog)
  {
    final ActionListener loActionListener = new ActionListener()
    {

      @Override
      public void actionPerformed(final ActionEvent toActionEvent)
      {
        toDialog.setVisible(false);
      }
    };

    toDialog.getRootPane().registerKeyboardAction(loActionListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
  }
// ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
