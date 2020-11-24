package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.anyOf;

/**
 * @program: SwebTest
 * @description: test inbound route based on global business hours
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteGlobalBusiness extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestInboundRouteGlobalBusiness() {
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
            initIVR();
//            initInbound();
            initOutbound();
            initFeatureCode();

            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");

        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    /**
     * Business Hours and Holidays->Business Hours 删除所有；添加一条
     * 00:00-23:59，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     *
     * 编辑呼入路由In1，启用Time Condition，Time-based Routing Mode选择“Based on Global Business Hours",Business Hours Destination 选择分机1001，Outside Business Hours Destination选择分机1002，Holidays Destination 选择分机1003;
     *
     */
    public void initBusinessHoursAndIn1() {
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-23:59");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        //init In1
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1",String.format("\"enb_time_condition\":1,\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"",
                        apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id, apiUtil.getExtensionSummary("1003").id)).apply();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("1.通过sps外线呼入\n" +
            "\t呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr\n" +
            "\t\t分机1000拨打*99切换为下班状态；\n" +
            "通过sps外线呼入；\n" +
            "\t\t\t呼入到下班目的地，分机1002响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t分机1000拨打*99切换回上班状态；\n" +
            "通过sps外线呼入；\n" +
            "\t\t\t\t\t呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P2"})
    public void testIR_01_HoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat( apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("0 results found").as( "初始状态 异常");

        step("分机1000拨打*99 切换上下班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("/FORCEDEST/global                                 : outoffice").as( "分机1000拨打*99切换为下班状态 异常");

        step("3:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        assertThat(getExtensionStatus(1002, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("分机1000拨打*99切换回上班状态");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("0 results found").as( "分机1000拨打*99切换回上班状态 异常");
        
        step("4:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("2.Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入到假期目的地，分机1003响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打*99切换为上班状态；\n" +
            "通过sps外线呼入；\n" +
            "\t\t\t\t呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t分机1000拨打*99切换回假期状态；\n" +
            "通过sps外线呼入；\n" +
            "\t\t\t\t\t\t呼入到假期目的地，分机1003响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t\t\t删除这条Holidays1;\n" +
            "通过sps外线呼入；\n" +
            "\t\t\t\t\t\t\t\t呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_02_HoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        step("Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期");
        apiUtil.createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:呼入到假期目的地，分机1003响铃，接听，挂断；检查cdr");
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1003, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        assertThat(getExtensionStatus(1003, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("0 results found").as( "初始状态 异常");

        step("分机1000拨打*99 切换上班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("/FORCEDEST/global                                 : office").as( "分机1000拨打*99切换为上班状态 异常");

        step("呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr" + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("分机1000拨打*99切换回假期状态");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .contains("0 results found").as( "切换回假期状态 异常");

        step("呼入到假期目的地，分机1003响铃，接听，挂断；检查cdr" + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("删除这条Holidays1");
        apiUtil.deleteHoliday("Holidays1").apply();

        step("呼入到上班目的地，分机1001响铃，接听，挂断；检查cdr" + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("3.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Hang up\n" +
            "\t通过sps外线呼入\n" +
            "\t\t通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_03_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Hang up");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"office_time_dest\": \"end_call\"").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("4.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Extension-1020\n" +
            "\t通过sps外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_04_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Extension-1020");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"extension\", \"office_time_dest_value\": \"%s\"",apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("5.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择ExtensionVoicemail-1000\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到分机1000的语音留言，通话15s，挂断；登录分机1000查看新增一条语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_05_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择ExtensionVoicemail-1000");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"ext_vm\", \"office_time_dest_value\": \"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();
        log.debug("【Voicemail 1000】 "+getExtensionStatus(1000,HUNGUP,10));
        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("6.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择IVR-IVR0\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_06_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择IVR-IVR0");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"ivr\", \"office_time_dest_value\": \"%s\"",apiUtil.getIVRSummary("6200").id)).apply();

        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "ivr-greeting-dial-ext.slin")).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("7.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择RingGroup-RingGroup0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_07_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择RingGroup-RingGroup0");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"ring_group\", \"office_time_dest_value\": \"%s\"",apiUtil.getRingGroupSummary("6300").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("8.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Queue-Queue0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_08_HoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Queue-Queue0");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"queue\", \"office_time_dest_value\": \"%s\"",apiUtil.getQueueSummary("6400").id)).apply();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400] " + result);
        if (!result.contains("1003")) {
            pjsip.Pj_Make_Call_Auto_Answer(1003, "*76400", DEVICE_IP_LAN);
        }
        if (!result.contains("1004")) {
            pjsip.Pj_Make_Call_Auto_Answer(1004, "*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] " + resultAfter);

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("9.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Conference-Conference0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_09_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Queue-Queue0");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"conference\", \"office_time_dest_value\": \"%s\"",apiUtil.getConferenceSummary("6500").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("10.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择External Number，Prefix: 1 ,号码：3001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_10_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("10.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择External Number，Prefix: 1 ,号码：3001\n");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"office_time_dest\":\"external_num\",\"office_time_dest_prefix\":\"1\",\"office_time_dest_value\":\"3001\"").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("11.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Outbound Route:Out1\n" +
            "\t通过sps外线呼入\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_11_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Outbound Route:Out1");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1, \"office_time_dest\": \"outroute\", \"office_time_dest_value\": \"%s\"",apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("12.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择PlayGreetingeThenHangUp，选择prompt1，播放1遍\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看播放提示音文件prompt1,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_12_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step(".编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择PlayGreetingeThenHangUp，选择prompt1，播放1遍\n");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"office_time_dest\":\"play_greeting\",\"office_time_dest_prefix\":\"1\",\"office_time_dest_value\":\"prompt1.wav\"").apply();

        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_1)).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("13.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择[None]\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_13_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择[None]");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"office_time_dest\":\"\",\"office_time_dest_prefix\":\"\",\"office_time_dest_value\":\"\"").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Business Hours Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t14.通过sps外线拨打991001呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_14_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Business Hours Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"office_time_dest\":\"pattern_to_ext\",\"office_time_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Business Hours Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t15.通过sps外线拨打991002呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_15_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Business Hours Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"office_time_dest\":\"pattern_to_ext\",\"office_time_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 1002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Business Hours Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t16.通过sps外线拨打995503301\n" +
            "\t\t分机1003响铃，接听，挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_16_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Business Hours Destination选择”Match Extension Range“分机选择1003-1004");
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"office_time_dest\":\"range_to_ext\",\"office_time_dest_value\":\"1003-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503301", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Business Hours Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t17.通过sps外线拨打995503302\n" +
            "\t\t分机1004响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_17_HoursDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Business Hours Destination选择”Match Extension Range“分机选择1003-1004");
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"office_time_dest\":\"range_to_ext\",\"office_time_dest_value\":\"1003-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("BusinessHoursDestination")
    @Description("18.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为x.，Caller ID为xxxx，呼入目的地Business Hours Destination选择分机-1000\n" +
            "\t通过sps外线拨打995503302\n" +
            "\t\t分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "BusinessHoursDestination", "SPS", "P3"})
    public void testIR_18_HoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为x.，Caller ID为xxxx，呼入目的地Business Hours Destination选择分机-1000");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"patterns\",\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"did_pattern_list\":[{\"did_pattern\":\"x.\"}],\"cid_pattern_list\":[{\"cid_pattern\":\"xxxx\"}]",apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "19.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择Hnag up\n" +
            "\t通过sps外线呼入\n" +
            "\t\t通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_19_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"outoffice_time_dest\":\"end_call\"").apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("test A<1000>", "*99", STATUS.ANSWER.toString(), "test A<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "20.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择Extension-分机1002\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_20_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1002").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "21.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择ExtensionVoicemail-1000\\n\" +\n" +
            "            \"\\t通过sps外线呼入\\n\" +\n" +
            "            \"\\t\\t进入到分机1000的语音留言，通话15s,挂断；分机1000登录查看新增一条语音留言，Name正确显示")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_21_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"ext_vm\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();
        log.debug("【Voicemail 1000】 "+getExtensionStatus(1000,HUNGUP,10));
        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "22.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择IVR-IVR0\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_22_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"ivr\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "ivr-greeting-dial-ext.slin")).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "23.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择RingGroup-RingGroup0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_23_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"ring_group\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getRingGroupSummary("6300").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "24.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择Queue-Queue0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_24_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"queue\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getQueueSummary("6400").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400] " + result);
        if (!result.contains("1003")) {
            pjsip.Pj_Make_Call_Auto_Answer(1003, "*76400", DEVICE_IP_LAN);
        }
        if (!result.contains("1004")) {
            pjsip.Pj_Make_Call_Auto_Answer(1004, "*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] " + resultAfter);

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "25.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择Conference-Conference0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_25_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"conference\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getConferenceSummary("6500").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "26.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择External Number，Prefix: 1 ,号码：3001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_26_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"outoffice_time_dest\":\"external_num\",\"outoffice_time_dest_prefix\":\"1\",\"outoffice_time_dest_value\":\"3001\"").apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "27.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择Outbound Route:Out1\n" +
            "\t通过sps外线呼入\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_27_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"outroute\",\"outoffice_time_dest_value\":\"%s\"",apiUtil.getOutBoundRouteSummary("Out1").id)).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "28.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择PlayGreetingeThenHangUp，选择prompt2，播放1遍\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看播放提示音文件prompt1,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_28_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"outoffice_time_dest\":\"play_greeting\",\"outoffice_time_dest_value\":\"prompt2.wav\"").apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_2)).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "29.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，OutsideBusinessHoursDestination选择[None]\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_29_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"outoffice_time_dest\":\"\",\"outoffice_time_dest_value\":\"\"")).apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地OutsideBusinessHoursDestination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t30.通过sps外线拨打991001呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_30_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"outoffice_time_dest\":\"pattern_to_ext\",\"outoffice_time_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地OutsideBusinessHoursDestination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t31.通过sps外线拨打991002呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_31_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"outoffice_time_dest\":\"pattern_to_ext\",\"outoffice_time_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地OutsideBusinessHoursDestination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t32.通过sps外线拨打995503301\n" +
            "\t\t分机1003响铃，接听，挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_32_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"outoffice_time_dest\":\"range_to_ext\",\"outoffice_time_dest_value\":\"1003-1004\"").
                apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503301", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地OutsideBusinessHoursDestination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t33.通过sps外线拨打995503302\n" +
            "\t\t分机1004响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_33_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"outoffice_time_dest\":\"range_to_ext\",\"outoffice_time_dest_value\":\"1003-1004\"").
                apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("OutsideBusinessHoursDestination")
    @Description("进入后台执行asterisk -rx \"database show FORCEDEST\" ，若返回结果为：0 results found. 则执行分机1000拨打*99切换为下班状态；" +
            "34.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为x.，Caller ID为xxxx，呼入目的地OutsideBusinessHoursDestination选择分机-1000\n" +
            "\t通过sps外线拨打995503302\n" +
            "\t\t分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "OutsideBusinessHoursDestination", "SPS", "P3"})
    public void testIR_34_OutSideHoursDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"patterns\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"did_pattern_list\":[{\"did_pattern\":\"x.\"}],\"cid_pattern_list\":[{\"cid_pattern\":\"xxxx\"}]",apiUtil.getExtensionSummary("1000").id)).
                apply();
        if(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"))
                .contains("0 results found")){
            pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
            sleep(WaitUntils.SHORT_WAIT);
            log.debug("[则执行分机1000拨打*99切换为下班状态 结果] " +SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")));
        }

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "35.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择Hnag up\n" +
            "\t通过sps外线呼入\n" +
            "\t\t通话被挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_35_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"holiday_dest\":\"end_call\"").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "36.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择Extension-分机1003\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1003响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_36_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1003").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "37.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择ExtensionVoicemail-1000\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到分机1000的语音留言，通话15s,挂断；分机1000登录查看新增一条语音留言，Name正确显示")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_37_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"ext_vm\",\"holiday_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        log.debug("【Voicemail 1000】 "+getExtensionStatus(1000,HUNGUP,10));

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "38.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择IVR-IVR0\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_38_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"ivr\",\"holiday_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();

        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "ivr-greeting-dial-ext.slin")).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "39.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择RingGroup-RingGroup0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_39_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"ring_group\",\"holiday_dest_value\":\"%s\"",apiUtil.getRingGroupSummary("6300").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "40.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择Queue-Queue0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_40_HolidaysDestination() throws IOException, JSchException {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"queue\",\"holiday_dest_value\":\"%s\"",apiUtil.getQueueSummary("6400").id)).apply();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400] " + result);
        if (!result.contains("1003")) {
            pjsip.Pj_Make_Call_Auto_Answer(1003, "*76400", DEVICE_IP_LAN);
        }
        if (!result.contains("1004")) {
            pjsip.Pj_Make_Call_Auto_Answer(1004, "*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] " + resultAfter);

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "41.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择Conference-Conference0\\n\" +\n" +
            "            \"\\t通过sps外线呼入\\n\" +\n" +
            "            \"\\t\\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_41_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").
                editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"conference\",\"holiday_dest_value\":\"%s\"",apiUtil.getConferenceSummary("6500").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "42.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择External Number，Prefix: 1 ,号码：3001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_42_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"holiday_dest\":\"external_num\",\"holiday_dest_prefix\":\"1\",\"holiday_dest_value\":\"3001\"").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "43.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择Outbound Route:Out1\\n\" +\n" +
            "            \"\\t通过sps外线呼入\\n\" +\n" +
            "            \"\\t\\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_43_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"outroute\",\"holiday_dest_value\":\"%s\"",apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "44.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择PlayGreetingeThenHangUp，选择prompt2，播放1遍\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看播放提示音文件prompt1,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_44_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1","\"enb_time_condition\": 1,\"holiday_dest\":\"play_greeting\",\"holiday_dest_value\":\"prompt2.wav\"").apply();

        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_2)).start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "45.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Holidays Destination选择[None]\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_45_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1",String.format("\"enb_time_condition\": 1,\"holiday_dest\":\"\",\"holiday_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, HUNGUP, 30)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Holidays Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t46.通过sps外线拨打991001呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_46_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"holiday_dest\":\"pattern_to_ext\",\"holiday_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Holidays Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t47.通过sps外线拨打991002呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_47_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"holiday_dest\":\"pattern_to_ext\",\"holiday_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Holidays Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t48.通过sps外线拨打995503301\n" +
            "\t\t分机1003响铃，接听，挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_48_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"holiday_dest\":\"range_to_ext\",\"holiday_dest_value\":\"1003-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503301", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Holidays Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t49.通过sps外线拨打995503302\n" +
            "\t\t分机1004响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_49_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1", "\"enb_time_condition\": 1,\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\",\"holiday_dest\":\"range_to_ext\",\"holiday_dest_value\":\"1003-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonGlobalBusinessHours")
    @Story("HolidaysDestination")
    @Description("【前置步骤】Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "50.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为x.，Caller ID为xxxx，呼入目的地Holidays Destination选择分机-1000\n" +
            "\t通过sps外线拨打995503302\n" +
            "\t\t分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonGlobalBusinessHours", "HolidaysDestination", "SPS", "P3"})
    public void testIR_50_HolidaysDestination()  {
        prerequisite();
        initBusinessHoursAndIn1();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\": 1,\"did_option\":\"patterns\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\",\"did_pattern_list\":[{\"did_pattern\":\"x.\"}],\"cid_pattern_list\":[{\"cid_pattern\":\"xxxx\"}]",apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
}