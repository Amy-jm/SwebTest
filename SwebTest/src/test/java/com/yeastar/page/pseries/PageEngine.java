
package com.yeastar.page.pseries;


public class PageEngine {
    LoginPage loginPage ;
    HomePage homePage;
    ExtensionPage extensionPage;
    TrunkPage trunkPage;
    BasePage basePage;
    CDRPage cdrPage;

    public PageEngine() {
        basePage = new BasePage();
        loginPage = new LoginPage();
        homePage =  new HomePage();
        extensionPage = new ExtensionPage();
        trunkPage = new TrunkPage();
        cdrPage = new CDRPage();
    }
    public BasePage basePage() { return basePage; }
    public LoginPage loginPage() { return loginPage; }
    public HomePage homePage() { return homePage; }
    public ExtensionPage extensionPage() { return extensionPage; }
    public TrunkPage trunkPage() {return  trunkPage; }
    public CDRPage cdrPage() { return cdrPage; }

//    public  LoginPage loginPage(){}


}
