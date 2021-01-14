package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.IVRObject;
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
    private String PAUSE_MINMON = "pause mixmon success";
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListPause = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListUNPause = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListSecond = new ArrayList<AsteriskObject>();
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
      apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"\",\"outbound_prompt\":\"\",\"inbound_prompt\":\"\",\"record_trunk_list\":[{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"LTE\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"FXO1-6\",\"value\":\"%s\",\"type\":\"FXO\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"BRI1-8\",\"value\":\"%s\",\"type\":\"BRI\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"E1\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"peer\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"register\"},{\"text\":\"%s\",\"value\":\"%s\",\"type\":\"account\"}],\"record_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Default_Extension_Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"record_conference_list\":[{\"value\":\"%s\",\"text\":\"Conference0\"}],\"record_queue_list\":[{\"value\":\"%s\",\"text\":\"Queue0\"}]",GSM,apiUtil.getTrunkSummary(GSM).id,FXO_1,apiUtil.getTrunkSummary(FXO_1).id,apiUtil.getTrunkSummary("FXO1-6").id,BRI_1,apiUtil.getTrunkSummary(BRI_1).id,apiUtil.getTrunkSummary("BRI1-8").id,E1,apiUtil.getTrunkSummary(E1).id,SPS,apiUtil.getTrunkSummary(SPS).id,SIPTrunk,apiUtil.getTrunkSummary(SIPTrunk).id,SIPTrunk2,apiUtil.getTrunkSummary(SIPTrunk2).id,ACCOUNTTRUNK,apiUtil.getTrunkSummary(ACCOUNTTRUNK).id,apiUtil.getExtensionGroupSummary("Default_Extension_Group").id,apiUtil.getConferenceSummary("6500").id,apiUtil.getQueueSummary("6400").id)).apply();

      log.info("1)新建IVR-Recording1-8888,\n" +
              "按0到分机1000，勾选Allow Opt-out of Call Recording；\n" +
              "按1到IVR0-6200,勾选Allow Opt-out of Call Recording；\n" +
              "按2到RingGroup0-6300,勾选Allow Opt-out of Call Recording；\n" +
              "按3到Queue0-6400,勾选Allow Opt-out of Call Recording；\n" +
              "\n" +
              "2)编辑In1到IVR-Recording1-8888");
        ArrayList<IVRObject.PressKeyObject> pressKeyObjects_1 = new ArrayList<>();
        pressKeyObjects_1.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
        apiUtil.createIVR("8888", "IVR-Recording1-8888", pressKeyObjects_1)
                .editIVR("8888",String.format("\"allow_out_record0\":1,\"allow_out_record1\":1,\"press1_dest\":\"ivr\",\"press1_dest_value\":\"%s\",\"allow_out_record2\":1,\"press2_dest\":\"ring_group\",\"press2_dest_value\":\"%s\",\"press3_dest\":\"queue\",\"allow_out_record3\":1,\"press3_dest_value\":\"%s\"",
                        apiUtil.getIVRSummary("6200").id,apiUtil.getRingGroupSummary("6300").id,apiUtil.getQueueSummary("6400").id)).apply();

        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary("8888").id)).apply();
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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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
        step("打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000");
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"3001<3001> called Extension", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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
        step("打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000");
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*2);
        pjsip.Pj_Send_Dtmf(1000,"*03");
        sleep(1000*2);
        pjsip.Pj_Send_Dtmf(1000,"1001");

        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"3001<3001> called Extension", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(),"3001<3001> called test A<1000>", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "test A<1000> blind transferred , 3001<3001> hung up", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1001.toString(),"Inbound"));

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
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step("主叫按1到IVR0-6200，asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000,分机1000接听");
        pjsip.Pj_Send_Dtmf(2000,"1");

        sleep(5000);
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.IVR0_6200.toString(), STATUS.ANSWER.toString(),"2000<2000> called Extension", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

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

        pjsip.Pj_Send_Dtmf(4000,"2");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(),CDRNAME.RINGGROUP0_6300.toString()+" connected", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

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

        pjsip.Pj_Send_Dtmf(4000,"2");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(),CDRNAME.RINGGROUP0_6300.toString()+" connected", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step("主叫按1到IVR0-6200，asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000,分机1000接听");
        pjsip.Pj_Send_Dtmf(2000,"3");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(),"Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        String startTime = DataUtils.getCurrentTime("yyyy-MM-dd hh:mm:ss");

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step("主叫按1到IVR0-6200，asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000,分机1000接听");
        pjsip.Pj_Send_Dtmf(2000,"3");

        sleep(1000*60);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.NO_ANSWER.toString(),"Queue Queue0<6400> timed out, failover", SPS, SPS, "Outbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");//todo  历史数据可能
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple("201212121221",CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));


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
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step(" 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0");
        pjsip.Pj_Send_Dtmf(2000,"3");

        sleep(1000*10);
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.NO_ANSWER.toString(),"Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .doesNotContain(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1001.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step("打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000");
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*","1");

         tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp ==301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"2000<2000> called Extension", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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
        step("打印ivr-greeting-dial-ext.slin 时，主叫按0到分机1000");
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*5);

        pjsip.Pj_Send_Dtmf(1000,"*","0","3");
        sleep(1000*2);
        pjsip.Pj_Send_Dtmf(1000,"1","0","0","1");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);

        step("主叫挂断");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"3001<3001> called Extension", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), "test A<1000>", STATUS.ANSWER.toString(),"3001<3001> called test A<1000>", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(),"test A<1000> blind transferred , 3001<3001> hung up", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListSecond.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadSecond.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
        step(" 打印ivr-greeting-dial-ext.slin 时，主叫按1到IVR0-6200");
        pjsip.Pj_Send_Dtmf(2000,"1");
        sleep(2000);

        tmp = 0;
        while (asteriskObjectListSecond.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
            }
            threadSecond.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListSecond.size());
        }
        thread.flag = false;

        step("asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按0到分机A-1000");
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp ==301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;

        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"2000<2000> IVR IVR-Recording1-8888<8888>", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), "IVR IVR0<6200>", STATUS.ANSWER.toString(),"2000<2000> called Extension", SPS, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

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

        pjsip.Pj_Send_Dtmf(4000,"2");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*1");
        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListPause.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;
        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), "RingGroup RingGroup0<6300>", STATUS.ANSWER.toString(),"RingGroup RingGroup0<6300> connected", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_4000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(), "4000<4000> called Ring Group",ACCOUNTTRUNK, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.RINGGROUP0_6300.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

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

        pjsip.Pj_Send_Dtmf(4000,"2");
        sleep(1000*12);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*","1");
        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListPause.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;
        sleep(1000*5);

        step("主叫挂断");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");//TODO CHECK
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_4000.toString() + " hung up", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.RINGGROUP0_6300.toString(), STATUS.NO_ANSWER.toString(),"RingGroup RingGroup0<6300> timed out, failover", ACCOUNTTRUNK, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_4000.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(), "4000<4000> called Ring Group", ACCOUNTTRUNK, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_4000.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

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
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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

        pjsip.Pj_Send_Dtmf(3001,"3");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*","1");
        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListPause.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;
        sleep(1000*5);

        step("挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(),"Queue Queue0<6400> connected", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(), "3001<3001> called Queue", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.QUEUE0_6400.toString(),"Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("15、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，等待60s后Failover到分机1000,1000接听，通话15s时，分机1000按*1暂停录音\n" +
            "\tasterisk 检查打印=== PAUSE MIXMON ，等待5s，分机A按*1恢复录音，asterisk 检查打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("到队列，no answer的时候，\"destinationTrunk\", \"communicatonType\" 两个字段显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_15_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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

        pjsip.Pj_Send_Dtmf(3001,"3");

        sleep(1000*60);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1000,"*","1");
        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListPause.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1000,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 2 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;
        sleep(1000*5);

        step("挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.QUEUE0_6400.toString(), STATUS.NO_ANSWER.toString(), "Queue Queue0<6400> timed out, failover", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(), "3001<3001> called Queue", SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1000.toString(),"Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR-AllowOptoutofCallRecording")
    @Story("Recording,FeatureCode,PauseRecording,ResumeRecording,CallRecording")
    @Description("16、通过sip外线呼入到IVR-8888,asterisk 打印ivr-greeting-dial-ext.slin 时，主叫按3到Queue0，坐席无人接听，主叫按0到分机1001,1001接听，通话15s时，分机1001按*1暂停录音\n" +
            "\tasterisk 检查不会打印=== PAUSE MIXMON ，等待5s，分机1001按*1恢复录音，asterisk 检查不会打印=== UNPAUSE MIXMON  ；等待5s后挂断通话，检查cdr，正常录音；检查Recording Files正常记录；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("到队列，no answer的时候，\"destinationTrunk\", \"communicatonType\" 两个字段显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "Recording","P3", "IVR-AllowOptoutofCallRecording","FeatureCode","PauseRecording","ResumeRecording","CallRecording"})
    public void testRecording_16_FeatureCode() {
        prerequisite();
        apiUtil.autorecordUpdate(String.format("\"enb_internal\":1,\"internal_prompt\":\"prompt1.wav\",\"outbound_prompt\":\"prompt2.wav\",\"inbound_prompt\":\"prompt3.wav\",\"record_trunk_list\":[],\"record_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editIVR("8888","\"allow_out_record0\":0,\"allow_out_record1\":0,\"allow_out_record2\":0,\"allow_out_record3\":0").apply();
        asteriskObjectList.clear();
        asteriskObjectListPause.clear();
        asteriskObjectListUNPause.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT);
        SSHLinuxUntils.AsteriskThread threadPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListPause,PAUSE_MINMON);
        SSHLinuxUntils.AsteriskThread threadUNPause=new SSHLinuxUntils.AsteriskThread(asteriskObjectListUNPause,PAUSE_MINMON);
        thread.start();
        threadPause.start();
        threadUNPause.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

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

        pjsip.Pj_Send_Dtmf(3001,"3");
        sleep(10000);
        pjsip.Pj_Send_Dtmf(3001,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
        sleep(1000*15);

        pjsip.Pj_Send_Dtmf(1001,"*","1");
        tmp = 0;
        while (asteriskObjectListPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadPause.flag = false;
            Assert.assertFalse(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListPause.size());
        }
        threadPause.flag = false;

        sleep(1000*5);
        pjsip.Pj_Send_Dtmf(1001,"*","1");

        tmp = 0;
        while (asteriskObjectListUNPause.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListUNPause.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListUNPause.get(i).getName() + " [asterisk object time] " + asteriskObjectListUNPause.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListPause.get(i).getTag());
            }
            threadUNPause.flag = false;
            Assert.assertFalse(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListUNPause.size());
        }
        threadUNPause.flag = false;
        sleep(1000*5);

        step("挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", SIPTrunk, "", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), "Queue Queue0<6400>", STATUS.NO_ANSWER.toString(),"Queue Queue0<6400> connected",SIPTrunk,"", "Inbound"))
                .contains(tuple(CDRNAME.Extension_3001.toString(), "IVR IVR-Recording1-8888<8888>", STATUS.ANSWER.toString(),"3001<3001> called Queue",SIPTrunk, "", "Inbound"));

        assertStep("[Recording校验]");
        softAssertPlus.assertThat(apiUtil.getRecordingRecord(1)).as("[Recording校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo","callType")
                .contains(tuple(CDRNAME.Extension_3001.toString(), CDRNAME.Extension_1001.toString(),"Inbound"));

        softAssertPlus.assertAll();
    }
}
