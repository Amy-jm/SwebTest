package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import org.testng.annotations.*;

import java.lang.reflect.Method;


/**
 * Created by AutoTest on 2017/10/19.
 */
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class IVR extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：======  IVR  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void InitIVR(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        ivr.add.shouldBe(Condition.exist);
        deletes(" 删除所有IVR",ivr.grid,ivr.delete,ivr.delete_yes,ivr.grid_Mask);
        Reporter.infoExec(" 添加IVR1：6500,按1到分机1000"); //执行操作
        m_callFeature.addIVR("IVR1","6500");

        Reporter.infoExec(" 编辑IVR1:按1到分机1000"); //执行操作
        gridClick(ivr.grid,Integer.parseInt(String.valueOf(gridLineNum(ivr.grid))),ivr.gridEdit);
        add_ivr_keyPressEvent.keyPressEvent.click();
        ys_waitingTime(1000);
        comboboxSelect(add_ivr_keyPressEvent.s_press1,add_ivr_keyPressEvent.s_extensin);
        comboboxSet(add_ivr_keyPressEvent.d_press1,extensionList,"1000");
        add_ivr_keyPressEvent.save.click();
        ys_waitingLoading(ivr.grid_Mask);

        Reporter.infoExec(" 新建IVR6502,默认设置");
        m_callFeature.addIVR("IVR6502","6502");
    }

    @Test(priority = 0,groups = "A")
    public void A0_Register() {
        pjsip.Pj_Init();
        //        被测设备注册分机1000、1103、1105，辅助1：分机3001，辅助2：分机2000、2001
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1103,EXTENSION_PASSWORD,"UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }

    @Test(priority =1,groups = "A" )
    public void A1_add() {
        Reporter.infoExec(" 新建IVRtest1，提示音选择autotestprompt，勾选Dial Extensions，勾选Dial Outbound Routes，勾选Dial to Check Voicemail" +
                "按0到分机1000，按1到1000的Voicemail，按5到Dial by Name，按#到hungup，按*到Select an Option，Timeout到customPrompt：prompt1，Invalid到分机1105"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ivr.add.shouldBe(Condition.exist);
        ivr.add.click();
        ys_waitingMask();
        add_ivr_basic.number.clear();
        add_ivr_basic.number.setValue("6501");
        add_ivr_basic.name.clear();
        add_ivr_basic.name.setValue("IVRtest1");
        comboboxSelect(add_ivr_basic.prompt,"autotestprompt");
//        setCheckBox(add_ivr_basic.dialExtensions,true);
        add_ivr_basic.choiceEnableNumber(true);
        setCheckBox(add_ivr_basic.dialOutboundRoutes,true);
        ys_waitingTime(1000);
//        全选呼出路由
        listSelectAll(add_ivr_basic.listOutboundRoutes);
        setCheckBox(add_ivr_basic.dialtoCheckVoicemail,true);
        if(add_ivr_basic.checkVoicemail_ok.isDisplayed()){
            add_ivr_basic.checkVoicemail_ok.click();
        }
//        设置Key Press Event
        ys_waitingTime(1000);
        add_ivr_keyPressEvent.keyPressEvent.click();
        ys_waitingTime(2000);
        comboboxSelect(add_ivr_keyPressEvent.s_press0,s_extensin);
        comboboxSelect(add_ivr_keyPressEvent.s_press1,s_voicemail);
        comboboxSelect(add_ivr_keyPressEvent.s_press5,s_dialByName);
        comboboxSelect(add_ivr_keyPressEvent.s_pressj,s_hangup);
        comboboxSelect(add_ivr_keyPressEvent.s_pressx,s_selectanOption);
        comboboxSelect(add_ivr_keyPressEvent.timeout,s_customPrompt);
        comboboxSelect(add_ivr_keyPressEvent.invaild,s_extensin);
        comboboxSet(add_ivr_keyPressEvent.d_press0,extensionList,"1000");
        comboboxSet(add_ivr_keyPressEvent.d_press1,extensionList,"1000");
        comboboxSet(add_ivr_keyPressEvent.d_timeout,nameList,"prompt1");
        comboboxSet(add_ivr_keyPressEvent.d_invalid,extensionList,"1105");
        add_ivr_keyPressEvent.save.click();
        ys_waitingLoading(ivr.grid_Mask);
        ys_apply();
    }

    @Test(priority =2,groups = "A")
    public void B_editInbound1() {
        Reporter.infoExec(" 编辑呼入路由Inbound1，呼入到IVRtest1"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_ivr);
        comboboxSet(add_inbound_route.destination,"name","IVRtest1");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

//    Dial Extensions
    @Test(priority = 3)
    public void C_dialextensions() {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入IVRtest1,直拨分机1103"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"1","1","0","3","#");
        ysAssertWithHangup(getExtensionStatus(1103,RING,10),RING,"预期1103会Ring");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","xlq <6501(1103)>","Answered",SIPTrunk," ",communication_inbound);
    }

//    Dial Outbound Routes
    @Test(priority = 4)
    public void D_dialoutbound() {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入IVRtest1,直拨3333通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"3","3","3","3","3","#");
//        getExtensionStatus(2000,RING,10);
        ysAssertWithHangup(getExtensionStatus(2000,RING,10),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6501(33333)","Answered", SIPTrunk,SPS,communication_outRoute);
    }

    @Test(priority = 5)
    public void E_key0toExtension() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入IVRtest1,按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,10),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6501(1000)>","Answered",SPS," ",communication_inbound);
    }

    @Test(priority = 6)
    public void F_key1toVoicemail() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入IVRtest1,按1到voicemail-1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2001,"1");
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6501(1000)","Voicemail",SPS," ",communication_inbound);
    }

    @Test(priority = 7)
    public void G_key5toDialName() {
        Reporter.infoExec(" 1000拨打6501，按5到DialbyName,按957到分机1103"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6501",DEVICE_IP_LAN,true);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1000,"5");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"9","5","7");
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(1000,"1");
       ysAssertWithHangup(getExtensionStatus(1103,RING,10),RING,"预期1103为Ring状态");
        pjsip.Pj_Answer_Call(1103,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","xlq <6501(1103)>","Answered","","",communication_internal);
    }

    @Test(priority = 8)
    public void H_keytoHangup() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入IVRtest1,按#挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2001,"#");
        ys_waitingTime(2000);
        getExtensionStatus(2001,HUNGUP,10);
        ysAssertWithHangup(getExtensionStatus(2001,HUNGUP,10),HUNGUP,"预期2001会HangUp");
        m_extension.checkCDR("2001 <2001>","6501(h)","Answered",SPS,"",communication_inbound);
    }

    @Test(priority = 9)
    public void I_keytoSelectOption() {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入IVRtest1，按*到SelecttoOption--预期1105响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2001,"*");
        ys_waitingTime(2000);
//        getExtensionStatus(1105,RING,10);
        ysAssertWithHangup(getExtensionStatus(1105,RING,10),RING,"预期1105为Ring状态");
        pjsip.Pj_Answer_Call(1105,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <6501(1105)>","Answered",SPS,"",communication_inbound);
    }

    @Test(priority =10 )
    public void J_keytoTimeout() {
        Reporter.infoExec(" 1103拨打6501，超时到prompt1"); //执行操作
        tcpSocket.connectToDevice(50000);
        pjsip.Pj_Make_Call_No_Answer(1103,"6501",DEVICE_IP_LAN,true);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("record/prompt1");
        tcpSocket.closeTcpSocket();
        System.out.println("Q_IVRtoCustomCall TcpSocket return: "+showKeyWord);
        if(showKeyWord){
            ys_waitingTime(10000);
            Reporter.infoExec(" 1103按0到分机1000");
            pjsip.Pj_Send_Dtmf(1103,"0");
//            getExtensionStatus(1000,RING,10);
            ysAssertWithHangup(getExtensionStatus(1000,RING,10),RING,"预期1000为Ring状态");
            pjsip.Pj_Answer_Call(1000,false);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("xlq <1103>","1000 <6501(1000)>","Answered","","",communication_internal);
        }
        pjsip.Pj_Hangup_All();
    }

    @Test(priority = 11)
    public void K_keytoInvalid() {
        Reporter.infoExec(" 1000拨打6501，按a错误按键到分机1105"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000,"6501",DEVICE_IP_LAN);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"a");
//        getExtensionStatus(1105,RING,10);
        ysAssertWithHangup(getExtensionStatus(1105,RING,10),RING,"预期1105为Ring状态");
        pjsip.Pj_Answer_Call(1105,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <6501(1105)>","Answered","","",communication_internal);
    }

//Dial to Check Voicemail
    @Test(priority = 12)
    public void L_checkvoicemail() {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入IVRtest1,直接拨打*02查看1000的语音留言"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"*","0","2");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","0","0","0","#");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","0","0","0");
        ys_waitingTime(2000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6501(*02)","Answered",SPS,"",communication_inbound);
    }

//    Delete
    @Test(priority = 13)
    public void M_delete() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        ivr.IVR.click();
        ivr.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：IVR6502-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"IVR6502",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        gridClick(ivr.grid,row,ivr.gridDelete);
        ivr.delete_no.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：IVR6502-取消删除");

        Reporter.infoExec(" 表格删除：IVR6502-确定删除"); //执行操作
        gridClick(ivr.grid,row,ivr.gridDelete);
        ivr.delete_yes.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：IVR6502-确定删除");

        Reporter.infoExec(" 删除：IVRtest1-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"IVRtest1",sort_ascendingOrder)));
        gridCheck(ivr.grid,row4,ivr.gridcolumn_Check);
        ivr.delete.click();
        ivr.delete_no.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：IVRtest1-取消删除");

        Reporter.infoExec(" 删除：IVRtest1-确定删除"); //执行操作
        ivr.delete.click();
        ivr.delete_yes.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：IVRtest1-确定删除");
        ys_apply();
    }

    @Test(priority = 14)
    public void N_editInbound2() {
        Reporter.infoExec(" 编辑呼入路由Inbound1，呼入到分机1000"); //执行操作
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

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() {
        ys_waitingTime(5000);
        Reporter.infoAfterClass("执行完毕：======  IVR ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
