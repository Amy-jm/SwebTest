package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INBOUND;
import static com.yeastar.untils.CDRObject.STATUS.ANSWER;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestQueuePreferences extends TestCaseBaseNew {

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

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();

            String queueInfo = execAsterisk("queue show queue-"+queueNum1);

            if (!queueInfo.contains("1000")){
                step("1003拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1003,  "*76401", DEVICE_IP_LAN, false);
            }

            if (!queueInfo.contains("1001")){
                step("1004拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1004,  "*76401", DEVICE_IP_LAN, false);
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
                    .editQueue(queueNum1,String.format("\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":10")).apply();

            step("编辑呼入路由In1呼入目的地为Queue1-6401");
            apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum1).id));
            runRecoveryEnvFlag = registerAllExtensions();

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);

            if(!runRecoveryEnvFlag){
                String queueInfo = execAsterisk("queue show queue-"+queueNum1);
                if (!queueInfo.contains("1000")){
                    step("1003拨号*76401，登录Queue1");
                    pjsip.Pj_Make_Call_No_Answer(1003,  "*76401", DEVICE_IP_LAN, false);
                }
                if (!queueInfo.contains("1001")){
                    step("1004拨号*76401，登录Queue1");
                    pjsip.Pj_Make_Call_No_Answer(1004,  "*76401", DEVICE_IP_LAN, false);
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
            if(dynamicAgentList != null && !dynamicAgentList.isEmpty()){
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
            if(staticAgentList != null && !staticAgentList.isEmpty()) {
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
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"," +
                "\"enb_announce_pos\":0,\"enb_announce_hold_time\":0," +
                "\"dynamic_agent_list\":%s,\"static_agent_list\":%s",apiUtil.getExtensionSummary("1004").id,jsonArray1,jsonArray2)).apply();
    }

    @Epic("P_Series")
    @Feature("Queue")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过sip外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","AgentTimeout","RetryInterval","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQP01_BasicTrunkInboundRoute1(){
        step("[恢复环境]");
        resetQueue1();

        prerequisite();

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
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }
        asteriskObjectList.clear();
        softAssertPlus.assertThat(asteriskObjectList.size()).as("").isEqualTo(1);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);


        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_3001.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

}
