package com.yeastar.swebtest.driver;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.yeastar.swebtest.driver.Config.sort_ascendingOrder;
import static com.yeastar.swebtest.driver.ConfigP.pageLogin;
import static com.yeastar.swebtest.driver.ConfigP.pageLogin;
import static com.yeastar.swebtest.driver.ConfigP.pageLogin;
import static com.yeastar.swebtest.driver.ConfigP.pageLogin;

/**
 * Created by GaGa on 2017-04-16.
 */
@Log4j2
public class SwebDriverP extends ConfigP {
    public static WebDriver webDriver = null;


    public static void quitDriver() {
        webDriver.quit();
        if(webDriver!=null) {
            close();
        }
    }

    public static void killChromePid() {
        if (!Platform.getCurrent().equals(Platform.LINUX)) {
            try {
                Runtime.getRuntime().exec("taskkill /im chrome.exe /f");
//            Runtime.getRuntime().exec("taskkill /im chromedriver.exe /f");
            } catch (IOException e) {
                log.error("[error]killChromePid----IOException"+e.getMessage()+e.getStackTrace());
            }
        }
    }

    /**
     * 登录S系列设备，默认语言为英文
     *
     * @param username
     * @param password
     */
    public static void login(String username, String password) {
        login(username, password, "english");
    }

    /**
     * 可选语言登录S系列设备
     *
     * @param username
     * @param password
     * @param language
     */
    public static void login(String username, String password, String language) {
        if (language != null) { //不修改语言
            if (language.equalsIgnoreCase("english")) { //修改语言，暂时就中文，英文
                select(pageLogin.language, pageLogin.english);
            } else {
                select(pageLogin.language, pageLogin.chineseSimpleFied);
            }
        }
        pageLogin.username.setValue(username);
        pageLogin.password.setValue(password);
        ys_waitingTime(1000);
        pageLogin.login.click();
        sleep(10000);
        if (pageDeskTop.pp_comfirm.isDisplayed()) {
            setCheckBox(pageDeskTop.pp_agreement_checkBox, true);
            pageDeskTop.pp_comfirm.click();
        }
        pageDeskTop.taskBar_User.should(exist);
//        mySettings.close.click();
//        sleep(1000);
        if (Integer.valueOf(VERSION_SPLIT[1]) > 9 || PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)) {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.extensions_panel.click();
            extensions.add.click();
            sleep(3000);//not delete ; may crash with devices not found with zalenium message
            ys_waitingMask();
            closeSetting();
        }
    }

    /**
     * 注销登录
     */
    public static void logout() {
        pageDeskTop.taskBar_User.shouldBe(visible).click();
        ys_waitingTime(1000);
        pageDeskTop.taskBar_User_Logout.shouldBe(visible).click();
        ys_waitingTime(1000);
        pageDeskTop.messageBox_Yes.shouldBe(visible).click();
        pageLogin.username.should(exist);
    }

//    public static int initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl) {
//        //Selenide的配置信息
//        Configuration.timeout = FINDELEMENT_TIMEOUT;
//        Configuration.collectionsTimeout = 20;
//        Configuration.startMaximized = true;
//        DesiredCapabilities grid;
//        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
//        if (browser.equals("chrome")) {
//            Configuration.browser = CHROME;
//            System.setProperty("webdriver.chrome.driver", CHROME_PATH);
//            grid = DesiredCapabilities.chrome();
//        } else if (browser.equals("firefox")) {
//            System.setProperty("webdriver.firefox.bin",FIREFOX_PATH);
//            grid = DesiredCapabilities.firefox();
//        } else {
//            log.error("浏览器参数有误："+browser);
//            return 0;
//        }
//
    //        if (hubUrl != null ) {
//            try {
//                webDriver = new RemoteWebDriver(new URL(hubUrl),grid);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
////            webDriver.get(relativeOrAbsoluteUrl);
// //           setWebDriver(webDriver);
//            open(relativeOrAbsoluteUrl);
//            webDriver = getWebDriver();
//        } else {
////            如果指定，则打开被测服务器
//            open(relativeOrAbsoluteUrl);
////            返回WebDriver的类型
//            webDriver = getWebDriver();
//        }
//
//        return 1;
//    }

    /**
     * S系列的下拉框选择过程比较特殊，单独写一个方法出来调用
     *
     * @param select
     * @param value
     */
    public static void select(SelenideElement select, SelenideElement value) {
        click(select);
        click(value);
    }

    /**
     * 单击按钮等
     * 点击按钮
     *
     * @param element
     */
    public static void click(SelenideElement element) {
        element.should(exist).click();
    }

    /**
     * Grid表格
     * 点击表格图片
     * gridClick 点击按钮
     * gridCheck 单选框按钮
     * gridContent 获取信息按钮
     */
    public static void gridClick(String gridname, int row, int column) {
        ys_waitingTime(5000);
        executeJs("Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td img')[" + column + "].click()");
        ys_waitingTime(2000);
    }

    /**
     * 表格中勾选框点击
     *
     * @param gridname
     * @param row
     * @param column
     * @throws InterruptedException
     */
//    Ext.query('#'+Ext.query('#tableview-1282'+ ' [data-recordindex]')[1].id+ ' tr td')[6]
//    public static void gridCheck(String gridname, int row, int column) {
//        executeJs("Ext.query('#'+Ext.query('#'+" +
//                gridname + ".id + ' [data-recordindex]')" +
//                "[" + --row + "].id + ' tr td input')[" + column + "].click()");
//    }
    public static void gridCheck(String gridname, int row, int column) {
        executeJs("Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td div div')[" + column + "].click()");
    }

    /**
     * 全选表格的每一行
     *
     * @param gridname
     */
    public static void gridSeleteAll(String gridname) {
        ys_waitingTime(2500);
        executeJs(gridname + ".getSelectionModel().selectAll()");
    }

    /**
     * 检查表格内容是否都删除
     *
     * @param gridname
     */
    public static void gridCheckDeleteAll(String gridname) {
        gridCheckDeleteAll(gridname, 0);
    }

    /**
     * 检查表格删除后还剩几行
     *
     * @param gridname
     * @param surplusLine
     */
    public static void gridCheckDeleteAll(String gridname, int surplusLine) {
        String line = String.valueOf(gridLineNum(gridname));
        YsAssert.assertEquals(line, String.valueOf(surplusLine), "检查表格删除");
    }

    /**
     * 获取表格内容
     *
     * @param gridname
     * @param row
     * @param column
     * @return
     */
    public static String gridContent(String gridname, int row, int column) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (String) executeJs("return Ext.query('#'+Ext.query('#'+" + gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td')[" + column + "].textContent");
    }

//========================================================================页面表格==============================================

    /**
     * 获取分机状态
     *
     * @param gridname
     * @param row
     * @param column
     * @return
     */
    public static String gridExtensonStatus(String gridname, int row, int column) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (String) executeJs("return Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td div div')[" + column + "].getAttribute('data-qtip')");
    }

    /**
     * 获取表格中图标颜色  Ext.query('#'+Ext.query('#tableview-1111'+' table')[3].id+' tr td img')[0].src
     *
     * @param grid
     * @param row
     * @param column
     * @return
     */
    public static String gridPicColor(String grid, int row, int column) {
        String actColor = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+" + grid + ".id+' table')[" + --row + "].id+' tr td img')[" + column + "].src"));
//        if(actColor.contains(color))
//            return color ;
        System.out.println("mmmmm-------" + actColor);
        return actColor;
    }

    /**
     * 获取表格当前行数
     *
     * @param gridname
     * @return
     */
    public static Object gridLineNum(String gridname) {
        return executeJs("return " + gridname + ".getStore().getCount()");
    }

    /**
     * 从表格中遍历查找添加的行
     *
     * @param grid         表格ID
     * @param column       第几列为查找依据
     * @param providerName 查找依据
     * @param sort         升序降序  ascendingOrder   descendingOrder
     * @return row 目标在第几行
     */
    public static int gridFindRowByColumn(String grid, int column, String providerName, int sort) {
        String lineNum = String.valueOf(gridLineNum(grid));
        int row = 0;
        if (sort == sort_ascendingOrder) {
            for (row = 1; row <= Integer.parseInt(lineNum); row++) {
                String actTrunkName = String.valueOf(gridContent(grid, row, column));
                if (actTrunkName.equals(providerName)) {
                    System.out.println("gridFindRowByColumn ascendingOrder name  " + actTrunkName);
                    return row;
                }
            }
        } else {
            for (row = Integer.parseInt(lineNum); row > 0; row--) {
                String actTrunkName = String.valueOf(gridContent(grid, row, column));
                if (actTrunkName.equals(providerName)) {
                    System.out.println("gridFindRowByColumn descendingOrder name  " + actTrunkName);
                    return row;
                }
            }
        }
        Reporter.error("没有找到指定的目标：" + providerName);
        System.out.println("gridFindRowByColumn no find providerName row=-1");
        return -1;
    }

    public static void listSelect(String listname, String recordname, String... valuelist) {
        String Id = "";
        for (String value :
                valuelist) {
            String listId = (String) listGetId(listname, recordname, value);
            Id = Id + listId;
            Id = Id + ",";
        }
        Id = Id.substring(0, Id.length() - 1);
        listSetValue(listname, Id);
    }

    /**
     * 选择多个参数的list选择
     *
     * @param listname   list表格的定位
     * @param recordname 查找依据
     * @param valuelist  要选择的参数  数组
     * @throws InterruptedException
     */
    public static void listSelect(String listname, String recordname, ArrayList<String> valuelist) {
        String Id = "";
        for (String value :
                valuelist) {
            String listId = (String) listGetId(listname, recordname, value);
            Id = Id + listId;
            Id = Id + ",";
        }
        Id = Id.substring(0, Id.length() - 1);
        listSetValue(listname, Id);
    }

    /**
     * 选择单个参数的list选择
     *
     * @param listname
     * @param recordname
     * @param value
     * @throws InterruptedException
     */
    public static void listSelect(String listname, String recordname, String value) {
        String Id = "";
        String listId = (String) listGetId(listname, recordname, value);
        Id = Id + listId;
        Id = Id + ",";

        Id = Id.substring(0, Id.length() - 1);
        listSetValue(listname, Id);
    }

    /**
     * 获取list表格中要选中值的ID
     *
     * @param listname   list表格名称
     * @param recordname 选中值的标识
     * @param value      值
     * @return //Ext.getCmp('listnameID').getStore().getAt(index).data  查看ID和标识的方法
     */
    public static Object listGetId(String listname, String recordname, String value) {
        return executeJs("return  Ext.getCmp('" + listname + "').getStore()." + "findRecord('" + recordname + "', \"" + value + "\", 0, false, false, true).data.id");
    }

    public static Object listGetValue(String listname, String recordname, String value) {
        return executeJs("return  Ext.getCmp('" + listname + "').getStore()." + "findRecord('" + recordname + "', \"" + value + "\", 0, false, false, true).data.value");
    }

    /**
     * 设置list表格内容
     *
     * @param listname
     * @param Id
     */
    public static void listSetValue(String listname, String Id) {
        executeJs("Ext.getCmp('" + listname + "').setValue('" + Id + "')");
    }

//====================================================================List 选择框========================================================

    /**
     * 获取list表格的行数
     *
     * @param listId list表格的ID
     * @return
     */
    public static String getListCount(String listId) {
        return String.valueOf(executeJs("return Ext.getCmp('" + listId + "').getStore().getCount()"));
    }

    /**
     * 返回动态数据的data 的id
     *
     * @param listId list表格的ID
     * @param value
     * @return
     */
    public static String getDynamicData(String listId, int value) throws InterruptedException {
        return String.valueOf(executeJs("return Ext.getCmp('" + listId + "').getStore().getAt(" + value + ").data.id"));
    }

    /**
     * 全选list表格内容
     *
     * @param list
     */
    public static void listSelectAll(String list) {
        String Id = "";
        String num = getListCount(list);
        for (int i = 1; i <= Integer.parseInt(num); i++) {
            String listId = getListId(list, i);
            Id = listId + "," + Id;
        }
        Id = Id.substring(0, Id.length() - 1);
        listSetValue(list, Id);
    }

    public static void listSelectAllbyValue(String list) {
        String Id = "";
        String num = getListCount(list);
        for (int i = 1; i <= Integer.parseInt(num); i++) {
            String listId = getListValue(list, i);
            Id = listId + "," + Id;
        }
        Id = Id.substring(0, Id.length() - 1);
        listSetValue(list, Id);
    }

    /**
     * 获取list表格内容的id
     *
     * @param listId
     * @param index
     * @return
     */
    public static String getListId(String listId, int index) {
        return String.valueOf(executeJs("return Ext.getCmp('" + listId + "').getStore().getAt(" + --index + ").id"));
    }

    public static String getListValue(String listId, int index) {
        return String.valueOf(executeJs("return Ext.getCmp('" + listId + "').getStore().getAt(" + --index + ").value"));
    }

    /**
     * 下拉框的选项是固定的
     *
     * @param comboboxId
     * @param value
     */
    public static void comboboxSelect(String comboboxId, String value) {
        executeJs("Ext.getCmp('" + comboboxId + "').setValue('" + value + "')");
    }

    public static void comboboxSelect(SelenideElement comboboxId, String value) {
        executeJs("Ext.getCmp('" + comboboxId + "').setValue('" + value + "')");
    }

    /**
     * 下拉框的值需要从数据库获取的
     *
     * @param listname
     * @param recordname 选择内容的类型
     * @param value
     */
    public static void comboboxSet(String listname, String recordname, String value) {
//        sleep(2000);
        String Id = "";
        String listId = (String) listGetId(listname, recordname, value);
        Id = Id + listId;
        Id = Id + ",";

        Id = Id.substring(0, Id.length() - 1);
        listSetValue(listname, Id);
        ys_waitingTime(3000);
    }

    public static void comboboxSetbyValue(String listname, String recordname, String value) {
//        sleep(2000);
        String Value = "";
        String listValue = (String) listGetValue(listname, recordname, value);
        Value = Value + listValue;
        Value = Value + ",";

        Value = Value.substring(0, Value.length() - 1);
        listSetValue(listname, Value);
        ys_waitingTime(3000);
    }

    /**
     * 调用Windows系统弹出的窗口
     *
     * @param filePath 插件所处路径
     */
    //TODO adapt linux
    public static void importFile(String filePath) {
        String path = System.getProperty("user.dir");
        try {
            ys_waitingTime(2000);
            Runtime.getRuntime().exec(path + "\\Import.exe " + filePath);
            ys_waitingTime(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * please wait 页面等待
     */
    @Step("等待页面加载")
    public static void ys_waitingMask() {
        Date date = new Date();
        long time = date.getTime();
        while (true) {
            Date currentDate = new Date();
            long currentTime = currentDate.getTime();
            if (executeJs("return Ext.get('ys-waiting').dom.style.display").toString().equals("none")) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (currentTime / 1000 - time / 1000 > 30) {
                System.out.println("ys-waiting timeout");
                break;
            }
        }
        ys_waitingTime(1000);
    }

//=======================================================================下拉框选择====================================================//

    /**
     * 表格loading 页面等待
     *
     * @param grid
     */
    public static void ys_waitingLoading(String grid) {
        ys_waitingTime(1500);
        while (true) {
            if (String.valueOf(executeJs("return Ext.get(" + grid + ".id).dom.style.display")).equals("none")) {
                break;
            }
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ys_waitingTime(1500);
    }

    /**
     * 等待响应时间， 通话 ，界面，等等
     *
     * @param ms
     * @throws InterruptedException
     */
    public static void ys_waitingTime(int ms) {
        sleep(ms);
    }

    /**
     * 等待重启
     * param sec 默认300 S
     */

    public static void waitReboot() {
        waitReboot(300);

    }
    @Step("等待重启 {0}")
    public static void waitReboot(int sec) {
//        new WebDriverWait(webDriver,sec).until(ExpectedConditions.presenceOfElementLocated(By.id("login-btn-btnEl")));
        while (true) {
            sleep(90000);
            sec = sec - 10;
            if (pageLogin.username.isDisplayed()) {
                break;
            }
            refresh();
            if (sec <= 0) {
                break;
            }
        }
        boolean rebootSuc = pageLogin.username.isDisplayed();
        log.debug("waitReboot ..check " + rebootSuc);
        YsAssert.assertEquals(rebootSuc, true, "waitReboot重启");
    }


//===============================================================================系统弹窗插件============================================================

    /**
     * 关闭Setting页面
     */
    public static void closeSetting() {
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#control-panel_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//================================================================================等待时间方法====================================================//

    /**
     * 关闭CDR页面
     */
    public static void closeCDRRecord() {
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#cdr-record_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭CDR 编辑框页面（编辑CDR列显示的页面）
     */
    public static void closeCDREditListOptions() {
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#'+Ext.getCmp(\"cdr-record\").down('cdrandrecord-edit').down('header').id + ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭MainTenance 维护页面
     */
    public static void closeMaintenance() {
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#maintance_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭PBX Monitor页面
     */
    public static void closePbxMonitor() {
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#pbxmonitor_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 页面apply 点击
     */
    public static void ys_apply() {
        ys_waitingTime(2000);
        if (versionCompare(DEVICE_VERSION, "30.11.0.25").equals(">")) {
            System.out.println("1111");
            if (pageDeskTop.apply_new.isDisplayed()) {
                System.out.println("2222");
                pageDeskTop.apply_new.shouldBe(visible).click();
            } else {
                System.out.println("3333");
                return;
            }
        } else {
            System.out.println("7777");
            if (pageDeskTop.apply.isDisplayed()) {
                System.out.println("8888");
                pageDeskTop.apply.shouldBe(visible).click();
            } else {
                System.out.println("9999");
                return;
            }
        }
        System.out.println("4444");
        if (PRODUCT.equals(PC)) {
            Date date = new Date();
            long time = date.getTime();
            while (true) {
                Date currentDate = new Date();
                long currentTime = currentDate.getTime();
                if (executeJs("return Ext.query('.x-mask-msg-text').filter(function(o){if(o.id == '')return true;return false;}).length").equals("0")) {
                    break;
                }
                sleep(50);
                if (currentTime / 1000 - time / 1000 > 30) {
                    Reporter.error("apply timeout");
                    break;
                }
            }
        } else {
            ys_waitingTime(6000);
        }
        ys_waitingTime(2000);
    }
    //=============================================================================窗口关闭方法====================================================================//

    /**
     * 分机页面apply点击
     */
    public static void ys_me_apply() {
        me.me_apply.click();
        if (PRODUCT.equals(PC)) {
            Date date = new Date();
            long time = date.getTime();
            while (true) {
                Date currentDate = new Date();
                long currentTime = currentDate.getTime();
                if (executeJs("return Ext.query('.x-mask-msg-text').filter(function(o){if(o.id == '')return true;return false;}).length").equals("0")) {
                    break;
                }
                sleep(50);
                if (currentTime / 1000 - time / 1000 > 30) {
                    Reporter.error("apply timeout");
                    break;
                }
            }
        } else {
            ys_waitingTime(6000);
        }
        ys_waitingTime(2000);
    }

    /**
     * ExtJs 调用方法
     *
     * @param js
     * @return
     */
    public static Object executeJs(String js) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Reporter.infoExec("Extjs： " + js);
        Object result = executeJavaScript(js);
//        Object result= (Object)((JavascriptExecutor)webDriver).executeScript(js);
        if (js.contains("return")) {
            System.out.println("Extjs: Result:  " + result);
        }
        return result;
    }

    /**
     * 带返回参数的extJs方法
     *
     * @param js
     * @return
     */
    public static Object return_executeJs(String js) {
        String reJs = "return " + js;
//        return (Object)((JavascriptExecutor)webDriver).executeScript(reJs);
        return executeJavaScript(reJs);
    }

    /**
     * 版本比较，返回“>” 小于"<" 等于"="
     *
     * @param ver
     * @param target
     * @return
     */
    public static String versionCompare(String ver, String target) {
        System.out.println("ver：" + ver + "target: " + target);

        if (ver.split("\\.").length < 4 || target.split("\\.").length < 4) {
            return null;
        }
        int verpart1 = Integer.parseInt(ver.split("\\.")[1]);
        int verpart3 = Integer.parseInt(ver.split("\\.")[3].split("-")[0]);

        int tarpart1 = Integer.parseInt(target.split("\\.")[1]);
        int tarpart3 = Integer.parseInt(target.split("\\.")[3]);

        if (verpart1 == tarpart1) {

            if (verpart3 == tarpart3) {
                return "=";
            } else if (verpart3 > tarpart3) {
                return ">";
            } else if (verpart3 < tarpart3) {
                return "<";
            }

        } else if (verpart1 > tarpart1) {
            return ">";

        } else if (verpart1 < tarpart1) {
            return "<";
        }
        return null;
    }

    /**
     * 设置文本、下拉框等值，网页标签要是Input类型，并且不是ReadOnly状态
     *
     * @param element
     * @param text
     */
    public static void setValue(SelenideElement element, String text) {
        element.should(exist).setValue(text);
    }
    //=======================================================apply点击======================================================//

    /**
     * 直接设置勾选框是否勾选
     *
     * @param checkBoxId
     * @param check
     */
    public static void setCheckBox(String checkBoxId, boolean check) {
        executeJs("Ext.getCmp('" + checkBoxId + "').setValue('" + check + "')");
    }

    /**
     * 设置分页显示数量
     *
     * @param grid
     * @param num
     */
    public static void setPageShowNum(String grid, int num) {
        try {
            Thread.sleep(3000);
            System.out.println(grid + ".down('mypagingtoolbar').plugins[0].setValue('" + num + "')");
            executeJs(grid + ".down('mypagingtoolbar').plugins[0].setValue('" + num + "')");
            executeJs(grid + ".down('mypagingtoolbar').store.setPageSize(" + num + ")");
            executeJs(grid + ".down('mypagingtoolbar').store.loadPage(1)");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //=============================================================EextJs命令执行==========================================================//

    /**
     * 删除所有
     */
    public static void deletes(String message, String grid, SelenideElement delete, SelenideElement delete_yes, String grid_Mask) {
        Reporter.infoExec(message); //执行操作
        setPageShowNum(grid, 100);
        ys_waitingLoading(grid_Mask);
//        System.out.println("deletes "+message +": "+Integer.parseInt(String.valueOf(gridLineNum(grid))));
        ys_waitingTime(3000);
        Reporter.infoExec("deletes " + message + ": " + Integer.parseInt(String.valueOf(gridLineNum(grid))));
        while (Integer.parseInt(String.valueOf(gridLineNum(grid))) > 0) {
            gridSeleteAll(grid);
            delete.click();
            delete_yes.click();
            ys_waitingMask();
            ys_waitingLoading(grid_Mask);
            ys_waitingTime(2000);
            Reporter.infoExec("deleting " + message + ": " + Integer.parseInt(String.valueOf(gridLineNum(grid))));
        }
        YsAssert.assertEquals(Integer.parseInt(String.valueOf(gridLineNum(grid))), 0, message);
    }

    public static void resetoreBeforetest(String pathName) {
        Reporter.infoExec("通过备份环境初始化");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        if (!PRODUCT.equals(CLOUD_PBX)) {
            ys_waitingMask();
        }
        ys_waitingLoading(backupandRestore.grid_Mask);
        gridClick(backupandRestore.grid, gridFindRowByColumn(backupandRestore.grid, backupandRestore.gridColumn_Name, pathName, sort_ascendingOrder), backupandRestore.gridColumn_Name);
        pageDeskTop.messageBox_Yes.click();
        ys_waitingMask();
        pageDeskTop.reboot_Yes.click();
        waitReboot();
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        if (!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9) {
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    //=================================================================其他==========================================================

    public static void backupEnviroment(String className) {
        backupEnviroment(className, false);
    }

    public static void backupEnviroment(String className, boolean cdr) {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        if (!PRODUCT.equals(CLOUD_PBX)) {
            ys_waitingMask();
        }
        ys_waitingLoading(backupandRestore.grid_Mask);
        System.out.println("current grid row: " + gridLineNum(backupandRestore.grid).toString());
        if (!gridLineNum(backupandRestore.grid).toString().equals("0")) {
            //Thread.currentThread().getStackTrace()[1].getMethodName();
            //delete all
            gridSeleteAll(backupandRestore.grid);
            backupandRestore.delete.shouldBe(Condition.visible).click();
            backupandRestore.delete_yes.shouldBe(Condition.visible).click();

            int row = gridFindRowByColumn(backupandRestore.grid, backupandRestore.gridColumn_Name, className + "_Local.bak", sort_ascendingOrder);
            System.out.println("backupEnviroment row :" + row);
            if (row != -1) {
                System.out.println("backupEnviroment class name : " + gridContent(backupandRestore.grid, row, backupandRestore.gridColumn_Name));
                if (gridContent(backupandRestore.grid, row, backupandRestore.gridColumn_Name).equals(className + "_Local.bak")) {
                    gridClick(backupandRestore.grid, row, backupandRestore.gridDelete);
                    backupandRestore.delete_yes.click();
                }
                ys_apply();
            }
        }
        backupandRestore.backup.click();
        create_new_backup_file.fileName.setValue(className);
        setCheckBox(create_new_backup_file.callLog_Checkbox, cdr);
        int t = 30;
        while (t > 0) {
            if (!return_executeJs("Ext.get('mt-br-storagetype-inputEl').getValue()").toString().equals("")) {
                System.out.println("find localType ..");
                break;
            }
            ys_waitingTime(500);
            t = t - 1;
        }
        create_new_backup_file.save.click();
        closeMaintenance();
    }

    /**
     * 因为YsAssert必然会终止当前Test,如果终止前没有挂断当前电话，会影响到下一通通话测试，故这个函数用在通话结束前的判断
     *
     * @param actStatus
     * @param expStatus
     * @param desc
     */
    public static void ysAssertWithHangup(int actStatus, int expStatus, String desc) {
//        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        if (actStatus != expStatus) {
            pjsip.Pj_Hangup_All();
            YsAssert.assertEquals(actStatus, expStatus, desc);
        }
    }

    /**
     * 获取分机通话状态
     *
     * @param username
     */
    public static int getExtensionStatus(int username, int expectStatus, int timeout) {
        UserAccount account;
        int time = 0;
        int status = -1;
        while (time <= timeout) {
            ys_waitingTime(1000);
            account = pjsip.getUserAccountInfo(username);
            if (account == null) {
                status = -1;
                System.out.println("first--------account is null----------");
            }
            if (account.status == expectStatus) {
                status = account.status;
                System.out.println("Second-------get status succ--------------");
                return status;
            }
            if (time == timeout) {
                status = account.status;
                System.out.println("third-------get status Timeout----------");
            }
            time++;
        }
        System.out.println("分机通话状态：" + status);
        return status;
    }

    public int initialDriver(String browser) {
        return initialDriver(browser, "");
    }

    public int initialDriver(String browser, String relativeOrAbsoluteUrl) {
        if (IS_RUN_REMOTE_SERVER.equals("true")) {
            log.debug("[IS_RUN_REMOTE_SERVER] " + IS_RUN_REMOTE_SERVER);
            return initialDriver(browser, relativeOrAbsoluteUrl, "http://" + GRID_HUB_IP + ":" + GRID_HUB_PORT + "/wd/hub");
        } else {
            return initialDriver(browser, relativeOrAbsoluteUrl, "");
        }
    }

    public int initialDriver(String browser, String relativeOrAbsoluteUrl, Method method) {
        if (IS_RUN_REMOTE_SERVER.equals("true")) {
            log.debug("[IS_RUN_REMOTE_SERVER] " + IS_RUN_REMOTE_SERVER);
            return initialDriver(browser, relativeOrAbsoluteUrl, "http://" + GRID_HUB_IP + ":" + GRID_HUB_PORT + "/wd/hub", method);
        } else {
            log.debug("init_driver_with_method");
            return initialDriver(browser, relativeOrAbsoluteUrl, "");
        }
    }

    public int initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl) {
        //Selenide的配置信息
        Configuration.timeout = FINDELEMENT_TIMEOUT;
        Configuration.collectionsTimeout = FINDELEMENT_TIMEOUT;
        Configuration.startMaximized = true;
        Configuration.reportsFolder = SCREENSHOT_PATH;
//        ScreenShooter.captureSuccessfulTests = true; //每个test后都截图，不论失败或成功
        Configuration.captureJavascriptErrors = false;//捕获js错误
        Configuration.savePageSource = false;
        DesiredCapabilities desiredCapabilities;
        //选择测试浏览器
        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
        if (browser.equals("chrome")) {
            Configuration.browser = CHROME;
//            Configuration.browserBinary = CHROME_PATH;
//            Configuration.browserVersion = 谷歌版本
//            System.setProperty("webdriver.chrome.driver", CHROME_PATH);
            desiredCapabilities = DesiredCapabilities.chrome();
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.firefox.bin", FIREFOX_PATH);
            desiredCapabilities = DesiredCapabilities.firefox();
        } else {
            log.error("浏览器参数有误：" + browser);
            return 0;
        }
//        if (hubUrl != null ) {
        if (GRID_HUB_IP != "" && hubUrl!="") {
            log.debug("[GRID_HUB_IP] " + hubUrl);
            desiredCapabilities.setCapability("name", Thread.currentThread().getStackTrace()[3].getMethodName());
            desiredCapabilities.setCapability("build", System.getProperty("serviceBuildName"));
            desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
            desiredCapabilities.setCapability("testFileNameTemplate", "myID_{browser}_{testStatus}");
            desiredCapabilities.setCapability("network", true);
            desiredCapabilities.setCapability("idleTimeout", 240);//150-100
            desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 90);//180-90

            try {
                webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            webDriver.manage().window().maximize();
            webDriver.get(relativeOrAbsoluteUrl);
            setWebDriver(webDriver);
        } else {
//            如果指定，则打开被测服务器
            open(relativeOrAbsoluteUrl);
            webDriver = getWebDriver();
        }

        return 1;
    }

    public int initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl, Method method) {
        //Selenide的配置信息
        Configuration.timeout = FINDELEMENT_TIMEOUT;
        Configuration.collectionsTimeout = FINDELEMENT_TIMEOUT;
        Configuration.startMaximized = true;
        Configuration.reportsFolder = SCREENSHOT_PATH;
//        ScreenShooter.captureSuccessfulTests = true; //每个test后都截图，不论失败或成功
        Configuration.captureJavascriptErrors = false;//捕获js错误
        Configuration.savePageSource = false;
        DesiredCapabilities desiredCapabilities;
        //选择测试浏览器
        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
        if (browser.equals("chrome")) {
            Configuration.browser = CHROME;
//            Configuration.browserBinary = CHROME_PATH;
//            Configuration.browserVersion = 谷歌版本
            System.setProperty("webdriver.chrome.driver", CHROME_PATH);
            desiredCapabilities = DesiredCapabilities.chrome();
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.firefox.bin", FIREFOX_PATH);
            desiredCapabilities = DesiredCapabilities.firefox();
        } else {
            log.error("浏览器参数有误：" + browser);
            return 0;
        }
//        if (hubUrl != null ) {
        if (GRID_HUB_IP != null) {
            log.debug("[GRID_HUB_IP] " + hubUrl);
            desiredCapabilities.setCapability("name", getTestName(method));
            desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.CHROME);
            desiredCapabilities.setCapability("network", true);
            //Build Name    String serviceName = System.getProperty("serviceName");
//            desiredCapabilities.setCapability("build", "AutoTestBuild_Seven");
            desiredCapabilities.setCapability("build", System.getProperty("serviceBuildName"));
            //Idle TimeOut
            desiredCapabilities.setCapability("idleTimeout", 120);//150-100
            desiredCapabilities.setCapability("ZALENIUM_PROXY_CLEANUP_TIMEOUT", 90);//180-90
            //Screen Resolution
            desiredCapabilities.setCapability("screenResolution", "1920x1080");
            desiredCapabilities.setCapability("network", true);
            try {
                webDriver = new RemoteWebDriver(new URL(hubUrl), desiredCapabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            webDriver.manage().window().maximize();
            webDriver.get(relativeOrAbsoluteUrl);
            setWebDriver(webDriver);
        } else {
//            如果指定，则打开被测服务器
            open(relativeOrAbsoluteUrl);
            webDriver = getWebDriver();
        }

        return 1;
    }

    /**
     * get test name
     *
     * @param method
     * @return
     */
    public String getTestName(Method method) {
        return this.getClass().getSimpleName() + "." + method.getName();
    }


}