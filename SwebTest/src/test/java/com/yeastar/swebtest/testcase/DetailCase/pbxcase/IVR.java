package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.read.biff.BiffException;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.file.ExcelUnit;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.junit.After;
import org.testng.annotations.*;

import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by AutoTest on 2017/11/27.
 */
public class IVR extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：=======  IVR  ======="); //执行操作
        initialDriver(BROWSER, "https://" + DEVICE_IP_LAN + ":" + DEVICE_PORT + "/");
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        if (!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9) {
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @Test
    public void A0_Init1() {
        if(!PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        if(!PRODUCT.equals(PC)){
            return;
        }
        resetoreBeforetest("BeforeTest_Local.bak");
     }
    @Test
    public void A0_Init2() {
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,EXTENSION_PASSWORD,"UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1103,EXTENSION_PASSWORD,"UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
    }

    @Test
    public void A1_Init() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        ivr.IVR.click();
        ivr.add.shouldBe(Condition.exist);
        deletes(" 删除所有IVR",ivr.grid,ivr.delete,ivr.delete_yes,ivr.grid_Mask);
    }

    @DataProvider(name="add")
    public Object[][] Numbers() throws BiffException, IOException{
        ExcelUnit e=new ExcelUnit("database", "ivr");
        return e.getExcelData();
    }

    @Test(dataProvider="add")
    public void A2_AddIVR(HashMap<String, String> data)  {
        Reporter.infoExec("添加IVR："+data.get("Name")+"："+data.get("Number"));
        ivr.add.shouldBe(Condition.exist);
        ivr.add.click();
        ys_waitingMask();
        add_ivr_basic.number.setValue(data.get("Number"));
        add_ivr_basic.name.setValue(data.get("Name"));
        comboboxSelect(add_ivr_basic.promptRepeatCount,data.get("RepeatCount"));
        add_ivr_basic.responseTimeout.setValue(data.get("Response"));
        add_ivr_basic.dightTimeout.setValue(data.get("Digit"));
        setCheckBox(add_ivr_basic.dialExtensions,Boolean.valueOf(data.get("DialExtension")));
        setCheckBox(add_ivr_basic.dialOutboundRoutes,Boolean.valueOf(data.get("DialOutbound")));
        if(data.get("DialOutbound").equals("true")){
            listSelectAll(add_ivr_basic.listOutboundRoutes);
        }
        setCheckBox(add_ivr_basic.dialtoCheckVoicemail,Boolean.valueOf(data.get("CheckVoicemail")));
        if(data.get("CheckVoicemail").equals("true")){
            if (add_ivr_basic.checkVoicemail_ok.isDisplayed()) {
                add_ivr_basic.checkVoicemail_ok.click();
            }
        }
        add_ivr_keyPressEvent.keyPressEvent.click();
        ys_waitingTime(1000);
        comboboxSelect(add_ivr_keyPressEvent.s_press0,data.get("Key0"));
        comboboxSelect(add_ivr_keyPressEvent.s_press1,data.get("Key1"));
        if (!data.get("Des0").equals("")) {
            if (data.get("Key0").equals("e") || data.get("Key0").equals("v")) {
                comboboxSet(add_ivr_keyPressEvent.d_press0, extensionList, data.get("Des0"));
            } else {
                comboboxSet(add_ivr_keyPressEvent.d_press0, nameList, data.get("Des0"));
            }
        }
        if(!data.get("Des1").equals("")) {
            if (data.get("Key1").equals("e") || data.get("Key1").equals("v")) {
                comboboxSet(add_ivr_keyPressEvent.d_press1, extensionList, data.get("Des1"));
            } else {
                comboboxSet(add_ivr_keyPressEvent.d_press1, nameList, data.get("Des1"));
            }
        }
        add_ivr_keyPressEvent.save.click();
        ys_waitingLoading(ivr.grid_Mask);
    }

    @Test
    public void A3_EditKeys()  {
        Reporter.infoExec(" 编辑IVR：Yeastar202Yeastar202，Prompt：选择prompt1，按2到Voicemail:1105，按3到RingGroup1，按4到Queue1，" +
                "按5到Conference1，按6到prompt1，按7到分机到IVR：*.*，按8到1106（fxs），按9到Dial by Name，按#到分机1100，" +
                "按*到分机1101，Timeout到分机1102，Invalid到prompt1"); //执行操作
        gridClick(ivr.grid,gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"Yeastar202Yeastar202",sort_ascendingOrder),ivr.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_ivr_basic.prompt,"prompt1");
        add_ivr_keyPressEvent.keyPressEvent.click();
        ys_waitingTime(1000);
        comboboxSelect(add_ivr_keyPressEvent.s_press2,s_voicemail);
        comboboxSelect(add_ivr_keyPressEvent.s_press3,s_ringGroup);
        comboboxSelect(add_ivr_keyPressEvent.s_press4,s_queue);
        comboboxSelect(add_ivr_keyPressEvent.s_press5,s_conference);
        comboboxSelect(add_ivr_keyPressEvent.s_press6,s_customPrompt);
        comboboxSelect(add_ivr_keyPressEvent.s_press7,s_ivr);
        comboboxSelect(add_ivr_keyPressEvent.s_press8,s_extensin);
        comboboxSelect(add_ivr_keyPressEvent.s_press9,s_dialByName);
        comboboxSelect(add_ivr_keyPressEvent.s_pressj,s_extensin);
        comboboxSelect(add_ivr_keyPressEvent.s_pressx,s_extensin);
        comboboxSelect(add_ivr_keyPressEvent.timeout,s_extensin);
        comboboxSelect(add_ivr_keyPressEvent.invaild,s_customPrompt);
        comboboxSet(add_ivr_keyPressEvent.d_press2,extensionList,"1105");
        comboboxSet(add_ivr_keyPressEvent.d_press3,nameList,"RingGroup1");
        comboboxSet(add_ivr_keyPressEvent.d_press4,nameList,"Queue1");
        comboboxSet(add_ivr_keyPressEvent.d_press5,nameList,"Conference1");
        comboboxSet(add_ivr_keyPressEvent.d_press6,nameList,"prompt1");
        comboboxSet(add_ivr_keyPressEvent.d_press7,nameList,"*.*");
        if(!FXS_1.equals("null")) {
            comboboxSet(add_ivr_keyPressEvent.d_press8, extensionList, "1106");
        }
        comboboxSet(add_ivr_keyPressEvent.d_pressj,extensionList,"1100");
        comboboxSet(add_ivr_keyPressEvent.d_pressx,extensionList,"1101");
        comboboxSet(add_ivr_keyPressEvent.d_timeout,extensionList,"1102");
        comboboxSet(add_ivr_keyPressEvent.d_invalid,nameList,"prompt1");
        add_ivr_keyPressEvent.save.click();
        ys_waitingLoading(ivr.grid_Mask);
    }

    @Test
    public void B_EditInRoute1()  {
        Reporter.infoExec(" 编辑呼入路由InRoute1，到IVR：Yeastar202Yeastar202"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_ivr);
        comboboxSet(add_inbound_route.destination,"name","Yeastar202Yeastar202");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();

    }

//    通话测试：各种外线呼入到IVR：Yeastar202
    @Test
    public void C1_sip_key0_1000()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(20000);
        System.out.println("3001通话状态："+getExtensionStatus(3001,TALKING,1));
        pjsip.Pj_Send_Dtmf(3001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <6502(1000)>","Answered",SIPTrunk,"",communication_inbound);
    }

    @Test
    public void C2_sps_keyj_1100()  {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到IVR：6502，按#到分机1100"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(25000);
        System.out.println("2001通话状态："+getExtensionStatus(2001,TALKING,1));
        pjsip.Pj_Send_Dtmf(2001,"#");
//        getExtensionStatus(1100,RING,10);
        ysAssertWithHangup(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1100 <6502(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void C3_iax_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到IVR：6502,按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3100",DEVICE_ASSIST_1,false);
        ys_waitingTime(15000);
        pjsip.Pj_Send_Dtmf(3001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <6502(1000)>","Answered",IAXTrunk,"",communication_inbound);
    }

    @Test
    public void C4_spx_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2001拨打88888通过spx外线呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"88888",DEVICE_ASSIST_2,false);
        ys_waitingTime(40000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6502(1000)>","Answered",SPX,"",communication_inbound);
    }

    @Test
    public void C5_fxo_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXO_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2001拨打2010通过fxo外线呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"2010",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6502(1000)>","Answered",FXO_1,"",communication_inbound);
    }

    @Test
    public void C6_bri_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2001拨打66666通过bri外线呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"66666",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6502(1000)>","Answered",BRI_1,"",communication_inbound);
    }

    @Test
    public void C7_e1_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2001拨打77777通过e1外线呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"77777",DEVICE_ASSIST_2,false);
        ys_waitingTime(4000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6502(1000)>","Answered",E1,"",communication_inbound);
    }

    @Test
    public void C8_gsm_key0_1000()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec(" 2001拨打被测设备的GSM号码呼入到IVR：6502，按0到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001, DEVICE_TEST_GSM, DEVICE_ASSIST_2);
        ys_waitingTime(15000);
        pjsip.Pj_Send_Dtmf(2001,"0");
//        getExtensionStatus(1000,RING,10);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <"+DEVICE_ASSIST_GSM+">","1000 <6502(1000)>","Answered",GSM,"",communication_inbound);
    }

//    各种Key到各种目的地
    @Test
    public void D1_key1_callback()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按1到Callback1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        getExtensionStatus(2000,HUNGUP,20);
//        pjsip.Pj_Hangup_All();
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000会响铃");
        pjsip.Pj_Answer_Call(2000,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPS,"",communication_callback);

    }

    @Test
    public void D2_key2_voicemail1()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按2到1105的Voicemail"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"2");
        ys_waitingTime(40000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6502(1105)","Voicemail",SPS,"",communication_inbound);
    }

    @Test
    public void D2_key2_voicemail2()  {
        Reporter.infoExec(" 1105登录查看存在CallerID为2000的语音留言"); //执行操作
        logout();
        if (PRODUCT.equals(CLOUD_PBX)) {
            login("autotest@yeastar.com", EXTENSION_PASSWORD);
        } else {
            login("1105", EXTENSION_PASSWORD);
        }
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_Voicemail.click();
        ys_waitingLoading(me_voicemail.grid_Mask);
        setPageShowNum(me_voicemail.grid,100);
        ys_waitingLoading(me_voicemail.grid_Mask);
        if (Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))) != 0) {
            YsAssert.assertEquals((gridContent(me_voicemail.grid, Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))), me_voicemail.gridColumn_Callerid)), "2000(2000)", "语音留言检查:预期第1行的CallerID为2000(2000)");
        } else {
            YsAssert.fail("语音留言检查:1105分机登录，预期第"+Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid)))+"1行的CallerID为2000(2000)");
        }
    }

    @Test
    public void D2_key2_voicemail3()  {
        Reporter.infoExec(" admin重新登录"); //执行操作
        logout();
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
//        m_extension.showCDRClounm();
    }

    @Test
    public void D3_key3_ringgroup()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按3到RingGroup1,1100应答"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"3");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会响铃");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会响铃");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6200(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D4_key4_queue()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR:6502，按4到Queue1，1105应答"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"4");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会响铃");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会响铃");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <6700(1105)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D5_key5_conference()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按5到Conference1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"5");
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6502(6400)","Answered",SPS,"",communication_inbound);

    }

    @Test
    public void D6_key6_custom_prompt()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按6到prompt1,提示音播放完就挂断"); //执行操作
        tcpSocket.connectToDevice(60000);
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"6");
        boolean showKeyWord= tcpSocket.getAsteriskInfo("record/prompt1");
        tcpSocket.closeTcpSocket();
        Reporter.infoExec("Q_IVRtoCustomCall TcpSocket return: "+showKeyWord);

        ys_waitingTime(10000);
        Reporter.infoExec(" 2000按0到分机1000");
        pjsip.Pj_Send_Dtmf(2000,"0");
        int s_2000=getExtensionStatus(2000,HUNGUP,30);
        Reporter.error("预期2000会挂断,实际"+s_2000);
//        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会挂断");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6502(6)","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D7_key7_ivr()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按7到IVR6504：*.*，按1到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"7");
        ys_waitingTime(10000);
        Reporter.infoExec(" 2000按1到分机1000");
        pjsip.Pj_Send_Dtmf(2000,"1");
        int s_1000= getExtensionStatus(1000,RING,10);
        Reporter.error("预期1000会响铃 实际："+s_1000);
//        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1000 <6504(1000)>","Answered",SPS,"",communication_inbound);
    }

//    FXS分机
    @Test
    public void D8_key8_fxs()  {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXS_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，按8到分机1106（fxs）"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"8");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000会响铃");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1106 <6502(1106)>","Answered",SIPTrunk,"",communication_inbound);

    }

//    DialByName
    @Test
    public void D90_key9_dialbyname()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按9到DialByName按957到分机1103"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,true);
        ys_waitingTime(8000);
        pjsip.Pj_Send_Dtmf(2000,"9");
        ys_waitingTime(10000);
        pjsip.Pj_Send_Dtmf(2000,"9","5","7");
        ys_waitingTime(6000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1103,RING,20),RING,"预期1103为Ring状态");
        pjsip.Pj_Answer_Call(1103,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6502(1103)>","Answered",SPS,"",communication_inbound);

    }

    @Test
    public void D91_keyj_1100()  {
        pjsip.Pj_Hangup_All();
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按#到分机1100"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"#");
        ys_waitingTime(2000);
        ysAssertWithHangup(getExtensionStatus(1100,RING,10),RING,"预期1100为Ring状态");
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6502(1100)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D92_keyx_1101()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按*到分机1101"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2000,"*");
        ys_waitingTime(3000);
        ysAssertWithHangup(getExtensionStatus(1101,RING,20),RING,"预期1101为Ring状态");
        pjsip.Pj_Answer_Call(1101,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1101 <6502(1101)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D93_TimeOut()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，Timeout到分机1102"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(70000);
        ysAssertWithHangup(getExtensionStatus(1102,RING,80),RING,"预期1102为Ring状态");
        pjsip.Pj_Answer_Call(1102,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1102 <6502(1102)>","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void D94_Invalid() {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，按A到Custom Prompt：prompt1"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("record/prompt1");
        ys_waitingTime(3000);
        Reporter.infoExec("2000的通话状态1："+getExtensionStatus(2000,TALKING,1));
        pjsip.Pj_Send_Dtmf(2000,"A");
        boolean showKeyWord1= tcpSocket.getAsteriskInfo("record/prompt1");
        tcpSocket.closeTcpSocket();
        Reporter.infoExec("Q_IVRtoCustomCall TcpSocket return: "+showKeyWord1);
        if(showKeyWord1){
            ys_waitingTime(8000);
            Reporter.infoExec(" 2000按0到分机1000");
            Reporter.infoExec("2000的通话状态2："+getExtensionStatus(2000,TALKING,1));
            pjsip.Pj_Send_Dtmf(2000,"0");
//            getExtensionStatus(1000,RING,10);
            int st= getExtensionStatus(1000,RING,20);
            if(st != RING){
                Reporter.error("预期1000分机状态"+RING+" 实际"+st);
  //            YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
            }

            pjsip.Pj_Answer_Call(1000,false);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1000 <6502(1000)>","Answered",SPS,"",communication_inbound);
        }
        pjsip.Pj_Hangup_All();

    }

//    Dial Extension
    @Test
    public void E1_DialExtension_1103()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，直播分机号码1103"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"1","1","0","3","#");
//        getExtensionStatus(1103,RING,10);
        ysAssertWithHangup(getExtensionStatus(1103,RING,20),RING,"预期1103会响铃");
        pjsip.Pj_Answer_Call(1103,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","xlq <6502(1103)>","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void E2_DialExtension_ringgroup1()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，直播响铃组号码6200,1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        System.out.println("2000的通话状态1："+getExtensionStatus(2000,RING,1));
        pjsip.Pj_Send_Dtmf(2000,"6","2","0","0","#");
//        getExtensionStatus(1000,RING,10);
//        getExtensionStatus(1100,RING,1);
//        getExtensionStatus(1105,RING,1);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ysAssertWithHangup(getExtensionStatus(1100,RING,1),RING,"预期1100会响铃");
        ysAssertWithHangup(getExtensionStatus(1105,RING,1),RING,"预期1105会响铃");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1100 <6200(1100)>","Answered",SPS,"",communication_inbound);
    }

//    Dial Extension 不启用时无法直播分机号
    @Test
    public void E3_DialExtension_disable()  {
        Reporter.infoExec(" 1100拨打6508呼入到IVR：6508，直播分机号1103，预期：走到key 1到分机1000，即1000响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6508",DEVICE_IP_LAN);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1100,"1","1","0","3","#");
        ysAssertWithHangup(getExtensionStatus(1000,RING,10),RING,"预期1000为Ring状态");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1000 <6508(1000)>","Answered","","",communication_internal);
    }

//    Dial Outbound Routes
    @Test
    public void F1_DialOut_sip()  {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，拨打13001通过sip外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"1","3","0","0","1","#");
        ysAssertWithHangup(getExtensionStatus(3001,RING,20),RING,"预期3001为Ring状态");
        pjsip.Pj_Answer_Call(3001,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6502(13001)","Answered",SPS,SIPTrunk,communication_outRoute);
    }

    @Test
    public void F2_DialOut_sps()  {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(5000);
        System.out.println("3001的通话状态1："+getExtensionStatus(3001,TALKING,1));
        pjsip.Pj_Send_Dtmf(3001,"3","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(32000)","Answered",SIPTrunk,SPS,communication_outRoute);
    }

    @Test
    public void F3_DialOut_iax()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，拨打23001通过iax外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"2","3","0","0","1","#");
        ysAssertWithHangup(getExtensionStatus(3001,RING,20),RING,"预期3001为Ring状态");
        pjsip.Pj_Answer_Call(3001,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","6502(23001)","Answered",SPS,IAXTrunk,communication_outRoute);
    }

    @Test
    public void F4_DialOut_spx()  {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打42000通过spx外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"4","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(42000)","Answered",SIPTrunk,SPX,communication_outRoute);
    }

    @Test
    public void F5_DialOut_fxo()  {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXO_1.equals("null") || FXO_1.equals("")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打52000通过pstn外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"5","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(52000)","Answered",SIPTrunk,FXO_1,communication_outRoute);
    }

    @Test
    public void F6_DialOut_bri()  {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打62000通过bri外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"6","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(62000)","Answered",SIPTrunk,BRI_1,communication_outRoute);
    }

    @Test
    public void F7_DialOut_e1()  {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打72000通过bri外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"7","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(72000)","Answered",SIPTrunk,E1,communication_outRoute);

    }
//    GSM呼出
    @Test
    public void F8_DialOut_gsm()  {
        if (PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到IVR：6502，拨打辅助设备的GSM号码通过GSM外线呼出"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(3001,"8");
        for(int i=0; i <DEVICE_ASSIST_GSM.length();i++) {
            pjsip.Pj_Send_Dtmf(3001,DEVICE_ASSIST_GSM.substring(i, i + 1));
        }
        pjsip.Pj_Send_Dtmf(3001,"#");
        ysAssertWithHangup(getExtensionStatus(2000,RING,60),RING,"预期2000为Ring状态");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6502(8"+DEVICE_ASSIST_GSM+")","Answered",SIPTrunk,GSM,communication_outRoute);
    }

//    不启用Dial Outbound Routes时不能呼出
    @Test
    public void F9_DialOut_disable()  {
        Reporter.infoExec(" 1100拨打6503，按32000（未启用Dial Outbound Routes)，预期1100被挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6503",DEVICE_IP_LAN);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1100,"3","2","0","0","0","#");
        ysAssertWithHangup(getExtensionStatus(1100,HUNGUP,20),HUNGUP,"预期1100为Hang up状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","6503","Answered"," "," ",communication_internal);
    }


//    Dial to Check to Voicemail
    @Test
    public void G1_voicemail_enable()  {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到IVR：6502，按*02查看分机1105的语音留言"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_Send_Dtmf(2001,"*","0","2");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","1","0","5","#");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","1","0","5");
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","6502(*02)","Answered",SPS,"",communication_inbound);
    }

    @Test
    public void G2_voicemail_disable()  {
        Reporter.infoExec(" 1100拨打6506，按*02查看语音留言（未启用Dial to Check Voicemail）"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"6506",DEVICE_IP_LAN);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1100,"*","0","2");
        ysAssertWithHangup(getExtensionStatus(1100,HUNGUP,20),HUNGUP,"预期1100为Hang up状态");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","6506","Answered"," "," ",communication_internal);

    }

    //    Prompt 、Prompt Repeat Count、Response Timeout
    @Test
    public void H1_prompt() {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到IVR：6502，检查播放4次的prompt1"); //执行操作
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        boolean showKeyWord1= tcpSocket.getAsteriskInfo("record/prompt1");
        ys_waitingTime(10000);
        boolean showKeyWord2= tcpSocket.getAsteriskInfo("record/prompt1");
        ys_waitingTime(10000);
        boolean showKeyWord3= tcpSocket.getAsteriskInfo("record/prompt1");
        ys_waitingTime(10000);
        boolean showKeyWord4= tcpSocket.getAsteriskInfo("record/prompt1");
        ys_waitingTime(5000);
        System.out.println("Q_IVRtoCustomCall TcpSocket return: "+showKeyWord4);
        Reporter.infoExec("H_prompt AMI info "+showKeyWord1+" "+showKeyWord2+" "+showKeyWord3+" "+showKeyWord4);
        if(showKeyWord4 ){
            Reporter.infoExec(" 2000按0到分机1000");
            pjsip.Pj_Send_Dtmf(2000,"0");
//            getExtensionStatus(1000,RING,10);
            int st= getExtensionStatus(1000,RING,20);
            if(st != RING){
                Reporter.error("预期1000分机状态"+RING+" 实际"+st);
//                YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
            }

            pjsip.Pj_Answer_Call(1000,false);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2000 <2000>","1000 <6502(1000)>","Answered",SPS,"",communication_inbound);
        }else{
            pjsip.Pj_Hangup_All();
            YsAssert.fail("AMI未检测到关键字");
        }
    }
    @Test
    public void H2_backup() {
        Reporter.infoExec("备份IVR基础环境"); //执行操作
        backupEnviroment(this.getClass().getSimpleName());
    }

    //    Delete
    @Test
    public void I_delete() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        ivr.IVR.click();
        ivr.add.shouldBe(Condition.exist);
        ys_waitingMask();
        ys_waitingLoading(ivr.grid_Mask);
        Reporter.infoExec(" 表格删除：*.*-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"*.*",sort_ascendingOrder)));
        if(row == -1){
            ScreenShot.takeScreenshotByAll(SCREENSHOT_PATH + "表格删除：*.*-取消删除.jpg");
            Reporter.sendReport("link", "Error: " + "表格删除：*.*-取消删除", SCREENSHOT_PATH + "表格删除：*.*-取消删除.jpg");
            YsAssert.fail("没有找到 *.*的ivr线路");
        }
        int rows=Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        ys_waitingTime(3000);
        gridClick(ivr.grid,row,ivr.gridDelete);
        ivr.delete_no.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：*.*-取消删除");

        Reporter.infoExec(" 表格删除：*.*-确定删除"); //执行操作
        gridClick(ivr.grid,row,ivr.gridDelete);
        ivr.delete_yes.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：*.*-确定删除");

        Reporter.infoExec(" 删除：12345abcdefghijklmnopqrstuvwxyz-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(ivr.grid,ivr.gridcolumn_Name,"12345abcdefghijklmnopqrstuvwxyz",sort_ascendingOrder)));
        if(row4 == -1){
            YsAssert.fail("没有找到名为12345abcdefghijklmnopqrstuvwxyz的IVR线路");
        }
        gridCheck(ivr.grid,row4,ivr.gridcolumn_Check);
        ivr.delete.click();
        ivr.delete_no.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：12345abcdefghijklmnopqrstuvwxyz-取消删除");

        Reporter.infoExec(" 删除：12345abcdefghijklmnopqrstuvwxyz-确定删除"); //执行操作
        ivr.delete.click();
        ivr.delete_yes.click();
        ys_waitingLoading(ivr.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(ivr.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：12345abcdefghijklmnopqrstuvwxyz-确定删除");
        closeSetting();
    }

    @Test
    public void J_recover(){

        Reporter.infoExec(" IVR恢复BeforeTest设置"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ys_waitingTime(3000);
        if(settings.callFeatures_panel.isDisplayed()){
            settings.callFeatures_panel.click();
        }else{
            settings.callFeatures_tree.click();
        }
        ivr.add.shouldBe(Condition.exist);
        deletes(" 删除所有IVR",ivr.grid,ivr.delete,ivr.delete_yes,ivr.grid_Mask);
        Reporter.infoExec(" 添加IVR1：6500,按1到分机1000"); //执行操作
        m_callFeature.addIVR("IVR1","6500");
        Reporter.infoExec(" 编辑IVR1:按1到分机1000"); //执行操作
        ys_waitingTime(5000);
        gridClick(ivr.grid,Integer.parseInt(String.valueOf(gridLineNum(ivr.grid))),ivr.gridEdit);
        comboboxSelect(add_ivr_keyPressEvent.s_press1,add_ivr_keyPressEvent.s_extensin);
        comboboxSet(add_ivr_keyPressEvent.d_press1,extensionList,"1000");
        add_ivr_keyPressEvent.save.click();
        ys_apply();

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

    //    恢复初始化环境
    @AfterClass
    public void AfterClass0()  {
        Reporter.infoAfterClass("执行完毕：=======  IVR  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();
    }

}

