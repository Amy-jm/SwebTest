
package com.yeastar.page.pseries;


import com.yeastar.page.pseries.CallControl.BusinessHoursAndHolidaysPage;
import com.yeastar.page.pseries.CallControl.InboundRoute;
import com.yeastar.page.pseries.CallControl.OutBoundRoutePage;
import com.yeastar.page.pseries.CallFeatures.IVRPage;
import com.yeastar.page.pseries.CallFeatures.QueuePage;
import com.yeastar.page.pseries.CallFeatures.RingGroupPage;
import com.yeastar.page.pseries.CdrRecording.CDRPage;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.page.pseries.ExtensionTrunk.TrunkPage;
import com.yeastar.page.pseries.PbxSettings.PreferencesPage;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import org.testng.asserts.SoftAssert;

public class PageEngine {
    LoginPage loginPage ;
    HomePage homePage;
    ExtensionPage extensionPage;
    TrunkPage trunkPage;
    InboundRoute inboundRoutePage;
    BasePage basePage;
    CDRPage cdrPage;
    PreferencesPage preferencesPage;
    OutBoundRoutePage outBoundRoutePage;
    BusinessHoursAndHolidaysPage businessHoursAndHolidaysPage;
    EmailPage emailPage;
    RingGroupPage ringGroupPage;
    IVRPage ivrPage;
    QueuePage queuePage;
    Me_HomePage me_homePage;

    public PageEngine() {
        basePage = new BasePage();
        loginPage = new LoginPage();
        homePage =  new HomePage();
        extensionPage = new ExtensionPage();
        trunkPage = new TrunkPage();
        inboundRoutePage = new InboundRoute();
        cdrPage = new CDRPage();
        preferencesPage = new PreferencesPage();
        outBoundRoutePage = new OutBoundRoutePage();
        businessHoursAndHolidaysPage = new BusinessHoursAndHolidaysPage();
        emailPage = new EmailPage();
        ringGroupPage = new RingGroupPage();
        ivrPage = new IVRPage();
        queuePage = new QueuePage();
        me_homePage = new Me_HomePage();

    }
    public BasePage basePage() { return basePage; }
    public LoginPage loginPage() { return loginPage; }
    public HomePage homePage() { return homePage; }
    public ExtensionPage extensionPage() { return extensionPage; }
    public TrunkPage trunkPage() {return  trunkPage; }
    public InboundRoute inboundRoute() {return inboundRoutePage;}
    public CDRPage cdrPage() { return cdrPage; }
    public RingGroupPage ringGroupPage(){return ringGroupPage;}
    public IVRPage ivrPage(){return ivrPage;}
    public QueuePage queuePage(){return queuePage;}
    public PreferencesPage preferencesPage() { return preferencesPage; }
    public OutBoundRoutePage outBoundRoutePage(){return outBoundRoutePage;}
    public BusinessHoursAndHolidaysPage businessHoursAndHoildaysPage(){return businessHoursAndHolidaysPage;}
    public EmailPage emailPage(){return emailPage;}
    public Me_HomePage me_homePage() { return me_homePage; }
}
