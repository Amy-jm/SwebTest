package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.controllers.BaseMethod;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.untils.*;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;


@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;
    public SoftAssert softAssert;
    public SoftAssertions softAssertPlus = new SoftAssertions();

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method){
        log.info("\r\n====== [SetUp] " + getTestName(method) + " [Times] " + DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss") + "======");

        webDriver = initialDriver(BROWSER,PBX_URL,method);
        setDriver(webDriver);
        log.debug("[Test PBX_URL]"+PBX_URL);
        open(PBX_URL);
        auto = PageEngine.getInstance();

        softAssert = new SoftAssert();
        softAssertPlus = new SoftAssertions();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) {
        log.info("\r\n====== [afterMethod] " + getTestName(method) + " [Times] " + DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss") + "======");

        if(EmptyUtil.isNotEmpty(pjsip)){
            log.debug("[start destroy pjsip]");
            pjsip.Pj_Destory();
            log.debug("[end destroy pjsip]");
        }

        log.debug("[remote session]{}",webDriver);
        try{
            if(EmptyUtil.isNotEmpty(webDriver)){
                BrowserUtils.getInstance().getLogType_Browser(method,webDriver);
                webDriver.quit();
            }
        }catch(Exception ex){
            log.error("[driver quite exception]"+ex.getMessage()+ex.getStackTrace());
        }
        log.debug("[clean remote session to null]{}",webDriver);

        log.info( "\r\n****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
    }

    /**
     * clean zalenium cheome session
     * @return
     */
    public String debugCleanSession(){
        String host = "192.168.3.252";
        int port = 22;
        String user = "root";
        String password = "r@@t";
        String command = "curl -sSL http://localhost:4444/grid/sessions?action=doCleanupActiveSessions";
        String result = "";

        try {
            result =  SSHLinuxUntils.exeCommand(host,port,user,password,command);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 设置cdr名称显示格式
     */
    public void preparationStepNameDisplay(){
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().selectCombobox(IPreferencesPageElement.NAME_DISPLAY_FORMAT.FIRST_LAST_WITH_SPACE.getAlias())
                .setElementValue(IPreferencesPageElement.ele_pbx_settings_preferences_max_call_duration_select,"1800")
                .clickSaveAndApply();
    }

    /**
     * admin 登录
     */
    public void loginWithAdmin(){
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
    }

}
