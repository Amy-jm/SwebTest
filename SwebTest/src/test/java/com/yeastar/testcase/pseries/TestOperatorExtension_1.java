package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD_DETAILS;
import com.yeastar.page.pseries.OperatorPanel.Record;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @program: SwebTest
 * @description: Operator Panel Test Case
 * @author: huangjx@yeastar.com
 * @create: 2020/07/30
 */
@Log4j2
public class TestOperatorExtension_1 extends TestCaseBaseNew {
    APIUtil apiUtil = new APIUtil();
    ArrayList<String> queueListNum = null;
    ArrayList<String> ringGroupNum_0 = null;
    String queueListName = "Q0";
    String ringGroupName_0 = "RG0";//6300
    Object[][] routes = null;
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isRunInitExtensionFlag = true;
    private final String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private final String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"1-3\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private final String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);

    /**
     * ????????????
     * 1.??????0?????????sps????????? ??????AutoTest_Route
     */
    public void prerequisite(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        //?????????????????? ????????????0 ????????????AutoTest_Route
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        list.add(SPS);
        list2.add("0");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        //????????????
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0", EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1000", EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1001", EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1002", EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        //??????trunk
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        auto.trunkPage().deleteTrunk(getDriver(), SPS).createSpsTrunk(SPS, DEVICE_ASSIST_2, DEVICE_ASSIST_2).clickSaveAndApply();

        //??????Inbound
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().deleteAllInboundRoutes().createInboundRoute("InRoute1", list).addDIDPatternAnd(0, "X.").selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(), "1000").clickSaveAndApply();

        auto.homePage().logout();

    }

    private boolean registerAllExtension(){
        log.debug("[prerequisite] init extension");
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_CreateAccount(4000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        return registerAllExtensions();
//        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
//        pjsip.Pj_Register_Account_WithoutAssist(4000,DEVICE_ASSIST_3);
//
//        boolean reg=false;
//        if(getExtensionStatus(1000, IDLE, 5) != IDLE) {
//            reg = true;
//            log.debug("1000????????????");
//        }
//        if(getExtensionStatus(1001, IDLE, 5) != IDLE) {
//            reg = true;
//            log.debug("1001????????????");
//        }
//        if(getExtensionStatus(1002, IDLE, 5) != IDLE) {
//            reg = true;
//            log.debug("1002????????????");
//        }
//        if(getExtensionStatus(1003, IDLE, 5) != IDLE) {
//            reg = true;
//            log.debug("1003????????????");
//        }
//        if(getExtensionStatus(1004, IDLE, 5) != IDLE){
//            reg=true;
//            log.debug("1004????????????");
//        }
//        if(getExtensionStatus(1005, IDLE, 5) != IDLE){
//            reg=true;
//            log.debug("1005????????????");
//        }
//        if(getExtensionStatus(2000, IDLE, 5) != IDLE){
//            reg=true;
//            log.debug("2000????????????");
//        }
//        if(getExtensionStatus(2001, IDLE, 5) != IDLE){
//            reg=true;
//            log.debug("2001????????????");
//        }
//        if(getExtensionStatus(4000, IDLE, 5) != IDLE){
//            reg=true;
//            log.debug("4000????????????");
//        }
//        return reg;
    }
    private boolean isDebugInitExtensionFlag = false;
    public void prerequisiteForAPIExtension() {
        //local debug
        if(isDebugInitExtensionFlag){
            log.debug("*****************init extension************");
            registerAllExtension();
            isRunRecoveryEnvFlag = false;
        }


        if (isRunRecoveryEnvFlag) {
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            trunks.add(BRI_1);
            trunks.add(FXO_1);
            trunks.add(E1);
            trunks.add(SIPTrunk);
            trunks.add(ACCOUNTTRUNK);
            trunks.add(GSM);
            List<String> extensionNum = new ArrayList<>();
            queueListNum = new ArrayList<>();
            ringGroupNum_0 = new ArrayList<>();

            step("???????????????");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");

            extensionNum.add("0");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");
            step("????????????1000-1005");
            apiUtil.deleteAllExtension();
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1000").replace("EXTENSIONLASTNAME", "A").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "E").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "F").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("??????SPS??????");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2);

            step("??????????????????InRoute1,??????????????????1000");
            apiUtil.deleteAllInbound().createInbound("InRoute1", trunks, "Extension", "1000");

            step("??????????????????");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);

            step("??????IVR_0");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));

            apiUtil.deleteAllIVR().createIVR("6200", "6200", pressKeyObjects_0);

            step("????????????");
//            queueListNum.add("1000");
            queueListNum.add("1001");
            queueListNum.add("1002");
            queueListNum.add("1003");
            apiUtil.deleteAllQueue().createQueue(queueListName, "6400", null, queueListNum, null);


            step("???????????????6300");
//            ringGroupNum_0.add("1000");
            ringGroupNum_0.add("1001");
            ringGroupNum_0.add("1002");
            ringGroupNum_0.add("1003");
            ringGroupNum_0.add("1004");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName_0, "6300", ringGroupNum_0);

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            isRunRecoveryEnvFlag = registerAllExtension();
        }
    }


    /**
     * ?????????????????????
     * routePrefix?????????????????? + caller???????????? + callee???????????? + device_assist????????????????????????ip??? + vcpCaller???VCP????????????????????????????????? + vcpDetail???VCP????????????Detail????????? + testRouteTypeMessage??????????????????
     *
     */
    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c) {
        Object[][] group = null;
        if(!FXO_1.trim().equalsIgnoreCase("null")){
            routes = new Object[][]{
                    {"99", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "SPS"},//sps   ?????? ??????
                    {"88", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "BRI"},//BRI   ?????? ??????
                    {"", 2000, "2005", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "FXO"},//FXO --77 ??????   2005???FXS???
                    {"77", 2000, "1000", DEVICE_ASSIST_2, "1020", RECORD_DETAILS.INTERNAL.getAlias(), "FXS"},//FXS    1.????????????????????????????????????(???????????????)  2.????????????1020FXS??????
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "E1"},//E1     ?????? ??????
                    {"", 3001, "3000", DEVICE_ASSIST_1, "3001", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_REGISTER"},
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "6700", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_ACCOUNT"},//SIP  --55 REGISTER
//            {"33",2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM,RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
            };
        }else {
            routes = new Object[][]{
                    {"99", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "SPS"},//sps   ?????? ??????
                    {"", 3001, "3000", DEVICE_ASSIST_1, "3001", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_REGISTER"},
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "6700", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_ACCOUNT"},//SIP  --55 REGISTER
            };
        }
        for (String groups : c.getIncludedGroups()) {
            log.debug("[c.getIncludedGroups]" + groups);
            for (int i = 0; i < routes.length; i++) {
                for (int j = 0; j < routes[i].length; j++) {
                    log.debug("[routes] i:" + i + "j:" + j + "---->>>" + routes[i][j]);
                    if (groups.equalsIgnoreCase("SPS")) {
                        group = new Object[][]{{"99", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "SPS"}};
                    } else if (groups.equalsIgnoreCase("BRI")) {
                        group = new Object[][]{{"88", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "BRI"}};
                    } else if (groups.equalsIgnoreCase("FXO")) {
                        group = new Object[][]{{"", 2000, "2005", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "FXO"}};
                    } else if (groups.equalsIgnoreCase("FXS")) {
                        group = new Object[][]{{"77", 2000, "1000", DEVICE_ASSIST_2, "1020", RECORD_DETAILS.INTERNAL.getAlias(), "FXS"}};
                    } else if (groups.equalsIgnoreCase("E1")) {
                        group = new Object[][]{{"66", 2000, "1000", DEVICE_ASSIST_2, "2000", RECORD_DETAILS.EXTERNAL.getAlias(), "E1"}};
                    } else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                        group = new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_REGISTER"}};
                    } else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                        group = new Object[][]{{"44", 4000, "1000", DEVICE_ASSIST_3, "4000", RECORD_DETAILS.EXTERNAL.getAlias(), "SIP_ACCOUNT"}};
                    } else if (groups.equalsIgnoreCase("GSM")) {
                        group = new Object[][]{{"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM, RECORD_DETAILS.EXTERNAL.getAlias(), "GSM"}};
                    } else {
                        group = routes;//??????????????????????????????????????????
                    }
                }
            }
        }
        //jenkins  run with xml and ITestContext c will be null
        if (group == null) {
            group = routes; //default run all routes
        }
        return group;
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->???????????????ringing\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRingStatus", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRingStatus(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("4:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);

        if(String.valueOf(caller).trim().equalsIgnoreCase("4000")){
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("["+vcpCaller+"]", "1000 A [1000]", "Ringing", vcpDetail));
        }else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));
        }

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???1001???Talking???\n" +
            "1:??????0,login web client\n" +
            "2:????????????[1002]-->[1001]??????\n+" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("??????????????????????????????????????????30s timeout ??????voicemail")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropWithCTalking", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropWithCTalking(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//        refresh();

        step("4:???1002 ???1001 ????????????1001 ???Talking??????");
        pjsip.Pj_Make_Call_Auto_Answer(1002, "1001", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("5:???2000 ?????? 1000??????1000 ???Ringing??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->?????????[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, "1000", OperatorPanelPage.DOMAIN.EXTENSION, "1001");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Send_Dtmf(1001,404);
        log.debug("2--???1010???"+getExtensionStatus(1001,HUNGUP,3));
        sleep(WaitUntils.SHORT_WAIT*10);
        refresh();


        assertStep("7:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        if(String.valueOf(caller).trim().equalsIgnoreCase("4000")){
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("["+vcpCaller+"]", "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));
        }else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));
        }

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???1001???idle???\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->????????????\n+" +
            "3:[2000 ?????? 1001]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropWithCIdle", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropWithCIdle(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        step("4:???1001??? ????????????");
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        step("5:???2000 ?????? 1001??????1001 ???Ringing??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        auto.operatorPanelPage().waitTableRecordAppear(OperatorPanelPage.TABLE_TYPE.INBOUND, 30);

        step("6???[Inbound]1001 -->?????????[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, "1000", OperatorPanelPage.DOMAIN.EXTENSION, "1001");

        assertStep("7:[VCP]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", vcpDetail));

        pjsip.Pj_Answer_Call(1001, false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:???????????? A--C talking");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        if(String.valueOf(caller).trim().equalsIgnoreCase("4000")){
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("["+vcpCaller+"]", "1001 B [1001]", "Talking", vcpDetail));
        }else {
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", vcpDetail));
        }

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???1001???????????????\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("1.????????????????????????????????????????????? ??????????????????????????? \n+" +
            "2.??????????????????>=6??????????????????,??????")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropWithCUnregistered", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropWithCUnregistered(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("???????????????????????????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.preferences);
        auto.preferencesPage().isChoice(auto.preferencesPage().preference_account_show_unregistered_extensions, true).clickSave();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        step("4:???1001??? ?????????");
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
         pjsip.Pj_Unregister_Account(1001);
         sleep(WaitUntils.SHORT_WAIT);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

//        refresh();//todo extension??????????????? ????????????????????????

        step("5:???2000 ?????? 1000??????1000 ???Ringing??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        auto.operatorPanelPage().waitTableRecordAppear(OperatorPanelPage.TABLE_TYPE.INBOUND, 30);

        step("6???[Inbound]1000 -->?????????[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, caller + "", OperatorPanelPage.DOMAIN.EXTENSION, "1001");

        assertStep("7:[VCP]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        pjsip.Pj_Hangup_All();
        sleep(5000);
        if(getExtensionStatus(1001, IDLE, 5) == IDLE) {
            pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
            if(getExtensionStatus(1001, IDLE, 5) != IDLE){
                log.debug("[????????????1001??????]1001????????????");
            }
        }

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???Ring Group 6300\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropRingGroup", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropRingGroup(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        step("5:???2000 ?????? 1000??????1000 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("6???[Inbound]1000 -->?????????[RingGroup]6300");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, "1000", OperatorPanelPage.DOMAIN.RINGGROUP, "6300");

        assertStep("[VCP??????]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1004 E [1004]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1004 E [1004]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        }
        softAssertPlus.assertThat(allRecordList).as("??????RingGroup??????").size().isEqualTo(ringGroupNum_0.size());

        step("7:????????????1001 ??????");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001, false);

        assertStep("[VCP??????]7:???????????? A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        }
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???Parking\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Parking]001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropParking", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropParking(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();
        ;
        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//
//        step("5:???2000 ?????? 1001??????1001 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1001 -->?????????[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, "1000", OperatorPanelPage.DOMAIN.PARKING, "001");

        assertStep("[VCP??????]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->DragAndDrop ???Queue 6400\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingDragAndDropQueue", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingDragAndDropQueue(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//
//        step("5:???2000 ?????? 1001??????1001 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        step("6???[Inbound]1001 -->?????????[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND, "1000", OperatorPanelPage.DOMAIN.QUEUE, "6400");

        assertStep("[VCP??????]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));
        }
        softAssertPlus.assertThat(allRecordList).as("??????Queue??????").size().isEqualTo(queueListNum.size());

        step("7:????????????1005 ??????");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001, false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP??????]");
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.INTERNAL_QUEUE.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));
        }

        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(1001);

        assertStep("9:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???"+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                    .contains(tuple("2000<2000>".replace("2000",vcpCaller), "Queue Q0<6400>", "ANSWERED", "Queue Q0<6400> connected"),
                            tuple("2000<2000>".replace("2000",vcpCaller), "1001 B<1001>", "ANSWERED", "1001 B<1001> hung up"),
                            tuple("2000<2000>".replace("2000",vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to Q0<6400>"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->??????Extension???C ,A?????????\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->[Redirect] C(??????)" +
            "4:A??????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionRedirectC_AHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionRedirectC_AHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("3:[VCP??????]2000->1000 ???????????? Ring??????");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));

        step("4:??????->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "1001");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("5:[VCP??????]2000->1001 ??????  Ring??????");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", vcpDetail));

        assertStep("6:[VCP??????]2000->1001  ????????? Talking??????");
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1001, false);
        getExtensionStatus(1001,TALKING,60);
//        sleep(WaitUntils.SHORT_WAIT * 2);

        List<Record> resultSum_talking = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_talking).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", vcpDetail));

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "1001 B<1001>", "ANSWERED", "2000<2000> hung up".replace("2000", vcpCaller)),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 1001 B<1001>"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->??????Extension???C ,C?????????\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->[Redirect] C(??????)" +
            "4:c??????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionRedirectC_CHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionRedirectC_CHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("3:[VCP??????]2000->1000 ???????????? Ring??????");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));

        step("4:??????->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "1001");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("5:[VCP??????]2000->1001 ??????  Ring??????");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", vcpDetail));

        assertStep("6:[VCP??????]2000->1001  ????????? Talking??????");
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1001, false);
        getExtensionStatus(1001,TALKING,60);
//        sleep(WaitUntils.SHORT_WAIT * 2);

        List<Record> resultSum_talking = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_talking).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", vcpDetail));

        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "1001 B<1001>", "ANSWERED", "1001 B<1001> hung up"),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 1001 B<1001>"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->Redirect ???Ring Group 6300\n" +
            "1:??????0,login web client\n" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->Redirect[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "????????????")
    @Test(groups = {"P0", "VCP", "testIncomingRedirectRingGroup", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRedirectRingGroup(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005, DEVICE_IP_LAN);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//
//        step("5:???2000 ?????? 1000??????1000 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        auto.operatorPanelPage().waitTableRecordAppear(OperatorPanelPage.TABLE_TYPE.INBOUND, 30);

        step("6???[Inbound]1000 -->??????-->Redirect[RingGroup]6300");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "6300");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP??????]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1004 E [1004]", "Ringing", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                            tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1004 E [1004]", "Ringing", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        }
        softAssertPlus.assertThat(allRecordList).as("??????RingGroup??????").size().isEqualTo(ringGroupNum_0.size());


        step("7:????????????1001 ??????");
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1001, false);
        getExtensionStatus(1001,TALKING,60);

        assertStep("[VCP??????]7:???????????? A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.INTERNAL_RING_GROUP.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(ringGroupName_0 + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        }
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        pjsip.Pj_hangupCall(caller);

        assertStep("9:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "RingGroup RG0<6300>", "ANSWERED", "RingGroup RG0<6300> connected"),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1001 B<1001>", "ANSWERED", "2000<2000> hung up".replace("2000", vcpCaller)),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to RG0<6300>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->Redirect ???Queue 6400\n" +
            "1:??????0,login web client\n" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "????????????")
    @Test(groups = {"P0", "VCP", "testIncomingRedirectQueue", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRedirectQueue(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005, DEVICE_IP_LAN);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

//        step("5:???2000 ?????? 1000??????1000 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->??????-->Redirect[Queue]6400");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "6400");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP??????]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.INTERNAL_AGENT_RING.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordList).extracting("caller", "callee", "status", "details")
                    .contains(
//                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1002 C [1002]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                            tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1003 D [1003]", "Ringing", RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));
        }

        softAssertPlus.assertThat(allRecordList).as("??????Queue??????").size().isEqualTo(queueListNum.size());

        step("7:????????????1005 ??????");
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Answer_Call(1001, false);
        getExtensionStatus(1001,TALKING,60);

        assertStep("[VCP??????]");
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.INTERNAL_QUEUE.getAlias()));
        } else {
            softAssertPlus.assertThat(allRecordListAfter).extracting("caller", "callee", "status", "details")
                    .contains(tuple(queueListName + ":2000 [2000]".replace("2000", vcpCaller + ""), "1001 B [1001]", "Talking", RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));
        }

        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(1001);

        assertStep("9:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "Queue Q0<6400>", "ANSWERED", "Queue Q0<6400> connected"),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1001 B<1001>", "ANSWERED", "1001 B<1001> hung up"),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to Q0<6400>"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->Redirect Voicemail ??????\n" +
            "1:??????0,login web client\n" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->Redirect[Voicemail]?????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRedirectVoicemail", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRedirectVoicemail(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        step("5:???2000 ?????? 1000??????1000 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        auto.operatorPanelPage().waitTableRecordAppear(OperatorPanelPage.TABLE_TYPE.INBOUND, 30);

        step("6???[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "1000", true);

        assertStep("5:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Talking", RECORD_DETAILS.INTERNAL_VOICEMAIL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Talking", RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));
        }

        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(caller);

        assertStep("9:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "Voicemail 1000 A<1000>", "VOICEMAIL", "2000<2000> hung up".replace("2000", vcpCaller)),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 1000 A<1000>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->Redirect IVR \n" +
            "1:??????0,login web client\n" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->Redirect[IVR]6200")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRedirectIVR", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRedirectIVR(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//
//        step("5:???2000 ?????? 1000??????1000 ???Ring??????");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        auto.operatorPanelPage().waitTableRecordAppear(OperatorPanelPage.TABLE_TYPE.INBOUND, 30);

        step("6???[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "6200");

        assertStep("5:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "6200 [6200]", "Talking", RECORD_DETAILS.INTERNAL_IVR.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "6200 [6200]", "Talking", RECORD_DETAILS.EXTERNAL_IVR.getAlias()));
        }

        pjsip.Pj_Hangup_All();

        assertStep("9:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "IVR 6200<6200>", "ANSWERED", "2000<2000> hung up".replace("2000", vcpCaller)),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 6200<6200>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->???????????????\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->?????????????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionUnDisplay", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionUnDisplay(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();
        ;
        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP??????] Ring ????????? Redirect???pick up???hang up");
        List<String> list = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000");

        assertThat(list).doesNotContain("Transfer", "Listen", "Whisper", "Barge", "Park", "Unpark", "Pause", "Recording", "Unrecording");
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->??????REDIRECT???C ,A?????????\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->[REDIRECT] C(??????)" +
            "4:A??????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionRedirectOffLineC_AHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionRedirectOffLineC_AHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(2001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001, DEVICE_ASSIST_2);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("4:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));

        step("5:??????->[Redirect] C(??????)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "2001");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP??????]");
        if (message.equals("FXS")) {
            List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.OUTBOUND);
            softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        } else {
            List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
            softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }

        step("7:[??????]");
        pjsip.Pj_Answer_Call(2001, false);
        getExtensionStatus(2001,TALKING,30);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:[VCP??????]");
        List<Record> resultSum_after_answer;
        if (message.equals("FXS")) {
            resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.OUTBOUND);
        } else {
            resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        }
        softAssertPlus.assertThat(resultSum_after_answer).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Talking", RECORD_DETAILS.EXTERNAL.getAlias()));
        step("9:[??????]");
        pjsip.Pj_hangupCall(caller);

        assertStep("10:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        if (message.equals("SPS") || message.equals("SIP_REGISTER") || message.equals("SIP_ACCOUNT") || message.equals("FXS")) { //?????????????????????????????????reason-????????????
            softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                    .contains(tuple("2000<2000>".replace("2000", vcpCaller), "2001", "ANSWERED", "2000<2000> hung up".replace("2000", vcpCaller)),
                            tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 2001"));
        } else {//??????????????????????????????????????????????????? ?????????????????????reason????????? ????????????
            softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                    .contains(tuple("2000<2000>".replace("2000", vcpCaller), "2001", "ANSWERED", "2001 hung up"),
                            tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 2001"));
        }
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->??????REDIRECT???C ,C?????????\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->[REDIRECT] C(??????)" +
            "4:c??????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionRedirectOffLineC_CHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionRedirectOffLineC_CHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(2001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("4:[VCP??????]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", vcpDetail));

        step("5:??????->[Redirect] C(??????)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT, "2001");
        sleep(WaitUntils.SHORT_WAIT * 2);

        assertStep("6:[VCP??????]");
        List<Record> resultSum_after_redirect;
        if (message.equals("FXS")) {
            resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.OUTBOUND);
        } else {
            resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        }
        softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller", "callee", "status", "details")
                .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));

        step("7:[??????]");
        pjsip.Pj_Answer_Call(2001, false);
        getExtensionStatus(2001,TALKING,30);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:[VCP??????]");
        if (message.equals("FXS")) {
            List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.OUTBOUND);
            softAssertPlus.assertThat(resultSum_after_answer).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Talking", RECORD_DETAILS.EXTERNAL.getAlias()));
        } else {
            List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
            softAssertPlus.assertThat(resultSum_after_answer).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "DOD [2001]", "Talking", RECORD_DETAILS.EXTERNAL.getAlias()));
        }
        step("9:[??????]");
        pjsip.Pj_hangupCall(2001);

        assertStep("10:[CDR??????]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR??????] Time???" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason")
                .contains(tuple("2000<2000>".replace("2000", vcpCaller), "2001", "ANSWERED", "2001 hung up"),
                        tuple("2000<2000>".replace("2000", vcpCaller), "1000 A<1000>", "NO ANSWER", "Redirected to 2001"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->??????HandUp\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->HandUp" +
            "4:????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("3:[VCP??????]2000->1000 ???????????? Ring??????");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }

        step("4:??????->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("5:[VCP??????]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND, 3);
        softAssertPlus.assertThat(resultSum_after.size()).isEqualTo(0);

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->?????? ?????? HandUp\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->?????? ?????? HandUp" +
            "4:????????? ????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionHoverHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionHoverHandUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("3:[VCP??????]2000->1000 ???????????? Ring??????");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }

        step("4:??????->[HandUp]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT);
        auto.operatorPanelPage().moveByOffsetAndClick(200, 200);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("5:[VCP??????]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }
        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->??????10000 ????????? -->PickUp\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????[1000]\n" +
            "3:??????->PickUp" +
            "4:????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0", "VCP", "testIncomingRightActionHandUp", "Regression", "PSeries", "VCP1", "VCP_Extension_1",
            "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT", "GSM"}, dataProvider = "routes")
    public void testIncomingRightActionPickUp(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String message) {
        prerequisiteForAPIExtension();

        step("1:login web click ??????????????????" + message);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP] "+ caller + " ?????? " +callee);
//        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(0, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//        pjsip.Pj_CreateAccount(caller, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
//
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller, deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("4:[VCP??????]2000->1000 ???????????? Ring??????");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecordNotEquals0(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.INTERNAL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "1000 A [1000]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }

        step("5:??????->[??????PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT * 4);//todo BUG ??????>=6s ????????????

        assertStep("6:[VCP??????]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "0 [0]", "Ringing", RECORD_DETAILS.INTERNAL.getAlias()));
        } else {
            softAssertPlus.assertThat(resultSum_after).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "0 [0]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
        }

        pjsip.Pj_Answer_Call(0, false);

        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP??????]");
        List<Record> resultSum_after_end = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        if (message.equals("FXS")) {
            softAssertPlus.assertThat(resultSum_after_end).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "0 [0]", "Talking", RECORD_DETAILS.INTERNAL.getAlias()));

        } else {
            softAssertPlus.assertThat(resultSum_after_end).extracting("caller", "callee", "status", "details")
                    .contains(tuple("2000 [2000]".replace("2000", vcpCaller + ""), "0 [0]", "Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        }
        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }
}
