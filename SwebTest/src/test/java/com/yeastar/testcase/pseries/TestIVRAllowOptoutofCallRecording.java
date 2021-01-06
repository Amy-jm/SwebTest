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
 * @description: test Recording
 * @author: huangjx@yeastar.com
 * @create: 2020/12/03
 */
@Log4j2
public class TestIVRAllowOptoutofCallRecording extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectList_true = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestIVRAllowOptoutofCallRecording() {
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
            initRecord();
//            initTestEnv();//TODO  local debug
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
            initRecord();
            initTestEnv();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }


    public void initTestEnv() {
      log.info("Call Features-》Recording\n" +
              "勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 全选会议室，RecordQueues 全选队列");

      log.info("1)新建IVR-Recording1-8888,\n" +
              "按0到分机1000，勾选Allow Opt-out of Call Recording；\n" +
              "按1到IVR0-6200,勾选Allow Opt-out of Call Recording；\n" +
              "按2到RingGroup0-6300,勾选Allow Opt-out of Call Recording；\n" +
              "按3到Queue0-6400,勾选Allow Opt-out of Call Recording；\n" +
              "\n" +
              "2)编辑In1到IVR-Recording1-8888");

    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("1、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000，分机1000接听，分机1000按*1暂停录音，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_01_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("2、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000，分机1000接听，1000按*03，停2秒按1001转移给分机B-1001,分机B接听，保持通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_02_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("3、通过sps外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按1到IVR0-6200，asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000,分机1000接听，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_03_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("4、通过Account外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按2到RingGroup0，成员1000接听，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_04_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("5、通过Account外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按2到RingGroup0，成员未接，10s后Failover到分机1000，分机1000接听，通话15s，挂断\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_05_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("6、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席1000接听，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_06_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("7、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，等待60s后Failover到分机1000,1000接听，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_07_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording")
    @Description("8、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，主叫按0到分机1001,1001接听，通话15s,挂断；\n" +
            "\t检查cdr ，不会录音；检查Recording Files没有产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording"})
    public void testRecording_08_Recording() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("9、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000，分机1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_09_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("10、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000，分机1000接听，1000按*03，停2秒按1001转移给分机B-1001,分机B接听，保持通话15s\n" +
            "\t检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_10_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("11、通过sps外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按1到IVR0-6200，asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000,分机1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_11_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("12、通过Account外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按2到RingGroup0，成员1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_12_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("13、通过Account外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按2到RingGroup0，成员未接，10s后Failover到分机1000，分机1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_13_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("14、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_14_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("15、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，等待60s后Failover到分机1000,1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_15_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("16、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，主叫按0到分机1001,1001接听，通话15s时，分机1001按*1暂停录音\n" +
            "\tasterisk 检查不会打印=== PAUSE MIXMON ，等待5s，分机1001按*1恢复录音，asterisk 检查不会打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_16_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }
}
