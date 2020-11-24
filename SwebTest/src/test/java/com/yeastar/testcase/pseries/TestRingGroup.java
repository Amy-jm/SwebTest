package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIObject.MenuOptionObject;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestRingGroup extends TestCaseBaseNew {

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean runRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;

    private final String IVR_GREETING_DIAL_EXT = "ivr-greeting-dial-ext.slin";
    private final String PROMPT_1 = "prompt1.slin";
    private final String ringGroupName0 = "RingGroup0";//6300
    private final String ringGroupName1 = "RingGroup1";//6301
    private final String ivrName0 = "IVR0";//6301
    private final String queueName0 = "Queue0";//6400
    private final String conferenceName0 = "Conference0";//6500

    private final String ringGroupNum0 = "6300";
    private final String ringGroupNum1 = "6301";
    private final String ivrNum0 = "6200";
    private final String queueNum0 = "6400";
    private final String conferenceNum0 = "6500";

    private final String cdrRingGroup0 = String.format("RingGroup %s<%s>", ringGroupName0, ringGroupNum0);
    private final String cdrRingGroup1 = String.format("RingGroup %s<%s>", ringGroupName1, ringGroupNum1);
    private final String cdrQueue0 = String.format("Queue %s<%s>", queueName0, queueNum0);
    private final String cdrIvr0 = String.format("IVR %s<%s>", ivrName0, ivrNum0);
    private final String ext_gsm = String.format("%s<%s>", DEVICE_ASSIST_GSM, DEVICE_ASSIST_GSM);

    private final String ext_1000 = "test A<1000>";
    private final String ext_1001 = "test2 B<1001>";
    private final String ext_1002 = "testta C<1002>";
    private final String ext_1003 = "testa D<1003>";
    private final String ext_1004 = "t estX<1004>";
    private final String ext_1020 = "1020 1020<1020>";
    private final String ext_2000 = "2000<2000>";
    private final String ext_3001 = "3001<3001>";
    private final String ext_4000 = "4000<4000>";


    APIUtil apiUtil = new APIUtil();
    ArrayList<String> queueStaticMembers;
    ArrayList<String> queueDynamicMembers;
    ArrayList<String> queueMembers;
    ArrayList<String> ringGroupMembers0;
    ArrayList<String> ringGroupMembers1;
    ArrayList<String> ringGroupExtGroupMembers0;
    ArrayList<String> ringGroupExtGroupMembers1;
    ArrayList<String> conferenceMember;

    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private void resetRingGroup1(){
        ringGroupMembers1 = new ArrayList<>(); 
        ringGroupExtGroupMembers1 = new ArrayList<>();


//        ringGroupMembers1.add("1000");
        ringGroupExtGroupMembers1.add("ExGroup1");
        ringGroupMembers1.add("1001");
        ringGroupMembers1.add("1002");
        ringGroupMembers1.add("1003");


        JSONArray jsonArray = new JSONArray();

        MenuOptionObject menuOptionObject = apiUtil.getRingGroupMenuOption();
        for (String extGroup : ringGroupExtGroupMembers1){
            for (MenuOptionObject.MemberList extensionGroupObject : menuOptionObject.extGroupOptions) {
                if(extGroup.equals(extensionGroupObject.text)){
                    JSONObject a = new JSONObject();
                    a.put("text",extensionGroupObject.text);
                    a.put("value",extensionGroupObject.value);
                    a.put("type","ext_group");
                    jsonArray.put(a);
                }
            }
        }

        for (String ext : ringGroupMembers1){
            for (MenuOptionObject.MemberList extensionObject: menuOptionObject.extensionOptions) {
                if (ext.equals(extensionObject.text2)){
                    JSONObject a = new JSONObject();
                    a.put("text",extensionObject.text);
                    a.put("text2",extensionObject.text2);
                    a.put("value",extensionObject.value);
                    a.put("type","extension");
                    jsonArray.put(a);
                }
            }
        }


        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":60," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"",jsonArray.toString(),apiUtil.getExtensionSummary("1004").id)).apply();
    }

    private String editRingGroup1Extension(List<String> extensions){

        JSONArray jsonArray = new JSONArray();

        MenuOptionObject menuOptionObject = apiUtil.getRingGroupMenuOption();
        for (String ext : extensions){

            if (ext.startsWith("group_")){
                String m_groupName = ext.replace("group_","");
                for (MenuOptionObject.MemberList extensionGroupObject : menuOptionObject.extGroupOptions) {
                    if(m_groupName.equals(extensionGroupObject.text)){
                        JSONObject a = new JSONObject();
                        a.put("text",extensionGroupObject.text);
                        a.put("value",extensionGroupObject.value);
                        a.put("type","ext_group");
                        jsonArray.put(a);
                    }
                }

            }else{
                for (MenuOptionObject.MemberList extensionObject: menuOptionObject.extensionOptions) {
                    if (ext.equals(extensionObject.text2)){
                        JSONObject a = new JSONObject();
                        a.put("text",extensionObject.text);
                        a.put("text2",extensionObject.text2);
                        a.put("value",extensionObject.value);
                        a.put("type","extension");
                        jsonArray.put(a);
                    }
                }
            }
        }
        return jsonArray.toString();
    }

    public void prerequisite() {
        //local debug
        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();
        }

        long startTime = System.currentTimeMillis();
        if (runRecoveryEnvFlag) {

            List<String> trunk1 = new ArrayList<>();
            List<String> trunk2 = new ArrayList<>();
            List<String> trunk3 = new ArrayList<>();
            List<String> trunk4 = new ArrayList<>();
            List<String> trunk5 = new ArrayList<>();
            List<String> trunk6 = new ArrayList<>();
            List<String> trunk7 = new ArrayList<>();
            List<String> trunk8 = new ArrayList<>();
            List<String> trunk9 = new ArrayList<>();

            trunk1.add(SIPTrunk);
            trunk2.add(SPS);
            trunk3.add(ACCOUNTTRUNK);
            trunk4.add(FXO_1);
            trunk5.add(BRI_1);
            trunk6.add(E1);
            trunk7.add(GSM);
            trunk8.add(GSM);
            trunk9.add(SPS);
            trunk9.add(BRI_1);
            trunk9.add(FXO_1);
            trunk9.add(E1);
            trunk9.add(SIPTrunk);
            trunk9.add(ACCOUNTTRUNK);
            trunk9.add(GSM);

            List<String> extensionNum = new ArrayList<>();
            List<String> extensionNumA = new ArrayList<>();

            queueStaticMembers = new ArrayList<>();
            queueDynamicMembers = new ArrayList<>();
            queueMembers = new ArrayList<>();
            ringGroupMembers0 = new ArrayList<>();
            ringGroupMembers1 = new ArrayList<>();
            conferenceMember = new ArrayList<>();

            extensionNum.add("0");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNumA.add("1000");


            step("创建分机1000-1010");
            apiUtil.deleteAllExtension().apply();
            sleep(WaitUntils.SHORT_WAIT);
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", FXS_1).replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建分机组 ExGroup1/ExGroup2");
            List<String> extensionExGroup1 = new ArrayList<>();
            List<String> extensionExGroup2 = new ArrayList<>();
            extensionExGroup1.add("1000");
            extensionExGroup1.add("1001");

            extensionExGroup2.add("1000");
            extensionExGroup2.add("1002");

            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
                    createExtensionGroup("ExGroup1",extensionExGroup1).
                    createExtensionGroup("ExGroup2",extensionExGroup2).apply();

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS)
                    .createSIPTrunk(reqDataCreateSPS_2);

            step("创建IVR 6301");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
            apiUtil.deleteAllIVR().createIVR(ivrNum0, ivrName0, pressKeyObjects_0);

            step("创建响铃组6300 6301");
            ringGroupMembers0.add("1003");
            ringGroupMembers0.add("group_ExGroup1");
            ringGroupMembers1.add("group_ExGroup1");
            ringGroupMembers1.add("1001");
            ringGroupMembers1.add("1002");
            ringGroupMembers1.add("1003");

            ExtensionObject extensionObject = apiUtil.getExtensionSummary("1000");
            ExtensionObject extensionObject2 = apiUtil.getExtensionSummary("1004");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName0, ringGroupNum0, ringGroupMembers0)
                    .createRingGroup(ringGroupName1, ringGroupNum1, ringGroupMembers1)
                    .editRingGroup(ringGroupNum0, String.format("\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", extensionObject.id))
                    .editRingGroup(ringGroupNum1, String.format("\"ring_strategy\":\"ring_all\",\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", extensionObject2.id));

            step("创建队列6400");
            queueStaticMembers.add("1000");
            queueStaticMembers.add("1001");
            queueDynamicMembers.add("1003");
            queueDynamicMembers.add("1004");
            apiUtil.deleteAllQueue().createQueue(queueName0, queueNum0, queueDynamicMembers, queueStaticMembers, null)
            .editQueue(queueNum0,String.format("\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\",\"max_wait_time\":60," +
                    "\"press_key\":\"0\",\"key_dest\":\"extension\",\"key_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1001").id));

            step("创建会议室");
            apiUtil.deleteAllConference().createConference(conferenceName0, conferenceNum0, conferenceMember);

            step("创建呼入路由InRoute3,目的地到响铃组6300");
            apiUtil.deleteAllInbound()
                    .createInbound("In1", trunk9, "ring_group", ringGroupNum1);

            step("创建呼出路由");
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
            //todo role_id 需要获取
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW).editExtension("0","\"role_id\":1").apply();
            runRecoveryEnvFlag = registerAllExtensions();

            step("1003 1004拨号*76400，登录Queue0");
            pjsip.Pj_Make_Call_No_Answer(1003,  "*76400", DEVICE_IP_LAN, false);
            pjsip.Pj_Make_Call_No_Answer(1004,  "*76400", DEVICE_IP_LAN, false);
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @AfterClass
    public void A_ringGroupAfterClass(){
        log.debug("[ring group after Class]  .");
        if (runRecoveryEnvFlag) {

            step("[环境恢复]： 响铃组只保留6300");
            ringGroupMembers0 = new ArrayList<>();
            ringGroupMembers0.add("1003");
            ringGroupMembers0.add("group_ExGroup1");

            ExtensionObject extensionObject = apiUtil.getExtensionSummary("1000");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName0, ringGroupNum0, ringGroupMembers0)
                    .editRingGroup(ringGroupNum0, String.format("\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", extensionObject.id))
            .editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();

        }
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("1.通过外线呼入到RingGroup1\n" +
            "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","RingGroup", "Basic","Trunk", "RingStategry", "RingTimeout", "InboundRoute",  "SIP_REGISTER","PSeries","Cloud","K2"})
    public void testRG01_CallRingGroup1() {

        prerequisite();
        resetRingGroup1();
        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1000应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话校验]:1000分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        resetRingGroup1();

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1000, CDRObject.STATUS.ANSWER.toString(), ext_1000+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("1.通过外线呼入到RingGroup1\n" +
            "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "Basic","Trunk", "RingStategry", "RingTimeout", "InboundRoute", "SIP_REGISTER","PSeries","Cloud","K2"})
    public void testRG02_CallRingGroup2() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1003应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验]:1003分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1003);
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1003, CDRObject.STATUS.ANSWER.toString(), ext_1003+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
        
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1","RingGroup", "Basic","Trunk", "RingStategry", "RingTimeout", "InboundRoute", "SPS","PSeries","Cloud","K2"})
    public void testRG03_CallRingGroup3() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SPS外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000, "996301", DEVICE_ASSIST_2, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("60s后分机1000、1001、1002、1003停止响铃");
        sleep(60000);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 5)).as("[通话校验]:1000分机10s内响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(HUNGUP);

        step("Failover到分机1004正常响铃、接听、挂断；cdr正常；");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        pjsip.Pj_Answer_Call(1004, false);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 10)).as("[通话校验]:1004分机接听").isEqualTo(TALKING);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1004, CDRObject.STATUS.ANSWER.toString(), ext_1004 +" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute","SIP_REGISTER","PSeries","Cloud","K2"})
    public void testRG04_CallRingGroup4() {

        prerequisite();

        step("编辑RingGroup1,RingTimeout时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);


        step("分机1000,1001,1002,1003同时响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        int result1 =getExtensionStatus(1004,HUNGUP,1);
        Assert.assertTrue((result1 ==HUNGUP) ||  (result1 ==IDLE));
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        sleep(10000);

        step("分机1004响铃.其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 10)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 5)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        step("分机1004应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1004, false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1004, TALKING, 5)).as("[通话校验]:1000分机5s内接听成功").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1,RingTimeout时间[恢复]为60s");
        resetRingGroup1();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1004, CDRObject.STATUS.ANSWER.toString(), ext_1004 +" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Basic","Trunk", "RingStategry", "RingTimeout", "InboundRoute","PSeries","Cloud","K2"})
    public void testRG05_CallRingGroup5() {
        prerequisite();

        step("编辑RingGroup1,RingTimeout时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":10").apply();

        step("网页admin登录,进入ringgroup界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ring_group);

        step("通过SIP外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        int result1 =getExtensionStatus(1004,HUNGUP,1);
        Assert.assertTrue((result1 ==HUNGUP) ||  (result1 ==IDLE));
//        sleep(3000);

        step("分机1002在响铃5s后应答，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1002, false);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1,RingTimeout时间[恢复]为60s");
        resetRingGroup1();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1002, CDRObject.STATUS.ANSWER.toString(), ext_1002+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("通过内部分机呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Basic","Extension", "RingStategry", "RingTimeout", "InboundRoute","PSeries","Cloud","K2"})
    public void testRG06_CallRingGroup6() {
        prerequisite();

        step("编辑RingGroup1,RingTimeout时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("内部分机1001线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(1001, ringGroupNum1, DEVICE_IP_LAN, false);

        step("分机1000,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
//        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        int result1 =getExtensionStatus(1004,HUNGUP,1);
        Assert.assertTrue((result1 ==HUNGUP) ||  (result1 ==IDLE));

        step("分机1003在响铃3s后应答，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验]:1003分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1003);
        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1,RingTimeout时间[恢复]为60s");
        resetRingGroup1();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_1001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", "", "", CDRObject.COMMUNICATION_TYPE.INTERNAL.toString()))
                .contains(tuple(ext_1001, ext_1003, CDRObject.STATUS.ANSWER.toString(), ext_1003+" hung up", "", "", CDRObject.COMMUNICATION_TYPE.INTERNAL.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Trunk,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Trunk","InboundRoute", "SIP_ACCOUNT","PSeries","Cloud","K2"})
    public void testRG07_TrunkInboundRoute1_Account() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过Account外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1001应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1001, false);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验]:1001分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_4000, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", ACCOUNTTRUNK, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_4000, ext_1001, CDRObject.STATUS.ANSWER.toString(), ext_1001+" hung up", ACCOUNTTRUNK, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Trunk,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Trunk","InboundRoute", "FXO","PSeries"})
    public void testRG08_TrunkInboundRoute2_FXO() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过FXO外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000, "2005", DEVICE_ASSIST_2, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1002应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1002, false);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", FXO_1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1002, CDRObject.STATUS.ANSWER.toString(), ext_1002+" hung up", FXO_1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Trunk,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Trunk","InboundRoute", "BRI","PSeries"})
    public void testRG09_TrunkInboundRoute3_BRI() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过BRI外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000, "886301", DEVICE_ASSIST_2, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1003应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1003);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", BRI_1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1003, CDRObject.STATUS.ANSWER.toString(), ext_1003+" hung up", BRI_1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Trunk,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Trunk","InboundRoute", "E1","PSeries"})
    public void testRG10_TrunkInboundRoute4_E1() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过E1外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000, "666301", DEVICE_ASSIST_2, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1003应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1003);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", E1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1003, CDRObject.STATUS.ANSWER.toString(), ext_1003+" hung up", E1, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Trunk,InboundRoute")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Trunk","InboundRoute", "GSM","PSeries"})
    public void testRG11_TrunkInboundRoute5_GSM() {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过GSM外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000, "33" + DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
//        pjsip.Pj_Make_Call_No_Answer(2000, "996301", DEVICE_ASSIST_2, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1001应答可正常接听，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1001, false);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 10)).as("[通话校验]:1002分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertAll();

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_gsm, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", GSM, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_gsm, ext_1001, CDRObject.STATUS.ANSWER.toString(), ext_1001+" hung up", GSM, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "RingStategry","RingTimeout", "PSeries","Cloud","K2"})
    public void testRG12_RingStategryRingTimeout1() {

        prerequisite();
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(2000, "993000", DEVICE_ASSIST_2, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 3)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃20秒后，分机1002响铃20秒");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 3)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "RingStategry","RingTimeout","PSeries","Cloud","K2"})
    public void testRG13_RingStategryRingTimeout2() {

        prerequisite();
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();


        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1000, CDRObject.STATUS.ANSWER.toString(), ext_1000+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "RingStategry","RingTimeout", "SIP_REGISTER","PSeries","Cloud","K2"})
    public void testRG14_RingStategryRingTimeout3() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SPS外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(2000, "993000", DEVICE_ASSIST_2, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 3)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃20秒后，分机1002响铃20秒");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 3)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(1002,false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

//        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
//        resetRingGroup1();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1002, CDRObject.STATUS.ANSWER.toString(), ext_1002+" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));
        
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "RingStategry","RingTimeout", "FailoverDestination","PSeries","Cloud","K2"})
    public void testRG15_RingStategryRingTimeout4() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SPS外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(2000, "993000", DEVICE_ASSIST_2, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 22)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1001响铃20s后，分机1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 22)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1002响铃20s后，分机1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 22)).as("[通话校验]:20s后1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);

        step("分机1003响铃20s未接，1004响铃");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 22)).as("[通话校验]:20s后1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

//        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
//        resetRingGroup1();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1004, CDRObject.STATUS.ANSWER.toString(), ext_1004+" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "RingStategry","RingTimeout", "SIP_REGISTER","PSeries","Cloud","K2"})
    public void testRG16_RingStategryRingTimeout5() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃10s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 12)).as("[通话校验]:10s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1001响铃10秒后，分机1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 12)).as("[通话校验]:10s内1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Hangup_All();

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "RingStategry","RingTimeout","PSeries","Cloud","K2"})
    public void testRG17_RingStategryRingTimeout6() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过SIP外线呼入到RingGroup1 ");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 3)).as("[通话校验]:3s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);
        step("分机1001接听，其他挂断");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1001, CDRObject.STATUS.ANSWER.toString(), ext_1001+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("RingStategry，RingTimeout")
    @Description("通过内部分机呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "RingStategry","RingTimeout","Extension","PSeries","Cloud","K2"})
    public void testRG18_RingStategryRingTimeout7() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();
        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("内部分机1004呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(1004,  ringGroupNum1, DEVICE_IP_LAN, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃10s后，分机1001响铃");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 12)).as("[通话校验]:2s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1001响铃10秒后，分机1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 12)).as("[通话校验]:2s内1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1002响铃10s后，分机1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 12)).as("[通话校验]:2s内1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        pjsip.Pj_Hangup_All();


        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG19_Members1() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        resetRingGroup1();

        step("编辑RingGroup1，RingStategry选择ring_all，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1002");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10",editRingGroup1Extension(extList)))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
        step("1000 1002未接，1004响铃未接，主叫挂断");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 3)).as("[通话校验]:1004分机响铃").isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1004, CDRObject.STATUS.NO_ANSWER.toString(), ext_3001+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG20_Members2() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员只选择1000、1002；RingStategry 选择RingAll，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1002");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10",editRingGroup1Extension(extList)))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);


        step("分机1002接听、挂断");
//        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验]:1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1002, CDRObject.STATUS.ANSWER.toString(), ext_1002+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG21_Members3() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员只选择1000、1002；RingStategry 选择Ring Sequentially，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1002");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        sleep(10000);

        step("分机1000响铃10s后，1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        step("1002接听 1002挂断");
        pjsip.Pj_Answer_Call(1002,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1002, CDRObject.STATUS.ANSWER.toString(), ext_1002+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        step("编辑RingGroup1，编辑成员顺序为1002、1000；");
        extList.clear();
        extList.add("1002");
        extList.add("1000");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1002响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        sleep(10000);

        step("分机1002响铃10s后，1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);

        step("1000接听 1000挂断");
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_3001, cdrRingGroup1, CDRObject.STATUS.ANSWER.toString(), cdrRingGroup1 + " connected", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_3001, ext_1000, CDRObject.STATUS.ANSWER.toString(), ext_1000+" hung up", SIPTrunk, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG22_Members4() {

        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1成员选择1001、1002、ExGroup1、1003；RingStategry 选择Ring Sequentially，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1001");
        extList.add("1002");
        extList.add("group_ExGroup1");
        extList.add("1003");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "993000", DEVICE_ASSIST_2, false);

        step("分机1001响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 5)).as("[通话校验1]:1001分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验1]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验1]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验1]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1001响铃10s后，1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 12)).as("[通话校验2]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验2]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验2]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验2]:1003分机未响铃").isEqualTo(HUNGUP);


        step("分机1002响铃10s后，1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 12)).as("[通话校验3]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验3]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验3]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验3]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃10s后，1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 12)).as("[通话校验4]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验4]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验4]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验4]:1002分机未响铃").isEqualTo(HUNGUP);


        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1，编辑成员顺序为1002、ExGroup1、1003、1001");
        extList.clear();
        extList.add("1002");
        extList.add("group_ExGroup1");
        extList.add("1003");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "993000", DEVICE_ASSIST_2, false);

        step("分机1002响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验5]:1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验5]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验5]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验5]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1002响铃10s后，1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 12)).as("[通话校验6]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验6]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验6]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验6]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃10s后，1001响铃");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 12)).as("[通话校验7]:1001分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验7]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验7]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验7]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1001响铃10s后，1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 12)).as("[通话校验8]:1003分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验8]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验8]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验8]:1000分机未响铃").isEqualTo(HUNGUP);
        pjsip.Pj_Hangup_All();


        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG23_Members5() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择ExGroup1、ExGroup2；RingStategry 选择Ring Sequentially，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("group_ExGroup1");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        sleep(10000);

        step("分机1000响铃10s后，1001响铃");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 3)).as("[通话校验]:1001分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        sleep(10000);

        step("分机1001响铃10s后，1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 3)).as("[通话校验]:1002分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members","PSeries","Cloud","K2"})
    public void testRG24_Members6() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择ExGroup1、ExGroup2；RingStategry 选择Ring All，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("group_ExGroup1");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000/1001/1002同时响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Members")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "Members", "PSeries","FXS"})
    public void testRG25_Members7() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1020（FXS分机）；RingStategry 选择Ring All，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1020");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000 1020同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 5)).as("[通话校验]:1020分机对接O口分机2000响铃").isEqualTo(RING);

        pjsip.Pj_Hangup_All();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到[None]\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination","None","PSeries","Cloud","K2"})
    public void testRG26_FailoverDestination01() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择[None]");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"\"", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 1)).as("[通话校验]:2000分机主叫被挂断").isEqualTo(HUNGUP);
//        pjsip.Pj_hangupCall(2000);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到HangUp\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "HangUp","PSeries","Cloud","K2"})
    public void testRG27_FailoverDestination02() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Hang Up");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                "\"fail_dest\":\"end_call\"", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(12000);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 1)).as("[通话校验]:2000分机主叫被挂断").isEqualTo(HUNGUP);
//        pjsip.Pj_hangupCall(2000);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到SIP分机1004\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "Extension","PSeries","Cloud","K2"})
    public void testRG28_FailoverDestination03() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Extension-分机1004");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", editRingGroup1Extension(extList).toString(),apiUtil.getExtensionSummary("1004").id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 5)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1004, CDRObject.STATUS.ANSWER.toString(), ext_1004+" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到FXS分机1020\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "FXS","PSeries"})
    public void testRG29_FailoverDestination04() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Extension-分机1020");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", editRingGroup1Extension(extList),apiUtil.getExtensionSummary("1020").id))
                .apply();

        pjsip.Pj_Make_Call_No_Answer(2001,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);
        step("分机1000 1001 1002同时响铃超时后，Failover到FXS分机1020，即辅助2的2000分机响铃");
        softAssertPlus.assertThat(getExtensionStatus(2000, RING, 11)).as("[通话校验]:2000分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(2000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(2001);
        pjsip.Pj_Hangup_All();

        //辅助设备sps trunk去电显示号码设置2000，所以此用例呼叫方cdr显示2000
        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1020, CDRObject.STATUS.ANSWER.toString(), ext_2000+" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到Voicemail-分机1004\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "SPS","PSeries","Cloud","K2"})
    public void testRG30_FailoverDestination05() {
        prerequisite();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Extension Voicemail-分机1004");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                "\"fail_dest\":\"ext_vm\",\"fail_dest_value\":\"%s\"", editRingGroup1Extension(extList),apiUtil.getExtensionSummary("1004").id))
                .apply();

        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(15000);
        pjsip.Pj_Hangup_All();

        step("所有分机1000\\1001\\1002同时响铃10s后，进入到分机1004的语音留言，登录1004查看新增一条语音留言，Name记录正确");

        step("网页admin登录,进入voicemail界面 ");
        auto.loginPage().login("1004", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Name");

        System.out.println("at 0 "+lists.get(0));
        softAssertPlus.assertThat(lists.size() > 0).as("至少有一条语音留言记录");
        if (lists.size() > 0){
            softAssertPlus.assertThat(lists.get(0)).as("最新一条记录是分机2000分机的记录").isEqualTo("2000\n" +
                    "External Number");
        }

        sleep(5000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到IVR0\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "IVR","PSeries","Cloud","K2"})
    public void testRG31_FailoverDestination06() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择IVR0-6200，按0");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                        "\"fail_dest\":\"ivr\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(),apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(13000);
        pjsip.Pj_Send_Dtmf(2000,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(2000);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrIvr0, CDRObject.STATUS.ANSWER.toString(), ext_2000 + " called Extension", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1000, CDRObject.STATUS.ANSWER.toString(), ext_2000 + " hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时RingGroup0\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination","PSeries","Cloud","K2"})
    public void testRG32_FailoverDestination07() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择RingGroup0-6300");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                        "\"fail_dest\":\"ring_group\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(),apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
        step("进入到RingGroup0，分机1000、1001、1003同时响铃；");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);

        step("1003接听，挂断；cdr正确");
        pjsip.Pj_Answer_Call(1003,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1003);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrRingGroup0, CDRObject.STATUS.ANSWER.toString(),    cdrRingGroup0 + " connected",           SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1003,      CDRObject.STATUS.ANSWER.toString(),     ext_1003 + " hung up",                  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时RingGroup0\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination","PSeries","Cloud","K2"})
    public void testRG33_FailoverDestination08() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择RingGroup0-6300");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                "\"fail_dest\":\"ring_group\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(),apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);

        step("进入到RingGroup0，分机1000、1001、1003同时响铃；");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);

        sleep(10000);

        step("无成员接听，响铃10s超时后分机1000响铃，接听，挂断；cdr正确");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机响铃").isEqualTo(HUNGUP);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrRingGroup0, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup0 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1000,      CDRObject.STATUS.ANSWER.toString(),    ext_1000 + " hung up",                  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时Queue\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "Queue","PSeries","Cloud","K2"})
    public void testRG34_FailoverDestination09() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Queue0-6400");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                        "\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(), apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);

        step("Failover进入到Queue0,分机1000、1001、1003、1004同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        step("分机1000接听");
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrQueue0,     CDRObject.STATUS.ANSWER.toString(), cdrQueue0 + " connected",               SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1000,      CDRObject.STATUS.ANSWER.toString(),    ext_1000 + " hung up",                   SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时Queue\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "Queue","PSeries","Cloud","K2"})
    public void testRG35_FailoverDestination10() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Queue0-6400");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"",
                editRingGroup1Extension(extList), apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);

        step("Failover进入到Queue0,静态坐席分机1000、1001、动态坐席分机1003、1004同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        sleep(WaitUntils.RETRY_WAIT*4);

        step("主叫按0到分机1001");
        pjsip.Pj_Send_Dtmf(2000,"0");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 3)).as("[通话校验]:1001分机响铃").isEqualTo(RING);

        step("1001接听、挂断；cdr正常");
        pjsip.Pj_Answer_Call(1001);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrQueue0,     CDRObject.STATUS.NO_ANSWER.toString(),    cdrQueue0 + " connected",               SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1001,      CDRObject.STATUS.ANSWER.toString(),    ext_1001 + " hung up",                   SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));


        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时Queue\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "Queue","PSeries","Cloud","K2"})
    public void testRG36_FailoverDestination11() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Queue0-6400");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"",
                editRingGroup1Extension(extList), apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);

        step("Failover进入到Queue0,分机1000、1001、1003、1004同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        step("Queue0的坐席成员无人应答，60s后Failover到分机1000");
        sleep(61000);

        step("1000接听、挂断；cdr正常");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrQueue0,     CDRObject.STATUS.NO_ANSWER.toString(), cdrQueue0 + " timed out, failover",     SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1000,      CDRObject.STATUS.ANSWER.toString(),    ext_1000 + " hung up",                  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到External Number\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "ExternalNumber","PSeries","Cloud","K2"})
    public void testRG37_FailoverDestination12() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择External Number  13001");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                        "\"fail_dest\":\"external_num\",\"fail_dest_prefix\":\"1\",\"fail_dest_value\":\"3001\"",editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
        step("分机1000 1001 1002同时响铃超时后，Failover到ExternalNumber 13001，预期辅助1的分机3001响铃");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[通话校验]:3001分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover",  SPS, "",       CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, "13001",       CDRObject.STATUS.ANSWER.toString(),    "13001 hung up",                         SPS, SIPTrunk, CDRObject.COMMUNICATION_TYPE.OUTBOUND.toString()));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到PlayPromptandExit\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "FailoverDestination", "PlayPromptandExit","PSeries","Cloud","K2"})
    public void testRG38_FailoverDestination13() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();
        //TODO  mei zuo

        step("编辑RingGroup1，成员选择1000、1001、ExGroup2；RingStategry 选择Ring All，响铃时间为10s；FailoverDestination选择Play Prompt and Exit ，选择prompt1，播放2遍");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        extList.add("group_ExGroup2");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10," +
                        "\"fail_dest\":\"ivr\",\"fail_dest_value\":\"%s\"",
                editRingGroup1Extension(extList), apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 1001 1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到[None]\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "None","PSeries","Cloud","K2"})
    public void testRG39_FailoverDestination14() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择[None]");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"\"", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 1)).as("[通话校验]:2000分机被挂断").isEqualTo(HUNGUP);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, hung up",  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时Hangup\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "HangUp","PSeries","Cloud","K2"})
    public void testRG40_FailoverDestination15() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择Hang Up");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"end_call\"", editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        softAssertPlus.assertThat(getExtensionStatus(2000, HUNGUP, 1)).as("[通话校验]:2000分机被挂断").isEqualTo(HUNGUP);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, hung up",  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时分机1004\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "Extension","PSeries","Cloud","K2"})
    public void testRG41_FailoverDestination16() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择Extension-分机1004");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", editRingGroup1Extension(extList).toString(),apiUtil.getExtensionSummary("1004").id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(5000);
        step("失败目的地1004分机响铃接听");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 5)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1004, CDRObject.STATUS.ANSWER.toString(), ext_1004+" hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时Voicemail\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "Voicemail","PSeries","Cloud","K2"})
    public void testRG42_FailoverDestination17() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择Extension Voicemail-分机1004");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"ext_vm\",\"fail_dest_value\":\"%s\"", editRingGroup1Extension(extList).toString(),apiUtil.getExtensionSummary("1004").id))
                .apply();

        step("通过sps外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        step("所有分机1000\\1001\\1002同时响铃10s后，进入到分机1004的语音留言，登录1004查看新增一条语音留言，Name记录正确");
        sleep(10000);
        pjsip.Pj_Hangup_All();

        step("网页admin登录,进入ringgroup界面 ");
        auto.loginPage().login("1004", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Name");

        System.out.println("at 0 "+lists.get(0));
        softAssertPlus.assertThat(lists.size() > 0).as("至少有一条语音留言记录");
        if (lists.size() > 0){
            softAssertPlus.assertThat(lists.get(0)).as("最新一条记录是分机2000分机的记录").isEqualTo("2000\n" +
                    "External Number");
        }

        sleep(5000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到IVR0\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "IVR","PSeries","Cloud","K2"})
    public void testRG43_FailoverDestination18() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择IVR0-6200，按0");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                        "\"fail_dest\":\"ivr\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(),apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1，分机1000、1001响铃超时后进入IVR，按0");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(7000);

        pjsip.Pj_Send_Dtmf(2000,"0");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);

        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(2000);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrIvr0, CDRObject.STATUS.ANSWER.toString(), ext_2000 + " called Extension", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1000, CDRObject.STATUS.ANSWER.toString(), ext_2000 + " hung up", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到RingGroup\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "RingGroup","PSeries","Cloud","K2"})
    public void testRG44_FailoverDestination19() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择RingGroup0-6300");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"ring_group\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(),apiUtil.getRingGroupSummary(ringGroupNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1，分机1000、1001响铃超时后进入RingGroup0");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(5000);
        step("进入到RingGroup0，分机1000、1001、1003同时响铃；");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);

        step("1003接听，挂断；cdr正确");
        pjsip.Pj_Answer_Call(1003);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1003);

        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrRingGroup0, CDRObject.STATUS.ANSWER.toString(),    cdrRingGroup0 + " connected",           SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1003,      CDRObject.STATUS.ANSWER.toString(),    ext_1003 + " hung up",                  SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到Queue\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "Queue","PSeries","Cloud","K2"})
    public void testRG45_FailoverDestination20() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择Queue0-6400");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"queue\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(), apiUtil.getQueueSummary(queueNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1，分机1000、1001响铃超时后进入Queue0-6400");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(5000);
        step("进入到Queue0,分机1000、1001、1003、1004同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);

        step("分机1000接听");
        pjsip.Pj_Answer_Call(1004);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover", SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, cdrQueue0,     CDRObject.STATUS.ANSWER.toString(),    cdrQueue0 + " connected",               SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, ext_1004,      CDRObject.STATUS.ANSWER.toString(),    ext_1004 + " hung up",                   SPS, "", CDRObject.COMMUNICATION_TYPE.INBOUND.toString()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到ExternalNumber\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "ExternalNumber","PSeries","Cloud","K2"})
    public void testRG46_FailoverDestination21() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择External Number  13001");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                "\"fail_dest\":\"external_num\",\"fail_dest_prefix\":\"1\",\"fail_dest_value\":\"3001\"",editRingGroup1Extension(extList).toString()))
                .apply();

        step("通过sps外线呼入到RingGroup1，分机1000、1001响铃超时后");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(5000);
        step("超时Failover到外部号码13001，预期辅助1的分机3001响铃");
        softAssertPlus.assertThat(getExtensionStatus(3001, RING, 10)).as("[通话校验]:3001分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_2000, cdrRingGroup1, CDRObject.STATUS.NO_ANSWER.toString(), cdrRingGroup1 + " timed out, failover",  SPS, "",       CDRObject.COMMUNICATION_TYPE.INBOUND.toString()))
                .contains(tuple(ext_2000, "13001",       CDRObject.STATUS.ANSWER.toString(),    "13001 hung up",                         SPS, SIPTrunk, CDRObject.COMMUNICATION_TYPE.OUTBOUND.toString()));



        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("FailoverDestination")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时到Play Prompt and Exit\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3","RingGroup", "FailoverDestination", "PlayPromptandExit","PSeries","Cloud","K2"})
    public void testRG47_FailoverDestination22() {
        prerequisite();
        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();
        //TODO  mei zuo

        step("编辑RingGroup1，成员选择1000、1001；RingStategry 选择Ring Sequentially，响铃时间为5s；FailoverDestination选择Play Prompt and Exit ，选择prompt1，播放1遍");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1001");
        apiUtil.editRingGroup(ringGroupNum1, String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":5," +
                        "\"fail_dest\":\"ivr\",\"fail_dest_value\":\"%s\"",editRingGroup1Extension(extList).toString(), apiUtil.getIVRSummary(ivrNum0).id))
                .apply();

        step("通过sps外线呼入到RingGroup1，分机1000、1001响铃超时后");
        pjsip.Pj_Make_Call_No_Answer(2000,  "996301", DEVICE_ASSIST_2, false);

        step("分机1000 响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 3)).as("[通话校验]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);

        step("分机1000响铃5秒后分机1001响铃5秒后，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING ,  6)).as("[通话校验]:1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);

        sleep(5000);


    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Delete")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "Delete","PSeries","Cloud","K2"})
    public void testRG48_Delete1() {
        prerequisite();

        step("网页admin登录,进入ringgroup界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ring_group);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if(!lists.contains(ringGroupNum1)){
            List<String > list = new ArrayList<>();
            list.add("1000");
            auto.ringGroupPage().createRingGroup(ringGroupNum1,ringGroupName1,list).clickSaveAndApply();
        }

        step("删除响铃组6301");
        auto.ringGroupPage().deleDataByDeleImage(ringGroupNum1).clickApply();

        assertStep("删除成功");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list).doesNotContain(ringGroupNum1);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Delete")
    @Description("通过外线呼入到RingGroup1\n" +
            "d等待响铃超时\n" +
            "检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2","RingGroup", "Delete","PSeries","Cloud","K2"})
    public void testRG49_Delete2() {

        step("网页admin登录,进入ringgroup界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ring_group);

        List<String > list = new ArrayList<>();
        list.add("1000");
        auto.ringGroupPage().createRingGroup("6305","RG2",list).clickSaveAndApply();

        step("批量删除响铃组");
        auto.ringGroupPage().deleAllRingGroup().clickSaveAndApply();

        assertStep("删除成功");
        List<String> list2 = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list2.size()).isEqualTo(0);
        softAssertPlus.assertAll();
    }

}
