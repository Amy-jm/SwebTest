package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.*;
import com.yeastar.page.pseries.OperatorPanel.Record;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.groups.Tuple.tuple;


public class TestOperatorIVR_2 extends TestCaseBase {
    private APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = true;

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

    private void registerAllExtension(){
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->前置条件")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Test(groups = {"P0","VCP","prerequisite","Regression","PSeries"},priority =0 )
    public void prerequisite() {

        ringGroupMembers.clear();
        ringGroupMembers.add("1001");
        ringGroupMembers.add("1002");
        ringGroupMembers.add("1003");

        ringGroupMembers2.clear();
        ringGroupMembers2.add("1004");
        ringGroupMembers2.add("1005");

        queueMembers.clear();
        queueMembers.add("1001");
        queueMembers.add("1002");
        queueMembers.add("1003");

        queueMembers2.clear();
        queueMembers2.add("1004");
        queueMembers2.add("1005");

        conferenceMember.clear();
        conferenceMember.add("1001");
        conferenceMember.add("1002");
        conferenceMember.add("1003");

        conferenceMember2.clear();
        conferenceMember2.add("1004");
        conferenceMember2.add("1005");

        if (runRecoveryEnvFlag){
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            List<String> emptyList = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");

            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");
            extensionNum.add("1020");
            step("创建SIP分机1000-1005,FXS:1020");
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

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("创建响铃组6300");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName,"6300",ringGroupMembers);

            step("创建响铃组6301");
            apiUtil.createRingGroup(ringGroupName2,"6301",ringGroupMembers2);

            step("创建队列6400");
            apiUtil.deleteAllQueue().createQueue(queueListName,"6400",null, queueMembers,null);

            step("创建队列6401");
            apiUtil.createQueue(queueListName2,"6401",null, queueMembers2,null);

            step("创建会议室");
            apiUtil.deleteAllConference().createConference(conferenceName, "6500", conferenceMember);

            step("创建会议室");
            apiUtil.createConference(conferenceName2, "6501", conferenceMember2);

            step("创建IVR 6200,6201");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0,"extension","","1001",0));
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_1 = new ArrayList<>();
            pressKeyObjects_1.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0,"extension","","1004",0));

            apiUtil.deleteAllIVR().createIVR("6200",ivrName,pressKeyObjects_0)
                    .createIVR("6201",ivrName2,pressKeyObjects_1).editIVR("6201","\"dial_ext_option\":\"all\"");

            step("创建呼入路由InRoute1,目的地到分机1000");
            apiUtil.deleteAllInbound().createInbound("InRoute1", trunks, "IVR", "6200");

            step("创建呼出路由");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1",trunks,extensionNum);

            step("设置网络磁盘");
            extensionNum.add("0");
            extensionNum.add("1");
            apiUtil.deleteNetworkDrive().setNetworkDriver().updateAutoRecord(emptyList,extensionNum,emptyList,emptyList);

            apiUtil.apply();

            apiUtil.loginWebClient("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            apiUtil.loginWebClient("1",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            runRecoveryEnvFlag = false;
        }

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 -->呼入状态：talking\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingTalkingStatus","Regression","PSeries"})
    public void testIVRIncomingTalkingStatus() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        assertStep("3.断言页面元素");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001接听 -->右击不显示PickUp、Redirect、unpark\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingTalkingRightClickNotDisplay","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickNotDisplay() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
       
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("3.右击后不显示Pick Up、Redirect");
        List rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001");
        softAssertPlus.assertThat(rightEventList).doesNotContain(OperatorPanelPage.RIGHT_EVENT.PICK_UP, OperatorPanelPage.RIGHT_EVENT.REDIRECT, OperatorPanelPage.RIGHT_EVENT.RETRIEVE).as("右击不显示Pick up,Redirect,Retrieve");

        step("4.点击park后右击显示unpark");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(3000);
        rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000");

        softAssertPlus.assertThat(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.RETRIEVE.getAlias())).isEqualTo(true).as("右击有显示Retrieve");

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击挂断" +
            "4.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingTalkingRightClickHangup","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickHangup() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
       
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.HANG_UP,"2000");
        sleep(5000);

        //todo cdr校验
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击点击Listen" +
            "4.断言分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "6.断言VCP页面元素")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","Operator Panel","testIVRIncomingTalkingRightClickListen","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickListen() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Listen");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.LISTEN,"2000");
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").isEqualTo("Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("预期分机0通话中").isEqualTo(TALKING);

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, RECORD_DETAILS.EXTERNAL_IVR.getAlias()));
        softAssertPlus.assertAll();

        //todo cdr校验
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击点击 WHISPER" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("来显预期Call Monitor实际只有Monitor")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickWhisper","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickWhisper() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Whisper");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.WHISPER,"1001");
        sleep(3000);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").isEqualTo("Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("预期分机0通话中").isEqualTo(TALKING);

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Barge" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickWhisper","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickBarge() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Barge");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.Barge_IN,"1001");
        sleep(3000);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").isEqualTo("Call Monitor");
        softAssertPlus.assertThat(getExtensionStatus(0, RING, 8)).as("预期分机0通话中").isEqualTo(RING);

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));
        softAssertPlus.assertAll();
        //todo cdr校验

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickPark() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        clearasteriskLog();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Park");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");

        assertStep("4.断言页面元素");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        assertStep("5.Asterisk断言：分机1001听到停泊语音call-parked-at.slin，然后挂断");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli确认有停泊提示音").contains("call-parked-at");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 8)).as("预期分机1000已挂断").isEqualTo(HUNGUP);

        sleep(5000);

        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("[cli确认有停泊提示音]").contains("call-parked-at");
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 8)).as("预期分机1000已挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickParkToUnPark() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Park->右键点击Retrieve，分机0接听");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(WaitUntils.RETRY_WAIT);
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");

        softAssertPlus.assertThat(getExtensionStatus(0, RING, 8)).as("预期分机0响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(0,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("4.断言页面元素");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","0 [0]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickRecord() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        clearasteriskLog();

        step("3.右击Unrecording");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.PAUSE_RECORD,"");

        assertStep("4.[Asterisk断言]：cli打印停止录音");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli打印停止录音").contains("PAUSE MIXMON");

        clearasteriskLog();
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.Resume_RECORD,"");

        assertStep("4.[Asterisk断言]：cli打印停止录音");
        softAssertPlus.assertThat(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG)).as("cli打印停止录音").contains("UNPAUSE MIXMON");

        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToRingGroup","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToRingGroup() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到响铃组6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6301");

        assertStep("1004 1005响铃");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("预期响铃组6301的分机1004响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 8)).as("预期响铃组6301的分机1005响铃").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName2+":2000 [2000]","1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        assertStep("1004 Talking");
        pjsip.Pj_Answer_Call(1004,200,false);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToQueue","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToQueue() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到队列6401");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6401");

        assertStep("1004 1005响铃");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("预期分机1004响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1005, RING, 8)).as("预期分机1005响铃").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName2+":2000 [2000]","1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));

        assertStep("1004 Talking");
        pjsip.Pj_Answer_Call(1004,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":2000 [2000]","1004 E [1004]",op_talking, RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3：进入1004voicemail" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToVoicemail","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToVoicemail() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到Voicemail");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        assertStep("1004响铃");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("预期分机1004响铃").isEqualTo(RING);

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("预期响分机1004 挂断，进入Voicemail");
        pjsip.Pj_Answer_Call(1004,404,false);
        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 8)).as("预期分机1004已挂断，进入Voicemail ").isEqualTo(HUNGUP);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToIVR","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToIVR() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到IVR 6201");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6201");

        assertStep("4.界面显示到IVR");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]",ivrName2+" [6201]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        step("5.IVR 呼叫1001");
        pjsip.Pj_Send_Dtmf(2000,"1004");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 8)).as("预期分机1004响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004,200,false);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("预期分机1004响铃").isEqualTo(TALKING);

        assertStep("6.[判断] 界面仅显示External，无IVR相关的");
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName2+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("停泊后VCP控制面吧无记录")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToParking","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToParking() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到停泊号码6000");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6000");

        assertStep("控制面板显示");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":2000 [2000]","6000",op_talking, OperatorPanelPage.RECORD_DETAILS.INTERNAL_PARKED.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferToConference","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferToConference() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到会议室6501");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6501");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]",conferenceName2+" [6501]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        //todo cdr校验
        softAssertPlus.assertAll();;

    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 -->转移到内部号码C -->外线号码先挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferInternalAHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到内部分机1001");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("1004响铃->接听");
        pjsip.Pj_Answer_Call(1004,200,false);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("预期分机1004接听").isEqualTo(TALKING);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(2000);
        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR-->分机1001接听 -->转移到内部号码C -->被转移号码C先挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries"})
    public void testIVRIncomingTalkingRightClickTransferInternalCHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.右击Transfer到内部分机1004");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("1004响铃->接听");
        pjsip.Pj_Answer_Call(1004,200,false);

        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 8)).as("预期分机1004接听").isEqualTo(TALKING);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(1001);
        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->主叫挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingCallerHangup","Regression","PSeries"})
    public void testIVRIncomingTalkingCallerHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("3.主叫挂断,控制面板没有记录");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("主叫挂断,控制面板没有记录").isEqualTo(0);

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->被叫挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫IVR 1001接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testIVRIncomingTalkingCalleeHangup","Regression","PSeries"})
    public void testIVRIncomingTalkingCalleeHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2:外线号码[2000]呼叫IVR 1001接听");
        pjsip.Pj_Init();
        registerAllExtension();

        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        assertStep("3.判断控制面板");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        step("4.被叫挂断,控制面板没有记录");
        pjsip.Pj_hangupCall(1001);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).isEqualTo(0).as("被叫挂断,控制面板没有记录");

        //todo cdr校验
        softAssertPlus.assertAll();;

    }

    //======================================================Drag and Drop 拖拽 ==========================================//
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop \n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1002]-->[1001]通话\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropWithCTalking","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropWithCTalking(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        refresh();

        step("4:【1002 与1000 通话】，1000 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(1002,"1000",DEVICE_IP_LAN,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1000,486,false);
        sleep(WaitUntils.SHORT_WAIT*4);
        refresh();

        assertStep("4:VCP 第一条显示状态 A--C Talking external,voicemail");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop （idle）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->空闲状态\n+" +
            "3:[2000 呼叫 1001]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropWithCIdle","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropWithCIdle(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 Queue】，1001 接听");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);

        step("6：[Inbound]1001 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:显示状态 A--C ring");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1000 A [1000]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:显示状态 A--C talking");
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop （未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "进入语音留言后页面不显示")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropWithCUnregistered","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropWithCUnregistered(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("勾选显示未注册分机");
        auto.homePage().intoPage(HomePage.Menu_Level_1.preferences);
        auto.preferencesPage().isChoice(auto.preferencesPage().preference_account_show_unregistered_extensions,true).clickSave();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 Queue】，1001 接听为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.TALKING_WAIT);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);

        refresh();
        step("6：[Inbound]1001 -->拖动到[Extension]1000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.EXTENSION,"1000");
        sleep(12000);

        assertStep("7:[VCP]显示状态 2000--1000 voicemail ");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1000 A [1000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();;
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Ring Group 6300\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropRingGroup","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropRingGroup(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000 接听为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.RINGGROUP,"6301");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":2000 [2000]","1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(resultSum.size()).as("验证RingGroup数量").isEqualTo(ringGroupMembers2.size());

        assertStep("7:显示状态1004 接通,验证界面");
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName2+":2000 [2000]","1004 E [1004]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Queue 6401\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropQueue","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropQueue(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6401");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.QUEUE,"6401");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":2000 [2000]","1004 E [1004]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName2+":2000 [2000]","1005 F [1005]",op_ringing, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));

        softAssertPlus.assertThat(resultSum.size()).as("验证RingGroup数量").isEqualTo(queueMembers2.size());

        assertStep("7:显示状态1004 接通,验证界面");
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(queueListName2+":2000 [2000]","1004 E [1004]",op_talking, RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，分机D拨打6000取回park，D接起后D挂")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropParking","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropParking(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        pjsip.Pj_Make_Call_No_Answer(1001,"6000",DEVICE_IP_LAN);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键Transfer")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropParkingRightClickTransfer","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropParkingRightClickTransfer(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.TALKING_WAIT);
        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","1001 B [1001]",op_talking, RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键UnPark")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropParkingRightClickUnpark","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropParkingRightClickUnpark(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:[2000 呼叫 Queue]，1001接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP验证]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");
        sleep(WaitUntils.RETRY_WAIT);

        pjsip.Pj_Answer_Call(0,false);
        sleep(WaitUntils.TALKING_WAIT);

        resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","0 [0]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键Hangup")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropParkingRightClickHangup","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropParkingRightClickHangup(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP验证]");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP验证]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000", OperatorPanelPage.RIGHT_EVENT.HANG_UP,"");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> IVR -->分机1001 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时主叫挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIVRIncomingDragAndDropParkingHangup","Regression","PSeries","VCP2"})
    public void testIVRIncomingDragAndDropParkingHangup(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        registerAllExtension();

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996200",DEVICE_ASSIST_2,false);
        pjsip.Pj_Send_Dtmf(2000,"0");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001", OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP验证]");
        List<Record> resultSum = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum).extracting("caller","callee","status","details")
                .contains(tuple(ivrName+":2000 [2000]","[6000]",op_talking, OperatorPanelPage.RECORD_DETAILS.EXTERNAL_PARKED.getAlias()));
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("[VCP验证]");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }
}
