package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;




/**
 * Created by GaGa on 2017-05-15.
 */

public class Extension extends SwebDriver {

    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_ExtensionTest"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.taskBar_Main.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        //在pjsip中创建分机
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",-1);
    }
//    @BeforeClass
    public void InitExtension(){
        if(Single_Init){
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.extensions_panel.click();
//            ys_waitingMask();
            ys_waitingLoading(extensions.grid_Mask);
            deletes(" 删除所有分机",extensions.grid,extensions.delete,extensions.delete_yes,extensions.grid_Mask);
            extensionGroup.extensionGroup.click();
            deletes(" 删除所有分机组",extensionGroup.grid,extensionGroup.delete,extensionGroup.delete_yes,extensionGroup.grid_Mask);
            closeSetting();
        }
    }

    @Test
    public void A_SettingSystemTime() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.dateTime_panel.click();
            executeJs("Ext.getCmp('"+dateTime.timeZone+"').setValue('"+dateTime.chinaTime+"')");
        }else{
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.dateTime_panel.click();
            Reporter.infoExec("设置系统时间为00点整");
            dateTime.setUpManually.click();
            dateTime.time_hour.setValue("00");
            dateTime.time_minute.setValue("00");
            dateTime.time_second.setValue("00");
        }
        dateTime.save.click();
        ys_waitingTime(2000);
        if(pageDeskTop.apply.isDisplayed()){
            pageDeskTop.apply.click();
        }
        closeSetting();
        if(pageDeskTop.reboot_Yes.isDisplayed()){
            System.out.println("go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("wait reboot ");
            waitReboot();
        }else if(pageDeskTop.loginout_OK.isDisplayed()){
            System.out.println("go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("wait logout ");
            waitReboot();
        }else{
            System.out.println("not to reboot");
            waitReboot();
        }

        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Test
    public void B_CreateExtension() throws InterruptedException {
        Reporter.infoExec("创建分机1000");
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.extensions_panel.click();
        setPageShowNum(extensions.grid,100);
        m_extension.addSipExtension(1000,EXTENSION_PASSWORD);

        Number lineAfterAdd = (Number) gridLineNum(extensions.grid);
        String actual = (String) gridContent(extensions.grid,lineAfterAdd.intValue(),extensions.gridcolumn_Extensions);
        Thread.sleep(500);
        YsAssert.assertEquals(actual,String.valueOf("1000"));
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);


    }

    @Test
    public void C_CreateFxsExtension() throws InterruptedException {
        if(PRODUCT.equals(PC) || PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec("创建分机1001");
            m_extension.addSipExtension(1001,EXTENSION_PASSWORD);
            pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        }else {
            Reporter.infoExec("创建FXS分机1001");
            m_extension.addFxsExtension(1001,EXTENSION_PASSWORD,FXS_1);
            pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",2);
        }

        Number lineAfterAdd = (Number) gridLineNum(extensions.grid);
        String actualUsername = (String) gridContent(extensions.grid,lineAfterAdd.intValue(),extensions.gridcolumn_Extensions);
        Thread.sleep(500);
        YsAssert.assertEquals(actualUsername,String.valueOf(1001));

    }

    @Test
    public void D_CreateBulkExtensions() throws InterruptedException {
        Reporter.infoExec("批量创建分机1100-1109");
        m_extension.addBulkExtensions(1100,10,REGIST_EXTENSION_PWD_FIX,EXTENSION_PASSWORD,REGIST_EXTENSION_PWD_FIX,EXTENSION_PASSWORD);
        String lineAfterAdd = String.valueOf(gridLineNum(extensions.grid)) ;
        String actual = (String) gridContent(extensions.grid,Integer.parseInt(lineAfterAdd),extensions.gridcolumn_Extensions);
        YsAssert.assertEquals(actual,String.valueOf(1100+10-1));
        ys_apply();

        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,5);
    }

    @Test
    public void E_RegisterExtension() throws InterruptedException {
        Reporter.infoExec("SIP分机1000拨打SIP分机1001，通话");
        Thread.sleep(10000);

        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1001, DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer(1001,"1000", DEVICE_IP_LAN,true);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1001 <1001>","1000 <1000>","Answered","","",communication_internal);
    }

    @Test
    public void F_ExportExtensions(){
        Reporter.infoExec("导出分机列表");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_tree.click();
//        settings.extensions_panel.click();
        extensions.Extensions.click();
        extensions.export.click();
        ys_waitingTime(3000);
    }

    @Test
    public void G_DelExtension() throws InterruptedException {
        Reporter.infoExec("删除1000分机");
        pjsip.Pj_Unregister_Accounts();
        m_extension.deleteExtension(1000);

    }

    @Test
    public void H_DelExteiosns() throws InterruptedException {
        Reporter.infoExec("删除所有分机");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.extensions_panel.click();

        }
        ys_waitingTime(3000);
        deletes(" 删除所有分机",extensions.grid,extensions.delete,extensions.delete_yes,extensions.grid_Mask);
        ys_waitingMask();
        gridCheckDeleteAll(extensions.grid);
    }

    @Test
    public void I_ImportExtension() throws InterruptedException {
        Reporter.infoExec("导入分机列表");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
            settings.extensions_panel.click();
        }
        m_extension.ImportExtensions(IMPORT_EXTENSION);



        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1001, DEVICE_IP_LAN);

        YsAssert.assertEquals(String.valueOf(gridLineNum(extensions.grid)),"12","导入分机列表");
    }
    @Test
    public void J_PstnCall() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        Reporter.infoExec("通过PSTN外线B设备2000拨打A设备1000");
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
        ys_waitingTime(5000);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        pjsip.Pj_Make_Call_Auto_Answer(2000,"61000",DEVICE_ASSIST_2,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1001 <1001>","1000 <1000>","Answered","","",communication_internal);
    }
    @Test
    public void K_ExtensionGroupSetting() throws InterruptedException {
        Reporter.infoExec("添加分机组extgroup1");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        extensionGroup.extensionGroup.click();
        m_extension.addExtensionGroup("extgroup1",1000,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109);
        ys_apply();
    }


    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
