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

import java.time.LocalTime;
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
public class TestVoiceMail extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListSecond = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListExten = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListOperator = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestVoiceMail() {
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
            initTestEnv();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    public void initTestEnv() {
       log.info("Beforeclass\n" +
               "\t分机1000~1005分别上传greeting提示音：\n" +
               "/ysdisk/ysapps/pbxcenter/var/lib/asterisk/sounds/record/1000/\n" +
               "/ysdisk/ysapps/pbxcenter/var/lib/asterisk/sounds/record/1001/\n" +
               "...\n" +
               "\t编辑分机1000的Presence选择Available，Call Forwarding：Internal Calls 勾选No Answer\\When Busy到Voicemail,External Calls 勾选No Answer\\When Busy到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择VoicemailDefaultExt.wav，Available 选择[None]\n" +
               "\t编辑分机0的Presence选择Available，Call Forwarding：Internal CallsAlways到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择VoicemailDefaultExt.wav，Available 选择VoicemailAvailable.wav\n" +
               "\t编辑分机1001的Presence选择Away，Call Forwarding：Internal Calls 勾选Always到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System],Away选择VoicemailAway.wav\n" +
               "\t编辑分机1002的Presence选择Business Trip，Call Forwarding：Internal Calls 勾选Always到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System],Business Trip选择VoicemailBusinessTrip.wav\n" +
               "\t编辑分机1003的Presence选择Do Not Disturb，Call Forwarding：Internal Calls 勾选Always到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System],Do Not Disturb选择VoicemailDND.wav\n" +
               "\t编辑分机1004的Presence选择Lunch Break，Call Forwarding：Internal Calls 勾选Always到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System],Lunch Break选择VoicemailLunchBreak.wav\n" +
               "\t编辑分机1005的Presence选择Off Work，Call Forwarding：Internal Calls 勾选Always到Voicemail,External Calls 勾选Always到Voicemail；\n" +
               "Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System],Off Work选择VoicemailOffWork.wav\n" +
               "\tVoicemail默认不勾选Allow callers to press 0 to break out from voicemail、Allow callers to dial extension、Ask callers to press 5 for leaving a message、Allow callers to review message\n" +

               "\t编辑In1到分机1000的语音留言；\n" +
               "\t编辑Feature Code-》勾选Check Voicemail/Subscribe Voicemail Status ，默认值为*2；\n" +
               "编辑Feature Code-》勾选Leave a Voicemail for an Extension，默认值为*12");

       //todo add wav file to pbx from ssh adapt win/linux  0119
       apiUtil.editExtension("1000","\"vm_greeting\":\"VoicemailDefaultExt.wav\"")
               .editExtension("0","\"vm_greeting\":\"VoicemailDefaultExt.wav\",\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"available\",\"vm_greeting\":\"VoicemailAvailable.wav\"}]")
               .editExtension("1001","\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"away\"}]")
               .editExtension("1002","\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"business_trip\",\"vm_greeting\":\"VoicemailBusinessTrip.wav\"}]")
               .editExtension("1003","\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"do_not_disturb\",\"vm_greeting\":\"VoicemailDND.wav\"}]")
               .editExtension("1004","\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"launch\",\"vm_greeting\":\"VoicemailLunchBreak.wav\"}]")
               .editExtension("1005","\"presence_list\":[{\"enb_in_always_forward\":1,\"enb_ex_always_forward\":1,\"status\":\"off_work\",\"vm_greeting\":\"VoicemailOffWork.wav\"}]")
               .apply();

       apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

       apiUtil.editFeatureCode("\"enb_vm\":1,\"voicemail\":\"*2\",\"enb_leave_vm_for_ext\":1,\"leave_vm_for_ext\":\"*12\"").apply();

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System]\n" +
            "通过sps外线呼入;\n" +
            "\t1.asterisk播放提示音vm-greeting-dial-operator.slin时，按0\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t编辑Call Features-》Voicemail-》不勾选Allow callers to press 0 to break out from voicemail ；\n" +
            "通过sps外线呼入；\n" +
            "asterisk播放提示音vm-greeting-dial-operator.slin时，按0；\n" +
            "\t\t\t\tasterisk不会打印vm-greeting-dial-operator.slin；\n" +
            "分机1000不会响铃，等待20s后挂断，登录分机1000查看新增一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_01_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();

        asteriskObjectList.clear();
        asteriskObjectListSecond.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"vm-greeting-dial-operator.slin");
        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,"vm-greeting-dial-operator.slin");
        thread.start();
        threadSecond.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();
        sleep(10000);

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        auto.homePage().logout();

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        step("编辑Call Features-》Voicemail-》不勾选Allow callers to press 0 to break out from voicemail");
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":0")).apply();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

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
            thread.flag = false;
            Assert.assertFalse(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListSecond.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        assertThat(getExtensionStatus(1000, HUNGUP, 20)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");

        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System]\n" +
            "通过sps外线呼入;\n" +
            "\t2.asterisk播放提示音vm-greeting-dial-ext.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr\n" +
            "\t\t\t编辑Call Features-》Voicemail-》不勾选Allow callers to dial extension；\n" +
            "通过sps外线呼入；\n" +
            "asterisk播放提示音vm-greeting-dial-operator.slin时，按*；\n" +
            "\t\t\t\tasterisk不会打印vm-greeting-dial-ext.slin；\n" +
            "分机1000不会响铃，等待20s后挂断，登录分机1000查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_02_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        asteriskObjectListOperator.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"vm-greeting-dial-ext.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        SSHLinuxUntils.AsteriskThread threadOperator=new SSHLinuxUntils.AsteriskThread(asteriskObjectListOperator,"vm-greeting-dial-operator.slin");
        thread.start();
        threadExten.start();
        threadOperator.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        sleep(5*1000);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");


        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            threadExten.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);
        auto.homePage().logout();

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        step("编辑Call Features-》Voicemail-》不勾选Allow callers to dial extension");
        apiUtil.voicemailUpdate("\"enb_dial_exts\":0").apply();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        tmp = 0;
        while (asteriskObjectListOperator.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectListOperator.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListOperator.get(i).getName() + " [asterisk object time] " + asteriskObjectListOperator.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListOperator.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListOperator.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机0，通过sps外线呼入;\n" +
            "\t3.asterisk播放提示音record/0/VoicemailAvailable.slin时,按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_03_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("0").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/0/VoicemailAvailable.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));
    }


    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机0，通过sps外线呼入;\n" +
            "\t4.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机0登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_04_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("0").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机0，通过sps外线呼入;\n" +
            "\t5.asterisk播放提示音record/0/VoicemailAvailable.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_05_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("0").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/0/VoicemailAvailable.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1001，通过sps外线呼入;\n" +
            "\t6.asterisk播放提示音record/1001/VoicemailAway.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")//todo  bug  set presence  always failed
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_06_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1001/VoicemailAway.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1001，通过sps外线呼入;\n" +
            "\t7.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1001登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_07_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1001",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1001，通过sps外线呼入;\n" +
            "\t8.asterisk播放提示音record/1001/VoicemailAway.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_08_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1001/VoicemailAway.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1002，通过sps外线呼入;\n" +
            "\t9.asterisk播放提示音record/1002/VoicemailBusinessTrip.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_09_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1002").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1002/VoicemailBusinessTrip.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1002，通过sps外线呼入;\n" +
            "\t10.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1002登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_10_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1002").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1002",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1002，通过sps外线呼入;\n" +
            "\t11.asterisk播放提示音record/1002/VoicemailBusinessTrip.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_11_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1002").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1002/VoicemailBusinessTrip.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1003，通过sps外线呼入;\n" +
            "\t12.asterisk播放提示音record/1003/VoicemailDND.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_12_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1003").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1003/VoicemailDND.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }



    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1003，通过sps外线呼入;\n" +
            "\t13.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1003登录查看，新增了一条语音留言\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_13_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1003").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1003",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1003，通过sps外线呼入;\n" +
            "\t14.asterisk播放提示音record/1003/VoicemailDND.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_14_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1003").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1003/VoicemailDND.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1004，通过sps外线呼入;\n" +
            "\t15.asterisk播放提示音record/1004/VoicemailLunchBreak.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_15_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1004/VoicemailLunchBreak.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1004，通过sps外线呼入;\n" +
            "\t16.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1004登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_16_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1004",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1004，通过sps外线呼入;\n" +
            "\t17.asterisk播放提示音record/1004/VoicemailLunchBreak.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_17_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1004/VoicemailLunchBreak.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1005，通过sps外线呼入;\n" +
            "\t18.asterisk播放提示音record/1005/VoicemailOffWork.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_18_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailOffWork.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1005，通过sps外线呼入;\n" +
            "\t19.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1005登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_19_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1005",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1005，通过sps外线呼入;\n" +
            "\t20.asterisk播放提示音record/1005/VoicemailOffWork.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_20_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailOffWork.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1000的语音留言，通过sps外线呼入;\n" +
            "\t21.asterisk播放提示音record/1005/VoicemailDefaultExt.slin时，按0；\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_21_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailDefaultExt.slin");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1000的语音留言，通过sps外线呼入;\n" +
            "\t22.asterisk播放提示音beep.gsm时,按0\n" +
            "\t\t分机1000不会响铃，等待10s,主叫挂断；分机1000登录查看，新增了一条语音留言\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_22_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"beep.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        sleep(20*1000);
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail -》Destination 选择到分机1000\n" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1000"+
            "编辑In1到分机1000的语音留言，通过sps外线呼入;\n" +
            "\t23.asterisk播放提示音record/1005/VoicemailDefaultExt.slin时,按*；\n" +
            "\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_23_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\",\"enb_dial_exts\":1,\"dial_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1000").id)).apply();
        apiUtil.editExtension("1000","\"vm_greeting\":\"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailDefaultExt.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "24.编辑In1到分机0，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/0/VoicemailAvailable.slin时,按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_24_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("0").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/0/VoicemailAvailable.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "25.编辑In1到分机1001，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1001/VoicemailAway.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_25_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1001/VoicemailAway.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "26.编辑In1到分机1002，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1002/VoicemailBusinessTrip.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_26_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1002").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1002/VoicemailBusinessTrip.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "27.编辑In1到分机1003，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1003/VoicemailDND.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_27_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1003").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1003/VoicemailDND.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "28.编辑In1到分机1004，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1004/VoicemailLunchBreak.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_28_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1004").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1004/VoicemailLunchBreak.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "29.编辑In1到分机1005，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1005/VoicemailOffWork.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_29_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailOffWork.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "30.编辑In1到分机1000的语音留言，通过sps外线呼入;\n" +
            "\tasterisk播放提示音record/1005/VoicemailDefaultExt.slin时，按0；\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_30_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailOffWork.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press0" +
            "编辑Call Features-》Voicemail-》勾选Allow callers to press 0 to break out from voicemail" +
            "Destination 选择到ivr-IVR0"+
            "31.编辑In1到分机1000的语音留言；\n" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System]\n" +
            "通过sps外线呼入;\n" +
            "\tasterisk播放提示音vm-greeting-dial-operator.slin时，按0\n" +
            "\t\tasterisk播放提示音ivr-greeting-dial-ext.slin，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_31_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"ivr\",\"press0_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).
                voicemailUpdate("\"vm_greeting\": \"follow_system\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"vm-greeting-dial-operator.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press*" +
            "32.编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机1001\n" +
            "\t编通过sps外线呼入;\n" +
            "\t\tasterisk播放提示音record/1005/VoicemailDefaultExt.slin时,按*；\n" +
            "\t\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，通话被挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_32_CallerOptions() {
        prerequisite();
        apiUtil.voicemailUpdate(String.format("\"enb_press0\":1,\"press0_dest\":\"extension\",\"press0_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1001").id)).apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        asteriskObjectList.clear();
        asteriskObjectListExten.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"record/1005/VoicemailDefaultExt.slin");
        SSHLinuxUntils.AsteriskThread threadExten=new SSHLinuxUntils.AsteriskThread(asteriskObjectListExten,"WaitExten");
        thread.start();
        threadExten.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"*");

        tmp = 0;
        while (asteriskObjectListExten.size() != 1 && tmp <=600) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectListExten.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListExten.get(i).getName() + " [asterisk object time] " + asteriskObjectListExten.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListExten.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListExten.size());
        }
        threadExten.flag = false;
        pjsip.Pj_Send_Dtmf(2000,"1","0","0","0");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 20)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_2000.toString(), CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press*" +
            "33.编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机\tExGroup1\n" +
            "\t通过sps外线呼入;\n" +
            "\t\tasterisk播放提示音record/1005/VoicemailDefaultExt.slin时,按*；\n" +
            "\t\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_33_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("press*" +
            "34.编辑Call Features-》Voicemail-》勾选Allow callers to dial extension 选择分机\tDefault_Extension_Group\n" +
            "\t通过sps外线呼入;\n" +
            "\t\tasterisk播放提示音record/1005/VoicemailDefaultExt.slin时,按*；\n" +
            "\t\t\tasterisk检测打印“vm-operate-dial-exten”时，按1000，分机1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions",""})
    public void testVoicemail_34_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("press5")
    @Description("编辑Call Features-》Voicemail-》勾选Ask callers to press 5 for leaving a message" +
            "35.通过sps外线呼入\n" +
            "\tasterisk检查播放了3遍record/1000/VoicemailDefaultExt.slin 后，通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","press5",""})
    public void testVoicemail_35_press5() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("press5")
    @Description("编辑Call Features-》Voicemail-》勾选Ask callers to press 5 for leaving a message" +
            "36.通过sps外线呼入\n" +
            "\tasterisk检查第2次播放record/1000/VoicemailDefaultExt.slin 时按5\n" +
            "\t\tasterisk 检查打印beep.gsm，等待10秒，主叫挂断；分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","press5",""})
    public void testVoicemail_36_press5() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("press5")
    @Description("编辑Call Features-》Voicemail-》勾选Ask callers to press 5 for leaving a message" +
            "37.编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting选择[Follow System]\n" +
            "通过sps外线呼入;\n" +
            "\tasterisk检查播放了3遍vm-greeting-leave-press5.slin，播放第3遍时按5，asterisk 检查打印beep.gsm，等待10秒，主叫挂断；分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","press5",""})
    public void testVoicemail_37_press5() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "38.asterisk检测到beep.gsm后，等待10秒，按#；asterisk播放提示音：review-save.slin、review-listen.slin、review-recordagain.slin、press-pound-cancel-and-exit.slin，按1\n" +
            "\tasterisk检查播放vm-msg-saved.slin ，主叫被挂断，分机1000登录查看新增了1条语音留言\n" +
            "\t\t编辑Call Features-》Voicemail-》不勾选Allow callers to review message；\n" +
            "通过sps外线呼入；asterisk检测到beep.gsm后，等待10秒，按#；\n" +
            "\t\t\tasterisk不会播放提示音：review-save.slin，主叫被挂断；分机1000登录查看新增一条语音留言；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","ReviewMessage",""})
    public void testVoicemail_38_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "39.asterisk检测到beep.gsm后，等待10秒，按#；asterisk播放提示音：review-save.slin、review-listen.slin、review-recordagain.slin、press-pound-cancel-and-exit.slin，按2\n" +
            "\tasterisk打印Reviewing the message、再次打印review-save.slin时，按1\n" +
            "\t\tasterisk检查播放vm-msg-saved.slin ，主叫被挂断，分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","ReviewMessage",""})
    public void testVoicemail_39_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "40.asterisk检测到beep.gsm后，等待10秒，按#；asterisk播放提示音：review-save.slin、review-listen.slin、review-recordagain.slin、press-pound-cancel-and-exit.slin，按3\n" +
            "\tasterisk打印Re-recording the message、再次打印review-save.slin时，按1\n" +
            "\t\tasterisk检查播放vm-msg-saved.slin ，主叫被挂断，分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","ReviewMessage",""})
    public void testVoicemail_40_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CallerOptions")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "41.asterisk检测到beep.gsm后，等待10秒，按#；asterisk播放提示音：review-save.slin、review-listen.slin、review-recordagain.slin、press-pound-cancel-and-exit.slin，按#\n" +
            "\t主叫被挂断，分机1000登录查看没有新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CallerOptions","ReviewMessage",""})
    public void testVoicemail_41_CallerOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("MaxMessageTime")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "42.编辑Call Features-》Voicemail-》Message Options-》Max Message Time选择60s\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk检测到beep.gsm后,再过60s，主叫被挂断；分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "MessageOptions","MaxMessageTime",""})
    public void testVoicemail_42_MaxMessageTime() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("MaxMessageTime")
    @Description("编辑Call Features-》Voicemail-》勾选Allow callers to review message,通过sps外线呼入" +
            "43.编辑Call Features-》Voicemail-》Message Options-》Max Message Time选择600s\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk检测到beep.gsm后,再过70s，主叫状态依然是Talking，主叫挂断；分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "MessageOptions","MaxMessageTime",""})
    public void testVoicemail_43_MaxMessageTime() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("MinMessageTime")
    @Description("编辑Call Features-》Voicemail-》Message Options-》Min Message Time 选择5s\n" +
            "\t44.通过sps外线呼入\n" +
            "\t\tasterisk检测到beep.gsm后,过1s，主叫被挂断；分机1000登录查看没有新增了1条语音留言\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "MessageOptions","MinMessageTime",""})
    public void testVoicemail_44_MinMessageTime() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("MinMessageTime")
    @Description("编辑Call Features-》Voicemail-》Message Options-》Min Message Time 选择5s\n" +
            "\t45.通过sps外线呼入\n" +
            "\t\tasterisk检测到beep.gsm后,过6s，主叫被挂断；分机1000登录查看新增了1条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "MessageOptions","MinMessageTime",""})
    public void testVoicemail_45_MinMessageTime() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("GreetingOptions")
    @Description("编辑Call Features-》Voicemail-》Caller Options 4个选项都不勾选；\n" +
            "编辑Call Features-》Voicemail-》Greeting Options->Max Greeting Time 选择30s；\n" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting 选择VoicemailMaxDuration30.wav；\n" +
            "\t46.通过sps外线呼入\n" +
            "\t\t30s后asterisk打印\n" +
            "Prompt is too long, cut short, max prompt time: 30\n" +
            "进入到语音留言，等待10s；主叫挂断；分机1000登录查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_46_GreetingOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("GreetingOptions")
    @Description("编辑Call Features-》Voicemail-》Caller Options 4个选项都不勾选；\n" +
            "编辑Call Features-》Voicemail-》Greeting Options->Max Greeting Time 选择30s；\n" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting 选择[Follow System]；Available 选择VoicemailMaxDuration30.wav；\n" +
            "\t47.通过Account外线呼入\n" +
            "\t\t30s后asterisk打印\n" +
            "Prompt is too long, cut short, max prompt time: 30\n" +
            "进入到语音留言，等待10s；主叫挂断；分机1000登录查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_47_GreetingOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("GreetingOptions")
    @Description("编辑Call Features-》Voicemail-》Caller Options 4个选项都不勾选；\n" +
            "编辑Call Features-》Voicemail-》Greeting Options->Max Greeting Time 选择30s；Global Voicemail Greeting 选择VoicemailMaxDuration30.wav；\n" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting 选择[Follow System]；Available 选择[None]；\n" +
            "\t48.通过sps外线呼入\n" +
            "\t\t30s后asterisk打印\n" +
            "Prompt is too long, cut short, max prompt time: 30\n" +
            "进入到语音留言，等待10s；主叫挂断；分机1000登录查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_48_GreetingOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("GreetingOptions")
    @Description("编辑Call Features-》Voicemail-》Caller Options 4个选项只勾选Ask callers to press 5 for leaving a message；\n" +
            "编辑Call Features-》Voicemail-》Greeting Options->Max Greeting Time 选择30s；Global Voicemail Greeting 选择VoicemailMaxDuration30.wav；\n" +
            "编辑分机1000的Voicemail-》Voicemail Greeting-》Default Greeting 选择[Follow System]；Available 选择[None]；\n" +
            "\t49.通过sps外线呼入\n" +
            "\t\t每隔30s，asterisk打印\n" +
            "Prompt is too long, cut short, max prompt time: 30 \n" +
            "1次，总的打印了3次后，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_49_GreetingOptions() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("IVR,DialtoCheckVoicemail")
    @Description("1）新建IVR-Voicemail-6201，勾选Dial to Check Voicemail；\n" +
            "2）编辑In1，呼入目的地为IVR-Voicemail-6201\n" +
            "\t50.通过sps外线呼入\n" +
            "\t\tasterisk打印ivr-greeting-dial-ext.slin时输入*21000#查看分机1000的语音留言；\n" +
            "asterisk打印vm-enterpin.slin时输入PIN码1000#；\n" +
            "asterisk 打印提示音：vm-youhave.slin、vm-main-play.slin、vm-main-greeting.slin、vm-main-changepin.slin、vm-main-recordname.slin、vm-main-mailboxoptions.slin、press-pound-exit.slin\n" +
            "\t\t\t按#退出语音信箱，\n" +
            "\t\t\t\tasterisk打印vm-goodbye.slin，通话被挂断；检查cdr\n" +
            "\t\t\t按其他键测试相关功能暂不测试")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_50_IVR() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("IVR,DialtoCheckVoicemail")
    @Description("1）新建IVR-Voicemail-6201，不勾选Dial to Check Voicemail；\n" +
            "2）编辑In1，呼入目的地为IVR-Voicemail-6201\n" +
            "\t51.通过sps外线呼入\n" +
            "\t\tasterisk打印ivr-greeting-dial-ext.slin时输入*21000#查看分机1000的语音留言；\n" +
            "\t\t\t通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "GreetingOptions","",""})
    public void testVoicemail_51_IVR() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CheckVoicemail")
    @Description("分机1000拨打*2查看语音留言\n" +
            "\t52.asterisk打印vm-enterpin.slin时输入PIN码1000#；\n" +
            "\t\tasterisk 打印提示音：vm-youhave.slin、vm-main-play.slin、vm-main-greeting.slin、vm-main-changepin.slin、vm-main-recordname.slin、vm-main-mailboxoptions.slin、press-pound-exit.slin；\n" +
            "挂断通话；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CheckVoicemail","",""})
    public void testVoicemail_52_CheckVoicemail() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CheckVoicemail")
    @Description("分机1000拨打*2查看语音留言\n" +
            "\t53.asterisk打印vm-enterpin.slin时输入PIN码1001#；\n" +
            "\t\tasterisk打印提示音：vm-incorrect.slin 再次输入PIN码1000#；asterisk 打印提示音：vm-youhave.slin、vm-main-play.slin、vm-main-greeting.slin、vm-main-changepin.slin、vm-main-recordname.slin、vm-main-mailboxoptions.slin、press-pound-exit.slin；挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CheckVoicemail","",""})
    public void testVoicemail_53_CheckVoicemail() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CheckVoicemail")
    @Description("54.编辑Feature Code-》不勾选Check Voicemail/Subscribe Voicemail Status ，默认值为*2；\n" +
            "\t分机1000拨打*2查看语音留言\n" +
            "\t\t通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CheckVoicemail","",""})
    public void testVoicemail_54_CheckVoicemail() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("CheckVoicemail")
    @Description("55.编辑Feature Code-》Check Voicemail/Subscribe Voicemail Status 值为*232323\n" +
            "\t分机1000拨打*232323 查看语音留言\n" +
            "\t\tasterisk打印vm-enterpin.slin时输入PIN码1000#；\n" +
            "\t\t\tasterisk 打印提示音：vm-youhave.slin、vm-main-play.slin、vm-main-greeting.slin、vm-main-changepin.slin、vm-main-recordname.slin、vm-main-mailboxoptions.slin、press-pound-exit.slin；\n" +
            "挂断通话；检查cdr\n" +
            "\t\t\t\t编辑Feature Code-》Check Voicemail/Subscribe Voicemail Status 值为*2；\n" +
            "分机1000拨打*232323 查看语音留言\n" +
            "\t\t\t\t\t通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "CheckVoicemail","",""})
    public void testVoicemail_55_CheckVoicemail() {

    }


    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("LeaveaVoicemailforanExtension")
    @Description("56.编辑Call Features-》Voicemail-》勾选Ask callers to press 5 for leaving a message\n" +
            "\t分机1000拨打*121001给分机1001语音留言\n" +
            "\t\tasterisk打印2遍record/1001/VoicemailAway.slin 后按5开始留言，通话10s后，挂断通话；分机1001登录查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "LeaveaVoicemailforanExtension",""})
    public void testVoicemail_56_LeaveaVoicemailforanExtension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("LeaveaVoicemailforanExtension")
    @Description("57.编辑Call Features-》Voicemail-》不勾选Ask callers to press 5 for leaving a message\n" +
            "\t分机1000拨打*121001给分机1001语音留言\n" +
            "\t\tasterisk打印record/1001/VoicemailAway.slin、beep.gsm 后开始给分机1001留言，通话10s后 挂断通话；分机1001登录检查新增一条语音留言；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "LeaveaVoicemailforanExtension",""})
    public void testVoicemail_57_LeaveaVoicemailforanExtension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("LeaveaVoicemailforanExtension")
    @Description("58.编辑Feature Code-》不勾选Leave a Voicemail for an Extension \n" +
            "编辑Call Features-》Voicemail-》不勾选Ask callers to press 5 for leaving a message\n" +
            "\t分机1000拨打*121001给分机1001语音留言\n" +
            "\t\t通话直接被挂断\n" +
            "\t\t\t编辑Feature Code-》勾选Leave a Voicemail for an Extension ；\n" +
            "分机1000拨打*121001给分机1001语音留言；\n" +
            "\t\t\t\tasterisk打印record/1001/VoicemailAway.slin后挂断通话；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "LeaveaVoicemailforanExtension",""})
    public void testVoicemail_58_LeaveaVoicemailforanExtension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("LeaveaVoicemailforanExtension")
    @Description("59.编辑Feature Code-》Leave a Voicemail for an Extension 修改值为*121212\n" +
            "编辑Call Features-》Voicemail-》不勾选Ask callers to press 5 for leaving a message\n" +
            "\t分机1000拨打*1212121001给分机1001语音留言\n" +
            "\t\tasterisk打印record/1001/VoicemailAway.slin、beep.gsm 后开始给分机1001留言，通话10s后 挂断通话；分机1001登录检查新增一条语音留言；\n" +
            "\t\t\t编辑Feature Code-》Leave a Voicemail for an Extension 修改值为*12；\n" +
            "分机1000拨打*1212121001给分机1001语音留言；\n" +
            "\t\t\t\t通话直接被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "LeaveaVoicemailforanExtension",""})
    public void testVoicemail_59_LeaveaVoicemailforanExtension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("Extension")
    @Description("修改分机1000的email为18850185120@163.com；\n" +
            "voicemail页面，启用voicemail，启用voicemail pin Authentication，pin码设置为1000，New Voicemail Notification设置为send email notification with attachment，afternotification设置为Do Nothing，勾选play date  and time/caller id/durations，voicemail greeting 默认（default为follow system，available等presence状态保持默认none），保存并应用" +
            "60.修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail\n" +
            "\t2001通过 sps trunk呼入，进入分机1000-voicemail,留言2min后挂断通话\n" +
            "\t\t留言成功，cdr记录正确;\n" +
            "18850185120@163.com收到New Voicemail的邮箱通知;\n" +
            "分机1000登录webclient，voicemail页面未新增一条来自2001未读的留言记录")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "Extension",""})
    public void testVoicemail_60_Extension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("Extension")
    @Description("修改分机1000的email为18850185120@163.com；\n" +
            "voicemail页面，启用voicemail，启用voicemail pin Authentication，pin码设置为1000，New Voicemail Notification设置为send email notification with attachment，afternotification设置为Do Nothing，勾选play date  and time/caller id/durations，voicemail greeting 默认（default为follow system，available等presence状态保持默认none），保存并应用" +
            "61.修改New Voicemail Notification设置为send email notification without attachment，afternotification设置为mark as read\n" +
            "\t2001通过 sps trunk呼入，进入分机1000-voicemail,留言2min后挂断通话\n" +
            "\t\t留言成功，cdr记录正确;\n" +
            "18850185120@163.com收到New Voicemail的邮箱通知;\n" +
            "分机1000登录webclient，voicemail页面新增一条已读的来自2001的语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "Extension",""})
    public void testVoicemail_61_Extension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("Extension")
    @Description("修改分机1000的email为18850185120@163.com；\n" +
            "voicemail页面，启用voicemail，启用voicemail pin Authentication，pin码设置为1000，New Voicemail Notification设置为send email notification with attachment，afternotification设置为Do Nothing，勾选play date  and time/caller id/durations，voicemail greeting 默认（default为follow system，available等presence状态保持默认none），保存并应用" +
            "62.修改New Voicemail Notification设置为do not send email notification\n" +
            "\t2001通过 sps trunk呼入，进入分机1000-voicemail,留言2min后挂断通话\n" +
            "\t\t留言成功，cdr记录正确;\n" +
            "18850185120@163.com未收到New Voicemail的邮箱通知;\n" +
            "分机1000登录webclient，voicemail页面新增一条未读的来自2001的语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "Extension",""})
    public void testVoicemail_62_Extension() {

    }

    @Epic("P_Series")
    @Feature("Voicemail")
    @Story("Extension")
    @Description("修改分机1000的email为18850185120@163.com；\n" +
            "voicemail页面，启用voicemail，启用voicemail pin Authentication，pin码设置为1000，New Voicemail Notification设置为send email notification with attachment，afternotification设置为Do Nothing，勾选play date  and time/caller id/durations，voicemail greeting 默认（default为follow system，available等presence状态保持默认none），保存并应用" +
            "63.禁用voicemail，保存并应用\n" +
            "\t2001通过 sps trunk呼入\n" +
            "\t\t通话被直接挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "Voicemail","P3", "Extension",""})
    public void testVoicemail_63_Extension() {

    }


    public void testcpvoice(){
        String commond = "sshpass -p '' scp -P 8022 /home/pbxUpdateImage/uImage ls@yf@${PBX_IP}:/ysdisk/imageupdate/";


    }
}
