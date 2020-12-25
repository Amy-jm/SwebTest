package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import com.yeastar.untils.CDRObject.*;

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test Speed Dial
 * @author: huangjx@yeastar.com
 * @create: 2020/12/03
 */
@Log4j2
public class TestSpeedDial extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestSpeedDial() {
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
            initTestEnv();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }


    public void initTestEnv() {
        log.info("Before"+
	                "添加速拨码 1，PhoneNumber:13001"+
	                "添加速拨码 81，PhoneNumber:813001"+
	                "添加速拨码 82，PhoneNumber:823001"+
	                "添加速拨码 83，PhoneNumber:823001");

        apiUtil.deleteAllSpeedDial().
                createSpeeddial("\"code\":\"1\",\"phone_number\":\"13001\"").
                createSpeeddial("\"code\":\"81\",\"phone_number\":\"813001\"").
                createSpeeddial("\"code\":\"82\",\"phone_number\":\"823001\"").
                createSpeeddial("\"code\":\"83\",\"phone_number\":\"833001\"").
                editFeatureCode("\"speed_dial\":\"*89\"")//设置prefix为默认  *89
        .apply();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("1.分机A拨打*891  通过sip外线呼出，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P2", "Trunk"})
    public void testSpeedDial_01_Trunk() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1000, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "13001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    
    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("2.添加速拨码 # ，PhoneNumber： 21234567890\n" +
            "\t分机B拨打*89#\n" +
            "\t\t通过sps外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial", "P3", "Trunk" })
    public void testSpeedDial_02_Trunk() {
        prerequisite();
        apiUtil.deleteSpeedDial("#").createSpeeddial("\"code\":\"#\",\"phone_number\":\"21234567890\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *89#");
        pjsip.Pj_Make_Call_No_Answer(1001, "*89#", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "21234567890", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    
    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("3.添加速拨码 *，PhoneNumber：33333\n" +
            "\t分机C拨打*89*\n" +
            "\t\t通过Account外线呼出，辅助3的分机4000响铃，接听，主叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial", "P3", "Trunk" })
    public void testSpeedDial_03_Trunk() {
        prerequisite();
        apiUtil.deleteSpeedDial("*").createSpeeddial("\"code\":\"*\",\"phone_number\":\"33333\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] *89*");
        pjsip.Pj_Make_Call_No_Answer(1002, "*89*", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);

        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "33333", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("4.添加速拨码 5，PhoneNumber：42000\n" +
            "\t分机B拨打*895\n" +
            "\t\t通过FXO外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "SpeedDial","P3", "Trunk"})
    public void testSpeedDial_04_Trunk() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();
        apiUtil.deleteSpeedDial("5").createSpeeddial("\"code\":\"5\",\"phone_number\":\"42000\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *895");
        pjsip.Pj_Make_Call_No_Answer(1001, "*895", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "42000",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", FXO_1, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("5.添加速拨码 55，PhoneNumber：52000\n" +
            "\t分机B拨打*8955\n" +
            "\t\t通过BRI外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "SpeedDial","P3", "Trunk"},enabled = false)
    public void testSpeedDial_05_Trunk() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        apiUtil.deleteSpeedDial("55").createSpeeddial("\"code\":\"55\",\"phone_number\":\"52000\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *8955");
        pjsip.Pj_Make_Call_No_Answer(1001, "*8955", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "52000",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", BRI_1, "Outbound"));

        softAssertPlus.assertAll();
        //TODO  call to
        /**
         * 1) [[CDR校验] Time：2020-12-23 16:22:13]
         * Expecting ArrayList:
         *  <[("test2 B<1001>", "2000", "ANSWERED", "test2 B<1001> hung up", "", "BRI1-7", "Outbound")]>
         * to contain:
         *  <[("test2 B<1001>", "52000", "ANSWERED", "test2 B<1001> hung up", "", "BRI1-7", "Outbound")]>
         * but could not find the following element(s):
         *  <[("test2 B<1001>", "52000", "ANSWERED", "test2 B<1001> hung up", "", "BRI1-7", "Outbound")]>
         */
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("6.添加速拨码 *#12，PhoneNumber：62000\n" +
            "\t分机B拨打*89*#12\n" +
            "\t\t通过E1外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常】https://www.tapd.cn/32809406/bugtrace/bugs/view?bug_id=1132809406001036056")
    @Test(groups = {"PSeries", "SpeedDial","P3", "Trunk"},enabled = false)
    public void testSpeedDial_06_Trunk() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1线路 不测试！");
        }
        prerequisite();
        apiUtil.deleteSpeedDial("*#12").createSpeeddial("\"code\":\"*#12\",\"phone_number\":\"62000\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *89*#12");
        pjsip.Pj_Make_Call_No_Answer(1001, "*89*#12", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "62000",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", E1, "Outbound"));

        softAssertPlus.assertAll();
        //todo call to
        /**
         * 1) [[CDR校验] Time：2020-12-23 16:25:24]
         * Expecting ArrayList:
         *  <[("test2 B<1001>", "2000", "ANSWERED", "test2 B<1001> hung up", "", "DIGIT2", "Outbound")]>
         * to contain:
         *  <[("test2 B<1001>", "62000", "ANSWERED", "test2 B<1001> hung up", "", "DIGIT2", "Outbound")]>
         * but could not find the following element(s):
         *  <[("test2 B<1001>", "62000", "ANSWERED", "test2 B<1001> hung up", "", "DIGIT2", "Outbound")]>
         */
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("7.添加速拨码 1234，PhoneNumber：7+辅助2的GSM号码\n" +
            "\t分机B拨打*891234\n" +
            "\t\t通过GSM外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", "Trunk"})
    public void testSpeedDial_07_Trunk() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        apiUtil.deleteSpeedDial("1234").createSpeeddial("\"code\":\"1234\",\"phone_number\":\"7"+DEVICE_ASSIST_GSM+"\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 7"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1001, "7"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 120)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "7"+DEVICE_ASSIST_GSM, CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", GSM, "Outbound"));

        softAssertPlus.assertAll();
    }
    
    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("FeatureCode")
    @Description("编辑Call Features-》Speed Dial-》Prefix 为*1238*3;\n" +
            "添加速拨码2，PhoneNumber:23001" +
            "8.分机A拨打*892\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial","FeatureCode", "P3", "" })
    public void testSpeedDial_08_FeatureCode() {
        prerequisite();
        apiUtil.editFeatureCode("\"speed_dial\":\"*1238*3\"").deleteSpeedDial("2").createSpeeddial("\"code\":\"2\",\"phone_number\":\"23001\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *892");
        pjsip.Pj_Make_Call_No_Answer(1000, "*892", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("FeatureCode")
    @Description("编辑Call Features-》Speed Dial-》Prefix 为*1238*3;\n" +
            "添加速拨码2，PhoneNumber:23001" +
            "9.分机A拨打*1238*32\n" +
            "\t通过sps外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr\n" +
            "\t\t编辑Call Features-》Feature Code-》Speed Dial Prefix 为*89；\n" +
            "分机A拨打*892；\n" +
            "\t\t\t通过sps外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial","FeatureCode", "P3", "" })
    public void testSpeedDial_09_FeatureCode() {
        prerequisite();
        apiUtil.editFeatureCode("\"speed_dial\":\"*1238*3\"").deleteSpeedDial("2").createSpeeddial("\"code\":\"2\",\"phone_number\":\"23001\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *1238*32");
        pjsip.Pj_Make_Call_No_Answer(1000, "*1238*32", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "23001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        apiUtil.editFeatureCode("\"speed_dial\":\"*89\"").apply();
        step("2:[caller] 1000" + ",[callee] *892");
        pjsip.Pj_Make_Call_No_Answer(1000, "*892", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "23001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("FeatureCode")
    @Description("10.编辑Call Features-》Feature Code-》Speed Dial Prefix  禁用；\n" +
            "添加速拨码3，PhoneNumber:23001\n" +
            "\t分机A拨打*893\n" +
            "\t\t呼出失败，通话被挂断\n" +
            "\t\t\t编辑Call Features-》Feature Code-》Speed Dial Prefix  启用；\n" +
            "分机A拨打*893；\n" +
            "\t\t\t\t通过sps外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2","SpeedDial", "FeatureCode", "P3", "" })
    public void testSpeedDial_10_FeatureCode() {
        prerequisite();
        apiUtil.editFeatureCode("\"enb_speed_dial\": 0").deleteSpeedDial("3").createSpeeddial("\"code\":\"3\",\"phone_number\":\"23001\"").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *893");
        pjsip.Pj_Make_Call_No_Answer(1000, "*893", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);

        apiUtil.editFeatureCode("\"enb_speed_dial\": 1").apply();
        step("2:[caller] 1000" + ",[callee] *893");
        pjsip.Pj_Make_Call_No_Answer(1000, "*893", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "23001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("OutboundRoutePassword")
    @Description("11.新建呼出路由OutSpeedDial1，呼出模式81.，选择sps外线，选择所有分机，启用OutboundRoute Password，密码设置为123；\n" +
            "\t分机A拨打*8981\n" +
            "\t\tasterisk后台检测到提示音enter-password.gsm时，输入密码123# ；\n" +
            "辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial","OutboundRoutePassword", "P3", "" })
    public void testSpeedDial_11_OutboundRoutePassword() {
        prerequisite();
        apiUtil.deleteOutbound("OutSpeedDial1").createOutbound("OutSpeedDial1",asList(SPS), asList("Default_Extension_Group"), "81.", 0).
                editOutbound("OutSpeedDial1","\"pin_protect\":\"single_pin\",\"pin\":\"123\"").apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"enter-password.gsm");
        thread.start();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *8981");
        pjsip.Pj_Make_Call_No_Answer(1001, "*8981", DEVICE_IP_LAN, false);

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
        pjsip.Pj_Send_Dtmf(1001, "123#");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "813001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Role")
    @Description("新建呼出路由OutSpeedDial2，Role 选择Supervisor，呼出模式82.，选择sps外线，不选分机\n" +
            "\t12.分机A-1000拨打*8982\n" +
            "\t\t呼出失败，通话被挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Role", "SpeedDial","P3", "" })
    public void testSpeedDial_12_Role() {
        prerequisite();
        apiUtil.deleteOutbound("OutSpeedDial2").createOutbound("OutSpeedDial2", asList(SPS), asList(""), "82.", 0).
                editOutbound("OutSpeedDial2", "\"role_list\":[{\"value\":\"2\",\"text\":\"Supervisor\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *8982");
        pjsip.Pj_Make_Call_No_Answer(1000, "*8982", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Role")
    @Description("新建呼出路由OutSpeedDial2，Role 选择Supervisor，呼出模式82.，选择sps外线，不选分机\n" +
            "\t13.分机B-1001拨打*8982\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Role","SpeedDial", "P3", "" })
    public void testSpeedDial_13_Role() {
        prerequisite();
        apiUtil.deleteOutbound("OutSpeedDial2").createOutbound("OutSpeedDial2", asList(SPS), asList(""), "82.", 0).
                editOutbound("OutSpeedDial2", "\"role_list\":[{\"value\":\"2\",\"text\":\"Supervisor\"}]").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] *8982");
        pjsip.Pj_Make_Call_No_Answer(1001, "*8982", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "823001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Role")
    @Description("新建呼出路由OutSpeedDial3，呼出模式83.，选择sps外线，只选分机C-1002\n" +
            "\t15.分机C-1002拨打*8983\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Role","SpeedDial", "P3", "" })
    public void testSpeedDial_14_Role() {
        prerequisite();
        apiUtil.deleteOutbound("OutSpeedDial3").createOutbound("OutSpeedDial3", asList(SPS), asList("1002"), "83.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] *8983");
        pjsip.Pj_Make_Call_No_Answer(1002, "*8983", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "833001", CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Role")
    @Description("新建呼出路由OutSpeedDial3，呼出模式83.，选择sps外线，只选分机C-1002\n" +
            "\t14.分机A-1000拨打*8983\n" +
            "\t\t呼出失败，通话被挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Role","SpeedDial", "P3", "" })
    public void testSpeedDial_15_Role() {
        prerequisite();
        apiUtil.deleteOutbound("OutSpeedDial3").createOutbound("OutSpeedDial3", asList(SPS), asList("1002"), "83.", 0).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] *8983");
        pjsip.Pj_Make_Call_No_Answer(1000, "*8983", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("DisableOutboundCalls")
    @Description("16.编辑分机D-1003，启用Disable Outbound Calls\n" +
            "\t分机D-1003拨打*891\n" +
            "\t\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "DisableOutboundCalls","SpeedDial", "P3", "" })
    public void testSpeedDial_16_DisableOutboundCalls() {
        prerequisite();
        apiUtil.editExtension("1003","\"disable_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1003, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1003, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("DisableOutboundCallsoutsideBusinessHours")
    @Description("17.编辑分机1004，启用Disable Outbound Calls outside Business Hours；\n" +
            "Business Hours and Holidays-》添加时间条件00:00-00:00，选择sun ;\n" +
            "\t分机1004拨打*891\n" +
            "\t\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "DisableOutboundCallsoutsideBusinessHours","SpeedDial", "P3", "" })
    public void testSpeedDial_17_DisableOutboundCallsoutsideBusinessHours() {
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        resetTimes.add("");

        prerequisite();
        apiUtil.editExtension("1004","\"disable_office_time_outb_call\":1").apply();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] *891");
        pjsip.Pj_Make_Call_No_Answer(1004, "*891", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP, IDLE);
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Delete")
    @Description("18.单条删除速拨码1\n" +
            "\t检查列表速拨码1被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Delete","SpeedDial", "P2", "" })
    public void testSpeedDial_18_Delete() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:进入界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_speed_dial);
        sleep(3000);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Speed Dial Number");
        if(!lists.contains("81")){
            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).createSpeeddial("\"code\":\"81\",\"phone_number\":\"813001\"").apply();
        }
        step("3:删除");
        auto.speedDialPage().deleDataByDeleImage("81").clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Speed Dial Number");
        softAssertPlus.assertThat(list).doesNotContain("81");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Delete")
    @Description("19.批量删除所有速拨码\n" +
            "\t检查列表速拨码列表全部删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "Delete","SpeedDial", "P2", "" })
    public void testSpeedDial_19_Delete() {
        prerequisite();
        apiUtil.deleteAllSpeedDial().apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:进入界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_speed_dial);

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Speed Dial Number");
        softAssertPlus.assertThat(list.size()).isEqualTo(0);
        softAssertPlus.assertAll();
    }



}
