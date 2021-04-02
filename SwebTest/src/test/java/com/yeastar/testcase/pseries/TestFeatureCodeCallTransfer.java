package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.swebtest.tools.pjsip.PjsipApp;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.*;
import static com.yeastar.untils.CDRObject.STATUS.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test FeatureCode CallTransfer
 * @author: lhr@yeastar.com
 * @create: 2021/1/25
 */
@Log4j2
public class TestFeatureCodeCallTransfer extends TestCaseBaseNew {


    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestFeatureCodeCallTransfer() {
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
    }
    private void prerequisite() {
        long startTime = System.currentTimeMillis();
        if (isDebugInitExtensionFlag) {
            apiUtil.editFeatureCode("\"transfer_digit_timeout\":8").apply();
            apiUtil.editFeatureCode("\"attend_transfer_timeout\":15").apply();
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

            apiUtil.editFeatureCode("\"transfer_digit_timeout\":8").apply();
            apiUtil.editFeatureCode("\"attend_transfer_timeout\":15").apply();

            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:\r" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }


    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
             "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机A挂断通话；" +
            "分机BC保持通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer01"})
    public void testFCCT01_AttendedTransfer()
    {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" );
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；" );
        pjsip.Pj_Send_Dtmf(1000,"*31002#");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 30)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        step("分机C接听后，等待3秒，分机A挂断通话；");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("4.[通话校验] 1000 hold").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("4.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("4.[通话校验] 1002预期通话中").isEqualTo(TALKING);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        step("分机BC保持通话，分机B挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("5.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("5.[通话校验] 1002预期通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);

        assertStep("检查cdr");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" attended transferred , "+Extension_1001.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机A挂断通话；" +
            "分机BC保持通话，分机C挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer02"})
    public void testFCCT02_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*31002#");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 30)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        step("分机C接听后，等待3秒，分机A挂断通话；");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("4.[通话校验] 1000 hold").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("4.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("4.[通话校验] 1002预期通话中").isEqualTo(TALKING);
        sleep(10000);
        pjsip.Pj_hangupCall(1000);

        step("分机BC保持通话，分机C挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("5.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("5.[通话校验] 1002预期通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("检查cdr");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" attended transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机C挂断通话；" +
            "分机AB恢复通话，分机A挂断通话，检查cdrr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer03"})
    public void testFCCT03_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" );
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*31002#");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        step("分机C接听后，等待3秒，分机C挂断通话；");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("4.[通话校验] 1000 hold").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("4.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("4.[通话校验] 1002预期通话中").isEqualTo(TALKING);
        sleep(3000);
        pjsip.Pj_hangupCall(1002);

        step("分机AB恢复通话，分机A挂断通话");
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);
        assertStep("检查cdr");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1002.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃，不接听" +
            "等待15秒后，分机C停止响铃，分机AB恢复通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer04"})
    public void testFCCT04_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*31002#");

        step("分机C保持响铃，不接听");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        sleep(18000);
        step("等待15秒后，分机C停止响铃，分机AB恢复通话，分机B挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("4.[通话校验] 1002挂断").isEqualTo(HUNGUP);

        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("5.[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("5.[通话校验] 1001通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(), NO_ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃3秒后，分机A挂断通话" +
            "分机C继续响铃，应答，BC保持通话，分机B挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer05"})
    public void testFCCT05_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*31002#");

        step("分机C保持响铃3秒后，分机A挂断通话" );
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);
        step("分机C继续响铃，应答，BC保持通话，分机B挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("4.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("5.[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("5.[通话校验] 1002通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1002.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " attended transferred , "+Extension_1001.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃3秒后，分机B挂断通话" +
            "通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer06"})
    public void testFCCT06_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*31002#");

        step("分机C保持响铃3秒后，分机B挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1001);

        step("通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as(".[通话校验] 1000挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),NO_ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机B挂断通话；" +
            "分机AC保持通话，分机A挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer07"})
    public void testFCCT07_AttendedTransfer()
    {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C接听后，等待3秒，分机B挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1002);
        sleep(3000);

        pjsip.Pj_hangupCall(1001);

        step("分机AC保持通话，分机A挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("4.[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("4.[通话校验] 1002通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " attended transferred , "+Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机B挂断通话；" +
            "分机AC保持通话，分机C挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("此处若校验失败，那么就是跑自动化出来的cdr有问题导致校验失败")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer08"})
    public void testFCCT08_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C接听后，等待3秒，分机B挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(3000);
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("4.[通话校验] 1001通话中").isEqualTo(HUNGUP);

        step("分机AC保持通话，分机C挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("5.[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("5.[通话校验] 1002通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " attended transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C接听后，等待3秒，分机C挂断通话；" +
            "分机AB恢复通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer09"})
    public void testFCCT09_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C接听后，等待3秒，分机C挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as(".[通话校验] 1002挂断").isEqualTo(HUNGUP);

        step("分机AB恢复通话，分机B挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);;
        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1001.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃，不接听" +
            "等待15秒后，分机C停止响铃，分机AB恢复通话，分机A挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer10"})
    public void testFCCT10_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C保持响铃，不接听");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        step("等待15秒后，分机C停止响铃，分机AB恢复通话，分机A挂断通话，检查cdr");
        sleep(15000);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as(".[通话校验] 1002挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() +" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),NO_ANSWER.toString(), Extension_1001.toString() +" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃3秒后，分机A挂断通话" +
            "通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("此处若校验失败，那么就是跑自动化出来的cdr有问题导致校验失败")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer11"})
    public void testFCCT11_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C保持响铃3秒后，分机A挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);

        step("通话被挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("4.[通话校验] 1000挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("4.[通话校验] 1001挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("4.[通话校验] 1002挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() +" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),NO_ANSWER.toString(), Extension_1001.toString() +" called "+Extension_1002.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" +
            "分机B拨打*31002#将通话转移给分机C-1002；" +
            "分机C保持响铃3秒后，分机B挂断通话" +
            "分机C继续响铃，应答，AC保持通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT01_AttendedTransfer12"})
    public void testFCCT12_AttendedTransfer()
    {
        prerequisite();
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("1.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("1.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("2.[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("2.[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B拨打*31002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1001,"*31002#");

        step("分机C保持响铃3秒后，分机B挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1001);

        step("分机C继续响铃，应答，AC保持通话，分机C挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1001.toString() + " attended transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机1005挂断通话； " +
            "分机B与外线保持通话，分机B挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT13_AttendedTransfer"})
    public void testFCCT13_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("1.[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("1.[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("2.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机B与外线保持通话，分机B挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("3.[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("3.[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1001.toString()+" hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机1005挂断通话； " +
            "分机B与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT14_AttendedTransfer"})
    public void testFCCT14_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("1.[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("1.[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B接听后，等待3秒，分机1005挂断通话 ");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("2.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机B与外线保持通话，外线挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("3.[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("3.[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_2000.toString()+" hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机B挂断通话； " +
            "分机1005与外线恢复通话，分机1005挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT15_AttendedTransfer"})
    public void testFCCT15_AttendedTransfer()
    {
        prerequisite();

        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();
        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as(".[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B接听后，等待3秒，分机B挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        sleep(3000);
        pjsip.Pj_hangupCall(1001);

        step("分机1005与外线恢复通话，分机1005挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_1005.toString() + " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃，不接听 " +
            "等待15秒后，分机B停止响铃，分机1005与外线恢复通话，外线挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT16_AttendedTransfer"})
    public void testFCCT16_AttendedTransfer()
    {
        prerequisite();

        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as(".[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B保持响铃，不接听 ");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);

        step("等待15秒后，分机B停止响铃，分机1005与外线恢复通话，外线挂断通话，检查cdr");
        sleep(15000);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as(".[通话校验] 1001挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃3秒后，外线挂断通话 " +
            "通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT17_AttendedTransfer"})
    public void testFCCT17_AttendedTransfer()
    {
        prerequisite();


        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("1.[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("1.[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B保持响铃3秒后，外线挂断通话 ");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("2.[通话校验] 1001响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        step("通话被挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("3.[通话校验] 1001挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "" , "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃3秒后，分机1005挂断通话 " +
            "分机B继续响铃，应答，分机B与外线正常通话，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT18_AttendedTransfer"})
    public void testFCCT18_AttendedTransfer()
    {
        prerequisite();


        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as(".[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");

        step("分机B保持响铃3秒后，分机1005挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机B继续响铃，应答，分机B与外线正常通话，挂断");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1001.toString()+" hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*36200#将通话转移给IVR0 " +
            "asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机1005按0，分机1000响铃，接听，分机1005挂断；" +
            "分机A挂断；检查cdr ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT19_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as(".[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext");
        thread.start();

        step("分机1005拨打*36200#将通话转移给IVR0");
        pjsip.Pj_Send_Dtmf(1005,"*36200#");

        step("asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机1005按0，分机1000响铃，接听，分机1005挂断 ");
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 400){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("asterisk后台检测到播放提示音“ivr-greeting-dial-ext").isTrue();

        pjsip.Pj_Send_Dtmf(1005,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("分机A挂断；检查cdr ");
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(4)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), IVR0_6200.toString(),   ANSWER.toString(), Extension_1005.toString() + " called Extension", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1000.toString(),   ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1000.toString()+" hung up", SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过Account外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*36300#将通话转移给响铃组RingGroup0 " +
            "分机1000、1001、1003同时响铃，分机1003应答后，分机1005挂断；分机1003与外线保持通话，外线挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","IVR","testFCCT20_AttendedTransfer"})
    public void testFCCT20_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过Account外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(4000,"441005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 30)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 30)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*36300#将通话转移给响铃组RingGroup0 ");
        pjsip.Pj_Send_Dtmf(1005,"*36300#");

        step("分机1000、1001、1003同时响铃，分机1003应答后，分机1005挂断；分机1003与外线保持通话，外线挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 30)).as("3.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as(".[通话校验] 1003响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 30)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 30)).as(".[通话校验] 4000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(4000);
        pjsip.Pj_Hangup_All();
        sleep(5000);
        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Account_6700.toString(), Extension_1005.toString(),   ANSWER.toString(), Account_6700.toString() + " called "+Extension_1005.toString(), ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), RINGGROUP0_6300.toString(),  ANSWER.toString(), RINGGROUP0_6300.toString() + " connected", "", "", INTERNAL.toString()))
                .contains(tuple(Account_6700.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Account_6700.toString()+" hung up", ACCOUNTTRUNK, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sip外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*36400#将通话转移给Queue0 " +
            "分机1000、1001、1003、1004同时响铃，分机1004应答，分机1005挂断；1004与外线保持通话，分机1004挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT21_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sip外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(3001,"3000");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*36400#将通话转移给Queue0 ");
        pjsip.Pj_Send_Dtmf(1005,"*36400#");

        step("分机1000、1001、1003、1004同时响铃，分机1004应答，分机1005挂断；1004与外线保持通话，分机1004挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("3.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as(".[通话校验] 1004响铃").isEqualTo(RING);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1004);
        pjsip.Pj_hangupCall(1005);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as(".[通话校验] 1004通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(4)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_3001.toString() + " called "+Extension_1005.toString(), SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), QUEUE0_6400.toString(),   ANSWER.toString(), QUEUE0_6400.toString() + " connected", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1004.toString(),   ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1004.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1004.toString()+" hung up", SIPTrunk, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*313001#将通话转移给外部号码：辅助1的3001 " +
            "辅助1的3001响铃，接听，分机1005挂断；sps外线与3001保持通话，3001挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT22_AttendedTransfer()
    {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*313001#将通话转移给外部号码：辅助1的3001 ");
        pjsip.Pj_Send_Dtmf(1005,"*313001#");

        step("辅助1的3001响铃，接听，分机1005挂断；sps外线与3001保持通话，3001挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("3.[通话校验] 3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        pjsip.Pj_hangupCall(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as(".[通话校验] 3001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), "13001",   ANSWER.toString(), Extension_1005.toString() +" attended transferred ,13001 hung up", SPS, SIPTrunk, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), "13001",   ANSWER.toString(), Extension_1005.toString() + " called 13001", "", SIPTrunk, OUTBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过sps外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*36500#将通话转移到会议室6500 " +
            "分机1005挂断通话，检查会议室6500存在一路通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT23_AttendedTransfer() throws IOException, JSchException {
        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过sps外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"991005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*36500#将通话转移到会议室6500 ");
        pjsip.Pj_Send_Dtmf(1005,"*36500#");
        sleep(3000);

        step("分机1005挂断通话，检查会议室6500存在一路通话，外线挂断，检查cdr");
        pjsip.Pj_hangupCall(1005);

        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500")))
                .as("会议室6500存在一路通话")
                .contains("1 users in that conference");

        pjsip.Pj_Hangup_All();
        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Conference0_6500.toString(),   ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过FXO外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31002#转移给分机C-1002 " +
            "分机C接听后，等待3秒，分机1005挂断通话；" +
            "分机C与外线保持通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT24_AttendedTransfer()
    {
        if(FXO_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"FXO不存在，不测");
        }

        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过FXO外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"2005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);

        step("分机1005拨打*31002#转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*31002#");

        step("分机C接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机C与外线保持通话，分机C挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), FXO_1, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1002.toString()+" hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过BRI外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31003#转移给分机D-1003 " +
            "分机D接听后，等待3秒，分机1005挂断通话；" +
            "分机D与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT25_AttendedTransfer()
    {
        if(BRI_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"BRI_1不存在，不测");
        }

        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过BRI外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"881005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        
        step("分机1005拨打*31003#转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*31003#");

        step("分机D接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("分机D与外线保持通话，外线挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), BRI_1, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_2000.toString()+" hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过E1外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31002#转移给分机C-1002 " +
            "分机C接听后，等待3秒，分机1005挂断通话；" +
            "分机C与外线保持通话，分机C挂断，检查cdr ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT26_AttendedTransfer()
    {
        if(E1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"E1不存在，不测");
        }

        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过E1外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"661005");
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1005);
        
        step("分机1005拨打*31002#转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*31002#");

        step("分机C接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("分机C与外线保持通话，分机C挂断，检查cdr ");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1005.toString(),   ANSWER.toString(), Extension_2000.toString() + " called "+Extension_1005.toString(), E1, "", INBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1002.toString()+" hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("修改In1呼入到分机1005" +
            "通过GSM外线呼入到分机1005，分机1005应答" +
            "分机1005拨打*31003#转移给分机D-1003 " +
            "分机D接听后，等待3秒，分机1005挂断通话；" +
            "分机D与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","IVR","testFCCT19_AttendedTransfer"})
    public void testFCCT27_AttendedTransfer()
    {
        if(GSM.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"GSM不存在，不测");
        }

        prerequisite();
        step("修改In1呼入到分机1005");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        step("通过GSM外线呼入到分机1005，分机1005应答");
        pjsip.Pj_Make_Call_No_Answer(2000,"331004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 30)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1005拨打*31003#转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*31003#");

        step("分机D接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 30)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("分机D与外线保持通话，外线挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 30)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 30)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), "22222",   ANSWER.toString(), Extension_1005.toString() + " called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple("22222", Extension_1001.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1001.toString()+" hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机1005挂断通话；" +
            "分机B与外线保持通话，分机B挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("此处若在cdr校验报错的话，那么就看实际生成的cdr是不是不正确")
    @Test(groups = {"P1","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT28_AttendedTransfer"})
    public void testFCCT28_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001； ");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        step("分机B接听后，等待3秒，分机1005挂断通话；");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机B与外线保持通话，分机B挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);
        pjsip.Pj_hangupCall(1001);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple("22222", Extension_1001.toString(), ANSWER.toString(), Extension_1005.toString()+" attended transferred , "+Extension_1001.toString()+" hung up", SPS, "", INBOUND.toString()))
        ;
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机1005挂断通话；" +
            "分机B与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT29_AttendedTransfer"})
    public void testFCCT29_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
//        pjsip.Pj_Make_Call_No_Answer(1005,"22222");
//
//        step("辅助2的分机2000应答 ");
//        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000);
//        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
//        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
//todo yong li bu zhun que
        step("分机1005拨打*31001#将通话转移给分机B-1001 ");

        step("分机B接听后，等待3秒，分机1005挂断通话；");
        step("分机B与外线保持通话，外线挂断，检查cdr");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B接听后，等待3秒，分机B挂断通话；" +
            "分机1005与外线恢复通话，分机1005挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT30_AttendedTransfer"})
    public void testFCCT30_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001 ");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);

        step("分机B接听后，等待3秒，分机B挂断通话；");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);

        sleep(3000);
        pjsip.Pj_hangupCall(1001);

        step("分机1005与外线恢复通话，分机1005挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as(".[通话校验] 1001挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(),  ANSWER.toString(), Extension_1005.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" hung up", "", SPS, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃，不接听" +
            "等待15秒后，分机B停止响铃，分机1005与外线恢复通话，外线挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT31_AttendedTransfer"})
    public void testFCCT31_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001 ");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);

        step("分机B保持响铃，不接听,等待15秒后");
        sleep(15000);
        step("分机B停止响铃，分机1005与外线恢复通话，外线挂断通话，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as(".[通话校验] 1001挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as(".[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), "22222 hung up", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1005.toString()+"  hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃3秒后，外线挂断通话 " +
            "通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT32_AttendedTransfer"})
    public void testFCCT32_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001 ");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);

        step("分机B保持响铃3秒后，外线挂断通话 ");
        sleep(3000);
        pjsip.Pj_hangupCall(2000);

        step("通话被挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as(".[通话校验] 2000挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as(".[通话校验] 1001挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222",ANSWER.toString(), Extension_1005.toString()+" called 22222", "",  SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), NO_ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), "22222",ANSWER.toString(), "22222 hung up", "",  SPS, OUTBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*31001#将通话转移给分机B-1001 " +
            "分机B保持响铃3秒后，分机1005挂断通话" +
            "分机B继续响铃，应答，分机B与外线正常通话，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT33_AttendedTransfer"})
    public void testFCCT33_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*31001#将通话转移给分机B-1001 ");
        pjsip.Pj_Send_Dtmf(1005,"*31001#");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);

        step("分机B保持响铃3秒后，分机1005挂断通话");
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机B继续响铃，应答，分机B与外线正常通话，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222",ANSWER.toString(), Extension_1005.toString()+" called 22222", "",  SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), "22222",ANSWER.toString(), Extension_1005.toString()+" attended transferred , "+Extension_1001.toString()+" hung up", "",  SPS, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出" +
            "辅助2的分机2000应答" +
            "分机1005拨打*36200#将通话转移给IVR0 " +
            "asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机1005按0，分机1000响铃，接听，分机1005挂断；分机A挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT34_AttendedTransfer"})
    public void testFCCT34_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext");
        thread.start();

        step("分机1005拨打*36200#将通话转移给IVR0");
        pjsip.Pj_Send_Dtmf(1005,"*36200#");

        step("asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机1005按0，分机1000响铃，接听，分机1005挂断 ");
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 400){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("asterisk后台检测到播放提示音“ivr-greeting-dial-ext").isTrue();

        pjsip.Pj_Send_Dtmf(1005,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("分机A挂断；检查cdr ");
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(4)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222",ANSWER.toString(), Extension_1005.toString()+" called 22222", "",  SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), IVR0_6200.toString(), ANSWER.toString(), Extension_1005.toString() + " called Extension", "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1000.toString(), ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1005.toString(), "22222",ANSWER.toString(), Extension_1005.toString()+" attended transferred , "+Extension_1001.toString()+" hung up", "",  SPS, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打13001通过sip外线呼出，辅助1的3001接听" +
            "分机1005拨打*36400#将通话转移给Queue0 " +
            "分机1000、1001、1003、1004同时响铃，分机1004应答，分机1005挂断；1004与外线保持通话，分机1004挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT35_AttendedTransfer"})
    public void testFCCT35_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打13001通过sip外线呼出，辅助1的3001接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"13001");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as(".[通话校验] 3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as(".[通话校验] 3001通话中").isEqualTo(TALKING);

        step("分机1005拨打*36400#将通话转移给Queue0 ");
        pjsip.Pj_Send_Dtmf(1005,"*36400#");

        step("分机1000、1001、1003、1004同时响铃，分机1004应答，分机1005挂断；1004与外线保持通话，分机1004挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("3.[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as(".[通话校验] 1004响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        pjsip.Pj_hangupCall(1005);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as(".[通话校验] 1004通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(4)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "13001",ANSWER.toString(), Extension_1005.toString()+" called 13001", "",  SIPTrunk, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), QUEUE0_6400.toString(), ANSWER.toString(), QUEUE0_6400.toString() + " connected", "", "", INTERNAL.toString()))
//                .contains(tuple(Extension_1005.toString(), Extension_1004, ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple("13001", Extension_1004.toString(),ANSWER.toString(), Extension_1005.toString()+" attended transferred , "+Extension_1004.toString()+" hung up",   SIPTrunk,"", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打3333通过Account外线呼出，辅助3的4000接听" +
            "分机1005拨打*36300#将通话转移给响铃组RingGroup0 " +
            "分机1000、1001、1003同时响铃，分机1003应答后，分机1005挂断；分机1003与外线保持通话，外线挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("此处若cdr校验失败，则看实际生成的cdr可能有误导致的")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT36_AttendedTransfer"})
    public void testFCCT36_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打3333通过Account外线呼出，辅助3的4000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"3333");
        softAssertPlus.assertThat(getExtensionStatus(4000, RING, 10)).as("[通话校验] 4000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(4000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[通话校验] 4000通话中").isEqualTo(TALKING);

        step("分机1005拨打*36300#将通话转移给响铃组RingGroup0 ");
        pjsip.Pj_Send_Dtmf(1005,"*36300#");

        step("分机1000、1001、1003同时响铃，分机1003应答后，分机1005挂断；分机1003与外线保持通话，外线挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[通话校验] 4000通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(4000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "3333",   ANSWER.toString(), Extension_1005.toString() + " called 3333", "", ACCOUNTTRUNK, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), RINGGROUP0_6300.toString(),   ANSWER.toString(), RINGGROUP0_6300.toString() + " connected", "", "", INTERNAL.toString()))
                .contains(tuple("3333", Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , 3333 hung up","", ACCOUNTTRUNK, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听" +
            "分机1005拨打*313001#将通话转移给外部号码：辅助1的3001 " +
            "辅助1的3001响铃，接听，分机1005挂断；sps外线与3001保持通话，3001挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT37_AttendedTransfer"})
    public void testFCCT37_AttendedTransfer()
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*313001#将通话转移给外部号码：辅助1的3001 ");
        pjsip.Pj_Send_Dtmf(1005,"*313001#");

        step("辅助1的3001响铃，接听，分机1005挂断；");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("3.[通话校验] 3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);

        step("sps外线与3001保持通话，3001挂断；检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as(".[通话校验] 3001通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(3001);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222",   ANSWER.toString(), Extension_1005.toString() + " called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), "13001",   ANSWER.toString(), Extension_1005.toString() + " called 13001", "", SIPTrunk, OUTBOUND.toString()))
                .contains(tuple("22222", "13001",   ANSWER.toString(), Extension_1005.toString() + " attended transferred ,13001 hung up", SPS, SIPTrunk, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听" +
            "分机1005拨打*36500#将通话转移到会议室6500 " +
            "分机1005挂断通话，检查会议室6500存在一路通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT38_AttendedTransfer"})
    public void testFCCT38_AttendedTransfer() throws IOException, JSchException
    {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*36500#将通话转移到会议室6500 ");
        pjsip.Pj_Send_Dtmf(1005,"*36500#");
        sleep(WaitUntils.TALKING_WAIT);
        step("分机1005挂断通话，检查会议室6500存在一路通话，外线挂断，检查cdr");
        pjsip.Pj_hangupCall(1005);

        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500")))
                .as("会议室6500存在一路通话")
                .contains("1 users in that conference");

        pjsip.Pj_Hangup_All();

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222",   ANSWER.toString(), Extension_1005.toString() + " called 22222","", SPS, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Conference0_6500.toString(),   ANSWER.toString(), Extension_1005.toString() + " hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打42000通过FXO外线呼出，辅助2的分机2000接听" +
            "分机1005拨打*31002#转移给分机C-1002 " +
            "分机C接听后，等待3秒，分机1005挂断通话；" +
            "分机C与外线保持通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","testFCCT39_AttendedTransfer"})
    public void testFCCT39_AttendedTransfer()
    {
        if(FXO_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"FXO不存在，不测");
        }

        prerequisite();

        step("分机1005拨打42000通过FXO外线呼出，辅助2的分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"42000");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*31002#转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*31002#");

        step("分机C接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);

        step("分机C与外线保持通话，分机C挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "42000",   ANSWER.toString(), Extension_1005.toString() + " called 42000", "", FXO_1, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1002.toString(), "42000",   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1002.toString()+" hung up", "", FXO_1, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打5555通过BRI外线呼出，辅助2的分机2000接听" +
            "分机1005拨打*31003#转移给分机D-1003 " +
            "分机D接听后，等待3秒，分机1005挂断通话；" +
            "分机D与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","testFCCT40_AttendedTransfer"})
    public void testFCCT40_AttendedTransfer()
    {
        if(BRI_1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"BRI_1不存在，不测");
        }

        prerequisite();

        step("分机1005拨打5555通过BRI外线呼出，辅助2的分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"5555");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*31003#转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*31003#");

        step("分机D接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机D与外线保持通话，外线挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as(".[通话校验] 1005挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "2000",   ANSWER.toString(), Extension_1005.toString() + " called 2000", "", BRI_1, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1003.toString(), "2000",   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_2000.toString()+" hung up",  BRI_1,"", INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打6666通过E1外线呼出，辅助2的分机2000接听" +
            "分机1005拨打*31002#转移给分机C-1002 " +
            "分机C接听后，等待3秒，分机1005挂断通话；" +
            "分机C与外线保持通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","testFCCT41_AttendedTransfer"})
    public void testFCCT41_AttendedTransfer()
    {
        if(E1.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"E1不存在，不测");
        }

        prerequisite();

        step("分机1005拨打6666通过E1外线呼出，辅助2的分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"6666");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*31002#转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*31002#");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("3.[通话校验] 1002响铃").isEqualTo(RING);

        step("分机C接听后，等待3秒，分机1005挂断通话；");
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1005);
        step("分机C与外线保持通话，分机C挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "2000",   ANSWER.toString(), Extension_1005.toString() + " called 2000",  "",E1, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()))
                .contains(tuple("2000", Extension_1002.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_1002.toString()+" hung up",  E1, "",INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("分机1005拨打7+辅助2GSM号码通过GSM外线呼出，辅助2分机2000接听" +
            "分机1005拨打*31003#转移给分机D-1003 " +
            "分机D接听后，等待3秒，分机1005挂断通话；" +
            "分机D与外线保持通话，外线挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","testFCCT42_AttendedTransfer"})
    public void testFCCT42_AttendedTransfer()
    {
        if(GSM.trim().equalsIgnoreCase("null")){
            Assert.assertTrue(false,"GSM不存在，不测");
        }

        prerequisite();

        step("分机1005拨打7+辅助2GSM号码通过GSM外线呼出，辅助2分机2000接听");
        pjsip.Pj_Make_Call_No_Answer(1005,"7"+DEVICE_TEST_GSM);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*31003#转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*31003#");

        step("分机D接听后，等待3秒，分机1005挂断通话；");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(3000);
        pjsip.Pj_hangupCall(1005);

        step("分机D与外线保持通话，外线挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as(".[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(2000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "7"+DEVICE_TEST_GSM,   ANSWER.toString(), Extension_1005.toString() + " called "+"7"+DEVICE_TEST_GSM, "", GSM, OUTBOUND.toString()))
                .contains(tuple(Extension_1005.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()))
                .contains(tuple("7"+DEVICE_TEST_GSM, Extension_1003.toString(),   ANSWER.toString(), Extension_1005.toString() + " attended transferred , "+Extension_2000.toString()+" hung up", "", GSM, OUTBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("编辑Feature Code-》Attended Transfer 禁用" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\n" +
            "分机A拨打*31002#将通话转移给分机C-1002； " +
            "转移失败，分机1002不会响铃，AB保持通话；通话挂断，检查cdr" +
            "编辑Feature Code-》Attended Transfer 启用；\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\n" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C响铃，接听后，等待3秒，分机A挂断通话；" +
            "分机BC保持通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT43_AttendedTransfer"})
    public void testFCCT43_AttendedTransfer()
    {
        prerequisite();

        step("编辑Feature Code-》Attended Transfer 禁用");


        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        step("分机A拨打*31002#将通话转移给分机C-1002； ");
        step("转移失败，分机1002不会响铃，AB保持通话；通话挂断，检查cdr");
        step("编辑Feature Code-》Attended Transfer 启用；");
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        step("分机A拨打*31002#将通话转移给分机C-1002；");
        step("分机C响铃，接听后，等待3秒，分机A挂断通话；");
        step("分机BC保持通话，分机B挂断通话，检查cdr");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransfer,Transfer")
    @Description("编辑Feature Code-》Attended Transfer 修改特征码为*12345*" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\n" +
            "分机A拨打*31002#将通话转移给分机C-1002； " +
            "转移失败，分机1002不会响铃，AB保持通话；通话挂断，检查cdr" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\n" +
            "分机A拨打*12345*1002#将通话转移给分机C-1002；" +
            "分机C响铃，接听后，等待3秒，分机A挂断通话；\n" +
            "分机BC保持通话，分机B挂断通话，检查cdr" +
            "编辑Feature Code-》Attended Transfer 修改特征码为*3；\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\n" +
            "分机A拨打*31002#将通话转移给分机C-1002；" +
            "分机C响铃，接听后，等待3秒，分机A挂断通话；\n" +
            "分机BC保持通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT44_AttendedTransfer"})
    public void testFCCT44_AttendedTransfer()
    {
        prerequisite();

        step("编辑Feature Code-》Attended Transfer 修改特征码为*12345*");
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        step("分机A拨打*31002#将通话转移给分机C-1002； ");
        step("转移失败，分机1002不会响铃，AB保持通话；通话挂断，检查cdr");
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        step("分机A拨打*12345*1002#将通话转移给分机C-1002；");
        step("分机C响铃，接听后，等待3秒，分机A挂断通话；");
        step("分机BC保持通话，分机B挂断通话，检查cdr");
        step("编辑Feature Code-》Attended Transfer 修改特征码为*3；");
        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        step("分机A拨打*31002#将通话转移给分机C-1002；");
        step("分机C响铃，接听后，等待3秒，分机A挂断通话；");
        step("分机BC保持通话，分机B挂断通话，检查cdr");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("DigitTimeout,Transfer")
    @Description("默认值8s " +
            "分机A-1000拨打分机C-1002,1002应答 " +
            "分机1002按*31 停8秒后按003#；" +
            "分机AC保持正常通话，分机1003不会响铃;挂断，检查cdr ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT45_AttendedTransfer"})
    public void testFCCT45_AttendedTransfer()
    {
        prerequisite();

        step("分机A-1000拨打分机C-1002,1002应答 ");
        pjsip.Pj_Make_Call_No_Answer(1000,"1002");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        step("分机1002按*31 停8秒后按003#；");
        pjsip.Pj_Send_Dtmf(1002,"*31");
        sleep(8000);
        pjsip.Pj_Send_Dtmf(1002,"003#");

        step("分机AC保持正常通话，分机1003不会响铃;挂断，检查cdr ");
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 10)).as(".[通话校验] 1003不会响铃").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("DigitTimeout,Transfer")
    @Description("默认值8s " +
            "分机A-1000拨打分机C-1002,1002应答 " +
            "分机1002按*31停5秒后按003#" +
            "分机1003响铃，接听，分机1002挂断；分机1000、1002正常通话，挂断，检查cdr ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT42_AttendedTransfer"})
    public void testFCCT46_AttendedTransfer()
    {
        prerequisite();

        step("分机A-1000拨打分机C-1002,1002应答 ");
        pjsip.Pj_Make_Call_No_Answer(1000,"1002");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);

        step("分机1002按*31停5秒后按003#");
        pjsip.Pj_Send_Dtmf(1002,"*31");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1002,"003#");
        step("分机1003响铃，接听，分机1002挂断；分机1000、1002正常通话，挂断，检查cdr ");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as(".[通话校验] 1002挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1000);
        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1002.toString() + " attended transferred , "+Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1002.toString(), Extension_1003.toString(),   ANSWER.toString(), Extension_1002.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),   ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1002.toString(), "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("DigitTimeout,Transfer")
    @Description("编辑Feature Code-》Digit Timeout (s) 为3秒；\n" +
            "分机A-1000拨打分机C-1002,1002应答 " +
            "分机1002按*31停5秒后按003# " +
            "分机AC保持正常通话，分机1003不会响铃;挂断，检查cdr" +
            "编辑Feature Code-》Digit Timeout (s) 为8秒；\n" +
            "分机A-1000拨打分机C-1002,1002应答" +
            "分机1002按*31停5秒后按003#" +
            "分机1003响铃，接听，分机1003挂断；分机1000、1002正常通话，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT42_AttendedTransfer"})
    public void testFCCT47_AttendedTransfer()
    {
        prerequisite();

        step("编辑Feature Code-》Digit Timeout (s) 为3秒；");
        apiUtil.editFeatureCode("\"transfer_digit_timeout\":3").apply();

        step("分机A-1000拨打分机C-1002,1002应答 ");
        pjsip.Pj_Make_Call_No_Answer(1000,"1002");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);

        step("分机1002按*31停5秒后按003# ");
        pjsip.Pj_Send_Dtmf(1002,"*31");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(1002,"003#");

        step("分机AC保持正常通话，分机1003不会响铃;挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 10)).as(".[通话校验] 1003不会响铃").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();
        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()));

        step("编辑Feature Code-》Digit Timeout (s) 为8秒；");
        apiUtil.editFeatureCode("\"transfer_digit_timeout\":8").apply();

        step("分机A-1000拨打分机C-1002,1002应答");
        pjsip.Pj_Make_Call_No_Answer(1000,"1002");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);

        step("分机1002按*31停5秒后按003#");
        pjsip.Pj_Send_Dtmf(1002,"*31");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(1002,"003#");

        step("分机1003响铃，接听，分机1003挂断；分机1000、1002正常通话，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as(".[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as(".[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 10)).as(".[通话校验] 1003挂断").isEqualTo(HUNGUP);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1002.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1002.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(),ANSWER.toString(), Extension_1000.toString()+" hung up",  "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-CallTransfer")
    @Story("AttendedTransferTimeout,Transfer")
    @Description("编辑Feature Code-》Attended Transfer Timeout 设置为5s" +
            "分机A-1000拨打分机D-1003,1003应答，分机按*31001" +
            "分机1001响铃未接，5秒后停止响铃，分机AD恢复通话，挂断，检查cdr" +
            "编辑Feature Code-》Attended Transfer Timeout 设置为15s;\n" +
            "分机A-1000拨打分机D-1003,1003应答，分机1003按*31001" +
            "分机1001响铃未接，5秒后继续响铃，分机1001接听，分机1003挂断，AB正常通话，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","FeatureCode","FeatureCodeCallTransfer","AttendedTransfer","Transfer","PSeries","Cloud","K2","testFCCT48_AttendedTransfer"})
    public void testFCCT48_AttendedTransfer()
    {
        prerequisite();

        step("编辑Feature Code-》Attended Transfer Timeout 设置为5s");
        apiUtil.editFeatureCode("\"attend_transfer_timeout\":5").apply();

        step("分机A-1000拨打分机D-1003,1003应答，分机按*31001");
        pjsip.Pj_Make_Call_No_Answer(1000,"1003");
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(1000,"*31001");

        step("分机1001响铃未接，5秒后停止响铃，分机AD恢复通话，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as(".[通话校验] 1001挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as(".[通话校验] 1003通话中").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),NO_ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()));

        step("编辑Feature Code-》Attended Transfer Timeout 设置为15s;");
        apiUtil.editFeatureCode("\"attend_transfer_timeout\":15").apply();

        step("分机A-1000拨打分机D-1003,1003应答，分机1003按*31001");
        pjsip.Pj_Make_Call_No_Answer(1000,"1003");
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(1000,"*31001");

        step("分机1001响铃未接，5秒后继续响铃，分机1001接听，分机1003挂断，AB正常通话，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        sleep(5000);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("3.[通话校验] 1001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as(".[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as(".[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("CDR校验");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().refresh();
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1000.toString() + " hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(),ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1003.toString(),ANSWER.toString(), Extension_1000.toString() + " called "+Extension_1003.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }
}
