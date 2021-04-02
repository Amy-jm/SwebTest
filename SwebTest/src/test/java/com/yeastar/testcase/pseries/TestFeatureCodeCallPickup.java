package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INBOUND;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INTERNAL;
import static com.yeastar.untils.CDRObject.STATUS.ANSWER;
import static com.yeastar.untils.CDRObject.STATUS.NO_ANSWER;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestFeatureCodeCallPickup extends TestCaseBaseNew {

    APIUtil apiUtil = new APIUtil();
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    private boolean runRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;
    /**
     * 前提环境
     */
    public void prerequisite() {

        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();
        }

        if (runRecoveryEnvFlag) {
            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initOutbound();
            initIVR();
            initInbound();

            ArrayList<String> extensionExGroup1 = new ArrayList<>();

            extensionExGroup1.add("1001");
            extensionExGroup1.add("1002");
            extensionExGroup1.add("1003");
            extensionExGroup1.add("1004");

            apiUtil.createExtensionGroup("ExtGroupPickUp1",extensionExGroup1);

            apiUtil.editFeatureCode("\"enb_pickup\":1,\"pickup\":\"*4\",\"enb_ext_pickup\":1,\"ext_pickup\":\"*04\"").apply();

            runRecoveryEnvFlag = registerAllExtensions();

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);

        }
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallPickup")
    @Story("GroupCallPickup,Pickup")
    @Description("分机1005拨打1004,1004响铃\n" +
            "分机1001拨打*4\n" +
            "分机1004停止响铃，分机1005、1001正常通话，1001挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
    public void testFCCP01_GroupCallPickupPickup1()
    {
        prerequisite();

        resetFeatureCode();

        step("分机1005拨打1004,1004响铃");
        pjsip.Pj_Make_Call_No_Answer(1005, "1004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        step("分机1001拨打*4");
        pjsip.Pj_Make_Call_No_Answer(1001,"*4");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[2.通话校验]:分机1004停止响铃").isEqualTo(HUNGUP);

        step("分机1005、1001正常通话");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[3.通话校验]:分机1001通话").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:分机1005通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("1001挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1001.toString(),Extension_1004.toString(),Extension_1001.toString()), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    private void resetFeatureCode(){
        step("恢复截答特征码环境");
        apiUtil.editFeatureCode("\"enb_pickup\":1,\"pickup\":\"*4\",\"enb_ext_pickup\":1,\"ext_pickup\":\"*04\"").apply();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallPickup")
    @Story("GroupCallPickup,Pickup")
    @Description("分机1005拨打1004,1004响铃\n" +
            "分机1000拨打*4\n" +
            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
    public void testFCCP02_GroupCallPickupPickup2()
    {
        prerequisite();

        resetFeatureCode();

        step("分机1005拨打1004,1004响铃");
        pjsip.Pj_Make_Call_No_Answer(1005, "1004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        step("分机1000拨打*4,分机1004继续响铃,应答");
        pjsip.Pj_Make_Call_No_Answer(1000,"*4");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:分机1004继续响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1005、1004正常通话");
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:分机1004通话").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:分机1005通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("1004挂断");
        pjsip.Pj_hangupCall(1004);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("通过sps外线呼入到分机1000,1000响铃\n" +
//            "分机1002拨打*4\n" +
//            "1000停止响铃，分机1002与外线正常通话，主叫挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP03_GroupCallPickupPickup3()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为1000");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//
//        step("通过sps外线呼入到分机1000,1000响铃");
//        pjsip.Pj_Make_Call_No_Answer(2001, "999999");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//
//        step("分机1002拨打*4");
//        pjsip.Pj_Make_Call_No_Answer(1002,"*4");
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1000停止响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1000停止响铃").isEqualTo(HUNGUP);
//
//        step("分机1002与外线正常通话，主叫挂断；");
//        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[3.通话校验]:分机1002通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 10)).as("[3.通话校验]:分机2001通话").isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(2001);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1002.toString(),Extension_1000.toString(),Extension_2000.toString()), SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), NO_ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1000.toString(),  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("通过sps外线呼入到分机1000,1000响铃\n" +
//            "分机1003拨打*4\n" +
//            "分机1000继续响铃，分机1000应答与外线正常通话，主叫挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP04_GroupCallPickupPickup4()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为1000");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        step("通过sps外线呼入到分机1000,1000响铃");
//        pjsip.Pj_Make_Call_No_Answer(2001, "999999");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1000继续响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:分机1000继续响铃").isEqualTo(RING);
//
//        step("分机1000应答与外线正常通话");
//        pjsip.Pj_Answer_Call(1000);
//        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[3.通话校验]:分机1000通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 10)).as("[3.通话校验]:分机2001通话").isEqualTo(TALKING);
//
//        step("主叫挂断");
//        pjsip.Pj_hangupCall(2001);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), ANSWER.toString(), Extension_2000.toString()+" hung up",  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑In1的呼入目的地为RingGroup0\n" +
//            "通过Account外线呼入到RingGroup0，分机1000、1001、1003同时响铃\n" +
//            "分机1004拨打*4截答\n" +
//            "分机1000、1001、1003停止响铃，分机1004与外线正常通话，主叫挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("ID1036717")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2","SIP_ACCOUNT"})
//    public void testFCCP05_GroupCallPickupPickup5()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为RingGroup0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"",apiUtil.getRingGroupSummary(ringGroupNum0).id)).apply();
//
//        step("通过Account外线呼入到RingGroup0，分机1000、1001、1003同时响铃");
//        pjsip.Pj_Make_Call_No_Answer(4000, "444444");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
//
//        step("分机1004拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1004,"*4");
//
//        step("分机1000、1001、1003停止响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1000停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:分机1003停止响铃").isEqualTo(HUNGUP);
//
//        step("分机1004与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[3.通话校验]:分机4000通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:分机1004通话").isEqualTo(TALKING);
//
//        step("主叫挂断");
//        pjsip.Pj_hangupCall(4000);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Account_6700.toString(), Extension_1004.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1004.toString(),RINGGROUP0_6300.toString(),Extension_4000.toString()), ACCOUNTTRUNK, "", INBOUND.toString()))
//                .contains(tuple(Account_6700.toString(), RINGGROUP0_6300.toString(), NO_ANSWER.toString(), RINGGROUP0_6300.toString()+" timed out, failover",  ACCOUNTTRUNK, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑In1的呼入目的地为Queue0\n" +
//            "通过SIP外线呼入到Queue0，分机1000、1001、1003、1004同时响铃\n" +
//            "分机1002拨打*4截答\n" +
//            "分机1000、1001、1003、1004停止响铃，分机1002与外线正常通话，被叫挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("此处有cdr方面的bug，对应id为：ID1036717")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2","SIP_REGISTER"})
//    public void testFCCP06_GroupCallPickupPickup6()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为Queue0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum0).id)).apply();
//
//        step("通过SIP外线呼入到Queue0，分机1000、1001、1003、1004同时响铃");
//        pjsip.Pj_Make_Call_No_Answer(3001, "3000");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1 )).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1002拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1002,"*4");
//
//        step("分机1000、1001、1003、1004停止响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1000停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:分机1003停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1 )).as("[2.通话校验]:分机1004停止响铃").isEqualTo(HUNGUP);
//
//        step("分机1002与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[3.通话校验]:分机3001通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 1 )).as("[3.通话校验]:分机1002通话").isEqualTo(TALKING);
//
//        step("被叫挂断");
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_3001.toString(), Extension_1002.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1002.toString(), QUEUE0_6400.toString(),Extension_1002.toString()), SIPTrunk, "", INBOUND.toString()))
//                .contains(tuple(Extension_3001.toString(), QUEUE0_6400.toString(), NO_ANSWER.toString(), QUEUE0_6400.toString()+" timed out, failover",  SIPTrunk, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑In1的呼入目的地为IVR0\n" +
//            "通过SPS外线呼入到IVR0，听到提示音ivr-greeting-dial-ext.slin时按0，分机1000响铃\n" +
//            "分机1001拨打*4截答\n" +
//            "分机1000停止响铃，分机1001与外线正常通话，挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP07_GroupCallPickupPickup7()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext.slin");
//        thread.start();
//
//        step("编辑In1的呼入目的地为IVR0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary(ivrNum0).id)).apply();
//
//        step("通过SPS外线呼入到IVR0");
//        pjsip.Pj_Make_Call_No_Answer(2001, "99999");
//
//        step("听到提示音ivr-greeting-dial-ext.slin时按0");
//        int tmp=0;
//        while (asteriskObjectList.size() < 1 && tmp++ < 400){
//            sleep(100);
//        }
//        thread.flag = false;
//        pjsip.Pj_Send_Dtmf(2001,"0");
//
//        step("分机1000响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:分机1000响铃").isEqualTo(RING);
//
//        step("分机1001拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1001,"*4");
//
//        step("分机1000停止响铃，分机1001与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10 )).as("[2.通话校验]:分机1001通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 1 )).as("[2.通话校验]:分机2001通话").isEqualTo(TALKING);
//
//        step("1001挂断");
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1001.toString(),Extension_1000.toString(),Extension_1001.toString()), SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), NO_ANSWER.toString(), "Call completed elsewhere",  SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), IVR0_6200.toString(), ANSWER.toString(), Extension_2000.toString()+" called Extension",  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @SneakyThrows
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("分机1000拨打6500进入Conference\n" +
//            "按#1001邀请分机1001加入会议室，1001响铃\n" +
//            "1003拨打*4截答\n" +
//            "会议室6500多了一个成员1003")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP08_GroupCallPickupPickup8()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("分机1000拨打6500进入Conference");
//        pjsip.Pj_Make_Call_No_Answer(1000, "6500");
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1000按#1001邀请分机1001加入会议室，1001响铃");
//        pjsip.Pj_Send_Dtmf(1000,"#","1","0","0","1","#");
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10 )).as("[1.通话校验]:分机1001响铃").isEqualTo(RING);
//
//        step("1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//        sleep(WaitUntils.TALKING_WAIT);
//        step("会议室6500多了一个成员1003");
//        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500"))).as("会议室6500多了一个成员1003").contains("1003");
//        pjsip.Pj_hangupCall(1003);
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Conference0_6500.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1001.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Conference0_6500.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1000.toString()+" invited "+Extension_1001.toString(),  "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1000.toString(), Conference0_6500.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up",  "", "", INTERNAL.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑分机组ExtGroupPickUp1-选择分机1001、1002、1004\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr\n" +
//            "编辑分机组ExtGroupPickUp1-选择分机1001、1002、1004、1003；\n" +
//            "分机1005拨打1004,1004响铃；\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP09_GroupCallPickupPickup9()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑分机组ExtGroupPickUp1-选择分机1001、1002、1004");
//        ArrayList<String> extensionExGroup1 = new ArrayList<>();
//        extensionExGroup1.add("1001");
//        extensionExGroup1.add("1002");
//        extensionExGroup1.add("1004");
//        apiUtil.editExtensionGroup("ExtGroupPickUp1",extensionExGroup1).apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004继续响铃，应答，分机1005、1004正常通话，挂断；");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:1004分机继续响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:1004分机继续响铃").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:1005分机继续响铃").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1004);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
//
//        step("编辑分机组ExtGroupPickUp1-选择分机1001、1002、1003、1004");
//        ArrayList<String> extensionExGroup2 = new ArrayList<>();
//        extensionExGroup2.add("1001");
//        extensionExGroup2.add("1002");
//        extensionExGroup2.add("1003");
//        extensionExGroup2.add("1004");
//        apiUtil.editExtensionGroup("ExtGroupPickUp1",extensionExGroup2).apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[4.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[5.通话校验]:1003分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[5.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑Feature Code-》Group Call Pickup 禁用\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr\n" +
//            "编辑Feature Code-》Group Call Pickup 启用；\n" +
//            "分机1005拨打1004,1004响铃；\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP10_GroupCallPickupPickup10() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑Feature Code-》Group Call Pickup 禁用");
//        apiUtil.editFeatureCode("\"enb_pickup\":0").apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004继续响铃，应答，分机1005、1004正常通话，挂断");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:1004分机继续响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:1004分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1004);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
//
//        step("编辑Feature Code-》Group Call Pickup 启用；");
//        apiUtil.editFeatureCode("\"enb_pickup\":1").apply();
//
//        step("分机1005拨打1004,1004响铃；");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("GroupCallPickup,Pickup")
//    @Description("编辑Feature Code-》Group Call Pickup 修改特征码#*12345\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打#*12345截答\n" +
//            "分机1004停止响铃，分机1005、1001正常通话，1001挂断；检查cdr\n" +
//            "编辑Feature Code-》Group Call Pickup 修改特征码*4；\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*4截答\n" +
//            "分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","GroupCallPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP11_GroupCallPickupPickup11() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑Feature Code-》Group Call Pickup 修改特征码#*12345");
//        apiUtil.editFeatureCode("\"pickup\":\"#*12345\"").apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004继续响铃，应答，分机1005、1004正常通话，挂断");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:1004分机继续响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:1004分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1004);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//
//        step("分机1003拨打#*12345截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"#*12345");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        step("编辑Feature Code-》Group Call Pickup 修改特征码*4；");
//        apiUtil.editFeatureCode("\"pickup\":\"*4\"").apply();
//
//        step("分机1005拨打1004,1004响铃；");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*4截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*4");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1005);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1005.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("分机1005拨打1004,1004响铃\n" +
//            "分机1001拨打*041004\n" +
//            "分机1004停止响铃，分机1005、1001正常通话，挂断；检查cdr\n" )
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P2","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP12_ExtensionPickupPickup1() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1001拨打*041004");
//        pjsip.Pj_Make_Call_No_Answer(1001,"*041004");
//
//        step("分机1004停止响铃，分机1005、1001正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[2.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[2.通话校验]:1001分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[2.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1005挂断；检查cdr");
//        pjsip.Pj_hangupCall(1005);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1001.toString(),Extension_1004.toString(),Extension_1005.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("分机1005拨打1004,1004响铃\n" +
//            "分机1000拨打*041004\n" +
//            "分机1004停止响铃，分机1005、1000正常通话，挂断；检查cdr\n" )
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P2","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP13_ExtensionPickupPickup2() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1000拨打*041004");
//        pjsip.Pj_Make_Call_No_Answer(1000,"*041004");
//
//        step("分机1004停止响铃，分机1005、1000正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[2.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[2.通话校验]:1000分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[2.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1005挂断；检查cdr");
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1000.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1000.toString(),Extension_1004.toString(),Extension_1000.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("通过sps外线呼入到分机1000,1000响铃\n" +
//            "分机1002拨打*041000\n" +
//            "1000停止响铃，分机1002与外线正常通话，主叫挂断；检查cdr\n" )
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP14_ExtensionPickupPickup3() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为1000");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        step("通过sps外线呼入到分机1000,1000响铃");
//        pjsip.Pj_Make_Call_No_Answer(2001,"991000");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//
//        step("分机1002拨打*041004");
//        pjsip.Pj_Make_Call_No_Answer(1002,"*041000");
//
//        step("1000停止响铃，分机1002与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[2.通话校验]:1002分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 10)).as("[2.通话校验]:2001分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("主叫挂断");
//        pjsip.Pj_hangupCall(2001);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1002.toString(),Extension_1000.toString(),Extension_2000.toString()), SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), NO_ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1000.toString(),  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("通过sps外线呼入到分机1000,1000响铃\n" +
//            "分机1003拨打*041000\n" +
//            "1000停止响铃，分机1003与外线正常通话，主叫挂断；检查cdr\n" )
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP15_ExtensionPickupPickup4() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为1000");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        step("通过sps外线呼入到分机1000,1000响铃");
//        pjsip.Pj_Make_Call_No_Answer(2001,"991000");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*041000");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041000");
//
//        step("1000停止响铃，分机1002与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[2.通话校验]:1003分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 10)).as("[2.通话校验]:2001分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("主叫挂断");
//        pjsip.Pj_hangupCall(2001);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1000.toString(),Extension_2000.toString()), SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), NO_ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1000.toString(),  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("编辑In1的呼入目的地为RingGroup0\n" +
//            "通过Account外线呼入到RingGroup0，分机1000、1001、1003同时响铃\n" +
//            "分机1004拨打*041003截答\n" +
//            "分机1000、1001、1003停止响铃，分机1004与外线正常通话，主叫挂断，检查cdr\n" )
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("ID1036717")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2","SIP_ACCOUNT"})
//    public void testFCCP16_ExtensionPickupPickup5() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为RingGroup0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"",apiUtil.getRingGroupSummary(ringGroupNum0).id)).apply();
//
//        step("通过Account外线呼入到RingGroup0，分机1000、1001、1003同时响铃");
//        pjsip.Pj_Make_Call_No_Answer(4000, "444444");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
//
//        step("分机1004拨打*041003截答");
//        pjsip.Pj_Make_Call_No_Answer(1004,"*041003");
//
//        step("分机1000、1001、1003停止响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1000停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:分机1003停止响铃").isEqualTo(HUNGUP);
//
//        step("分机1004与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[3.通话校验]:分机4000通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:分机1004通话").isEqualTo(TALKING);
//
//        step("主叫挂断");
//        pjsip.Pj_hangupCall(4000);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Account_6700.toString(), Extension_1004.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1004.toString(),Extension_1003.toString(),Extension_4000.toString()), ACCOUNTTRUNK, "", INBOUND.toString()))
//                .contains(tuple(Account_6700.toString(), RINGGROUP0_6300.toString(), NO_ANSWER.toString(), RINGGROUP0_6300.toString()+" timed out, failover",  ACCOUNTTRUNK, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("编辑In1的呼入目的地为Queue0\n" +
//            "通过SIP外线呼入到Queue0，分机1000、1001、1003、1004同时响铃\n" +
//            "分机1002拨打*041004截答\n" +
//            "分机1000、1001、1003、1004停止响铃，分机1002与外线正常通话，被叫挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("此处cdr有bug，对应bugid为：ID1036717")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2","SIP_REGISTER"})
//    public void testFCCP17_ExtensionPickupPickup6()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑In1的呼入目的地为Queue0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum0).id)).apply();
//
//        step("通过SIP外线呼入到Queue0，分机1000、1001、1003、1004同时响铃");
//        pjsip.Pj_Make_Call_No_Answer(3001, "3000");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1 )).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1002拨打*041004截答");
//        pjsip.Pj_Make_Call_No_Answer(1002,"*041004");
//
//        step("分机1000、1001、1003、1004停止响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1000停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:分机1003停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1 )).as("[2.通话校验]:分机1004停止响铃").isEqualTo(HUNGUP);
//
//        step("分机1002与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[3.通话校验]:分机3001通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 1 )).as("[3.通话校验]:分机1002通话").isEqualTo(TALKING);
//
//        step("被叫挂断");
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_3001.toString(), Extension_1002.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1002.toString(),Extension_1004.toString(),Extension_1001.toString()), SIPTrunk, "", INBOUND.toString()))
//                .contains(tuple(Extension_3001.toString(), QUEUE0_6400.toString(), NO_ANSWER.toString(), QUEUE0_6400.toString()+" timed out, failover",  SIPTrunk, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("编辑In1的呼入目的地为IVR0\n" +
//            "通过SPS外线呼入到IVR0，听到提示音ivr-greeting-dial-ext.slin时按0，分机1000响铃\n" +
//            "分机1001拨打*041000截答\n" +
//            "分机1000停止响铃，分机1001与外线正常通话，挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2","SPS"})
//    public void testFCCP18_ExtensionPickupPickup7()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext.slin");
//        thread.start();
//
//        step("编辑In1的呼入目的地为IVR0");
//        apiUtil.editInbound(inboundName1,String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary(ivrNum0).id)).apply();
//
//        step("通过SPS外线呼入到IVR0");
//        pjsip.Pj_Make_Call_No_Answer(2001, "99999");
//
//        step("听到提示音ivr-greeting-dial-ext.slin时按0");
//        int tmp=0;
//        while (asteriskObjectList.size() < 1 && tmp++ < 400){
//            sleep(100);
//        }
//        thread.flag = false;
//        pjsip.Pj_Send_Dtmf(2001,"0");
//
//        step("分机1000响铃");
//        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:分机1000响铃").isEqualTo(RING);
//
//        step("分机1001拨打*041000截答");
//        pjsip.Pj_Make_Call_No_Answer(1001,"*041000");
//
//        step("分机1000停止响铃，分机1001与外线正常通话");
//        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:分机1001停止响铃").isEqualTo(HUNGUP);
//        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10 )).as("[2.通话校验]:分机1001通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 1 )).as("[2.通话校验]:分机2001通话").isEqualTo(TALKING);
//
//        step("1001挂断");
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1001.toString(),Extension_1000.toString(),Extension_1001.toString()), SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), NO_ANSWER.toString(), "Call completed elsewhere",  SPS, "", INBOUND.toString()))
//                .contains(tuple(Extension_2001.toString(), IVR0_6200.toString(), ANSWER.toString(), Extension_2000.toString()+" called Extension",  SPS, "", INBOUND.toString()));
//        softAssertPlus.assertAll();
//    }
//
//    @SneakyThrows
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("分机1000拨打6500进入Conference\n" +
//            "按#1001邀请分机1001加入会议室，1001响铃\n" +
//            "1003拨打*041001截答\n" +
//            "会议室6500多了一个成员1003")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("此处cdr有问题，对应bug id为ID1036722")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP19_ExtensionPickupPickup8()
//    {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("分机1000拨打6500进入Conference");
//        pjsip.Pj_Make_Call_No_Answer(1000, "6500");
//        sleep(WaitUntils.TALKING_WAIT);
//
//        step("1000按#1001邀请分机1001加入会议室，1001响铃");
//        pjsip.Pj_Send_Dtmf(1000,"#","1","0","0","1","#");
//        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10 )).as("[1.通话校验]:分机1001响铃").isEqualTo(RING);
//
//        step("1003拨打*041001截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041001");
//        sleep(WaitUntils.TALKING_WAIT);
//        step("会议室6500多了一个成员1003");
//        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500"))).as("会议室6500多了一个成员1003").contains("1003");
//        pjsip.Pj_hangupCall(1003);
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("CDR校验");
//        auto.loginPage().loginWithAdmin();
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Conference0_6500.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1001.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Conference0_6500.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1000.toString()+" invited "+Extension_1001.toString(),  "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1000.toString(), Conference0_6500.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up",  "", "", INTERNAL.toString()));
//        softAssertPlus.assertAll();
//    }
//
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("编辑Feature Code-》Extension Pickup 禁用\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*041004截答\n" +
//            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr\n" +
//            "编辑Feature Code-》Group Call Pickup 启用；\n" +
//            "分机1005拨打1004,1004响铃；\n" +
//            "分机1003拨打*041004截答\n" +
//            "分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP20_ExtensionPickupPickup10() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑Feature Code-》Extension Pickup 禁用");
//        apiUtil.editFeatureCode("\"enb_ext_pickup\":0").apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*041004截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041004");
//
//        step("分机1004继续响铃，应答，分机1005、1004正常通话，挂断");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:1004分机继续响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:1004分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1004);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
//
//        step("编辑Feature Code-》Group Call Pickup 启用；");
//        apiUtil.editFeatureCode("\"enb_ext_pickup\":1").apply();
//
//        step("分机1005拨打1004,1004响铃；");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*041004截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041004");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallPickup")
//    @Story("ExtensionPickup,Pickup")
//    @Description("编辑Feature Code-》Extension Pickup 修改特征码#****11\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*041004截答\n" +
//            "分机1004继续响铃，应答，分机1005、1004正常通话，挂断；检查cdr\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打#****111004截答\n" +
//            "分机1004停止响铃，分机1005、1001正常通话，1001挂断；检查cdr\n" +
//            "编辑Feature Code-》Group Call Pickup 修改特征码*04；\n" +
//            "分机1005拨打1004,1004响铃\n" +
//            "分机1003拨打*041004截答\n" +
//            "分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"P3","FeatureCode","CallPickup","ExtensionPickup","Pickup","PSeries","Cloud","K2"})
//    public void testFCCP21_ExtensionPickupPickup11() {
//        prerequisite();
//
//        resetFeatureCode();
//
//        step("编辑Feature Code-》Extension Pickup 修改特征码#****11");
//        apiUtil.editFeatureCode("\"ext_pickup\":\"#****11\"").apply();
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*041004截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041004");
//
//        step("分机1004继续响铃，应答，分机1005、1004正常通话，挂断");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[2.通话校验]:1004分机继续响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004);
//        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[3.通话校验]:1004分机通话").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[3.通话校验]:1005分机通话").isEqualTo(TALKING);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1004);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
//
//        step("分机1005拨打1004,1004响铃");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//
//        step("分机1003拨打#****111004截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"#****111004");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1003.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        step("编辑Feature Code-》Group Call Pickup 修改特征码*04；");
//        apiUtil.editFeatureCode("\"ext_pickup\":\"*04\"").apply();
//
//        step("分机1005拨打1004,1004响铃；");
//        pjsip.Pj_Make_Call_No_Answer(1005,"1004");
//        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);
//
//        step("分机1003拨打*041004截答");
//        pjsip.Pj_Make_Call_No_Answer(1003,"*041004");
//
//        step("分机1004停止响铃，分机1005、1003正常通话，1003挂断；检查cdr");
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[5.通话校验]:1004分机停止响铃").isEqualTo(HUNGUP);
//        sleep(WaitUntils.TALKING_WAIT);
//        pjsip.Pj_hangupCall(1005);
//
//        assertStep("检查cdr");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(), ANSWER.toString(), String.format("%s picked up %s , %s hung up",Extension_1003.toString(),Extension_1004.toString(),Extension_1005.toString()), "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(), NO_ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1004.toString(),  "", "", INTERNAL.toString()));
//
//        softAssertPlus.assertAll();
//    }
}
