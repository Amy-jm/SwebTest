package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.controllers.BaseMethod;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.untils.BrowserUtils;
import com.yeastar.untils.DataUtils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;
    public SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception
    {
        log.info("====== [SetUp] " + getTestName(method) + " [Times] " + DataUtils
                .getCurrentTime("yyyy-MM-dd hh:mm:ss") +
                "======");
        webDriver = initialDriver(BROWSER,PBX_URL,method);
        setDriver(webDriver);
        open(PBX_URL);
        auto = new PageEngine();
        softAssert = new SoftAssert();
//        pjsip.Pj_Init();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) throws Exception
    {
        sleep(5000);
//        pjsip.Pj_Destory();
        new BrowserUtils().getLogType_Browser(method,webDriver);
        getWebDriver().quit();

        log.info( "****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
    }

    /**
     * 设置cdr名称显示格式
     */
    public void preparationStepNameDisplay(){
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferences().selectCombobox(IPreferencesPageElement.NAME_DISPLAY_FORMAT.FIRST_LAST_WITH_SPACE.getAlias()).clickSaveAndApply();
    }

    /**
     * admin 登录
     */
    public void loginWithAdmin(){
        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
    }

}
