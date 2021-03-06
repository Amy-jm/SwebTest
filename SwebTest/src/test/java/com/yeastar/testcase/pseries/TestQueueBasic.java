package com.yeastar.testcase.pseries;


import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.javatuples.Quintet;
import org.testng.Assert;
import org.testng.annotations.Test;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.*;
import static com.yeastar.untils.CDRObject.STATUS.*;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestQueueBasic extends TestCaseBaseNew {

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;
    private String PROMPT_1 = "prompt1.slin";
    private String PROMPT_2 = "prompt2.slin";
    private String PROMPT_3 = "prompt3.slin";
    private String PROMPT_4 = "prompt4.slin";
    private String PROMPT_5 = "prompt5.slin";

    /**
     * 前提环境
     */
    public void prerequisite() {

        if (isDebugInitExtensionFlag) {
            log.debug("***************** init extension ************");

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();

            String queueInfo = execAsterisk("queue show queue-"+queueNum1);

            if (!queueInfo.contains("1000")){
                step("1000拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1000,  "*76401", DEVICE_IP_LAN, false);
            }

            if (!queueInfo.contains("1001")){
                step("1001拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1001,  "*76401", DEVICE_IP_LAN, false);
            }

            try {
                if(!apiUtil.getQueueSummary(queueNum1).number.equals("6401")){
                    initQueue6401();
                }
            }catch (java.lang.NullPointerException e){
                log.error("ignore queue 6401 error");
            }

            try {
                if(!apiUtil.getQueueSummary("6402").number.equals("6402")){
                    initQueue6402();
                }
            }catch (java.lang.NullPointerException e){
                log.error("ignore queue 6402 error");
            }

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

            initQueue6401();
            initQueue6402();

            step("编辑呼入路由In1呼入目的地为Queue1-6401");
            apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum1).id));
            runRecoveryEnvFlag = registerAllExtensions();

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);

            String queueInfo = execAsterisk("queue show queue-"+queueNum1);
            if (!queueInfo.contains("1000")){
                step("1003拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1000,  "*76401", DEVICE_IP_LAN, false);
            }
            if (!queueInfo.contains("1001")){
                step("1004拨号*76401，登录Queue1");
                pjsip.Pj_Make_Call_No_Answer(1001,  "*76401", DEVICE_IP_LAN, false);
            }

        }
    }

    public void initQueue6401(){
        ArrayList<String> queueStaticMembers = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers.add("1002");
        queueStaticMembers.add("1003");
        queueDynamicMembers.add("1000");
        queueDynamicMembers.add("1001");

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            queueStaticMembers.add("1020");
        }

        step("创建队列6401");
        apiUtil.createQueue(queueName1, queueNum1, queueDynamicMembers, queueStaticMembers, null)
                .editQueue(queueNum1,String.format("\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":10"))
                .apply();
    }

    public void initQueue6402(){
        ArrayList<String> queueStaticMembers2 = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers2.add("1002");
        queueStaticMembers2.add("1003");
        queueDynamicMembers.add("1000");
        queueDynamicMembers.add("1001");
        step("创建队列6401");
        apiUtil.createQueue("Queue3","6402",queueStaticMembers2,queueDynamicMembers,null)
                .apply();
    }

    private void resetQueue1(){
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        ArrayList<String> staticAgentList = new ArrayList<>();
        ArrayList<String> dynamicAgentList = new ArrayList<>();

        staticAgentList.add("1002");
        staticAgentList.add("1003");

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            staticAgentList.add("1020");
        }

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
        try{
        apiUtil.editQueue(queueNum1,String.format("\"agent_timeout\":10,\"retry_time\":10,\"wrap_up_time\":10,\"ring_strategy\": \"ring_all\",\"max_wait_time\":1800,\"fail_dest\":\"end_call\",\"dynamic_agent_list\":%s,\"static_agent_list\":%s",jsonArray1,jsonArray2)).apply();
    }catch (java.lang.NullPointerException e){

        }
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过sip外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Queue","QueueBasic","Basic","Trunk","InboundRoute","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu01_BasicTrunkInboundRoute1(){

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);
//        pjsip.Pj_Hangup_All();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_3001.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过sps外线呼入到Queue1-6401\n" +
                 "2.坐席1000、1001、1002、1003、1020同时响铃，" +
                 "3.分机1002接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Queue","QueueBasic","Basic","Trunk","InboundRoute","SPS","PSeries","Cloud","K2"})
    public void testQu02_BasicTrunkInboundRoute2(){

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("通过sps外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401", DEVICE_ASSIST_2, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if (!FXO_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("分机1002接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING,10)).as("[2.通话校验]:1002分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if (!FXO_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);
        pjsip.Pj_Hangup_All();

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_2001.toString()+" hung up",  SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.内部分机呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1003接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","Queue","QueueBasic","Basic","Trunk","Internal","PSeries","Cloud","K2"})
    public void testQu03_BasicTrunkInboundRoute3(){
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("通过分机1004呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(1004, "6401", DEVICE_IP_LAN, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("分机1003接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING,10)).as("[2.通话校验]:1003分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1004);
//        pjsip.Pj_Hangup_All();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1004.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1004.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1004.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过Account外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","Trunk","InboundRoute","SIP_ACCOUNT","PSeries","Cloud","K2"})
    public void testQu04_TrunkInboundRoute1Account(){
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(4000, "446401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("分机1000接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(4000);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Account_6700.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple(Account_6700.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_4000.toString()+" hung up",  ACCOUNTTRUNK, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过FXO外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1001接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","Trunk","InboundRoute","FXO","PSeries"})
    public void testQu05_TrunkInboundRoute1FXO(){
        if(FXO_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"FXO_1不存在,不测");
        }

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1001接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING,10)).as("[2.通话校验]:1001分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);

        sleep(WaitUntils.RETRY_WAIT);
        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", FXO_1, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_2001.toString()+" hung up",  FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过BRI外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1002接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","Trunk","InboundRoute","BRI","PSeries"})
    public void testQu06_TrunkInboundRoute1BRI(){
        if(BRI_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"BRI_1中继不存在,不测");
        }

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "886401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1002接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING,10)).as("[2.通话校验]:1002分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", BRI_1, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_2001.toString()+" hung up",  BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过E1外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1003接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","Trunk","InboundRoute","E1","PSeries"})
    public void testQu07_TrunkInboundRoute1E1(){
        if(E1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"E1口不存在,不测");
        }

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "666401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1003接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING,10)).as("[2.通话校验]:1003分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", E1, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_2001.toString()+" hung up",  E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Basic,Trunk,InboundRoute")
    @Description("1.通过GSM外线呼入到Queue1-6401\n" +
            "2.坐席1000、1001、1002、1003、1020同时响铃，" +
            "3.分机1000接听后其它坐席停止响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","Trunk","InboundRoute","GSM","PSeries"})
    public void testQu08_TrunkInboundRoute1GSM(){
        if(GSM.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"GSM不存在,不测");
        }

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "33" + DEVICE_TEST_GSM);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        step("分机1003接听后其它坐席停止响铃");
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,10)).as("[2.通话校验]:1000分机接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(DEVICE_ASSIST_GSM+"<"+DEVICE_ASSIST_GSM+">".toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", GSM, "", INBOUND.toString()))
                .contains(tuple(DEVICE_ASSIST_GSM+"<"+DEVICE_ASSIST_GSM+">".toString(), Extension_1000.toString(),ANSWER.toString(), DEVICE_ASSIST_GSM+"<"+DEVICE_ASSIST_GSM+">".toString()+" hung up",  GSM, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Ring All\n" +
            "2.通过sip外线呼入到Queue1，响铃2个周期后，坐席1000接听" +
            "3.1000正常通话，其它坐席停止响铃，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SPS","PSeries","Cloud","K2","RingAll"})
    public void testQu09_RingStrategy1() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 15)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }

        step("10s响铃，坐席1000、1001、1002、1003、1020挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 15)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        step("10s等待，第二轮坐席1000、1001、1002、1003、1020响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 20)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 15)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }

        step("10s响铃，第二轮坐席1000、1001、1002、1003、1020挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 15)).as("[4.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        step("10s等待，第三轮坐席1000、1001、1002、1003、1020响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[5.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[5.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[5.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[5.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }
        
        step("1000响铃接听");
        pjsip.Pj_Answer_Call(1000);

        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 5)).as("[6.通话校验]:1000分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[6.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[6.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[6.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SPS, "", INBOUND.toString()));
        
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Ring All，响铃时间20s\n" +
            "2.通过sps外线呼入到Queue1，坐席响铃时，分机1001拒接" +
            "3.第2个周期响铃时，分机1001再次响铃，接听，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SPS","PSeries","Cloud","K2","RingAll"})
    public void testQu10_RingStrategy2() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"agent_timeout\":20,\"ring_strategy\": \"ring_all\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }

        step("1001拒接，其他分机继续响铃");
        pjsip.Pj_Answer_Call(1001,486);

        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 5 )).as("[2.通话校验]:1001分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 1)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[2.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 5 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }

        step("10s响铃，坐席1000、1001、1002、1003、1020挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 22)).as("[3.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[3.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[3.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[3.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        step("10s等待，第二轮坐席1000、1001、1002、1003、1020响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 22)).as("[4.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[4.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[4.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[4.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(RING);
        }
        
        step("1001接听");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 5)).as("[5.通话校验]:1001分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[5.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[5.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[5.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[2.通话校验]:FXS分机挂断").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Ring All\n" +
            "2.通过sps外线呼入到Queue1，坐席响铃时，分机1001拒接" +
            "3.分机1002接听，挂断；cdr正确；；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SPS","PSeries","Cloud","K2","RingAll"})
    public void testQu11_RingStrategy3() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"ring_all\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("1001拒接，其他分机继续响铃");
        pjsip.Pj_Answer_Call(1001,486);

        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 5 )).as("[2.通话校验]:1001分机响铃").isEqualTo(HUNGUP);

        step("1001接听");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 5)).as("[5.通话校验]:1001分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[5.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[5.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[5.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1002);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1002.toString()+" hung up",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Ring All\n" +
            "2.分机1000拨打*76401退出Queue1" +
            "3.通过sps外线呼入到Queue1" +
            "4.坐席1000不会响铃，其它坐席1001、1002、1003、1020同时响铃，1003接听正常，挂断；cdr正确；" +
            "5.分机1000拨打*76401进入Queue1；通过sps外线呼入到Queue1" +
            "6.坐席1000、1001、1002、1003、1020同时响铃，1000接听正常，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SPS","PSeries","Cloud","K2","RingAll","FeatureCode"})
    public void testQu12_RingStrategy4() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();
        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"ring_all\",\"agent_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("分机1000拨打*76401退出Queue1");
        pjsip.Pj_Make_Call_No_Answer(1000, "*76401");

        assertStep("CLI验证：queue show queue 6401 确认分机1000退出6401");
        softAssertPlus.assertThat(!execAsterisk("queue show queue-"+queueNum1).contains("Local/1000"));

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP,1)).as("[1.通话校验]:1000分机不会响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("1003接听");
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 5)).as("[2.通话校验]:1003分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1003);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1003.toString()+" hung up",  SPS, "", INBOUND.toString()));

        step("分机1000拨打*76401加入Queue1");
        pjsip.Pj_Make_Call_No_Answer(1000, "*76401");

        assertStep("CLI验证：queue show queue 6401 确认分机1000已加入6401");
        softAssertPlus.assertThat(execAsterisk("queue show queue-"+queueNum1).contains("Local/1000"));

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("1000接听");
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 5)).as("[4.通话校验]:1000分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("1000挂断");
        pjsip.Pj_hangupCall(1000);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Ring All\n" +
            "2.分机1001拨打*076401暂停" +
            "3.通过sps外线呼入到Queue1" +
            "4.坐席1001不会响铃，其它坐席1000、1002、1003、1020同时响铃，1002接听正常，挂断；cdr正确；" +
            "5.分机1001拨打*076401进入Queue1；通过sps外线呼入到Queue1" +
            "6.坐席1001、1001、1002、1003、1020同时响铃，1000接听正常，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SPS","PSeries","Cloud","K2","FeatureCode"})
    public void testQu13_RingStrategy5() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"ring_all\",\"agent_timeout\":20").apply();
//        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"ring_all\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("分机1001拨打*076401退出Queue1");
        pjsip.Pj_Make_Call_No_Answer(1001, "*076401");

        assertStep("CLI验证：queue show queue 6401 确认分机1001暂停");
        softAssertPlus.assertThat(execAsterisk("queue show queue-"+queueNum1).contains("1001@only-dialextension-q6401 (ringinuse disabled) (dynamic) (paused)"));

        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1002、1003、1020同时响铃,1001不会响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP,1)).as("[1.通话校验]:1001分机不会响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("1002接听");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 5)).as("[2.通话校验]:1002分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("1002挂断");
        pjsip.Pj_hangupCall(1002);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1002.toString()+" hung up",  SPS, "", INBOUND.toString()));

        step("分机1001拨打*076401取消暂停");
        pjsip.Pj_Make_Call_No_Answer(1001, "*076401");

        assertStep("CLI验证：queue show queue 6401 确认分机1001已取消暂停");
        softAssertPlus.assertThat(execAsterisk("queue show queue-"+queueNum1).contains("1001@only-dialextension-q6401 (ringinuse disabled) (dynamic) (In use"));
        sleep(2000);
        step("通过SPS外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("1001接听");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 5)).as("[4.通话校验]:1001分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[4.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        step("1001挂断");
        pjsip.Pj_hangupCall(1001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+" hung up",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Least Recent\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.分机xx响铃，未接，等待10s后，分机xx再次响铃，挂断通话" +
            "4.通过sip外线再次呼入到Queue1" +
            "5.分机xx再次响铃，接听，挂断" +
            "6.通过sip外线再次呼入到Queue1,换成分机yy响铃，未接挂断" +
            "7.通过sps外线再次呼入到Queue1,分机yy响铃接听，挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","LeastRecent"})
    public void testQu14_RingStrategy6() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"least_recent\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"least_recent");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());
        int ext2 = Integer.parseInt(extList.get(1).getValue0());

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("最久没响铃的分机"+ext1+"响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,10)).as("[1.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  10)).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);

        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,  10)).as("[2.通话校验]:等待"+ext1+"分机挂断").isEqualTo(HUNGUP);
        step("等待10s,分机"+ext1+"再次响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  22)).as("[2.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,10)).as("[2.通话校验]:"+ext2+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(), NO_ANSWER.toString(), Extension_3001.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        step("再次通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("分机"+ext1+"响铃,接通");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  10)).as("[3.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,10)).as("[3.通话校验]:"+ext2+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(ext1);
        softAssertPlus.assertThat(getExtensionStatus(ext1, TALKING,  10)).as("[3.通话校验]:"+ext1+"分机通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);

        step("再次通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("分机"+ext2+"响铃,接通");
        softAssertPlus.assertThat(getExtensionStatus(ext2, RING,  10)).as("[4.通话校验]:"+ext2+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,10)).as("[4.通话校验]:"+ext2+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(ext2);
        softAssertPlus.assertThat(getExtensionStatus(ext2, TALKING,  10)).as("[4.通话校验]:"+ext2+"分机通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("被叫挂断");
        pjsip.Pj_hangupCall(ext2);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Fewest Calls\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.分机xx响铃，未接，等待10s后，分机xx再次响铃,挂断通话" +
            "4.通过sip外线再次呼入到Queue1" +
            "5.分机xx再次响铃，接听，挂断" +
            "6.通过sip外线再次呼入到Queue1，换成分机yy响铃，分机yy接听，挂断" +
            "7.通过sps外线再次呼入到Queue1，换成分机zz响铃，接听，挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","FewestCalls"})
    public void testQu15_RingStrategy7() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"fewest_calls\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"fewest_calls");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());
        int ext2 = Integer.parseInt(extList.get(1).getValue0());

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("通话最少的分机"+ext1+"响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,15)).as("[1.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  15)).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        step("响铃10s挂断");
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,  15)).as("[2.通话校验]:等待"+ext1+"分机挂断").isEqualTo(HUNGUP);
        step("等待10s,分机"+ext1+"再次响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  20)).as("[2.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,15)).as("[2.通话校验]:"+ext2+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(), NO_ANSWER.toString(), Extension_3001.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        step("再次通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("分机"+ext1+"响铃,接通");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  15)).as("[3.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,15)).as("[3.通话校验]:"+ext2+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(ext1);
        softAssertPlus.assertThat(getExtensionStatus(ext1, TALKING,  15)).as("[3.通话校验]:"+ext1+"分机通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);

        step("再次通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("分机"+ext2+"响铃,接通");
        softAssertPlus.assertThat(getExtensionStatus(ext2, RING,  15)).as("[4.通话校验]:"+ext2+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,15)).as("[4.通话校验]:"+ext1+"分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(ext2);
        softAssertPlus.assertThat(getExtensionStatus(ext2, TALKING,  15)).as("[4.通话校验]:"+ext2+"分机通话").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        step("被叫挂断");
        pjsip.Pj_hangupCall(ext2);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Random\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.所有坐席中只有一个分机响铃，接听正常，挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","Random"})
    public void testQu16_RingStrategy8() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        int index=0;

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"random\",\"agent_timeout\":60").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"random");

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        for (int i=0; i< extList.size(); i++){
            if (getExtensionStatus(Integer.parseInt(extList.get(i).getValue0()), RING,  1) == RING){
                index = i;
                break;
            }
        }
        step("分机"+extList.get(index).getValue0()+"响铃,其他分机未响铃");
        softAssertPlus.assertThat(getExtensionStatus(Integer.parseInt(extList.get(index).getValue0()), RING,  10)).as("[1.通话校验]:"+extList.get(index).getValue0()+"分机响铃").isEqualTo(RING);
        for (int i=0; i< extList.size(); i++){
            if (i != index){
                softAssertPlus.assertThat(getExtensionStatus(Integer.parseInt(extList.get(i).getValue0()), HUNGUP,1)).as("[1.通话校验]:"+extList.get(i).getValue0()+"分机未响铃").isEqualTo(HUNGUP);
            }
        }
        pjsip.Pj_Answer_Call(Integer.parseInt(extList.get(index).getValue0()));

        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),        ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), extList.get(index).getValue1(),ANSWER.toString(), Extension_3001.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Rrmemory\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.所有坐席中只有一个分机响铃，接听正常，挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("待补充预期检查方法")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","Rrmemory"})
    public void testQu17_RingStrategy9() {
        //todo 待补充预期检查方法
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Linear\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席按顺序依次响铃1000响铃，未接；等待10s,1001响铃，未接，等待10s,1002响铃，未接，等待10s,1003响铃，未接，10s后1000再次响铃，接听，挂断；cdr正确" +
            "4.通过sip外线呼入到Queue1" +
            "5.分机1000响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","Linear"})
    public void testQu18_RingStrategy10() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"linear\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"linear");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());
        int ext2 = Integer.parseInt(extList.get(1).getValue0());
        int ext3 = Integer.parseInt(extList.get(2).getValue0());
        int ext4 = Integer.parseInt(extList.get(3).getValue0());
        int ext5 = 0;
        if(!FXS_1.trim().equalsIgnoreCase("null")){
            ext5 = Integer.parseInt(extList.get(4).getValue0());
        }

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席按顺序依次响铃,一个循环后，第一个坐席再次响铃，接听，挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  22)).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[1.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[1.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[1.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[1.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("第二个坐席响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext2, RING,  22)).as("[2.通话校验]:"+ext2+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,22)).as("[2.通话校验]:"+ext1+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[2.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[2.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("第三个坐席响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext3, RING,  22)).as("[3.通话校验]:"+ext3+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[3.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,22)).as("[3.通话校验]:"+ext1+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[3.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("第四个坐席响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext4, RING,  22)).as("[4.通话校验]:"+ext4+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[4.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[4.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,22)).as("[4.通话校验]:"+ext1+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            step("第五个坐席响铃");
            softAssertPlus.assertThat(getExtensionStatus(ext5, RING,  22)).as("[5.通话校验]:"+ext5+"分机响铃").isEqualTo(RING);
            softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[5.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
            softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[5.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
            softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[5.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);
            softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,22)).as("[5.通话校验]:"+ext1+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("一个循环结束，第一个坐席再次响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  22)).as("[6.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[6.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[6.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[6.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("接听，被叫挂断");
        pjsip.Pj_Answer_Call(ext1);
        softAssertPlus.assertThat(getExtensionStatus(ext1, TALKING,  22)).as("[7.通话校验]:"+ext1+"分机接听").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(ext1);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),        ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), extList.get(0).getValue1(),    ANSWER.toString(), extList.get(0).getValue1()+" hung up",  SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Linear\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席按顺序依次响铃1000响铃，未接；等待10s,1001响铃，接听，通话正常，挂断，cdr正确" +
            "4.通过sip外线呼入到Queue1" +
            "5.分机1000响铃，主叫挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","Linear"})
    public void testQu19_RingStrategy11() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1,"\"ring_strategy\": \"linear\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"linear");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());
        int ext2 = Integer.parseInt(extList.get(1).getValue0());
        int ext3 = Integer.parseInt(extList.get(2).getValue0());
        int ext4 = Integer.parseInt(extList.get(3).getValue0());
        int ext5 = 0;

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            ext5 = Integer.parseInt(extList.get(4).getValue0());
        }

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席按顺序依次响铃,一个循环后，第一个坐席再次响铃，接听，挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  22)).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,22)).as("[1.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[1.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[1.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("第二个坐席响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext2, RING,  22)).as("[2.通话校验]:"+ext2+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,22)).as("[2.通话校验]:"+ext1+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,22)).as("[2.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,22)).as("[2.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,22)).as("[2.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("接听，被叫挂断");
        pjsip.Pj_Answer_Call(ext2);
        softAssertPlus.assertThat(getExtensionStatus(ext2, TALKING,  22)).as("[7.通话校验]:"+ext2+"分机接听").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(ext2);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),     ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), extList.get(1).getValue1(), ANSWER.toString(), extList.get(1).getValue1()+" hung up",  SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择Linear\n" +
            "2.编辑Queue1的静态坐席顺序为1003、1002" +
            "3.通过sip外线呼入到Queue1" +
            "4.坐席按顺序依次响铃1000响铃，未接；等待10s,1001响铃，未接，等待10s,1003响铃，未接，等待10s,1002响铃，未接，10s后1000再次响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","Linear"})
    public void testQu20_RingStrategy12() {

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        JSONArray jsonArray1 = new JSONArray();

        step("编辑Queue1的静态坐席顺序为1003、1002");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1003");
        extList.add("1002");

        List<ExtensionObject> extensionObjects = apiUtil.getExtensionSummary();

        for (String ext : extList) {
            if (extList != null && !extList.isEmpty()) {
                for (ExtensionObject extensionObject: extensionObjects) {
                    if (ext.equals(extensionObject.number)) {
                        JSONObject a = new JSONObject();
                        a.put("text", extensionObject.callerIdName);
                        a.put("text2", extensionObject.number);
                        a.put("value", String.valueOf(extensionObject.id));
                        a.put("type", "extension");
                        jsonArray1.put(a);
                    }
                }
            }
        }

        apiUtil.editQueue(queueNum1,String.format("\"ring_strategy\": \"linear\",\"static_agent_list\":%s",jsonArray1.toString()))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("1003响铃，1002不响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  50)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP,22)).as("[1.通话校验]:1002分机未响铃").isEqualTo(HUNGUP);

        step("1002响铃，1003不响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING,  30)).as("[2.通话校验]:1002分机响铃").isEqualTo(RING);
        log.debug("!11111111111111111");
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP,22)).as("[2.通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("接听，被叫挂断");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING,  22)).as("[3.通话校验]:1002分机接听").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),     ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1002.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("RingStrategy")
    @Description("1.编辑Queue1的RingStrategy选择RingAll\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.所有坐席同时响铃，坐席1003接听，挂断；cdr正确" +
            "注意：如果执行完毕，CDR校验没有报错，且报错只有-》[2.通话校验]:1001分机响铃] expected:<[2]> but was:<[0]>，那么可以忽略该报错")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","RingStrategy","SIP_REGISTER","PSeries","Cloud","K2","RingAll"})
    public void testQu21_RingStrategy13() {

        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("坐席1003接听，挂断；cdr正确");
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING,10 )).as("[1.通话校验]:1003分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1 )).as("[1.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[1.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[1.通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1003);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),     ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1003.toString(),  ANSWER.toString(), Extension_1003.toString()+" hung up",  SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("MaximumWaitingTime")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为20，Failover Destination为HangUp\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，20s内无人接听，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","MaximumWaitingTime","SIP_REGISTER","PSeries","Cloud","K2","HangUp"})
    public void testQu22_MaximumWaitingTime1() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"max_wait_time\":20,\"fail_dest\":\"end_call\"").apply();
        
        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("20s内无人接听，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 22)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),      ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                ;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("MaximumWaitingTime")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为20，Failover Destination为HangUp\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，等待10s后，分机1000接听，正常通话挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","MaximumWaitingTime","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu23_MaximumWaitingTime2() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"agent_timeout\":30,\"max_wait_time\":20,\"fail_dest\":\"end_call\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("等待10s后，分机1000接听，正常通话挂断；cdr正确");
        sleep(10000);
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 5)).as("[2.通话校验]:1000分机接听").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   ANSWER.toString(), QUEUE1_6401.toString() + " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("MaximumWaitingTime")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为20，Failover Destination为HangUp\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，20s内无人接听，主叫仍保持通话，主叫挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","MaximumWaitingTime","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu24_MaximumWaitingTime3() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"agent_timeout\":30,\"max_wait_time\":1000,\"fail_dest\":\"end_call\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("坐席全部响铃，20s内无人接听，主叫仍保持通话，主叫挂断");
        sleep(20000);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 5)).as("[2.通话校验]:1主叫仍保持通话").isEqualTo(TALKING);

        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);
//        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), Extension_3001.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("MaximumWaitingTime")
    @Description("1.编辑Queue1的RingStrategy选择Rrmemory，MaximumWaitingTime为20，Failover Destination为HangUp\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.20s后主叫被挂断，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","MaximumWaitingTime","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu25_MaximumWaitingTime4() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"ring_strategy\": \"rrmemory\",\"agent_timeout\":30,\"max_wait_time\":20,\"fail_dest\":\"end_call\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席分机响铃,20s后主叫被挂断，cdr正常");
        sleep(20000);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, hung up", SIPTrunk, "", INBOUND.toString()))
                ;

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为HangUp\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，10s内无人接听，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testQu26_FailoverDestination01() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, "\"ring_strategy\": \"ring_all\",\"agent_timeout\":10,\"max_wait_time\":10,\"fail_dest\":\"end_call\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5 )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("10s内无人接听，通话被挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 11)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Least Recent，MaximumWaitingTime为10，Failover Destination为【None】\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席xx响铃，10s内无人接听，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2","None"})
    public void testQu27_FailoverDestination02() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();
        apiUtil.editQueue(queueNum1, "\"ring_strategy\": \"least_recent\",\"agent_timeout\":10,\"max_wait_time\":10,\"fail_dest\":\"\"").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("获取最久未被叫响铃的分机");
        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"least_recent");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席"+ext1+"响铃");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING, 5 )).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);

        step("10s内无人接听，通话被挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP, 15 )).as("[1.通话校验]:"+ext1+"分机挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为Extension-分机A-1000\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，10s内无人接听，分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2","Extension"})
    public void testQu28_FailoverDestination03() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5 )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        sleep(11000);

        step("10s内无人接听，分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        step("1000接听，挂断；cdr正确");
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, failover", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为Extension-分机A-1000\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席全部响铃，10s内无人接听，分机1000响铃，主叫接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2","Extension"})
    public void testQu29_FailoverDestination04() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席1000、1001、1002、1003、1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5 )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        sleep(10000);
        step("10s内无人接听，分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        step("1000接听，挂断；cdr正确");
        pjsip.Pj_Answer_Call(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString() + " timed out, failover", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Fewest Calls，MaximumWaitingTime为10，Failover Destination为Extension Voicemail-分机A-1000\n" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席xx响铃，10s内无人接听，进入到分机1000的语音留言；等待10s挂断；登录分机1000查看新增一条语音留言；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2","Voicemail"})
    public void testQu30_FailoverDestination05() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"fewest_calls\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"ext_vm\",\"fail_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id))
                .apply();

        List<Quintet<String, String, String, String, String>> extList = getQueueExtNumWithRingStrategy(queueNum1,"fewest_calls");
        int ext1 = Integer.parseInt(extList.get(0).getValue0());
        int ext2 = Integer.parseInt(extList.get(1).getValue0());
        int ext3 = Integer.parseInt(extList.get(2).getValue0());
        int ext4 = Integer.parseInt(extList.get(3).getValue0());
        int ext5 = 0;

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            ext5 = Integer.parseInt(extList.get(4).getValue0());
        }

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席"+ext1+"响铃，10s内无人接听");
        softAssertPlus.assertThat(getExtensionStatus(ext1, RING,  10)).as("[1.通话校验]:"+ext1+"分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(ext2, HUNGUP,10)).as("[1.通话校验]:"+ext2+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext3, HUNGUP,10)).as("[1.通话校验]:"+ext3+"分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(ext4, HUNGUP,10)).as("[1.通话校验]:"+ext4+"分机未响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(ext5, HUNGUP,10)).as("[1.通话校验]:"+ext5+"分机未响铃").isEqualTo(HUNGUP);
        }

        step("进入到分机"+ext1+"的语音留言；等待10s挂断");
        softAssertPlus.assertThat(getExtensionStatus(ext1, HUNGUP,10)).as("[2.通话校验]:"+ext1+"分机挂断").isEqualTo(HUNGUP);

        sleep(15000);
        pjsip.Pj_Hangup_All();

        step("进入到分的语机1000音留言，查看新增一条语音留言，Name记录正确");

        step("网页admin登录,进入voicemail界面 ");
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Name");

        System.out.println("at 0 "+lists.get(0));
        softAssertPlus.assertThat(lists.size() > 0).as("至少有一条语音留言记录");
        if (lists.size() > 0){
            softAssertPlus.assertThat(lists.get(0)).as("最新一条记录是分机3001分机的记录").isEqualTo("3001\n" +
                    "External Number");
        }

        sleep(5000);
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Random，MaximumWaitingTime为10，Failover Destination为IVR-IVR0" +
            "2.通过sip外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到IVR0，asterisk后台检测到提示音“vm-greeting-dial-operator.slin”时，按0，分机A-1000响铃，接听，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SIP_REGISTER","PSeries","Cloud","K2","IVR"})
    public void testQu31_FailoverDestination06() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"random\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"ivr\",\"fail_dest_value\":\"%s\"",apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"vm-greeting-dial-operator.slin");
        thread.start();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("坐席10s内无人接听，进入IVR，检测提示音vm-greeting-dial-operator.slin");
        sleep(8000);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        step("3001进入IVR按0");
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("1000分机响铃，接听，挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(),    Extension_1000.toString() + " hung up"            , SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), IVR0_6200.toString(),     ANSWER.toString(),    Extension_3001.toString() + " called Extension"   , SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Rrmemory，MaximumWaitingTime为10，Failover Destination为RingGroup-RingGroup0" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到IVR0，asterisk后台检测到提示音“vm-greeting-dial-operator.slin”时，按0，分机A-1000响铃，接听，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","RingGroup"})
    public void testQu32_FailoverDestination07() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"rrmemory\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"ring_group\",\"fail_dest_value\":\"%s\"", apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听进入到RingGroup0,分机1000、1001、1003同时响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING,  10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  10)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING,  10)).as("[1.通话校验]:1003分机接听").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1003);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1003.toString(), ANSWER.toString(),    Extension_1003.toString() + " hung up"            , SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), RINGGROUP0_6300.toString(),ANSWER.toString(),    RINGGROUP0_6300.toString() + " connected"   , SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Rrmemory，MaximumWaitingTime为10，Failover Destination为RingGroup-RingGroup0" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到RingGroup0,分机1000、1001、1003同时响铃，无人接听，10s后到Failover Destination只剩分机1000响铃，1000接听，通话挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","RingGroup"})
    public void testQu33_FailoverDestination08() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"rrmemory\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"ring_group\",\"fail_dest_value\":\"%s\"", apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();


        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听进入到RingGroup0,分机1000、1001、1003同时响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING,  10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  10)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), ANSWER.toString(),    Extension_1000.toString() + " hung up"            , SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), RINGGROUP0_6300.toString(),NO_ANSWER.toString(), RINGGROUP0_6300.toString()+ " timed out, failover"   , SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Linear，MaximumWaitingTime为10，Failover Destination为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到Queue0，分机1000、1001、1003、1004同时响铃，1004接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu34_FailoverDestination09() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"linear\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听进入到Queue0,分机1000、1001、1003同时响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING,  10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  10)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING,  10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1004.toString(), ANSWER.toString(),    Extension_1004.toString() + " hung up"            , SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),    ANSWER.toString(),    QUEUE0_6400.toString()    + " connected"    , SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Linear，MaximumWaitingTime为10，Failover Destination为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到Queue0，分机1000、1001、1003、1004同时响铃，主叫按0，只剩分机1001响铃，1001接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu35_FailoverDestination10() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"linear\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听进入到Queue0,分机1000、1001、1003同时响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING,  10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  10)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING,  10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Send_Dtmf(3001,"0");

        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(), ANSWER.toString(),    Extension_1001.toString() + " hung up"            , SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),    ANSWER.toString(),    QUEUE0_6400.toString()    + " connected"    , SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择Linear，MaximumWaitingTime为10，Failover Destination为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席xx响铃，10s后无人接听进入到Queue0，分机1000、1001、1003、1004同时响铃；无人接听60s后到Failover Destination，分机1000响铃，接听，通话，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu36_FailoverDestination11() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"linear\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听进入到Queue0,分机1000、1001、1003同时响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING,  10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING,  10)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING,  10)).as("[1.通话校验]:1004分机响铃").isEqualTo(RING);

        sleep(12000);

        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(), ANSWER.toString(),    Extension_1000.toString() + " hung up"            , SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),    ANSWER.toString(),    QUEUE0_6400.toString()    + " connected"          , SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为External Number，Prefix：1 号码：3001" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席全部响铃，10s后无人接听拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","ExternalNumber"})
    public void testQu37_FailoverDestination12() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"ring_all\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"external_num\",\"fail_dest_prefix\":\"1\",\"fail_dest_value\":\"3001\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");

        step("10s后无人接听拨打外部号码13001，辅助1的分机3001响铃");
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(3001, RING,  10)).as("[2.通话校验]:3001分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),    NO_ANSWER.toString(), QUEUE1_6401.toString()    + " timed out, failover", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), "13001",                   ANSWER.toString(),    "13001 hung up"            , SPS, SIPTrunk, OUTBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为Play Prompt and Exit，选择prompt1，播放1遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席全部响铃，10s后无人接听查看后台打印提示音文件prompt1 1遍后，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu38_FailoverDestination13() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"ring_all\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"play_greeting\",\"fail_dest_value\":\""+PROMPT_1.replace("slin","wav")+"\",\"fail_dest_prefix\":\"1\""))
                .apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("无分机应答；asterisk后台检测共打印语音文件prompt1 一遍");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为10，Failover Destination为Play Prompt and Exit，选择prompt2，播放5遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席全部响铃，10s后无人接听查看后台打印提示音文件prompt2， 5遍后，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu39_FailoverDestination14() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"ring_all\",\"agent_timeout\":10,\"max_wait_time\":10," +
                "\"fail_dest\":\"play_greeting\",\"fail_dest_value\":\""+PROMPT_2.replace("slin","wav")+"\",\"fail_dest_prefix\":\"5\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_2);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("无分机应答；asterisk后台检测共打印语音文件prompt2 五遍");

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ < 800){
            sleep(70);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FailoverDestination")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Failover Destination为HangUp" +
            "2.通过sps外线呼入到Queue1" +
            "3.坐席全部响铃，响铃结束后再等10s，所有坐席再次响铃，主叫挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FailoverDestination","SPS","PSeries","Cloud","K2","HangUp"})
    public void testQu40_FailoverDestination15() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"ring_strategy\": \"ring_all\",\"agent_timeout\":10,\"max_wait_time\":600," +
                "\"fail_dest\":\"end_call\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 15)).as("[2.通话校验]:1000分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        step("响铃结束后再等10s，所有坐席再次响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[3.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }
        
        step("主叫挂断，cdr正确");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[4.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[4.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[4.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[4.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(), NO_ANSWER.toString(), Extension_2000.toString()+ " hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择1，目的地为Hang Up" +
            "2.通过sip外线呼入到Queue1" +
            "3.全部坐席响铃时，主叫按1；通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SIP_REGISTER","PSeries","Cloud","K2","HangUp"})
    public void testQu41_KeyPressEvent01() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600,\"press_key\":\"1\"," +
                "\"key_dest\":\"end_call\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按1；通话被挂断");
        pjsip.Pj_Send_Dtmf(3001,"1");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), Extension_3001.toString()+ " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择2，目的地为Extension-分机1004" +
            "2.通过sip外线呼入到Queue1" +
            "3.全部坐席第一轮响铃结束时，主叫按2；分机1004响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SIP_REGISTER","PSeries","Cloud","K2","Extension"})
    public void testQu42_KeyPressEvent02() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"2\",\"key_dest\":\"extension\",\"key_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        step("主叫按2；1004响铃、接听、挂断");
        pjsip.Pj_Send_Dtmf(3001,"2");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+ " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择3，目的地为Extension Voicemail-分机1001" +
            "2.通过sip外线呼入到Queue1" +
            "3.全部坐席响铃时，主叫按3；进入到分机1001的语音留言，等待15s后挂断通话；登录分机1001查看存在一条新的语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SIP_REGISTER","PSeries","Cloud","K2","Voicemail"})
    public void testQu43_KeyPressEvent03() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"3\",\"key_dest\":\"ext_vm\",\"key_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id))
                .apply();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按3；进入1001语音留言");
        pjsip.Pj_Send_Dtmf(3001,"3");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        sleep(15000);
        pjsip.Pj_Hangup_All();

        step("进入到分的语机1000音留言，查看新增一条语音留言，Name记录正确");

        step("网页admin登录,进入voicemail界面 ");
        auto.loginPage().login("1001", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Name");

        System.out.println("at 0 "+lists.get(0));
        softAssertPlus.assertThat(lists.size() > 0).as("至少有一条语音留言记录");
        if (lists.size() > 0){
            softAssertPlus.assertThat(lists.get(0)).as("最新一条记录是分机3001分机的记录").isEqualTo("3001\n" +
                    "External Number");
        }

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择4，目的地为IVR-IVR0" +
            "2.通过sip外线呼入到Queue1" +
            "3.全部坐席响铃时，主叫按4；查看asterisk后台打印播放提示音文件“vm-greeting-dial-operator.slin”时，按0，分机A-1000响铃，接听，挂断；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("------------该case如果执行完毕cdr校验提示的是目的中继不一致的话，暂时先忽略----------------")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SIP_REGISTER","PSeries","Cloud","K2","IVR"})
    public void testQu44_KeyPressEvent04() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"4\",\"key_dest\":\"ivr\",\"key_dest_value\":\"%s\"",apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"vm-greeting-dial-operator.slin");
        thread.start();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按4；进入IVR0，等待提示音vm-greeting-dial-operator.slin");
        pjsip.Pj_Send_Dtmf(3001,"4");
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        step("3001进入IVR按0");
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("1000分机响铃，接听，挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,  10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), QUEUE1_6401.toString()    + " connected"          , SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(),ANSWER.toString(),    Extension_1000.toString() + " hung up"            , SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), IVR0_6200.toString(),     ANSWER.toString(),    Extension_3001.toString() + " called Extension"   , SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择5，目的地为RingGroup-RingGroup0" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部坐席响铃结束时，主叫按5；分机1000、1001、1003同时响铃，1003接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","RingGroup"})
    public void testQu45_KeyPressEvent05() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"5\",\"key_dest\":\"ring_group\",\"key_dest_value\":\"%s\"",apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 15)).as("[2.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[2.通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        step("主叫按5；分机1000、1001、1003同时响铃，1003接听");
        pjsip.Pj_Send_Dtmf(2001,"5");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 15)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[3.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[3.通话校验]:1003分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1003.toString()+ " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), RINGGROUP0_6300.toString(),ANSWER.toString(), RINGGROUP0_6300.toString()+ " connected", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择5，目的地为RingGroup-RingGroup0" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部坐席响铃结束时，主叫按5；分机1000、1001、1003同时响铃，无人接听，10s后到Failover Destination-分机1000，1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","RingGroup"})
    public void testQu46_KeyPressEvent06() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"5\",\"key_dest\":\"ring_group\",\"key_dest_value\":\"%s\"", apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("全部坐席响铃结束时，主叫按5；分机1000、1001、1003同时响铃，无人接听，10s后到Failover Destination-分机1000，1000响铃，接听，挂断");
        pjsip.Pj_Send_Dtmf(2001,"5");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[2.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 22)).as("[3.通话校验]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+ " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), RINGGROUP0_6300.toString(),ANSWER.toString(), RINGGROUP0_6300.toString()+ " connected", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择6，目的地为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部坐席响铃结束时，主叫按6；1000、1001、1003、1004同时响铃，1004接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu47_KeyPressEvent07() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"6\",\"key_dest\":\"queue\",\"key_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("等待全部坐席响铃结束时");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 15)).as("[3.通话校验]:1000分机挂断").isEqualTo(HUNGUP);

        step("主叫按6；1000、1001、1003、1004同时响铃，1004接听，挂断");
        pjsip.Pj_Send_Dtmf(2001,"6");
        sleep(3000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机挂断").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[2.通话校验]:1001分机挂断").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机挂断").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1 )).as("[2.通话校验]:1004分机挂断").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1004.toString(),ANSWER.toString(), Extension_1004.toString()+ " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),   ANSWER.toString(), QUEUE0_6400.toString()+ " connected",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择6，目的地为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部坐席响铃结束时，主叫按6；1000、1001、1003、1004同时响铃，主叫按0，分机1001响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu48_KeyPressEvent08() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"6\",\"key_dest\":\"queue\",\"key_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按5；1000、1001、1003、1004同时响铃，1004接听，挂断");
        pjsip.Pj_Send_Dtmf(2001,"6");
        sleep(3000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[2.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1 )).as("[2.通话校验]:1004分机响铃").isEqualTo(RING);

        step("等待第一轮响铃结束");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 22)).as("[3.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[3.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[3.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1 )).as("[3.通话校验]:1004分机挂断").isEqualTo(HUNGUP);

        step("主叫按0,1001响铃 接听 挂断");
        pjsip.Pj_Send_Dtmf(2001,"0");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[4.通话校验]:1001分机响铃").isEqualTo(RING);
        
        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1001.toString()+ " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),NO_ANSWER.toString(), QUEUE0_6400.toString()+ " connected",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择6，目的地为Queue-Queue0" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部坐席响铃结束时，主叫按6；1000、1001、1003、1004同时响铃，无人接听，等待60s到FailoverDestination-分机1000，分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","Queue"})
    public void testQu49_KeyPressEvent09() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"6\",\"key_dest\":\"queue\",\"key_dest_value\":\"%s\"", apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按5；1000、1001、1003、1004同时响铃，1004接听，挂断");
        pjsip.Pj_Send_Dtmf(2001,"6");
        sleep(3000);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1 )).as("[2.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[2.通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1 )).as("[2.通话校验]:1004分机响铃").isEqualTo(RING);

        step("第一轮响铃结束");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 22)).as("[3.通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[3.通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[3.通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1 )).as("[3.通话校验]:1004分机挂断").isEqualTo(HUNGUP);


        step("等待60s到Queue0的失败目的地1000分机");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 65)).as("[4.通话校验]:1000分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),  NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),ANSWER.toString(), Extension_1000.toString()+ " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), QUEUE0_6400.toString(),NO_ANSWER.toString(), QUEUE0_6400.toString()+ " timed out, failover",  SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择7，目的地为External Number：Prefix：1 号码3001" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按7，辅助1的分机3001响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","ExternalNumber",})
    public void testQu50_KeyPressEvent10() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"7\",\"key_dest\":\"external_num\",\"key_dest_value\":\"3001\",\"key_dest_prefix\":\"1\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按7；3001响铃，接听，挂断");
        pjsip.Pj_Send_Dtmf(2001,"7");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[2.通话校验]:3001分机响铃").isEqualTo(RING);


        pjsip.Pj_Answer_Call(3001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),NO_ANSWER.toString(), QUEUE1_6401.toString()+ " connected",  SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2001.toString(), "13001",               ANSWER.toString(),    "13001 hung up"            , SPS, SIPTrunk, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择8，目的地为Play Prompt and Exit ，选择prompt3，播放1遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按8，asterisk后台检测共打印语音文件prompt3 一遍")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu51_KeyPressEvent11() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"8\",\"key_dest\":\"play_greeting\",\"key_dest_value\":\""+PROMPT_3.replace("slin","wav")+"\",\"key_dest_prefix\":\"1\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        asteriskObjectList.clear();
//        Thread thread = new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_3));
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_3);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按8；asterisk后台检测共打印语音文件prompt3 一遍");
        pjsip.Pj_Send_Dtmf(2001,"8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }

        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择9，目的地为Play Prompt and Exit ，选择prompt4，播放3遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按9，asterisk后台检测共打印语音文件prompt4 三遍")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu52_KeyPressEvent12() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"9\",\"key_dest\":\"play_greeting\",\"key_dest_value\":\""+PROMPT_4.replace("slin","wav")+"\",\"key_dest_prefix\":\"3\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        asteriskObjectList.clear();
//        Thread thread = new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_4));
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_4);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按9；asterisk后台检测共打印语音文件prompt4 三遍");
        pjsip.Pj_Send_Dtmf(2001,"9");

        int tmp = 0;
        while (asteriskObjectList.size() != 3 && tmp++ < 800){
            sleep(70);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择*，目的地为Play Prompt and Exit ，选择prompt5，播放5遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按9，asterisk后台检测共打印语音文件prompt4 三遍")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu53_KeyPressEvent13() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"*\",\"key_dest\":\"play_greeting\",\"key_dest_value\":\""+PROMPT_5.replace("slin","wav")+"\",\"key_dest_prefix\":\"5\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_5);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按*；asterisk后台检测共打印语音文件prompt5 五遍");
        pjsip.Pj_Send_Dtmf(2001,"*");

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择#，目的地为Play Prompt and Exit ，选择prompt2，播放2遍" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按#，asterisk后台检测共打印语音文件prompt2 两遍")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","PlayPromptandExit"})
    public void testQu54_KeyPressEvent14() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"#\",\"key_dest\":\"play_greeting\",\"key_dest_value\":\""+PROMPT_2.replace("slin","wav")+"\",\"key_dest_prefix\":\"2\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_2);
        thread.start();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按*；asterisk后台检测共打印语音文件prompt2 两遍");
        pjsip.Pj_Send_Dtmf(2001,"#");

        int tmp = 0;
        while (asteriskObjectList.size() != 2 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
            sleep(WaitUntils.RETRY_WAIT);
        }
        thread.flag = false;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("KeyPressEvent")
    @Description("1.编辑Queue1的RingStrategy选择RingAll，MaximumWaitingTime为600，Key Press Event选择0，目的地为[None]" +
            "2.通过sps外线呼入到Queue1" +
            "3.全部分机响铃时，主叫按0，通话被挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","KeyPressEvent","SPS","PSeries","Cloud","K2","None"})
    public void testQu55_KeyPressEvent15() {
        prerequisite();

        step("[恢复环境]");
        resetQueue1();

        apiUtil.editQueue(queueNum1, String.format("\"max_wait_time\":600," +
                "\"press_key\":\"0\",\"key_dest\":\"\""))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到Queue1");
        pjsip.Pj_Make_Call_No_Answer(2001, "996401");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(RING);
        }

        step("主叫按0；通话被挂断；cdr正确");
        pjsip.Pj_Send_Dtmf(2001, "0");

        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[2.通话校验]:1000分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[2.通话校验]:1001分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[2.通话校验]:1002分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[2.通话校验]:1003分机响铃").isEqualTo(HUNGUP);

        if(!FXS_1.trim().equalsIgnoreCase("null")){
            softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[1.通话校验]:FXS分机响铃").isEqualTo(HUNGUP);
        }

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), QUEUE1_6401.toString(),   NO_ANSWER.toString(), Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FeatureCode")
    @Description("1.Queue2-6402，动态坐席选择1000、1001，静态坐席选择1002、1003，响铃策略RingAll，其它默认；" +
            "2.编辑Feature Code-》Login/Logout 禁用；\n" +
            "3.分机1000拨打*76402加入队列;" +
            "4.asterisk检查加入失败" +
            "5.编辑Feature Code-》Login/Logout 启用；\n" +
            "6.分机1000拨打*76402加入队列；" +
            "7.asterisk检查加入成功；分机1005拨打队列6402，分机1000、1002、1003同时响铃，主叫挂断，检查cdr；" +
            "8.分机1000拨打*76402退出队列；" +
            "9.asterisk检查退出成功；\n" +
            "10.分机1005拨打队列6402，分机1002、1003同时响铃,1000不会响铃，主叫挂断，检查cdr；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FeatureCode","Login","Logout","SPS","PSeries","Cloud","K2","None"})
    public void testQu56_FeatureCode1() {
        prerequisite();

        step("网页admin登录,进入Queue界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if (lists.contains("6402")){
            step("删除队列6402");
            auto.queuePage().deleDataByDeleImage("6402").clickApply();
        }
        step("创建队列Queue3-6402");
        ArrayList<String> queueStaticMembers = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers.add("1002");
        queueStaticMembers.add("1003");
        queueDynamicMembers.add("1000");
        queueDynamicMembers.add("1001");
        apiUtil.createQueue("Queue2","6402",queueDynamicMembers,queueStaticMembers,null).apply();

        step("编辑Feature Code-》Login/Logout 禁用");
        apiUtil.editFeatureCode("\"enb_queue_login_logout\":0,\"enb_queue_pause_unpause\":0").apply();

        step("分机1000拨打*76402加入队列;");
        pjsip.Pj_Make_Call_No_Answer(1000,"*76402");
        sleep(WaitUntils.TALKING_WAIT);

        String queueInfo = execAsterisk("queue show queue-6402");
        softAssertPlus.assertThat(queueInfo.contains("1000")).as("1000登录队列6402失败").isFalse();

        step("编辑Feature Code-》Login/Logout 启用");
        apiUtil.editFeatureCode("\"enb_queue_login_logout\":1").apply();

        step("分机1000拨打*76402加入队列;");
        pjsip.Pj_Make_Call_No_Answer(1000,"*76402");
        sleep(WaitUntils.TALKING_WAIT);

        queueInfo = execAsterisk("queue show queue-6402");
        Assert.assertTrue(queueInfo.contains("1000"),"1000登录队列6402成功");

        pjsip.Pj_Make_Call_No_Answer(1005,"6402");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[1.通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE2_6402.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), "*76402",ANSWER.toString(), Extension_1000.toString()+" logged in Queue",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

        step("分机1000拨打*76402退出队列;");
        pjsip.Pj_Make_Call_No_Answer(1000,"*76402");
        sleep(WaitUntils.TALKING_WAIT);

        queueInfo = execAsterisk("queue show queue-6402");
        Assert.assertFalse(queueInfo.contains("1000"),"1000登录队列6402成功");

        pjsip.Pj_Make_Call_No_Answer(1005,"6402");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[1.通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[1.通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE2_6402.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), "*76402",ANSWER.toString(), Extension_1000.toString()+" logged out Queue",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FeatureCode")
    @Description("1.Queue2-6402，动态坐席选择1000、1001，静态坐席选择1002、1003，响铃策略RingAll，其它默认；" +
            "2.编辑Feature Code-》Login/Logout 特征码为***####；\n" +
            "3.分机1000拨打*76402加入队列;" +
            "4.asterisk检查加入失败" +
            "5.分机1001拨打***####6402加入队列;；\n" +
            "6.asterisk检查加入成功；" +
            "7.分机1005拨打队列6402，分机1001、1002、1003同时响铃，主叫挂断，检查cdr；" +
            "8.编辑Feature Code-》Login/Logout 特征码为*7；" +
            "9.分机1001拨打*76402退出队列;；\n" +
            "10.asterisk检查退出成功；\n" +
            "11.分机1005拨打队列6402，分机1002、1003同时响铃,1001不会响铃，主叫挂断，检查cdr；；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","FeatureCode","Login","Logout","SPS","PSeries","Cloud","K2","None"})
    public void testQu57_FeatureCode2() {
        prerequisite();

        step("网页admin登录,进入Queue界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if (lists.contains("6402")){
            step("删除队列6402");
            auto.queuePage().deleDataByDeleImage("6402").clickApply();
        }
        step("创建队列Queue3-6402");
        ArrayList<String> queueStaticMembers = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers.add("1002");
        queueStaticMembers.add("1003");
        queueDynamicMembers.add("1000");
        queueDynamicMembers.add("1001");
        apiUtil.createQueue("Queue2","6402",queueDynamicMembers,queueStaticMembers,null).apply();

        step("编辑Feature Code-》Login/Logout 特征码#***6**");
        apiUtil.editFeatureCode("\"enb_queue_login_logout\":1,\"queue_login_logout\":\"#***6**\"").apply();

        step("分机1001拨打*76402加入队列;");
        pjsip.Pj_Make_Call_No_Answer(1001,"*76402");
        sleep(WaitUntils.TALKING_WAIT);

        String queueInfo = execAsterisk("queue show queue-6402");
        softAssertPlus.assertThat(queueInfo.contains("1001")).as("1000登录队列6402失败").isFalse();

        step("分机1001拨打#***6**6402加入队列;");
        pjsip.Pj_Make_Call_No_Answer(1001,"#***6**6402");
        sleep(WaitUntils.TALKING_WAIT);

        queueInfo = execAsterisk("queue show queue-6402");
        softAssertPlus.assertThat(queueInfo.contains("1001")).as("1000登录队列6402成功").isTrue();

        pjsip.Pj_Make_Call_No_Answer(1005,"6402");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP,1 )).as("[1.通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE2_6402.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), "#***6**6402",ANSWER.toString(), Extension_1001.toString()+" logged in Queue",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

        step("编辑Feature Code-》Login/Logout 特征码*7");
        apiUtil.editFeatureCode("\"enb_queue_login_logout\":1,\"queue_login_logout\":\"*7\"").apply();

        step("分机1000拨打*76402退出队列;");
        pjsip.Pj_Make_Call_No_Answer(1001,"*76402");
        sleep(WaitUntils.TALKING_WAIT);


        queueInfo = execAsterisk("queue show queue-6402");
        Assert.assertFalse(queueInfo.contains("1001"),"1001退出登录队列6402成功");

        pjsip.Pj_Make_Call_No_Answer(1005,"6402");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[1.通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1 )).as("[1.通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE2_6402.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), "*76402",ANSWER.toString(), Extension_1001.toString()+" logged out Queue",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FeatureCode")
    @Description("1.新建Queue3-6403，静态坐席选择1000、1001，动态坐席选择1002、1003，响铃策略RingAll，其它默认；动态坐席1002、1003分别拨打*76403加入队列" +
            "2.编辑Feature Code-》Pause/Unpause 禁用；\n" +
            "3.分机1000拨打*076403暂停,分机1002拨打*076403暂停;" +
            "4.asterisk检查暂停失败" +
            "5.编辑Feature Code-》Pause/Unpause 启用；\n" +
            "6.分机1000拨打*076403暂停,分机1002拨打*076403暂停" +
            "7.asterisk检查坐席1000、1002为暂停服务状态；\n" +
            "8.分机1005拨打队列号码6403，分机1001、1003同时响铃，主叫挂断，检查cdr;\n" +
            "9.分机1000拨打*076403取消暂停,分机1002拨打*076403取消暂停" +
            "10.asterisk检查坐席1000、1002为正常状态；\n" +
            "11.分机1005拨打队列号码6403，分机1000、1001、1002、1003同时响铃，主叫挂断，检查cdr;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","FeatureCode","Pause","Unpause","SPS","PSeries","Cloud","K2","None"})
    public void testQu58_FeatureCode3() {
        prerequisite();

        apiUtil.editFeatureCode("\"enb_queue_login_logout\":1,\"queue_login_logout\":\"*7\"").apply();

        step("1.新建Queue3-6403，静态坐席选择1000、1001，动态坐席选择1002、1003，响铃策略RingAll，其它默认；");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);
        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if (lists.contains("6403")){
            step("删除队列6403");
            auto.queuePage().deleDataByDeleImage("6403").clickApply();
        }
        step("创建队列Queue3-6403");
        ArrayList<String> queueStaticMembers = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers.add("1000");
        queueStaticMembers.add("1001");
        queueDynamicMembers.add("1002");
        queueDynamicMembers.add("1003");
        apiUtil.createQueue("Queue3","6403",queueDynamicMembers,queueStaticMembers,null).apply();


        step("动态坐席1002、1003分别拨打*76403加入队列");
        pjsip.Pj_Make_Call_No_Answer(1003,"*76403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002,"*76403");
        sleep(WaitUntils.TALKING_WAIT);
        step("2.编辑Feature Code-》Pause/Unpause 禁用；") ;
        apiUtil.editFeatureCode("\"enb_queue_pause_unpause\":0").apply();

        step("3.分机1000拨打*076403暂停,分机1002拨打*076403暂停;" );
        pjsip.Pj_Make_Call_No_Answer(1000,"*076403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002,"*076403");
        sleep(WaitUntils.TALKING_WAIT);

        step("4.asterisk检查暂停失败") ;
        String queue1000 = getQueueExtInfoByExtNum("6403","1000");
        String queue1002 = getQueueExtInfoByExtNum("6403","1002");
        softAssertPlus.assertThat(queue1000.contains("Not in use")).as("[asterisk校验]1000暂停失败").isTrue();
        softAssertPlus.assertThat(queue1002.contains("Not in use")).as("[asterisk校验]1002暂停失败").isTrue();

        step("5.编辑Feature Code-》Pause/Unpause 启用；") ;
        apiUtil.editFeatureCode("\"enb_queue_pause_unpause\":1").apply();

        step("6.分机1000拨打*076403暂停,分机1002拨打*076403暂停" );
        pjsip.Pj_Make_Call_No_Answer(1000,"*076403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002,"*076403");
        sleep(WaitUntils.TALKING_WAIT);

        step("7.asterisk检查坐席1000、1002为暂停服务状态；" );
        queue1000 = getQueueExtInfoByExtNum("6403","1000");
        queue1002 = getQueueExtInfoByExtNum("6403","1002");
        softAssertPlus.assertThat(queue1000.contains("paused")).as("[asterisk校验]1000暂停成功").isTrue();
        softAssertPlus.assertThat(queue1002.contains("paused")).as("[asterisk校验]1002暂停成功").isTrue();

        step("8.分机1005拨打队列号码6403，分机1001、1003同时响铃，主叫挂断，检查cdr;") ;
        pjsip.Pj_Make_Call_No_Answer(1005,"6403");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP,1 )).as("[1.通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1 )).as("[1.通话校验]:1002分未机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE3_6403.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), "*076403",ANSWER.toString(), Extension_1000.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1002.toString(), "*076403",ANSWER.toString(), Extension_1002.toString()+" hung up",  "", "", INTERNAL.toString()));


        step("9.分机1000拨打*076403取消暂停,分机1002拨打*076403取消暂停" );
        pjsip.Pj_Make_Call_No_Answer(1000,"*076403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002,"*076403");
        sleep(WaitUntils.TALKING_WAIT);

        step("10.asterisk检查坐席1000、1002为正常状态；") ;
        queue1000 = getQueueExtInfoByExtNum("6403","1000");
        queue1002 = getQueueExtInfoByExtNum("6403","1002");
        softAssertPlus.assertThat(queue1000.contains("Not in use")).as("[asterisk校验]1000取消暂停成功").isTrue();
        softAssertPlus.assertThat(queue1002.contains("Not in use")).as("[asterisk校验]1002取消暂停成功").isTrue();

        step("11.分机1005拨打队列号码6403，分机1000、1001、1002、1003同时响铃，主叫挂断，检查cdr;");
        pjsip.Pj_Make_Call_No_Answer(1005,"6403");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,1 )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(1);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE3_6403.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("FeatureCode")
    @Description("1.新建Queue3-6403，静态坐席选择1000、1001，动态坐席选择1002、1003，响铃策略RingAll，其它默认；动态坐席1002、1003分别拨打*76403加入队列" +
            "2.编辑Feature Code-》Pause/Unpause 特征码为#*8*6**；；\n" +
            "3.分机1001拨打*076403暂停 分机1003拨打*076403暂停;" +
            "4.asterisk检查暂停失败" +
            "5.分机1001拨打#*8*6**6403暂停 分机1003拨打#*8*6**6403暂停" +
            "7.asterisk检查坐席1001、1003为暂停服务状态；\n" +
            "7.分机1005拨打队列号码6403，分机1000、1002同时响铃，主叫挂断，检查cdr;；\n" +
            "8.编辑Feature Code-》Pause/Unpause 特征码为*07；\n" +
            "9.分机1001拨打*076403取消暂停 分机1003拨打*076403取消暂停\n" +
            "10.asterisk检查坐席1001、1003为正常状态；\n" +
            "11.分机1005拨打队列号码6403，分机1000、1001、1002、1003同时响铃，主叫挂断，检查cdr;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","Queue","QueueBasic","FeatureCode","Pause","Unpause","SPS","PSeries","Cloud","K2","None"})
    public void testQu59_FeatureCode4() {
        prerequisite();

        step("1.新建Queue3-6403，静态坐席选择1000、1001，动态坐席选择1002、1003，响铃策略RingAll，其它默认；");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);
        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if (lists.contains("6403")){
            step("删除队列6403");
            auto.queuePage().deleDataByDeleImage("6403").clickApply();
        }
        step("创建队列Queue3-6403");
        ArrayList<String> queueStaticMembers = new ArrayList<>();
        ArrayList<String> queueDynamicMembers = new ArrayList<>();
        queueStaticMembers.add("1000");
        queueStaticMembers.add("1001");
        queueDynamicMembers.add("1002");
        queueDynamicMembers.add("1003");
        apiUtil.createQueue("Queue3","6403",queueDynamicMembers,queueStaticMembers,null).apply();

        step("动态坐席1002、1003分别拨打*76403加入队列");
        pjsip.Pj_Make_Call_No_Answer(1003,"*76403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002,"*76403");
        sleep(WaitUntils.TALKING_WAIT);

        step("2.编辑Feature Code-》Pause/Unpause 特征码为#*8*6**；");
        apiUtil.editFeatureCode("\"enb_queue_pause_unpause\":1,\"queue_pause_unpause\":\"#*8*6**\"").apply();

        execAsterisk("queue show queue-6403");

        step("3.分机1001拨打*076403暂停 分机1003拨打*076403暂停;");
        pjsip.Pj_Make_Call_No_Answer(1001,"*076403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1003,"*076403");
        sleep(WaitUntils.TALKING_WAIT);

        step("4.asterisk检查暂停失败") ;
        String queue1001 = getQueueExtInfoByExtNum("6403","1001");
        String queue1003 = getQueueExtInfoByExtNum("6403","1003");

        softAssertPlus.assertThat(queue1001.contains("Not in use")).as("[asterisk校验]1001暂停失败").isTrue();
        softAssertPlus.assertThat(queue1003.contains("Not in use")).as("[asterisk校验]1003暂停失败").isTrue();

        step("分机1001拨打#*8*6**6403暂停,分机1003拨打#*8*6**6403暂停" );
        pjsip.Pj_Make_Call_No_Answer(1001,"#*8*6**6403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1003,"#*8*6**6403");
        sleep(WaitUntils.TALKING_WAIT);

        step("asterisk检查坐席1001、1003为暂停服务状态；" );
        queue1001 = getQueueExtInfoByExtNum("6403","1001");
        queue1003 = getQueueExtInfoByExtNum("6403","1003");

        softAssertPlus.assertThat(queue1001.contains("paused")).as("[asterisk校验]1001暂停成功").isTrue();
        softAssertPlus.assertThat(queue1003.contains("paused")).as("[asterisk校验]1003暂停成功").isTrue();

        step("分机1005拨打队列号码6403，分机1001、1003同时响铃，主叫挂断，检查cdr;") ;
        pjsip.Pj_Make_Call_No_Answer(1005,"6403");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP,1 )).as("[1.通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1 )).as("[1.通话校验]:1003分未机响铃").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE3_6403.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), "#*8*6**6403",ANSWER.toString(), Extension_1001.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1003.toString(), "#*8*6**6403",ANSWER.toString(), Extension_1003.toString()+" hung up",  "", "", INTERNAL.toString()));

        step("编辑Feature Code-》Pause/Unpause 特征码为*07；" );
        apiUtil.editFeatureCode("\"queue_pause_unpause\":\"*07\"").apply();

        step("分机1001拨打*076403取消暂停 分机1003拨打*076403取消暂停" );
        pjsip.Pj_Make_Call_No_Answer(1001,"*076403");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1003,"*076403");
        sleep(WaitUntils.TALKING_WAIT);

        step("10.asterisk检查坐席1001、1003为正常状态；") ;
        queue1001 = getQueueExtInfoByExtNum("6403","1001");
        queue1003 = getQueueExtInfoByExtNum("6403","1003");
        softAssertPlus.assertThat(queue1001.contains("Not in use")).as("[asterisk校验]1001取消暂停成功").isTrue();
        softAssertPlus.assertThat(queue1003.contains("Not in use")).as("[asterisk校验]1003取消暂停成功").isTrue();

        step("11.分机1005拨打队列号码6403，分机1000、1001、1002、1003同时响铃，主叫挂断，检查cdr;");
        pjsip.Pj_Make_Call_No_Answer(1005,"6403");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[1.通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,1 )).as("[1.通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1 )).as("[1.通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1 )).as("[1.通话校验]:1003分机响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), QUEUE3_6403.toString(),NO_ANSWER.toString(), Extension_1005.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), "*076403",ANSWER.toString(), Extension_1001.toString()+" hung up",  "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1003.toString(), "*076403",ANSWER.toString(), Extension_1003.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

    }


    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Delete")
    @Description("1.单条删除Queue1" +
            "2.检查列表Queue1被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","Delete","PSeries","Cloud","K2"})
    public void testQu60_Delete1() {
//        prerequisite();

        step("网页admin登录,进入Queue界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);

        sleep(2000);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        System.out.println("list "+lists);
        if(!lists.contains(queueNum1)){
            auto.queuePage().createQueue(queueNum1,queueName1).clickSaveAndApply();
        }

        step("删除队列6401");
        auto.queuePage().deleDataByDeleImage(queueNum1).clickApply();

        sleep(2000);

        assertStep("删除成功");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list).doesNotContain(queueNum1);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("QueueBasic")
    @Story("Delete")
    @Description("1.再次创建Queue2" +
            "2.批量选择删除Queue2" +
            "3.检查列表Queue2被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","Queue","QueueBasic","Delete","PSeries","Cloud","K2"})
    public void testQu61_Delete2() {
        prerequisite();

        step("网页admin登录,进入Queue界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_queue);

        auto.queuePage().createQueue("6444","QU6").clickSaveAndApply();

        step("批量删除队列");
        auto.queuePage().deleAllQueue().clickSaveAndApply();

        assertStep("删除成功");
        List<String> list2 = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list2.size()).isEqualTo(0);
        softAssertPlus.assertAll();
    }

}

