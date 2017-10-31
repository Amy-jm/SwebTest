package com.yeastar.swebtest.driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import com.yeastar.swebtest.tools.reporter.Logger;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.*;

/**
 * Created by GaGa on 2017-04-16.
 */
public class SwebDriver extends Config {
    public static WebDriver webDriver = null;

    public static int initialDriver(String browser) {
        return initialDriver(browser,"");
    }

    public static int initialDriver(String browser, String relativeOrAbsoluteUrl) {
        return initialDriver(browser,relativeOrAbsoluteUrl,null);
    }

    public static int initialDriver(String browser, String relativeOrAbsoluteUrl, String hubUrl) {
        //Selenide的配置信息
        Configuration.timeout = FINDELEMENT_TIMEOUT;
//        Configuration.collectionsTimeout = 20;

        DesiredCapabilities grid;
        //选择测试浏览器
        //这里保留IE、EDGE等浏览器的判断，以及比较重要的一个：PhantomJS
        if (browser.equals("chrome")) {
//            HashMap<String, Object> chromePrefs = new HashMap<>();
//            chromePrefs.put("profile.default_content_settings.popups",2);
//            chromePrefs.put("download.default_directory","D:\\");

//            ChromeOptions options = new ChromeOptions();
//            options.setExperimentalOption("prefs",chromePrefs);
            Configuration.browser = "chrome";
            System.setProperty("selenide.browser", "Chrome");
            System.setProperty("webdriver.chrome.driver", CHROME_PATH);
            grid = DesiredCapabilities.chrome();
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.firefox.bin",FIREFOX_PATH);
            grid = DesiredCapabilities.firefox();
        } else {
            Logger.error("浏览器参数有误："+browser);
            return 0;
        }

        if (hubUrl != null ) {
            try {
                webDriver = new RemoteWebDriver(new URL(hubUrl),grid);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            webDriver.get(relativeOrAbsoluteUrl);
            setWebDriver(webDriver);
        } else {
//            如果指定，则打开被测服务器
            open(relativeOrAbsoluteUrl);
//            返回WebDriver的类型
            webDriver = getWebDriver();
        }

        return 1;
    }

    public static void quitDriver() {
        webDriver.quit();
        ys_waitingTime(1000);
    }
    /**
     * 登录S系列设备，默认语言为英文
     * @param username
     * @param password
     */
    public static void login(String username,String password) {
        login(username,password,"english");
    }

    /**
     * 可选语言登录S系列设备
     * @param username
     * @param password
     * @param language
     */
    public static void login(String username,String password,String language) {
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
        pageDeskTop.taskBar_User.should(exist);
//        mySettings.close.click();
        sleep(1000);
    }

    /**
     * 注销登录
     */
    public static void logout() {
        pageDeskTop.taskBar_User.click();
        pageDeskTop.taskBar_User_Logout.click();
        pageDeskTop.messageBox_Yes.click();
        pageLogin.username.should(exist);
    }

    /**
     * S系列的下拉框选择过程比较特殊，单独写一个方法出来调用
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
     * @param element
     */
    public static void click(SelenideElement element) {
        element.should(exist).click();
    }

//========================================================================页面表格==============================================
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
        System.out.println("Ext.query('#'+Ext.query('#'+" +
                gridname + ".id + ' [data-recordindex]')" +
                        "[" + --row + "].id + ' tr td img')[" + column + "].click()");
    }

    /**
     * 表格中勾选框点击
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
     * @param gridname
     */
    public static void gridSeleteAll(String gridname) {
        ys_waitingTime(2500);
        executeJs(gridname+".getSelectionModel().selectAll()");
    }

    /**
     * 检查表格内容是否都删除
     * @param gridname
     */
    public static void gridCheckDeleteAll(String gridname){
        gridCheckDeleteAll(gridname,0);
    }

    /**
     * 检查表格删除后还剩几行
     * @param gridname
     * @param surplusLine
     */
    public static void gridCheckDeleteAll(String gridname,int surplusLine){
        String line = String.valueOf(gridLineNum(gridname));
        YsAssert.assertEquals(line,String.valueOf(surplusLine),"检查表格删除");
    }

    /**
     * 获取表格内容
     * @param gridname
     * @param row
     * @param column
     * @return
     */
    public static String gridContent(String gridname, int row, int column)  {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (String) executeJs("return Ext.query('#'+Ext.query('#'+" +gridname + ".id + ' [data-recordindex]')" +
                "[" + --row + "].id + ' tr td')[" + column + "].textContent");
    }

    /**
     * 获取分机状态
     * @param gridname
     * @param row
     * @param column
     * @return
     */
    public static String gridExtensonStatus(String gridname, int row, int column)  {
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
     * @param grid
     * @param row
     * @param column
     * @return
     */
    public static String gridPicColor(String grid,int row,int column){
        String actColor = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+"+grid+".id+' table')[" + --row + "].id+' tr td img')[" + column + "].src"));
//        if(actColor.contains(color))
//            return color ;
        System.out.println("mmmmm-------"+actColor);
        return actColor;
    }

    /**
     * 获取表格当前行数
     * @param gridname
     * @return
     */
    public static Object gridLineNum(String gridname) {
        return executeJs("return "+gridname+ ".getStore().getCount()");
    }

    /**
     * 从表格中遍历查找添加的行
     * @param grid   表格ID
     * @param column  第几列为查找依据
     * @param providerName  查找依据
     * @param sort 升序降序  ascendingOrder   descendingOrder
     * @return row 目标在第几行
     */
    public static int gridFindRowByColumn(String grid,int column, String providerName,int sort){
        String lineNum = String.valueOf(gridLineNum(grid));
        int row=0;
        if(sort ==sort_ascendingOrder){
            for(row = 1 ; row<=Integer.parseInt(lineNum) ; row++){
                String actTrunkName = String.valueOf(gridContent(grid,row,column));
                if(actTrunkName.equals(providerName)){
                    System.out.println("gridFindRowByColumn ascendingOrder name  "+ actTrunkName);
                    break;
                }
            }
        }else{
            for(row = Integer.parseInt(lineNum) ; row>0 ; row--){
                String actTrunkName = String.valueOf(gridContent(grid,row,column));
                if(actTrunkName.equals(providerName)){
                    System.out.println("gridFindRowByColumn descendingOrder name  "+ actTrunkName);
                    break;
                }
            }
        }
        return row;
    }

//====================================================================List 选择框========================================================

    /**
     * 选择多个参数的list选择
     * @param listname   list表格的定位
     * @param recordname 查找依据
     * @param valuelist  要选择的参数  数组
     * @throws InterruptedException
     */
    public static void listSelect(String listname, String recordname, ArrayList<String> valuelist) {
        String Id = "";
        for (String value :
                valuelist) {
            String listId = (String)listGetId(listname, recordname, value);
            Id =  Id+ listId;
            Id = Id + ",";
        }
        Id = Id.substring(0,Id.length()-1);
        listSetValue(listname,Id);
    }

    public static void listSelect(String listname, String recordname, String... valuelist) {
        String Id = "";
        for (String value :
                valuelist) {
            String listId = (String)listGetId(listname, recordname, value);
            Id =  Id+ listId;
            Id = Id + ",";
        }
        Id = Id.substring(0,Id.length()-1);
        listSetValue(listname,Id);
    }

    /**
     *  选择单个参数的list选择
     * @param listname
     * @param recordname
     * @param value
     * @throws InterruptedException
     */
    public static void listSelect(String listname, String recordname, String value) {
        String Id = "";
        String listId = (String)listGetId(listname, recordname, value);
        Id =  Id+ listId;
        Id = Id + ",";

        Id = Id.substring(0,Id.length()-1);
        listSetValue(listname,Id);
    }
    /**
     * 获取list表格中要选中值的ID
     * @param listname   list表格名称
     * @param recordname 选中值的标识
     * @param value       值
     * @return
     * //Ext.getCmp('listnameID').getStore().getAt(index).data  查看ID和标识的方法
     */
    public static Object listGetId(String listname, String recordname, String value) {
        return executeJs("return  Ext.getCmp('" + listname + "').getStore()." +"findRecord('" + recordname + "', \"" + value + "\", 0, false, false, true).data.id");
    }



    /**
     * 设置list表格内容
     * @param listname
     * @param Id
     */
    public static void listSetValue(String listname,String Id) {
        executeJs("Ext.getCmp('" + listname + "').setValue('" + Id + "')");
    }

    /**
     * 获取list表格的行数
     * @param listId  list表格的ID
     * @return
     */
    public static String getListCount(String listId) {
        return String.valueOf(executeJs("return Ext.getCmp('"+listId+"').getStore().getCount()"));
    }

    /**
     * 返回动态数据的data 的id
     * @param listId   list表格的ID
     * @param value
     * @return
     */
    public static String getDynamicData(String listId, int value) throws InterruptedException {
        return String.valueOf(executeJs("return Ext.getCmp('"+listId+"').getStore().getAt("+value+").data.id"));
    }

    /**
     * 全选list表格内容
     * @param list
     */
    public static void listSelectAll(String list)  {
        String Id = "";
        String  num = getListCount(list);
        for(int i=1; i<=Integer.parseInt(num); i++){
            String listId = (String)getListId(list,i);
            Id = listId + "," + Id;
        }
        Id = Id.substring(0,Id.length()-1);
        listSetValue(list, Id);
    }
    /**
     * 获取list表格内容的id
     * @param listId
     * @param index
     * @return
     */
    public static String getListId(String listId, int index)  {
        return String.valueOf(executeJs("return Ext.getCmp('"+listId+"').getStore().getAt("+--index+").id"));
    }
//=======================================================================下拉框选择====================================================//
    /**
     * 下拉框的选项是固定的
     * @param comboboxId
     * @param value
     */
    public static void comboboxSelect(String comboboxId, String value) {
        executeJs("Ext.getCmp('"+comboboxId+"').setValue('"+value+"')");
    }

    public static void comboboxSelect(SelenideElement comboboxId, String value){
        executeJs("Ext.getCmp('"+comboboxId+"').setValue('"+value+"')");
    }
    /**
     * 下拉框的值需要从数据库获取的
     * @param listname
     * @param recordname 选择内容的类型
     * @param value
     */
    public static void comboboxSet(String listname, String recordname, String value) {
        String Id = "";

        String listId = (String)listGetId(listname, recordname, value);
        Id =  Id+ listId;
        Id = Id + ",";

        Id = Id.substring(0,Id.length()-1);
        listSetValue(listname,Id);
        ys_waitingTime(3000);
    }


//===============================================================================系统弹窗插件============================================================
    /**
     * 调用Windows系统弹出的窗口
     * @param filePath  插件所处路径
     */
    public static void importFile(String filePath){
        String path = System.getProperty("user.dir");
        try {
            ys_waitingTime(2000);
            Runtime.getRuntime().exec(path+"\\Import.exe "+filePath);
            ys_waitingTime(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//================================================================================等待时间方法====================================================//
    /**
     * please wait 页面等待
     */
    public static void ys_waitingMask() {
        Date date  = new Date();
        long time = date.getTime();
        while (true){
            Date currentDate  = new Date();
            long currentTime = currentDate.getTime();
            if(executeJs("return Ext.get('ys-waiting').dom.style.display").equals("none") ){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(currentTime/1000-time/1000 > 30){
                System.out.println("ys-waiting timeout");
                break;
            }
        }
    }
    /**
     * 表格loading 页面等待
     * @param grid
     */
    public static void ys_waitingLoading(String grid) {
        ys_waitingTime(1500);
        while(true){
                if(String.valueOf(executeJs("return Ext.get("+grid+".id).dom.style.display")).equals("none")){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ys_waitingTime(1500);
    }
    /**
     * 等待响应时间， 通话 ，界面，等等
     * @param ms
     * @throws InterruptedException
     */
    public static void ys_waitingTime(int ms)  {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 等待重启
     * param sec 默认200 S
     */
    public static void waitReboot(){
        waitReboot(200);

    }
    public static void waitReboot(int sec){
        new WebDriverWait(webDriver,sec).until(ExpectedConditions.presenceOfElementLocated(By.id("login-btn-btnEl")));

        boolean rebootSuc= pageLogin.username.isDisplayed();
        System.out.println("waitReboot ..check "+rebootSuc);
        YsAssert.assertEquals(rebootSuc,true,"waitReboot重启");
    }
    //=============================================================================窗口关闭方法====================================================================//
    /**
     * 关闭Setting页面
     */
    public static void closeSetting(){
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#control-panel_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *关闭CDR页面
     */
    public static void closeCDRRecord(){
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
    public static void closeCDREditListOptions(){
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
    public static void closeMaintenance(){
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
    public static void closePbxMonitor(){
        try {
            Thread.sleep(500);
            executeJs("Ext.get(Ext.query('#pbxmonitor_header-targetEl'+ ' .x-tool-close')[0].id).dom.click()");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //=======================================================apply点击======================================================//
    /**
     * 页面apply 点击
     */
    public static void ys_apply(){
        pageDeskTop.apply.click();
        ys_waitingTime(8000);
    }

    /**
     * 分机页面apply点击
     */
    public static void ys_me_apply(){
        me.me_apply.click();
        ys_waitingTime(8000);
    }
    //=============================================================EextJs命令执行==========================================================//
    /**
     * ExtJs 调用方法
     * @param js
     * @return
     */
    public static Object executeJs(String js)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Extjs：  "+js);
        return (Object)((JavascriptExecutor)webDriver).executeScript(js);
    }

    /**
     * 带返回参数的extJs方法
     * @param js
     * @return
     */
    public static Object return_executeJs(String js) {
        String reJs = "return "+ js;
        return (Object)((JavascriptExecutor)webDriver).executeScript(reJs);
    }

    //=================================================================其他==========================================================

    /**
     * 设置文本、下拉框等值，网页标签要是Input类型，并且不是ReadOnly状态
     * @param element
     * @param text
     */
    public static void setValue(SelenideElement element, String text) {
        element.should(exist).setValue(text);
    }
    /**
     * 直接设置勾选框是否勾选
     * @param checkBoxId
     * @param check
     */
    public static void setCheckBox(String checkBoxId,boolean check){
        executeJs("Ext.getCmp('"+checkBoxId+"').setValue('"+String.valueOf(check)+"')");
    }

    /**
     * 设置分页显示数量
     * @param grid
     * @param num
     */
    public static void setPageShowNum(String grid ,int num){
        try {
            Thread.sleep(3000);
            System.out.println(grid+".down('mypagingtoolbar').plugins[0].setValue('"+String.valueOf(num)+"')");
            executeJs(grid+".down('mypagingtoolbar').plugins[0].setValue('"+String.valueOf(num)+"')");
            executeJs(grid+".down('mypagingtoolbar').store.setPageSize("+String.valueOf(num)+")");
            executeJs(grid+".down('mypagingtoolbar').store.loadPage(1)");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有
     */
    public static void deletes(String message,String grid,SelenideElement delete,SelenideElement delete_yes,String grid_Mask ) {
        Reporter.infoExec(message); //执行操作
        setPageShowNum(grid, 100);
        while (Integer.parseInt(String.valueOf(gridLineNum(grid))) != 0) {
            gridSeleteAll(grid);
            delete.click();
            delete_yes.click();
            ys_waitingLoading(grid_Mask);
        }
        YsAssert.assertEquals(Integer.parseInt(String.valueOf(gridLineNum(grid))),0,message);
    }

    /**
     * 获取分机通话状态
     * @param username
     */
    public static int getExtensionStatus(int username,int expectStatus,int timeout) {
        UserAccount account;
        int time = 0;
        int status = -1;
        while (time<=timeout) {
            ys_waitingTime(1000);
            account = pjsip.getUserAccountInfo(username);
            if (account == null) {
                status=-1;
                System.out.println("first--------account is null----------");
            }
            if (account.status == expectStatus) {
                status=account.status;
                System.out.println("Second-------get status succ--------------");
                return status;
            }
            if (time == timeout) {
                status=account.status;
                System.out.println("third-------get status Timeout----------");
            }
            time++;
        }
        System.out.println("分机通话状态："+status);
        return status;
    }


}