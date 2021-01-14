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
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
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
    List<AsteriskObject> asteriskObjectList_true = new ArrayList<AsteriskObject>();
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

    public void initRecord(){
        //System-》Storage-》删除所有网络磁盘-》添加网络磁盘record
        apiUtil.editStoragelocation(String.format("\"recording\":0")).deleteAllNetWorkStorage().
                createStorage(String.format("\"name\":\"record\",\"host\":\"%s\",\"share_name\":\"%s\",\"username\":\"%s\",\"password\":\"cQ==\",\"work_group\":\"\",\"samba_version\":\"auto\"",NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,NETWORK_DEVICE_USER_NAME, md5Hex(NETWORK_DEVICE_USER_PASSWORD))).apply();
//                createStorage("\"name\":\"record\",\"host\":\"192.168.3.5\",\"share_name\":\"record\",\"username\":\"\",\"password\":\"\",\"work_group\":\"\",\"samba_version\":\"auto\"").apply();
        String request = String.format("\"recording\":%d",apiUtil.getStorageObjectSummary("record"));
        apiUtil.editStoragelocation(request).apply();
        //编辑分机A-》Features-》勾选Allow the extension to stop or restart call recording during a call 【其它分机默认未勾选】
        apiUtil.editExtension("1000","\"enb_ctl_record\":1").apply();
    }

    public void initTestEnv() {
        //System-》Storage-》删除所有网络磁盘-》添加网络磁盘record
        apiUtil.editStoragelocation(String.format("\"recording\":0")).deleteAllNetWorkStorage().
                createStorage(String.format("\"name\":\"record\",\"host\":\"%s\",\"share_name\":\"%s\",\"username\":\"%s\",\"password\":\"cQ==\",\"work_group\":\"\",\"samba_version\":\"auto\"",NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,NETWORK_DEVICE_USER_NAME, md5Hex(NETWORK_DEVICE_USER_PASSWORD))).apply();
        String request = String.format("\"recording\":%d",apiUtil.getStorageObjectSummary("record"));
        apiUtil.editStoragelocation(request).apply();
        //编辑分机A-》Features-》勾选Allow the extension to stop or restart call recording during a call 【其它分机默认未勾选】
        apiUtil.editExtension("1000","\"enb_ctl_record\":1").apply();
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
    @Issue("【ID1037991】【P系列】【自动化】外线呼入（SPS/SIPRegist/Accout）到Conference CDR/Recording 记录 Communication Type字段显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"},enabled = true)
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
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

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
    @Issue(" @Issue(\"【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常\")")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "EnableRecordingofInternalCalls","RecordExtensions","InternalCallBeingRecordedPrompt","OutboundCallBeingRecordedPrompt","InboundCallBeingRecordedPrompt"},enabled = true)
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
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"},enabled = true)
    public void testRecording_10_RecordTrunks() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P2", "RecordTrunks"},enabled = true)
    public void testRecording_11_RecordTrunks() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordTrunks"},enabled = true)
    public void testRecording_12_RecordTrunks() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries","Recording","P3", "RecordTrunks"},enabled = true)
    public void testRecording_13_RecordTrunks() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 2005");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", FXO_1, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Recording","P3", "RecordTrunks"},enabled = true)
    public void testRecording_14_RecordTrunks() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 881000");
        pjsip.Pj_Make_Call_No_Answer(2000, "881000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", BRI_1, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Recording","P3", "RecordTrunks"},enabled = true)
    public void testRecording_15_RecordTrunks() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1线路 不测试！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 661000");
        pjsip.Pj_Make_Call_No_Answer(2000, "661000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", E1, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
    @Test(groups = {"PSeries", "Recording","P3", "RecordTrunks"})
    public void testRecording_16_RecordTrunks() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 33"+DEVICE_TEST_GSM);
        pjsip.Pj_Make_Call_No_Answer(2000 ,"33"+DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 150)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*90);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(DEVICE_ASSIST_GSM,CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", GSM, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(DEVICE_ASSIST_GSM,CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1001, "13001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up",  "", SIPTrunk,"Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "13001","Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 22222");
        pjsip.Pj_Make_Call_No_Answer(1001, "22222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"22222", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up","",SPS, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22222","Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 3333");
        pjsip.Pj_Make_Call_No_Answer(1001, "3333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"3333", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up","",ACCOUNTTRUNK, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"3333","Outbound"));

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
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller]1001 " + ",[callee] 42000");
        pjsip.Pj_Make_Call_No_Answer(1001, "42000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"42000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up",  "",FXO_1, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"42000","Outbound"));

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
    @Test(groups = {"PSeries", "Recording","P3", "RecordTrunks"})
    public void testRecording_21_RecordTrunks() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 5555");
        pjsip.Pj_Make_Call_No_Answer(1001, "5555", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"2000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", BRI_1, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"2000","Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller]1001 " + ",[callee] 6666");
        pjsip.Pj_Make_Call_No_Answer(1001, "6666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"2000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", E1, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"2000","Outbound"));

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
        if((GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")) && (DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase(""))){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] "+"7"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1001, "7"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*90);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"7"+DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "",GSM, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "7"+DEVICE_ASSIST_GSM,"Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_2000.toString(),CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 22222");
        pjsip.Pj_Make_Call_No_Answer(1001, "22222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"22222", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1001.toString(),"22222","Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + ",[callee] 6500");
        pjsip.Pj_Make_Call_No_Answer(1000, "6500", DEVICE_IP_LAN, false);
        pjsip.Pj_Make_Call_No_Answer(1001, "6500", DEVICE_IP_LAN, false);

        sleep(15*1000);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"))
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(),CDRNAME.Conference0_6500.toString(),"Internal"))
                .doesNotContain(tuple(CDRNAME.Extension_1001.toString(),CDRNAME.Conference0_6500.toString(),"Internal"));

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
    @Issue("【【P系列】【自动化】外线呼入（SPS/SIPRegist/Accout）到Conference   CDR/Recording 记录   Communication Type字段显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001037991")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "RecordConferences"},enabled = false)
    public void testRecording_27_RecordConferences() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"record_trunk_list\":[],\"record_ext_list\":[],\"record_conference_list\":[{\"value\":\"%s\",\"text\":\"Conference0\"}]",apiUtil.getConferenceSummary("6500").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"conference\",\"def_dest_value\":\"%s\"",apiUtil.getConferenceSummary("6500").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        // SIP 呼入
        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        // SPS 呼入
        step("2:[caller] 2000" + ",[callee] 996500");
        pjsip.Pj_Make_Call_No_Answer(2000, "996500", DEVICE_ASSIST_2, false);

        // ACCOUNT 呼入
        step("2:[caller] 4000" + ",[callee] 446500");
        pjsip.Pj_Make_Call_No_Answer(4000, "446500", DEVICE_ASSIST_3, false);

        //分机1000 1001
        step("2:[caller] 1000/1001" + ",[callee] 6500");
        pjsip.Pj_Make_Call_No_Answer(1000, "6500", DEVICE_IP_LAN, false);
        pjsip.Pj_Make_Call_No_Answer(1001, "6500", DEVICE_IP_LAN, false);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(2000);
        pjsip.Pj_hangupCall(3000);
        pjsip.Pj_hangupCall(4000);
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(5)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_3001.toString() + " hung up", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_4000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"))
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(5)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Conference0_6500.toString(),"Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Conference0_6500.toString(),"Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Conference0_6500.toString(),"Inbound"))
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Conference0_6500.toString(),"Internal"))
                .contains(tuple(CDRNAME.Extension_1001.toString(), CDRNAME.Conference0_6500.toString(),"Internal"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 6400");
        pjsip.Pj_Make_Call_No_Answer(1000, "6400", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);
        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(),CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(),"Queue Queue0<6400> connected", "", "", "Internal"))
                .contains(tuple(CDRNAME.Extension_1000.toString(),CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_1000.toString(),CDRNAME.QUEUE0_6400.toString(),"Internal"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"record_trunk_list\":[],\"record_ext_list\":[],\"record_conference_list\":[],\"record_queue_list\":[{\"value\":\"%s\",\"text\":\"Queue0\"}]",apiUtil.getQueueSummary("6400").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary("6400").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee]3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected",SIPTrunk, "",  "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up",  SIPTrunk,"", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.QUEUE0_6400.toString(),"Outbound"));

        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").apply();

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":0,\"record_trunk_list\":[],\"record_ext_list\":[],\"record_conference_list\":[],\"record_queue_list\":[{\"value\":\"%s\",\"text\":\"Queue0\"}]",apiUtil.getQueueSummary("6400").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary("6400").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee]991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1003, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003, false);
        assertThat(getExtensionStatus(1003, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(),  "Queue Queue0<6400> connected",  SPS,"", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_2000.toString() + " hung up",  SPS,"", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.QUEUE0_6400.toString(),"Inbound"));

        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").apply();

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
    public void testRecording_31_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

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
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "分机A-1000 拨打分机B-1001，接听，通话5s\n" +
            "\t32、分机B按*1 暂停录音，asterisk 检查会打印no permission to pause or resume recording ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_32_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"no permission to pause or resume recording");

        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1001,"*","1");
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1001,"*","1");

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
    @Story("FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("勾选Enable Recording of Internal Calls、InternalCallBeingRecordedPrompt 选择空，Outbound Call Being Recorded Prompt 选择空，Inbound Call Being Recorded Prompt 选择空、Record Extensions 全选分机，RecordTrunks 全选所有外线，RecordConferences 为空，RecordQueues 为空" +
            "33、通过sps外线呼入到分机1000，接听，通话5s\n" +
            "\t分机A按*1 暂停录音，asterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1037962】【P系列】【自动化】Recording call from 字段格式显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "FeatureCode","PauseRecording","ResumeRecording","CallRecording"},enabled = true)
    public void testRecording_33_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS,"",  "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 22222");
        pjsip.Pj_Make_Call_No_Answer(1000, "22222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22222","Outbound"));

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();
        apiUtil.editFeatureCode("\"enb_auto_record\":0").apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        SSHLinuxUntils.AsteriskThread thread_1=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
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
            Assert.assertFalse(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        apiUtil.editFeatureCode("\"enb_auto_record\":1").apply();

        asteriskObjectList.clear();
        thread_1.start();
        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
        tmp=0;
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

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
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();
        apiUtil.editFeatureCode("\"auto_record\":\"******1\"").apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        SSHLinuxUntils.AsteriskThread thread_false=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"=== PAUSE MIXMON");
        SSHLinuxUntils.AsteriskThread thread_true=new SSHLinuxUntils.AsteriskThread(asteriskObjectList_true,"=== PAUSE MIXMON");

        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","*","*","*","*","*","1");
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
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", "", "Internal"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), CDRNAME.Extension_1001.toString(),"Internal"));

        apiUtil.editFeatureCode("\"auto_record\":\"*1\"").apply();
        asteriskObjectList.clear();
        thread_false.start();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","*","*","*","*","*","1");
        tmp = 0;
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
            Assert.assertTrue(true, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread_false.flag = false;

        thread_true.start();
        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");
        tmp=0;
        while (asteriskObjectList_true.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
        softAssertPlus.assertAll();
    }
}
