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

package com.beowurks.BeoCommon.Dialogs.About;

import java.awt.image.BufferedImage;

// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
public class AboutAdapter implements IAbout
{
  private final String fcTitle;
  private final String fcTitleURL;

  private final BufferedImage foLogo;
  private final String fcLogoURL;

  private final String fcLicense;
  private final String fcLicenseURL;

  private final int fnCopyrightStartYear;
  private final String fcCopyrightCompany;
  private final String fcCopyrightCompanyURL;

  // ---------------------------------------------------------------------------------------------------------------------
  public AboutAdapter(final String tcTitle, final String tcTitleURL,
                      final BufferedImage toLogo, final String tcLogoURL,
                      final String tcLicense, final String tcLicenseURL,
                      final int tnCopyrightStartYear, final String tcCopyrightCompany, final String tcCopyrightCompanyURL)
  {
    this.fcTitle = tcTitle;
    this.fcTitleURL = tcTitleURL;

    this.foLogo = toLogo;
    this.fcLogoURL = tcLogoURL;

    this.fcLicense = tcLicense;
    this.fcLicenseURL = tcLicenseURL;

    this.fnCopyrightStartYear = tnCopyrightStartYear;
    this.fcCopyrightCompany = tcCopyrightCompany;
    this.fcCopyrightCompanyURL = tcCopyrightCompanyURL;
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getTitle()
  {
    return (this.fcTitle);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getTitleURL()
  {
    return (this.fcTitleURL);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public BufferedImage getLogo()
  {
    return (this.foLogo);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getLogoURL()
  {
    return (this.fcLogoURL);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getLicense()
  {
    return (this.fcLicense);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getLicenseURL()
  {
    return (this.fcLicenseURL);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public int getCopyrightStartYear()
  {
    return (this.fnCopyrightStartYear);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getCopyrightCompany()
  {
    return (this.fcCopyrightCompany);
  }

  // ---------------------------------------------------------------------------------------------------------------------
  @Override
  public String getCopyrightCompanyURL()
  {
    return (this.fcCopyrightCompanyURL);
  }
// ---------------------------------------------------------------------------------------------------------------------
}
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
