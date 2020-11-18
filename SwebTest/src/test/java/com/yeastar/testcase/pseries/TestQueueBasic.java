package com.yeastar.testcase.pseries;


import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.AsteriskObject;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
        }

        if (runRecoveryEnvFlag) {
            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initIVR();
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

            if(runRecoveryEnvFlag){
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
    @Test(groups = {"P1","RingGroup", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute",  "SIP_REGISTER"})
    public void testQueueBasic01_BTIR(){

    }
}
