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
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", ""})
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "SpeedDial", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", ""})
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", ""})
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", ""})
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SpeedDial","P3", ""})
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
    @Description("")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = { "PSeries", "Cloud", "K2", "", "P3", "" })
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
