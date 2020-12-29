package com.yeastar.testcase.pseries;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test Recording
 * @author: huangjx@yeastar.com
 * @create: 2020/12/03
 */
@Log4j2
public class TestRecording extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestRecording() {
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
            initTestEnv();//TODO  local debug
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
        //System-》Storage-》删除所有网络磁盘-》添加网络磁盘record
        apiUtil.editStoragelocation(String.format("\"recording\":0")).deleteAllNetWorkStorage().
                createStorage("\"name\":\"record\",\"host\":\"192.168.3.5\",\"share_name\":\"record\",\"username\":\"\",\"password\":\"\",\"work_group\":\"\",\"samba_version\":\"auto\"").apply();
        String request = String.format("\"recording\":%d",apiUtil.getStorageObjectSummary("record"));
        apiUtil.editStoragelocation(request).apply();
        //编辑分机A-》Features-》勾选Allow the extension to stop or restart call recording during a call 【其它分机默认未勾选】
        apiUtil.editExtension("1000","\"enb_ctl_record\":1").apply();
    }
    @Test
    public void test(){
        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(2)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting( "callFrom", "callTo","callType", "duration", "size", "recordFile","time")
                .contains(tuple( "111", "222", " 333","444","555","666","777"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t1、分机A-1000 拨打分机B-1001，分机B响铃，接听\n" +
            "\t\tasterisk 检查播放提示音prompt1；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P2", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_01_RecordedPrompt() {
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
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t2、分机B-1001拨打分机A-1000，分机A响铃，接听\n" +
            "\t\tasterisk 检查播放提示音prompt1；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P2", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_02_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 1000");
        pjsip.Pj_Make_Call_No_Answer(1001, "1000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1000.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t3、分机A-1000 拨打13001通过sip外线呼出，接听\n" +
            "\t\tasterisk 检查播放提示音prompt2；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_03_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_2);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1000, "13001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
                .contains(tuple(CDRNAME.Extension_1000.toString(),"13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001","Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t4、通过sps外线呼入到分机A-1000，接听\n" +
            "\t\tasterisk 检查播放提示音prompt3；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_04_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_3);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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

        step("1000挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple("2000", CDRNAME.Extension_1000.toString(),"Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t5、分机B-1001 拨打分机C-1002，接听\n" +
            "\t\t通话15s，挂断，检查cdr，不会被录音；检查Recording Files没有产生记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_05_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 1002");
        pjsip.Pj_Make_Call_No_Answer(1001, "1002", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002, false);
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1002.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t6、编辑Record Extensions 选择ExGroup1\n" +
            "\t\t分机B-1001 拨打分机C-1002，接听\n" +
            "\t\t\tasterisk 检查播放提示音prompt1；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n" +
            "\t编辑不勾选Enable Recording of Internal Calls\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_06_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.autorecordUpdate(String.format("\"record_ext_list\":[{\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\",\"value\":\"%s\",\"type\":\"ext_group\"}]",apiUtil.getExtensionGroupSummary("ExGroup1").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 1002");
        pjsip.Pj_Make_Call_No_Answer(1001, "1002", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002, false);
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Extension_1002.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t\t7、分机A-1000 拨打分机B-1001，分机B响铃，接听\n" +
            "\t\t\t通话15s，挂断，检查cdr，不会被录音；检查Recording Files没有产生记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_07_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t\t8、分机A-1000 拨打13001通过sip外线呼出，接听\n" +
            "\t\t\tasterisk 检查播放提示音prompt2；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_08_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_2);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1000, "13001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001","Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("EnableRecordingofInternalCalls,RecordExtensions，InternalCallBeingRecordedPrompt，OutboundCallBeingRecordedPrompt，InboundCallBeingRecordedPrompt")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 选择分机A-1000，RecordTrunks 为空\n" +
            "\t\t9、通过sps外线呼入到分机A-1000，接听\n" +
            "\t\t\tasterisk 检查播放提示音prompt3；通话15s，挂断，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"})
    public void testRecording_09_RecordedPrompt() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_3);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple("2000", CDRNAME.Extension_1000.toString(),"Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "10、通过sip外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_10_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "11、通过sps外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P2", "RecordTrunks"})
    public void testRecording_11_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "12、通过Account外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_12_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "13、通过FXO外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_13_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "14、通过BRI外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_14_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "15、通过E1外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_15_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "16、通过GSM外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t检查cdr；正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_16_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "17、分机B拨打13001通过sip外线呼出，接听，通话15s,挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_17_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "18、分机B拨打22222通过sps外线呼出，接听，通话15s,挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P2", "RecordTrunks"})
    public void testRecording_18_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "19、分机B拨打3333通过Account外线呼出，接听，通话15s，挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_19_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "20、分机B拨打42000通过FXO外线呼出，接听，通话15s，挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_20_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "21、分机B拨打5555通过BRI外线呼出，接听，通话15s,挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_21_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "22、分机B拨打6666通过E1外线呼出，接听，通话15s，挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_22_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "23、分机B拨打7+辅助2GSM号码外线呼出，接听，通话15s，挂断\n" +
            "\t检查cdr;正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_23_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "编辑RecordTrunks 不选sps外线\n" +
            "\t24、通过sps外线呼入到分机1000，接听，通话15s，挂断\n" +
            "\t\t检查cdr；未录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_24_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordTrunks")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择prompt1，Outbound Call Being Recorded Prompt 选择prompt2，Inbound Call Being Recorded Prompt 选择prompt3，Record Extensions 为空，RecordTrunks 全选所有外线" +
            "编辑RecordTrunks 不选sps外线\n" +
            "\t25、分机B拨打22222通过sps外线呼出，接听，通话15s,挂断\n" +
            "\t\t检查cdr；未录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"})
    public void testRecording_25_RecordTrunks() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordConferences")
    @Description("26、勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空\n" +
            "\t分机A-1000、分机B-1001分别 拨打Conference0-6500,通话15s，挂断\n" +
            "\t\t检查cdr;未被录音；检查Recording Files不会产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordConferences"})
    public void testRecording_26_RecordConferences() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordConferences")
    @Description("27、不勾选Enable Recording of Internal Calls、Record Extensions 为空，RecordTrunks 为空，RecordConferences 选择Conference0，RecordQueues 为空;\n" +
            "编辑In1的呼入目的地为Conference0\n" +
            "\t分别通过sip/sps/Account/外线呼入到Conference0;\n" +
            "分机A-1000、分机B-1001 呼入到Conference0;\n" +
            "\t\t通话15s，挂断\n" +
            "\t\t\t检查cdr，每条记录都有被录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordConferences"})
    public void testRecording_27_RecordConferences() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordQueues")
    @Description("28、勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空\n" +
            "\t分机A-1000 拨打Queue0-6400，坐席1001接听，通话15s，挂断\n" +
            "\t\t检查cdr;未被录音；检查Recording Files不会产生记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordQueues"})
    public void testRecording_28_RecordQueues() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordQueues")
    @Description("不勾选Enable Recording of Internal Calls、Record Extensions 为空，RecordTrunks 为空，RecordConferences 为空，RecordQueues 选择Queue0;\n" +
            "编辑In1的呼入目的地为Queue0\n" +
            "\t29、通过sip外线呼入到Queue0，坐席1000接听，通话15s，挂断\n" +
            "\t\t检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordQueues"})
    public void testRecording_29_RecordQueues() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("RecordQueues")
    @Description("不勾选Enable Recording of Internal Calls、Record Extensions 为空，RecordTrunks 为空，RecordConferences 为空，RecordQueues 选择Queue0;\n" +
            "编辑In1的呼入目的地为Queue0\n" +
            "\t30、通过sps外线呼入到Queue0，坐席1003接听，通话15s，挂断\n" +
            "\t\t检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordQueues"})
    public void testRecording_30_RecordQueues() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "分机A-1000 拨打分机B-1001，接听，通话5s\n" +
            "\t31、分机A按*1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", ""})
    public void testRecording_31_() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "分机A-1000 拨打分机B-1001，接听，通话5s\n" +
            "\t32、分机B按*1 暂停录音，asterisk 检查不会打印=== PAUSE MIXMON ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_32_FeatureCode() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "33、通过sps外线呼入到分机1000，接听，通话5s\n" +
            "\t分机A按*1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_33_FeatureCode() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "34、分机A拨打22222通过sps外线呼出，接听，通话5s\n" +
            "\t分机A按*1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_34_FeatureCode() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "35、编辑Call Features-》Feature Code 不勾选Pause/Resume Recording\n" +
            "\t分机A-1000 拨打分机B-1001，接听，通话5s\n" +
            "\t\t分机A按*1 暂停录音，asterisk 检查不会打印=== PAUSE MIXMON ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；\n" +
            "\t\t\t编辑Call Features-》Feature Code 勾选Pause/Resume Recording\n" +
            "\t\t\t\t分机A按*1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_35_FeatureCode() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Recording")
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "36、编辑Call Features-》Feature Code 修改Pause/Resume Recording特征码为******1\n" +
            "\t分机A-1000 拨打分机B-1001，接听，通话5s\n" +
            "\t\t分机A按******1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按******1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；\n" +
            "\t\t\t编辑Call Features-》Feature Code 修改Pause/Resume Recording特征码为*1；\n" +
            "分机A-1000 拨打分机B-1001，接听，通话5s；\n" +
            "\t\t\t\t分机A按******1 暂停录音，asterisk 检查不会打印=== PAUSE MIXMON ，分机A按*1暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_36_FeatureCode() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

}
