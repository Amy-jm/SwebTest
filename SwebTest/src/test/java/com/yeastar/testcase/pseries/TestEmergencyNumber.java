package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import com.yeastar.untils.CDRObject.*;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test emergency number
 * @author: huangjx@yeastar.com
 * @create: 2020/12/03
 */
@Log4j2
public class TestEmergencyNumber extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestEmergencyNumber() {
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
    }

    public void prerequisite() {
        long startTime = System.currentTimeMillis();
        if (isDebugInitExtensionFlag) {
            initTestEnv();//TODO  local debug
            isDebugInitExtensionFlag = registerAllExtensions();
            isRunRecoveryEnvFlag = false;
        }

        if (isRunRecoveryEnvFlag) {
            step("=========== init before class  start =========");

            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initOutbound();
            initIVR();
            initInbound();
            initFeatureCode();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }


    public void initTestEnv() {
        log.info("################ before\n" +
                "\t编辑分机A-1000的Emergency Outbound Caller ID 为EmergencyExtA1000\n" +

                "\t编辑SPS外线-》Outbound Caller ID-》Outbound Caller ID List的Outbound Caller ID 为EmergencySPS ，Associated Extensions 为Default_Extension_Group\n" +
                "\t编辑SIP外线-》Outbound Caller ID-》Outbound Caller ID List的Outbound Caller ID 为EmergencySIP ，Associated Extensions 为Default_Extension_Group\n" +
                "\t编辑Account外线-》Outbound Caller ID-》Outbound Caller ID List的Outbound Caller ID 为EmergencyAccount ，Associated Extensions 为Default_Extension_Group\n" +
                "\t编辑BRI外线-》Outbound Caller ID-》Outbound Caller ID List的Outbound Caller ID 为EmergencyBRI ，Associated Extensions 为Default_Extension_Group\n" +
                "\t编辑E1外线-》Outbound Caller ID-》Outbound Caller ID List的Outbound Caller ID 为EmergencyE1，Associated Extensions 为Default_Extension_Group\n" +

                "\t添加紧急号码，Name:EmergencyOthertest ,Emergency Number:999,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
                "Trunk：SPS，\tTrunk's Emergency Outbound Caller ID：abc123\n" +
                "\t添加紧急号码，Name:Emergency1 ,Emergency Number:3001,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
                "Trunk ：SIP1，Trunk's Emergency Outbound Caller ID为空，保存");
        String EMERGENCY =String.format("\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"\",\"outbound_cid\":\"EMERGENCYNAME\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id);
        apiUtil.editExtension("1000","\"emergency_caller_id\":\"EmergencyExtA1000\"").apply();
        apiUtil.editSipTrunk(SPS,EMERGENCY.replace("EMERGENCYNAME","EmergencySPS")).
                editSipTrunk(SIPTrunk,EMERGENCY.replace("EMERGENCYNAME","EmergencySIP")).
                editSipTrunk(ACCOUNTTRUNK,EMERGENCY.replace("EMERGENCYNAME","EmergencyAccount")).
                editBriTrunk(BRI_1,EMERGENCY.replace("EMERGENCYNAME","EmergencyBRI")).
                editDigitalTrunk(E1,EMERGENCY.replace("EMERGENCYNAME","EmergencyE1")).
                apply();

        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"EmergencyOthertest\",\"number\":\"999\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"abc123\"}]}",apiUtil.getTrunkSummary(SPS).id)).
                createEmergency(String.format("{\"name\":\"Emergency1\",\"number\":\"3001\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(SIPTrunk).id)).apply();

    }

    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("1.分机A拨打3001\n" +
            "\t辅助1的3001响铃，接听，主叫挂断；检查cdr,Outbound Caller ID 为EmergencySIP")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P1",  ""})
    public void testPreferences_01_TrunkEmergency() {
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 3001");
        pjsip.Pj_Make_Call_No_Answer(1000, "3001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound","EmergencySIP"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("2.分机B拨打3001\n" +
            "\t辅助1的3001响铃，接听，被叫挂断；检查cdr,Outbound Caller ID 为EmergencySIP")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_02_TrunkEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 3001");
        pjsip.Pj_Make_Call_No_Answer(1001, "3001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("被叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString()+" hung up", "", SIPTrunk, "Outbound","EmergencySIP"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("3.编辑Emergency1，Trunk's Emergency Outbound Caller ID 设置为siptest\n" +
            "\t分机A拨打3001\n" +
            "\t\t辅助1的3001响铃，接听，被叫挂断；检查cdr,Outbound Caller ID 为siptest")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P1",  ""})
    public void testPreferences_03_TrunkEmergency() {
        prerequisite();
        apiUtil.editEmergency("Emergency1",String.format("\"trunk_list\": [{\"value\": \"%s\", \"outb_cid\": \"siptest\"}]",apiUtil.getTrunkSummary(SIPTrunk).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 3001");
        pjsip.Pj_Make_Call_No_Answer(1000, "3001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("被叫挂断");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), "3001 hung up", "", SIPTrunk, "Outbound","siptest"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t4.分机A拨打2101\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencySPS\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_04_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency2\",\"number\":\"2101\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(SPS).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]2101 ");
        pjsip.Pj_Make_Call_No_Answer(1000, "2101", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2101",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound","EmergencySPS"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t5.编辑Emergency2，Trunk's Emergency Outbound Caller ID 设置为0123456789\n" +
            "\t\t分机A拨打2101\n" +
            "辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为0123456789")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_05_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency2\",\"number\":\"2101\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(SPS).id)).apply();
        apiUtil.editEmergency("Emergency2",String.format("\"trunk_list\": [{\"value\": \"%s\", \"outb_cid\": \"0123456789\"}]",apiUtil.getTrunkSummary(SPS).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2101");
        pjsip.Pj_Make_Call_No_Answer(1000, "2101", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2101",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound","0123456789"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t6.辅助2分机2001拨打9999呼入到分机A并保持通话\n" +
            "\t\t分机B拨打2101\n" +
            "\t\t\t分机2001、分机A保持为Talking状态；\n" +
            "辅助2分机2000响铃，接听，挂断所有通话，检查cdr,Outbound Caller ID 为EmergencySPS\n" +
            "\t\tSPS外线的通话并发数大于1，呼叫紧急号码时原有通话不会被掐断\n" +
            "\t\t关键字TrunkPriority")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","TrunkPriority","P3",  ""})
    public void testPreferences_06_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency2 ,Emergency Number:2101,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：SPS，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency2\",\"number\":\"2101\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(SPS).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机B拨打2101");
        pjsip.Pj_Make_Call_No_Answer(1001, "2101", DEVICE_IP_LAN, false);
        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(),  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS,  "","Inbound",""))
                .contains(tuple(CDRNAME.Extension_1001.toString(), "2101",  CDRObject.STATUS.ANSWER.toString(), "2101 hung up", "", SPS, "Outbound","EmergencySPS"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency3 ,Emergency Number:2102,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：Account，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t7.分机A拨打2102\n" +
            "\t\t辅助3分机4000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyAccount\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_07_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency3 ,Emergency Number:2102,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：Account，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency3\",\"number\":\"2102\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2102");
        pjsip.Pj_Make_Call_No_Answer(1000, "2102", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2102",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound","EmergencyAccount"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency3 ,Emergency Number:2102,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：Account，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t8.编辑Emergency3，Trunk's Emergency Outbound Caller ID 设置为testAccount123\n" +
            "\t\t分机A拨打2102\n" +
            "\t\t\t辅助3分机4000响铃，接听，挂断；检查cdr,Outbound Caller ID 为testAccount123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_08_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency3 ,Emergency Number:2102,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：Account，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency3\",\"number\":\"2102\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).
                editEmergency("Emergency3",String.format("\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"testAccount123\"}]",apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2102");
        pjsip.Pj_Make_Call_No_Answer(1000, "2102", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2102",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound","testAccount123"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency4 ,Emergency Number:2103,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：BRI，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t9.分机A拨打2103\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyBRI\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_09_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency4 ,Emergency Number:2103,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：BRI，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency4\",\"number\":\"2103\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(BRI_1).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2103");
        pjsip.Pj_Make_Call_No_Answer(1000, "2103", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2103",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound","EmergencyBRI"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency4 ,Emergency Number:2103,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：BRI，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t10.编辑Emergency4，Trunk's Emergency Outbound Caller ID设置为testbri\n" +
            "\t\t分机A拨打2103\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为testbri")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_10_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency4 ,Emergency Number:2103,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：BRI，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency4\",\"number\":\"2103\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(BRI_1).id)).
                editEmergency("Emergency4",String.format("\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"testbri\"}]",apiUtil.getTrunkSummary(BRI_1).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency5 ,Emergency Number:2104,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：E1，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t11.分机A拨打2104\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyE1\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_11_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency5 ,Emergency Number:2104,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：E1，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency5\",\"number\":\"2104\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(E1).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2104");
        pjsip.Pj_Make_Call_No_Answer(1000, "2104", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2104",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound","EmergencyE1"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency5 ,Emergency Number:2104,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：E1，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t12.编辑Emergency5，Trunk's Emergency Outbound Caller ID设置为testE1\n" +
            "\t\t分机A拨打2104\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为testE1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_12_TrunkEmergency() {
        prerequisite();
        step("添加紧急号码，Name:Emergency5 ,Emergency Number:2104,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：E1，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency5\",\"number\":\"2104\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(E1).id)).
                editEmergency("Emergency5",String.format("\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"testbri\"}]",apiUtil.getTrunkSummary(E1).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "2104", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2104",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t13.分机A拨打辅助2的GSM号码\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_13_TrunkEmergency() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        step("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency6\",\"number\":\"%s\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",DEVICE_ASSIST_GSM,apiUtil.getTrunkSummary(GSM).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] "+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), DEVICE_ASSIST_GSM,  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound","1000"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t14.编辑Emergency6，Trunk's Emergency Outbound Caller ID设置为testgsm\n" +
            "\t\t分机A拨打辅助2的GSM号码\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为testgsm\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_14_TrunkEmergency() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency6\",\"number\":\"%s\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",DEVICE_ASSIST_GSM,apiUtil.getTrunkSummary(GSM).id)).
                editEmergency("Emergency6",String.format("\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"testgsm\"}]",apiUtil.getTrunkSummary(GSM).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] "+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000,DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), DEVICE_ASSIST_GSM,  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound","testgsm"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t15.编辑Emergency6，Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "\t\t分机A拨打辅助2的GSM号码\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyExtA1000\n" +
            "\t编辑Emergency6，Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID，Trunk's Emergency Outbound Caller ID为ExtTestGSM\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_15_TrunkEmergency() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency6\",\"number\":\"%s\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",DEVICE_ASSIST_GSM,apiUtil.getTrunkSummary(GSM).id)).
                editEmergency("Emergency6",String.format("\"outb_cid_option\":\"ext_first\",\"trunk_list\":[{\"value\":\"%s\"}]",apiUtil.getTrunkSummary(GSM).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] "+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
        sleep(3000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(),DEVICE_ASSIST_GSM,  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound","EmergencyExtA1000"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t\t16.分机A拨打辅助2的GSM号码\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_16_TrunkEmergency() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        step("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency6\",\"number\":\"%s\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",DEVICE_ASSIST_GSM,apiUtil.getTrunkSummary(GSM).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), DEVICE_ASSIST_GSM,  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound","EmergencyExtA1000"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "\t\t17.分机B拨打辅助2的GSM号码\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为ExtTestGSM")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_17_TrunkEmergency() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        step("添加紧急号码，Name:Emergency6 ,Emergency Number:辅助2的GSM号码,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：GSM，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency6\",\"number\":\"%s\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",DEVICE_ASSIST_GSM,apiUtil.getTrunkSummary(GSM).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] "+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1001, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(),DEVICE_ASSIST_GSM,  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound","ExtTestGSM"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t18.分机A拨打2000\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_18_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        step("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\\n\" +\n" +
                "            \"Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存");
        apiUtil.deleteAllEmergency().
                createEmergency(String.format("{\"name\":\"Emergency7\",\"number\":\"2000\",\"outb_cid_option\":\"emergency_first\",\"trunk_list\":[{\"value\":\"%s\",\"outb_cid\":\"\"}]}",apiUtil.getTrunkSummary(FXO_1).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2000");
        pjsip.Pj_Make_Call_No_Answer(1000, "2000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2000",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", FXO_1, "Outbound","1000"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t19..编辑Emergency7，Trunk's Emergency Outbound Caller ID设置为testfxo\n" +
            "\t\t分机A拨打2000\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为testfxo\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_19_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t20.编辑Emergency7 ，Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "\t\t分机A拨打2000\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyExtA1000\n" +
            "\t编辑Emergency7 ，Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID，Trunk's Emergency Outbound Caller ID为ExtTestFXO\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_20_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t\t21.分机A拨打2000\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_21_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t\t22.分机B拨打2000\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为ExtTestFXO\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_22_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency7 ,Emergency Number:2000,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "Trunk ：FXO，Trunk's Emergency Outbound Caller ID为空，保存\n" +
            "\t23.辅助2分机2001拨打2005呼入，分机A-1000响铃接听，并保持为通话状态\n" +
            "\t\t关键字TrunkPriority\n" +
            "\t\t标注\n" +
            "\t\tFXO的并发数为1，拨打紧急号码时，原有通话会被掐断\n" +
            "\t\t分机B拨打2000\n" +
            "\t\t\t2001的通话被掐断【检查分机A和2001的状态由Talking变为HangUp】；\n" +
            "辅助2的分机2000响铃，接听，挂断；检查cdr ,Outbound Caller ID 为1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_23_TrunkEmergency() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency8 ,Emergency Number:2105,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "1）Trunk ：SIP2【备注该外线不可用】，Trunk's Emergency Outbound Caller ID为SIP\n" +
            "2）Trunk ：SPS, Trunk's Emergency Outbound Caller ID为SPS\n" +
            "3) Trunk ：Account, Trunk's Emergency Outbound Caller ID为Account\n" +
            "\t24.分机A拨打2105\n" +
            "\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为SPS\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P2",  ""})
    public void testPreferences_24_TrunkEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("TrunkEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:Emergency8 ,Emergency Number:2105,Emergency Outbound Caller ID Priority选择Trunk's Emergency Outbound Caller ID\n" +
            "1）Trunk ：SIP2【备注该外线不可用】，Trunk's Emergency Outbound Caller ID为SIP\n" +
            "2）Trunk ：SPS, Trunk's Emergency Outbound Caller ID为SPS\n" +
            "3) Trunk ：Account, Trunk's Emergency Outbound Caller ID为Account\n" +
            "\t25.编辑Emergency8，调整外线Account在sps前面\n" +
            "\t\t分机A拨打2105\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断，检查cdr,Outbound Caller ID 为Account")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","TrunkEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_25_TrunkEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("编辑Emergency1，Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SIP1，\tTrunk's Emergency Outbound Caller ID：exttestsip\n" +
            "\t26.分机A拨打3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P2",  ""})
    public void testPreferences_26_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("编辑Emergency1，Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SIP1，\tTrunk's Emergency Outbound Caller ID：exttestsip\n" +
            "\t27.分机B拨打3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr,Outbound Caller ID 为exttestsip")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P2",  ""})
    public void testPreferences_27_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("编辑Emergency1，Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SIP1，Trunk's Emergency Outbound Caller ID 改为空\n" +
            "\t28.分机A拨打3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_28_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("编辑Emergency1，Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SIP1，Trunk's Emergency Outbound Caller ID 改为空\n" +
            "\t29.分机B拨打3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencySIP")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_29_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt2 ,Emergency Number:2201,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SPS，\tTrunk's Emergency Outbound Caller ID：EXTTestSPS\n" +
            "\t30.分机A拨打2201\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_30_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt2 ,Emergency Number:2201,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SPS，\tTrunk's Emergency Outbound Caller ID：EXTTestSPS\n" +
            "\t31.分机B拨打2201\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EXTTestSPS\n" +
            "\t编辑EmergencyExt2，Trunk's Emergency Outbound Caller ID 改为空\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_31_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt2 ,Emergency Number:2201,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SPS，\tTrunk's Emergency Outbound Caller ID：EXTTestSPS\n" +
            "\t\t32.分机A拨打2201\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_32_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt2 ,Emergency Number:2201,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：SPS，\tTrunk's Emergency Outbound Caller ID：EXTTestSPS\n" +
            "\t\t33.分机B拨打2201\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencySPS")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_33_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt3 ,Emergency Number:2202,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：Account，\tTrunk's Emergency Outbound Caller ID：ExtTestAccount\n" +
            "\t34.分机A拨打2202\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_34_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt3 ,Emergency Number:2202,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：Account，\tTrunk's Emergency Outbound Caller ID：ExtTestAccount\n" +
            "\t35.分机B拨打2202\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为ExtTestAccount\n" +
            "\t编辑EmergencyExt3，Trunk's Emergency Outbound Caller ID 改为空\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_35_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt3 ,Emergency Number:2202,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：Account，\tTrunk's Emergency Outbound Caller ID：ExtTestAccount\n" +
            "\t\t36.分机B拨打2202\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyAccount\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_36_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt3 ,Emergency Number:2202,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：Account，\tTrunk's Emergency Outbound Caller ID：ExtTestAccount\n" +
            "\t\t37.分机A拨打2202\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_37_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt4 ,Emergency Number:2203,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：BRI，\tTrunk's Emergency Outbound Caller ID：ExtTestBRI\n" +
            "\t38.分机A拨打2203\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_38_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt4 ,Emergency Number:2203,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：BRI，\tTrunk's Emergency Outbound Caller ID：ExtTestBRI\n" +
            "39.分机B拨打2203\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为ExtTestBRI")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_39_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt4 ,Emergency Number:2203,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：BRI，\tTrunk's Emergency Outbound Caller ID：ExtTestBRI\n" +
            "\t\t40.分机A拨打2203\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_40_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt4 ,Emergency Number:2203,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：BRI，\tTrunk's Emergency Outbound Caller ID：ExtTestBRI\n" +
            "\t\t41.分机B拨打2203\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为ExtTestBRI")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_41_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt5 ,Emergency Number:2204,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：E1，\tTrunk's Emergency Outbound Caller ID：ExtTestE1\n" +
            "\t42.分机A拨打2204\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_42_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt5 ,Emergency Number:2204,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：E1，\tTrunk's Emergency Outbound Caller ID：ExtTestE1\n" +
            "\t43.分机B拨打2204\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为ExtTestE1\n" +
            "\t编辑EmergencyExt5，Trunk's Emergency Outbound Caller ID 改为空\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_43_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt5 ,Emergency Number:2204,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：E1，\tTrunk's Emergency Outbound Caller ID：ExtTestE1\n" +
            "\t\t44.分机A拨打2204\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为EmergencyExtA1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_44_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("ExtensionEmergencyOutboundCallerID")
    @Description("添加紧急号码，Name:EmergencyExt5 ,Emergency Number:2204,Emergency Outbound Caller ID Priority选择Extension's Emergency Outbound Caller ID\n" +
            "Trunk：E1，\tTrunk's Emergency Outbound Caller ID：ExtTestE1\n" +
            "\t\t45.分机B拨打2204\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr,Outbound Caller ID 为ExtTestE1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","ExtensionEmergencyOutboundCallerID","P3",  ""})
    public void testPreferences_45_ExtensionEmergency() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("MaxCallDuration")
    @Description("46.编辑Preferences-》Max Call Duration 为40s;\n" +
            "\t分机C-1002 拨打999呼出\n" +
            "\t\t辅助2分机2000响铃、接听，50s 后依然保持通话，不会被自动挂断；挂断，检查cdr,\n" +
            "Outbound Caller ID：abc123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","MaxCallDuration","P3",  ""})
    public void testPreferences_46_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("DisableOutboundCalls")
    @Description("47.编辑分机D-1003启用Disable Outbound Calls\n" +
            "\t分机D-1003拨打999呼出\n" +
            "\t\t辅助2分机2000响铃、接听，挂断，检查cdr,\n" +
            "Outbound Caller ID：abc123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","DisableOutboundCalls","P3",  ""})
    public void testPreferences_47_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("Delete")
    @Description("48.删除Emergency1\n" +
            "\t分机A拨打3001\n" +
            "\t\t辅助3的分机4000响铃，接听，主叫挂断；检查cdr,Outbound Caller ID 为EmergencyAccount")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","Delete","P2",  ""})
    public void testPreferences_48_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("EmergencyNumber")
    @Story("Delete")
    @Description("49.批量删除所有紧急号码\n" +
            "\t删除成功，列表为空")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "EmergencyNumber","Delete","P2",  ""})
    public void testPreferences_49_Delete() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
       
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound",""));

        softAssertPlus.assertAll();
    }
}
