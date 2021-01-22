package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.swebtest.pobject.Settings.PBX.General.API.API;
import com.yeastar.untils.APIObject.TrunkObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.INBOUND;
import static org.assertj.core.api.Assertions.tuple;


/**
 * @program: SwebTest
 * @description:
 * @author: xlq@yeastar.com
 * @create: 2020/12/17
 */

@Log4j2
public class TestTrunkInboundCallerIDReformatting extends TestCaseBaseNew {

    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;
    private String answer = "ANSWERED";

    /**
     * 前提环境
     */
    public void prerequisite() {

        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            apiUtil.loginWeb("0", EXTENSION_PASSWORD_NEW);
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
            initOutbound();
            initIVR();
            initInbound();
            runRecoveryEnvFlag = registerAllExtensions();
        }
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "1.Patterns: .  ,Strip: 1 ,Prepend：0591" +
            "辅助2的分机2000拨打9999呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat01_sps() {
        prerequisite();

        step("[编辑sps外线-》Inbound Caller ID Reformatting]" +
                "Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打9999呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", Extension_1000.toString(), answer, "2000<0591000> hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "2.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助2的分机2000拨打9999呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat02_sps() {
        prerequisite();

        step("[编辑sps外线-》Inbound Caller ID Reformatting]" +
                "2.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打9999呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", Extension_1000.toString(), answer, ext_1000 + " hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat03_sps() {
        prerequisite();

        step("编辑sps外线-》Inbound Caller ID Reformatting" +
                "[3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301]");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", Extension_1000.toString(), answer, "2000<5503301> hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n" +
            "辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat04_sps() {
        prerequisite();

        step("编辑sps外线-》Inbound Caller ID Reformatting" +
                "[4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123]");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("辅助2的分机2000拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>");
        pjsip.Pj_Make_Call_No_Answer(2000, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", SPS, "", INBOUND.toString()))
                .contains(tuple("2000<2000>", Extension_1000.toString(), answer, "2000<2000> hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "5.辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;" +
            "调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat05_sps() {
        prerequisite();

        step("[编辑sps外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
                "辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("5.辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]：1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("调整顺序为\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", SPS, "", INBOUND.toString()))
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat06_sps() {
        prerequisite();

        step("编辑sps外线-》Inbound Caller ID Reformatting" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":2}]").apply();

        step("辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SPS Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sps外线-》Inbound Caller ID Reformatting" +
            "7.批量删除所有模式" +
            "辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SPS", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat07_sps() {
        prerequisite();

        step("[编辑sps外线-》Inbound Caller ID Reformatting]+Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("7.批量删除所有模式");
        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        step("辅助2的分机2001拨打9999呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "9999", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editSipTrunk(SPS, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", Extension_1000.toString(), answer, "2001<2001> hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "8.Patterns: .  ,Strip: 1 ,Prepend：0591")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat08_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "8.Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助1的分机3001拨打3000呼入，分机1000接听，主叫挂断；检查cdr，主叫为3001<0591001> ；Reason：\t3001<0591001> hung up");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<0591001>", Extension_1000.toString(), answer, "3001<0591001> hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "9.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<550330101> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat09_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "9.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<550330101> ；");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<550330101>", Extension_1000.toString(), answer, "3001<550330101> hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "10.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<5503301> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat10_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "10.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<5503301> ；");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<5503301>", Extension_1000.toString(), answer, "3001<5503301> hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "11.Patterns: NXX2!  ,Strip:  ,Prepend：Abc123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat11_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "11.Patterns: NXX2!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXX2!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助1的分机3002拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3002<Abc1233002> ;");
        pjsip.Pj_Make_Call_No_Answer(3002, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3002);

        step("辅助1的分机3001拨打3000呼入，分机1000接听，被叫挂断；检查cdr，主叫为3001<3001> ");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3002<Abc1233002>", Extension_1000.toString(), answer, "3002<Abc1233002> hung up", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(), answer, Extension_3001.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666" +
            "12.辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<Abc1233001>;" +
            "调整顺序为\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat12_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 3001  ,Strip:  ,Prepend：666");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("12.辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<Abc1233001>;");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        step("调整顺序为\n" +
                "Patterns: 3001  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();
        step("辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<Abc1233001>", Extension_1000.toString(), answer, "3001<Abc1233001> hung up", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple("3001<6663001>", Extension_1000.toString(), answer, "3001<6663001> hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 3001  ,Strip:  ,Prepend：666" +
            "13.单个删除呼出模式Patterns: NXX1!  ,Strip:  ,Prepend：Abc123" +
            "辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat13_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 3001  ,Strip:  ,Prepend：666");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        step("13.单个删除呼出模式Patterns: NXX1!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"3001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        step("辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<6663001>;");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<6663001>", Extension_1000.toString(), answer, "3001<6663001> hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑sip外线-》Inbound Caller ID Reformatting" +
            "14.批量删除所有模式")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_REGISTER", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat14_sip() {
        prerequisite();
        step("[编辑sip外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 3001  ,Strip:  ,Prepend：666" +
                "14.批量删除所有模式");
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        step("辅助1的分机3001拨打3000呼入，分机1000接听，挂断；检查cdr，主叫为3001<3001>;");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(3001);

        apiUtil.editSipTrunk(SIPTrunk, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), Extension_1000.toString(), answer, Extension_3001.toString() + " hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    //    以下开始测试Account外线
    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "8.Patterns: .  ,Strip: 1 ,Prepend：0591" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，主叫挂断；检查cdr，主叫为4000<0591000> ；Reason：\t4000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat15_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "8.Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助3的分机4000拨打4444呼入，分机1000接听，主叫挂断；检查cdr，主叫为4000<0591000> ；Reason：\t4000<0591000> hung up");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<0591000>", Extension_1000.toString(), answer, "4000<0591000> hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "9.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<550330100> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat16_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "9.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<550330100> ； ；");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<550330100>", Extension_1000.toString(), answer, "4000<550330100> hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "10.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<5503301> ；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat17_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "10.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<5503301> ；");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<5503301>", Extension_1000.toString(), answer, "4000<5503301> hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "18.Patterns: NXX1!  ,Strip:  ,Prepend：Abc123" +
            "辅助3的分机4001拨打4444呼入，分机1000接听，被叫挂断；；检查cdr，主叫为4001<Abc1233001> ;" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<4000> ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat18_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "18.Patterns: NXX1!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助3的分机4001拨打4444呼入，分机1000接听，被叫挂断；；检查cdr，主叫为4001<Abc1233001> ;");
        pjsip.Pj_Make_Call_No_Answer(4001, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4001);

        step("辅助3的分机4000拨打4444呼入，分机1000接听，被叫挂断；检查cdr，主叫为4000<4000> ");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4001<Abc1234001>", Extension_1000.toString(), answer, "4001<Abc1234001> hung up", ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple(Extension_4000.toString(), Extension_1000.toString(), answer, Extension_4000.toString() + " hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666" +
            "12.辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<Abc1234000>;" +
            "调整顺序为\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<6664000>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat19_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX0!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 4000  ,Strip:  ,Prepend：666");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX0!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("12.辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<Abc1234000>;");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        step("调整顺序为\n" +
                "Patterns: 4000  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX0!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXX0!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();
        step("辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<6664000>;");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<Abc1234000>", Extension_1000.toString(), answer, "4000<Abc1234000> hung up", ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple("4000<6664000>", Extension_1000.toString(), answer, "4000<6664000> hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("Account->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 4000  ,Strip:  ,Prepend：666" +
            "13.单个删除呼出模式Patterns: NXX1!  ,Strip:  ,Prepend：Abc123" +
            "辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<6664000>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat20_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 4000  ,Strip:  ,Prepend：666");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXX1!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        step("13.单个删除呼出模式Patterns: NXX1!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"4000\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        step("辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<6664000>;");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<6664000>", Extension_1000.toString(), answer, "4000<6664000> hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("SIP Trunk->Inbound Caller ID Reformatting")
    @Description("编辑Account外线-》Inbound Caller ID Reformatting" +
            "14.批量删除所有模式")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "SIP_ACCOUNT", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat21_Account() {
        prerequisite();
        step("[编辑Account外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXX1!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 4000  ,Strip:  ,Prepend：666" +
                "14.批量删除所有模式");
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        step("辅助3的分机4000拨打4444呼入，分机1000接听，挂断；检查cdr，主叫为4000<4000>;");
        pjsip.Pj_Make_Call_No_Answer(4000, "4444", DEVICE_ASSIST_3, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(4000);

        apiUtil.editSipTrunk(ACCOUNTTRUNK, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_4000.toString(), Extension_1000.toString(), answer, Extension_4000.toString() + " hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

// 接下去开始测试FXO外线

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "1.Patterns: .  ,Strip: 1 ,Prepend：0591" +
            "辅助2的分机2000拨打2005呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat22_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("[编辑fxo外线-》Inbound Caller ID Reformatting]" +
                "Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打2005呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", Extension_1000.toString(), answer, "2000<0591000> hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "2.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助2的分机2000拨打2005呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat23_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("[编辑fxo外线-》Inbound Caller ID Reformatting]" +
                "2.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打2005呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", Extension_1000.toString(), answer, ext_1000 + " hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat24_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("编辑fxo外线-》Inbound Caller ID Reformatting" +
                "[3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301]");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", Extension_1000.toString(), answer, "2000<5503301> hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n" +
            "辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat25_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("编辑fxo外线-》Inbound Caller ID Reformatting" +
                "[4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123]");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("辅助2的分机2000拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", FXO_1, "", INBOUND.toString()))
                .contains(tuple("2000<2000>", Extension_1000.toString(), answer, "2000<2000> hung up", FXO_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "5.辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;" +
            "调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat26_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("[编辑fxo外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
                "辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("5.辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]：1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("调整顺序为\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", FXO_1, "", INBOUND.toString()))
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", FXO_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat27_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("编辑fxo外线-》Inbound Caller ID Reformatting" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":2}]").apply();

        step("辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", FXO_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("FXO TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑fxo外线-》Inbound Caller ID Reformatting" +
            "7.批量删除所有模式" +
            "辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "FXO", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat28_fxo() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            step("FXO外线未配置");
            return;
        }
        prerequisite();

        step("[编辑fxo外线-》Inbound Caller ID Reformatting]+Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("7.批量删除所有模式");
        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        step("辅助2的分机2001拨打2005呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editFXOTrunk(FXO_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", Extension_1000.toString(), answer, "2001<2001> hung up", FXO_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    //    接下去开始测试BRI外线
    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "1.Patterns: .  ,Strip: 1 ,Prepend：0591" +
            "辅助2的分机2000拨打8888呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat29_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }

        prerequisite();

        step("[编辑bri外线-》Inbound Caller ID Reformatting]" +
                "Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打8888呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up");
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", Extension_1000.toString(), answer, "2000<0591000> hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "2.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助2的分机2000拨打8888呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat30_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("[编辑bri外线-》Inbound Caller ID Reformatting]" +
                "2.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打8888呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>");
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", Extension_1000.toString(), answer, ext_1000 + " hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat31_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("编辑bri外线-》Inbound Caller ID Reformatting" +
                "[3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301]");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>");
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", Extension_1000.toString(), answer, "2000<5503301> hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n" +
            "辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat32_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("编辑bri外线-》Inbound Caller ID Reformatting" +
                "[4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123]");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("辅助2的分机2000拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>");
        pjsip.Pj_Make_Call_No_Answer(2000, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", BRI_1, "", INBOUND.toString()))
                .contains(tuple("2000<2000>", Extension_1000.toString(), answer, "2000<2000> hung up", BRI_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "5.辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;" +
            "调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat33_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("[编辑bri外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
                "辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("5.辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]：1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("调整顺序为\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", BRI_1, "", INBOUND.toString()))
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", BRI_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat34_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("编辑bri外线-》Inbound Caller ID Reformatting" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":2}]").apply();

        step("辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", BRI_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("BRI TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑bri外线-》Inbound Caller ID Reformatting" +
            "7.批量删除所有模式" +
            "辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "BRI", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat35_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();

        step("[编辑bri外线-》Inbound Caller ID Reformatting]+Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("7.批量删除所有模式");
        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        step("辅助2的分机2001拨打8888呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "8888", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editBriTrunk(BRI_1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", Extension_1000.toString(), answer, "2001<2001> hung up", BRI_1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    //    接下去开始修改E1外线
    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "1.Patterns: .  ,Strip: 1 ,Prepend：0591" +
            "辅助2的分机2000拨打6666呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat36_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("[编辑e1外线-》Inbound Caller ID Reformatting]" +
                "Patterns: .  ,Strip: 1 ,Prepend：0591");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\".\",\"strip\":\"1\",\"prepend\":\"0591\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打6666呼入，分机1000接听，主叫挂断；检查cdr，主叫为2000<0591000>，Reason：\t2000<0591000> hung up");
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<0591000>", Extension_1000.toString(), answer, "2000<0591000> hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "2.Patterns: !  ,Strip: 2 ,Prepend：5503301" +
            "辅助2的分机2000拨打6666呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat37_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("[编辑e1外线-》Inbound Caller ID Reformatting]" +
                "2.Patterns: !  ,Strip: 2 ,Prepend：5503301");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"!\",\"strip\":\"2\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打6666呼入，分机1000接听，被叫挂断；检查cdr，主叫为2000<550330100>");
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<550330100>", Extension_1000.toString(), answer, ext_1000 + " hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301" +
            "辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat38_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("编辑e1外线-》Inbound Caller ID Reformatting" +
                "[3.Patterns: [12-4].  ,Strip: 4 ,Prepend：5503301]");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"[12-4].\",\"strip\":\"4\",\"prepend\":\"5503301\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<5503301>");
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<5503301>", Extension_1000.toString(), answer, "2000<5503301> hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;\n" +
            "辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat39_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("编辑e1外线-》Inbound Caller ID Reformatting" +
                "[4.Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123]");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("辅助2的分机2000拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2000<2000>");
        pjsip.Pj_Make_Call_No_Answer(2000, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("【来电分机响铃】：1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", E1, "", INBOUND.toString()))
                .contains(tuple("2000<2000>", Extension_1000.toString(), answer, "2000<2000> hung up", E1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "5.辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;" +
            "调整顺序为\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat40_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("[编辑e1外线-》Inbound Caller ID Reformatting]" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
                "辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("5.辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<Abc1232001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]：1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        step("调整顺序为\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666\n" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":2},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":3},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":1}]").apply();

        step("辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<Abc1232001>", Extension_1000.toString(), answer, "2001<Abc1232001> hung up", E1, "", INBOUND.toString()))
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", E1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
            "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
            "Patterns: 2001  ,Strip:  ,Prepend：666" +
            "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123" +
            "辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat41_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("编辑e1外线-》Inbound Caller ID Reformatting" +
                "Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666" +
                "6.单个删除呼出模式Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":2}]").apply();

        step("辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<6662001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[来电显示]:1000分机来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();


        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<6662001>", Extension_1000.toString(), answer, "2001<6662001> hung up", E1, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("E1 TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑e1外线-》Inbound Caller ID Reformatting" +
            "7.批量删除所有模式" +
            "辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk", "E1", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat42_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();

        step("[编辑e1外线-》Inbound Caller ID Reformatting]+Patterns: XNZ.  ,Strip:  ,Prepend：888\n" +
                "Patterns: NXXZ!  ,Strip:  ,Prepend：Abc123\n" +
                "Patterns: 2001  ,Strip:  ,Prepend：666");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[{\"inbound_cid\":\"XNZ.\",\"strip\":\"\",\"prepend\":\"888\",\"pos\":1},{\"inbound_cid\":\"NXXZ!\",\"strip\":\"\",\"prepend\":\"Abc123\",\"pos\":2},{\"inbound_cid\":\"2001\",\"strip\":\"\",\"prepend\":\"666\",\"pos\":3}]").apply();

        step("7.批量删除所有模式");
        apiUtil.editDigitalTrunk(E1, "\"inbound_cid_list\":[]").apply();

        step("辅助2的分机2001拨打6666呼入，分机1000接听，挂断；检查cdr，主叫为2001<2001>;");
        pjsip.Pj_Make_Call_No_Answer(2001, "6666", DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[分机1000来电响铃]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(3000);
        pjsip.Pj_hangupCall(2001);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", Extension_1000.toString(), answer, "2001<2001> hung up", E1, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

//    接下去开始测试GSM外线

    @Epic("P_Series")
    @Feature("Trunk-Inbound Caller ID Reformatting")
    @Story("GSM TRUNK->Inbound Caller ID Reformatting")
    @Description("编辑gsm外线-》Inbound Caller ID Reformatting" +
            "43.Patterns: X.  ,Strip: 1 ,Prepend：0591" +
            "辅助2的分机2000拨打33+被测设备的GSM号码呼入，分机1000接听，主叫挂断；检查cdr，主叫为辅助2的GSM号码<0591辅助2的GSM号码去掉第一位剩下的号码>，Reason：\t辅助2的GSM号码<0591辅助2的GSM号码去掉第一位剩下的号码> hung up" +
            "44.批量删除所有模式")

    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "GSM", "Trunk", "InboundCallerIDReformatting"})
    public void testTrunkCIDFormat43_gsm() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            step("GSM外线未配置");
            return;
        }
        prerequisite();
        step("[编辑gsm外线-》Inbound Caller ID Reformatting]" +
                "43.Patterns: X.  ,Strip: 1 ,Prepend：0595" +
                "");
        apiUtil.editGSMTrunk(GSM, "\"inbound_cid_list\":[{\"inbound_cid\":\"X.\",\"strip\":\"1\",\"prepend\":\"0595\",\"pos\":1}]").apply();

        step("辅助2的分机2000拨打33+被测设备的GSM号码呼入，分机1000接听，主叫挂断；检查cdr，主叫为辅助2的GSM号码<0595辅助2的GSM号码去掉第一位剩下的号码>，Reason：\t辅助2的GSM号码<0595辅助2的GSM号码去掉第一位剩下的号码> hung up");
        pjsip.Pj_Make_Call_No_Answer(2000, "33" + DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 30)).as("分机1000来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
//        GSM通话设置超过1分钟，通话太短，太频繁容易被锁定
        sleep(80000);
        pjsip.Pj_hangupCall(2000);

        step("44.批量删除所有模式");
        apiUtil.editGSMTrunk(GSM, "\"inbound_cid_list\":[]").apply();
        step("辅助2的分机2000拨打33+被测设备的GSM号码呼入，分机1000接听，主叫挂断；检查cdr，主叫为 辅助2的GSM号码;");
        pjsip.Pj_Make_Call_No_Answer(2000, "33" + DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 30)).as("分机1000来电响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
//        GSM通话设置超过1分钟，通话太短，太频繁容易被锁定
        sleep(80000);
        pjsip.Pj_hangupCall(2000);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(DEVICE_ASSIST_GSM + "<0595" + DEVICE_ASSIST_GSM.substring(1) + ">", Extension_1000.toString(), answer, DEVICE_ASSIST_GSM + "<0595" + DEVICE_ASSIST_GSM.substring(1) + ">" + " hung up", GSM, "", INBOUND.toString()))
                .contains(tuple(DEVICE_ASSIST_GSM + "<" + DEVICE_ASSIST_GSM + ">", Extension_1000.toString(), answer, DEVICE_ASSIST_GSM + "<" + DEVICE_ASSIST_GSM + ">" + " hung up", GSM, "", INBOUND.toString()));

        softAssertPlus.assertAll();

    }

}
