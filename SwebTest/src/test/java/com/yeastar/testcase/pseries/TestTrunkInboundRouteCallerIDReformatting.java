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
public class TestTrunkInboundRouteCallerIDReformatting extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestTrunkInboundRouteCallerIDReformatting() {
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
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t1、辅助2的分机2000拨打9999呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<05912000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_01_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

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
                .contains(tuple("2000<0591000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<0591000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t2、辅助2的分机2000拨打9999呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_02_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

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
                .contains(tuple("2000<550330100>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<550330100> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t3、辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_03_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

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
                .contains(tuple("2000<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<5503301> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t4、辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_04_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "5、辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_05_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

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
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t辅助2的分机2001拨打9999呼入，分机1000接听，挂断；\n" +
            "\t\t检查cdr，主叫为2001<Abc1232001>;\n" +
            "\t\t\t调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t\t6、辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_06_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", SPS, "", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSpsTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", SPS, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t7、辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_07_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").editSipTrunk(SPS,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t8、辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_08_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<2001> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t9、辅助1的分机3001拨打3000呼入，分机1000接听，主叫挂断；检查cdr，主叫为3001<0591001> ；Reason：\t3001<0591001> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_09_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

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
                .contains(tuple("3001<0591001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<0591001> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t10、辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<550330101> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_10_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

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
                .contains(tuple("3001<550330101>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<550330101> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t11、辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<5503301> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_11_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
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
                .contains(tuple("3001<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<5503301> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXX2!  ,Strip:  ,Prepend：Abc123\n" +
            "\t12、辅助1的分机3002拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3002<Abc1233002> ;\n" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<3001> ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_12_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXX2!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SIPTrunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3002" + ",[callee] 3000" + ",[trunk] " + SIPTrunk);
        pjsip.Pj_Make_Call_No_Answer(3002, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(3002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3002<Abc1233002>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3002<Abc1233002> hung up", SIPTrunk, "", "Inbound"));

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
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，挂断；\n" +
            "\t检查cdr，主叫为3001<Abc1233001>;\n" +
            "\t\t调整顺序为\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t13、辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_13_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

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
                .contains(tuple("3001<Abc1233001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<Abc1233001> hung up", SIPTrunk, "", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editSpsTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSpsTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

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
                .contains(tuple("3001<6663001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<6663001> hung up", SIPTrunk, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666\n" +
            "\t单个删除呼出模式Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t14、辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SPS", ""})
    public void testIR_14_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").editSipTrunk(SIPTrunk,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

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
                .contains(tuple("3001<6663001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "3001<6663001> hung up", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("SPS")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t15、辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<3001>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","SIP", ""})
    public void testIR_15_SIP() {
        prerequisite();
        apiUtil.editSipTrunk(SIPTrunk,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + SIPTrunk);
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
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t16、辅助3的分机4000拨打4444呼入，分机1000接听，主叫挂断；检查cdr，主叫为4000<0591000> ；Reason：\t4000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_16_Account() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<0591000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<0591000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t17、辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<550330100> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_17_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<550330100>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<550330100> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t18、辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<5503301> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_18_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<5503301> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "\t19、辅助3的分机4001拨打4444呼入，分机1000接听，被叫挂断；；检查cdr，主叫为4001<Abc1233001> ;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_19_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4001, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4001<Abc1234001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4001<Abc1234001> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "20、辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<4000> ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_20_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<4000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<4000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX0!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666\n" +
            "\t辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；\n" +
            "\t\t21、检查cdr，主叫为4000<Abc1233001>;\n" +
            "\t\t\t调整顺序为\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t\t辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<6664000>;\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_21_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX0!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<Abc1234000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<Abc1234000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XN0.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("2:[caller] 4001" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<6664000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<6664000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX0!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666\n" +
            "\t单个删除呼出模式Patterns: NXX0!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t22、辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<6664000>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_22_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").editSipTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<6664000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<6664000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("Account")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t23、辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<4000>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","Account", ""})
    public void testIR_23_ACCOUNTTRUNK() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + ACCOUNTTRUNK);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 4444" + ",[trunk] " + ACCOUNTTRUNK);
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<4000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<4000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t24、辅助2的分机2000拨打2005呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<05912000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_24_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<0591000> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t25、辅助2的分机2000拨打2005呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_25_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<550330100> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t26、辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_26_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<5503301> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "27、辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_27_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "28、辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_28_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t辅助2的分机2001拨打2005呼入，分机1000接听，挂断；\n" +
            "\t\t29、检查cdr，主叫为2001<Abc1232001>;\n" +
            "\t\t\t调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t\t辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_29_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", FXO_1 ,"", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("2:[caller] 2001" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", FXO_1 ,"", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t30、辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_30_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").editFXOTrunk(FXO_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("FXO")
    @Description("批量删除所有模式\n" +
            "\t31、辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","FXO", ""})
    public void testIR_31_FXO() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.editFXOTrunk(FXO_1,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + FXO_1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 2005" + ",[trunk] " + FXO_1);
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<2001> hung up", FXO_1 ,"", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t32、辅助2的分机2000拨打8888呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<05912000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_32_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 8888" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<0591000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t33、辅助2的分机2000拨打8888呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_33_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 8888" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<550330100> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t34、辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_34_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 8888" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<5503301> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t35、辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_35_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 8888" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "36、辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_36_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

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
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t辅助2的分机2001拨打8888呼入，分机1000接听，挂断；\n" +
            "\t\t37、检查cdr，主叫为2001<Abc1232001>;\n" +
            "\t\t\t调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t\t辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_37_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", BRI_1, "", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editSpsTrunk(BRI_1,"\"inbound_cid_list\": []").editSpsTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", BRI_1, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t38、辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_38_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").editBriTrunk(BRI_1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("BRI")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t39、辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Trunk-InboundCallerIDReformatting","BRI", ""})
    public void testIR_39_BRI() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<2001> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: .  ,Strip: 1 ,Prepend：0591\n" +
            "\t40、辅助2的分机2000拨打6666呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<05912000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_40_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 6666" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<0591000> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: !  ,Strip: 2 ,Prepend：5503301\n" +
            "\t41、辅助2的分机2000拨打6666呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_41_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 6666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<550330100> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301\n" +
            "\t42、辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_42_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 6666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<5503301> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t43、辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_43_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 6666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "44、辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_44_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2000, "666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t辅助2的分机2001拨打6666呼入，分机1000接听，挂断；\n" +
            "\t\t45、检查cdr，主叫为2001<Abc1232001>;\n" +
            "\t\t\t调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t\t\t辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_45_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 6666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<Abc1232001> hung up", E1, "", "Inbound"));

        step("调整顺序为");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW)
                .editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1},{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("2:[caller] 2001" + ",[callee] 6666" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", E1, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "\t辅助2的分机2001拨打6666呼入，分机1000接听，挂断；\n" +
            "\t单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "\t\t46、辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_46_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").editDigitalTrunk(E1,"\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 9999" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<6662001> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t47、辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_47_E1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测试！");
        }
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 6666" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2001<2001> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("GSM")
    @Description("编辑gsm外线-》Inbound Caller ID Reformatting" +
            "Patterns: X.  ,Strip: 1 ,Prepend：0591\n" +
            "\t48、辅助2的分机2000拨打33+被测设备的GSM号码呼入，分机1000接听，主叫挂断；检查cdr，主叫为辅助2的GSM号码<0591辅助2的GSM号码去掉第一位剩下的号码>，Reason：\t辅助2的GSM号码<0591辅助2的GSM号码去掉第一位剩下的号码> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_48_GSM() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM 线路 不测试！");
        }
        prerequisite();
        apiUtil.editGSMTrunk(GSM,"\"inbound_cid_list\": []").editGSMTrunk(GSM,"\"inbound_cid_list\":[{\"inbound_cid\":\"X.\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("1:login with admin,trunk: " + GSM);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 33"+DEVICE_TEST_GSM + ",[trunk] " + GSM);
        pjsip.Pj_Make_Call_No_Answer(2000, "33"+DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 150)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(90*1000);
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");//todo adapt gsm cdr
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("<05910>"+DEVICE_TEST_GSM, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<0591000> hung up", GSM, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-InboundCallerIDReformatting")
    @Story("E1")
    @Description("编辑E1外线-》Inbound Caller ID Reformatting" +
            "批量删除所有模式\n" +
            "\t49、辅助2的分机2000拨打33+被测设备的GSM号码呼入，分机1000接听，主叫挂断；检查cdr，主叫为 辅助2的GSM号码;\n" +
            "\t恢复环境需要执行这一步，不然会影响其它测试的cdr检查")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries",   "Trunk-InboundCallerIDReformatting","E1", ""})
    public void testIR_49_GSM() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM 线路 不测试！");
        }
        prerequisite();
        apiUtil.editGSMTrunk(GSM,"\"inbound_cid_list\": []").apply();

        step("1:login with admin,trunk: " + GSM);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 33"+DEVICE_TEST_GSM + ",[trunk] " + GSM);
        pjsip.Pj_Make_Call_No_Answer(2000, "33"+DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 150)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(90*1000);
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");//todo adapt gsm cdr
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(DEVICE_TEST_GSM+"<"+DEVICE_TEST_GSM+">", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), DEVICE_TEST_GSM+"<"+DEVICE_TEST_GSM+">"+" hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }


}
