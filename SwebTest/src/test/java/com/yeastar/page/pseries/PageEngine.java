
package com.yeastar.page.pseries;


import com.yeastar.page.pseries.CallControl.InboundRoute;
import com.yeastar.page.pseries.CdrRecording.CDRPage;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.page.pseries.ExtensionTrunk.TrunkPage;
import com.yeastar.page.pseries.PbxSettings.Preferences;
import com.yeastar.page.pseries.WebClient.Me_HomePage;

public class PageEngine {
    LoginPage loginPage ;
    HomePage homePage;
    ExtensionPage extensionPage;
    TrunkPage trunkPage;
    InboundRoute inboundRoutePage;
    BasePage basePage;
    CDRPage cdrPage;
    Preferences preferences;
    Me_HomePage me_homePage;

    public PageEngine() {
        basePage = new BasePage();
        loginPage = new LoginPage();
        homePage =  new HomePage();
        extensionPage = new ExtensionPage();
        trunkPage = new TrunkPage();
        inboundRoutePage = new InboundRoute();
        cdrPage = new CDRPage();
        preferences = new Preferences();
        me_homePage = new Me_HomePage();
    }
    public BasePage basePage() { return basePage; }
    public LoginPage loginPage() { return loginPage; }
    public HomePage homePage() { return homePage; }
    public ExtensionPage extensionPage() { return extensionPage; }
    public TrunkPage trunkPage() {return  trunkPage; }
    public InboundRoute inboundRoute() {return inboundRoutePage;}
    public CDRPage cdrPage() { return cdrPage; }

    public Preferences preferences(){return preferences;}
    public Me_HomePage me_homePage() { return me_homePage; }


//    public  LoginPage loginPage(){}


}
