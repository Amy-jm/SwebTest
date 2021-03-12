package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.controllers.BaseMethod;
import com.yeastar.page.pseries.PageEngine;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.swebtest.tools.pjsip.PjsipApp;
import com.yeastar.untils.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;

/**
 *
 *  TestCaseBase
 * 1.pjsip init 在setup进行初始化
 *
 * 【区别】TestCaseBaseNew
 * 1.新增 beforeClass and afterClass
 * 2.pjsip init 在beforeclass进行初始化
 */

@Log4j2
public class TestCaseBase extends BaseMethod {
    public PageEngine auto;
    private WebDriver webDriver;
    public SoftAssert softAssert;
    public SoftAssertions softAssertPlus = new SoftAssertions();

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method){
        log.info("\r\n====== [SetUp] " + getTestName(method) + " [Times] " + DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss") + "======");

        long startTime=System.currentTimeMillis();
        webDriver = initialDriver(BROWSER,PBX_URL,method);
        log.debug("[init driver time]:"+(System.currentTimeMillis()-startTime)/1000+" Seconds");

        long startTime_1=System.currentTimeMillis();
        setDriver(webDriver);

        setPjsip(new PjsipApp());
        pjsip=getPjsip();

        log.debug("[Test PBX_URL]"+PBX_URL);
        try {
            open(PBX_URL);//may throw IllegalStateException
        } catch (IllegalStateException ex) {
            log.error("【open url exception】" + ex);
        }
        log.debug("[open url time]:"+(System.currentTimeMillis()-startTime_1)/1000+" Seconds");

        long startTime_2=System.currentTimeMillis();
        auto = PageEngine.getInstance();
        log.debug("[open url time]:"+(System.currentTimeMillis()-startTime_2)/1000+" Seconds");

        long startTime_3=System.currentTimeMillis();
        softAssert = new SoftAssert();
        softAssertPlus = new SoftAssertions();
//        log.debug("[soft time]:"+(System.currentTimeMillis()-startTime_3)/1000+" Seconds");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(Method method) {
        log.info("\r\n====== [afterMethod] " + getTestName(method) + " [Times] " + DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss") + "======");
        log.debug("[remote session]{}",webDriver);
        try{
            if(EmptyUtil.isNotEmpty(webDriver)){
//                BrowserUtils.getInstance().getLogType_Browser(method,webDriver);
//                BrowserUtils.getInstance().getAnalyzeLog(method,webDriver);
                webDriver.quit();
            }
        }catch(Throwable ex){
            log.error("[driver quite exception]"+ex.getMessage()+ex.getStackTrace());
        }

        if(EmptyUtil.isNotEmpty(pjsip)){
            pjsip.Pj_Destory();
            log.debug("[end destroy pjsip] pjsip->"+pjsip);
        }
        log.debug("[clean remote session to null]{}",webDriver);
//        checkExtensionStats();
        log.info( "\r\n****** [TearDown] "+ getTestName(method)+" [Times] "+ DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss")+"**********************");
    }

    public void checkExtensionStats() {
        try {
            for (int i = 1000; i <= 1012; i++) {
                log.debug("1000状态：" + getExtensionStatus(i, IDLE, 5));
            }
            log.debug("2000状态：" + getExtensionStatus(2000, IDLE, 5));
            log.debug("2001状态：" + getExtensionStatus(2001, IDLE, 5));
            log.debug("2002状态：" + getExtensionStatus(2002, IDLE, 5));
            log.debug("4000状态：" + getExtensionStatus(4000, IDLE, 5));
        } catch (NullPointerException e) {
        }
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
