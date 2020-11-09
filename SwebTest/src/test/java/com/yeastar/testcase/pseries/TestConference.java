package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test conference
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestConference extends TestCaseBaseNew {
    private boolean isRunRecoveryEnvFlag = true;
    APIUtil apiUtil = new APIUtil();
    private String MEETME_LIST_6501 = "meetme list 6501";
    private String CONF_INVALIDPIN_GSM = "conf-invalidpin.gsm";
    private String CONF_GETPIN_GSM = "conf-getpin.gsm";
    private String CONF_WAITFORLEADER_GSM = "conf-waitforleader.gsm";
    private String CONF_PLACEINTOCONF_GSM = "conf-placeintoconf.gsm";
    String  CDR_CONFERENCE_6501 ="Conference Conference1<6501>";//6201

    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateExtensionWithRoleAdmin = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":1,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private boolean registerAllExtension(){
        step("===========[Extension]  create & register extension  start =========");
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1020,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(4000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1020,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(4000,DEVICE_ASSIST_3);

        boolean reg=false;
        if(getExtensionStatus(1000, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1000注册失败");
        }
        if(getExtensionStatus(1001, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1001注册失败");
        }
        if(getExtensionStatus(1002, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1002注册失败");
        }
        if(getExtensionStatus(1003, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1003注册失败");
        }
        if(getExtensionStatus(2001, IDLE, 5) != IDLE){
            reg=true;
            log.debug("2001注册失败");
        }
        if(getExtensionStatus(4000, IDLE, 5) != IDLE){
            reg=true;
            log.debug("4000注册失败");
        }
        step("===========[Extension]  create & register extension  end =========");
        return reg;
    }
    private boolean isDebugInitExtensionFlag = true;

    public void prerequisite(boolean isRestConference6501ToDefault) {
        //local debug
        long startTime=System.currentTimeMillis();
        if(isDebugInitExtensionFlag){
            registerAllExtension();
            isRunRecoveryEnvFlag = false;
        }

        if (isRunRecoveryEnvFlag) {
            step("=========== init before class  start =========");

            step("1.创建分机");
            apiUtil.deleteAllExtension().apply();
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleAdmin.replace("EXTENSIONFIRSTNAME","0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
//                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME","FXS").replace("EXTENSIONLASTNAME", "FXS").replace("FXSPORT","1-4").replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME","1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT","1-3").replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("2.创建分机组 ExGroup1/ExGroup2");
            List<String> extensionExGroup1 = new ArrayList<>();
            List<String> extensionExGroup2 = new ArrayList<>();
            extensionExGroup1.add("1000");
            extensionExGroup1.add("1001");

            extensionExGroup2.add("1002");
            extensionExGroup2.add("1003");

            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
                    createExtensionGroup("ExGroup1",extensionExGroup1).
                    createExtensionGroup("ExGroup2",extensionExGroup2).apply();

            step("3.创建队列");
            ArrayList<String> queueStaticListNum_0 = new ArrayList<>();
            ArrayList<String> queueDynamicListNum_0 = new ArrayList<>();
            ArrayList<String> ringGroupNum_0 = new ArrayList<>();
            queueStaticListNum_0.add("1000");
            queueStaticListNum_0.add("1001");

            queueDynamicListNum_0.add("1003");
            queueDynamicListNum_0.add("1004");
            apiUtil.deleteAllQueue().createQueue("Queue0", "6400", 60,queueDynamicListNum_0, queueStaticListNum_0, null,"extension","1000","0","extension","1001");


            step("4.创建响铃组6300");
            ringGroupNum_0.add("ExGroup1");
            ringGroupNum_0.add("1003");
            apiUtil.deleteAllRingGroup().createRingGroup("RingGroup0", "6300", ringGroupNum_0,10,"extension","","1000");

            step("5.创建IVR IVR0_6200");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));

            apiUtil.deleteAllIVR().createIVR("6200","IVR0_6200",pressKeyObjects_0);

            List<String> trunk1 = new ArrayList<>();
            List<String> trunk2 = new ArrayList<>();
            List<String> trunk3 = new ArrayList<>();
            List<String> trunk4 = new ArrayList<>();
            List<String> trunk5 = new ArrayList<>();
            List<String> trunk6 = new ArrayList<>();
            List<String> trunk7 = new ArrayList<>();
            List<String> trunk8 = new ArrayList<>();
            List<String> trunk9 = new ArrayList<>();

            List<String> extensionNum = new ArrayList<>();
            List<String> extensionNumA = new ArrayList<>();

            trunk1.add(SIPTrunk);
            trunk2.add(SPS);
            trunk3.add(ACCOUNTTRUNK);
            trunk4.add(FXO_1);
            trunk5.add(BRI_1);
            trunk6.add(E1);
            trunk7.add(GSM);
            trunk8.add(SPS);
            trunk9.add(SPS);
            trunk9.add(BRI_1);
            trunk9.add(FXO_1);
            trunk9.add(E1);
            trunk9.add(SIPTrunk);
            trunk9.add(ACCOUNTTRUNK);
            trunk9.add(GSM);

            extensionNum.add("0");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNumA.add("1000");

            step("6.创建Conference0");
            List<String> moderators = new ArrayList<>();
            moderators.add("1000");
            apiUtil.deleteAllConference().createConference("Conference0","6500",moderators)
                    .createConference("Conference1","6501",moderators);

            step("7.创建呼入路由InRoute1,目的地到Conference 6500");
            apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "Conference", "6501");

            step("8.创建呼出路由：Out1，Out2，Out3，Out4，Out5，Out6，Out7，Out8，Out9");
            apiUtil.deleteAllOutbound().createOutbound("Out1", trunk1, extensionNum,"1.",1).
                    createOutbound("Out2", trunk2, extensionNum,"2.",1).
                    createOutbound("Out3", trunk3, extensionNum,"3.",1).
                    createOutbound("Out4", trunk4, extensionNum,"4.",1).
                    createOutbound("Out5", trunk5, extensionNum,"5.",1).
                    createOutbound("Out6", trunk6, extensionNum,"6.",1).
                    createOutbound("Out7", trunk7, extensionNum,"7.",1).
                    createOutbound("Out8", trunk8, extensionNumA).
                    createOutbound("Out9", trunk9, extensionNum);

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            apiUtil.loginWebClient("1001", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            isRunRecoveryEnvFlag = registerAllExtension();
        }
        if(isRestConference6501ToDefault){
            restConference6501ToDefault();
        }

        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:"+(System.currentTimeMillis()-startTime)/1000+" Seconds");
    }

    /**
     * 重置ivr6201to default，只有按0到分机A
     */
    public void restConference6501ToDefault(){
        step("=========== rest conference 6501 to default start =========");
        List<String> moderators = new ArrayList<>();
        moderators.add("1000");
        apiUtil.deleteConference("6501").apply();
        apiUtil.createConference("Conference1","6501",moderators).apply();

        step("7.创建呼入路由InRoute1,目的地到Conference 6500");
        List<String> trunk9 = new ArrayList<>();
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
        apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "Conference", "6501").apply();
        step("=========== rest conference 6501 to default end =========");

    }

    //############### dataProvider #########################

    Object[][] routes = new Object[][]{

            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"},//sps   前缀 替换
            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "BRI"},//BRI   前缀 替换
            {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "FXO"},//FXO --77 不输   2005（FXS）
            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "E1"},//E1     前缀 替换
            {"",   3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_REGISTER"},//SIP  --55 REGISTER
            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_ACCOUNT"},
//            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
    };

    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        if(methodName.equals("AAAAA")){//SPS
            return  new Object[][]{{"99", 2000, "6201", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(),SPS, "SPS"}};
        }else{ //sip 呼入
            return  new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias(),SIPTrunk,"SIP_REGISTER"}};
        }
    }

    //############### asterisk 后台子进程 ####################
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();





    @Epic("P_Series")
    @Feature("Conference")
    @Story("DialExtension")
    @Description("1.通过sip外线呼入到Conference1-6501\n" +
            "\tasterisk -rx \"meetme list 6501\"\n" +
            "后台查看会议室6501存在一个成员\n" +
            "\t\t通过sps外线呼入到Conference1-6501\n" +
            "\t\t\t后台查看会议室6501多一个成员\n" +
            "\t\t\t\t分机1004呼入到6501\n" +
            "\t\t\t\t\t后台查看会议室6501多一个成员\n" +
            "\t\t\t\t\t\tsip外线主叫、sps外线主叫、分机分别挂断；查看挂断时会议室成员相应减少，cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P1", "Conference","Basic","Trunk","InboundRoute","testConference_1"}, dataProvider = "routes")
    public void testConference_01(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."));

        step("[SPS  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));


        step("[1004  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(1004, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."));

        step("[sip hangup] ");
        pjsip.Pj_hangupCall(caller);
        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        step("[sps hangup] ");
        pjsip.Pj_hangupCall(2000);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."));

        step("[1004 hangup] ");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(TALKING);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                 contains(tuple("3001<3001>", "Conference Conference1<6501>", "ANSWERED", "3001<3001> hung up", "sipRegister", "", "Internal")).
                 contains(tuple("2000<2000>", "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up", "SPS1", "", "Internal")).
                 contains(tuple("t estX<1004>", "Conference Conference1<6501>", "ANSWERED", "t estX<1004> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("DialExtension")
    @Description("2.通过SIP、SPS、Account、FXO、BRI、E1、GSM外线分别呼入到Conference1-6501\n" +
            "\tasterisk -rx \"meetme list 6501\"\n" +
            "后台查看会议室6501成员依次递增\n" +
            "\t\tsip、sps、Account、FXO、BRI、E1、GSM外线分别退出Conference1-6501\n" +
            "\t\t\tasterisk -rx \"meetme list 6501\"\n" +
            "后台查看会议室6501成员依次递减")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","Basic","Trunk","InboundRoute","testConference_2"}, dataProvider = "routes")
    public void testConference_02(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼入失败");

        step("[SPS  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SPS 呼入失败");

        step("[Account  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(4000, "446501", DEVICE_ASSIST_3, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."),"Account 呼入失败");

        step("[FXO  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."),"FXO 呼入失败");

        step("[BRI  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "886501", DEVICE_ASSIST_2, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("5 users in that conference."),"BRI 呼入失败");

        step("[E1  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "666501", DEVICE_ASSIST_2, false);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("6 users in that conference."),"E1 呼入失败");

        step("[GSM  呼入6501] ");
        if(!DEVICE_TEST_GSM.equals("null") && !DEVICE_ASSIST_GSM.equals("null")){
            pjsip.Pj_Make_Call_No_Answer(2000, "33"+DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
            sleep(WaitUntils.SHORT_WAIT*10);
            Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("7 users in that conference."),"GSM 呼入失败");
        }

        int sum = getConferenceUser();

        step("[sip hangup] ");
        pjsip.Pj_hangupCall(caller);//3001
        Assert.assertEquals(sum-1,getConferenceUser(),"sip hang up 异常");

        step("[sps hangup] ");
        pjsip.Pj_hangupCall(4000);
        Assert.assertEquals(sum-2,getConferenceUser(),"sip hang up 异常");

        step("[2000 hangup] ");
        pjsip.Pj_hangupCall(2000);
        log.debug("【hangup 2000】"+ getConferenceUser()," 2000 hang up 异常");

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(6);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple ("4000<6700>", "Conference Conference1<6501>", "ANSWERED", "4000<6700> hung up", ACCOUNTTRUNK, "", "Internal")).
                contains(tuple ("3001<3001>", "Conference Conference1<6501>", "ANSWERED", "3001<3001> hung up", SIPTrunk, "", "Internal")).
                contains(tuple ("2000<2000>", "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up", FXO_1, "", "Internal")).
                contains(tuple ("2000<2000>", "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up", E1, "", "Internal")).
                contains(tuple ("2000<2000>", "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up", BRI_1, "", "Internal")).
                contains(tuple ("2000<2000>", "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up", SPS, "", "Internal"));

        if(!DEVICE_TEST_GSM.equals("null") && !DEVICE_ASSIST_GSM.equals("null")){
            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple (DEVICE_ASSIST_GSM, "Conference Conference1<6501>", "ANSWERED", DEVICE_ASSIST_GSM+" hung up", GSM, "", "Internal"));
        }

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "3.通过sip外线呼入到Conference1-6501，输入密码123\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","ParticipantPassword","ModeratorPassword","testConference_3"}, dataProvider = "routes")
    public void testConference_03(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
//        auto.conferencePage().edit("Number","6501").
//                setElementValue(auto.conferencePage().ele_conference_partic_password,"123").
//                setElementValue(auto.conferencePage().ele_conference_moderator_password,"456").clickSaveAndApply();
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, "123");

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "4.通过sip外线呼入到Conference1-6501，连续3次输入密码1234\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501没有新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_4"}, dataProvider = "routes")
    public void testConference_04(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, "1234");
        pjsip.Pj_Send_Dtmf(caller, "1234");
        pjsip.Pj_Send_Dtmf(caller, "1234");

        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("0 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(caller);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), CDR_CONFERENCE_6501, "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "5.通过sip外线呼入到Conference1-6501,不输入密码\n" +
            "\tasterisk后台查看分别播放了3次conf-getpin.gsm、conf-invalidpin.gsm 提示音后，通话被挂断\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_5"}, dataProvider = "routes")
    public void testConference_05(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        List<AsteriskObject> asteriskObjectList_1 = new ArrayList<AsteriskObject>();
        List<AsteriskObject> asteriskObjectList_2 = new ArrayList<AsteriskObject>();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_1,CONF_GETPIN_GSM)).start();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_2,CONF_INVALIDPIN_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();
        sleep(WaitUntils.SHORT_WAIT*4);
        step("[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        step("3.tasterisk后台查看分别播放了3次conf-getpin");
        int tmp = 0;
        while (asteriskObjectList_1.size() !=3 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList_1.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList_1.get(i).getName() +" [asterisk object time] "+asteriskObjectList_1.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList_1.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        step("4.conf-invalidpin.gsm 提示音后，通话被挂断");
        tmp = 0;
        while (asteriskObjectList_2.size() !=2 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList_2.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList_2.get(i).getName() +" [asterisk object time] "+asteriskObjectList_2.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList_2.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),HUNGUP);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "6.通过sip外线呼入到Conference1-6501,输入密码456\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","ParticipantPassword","ModeratorPassword","testConference_6"}, dataProvider = "routes")
    public void testConference_06(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        pjsip.Pj_Send_Dtmf(caller, "456#");

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "7.通过sip外线呼入到Conference1-6501,第一次输入密码124，第2次输入密码126，第3次输入密码123\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_7"}, dataProvider = "routes")
    public void testConference_07(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Send_Dtmf(caller, "124#");
        pjsip.Pj_Send_Dtmf(caller, "126#");
        pjsip.Pj_Send_Dtmf(caller, "123#");
        sleep(WaitUntils.SHORT_WAIT*3);

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "8.通过sip外线呼入到Conference1-6501,第一次输入密码124，第2次输入密码126，第3次输入密码456\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_8"}, dataProvider = "routes")
    public void testConference_08(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","123","456","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "124#");
        pjsip.Pj_Send_Dtmf(caller, "126#");
        pjsip.Pj_Send_Dtmf(caller, "456#");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456" +
            "9.通过sip外线呼入到Conference1-6501，输入密码123\n" +
            "通过sps外线呼入到Conference1-6501，输入密码456\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增2个成员\n" +
            "保持通话，修改Conference1-6501的ParticipantPassword为555，ModeratorPassword 为888\n" +
            "\t分机1000呼入Conference1-6501，输入密码555；\n" +
            "分机1001呼入Conference1-6501，输入密码888；\n" +
            "\t\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501成员递增\n" +
            "\t\t\tsip、sps外线主叫挂断通话\n" +
            "\t\t\t\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501成员分别递减1个\n" +
            "\t\t\t\t\t通过sip外线呼入到Conference1-6501，输入密码123\n" +
            "通过sps外线呼入到Conference1-6501，输入密码456\n" +
            "\t\t\t\t\t\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501成员没有递增\n" +
            "\t\t\t\t\t\t\t通过sip外线呼入到Conference1-6501，输入密码555\n" +
            "通过sps外线呼入到Conference1-6501，输入密码888\n" +
            "\t\t\t\t\t\t\t\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501成员分别递增1个")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_9"}, dataProvider = "routes")
    public void testConference_09(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为123，ModeratorPassword 为456");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);

        auto.conferencePage().edit("Number","6501").
                setElementValue(auto.conferencePage().ele_conference_partic_password,"123").
                setElementValue(auto.conferencePage().ele_conference_moderator_password,"456").clickSaveAndApply();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        pjsip.Pj_Send_Dtmf(caller, "123");

        step("4:[SPS  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);
        pjsip.Pj_Send_Dtmf(2000, "456");

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SIP 呼入失败");

        step("5.保持通话，修改Conference1-6501的ParticipantPassword为空，ModeratorPassword 为888");
        auto.conferencePage().edit("Number","6501").
                setElementValue(auto.conferencePage().ele_conference_partic_password,"555").
                setElementValue(auto.conferencePage().ele_conference_moderator_password,"888").clickSaveAndApply();

        step("6.分机1000呼入Conference1-6501，不输入密码；");
        pjsip.Pj_Make_Call_No_Answer(1000, "6501", DEVICE_IP_LAN, false);
        pjsip.Pj_Send_Dtmf(1001, "555");

        step("7.分机1001呼入Conference1-6501，输入密码888；");
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        pjsip.Pj_Send_Dtmf(1001, "888");

        assertStep("[Asterisk校验] asterisk -rx \"meetme list 6501\" 后台查看会议室6501成员递增");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."),"SIP 失败");

        step("8.sip、sps外线主叫挂断通话");
        pjsip.Pj_hangupCall(caller);
        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."),"SIP 失败");

        pjsip.Pj_hangupCall(2000);
        sleep(2000);
        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SPS 失败");

        step("通过sip外线呼入到Conference1-6501，输入密码123");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        pjsip.Pj_Send_Dtmf(caller, "123");
        sleep(5000);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SIP 呼入失败");

        step("通过sps外线呼入到Conference1-6501，输入密码456");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);
        pjsip.Pj_Send_Dtmf(2000, "456");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SIP 呼入失败");

        //挂断电话，后面重新拨打
        pjsip.Pj_hangupCall(caller);
        pjsip.Pj_hangupCall(2000);

        step("通过sip外线呼入到Conference1-6501，输入密码555");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        pjsip.Pj_Send_Dtmf(caller, "555");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."),"SIP 呼入失败");

        step("通过sps外线呼入到Conference1-6501，输入密码888");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);
        pjsip.Pj_Send_Dtmf(2000, "888");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."),"SIP 呼入失败");

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_hangupCall(caller);
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(7);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为888" +
            "10.通过sip外线呼入到Conference1-6501,不输入密码\n" +
            "\tasterisk后台查看分别播放了1次conf-getpin.gsm后，加入会议室成功；\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_10"}, dataProvider = "routes")
    public void testConference_10(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_GETPIN_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为888");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","888","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."),"SIP 呼叫失败");

        pjsip.Pj_hangupCall(caller);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(7);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为888" +
            "11.通过sip外线呼入到Conference1-6501，输入密码888\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫 ，且是Admin\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_11"}, dataProvider = "routes")
    public void testConference_11(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为888");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","888","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        pjsip.Pj_Send_Dtmf(caller, "888");

        assertStep("[Asterisk校验]");
        String result = SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501));
        Assert.assertTrue(result.contains("1 users in that conference."),"用户验证失败");
        Assert.assertTrue(result.contains("Admin"),"Admin验证失败");
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为999，ModeratorPassword 为空" +
            "12.通过sip外线呼入到Conference1-6501,不输入密码\n" +
            "\tasterisk后台查看分别播放了1次conf-getpin.gsm后，加入会议室成功；\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫，且是Admin\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_12"}, dataProvider = "routes")
    public void testConference_12(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_GETPIN_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为999，ModeratorPassword 为空");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","999","","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        assertStep("[Asterisk校验]");
        sleep(WaitUntils.SHORT_WAIT*2);
        String result = SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501));
        Assert.assertTrue(result.contains("1 users in that conference."),"用户验证失败");
        Assert.assertTrue(result.contains("Admin"),"Admin验证失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR =  apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(7);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为999，ModeratorPassword 为空" +
            "13.通过sip外线呼入到Conference1-6501，输入密码999\n" +
            "\tasterisk后台查看分别播放了1次conf-getpin.gsm后，加入会议室成功；\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫，且是普通成员\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ParticipantPassword","ModeratorPassword","testConference_13"}, dataProvider = "routes")
    public void testConference_13(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_GETPIN_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为999，ModeratorPassword 为空");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","999","","default",0,0,extensions).apply();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        pjsip.Pj_Send_Dtmf(caller,"999");

        assertStep("[Asterisk校验]");
        String result = SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501));
        Assert.assertTrue(result.contains("1 users in that conference."),"用户验证失败");
        Assert.assertFalse(result.contains("Admin"),"Admin验证失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(7);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ParticipantPassword,ModeratorPassword")
    @Description("1.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为空\n" +
            "\t14.通过sip外线呼入到Conference1-6501,不输入密码\n" +
            "\t\tasterisk后台查看分别播放了1次conf-getpin.gsm后，加入会议室成功；\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫，且是普通成员\n" +
            "\t\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","ParticipantPassword","ModeratorPassword","testConference_14"}, dataProvider = "routes")
    public void testConference_14(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_GETPIN_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为空");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","","default",0,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[Asterisk校验]");
        String result = SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501));
        Assert.assertTrue(result.contains("1 users in that conference."),"用户验证失败");
        Assert.assertFalse(result.contains("Admin"),"Admin验证失败");

        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR =  apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(7);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("2000<2000>".replace("2000",caller+""), "Conference Conference1<6501>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("WaitforModerator")
    @Description("1.编辑Conference1-6501的PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator" +
            "15.通过sip/sps外线分别呼入到Conference1-6501，不输入密码\n" +
            "\t呼入时asterisk后台都会打印播放提示音conf-waitforleader.gsm\n" +
            "\t\t分机1004呼入Conference1-6501，输入密码123\n" +
            "\t\t\tasterisk后台打印播放提示音conf-placeintoconf.gsm\n" +
            "\t\t\t\t分机1004挂断通话\n" +
            "\t\t\t\t\t呼入时asterisk后台都会打印播放提示音conf-waitforleader.gsm")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","WaitforModerator","testConference_15"}, dataProvider = "routes")
    public void testConference_15(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        List<AsteriskObject> asteriskObjectList_1 = new ArrayList<AsteriskObject>();
        List<AsteriskObject> asteriskObjectList_2 = new ArrayList<AsteriskObject>();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_WAITFORLEADER_GSM)).start();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_1,CONF_PLACEINTOCONF_GSM)).start();


        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator");
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();


        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[SPS  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."),"SPS 呼入失败");

        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_2,CONF_WAITFORLEADER_GSM)).start();//
        step("[1004  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(1004, "996501", DEVICE_IP_LAN, false);
        pjsip.Pj_Send_Dtmf(1004,"123");

        tmp = 0;
        while (asteriskObjectList_1.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList_1.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList_1.get(i).getName() +" [asterisk object time] "+asteriskObjectList_1.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList_1.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList_1.size());
        }

        assertStep("[Asterisk校验]");
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."),"SPS 呼入失败");

        pjsip.Pj_hangupCall(1004);

        tmp = 0;
        while (asteriskObjectList_2.size() !=1 && tmp <= 600){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList_2.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList_2.get(i).getName() +" [asterisk object time] "+asteriskObjectList_2.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList_2.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList_2.size());
        }
    }


    @Epic("P_Series")
    @Feature("Conference")
    @Story("WaitforModerator")
    @Description("1.编辑Conference1-6501的PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator" +
            "16.编辑Conference1-6501，不勾选WaitforModerator\n" +
            "\t通过sip/sps外线分别呼入到Conference1-6501，不输入密码\n" +
            "\t\t呼入时asterisk后台都不会打印播放提示音conf-waitforleader.gsm")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","WaitforModerator","testConference_16"}, dataProvider = "routes")
    public void testConference_16(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_WAITFORLEADER_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501的ParticipantPassword为空，ModeratorPassword 为空");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",0,1,extensions).apply();

        step("3:[SIP 呼入6501][caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+message);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[SPS  呼入6501] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 200){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        Assert.assertEquals(tmp,201,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());//没有提示音

    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("AllowParticipantstoInvite")
    @Description("1.编辑Conference1-6501，勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator\n" +
            "\t17.分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室\n" +
            "\t\t辅助1的3001分机响铃、接听\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t\t分机1000按#23001 邀请辅助2的2000加入会议室;\n" +
            "分机1000按#1001 邀请分机1001加入会议室；\n" +
            "\t\t\t\t辅助2的2000分机响铃、接听；asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sps主叫、一个成员1001分机\n" +
            "\t\t\t\t\t通话挂断，所有分机退出会议室；cdr正确；")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","AllowParticipantstoInvite","testConference_17"}, dataProvider = "routes")
    public void testConference_17(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501，勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("0");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","","default",0,1,extensions).apply();

        step("分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1000, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1000, "#");
        pjsip.Pj_Send_Dtmf(1000, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));


        step("分机1000按#23001 邀请辅助2的2000加入会议室;");
        pjsip.Pj_Send_Dtmf(1000, "#");
        pjsip.Pj_Send_Dtmf(1000, "23001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."));

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(2000);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(4);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "test A<1000> invited 13001", "",SIPTrunk , "Outbound")).
                contains(tuple(CDR_CONFERENCE_6501, "23001", "ANSWERED", "test A<1000> invited 23001", "", SPS, "Outbound")).
                contains(tuple("test A<1000>", CDR_CONFERENCE_6501, "ANSWERED", "test A<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("AllowParticipantstoInvite")
    @Description("1.编辑Conference1-6501，不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator，Moderators 选择分机1001\n" +
            "18.分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室\n" +
            "\t辅助1的3001分机不会响铃；asterisk -如下“meetme list 6501\" 后台查看会议室6501没有新增成员sip\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","AllowParticipantstoInvite","testConference_18"}, dataProvider = "routes")
    public void testConference_18(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501，勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","","default",0,0,extensions).apply();

        step("分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1000, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("1 users in that conference."));
        pjsip.Pj_Send_Dtmf(1000, "#");
        pjsip.Pj_Send_Dtmf(1000, "13001");

        assertStep("[通话状态校验]");
        int result =getExtensionStatus(3001,RING,10);
        Assert.assertTrue(result == HUNGUP || result == IDLE);
        Assert.assertFalse(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("test A<1000>", CDR_CONFERENCE_6501, "ANSWERED", "test A<1000> hung up", "","" , "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("AllowParticipantstoInvite")
    @Description("1.编辑Conference1-6501，不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator，Moderators 选择分机1001\n" +
            "19.分机1001呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室\n" +
            "\t辅助1的3001分机响铃、接听\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫\n" +
            "\t\t通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","AllowParticipantstoInvite","testConference_19"}, dataProvider = "routes")
    public void testConference_19(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501，不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator，Moderators 选择分机1001\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","","default",0,0,extensions).apply();

        step("分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1001, "#");
        pjsip.Pj_Send_Dtmf(1001, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(4);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("test2 B<1001>", CDR_CONFERENCE_6501, "ANSWERED", "test2 B<1001> hung up", "", "", "Internal")).
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "test2 B<1001> invited 13001", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Moderators")
    @Description("1.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "2.分机1002呼入Conference1-6501" +
            "20.通过sip外线呼入到Conference1-6501，不输入密码\n" +
            "\t呼入时asterisk后台不会打印播放提示音conf-waitforleader.gsm")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","Moderators","testConference_20"}, dataProvider = "routes")
    public void testConference_20(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CONF_WAITFORLEADER_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501，不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为空，不勾选WaitforModerator，Moderators 选择分机1001\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","","default",1,0,extensions).apply();

        step("分机1000呼入Conference1-6501,按#13001 邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1002, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1002, "#");
        pjsip.Pj_Send_Dtmf(1002, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 100){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp != 101){
            Assert.assertFalse(true,"[有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(RING);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("testta C<1002>", CDR_CONFERENCE_6501, "ANSWERED", "testta C<1002> hung up", "", "", "Internal")).
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "testta C<1002> invited 13001", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Moderators")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "2.分机1002呼入Conference1-6501" +
            "21.1002按#13001邀请辅助1的3001加入会议室\n" +
            "\t辅助1的3001分机响铃、接听\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","Moderators","testConference_21"}, dataProvider = "routes")
    public void testConference_21(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1002呼入Conference1-6501,1002按#13001邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1002, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1002, "#");
        pjsip.Pj_Send_Dtmf(1002, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(RING);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("testta C<1002>", CDR_CONFERENCE_6501, "ANSWERED", "testta C<1002> hung up", "", "", "Internal")).
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "testta C<1002> invited 13001", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Moderators")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "分机1000呼入Conference1-6501\n" +
            "\t22.1000按#13001邀请辅助1的3001加入会议室\n" +
            "\t\t辅助1的3001分机响铃、接听\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","Moderators","testConference_22"}, dataProvider = "routes")
    public void testConference_22(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1000呼入Conference1-6501,1000按#13001邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1000, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1000, "#");
        pjsip.Pj_Send_Dtmf(1000, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(RING);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "test A<1000> invited 13001", "",SIPTrunk , "Outbound")).
                contains(tuple("test A<1000>", CDR_CONFERENCE_6501, "ANSWERED", "test A<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Moderators")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "分机1001呼入Conference1-6501\n" +
            "\t23.1001按#13001邀请辅助1的3001加入会议室\n" +
            "\t\t辅助1的3001分机响铃、接听\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501新增一个成员sip主叫")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","Moderators","testConference_23"}, dataProvider = "routes")
    public void testConference_23(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1001呼入Conference1-6501,1001按#13001邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1001, "#");
        pjsip.Pj_Send_Dtmf(1001, "13001");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(4);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
                contains(tuple("test2 B<1001>", CDR_CONFERENCE_6501, "ANSWERED", "test2 B<1001> hung up", "", "", "Internal")).
                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "test2 B<1001> invited 13001", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Moderators")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "编辑Conference1-6501，Moderators 选择分机1002、1003\n" +
            "\t24.分机1000呼入Conference1-6501\n" +
            "\t\t呼入时asterisk后台会打印播放提示音conf-waitforleader.gsm\n" +
            "\t\t\t分机1003呼入Conference-6501\n" +
            "\t\t\t\tasterisk后台打印播放提示音conf-placeintoconf.gsm;\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501有2个成员\n" +
            "\t\t\t分机1002呼入Conference-6501\n" +
            "\t\t\t\tasterisk后台打印播放提示音conf-placeintoconf.gsm;\n" +
            "asterisk -rx \"meetme list 6501\" 后台查看会议室6501有2个成员")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","Moderators","testConference_24"}, dataProvider = "routes")
    public void testConference_24(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);
        List<AsteriskObject> asteriskObjectList_1 = new ArrayList<AsteriskObject>();
        List<AsteriskObject> asteriskObjectList_2 = new ArrayList<AsteriskObject>();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_1,CONF_WAITFORLEADER_GSM)).start();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList_2,CONF_PLACEINTOCONF_GSM)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1000呼入Conference1-6501,1001按#13001邀请辅助1的3001加入会议室");
        pjsip.Pj_Make_Call_No_Answer(1000, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(1001, "#");
        pjsip.Pj_Send_Dtmf(1001, "13001");
//
//        assertStep("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
//        pjsip.Pj_Answer_Call(3001,false);
//        assertStep("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);
//        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));
//
//        pjsip.Pj_hangupCall(1001);
//        pjsip.Pj_hangupCall(3001);
//
//        assertStep("[CDR校验]");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//        List<CDRObject> resultCDR = apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD).getCDRRecord(4);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType").
//                contains(tuple(CDR_CONFERENCE_6501, "13001", "ANSWERED", "test A<1000> invited 13001", "",SIPTrunk , "Outbound")).
//                contains(tuple(CDR_CONFERENCE_6501, "23001", "ANSWERED", "test A<1000> invited 23001", "", SPS, "Outbound")).
//                contains(tuple("test A<1000>", CDR_CONFERENCE_6501, "ANSWERED", "test A<1000> hung up", "", "", "Internal"));
//
//        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ConferenceMenu")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "25.分机1001、1002先后呼入Conference1-6501\n" +
            "\t分机1001按* 再按2锁住会议室6501\n" +
            "\t\t通过sip外线呼入到Conference1-6501，不输入密码\n" +
            "\t\t\tasterisk -rx \"meetme list 6501\" 后台查看只有2个成员\n" +
            "\t\t\t\t分机1001按* 再按2开放会议室6501\n" +
            "\t\t\t\t\t通过sip外线呼入到Conference1-6501，不输入密码\n" +
            "\t\t\t\t\t\tasterisk -rx \"meetme list 6501\" 后台查看有3个成员")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ConferenceMenu","testConference_25"}, dataProvider = "routes")
    public void testConference_25(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1001、1002先后呼入Conference1-6501");
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Make_Call_No_Answer(1002, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));

        step("分机1001按* 再按2锁住会议室6501");
        pjsip.Pj_Send_Dtmf(1001, "*");
        pjsip.Pj_Send_Dtmf(1001, "2");


        step("通过sip外线呼入到Conference1-6501，不输入密码");
        pjsip.Pj_Make_Call_No_Answer(caller, callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("2 users in that conference."));
        pjsip.Pj_hangupCall(caller);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("分机1001按* 再按2开放会议室6501");
        pjsip.Pj_Send_Dtmf(1001, "*");
        pjsip.Pj_Send_Dtmf(1001, "2");

        step("通过sip外线呼入到Conference1-6501，不输入密码");
        pjsip.Pj_Make_Call_No_Answer(caller, callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."));
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ConferenceMenu")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "26.分机1001、1002、sps外线、sip外线先后呼入到Conference1-6501\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看只有4个成员\n" +
            "\t\t分机1001按* 再按3剔除最后一个成员加入会议室\n" +
            "\t\t\tasterisk -rx \"meetme list 6501\" 后台查看只有3个成员")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ConferenceMenu","testConference_26"}, dataProvider = "routes")
    public void testConference_26(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1001、1002、sps外线、sip外线先后呼入到Conference1-6501");
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(1002, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(caller, "6501", deviceAssist, false);//SIP
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);//SPS
        sleep(WaitUntils.SHORT_WAIT*2);

        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."));

        step("分机1001按* 再按3剔除最后一个成员加入会议室");
        pjsip.Pj_Send_Dtmf(1001, "*");
        pjsip.Pj_Send_Dtmf(1001, "3");

        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("3 users in that conference."));
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("ConferenceMenu")
    @Description("编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1" +
            "27.分机1002、sps外线、sip外线、分机1001先后呼入到Conference1-6501\n" +
            "\tasterisk -rx \"meetme list 6501\" 后台查看只有4个成员\n" +
            "\t\t分机1001按* 再按3剔除最后一个成员加入会议室\n" +
            "\t\t\tasterisk -rx \"meetme list 6501\" 后台查看还是有4个成员\n" +
            "\t\t分机1002按* 再按3剔除最后一个成员加入会议室\n" +
            "\t\t\t管理员不会被剔除，asterisk -rx \"meetme list 6501\" 后台查看还是有4个成员")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P3", "Conference","ConferenceMenu","testConference_27"}, dataProvider = "routes")
    public void testConference_27(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑Conference1-6501,不勾选AllowParticipantstoInvite,PartcipantPassword为空，ModeratorPassword为123，勾选WaitforModerator，Moderators 选择分机1001、1002、ExGroup1\n");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);
        List<String> extensions = new ArrayList<>();
        extensions.add("1001");
        extensions.add("1002");
        extensions.add("ExGroup1");
        apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW).editConference("6501","","123","default",1,0,extensions).apply();

        step("分机1002、sps外线、sip外线、分机1001先后呼入到Conference1-6501");
        pjsip.Pj_Make_Call_No_Answer(1002, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(2000, "996501", DEVICE_ASSIST_2, false);//SPS
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(caller, callee, deviceAssist, false);//SIP
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Make_Call_No_Answer(1001, "6501", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."));

        step("分机1001按* 再按3剔除最后一个成员加入会议室");
        pjsip.Pj_Send_Dtmf(1001, "*");
        pjsip.Pj_Send_Dtmf(1001, "3");

        sleep(WaitUntils.SHORT_WAIT*2);
        Assert.assertTrue(SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501)).contains("4 users in that conference."));
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Delete")
    @Description("28.单条删除Conference1\n" +
            "\t检查列表Conference1被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","Delete","testConference_28"}, dataProvider = "routes")
    public void testConference_28(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:进入IVR界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if(!lists.contains("6502")){
            auto.ivrPage().createIVR("6202","IVR2_6202").clickSaveAndApply();
        }
        step("3:删除IVR");
        auto.ivrPage().deleDataByDeleImage("6202").clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list).doesNotContain("6202");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Conference")
    @Story("Delete")
    @Description("29.再次创建Conference2\n" +
            "\t批量选择删除Conference2\n" +
            "\t\t检查列表Conference2被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Cloud","K2","P2", "Conference","Delete","testConference_29"}, dataProvider = "routes")
    public void testConference_29(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:进入IVR界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_conference);

        step("3:批量删除IVR1");
        auto.ivrPage().deleAllIVR().clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list.size()).isEqualTo(0);
        softAssertPlus.assertAll();
    }

    /**
     * 获取 conference 6501 user的个数
     * @return
     * @throws IOException
     * @throws JSchException
     */
    private int getConferenceUser() throws IOException, JSchException {
        String result = SSHLinuxUntils.exePjsip(String.format(ASTERISK_CLI,MEETME_LIST_6501));
        return Integer.valueOf(result.substring(result.indexOf("users in that conference.")-2,result.indexOf("users in that conference.")).trim());
    }
}
