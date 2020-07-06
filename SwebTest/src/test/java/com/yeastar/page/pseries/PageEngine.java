
package com.yeastar.page.pseries;


public class PageEngine {
    LoginPage loginPage ;
    HomePage homePage;
    ExtensionPage extensionPage;
    BasePage basePage;
    CDRPage cdrPage;
    PreferencesPage preferencesPage;
    OutBoundRoutePage outBoundRoutePage;
    BusinessHoursAndHolidaysPage businessHoursAndHolidaysPage;
    EmailPage emailPage;


    public PageEngine() {
        basePage = new BasePage();
        loginPage = new LoginPage();
        homePage =  new HomePage();
        extensionPage = new ExtensionPage();
        cdrPage = new CDRPage();
        preferencesPage = new PreferencesPage();
        outBoundRoutePage = new OutBoundRoutePage();
        businessHoursAndHolidaysPage = new BusinessHoursAndHolidaysPage();
        emailPage = new EmailPage();

    }
    public BasePage basePage() { return basePage; }
    public LoginPage loginPage() { return loginPage; }
    public HomePage homePage() { return homePage; }
    public ExtensionPage extensionPage() { return extensionPage; }
    public CDRPage cdrPage() { return cdrPage; }
    public PreferencesPage preferencesPage() { return preferencesPage; }
    public OutBoundRoutePage outBoundRoutePage(){return outBoundRoutePage;}
    public BusinessHoursAndHolidaysPage businessHoursAndHoildaysPage(){return businessHoursAndHolidaysPage;}
    public EmailPage emailPage(){return emailPage;}


}
