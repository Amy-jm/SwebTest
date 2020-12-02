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

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

/**
 * @program: SwebTest
 * @description: test extension call restrictions
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestExtensionCallRestrictions extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();

    TestExtensionCallRestrictions() {
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);

        /**
         * ##########
         * "Business Hours and Holidays->Business Hours 添加上班时间条件:\\n\" +\n" +
         * "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\\n\" +\n" +
         * "Break Hours：07:00-14:00;18:00-23:59\\n\" +\n" +
         * "Days of week：\\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday\" +"
         * ##########
         */
        officeTimes.add("00:00-06:00");
        officeTimes.add("06:00-08:00");
        officeTimes.add("13:00-18:00");
        resetTimes.add("07:00-14:00");
        resetTimes.add("18:00-23:59");
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
            initOutBounds();// todo 提交的时候 启用
            isRunRecoveryEnvFlag = registerAllExtensions();
        step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    /**
     * 时间条件
     */
    public void initOutBounds(){
        log.info("######### Beforeclass\n" +
                "\t新建呼出路由OutRole1，Role选择Supervisor，Dial Pattern ：81. Strip:2，选择sps外线\n" +
                "\t新建呼出路由OutBusinessHours2，Dial Pattern：82. ，Strip:2 ,选择sps外线，Time Condition 选择Always\n" +
                "\t新建呼出路由OutBusinessHours3，Dial Pattern：83. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Global Business Hours ，选择Business Hours、Outside Business Hours、Holidays\n" +
                "\t新建呼出路由OutBusinessHours4，Dial Pattern：84. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Global Business Hours ，只选择Business Hours\n" +
                "\t新建呼出路由OutBusinessHours5，Dial Pattern：85. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Global Business Hours ，只选择Outside Business Hours\n" +
                "\t新建呼出路由OutBusinessHours6，Dial Pattern：86. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Global Business Hours ，只选择Holidays\n" +
                "\t新建呼出路由OutBusinessHours7，Dial Pattern：87. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Custom Business Hours ，自定义时间00:00-23:59 ,周一到周天，选择Business Hours 、Outside Business Hours、Holidays\n" +
                "\t新建呼出路由OutBusinessHours8，Dial Pattern：88. ，Strip:2 ,选择sps外线，Time Condition 选择Based on Custom Time Periods ，自定义时间00:00-23:59 ,周一到周天，选择Holidays");


        apiUtil.createOutbound("OutRole1", asList(SPS), asList("Default_Extension_Group"), "81.", 2).
                editOutbound("OutRole1","\"role_list\":[{\"value\":\"2\",\"text\":\"Supervisor\"}]").apply();

        apiUtil.createOutbound("OutBusinessHours2", asList(SPS), asList("Default_Extension_Group"), "82.", 2).
                editOutbound("OutBusinessHours2","\"available_time\":\"always\"").apply();

        apiUtil.createOutbound("OutBusinessHours3", asList(SPS), asList("Default_Extension_Group"), "83.", 2).
                editOutbound("OutBusinessHours3","\"available_time\":\"global\",\"enb_office_time\":1,\"enb_out_of_office_time\":1,\"enb_holiday\":1").apply();

        apiUtil.createOutbound("OutBusinessHours4", asList(SPS), asList("Default_Extension_Group"), "84.", 2).
                editOutbound("OutBusinessHours4","\"available_time\":\"global\",\"enb_office_time\":1,\"enb_out_of_office_time\":0,\"enb_holiday\":0").apply();

        apiUtil.createOutbound("OutBusinessHours5", asList(SPS), asList("Default_Extension_Group"), "85.", 2).
                editOutbound("OutBusinessHours5","\"available_time\":\"global\",\"enb_office_time\":0,\"enb_out_of_office_time\":1,\"enb_holiday\":0").apply();

        apiUtil.createOutbound("OutBusinessHours6", asList(SPS), asList("Default_Extension_Group"), "86.", 2).
                editOutbound("OutBusinessHours6","\"available_time\":\"global\",\"enb_office_time\":0,\"enb_out_of_office_time\":0,\"enb_holiday\":1").apply();

        apiUtil.createOutbound("OutBusinessHours7", asList(SPS), asList("Default_Extension_Group"), "87.", 2).
                editOutbound("OutBusinessHours7","\"available_time\":\"global\",\"enb_office_time\":1,\"enb_out_of_office_time\":1,\"enb_holiday\":1,\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-23:59\"}],\"pos\":1}]").apply();

        apiUtil.createOutbound("OutBusinessHours8", asList(SPS), asList("Default_Extension_Group"), "88.", 2).
                editOutbound("OutBusinessHours8","\"available_time\":\"custom\",\"office_time_list\":[{\"days_of_week\":\"sun mon tue wed thu fri sat\",\"office_times\":[{\"value\":\"00:00-23:59\"}],\"pos\":1}]").apply();

    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCalls,OutboundRoute,Extension")
    @Description("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n" +
            "\t1.分机1001拨打2222呼出\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCalls","OutboundRoute","P2",""})
    public void testExtension_01_DisableOutboundCalls() {
        prerequisite();
        step("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n");
        apiUtil.editExtension("1001","\"disable_outb_call\":1,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1001, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCalls,OutboundRoute,Extension")
    @Description("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n" +
            "\t2.分机1001拨打818888呼出\n" +
            "\t\t呼出失败，通话被挂断；\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCalls","OutboundRoute","P2",""})
    public void testExtension_02_DisableOutboundCalls() {
        prerequisite();
        step("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n");
        apiUtil.editExtension("1001","\"disable_outb_call\":1,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 818888");
        pjsip.Pj_Make_Call_No_Answer(1001, "818888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCalls,OutboundRoute,Extension")
    @Description("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n" +
            "\t编辑分机1001，不启用Disable Outbound Calls ，不启用Disable Outbound Calls outside Business Hours\n" +
            "\t\t3.分机1001拨打2222呼出\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCalls","OutboundRoute","P2",""})
    public void testExtension_03_DisableOutboundCalls() {
        prerequisite();
        step("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours,编辑分机1001，不启用Disable Outbound Calls ，不启用Disable Outbound Calls outside Business Hours\n");
        apiUtil.editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1001, "2222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "2222", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCalls,OutboundRoute,Extension")
    @Description("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n" +
            "\t编辑分机1001，不启用Disable Outbound Calls ，不启用Disable Outbound Calls outside Business Hours\n" +
            "\t\t4.分机1001拨打818888呼出\n" +
            "\t\t\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCalls","OutboundRoute","P2",""})
    public void testExtension_04_DisableOutboundCalls() {
        prerequisite();
        step("编辑分机1001，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n,编辑分机1001，不启用Disable Outbound Calls ，不启用Disable Outbound Calls outside Business Hours");
        apiUtil.editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 818888");
        pjsip.Pj_Make_Call_No_Answer(1001, "818888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "818888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCalls,OutboundRoute,Extension")
    @Description("编辑分机1020，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours\n" +
            "\tFXS需判断是否存在\n" +
            "\t5.分机1020拨打13001呼出；即辅助2分机2001拨打7713001\n" +
            "\t\t等待30s，辅助1的分机3001始终不会响铃，主叫挂断\n" +
            "\t\t\tFXO挂不断\n" +
            "\t\t\t编辑分机1020，Security-》禁用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours ；\n" +
            "分机1020拨打13001呼出；即辅助2分机2001拨打7713001；\n" +
            "\t\t\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","FXS","Extension", "CallRestrictions","DisableOutboundCalls","OutboundRoute","P3",""})
    public void testExtension_05_DisableOutboundCalls() {
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        prerequisite();
        step("编辑分机1020，Security-》启用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours");
        apiUtil.editExtension("1020","\"disable_outb_call\":1,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 7713001");
        pjsip.Pj_Make_Call_No_Answer(2001, "7713001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertFalse(getExtensionStatus(3001, RING, 30)==RING,"[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        step("编辑分机1020，Security-》禁用Disable Outbound Calls ,不启用Disable Outbound Calls outside Business Hours ");
        apiUtil.editExtension("1020","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();
        sleep(5000);

        step("2:[caller] 2001" + ",[callee] 7713001");
        pjsip.Pj_Make_Call_No_Answer(2001, "7713001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1020.toString(), "13001", STATUS.ANSWER.toString(), CDRNAME.Extension_1020.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "6.分机1001拨打82888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_06_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "7.分机1001拨打83888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_07_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 83888");
        pjsip.Pj_Make_Call_No_Answer(1001, "83888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "8.分机1001拨打84888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_08_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 84888");
        pjsip.Pj_Make_Call_No_Answer(1001, "84888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "9.分机1001拨打85888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_09_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 85888");
        pjsip.Pj_Make_Call_No_Answer(1001, "85888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "10.分机1001拨打86888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_10_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 86888");
        pjsip.Pj_Make_Call_No_Answer(1001, "86888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "11.分机1001拨打87888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_11_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 87888");
        pjsip.Pj_Make_Call_No_Answer(1001, "87888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "12.分机1001拨打88888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_12_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 88888");
        pjsip.Pj_Make_Call_No_Answer(1001, "88888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "13.分机1001拨打82888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_13_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "82888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "14.分机1001拨打83888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_14_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 83888");
        pjsip.Pj_Make_Call_No_Answer(1001, "83888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "83888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "15.分机1001拨打84888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_15_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 84888");
        pjsip.Pj_Make_Call_No_Answer(1001, "84888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "84888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "16.分机1001拨打85888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_16_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 85888");
        pjsip.Pj_Make_Call_No_Answer(1001, "85888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "17.分机1001拨打86888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_17_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 86888");
        pjsip.Pj_Make_Call_No_Answer(1001, "86888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "18.分机1001拨打87888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_18_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 87888");
        pjsip.Pj_Make_Call_No_Answer(1001, "87888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "87888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "19.分机1001拨打88888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_19_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 88888");
        pjsip.Pj_Make_Call_No_Answer(1001, "88888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "88888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "20.分机1000拨打*99强制为下班时间\n" +
            "\t分机1001拨打82888呼出\n" +
            "\t\t呼出失败，通话被挂断\n" +
            "\t\t\t分机1000拨打*99取消强制为下班时间；\n" +
            "分机1001拨打82888呼出；\n" +
            "\t\t\t\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_20_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        step("****** 分机1000拨打*99强制为下班时间 *****");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        step("****** 分机1000拨打*99强制为下班时间 *****");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "82888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "21.分机1001拨打82888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_21_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "22.分机1001拨打83888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_22_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 83888");
        pjsip.Pj_Make_Call_No_Answer(1001, "83888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "23.分机1001拨打84888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_23_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 84888");
        pjsip.Pj_Make_Call_No_Answer(1001, "84888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "24.分机1001拨打85888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_24_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 2222");
        pjsip.Pj_Make_Call_No_Answer(1001, "85888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "25.分机1001拨打86888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_25_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 86888");
        pjsip.Pj_Make_Call_No_Answer(1001, "86888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "26.分机1001拨打87888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_26_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 87888");
        pjsip.Pj_Make_Call_No_Answer(1001, "87888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "27.分机1001拨打88888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_27_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 88888");
        pjsip.Pj_Make_Call_No_Answer(1001, "88888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件:\n" +
            "Business Hours：00:00-06:00;06:00-08:00;13:00-18:00\n" +
            "Break Hours：07:00-14:00;18:00-23:59\n" +
            "Days of week：\tSunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday" +
            "Business Hours and Holidays->Holidays 添加2020-1-1~2030-12-31 为Holidays" +
            "28.分机1000拨打*99强制为上班时间\n" +
            "\t分机1001拨打82888呼出\n" +
            "\t\t辅助2分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打*99取消强制为上班时间；\n" +
            "分机1001拨打82888呼出\n" +
            "\t\t\t\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_28_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":1").
                createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
        apiUtil.deleteHoliday("Holidays1").createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();

        step("****** 分机1000拨打*99强制为上班时间 *****");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "82888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        step("****** 分机1000拨打*99强制为上班时间 *****");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "29.分机1001拨打82888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_29_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 82888");
        pjsip.Pj_Make_Call_No_Answer(1001, "82888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "82888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "30.分机1001拨打83888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P2",""})
    public void testExtension_30_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 83888");
        pjsip.Pj_Make_Call_No_Answer(1001, "83888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "83888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "31.分机1001拨打84888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_31_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 84888");
        pjsip.Pj_Make_Call_No_Answer(1001, "84888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "32.分机1001拨打85888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_32_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 85888");
        pjsip.Pj_Make_Call_No_Answer(1001, "85888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "85888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "33.分机1001拨打86888呼出\n" +
            "\t呼出失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_33_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 86888");
        pjsip.Pj_Make_Call_No_Answer(1001, "86888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).isIn(HUNGUP, IDLE).as("通话状态校验 失败!");
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "34.分机1001拨打87888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_34_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 87888");
        pjsip.Pj_Make_Call_No_Answer(1001, "87888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "87888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Extension-CallRestrictions")
    @Story("DisableOutboundCallsoutsideBusinessHours,OutboundRoute,BusinessHours,Holidays")
    @Description("1、Business Hours and Holidays->Business Hours 删除所有\n" +
            "2、Business Hours and Holidays->Holidays 删除所有；\n" +
            "3、编辑分机1001，Security-》禁用Disable Outbound Calls ,启用Disable Outbound Calls outside Business Hours" +
            "编辑分机1001，Security-》禁用Disable Outbound Calls ,禁用Disable Outbound Calls outside Business Hours" +
            "35.分机1001拨打88888呼出\n" +
            "\t辅助2分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","Extension", "CallRestrictions","DisableOutboundCallsoutsideBusinessHours","OutboundRoute","BusinessHours","Holidays","P3",""})
    public void testExtension_35_Callsoutside() {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().
                editExtension("1001","\"disable_outb_call\":0,\"disable_office_time_outb_call\":0").apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 88888");
        pjsip.Pj_Make_Call_No_Answer(1001, "88888", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "88888", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
}