package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Yeastar on 2017/10/26.
 */
public class Disa_PinList extends SwebDriver {
    @BeforeClass
    public void A_Login() {

        Reporter.infoBeforeClass("开始执行：======  Disa_PinList  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void C_InitDisa_PinList(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(3000);
        }
        callFeatures.more.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        deletes("初始化DISA--删除所有DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        pinList.PINList.click();
        pinList.add.shouldBe(Condition.exist);
        deletes("初始化PIN LIST--删除所有pin码",pinList.grid,pinList.delete,pinList.delete_yes,pinList.grid_Mask);
    }
    @Test
    public  void A0_register(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(3002,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3002,DEVICE_ASSIST_1);
        closePbxMonitor();
    }
    @Test
    public void A1_CreatePin() {
        Reporter.infoExec("创建PinList：pin1,123-456"); //执行操作
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
    public void B1_CreateDisa() {
        Reporter.infoExec("创建DISA1：选择pin1,呼出路由：OutRoute1_sip、OutRoute3_sps"); //执行操作
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
    public void B2_EditInroute() {
        Reporter.infoExec("编辑呼入路由InRoute1，呼入目的地到DISA1"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        add_inbound_route.save.shouldBe(Condition.exist);
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        comboboxSelect(add_inbound_route.destinationType,s_disa);
        ys_waitingTime(3000);
        comboboxSet(add_inbound_route.destination,nameList,"DISA1");
        ys_waitingTime(2000);
        add_inbound_route.save.click();
        ys_waitingTime(5000);
        ys_apply();
    }

    @Test
    public void C1_CalltoDISA1() throws InterruptedException {
        Reporter.infoExec("2001拨打991100通过sps外线呼入到DISA1，输入pin码123#，二次拨号：13001#，预期3001响铃并接听");
//        tcpSocket.connectToDevice();
        pjsip.Pj_Make_Call_Auto_Answer(2001,"991100",DEVICE_ASSIST_2);
//        boolean showKeyWord= tcpSocket.getAsteriskInfo("pin");
//        tcpSocket.closeTcpSocket();
//        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(10000);
        ysAssertWithHangup(getExtensionStatus(3001,TALKING,20),TALKING,"预期3001会Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);

    }

    @Test
    public void D1_EditDisa() {
        Reporter.infoExec("编辑DISA1，Password：选择Single Pin--789，ResponseTimeout：15，DightTimeout：10"); //执行操作
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
    }

    @Test
    public void D2_CalltoDISA1() throws InterruptedException {
        Reporter.infoExec("3001拨打3000通过sip外线呼入到DISA1，输入pin码：789#，二次拨号13002#，预期3002响铃并接听");
//        tcpSocket.connectToDevice(60000);
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
//        boolean showKeyWord= tcpSocket.getAsteriskInfo("DISA1");
//        tcpSocket.closeTcpSocket();
//        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pin1List");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(3001,"7","8","9","#");
        pjsip.Pj_Send_Dtmf(3001,"1","3","0","0","2","#");
        ys_waitingTime(15000);
        ysAssertWithHangup(getExtensionStatus(3002,TALKING,20),TALKING,"预期3002会Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","13002","Answered",SIPTrunk,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"789",1);
    }

    @Test
    public void E_Disa1() {
        Reporter.infoExec("3001拨打3000通过sip外线呼入到DISA1,输入完pin码789#后，不输人数字等待15s后通话被挂断"); //执行操作
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
        ys_waitingTime(5000);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  Disa_PinList  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();
    }
}
