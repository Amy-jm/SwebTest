package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.sun.jna.platform.win32.WinNT;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/10/26.
 */
public class Disa_PinList extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  Disa_PinList  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",2);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(3002,"Yeastar202","UDP",-1);

        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3002,DEVICE_ASSIST_1);
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
    }

    @BeforeClass
    public void InitDisa_PinList(){
        disa.DISA.click();
        deletes("初始化DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        pinList.PINList.click();
        deletes("初始化PIN LIST",pinList.grid,pinList.delete,pinList.delete_yes,pinList.grid_Mask);
    }

    @Test
    public void A_CreatePin() {
        Reporter.infoExec("--创建PinList--"); //执行操作
        pinList.PINList.click();
        pinList.add.shouldBe(Condition.exist).click();
        add_pin_list.name.setValue("pin1");
        add_pin_list.recordInCDR.click();
        add_pin_list.PINList.setValue("123-456");
        add_pin_list.save.click();
        ys_waitingLoading(pinList.grid_Mask);
        ys_apply();
        String actName = String.valueOf(gridContent(pinList.grid,gridFindRowByColumn(pinList.grid,pinList.gridcolumn_Name,"pin1",sort_ascendingOrder),pinList.gridcolumn_Name));
        YsAssert.assertEquals(actName,"pin1");
    }

    @Test
    public void B_CreateDisa() {
        Reporter.infoExec("--创建DISA1--"); //执行操作
        disa.DISA.click();
        disa.add.click();
        ys_waitingTime(8000);
        add_disa.name.setValue("DISA1");
        comboboxSelect(add_disa.password,add_disa.passwordType_Pinset);
        ys_waitingTime(2000);
        comboboxSet(add_disa.password_Pinset,nameList,"pin1");
        listSelect(add_disa.list,nameList,"OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
        String actName = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_Name));
        YsAssert.assertEquals(actName,"DISA1");
    }

    @Test
    public void C_EditInroute() {
        Reporter.infoExec("--编辑呼入路由--"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        add_inbound_route.save.shouldBe(Condition.exist);
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        comboboxSelect(add_inbound_route.destinationType,s_disa);
        ys_waitingTime(3000);
        comboboxSet(add_inbound_route.destination,nameList,"DISA1");
        add_inbound_route.save.click();
        ys_apply();


        Reporter.infoExec("辅助设备2通过sps呼叫被测设备1100");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("pin");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);
    }

    @Test
    public void D_EditDisa() {
        Reporter.infoExec("--编辑DISA1，Password：选择Single Pin--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridEdit);
        comboboxSelect(add_disa.password,add_disa.passwordType_Singlepin);
        add_disa.password_Singlepin.setValue("789");
        add_disa.responseTimeout.setValue("15");
        add_disa.dightTimeout.setValue("10");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
        String res = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_ResponseTimeout));
        String dig = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_DigtTimeout));
        YsAssert.assertEquals(res,"15");
        YsAssert.assertEquals(dig,"10");

        Reporter.infoExec("辅助设备1通过SIP1拨打到被测设备，在进行二次拨号");
        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("DISA1");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        pjsip.Pj_Send_Dtmf(3001,"7","8","9","#");
        pjsip.Pj_Send_Dtmf(3001,"1","3","0","0","2","#");
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("3001 <3001>","13002","Answered",SIPTrunk,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"789",1);

    }
    @Test
    public void E_Disa1() {
        Reporter.infoExec("通过SIP1呼入,输入完pin码后，不输人数字等待15s后通话被挂断"); //执行操作
        tcpSocket.connectToDevice(60000);
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("DISA1");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        pjsip.Pj_Send_Dtmf(3001,"7","8","9","#");
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(3001,HUNGUP,10), HUNGUP);
    }

    @Test
    public void G_Delete() throws InterruptedException {
        Reporter.infoExec("--删除DISA--"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        disa.DISA.click();
        gridClick(disa.grid,1,disa.gridDelete);
        disa.delete_yes.click();
        ys_waitingLoading(disa.grid_Mask);
        ys_apply();
        m_callFeature.addDISA("disa2","",0,0,"OutRoute1_sip");
        deletes("批量删除DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        ys_waitingLoading(disa.grid_Mask);
    }

    @Test
    public void G_Recovery() throws InterruptedException {
        Reporter.infoExec(" 恢复呼入路由InRoute1到分机1000");
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1000");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  Disa_PinList  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
