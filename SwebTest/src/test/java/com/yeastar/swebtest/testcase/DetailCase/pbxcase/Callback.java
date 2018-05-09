package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.read.biff.BiffException;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.file.ExcelUnit;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/11/30.
 */
public class Callback extends SwebDriver {
//  创建Callback的函数
    public void addcallback(String name,String throughtrunk, String strip,String prepend,String Des1,String Des2){
        Reporter.infoExec(" 创建"+name+"，Through："+throughtrunk+"外线，Destination："+Des2); //执行操作
        callback.add.shouldBe(Condition.exist);
        callback.add.click();
        ys_waitingMask();
        ys_waitingTime(2000);
        add_callback.name.setValue(name);
        add_callback.strip.setValue(strip);
        add_callback.prepend.setValue(prepend);
        comboboxSet(add_callback.callbackThrough,trunkList,throughtrunk);
        comboboxSelect(add_callback.destination,Des1);

        if (Des1.equals("e") || Des1.equals("v")) {
            comboboxSet(add_callback.destinationDest, extensionList,Des2);
        } else {
            comboboxSet(add_callback.destinationDest,"name",Des2);
        }
        add_callback.save.click();
        ys_waitingTime(3000);
    }

    public void addcallback(String name, String throughtrunk,String Des1,String Des2){
        addcallback(name,throughtrunk,"","",Des1,Des2);
    }

    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：=======  Callback  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void Register() {
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1103,"Yeastar202","UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(3002,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2002,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3002,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002,DEVICE_ASSIST_2);
     }

    @Test
     public void A1_Init() throws InterruptedException {
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.callFeatures_panel.click();
         if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && LOGIN_ADMIN.equals("yes")){
             ys_waitingMask();
         }else{
             ys_waitingTime(5000);
         }
         callFeatures.more.click();
         callback.callback.click();
         callback.add.shouldBe(Condition.exist);
         deletes(" 删除所有Callback",callback.grid,callback.delete,callback.delete_yes,callback.grid_Mask);
     }

//     新建callback，Through 不同外线
    @Test
    public void A2_add_trunks() throws InterruptedException {
        addcallback("Callback_SPS",SPS,s_extensin,"1000");
        addcallback("Callback_SIP",SIPTrunk,"7","3001",s_extensin,"1100");
        if(!PRODUCT.equals(CLOUD_PBX)){
            addcallback("Callback_IAX",IAXTrunk,"7","3001",s_extensin,"1000");
            addcallback("Callback_SPX",SPX,s_extensin,"1000");
            if(!PRODUCT.equals(PC)){
                if(!FXO_1.equals("null")){
                    addcallback("Callback_FXO",FXO_1,s_extensin,"1000");
                }
                if(!BRI_1.equals("null")){
                    addcallback("Callback_BRI",BRI_1,s_extensin,"1000");
                }
                if(!E1.equals("null")){
                    addcallback("Callback_E1",E1,s_extensin,"1000");
                }
                if(!GSM.equals("null")){
                    addcallback("Callback_gsm",GSM,"7",DEVICE_ASSIST_GSM,s_extensin,"1000");
                }
                if(!FXS_1.equals("null")){
                    addcallback("Callback_fxs",SPS,s_extensin,"1106");
                }
            }
        }
        ys_waitingTime(2000);
    }

//    创建Callback，到不同目的地
    @DataProvider(name="add")
    public Object[][] Numbers() throws BiffException, IOException{
        ExcelUnit e=new ExcelUnit("database", "callback");
        return e.getExcelData();
    }
    @Test(dataProvider="add")
    public void A3_add_destination(HashMap<String, String> data) throws InterruptedException {
        Reporter.infoExec("添加Callback："+data.get("Name")+"，Destination："+data.get("Des2"));
        callback.add.shouldBe(Condition.exist);
        callback.add.click();
        ys_waitingMask();
        add_callback.name.setValue(data.get("Name"));

//        选择Callback from where the call come in
        listSetValue(add_callback.callbackThrough,"0");

        add_callback.delayBeforeCallback.setValue(data.get("Delay"));
        add_callback.strip.setValue(data.get("Strip"));
        add_callback.prepend.setValue(data.get("Prepend"));
        comboboxSelect(add_callback.destination,data.get("Des1"));
        if (!data.get("Des2").equals("")) {
            if (data.get("Des1").equals("e") || data.get("Des1").equals("v")) {
                comboboxSet(add_callback.destinationDest, extensionList, data.get("Des2"));
            } else {
                comboboxSet(add_callback.destinationDest, nameList, data.get("Des2"));
            }
        }
        add_callback.save.click();
        ys_waitingLoading(callback.grid_Mask);
    }

//     创建不同的呼入路由到各个Callback
    @Test
    public void B1_add_InRoute_trunk() throws InterruptedException {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add("all");
        Reporter.infoExec(" 添加呼入路由：Callback_SPS，DID：3000，Destination：Callback_SPS"); //执行操作
        m_callcontrol.addInboundRoutes("Callback_SPS","3000","",s_callback,"Callback_SPS",memberList);
        Reporter.infoExec(" 添加呼入路由：Callback_SIP，DID：6603302，Destination：Callback_SIP"); //执行操作
        m_callcontrol.addInboundRoutes("Callback_SIP","6603302","",s_callback,"Callback_SIP",memberList);
        if(!PRODUCT.equals(CLOUD_PBX)){
            Reporter.infoExec(" 添加呼入路由：Callback_IAX，DID：6603303，Destination：Callback_IAX"); //执行操作
            m_callcontrol.addInboundRoutes("Callback_IAX","6603303","",s_callback,"Callback_IAX",memberList);
            Reporter.infoExec(" 添加呼入路由：Callback_SPX，DID：3100，Destination：Callback_SPX"); //执行操作
            m_callcontrol.addInboundRoutes("Callback_SPX","3100","",s_callback,"Callback_SPX",memberList);
            if(!PRODUCT.equals(PC)){
                if(!FXO_1.equals("null")) {
                    Reporter.infoExec(" 添加呼入路由：Callback_FXO，DID：6603305，Destination：Callback_FXO"); //执行操作
                    m_callcontrol.addInboundRoutes("Callback_FXO", "6603305", "", s_callback, "Callback_FXO", memberList);
                }
                if(!BRI_1.equals("null")){
                    Reporter.infoExec(" 添加呼入路由：Callback_BRI，DID：6603306，Destination：Callback_BRI"); //执行操作
                    m_callcontrol.addInboundRoutes("Callback_BRI","6603306","",s_callback,"Callback_BRI",memberList);
                }
                if(!E1.equals("null")){
                    Reporter.infoExec(" 添加呼入路由：Callback_E1，DID：6603307，Destination：Callback_E1"); //执行操作
                    m_callcontrol.addInboundRoutes("Callback_E1","6603307","",s_callback,"Callback_E1",memberList);
                }
                if(!GSM.equals("null")){
                    Reporter.infoExec(" 添加呼入路由：Callback_gsm，DID：6603308，Destination：Callback_gsm"); //执行操作
                    m_callcontrol.addInboundRoutes("Callback_gsm","6603308","",s_callback,"Callback_gsm",memberList);
                }
                if(!FXS_1.equals("null")){
                    Reporter.infoExec(" 添加呼入路由：Callback_fxs，DID：6603301，Destination：Callback_fxs"); //执行操作
                    m_callcontrol.addInboundRoutes("Callback_fxs","6603301","",s_callback,"Callback_fxs",memberList);
                }
            }
        }
    }

    @Test(dataProvider="add")
    public void B2_add_InRoute_destination(HashMap<String, String> data) throws InterruptedException {
        Reporter.infoExec("添加呼入路由："+data.get("Name")+"DID："+data.get("DID1")+"，Destination："+data.get("Name"));
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add("all");
        m_callcontrol.addInboundRoutes(data.get("Name"),data.get("DID1"),"",s_callback,data.get("Name"),memberList);
    }
    @Test
    public void B3_bakckUp(){
        backupEnviroment(this.getClass().getName());
    }
    @Test
    public void B4_apply(){
        ys_apply();
    }
//    通话测试：通过指定外线回拨
    @Test
    public void C1_through_sps() {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入，预期通过sps回拨，2000分机响铃接听后，分机1000响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        YsAssert.assertEquals(getExtensionStatus(3001,HUNGUP,40),HUNGUP,"预期3001会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback3001","1000 <1000>","Answered",SPS,"",communication_callback);
    }

    @Test
    public void C2_through_sip()  {
        Reporter.infoExec(" 2000拨打996603302通过sps外线呼入，预期通过sip回拨，3001分机响铃接听后，分机1100响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"996603302",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(3001,RING,20),RING,"预期3001会Ring");
        pjsip.Pj_Answer_Call(3001,false);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback3001","1100 <1100>","Answered",SIPTrunk,"",communication_callback);
    }

    @Test
    public void C3_through_iax() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打996603303通过sps外线呼入，预期通过iax回拨，3001分机响铃接听后，分机1000接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"996603303",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(3001,RING,20),RING,"预期3001会Ring");
        pjsip.Pj_Answer_Call(3001,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback3001","1000 <1000>","Answered",IAXTrunk,"",communication_callback);
    }

    @Test
    public void C4_through_spx() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入，预期通过spx回拨，2000分机响铃接听后，分机1000响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3100",DEVICE_ASSIST_1,false);
        YsAssert.assertEquals(getExtensionStatus(3001,HUNGUP,40),HUNGUP,"预期3001会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback3001","1000 <1000>","Answered",SPX,"",communication_callback);
    }

    @Test
    public void C5_through_fxo() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if (!FXO_1.equals("null") || !FXO_1.equals("")){
            Reporter.infoExec(" 2001拨打996603305通过sps外线呼入，预期通过fxo回拨，2001分机响铃接听，分机1000接听"); //执行操作
            pjsip.Pj_Make_Call_No_Answer(2001,"996603305",DEVICE_ASSIST_2,false);
            YsAssert.assertEquals(getExtensionStatus(2001,HUNGUP,40),HUNGUP,"预期2001会被挂断");
            YsAssert.assertEquals(getExtensionStatus(2001,RING,20),RING,"预期2001会Ring");
            pjsip.Pj_Answer_Call(2001,false);
            YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
            pjsip.Pj_Answer_Call(1000,true);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("callback2001","1000 <1000>","Answered",FXO_1,"",communication_callback);
        }
    }

    @Test
    public void C6_through_bri() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if (!BRI_1.equals("null") || !BRI_1.equals("")) {
            Reporter.infoExec(" 2000拨打996603306通过sps外线呼入，预期通过bri回拨，2000分机响铃接听，分机1000接听");
            pjsip.Pj_Make_Call_No_Answer(2000,"996603306",DEVICE_ASSIST_2,false);
            YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
            YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
            pjsip.Pj_Answer_Call(2000,false);
            YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
            pjsip.Pj_Answer_Call(1000,true);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("callback2000","1000 <1000>","Answered",BRI_1,"",communication_callback);

        }
    }

    @Test
    public void C7_through_e1(){
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }

        if (!E1.equals("null")) {
            Reporter.infoExec(" 2000拨打996603307通过sps外线呼入，预期通过E1回拨，2000分机响铃接听，分机1000接听");
            pjsip.Pj_Make_Call_No_Answer(2000,"996603307",DEVICE_ASSIST_2,false);
            YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
            YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
            pjsip.Pj_Answer_Call(2000,false);
            YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
            pjsip.Pj_Answer_Call(1000,true);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("callback2000","1000 <1000>","Answered",E1,"",communication_callback);
        }

    }

    @Test
    public void C8_through_gsm() {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if (!GSM.equals("null")) {
            Reporter.infoExec(" 2000拨打996603308通过sps外线呼入，预期通过GSM回拨，2000分机响铃接听，分机1000接听");
            pjsip.Pj_Make_Call_No_Answer(2000,"996603308",DEVICE_ASSIST_2,false);
            YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
            YsAssert.assertEquals(getExtensionStatus(2000,RING,30),RING,"预期2000会Ring");
            pjsip.Pj_Answer_Call(2000,false);
            YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
            pjsip.Pj_Answer_Call(1000,true);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR(DEVICE_ASSIST_GSM+" <callback"+DEVICE_ASSIST_GSM+">","1000 <1000>","Answered",GSM,"",communication_callback);
        }
    }

//    通话测试：呼入各种目的地
    @Test
    public void D1_des_hangup()  {
        Reporter.infoExec(" 2000拨打995503301通过sps外线呼入，预期通过sps回拨，回拨Destination：Hangup"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503301",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
    }

    @Test
    public void D2_des_extension()  {
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入，预期通过sps回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPS,"",communication_callback);
    }
    @Test
    public void D3_des_ivr() {
        Reporter.infoExec(" 2000拨打995503304通过sps外线呼入，预期通过sps回拨，回拨Destination：IVR1，按1到分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503304",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(2000,"1");
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback12300","1000 <6500(1000)>","Answered",SPS,"",communication_callback);
    }

    @Test
    public void D4_des_ringgroup() throws InterruptedException {
        Reporter.infoExec(" 2000拨打995503305通过sps外线呼入，预期通过sps外线回拨，回拨Destination：RingGroup1，成员都响铃，1105接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503305",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,1),RING,"预期1100会Ring");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,1),RING,"预期1105会Ring");
        pjsip.Pj_Answer_Call(1105,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        ys_waitingTime(1000);
        m_extension.checkCDR("callback000","1105 <6200(1105)>","Answered",SPS,"",communication_callback);
    }

    @Test
    public void D5_des_queue() throws InterruptedException {
        Reporter.infoExec(" 2000拨打995503306通过sps外线呼入，预期通过sps外线回拨，回拨Destination：Queue1，坐席都响铃，1100接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503306",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会Ring");
        YsAssert.assertEquals(getExtensionStatus(1100,RING,1),RING,"预期1100会Ring");
        YsAssert.assertEquals(getExtensionStatus(1105,RING,1),RING,"预期1105会Ring");
        pjsip.Pj_Answer_Call(1100,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1100 <6700(1100)>","Answered",SPS,"",communication_callback);
    }

    @Test
    public void D6_des_conference() throws InterruptedException {
        Reporter.infoExec(" 2000拨打995503307通过sps外线呼入，预期通过sps外线回拨，回拨Destination：Conference1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503307",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,1),HUNGUP,"预期2000会延迟响铃");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback1234567890","6400","Answered",SPS,"",communication_callback);
    }

    @Test
    public void D7_des_disa() throws InterruptedException {
        Reporter.infoExec(" 2000拨打995503308通过sps外线呼入，预期通过sps外线回拨，回拨Destination：DISA1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"995503308",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        ys_waitingTime(8000);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,1),HUNGUP,"预期2000会延迟响铃");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        Reporter.infoExec(" 转入DISA后，2000发送DTMF：13001通过sip外线呼出");
        pjsip.Pj_Send_Dtmf(2000,"1","3","0","0","1","#");
        YsAssert.assertEquals(getExtensionStatus(3001,RING,20),RING,"预期3001会响铃");
        pjsip.Pj_Answer_Call(3001,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000 <callback2000>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
    }

    @Test
    public void D8_des_fxs(){
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXS_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2002拨打996603301通过sps外线呼入，预期通过sps外线回拨，回拨Destination：分机1106（fxs）--通过pstn线会呼入到2000分机"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2002,"996603301",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2002,HUNGUP,20),HUNGUP,"预期2002会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2002,RING,20),RING,"预期2002会Ring");
        pjsip.Pj_Answer_Call(2002,false);
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2002","1106 <1106>","Answered",SPS,"",communication_callback);
    }

//    通话测试：Callback from where the call come in---sip/iax因为前面设置了DID呼入到sps/spx外线回拨，所以这边未测试
    @Test
    public void E1_sps() throws InterruptedException {
        Reporter.infoExec(" 2000拨打999999通过sps外线呼入，预期通过sps外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPS,"",communication_callback);
    }

    @Test
    public void E2_spx() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打88888通过spx外线呼入，预期通过spx外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"88888",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,20),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPX,"",communication_callback);
    }

    @Test
    public void E3_fxo() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXO_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打2010通过pstn外线呼入，预期通过pstn外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"2010",DEVICE_ASSIST_2,false);
        ys_waitingTime(5000);
        pjsip.Pj_hangupCall(2000,2000);
        YsAssert.assertEquals(getExtensionStatus(2000,RING,40),RING,"预期2000会Ring");
        YsAssert.assertEquals(getExtensionStatus(1000,RING,40),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",FXO_1,"",communication_callback);
    }

    @Test
    public void E4_bri() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打66666通过bri外线呼入，预期通过bri外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"66666",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",BRI_1,"",communication_callback);
    }

    @Test
    public void E5_e1() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打77777通过E1外线呼入，预期通过E1外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"777777",DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,20),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",E1,"",communication_callback);
    }

    @Test
    public void E6_gsm() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打被测设备的GSM号码呼入，预期通过GSM外线回拨，回拨Destination：分机1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
        YsAssert.assertEquals(getExtensionStatus(2000,HUNGUP,40),HUNGUP,"预期2000会被挂断");
        YsAssert.assertEquals(getExtensionStatus(2000,RING,30),RING,"预期2000会Ring");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(2000);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <callback"+DEVICE_ASSIST_GSM+">","1000 <1000>","Answered",GSM,"",communication_callback);
    }

    //删除测试
    @Test
    public void F_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        Reporter.infoExec(" 表格删除：Callback7_Conference-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(callback.grid,callback.gridcolumn_Name,"Callback7_Conference",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        gridClick(callback.grid,row,callback.gridDelete);
        callback.delete_no.click();
        ys_waitingLoading(callback.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Callback7_Conference-取消删除");

        Reporter.infoExec(" 表格删除：Callback7_Conference-确定删除"); //执行操作
        gridClick(callback.grid,row,callback.gridDelete);
        callback.delete_yes.click();
        ys_waitingLoading(callback.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Callback7_Conference-确定删除");

        Reporter.infoExec(" 删除：Callback5_RingGroup-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(callback.grid,callback.gridcolumn_Name,"Callback5_RingGroup",sort_ascendingOrder)));
        gridCheck(callback.grid,row4,callback.gridcolumn_Check);
        callback.delete.click();
        callback.delete_no.click();
        ys_waitingLoading(callback.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Callback5_RingGroup-取消删除");

        Reporter.infoExec(" 删除：Callback5_RingGroup-确定删除"); //执行操作
        callback.delete.click();
        callback.delete_yes.click();
        ys_waitingLoading(callback.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Callback5_RingGroup-确定删除");

    }

    //    恢复测试环境
    @Test
    public void G1_recovery_callback() throws InterruptedException {
        callback.add.shouldBe(Condition.exist);
        deletes(" 删除所有Callback",callback.grid,callback.delete,callback.delete_yes,callback.grid_Mask);
        Reporter.infoExec(" 创建Callback1，Destination：分机1000，其它默认");
        m_callFeature.addCallBack("Callback1",s_extensin,"1000");
    }

    @Test
    public void G2_recovery_inbound() throws InterruptedException {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1","","",add_inbound_route.s_extensin,"1000",arraytrunk1);
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
        Reporter.infoAfterClass("执行完毕：=======  Callback  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
