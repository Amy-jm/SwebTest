package com.yeastar.swebtest.testcase.smokecase.syscase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Created by Yeastar on 2017/8/16.
 */
public class UserPermission extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {

        Reporter.infoBeforeClass("打开游览器并登录设备_UserPermission"); //执行操作
        initialDriver(BROWSER,"http://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
        m_extension.showCDRClounm();
    }
    @BeforeClass
    public void CaseName() {
      ys_waitingTime(1500);

    }
    /**
     * 1）User设置为分机A，Set Privilege As为Administrator，网页设置项默认全部勾选，点击“Save”并点击“Apply”
     2）登陆分机A的网页
     */
    @Test
    public void A_AddUserPermissionAdmin() {
        Reporter.infoExec("添加UserPermession,admin权限"); //执行操作
        pageDeskTop.settings.click();
        settings.userPermission_panel.click();
        m_userPermission.addUserPermission(1100,grant_privilege_application.privilegeAs_Administrator);
//        ys_apply();

        String username= String.valueOf(gridContent(userPermission.grid,1,userPermission.gridColumn_User)) ;
        String role = String.valueOf(gridContent(userPermission.grid,1,userPermission.gridColumn_Role));
        YsAssert.assertEquals(username,"1100 - 1100","新添加的User");
        YsAssert.assertEquals(role,"Administrator","新添加的Role");
    }
    @Test
    public void B_CheckUserPermisson() {
        Reporter.infoExec("登录分机A 1100的页面，检查主菜单内所有勾选功能"); //执行操作
        logout();
        login("1100","Yeastar202");
        me.taskBar_Main.click();
        me.settingShortcut.shouldBe(Condition.exist);
        me.cdrShortcut.shouldBe(Condition.exist);
        me.pbxmonitorShortcut.shouldBe(Condition.exist);
        me.resourcemonitorShortcut.shouldBe(Condition.exist);
        me.applicationCenterShortcut.shouldBe(Condition.exist);
        me.maintanceShortcut.shouldBe(Condition.exist);
        me.mesettingShortcut.shouldBe(Condition.exist);
        me.autopShortcut.shouldBe(Condition.exist);
        me.helpShortcut.shouldBe(Condition.exist);
    }
    /**
     * 1）User设置为分机B，Set Privilege As为Custom，网页设置项默认未勾选，勾选PBX、Auto Provisioning，点击“Save”并点击“Apply”
     2）登陆分机B的网页
     */
    @Test
    public void C_AddUserPermissionCustom() {
        Reporter.infoExec("User设置为分机B 1101，Set Privilege As为Custom，网页设置项默认未勾选，勾选PBX、Auto Provisioning"); //执行操作
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.click();
        settings.userPermission_panel.click();
        ys_waitingLoading(userPermission.grid_Mask);
        m_userPermission.addUserPermission(1101,grant_privilege_application.privilegeAs_Custom);
        gridClick(userPermission.grid,2,userPermission.gridColumnEdit);
        ys_waitingMask();
        grant_privilege_settings.PBX.click();
        grant_privilege_application.grant_Privilege_Application.click();
        grant_privilege_application.autoProvisioning.click();
        grant_privilege_application.save.click();
        ys_waitingLoading(userPermission.grid_Mask);

        String username= String.valueOf(gridContent(userPermission.grid,2,userPermission.gridColumn_User)) ;
        String role = String.valueOf(gridContent(userPermission.grid,2,userPermission.gridColumn_Role));
        YsAssert.assertEquals(username,"1101 - 1101");
        YsAssert.assertEquals(role,"Custom");
    }
    @Test
    public void D_CheckUserPerssion() {
        Reporter.infoExec("登录分机A 1101的页面，检查主菜单内所有勾选功能"); //执行操作
        logout();
        login("1101","Yeastar202");
        me.taskBar_Main.click();
        me.autopShortcut.shouldBe(Condition.exist);
        me.settingShortcut.click();
        ys_waitingTime(2000);
        //Ext.query('#cp-navigation-innerdiv'+ ' .cp-navigation-title')[0].textContent
        String titleNum = String.valueOf(return_executeJs("Ext.query('#cp-navigation-innerdiv'+ ' .cp-navigation-title').length"));
        String pbx = String.valueOf(return_executeJs("Ext.query('#cp-navigation-innerdiv'+ ' .cp-navigation-title')[0].textContent"));

        YsAssert.assertEquals(titleNum,"1","Setting界面只有PBX");
        YsAssert.assertEquals(pbx,"PBX","Setting界面只有PBX");
    }
    @Test
    public void E_Delete() {
        Reporter.infoExec("选择User为分机A的数据，点击“Delete”图标，点击yes"); //执行操作
        executeJs(me.closeMeSetting);
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.click();
        settings.userPermission_panel.click();

        gridClick(userPermission.grid,1,userPermission.gridColumnDelete);
        userPermission.delete_yes.click();
    }
    @Test
    public void F_CheckUserPerssion() {
        Reporter.infoExec("登录分机A 1101的页面，检查主菜单内只有ME功能"); //执行操作
        logout();
        login("1101","Yeastar202");
        me.taskBar_Main.click();
        me.mesettingShortcut.shouldBe(Condition.exist);

    }
    @Test
    public void G_DeleteAll() {
        Reporter.infoExec("勾选全部UserPermission,删除"); //执行操作
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.click();
        settings.userPermission_panel.click();
        gridSeleteAll(userPermission.grid);
        userPermission.delete.click();
        userPermission.delete_yes.click();
        String line = String.valueOf(gridLineNum(userPermission.grid));
        YsAssert.assertEquals(line,"0","全部UserPermission,删除");
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作

        quitDriver();
        Thread.sleep(10000);
    }
}
