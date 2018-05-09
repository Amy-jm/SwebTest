package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import java.util.ArrayList;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/12/27.
 */

public class PickupGroup extends SwebDriver{

    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：=======  Pickup Group  ======="); //执行操作
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
//        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1103,"Yeastar202","UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
//        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1102,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
     }

    @Test
    public void A1_InitFeatureCode() throws InterruptedException {
        Reporter.infoExec(" 初始化Pickup Group的特征码为*4 、*04"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(3000);
        }
        featureCode.featureCode.click();
        featureCode.oneTouchRecord.shouldBe(Condition.exist);
        setCheckBox(featureCode.callPickup_check,true);
        setCheckBox(featureCode.extensionPickup_check,true);
        featureCode.callPickup.clear();
        featureCode.callPickup.setValue("*4");
        featureCode.extensionPickup.clear();
        featureCode.extensionPickup.setValue("*04");
        featureCode.save.click();
        ys_waitingTime(3000);
    }

    @Test
    public void A2_InitPickupGroup() throws InterruptedException {
        settings.callFeatures_tree.click();
        pickupGroup.pickupGroup.click();
        ys_waitingTime(5000);
        deletes(" 删除所有PickupGroup",pickupGroup.grid,pickupGroup.delete,pickupGroup.delete_yes,pickupGroup.grid_Mask);
        ys_apply();
    }

//    新建截答组
    @Test
    public void B1_AddPickupGroup1() throws InterruptedException {
        Reporter.infoExec(" 新建截答组PickupGroup1,成员分机1000、1105");
        m_callFeature.addPickupGroup("PickupGroup1",1000,1105);
    }

    @Test
    public void B2_AddPickupGroup2() throws InterruptedException {
        Reporter.infoExec(" 新建截答组PickupGroup2,成员分机组ExtensionGroup1、分机1103"); //执行操作
        m_callFeature.addPickupGroup("PickupGroup2","ExtensionGroup1","1103");
    }

//    如果存在FXS分机
    @Test
    public void B3_EditPickupGroup1() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXS_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 编辑截答组PickupGroup1,成员分机1000、1105、1106（fxs）"); //执行操作
        gridClick(pickupGroup.grid,gridFindRowByColumn(pickupGroup.grid,pickupGroup.gridcolumn_Name,"PickupGroup1",sort_ascendingOrder),pickupGroup.gridEdit);
        ys_waitingMask();
        ArrayList<String> memberList = new ArrayList<>();
        memberList.add("1000");
        memberList.add("1105");
        memberList.add("1106");
        listSelect(add_pickup_group.list_PickupGroup,extensionList,memberList);
        add_pickup_group.save.click();
        ys_waitingTime(5000);
    }

    @Test
    public void B4_apply() throws InterruptedException {
        ys_apply();
    }

//    通话测试
    @Test
    public void C1_sip_4() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
//        getExtensionStatus(1000,HUNGUP,10);
        YsAssert.assertEquals(getExtensionStatus(1000,HUNGUP,20),HUNGUP,"预期1000会HangUp");
        ys_waitingTime(5000);
        YsAssert.assertEquals(getExtensionStatus(1105,TALKING,1),TALKING,"预期1105会Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1105 <1105(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound,1,2);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk,"",communication_inbound,1,2);
    }

    @Test
    public void C2_sip_4_1() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1102按*4截答，预期截答失败"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(1000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1102,HUNGUP,6);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk,"",communication_inbound);
        ys_waitingTime(5000);
    }

    //    分机组成员1103
    @Test
    public void C2_sip_4_2() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1103按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","xlq <1103(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound,1,2);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk,"",communication_inbound,1,2);

    }

    @Test
    public void C3_sip_04_1() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1103按*041000截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"*041000",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","xlq <1103(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound,1,2);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk,"",communication_inbound,1,2);
    }

    @Test
    public void C3_sip_04_2() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1102按*041000截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"*041000",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1102 <1102(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound,1,2);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",SIPTrunk,"",communication_inbound,1,2);
    }

    @Test
    public void C4_sps() throws InterruptedException {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <1105(pickup 1000)>","Answered",SPS,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","No Answer",SPS,"",communication_inbound,1,2);
    }

    @Test
    public void C5_iax() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3100",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1105 <1105(pickup 1000)>","Answered",IAXTrunk,"",communication_inbound,1,2);
        m_extension.checkCDR("3001 <3001>","1000 <1000>","No Answer",IAXTrunk,"",communication_inbound,1,2);
    }

    @Test
    public void C6_spx() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2000拨打88888通过spx外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"88888",DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <1105(pickup 1000)>","Answered",SPX,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","No Answer",SPX,"",communication_inbound,1,2);
    }

    @Test
    public void C7_fxo() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
       }
       if (FXO_1.equals("null")){
            return;
       }
        Reporter.infoExec(" 2000拨打2010通过pstn外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"2010",DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <1105(pickup 1000)>","Answered",FXO_1,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","No Answer",FXO_1,"",communication_inbound,1,2);
    }

    @Test
    public void C8_bri() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(BRI_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打66666通过bri外线呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"66666",DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <1105(pickup 1000)>","Answered",BRI_1,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","No Answer",BRI_1,"",communication_inbound,1,2);
    }

    @Test
    public void C9_e1() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(E1.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打77777通过E1线路呼入到分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"77777",DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2000 <2000>","1105 <1105(pickup 1000)>","Answered",E1,"",communication_inbound,1,2);
        m_extension.checkCDR("2000 <2000>","1000 <1000>","No Answer",E1,"",communication_inbound,1,2);
    }

    @Test
    public void Ca_gsm() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(GSM.equals("null")){
            return;
        }
        Reporter.infoExec(" 2000拨打被测设备的GSM号码呼入分机1000,1105按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,false);
//        getExtensionStatus(1000,RING,30);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,40),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <"+DEVICE_ASSIST_GSM+">","1105 <1105(pickup 1000)>","Answered",GSM,"",communication_inbound,1,2);
        m_extension.checkCDR(DEVICE_ASSIST_GSM+" <"+DEVICE_ASSIST_GSM+">","1000 <1000>","No Answer",GSM,"",communication_inbound,1,2);
    }

    @Test
    public void Cb_fxs() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(FXS_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000,1106按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
//        getExtensionStatus(1000,RING,30);
        YsAssert.assertEquals(getExtensionStatus(1000,RING,40),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(2000,"5*4",DEVICE_ASSIST_2,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(15000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1106 <1106(pickup 1000)>","Answered",SIPTrunk,"",communication_inbound,1,2);
    }

//    呼入到各种目的地截答成功
    @Test
    public void D1_ivr() throws InterruptedException {
        Reporter.infoExec(" 1103拨打6500呼入到IVR，按1到分机1000,1100按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1103,"6500",DEVICE_IP_LAN,true);
        getExtensionStatus(1103,TALKING,10);
        ys_waitingTime(3000);
        pjsip.Pj_Send_Dtmf(1103,"1");
        YsAssert.assertEquals(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        ys_waitingTime(2000);
        pjsip.Pj_Make_Call_Auto_Answer(1100,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xlq <1103>","1100 <1100(pickup 6500(1100))>","Answered","","",communication_internal);
    }

    @Test
    public void D2_ringgroup() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6200呼入到RingGroup，1103按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6200",DEVICE_IP_LAN,true);
//        getExtensionStatus(1100,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","xlq <1103(pickup 6200(1103))>","Answered","","",communication_internal);
    }

    @Test
    public void D3_queue() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6700呼入到队列，1103按*4截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6700",DEVICE_IP_LAN,true);
//        getExtensionStatus(1100,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1103,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","xlq <1103(pickup 6700(1105))>","Answered","","",communication_internal,1,2);
    }

//    修改特征码
    @Test
    public void E1_editfeaturecode() throws InterruptedException {
        Reporter.infoExec(" 编辑截答的特征码为*4004,指定截答的特征码为*0401"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_tree.click();
        ys_waitingTime(2000);
        m_general.setPickup(true,"*4004",true,"*0401");
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void E2_pickup() throws InterruptedException {
        Reporter.infoExec(" 1100拨打1000,1105按*4004截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"1000",DEVICE_IP_LAN,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4004",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1105 <1105(pickup 1000)>","Answered","","",communication_internal,1,2);
    }

    @Test
    public void E3_pickup_ex() throws InterruptedException {
        Reporter.infoExec(" 1100拨打1000,1102按*04011000截答，预期截答成功"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"1000",DEVICE_IP_LAN,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"*04011000",DEVICE_IP_LAN,false);
        getExtensionStatus(1000,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1102 <1102(pickup 1000)>","Answered","","",communication_internal,1,2);
    }

//    不启用截答特征码
    @Test
    public void F1_edit_disablefeaturecode() throws InterruptedException {
        Reporter.infoExec(" 编辑特征码：不启用截答*4、指定截答*04"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_tree.click();
        ys_waitingTime(2000);
        m_general.setPickup(false,"*4",false,"*04");
        ys_apply();
        ys_waitingTime(5000);
    }

    @Test
    public void F2_pickup() throws InterruptedException {
        Reporter.infoExec(" 1100拨打1000,1105按*4截答，预期截答失败"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"1000",DEVICE_IP_LAN,false);
//        getExtensionStatus(1000,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1100,RING,20),RING,"预期1100会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1105,"*4",DEVICE_IP_LAN,false);
        getExtensionStatus(1105,HUNGUP,20);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1000 <1000>","No Answer","","",communication_internal);
    }

    @Test
    public void F3_pickup_ex() throws InterruptedException {
        Reporter.infoExec(" 1100拨打1105,1102按*041105截答，预期截答失败"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1100,"1105",DEVICE_IP_LAN,false);
//        getExtensionStatus(1105,RING,20);
        YsAssert.assertEquals(getExtensionStatus(1105,RING,20),RING,"预期1105会响铃");
        ys_waitingTime(3000);
        pjsip.Pj_Make_Call_Auto_Answer(1102,"*041105",DEVICE_IP_LAN,false);
        getExtensionStatus(1102,HUNGUP,10);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","1105 <1105>","No Answer","","",communication_internal);
    }

//    删除
    @Test
    public void G1_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        pickupGroup.pickupGroup.click();
        pickupGroup.add.shouldBe(Condition.exist);
        setPageShowNum(pickupGroup.grid, 50);
        Reporter.infoExec(" 表格删除：PickupGroup1-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(pickupGroup.grid, pickupGroup.gridcolumn_Name, "PickupGroup1", sort_ascendingOrder)));
        int rows = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        gridClick(pickupGroup.grid, row, pickupGroup.gridDelete);
        pickupGroup.delete_no.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row1:" + row1);
        YsAssert.assertEquals(rows, row1, "表格删除：PickupGroup1-取消删除");

        Reporter.infoExec(" 表格删除：PickupGroup1-确定删除"); //执行操作
        gridClick(pickupGroup.grid, row, pickupGroup.gridDelete);
        pickupGroup.delete_yes.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row2:" + row2);
        int row3 = row2 + 1;
        System.out.println("row3:" + row3);
        YsAssert.assertEquals(row3, row1, "表格删除：PickupGroup1-确定删除");

        Reporter.infoExec(" 删除：PickupGroup2-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(pickupGroup.grid, pickupGroup.gridcolumn_Name, "PickupGroup2", sort_ascendingOrder)));
        gridCheck(pickupGroup.grid, row4, pickupGroup.gridcolumn_Check);
        pickupGroup.delete.click();
        pickupGroup.delete_no.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row5:" + row5);
        YsAssert.assertEquals(row8, row5, "删除：PickupGroup2-取消删除");

        Reporter.infoExec(" 删除：PickupGroup2-确定删除"); //执行操作
        pickupGroup.delete.click();
        pickupGroup.delete_yes.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid)));
        System.out.println("row6:" + row6);
        int row7 = row6 + 1;
        System.out.println("row7:" + row7);
        YsAssert.assertEquals(row5, row7, "删除：PickupGroup2-确定删除");
        ys_apply();
    }

    @Test
    public void G_recovery_featurecode() throws InterruptedException {
        Reporter.infoExec(" 恢复截答特征码设置"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_tree.click();
        ys_waitingTime(2000);
        m_general.setPickup(true,"*4",true,"*04");
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
        Reporter.infoAfterClass("执行完毕：=======  Pickup Group  ======="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
