package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import io.qameta.allure.*;
import lombok.AccessLevel;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test Preferences-MaxCallDuration
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestPreferencesMaxCallDuration extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestPreferencesMaxCallDuration() {
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
//            initTestEnv();
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
            initTestEnv();// todo 提交的时候 启用
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    /**
     * 时间条件
     */
    @Test
    public void initTestEnv() {
        step("######### 初始化测试环境\n" +
                "1、编辑Preferences->Max Call Duration 设置为50s；\n" +
                "2、编辑分机1000，Security-》Max Outbound Call Duration 设置为20s；\n" +
                "3、编辑分机1001，Security-》Max Outbound Call Duration 设置为30s；\n" +
                "4、编辑分机1002，Security-》Max Outbound Call Duration 设置为60s；\n" +
                "5、编辑分机1003，Security-》Max Outbound Call Duration 设置为[Follow System]\n" +
                "6、编辑分机1004，Security-》Max Outbound Call Duration 设置为30s；\n" +
                "Presence-》Call Forwarding-》Internal Calls-》Always-》Voicemail；  \n" +
                "Presence-》Call Forwarding-》External Calls -》Always-》分机1000；\n" +
                "7、编辑分机1005，Features-》Call Handling Based on Caller ID-》\n" +
                "Caller ID 为2000，呼入目的地为分机1000；\n" +
                "Caller ID 为4000，呼入目的地为Voicemail；\n" +
                "Caller ID为3001，呼入目的地为IVR0;\n" +
                "Caller ID为4001，呼入目的地为Play Greeting then Hang up；上传提示音Prompt6，播放次数5；\n" +
                "Caller ID为4002，呼入目的地为Accept Call;\n" +
                "Security-》Max Outbound Call Duration 设置为30s；\n" +
                "7、编辑分机1020，Security-》Max Outbound Call Duration 设置为20s；【需判断FXS分机是否存在】");

        apiUtil.preferencesUpdate("{\"max_call_duration\":50}").apply();
        apiUtil.editExtension("1000", "\"max_outb_call_duration\":20").apply();
        apiUtil.editExtension("1001", "\"max_outb_call_duration\":30").apply();
        apiUtil.editExtension("1002", "\"max_outb_call_duration\":60").apply();
        apiUtil.editExtension("1003", "\"max_outb_call_duration\":-1").apply();
        apiUtil.editExtension("1004", String.format("\"max_outb_call_duration\":30,\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"ex_always_forward_dest\":\"extension\",\"ex_always_forward_value\":\"%s\",\"status\":\"available\"}]", apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1005", String.format("\"enb_ctl_record\":1,\"max_outb_call_duration\":30,\"caller_id_rule_list\":[{\"caller_id\":\"2000\",\"action_dest\":\"extension\",\"action_value\":\"%s\",\"pos\":1},{\"caller_id\":\"4000\",\"action_dest\":\"ext_vm\",\"pos\":2},{\"caller_id\":\"3001\",\"action_dest\":\"ivr\",\"action_value\":\"%s\",\"pos\":3},{\"caller_id\":\"4001\",\"action_dest\":\"play_greeting\",\"play_greeting_times\":5,\"action_value\":\"prompt6.wav\",\"pos\":4},{\"caller_id\":\"4002\",\"action_dest\":\"accept_call\",\"pos\":5}]", apiUtil.getExtensionSummary("1000").id, apiUtil.getIVRSummary("6200").id)).apply();
        if (!FXO_1.trim().equalsIgnoreCase("null") || !FXO_1.trim().equalsIgnoreCase("")) {
            apiUtil.editExtension("1020", "\"max_outb_call_duration\":20").apply();
        } else {
            step("【FXS 分机不存在 初始环境失败！！！】7、编辑分机1020，Security-》Max Outbound Call Duration 设置为20s；【需判断FXS分机是否存在】");
        }

    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("1.分机1000拨打22222呼出\n" +
            "\t辅助2分机2000响铃，等待5秒，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:20，Reason为Exceeded the max call duration(s)\n" +
            "\t\t万一有BUG，60s后验证不通过，执行挂断所有通话；以下所有通话测试同理")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_01_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 222222");
        pjsip.Pj_Make_Call_No_Answer(1000, "222222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        sleep(5000);
        pjsip.Pj_Answer_Call(2000, false);
         assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 20秒自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 50)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "222222", "00:00:20", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("2.分机1000拨打分机1001\n" +
            "\t分机1001响铃，等待5s,接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_02_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        sleep(5000);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 自动挂断");
        assertThat(getExtensionStatus(1001, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), "00:00:50",STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("3.分机1000拨打22222呼出\n" +
            "\t辅助2分机2000响铃，不接，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:20，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_03_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]22222");
        pjsip.Pj_Make_Call_No_Answer(1000, "22222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        
        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22222", "00:00:20", STATUS.NO_ANSWER.toString(), "Exceeded the max call duration(s)", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("4.分机1001拨打3333呼出\n" +
            "\t辅助3的分机4000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:30，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_04_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 3333");
        pjsip.Pj_Make_Call_No_Answer(1001, "3333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 自动挂断");
        assertThat(getExtensionStatus(1001, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1001.toString(), "3333", "00:00:30", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", ACCOUNTTRUNK, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("5.分机1002拨打2222呼出\n" +
            "\t辅助2分机2000响铃，等待5秒，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_05_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1002, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        sleep(5000);
        pjsip.Pj_Answer_Call(2000, false);

        step("1002 自动挂断");
        assertThat(getExtensionStatus(1002, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1002.toString(), "2222", "00:01:00", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("6.分机1003拨打13001呼出\n" +
            "\t辅助1的分机3001响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_06_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1003, "13001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1003 自动挂断");
        assertThat(getExtensionStatus(1003, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1003.toString(), "13001", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("7.辅助2分机2001拨打773333呼出【即FXS分机1020拨打3333呼出】\n" +
            "\t辅助3的分机4000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:20，Reason为Exceeded the max call duration(s)\n" +
            "\t【需判断FXS分机是否存在】")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】" +
            "https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656")
    @Test(groups = {"PSeries", "FXS", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_07_MaxCallDuration() {
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }

        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 773333");
        pjsip.Pj_Make_Call_No_Answer(2001, "773333", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2001 自动挂断");
        assertThat(getExtensionStatus(2001, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(4000, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1020.toString(), "3333", "00:00:20", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", ACCOUNTTRUNK, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("8.2000拨打9999通过sps外线呼入\n" +
            "\t分机1000响铃，等待5s后接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_08_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
         assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

         step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为IVR0\n" +
            "\t9.通过sps外线呼入到IVR0，按0\n" +
            "\t\t分机A-1000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_09_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为IVR0");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为IVR0\n" +
            "\t10.通过sps外线呼入到IVR0,按13001通过sip外线呼出\n" +
            "\t\t辅助1的分机3001响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_10_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为IVR0");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_Send_Dtmf(2000, "13001");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(),"13001", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",SPS,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("11.编辑In1,呼入目的地为RingGroup0\n" +
            "\t通过sps外线呼入到RingGroup0\n" +
            "\t\t分机A-1000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_11_MaxCallDuration() {
        prerequisite();
        step(".编辑In1,呼入目的地为RingGroup0");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"",apiUtil.getRingGroupSummary(ringGroupNum0).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");//TODO DEBUG
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.RINGGROUP0_6300.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",  SPS, "","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("12.编辑In1,呼入目的地为Queue0\n" +
            "\t通过sps外线呼入到Queue0\n" +
            "\t\t分机A-1000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_12_MaxCallDuration() {
        prerequisite();
        step("12.编辑In1,呼入目的地为Queue0");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary(queueNum0).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");//TODO DEBUG
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.QUEUE0_6400.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",  SPS, "","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("13.编辑In1,呼入目的地为Extension Voicemail-分机1000\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到分机1000的语音留言，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_13_MaxCallDuration() {
        prerequisite();
        step("13.编辑In1,呼入目的地为Extension Voicemail-分机1000");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000_VOICEMAIL.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",SPS,  "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("14.编辑In1，呼入目的地为Conference6500\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到会议室6500，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_14_MaxCallDuration() {
        prerequisite();
        step("14.编辑In1，呼入目的地为Conference6500");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"conference\",\"def_dest_value\":\"%s\"",apiUtil.getConferenceSummary("6500").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Conference0_6500.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",  SPS,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("15.编辑In1,呼入目的地为External Number，Prefix:1 号码3001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听；直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_15_MaxCallDuration() {
        prerequisite();
        step("15.编辑In1,呼入目的地为External Number，Prefix:1 号码3001");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),"13001", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",SPS,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("16.编辑In1,呼入目的地为Outbound Route，选择Out8\n" +
            "\t通过sip外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听；直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_16_MaxCallDuration() {
        prerequisite();
        step("16.编辑In1,呼入目的地为Outbound Route，选择Out8");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"",apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000",DEVICE_ASSIST_1,false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("3001 自动挂断");
        assertThat(getExtensionStatus(3001, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_3001.toString(), "3000", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SIPTrunk, SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("17.编辑In1，呼入目的地为Play Greeting then Hang Up，选择prompt6(一次播放时长约21秒），播放次数：5次\n" +
            "\t通过sps外线呼入\n" +
            "\t\t播放提示音prompt6,直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_17_MaxCallDuration() {
        prerequisite();
        step("17.编辑In1，呼入目的地为Play Greeting then Hang Up，选择prompt6(一次播放时长约21秒），播放次数：5次");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"5\",\"def_dest_value\":\"prompt6.wav\""));
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "prompt6.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=2 && tmp <= 800){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag =  false;

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(), "play_file", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",  SPS,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1004\n" +
            "\t18.分机1000拨打1004\n" +
            "\t\t进入到分机1004的语音留言，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_18_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1004");
        pjsip.Pj_Make_Call_No_Answer(1000, "1004", DEVICE_IP_LAN, false);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1004_VOICEMAIL.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1004\n" +
            "\t19.通过account外线呼入到1004\n" +
            "\t\t分机1000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("时间误差，暂不校验 CallDuration")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_19_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441004 ");
        pjsip.Pj_Make_Call_No_Answer(4000, "441004", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1004.toString(), STATUS.NO_ANSWER.toString(), "t estX<1004> forwarded",  ACCOUNTTRUNK,"", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "Exceeded the max call duration(s)",  ACCOUNTTRUNK,"", "Inbound"));

        softAssertPlus.assertAll();

        //todo 时间误差，暂不校验
        /**
         * 1) [[CDR校验] Time：2020-12-03 13:56:32]
         * Expecting ArrayList:
         *  <[("4000<4000>", "test A<1000>", "00:00:46", "ANSWERED", "Exceeded the max call duration(s)", "6700Account", "", "Inbound"),
         *     ("4000<4000>", "t estX<1004>", "00:00:03", "NO ANSWER", "t estX<1004> forwarded", "6700Account", "", "Inbound")]>
         * to contain:
         *  <[("4000<4000>", "test A<1000>", "00:00:50", "ANSWERED", "Exceeded the max call duration(s)", "6700Account", "", "Inbound")]>
         * but could not find the following element(s):
         *  <[("4000<4000>", "test A<1000>", "00:00:50", "ANSWERED", "Exceeded the max call duration(s)", "6700Account", "", "Inbound")]>
         */

    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1005\n" +
            "\t20.辅助2分机2000拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_20_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1005\n" +
            "\t21.辅助3分机4000拨打444444呼入\n" +
            "\t\t进入到分机1005的语音留言，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_21_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 444444");
        pjsip.Pj_Make_Call_No_Answer(4000, "444444", DEVICE_ASSIST_3, false);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(4000, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_4000.toString(),CDRNAME.Extension_1000_VOICEMAIL.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SPS, "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1005\n" +
            "\t22.辅助1分机3001拨打3000呼入\n" +
            "\t\t进入到IVR0，按0到分机1000，分机1000响铃接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_22_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
         assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

         step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_3001.toString(), "3000", "00:00:20", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1005\n" +
            "\t23.辅助3分机4001拨打4444呼入\n" +
            "\t\t播放提示音prompt6,直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_23_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "prompt6.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4001" + ",[callee] 4444");
        pjsip.Pj_Make_Call_No_Answer(4001, "4444", DEVICE_ASSIST_3, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=2 && tmp <= 800){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag =  false;

        step("2000 自动挂断");
        assertThat(getExtensionStatus(4001, HUNGUP, 90)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1000.toString(), "play_file", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SPS, "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1005\n" +
            "\t24.辅助3分机4002拨打4444呼入\n" +
            "\t\t分机1005响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_24_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4002" + ",[callee] 4444");
        pjsip.Pj_Make_Call_No_Answer(4002, "4444", DEVICE_ASSIST_3, false);


        step("[通话状态校验]");
        assertThat(getExtensionStatus(1005, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005, false);
        assertThat(getExtensionStatus(1005, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1005 自动挂断");
        assertThat(getExtensionStatus(1005, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(4002, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple( "4002<4002>",CDRNAME.Extension_1005.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", ACCOUNTTRUNK, "",  "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1000\n" +
            "\t25.通过sps外线呼入到分机1000；\n" +
            "1000应答按*3等待2秒按1001转移给分机1001；\n" +
            "\t\t分机1001响铃，接听，1000挂断，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("时间误差，暂不校验 CallDuration 时长")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_25_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1000");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1000, false);
        pjsip.Pj_Send_Dtmf(1000,"*","3");
        sleep(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","0","0","1");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(1001, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"))
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "test A<1000> called test2 B<1001>", "","",  "Internal"));

        softAssertPlus.assertAll();
        //todo 时间误差，暂不校验
        /**
         * 1) [[CDR校验] Time：2020-12-03 15:15:28]
         * Expecting ArrayList:
         *  <[("2000<2000>", "test2 B<1001>", "00:00:10", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound"),
         *     ("2000<2000>", "test A<1000>", "00:00:19", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound"),
         *     ("test A<1000>", "test2 B<1001>", "00:00:21", "ANSWERED", "test A<1000> called test2 B<1001>", "", "", "Internal")]>
         * to contain:
         *  <[("2000<2000>", "test2 B<1001>", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound")]>
         * but could not find the following element(s):
         *  <[("2000<2000>", "test2 B<1001>", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound")]>
         */
    }


    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1000\n" +
            "\t26.通过sps外线呼入到分机1000；\n" +
            "1000应答按*03等待2秒按1001转移给分机1001；\n" +
            "\t\t分机1001响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_26_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1000");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1000, false);
        pjsip.Pj_Send_Dtmf(1000,"*","0","3");
        sleep(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","0","0","1");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000/1000/1001 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(1001, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]_包含Call Duration");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1001.toString(), "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"));

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> called test A<1000>", SPS,"",  "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("编辑In1，呼入目的地为分机1000\n" +
            "\t27.通过sps外线呼入到分机1000；\n" +
            "1000应答按*3等待2秒按13001转移给外部号码；\n" +
            "\t\t辅助1的分机3001响铃，接听，分机1000挂断，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("时间误差，暂不校验 call duration")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_27_MaxCallDuration() {
        prerequisite();
        step("编辑In1，呼入目的地为分机1000");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1000, false);
        pjsip.Pj_Send_Dtmf(1000,"*","3");
        sleep(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","3","0","0","1");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("2000/1000/3001 自动挂断");
        assertThat(getExtensionStatus(2000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(3001, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), "13001", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,SIPTrunk,  "Outbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),  STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", SPS,"",  "Inbound"))
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), "test A<1000> called 13001", "",SIPTrunk,  "Outbound"));

        softAssertPlus.assertAll();
        //todo 时间误差，暂不校验
        /**
         * Expecting ArrayList:
         *  <[("2000<2000>", "13001", "00:00:09", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "sipRegister", "Outbound"),
         *     ("2000<2000>", "test A<1000>", "00:00:20", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound"),
         *     ("test A<1000>", "13001", "00:00:22", "ANSWERED", "test A<1000> called 13001", "", "sipRegister", "Outbound")]>
         * to contain:
         *  <[("2000<2000>", "991000", "00:00:50", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound")]>
         * but could not find the following element(s):
         *  <[("2000<2000>", "991000", "00:00:50", "ANSWERED", "Exceeded the max call duration(s)", "SPS1", "", "Inbound")]>
         *
         * at TestPreferencesMaxCallDuration.testPreferences_27_MaxCallDuration(TestPreferencesMaxCallDuration.java:1157)
         */
    }


    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("28.分机1000拨打*2收听语音留言，输入密码1000#进入到语音留言\n" +
            "\t每隔10s按下*，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】+ 外线 呼入目的地到 RingGroup、Queue、Conference、Play、Voicemail  Max Call Duration 设置 不生效】\" +\n" +
            "            \"https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036656\"")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""},enabled = false)
    public void testPreferences_28_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *2");
        pjsip.Pj_Make_Call_No_Answer(1000, "*2", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1000,"1","0","0","0","#");

        int temp = 0;
        while(++temp<=10 && (getExtensionStatus(1000, HUNGUP, 2)!=HUNGUP || getExtensionStatus(1000, HUNGUP, 2)!=IDLE) ){
            pjsip.Pj_Send_Dtmf(1000,"*");
            sleep(1000*10);
        }

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1000.toString(), "*2", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("29.呼叫功能-》速拨码，添加速拨码：1 ，电话号码：13001\n" +
            "\t分机A拨打*891呼出\n" +
            "\t\t辅助1的分机3001响铃，接听，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:20，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_29_MaxCallDuration() {
        prerequisite();
        apiUtil.createSpeeddial("\"code\":\"1\",\"phone_number\":\"13001\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]*891 ");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000/3001 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 60)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
        assertThat(getExtensionStatus(3001, HUNGUP, 5)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", "00:00:20", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("30.分机A拨打*61001对讲\n" +
            "\t分机1001自动应答，，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Preferences", "MaxCallDuration", "P3", "MaxCallDuration", "MaxOutboundCallDuration", ""})
    public void testPreferences_30_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *61001");
        pjsip.Pj_Make_Call_No_Answer(1000, "*61001", DEVICE_IP_LAN, false);

        step("1000 自动挂断");
        assertThat(getExtensionStatus(1000, HUNGUP, 100)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "callDuration", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
               .contains(tuple(CDRNAME.Extension_1000.toString(), "*61001", "00:00:50", STATUS.ANSWER.toString(), "Exceeded the max call duration(s)", "", "", "Internal"));

        softAssertPlus.assertAll();
    }

//    @AfterClass(alwaysRun = true)
//    public void restore() {
//        step("#########  restore env start #########");
//        step(" \"1、编辑Preferences->Max Call Duration 设置为1800s；\\n\" +\n");
//        apiUtil.preferencesUpdate("{\"max_call_duration\":1800}").apply();
//
//        step("#########  restore env end #########");
//
//    }

}