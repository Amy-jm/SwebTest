package com.yeastar.controllers;

import com.jcraft.jsch.JSchException;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Log4j2
public class BaseMethod extends WebDriverFactory {

	public final String reqDataCreateExtension = String.format("" +
					"{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
			, enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
	public final String reqDataCreateSPS_2 = String.format("" +
					"{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
			, SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
	public final String reqDataCreateExtensionFXS = String.format("" +
					"{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
			, enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

	public final String IVR_GREETING_DIAL_EXT = "ivr-greeting-dial-ext.slin";
	public final String PROMPT_1 = "prompt1.slin";
	public final String ringGroupName0 = "RingGroup0";//6300
	public final String ringGroupName1 = "RingGroup1";//6301
	public final String ivrName0 = "IVR0";//6301
	public final String queueName0 = "Queue0";//6400
	public final String queueName1 = "Queue1";
	public final String conferenceName0 = "Conference0";//6500

	public final String ringGroupNum0 = "6300";
	public final String ringGroupNum1 = "6301";
	public final String ivrNum0 = "6200";
	public final String queueNum0 = "6400";
	public final String queueNum1 = "6401";
	public final String conferenceNum0 = "6500";

	public final String cdrRingGroup0 = String.format("RingGroup %s<%s>", ringGroupName0, ringGroupNum0);
	public final String cdrRingGroup1 = String.format("RingGroup %s<%s>", ringGroupName1, ringGroupNum1);
	public final String cdrQueue0 = String.format("Queue %s<%s>", queueName0, queueNum0);
	public final String cdrQueue1 = String.format("Queue %s<%s>", queueName1, queueNum1);
	public final String cdrIvr0 = String.format("IVR %s<%s>", ivrName0, ivrNum0);

	public final String ext_1000 = "test A<1000>";
	public final String ext_1001 = "test2 B<1001>";
	public final String ext_1002 = "testta C<1002>";
	public final String ext_1003 = "testa D<1003>";
	public final String ext_1004 = "t estX<1004>";
	public final String ext_1020 = "1020 1020<1020>";
	public final String ext_2000 = "2000<2000>";
	public final String ext_3001 = "3001<3001>";
	public final String ext_4000 = "4000<4000>";

	APIUtil apiUtil = new APIUtil();

	@Step("{0}")
	public void step(String desc){
		log.debug("[step] "+desc);
		sleep(5);
		try{
			Cookie cookie = new Cookie("zaleniumMessage", desc);
			getWebDriver().manage().addCookie(cookie);
		}catch (org.openqa.selenium.WebDriverException exception){
			log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
		}catch(Exception ex){
			log.error("[BaseMethod on step ] "+ex);
		}
	}

	@Step("{0}")
	public void assertStep(String desc){
		log.debug("[Assert] "+desc);
		sleep(5);
		try{
			Cookie cookie = new Cookie("zaleniumMessage", desc);
			getWebDriver().manage().addCookie(cookie);
		}catch (org.openqa.selenium.WebDriverException exception){
			log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
		}catch(Exception ex){
			log.error("[BaseMethod on assertStep ] "+ex);
		}

	}

	/**
	 * get test name
	 *
	 * @param method
	 * @return
	 */
	public String getTestName(Method method) {
		return this.getClass().getSimpleName() + "." + method.getName();
	}

	/* To ScrollUp using JavaScript Executor */
	public void scrollUp() throws Exception 
	{
		((JavascriptExecutor) getWebDriver()).executeScript("scroll(0, -100);");
	}


	/* To ScrollDown using JavaScript Executor */
	public void scrollDown() throws Exception 
	{
		((JavascriptExecutor) getWebDriver()).executeScript("scroll(0, 100);");
	}

	/*To Switch To Frame By Index */
	public void switchToFrameByIndex(int index) throws Exception
	{
		getWebDriver().switchTo().frame(index);
	}


	/*To Switch To Frame By Frame Name */
	public void switchToFrameByFrameName(String frameName) throws Exception
	{
		getWebDriver().switchTo().frame(frameName);
	}


	/*To Switch To Frame By Web Element */
	public void switchToFrameByWebElement(WebElement element) throws Exception
	{
		getWebDriver().switchTo().frame(element);
	}


	/*To Switch out of a Frame */
	public void switchOutOfFrame() throws Exception
	{
		getWebDriver().switchTo().defaultContent();
	}


	/*To Get Tooltip Text */
	public String getTooltipText(WebElement element)
	{
		String tooltipText = element.getAttribute("title").trim();
		return tooltipText;
	}


	/*To Close all Tabs/Windows except the First Tab */
	public void closeAllTabsExceptFirst() 
	{
		ArrayList<String> tabs = new ArrayList<String> (getWebDriver().getWindowHandles());
		for(int i=1;i<tabs.size();i++)
		{	
			getWebDriver().switchTo().window(tabs.get(i));
			getWebDriver().close();
		}
		getWebDriver().switchTo().window(tabs.get(0));
	}
	
	
	/*To Print all the Windows */
	public void printAllTheWindows() 
	{
		ArrayList<String> al = new ArrayList<String>(getWebDriver().getWindowHandles());
		for(String window : al)
		{
			System.out.println(window);
		}
	}

	/**
	 * Base64加密字符串
	 * @param str
	 * @return
	 */
	public String enBase64(String str) {
		byte[] bytes = str.getBytes();

		String encoded = Base64.getEncoder().encodeToString(bytes);

		return encoded;
	}

	/**
	 * ssh操作执行静态命令查看
	 * @param asteriskCommand
	 * @return
	 * @throws IOException
	 * @throws JSchException
	 */
	public  String execAsterisk(String asteriskCommand)  {
		String asterisk_commond = String.format(ASTERISK_CLI,asteriskCommand);
		log.debug("[asterisk_command]"+asterisk_commond);
		String str = null;
		try {
			str = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, asterisk_commond);
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("[asterisk_command_return_string] "+str);
		return str;
	}

	@Step("...清空/ysdisk/syslog/pbxlog.0文件")
	public String clearasteriskLog()  {
		log.debug("[CLEAR_CLI_LOG_command]"+CLEAR_CLI_LOG);
		String str = null;
		try {
			str = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, CLEAR_CLI_LOG);
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug("[asterisk_command_return_string] "+str);
		return str;
	}

	/**
	 * 获取分机通话状态
	 *
	 * @param username
	 */
	@Step("获取分机通话状态")
	public  int getExtensionStatus(int username, int expectStatus, int timeout) {
		UserAccount account;
		int time = 0;
		int status = -1;
		while (time <= timeout) {
			sleep(1000);
			account = pjsip.getUserAccountInfo(username);
//                        account = getPjsip().getUserAccountInfo(username);

			if (account == null) {
				status = -1;
			}
			if (expectStatus == IDLE || expectStatus == HUNGUP){
				if (account.status == IDLE || account.status == HUNGUP) {
					status = expectStatus;//account.status;
					return status;
				}
			}else{
				if (account.status == expectStatus) {
					status = account.status;
					return status;
				}
			}

			if (time == timeout) {
				status = account.status;
			}
			time++;
		}
		System.out.println("分机通话状态：" + status);
		return status;
	}

	/**
	 * 界面刷新
	 */
	public void refresh(){
		getWebDriver().navigate().refresh();
	}


	/**
	 * 分机1000、名称: test A     角色:Administrator
	 * 分机1001、名称：test2 B    角色：Supervisor
	 * 分机1002、名称：testta C   角色：Supervisor
	 * 分机1003、名称：testa D    角色：Supervisor
	 * 分机1004、名称：test2X     角色：Supervisor
	 * 分机1005、名称：First Last    角色：Supervisor
	 * fxs分机1020、名称：1020 1020  角色：Supervisor
	 * 分机0   、名称：0          角色：空
	 */
	public void initExtension(){
		step("创建分机1000-1005、fxs 1020");
		apiUtil.deleteAllExtension().apply();
		sleep(WaitUntils.SHORT_WAIT);
		String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");
		apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "First").replace("EXTENSIONLASTNAME", "Last").replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "Last").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", "1-3").replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
				.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW).editExtension("0","\"role_id\":1")
				.loginWebClient("1004", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW).apply();
	}

	/**
	 * 初始化分机组
	 * ExGroup1：1000 1001
	 * ExGroup2：1000 1002
	 */
	public void initExtensionGroup(){

		step("初始化分机组 ExGroup1/ExGroup2");
		List<String> extensionExGroup1 = new ArrayList<>();
		List<String> extensionExGroup2 = new ArrayList<>();
		extensionExGroup1.add("1000");
		extensionExGroup1.add("1001");

		extensionExGroup2.add("1000");
		extensionExGroup2.add("1002");

		apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
				createExtensionGroup("ExGroup1",extensionExGroup1).
				createExtensionGroup("ExGroup2",extensionExGroup2).apply();

	}

	/**
	 * 初始化Trunk
	 */
	public void initTrunk(){
		step("创建SPS中继");
		apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2).apply();
	}

	/**
	 * 初始化IVR
	 * 号码：6200
	 * 名称：IVR0
	 * 其他：按0到分机A，其它默认设置
	 */
	public void initIVR(){
		step("创建IVR 6200");
		ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
		pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
		apiUtil.deleteAllIVR().createIVR("6200", "IVR0", pressKeyObjects_0).apply();
	}

	/**
	 * 初始化响铃组
	 * 号码：6300
	 * 名称：RingGroup0
	 * 成员：ExGroup1、1003
	 * 其他：选择同时响铃，响铃时长10秒，Failover到分机1000；其它默认设置
	 */
	public void initRingGroup(){
		step("创建响铃组6300 ");

		ArrayList<String> ringGroupMembers0 = new ArrayList<>();
		ArrayList<String> ringGroupMembers1 = new ArrayList<>();

		ringGroupMembers0.add("group_ExGroup1");
		ringGroupMembers0.add("1003");

		ExtensionObject extensionObject = apiUtil.getExtensionSummary("1000");

		apiUtil.deleteAllRingGroup().createRingGroup("RingGroup0", "6300", ringGroupMembers0)
				.editRingGroup("RingGroup0", String.format("\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"extension\",\"" +
						"fail_dest_value\":\"%s\"", extensionObject.id)).apply();

	}

	/**
	 * 初始化会议室
	 * 号码：6500
	 * 名称：Conference0
	 * 成员：1000
	 * 其他默认设置
	 */
	public void initConference(){
		step("创建会议室");

		ArrayList<String> conferenceMember = new ArrayList<>();
		conferenceMember.add("1000");
		apiUtil.deleteAllConference().createConference("Conference0", "6500", conferenceMember).apply();

	}

	/**
	 * 初始化队列
	 * 号码：6400
	 * 名称：Queue0
	 * 静态成员：1000 1001
	 * 动态成员：1003 1004
	 * 最大等待时间60s；Failover到分机1000；按Key 0 到分机1001、
	 * 其它默认设置
	 */
	public void initQueue(){
		step("创建队列6400");

		ArrayList<String> queueStaticMembers = new ArrayList<>();
		ArrayList<String> queueDynamicMembers = new ArrayList<>();
		queueStaticMembers.add("1000");
		queueStaticMembers.add("1001");
		queueDynamicMembers.add("1003");
		queueDynamicMembers.add("1004");
		apiUtil.deleteAllQueue().createQueue(queueName0, queueNum0, queueDynamicMembers, queueStaticMembers, null)
				.editQueue(queueNum0,String.format("\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\",\"max_wait_time\":60," +
						"\"press_key\":\"0\",\"key_dest\":\"extension\",\"key_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1001").id)).apply();

	}

	/**
	 * 初始化呼入路由
	 * 名称：In1
	 * 选择所有外线，分机目的地设置为分机1000，其它默认
	 */
	public void initInbound(){
		step("创建呼入路由InRoute3,目的地到响铃组6300");

		List<String> trunk9 = new ArrayList<>();

		trunk9.add(SPS);
		trunk9.add(BRI_1);
		trunk9.add(FXO_1);
		trunk9.add(E1);
		trunk9.add(SIPTrunk);
		trunk9.add(ACCOUNTTRUNK);
		trunk9.add(GSM);
		apiUtil.deleteAllInbound().createInbound("In1", trunk9, "extension", "1000").apply();
	}

	/**
	 * 初始化呼出路由
	 * Out1，号码规则：1.  ，删除前缀：1，选择外线：sip1，选择所有分机
	 * Out2，号码规则：2. ，删除前缀：1，外线：sps，选择所有分机
	 * Out3，号码规则：3. ，删除前缀：1，外线：account，选择所有分机
	 * Out4，号码规则：4.，删除前缀：1，外线：FXO，选择所有分机
	 * Out5，号码规则：5. ，删除前缀：1，外线：BRI，选择所有分机
	 * Out6，号码规则：6.，删除前缀：1，外线：E1，选择所有分机
	 * Out7，号码规则：7.，删除前缀：1，外线：LTE/GSM，选择所有分机
	 * Out8，号码规则：X. ，删除前缀：空，外线sps外线，选择分机A
	 * Out9，号码规则：X.，删除前缀：空，全选外线，全选分机
	 */
	public void initOutbound(){

		step("创建呼出路由");

		List<String> extensionNum = new ArrayList<>();
		List<String> extensionNumA = new ArrayList<>();

		extensionNum.add("0");
		extensionNum.add("1000");
		extensionNum.add("1001");
		extensionNum.add("1002");
		extensionNum.add("1003");
		extensionNumA.add("1000");

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
		apiUtil.deleteAllOutbound().createOutbound("Out1", trunk1, extensionNum, "1.", 1).
				createOutbound("Out2", trunk2, extensionNum, "2.", 1).
				createOutbound("Out3", trunk3, extensionNum, "3.", 1).
				createOutbound("Out4", trunk4, extensionNum, "4.", 1).
				createOutbound("Out5", trunk5, extensionNum, "5.", 1).
				createOutbound("Out6", trunk6, extensionNum, "6.", 1).
				createOutbound("Out7", trunk7, extensionNum, "7.", 1).
				createOutbound("Out8", trunk8, extensionNumA).
				createOutbound("Out9", trunk9, extensionNum).apply();
	}

	public boolean registerAllExtensions() {
		log.debug("[prerequisite] init extension");
		pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(2001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(3001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(4000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

		pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
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
		if (getExtensionStatus(1004, IDLE, 5) != IDLE) {
			reg = true;
			log.debug("1004注册失败");
		}
		if (getExtensionStatus(2000, IDLE, 5) != IDLE) {
			reg = true;
			log.debug("2000注册失败");
		}
		if (getExtensionStatus(2001, IDLE, 5) != IDLE) {
			reg = true;
			log.debug("2001注册失败");
		}
		if (getExtensionStatus(3001, IDLE, 5) != IDLE) {
			reg = true;
			log.debug("3001注册失败");
		}
		if (getExtensionStatus(4000, IDLE, 5) != IDLE) {
			reg = true;
			log.debug("4000注册失败");
		}
		if(!reg){
			pjsip.Pj_Unregister_Accounts();
		}else{
			step("1003 1004拨号*76400，登录Queue0");
			pjsip.Pj_Make_Call_No_Answer(1003,  "*76400", DEVICE_IP_LAN, false);
			pjsip.Pj_Make_Call_No_Answer(1004,  "*76400", DEVICE_IP_LAN, false);
		}
		return reg;
	}
}
