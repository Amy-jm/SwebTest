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
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test inbound route based on Custom time periods
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteCustomTime extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestInboundRouteCustomTime() {
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
            initInbound();
            initOutbound();

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
     * 00:00-00:00，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     */
    public void initBusinessHoursAndHolidays() {
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("1.编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods Hours；Custom Time Periods 为空，Holidays Destination 选择分机1002；Default Destination 选择分机1001；\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打*99 切换上下班时间；\n" +
            "通过sps外线呼入\n" +
            "\t\t\t\t*99不会切换自定义办公时间状态；分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P2"})
    public void testIR_01_BCTP() throws IOException, JSchException {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods Hours；Custom Time Periods 为空，Holidays Destination 选择分机1002；Default Destination 选择分机1001");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
        log.debug("[database show FORCEDEST ] " + result);
        Assert.assertTrue(result.contains("0 results found"), "*99 切换上下班时间 异常");

        step("分机1000拨打*99 切换上下班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);

        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
        log.debug("[database show FORCEDEST ] " + resultAfter);
        Assert.assertTrue(resultAfter.contains("0 results found"), "*99 切换上下班时间 异常");

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR_2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR_2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "2.通过sps外线呼入\n" +
            "\t分机1003响铃，接听，挂断；检查cdr\n" +
            "\t\t分机1000拨打*99 切换上下班时间；\n" +
            "通过sps外线呼入\n" +
            "\t\t\t*99不会切换自定义办公时间状态；分机1003响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P2"})
    public void testIR_02_BCTP() throws IOException, JSchException {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"extension\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
        log.debug("[database show FORCEDEST ] " + result);
        Assert.assertTrue(result.contains("0 results found"), "*99 切换上下班时间 异常");

        step("分机1000拨打*99 切换上下班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);

        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
        log.debug("[database show FORCEDEST ] " + resultAfter);
        Assert.assertTrue(resultAfter.contains("0 results found"), "*99 切换上下班时间 异常");

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR_2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR_2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "3.编辑In1的Edit Custom Time Periods的Destination为HangUp\n" +
            "\t通过sps外线呼入\n" +
            "\t\t通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_03_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"end_call\",\"dest_prefix\":\"\",\"dest_value\":\"\",\"dest_ext_list\":[],\"pos\":1}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "4.编辑In1的Edit Custom Time Periods的Destination为[None]\n" +
            "\t通过sps外线呼入\n" +
            "\t\t通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_04_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"\",\"dest_prefix\":\"\",\"dest_value\":\"\",\"dest_ext_list\":[],\"pos\":1}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "5.编辑In1的Edit Custom Time Periods的Destination为Extension Voicemail-分机1001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到分机1001的语音留言，通话15s挂断；登录分机1001，查看新增1条语音留言，Name显示正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_05_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"ext_vm\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getExtensionSummary("1001").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1001", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "6.编辑In1的Edit Custom Time Periods的Destination为IVR0\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_06_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"ivr\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getIVRSummary("6200").id)).
                apply();
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
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "7.编辑In1的Edit Custom Time Periods的Destination为RingGroup0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_07_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"ring_group\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getRingGroupSummary("6300").id)).
                apply();

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
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "8.编辑In1的Edit Custom Time Periods的Destination为Queue0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_08_BCTP() throws IOException, JSchException {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"queue\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getQueueSummary("6400").id)).
                apply();

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
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "9.编辑In1的Edit Custom Time Periods的Destination为Conference0\n" +
            "\t通过sps外线呼入\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_09_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"conference\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getConferenceSummary("6500").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "10.编辑In1的Edit Custom Time Periods的Destination为External Number Prefix: 1 号码3001\n" +
            "\t通过sps外线呼入\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_10_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"external_num\",\"dest_prefix\":\"1\",\"dest_value\":\"3001\",\"dest_ext_list\":[],\"pos\":1}]")).
                apply();

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
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "11.编辑In1的Edit Custom Time Periods的Destination为Outbound Route：Out1\n" +
            "\t通过sps外线呼入\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_11_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"outroute\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getOutBoundRouteSummary("Out1").id)).
                apply();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "12.编辑In1的Edit Custom Time Periods的Destination为Play Greeting then Hang Up 选择提示音prompt3，播放1遍\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看播放提示音文件prompt3,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_12_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"play_greeting\",\"dest_prefix\":\"1\",\"dest_value\":\"prompt3.wav\",\"dest_ext_list\":[],\"pos\":1}]")).
                apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_3)).start();

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

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Edit Custom Time Periods的Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t13.通过sps外线拨打991001呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_13_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\"")).
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"pattern_to_ext\",\"dest_prefix\":\"\",\"dest_ext_list\":[{\"value\":\"%s\"}],\"pos\":1}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
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
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Pattern to Extensions\"，值为{{.Ext}} 呼入目的地Edit Custom Time Periods的Destination选择”Match Selected Extensions“ 分机选择”Extension Group-Default_All_Extensions\"\n" +
            "\t14.通过sps外线拨打991002呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_14_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\"")).
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"pattern_to_ext\",\"dest_prefix\":\"\",\"dest_ext_list\":[{\"value\":\"%s\"}],\"pos\":1}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Edit Custom Time Periods的Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t15.通过sps外线拨打995503301\n" +
            "\t\t分机1003响铃，接听，挂断；cdr正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_15_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\"").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"range_to_ext\",\"dest_prefix\":\"\",\"dest_value\":\"1003-1004\",\"pos\":1}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503301", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "编辑呼入路由In1,DID Pattern选择“Match DID Range to Extension Range\"，分机范围设置5503301-5503302；呼入目的地Edit Custom Time Periods的Destination选择”Match Extension Range“分机选择1003-1004\n" +
            "\t16.通过sps外线拨打995503302\n" +
            "\t\t分机1004响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_16_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503301\",\"did_to_ext_end\":\"5503302\"").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"range_to_ext\",\"dest_prefix\":\"\",\"dest_value\":\"1003-1004\",\"pos\":1}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING);
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "17.编辑呼入路由In1,DID Pattern选择DID Pattern，模式为x.，Caller ID为xxxx，呼入目的地Edit Custom Time Periods的Destination选择分机-1000\n" +
            "\t通过sps外线拨打995503302\n" +
            "\t\t分机1000响铃，接听，挂断；cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P3"})
    public void testIR_17_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"patterns\",\"did_pattern_list\":[{\"did_pattern\":\"x.\"}],\"cid_pattern_list\":[{\"cid_pattern\":\"xxxx\"}]")).
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"extension\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "18.Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打*99 切换上下班时间；\n" +
            "通过sps外线呼入\n" +
            "\t\t\t\t*99不会切换自定义办公时间状态；分机1002响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\tBusiness Hours and Holidays->Holidays 删除所有假期设置\n" +
            "\t\t\t\t\t\t恢复环境，避免影响其它测试点")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P2"})
    public void testIR_18_BCTP() throws IOException, JSchException {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-05:00\"},{\"value\":\"05:00-12:00\"},{\"value\":\"12:00-15:00\"},{\"value\":\"15:00-16:00\"},{\"value\":\"14:30-23:59\"}],\"dest\":\"extension\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]", apiUtil.getExtensionSummary("1003").id)).
                apply();

        apiUtil.createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("分机1000拨打*99 切换上下班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);

        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
        log.debug("[database show FORCEDEST ] " + resultAfter);
        Assert.assertTrue(resultAfter.contains("0 results found"), "*99 切换上下班时间 异常");

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR_2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR_2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        apiUtil.deleteAllHoliday();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomTimePeriods")
    @Story("BasedonCustomTimePeriods")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\n" +
            "Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \n" +
            "Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
            "\t\n" +
            "Destination: 分机1003；Holidays Destination 选择分机1002；Default Destination 选择分机1001；" +
            "19.获取当下运行时间是星期几，假如为星期日；编辑自定义办公间Days of week只选择当下运行时间的前一天即星期六\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute-BasedonCustomTimePeriods", "BasedonCustomTimePeriods", "SPS", "P2"})
    public void testIR_19_BCTP()  {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);

        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");

        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Time Periods；时间段设置为\\n\" +\n" +
                "            \"Custom Time Periods: 00:00-05:00;05:00-12:00;12:00-15:00;15:00-16:00;14:30-23:59; \\n\" +\n" +
                "            \"Days of Week: Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday");
        initBusinessHoursAndHolidays();
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"custom\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).
                editInbound("In1", String.format("\"office_time_list\":[{\"days_of_week\":\"%s\",\"office_times\":[{\"value\":\"00:00-23:00\"}],\"dest\":\"extension\",\"dest_prefix\":\"\",\"dest_value\":\"%s\",\"dest_ext_list\":[],\"pos\":1}]",DataUtils.getYesterdayWeekDay(), apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
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
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

}