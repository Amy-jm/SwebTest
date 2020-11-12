package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.*;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalTime;
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
public class TestInboundRouteDIDPattern extends TestCaseBaseNew {
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    APIUtil apiUtil = new APIUtil();
    List<String> trunk9 = new ArrayList<>();
    private String EXTENSION_1000_HUNGUP = "test A<1000> hung up";

    TestInboundRouteDIDPattern(){
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
    }
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    private String PROMPT_1 = "prompt1.slin";
    private String PROMPT_2 = "prompt2.slin";
    private String FAX_TO_EMAIL_1001 = "1001@fax_to_email";
    private  int ROLEID = 0;

    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":ROLEID,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateExtensionWithRoleAdmin = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":1,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionWithRoleSuper = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":2,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionWithRoleOperator = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":3,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionWithRoleEmployee = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":4,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionWithRoleHumanRes = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":5,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateExtensionWithRoleAccount = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":6,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private boolean registerAllExtension() {
        step("===========[Extension]  create & register extension  start =========");
        pjsip.Pj_CreateAccount(0, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(3001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(4000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(0, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);

        boolean reg = false;
        if (getExtensionStatus(1000, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1000注册失败");
        }
        if (getExtensionStatus(1001, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1001注册失败");
        }
        if (getExtensionStatus(1002, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1002注册失败");
        }
        if (getExtensionStatus(1003, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1003注册失败");
        }
        if(getExtensionStatus(1004, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1004注册失败");
        }
        if(getExtensionStatus(1005, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("1004注册失败");
        }
        if(getExtensionStatus(2000, IDLE, 5) != IDLE){
            reg=true;
            log.debug("2000注册失败");
        }
        if(getExtensionStatus(2001, IDLE, 5) != IDLE){
            reg=true;
            log.debug("2001注册失败");
        }
        if(getExtensionStatus(4000, IDLE, 5) != IDLE){
            reg=true;
            log.debug("4000注册失败");
        }
        if(reg){
            pjsip.Pj_Unregister_Accounts();
        }
        step("===========[Extension]  create & register extension  end =========");
        return reg;
    }

    public void prerequisite() {
        //local debug
        long startTime = System.currentTimeMillis();
        if (isDebugInitExtensionFlag) {
            isDebugInitExtensionFlag = registerAllExtension();
            isRunRecoveryEnvFlag = false;
        }

        if (isRunRecoveryEnvFlag) {
            step("=========== init before class  start =========");

            step("1.创建分机");
            apiUtil.deleteAllExtension().apply();
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");
            apiUtil.createExtension(reqDataCreateExtensionWithRoleAdmin.replace("EXTENSIONFIRSTNAME", "test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleSuper.replace("EXTENSIONFIRSTNAME", "test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleOperator.replace("EXTENSIONFIRSTNAME", "testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleEmployee.replace("EXTENSIONFIRSTNAME", "testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleHumanRes.replace("EXTENSIONFIRSTNAME", "t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleAccount.replace("EXTENSIONFIRSTNAME", "First").replace("EXTENSIONLASTNAME", "Last").replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionWithRoleAdmin.replace("EXTENSIONFIRSTNAME", "0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", "1-3").replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("2.创建分机组 ExGroup1/ExGroup2");
            List<String> extensionExGroup1 = new ArrayList<>();
            List<String> extensionExGroup2 = new ArrayList<>();
            extensionExGroup1.add("1000");
            extensionExGroup1.add("1001");

            extensionExGroup2.add("1002");
            extensionExGroup2.add("1003");

            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
                    createExtensionGroup("ExGroup1", extensionExGroup1).
                    createExtensionGroup("ExGroup2", extensionExGroup2).apply();

            step("3.创建队列");
            ArrayList<String> queueStaticListNum_0 = new ArrayList<>();
            ArrayList<String> queueDynamicListNum_0 = new ArrayList<>();
            ArrayList<String> ringGroupNum_0 = new ArrayList<>();
            queueStaticListNum_0.add("1000");
            queueStaticListNum_0.add("1001");

            queueDynamicListNum_0.add("1003");
            queueDynamicListNum_0.add("1004");
            apiUtil.deleteAllQueue().createQueue("Queue0", "6400", 60, queueDynamicListNum_0, queueStaticListNum_0, null, "extension", "1000", "0", "extension", "1001");


            step("4.创建响铃组6300");
            ringGroupNum_0.add("ExGroup1");
            ringGroupNum_0.add("1003");
            apiUtil.deleteAllRingGroup().createRingGroup("RingGroup0", "6300", ringGroupNum_0, 10, "extension", "", "1000");

            step("5.创建IVR IVR0_6200");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));

            apiUtil.deleteAllIVR().createIVR("6200", "IVR0_6200", pressKeyObjects_0);

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
            apiUtil.deleteAllConference().createConference("Conference0", "6500", moderators)
                    .createConference("Conference1", "6501", moderators);

            step("7.创建呼入路由InRoute1,目的地到Conference 6500");
            apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000");

            step("8.创建呼出路由：Out1，Out2，Out3，Out4，Out5，Out6，Out7，Out8，Out9");
            apiUtil.deleteAllOutbound().createOutbound("Out1", trunk1, extensionNum, "1.", 1).
                    createOutbound("Out2", trunk2, extensionNum, "2.", 1).
                    createOutbound("Out3", trunk3, extensionNum, "3.", 1).
                    createOutbound("Out4", trunk4, extensionNum, "4.", 1).
                    createOutbound("Out5", trunk5, extensionNum, "5.", 1).
                    createOutbound("Out6", trunk6, extensionNum, "6.", 1).
                    createOutbound("Out7", trunk7, extensionNum, "7.", 1).
                    createOutbound("Out8", trunk8, extensionNumA).
                    createOutbound("Out9", trunk9, extensionNum);

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            apiUtil.loginWebClient("1000", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            isRunRecoveryEnvFlag = registerAllExtension();
        }
        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }


    //############### dataProvider #########################

    Object[][] routes = new Object[][]{
            //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）   + InBoundTrunk(呼入线路) + OutBoundTrunk(呼出线路)


            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), SPS},//sps   前缀 替换
            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), BRI_1},//BRI   前缀 替换
            {"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), FXO_1},//FXO --77 不输   2005（FXS）
            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), E1},//E1     前缀 替换
            {"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), SIPTrunk},//SIP  --55 REGISTER
            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), ACCOUNTTRUNK},
//            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
    };

    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        //SIP_REGISTER,SPS,ACCOUNT
        if (methodName.contains("_01_03")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 3001, "3000", DEVICE_ASSIST_1,"3001<3001>",SIPTrunk},//SIP  --55 REGISTER
                    {"99", 2000, "1000", DEVICE_ASSIST_2,"2000<2000>",SPS},//sps   前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3,"4000<4000>",ACCOUNTTRUNK},
            };
        }
        //BRI,E1
        if(methodName.contains("_04_05") || methodName.contains("_10_11") || methodName.contains("_17_18") || methodName.contains("_24_25") ||
                methodName.contains("_31_32")){
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"88", 2000, "1000", DEVICE_ASSIST_2,"2000<2000>",BRI_1},//BRI   前缀 替换
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>",E1}
            };
        }
        //FXO,GSM
        if(methodName.contains("_06_07") || methodName.contains("_12_13") || methodName.contains("_19_20") || methodName.contains("_26_27") ||
            methodName.contains("_33_34") || methodName.contains("_36_37")){
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 2000, "2005", DEVICE_ASSIST_2, "2000<2000>", FXO_1},//FXO --77 不输   2005（FXS）
                    {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+"<"+DEVICE_ASSIST_GSM+">",GSM}
            };
        }

        //SIP_REGEIST
        if (methodName.contains("_08") || methodName.contains("_14") ||methodName.contains("_21") || methodName.contains("_28") ||
            methodName.contains("_35") || methodName.contains("_45") || methodName.contains("_46")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 3001, "3000", DEVICE_ASSIST_1,"3001<3001>",SIPTrunk},//SIP  --55 REGISTER
            };
        }

        //ACCOUNT
        if (methodName.contains("_09") || methodName.contains("_38")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"44", 4000, "1000", DEVICE_ASSIST_3,"4000<4000>",ACCOUNTTRUNK},
            };
        }

        //SPS,ACCOUNT
        if (methodName.contains("_15_16") || methodName.contains("_22_23") || methodName.contains("_29_30")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "1000", DEVICE_ASSIST_2,"2000<2000>",SPS},//sps   前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3,"4000<4000>",ACCOUNTTRUNK},
            };
        }

        //SPS
        if (methodName.contains("_43") || methodName.contains("_42") || methodName.contains("_47") || methodName.contains("_44")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "1000", DEVICE_ASSIST_2,"2000<2000>",SPS},//sps   前缀 替换
            };
        }
        return routes;
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("1.通过sps外线拨打990123456789\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_01_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("2.通过sps外线拨打99+123456\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_02_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("3.通过sps外线拨打99s\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_03_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("4.通过sps外线拨打99abcdefghijklmno\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_04_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("5.通过sps外线拨打99056789+\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_05_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("6.新建呼入路由Inbound1，DID Pattern选择“DID Pattern”，添加规则0123456789；Trunk选择sps外线，呼入目的地为分机B-1001\n" +
            "\t通过sps外线拨打990123456789\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P2"})
    public void testIRDID_06_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t7.通过sps外线拨打99+123456\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P2"})
    public void testIRDID_07_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t8.通过sps外线拨打99s\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_08_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t9.通过sps外线拨打99abcdefghijklmno\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_09_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t10.通过sps外线拨打99056789+\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_10_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t11.通过sps外线拨打99123456\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_11_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由In1,DID Pattern选择“DID Pattern\",添加规则： .\n" +
            "\t12.通过sps外线拨打99123abc\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_12_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t13.通过sps外线拨打99123\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_13_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t14.通过sps外线拨打991234abc\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_14_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t15.通过sps外线拨打9912\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_15_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t16.通过sps外线拨打99+059109129921\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P2"})
    public void testIRDID_16_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t17.通过sps外线拨打99+059118237877\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_17_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t18.通过sps外线拨打99+059209129921\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_18_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t19.通过sps外线拨打99+059100029955\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_19_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t20.通过sps外线拨打99+059100219988\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_20_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t21.通过sps外线拨打99+059109129999\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_21_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t22.通过sps外线拨打99+05910912992\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_22_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地HangUp\n" +
            "\t23.通过sps外线拨打9913001\n" +
            "\t\t呼入失败，通话被直接挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_23_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Extension Voicemail-分机1001\n" +
            "\t24.通过sps外线拨打9913001\n" +
            "\t\t进入到分机1001的语音留言，通话15s后挂断；登录1001查看新增一条语音留言；Name正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_24_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Conference-Conference0\n" +
            "\t25.通过sps外线拨打9913001\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_25_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Queue-Queue0\n" +
            "\t26.通过sps外线拨打9913001\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_26_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地External Number：prefix : 1 ,号码：3001\n" +
            "\t27.通过sps外线拨打9913001\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_27_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Out1\n" +
            "\t28.通过sps外线拨打9913001\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_28_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Play Greeting then HangUp 选择prompt1，播放1遍\n" +
            "\t29.通过sps外线拨打9913001\n" +
            "\t\tasterisk后台查看播放提示音文件prompt1,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","InboundRoute", "DIDPattern","SPS","P3"})
    public void testIRDID_29_DIDPattern(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t30.通过sps外线拨打991001\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P2"})
    public void testIRDID_30_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t31.通过sps外线拨打991002\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P2"})
    public void testIRDID_31_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t32.通过sps外线拨打991020\n" +
            "\t\t辅助2的2000分机响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_32_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t33.通过BRI外线拨打881003\n" +
            "\t\t分机1003响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_33_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t34.通过E1外线拨打661004\n" +
            "\t\t分机1004响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_34_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t35.通过Account外线拨打441000\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_35_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t36.通过sps外线拨打99+12310010591012\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_36_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t37.通过sps外线拨打99+1231003059199999\n" +
            "\t\t分机1003响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_37_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t38.通过sps外线拨打99+12310020591012\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_38_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t39.通过sps外线拨打99123100105910123\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_39_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t40.通过sps外线拨打99+1231001059100222\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_40_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t41.通过sps外线拨打99+12310010590012\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_41_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t42.通过sps外线拨打99+12310010591011\n" +
            "\t\t打不通，通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_42_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t43.通过sps外线拨打991002a\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_43_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t44.通过sps外线拨打991002\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_44_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t45.通过sps外线拨打9910041234567890\n" +
            "\t\t分机1004响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_45_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t46.通过sps外线拨打99100333\n" +
            "\t\t打不通，通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_46_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n" +
            "\t47.通过sps外线拨打991003\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_47_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n" +
            "\t48.通过sps外线拨打99999999\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_48_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择RingGroup-RingGroup0\n" +
            "\t49.通过sps外线拨打991000\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_49_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择HangUp\n" +
            "\t50.通过sps外线拨打991000\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_50_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension Voicemail-分机1000\n" +
            "\t51.通过sps外线拨打991001\n" +
            "\t\t进入到分机1000的语音留言，通话15s后挂断；登录1000查看新增一条语音留言；Name正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_51_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择IVR-IVR0\n" +
            "\t52.通过sps外线拨打991002\n" +
            "\t\t进入到IVR0，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_52_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Conference-Conference0\n" +
            "\t53.通过sps外线拨打991003\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_53_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Queue-Queue0\n" +
            "\t54.通过sps外线拨打991003\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_54_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择External Number：prefix : 1 ,号码：3001\n" +
            "\t55.通过sps外线拨打991004\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_55_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Out1\n" +
            "\t56.通过sps外线拨打9913001\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_56_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；Play Greeting then HangUp 选择prompt2，播放1遍\n" +
            "\t57.通过sps外线拨打9913001\n" +
            "\t\tasterisk后台查看播放提示音文件prompt2,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDPatterntoExtensions","SPS","P3"})
    public void testIRDID_57_MatchDIDToExt(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t58.通过sps外线拨打995503300\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P2"})
    public void testIRDID_58_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t59.通过sps外线拨打995503304\n" +
            "\t\t分机1004响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P2"})
    public void testIRDID_59_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t60.通过bri外线拨打885503301\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_60_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t61.通过E1外线拨打665503302\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_61_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t62.通过sps外线拨打995503305\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_62_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Hang up\n" +
            "\t63.通过sps外线拨打995503300\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_63_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension-分机1002\n" +
            "\t64.通过sps外线拨打995503300\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_64_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension Voicemail-分机1000\n" +
            "\t65.通过sps外线拨打995503300\n" +
            "\t\t进入到分机1000的语音留言，通话15s，挂断；分机1000登录查看新增一条语音留言；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_65_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择IVR-IVR0\n" +
            "\t66.通过sps外线拨打995503300\n" +
            "\t\t进入到IVR0，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_66_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择RingGroup-RingGroup0\n" +
            "\t67.通过sps外线拨打995503300\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_67_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Queue-Queue0\n" +
            "\t68.通过sps外线拨打995503300\n" +
            "\t\t呼入到Queue0，坐席1000、1001、1003、1004同时响铃，1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_68_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Conference-Conference0\n" +
            "\t69.通过sps外线拨打995503300\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_69_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择External Number ,prefix : 1 ,号码：3001\n" +
            "\t70.通过sps外线拨打995503300\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_70_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Outbound Route-Out1\n" +
            "\t71.通过sps外线拨打995503300\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_71_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Play Greeting then Hang Up选择prompt3，播放1遍\n" +
            "\t72.通过sps外线拨打995503300\n" +
            "\t\tasterisk后台查看播放提示音文件prompt3,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","InboundRoute-DIDPattern","MatchDIDRangetoExtensionRange","SPS","P3"})
    public void testIRDID_72_MatchDIDToExtRange(){
        prerequisite();

        step("1:login with admin,trunk: "+SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 991000" +",[trunk] "+SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }
}