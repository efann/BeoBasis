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

import org.w3c.dom.Node;

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
public class SchemaWriter extends XMLTextWriter
{
  static public final String FIELDTYPE_STRING = "xsd:string";
  static public final String FIELDTYPE_NUMBER = "xsd:decimal";

  static public final String RECORDS_LABEL = "Records";

  private Object[][] faFields = null;
  private Node foSequence = null;
  private String fcSchemaID;

  // ---------------------------------------------------------------------------
  public SchemaWriter()
  {
  }

  // ---------------------------------------------------------------------------
  public SchemaWriter(final boolean tlDeleteOnExit)
  {
    super(tlDeleteOnExit);
  }

  // ---------------------------------------------------------------------------
  public void setSchemaID(final String tcSchemaID)
  {
    this.fcSchemaID = tcSchemaID;
  }

  // ---------------------------------------------------------------------------
  public boolean setFields(final Object[][] taFields)
  {
    this.faFields = taFields;
    boolean llOkay = true;

    try
    {
      for (final Object[] laField : this.faFields)
      {
        final int lnCols = laField.length;
        if (lnCols != 4)
        {
          throw new Exception("Field array should have 4 columns Field Name, Field Type, Field Length, Field Decimals.");
        }

        if (!((laField[0] instanceof String) && (laField[1] instanceof String)
                && (laField[2] instanceof Integer) && (laField[3] instanceof Integer)))
        {
          throw new Exception("Field array should have 4 columns Field Name, Field Type, Field Length, Field Decimals.");
        }

      }
    }
    catch (final Exception loErr)
    {
      llOkay = false;
      Util.errorMessage(null, loErr.getMessage());
    }

    return (llOkay);
  }

  // ---------------------------------------------------------------------------
  // Could this be any more convoluted?
  private void createSchemaBody()
  {

    try
    {
      this.createRootNode("xsd:schema", new Object[][]{{"id", this.fcSchemaID},
              {"xmlns:xsd", "http://www.w3.org/2001/XMLSchema"},
              {"xmlns:msdata", "urn:schemas-microsoft-com:xml-msdata"}});

      final Node loElementName = this.appendNodeToRoot("xsd:element", (String) null, new Object[][]{
              {"name", this.fcSchemaID}, {"msdata:IsDataSet", "true"}});

      final Node loComplexType1 = this.appendToNode(loElementName, "xsd:complexType", (String) null, null);

      final Node loChoice = this.appendToNode(loComplexType1, "xsd:choice", (String) null, new Object[][]{{
              "maxOccurs", "unbounded"}});

      final Node loElementRecords = this.appendToNode(loChoice, "xsd:element", (String) null, new Object[][]{
              {"name", SchemaWriter.RECORDS_LABEL}, {"minOccurs", "0"}, {"maxOccurs", "unbounded"}});

      final Node loComplexType2 = this.appendToNode(loElementRecords, "xsd:complexType", (String) null, null);

      this.foSequence = this.appendToNode(loComplexType2, "xsd:sequence", (String) null, null);

      this.appendToNode(loComplexType1, "xsd:anyAttribute", (String) null, new Object[][]{
              {"namespace", "http://www.w3.org/XML/1998/namespace"}, {"processContents", "lax"}});
    }
    catch (final Exception loErr)
    {
      Util.errorMessage(null, loErr.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  private void appendFieldsToSequenceNode()
  {
    try
    {
      if (this.foSequence == null)
      {
        throw new Exception("this.foSequence is null in appendFieldsToSequenceNode.");
      }

      final Node loSequence = this.foSequence;

      for (final Object[] laField : this.faFields)
      {
        final Node loName = this.appendToNode(loSequence, "xsd:element", (String) null, new Object[][]{{"name",
                laField[0]}});

        final Node loSimpleType = this.appendToNode(loName, "xsd:simpleType", (String) null, null);

        final String lcFieldType = laField[1].toString();
        final Node loRestriction = this.appendToNode(loSimpleType, "xsd:restriction", (String) null, new Object[][]{{
                "base", lcFieldType}});

        if (lcFieldType.compareTo(SchemaWriter.FIELDTYPE_STRING) == 0)
        {
          this.appendToNode(loRestriction, "xsd:maxLength", (String) null, new Object[][]{{"value",
                  laField[2].toString()}});
        }
        else if (lcFieldType.compareTo(SchemaWriter.FIELDTYPE_NUMBER) == 0)
        {
          this.appendToNode(loRestriction, "xsd:totalDigits", (String) null, new Object[][]{{"value",
                  laField[2].toString()}});
          this.appendToNode(loRestriction, "xsd:fractionDigits", (String) null, new Object[][]{{"value",
                  laField[3].toString()}});
        }
        else
        {
          throw new Exception(lcFieldType + " is unrecognized in appendFieldsToSequenceNode.");
        }

      }
    }
    catch (final Exception loErr)
    {
      Util.errorMessage(null, loErr.getMessage());
    }

  }

  // ---------------------------------------------------------------------------
  public boolean generateSchema(final String tcFileName, final int tnIndent)
  {
    final boolean llOkay = true;

    this.initializeXMLDocument();
    this.createSchemaBody();
    this.appendFieldsToSequenceNode();

    if (!this.saveToFile(tcFileName, tnIndent))
    {
      Util.errorMessage(null, "Unable to save the schema file of " + tcFileName);
    }

    return (llOkay);
  }
  // ---------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------
