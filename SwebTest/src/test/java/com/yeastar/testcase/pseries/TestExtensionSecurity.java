package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement.*;
import static com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement.*;
import static com.yeastar.swebtest.driver.SwebDriverP.*;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * @program: SwebTest
 * @description: Extension security 用例
 * @author: huangjx@yeastar.com
 * @create: 2020/06/17
 *
 *  * 【修改记录】
 *  *  20210126--重复--注释7条用例,enabled = false
 */
@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionSecurity extends TestCaseBase {
    private APIUtil apiUtil = new APIUtil();
    /**
     * 前提条件
     * 1.添加0分机和sps中继到 路由AutoTest_Route
     */
    public void prerequisite(){
        //新增呼出路由 添加分机0 ，到路由AutoTest_Route
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        List<String> trunks = new ArrayList<>();
        trunks.add(SPS);
        list.clear();
        list.add(SPS);
        list2.clear();
        list2.add("0");

         String reqDataCreateSPS_2 = String.format("" +
                        "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
                ,SPS,"DOD",DEVICE_ASSIST_2,DEVICE_ASSIST_2);
        final String reqDataCreateExtension = String.format("" +
                        "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
                , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
        String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");
        apiUtil.deleteAllExtension();
        apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));
        //创建trunk
//        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
//        auto.trunkPage().deleteTrunk(getDriver(),SPS).createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();
        step("创建SPS中继");
        apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2);
        //创建路由-UI
//        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_outbound_routes);
//        auto.outBoundRoutePage().deleteAllOutboundRoutes().createOutbound("AutoTest_Route",list,list2)
//                .addPatternAndStrip(0,"X.","").clickSaveAndApply();
        //创建路由-API
        step("创建呼出路由");
        apiUtil.deleteAllOutbound().createOutbound("Outbound1",trunks,list2);
        apiUtil.apply();

    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("[前提条件] 1.sip 点对点中继" +
            "2.呼出路由AutoTest_Route" +
            "启用disable outbound call-呼出失败：1:login PBX->2:创建分机号0,启用disable outbound call->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("1001596")
    @Issue("")
    @Test(groups = {"P0","testEnableDisableOutCall","Extension","Regression","PSeries","Security"},enabled = false)
    public void testEnableDisableOutCall(){
        prerequisite();

        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
//        auto.extensionPage().deleAllExtension().clickSaveAndApply();
        auto.extensionPage().editExtension("0").
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_outb_call_checkbox,true).
                clickSaveAndApply();

        assertStep("3.验证通话状态,1.预期0会Ring ,2.预期2000不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(0,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(0,HUNGUP,20),HUNGUP,"预期0为HUNGUP状态");
        softAssert.assertEquals(getExtensionStatus(2000,IDLE,10),IDLE,"预期2000不会响铃");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("禁用disable outbound call-呼出成功：1:login PBX->2:创建分机号0,启用disable outbound call->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("1001599")
    @Issue("")
    @Test(groups = {"P0","testDisableOutCall","Extension","Regression","PSeries","Security"},enabled = false)
    public void testDisableOutCall() throws IOException, JSchException {
        prerequisite();

        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
//
        step("2:创建分机号0,启用disable outbound call");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
//        auto.extensionPage().deleAllExtension().clickSaveAndApply();
        auto.extensionPage().editExtension("0").
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_outb_call_checkbox,false).clickSaveAndApply();

        assertStep("3.验证通话状态,1.预期0会Ring ,2.预期2001不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);

        if(getExtensionStatus(0, IDLE, 5) != IDLE) {
            log.debug("0注册失败" + "stats："+getExtensionStatus(0, IDLE, 5));
        }
        if(getExtensionStatus(2001, IDLE, 5) != IDLE) {
            log.debug("2001注册失败" + "stats："+getExtensionStatus(2001, IDLE, 5));
        }

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"2001",DEVICE_IP_LAN,false);
        softAssertPlus.assertThat(getExtensionStatus(0,TALKING,10)).isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(2001,TALKING,10)).isEqualTo(TALKING);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]4.验证通话状态");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
//        ys_waitingTime(WaitUntils.SHORT_WAIT);
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Status",0),"ANSWERED");
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call From",0),"0<0>");
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call To",0),"2001");
        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(Assertions.tuple("0<0>", "2001", "ANSWERED", "0<0> hung up"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启用disable outbound calls outside business hours功能，则下班时间不能呼出：1:login PBX->2:创建分机号0,启用disable outbound calls outside business hours->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Issue(" bug 23版本 设置不生效，待24版本验证")
    @Test(groups = {"P0","testEnableDisableOutCallBusinessHours","Extension","Regression","PSeries","Security"},enabled = false)
    public void testEnableDisableOutCallBusinessHours(){
        prerequisite();
        //新增时间条件
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用disable outbound calls outside business hours");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension("0").
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_office_time_outb_call_checkbox,true).
                clickSaveAndApply();


        //设置下班时间为当前时间的前2小时-前3小时
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_office_time_and_holidays);
        auto.businessHoursAndHoildaysPage().deleAllData().addBusinessHours(
                DataUtils.getCurrentTimeAndOffset("HH:mm",-3,0),
                DataUtils.getCurrentTimeAndOffset("HH:mm",-2,0),
               "","",
                DataUtils.getCurrentWeekDay()).clickSaveAndApply();


        assertStep("3.验证通话状态,1.预期0会Ring ,2.预期2001不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);

        //勾选，下班时间不能呼出
        pjsip.Pj_Make_Call_No_Answer(0,"2000",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(0,HUNGUP,10),HUNGUP,"预期0为HUNGUP状态");
        softAssert.assertEquals(getExtensionStatus(2001,IDLE,10),IDLE,"预期2001不会响铃");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
        //todo bug 23版本 设置不生效，待24版本验证
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("禁用disable outbound calls outside business hours，下班时间可以正常呼出：1:login PBX->2:创建分机号0,禁用disable outbound calls outside business hours->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("1001597")
    @Issue("")
    @Test(groups = {"P0","testDisableOutCallBusinessHours","Extension","Regression","PSeries","Security"},enabled = false)
    public void testDisableOutCallBusinessHours() throws IOException, JSchException {
        prerequisite();

        //新增时间条件
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,禁用disable outbound calls outside business hours");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension("0").
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_disable_office_time_outb_call_checkbox,false).clickSaveAndApply();

        //设置下班时间为当前时间的前2小时-前3小时
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_office_time_and_holidays);
        auto.businessHoursAndHoildaysPage().deleAllData().addBusinessHours(
                DataUtils.getCurrentTimeAndOffset("HH:mm",-3,0),
                DataUtils.getCurrentTimeAndOffset("HH:mm",-2,0),
                "","",
                DataUtils.getCurrentWeekDay()).clickSaveAndApply();

        assertStep("3.验证通话状态,1.预期0会Ring ,2.预期2001不会响铃");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"2001",DEVICE_IP_LAN,false);
        softAssert.assertEquals(getExtensionStatus(0,TALKING,10),TALKING,"预期0为TALKING状态");
        softAssert.assertEquals(getExtensionStatus(2001,TALKING,10),TALKING,"预期2001为TALKING状态");
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]4.验证通话状态");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Status",0),"ANSWERED");
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call From",0),"0<0>");
        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call To",0),"2001");
        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("启动Allow Register Remotely：1:login PBX->2:创建分机号0,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001600"), @TmsLink(value = "ID1001601")})
    @Issue("")
    @Test(groups = {"P0","testEnableAllowRegisterRemotely","Extension","Regression","PSeries","Security"})
    public void testEnableAllowRegisterRemotely() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_allow_reg_remotely_checkbox,false).clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：remoteregister                     : no ");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains(" remoteregister                : no"),"[remoteregister no]");

        step("4:修改分机号0,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_allow_reg_remotely_checkbox,true).clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：remoteregister                     : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains(" remoteregister                : yes"),"[remoteregister yes]");

        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("验证 Enable User Agent Registration Authorization：1:login PBX->2:创建分机号0,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001602"), @TmsLink(value = "ID1001603")})
    @Issue("")
    @Test(groups = {"P0","testEnableUserAgentRegistrationAuthorization","Extension","Regression","PSeries","Security"})
    public void testEnableUserAgentRegistrationAuthorization() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_enb_user_agent_ident_checkbox,false).clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：enableuseragent               : no");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("enableuseragent               : no"),"[enableuseragent no]");

        step("4:修改分机号0,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_enb_user_agent_ident_checkbox,true)
                .addUserAgent(0,"Yealink.")
                .addUserAgent(1,"Test.")
                .addUserAgent(2,"Yeastar.")
                .addUserAgent(3,"Zoiper.")
                .addUserAgent(4,"Y.")
                .clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：enableuseragent               : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("enableuseragent               : yes"),"[enableuseragent yes]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("limituseragent                : Yealink.,Test.,Yeastar.,Zoiper.,Y."),"[limituseragent yes]");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Security")
    @Description("验证 SIP Registration IP Restriction：1:login PBX->2:创建分机号0,启用Allow Register Remotely->3.验证通话状态")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLinks(value = {@TmsLink(value = "ID1001604"), @TmsLink(value = "ID1001605"), @TmsLink(value = "1001606")})
    @Issue("")
    @Test(groups = {"P0","testSIPRegistrationIPRestriction","Extension","Regression","PSeries","Security","Security"})
    public void testSIPRegistrationIPRestriction() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Security").
                isCheckbox(IExtensionPageElement.ele_extension_security_enb_ip_rstr_checkbox,false).clickSaveAndApply();

        assertStep("3:[PJSIP]期望结果：enableiprestrict               : no");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("enableiprestrict              : no"),"[enableiprestrict no]");

        step("4:修改分机号0,remoteregister->启用");
        auto.extensionPage().editFirstData().switchToTab("Security").isCheckbox(IExtensionPageElement.ele_extension_security_enb_ip_rstr_checkbox,true)
                .addIPRestriction(0,"192.168.3.0","255.255.255.0")
                .addIPRestriction(1,"192.168.0.0","255.255.0.0")
                .addIPRestriction(2,"110.80.36.162","255.255.255.255")
                .addIPRestriction(3,"192.168.202.0","255.255.254.0")
                .clickSaveAndApply();

        assertStep("5:[PJSIP]期望结果：enableiprestrict               : yes");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("enableiprestrict              : yes"),"[enableiprestrict yes]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("permitip1                     : 192.168.3.0/255.255.255.0"),"[permitip1 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("permitip2                     : 192.168.0.0/255.255.0.0"),"[permitip2 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("permitip3                     : 110.80.36.162/255.255.255.255"),"[permitip3 ]");
        softAssert.assertTrue(execAsterisk(PJSIP_SHOW_ENDPOINT+"0").contains("permitip4                     : 192.168.202.0/255.255.254.0"),"[permitip4 ]");
        softAssert.assertAll();
    }



    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
                 "b.全局  preference，Max Call Duration -> 设置30s" +
                 "【验证内部分机互打】" +
                 "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001609")
    @Issue("bug cdr 多显示一条 call to T")
    @Test(groups = {"P0","TestExtensionBasicDisplayAndRegistration","testCalled0To9999999","Regression","PSeries","Security"},enabled = false)
    public void testMaxCallDurationForInternalExtension() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0，分机：9999999");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202").clickSave();
        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999","Yeastar202").clickSaveAndApply();;

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editFirstData().switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();

        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30")
                .clickSaveAndApply();


        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(9999999,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(9999999,DEVICE_IP_LAN);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"9999999",DEVICE_IP_LAN,false);

        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        //todo cdr 显示全选
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
//        ys_waitingTime(WaitUntils.SHORT_WAIT);
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Reason",0),"Exceeded the max call duration(s)");
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call Duration(s)",0),"00:00:30");
//        softAssert.assertAll();


        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","callDuration","status","reason")
                    .contains(tuple("Yeastar Test0 朗视信息科技<0>", "Yeastar Test9999999 朗视信息科技<9999999>", "30","ANSWERED", "Exceeded the max call duration(s)"));
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
            "b.全局  preference，Max Call Duration -> 设置30s" +
            "【验证SPS Trunk 呼出】，0 呼叫 2000" +
            "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001610")
    @Issue("bug cdr 多显示一条为 call to T的记录")
    @Test(groups = {"P0","testMaxCallDurationForSPSTrunk","testCalled0To9999999","Regression","PSeries","Security"},enabled = false)
    public void testMaxCallDurationForSPSTrunk() throws IOException, JSchException {
        prerequisite();

        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
//        auto.extensionPage().
//                deleAllExtension().
//                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202").clickSaveAndApply();

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editExtension("0").switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();



//        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_outbound_routes);
//        auto.outBoundRoutePage().editOutbound("Outbound1","Name").addExtensionOrExtensionGroup("0").clickSaveAndApply();


        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30").clickSaveAndApply();


        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(3000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"3000",DEVICE_IP_LAN,false);
        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","callDuration","status","reason")
                .contains(tuple("0<0>", "3000", "60","ANSWERED", "Exceeded the max call duration(s)"));
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("BasicDisplayAndRegistration")
    @Description("a.分机 0，Max Call Duration -> 设置60s," +
            "b.全局  preference，Max Call Duration -> 设置30s" +
            "【验证SPS Trunk 呼入】，2000 呼叫 0-->通话时长为30S" +
            "1:login PBX->2:创建分机号0->3:验证保存成功->4:删除分机->5:验证删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("ID1001611")
    @Issue("bug cdr 多显示一条 call to T")
    @Test(groups = {"P0","TestExtensionSecurity","testMaxCallDurationForSPSTrunkTo0","Regression","PSeries","Security"},enabled = false)
    public void testMaxCallDurationForSPSTrunkTo0() throws IOException, JSchException {
        step("1.login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2.创建分机：0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().
                deleAllExtension().
                createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0","Yeastar202Yeastar202").clickSaveAndApply();;

        assertStep("3:设置分机0，Max Call Duration 设置60s");
        auto.extensionPage().editFirstData().switchToTab("Security").setElementValue(ele_extension_security_max_outb_call_duration_select,"60")
                .clickSaveAndApply();

        assertStep("4:全局设置 preference，Max Call Duration 设置30s");
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().setElementValue(ele_pbx_settings_preferences_max_call_duration_select,"30")
                .clickSaveAndApply();

        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"995550330",DEVICE_ASSIST_2,false);
        ys_waitingTime(70000);
        pjsip.Pj_Hangup_All();

        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ; Call Duration(s)=00:00:30");
        //todo cdr 显示全选
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        //todo delete sleep
//        ys_waitingTime(WaitUntils.SHORT_WAIT);
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Reason",0),"Exceeded the max call duration(s)");
//        softAssert.assertEquals(TableUtils.getTableForHeader(getDriver(),"Call Duration(s)",0),"00:01:00");
//        softAssert.assertAll();
//        assertStep("[CDR]5.第一条记录：Reason = Exceeded the max call duration(s) ；Call Duration(s)=00:00:30");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","callDuration","status","reason","callDuration")
                .contains(tuple("Yeastar Test0 朗视信息科技<0>", "3000", "60","ANSWERED", "Exceeded the max call duration(s)","60"));
        softAssertPlus.assertAll();
    }


    //@Test
    public void testSelectTime() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号0,启用Allow Register Remotely");
        auto.homePage().intoPage(HomePage.Menu_Level_1.system, HomePage.Menu_Level_2.system_tree_email);
        auto.extensionPage().testBtn.shouldBe(Condition.enabled).click();

        sleep(3000);
        List<WebElement> elements_input = getWebDriver().findElements(By.xpath("//input"));
        elements_input.get(elements_input.size()-1).sendKeys("yeastarautotest@163.com");
        actions().sendKeys(Keys.ENTER).perform();

        sleep(3000);
//        List<WebElement> elements_Test = getWebDriver().findElements(By.xpath("//span[contains(text(),'Test')]"));
//        actions().moveToElement(elements_Test.get(elements_Test.size()-1),2,2).click().build().perform();
//        elements_input.get(elements_Test.size()-1).click();

       // auto.extensionPage().getLastElementOffsetAndClick(auto.extensionPage().testBtn,2,2);

        SelenideElement element_success = $(By.xpath("//span[contains(text(),'Success')]"));
        auto.extensionPage().waitElementDisplay(element_success,WaitUntils.TIME_OUT_SECOND);

    }

}
