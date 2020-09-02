package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.*;
import com.yeastar.page.pseries.OperatorPanel.Record;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
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
public class TestOperatorRingGroup_1 extends TestCaseBase {
    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlagExtension = true;
    private boolean runRecoveryEnvFlagRingGroup = true;
    private boolean runRecoveryEnvFlagQueue = true;
    private boolean runRecoveryEnvFlagConference = true;
    ArrayList<String> queueListNum = null;
    ArrayList<String> queueListNum_1 = null;
    ArrayList<String> ringGroupNum = null;
    ArrayList<String> ringGroupNum_1 = null;
    ArrayList<String> conferenceList = null;

    String queueListName = "Q0";
    String queueListName_1 = "Q1";
    String ringGroupName_0 = "RG0:";//6300
    String ringGroupName_1 = "RG1:";//6301
    String conferenceListName = "CONF1";


    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"1-3\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,"DOD",DEVICE_ASSIST_2,DEVICE_ASSIST_2);


    public void prerequisiteForAPIForRingGroup(boolean booRunRecoveryEnvFlag) {
        if (booRunRecoveryEnvFlag) {
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            trunks.add(BRI_1);
            trunks.add(FXO_1);
            trunks.add(E1);
            trunks.add(SIPTrunk);
            trunks.add(ACCOUNTTRUNK);
            List<String> extensionNum = new ArrayList<>();
            queueListNum = new ArrayList<>();
            queueListNum_1 = new ArrayList<>();
            ringGroupNum = new ArrayList<>();
            ringGroupNum_1 = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");

            extensionNum.add("0");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");
            extensionNum.add("1006");
            extensionNum.add("1007");
            extensionNum.add("1008");
            extensionNum.add("1009");
            extensionNum.add("1010");
            extensionNum.add("1011");
            extensionNum.add("1012");

            step("创建分机1000-1010");
            apiUtil.deleteAllExtension().apply();
            sleep(WaitUntils.SHORT_WAIT);
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1000").replace("EXTENSIONLASTNAME", "A").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "E").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "F").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1006").replace("EXTENSIONLASTNAME", "G").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1007").replace("EXTENSIONLASTNAME", "H").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1008").replace("EXTENSIONLASTNAME", "I").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1009").replace("EXTENSIONLASTNAME", "J").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1010").replace("EXTENSIONLASTNAME", "K").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1011").replace("EXTENSIONLASTNAME", "L").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1012").replace("EXTENSIONLASTNAME", "N").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2);

            step("创建响铃组6300");
            ringGroupNum.add("1000");
            ringGroupNum.add("1001");
            ringGroupNum.add("1002");
            ringGroupNum.add("1003");
            ringGroupNum.add("1004");

            step("创建响铃组6301");
            ringGroupNum_1.add("1005");
            ringGroupNum_1.add("1006");
            ringGroupNum_1.add("1007");
            ringGroupNum_1.add("1008");
            ringGroupNum_1.add("1009");
            apiUtil.deleteAllRingGroup().createRingGroup("RG0", "6300", ringGroupNum)
                    .createRingGroup("RG1", "6301", ringGroupNum_1);

            step("创建队列");
            queueListNum.add("1000");
            queueListNum.add("1001");
            queueListNum.add("1002");
            queueListNum.add("1003");
            queueListNum.add("1004");


            step("创建队列");
            queueListNum_1.add("1005");
            queueListNum_1.add("1006");
            queueListNum_1.add("1007");
            queueListNum_1.add("1008");
            queueListNum_1.add("1009");
            apiUtil.deleteAllQueue().createQueue(queueListName, "6400", null, queueListNum, null)
                    .createQueue(queueListName_1, "6401", null, queueListNum_1, null);

            step("创建ivr 6200");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0,"extension","","1000",0));
            apiUtil.deleteAllIVR().createIVR("6200","6200",pressKeyObjects_0);

            step("创建呼入路由InRoute2,目的地到ring_group 6300");
            apiUtil.deleteAllInbound().createInbound("InRoute2", trunks, "ring_group", "6300");

            step("创建呼出路由");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);


            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlagRingGroup = false;
        }
    }
    Object[][] routes = new Object[][] {
            {"99",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SPS"},//sps   前缀 替换
            {"88",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"BRI"},//BRI   前缀 替换
            {""  ,2000,"2005",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"FXO"},//FXO --77 不输   2005（FXS）
            {"66",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"E1"},//E1     前缀 替换
            {""  ,2000,"2001",DEVICE_ASSIST_1,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SIP_REGISTER"},
            {"44",4000,"6300",DEVICE_ASSIST_3,"4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(),"SIP_ACCOUNT"}//SIP  --55 REGISTER
    };
    /**
     * 多线路测试数据
     * routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + vcpCaller（VCP列表中显示的主叫名称） + vcpDetail（VCP中显示的Detail信息） + testRouteTypeMessage（路由类型）
     * @return
     */
    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c) {
        Object[][] group = null;
        for (String groups : c.getIncludedGroups()) {
            for (int i = 0; i < routes.length; i++) {
                for (int j = 0; j < routes[i].length; j++) {
                    if (groups.equalsIgnoreCase("SPS")) {
                        group = new Object[][] {{"99",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SPS"}};
                    }else if (groups.equalsIgnoreCase("BRI")) {
                        group = new Object[][] {{"88",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"BRI"}};
                    }else if (groups.equalsIgnoreCase("FXO")) {
                        group = new Object[][] {{""  ,2000,"2005",DEVICE_ASSIST_2,"2000 [2000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"FXO"}};
                    }else if (groups.equalsIgnoreCase("E1")) {
                        group = new Object[][] {{"66",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"E1"}};
                    }else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                        group = new Object[][] {{""  ,2000,"2001",DEVICE_ASSIST_1,"2000 [2000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_REGISTER"}};
                    }else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                        group = new Object[][] {{"44",4000,"6300",DEVICE_ASSIST_3,"4000 [4000]", RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_ACCOUNT"}};
                    }else {
                        group =routes;//默认选择具体的用例跑所有线路
                    }
                }
            }
        }
        return group;
    }
    /**
     * 多线路测试数据
     * routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + vcpCaller（VCP列表中显示的主叫名称） + vcpDetail（VCP中显示的Detail信息） + testRouteTypeMessage（路由类型）
     * @return
     */
    @DataProvider(name = "routesDebug")
    public Object[][] RoutesDebug() {
        return new Object[][] {
                {"99",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SPS"},//sps   前缀 替换
                {"88",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]",RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"BRI"},//BRI   前缀 替换
                {""  ,2000,"2005",DEVICE_ASSIST_2,"2000 [2000]",RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"FXO"},//FXO --77 不输   2005（FXS）
                {"66",2000,"6300",DEVICE_ASSIST_2,"2000 [2000]",RECORD_DETAILS.EXTERNAL_IVR.getAlias(),"E1"},//E1     前缀 替换
                {""  ,2000,"2001",DEVICE_ASSIST_1,"2000 [2000]",RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_REGISTER"},//SIP  --55 REGISTER
                {"44",4000,"6300",DEVICE_ASSIST_3,"4000 [4000]",RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias(),"SIP_ACCOUNT"}
        };
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRingStatus","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingStatus(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
//       prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

          softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                    .contains(tuple(ringGroupName_0 +vcpCaller, "1000 A [1000]","Ringing", vcpDetail),
                              tuple(ringGroupName_0 +vcpCaller, "1001 B [1001]","Ringing", vcpDetail),
                              tuple(ringGroupName_0 +vcpCaller, "1002 C [1002]","Ringing", vcpDetail),
                              tuple(ringGroupName_0 +vcpCaller, "1003 D [1003]","Ringing", vcpDetail));


        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 响铃中 -->[DragAndDrop]-->[RingGroup]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.DragAndDrop RG\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRingGroupIncomingRingStatus","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropRG(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1006,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1007,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1008,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1009,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT*2);


        step("4：[RingGroup]6300 -->拖动到[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.RINGGROUP,"6301");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
            softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                    .contains(tuple(ringGroupName_1+vcpCaller, "1005 F [1005]","Ringing", vcpDetail),
                              tuple(ringGroupName_1+vcpCaller, "1006 G [1006]","Ringing", vcpDetail),
                              tuple(ringGroupName_1+vcpCaller, "1007 H [1007]","Ringing", vcpDetail),
                              tuple(ringGroupName_1+vcpCaller, "1008 I [1008]","Ringing", vcpDetail),
                              tuple(ringGroupName_1+vcpCaller, "1009 J [1009]","Ringing", vcpDetail));

//        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());

        step("5:1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("6:[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                    .contains(tuple(ringGroupName_1+vcpCaller, "1005 F [1005]","Talking", vcpDetail));

        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300-->DragAndDrop 到Parking\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 6300]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Parking]001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropParking","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropParking(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("6：[Inbound]1001 -->拖动到[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"001");

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

//        softAssertPlus.assertThat(allRecordList).as("验证数量").size().isEqualTo(queueListNum.size());
        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300-->DragAndDrop Queue\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 6300]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Queue]6401")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropQueue","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropQueue(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        step("5:【2000 呼叫 1001】，1001 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6400");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.QUEUE,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("7:[VCP显示]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(queueListName+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                          tuple(queueListName+vcpCaller, "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                          tuple(queueListName+vcpCaller, "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                          tuple(queueListName+vcpCaller, "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300 -->DragAndDrop 1000 到分机 1010（Talking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1011]-->[1010]通话,1010-->Talking\n+" +
            "3:[2000 呼叫 RingGroup 6300]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1010")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("bug 等待留言的时候才开始录音")
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCTalking","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropWithCTalking(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1010,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1011,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1011,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);
        refresh();

        step("4:【1011 与1010 通话】，【1010】 Talking状态");
        pjsip.Pj_Make_Call_Auto_Answer(1011,"1010",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("5:【2000 呼叫 1000】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);//TODO 拖动后状态无法直接接听，通过30s timeout 转到voicemail?? 手动不出现
        refresh();

        assertStep("7:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300 -->DragAndDrop 1000 到分机 1010（idle）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 RingGroup 6300]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（idle）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCIdle","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropWithCIdle(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1010,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 1001】，1010 空闲状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");

        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:显示状态 A--C talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300 -->DragAndDrop 1000 到分机 1010（未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 RingGroup 6300]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（未注册）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("勾选显示未注册分机，概率性出现 未注册分机不能显示")
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCUnregistered","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRingDragAndDropWithCUnregistered(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

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
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1010,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        step("4:【1010】 未注册");
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 6300】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:[VCP]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300[响铃中] -->右键HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键->HandUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);
//
        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        List<Record> resultSum_after =auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        assertStep("6:[VCP显示]");
        softAssertPlus.assertThat(resultSum_before.size()).isEqualTo(resultSum_after.size()+1);

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键 悬停 HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->右键 悬停 HandUp" +
            "4:移开后 通话继续")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionHoverHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionHoverHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[HandUp->悬停，移开]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.RETRY_WAIT);
        auto.operatorPanelPage().moveByOffsetAndClick(200,200);
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键PickUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键->PickUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionPickUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]2000->1000 初始状态 Ring状态");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[右键PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "0 [0]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_Answer_Call(0,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after_end = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_end).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "0 [0]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中] -->右键Extension：C ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("RG 响铃状态不会有CDR记录  Redirected to 0<2001>")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectC_AHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionRedirectC_AHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("7:[接通]");
        pjsip.Pj_Answer_Call(2001,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_answer).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("9:[挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("10:[CDR显示]");
//        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2000<spsOuntCid> hung up"),
//                        tuple ("2000<2000>", "Queue Q0<6300>", "NO ANSWER", "Redirected to 0<2001>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300[响铃中]-->右键Extension：C ,C先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:c挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("RG 响铃状态不会有CDR记录  Redirected to 0<2001>")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectC_CHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionRedirectC_CHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_0+vcpCaller, "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]","DOD [2001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("7:[接通]");
        pjsip.Pj_Answer_Call(caller,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_answer).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("9:[挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("10:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2000<spsOuntCid> hung up"),
                          tuple ("2000<2000>", "Queue Q0<6300>", "NO ANSWER", "Redirected to 0<2001>"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键Extension 到Ring Group 6301\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Ring Group]6301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRedirectRingGroup","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRedirectRingGroup(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1006,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1007,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1008,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1009,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[RingGroup]6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6301");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+vcpCaller, "1005 F [1005]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_1+vcpCaller, "1006 G [1006]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_1+vcpCaller, "1007 H [1007]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_1+vcpCaller, "1008 I [1008]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                          tuple(ringGroupName_1+vcpCaller, "1009 J [1009]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());

        step("7:1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+vcpCaller, "1005 F [1005]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(1005);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<2000>", "RingGroup RG1<6301>", "ANSWERED", "RingGroup RG1<6301> connected"),
//                          tuple ("2000<2000>", "1005 F<1005>", "ANSWERED", "1005 F<1005> hung up"),
//                          tuple ("2000<2000>", "RingGroup RG0<6300>", "NO ANSWER", "Redirected to RingGroup RG0<6300>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键Extension  到Queue 6401\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6401")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRedirectQueue","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRedirectQueue(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1006,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1007,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1008,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1009,DEVICE_IP_LAN);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[Queue]6401");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6401");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(queueListName_1+vcpCaller, "1005 F [1005]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName_1+vcpCaller, "1006 G [1006]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName_1+vcpCaller, "1007 H [1007]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName_1+vcpCaller, "1008 I [1008]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(queueListName_1+vcpCaller, "1009 J [1009]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum_1.size());

        step("7:显示状态1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP验证]");
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                .contains(tuple(queueListName_1+vcpCaller, "1005 F [1005]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_QUEUE.getAlias()));
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(1005);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<2000>", "Queue Q1<6401>", "ANSWERED", "Queue Q1<6401> connected"),
//                          tuple ("2000<2000>", "1005 F<1005>", "ANSWERED", "1005 F<1005> hung up"),
//                          tuple ("2000<2000>", "RingGroup RG0<6300>", "NO ANSWER", "Redirected to RingGroup RG0<6300>"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键Extension  Voicemail 图标\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Voicemail]小图标")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRedirectVoicemail","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRedirectVoicemail(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1000",true);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("5:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Hangup_All();

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<2000>", "1000 A<1000>", "VOICEMAIL", "2000<2000> hung up"),
//                          tuple ("2000<2000>", "RingGroup RG0<6300>", "NO ANSWER", "Redirected to RingGroup RG0<6300>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键Extension -->Redirect IVR \n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[IVR]6200")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRedirectIVR","Regression","PSeries","VCP1","RingGroup1","Jack",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRedirectIVR(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
       prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1009,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6200");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("5:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "6200 [6200]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        pjsip.Pj_Send_Dtmf(caller,"0");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1009,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        int returnStr = pjsip.Pj_hangupCall(1009);
        System.out.println("***************[1010]"+returnStr);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("9:[CDR显示]");//todo add cdr
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(5);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<2000>", "1010 K<1010>", "ANSWERED", "1010 K<1010> hung up"),
//                          tuple ("2000<2000>", "IVR 6200<6200>", "ANSWERED", "2000<2000> hung up"),//todo bug
//                          tuple ("2000<2000>", "RingGroup RG0<6300>", "NO ANSWER", "Redirected to RingGroup RG0<6300>"));//todo bug

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300[响铃中]-->右键Extension-->右键Redirect：Y ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键Redirect Y(外线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectOffLineY_AHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionRedirectOffLineY_AHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("7:[接通]");
        pjsip.Pj_Answer_Call(2001,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_answer).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("9:[挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("10:[CDR显示]");
//        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2000<spsOuntCid> hung up"),//
//                        tuple ("2000<2000>", "Queue Q0<6400>", "NO ANSWER", "Redirected to 0<2001>"));//todo bug

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [RingGroup] 呼入6300[响铃中]-->右键Extension-->右键Redirect：Y,Y先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[Redirect] Y(外线)" +
            "4:Y挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")//todo  make jenkins vm exception
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectOffLineY_YHandUp","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionRedirectOffLineY_YHandUp(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
      prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_0+vcpCaller, "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        step( "5:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after_redirect = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_redirect).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("7:[接通]");
        pjsip.Pj_Answer_Call(2001,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_after_answer = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after_answer).extracting("caller","callee","status","details")
                .contains(tuple("2000 [2000]", "DOD [2001]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        step("9:[挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("10:[CDR显示]");
//        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
//                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2000<spsOuntCid> hung up"),//
//                        tuple ("2000<2000>", "Queue Q0<6400>", "NO ANSWER", "Redirected to 0<2001>"));//todo bug

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中] -->右键不显示\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300\n" +
            "3:右键->查看显示的条目")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionUnDisplay","Regression","PSeries","VCP1","RingGroup1",
            "SPS","BRI","FXO","FXS","E1","SIP_REGISTER","SIP_ACCOUNT"},dataProvider = "routes")
    public void testRGIncomingRightActionUnDisplay(String routePrefix,int caller,String callee,String deviceAssist,String vcpCaller,String vcpDetail,String message){
        prerequisiteForAPIForRingGroup(runRecoveryEnvFlagRingGroup);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
      pjsip.Pj_CreateAccount(caller,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(caller,deviceAssist);

        pjsip.Pj_Make_Call_No_Answer(caller,routePrefix+callee,deviceAssist,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示] Ring 只显示 Redirect，pick up，hang up");
        List list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Recording","Unrecording");
        pjsip.Pj_Hangup_All();
    }
}
