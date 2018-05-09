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
 * Created by AutoTest on 2017/11/21.
 */
public class RingGroup extends SwebDriver{

    @BeforeClass
    public void A_BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：=======  RingGroup  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void B_Register() {
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1102,"Yeastar202","UDP",UDP_PORT,4);
        pjsip.Pj_CreateAccount(1103,"Yeastar202","UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
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
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        deletes(" 删除所有RingGroup",ringGroup.grid,ringGroup.delete,ringGroup.delete_yes,ringGroup.grid_Mask);
    }

//    添加响铃组的数据
    @DataProvider(name="add")
    public Object[][] Numbers() throws BiffException, IOException{
        ExcelUnit e=new ExcelUnit("database", "ringgroup");
        return e.getExcelData();
    }

    @Test(dataProvider="add")
    public void A2_AddRingGroups(HashMap<String, String> data) throws InterruptedException {
        Reporter.infoExec("添加响铃组"+data.get("Name")+"："+data.get("Number"));
//        System.out.println(data.toString());
        ringGroup.add.shouldBe(Condition.exist);
        ringGroup.add.click();
        ys_waitingMask();
        add_ring_group.number.clear();
        add_ring_group.number.setValue(data.get("Number"));
        add_ring_group.name.clear();
        add_ring_group.name.setValue(data.get("Name"));
        comboboxSelect(add_ring_group.ringStrategy,data.get("RingStrategy"));
        add_ring_group.secondstoringeachmenmber.clear();
        add_ring_group.secondstoringeachmenmber.setValue(data.get("Seconds"));

        ArrayList<String> memberList = new ArrayList<>();
        String[] List1 = data.get("Members").split(",");
        for(int i=0;i< List1.length;i++){
            System.out.println(String.valueOf(List1[i]));
            memberList.add(String.valueOf(List1[i]));
        }
        listSelect(add_ring_group.list_RingGroup,extensionList,memberList);

        if(!data.get("Failover").equals("")){
            comboboxSelect(add_ring_group.failoverDestinationtype,data.get("Failover"));
        }
        if(!data.get("Destination").equals("")){
            if(data.get("Failover").equals("e") || data.get("Failover").equals("v")){
                comboboxSet(add_ring_group.failoverDestination,extensionList,data.get("Destination"));
            }else{
                comboboxSet(add_ring_group.failoverDestination,"name",data.get("Destination"));
            }
        }
        add_ring_group.save.click();
        ys_waitingTime(1000);
        String lineNum = String.valueOf(gridLineNum(ringGroup.grid)) ;
        m_callFeature.assertRingGroup(Integer.parseInt(lineNum),"",String.valueOf(data.get("Name")),"");
    }

//    添加各项，点击Cancel
    @Test
    public void A3_add_cancel() throws InterruptedException {
        Reporter.infoExec(" 新建RingGroup：xxx，点击取消"); //执行操作
        ringGroup.add.shouldBe(Condition.exist);
        String lineNum1 = String.valueOf(gridLineNum(ringGroup.grid)) ;
        ringGroup.add.click();
        ys_waitingMask();
        add_ring_group.number.clear();
        add_ring_group.number.setValue("6210");
        add_ring_group.name.clear();
        add_ring_group.name.setValue("xxx");
        comboboxSelect(add_ring_group.ringStrategy,add_ring_group.rs_sequentially);
        add_ring_group.secondstoringeachmenmber.clear();
        add_ring_group.secondstoringeachmenmber.setValue("30");
        listSelectAll(add_ring_group.list_RingGroup);
        add_ring_group.cancel.click();
        ys_waitingTime(1000);
        String lineNum2 = String.valueOf(gridLineNum(ringGroup.grid)) ;
        if (lineNum1.equals(lineNum2)){
            Reporter.pass(" 取消成功");
        }else{
            YsAssert.fail(" 取消失败");
        }
    }

//    编辑RingGroup：viszontlátasra，新增成员分机1106（FXS分机）
    @Test
    public void B_EditRingGroupFxs() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals((PC))){
            return;
        }
        if(FXS_1.equals("") || FXS_1.equals("null")){
            return;
        }
        Reporter.infoExec(" 编辑RingGroup：viszontlátasra，新增成员分机1106（FXS分机）"); //执行操作
        ringGroup.add.shouldBe(Condition.exist);
        gridClick(ringGroup.grid,gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"viszontlátasra",sort_ascendingOrder),ringGroup.gridEdit);
        ArrayList<String> memberlist =new ArrayList<>();
        memberlist.add("1106");
        memberlist.add("ExtensionGroup1");
        listSelect(add_ring_group.list_RingGroup,extensionList,memberlist);
        add_ring_group.save.click();
        ys_waitingTime(1000);
    }

//    编辑呼入路由InRoute1，到RingGroup：a
    @Test
    public void C_EditInRoute1() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由InRoute1，到RingGroup：a"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_ringGroup);
        comboboxSet(add_inbound_route.destination,"name","a");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

//    各种外线呼入到RingGroup:a通话测试
    @Test
    public void D_InboundtoRingGroup_a() throws InterruptedException {
        for (int i = 1; i < 9; i++) {
            switch (i) {
                case 1:
                    Reporter.infoExec(" 3001拨打3000通过sip外线呼入到RingGroup:a，分机1103接听"); //执行操作
                    pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1);
                    break;
                case 2:
                    Reporter.infoExec(" 2001拨打999999通过sps外线呼入到RingGroup:a，分机1103接听"); //执行操作
                    pjsip.Pj_Make_Call_No_Answer(2001, "999999", DEVICE_ASSIST_2);
                    break;
                case 3:
                    if(PRODUCT.equals(CLOUD_PBX)){
                        break;
                    }else{
                        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到RingGroup:a，分机1103接听"); //执行操作
                        pjsip.Pj_Make_Call_No_Answer(3001, "3100", DEVICE_ASSIST_1);
                        break;
                    }
                case 4:
                    if(PRODUCT.equals(CLOUD_PBX)){
                        break;
                    }else {
                        Reporter.infoExec(" 2001拨打888888通过spx外线呼入到RingGroup:a，分机1103接听"); //执行操作
                        pjsip.Pj_Make_Call_No_Answer(2001, "888888", DEVICE_ASSIST_2);
                        break;
                    }
                case 5:
                    if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
                        break;
                    }else {
                        if (!FXO_1.equals("null")) {
                            Reporter.infoExec("  2001拨打2010通过fxo外线呼入到RingGroup:a，分机1103接听"); //执行操作
                            pjsip.Pj_Make_Call_No_Answer(2001, "2010", DEVICE_ASSIST_2);
                            break;
                        } else {
                            break;
                        }
                    }
                case 6:
                    if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
                        break;
                    }else{
                        if(!BRI_1.equals("null")) {
                            Reporter.infoExec("  2001拨打66666通过bri外线呼入到RingGroup:a，分机1103接听"); //执行操作
                            pjsip.Pj_Make_Call_No_Answer(2001, "66666", DEVICE_ASSIST_2);
                            break;
                        }else {
                            break;
                        }
                    }
                case 7:
                    if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
                        break;
                    }else{
                        if(!E1.equals("null")) {
                            Reporter.infoExec("  2001拨打77777通过E1外线呼入到RingGroup:a，分机1103接听"); //执行操作
                            pjsip.Pj_Make_Call_No_Answer(2001, "77777", DEVICE_ASSIST_2);
                            break;
                        }else {
                            break;
                        }
                    }
                case 8:
                    if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
                        break;
                    }else{
                        if(!GSM.equals("null")) {
                            Reporter.infoExec("  2001拨打被测设备的gsm号码呼入到RingGroup:a，分机1103接听"); //执行操作
                            pjsip.Pj_Make_Call_No_Answer(2001, DEVICE_TEST_GSM, DEVICE_ASSIST_2);
                            break;
                        }else {
                            break;
                        }
                    }
                case 9:
                    Reporter.infoExec("  1102拨打6201号码呼入到RingGroup:a，分机1103接听"); //执行操作
                    pjsip.Pj_Make_Call_No_Answer(1102, "6201",DEVICE_IP_LAN);
                    break;
            }
            ys_waitingTime(10000);
            if (getExtensionStatus(1000, RING, 1) == RING) {
                Reporter.pass(" 1000--Ring");
            } else {
                Reporter.error(" 预期1000状态为Ring");
            }
            if (getExtensionStatus(1100, RING, 1) == RING) {
                Reporter.pass(" 1100--Ring");
            } else {
                Reporter.error(" 预期1100状态为Ring");
            }
            if (getExtensionStatus(1101, RING, 1) == RING) {
                Reporter.pass(" 1101--Ring");
            } else {
                Reporter.error(" 预期1101状态为Ring");
            }
            if (getExtensionStatus(1103, RING, 1) == RING) {
                Reporter.pass(" 1103--Ring");
            } else {
                Reporter.error(" 预期1103状态为Ring");
            }
            if (getExtensionStatus(1105, RING, 1) == RING) {
                Reporter.pass(" 1105--Ring");
            } else {
                Reporter.error(" 预期1105状态为Ring");
            }
            pjsip.Pj_Answer_Call(1103, true);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            switch (i){
                case 1:
                    m_extension.checkCDR("3001 <3001>", "xlq <6201(1103)>", "Answered", SIPTrunk, "", communication_inbound);
                    break;
                case 2:
                    m_extension.checkCDR("2001 <2001>", "xlq <6201(1103)>", "Answered", SPS, "", communication_inbound);
                    break;
                case 3:
                    if(PRODUCT.equals(CLOUD_PBX)) {
                        break;
                    }else {
                        m_extension.checkCDR("3001 <3001>", "xlq <6201(1103)>", "Answered", IAXTrunk, "", communication_inbound);
                        break;
                    }
                case 4:
                    if(PRODUCT.equals(CLOUD_PBX)) {
                        break;
                    }else {
                        m_extension.checkCDR("2001 <2001>", "xlq <6201(1103)>", "Answered", SPX, "", communication_inbound);
                        break;
                    }
                case 5:
                    if(PRODUCT.equals(CLOUD_PBX) || FXO_1.equals("null")) {
                        break;
                    }else {
                        m_extension.checkCDR("2001 <2001>", "xlq <6201(1103)>", "Answered", FXO_1, "", communication_inbound);
                        break;
                    }
                case 6:
                    if(PRODUCT.equals(CLOUD_PBX) || BRI_1.equals("null")) {
                        break;
                    }else {
                        m_extension.checkCDR("2001 <2001>", "xlq <6201(1103)>", "Answered", BRI_1, "", communication_inbound);
                        break;
                    }
                case 7:
                    if(PRODUCT.equals(CLOUD_PBX) || E1.equals("null")) {
                        break;
                    }else {
                        m_extension.checkCDR("2001 <2001>", "xlq <6201(1103)>", "Answered", E1, "", communication_inbound);
                        break;
                    }
                case 8:
                    if(PRODUCT.equals(CLOUD_PBX) || GSM.equals("null")) {
                        break;
                    }else {
                        m_extension.checkCDR(DEVICE_ASSIST_GSM + " <" + DEVICE_ASSIST_GSM + ">", "xlq <6201(1103)>", "Answered", GSM, "", communication_inbound);
                        break;
                    }
                case 9:
                    m_extension.checkCDR("1102 <1102>","xlq <6201(1103)>","Answered","","",communication_internal);
                    break;
            }
        }
    }

    //    分机xx接听后，其它分机不会再继续响铃
    @Test
    public void E1_Sequentially() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6208呼入到响铃组viszontlátasra，到分机1101响铃时，1101接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6208",DEVICE_IP_LAN);
        if(!FXS_1.equals("null")){
            if(getExtensionStatus(2000,RING,10) == RING){
                Reporter.pass(" 2000--Ring");
                ys_waitingTime(2000);
                pjsip.Pj_Answer_Call(2000,false);
                ys_waitingTime(5000);
                pjsip.Pj_Hangup_All();
                m_extension.checkCDR("1102 <1102>","1106 <6208(1106)>","Answered","","",communication_internal);
            }else{
                YsAssert.fail(" 预期2000状态为Ring");
            }
        }else {
            if (getExtensionStatus(1000, RING, 15) == RING) {
                Reporter.pass(" 1000--Ring");
            } else {
                YsAssert.fail(" 预期1000状态为Ring");
            }
            if (getExtensionStatus(1100, RING, 12) == RING) {
                Reporter.pass(" 1100--Ring");
            } else {
                YsAssert.fail(" 预期1100状态为Ring");
            }
//            if (getExtensionStatus(1000, HUNGUP, 3) == HUNGUP) {
//                Reporter.pass(" 1000--Hang up");
//            } else {
//                System.out.println("1000的分机状态：预期为HUNGUP"+getExtensionStatus(1000, HUNGUP, 1));
//                YsAssert.fail(" 预期1000状态为Hang up");
//            }
            if (getExtensionStatus(1101, RING, 12) == RING) {
                Reporter.pass(" 1101--Ring");
            } else {
                YsAssert.fail(" 预期1101状态为Ring");
            }
//            if (getExtensionStatus(1100, HUNGUP, 3) == HUNGUP) {
//                Reporter.pass(" 1100--Hang up");
//            } else {
//                YsAssert.fail(" 预期1100状态为Hang up");
//            }
            pjsip.Pj_Answer_Call(1101, false);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1102 <1102>", "1101 <6208(1101)>", "Answered", "", "", communication_internal);
        }
    }

    @Test
    public void E2_RingAll() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6201呼入到响铃组a，分机1105接听，预期其它分机停止响铃"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6201",DEVICE_IP_LAN);
        ys_waitingTime(25000);
        if (getExtensionStatus(1000, RING, 1) == RING) {
            Reporter.pass(" 1000--Ring");
        } else {
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if (getExtensionStatus(1100, RING, 1) == RING) {
            Reporter.pass(" 1100--Ring");
        } else {
            YsAssert.fail(" 预期1100状态为Ring");
        }
        if (getExtensionStatus(1101, RING, 1) == RING) {
            Reporter.pass(" 1101--Ring");
        } else {
            YsAssert.fail(" 预期1101状态为Ring");
        }
        if (getExtensionStatus(1103, RING, 1) == RING) {
            Reporter.pass(" 1103--Ring");
        } else {
            YsAssert.fail(" 预期1103状态为Ring");
        }
        if (getExtensionStatus(1105, RING, 1) == RING) {
            Reporter.pass(" 1105--Ring");
        } else {
            YsAssert.fail(" 预期1105状态为Ring");
        }
        pjsip.Pj_Answer_Call(1105,true);
//        1105应答后其它分机停止响铃
        if (getExtensionStatus(1000, HUNGUP, 10) == HUNGUP) {
            Reporter.pass(" 1000--Hang up");
        } else {
            YsAssert.fail(" 预期1000状态为Hang up");
        }
        if (getExtensionStatus(1100, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1100--Hang up");
        } else {
            YsAssert.fail(" 预期1100状态为Hang up");
        }
        if (getExtensionStatus(1101, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1101--Hang up");
        } else {
            YsAssert.fail(" 预期1101状态为Hang up");
        }
        if (getExtensionStatus(1103, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1103--Hang up");
        } else {
            YsAssert.fail(" 预期1103状态为Hang up");
        }
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1105 <6201(1105)>","Answered","","",communication_internal);
    }

    //    呼入到不同的响铃组Failover
    @Test
    public void F1_FailtoHangup() throws InterruptedException {
        Reporter.infoExec(" 2001拨打999999通过sps外线呼入到RingGroup:a，无人接听，60s后通话挂断"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2001, "999999", DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        if (getExtensionStatus(1000, RING, 1) == RING) {
            Reporter.pass(" 1000--Ring");
        } else {
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if (getExtensionStatus(1100, RING, 1) == RING) {
            Reporter.pass(" 1100--Ring");
        } else {
            YsAssert.fail(" 预期1100状态为Ring");
        }
        if (getExtensionStatus(1101, RING, 1) == RING) {
            Reporter.pass(" 1101--Ring");
        } else {
            YsAssert.fail(" 预期1101状态为Ring");
        }
        if (getExtensionStatus(1103, RING, 1) == RING) {
            Reporter.pass(" 1103--Ring");
        } else {
            YsAssert.fail(" 预期1103状态为Ring");
        }
        if (getExtensionStatus(1105, RING, 1) == RING) {
            Reporter.pass(" 1105--Ring");
        } else {
            YsAssert.fail(" 预期1105状态为Ring");
        }
        if (getExtensionStatus(1000, HUNGUP, 65) == HUNGUP) {
            Reporter.pass(" 1000--Hang up");
        } else {
            YsAssert.fail(" 预期1000状态为Hang up");
        }
        if (getExtensionStatus(1100, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1100--Hang up");
        } else {
            YsAssert.fail(" 预期1100状态为Hang up");
        }
        if (getExtensionStatus(1101, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1101--Hang up");
        } else {
            YsAssert.fail(" 预期1101状态为Hang up");
        }
        if (getExtensionStatus(1103, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1103--Hang up");
        } else {
            YsAssert.fail(" 预期1103状态为Hang up");
        }
        if (getExtensionStatus(1105, HUNGUP, 1) == HUNGUP) {
            Reporter.pass(" 1105--Hang up");
        } else {
            YsAssert.fail(" 预期1105状态为Hang up");
        }
        if(getExtensionStatus(2001,HUNGUP,5) == HUNGUP){
            Reporter.pass(" 2001--Hang up");
        }else{
            YsAssert.fail(" 预期2001状态为Hang up");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <6201(1000)>","No Answer",SPS,"",communication_inbound);
    }

    @Test
    public void F2_FailtoExtension() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6202呼入到响铃组Yeastar202，无人接听，20s后分机1000响铃接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6202",DEVICE_IP_LAN);
        if(getExtensionStatus(1103,RING,10) == RING){
            Reporter.pass(" 1103--Ring");
        }else{
            YsAssert.fail(" 预期1103状态为Ring");
        }
        ys_waitingTime(20000);
        if(getExtensionStatus(1103,HUNGUP,5) == HUNGUP){
            Reporter.pass(" 1103--Hang up");
        }else{
            YsAssert.fail(" 预期1103状态为Hang up");
        }
        if(getExtensionStatus(1000,RING,1) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期100状态为Ring");
        }
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1000 <6202(1000)>","Answered","","",communication_internal);
    }

    @Test
    public void F3_FailtoIVR() throws InterruptedException {
        Reporter.infoExec(" 1103拨打6204呼入到响铃组*.*，无人接听，20S后到IVR1按1到1000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1103,"6204",DEVICE_IP_LAN);
        if(getExtensionStatus(1000,RING,10) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if(getExtensionStatus(1105,RING,1) == RING){
            Reporter.pass(" 1105--Ring");
        }else{
            YsAssert.fail(" 预期1105状态为Ring");
        }
        ys_waitingTime(20000);
        if(getExtensionStatus(1105,HUNGUP,5) == HUNGUP){
            Reporter.pass(" 1105--Hang up");
        }else{
            YsAssert.fail(" 预期1105状态为Hang up");
        }
        if(getExtensionStatus(1000,HUNGUP,5) == HUNGUP){
            Reporter.pass(" 1000--Hang up");
        }else{
            YsAssert.fail(" 预期1000状态为Hang up");
        }
//        预期转到IVR1
        pjsip.Pj_Send_Dtmf(1103,"1");
        if(getExtensionStatus(1000,RING,10) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期1000状态为Ring");
        }
        pjsip.Pj_Answer_Call(1000,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xlq <1103>","1000 <6500(1000)>","Answered","","",communication_internal);
    }

    @Test
    public void F4_FailtoRingGroup() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6205呼入到响铃组RingGroup6205，顺序响铃10s，无人接听，转到响铃组Yeastar202，1103接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6205",DEVICE_IP_LAN,false);
        if(getExtensionStatus(1000,RING,10) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if(getExtensionStatus(1100,RING,12) == RING){
            Reporter.pass(" 1100--Ring");
        }else{
            YsAssert.fail(" 预期1100状态为Ring");
        }
        if(getExtensionStatus(1000,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1000--Hang up");
        }else{
            YsAssert.fail(" 预期1000状态为Hang up");
        }
        if(getExtensionStatus(1101,RING,12) == RING){
            Reporter.pass(" 1101--Ring");
        }else{
            YsAssert.fail(" 预期1101状态为Ring");
        }
        if(getExtensionStatus(1100,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1100--Hang up");
        }else{
            YsAssert.fail(" 预期1100状态为Hang up");
        }
        if(getExtensionStatus(1105,RING,12) == RING){
            Reporter.pass(" 1105--Ring");
        }else{
            YsAssert.fail(" 预期1105状态为Ring");
        }
        if(getExtensionStatus(1101,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1101--Hang up");
        }else{
            YsAssert.fail(" 预期1101状态为Hang up");
        }
//       响铃组Yeastar202的成员1103响铃
        if(getExtensionStatus(1103,RING,12) == RING){
            Reporter.pass(" 1103--Ring");
        }else{
            YsAssert.fail(" 预期1103状态为Ring");
        }
        pjsip.Pj_Answer_Call(1103,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","xlq <6202(1103)>","Answered","","",communication_internal);
    }

    @Test
    public void F5_FailtoQueue() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6206呼入到响铃组さようなら，顺序响铃10s，无人接听，转到队列Queue1，1105接听"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6206",DEVICE_IP_LAN);
        if(getExtensionStatus(1000,RING,10) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if(getExtensionStatus(1100,RING,12) == RING){
            Reporter.pass(" 1100--Ring");
        }else{
            YsAssert.fail(" 预期1100状态为Ring");
        }
        if(getExtensionStatus(1000,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1000--Hang up");
        }else{
            YsAssert.fail(" 预期1000状态为Hang up");
        }
        if(getExtensionStatus(1101,RING,12) == RING){
            Reporter.pass(" 1101--Ring");
        }else{
            YsAssert.fail(" 预期1101状态为Ring");
        }
        if(getExtensionStatus(1100,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1100--Hang up");
        }else{
            YsAssert.fail(" 预期1100状态为Hang up");
        }
        if(getExtensionStatus(1105,RING,12) == RING){
            Reporter.pass(" 1105--Ring");
        }else{
            YsAssert.fail(" 预期1105状态为Ring");
        }
        if(getExtensionStatus(1101,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1101--Hang up");
        }else{
            YsAssert.fail(" 预期1101状态为Hang up");
        }
        if(getExtensionStatus(1103,RING,12) == RING){
            Reporter.pass(" 1103--Ring");
        }else{
            YsAssert.fail(" 预期1103状态为Ring");
        }
        if(getExtensionStatus(1105,HUNGUP,1) == HUNGUP){
            Reporter.pass(" 1105--Hang up");
        }else{
            YsAssert.fail(" 预期1105状态为Hang up");
        }
//        队列分机1100响铃
        if(getExtensionStatus(1100,RING,12) == RING){
            Reporter.pass(" 1100--Ring");
        }else{
            YsAssert.fail(" 预期1100状态为Ring");
        }
        pjsip.Pj_Answer_Call(1100,false);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1100 <6700(1100)>","Answered","","",communication_internal);
    }

    @Test
    public void F6_FailtoConference() throws InterruptedException {
        Reporter.infoExec(" 1105拨打6207呼入到响铃组Досвидания，顺序响铃30s，无人接听，转到Conference1"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1105,"6207",DEVICE_IP_LAN);
        if(getExtensionStatus(1000,RING,12) == RING){
            Reporter.pass(" 1000--Ring");
        }else{
            YsAssert.fail(" 预期1000状态为Ring");
        }
        if(getExtensionStatus(1000,HUNGUP,32) == HUNGUP){
            Reporter.pass(" 1000--Hang up");
        }else{
            YsAssert.fail(" 预期1000状态为Hang up");
        }
//        预期转到会议室6400
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1105 <1105>","1000 <6207(6400)>","Answered","","",communication_internal);
    }

    @Test
    public void F7_FailtoVoicemail() throws InterruptedException {
        Reporter.infoExec(" 1102拨打6203呼入到响铃组，同时响铃10s，无人接听，预期到1105的voicemail"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1102,"6203",DEVICE_IP_LAN);
        ys_waitingTime(40000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1102 <1102>","1105 <6203(1105)>","Voicemail","","",communication_internal);
    }

    @Test
    public void F8_check_Voicemail() {
        Reporter.infoExec(" 分机1105登录，查看存在1102留下的语音留言"); //执行操作
        logout();
        if (PRODUCT.equals(CLOUD_PBX)) {
            login("autotest@yeastar.com", "Yeastar202");
        } else {
            login("1105", "Yeastar202");
        }
        me.taskBar_Main.click();
        me.mesettingShortcut.click();
        me.me_Voicemail.click();
        ys_waitingLoading(me_voicemail.grid_Mask);
        if (Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))) != 0) {
            YsAssert.assertEquals((gridContent(me_voicemail.grid, Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid))), me_voicemail.gridColumn_Callerid)),
                    "1102(1102)", "语音留言检查:预期第"+Integer.parseInt(String.valueOf(gridLineNum(me_voicemail.grid)))+"行的CallerID为1102(1102)");
        } else {
            YsAssert.fail("语音留言检查:预期第1行的CallerID为1102(1102)");
        }
        logout();
        ys_waitingTime(5000);
    }

    @Test
    public void G_delete() throws InterruptedException {
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
            mySettings.close.click();
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：Yeastar202-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"Yeastar202",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        gridClick(ringGroup.grid,row,ringGroup.gridDelete);
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Yeastar202-取消删除");

        Reporter.infoExec(" 表格删除：Yeastar202-确定删除"); //执行操作
        gridClick(ringGroup.grid,row,ringGroup.gridDelete);
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：RingGroup6201-确定删除");

        Reporter.infoExec(" 删除：さようなら-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid,ringGroup.gridcolumn_Name,"さようなら",sort_ascendingOrder)));
        gridCheck(ringGroup.grid,row4,ringGroup.gridcolumn_Check);
        ringGroup.delete.click();
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：さようなら-取消删除");

        Reporter.infoExec(" 删除：さようなら-确定删除"); //执行操作
        ringGroup.delete.click();
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：さようなら-确定删除");
        ys_apply();

    }

//    恢复初始化环境
    @Test
    public void H1_recovery() throws InterruptedException {
        Reporter.infoExec(" 恢复初始化环境"); //执行操作
        ringGroup.ringGroup.click();
        deletes(" 删除所有RingGroup",ringGroup.grid,ringGroup.delete,ringGroup.delete_yes,ringGroup.grid_Mask);
        Reporter.infoExec(" 添加RingGroup1：6200，选择分机1000,1100,1105，其它默认"); //执行操作
        m_callFeature.addRingGroup("RingGroup1","6200",add_ring_group.rs_ringall,1000,1100,1105);
    }

    @Test
    public void H2_recovery() throws InterruptedException {
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
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：=====  RingGroup ====="); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }

}
