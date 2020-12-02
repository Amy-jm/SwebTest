package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
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

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();

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
            initTestEnv();
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
    public void initTestEnv(){
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

        apiUtil.preferencesUpdate("\"max_call_duration\":50").apply();
        apiUtil.editExtension("1000","\"max_outb_call_duration\":20").apply();
        apiUtil.editExtension("1001","\"max_outb_call_duration\":30").apply();
        apiUtil.editExtension("1002","\"max_outb_call_duration\":60").apply();
        apiUtil.editExtension("1003","\"max_outb_call_duration\":-1").apply();
        apiUtil.editExtension("1004",String.format("\"max_outb_call_duration\":30,\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"ex_always_forward_dest\":\"extension\",\"ex_always_forward_value\":\"%s\",\"status\":\"available\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1005",String.format("\"enb_ctl_record\":1,\"max_outb_call_duration\":30,\"caller_id_rule_list\":[{\"caller_id\":\"2000\",\"action_dest\":\"extension\",\"action_value\":\"%s\",\"pos\":1},{\"caller_id\":\"4000\",\"action_dest\":\"ext_vm\",\"pos\":2},{\"caller_id\":\"3001\",\"action_dest\":\"ivr\",\"action_value\":\"%s\",\"pos\":3},{\"caller_id\":\"4001\",\"action_dest\":\"play_greeting\",\"play_greeting_times\":5,\"action_value\":\"prompt6.wav\",\"pos\":4},{\"caller_id\":\"4002\",\"action_dest\":\"accept_call\",\"pos\":5}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getIVRSummary("6200").id)).apply();
        if(!FXO_1.trim().equalsIgnoreCase("null") ||!FXO_1.trim().equalsIgnoreCase("")){
            apiUtil.editExtension("1020","\"max_outb_call_duration\":20").apply();
        }else{
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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_01_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_02_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_03_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_04_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_05_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_06_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_07_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_08_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_09_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_10_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_11_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_12_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_13_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_14_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_15_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_16_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_17_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_18_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_19_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_20_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_21_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_22_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_23_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_24_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_25_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_26_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_27_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Preferences-MaxCallDuration")
    @Story("MaxCallDuration,MaxOutboundCallDuration")
    @Description("28.分机1000拨打*2收听语音留言，输入密码1000#进入到语音留言\n" +
            "\t每隔10s按下*，直到通话被自动挂断；检查cdr,CallDuration为： 00:00:50，Reason为Exceeded the max call duration(s)")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_28_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_29_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2","Preferences", "MaxCallDuration","P3","MaxCallDuration","MaxOutboundCallDuration",""})
    public void testPreferences_30_MaxCallDuration() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @AfterClass(alwaysRun = true)
    public void restore(){
        step("#########  restore env start #########");
        step(" \"1、编辑Preferences->Max Call Duration 设置为1800s；\\n\" +\n");
        apiUtil.preferencesUpdate("\"max_call_duration\":1800").apply();

        step("#########  restore env end #########");

    }
}