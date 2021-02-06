package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.swebtest.tools.pjsip.PjsipDll;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.*;
import static com.yeastar.untils.CDRObject.STATUS.*;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test Bline Transfer
 * @author: lhr@yeastar.com
 * @create: 2020/11/20
 */
@Log4j2
public class TestFeatureCodeBlindTransfer extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestFeatureCodeBlindTransfer() {
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
            apiUtil.editFeatureCode("\"enb_blind_transfer\":1,\"blind_transfer\":\"*03\"").apply();
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
            apiUtil.editFeatureCode("\"enb_blind_transfer\":1,\"blind_transfer\":\"*03\"").apply();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:\r\n" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    private void recoveryEvn(){
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("1.分机A-1000拨打分机B-1001，B-1001应答，等待5s；分机A按*031002#将通话盲转移给分机C-1002；\r\n" +
            "分机A自动挂断，分机C响铃  \r\n" +
            "分机C-1002接听，分机C挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "Transfer", "testFCBT01"})
    public void testFCBT01() {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；分机A按*031002#将通话盲转移给分机C-1002；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        pjsip.Pj_Send_Dtmf(1000,"*031002#");
        step("分机A自动挂断，分机C响铃 ");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000预期挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验] 1002预期响铃").isEqualTo(RING);

        step("分机C-1002接听，分机C挂断；");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.SHORT_WAIT);

        pjsip.Pj_hangupCall(1002);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("1.分机A-1000拨打分机B-1001，B-1001应答，等待5s；分机A按*031002#将通话盲转移给分机C-1002；\r\n" +
            "分机A自动挂断，分机C响铃  \r\n" +
            "分机C-1002接听，分机B挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "Transfer", "testFCBT02"})
    public void testFCBT02() {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；分机A按*031002#将通话盲转移给分机C-1002；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("分机A自动挂断，分机C响铃 ");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000预期挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验] 1002预期响铃").isEqualTo(RING);

        step("分机C-1002接听，分机B挂断");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.SHORT_WAIT);

        pjsip.Pj_hangupCall(1001);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1001.toString()+" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s； \r\n" +
            "分机A按*031002#将通话盲转移给分机C-1002；\r\n" +
            "分机A自动挂断，分机B挂断  \r\n" +
            "分机C停止响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "Transfer", "testFCBT03"})
    public void testFCBT03() {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；分机A按*031002#将通话盲转移给分机C-1002；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");

        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("分机A自动挂断，分机B挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000预期挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验] 1002响铃").isEqualTo(RING);
        sleep(3000);
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 5)).as("[通话校验] 1001预期响铃").isEqualTo(HUNGUP);

        step("分机C停止响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 5)).as("[通话校验] 1002预期停止响铃").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), NO_ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1001.toString()+" hung up", "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机A-1000拨打分机B-1001，B-1001应答，等待5s；\r\n" +
            "分机B按*031002#将通话盲转移给分机C-1002；\r\n" +
            "分机B自动挂断，分机C响铃 \r\n" +
            "分机C-1002接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "Transfer", "testFCBT04"})
    public void testFCBT04() {
        prerequisite();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话校验] 1000预期通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 1 )).as("[通话校验] 1001预期通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机B按*031002#将通话盲转移给分机C-1002； ");
        pjsip.Pj_Send_Dtmf(1001,"*031002#");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002预期响铃").isEqualTo(RING);

        step("分机B自动挂断，分机C响铃 ");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("[通话校验] 1001挂断").isEqualTo(HUNGUP);

        step("分机C-1002接听，挂断；");
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1001.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("修改In1呼入到分机1004\r\n" +
            "通过sps外线呼入到分机1004,1004接听\r\n" +
            "1004按*031002#将通话盲转给分机C-1002 \r\n" +
            "分机1004自动挂断，分机1002响铃，接听挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT05"})
    public void testFCBT05() {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过sps外线呼入到分机1004,1004接听");
        pjsip.Pj_Make_Call_No_Answer(2000,"991004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004挂断").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        step("1004按*031002#将通话盲转给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1004,"*031002#");

        step("分机1004自动挂断，分机1002响铃，接听挂断；");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("修改In1呼入到分机1004\r\n" +
            "通过sps外线呼入到分机1004,1004接听\r\n" +
            "1004按*036200#将通话盲转给IVR0 \r\n" +
            "1004自动挂断，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，主叫按0，分机1000响铃，接听，挂断；\r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT06"})
    public void testFCBT06() {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过sps外线呼入到分机1004,1004接听");
        pjsip.Pj_Make_Call_No_Answer(2000,"991004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext");
        thread.start();

        step("1004按*036200#将通话盲转给IVR0；");
        pjsip.Pj_Send_Dtmf(1004,"*036200#");

        step("1004自动挂断，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，主叫按0，分机1000响铃，接听，挂断；");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004挂断").isEqualTo(HUNGUP);

        //超时等待时间400*100=40000ms
        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 400){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("asterisk后台检测到播放提示音“ivr-greeting-dial-ext").isTrue();

        sleep(WaitUntils.RETRY_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), IVR0_6200.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_2000.toString()+" called Extension", SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过SIP外线呼入到分机1004,1004接听\r\n" +
            "分机1004按*036400#将通话转移给Queue0\r\n" +
            "分机1000、1001、1003、1004同时响铃，分机1003应答，1003与外线保持通话，分机1003挂断；\r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SIP_REGISTER","Transfer", "testFCBT07"})
    public void testFCBT07() {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过SIP外线呼入到分机1004,1004接听");
        pjsip.Pj_Make_Call_No_Answer(3001,"3000");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1004按*036400#将通话转移给Queue0");
        pjsip.Pj_Send_Dtmf(1004,"*036400#");

        step("分机1000、1001、1003、1004同时响铃，分机1003应答，1003与外线保持通话，分机1003挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1003);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_3001.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_3001.toString()+" called "+Extension_1004.toString(), SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), QUEUE0_6400.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+QUEUE0_6400.toString()+" connected", SIPTrunk, "", INBOUND.toString()))
                .contains(tuple(Extension_3001.toString(), Extension_1003.toString(), ANSWER.toString(), Extension_1003.toString()+" hung up", SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过Account外线呼入到分机1004,1004接听 \r\n" +
            "分机1004按*036300#将通话盲转移给响铃组RingGroup0 \r\n" +
            "分机1004自动挂断，分机1000、1001、1003同时响铃，分机1003应答，分机1003与外线保持通话，外线挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SIP_ACCOUNT","Transfer", "testFCBT08"})
    public void testFCBT08() {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过Account外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(4000,"441004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1004按*036300#将通话盲转移给响铃组RingGroup0 ");
        pjsip.Pj_Send_Dtmf(1004,"*036300#");

        step("分机1004自动挂断，分机1000、1001、1003同时响铃，分机1003应答，分机1003与外线保持通话，外线挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);

        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(4000);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_4000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_4000.toString()+" called "+Extension_1004.toString(), ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple(Extension_4000.toString(), RINGGROUP0_6300.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+RINGGROUP0_6300.toString()+" connected", ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple(Extension_4000.toString(), Extension_1003.toString(), ANSWER.toString(), Extension_4000.toString()+" hung up", ACCOUNTTRUNK, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过sps外线呼入到分机1004,1004接听 \r\n" +
            "分机1004按*0313001#将通话盲转移给外部号码：辅助1的3001 \r\n" +
            "分机1004自动挂断，辅助1的3001响铃，接听；sps外线与3001保持通话，3001挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT09"})
    public void testFCBT09() {
        prerequisite();
        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过sps外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"991004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1004按*0313001#将通话盲转移给外部号码：辅助1的3001 ");
        pjsip.Pj_Send_Dtmf(1004,"*0313001#");

        step("分机1004自动挂断，辅助1的3001响铃，接听；sps外线与3001保持通话，3001挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[通话校验] 辅助3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[通话校验] 辅助3001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), "13001", ANSWER.toString(), Extension_1004.toString()+" blind transferred , 13001 hung up", SPS, SIPTrunk, OUTBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过sps外线呼入到分机1004,1004接听 \r\n" +
            "分机1004按*036500#将通话盲转移到会议室6500 \r\n" +
            "分机1004自动挂断通话，检查会议室6500存在一路通话，外线挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT10"})
    public void testFCBT10() throws IOException, JSchException {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过sps外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"991004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("分机1004按*036500#将通话盲转移到会议室6500 ");
        pjsip.Pj_Send_Dtmf(1004,"*036500#");

        step("分机1004自动挂断通话，检查会议室6500存在一路通话，外线挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);

        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500")))
                .as("会议室6500存在一路通话")
                .contains("1 users in that conference");

        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[通话校验] 2000挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004挂断").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), SPS, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Conference0_6500.toString(), ANSWER.toString(), Extension_2000.toString()+" hung up", SPS, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过FXO外线呼入到分机1004,1004接听 \r\n" +
            "1004按*031002#将通话盲转给分机C-1002 \r\n" +
            "分机1004自动挂断，分机1002响铃，接听，挂断；； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "FXO","Transfer", "testFCBT11"})
    public void testFCBT11() {
        prerequisite();

        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过FXO外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"2005");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("1004按*031002#将通话盲转给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1004,"*031002#");

        step("分机1004自动挂断，分机1002响铃，接听，挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("[通话校验] 1002挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[通话校验] 2000挂断").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), FXO_1, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过BRI外线呼入到分机1004,1004接听 \r\n" +
            "1004按*031002#将通话盲转给分机C-1002 \r\n" +
            "分机1004自动挂断，分机1002响铃，接听，挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "BRI","Transfer", "testFCBT12"})
    public void testFCBT12() {
        prerequisite();
        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过BRI外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"881004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("1004按*031002#将通话盲转给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1004,"*031002#");

        step("分机1004自动挂断，分机1002响铃，接听，挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("[通话校验] 1002挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[通话校验] 2000挂断").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), BRI_1, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过E1外线呼入到分机1004,1004接听 \r\n" +
            "1004按*031002#将通话盲转给分机C-1002 \r\n" +
            "分机1004自动挂断，分机1002响铃，接听，挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer","E1", "Transfer", "testFCBT13"})
    public void testFCBT13() {
        prerequisite();
        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过E1外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"661004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("1004按*031002#将通话盲转给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1004,"*031002#");

        step("分机1004自动挂断，分机1002响铃，接听，挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("[通话校验] 1002挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[通话校验] 2000挂断").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), E1, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("通过GSM外线呼入到分机1004,1004接听 \r\n" +
            "1004按*031002#将通话盲转给分机C-1002 \r\n" +
            "分机1004自动挂断，分机1002响铃，接听，挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "GSM","Transfer", "testFCBT14"})
    public void testFCBT14() {
        prerequisite();
        step("修改In1呼入到分机1004");
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        step("通过GSM外线呼入到分机1004,1004接听 ");
        pjsip.Pj_Make_Call_No_Answer(2000,"331004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004);

        step("1004按*031002#将通话盲转给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1004,"*031002#");

        step("分机1004自动挂断，分机1002响铃，接听，挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 10)).as("[通话校验] 1004自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 10)).as("[通话校验] 1002挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 10)).as("[通话校验] 2000挂断").isEqualTo(HUNGUP);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1004.toString(), ANSWER.toString(), Extension_2000.toString()+" called "+Extension_1004.toString(), GSM, "", INBOUND.toString()))
                .contains(tuple(Extension_2000.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1004.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", GSM, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打22222通过sps外线呼出 \r\n" +
            "辅助2的分机2000应答 \r\n" +
            "分机1005拨打*031001#将通话盲转移给分机B-1001； \r\n" +
            "分机1005自动挂断，分机B响铃，接听，挂断；\r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT15"})
    public void testFCBT15() {
        prerequisite();
        recoveryEvn();
        step("分机1005拨打22222通过sps外线呼出 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        step("分机1005拨打*031001#将通话盲转移给分机B-1001； ");
        pjsip.Pj_Send_Dtmf(1005,"*031001#");

        step("分机1005自动挂断，分机B 1001响铃，接听，挂断；");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1005响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple("22222", Extension_1001.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+Extension_1001.toString()+" hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打22222通过sps外线呼出 \r\n" +
            "辅助2的分机2000应答 \r\n" +
            "分机1005拨打*036200#将通话盲转移给IVR0\r\n" +
            "分机1005自动挂断，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机2000按0，分机1000响铃，接听，挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT16"})
    public void testFCBT16() {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");

        step("辅助2的分机2000应答 ");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"ivr-greeting-dial-ext");
        thread.start();

        step("分机1005拨打*036200#将通话盲转移给IVR0 ");
        pjsip.Pj_Send_Dtmf(1005,"*036200#");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);

        step("分机1005自动挂断，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，分机2000按0，分机1000响铃，接听，挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);

        int tmp=0;
        while (asteriskObjectList.size() < 1 && tmp++ < 400){
            sleep(100);
        }
        thread.flag = false;
        softAssertPlus.assertThat(asteriskObjectList.size() >= 1).as("asterisk后台检测到播放提示音“ivr-greeting-dial-ext").isTrue();

        pjsip.Pj_Send_Dtmf(2000,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple("22222", IVR0_6200.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , 22222 called Extension",  SPS, "", INBOUND.toString()))
                .contains(tuple("22222", Extension_1000.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打13001通过sip外线呼出，辅助1的3001接听 \r\n" +
            "分机1005拨打*036400#将通话盲转移给Queue0 \r\n" +
            "分机1005自动挂断，分机1000、1001、1003、1004同时响铃，分机1004应答；1004与外线保持通话，分机1004挂断 \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SIP_REGISTER","Transfer", "testFCBT17"})
    public void testFCBT17() {
        prerequisite();

        step("分机1005拨打13001通过sip外线呼出，辅助1的3001接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"13001");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[通话校验] 3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[通话校验] 3001通话中").isEqualTo(TALKING);

        step("分机1005拨打*036400#将通话盲转移给Queue0 ");
        pjsip.Pj_Send_Dtmf(1005,"*036400#");

        step("分机1005自动挂断，分机1000、1001、1003、1004同时响铃，分机1004应答；1004与外线保持通话，分机1004挂断 ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验] 1004响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[通话校验] 1004通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[通话校验] 3001通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("[通话校验] 1001挂断").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "13001", ANSWER.toString(), Extension_1005.toString()+" called 13001", "", SIPTrunk, OUTBOUND.toString()))
                .contains(tuple("13001", QUEUE0_6400.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+QUEUE0_6400.toString()+" connected",  SIPTrunk, "", INBOUND.toString()))
                .contains(tuple("13001", Extension_1004.toString(), ANSWER.toString(), Extension_1004.toString()+" hung up", SIPTrunk, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打3333通过Account外线呼出，辅助3的4000接听 \r\n" +
            "分机1005拨打*036300#将通话盲转移给响铃组RingGroup0 \r\n" +
            "分机1005自动挂断，分机1000、1001、1003同时响铃，分机1003应答；分机1003与外线保持通话，外线挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SIP_ACCOUNT","Transfer", "testFCBT18"})
    public void testFCBT18() {
        prerequisite();

        step("分机1005拨打3333通过Account外线呼出，辅助3的4000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"3333");
        softAssertPlus.assertThat(getExtensionStatus(4000, RING, 10)).as("[通话校验] 4000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(4000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[通话校验] 4000通话中").isEqualTo(TALKING);

        step("分机1005拨打*036300#将通话盲转移给响铃组RingGroup0 ");
        pjsip.Pj_Send_Dtmf(1005,"*036300#");

        step("分机1005自动挂断，分机1000、1001、1003同时响铃，分机1003应答；分机1003与外线保持通话，外线挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(4000, TALKING, 10)).as("[通话校验] 4000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 10)).as("[通话校验] 1001挂断").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(4000);
        assertStep("检查cdr");

        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "3333", ANSWER.toString(), Extension_1005.toString()+" called 3333", "", ACCOUNTTRUNK, OUTBOUND.toString()))
                .contains(tuple("6700<3333>", RINGGROUP0_6300.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+RINGGROUP0_6300.toString()+" connected",  ACCOUNTTRUNK, "", INBOUND.toString()))
                .contains(tuple("6700<3333>", Extension_1003.toString(), ANSWER.toString(), Extension_4000.toString()+" hung up", ACCOUNTTRUNK, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听 \r\n" +
            "分机1005拨打*0313001#将通话盲转移给外部号码：辅助1的3001 \r\n" +
            "分机1005自动挂断，辅助1的3001响铃，接听；sps外线与3001保持通话，3001挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT19"})
    public void testFCBT19() {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*0313001#将通话盲转移给外部号码：辅助1的3001 ");
        pjsip.Pj_Send_Dtmf(1005,"*0313001#");

        step("分机1005自动挂断，辅助1的3001响铃，接听；sps外线与3001保持通话，3001挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[通话校验] 3001响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(3001, TALKING, 10)).as("[通话校验] 3001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(3001);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple("22222", "13001", ANSWER.toString(), Extension_1005.toString()+" blind transferred , 13001 hung up",  SPS, SIPTrunk, OUTBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听 \r\n" +
            "分机1005拨打*036500#将通话盲转移到会议室6500 \r\n" +
            "分机1005自动挂断，检查会议室6500存在一路通话，外线挂断； \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "SPS","Transfer", "testFCBT20"})
    public void testFCBT20() throws IOException, JSchException {
        prerequisite();

        step("分机1005拨打22222通过sps外线呼出，辅助2的分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"22222");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*036500#将通话盲转移到会议室6500 ");
        pjsip.Pj_Send_Dtmf(1005,"*036500#");

        step("分机1005自动挂断，检查会议室6500存在一路通话，外线挂断；");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);

        softAssertPlus.assertThat(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,"meetme list 6500")))
                .as("会议室6500存在一路通话")
                .contains("1 users in that conference");

        pjsip.Pj_Hangup_All();
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "22222", ANSWER.toString(), Extension_1005.toString()+" called 22222", "", SPS, OUTBOUND.toString()))
                .contains(tuple("22222", Conference0_6500.toString(), ANSWER.toString(), "22222 hung up", SPS, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打42000通过FXO外线呼出，辅助2的分机2000接听 \r\n" +
            "分机1005拨打*031002#盲转移给分机C-1002 \r\n" +
            "分机1005自动挂断，分机C响铃，接听；分机C与外线保持通话，分机C挂断 \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "FXO","Transfer", "testFCBT21"})
    public void testFCBT21() {
        prerequisite();

        step("分机1005拨打42000通过FXO外线呼出，辅助2的分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"42000");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*031002#盲转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*031002#");

        step("分机1005自动挂断，分机C响铃，接听；分机C-1002与外线保持通话，分机C挂断 ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), "42000", ANSWER.toString(), Extension_1005.toString()+" called 42000", "", FXO_1, OUTBOUND.toString()))
                .contains(tuple("42000", Extension_1002.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", FXO_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打5555通过BRI外线呼出，辅助2的分机2000接听 \r\n" +
            "分机1005拨打*031003#盲转移给分机D-1003 \r\n" +
            "分机1005自动挂断，分机D响铃，接听；分机D与外线保持通话，外线挂断 \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("呼出的CDR被叫方显示缺少分机号")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "BRI","Transfer", "testFCBT22"})
    public void testFCBT22() {
        prerequisite();

        step("分机1005拨打5555通过BRI外线呼出，辅助2的分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"5555");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*031003#盲转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*031003#");

        step("分机1005自动挂断，分机D响铃，接听；分机D-1003与外线保持通话，1003挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), Extension_2000, ANSWER.toString(), Extension_1005.toString()+" called "+Extension_2000, "", BRI_1, OUTBOUND.toString()))
                .contains(tuple(Extension_2000, Extension_1003.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+Extension_1003.toString()+" hung up", BRI_1, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打6666通过E1外线呼出，辅助2的分机2000接听 \r\n" +
            "分机1005拨打*031002#盲转移给分机C-1002 \r\n" +
            "分机1005自动挂断，分机C响铃，接听；分机C与外线保持通话，分机C挂断 \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("呼出的CDR被叫方显示缺少分机号")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "E1","Transfer", "testFCBT23"})
    public void testFCBT23() {
        prerequisite();

        step("分机1005拨打6666通过E1外线呼出，辅助2的分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"6666");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*031002#盲转移给分机C-1002 ");
        pjsip.Pj_Send_Dtmf(1005,"*031002#");

        step("分机1005自动挂断，分机C响铃，接听；分机C-1002与外线保持通话，分机C挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), Extension_2000, ANSWER.toString(), Extension_1005.toString()+" called "+Extension_2000, "", E1, OUTBOUND.toString()))
                .contains(tuple(Extension_2000, Extension_1002.toString(), ANSWER.toString(), Extension_1005.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", E1, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("分机1005拨打7+辅助2GSM号码通过GSM外线呼出，辅助2分机2000接听 \r\n" +
            "分机1005拨打*031003#盲转移给分机D-1003 \r\n" +
            "分机1005自动挂断，分机D响铃，接听；分机D与外线保持通话，外线挂断 \r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "GSM","Transfer", "testFCBT24"})
    public void testFCBT24() {
        prerequisite();

        step("分机1005拨打7+辅助2GSM号码通过GSM外线呼出，辅助2分机2000接听 ");
        pjsip.Pj_Make_Call_No_Answer(1005,"7"+DEVICE_TEST_GSM);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 10)).as("[通话校验] 2000响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000);
        softAssertPlus.assertThat(getExtensionStatus(1005, TALKING, 10)).as("[通话校验] 1005通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);

        step("分机1005拨打*031003#盲转移给分机D-1003 ");
        pjsip.Pj_Send_Dtmf(1005,"*031003#");
        step("分机1005自动挂断，分机D响铃，接听；分机D与外线保持通话，外线挂断； ");
        softAssertPlus.assertThat(getExtensionStatus(1005, HUNGUP, 10)).as("[通话校验] 1005自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 10)).as("[通话校验] 1003响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验] 1003通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2000, TALKING, 10)).as("[通话校验] 2000通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1003);
        assertStep("检查cdr");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1005.toString(), Extension_2000, ANSWER.toString(), Extension_1005.toString()+" called "+Extension_2000, "", GSM, OUTBOUND.toString()))
                .contains(tuple(Extension_2000, Extension_1003.toString(), ANSWER.toString(), Extension_1003.toString()+" blind transferred , "+Extension_1003.toString()+" hung up", GSM, "", INTERNAL.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("编辑Feature Code-》Blind Transfer 禁用 \r\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\r\n" +
            "分机A拨打*031002#将通话盲转移给分机C-1002； \r\n" +
            "转移失败，分机1002不会响铃，AB保持通话；通话挂断 \r\n" +
            "检查cdr \r\n" +
            "编辑Feature Code-》Blind Transfer  启用；\r\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\r\n" +
            "分机A拨打*031002#将通话盲转移给分机C-1002； \r\n" +
            "分机A自动挂断，分机C响铃，接听；分机BC保持通话，分机B挂断通话，\r\n" +
            "检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer","Transfer", "testFCBT25"})
    public void testFCBT25() {
        prerequisite();

        step("编辑Feature Code-》Blind Transfer 禁用 " );
        apiUtil.editFeatureCode("\"enb_blind_transfer\":0").apply();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" );
        pjsip.Pj_Make_Call_No_Answer(1000,"1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000通话中").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(5000);
        step("分机A拨打*031002#将通话盲转移给分机C-1002； " );
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("转移失败，分机1002不会响铃，AB保持通话；通话挂断 " );
        softAssertPlus.assertThat(getExtensionStatus(1002, IDLE, 5)).as("[通话校验] 1002通话中").isEqualTo(IDLE);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        pjsip.Pj_Hangup_All();

        assertStep("检查cdr " );
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()));

        step("编辑Feature Code-》Blind Transfer  启用；" );
        apiUtil.editFeatureCode("\"enb_blind_transfer\":1").apply();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；" );
        pjsip.Pj_Make_Call_No_Answer(1000,"1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000通话中").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*031002#将通话盲转移给分机C-1002； " );
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("分机A自动挂断，分机C响铃，接听；分机BC保持通话，分机B挂断通话，" );
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1001);

        assertStep("检查cdr " );

        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1001.toString()+" hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-BlindTransfer")
    @Story("BlindTransferTransfer")
    @Description("编辑Feature Code-》Blind Transfer 修改特征码为#*1234*\r\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s； \r\n" +
            "分机A拨打*031002#将通话盲转移给分机C-1002； \r\n" +
            "转移失败，分机1000不会自动挂断，AB保持通话；通话挂断 \r\n" +
            "检查cdr \r\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\r\n" +
            "分机A拨打#*1234*1002#将通话盲转移给分机C-1002；\r\n" +
            "分机A自动挂断，分机C响铃，接听；\r\n" +
            "分机BC保持通话，分机B挂断通话，检查cdr\r\n" +
            "编辑Feature Code-》Blind Transfer  修改特征码为*03；\r\n" +
            "分机A-1000拨打分机B-1001，B-1001应答，等待5s；\r\n" +
            "分机A拨打*031002#将通话转移给分机C-1002；\r\n" +
            "分机A自动挂断，分机C响铃，接听；\r\n" +
            "分机BC保持通话，分机B挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","PSeries", "Cloud", "K2", "FeatureCode-CallTransfer","BlindTransfer", "Transfer", "testFCBT26"})
    public void testFCBT26() {
        prerequisite();

        step("编辑Feature Code-》Blind Transfer 修改特征码为#*1234*" );
        apiUtil.editFeatureCode("\"enb_blind_transfer\":1,\"blind_transfer\":\"#*1234*\"").apply();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s； ");
        pjsip.Pj_Make_Call_No_Answer(1000,"1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000通话中").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*031002#将通话盲转移给分机C-1002； ");
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("转移失败，分机1000不会自动挂断，AB保持通话；通话挂断 ");
        softAssertPlus.assertThat(getExtensionStatus(1002, IDLE, 5)).as("[通话校验] 1002通话中").isEqualTo(IDLE);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        pjsip.Pj_Hangup_All();

        step("检查cdr " );
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" hung up", "", "", INTERNAL.toString()));

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000,"1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000通话中").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打#*1234*1002#将通话盲转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"#*1234*1002#");

        step("分机A自动挂断，分机C响铃，接听；");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);

        step("分机BC保持通话，分机B挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        step("检查cdr " );
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));

        step("编辑Feature Code-》Blind Transfer  修改特征码为*03；");
        apiUtil.editFeatureCode("\"blind_transfer\":\"*03\"").apply();

        step("分机A-1000拨打分机B-1001，B-1001应答，等待5s；");
        pjsip.Pj_Make_Call_No_Answer(1000,"1001");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验] 1000通话中").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验] 1001通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验] 1000通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(5000);

        step("分机A拨打*031002#将通话转移给分机C-1002；");
        pjsip.Pj_Send_Dtmf(1000,"*031002#");

        step("分机A自动挂断，分机C响铃，接听；");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验] 1000自动挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验] 1002通话中").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002);

        step("分机BC保持通话，分机B挂断通话");
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验] 1002通话中").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验] 1001通话中").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_hangupCall(1002);

        step("检查cdr " );
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_1000.toString(), Extension_1001.toString(), ANSWER.toString(), Extension_1000.toString()+" called "+Extension_1001.toString(), "", "", INTERNAL.toString()))
                .contains(tuple(Extension_1001.toString(), Extension_1002.toString(), ANSWER.toString(), Extension_1000.toString()+" blind transferred , "+Extension_1002.toString()+" hung up", "", "", INTERNAL.toString()));

        softAssertPlus.assertAll();
    }
}
