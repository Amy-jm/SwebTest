package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Caroline on 2018/1/10.
 */
public class Extensions_FeatureCode extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：====== Extensions_FeatureCode ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A_RegisterExtensions(){
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        closePbxMonitor();
    }
    @Test
    public void B1_addExtension(){
        Reporter.infoExec(" 添加分机1200");
        pageDeskTop.settings.click();
        ys_waitingTime(1000);
        settings.extensions_panel.click();
        ys_waitingTime(1000);
        extensions.Extensions.click();
        m_extension.addSipExtension(1200, "Yeastar202");
        ys_apply();
//        注册1200
        Reporter.infoExec(" 主测设备注册分机1200"); //执行操作
        pjsip.Pj_CreateAccount(1200, "Yeastar202", "UDP", UDP_PORT, 10);
        pjsip.Pj_Register_Account(1200, DEVICE_IP_LAN);

        closePbxMonitor();
    }
    @Test
    public void B2_RingTimeout_default1(){
//        通话验证——No Answer走到voicemail
        pjsip.Pj_Make_Call_No_Answer(1000, "1200", DEVICE_IP_LAN, false);
        ys_waitingTime(40000);//验证默认的RingTimeout，30秒未接后走到follow me目的地
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1200 <1200>","Voicemail"," "," ",communication_internal);
    }
    @Test
    public void B2_RingTimeout_default2(){
//        通话验证——When Busy走到voicemail
        pjsip.Pj_Make_Call_Auto_Answer(1200, "6400", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        pjsip.Pj_Make_Call_Auto_Answer(1000, "1200", DEVICE_IP_LAN, false);
        ys_waitingTime(6000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1200 <1200>","Voicemail"," "," ",communication_internal);
    }
    @Test
    public void B3_RingTimeout_edit(){
        Reporter.infoExec(" 编辑呼出路由RingTimeout为10s,");
        gridClick(extensions.grid, gridFindRowByColumn(extensions.grid, extensions.gridcolumn_Name, "1200", sort_ascendingOrder), extensions.gridEdit);
        ys_waitingMask();
        comboboxSelect(addExtensionFeatures.noAnswer,"e");
        comboboxSet(addExtensionFeatures.noAnswer, extensionList, "1100");
        comboboxSelect(addExtensionFeatures.whenBusy,"e");
        comboboxSet(addExtensionFeatures.whenBusy, extensionList, "1100");
        executeJs("Ext.getCmp('"+addExtensionFeatures.ringTimeout+"').setValue('10')");
        addExtensionFeatures.save.click();
        ys_waitingLoading(extensions.grid_Mask);
    }
    @Test
    public void B4_RingTimeout_check(){
        //        通话验证——No Answer走到voicemail
        pjsip.Pj_Make_Call_No_Answer(1000, "1200", DEVICE_IP_LAN, false);
        ys_waitingTime(15000);//验证默认的RingTimeout，30秒未接后走到follow me目的地

        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1200 <1200>","Voicemail"," "," ",communication_internal);
    }

    @Test
    public void recovery(){
//        删除分机1200
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：====== Extensions_FeatureCode ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
