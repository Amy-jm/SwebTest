package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.Record;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIObject.InboundRouteObject;
import com.yeastar.untils.APIObject.RingGroupObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.groups.Tuple.tuple;
@Log4j2
public class TestOperatorRingGroup_2 extends TestCaseBaseNew {
    private APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = true;
    private boolean lhrFlag = !runRecoveryEnvFlag;

    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateFxs = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTQxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    private final String queueListName = "QU";
    private final String ringGroupName = "RG";//6300
    private final String conferenceName = "CO";
    private final String ivrName = "IR";
    private final String queueListName2 = "QE";
    private final String ringGroupName2 = "RI";//6300
    private final String conferenceName2 = "CF";
    private final String ivrName2 = "IV";

    private final ArrayList<String> queueMembers = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers = new ArrayList<>();
    private final ArrayList<String> conferenceMember = new ArrayList<>();
    private final ArrayList<String> queueMembers2 = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers2 = new ArrayList<>();
    private final ArrayList<String> conferenceMember2 = new ArrayList<>();

    private final String op_talking = UI_MAP.getString("web_client.talking").trim();
    private final String op_ringing = UI_MAP.getString("web_client.ringing").trim();

    Object[][] routes = new Object[][] {
            {"99",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SPS"},//sps   ?????? ??????
            {"88",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"BRI"},//BRI   ?????? ??????
            {""  ,2000,"2005",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"FXO"},//FXO --77 ??????   2005???FXS???
            {""  ,3001,"3000",DEVICE_ASSIST_1,"3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SIP_REGISTER"},
            {"66",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"E1"},//E1     ?????? ??????
            {"44",4000,"6300",DEVICE_ASSIST_3,"4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SIP_ACCOUNT"}//SIP  --55 REGISTER
    };

    /**
     * ?????????????????????
     * routePrefix?????????????????? + caller???????????? + callee???????????? + device_assist????????????????????????ip??? + vcpCaller???VCP????????????????????????????????? + vcpDetail???VCP????????????Detail????????? + testRouteTypeMessage??????????????????
     * @return
     */
    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c) {
        Object[][] group = null;
        for (String groups : c.getIncludedGroups()) {
            for (int i = 0; i < routes.length; i++) {
                for (int j = 0; j < routes[i].length; j++) {
                    if (groups.equalsIgnoreCase("SPS")) {
                        group = new Object[][] {{"99",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SPS"}};
                    }else if (groups.equalsIgnoreCase("BRI")) {
                        group = new Object[][] {{"88",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"BRI"}};
                    }else if (groups.equalsIgnoreCase("FXO")) {
                        group = new Object[][] {{""  ,2000,"2005",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"FXO"}};
                    }else if (groups.equalsIgnoreCase("E1")) {
                        group = new Object[][] {{"66",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"E1"}};
                    }else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                        group = new Object[][] {{""  ,3001,"3000",DEVICE_ASSIST_1,"3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_REGISTER"}};
                    }else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                        group = new Object[][] {{"44",4000,"6300",DEVICE_ASSIST_3,"4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_ACCOUNT"}};
                    }else {
                        group =routes;//??????????????????????????????????????????
                    }
                }
            }
        }
        //jenkins  run with xml and ITestContext c will be null
        if(group ==null){
            group =routes; //default run all routes
        }
        return group;
    }

    /**
     * ??????????????????
     */
    private boolean registerAllExtension(  ){
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(4000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(4000,DEVICE_ASSIST_3);

        boolean reg=true;
        if(getExtensionStatus(1000, IDLE, 5) != IDLE) {
            reg = false;
            log.debug("1000????????????");
        }
        if(getExtensionStatus(1001, IDLE, 5) != IDLE) {
            reg = false;
            log.debug("1001????????????");
        }
        if(getExtensionStatus(1002, IDLE, 5) != IDLE) {
            reg = false;
            log.debug("1002????????????");
        }
        if(getExtensionStatus(1003, IDLE, 5) != IDLE) {
            reg = false;
            log.debug("1003????????????");
        }
        if(getExtensionStatus(1004, IDLE, 5) != IDLE){
            reg=false;
            log.debug("1004????????????");
        }
        if(getExtensionStatus(1005, IDLE, 5) != IDLE){
            reg=false;
            log.debug("1005????????????");
        }
        if(getExtensionStatus(2000, IDLE, 5) != IDLE){
            reg=false;
            log.debug("2000????????????");
        }
        return !reg;
    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Test(groups = {"P0","VCP","prerequisite","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},priority =0 )
    public void prerequisite() {

        ringGroupMembers.clear();
        ringGroupMembers2.clear();
        queueMembers2.clear();
        queueMembers.clear();
        conferenceMember.clear();
        conferenceMember2.clear();

        ringGroupMembers.add("1001");
        ringGroupMembers.add("1002");
        ringGroupMembers.add("1003");
        ringGroupMembers2.add("1004");
        ringGroupMembers2.add("1005");
        queueMembers.add("1001");
        queueMembers.add("1002");
        queueMembers.add("1003");
        queueMembers2.add("1004");
        queueMembers2.add("1005");
        conferenceMember.add("1001");
        conferenceMember.add("1002");
        conferenceMember.add("1003");
        conferenceMember2.add("1004");
        conferenceMember2.add("1005");

        if(lhrFlag && !runRecoveryEnvFlag){
            //registerAllExtension();
            lhrFlag = false;
        }

        if (runRecoveryEnvFlag){
            List<String> trunks = new ArrayList<>();
            if(!SPS.toLowerCase().endsWith("null"))trunks.add(SPS);
            if(!BRI_1.toLowerCase().endsWith("null"))trunks.add(BRI_1);
            if(!FXO_1.toLowerCase().endsWith("null"))trunks.add(FXO_1);
            if(!E1.toLowerCase().endsWith("null"))trunks.add(E1);
            if(!SIPTrunk.toLowerCase().endsWith("null"))trunks.add(SIPTrunk);
            if(!ACCOUNTTRUNK.toLowerCase().endsWith("null"))trunks.add(ACCOUNTTRUNK);

            List<String> extensionNum = new ArrayList<>();
            List<String> emptyList = new ArrayList<>();

            step("???????????????");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");

            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");
            step("????????????1000-1005");
            apiUtil.deleteAllExtension();
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1000").replace("EXTENSIONLASTNAME","A").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1001").replace("EXTENSIONLASTNAME","B").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1002").replace("EXTENSIONLASTNAME","C").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1003").replace("EXTENSIONLASTNAME","D").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1004").replace("EXTENSIONLASTNAME","E").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1005").replace("EXTENSIONLASTNAME","F").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","0").replace("EXTENSIONLASTNAME","").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1").replace("EXTENSIONLASTNAME","").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateFxs.replace("EXTENSIONNUM","1020").replace("EXTENSIONLASTNAME","FXS").replace("FXSPORT",FXS_1).replace("GROUPLIST",groupList));


            step("??????SPS??????");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("??????????????????InRoute1,??????????????????1000");
            apiUtil.deleteAllInbound().createInbound("InRoute1",trunks,"Extension","1000");

            step("??????????????????");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1",trunks,extensionNum);

            step("???????????????6300");

            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName,"6300",ringGroupMembers);

            step("???????????????6301");
            apiUtil.createRingGroup(ringGroupName2,"6301",ringGroupMembers2);

            step("????????????6400");
            apiUtil.deleteAllQueue().createQueue(queueListName,"6400",null, queueMembers,null);

            step("????????????6401");
            apiUtil.createQueue(queueListName2,"6401",null, queueMembers2,null);

            step("???????????????");
            apiUtil.deleteAllConference().createConference(conferenceName, "6500", conferenceMember);

            step("???????????????");
            apiUtil.createConference(conferenceName2, "6501", conferenceMember2);

            step("??????IVR 6200,6201");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0,"extension","","1000",0));
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_1 = new ArrayList<>();
            pressKeyObjects_1.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0,"extension","","1001",0));
            apiUtil.deleteAllIVR().createIVR("6200",ivrName,pressKeyObjects_0)
                    .createIVR("6201",ivrName2,pressKeyObjects_1).editIVR("6201","\"dial_ext_option\":\"all\"");

            step("??????????????????");
            extensionNum.add("0");
            extensionNum.add("1");
            apiUtil.deleteNetworkDrive().setNetworkDriver().updateAutoRecord(emptyList,extensionNum,emptyList,emptyList);

            List<InboundRouteObject> inboundObjectList = apiUtil.getInboundSummary();

            for(int i=0; i<inboundObjectList.size(); i++){
                InboundRouteObject object = inboundObjectList.get(i);
                if(object.name.equals("InRoute1")){
                    RingGroupObject rg = apiUtil.getRingGroupSummary("6300");
                    apiUtil.editInbound(String.format("{\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\",\"id\":%s}",rg.id,object.id));
                }
            }

            apiUtil.apply();

            apiUtil.loginWebClient("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            apiUtil.loginWebClient("1",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            runRecoveryEnvFlag = registerAllExtension();
        }
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? -->???????????????talking\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingTalkingStatus","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingStatus(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_Answer_Call(1001,200,false);
        assertStep("3.??????????????????");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001?????? -->???????????????PickUp???Redirect???unpark\n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingTalkingRightClickNotDisplay","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickNotDisplay(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("3.??????????????????Pick Up???Redirect");
        List rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001");
        softAssertPlus.assertThat(rightEventList).doesNotContain(OperatorPanelPage.RIGHT_EVENT.PICK_UP, OperatorPanelPage.RIGHT_EVENT.REDIRECT, OperatorPanelPage.RIGHT_EVENT.RETRIEVE).as("???????????????Pick up,Redirect,Retrieve");

        step("4.??????park???????????????unpark");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(3000);
        rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000");

        softAssertPlus.assertThat(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.RETRIEVE.getAlias())).isEqualTo(true).as("???????????????Retrieve");

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.????????????" +
            "4.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingTalkingRightClickHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.????????????");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.HANG_UP,"2000");
        sleep(5000);

        //todo cdr??????
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.????????????Listen" +
            "4.????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "6.??????VCP????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","Operator Panel","testRinggroupIncomingTalkingRightClickListen","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickListen(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Listen");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.LISTEN,"2000");
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("4.???????????????0???????????????Call Monitor?????????0??????Talking??????");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("??????0???????????????Call Monitor").isEqualTo("Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("????????????0?????????").isEqualTo(TALKING);

        assertStep("5.?????????Inbound&Internal Call???????????????????????????");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call???????????????????????????");

        assertStep("6.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertAll();

        //todo cdr??????
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.???????????? WHISPER" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickWhisper","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickWhisper(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Whisper");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.WHISPER,"1001");
        sleep(3000);

        assertStep("4.???????????????0???????????????Call Monitor?????????0??????Talking??????");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("??????0???????????????Call Monitor").isEqualTo("Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("????????????0?????????").isEqualTo(TALKING);

        assertStep("5.?????????Inbound&Internal Call???????????????????????????");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call???????????????????????????");

        assertStep("6.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Barge" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickWhisper","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickBarge(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Barge");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.Barge_IN,"1001");
        sleep(3000);

        assertStep("4.???????????????0???????????????Call Monitor?????????0??????Talking??????");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("??????0???????????????Call Monitor").isEqualTo("Call Monitor");
        softAssertPlus.assertThat(getExtensionStatus(0, RING, 8)).as("????????????0?????????").isEqualTo(RING);

        assertStep("5.?????????Inbound&Internal Call???????????????????????????");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call???????????????????????????");

        assertStep("6.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertAll();
        //todo cdr??????

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickPark","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickPark(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        clearasteriskLog();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Park");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");

        assertStep("4.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        assertStep("5.Asterisk???????????????1001??????????????????call-parked-at.slin???????????????");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli????????????????????????").contains("call-parked-at");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 8)).as("????????????1000?????????").isEqualTo(HUNGUP);

        sleep(5000);

        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("[cli????????????????????????]").contains("call-parked-at");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 8)).as("????????????1000?????????").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickPark","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickParkToUnPark(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Park->????????????Retrieve?????????0??????");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(WaitUntils.RETRY_WAIT);
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");

        softAssertPlus.assertThat(getExtensionStatus(0, RING, 8)).as("????????????0??????").isEqualTo(RING);
        pjsip.Pj_Answer_Call(0,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("4.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"0 [0]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickPark","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickRecord(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        clearasteriskLog();

        step("3.??????Unrecording");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.PAUSE_RECORD,"");

        assertStep("4.[Asterisk??????]???cli??????????????????");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli??????????????????").contains("PAUSE MIXMON");

        clearasteriskLog();
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.Resume_RECORD,"");

        assertStep("4.[Asterisk??????]???cli??????????????????");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli??????????????????").contains("UNPAUSE MIXMON");

        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToRingGroup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToRingGroup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer????????????6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6301");

        assertStep("1004 1005??????");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("???????????????6301?????????1004??????").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 8)).as("???????????????6301?????????1005??????").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName2+":"+vcpCaller,"1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        assertStep("1004 Talking");
        pjsip.Pj_Answer_Call(1004,200,false);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToQueue","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToQueue(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer?????????6401");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6401");

        assertStep("1004 1005??????");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("????????????1004??????").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 8)).as("????????????1005??????").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName2+":"+vcpCaller,"1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));

        assertStep("1004 1005Talking");
        pjsip.Pj_Answer_Call(1004,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3?????????1004voicemail" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToVoicemail","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToVoicemail(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer???Voicemail");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        assertStep("1004??????");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("????????????1004??????").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("???????????????1004 ???????????????Voicemail");
        pjsip.Pj_Answer_Call(1004,404,false);
        sleep(WaitUntils.TALKING_WAIT*4);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 8)).as("????????????1004??????????????????Voicemail ").isEqualTo(HUNGUP);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToIVR","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToIVR(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer???IVR 6201");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6201");

        assertStep("4.???????????????IVR");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,ivrName2+" [6201]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        step("5.IVR ??????1001");
        pjsip.Pj_Send_Dtmf(2000,"1004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("????????????1004??????").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004,200,false);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("????????????1004??????").isEqualTo(TALKING);

        assertStep("6.[??????] ???????????????External??????IVR?????????");
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName2+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("?????????VCP?????????????????????")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToParking","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToParking(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer???????????????6000");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6000");

        assertStep("??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"6000",op_talking, OperatorPanelPage.RECORD_DETAILS.INTERNAL_PARKED.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n" +
            "3.??????Park" +
            "4.???????????????0???????????????Call Monitor?????????0??????Talking??????" +
            "5.?????????Inbound&Internal Call???????????????????????????" +
            "6.??????CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferToConference","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferToConference(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer????????????6501");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6501");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,conferenceName2+" [6501]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        //todo cdr??????
        softAssertPlus.assertAll();;

    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? -->?????????????????????C -->????????????????????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferInternalAHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer???????????????1001");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("1004??????->??????");
        pjsip.Pj_Answer_Call(1004,200,false);

        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("????????????1004??????").isEqualTo(TALKING);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(2000);
        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup-->??????1001?????? -->?????????????????????C -->???????????????C????????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingRightClickTransferInternalCHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.??????Transfer???????????????1004");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("1004??????->??????");
        pjsip.Pj_Answer_Call(1004,200,false);

        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("????????????1004??????").isEqualTo(TALKING);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(1001);
        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->???????????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingCallerHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingCallerHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.????????????,????????????????????????");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("????????????,????????????????????????").isEqualTo(0);

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Extension ???????????????1000-->???????????? \n" +
            "1:??????0,login web client\n" +
            "2:????????????[2000]??????Ringgroup 1001??????\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testRinggroupIncomingTalkingCalleeHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM"},dataProvider = "routes")
    public void testRinggroupIncomingTalkingCalleeHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:????????????[2000]??????Ringgroup 1001??????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("3.??????????????????");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":2000 [2000]","1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step("4.????????????,????????????????????????");
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).isEqualTo(0).as("????????????,????????????????????????");

        //todo cdr??????
        softAssertPlus.assertAll();;

    }

    //======================================================Drag and Drop ?????? ==========================================//
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop \n" +
            "1:??????0,login web client\n" +
            "2:????????????[1002]-->[1001]??????\n+" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropWithCTalking","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropWithCTalking(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        refresh();

        step("4:???1002 ???1000 ????????????1000 ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(1002,"1000",DEVICE_IP_LAN,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("5:???2000 ?????? 1001??????1001?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6???[Inbound]1001 -->?????????[Extension]1000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1000,486,false);
        sleep(WaitUntils.SHORT_WAIT*4);
        refresh();

        assertStep("4:VCP ????????????????????? A--C Talking external,voicemail");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???idle???\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->????????????\n+" +
            "3:[2000 ?????? 1001]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropWithCIdle","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropWithCIdle(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:???2000 ?????? Queue??????1001 ??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);

        step("6???[Inbound]1001 -->?????????[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:???????????? A--C ring");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1000 A [1000]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:???????????? A--C talking");
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???????????????\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "39????????????????????????????????????")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropWithCUnregistered","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropWithCUnregistered(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        //pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        //pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        //pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:???2000 ?????? Queue??????1001 ?????????Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("6???[Inbound]1001 -->?????????[Extension]1000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(WaitUntils.TALKING_WAIT*7);

        assertStep("7:[VCP]???????????? 2000--1000 voicemail ");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Ring Group 6300\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1000 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropRingGroup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropRingGroup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000 ?????????Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1001 -->?????????[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.RINGGROUP,"6301");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP??????]");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(resultSum.size()).as("??????RingGroup??????").isEqualTo(ringGroupMembers2.size());

        assertStep("7:????????????1004 ??????,????????????");
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Queue 6401\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Ring??????\n" +
            "4:[Inbound]1000 -->?????????[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropQueue","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropQueue(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->?????????[???Queue]6401");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.QUEUE,"6401");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP??????]");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":"+vcpCaller,"1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName2+":"+vcpCaller,"1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));

        softAssertPlus.assertThat(resultSum.size()).as("??????RingGroup??????").isEqualTo(queueMembers2.size());

        assertStep("7:????????????1004 ??????,????????????");
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":"+vcpCaller,"1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Park 6000\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Talking??????\n" +
            "4:[Inbound]1000 -->?????????[Park]6000" +
            "5:???park????????????D??????6000??????park???D?????????D???")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropParking","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropParking(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->?????????[Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP??????]");

        pjsip.Pj_Make_Call_No_Answer(1001,"6000",DEVICE_IP_LAN);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Park 6000\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Talking??????\n" +
            "4:[Inbound]1000 -->?????????[Park]6000" +
            "5:???park?????????park??????,???????????????????????????Transfer")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropParkingRightClickTransfer","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropParkingRightClickTransfer(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1001 -->?????????[???Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP??????]");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Park 6000\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Talking??????\n" +
            "4:[Inbound]1000 -->?????????[Park]6000" +
            "5:???park?????????park??????,???????????????????????????UnPark")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropParkingRightClickUnpark","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropParkingRightClickUnpark(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:[2000 ?????? Queue]???1001?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1001 -->?????????[???Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP??????]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");
        sleep(WaitUntils.RETRY_WAIT);
        pjsip.Pj_Answer_Call(0,false);
        sleep(WaitUntils.RETRY_WAIT);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"0 [0]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Park 6000\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Talking??????\n" +
            "4:[Inbound]1000 -->?????????[Park]6000" +
            "5:???park?????????park??????,???????????????????????????Hangup")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropParkingRightClickHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropParkingRightClickHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->?????????[???Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP??????]");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP??????]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.HANG_UP,"");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("????????????????????????").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("????????????A ?????????")
    @Description("????????????2000 ?????????--> Ringgroup -->??????1001 ?????? -->DragAndDrop ???Park 6000\n" +
            "1:??????0,login web client\n" +
            "2:[1001(idle)]-->?????????\n+" +
            "3:[2000 ?????? 1000]???1001 ???Talking??????\n" +
            "4:[Inbound]1000 -->?????????[Park]6000" +
            "5:???park?????????park??????,?????????????????????????????????")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRinggroupIncomingDragAndDropParkingHangup","Regression","PSeries","VCP2","VCP_RingGroup_2",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT","GSM","VCP2"},dataProvider = "routes")
    public void testRinggroupIncomingDragAndDropParkingHangup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message) {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:??????Operator panel ??????");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP??????] ?????????????????????");
        //pjsip.Pj_Init();
        //registerAllExtension();

        step("5:???2000 ?????? 1000??????1000?????? ???Talking??????");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6???[Inbound]1000 -->?????????[???Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP??????]");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName+":"+vcpCaller,"[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP??????]");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("????????????????????????").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }
}
