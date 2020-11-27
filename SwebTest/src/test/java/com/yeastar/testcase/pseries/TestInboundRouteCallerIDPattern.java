package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.SSHLinuxUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test inbound route caller id pattern
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteCallerIDPattern extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestInboundRouteCallerIDPattern() {
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
        }
        step("=========== init before class  end =========");

        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1，CallerIDPattern设置为3.\n" +
            "\t1.辅助1分机3001拨打3000呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "SIP_REGISTER", "P2","testIR_01_CallerID"})
    public void testIR_01_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"3.\"}]").apply();

        step("1:login with admin,trunk: " + SIPTrunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000" + ",[trunk] " + SIPTrunk);
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<3001> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1，CallerIDPattern设置为3.\n" +
            "\t2.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "SPS", "P2","testIR_02_CallerID"})
    public void testIR_02_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"3.\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xxx\n" +
            "\t3.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "SPS", "P3","testIR_03_CallerID"})
    public void testIR_03_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xxx\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xxn\n" +
            "\t4.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_04_CallerID"})
    public void testIR_04_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xxn\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("呼入失败，通话被挂断");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xxz\n" +
            "\t5.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute", "CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_05_CallerID"})
    public void testIR_05_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xxz\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("呼入失败，通话被挂断");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xxx!\n" +
            "\t6.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_06_CallerID"})
    public void testIR_06_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xxx!\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xxx.\n" +
            "\t7.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "SPS", "P3","testIR_07_CallerID"})
    public void testIR_07_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xxx.\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("呼入失败，通话被挂断");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2xx.\n" +
            "\t8.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_08_CallerID"})
    public void testIR_08_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2xx.\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为[1-5]00.\n" +
            "\t9.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_09_CallerID"})
    public void testIR_09_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"[1-5]00.\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2000\\3xxx\n" +
            "\t10.辅助1分机3001拨打3000呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P2","testIR_10_CallerID"})
    public void testIR_10_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2000\"},{\"cid_pattern\":\"3xxx\"}]").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<3001> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,Caller ID Pattern 设置为2000\\3xxx\n" +
            "\t11.辅助2分机2000通过拨打9999呼入\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "P3","testIR_11_CallerID"})
    public void testIR_11_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2000\"},{\"cid_pattern\":\"3xxx\"}]").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  ;Caller ID Pattern 设置为2000\\3xxx;呼入目的地为IVR-IVR0\n" +
            "\t12.辅助2分机2000通过拨打99+059100121222呼入\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "DIDPattern","IVR" ,"P3","testIR_12_CallerID"})
    public void testIR_12_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "IVR", "6200").
                editInbound("In1", "\"did_option\":\"patterns\",\"did_pattern_list\":[{\"did_pattern\":\"+0591XXZNZN[25-8].\"}]").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2000\"},{\"cid_pattern\":\"3xxx\"}]").apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+059100121222" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+059100121222", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-CallerIDPattern")
    @Story("CallerIDPattern,CallerIDMatchingSettings")
    @Description("编辑In1,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  ;Caller ID Pattern 设置为2000\\3xxx;呼入目的地为IVR-IVR0\n" +
            "\t13.辅助2分机2000拨打99+059100000000呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","CallerIDPattern", "CallerIDMatchingSettings", "DIDPattern", "IVR", "P3","testIR_13_CallerID"})
    public void testIR_13_CallerID() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "IVR", "6200").
                editInbound("In1", "\"did_option\":\"patterns\",\"did_pattern_list\":[{\"did_pattern\":\"+0591XXZNZN[25-8].\"}]").
                editInbound("In1", "\"cid_pattern_list\":[{\"cid_pattern\":\"2000\"},{\"cid_pattern\":\"3xxx\"}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+059100000000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+059100000000", DEVICE_ASSIST_2, false);

        step("呼入失败，通话被挂断");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));

    }

}
