package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
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

    private boolean isRunRecoveryEnvFlag = true;
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
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n" +
            "\t2.分机1000拨打3001呼出\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n" +
            "\t3.分机1000拨打abc呼出\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n" +
            "\t4.分机1001拨打3001呼出\n" +
            "\t\t辅助1的分机3001正常响铃，接听，挂断；检查cdr\n" +
            "\t5.分机1002拨打0呼出\n" +
            "\t\t呼出失败，通话被挂断；\n" +
            "\t6.分机1002拨打00呼出\n" +
            "\t\t辅助3的分机4000正常响铃，接听，挂断；检查cdr;\n" +
            "\t7.分机1002拨打abc呼出\n" +
            "\t\t呼出失败，通话被挂断；\n" +
            "\t8.分机1002拨打3001abc呼出\n" +
            "\t\t辅助3的分机4000正常响铃，接听，挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_01_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_02_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_03_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_04_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-DialPattern")
    @Story("Pattern,Trunk,Extension")
    @Description("新建呼出路由OR_Pattern1，Dial Pattern为.   ,Strip：空，Prepend：空，外线：sps，分机：分机A-1000;\n" +
            "新建呼出路由OR_Pattern2，Dial Pattern为X!   ,Strip：空，Prepend：空，外线：sip外线，分机：分机B-1001;\n" +
            "新建呼出路由OR_Pattern3，Dial Pattern为X.   ,Strip：空，Prepend：空，外线：Account，分机：分机C-1002;\n" +
            "\t5.分机1002拨打0呼出\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_05_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_06_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_07_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P2"})
    public void testOR_08_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_09_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_10_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_11_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_12_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_13_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_14_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_15_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t16.分机1004拨打11111\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_16_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_17_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t18.分机1004拨打222222\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_18_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_19_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t20.分机1004拨打9999\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_20_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_21_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t22.分机1004拨打9200123\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_22_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t23.分机1004拨打8888123\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_23_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t24.分机1004拨打9210123\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_24_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_25_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_26_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t27.分机1004拨打14050\n" +
            "\t\t辅助2的分机2000正常响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_27_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_28_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_29_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Pattern","Trunk","Extension","P3"})
    public void testOR_30_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Strip","Prepend","P2"})
    public void testOR_31_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Strip","Prepend","P3"})
    public void testOR_32_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
            "\t\t辅助2的分机2001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Strip","Prepend","P2"})
    public void testOR_33_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-DialPattern","Strip","Prepend","P2"})
    public void testOR_34_Pattern_Trunk_Extesnion() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }


}
