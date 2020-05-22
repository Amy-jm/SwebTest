
package com.yeastar.pageObject.pSeries;


public class PageEngine {
    LoginPage loginPage ;
    HomePage homePage;
    ExtensionPage extensionPage;

    public PageEngine() {

        loginPage = new LoginPage();
        homePage =  new HomePage();
    }

    public LoginPage loginPage() { return loginPage; }
    public HomePage homePage() { return homePage; }
    public ExtensionPage extensionPage() { return extensionPage; }

//    public  LoginPage loginPage(){}


}
