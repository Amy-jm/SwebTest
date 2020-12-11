package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INBOUND;
import static com.yeastar.untils.CDRObject.STATUS.ANSWER;
import static com.yeastar.untils.CDRObject.STATUS.NO_ANSWER;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestQueuePreferences extends TestCaseBaseNew {

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = true;
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

            String queueInfo = execAsterisk("queue show queue-"+queueNum1);

            if (!queueInfo.contains("1000")){
                step("1000拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1000,  "*76401");
            }

            if (!queueInfo.contains("1001")){
                step("1001拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1001,  "*76401");
            }
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
                    .editQueue(queueNum1, "\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":10").apply();

            step("编辑呼入路由In1呼入目的地为Queue1-6401");
            apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum1).id));
            runRecoveryEnvFlag = registerAllExtensions();

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);

            if(!runRecoveryEnvFlag){
                String queueInfo = execAsterisk("queue show queue-"+queueNum1);
                if (!queueInfo.contains("1000")){
                    step("1000拨号*76401，登录Queue1");
                    pjsip.Pj_Make_Call_No_Answer(1000,  "*76401", DEVICE_IP_LAN, false);
                }
                if (!queueInfo.contains("1001")){
                    step("1001拨号*76401，登录Queue1");
                    pjsip.Pj_Make_Call_No_Answer(1001,  "*76401", DEVICE_IP_LAN, false);
                }
            }
        }
    }

    private void resetQueue1(){
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        ArrayList<String> staticAgentList = new ArrayList<>();
        ArrayList<String> dynamicAgentList = new ArrayList<>();

        staticAgentList.add("1002");
        staticAgentList.add("1003");
        dynamicAgentList.add("1000");
        dynamicAgentList.add("1001");

        List<ExtensionObject> extensionObjects = apiUtil.getExtensionSummary();

        for (ExtensionObject extensionObject: extensionObjects) {
            if(!dynamicAgentList.isEmpty()){
                for (String ext : dynamicAgentList){
                    if (ext.equals(extensionObject.number)){
                        JSONObject a = new JSONObject();
                        a.put("text",extensionObject.callerIdName);
                        a.put("text2",extensionObject.number);
                        a.put("value",String.valueOf(extensionObject.id));
                        a.put("type","extension");
                        jsonArray1.put(a);
                    }
                }
            }
            if(!staticAgentList.isEmpty()) {
                for (String ext : staticAgentList) {
                    if (ext.equals(extensionObject.number)) {
                        JSONObject a = new JSONObject();
                        a.put("text", extensionObject.callerIdName);
                        a.put("text2", extensionObject.number);
                        a.put("value", String.valueOf(extensionObject.id));
                        a.put("type", "extension");
                        jsonArray2.put(a);
                    }
                }
            }
        }
        apiUtil.editQueue(queueNum1,String.format("\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":0,\"ring_strategy\": \"ring_all\",\"max_wait_time\":100,\"fail_dest\":\"end_call\"," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\",\"enb_ring_in_use\":0,\"max_calls\":0," +
                "\"agent_prompt\":\"\"," +
                "\"enb_leave_empty\":1,\"enb_join_empty\":0," +
                "\"enb_announce_pos\":0,\"enb_announce_hold_time\":0," +
                "\"join_prompt\":\"\",\"enb_announce_agent_id\":0," +
                "\"enb_announce_pos\":0,\"enb_announce_hold_time\":0,\"caller_announce_freq\":30," +
                "\"sys_announce_prompt\":\"\",\"sys_announce_freq\":60," +
                "\"satisfa_survey_prompt\":\"\"," +
                "\"dynamic_agent_list\":%s,\"static_agent_list\":%s",apiUtil.getExtensionSummary("1004").id,jsonArray1,jsonArray2)).apply();
        String queueInfo = execAsterisk("queue show queue-"+queueNum1);
        if (!queueInfo.contains("1000")){
            step("1000拨号*76401，登录Queue1");
            pjsip.Pj_Make_Call_No_Answer(1000,  "*76401");
        }
        if (!queueInfo.contains("1001")){
            step("1001拨号*76401，登录Queue1");
            pjsip.Pj_Make_Call_No_Answer(1001,  "*76401");
        }
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("AgentTimeout,RetryInterval")
    @Description("1.通过sip外线呼入到Queue1-6401\n" +
            "2.所有坐席同时响铃，无人接听，10s后查看asterisk后台打印Nobody picked up in 10000 ms时，所有坐席停止响铃；\n" +
            "3.等待10s后，所有坐席同时响铃，10s后查看asterisk后台打印Nobody picked up in 10000 ms时，所有坐席停止响铃；\n" +
            "4.等待10s后，所有坐席同时响铃，坐席1000接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","AgentTimeout","RetryInterval","SIP_REGISTER","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP01_AgentTimeoutRetryInterval1(){

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"Nobody picked up in 10000 ms");
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("所有坐席同时响铃，无人接听");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        int tmp = 0;
        while (asteriskObjectList.size() < 1 && tmp++ < 600){
            sleep(50);
        }
        step("等待111asterisk出现Nobody picked up in 10000 ms");
        softAssertPlus.assertThat(asteriskObjectList.size()).as("2.查看asterisk后台打印Nobody picked up in 10000 ms").isGreaterThan(0);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        step("等待10s后，所有坐席同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 17)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        tmp = 0;
        asteriskObjectList.clear();
        step("等待asterisk出现Nobody picked up in 10000 ms");
        while (asteriskObjectList.size() < 1 && tmp++ < 600){
            sleep(50);
        }

        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("4.查看asterisk后台打印Nobody picked up in 10000 ms");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[4.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        step("等待10s后，所有坐席再次同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 17)).as("[5.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[5.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[5.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[5.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[6.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("1000挂断");
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("AgentTimeout,RetryInterval")
    @Description("1.编辑Queue1的Agent Timeout：15，Retry Interval ：20\n" +
            "2.通过sps外线呼入到Queue1-6401\n" +
            "3.所有坐席同时响铃，无人接听，15秒后查看asterisk后台打印Nobody picked up in 15000 ms时，所有坐席停止响铃；\n" +
            "4.等待20秒后，所有坐席同时响铃，坐席1001接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","AgentTimeout","RetryInterval","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP02_AgentTimeoutRetryInterval2() {

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"agent_timeout\":15,\"retry_time\":20").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"Nobody picked up in 15000 ms");
        thread.start();

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("所有坐席同时响铃，无人接听");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("等待asterisk出现Nobody picked up in 15000 ms");
        int tmp = 0;
        while (asteriskObjectList.size() < 1 && tmp++ < 160){
            sleep(100);
        }

        softAssertPlus.assertThat(asteriskObjectList.size()).as("2.查看asterisk后台打印Nobody picked up in 15000 ms").isGreaterThan(0);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        step("等待15s后，所有坐席同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 22)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        tmp = 0;
        asteriskObjectList.clear();
        step("等待asterisk出现Nobody picked up in 15000 ms");
        while (asteriskObjectList.size() < 1 && tmp++ < 160){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("4.查看asterisk后台打印Nobody picked up in 15000 ms");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[4.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        step("等待15s后，所有坐席再次同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 22)).as("[5.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[5.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[5.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[5.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1001接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1001, false);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING,10)).as("[6.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("1000挂断");
        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("AgentTimeout,RetryInterval")
    @Description("1.编辑Queue1的RingStrategy选择Linear，Agent Timeout：5，Retry Interval ：5\n" +
            "2.通过sps外线呼入到Queue1-6401\n" +
            "3.坐席1000响铃，无人接听，5秒后查看asterisk后台打印Nobody picked up in 5000 ms时，1000已停止响铃；\n" +
            "4.等待5秒后，分机1001响铃，无人接听，5秒后查看asterisk后台打印Nobody picked up in 5000 ms时，1001已停止响铃；\n" +
            "5.等待5秒后，分机1002响铃，接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","AgentTimeout","RetryInterval","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP03_AgentTimeoutRetryInterval3() {

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"ring_strategy\": \"linear\",\"agent_timeout\":5,\"retry_time\":5").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"Nobody picked up in 5000 ms");
        thread.start();

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000响铃，无人接听");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);

        step("等待asterisk出现Nobody picked up in 5000 ms");
        int tmp = 0;
        while (asteriskObjectList.size() < 1 && tmp++ < 60){
            sleep(100);
        }

        softAssertPlus.assertThat(asteriskObjectList.size()).as("2.查看asterisk后台打印Nobody picked up in 5000 ms").isGreaterThan(0);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);

        step("等待5秒后，分机1001响铃，无人接听");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 8 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);

        tmp = 0;
        asteriskObjectList.clear();
        step("等待asterisk出现Nobody picked up in 5000 ms");
        while (asteriskObjectList.size() < 1 && tmp++ < 60){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("4.查看asterisk后台打印Nobody picked up in 5000 ms");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);

        step("等待5秒后，分机1002响铃，接听，通话，挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 11 )).as("[5.通话校验]:1002分机响铃").isEqualTo(RING);

        step("分机1002接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1002, false);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING,10)).as("[6.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("1000挂断");
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1002.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("Wrap-upTime")
    @Description("1.编辑Queue1的Wrap-up Time：20；\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.所有坐席同时响铃，分机1000接听，通话5秒，挂断；\n" +
            "4.等待2秒，再次通过sps外线呼入到Queue1，分机1000不会响铃，1001、1002、1003同时响铃；\n" +
            "5.10s后所有坐席1000、1001、1001、1003 同时响铃；挂断；\n" +
            "6.cdr正确" +
            "7.编辑Queue1的Wrap-up Time：0；通过sps外线呼入到Queue1\n" +
            "8.所有坐席同时响铃，分机1000接听，通话5秒，挂断；等待2秒，再次通过sps外线呼入到Queue1，所有坐席1000、1001、1002、1003再次同时响铃；挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Wrap-upTime","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP04_WrapupTime1()
    {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"wrap_up_time\":20").apply();

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("所有坐席同时响铃，分机1000接听，通话5秒，挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[1.通话校验]:1000分机接听").isEqualTo(TALKING);
        sleep(5000);
        pjsip.Pj_hangupCall(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("[1.通话校验]:1000分机挂断").isEqualTo(HUNGUP);

        step("等待2秒，再次通过sps外线呼入到Queue1，分机1000不会响铃，1001、1002、1003同时响铃；");
        sleep(2000);
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[2.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[2.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);
        
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 11)).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(2001);
        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

        step("编辑Queue1的Wrap-up Time：0；通过sps外线呼入到Queue1");
        apiUtil.editQueue(queueNum1, "\"wrap_up_time\":0").apply();

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000);
        sleep(5000);
        pjsip.Pj_hangupCall(1000);

        step("等待2秒，再次通过sps外线呼入到Queue1，分机1000、1001、1002、1003同时响铃；");
        sleep(2000);
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[4.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[4.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[4.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[4.通话校验]:1003分机响铃").isEqualTo(RING);

        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 11)).as("[4.通话校验]:1000分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机响铃").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(2001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("AgentAnnouncement")
    @Description("1.编辑Queue1的Agent Announcement选择prompt5\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.所有坐席同时响铃，分机1001接听时，asterisk后台打印播放语音文件prompt5，通话，挂断；cdr正确\n" +
            "4.编辑Queue1的Agent Announcement选择[None];通过sps外线呼入到Queue1\n" +
            "5.所有坐席同时响铃，分机1001接听时，asterisk后台不会打印播放语音文件prompt5，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","AgentAnnouncement","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP05_AgentAnnouncement1()
    {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的Agent Announcement选择prompt5");
        apiUtil.editQueue(queueNum1, "\"agent_prompt\":\"prompt5.wav\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"prompt5.slin");
        thread.start();

        step("2.通过sps外线呼入到Queue1" );
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step( "3.所有坐席同时响铃，分机1001接听时，asterisk后台打印播放语音文件prompt5，通话，挂断；cdr正确" );
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1001);
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 160){
            sleep(100);
        }
        softAssertPlus.assertThat(asteriskObjectList.size() >=1).as("asterisk后台打印播放语音文件prompt5.slin").isTrue();
        pjsip.Pj_hangupCall(1001);

        step("4.编辑Queue1的Agent Announcement选择[None];通过sps外线呼入到Queue1" );
        apiUtil.editQueue(queueNum1, "\"agent_prompt\":\"\"").apply();
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("5.所有坐席同时响铃，分机1001接听时，asterisk后台不会打印播放语音文件prompt5，通话，挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1001);
        tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 160){
            sleep(100);
        }
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(asteriskObjectList.size() >=1).as("asterisk后台打印播放语音文件prompt5.slin").isTrue();


        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("RingInUse")
    @Description("1.编辑Queue1启用RingInUse\n" +
            "2.进入redis-cli执行“Hget QueueCache 6401” 查看\"enb_ring_in_use\\\":1\n" +
            "3.编辑Queue1禁用RingInUse\n" +
            "4.进入redis-cli执行“Hget QueueCache 6401” 查看\"enb_ring_in_use\\\":0\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingInUse","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP06_RingInUse()
    {

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1启用RingInUse\n") ;
        apiUtil.editQueue(queueNum1, "\"enb_ring_in_use\":1").apply();

        step("2.进入redis-cli执行“Hget QueueCache 6401” 查看\"enb_ring_in_use\\\":1\n");
        softAssertPlus.assertThat(RedisUtils.hget(RedisUtils.REDIS_KEY.QueueCache.toString(),"6401", RedisUtils.QUEUE_KEY.RingInUse.toString()))
                .as("1.redis执行Hget QueueCache 6401查询enb_ring_in_use:1 ").isEqualTo("1");

        step("3.编辑Queue1禁用RingInUse\n");
        apiUtil.editQueue(queueNum1, "\"enb_ring_in_use\":0").apply();

        step("4.进入redis-cli执行“Hget QueueCache 6401” 查看\"enb_ring_in_use\\\":0\n");
        softAssertPlus.assertThat(RedisUtils.hget(RedisUtils.REDIS_KEY.QueueCache.toString(),"6401", RedisUtils.QUEUE_KEY.RingInUse.toString()))
                .as("1.redis执行Hget QueueCache 6401查询enb_ring_in_use:1 ").isEqualTo("0");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("MaximumCallersInQueue")
    @Description("1.编辑Queue1的MaximumCallersInQueue为1\n" +
            "2.通过sps外线呼入到Queue1，坐席1000,1001；,1002响铃\n" +
            "3.通过sip外线呼入到Queue1,等待通话数超过1，到Failover Destination-分机1004,1004响铃，接听，挂断所有通话；cdr正确" +
            "4.编辑Queue1的MaximumCallersInQueue为5；通过sps外线呼入到Queue1，坐席1001接听并保持通话；通过sip外线呼入到Queue1\n" +
            "5.剩下坐席1000、1002、1003同时响铃，1003接听，通话；挂断所有通话；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","MaximumCallersInQueue","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP07_MaximumCallersInQueue()
    {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的MaximumCallersInQueue为1");
        apiUtil.editQueue(queueNum1, "\"max_calls\":1").apply();

        step("2.通过sps外线呼入到Queue1，坐席1001接听并保持通话；\n" );
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 1  )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1  )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1  )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("3.通过sip外线呼入到Queue1,等待通话数超过1，到Failover Destination-分机1004,1004响铃，接听，挂断所有通话；cdr正确");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10 )).as("[2.通话校验]:1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(4);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));

        pjsip.Pj_Hangup_All();

        step("4.编辑Queue1的MaximumCallersInQueue为5；通过sps外线呼入到Queue1，坐席1001接听并保持通话；\n" );
        apiUtil.editQueue(queueNum1, "\"max_calls\":5").apply();
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 1  )).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1  )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1  )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        step("5.通过sip外线呼入到Queue1,剩下坐席1000、1002、1003同时响铃，1003接听，通话；挂断所有通话；cdr正确\n");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);
        pjsip.Pj_hangupCall(1001);

        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(4);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1003.toString()+" hung up",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("LeaveEmpty,Join Empty")
    @Description("1.编辑Queue1的Leave Empty、JoinEmpty 都不勾选；静态坐席、动态坐席都不选\n" +
            "2.通过sps外线呼入到Queue1；\n" +
            "3.通话转到Failover Destination，分机1004响铃，接听，通话，挂断；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","LeaveEmpty","JoinEmpty","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP08_LeaveEmptyJoinEmpty1() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的Leave Empty、JoinEmpty 都不勾选；静态坐席、动态坐席都不选");
        apiUtil.editQueue(queueNum1, "\"enb_leave_empty\":0,\"enb_join_empty\":0,\"dynamic_agent_list\":[],\"static_agent_list\":[]").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("LeaveEmpty,Join Empty")
    @Description("1.编辑Queue1的Leave Empty、JoinEmpty 都勾选；静态坐席、动态坐席都不选\n" +
            "2.通过sps外线呼入到Queue1；\n" +
            "3.通话转到Failover Destination，分机1004响铃，接听，通话，挂断；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","LeaveEmpty","JoinEmpty","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP09_LeaveEmptyJoinEmpty2() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的Leave Empty、JoinEmpty 都勾选；静态坐席、动态坐席都不选");
        apiUtil.editQueue(queueNum1, "\"enb_leave_empty\":1,\"enb_join_empty\":1,\"dynamic_agent_list\":[],\"static_agent_list\":[]").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("LeaveEmpty,Join Empty")
    @Description("1.编辑Queue1 不勾选Leave Empty、 勾选JoinEmpty；；静态坐席、动态坐席都不选\n" +
            "2.通过sps外线呼入到Queue1；\n" +
            "3.主叫保持通话状态，分机1004不会响铃，主叫挂断，cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","LeaveEmpty","JoinEmpty","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP10_LeaveEmptyJoinEmpty3() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1 不勾选Leave Empty、 勾选JoinEmpty；；静态坐席、动态坐席都不选");
        apiUtil.editQueue(queueNum1, "\"enb_leave_empty\":0,\"enb_join_empty\":1,\"dynamic_agent_list\":[],\"static_agent_list\":[]").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        sleep(5000);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[1.通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING  , 10)).as("[1.通话校验]:2001分机保持响铃").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(2001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, failover", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("LeaveEmpty,Join Empty")
    @Description("1.编辑Queue1的勾选Leave Empty、 不勾选JoinEmpty；静态坐席、动态坐席都不选\n" +
            "2.通过sps外线呼入到Queue1；\n" +
            "3.通话转到Failover Destination，分机1004响铃，接听，通话，挂断；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","LeaveEmpty","JoinEmpty","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP11_LeaveEmptyJoinEmpty4() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的勾选Leave Empty、 不勾选JoinEmpty；静态坐席、动态坐席都不选");
        apiUtil.editQueue(queueNum1, "\"enb_leave_empty\":1,\"enb_join_empty\":0,\"dynamic_agent_list\":[],\"static_agent_list\":[]").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        //todo  CDR结果需要确认
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("LeaveEmpty,Join Empty")
    @Description("1.编辑Queue1的勾选Leave Empty、 不勾选JoinEmpty；静态坐席添加1002、1003；动态坐席添加1000、1001；\n" +
            "2.通过sps外线呼入到Queue1；\n" +
            "3.通话转到Failover Destination，分机1004响铃，接听，通话，挂断；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","LeaveEmpty","JoinEmpty","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP12_LeaveEmptyJoinEmpty5() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的勾选Leave Empty、 不勾选JoinEmpty,静态坐席添加1002、1003；动态坐席添加1000、1001(坐席在环境恢复时已完成)");
        apiUtil.editQueue(queueNum1, "\"enb_leave_empty\":1,\"enb_join_empty\":0").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1003.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("JoinAnnouncement")
    @Description("1.编辑Queue1的JoinAnnouncement选择prompt4\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.asterisk后台查看提示音文件prompt4播放完后，所有坐席开始同时响铃，分机1002接听，通话，挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","JoinAnnouncement","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP13_JoinAnnouncement1() {
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"prompt4.slin");
        thread.start();

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的JoinAnnouncement选择prompt4");
        apiUtil.editQueue(queueNum1, "\"join_prompt\":\"prompt4.wav\"").apply();

        step("2.通过sps外线呼入到Queue1\n");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");


        step("3.asterisk后台查看提示音文件prompt4播放完后，所有坐席开始同时响铃，分机1002接听，通话，挂断；cdr正确");
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 400){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >=1).as("asterisk后台打印播放语音文件prompt4.slin").isTrue();
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 4)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1002.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("AgentIDAnnouncement")
    @Description("1.编辑Queue1勾选Agent ID Announcement\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.所有坐席同时响铃，分机1001接听后，asterisk后台打印播放语音文件queue-agent-will-serve.gsm，通话5s,主叫挂断；cdr正确" +
            "4.编辑Queue1不勾选AgentIDAnnouncement，通过sps外线呼入到Queue1\n" +
            "5.所有坐席同时响铃，分机1000接听后，asterisk后台不会打印播放语音文件queue-agent-will-serve.gsm，通话5s,主叫挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","AgentIDAnnouncement","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP14_AgentIDAnnouncement1() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"queue-agent-will-serve.gsm");
        thread.start();

        step("1.编辑Queue1勾选Agent ID Announcement");
        apiUtil.editQueue(queueNum1, "\"enb_announce_agent_id\":1").apply();

        step("2.通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("3.所有坐席同时响铃，分机1001接听");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1001);
        step("asterisk后台打印播放语音文件queue-agent-will-serve.gsm，通话5s,主叫挂断；cdr正确");
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 100){
            sleep(100);
        }
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[2.通话校验]:asterisk后台打印播放语音文件queue-agent-will-serve.gsm").isGreaterThan(0);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));


        step("编辑Queue1不勾选AgentIDAnnouncement，通过sps外线呼入到Queue1");
        apiUtil.editQueue(queueNum1, "\"enb_announce_agent_id\":0").apply();

        asteriskObjectList.clear();
        step("2.通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("3.所有坐席同时响铃，分机1001接听");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[2.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[2.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000);
        step("所有坐席同时响铃，分机1000接听后，asterisk后台不会打印播放语音文件queue-agent-will-serve.gsm，通话5s,主叫挂断；cdr正确");
        tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 50){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[2.通话校验]:asterisk后台打印播放语音文件queue-agent-will-serve.gsm").isEqualTo(0);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("CallerPositionAnnouncements，AnnouncePosition，AnnounceHoldTime")
    @Description("1.编辑Queue1勾选Announce Position、Announce Hold Time，Frequency 设置为45\n" +
            "2.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确announce-position = yes announce-holdtime = yes announce-frequency = 45\n" +
            "3.编辑Queue1不勾选Announce Position、Announce Hold Time，Frequency 设置为60\n" +
            "4.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确announce-position = no announce-holdtime = no announce-frequency = 60\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","CallerPositionAnnouncements","AnnouncePosition","AnnounceHoldTime","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP15_CAA1() throws IOException, JSchException {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1勾选Announce Position、Announce Hold Time，Frequency 设置为45" );
        apiUtil.editQueue(queueNum1, "\"enb_announce_pos\":1,\"enb_announce_hold_time\":1,\"caller_announce_freq\":45").apply();

        step("2.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确announce-position = yes announce-holdtime = yes announce-frequency = 45" );
        String queues = SSHLinuxUntils.exePjsip("cat /etc/asterisk/queues.conf");
        System.out.println("!!!!!!!!!!!!!!!!!!  "+queues);
        String checkData = "";
        for (int i=0; i<queues.split("queue-").length; i++){
            checkData = queues.split("queue-")[i];
            if (checkData.contains("6401]")){
                break;
            }
        }
        softAssertPlus.assertThat(checkData).as("1.查看配置文件/etc/asterisk/queues.conf").contains("announce-position = yes");
        softAssertPlus.assertThat(checkData).as("1.查看配置文件/etc/asterisk/queues.conf").contains("announce-holdtime = yes");
        softAssertPlus.assertThat(checkData).as("1.查看配置文件/etc/asterisk/queues.conf").contains("announce-frequency = 45");

        step("3.编辑Queue1不勾选Announce Position、Announce Hold Time，Frequency 设置为60" );
        apiUtil.editQueue(queueNum1, "\"enb_announce_pos\":0,\"enb_announce_hold_time\":0,\"caller_announce_freq\":60").apply();

        step("4.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确announce-position = no announce-holdtime = no announce-frequency = 60");
        queues = SSHLinuxUntils.exePjsip("cat /etc/asterisk/queues.conf");
        checkData = "";
        for (int i=0; i<queues.split("queue-").length; i++){
            checkData = queues.split("queue-")[i];
            log.debug(checkData);
            if (checkData.contains("6401]")){
                break;
            }
        }
        softAssertPlus.assertThat(checkData).as("2.查看配置文件/etc/asterisk/queues.conf").contains("announce-position = no");
        softAssertPlus.assertThat(checkData).as("2.查看配置文件/etc/asterisk/queues.conf").contains("announce-holdtime = no");
        softAssertPlus.assertThat(checkData).as("2.查看配置文件/etc/asterisk/queues.conf").contains("announce-frequency = 60");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("QueueAnnouncements")
    @Description("1.编辑Queue1的Queue Announcements 选择提示音prompt1,Frequency设置为120\n" +
            "2.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确 periodic-announce = record/prompt1 periodic-announce-frequency = 120\n" +
            "3.编辑Queue1的Queue Announcements 选择提示音[None],Frequency设置为60\n" +
            "4.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确 不存在如下字段 periodic-announce = record/prompt1 periodic-announce-frequency = 60\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","QueueAnnouncements","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP16_QueueAnnouncements1() throws IOException, JSchException {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的Queue Announcements 选择提示音prompt1,Frequency设置为120");
        apiUtil.editQueue(queueNum1, "\"sys_announce_prompt\":\"prompt1.wav\",\"sys_announce_freq\":120").apply();

        step("2.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确 periodic-announce = record/prompt1 periodic-announce-frequency = 120");
        String queues = SSHLinuxUntils.exePjsip("cat /etc/asterisk/queues.conf");
        System.out.println("!!!!!!!!!!!!!!!!!!  "+queues);
        String checkData = "";
        for (int i=0; i<queues.split("queue-").length; i++){
            checkData = queues.split("queue-")[i];
            if (checkData.contains("6401]")){
                break;
            }
        }
        softAssertPlus.assertThat(checkData).as("1.查看配置文件/etc/asterisk/queues.conf").contains("periodic-announce = record/prompt1");
        softAssertPlus.assertThat(checkData).as("1.查看配置文件/etc/asterisk/queues.conf").contains("periodic-announce-frequency = 120");

        step("3.编辑Queue1的Queue Announcements 选择提示音[None],Frequency设置为60" );
        apiUtil.editQueue(queueNum1, "\"sys_announce_prompt\":\"\",\"sys_announce_freq\":120").apply();

        step("4.查看Queue1-6401配置文件/etc/asterisk/queues.conf生成正确 不存在如下字段 periodic-announce = record/prompt1 periodic-announce-frequency = 60");
        queues = SSHLinuxUntils.exePjsip("cat /etc/asterisk/queues.conf");
        checkData = "";
        for (int i=0; i<queues.split("queue-").length; i++){
            checkData = queues.split("queue-")[i];
            log.debug(checkData);
            if (checkData.contains("6401]")){
                break;
            }
        }
        softAssertPlus.assertThat(checkData).as("2.查看配置文件/etc/asterisk/queues.conf").doesNotContain("periodic-announce = record/prompt1");
        softAssertPlus.assertThat(checkData).as("2.查看配置文件/etc/asterisk/queues.conf").doesNotContain("periodic-announce-frequency = 60");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("SatisfactionSurveyPrompt")
    @Description("1.编辑Queue1的SatisfactionSurveyPrompt选择[Default]\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.坐席1001接听，通话，1001挂断，主叫还保持为Talking状态，asteirsk查看后台打印播放语言文件queue-satisfaction-survey.gsm\n" +
            "4.按1，asterisk查看后台打印播放语言文件queue-thanks-calling.slin\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","SatisfactionSurveyPrompt","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP17_SatisfactionSurveyPrompt1() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"queue-satisfaction-survey.gsm");
        thread.start();

        step("1.编辑Queue1的SatisfactionSurveyPrompt选择[Default]");
        apiUtil.editQueue(queueNum1, "\"satisfa_survey_prompt\":\"default\"").apply();

        step("通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001,"996401");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);

        step("坐席1001接听，通话，1001挂断，主叫还保持为Talking状态，asteirsk查看后台打印播放语言文件queue-satisfaction-survey.gsm");
        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 1)).as("[1.通话校验]:2001还保持通话").isEqualTo(TALKING);

        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 40){
            sleep(100);
        }
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[2.asterisk校验]asteirsk打印播放语言文件queue-satisfaction-survey.gsm").isGreaterThan(0);

        step("按1，asterisk查看后台打印播放语言文件queue-thanks-calling.slin");
        thread.asteriskKey = "queue-thanks-calling.slin";
        asteriskObjectList.clear();

        pjsip.Pj_Send_Dtmf(2001,"1");
        tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 40){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[3.asterisk校验]asteirsk打印播放语言文件queue-thanks-calling.slin").isGreaterThan(0);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("SatisfactionSurveyPrompt")
    @Description("1.编辑Queue1的SatisfactionSurveyPrompt选择[Default]\n" +
            "2.1.通过sps外线呼入到Queue1\n" +
            "3.坐席1001接听，通话，主叫挂断，通话被直接挂断，asterisk后台查看不会播放语音文件queue-satisfaction-survey.gsm\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","SatisfactionSurveyPrompt","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP18_SatisfactionSurveyPrompt2() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的SatisfactionSurveyPrompt选择[Default]");
        apiUtil.editQueue(queueNum1, "\"satisfa_survey_prompt\":\"default\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"queue-satisfaction-survey.gsm");
        thread.start();

        pjsip.Pj_Make_Call_No_Answer(2001,"996401");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("[1.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2001, HUNGUP, 1)).as("[1.通话校验]:2001分机挂断").isEqualTo(HUNGUP);

        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[2.asterisk校验]asteirsk不会打印播放语言文件queue-satisfaction-survey.gsm").isEqualTo(0);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("SatisfactionSurveyPrompt")
    @Description("1.编辑Queue1的SatisfactionSurveyPrompt选择prompt5\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.坐席1002接听，通话，1002挂断，主叫还保持为Talking状态，asterisk查看后台打印语音文件prompt5\n" +
            "4.按3，asterisk查看后台打印播放语言文件queue-thanks-calling.slin\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","SatisfactionSurveyPrompt","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP19_SatisfactionSurveyPrompt3() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的SatisfactionSurveyPrompt选择[Default]");
        apiUtil.editQueue(queueNum1, "\"satisfa_survey_prompt\":\"prompt5.wav\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"prompt5.slin");
        thread.start();

        step("通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001,"996401");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);

        step("坐席1002接听，通话，1002挂断，主叫还保持为Talking状态，asterisk查看后台打印语音文件prompt5");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(2001, TALKING, 10)).as("[1.通话校验]:2001保持通话").isEqualTo(TALKING);

        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 40){
            sleep(100);
        }
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[2.asterisk校验]asteirsk打印播放语言文件prompt5").isGreaterThan(0);

        thread.asteriskKey = "queue-thanks-calling.slin";
        asteriskObjectList.clear();

        step("按3，asterisk查看后台打印播放语言文件queue-thanks-calling.slin");
        pjsip.Pj_Send_Dtmf(2001,"3");
        tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 40){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[3.asterisk校验]asteirsk打印播放语言文件queue-thanks-calling.slin").isGreaterThan(0);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueuePreferences")
    @Story("SatisfactionSurveyPrompt")
    @Description("1.编辑Queue1的SatisfactionSurveyPrompt选择[None]\n" +
            "2.通过sps外线呼入到Queue1\n" +
            "3.坐席1003接听，通话，1003挂断，通话立马被挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","SatisfactionSurveyPrompt","SPS","PSeries","Cloud","K2","Queue","QueuePreferences"})
    public void testQP20_SatisfactionSurveyPrompt2() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("1.编辑Queue1的SatisfactionSurveyPrompt选择[None]");
        apiUtil.editQueue(queueNum1, "\"satisfa_survey_prompt\":\"\"").apply();

        step("通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001,"996401");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);

        step("坐席1003接听，通话，1003挂断，通话立马被挂断；cdr正确");
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);

        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 10)).as("[1.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2001, HUNGUP, 1)).as("[1.通话校验]:2001分机挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1003.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    }
