package com.yeastar.testcase.pseries;


import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INBOUND;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INTERNAL;
import static com.yeastar.untils.CDRObject.STATUS.ANSWER;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestQueueBasic extends TestCaseBaseNew {

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;

    /**
     * 前提环境
     */
    public void prerequisite() {

        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();

//            if(!isDebugInitExtensionFlag){
//                step("1000 1001拨号*76401，登录Queue1");
//                pjsip.Pj_Make_Call_No_Answer(1000,  "*76401", DEVICE_IP_LAN, false);
//                pjsip.Pj_Make_Call_No_Answer(1001,  "*76401", DEVICE_IP_LAN, false);
//            }
        }

        if (runRecoveryEnvFlag) {
            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initIVR();
            initInbound();
            initOutbound();

            ArrayList<String> queueStaticMembers = new ArrayList<>();
            ArrayList<String> queueDynamicMembers = new ArrayList<>();
            queueStaticMembers.add("1002");
            queueStaticMembers.add("1003");
            queueStaticMembers.add("1020");
            queueDynamicMembers.add("1000");
            queueDynamicMembers.add("1001");
            apiUtil.createQueue(queueName1, queueNum1, queueDynamicMembers, queueStaticMembers, null)
                    .editQueue(queueNum1,String.format("\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":10")).apply();

            step("编辑呼入路由In1呼入目的地为Queue1-6401");
            apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum1).id));
            runRecoveryEnvFlag = registerAllExtensions();

            if(!runRecoveryEnvFlag){
                step("1000 1001拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1000,  "*76401", DEVICE_IP_LAN, false);
                pjsip.Pj_Make_Call_No_Answer(1001,  "*76401", DEVICE_IP_LAN, false);
            }
        }
    }

    @Epic("P_Series")
    @Feature("Queue")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过sip外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Basic","Trunk","InboundRoute","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu01_BasicTrunkInboundRoute1(){
        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机10s内响铃").isEqualTo(RING);

        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001, QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001, Extension_1000.toString(),ANSWER.toString(), Extension_3001.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Queue")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过sps外线呼入到Queue1-6401\n" +
                 "2.坐席1000、1001、1002、1003、1020同时响铃，" +
                 "3.分机1002接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Basic","Trunk","InboundRoute","SPS","PSeries","Cloud","K2"})
    public void testQu02_BasicTrunkInboundRoute2(){
        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401", DEVICE_ASSIST_2, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机10s内响铃").isEqualTo(RING);

        step("分机1002接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1002, false);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING,10)).as("[2.通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001, QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001, Extension_1002.toString(),ANSWER.toString(), Extension_2001.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Queue")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.内部分机呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1003接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Basic","Trunk","InboundRoute","PSeries","Cloud","K2"})
    public void testQu03_BasicTrunkInboundRoute3(){
        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过分机1004呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(1004, "6401", DEVICE_IP_LAN, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机10s内响铃").isEqualTo(RING);

        step("分机1003接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING,10)).as("[2.通话校验]:1003分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1004, QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1004, Extension_1003.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Queue")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过Account外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Trunk","InboundRoute","SIP_ACCOUNT","PSeries","Cloud","K2"})
    public void testQu04_TrunkInboundRoute1(){
        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(4000, "446401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机10s内响铃").isEqualTo(RING);

        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(4000);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_4000, QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", ACCOUNTTRUNK, "", INTERNAL.toString()))
                .contains(tuple(Extension_4000, Extension_1000.toString(),ANSWER.toString(), Extension_4000.toString()+" hung up",  ACCOUNTTRUNK, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }
}
