package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test out bound tim condition
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestOutboundTimeCondition extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestOutboundTimeCondition() {
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
            timeComdition();
            isRunRecoveryEnvFlag = registerAllExtensions();
        step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    /**
     * 时间条件
     */
    public void timeComdition(){
        step("######### time condition" +
                "1、Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
                "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
                "Break Hours：07:00-14:00;18:00-23:59\n" +
                "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\n" +
                "2、Business Hours and Holidays->Holidays 删除所有；\n" +
                "3、新建呼出路由OR_GlobalBusiness1，外线选择sps外线，选择分机A-1000，Available Time选择Based on Global Business Hours ，勾选Business Hours;\n" +
                "4、新建呼出路由OR_GlobalBusiness2，外线选择sps外线，选择分机B-1001，Available Time选择Based on Global Business Hours ，勾选Outside Business Hours;\n" +
                "5、新建呼出路由OR_GlobalBusiness3，外线选择sps外线，选择分机C-1002，Available Time选择Based on Global Business Hours ，勾选Holidays;\n" +
                "\n" +
                "新建呼出路由OR_GlobalBusiness4，外线选择sps外线，选择分机1005，Available Time选择Based on Global Business Hours ，勾选Business Hours、Holidays、Outside Business Hours;\n" +
                "\n" +
                "6、新建呼出路由OR_CustomBusiness4，Dial Pattern：21.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-12:00；12:00-23:59，选择星期一至星期天，勾选Business Hours;\n" +
                "7、新建呼出路由OR_CustomBusiness5，Dial Pattern：22.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-12:00；12:00-23:59，选择星期一至星期天，勾选Outside Business Hours;\n" +
                "8.新建呼出路由OR_CustomBusiness6，Dial Pattern：23.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-00:01，选择星期一至星期天，勾选Holidays;\n" +
                "9、新建呼出路由OR_CustomBusiness7，Dial Pattern：24.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-00:01；选择星期一至星期天，勾选Outside Business Hours;\n" +
                "10、新建呼出路由OR_CustomBusiness8，Dial Pattern：25.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-12:00；12:00-23:59，获取当前运行时间是星期几，Days of week取前一天的，比如当前执行时间是星期一则Days of week选择sun，勾选Outside Business Hours;\n" +
                "\n" +
                "新建呼出路由OR_CustomBusiness9，Dial Pattern：26.  ,Strip :2 ,外线选择sps外线，选择分机D-1003，Available Time选择Based on Custom Business Hours ，添加自定义时间00:00-12:00，Days of week选择sun，勾选Business Hours、Outside Business Hours、Holidays;");

        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-06:00");
        officeTimes.add("06:00-08:00");
        officeTimes.add("13:00-18:00");
        resetTimes.add("07:00-14:00");
        resetTimes.add("18:00-23:59");

        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteAllHoliday().apply();
        apiUtil.deleteAllOutbound().createOutbound("OR_GlobalBusiness1", asList(SPS), asList("1000"), "", 0).
                editOutbound("OR_GlobalBusiness1","\"available_time\":\"global\"").apply();

        apiUtil.createOutbound("OR_GlobalBusiness2", asList(SPS), asList("1001"), "", 0).
                editOutbound("OR_GlobalBusiness2","\"available_time\": \"global\", \"enb_office_time\": 0, \"enb_out_of_office_time\": 1").apply();

        apiUtil.createOutbound("OR_GlobalBusiness3", asList(SPS), asList("1002"), "", 0).
                editOutbound("OR_GlobalBusiness3","\"available_time\":\"global\",\"enb_office_time\":0,\"enb_out_of_office_time\": 0,\"enb_holiday\":1").apply();

        apiUtil.createOutbound("OR_GlobalBusiness4", asList(SPS), asList("1005"), "", 0).
                editOutbound("OR_GlobalBusiness4","\"available_time\":\"route_scope\",\"enb_office_time\":1,\"enb_out_of_office_time\":1,\"enb_holiday\":1").apply();

        apiUtil.createOutbound("OR_CustomBusiness4", asList(SPS), asList("1003"), "21.", 2).
                editOutbound("OR_CustomBusiness4","\"available_time\":\"route_scope\",\"enb_office_time\":1,\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-12:00\"},{\"value\":\"12:00-23:59\"}],\"pos\":1}]").apply();

        apiUtil.createOutbound("OR_CustomBusiness5", asList(SPS), asList("1003"), "22.", 2).
                editOutbound("OR_CustomBusiness5","\"available_time\":\"route_scope\",\"enb_office_time\":0,\"enb_out_of_office_time\":1,\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-12:00\"},{\"value\":\"12:00-23:59\"}],\"pos\":1}]").apply();

        apiUtil.createOutbound("OR_CustomBusiness6", asList(SPS), asList("1003"), "23.", 2).
                editOutbound("OR_CustomBusiness6","\"available_time\":\"route_scope\",\"enb_office_time\":0,\"enb_out_of_office_time\":0,\"enb_holiday\": 1,\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-00:01\"}],\"pos\":1}]").apply();

        apiUtil.createOutbound("OR_CustomBusiness7", asList(SPS), asList("1003"), "24.", 2).
                editOutbound("OR_CustomBusiness7","\"available_time\":\"route_scope\",\"enb_office_time\":0,\"enb_out_of_office_time\":1,\"enb_holiday\": 0,\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-00:01\"}],\"pos\":1}]").apply();

        apiUtil.createOutbound("OR_CustomBusiness8", asList(SPS), asList("1003"), "25.", 2).
                editOutbound("OR_CustomBusiness8",String.format("\"available_time\":\"route_scope\",\"enb_office_time\":0,\"enb_out_of_office_time\":1,\"office_time_list\":[{\"days_of_week\":\"%s\",\"office_times\":[{\"value\":\"00:00-12:00\"},{\"value\":\"12:00-23:59\"}],\"pos\":1}]",DataUtils.getYesterdayWeekDay())).apply();

        apiUtil.createOutbound("OR_CustomBusiness9", asList(SPS), asList("1003"), "26.", 2).
                editOutbound("OR_CustomBusiness9","\"available_time\":\"route_scope\",\"enb_office_time\":1,\"enb_out_of_office_time\":1,\"enb_holiday\": 1,\"office_time_list\":[{\"days_of_week\":\"sun\",\"office_times\":[{\"value\":\"00:00-00:12\"}],\"pos\":1}]").apply();

    }

    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("1.分机1000拨打2222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonGlobalBusinessHours"})
    public void testOR_01_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1000, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("2.分机1001拨打2222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonGlobalBusinessHours"})
    public void testOR_02_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1001, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("3.分机1002拨打2222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonGlobalBusinessHours"})
    public void testOR_03_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1002" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1002, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("4.分机1005拨打2222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonGlobalBusinessHours"})
    public void testOR_04_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1005" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1005, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("5.分机1003拨打212222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_05_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 212222");
        pjsip.Pj_Make_Call_No_Answer(1003, "212222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "212222", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("6.分机1003拨打22222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_06_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 22222");
        pjsip.Pj_Make_Call_No_Answer(1003, "22222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1003, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("7.分机1003拨打23222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_07_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 23222");
        pjsip.Pj_Make_Call_No_Answer(1003, "616666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1003, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("8.分机1003拨打24222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_08_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 24222");
        pjsip.Pj_Make_Call_No_Answer(1003, "24222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "24222", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("9.分机1003拨打25222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_09_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 25222");
        pjsip.Pj_Make_Call_No_Answer(1003, "25222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "25222", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("10.分机1003拨打26222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomBusinessHours"})
    public void testOR_10_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 26222");
        pjsip.Pj_Make_Call_No_Answer(1003, "26222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "26222", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("11.分机1004拨打31222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomTimePeriods"})
    public void testOR_11_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 31222");
        pjsip.Pj_Make_Call_No_Answer(1004, "31222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "31222", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("12.分机1004拨打32222呼出\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomTimePeriods"})
    public void testOR_12_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 32222");
        pjsip.Pj_Make_Call_No_Answer(1004, "32222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "32222", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("13.分机1004拨打33222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomTimePeriods"})
    public void testOR_13_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 33222");
        pjsip.Pj_Make_Call_No_Answer(1004, "33222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("14.分机1004拨打34222呼出\n" +
            "\t呼出失败，通话被挂断；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","BasedonCustomTimePeriods"})
    public void testOR_14_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1004" + ",[callee] 34222");
        pjsip.Pj_Make_Call_No_Answer(1004, "34222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1004, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-TimeCondition")
    @Story("TimeCondition")
    @Description("15.分机1000拨打*99强制为下班时间\n" +
            "\t分机1000、1001、1002、1005分别拨打2222呼出；\n" +
            "分机1003拨打21222呼出；\n" +
            "分机1004拨打31222呼出；\n" +
            "\t\t1000、1002呼出时，呼出失败，通话被挂断；\n" +
            "1001、1005呼出时，辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "分机1003、1004呼出时，辅助2的分机2000响铃，接听，挂断；检查cdr【强制上下班只对GlobalBussiness有效】\n" +
            "\t\t\t分机1000拨打*99取消强制为下班时间；\n" +
            "分机1000、1001、1002分别拨打2222呼出；\n" +
            "\t\t\t\t1001、1002呼出时，呼出失败，通话被挂断；\n" +
            "1000、1005呼出时，辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "OutboundRoute-TimeCondition", "TimeCondition", "BusinessHours","OutsideBusinessHours","P2","SwitchBusinessHoursStatus"},enabled = false)
    public void testOR_15_TimeCondition() {
        prerequisite();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("分机1000拨打*99强制为下班时间");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);

        step("2:[caller] 1000" + ",[callee] 616666");
        pjsip.Pj_Make_Call_No_Answer(1000, "616666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "616666", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
}