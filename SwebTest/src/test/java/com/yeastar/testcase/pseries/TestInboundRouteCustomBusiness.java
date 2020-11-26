package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.SSHLinuxUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @program: SwebTest
 * @description: test inbound route based on custom business hours
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteCustomBusiness extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestInboundRouteCustomBusiness() {
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
            initOutbound();
            initIVR();
            initInbound();
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
     * 00:00-00:00，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     * *
     */
    public void initBusinessHours() {
        step("初始化环境，设置每天都是下班时间");
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomBusinessHours")
    @Story("BasedonCustomBusinessHours")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\n" +
            "设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；" +
            "1.通过sps外线呼入\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "\t\t分机1000拨打*99 切换上下班时间；\n" +
            "通过sps外线呼入\n" +
            "\t\t\t*99不会切换自定义办公时间状态；分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute", "CustomBusinessHours", "BusinessHoursDestination","FeatureCode","SwitchBusinessHoursStatus", "P1"})
    public void testIR_01_CustomBusiness() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\\n\" +\n" +
                "            \"设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"route_scope\",\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\",\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-10:00\"},{\"value\":\"09:00-23:59\"}],\"pos\":1}]",
                apiUtil.getExtensionSummary("1000").id, apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("分机1000拨打*99 切换上下班时间 ,99不会切换自定义办公时间状态");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .has(anyOf(contains("/FORCEDEST/global                                 : office"), contains("0 results found"))).as("99不会切换自定义办公时间状态 异常");


        step("3:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomBusinessHours")
    @Story("BasedonCustomBusinessHours")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\n" +
            "设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；" +
            "2.Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期\n" +
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
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute", "CustomBusinessHours", "Holidays","HolidaysDestination","FeatureCode","SwitchBusinessHoursStatus", "P3"})
    public void testIR_02_CustomBusiness() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\\n\" +\n" +
                "            \"设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"route_scope\",\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\",\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-10:00\"},{\"value\":\"09:00-23:59\"}],\"pos\":1}]",
                apiUtil.getExtensionSummary("1000").id, apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).apply();
        step("Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期");
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:sps 呼入 分机1002响铃，接听，挂断；检查cdr");
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        assertThat(getExtensionStatus(1002, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("分机1000拨打*99 切换上班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .has(anyOf(contains("/FORCEDEST/global                                 : office"), contains("0 results found"))).as("99不会切换自定义办公时间状态 异常");

        step("分机1002响铃，接听，挂断；检查cdr" + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002, false);
        assertThat(getExtensionStatus(1002, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        step("删除所有");
        apiUtil.deleteAllHoliday().apply();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-BasedonCustomBusinessHours")
    @Story("BasedonCustomBusinessHours")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\n" +
            "设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；" +
            "3.获取当下运行时间是星期几，假如为星期日；编辑自定义办公间Days of week只选择当下运行时间的前一天即星期六\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打*99 切换上下班时间；\n" +
            "通过sps外线呼入\n" +
            "\t\t\t\t*99不会切换自定义办公时间状态；分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute", "CustomBusinessHours", "OutsideBusinessHoursDestination", "FeatureCode","SwitchBusinessHoursStatus","P3"})
    public void testIR_03_CustomBusiness() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();
        step("编辑呼入路由In1,DID Pattern选择DID Pattern，模式为空，Caller IDPattern为空，Business Hours Destination选择Hang up");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"route_scope\",\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\",\"office_time_list\":[{\"days_of_week\":\"%s\",\"office_times\":[{\"value\":\"00:00-10:00\"},{\"value\":\"09:00-23:59\"}],\"pos\":1}]",
                apiUtil.getExtensionSummary("1000").id, apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id, DataUtils.getYesterdayWeekDay())).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
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

        step("分机1000拨打*99 切换上班时间；");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);
        assertThat(SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST")))
                .has(anyOf(contains("/FORCEDEST/global                                 : office"), contains("0 results found"))).as("99不会切换自定义办公时间状态 异常");

        step("分机1001响铃，接听，挂断；检查cdr" + SPS);
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
    @Feature("InboundRoute-BasedonCustomBusinessHours")
    @Story("BasedonCustomBusinessHours")
    @Description("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\n" +
            "设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；" +
            "4.编辑In1删除所有Custom Business Hours；\n" +
            "\t通过sps外线呼入\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute", "CustomBusinessHours", "P3"})
    public void testIR_04_CustomBusiness() {
        prerequisite();
        initBusinessHours();
        step("编辑In1启用时间条件，Time-based Routing Mode选择Based on Custom Business Hours；添加自定义办公时间：00:00-10:00;09:00-23:59\\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday；\\n\" +\n" +
                "            \"设置Business Hours Destination到分机A-1000；Outside Business Hours Destination到分机B-1001；Holidays Destination到分机C-1002；");
        apiUtil.editInbound("In1", String.format("\"enb_time_condition\":1,\"time_condition\":\"route_scope\",\"office_time_dest\":\"extension\",\"office_time_dest_value\":\"%s\",\"outoffice_time_dest\":\"extension\",\"outoffice_time_dest_value\":\"%s\",\"holiday_dest\":\"extension\",\"holiday_dest_value\":\"%s\",\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-10:00\"},{\"value\":\"09:00-23:59\"}],\"pos\":1}]",
                apiUtil.getExtensionSummary("1000").id, apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1002").id)).apply();
        step("编辑In1删除所有Custom Business Hours；");
        apiUtil.editInbound("In1", "\"office_time_list\":[]").apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
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
}
