package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: Feature Code Switch Business Hours Status
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestFeatureCodeSwitchBusinessHoursStatus extends TestCaseBaseNew {
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

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
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }
    /**
     * Business Hours and Holidays->Business Hours 删除所有；添加一条
     * 00:00-00:00，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     * *
     */
    public void initBusinessHours() {
        log.info("#### Business Hours and Holidays->Business Hours 添加上班时间条件00:00~23:59 选择星期一至星期日；");
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-23:59");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("SwitchBusinessHoursStatus")
    @Description("Business Hours and Holidays->Business Hours 添加上班时间条件00:00~23:59 选择星期一至星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；\n" +
            "\t1.编辑Switch Business Hours Status 禁用\n" +
            "\t\t分机1000拨打*99\n" +
            "\t\t\tasterisk 检查强制切换失败\n" +
            "\t\t\t\t编辑Switch Business Hours Status 启用\n" +
            "\t\t\t\t\t分机1000拨打*99\n" +
            "\t\t\t\t\t\tasterisk 检查强制切换为下班时间\n" +
            "\t\t\t\t\t\t\t分机1000拨打*99\n" +
            "\t\t\t\t\t\t\t\tasterisk 检查取消强制为下班时间\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2","testOR_01_SwitchBusinessHoursStatus"})
    public void testOR_01_SwitchBusinessHoursStatus() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();

        step("###  编辑Switch Business Hours Status 禁用");
        apiUtil.editFeatureCode("\"enb_office_time\":0").apply();

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("####  编辑Switch Business Hours Status 启用");
        apiUtil.editFeatureCode("\"enb_office_time\":1").apply();
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).contains("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("SwitchBusinessHoursStatus")
    @Description("Business Hours and Holidays->Business Hours 添加上班时间条件00:00~23:59 选择星期一至星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；\n" +
            "\t2.编辑Switch Business Hours Status 特征码为****123\n" +
            "\t\t分机1000拨打*99\n" +
            "\t\t\tasterisk 检查强制切换失败\n" +
            "\t\t\t\t分机1000拨打****123\n" +
            "\t\t\t\t\tasterisk 检查强制切换为下班时间\n" +
            "\t\t\t\t\t\t编辑Switch Business Hours Status 特征码为*99\n" +
            "分机1000拨打****123\n" +
            "\t\t\t\t\t\t\tasterisk 检查保持强制切换为下班时间\n" +
            "\t\t\t\t\t\t\t\t分机1000拨打*99\n" +
            "\t\t\t\t\t\t\t\t\tasterisk 检查取消强制为下班时间\n" +
            "\t\t有BUG ，Apply 加载值不对")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_02_SwitchBusinessHoursStatus() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();

        step("###  编辑Switch Business Hours Status 特征码为****123");
        apiUtil.editFeatureCode("\"enb_office_time\":1,\"office_time\":\"****123\"").apply();

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1000拨打****123");
        pjsip.Pj_Make_Call_No_Answer(1000, "****123", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).contains("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("###  编辑Switch Business Hours Status 特征码为*99");
        apiUtil.editFeatureCode("\"enb_office_time\":1,\"office_time\":\"*99\"").apply();

        step("分机1000拨打****123");
        pjsip.Pj_Make_Call_No_Answer(1000, "****123", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).contains("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件00:00~23:59 选择星期一至星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；" +
            "3.分机1000拨打*99\n" +
            "\tasterisk 检查强制切换为下班时间\n" +
            "\t\t分机1000拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为下班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_03_Permission() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();
        apiUtil.editFeatureCode("\"enb_office_time\":1,\"office_time\":\"*99\"").apply();

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).contains("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件00:00~23:59 选择星期一至星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；" +
            "4.分机1002拨打*99\n" +
            "\tasterisk 检查强制切换为下班时间\n" +
            "\t\t分机1003拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为下班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_04_Permission() throws IOException, JSchException {
        prerequisite();
        initBusinessHours();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();

        step("分机1002拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1002, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1002, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).contains("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1003, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1003, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件00:00~00:00 选择星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；" +
            "5.分机1000拨打*99\n" +
            "\tasterisk 检查强制切换为上班时间\n" +
            "\t\t分机1001拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为上班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_05_Permission() throws IOException, JSchException {
        prerequisite();
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun", officeTimes, resetTimes).apply();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1001拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1001, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1001, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : office").as("[asterisk 切换失败]");

    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Business Hours 添加上班时间条件00:00~00:00 选择星期日；\n" +
            "Business Hours and Holidays->Holidays 删除所有；" +
            "6.分机1002拨打*99\n" +
            "\tasterisk 检查强制切换为上班时间\n" +
            "\t\t分机1003拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为上班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_06_Permission() throws IOException, JSchException {
        prerequisite();
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun", officeTimes, resetTimes).apply();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();

        step("分机1002拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1002, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1002, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1003拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1003, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1003, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : office").as("[asterisk 切换失败]");
    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "7.分机1000拨打*99\n" +
            "\tasterisk 检查强制切换为上班时间\n" +
            "\t\t分机1001拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为上班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_07_Permission() throws IOException, JSchException {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();

        step("分机1000拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1000, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1000, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1001拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1001, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1001, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : office").as("[asterisk 切换失败]");

    }

    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "Business Hours and Holidays->Holidays，添加Holidays1，Type：By Date ，Date：2020-1-1~2030-12-31 为假期【判断是否存在，存在则不用重复添加】" +
            "8.分机1002拨打*99\n" +
            "\tasterisk 检查强制切换为上班时间\n" +
            "\t\t分机1003拨打*99\n" +
            "\t\t\tasterisk 检查取消强制为上班时间")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_08_Permission() throws IOException, JSchException {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createHolidayTime("Holidays1", "date", "01/01/2020-12/31/2030").apply();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\"},{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"testa D\",\"text2\":\"1003\"},{\"value\":\"%s\",\"type\":\"ext_group\",\"text\":\"ExGroup2\",\"text2\":\"ExGroup2\"}]",
                apiUtil.getExtensionGroupSummary("ExGroup1").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionGroupSummary("ExGroup2").id)).apply();

        step("分机1002拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1002, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1002, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

        step("分机1003拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1003, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1003, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : office").as("[asterisk 切换失败]");
    }
    @Epic("P_Series")
    @Feature("FeatureCode-SwitchBusinessHoursStatus")
    @Story("Permission")
    @Description("Permission选择分机组ExGroup1、分机1003、ExGroup2" +
            "9.编辑只选择分机1000\n" +
            "\t分机1002拨打*99\n" +
            "\t\tasterisk 检查强制切换失败")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2",""})
    public void testOR_09_Permission() throws IOException, JSchException {
        prerequisite();
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().apply();
        apiUtil.deleteAllHoliday().editFeatureCode(String.format("\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"test A\",\"text2\":\"1000\"}]",
                apiUtil.getExtensionSummary("1000").id)).apply();

        step("分机1002拨打*99");
        pjsip.Pj_Make_Call_No_Answer(1002, "*99", DEVICE_IP_LAN, false);
        getExtensionStatus(1002, HUNGUP, 30);

        step("[Asterisk校验]");
        assertThat(getAsteriskFORCEDEST()).doesNotContain("/FORCEDEST/global                                 : outoffice").as("[asterisk 切换失败]");

    }

    /**
     * 获取 database show FORCEDEST 返回值
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public String getAsteriskFORCEDEST() throws IOException, JSchException {
        return SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "database show FORCEDEST"));
    }

}