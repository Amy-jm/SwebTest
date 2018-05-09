package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by Yeastar on 2017/7/19.
 */
public class InboundRoutes extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备_InboundRoutesTest"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @BeforeClass
    public void InitInboundRoutes() {
        if(Single_Init){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.callControl_panel.click();
            inboundRoutes.inboundRoutes.click();
            ys_waitingLoading(inboundRoutes.grid_Mask);
            if(Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid))) != 0){
                gridSeleteAll(inboundRoutes.grid);
                inboundRoutes.delete.click();
                inboundRoutes.delete_yes.click();
            }
            closeSetting();
        }
    }
    @BeforeMethod
    public void waitMethod(){
        ys_waitingTime(1000);
    }
//    @Test
    public void A_EmailSettings() throws InterruptedException {
        Reporter.infoExec("设置Email");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.email_panel.click();
        email.emailAddress.setValue("gagatest@sina.com");
        email.password.setValue("yeastar6205");
        email.SMTP.setValue("smtp.sina.com");
        email.POP3.setValue("pop.sina.com");
        email.save.click();
        ys_waitingMask();
        ys_apply();
//        email.test.click();
//        YsAssert.assertEquals(email.SMTP.text(),"smtp.sina.com");
//        YsAssert.assertEquals(email.POP3.text(),"pop.sina.com");
    }
    @Test
    public void B_CreateInboundRoutes() {
        Reporter.infoExec("创建呼入路由");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        inboundRoutes.inboundRoutes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        m_callcontrol.addInboundRoutes("inrouter1","","","all");
        /**
         * 设置Time contition
         */
        ys_waitingLoading(inboundRoutes.grid_Mask);
        gridClick(inboundRoutes.grid,1,inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,true);
        add_inbound_route.addTimeCondition.click();
        add_inbound_route.SetTimeConditionTableviewDestition(1,1,"time1");
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_ivr);
        add_inbound_route.SetTimeConditionTableviewDestition(1,3,"ivr1");
        add_inbound_route.SetTimeConditionTableviewDestination(2,2,add_inbound_route.s_voicemail);
        add_inbound_route.save.click();
        ys_apply();

    }
    @Test
    public void C_RegisterExtensions() throws InterruptedException {
        Reporter.infoExec("注册测试分机");

        if(!Single_Device_Test){
            pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,3);
            pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,4);
            pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,5);
            pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
            pjsip.Pj_CreateAccount(3000,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
//            pjsip.Pj_CreateAccount(2010,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
//            pjsip.Pj_CreateAccount(3030,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
//            pjsip.Pj_CreateAccount(3034,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
//            pjsip.Pj_CreateAccount(9999,"Yeastar202","UDP",ASSIST_PORT,-1);
//            pjsip.Pj_CreateAccount(8888,"Yeastar202","UDP",ASSIST_PORT,-1);

            pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account(1101, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account(1102, DEVICE_IP_LAN);
            pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
            pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
        }

    }
    @Test
    public void D_CallSip() throws InterruptedException {
        Reporter.infoExec("设备3通过sip外线呼入到设备1，听到IVR提示音后，按0");
        pjsip.Pj_Make_Call_No_Answer(3000,"3030",DEVICE_ASSIST_1);
        pjsip.Pj_Send_Dtmf(3000,"0");
        ys_waitingTime(1000);
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3000 <3000>","1100 <6500(1100)>","Answered",SIPTrunk,"",communication_inbound);
    }
    @Test
    public void E_CallIax() throws InterruptedException {
        Reporter.infoExec("设备3通过iax外线呼入到设备1，听到IVR的提示音后，按1");
        pjsip.Pj_Make_Call_No_Answer(3000,"3034",DEVICE_ASSIST_1);
        System.out.println("DTMF send suc "+pjsip.Pj_Send_Dtmf(3000,"1"));;
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3000 <3000>","6500(1100)","Voicemail");
    }
    @Test
    public void F_CallSps() throws InterruptedException {
        Reporter.infoExec("设备2通过sps外线呼入到设备1，听到IVR的提示音后，按2");
        pjsip.Pj_Make_Call_No_Answer(2000,"9999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"2");
        ys_waitingTime(15000);
        String actualStatusRinging = String.valueOf(gridExtensonStatus(extensions.grid_status,4,0));
        System.out.println("actualStatus " + actualStatusRinging);
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        String actualStatusRegistered = String.valueOf(gridExtensonStatus(extensions.grid_status,4,0));

        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6200(1100)>","Answered");
//        YsAssert.assertEquals(actualStatusRinging,"Ringing");
        YsAssert.assertEquals(actualStatusRegistered,"Registered");
    }
    @Test
    public void G_CallSpx() throws InterruptedException {
        Reporter.infoExec("A拨打90+分机号码（设备3的分机），SPX通话");
        pjsip.Pj_Make_Call_No_Answer(2000,"8888",DEVICE_ASSIST_2);
        pjsip.Pj_Send_Dtmf(2000,"3");
        String actualStatusRinging = String.valueOf(gridExtensonStatus(extensions.grid_status,4,0));

        ys_waitingTime(8000);
        pjsip.Pj_Answer_Call(1101,true);
        ys_waitingTime(5000);
        String actualStatusRegistered = String.valueOf(gridExtensonStatus(extensions.grid_status,5,0));
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1101 <6700(1101)>","Answered");
//        YsAssert.assertEquals(actualStatusRinging,"Ringing");
        YsAssert.assertEquals(actualStatusRegistered,"Registered");
    }
    @Test
    public void H_CallGsm() throws InterruptedException {
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec("A拨打90+分机号码（设备3的分机），GSM通话");
        if(PRODUCT.equals(PC) || PRODUCT.equals(CLOUD_PBX)){
            pjsip.Pj_Make_Call_No_Answer(2000,"9999",DEVICE_ASSIST_2);
        }else {
            pjsip.Pj_Make_Call_No_Answer(2000, DEVICE_ASSIST_GSM,DEVICE_ASSIST_2);
        }
        pjsip.Pj_Send_Dtmf(2000,"4");
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>", DEVICE_ASSIST_GSM,"Answered");
    }
    @Test
    public void I_CallPstn() throws InterruptedException {
        Reporter.infoExec("A拨打90+分机号码（设备3的分机），PSTN通话");
        if(PRODUCT.equals(PC) || PRODUCT.equals(CLOUD_PBX)){
            pjsip.Pj_Make_Call_No_Answer(2000,"9999",DEVICE_ASSIST_2);
        }else {
            pjsip.Pj_Make_Call_No_Answer(2000,"2010",DEVICE_ASSIST_2);
        }
        Thread.sleep(5000);
        pjsip.Pj_Send_Dtmf(2000,"5");
        Thread.sleep(2000);
        pjsip.Pj_Send_Dtmf(2000,"9","0","3","0","0","0");
        Thread.sleep(5000);
        pjsip.Pj_Answer_Call(3000,false);
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6500(903000)","Answered","",SIPTrunk,communication_outRoute);
    }
    @Test
    public void J_CallE1() throws InterruptedException {
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec("A拨打90+分机号码（设备3的分机），E1通话");
        if(PRODUCT.equals(PC) || PRODUCT.equals(CLOUD_PBX)){
            pjsip.Pj_Make_Call_No_Answer(2000,"9999",DEVICE_ASSIST_2);
        }else {
            pjsip.Pj_Make_Call_No_Answer(2000,"5555",DEVICE_ASSIST_2);
        }
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2000,"6");
        ys_waitingTime(10000);
        pjsip.Pj_Answer_Call(2000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1100 <1100>","Answered",E1,"",communication_callback);
    }
    @Test
    public void K_SetDidSps() throws InterruptedException {
        Reporter.infoExec("编辑呼入路由,设置DID");
        if(Single_Device_Test){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.callControl_panel.click();
        }

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(inboundRoutes.grid_Mask);

        gridClick(inboundRoutes.grid, 1,inboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(3000);
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+add_inbound_route.destinationType+"').setValue('E')");
        ys_waitingTime(2000);
        add_inbound_route.destinationInput.shouldBe(Condition.exist).setValue("1100-1105");
        add_inbound_route.DIDPatem.setValue("2000-2005");
        add_inbound_route.save.click();
        ys_apply();
    }
    @Test
    public void L_CallDidSps() throws InterruptedException {
        Reporter.infoExec("设备2通过sps外线呼入到设备（呼出前缀后+分机B）");
        pjsip.Pj_Make_Call_Auto_Answer(2000,"992000",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        String actualStatusRegistered = String.valueOf(gridExtensonStatus(extensions.grid_status,4,0));
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <1100>","Answered",SPS,"",communication_inbound);
//        YsAssert.assertEquals(actualStatusRinging,"Ringing");
        YsAssert.assertEquals(actualStatusRegistered,"Registered");
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("关闭游览器InboundRoutesTest"); //执行操作
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        quitDriver();
        Thread.sleep(10000);
    }
}
