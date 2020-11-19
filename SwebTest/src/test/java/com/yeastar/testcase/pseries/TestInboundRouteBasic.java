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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.yeastar.untils.CDRObject.*;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test conference
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteBasic extends TestCaseBaseNew {
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    APIUtil apiUtil = new APIUtil();
    List<String> trunk9 = new ArrayList<>();
    private String EXTENSION_1000_HUNGUP = "test A<1000> hung up";

    TestInboundRouteBasic(){
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
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", FXS_1).replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

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
            {"44", 4000, "4444", DEVICE_ASSIST_3, "4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), ACCOUNTTRUNK},
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
                    {"44", 4000, "4444", DEVICE_ASSIST_3,"4000<4000>",ACCOUNTTRUNK},
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
                    {"44", 4000, "4444", DEVICE_ASSIST_3,"4000<4000>",ACCOUNTTRUNK},
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
        if (methodName.contains("_39_") || methodName.contains("_43") || methodName.contains("_42") || methodName.contains("_47") || methodName.contains("_44")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "1000", DEVICE_ASSIST_2,"2000<2000>",SPS},//sps   前缀 替换
            };
        }
        return routes;
    }

//    @BeforeMethod(alwaysRun = true)
//    public void initEnv(){
//        step("=========== init env start ==========");
//        prerequisite();
//        step("=========== init env end ==========");
//    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t1.通过sip外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，主叫挂断；检测cdr\n" +
            "\t2.通过sps外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，被叫挂断；检测cdr\n" +
            "\t3.通过Account外线呼入到分机1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P1", "Basic","Trunk","InboundRoute","Extension","SPS","SIP_REGISTER","SIP_ACCOUNT"}, dataProvider = "routes")
    public void testIRB_01_03_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);

        step("[主叫挂断]");
        if (caller==3001) {
            pjsip.Pj_hangupCall(caller);
        }else{
            pjsip.Pj_hangupCall(1000);
        }

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        if(caller==3001){
            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                    .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));
        }else {
            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                    .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP, trunk, "", "Inbound"));
        }
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t4.通过BRI外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr\n" +
            "\t5.通过E1外线呼入到分机1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P1", "Basic","Trunk","InboundRoute","Extension","BRI","E1"}, dataProvider = "routes")
    public void testIRB_04_05_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,trunk+"线路不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1001").editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,20),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,20),TALKING);

        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t6.通过FXO外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr\n" +
            "\t7.通过GSM外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P1", "Basic","Trunk","InboundRoute","Extension","GSM","FXO"}, dataProvider = "routes")
    public void testIRB_06_07_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) throws IOException, JSchException {
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM或FXO线路不测试！");
        }
//        if(DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") ||  DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")){
//            Assert.assertTrue(false,"GSM 线路不测试！");
//        }
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 120), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 60), TALKING);

        //GSM 通话时长必须大于 60s
        if(trunk.trim().equalsIgnoreCase(GSM)){
            sleep(WaitUntils.SHORT_WAIT*30);
        }
        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020" +
            "8.通过sip外线呼入到分机1020\n" +
            "\t辅助2对应的分机2000响铃，接听，主叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2", "Trunk","InboundRoute","FXS","_08","SIP_REGISTER"}, dataProvider = "routes")
    public void testIRB_08_FXS_1callerhangup(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");

        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020" +
            "8.通过sip外线呼入到分机1020\n" +
            "\t辅助2对应的分机2000响铃，接听，主叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2", "Trunk","InboundRoute","FXS","_08","SPS"})
    public void testIRB_08_FXS_2CalleeHangup(){
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");

        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: ");
        auto.loginPage().loginWithAdmin();

        step("2:辅助2通过2001拨打99999呼入");
        pjsip.Pj_Make_Call_No_Answer(2001, "999999", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), "1020 1020<1020> hung up",SPS,"","Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t9.通过Account外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","FXS","SIP_ACCOUNT"}, dataProvider = "routes")
    public void testIRB_09_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t10.通过BRI外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t11.通过E1外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","FXS","BRI","E1"}, dataProvider = "routes")
    public void testIRB_10_11_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t12.通过FXO外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t13.通过GSM外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t拨打电话时注意辅助2的主叫不能是2000分机")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","FXS","FXO","GSM"}, dataProvider = "routes")
    public void testIRB_12_13_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        if(FXS_1.trim().equalsIgnoreCase("null") || FXS_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXS 分机不存在！");
        }
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO或GSM 线路不测试！");
        }
//        if(DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") ||  DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")){
//            Assert.assertTrue(false,"GSM 线路 不通！");
//        }
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 120), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 60), TALKING);

        //GSM 通话时长必须大于 60s
        if(trunk.trim().equalsIgnoreCase(GSM)){
            sleep(WaitUntils.SHORT_WAIT*30);
        }
        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "14.通过sip外线呼入\n" +
            "\t通话被自动挂断;检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","HangUp"}, dataProvider = "routes",enabled = true)
    public void testIRB_14_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,100),HUNGUP);

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t15.通过sps外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t16.通过Account外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","HangUp"}, dataProvider = "routes")
    public void testIRB_15_16_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,100),HUNGUP);
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t17.通过BRI外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t18.通过E1外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","HangUp"}, dataProvider = "routes")
    public void testIRB_17_18_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 或E1 线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,100),HUNGUP);
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t19通过FXO外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t20.通过GSM外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","HangUp"}, dataProvider = "routes")
    public void testIRB_19_20_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 或GSM 线路 不测试！");
        }

        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        //FXO 线路不判断
        if(trunk.trim().equalsIgnoreCase(GSM)) {
            step("[通话状态校验]");
            Assert.assertEquals(getExtensionStatus(caller, HUNGUP, 100), HUNGUP, "[caller] " + caller);
        }
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "21.通过sip外线呼入\n" +
            "\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","Voicemail"}, dataProvider = "routes")
    public void testIRB_21_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        sleep(20*1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains(caller+""),"没有生成语音留言");

        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t22.通过sps外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t23.通过Account外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","Voicemail"}, dataProvider = "routes")
    public void testIRB_22_23_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        sleep(20*1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains(caller+""),"没有生成语音留言");

        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t24.通过BRI外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t25.通过E1外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","Voicemail"}, dataProvider = "routes")
    public void testIRB_24_25_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 或E1线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        sleep(20*1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains(caller+""),"没有生成语音留言");

        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t26.通过FXO外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t27.通过GSM外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","Voicemail"}, dataProvider = "routes")
    public void testIRB_26_27_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 或GSM线路 不测试！");
        }


        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        //GSM 通话时长必须大于 60s
        if(trunk.trim().equalsIgnoreCase(GSM)){
            sleep(WaitUntils.SHORT_WAIT*30);
        }else {
            sleep(20 * 1000);
        }
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);

        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        if(trunk.trim().equalsIgnoreCase(GSM)){
            Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains(DEVICE_ASSIST_GSM),"没有生成语音留言");
        }else{
            Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains(caller+""),"没有生成语音留言");
        }

        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890\n" +
            "\t28.通过sip外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","ExternalNumber"}, dataProvider = "routes")
    public void testIRB_28_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"2\",\"def_dest_value\":\"1234567890\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "21234567890", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SPS,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001\n" +
            "\t29.通过sps外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t30.通过Account外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","ExternalNumber"}, dataProvider = "routes")
    public void testIRB_29_30_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "13001", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001\n" +
            "\t31.通过BRI外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t32.通过E1外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("Bug CDR HangUp exception")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","ExternalNumber"}, dataProvider = "routes")
    public void testIRB_31_32_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 或E1线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(caller,3001,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "13001", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t33.通过FXO外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t34.通过GSM外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("Bug CDR HangUp exception")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","ExternalNumber"}, dataProvider = "routes")
    public void testIRB_33_34_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        if(trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不通！");
        }
//        if(DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") ||  DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")){
//            Assert.assertTrue(false,"GSM 线路 不通！");
//        }
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);

        //GSM 通话时长必须大于 60s
        if(trunk.trim().equalsIgnoreCase(GSM)){
            sleep(WaitUntils.SHORT_WAIT*30);
        }

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "13001", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t35.通过sip外线呼入\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","OutboundRoute"}, dataProvider = "routes")
    public void testIRB_35_OutBoundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "3000", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SPS,"Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t36.辅助2分机2001通过GSM外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_36_OutBoundRoute(){
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: " );
        auto.loginPage().loginWithAdmin();

        step("2:辅助2分机2001通过GSM外线呼入");
        pjsip.Pj_Make_Call_No_Answer(2001, 33 + DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,40),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        sleep(WaitUntils.SHORT_WAIT*30);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(DEVICE_ASSIST_GSM+"<"+DEVICE_ASSIST_GSM+">", 7+DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), "2001<2001> hung up",GSM,SPS,"Outbound"));

        softAssertPlus.assertAll();

    }


    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t37.辅助2分机2001拨打2010通过FXO外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_37_OutBoundRoute(){
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }

        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin" );
        auto.loginPage().loginWithAdmin();

        step("2:辅助2分机2001拨打2010通过FXO外线呼入 ");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005" , DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2001<2001>", "13000", STATUS.ANSWER.toString(), "2001<2001> hung up",FXO_1,SPS,"Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t38.通过Account外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t未实测跑不通过时Q一下")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","OutboundRoute"}, dataProvider = "routes")
    public void testIRB_38_OutBoundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,30),RING);
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "4444", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,SPS,"Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t39.通过sps外线拨打9913001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_39_OutBoundRoute(){
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 9913001");
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t40.通过BRI外线拨打8813001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_40_OutBoundRoute_BRI(){
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI 线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin,trunk: " );
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000"+",[callee] 8813001");
        pjsip.Pj_Make_Call_No_Answer(2000, "8813001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up",BRI_1,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();

    }


    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t41.通过E1外线拨打6613001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_41_OutBoundRoute_E1(){
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1线路 不测试！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000,[callee] 6613001");
        pjsip.Pj_Make_Call_No_Answer(2000, "6613001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001,RING,30),RING);
        pjsip.Pj_Answer_Call(3001,false);
        Assert.assertEquals(getExtensionStatus(3001,TALKING,30),TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up",E1,SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,PlayGreetingthenHangUp")
    @Description("编辑呼入路由In1的目的地为Play Greeting then Hang Up，选择prompt1,1遍\n" +
            "\t42.通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印播放语音文件prompt1 一遍后，主叫被挂断；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","PlayGreetingthenHangUp"}, dataProvider = "routes")
    public void testIRB_42_PlayGreetign(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"prompt1.wav\"")).
                apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_1)).start();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 301){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,50),HUNGUP);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,PlayGreetingthenHangUp")
    @Description("编辑呼入路由In1的目的地为Play Greeting then Hang Up，选择prompt2,5遍\n" +
            "\t43.通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印播放语音文件prompt2 五遍后，主叫被挂断；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","PlayGreetingthenHangUp"}, dataProvider = "routes")
    public void testIRB_43_PlayGreetign(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"5\",\"def_dest_value\":\"prompt2.wav\"")).apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,PROMPT_2)).start();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp <= 600){
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

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FaxToEmail")
    @Description("44.编辑呼入路由In1的目的地为Fax to Email-分机1001\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印“1001@fax_to_email”挂断通话；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","FaxToEmail"}, dataProvider = "routes")
    public void testIRB_44_FaxToEmail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为Fax to Email-分机1001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"fax_to_email\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id)).apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList,FAX_TO_EMAIL_1001)).start();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 301){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Move")
    @Description("45.编辑呼入路由In1的目的地为分机1000；\n" +
            "新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip;\n" +
            "\t通过sip外线呼入\n" +
            "\t\t分机1000响铃\n" +
            "\t\t\t呼入路由List调整InRoute2在In1前面；通过sip外线呼入\n" +
            "\t\t\t\t分机1000响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","Move"}, dataProvider = "routes")
    public void testIRB_45_Move(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000 ; 新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                createInbound("InRoute2", trunk1, "Extension", "1001").
                apply();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);//sip
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(caller);

        step("呼入路由List调整InRoute2在In1前面");//{"id":1612,"pos_from":2,"pos_to":1}
        apiUtil.editInbound("InRoute2",String.format("\"pos_from\":2,\"pos_to\":1")).apply();


        step("[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(caller);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "test A<1000>", STATUS.ANSWER.toString(), cdrCaller+" hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute")
    @Description("46.编辑呼入路由In1的目的地为分机1000，Trunk只选择sip\n" +
            "\t分别通过sip、account外线呼入\n" +
            "\t\t通过sip外线呼入时分机1000响铃，通过account外线呼入失败；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000，Trunk全选")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute"}, dataProvider = "routes")
    public void testIRB_46_InboundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000，Trunk只选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk1, "Extension", "1000").apply();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(1000);

        step("通过account外线呼入失败");
        step("[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(2000, "441000", DEVICE_ASSIST_2, false);

        int result =getExtensionStatus(1000,RING,10);
        log.debug("[result] "+result);
        Assert.assertTrue((result == HUNGUP) ||  (result == IDLE));

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple(cdrCaller, "test A<1000>", STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute")
    @Description("47.编辑呼入路由In1的目的地为[None]，全选外线\n" +
            "\t通过sps外线呼入 99999\n" +
            "\t\t通话被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk","InboundRoute","None"}, dataProvider = "routes")
    public void testIRB_47_InboundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk){
        prerequisite();
        step("编辑呼入路由In1的目的地为[None]，全选外线");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"\"")).apply();

        step("1:login with admin,trunk: " +trunk);
        auto.loginPage().loginWithAdmin();

        step("通过account外线呼入失败");
        step("[caller] "+caller+",[callee] "+routePrefix + callee +",[trunk] "+trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, "999999", DEVICE_ASSIST_2, false);

        int result =getExtensionStatus(caller,HUNGUP,30);
        log.debug("[result] "+result);
        Assert.assertTrue((result == HUNGUP) ||  (result == IDLE));

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Delete")
    @Description("48.单条删除In1\n" +
            "\t检查列表In1被删除成功\n" +
            "\t\t添加呼入路由In1，选择所有外线，分机目的地设置为分机1000，其它默认\n" +
            "\t\t\t恢复初始化环境")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_48_Delete(){
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control,HomePage.Menu_Level_2.call_control_tree_inbound_routes);

        step("单条删除");
        auto.inboundRoute().deleDataByDeleImage("In1").clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Name");
        softAssertPlus.assertThat(list).doesNotContain("In1");
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Delete")
    @Description("49.再次创建InboundRoute2,选择sip外线\n" +
            "\t批量选择删除InboundRoute2\n" +
            "\t\t检查列表InboundRoute2被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk","InboundRoute","OutboundRoute"})
    public void testIRB_49_Delete(){
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000 ; 新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                createInbound("InRoute2", trunk1, "Extension", "1001").
                apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control,HomePage.Menu_Level_2.call_control_tree_inbound_routes);

        step("2:批量删除");
        auto.inboundRoute().deleteAllInboundRoutes().clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list.size()).isEqualTo(0);
        softAssertPlus.assertAll();

    }
}