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
    private boolean isRunRecoveryEnvFlag = false;
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
        log.info("Before"+
	                "添加速拨码 1，PhoneNumber:13001"+
	                "添加速拨码 81，PhoneNumber:813001"+
	                "添加速拨码 82，PhoneNumber:823001"+
	                "添加速拨码 83，PhoneNumber:823001");

        apiUtil.deleteAllSpeedDial().
            createSpeeddial("\"code\":\"1\",\"phone_number\":\"13001\"").
            createSpeeddial("\"code\":\"81\",\"phone_number\":\"813001\"").
            createSpeeddial("\"code\":\"82\",\"phone_number\":\"823001\"").
            createSpeeddial("\"code\":\"83\",\"phone_number\":\"833001\"")
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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", "Trunk"})
    public void testSpeedDial_04_Trunk() {
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound","EmergencySIP"));

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
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", "Trunk"})
    public void testSpeedDial_05_Trunk() {
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound","EmergencySIP"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("SpeedDial")
    @Story("Trunk")
    @Description("6.添加速拨码 *#12，PhoneNumber：62000\n" +
            "\t分机B拨打*89*#12\n" +
            "\t\t通过E1外线呼出，辅助2的分机2000响铃，接听，被叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", "Trunk"})
    public void testSpeedDial_06_Trunk() {
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo",  "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType","dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001",  CDRObject.STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound","EmergencySIP"));

        softAssertPlus.assertAll();
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
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
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
        prerequisite();
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

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
        initTestEnv();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] ");
        pjsip.Pj_Make_Call_No_Answer(1000, "", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime())
                .isEqualTo(TALKING);

        step("主叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime())
                .extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk",
                        "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "3001", CDRObject.STATUS.ANSWER.toString(),
                        CDRNAME.Extension_1000.toString() + " hung up", "", SIPTrunk, "Outbound", "EmergencySIP"));

        softAssertPlus.assertAll();
    }



}
