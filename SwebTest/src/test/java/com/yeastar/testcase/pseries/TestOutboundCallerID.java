package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test out bound callerID
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestOutboundCallerID extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestOutboundCallerID() {
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

        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("1.新建呼出路由OutboundCallerID1,Outbound Caller ID 设置为1234567890，Pattern：61. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1000拨打616666呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为1234567890")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P2", "testOR_01_CallerID_Trunk"})
    public void testOR_01_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID1,Outbound Caller ID 设置为1234567890，Pattern：61. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID1", asList(SPS), asList("ExGroup1", "1002", "1003"), "61.", 2).
                editOutbound("OutboundCallerID1", "\"outb_cid\":\"1234567890\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 616666");
        pjsip.Pj_Make_Call_No_Answer(1000, "616666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "616666", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "1234567890"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("2.新建呼出路由OutboundCallerID2,Outbound Caller ID 设置为+123abc0，Pattern：62. ,Strip:2，选择sip外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1001拨打623001呼出\n" +
            "\t\t辅助1分机3001响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为+123abc0")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_02_CallerID_Trunk"})
    public void testOR_02_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID2,Outbound Caller ID 设置为+123abc0，Pattern：62. ,Strip:2，选择sip外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID2", asList(SIPTrunk), asList("ExGroup1", "1002", "1003"), "62.", 2).
                editOutbound("OutboundCallerID2", "\"outb_cid\":\"+123abc0\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 623001");
        pjsip.Pj_Make_Call_No_Answer(1001, "623001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "623001", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SIPTrunk, "Outbound", "+123abc0"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("3.新建呼出路由OutboundCallerID3,Outbound Caller ID 设置为{{.Ext}}，Pattern：63. ,Strip:2，选择account外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1002拨打63333呼出\n" +
            "\t\t辅助3分机4000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为1002")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P2", "testOR_03_CallerID_Trunk"})
    public void testOR_03_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID3,Outbound Caller ID 设置为{{.Ext}}，Pattern：63. ,Strip:2，选择account外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID3", asList(ACCOUNTTRUNK), asList("ExGroup1", "1002", "1003"), "63.", 2).
                editOutbound("OutboundCallerID3", "\"outb_cid\":\"{{.Ext}}\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 63333");
        pjsip.Pj_Make_Call_No_Answer(1002, "63333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "63333", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "1002"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("新建呼出路由OutboundCallerID4,Outbound Caller ID 设置为+123abc0{{.Ext}}，Pattern：64. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；\n" +
            "\t4.分机1000拨打646666呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为+123abc01000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_04_CallerID_Trunk"})
    public void testOR_04_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID4,Outbound Caller ID 设置为+123abc0{{.Ext}}，Pattern：64. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID4", asList(SPS), asList("ExGroup1", "1002", "1003"), "64.", 2).
                editOutbound("OutboundCallerID4", "\"outb_cid\":\"+123abc0{{.Ext}}\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 646666");
        pjsip.Pj_Make_Call_No_Answer(1000, "646666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "646666", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "+123abc01000"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("新建呼出路由OutboundCallerID4,Outbound Caller ID 设置为+123abc0{{.Ext}}，Pattern：64. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；\n" +
            "\t5.分机1002拨打646666呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为+123abc01002")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_05_CallerID_Trunk"})
    public void testOR_05_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID4,Outbound Caller ID 设置为+123abc0{{.Ext}}，Pattern：64. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID4", asList(SPS), asList("ExGroup1", "1002", "1003"), "64.", 2).
                editOutbound("OutboundCallerID4", "\"outb_cid\":\"+123abc0{{.Ext}}\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 646666");
        pjsip.Pj_Make_Call_No_Answer(1002, "646666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "646666", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", SPS, "Outbound", "+123abc01002"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("6.新建呼出路由OutboundCallerID5,Outbound Caller ID 设置为{{.Ext}}+123abc0，Pattern：65. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1003拨打655555呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示为1003+123abc0")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_06_CallerID_Trunk"})
    public void testOR_06_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID5,Outbound Caller ID 设置为{{.Ext}}+123abc0，Pattern：65. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID5", asList(SPS), asList("ExGroup1", "1002", "1003"), "65.", 2).
                editOutbound("OutboundCallerID5", "\"outb_cid\":\"{{.Ext}}+123abc0\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 655555");
        pjsip.Pj_Make_Call_No_Answer(1003, "655555", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "655555", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound", "1003+123abc0"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("7.新建呼出路由OutboundCallerID6,Outbound Caller ID 设置为{{.Ext}}{{.Ext}}505{{.Ext}}123，Pattern：66. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1000拨打66666呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示100010005051000123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_07_CallerID_Trunk"})
    public void testOR_07_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID6,Outbound Caller ID 设置为{{.Ext}}{{.Ext}}505{{.Ext}}123，Pattern：66. ,Strip:2，选择sps外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID6", asList(SPS), asList("ExGroup1", "1002", "1003"), "66.", 2).
                editOutbound("OutboundCallerID6", "\"outb_cid\":\"{{.Ext}}{{.Ext}}505{{.Ext}}123\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 66666");
        pjsip.Pj_Make_Call_No_Answer(1000, "66666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "66666", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "100010005051000123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("8.新建呼出路由OutboundCallerID7,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：67. ,Strip:2，选择FXO外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1001拨打672000呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示1001abc+1001123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_08_CallerID_Trunk"})
    public void testOR_08_CallerID_Trunk() {
        if (FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OutboundCallerID7,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：67. ,Strip:2，选择FXO外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID7", asList(FXO_1), asList("ExGroup1", "1002", "1003"), "67.", 2).
                editOutbound("OutboundCallerID7", "\"outb_cid\":\"{{.Ext}}abc+{{.Ext}}123\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 672000");
        pjsip.Pj_Make_Call_No_Answer(1001, "672000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "672000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", FXO_1, "Outbound", "1001abc+1001123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("9.新建呼出路由OutboundCallerID8,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：68. ,Strip:2，选择BRI外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1002拨打688888外线呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示1002abc+1002123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_09_CallerID_Trunk"})
    public void testOR_09_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID8,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：68. ,Strip:2，选择BRI外线，分机选择ExGroup1,1002,1003");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID8", asList(SPS), asList("ExGroup1", "1002", "1003"), "68.", 2).
                editOutbound("OutboundCallerID8", "\"outb_cid\":\"{{.Ext}}abc+{{.Ext}}123\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 688888");
        pjsip.Pj_Make_Call_No_Answer(1002, "688888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "688888", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", SPS, "Outbound", "1002abc+1002123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("10.新建呼出路由OutboundCallerID9,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：69. ,Strip:2，选择E1外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1003拨打699999外线呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示1003abc+1003123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_10_CallerID_Trunk"})
    public void testOR_10_CallerID_Trunk() {
        prerequisite();
        step("新建呼出路由OutboundCallerID9,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：69. ,Strip:2，选择E1外线，分机选择ExGroup1,1002,1003；");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID9", asList(SPS), asList("ExGroup1", "1002", "1003"), "69.", 2).
                editOutbound("OutboundCallerID9", "\"outb_cid\":\"{{.Ext}}abc+{{.Ext}}123\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 699999");
        pjsip.Pj_Make_Call_No_Answer(1003, "699999", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "699999", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound", "1003abc+1003123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-OutboundCallerID")
    @Story("OutboundCallerID,Trunk")
    @Description("11.新建呼出路由OutboundCallerID0,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：60. ,Strip:2，选择GSM外线，分机选择ExGroup1,1002,1003；\n" +
            "\t分机1000拨打60+辅助2的GSM号码呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr,其中Outbound Caller ID显示1000abc+1000123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-OutboundCallerID", "OutboundCallerID", "Trunk", "P3", "testOR_11_CallerID_Trunk"})
    public void testOR_11_CallerID_Trunk() {
        if (GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "GSM 线路 不测试！");
        }
        prerequisite();
        step("11.新建呼出路由OutboundCallerID0,Outbound Caller ID 设置为{{.Ext}}abc+{{.Ext}}123，Pattern：60. ,Strip:2，选择GSM外线，分机选择ExGroup1,1002,1003");
        apiUtil.deleteAllOutbound().createOutbound("OutboundCallerID0", asList(GSM), asList("ExGroup1", "1002", "1003"), "60.", 2).
                editOutbound("OutboundCallerID0", "\"outb_cid\":\"{{.Ext}}abc+{{.Ext}}123\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 60" + DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, 60 + DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "60" + DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound", "1000abc+1000123"));

        softAssertPlus.assertAll();
    }
}