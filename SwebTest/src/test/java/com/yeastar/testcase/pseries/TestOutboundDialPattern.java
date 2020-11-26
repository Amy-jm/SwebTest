package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test out bound dial pattern
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestOutboundDialPattern extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestOutboundDialPattern() {
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

    /**
     * Business Hours and Holidays->Business Hours 删除所有；添加一条
     * 00:00-00:00，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     * *
     */
    public void initBusinessHours() {
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t1.分机1000拨打0呼出\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_01_Pattern_Trunk_Extesnion"})
    public void testOR_01_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]9 ");
        pjsip.Pj_Make_Call_No_Answer(1000, "9", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "9", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t2.分机1000拨打3001呼出\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_02_Pattern_Trunk_Extesnion"})
    public void testOR_02_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 3001");
        pjsip.Pj_Make_Call_No_Answer(1000, "3001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t3.分机1000拨打abc呼出\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_03_Pattern_Trunk_Extesnion"})
    public void testOR_03_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] abc");
        pjsip.Pj_Make_Call_No_Answer(1000, "abc", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "abc", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t4.分机1001拨打3001呼出\n" +
            "\t\t辅助1的分机3001正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_04_Pattern_Trunk_Extesnion"})
    public void testOR_04_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 3001");
        pjsip.Pj_Make_Call_No_Answer(1001, "3001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "3001", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t5.分机1002拨打9呼出\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_05_Pattern_Trunk_Extesnion"})
    public void testOR_05_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 9");
        pjsip.Pj_Make_Call_No_Answer(1002, "9", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t6.分机1002拨打00呼出\n" +
            "\t\t辅助3的分机4000正常响铃，接听，挂断；检查cdr;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_06_Pattern_Trunk_Extesnion"})
    public void testOR_06_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee]00 ");
        pjsip.Pj_Make_Call_No_Answer(1002, "00", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "00", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t7.分机1002拨打abc呼出\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_07_Pattern_Trunk_Extesnion"})
    public void testOR_07_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee]abc");
        pjsip.Pj_Make_Call_No_Answer(1002, "abc", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t8.分机1002拨打3001abc呼出\n" +
            "\t\t辅助3的分机4000正常响铃，接听，挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P2","testOR_08_Pattern_Trunk_Extesnion"})
    public void testOR_08_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\\n\" +\n" +
                "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\\n\" +\n" +
                "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern1", asList(SPS), asList("1000"), ".", 0).
                createOutbound("OR_Pattern2", asList(SIPTrunk), asList("1001"), "X!", 0).
                createOutbound("OR_Pattern3", asList(ACCOUNTTRUNK), asList("1002"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee]3001abc ");
        pjsip.Pj_Make_Call_No_Answer(1002, "3001abc", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "3001abc", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\n" +
            "新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\n" +
            "\t只有P系列支持，注意判断测试环境外线是否存在\n" +
            "\t9.分机1003拨打2000\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_09_Pattern_Trunk_Extesnion"})
    public void testOR_09_Pattern_Trunk_Extesnion() {
        if (FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\\n\"");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern4", asList(FXO_1), asList("1003"), "xxxx", 0).
                createOutbound("OR_Pattern5", asList(BRI_1), asList("1004"), "233.", 0).
                createOutbound("OR_Pattern6", asList(E1), asList("1004"), "233!", 0).
                createOutbound("OR_Pattern7", asList(GSM), asList("1005"), "1.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee]2000 ");
        pjsip.Pj_Make_Call_No_Answer(1003, "2000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "2000", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", FXO_1, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\n" +
            "新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\n" +
            "\t10.分机1004拨打23333\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr，呼出外线是BRI\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化测试】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_10_Pattern_Trunk_Extesnion"})
    public void testOR_10_Pattern_Trunk_Extesnion() {
        if (BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "BRI_1线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\\n\"");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern4", asList(FXO_1), asList("1003"), "xxxx", 0).
                createOutbound("OR_Pattern5", asList(BRI_1), asList("1004"), "233.", 0).
                createOutbound("OR_Pattern6", asList(E1), asList("1004"), "233!", 0).
                createOutbound("OR_Pattern7", asList(GSM), asList("1005"), "1.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 23333");
        pjsip.Pj_Make_Call_No_Answer(1004, "23333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "23333", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", BRI_1, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\n" +
            "新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\n" +
            "\t11.分机1004拨打233\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr，呼出外线是E1\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化测试】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_11_Pattern_Trunk_Extesnion"})
    public void testOR_11_Pattern_Trunk_Extesnion() {
        if (E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "BRI_1线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\\n\"");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern4", asList(FXO_1), asList("1003"), "xxxx", 0).
                createOutbound("OR_Pattern5", asList(BRI_1), asList("1004"), "233.", 0).
                createOutbound("OR_Pattern6", asList(E1), asList("1004"), "233!", 0).
                createOutbound("OR_Pattern7", asList(GSM), asList("1005"), "1.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 233");
        pjsip.Pj_Make_Call_No_Answer(1004, "233", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "233", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", E1, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\n" +
            "新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\n" +
            "新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\n" +
            "\t12.分机1005拨打辅助2的GSM号码\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_12_Pattern_Trunk_Extesnion"})
    public void testOR_12_Pattern_Trunk_Extesnion() {
        if (GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "BRI_1线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR_Pattern4,Dial Pattern为xxxx ，Strip：空，Prepend：空，外线：FXO，分机：分机D-1003；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern5,Dial Pattern 为233.  ,Strip：空，Prepend：空，外线BRI，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern6,Dial Pattern 为233!  ,Strip：空，Prepend：空，外线E1，分机：分机X-1004；\\n\" +\n" +
                "            \"新建呼出路由OR_Pattern7,Dial Pattern 为1.  ,Strip：空，Prepend：空，外线GSM，分机：分机X-1005；\\n\"");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern4", asList(FXO_1), asList("1003"), "xxxx", 0).
                createOutbound("OR_Pattern5", asList(BRI_1), asList("1004"), "233.", 0).
                createOutbound("OR_Pattern6", asList(E1), asList("1004"), "233!", 0).
                createOutbound("OR_Pattern7", asList(GSM), asList("1005"), "1.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] " + DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1005, DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(WaitUntils.SHORT_WAIT * 30);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", GSM, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern8,Dial Pattern 为X.  ,Strip：空，Prepend：空，外线sip外线，分机：FXS分机1020；\n" +
            "\t只有P系列支持，注意判断FXS是否存在\n" +
            "\t13.辅助2的分机2000通过FXO外线拨打773001\n" +
            "\t\t辅助1的分机3001正常响铃，接听，挂断；检查cdr，主叫是1020")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_13_Pattern_Trunk_Extesnion"})
    public void testOR_13_Pattern_Trunk_Extesnion() {
        if (FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXS_1线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR_Pattern8,Dial Pattern 为X.  ,Strip：空，Prepend：空，外线sip外线，分机：FXS分机1020；");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern8", asList(SIPTrunk), asList("1020"), "X.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee]773001 ");
        pjsip.Pj_Make_Call_No_Answer(2000, "773001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1020.toString(), "3001", STATUS.ANSWER.toString(), CDRNAME.Extension_1020.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t14.分机1004拨打0000\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_14_Pattern_Trunk_Extesnion"})
    public void testOR_14_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 0000");
        pjsip.Pj_Make_Call_No_Answer(1004, "0000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "0000", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t15.分机1004拨打00000\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_15_Pattern_Trunk_Extesnion"})
    public void testOR_15_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 00000");
        pjsip.Pj_Make_Call_No_Answer(1004, "00000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t16.分机1004拨打11111\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_16_Pattern_Trunk_Extesnion"})
    public void testOR_16_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 11111");
        pjsip.Pj_Make_Call_No_Answer(1004, "11111", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "11111", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t17.分机1004拨打11110\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_17_Pattern_Trunk_Extesnion"})
    public void testOR_17_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 11110");
        pjsip.Pj_Make_Call_No_Answer(1004, "11110", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t18.分机1004拨打222222\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_18_Pattern_Trunk_Extesnion"})
    public void testOR_18_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 222222");
        pjsip.Pj_Make_Call_No_Answer(1004, "222222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "222222", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t19.分机1004拨打222221\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_19_Pattern_Trunk_Extesnion"})
    public void testOR_19_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 222221");
        pjsip.Pj_Make_Call_No_Answer(1004, "222221", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t20.分机1004拨打9999\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_20_Pattern_Trunk_Extesnion"})
    public void testOR_20_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee]9999 ");
        pjsip.Pj_Make_Call_No_Answer(1004, "9999", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "9999", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t21.分机1004拨打9133123\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_21_Pattern_Trunk_Extesnion"})
    public void testOR_21_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 9133123");
        pjsip.Pj_Make_Call_No_Answer(1004, "9133123", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t22.分机1004拨打9200123\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_22_Pattern_Trunk_Extesnion"})
    public void testOR_22_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 9200123");
        pjsip.Pj_Make_Call_No_Answer(1004, "9200123", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t23.分机1004拨打8888123\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_23_Pattern_Trunk_Extesnion"})
    public void testOR_23_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "8888123", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t24.分机1004拨打9210123\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_24_Pattern_Trunk_Extesnion"})
    public void testOR_24_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 9210123");
        pjsip.Pj_Make_Call_No_Answer(1004, "9210123", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "9210123", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t25.分机1004拨打1234567890\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_25_Pattern_Trunk_Extesnion"})
    public void testOR_25_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 1234567890");
        pjsip.Pj_Make_Call_No_Answer(1004, "1234567890", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "1234567890", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t26.分机1004拨打123456789\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_26_Pattern_Trunk_Extesnion"})
    public void testOR_26_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 123456789");
        pjsip.Pj_Make_Call_No_Answer(1004, "123456789", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t27.分机1004拨打14050\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_27_Pattern_Trunk_Extesnion"})
    public void testOR_27_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 14050");
        pjsip.Pj_Make_Call_No_Answer(1004, "14050", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "14050", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t28.分机1004拨打24160\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_28_Pattern_Trunk_Extesnion"})
    public void testOR_28_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 24160");
        pjsip.Pj_Make_Call_No_Answer(1004, "24160", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "24160", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t29.分机1004拨打34980\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_29_Pattern_Trunk_Extesnion"})
    public void testOR_29_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();


        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 34980");
        pjsip.Pj_Make_Call_No_Answer(1004, "34980", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "34980", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：\n" +
            "Dial Pattern为XXXX,Strip为空，Prepend为空；\n" +
            "Dial Pattern为ZZZZZ,Strip为空，Prepend为空；\n" +
            "Dial Pattern为NNNNNN,Strip为空，Prepend为空；\n" +
            "Dial Pattern为9NZX123,Strip为空，Prepend为空；\n" +
            "Dial Pattern为1234567890,Strip为空，Prepend为空；\n" +
            "Dial Pattern为[1-3]4X[567-9]0,Strip为空，Prepend为空；\n" +
            "\t30.分机1004拨打34040\n" +
            "\t\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Pattern", "Trunk", "Extension", "P3","testOR_30_Pattern_Trunk_Extesnion"})
    public void testOR_30_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Pattern9", asList(SPS), asList("1004"), "", 0).
                editOutbound("OR_Pattern9", "\"dial_pattern_list\":[{\"dial_pattern\":\"XXXX\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"ZZZZZ\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"NNNNNN\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"9NZX123\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"1234567890\",\"strip\":0,\"prepend\":\"\"},{\"dial_pattern\":\"[1-3]4X[567-9]0\",\"strip\":0,\"prepend\":\"\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 34040");
        pjsip.Pj_Make_Call_No_Answer(1004, "34040", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Strip,Prepend")
    @Description("新建呼出路由OR_Strip1，外线选择sps，分机选择1005，添加多个Dial Pattern：\n" +
            "Dial Pattern为230. ,Strip为1，Prepend为空；\n" +
            "Dial Pattern为231. ,Strip为5，Prepend为3000；\n" +
            "Dial Pattern为2NZXX0 ,Strip为3，Prepend为3；\n" +
            "\t31.分机1005拨打23000\n" +
            "\t\t辅助2的分机2001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Strip", "Prepend", "P2","testOR_31_Pattern_Trunk_Extesnion"})
    public void testOR_31_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Strip1", asList(SPS), asList("1005"), "", 0).
                editOutbound("OR_Strip1", "\"dial_pattern_list\":[{\"dial_pattern\":\"230.\",\"strip\":1,\"prepend\":\"\"},{\"dial_pattern\":\"231.\",\"strip\":5,\"prepend\":\"3000\"},{\"dial_pattern\":\"2NZXX0\",\"strip\":3,\"prepend\":\"3\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] 23000");
        pjsip.Pj_Make_Call_No_Answer(1005, "23000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "23000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Strip,Prepend")
    @Description("新建呼出路由OR_Strip1，外线选择sps，分机选择1005，添加多个Dial Pattern：\n" +
            "Dial Pattern为230. ,Strip为1，Prepend为空；\n" +
            "Dial Pattern为231. ,Strip为5，Prepend为3000；\n" +
            "Dial Pattern为2NZXX0 ,Strip为3，Prepend为3；\n" +
            "\t32.分机1005拨打23010\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Strip", "Prepend", "P3","testOR_32_Pattern_Trunk_Extesnion"})
    public void testOR_32_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Strip1", asList(SPS), asList("1005"), "", 0).
                editOutbound("OR_Strip1", "\"dial_pattern_list\":[{\"dial_pattern\":\"230.\",\"strip\":1,\"prepend\":\"\"},{\"dial_pattern\":\"231.\",\"strip\":5,\"prepend\":\"3000\"},{\"dial_pattern\":\"2NZXX0\",\"strip\":3,\"prepend\":\"3\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] 23010");
        pjsip.Pj_Make_Call_No_Answer(1005, "23010", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "23010", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Strip,Prepend")
    @Description("新建呼出路由OR_Strip1，外线选择sps，分机选择1005，添加多个Dial Pattern：\n" +
            "Dial Pattern为230. ,Strip为1，Prepend为空；\n" +
            "Dial Pattern为231. ,Strip为5，Prepend为3000；\n" +
            "Dial Pattern为2NZXX0 ,Strip为3，Prepend为3；\n" +
            "\t33.分机1005拨打23110\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Strip", "Prepend", "P2","testOR_33_Pattern_Trunk_Extesnion"})
    public void testOR_33_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Strip1", asList(SPS), asList("1005"), "", 0).
                editOutbound("OR_Strip1", "\"dial_pattern_list\":[{\"dial_pattern\":\"230.\",\"strip\":1,\"prepend\":\"\"},{\"dial_pattern\":\"231.\",\"strip\":5,\"prepend\":\"3000\"},{\"dial_pattern\":\"2NZXX0\",\"strip\":3,\"prepend\":\"3\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] 23110");
        pjsip.Pj_Make_Call_No_Answer(1005, "23110", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "23110", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Strip,Prepend")
    @Description("新建呼出路由OR_Strip1，外线选择sps，分机选择1005，添加多个Dial Pattern：\n" +
            "Dial Pattern为230. ,Strip为1，Prepend为空；\n" +
            "Dial Pattern为231. ,Strip为5，Prepend为3000；\n" +
            "Dial Pattern为2NZXX0 ,Strip为3，Prepend为3；\n" +
            "\t34.分机1005拨打226000\n" +
            "\t\t辅助2的分机2001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern", "Strip", "Prepend", "P2","testOR_34_Pattern_Trunk_Extesnion"})
    public void testOR_34_Pattern_Trunk_Extesnion() {
        prerequisite();
        step("新建呼出路由OR_Pattern9，外线选择sps，分机选择1004,添加多个DialPattern：");
        apiUtil.deleteAllOutbound().createOutbound("OR_Strip1", asList(SPS), asList("1005"), "", 0).
                editOutbound("OR_Strip1", "\"dial_pattern_list\":[{\"dial_pattern\":\"230.\",\"strip\":1,\"prepend\":\"\"},{\"dial_pattern\":\"231.\",\"strip\":5,\"prepend\":\"3000\"},{\"dial_pattern\":\"2NZXX0\",\"strip\":3,\"prepend\":\"3\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] 226000");
        pjsip.Pj_Make_Call_No_Answer(1005, "226000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "226000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }


}
