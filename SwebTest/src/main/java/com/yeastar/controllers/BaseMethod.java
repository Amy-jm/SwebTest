package com.yeastar.controllers;

import com.jcraft.jsch.JSchException;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.*;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.javatuples.Quintet;
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
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Log4j2
public class BaseMethod extends WebDriverFactory {
	private  int ROLE = 9999999;
	public APIUtil apiUtil = new APIUtil();

	public final String reqDataCreateExtension = String.format("" +
					"{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":%d,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
			, enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), ROLE,enBase64(EXTENSION_PASSWORD));
	public final String reqDataCreateSPS_2 = String.format("" +
					"{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
			, SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);

	public final String reqDataCreateS_SIPTrunk = String.format("" +
					"{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"register\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\",\"username\":\"3000\",\"password\":\"%s\",\"auth_name\":\"%s\",\"enb_outbound_proxy\":0,\"outbound_proxy_server\":\"\",\"outbound_proxy_port\":0,\"realm\":\"\"}"
			,SIPTrunk, "DOD", DEVICE_ASSIST_1, DEVICE_ASSIST_1,enBase64(EXTENSION_PASSWORD),"3000");

	public final String reqDataCreateS_SIPTrunk2 = String.format("" +
					"{\"name\":\"%s\",\"enable\":0,\"country\":\"general\",\"itsp\":\"\",\"type\":\"register\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\",\"username\":\"3003\",\"password\":\"%s\",\"auth_name\":\"3003\",\"enb_outbound_proxy\":0,\"outbound_proxy_server\":\"\",\"outbound_proxy_port\":0,\"realm\":\"\"}"
			,SIPTrunk2, "DOD", DEVICE_ASSIST_1, DEVICE_ASSIST_1,enBase64(EXTENSION_PASSWORD));

	public final String reqDataCreateS_account = String.format("" +
					"{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"account\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"number\":\"%s\",\"password\":\"%s\",\"auth_name\":\"%s\",\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0,\"user_agent_ident_list\":[],\"ip_rstr_list\":[]}"
			,ACCOUNTTRUNK, "6700", enBase64(EXTENSION_PASSWORD),EXTENSION_PASSWORD);

	public final String reqDataCreatAccount =String.format(""+
					"{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"account\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
			, ACCOUNTTRUNK, "DOD", DEVICE_ASSIST_3, DEVICE_ASSIST_3,enBase64(EXTENSION_PASSWORD));
	public final String reqDataCreateExtensionFXS = String.format("" +
					"{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
			, enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

	enum ROLE_ID{
		Administrator("1"),
		Supervisor("2"),
		Operator("3"),
		Employee("4"),
		HumanResource("5"),
		Accounting("6");
		private final String alias;
		ROLE_ID(String alias) {
			this.alias = alias;
		}
		@Override
		public String toString() {
			return this.alias;
		}
	}

	public final String IVR_GREETING_DIAL_EXT = "ivr-greeting-dial-ext.slin";
	public final String CALL_PARKED_AT = "call-parked-at.slin";
	public final String PROMPT_1 = "prompt1.slin";
	public final String PROMPT_2 = "prompt2.slin";
	public final String PROMPT_3 = "prompt3.slin";
	public final String FAX_TO_EMAIL_1001 = "1001@fax_to_email";
	public final String inboundName1 = "In1";
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


	@Step("{0}")
	public void step(String desc){
		log.debug("[step] "+desc);
		sleep(5);
		try{
			Cookie cookie = new Cookie("zaleniumMessage", desc);
			getWebDriver().manage().addCookie(cookie);
		}catch (org.openqa.selenium.WebDriverException exception){
			log.error("[Step -> org.openqa.selenium.WebDriverException: unable to set cookie]");
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
	 * Base64???????????????
	 * @param str
	 * @return
	 */
	public String enBase64(String str) {
		byte[] bytes = str.getBytes();

		String encoded = Base64.getEncoder().encodeToString(bytes);

		return encoded;
	}

	/**
	 * ssh??????????????????????????????
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

	private String findExtCdrRecordByExtNum(String extNum){
		if (extNum.equals("1000")){
			return CDRObject.CDRNAME.Extension_1000.toString();
		}else if (extNum.equals("1001")){
			return CDRObject.CDRNAME.Extension_1001.toString();
		}else if (extNum.equals("1002")){
			return CDRObject.CDRNAME.Extension_1002.toString();
		}else if (extNum.equals("1003")){
			return CDRObject.CDRNAME.Extension_1003.toString();
		}else if (extNum.equals("1004")){
			return CDRObject.CDRNAME.Extension_1004.toString();
		}else if (extNum.equals("1020")){
			return CDRObject.CDRNAME.Extension_1020.toString();
		}else if (extNum.equals("2000")){
			return CDRObject.CDRNAME.Extension_2000.toString();
		}else if (extNum.equals("3001")){
			return CDRObject.CDRNAME.Extension_3001.toString();
		}else if (extNum.equals("2001")){
			return CDRObject.CDRNAME.Extension_2001.toString();
		}else if (extNum.equals("4000")){
			return CDRObject.CDRNAME.Extension_4000.toString();
		}
		return "";
	}

	@Step("???????????????????????????????????????????????????")
	public String getQueueExtInfoByExtNum(String queuenum, String extNum){
		List<Quintet<String, String, String, String, String>> q =getQueueExtNumWithRingStrategy(queuenum,"");
		for (int i=0; i< q.size(); i++){
			if (q.get(i).getValue0().equals(extNum)){
				log.debug("[find  get value4] "+q.get(i).getValue4());
				return q.get(i).getValue4();
			}
		}
		return null;
	}
	/**
	 * ???????????????????????????????????????????????????
	 * @param queuenum
	 * @param ringStrategy : least_recent /
	 * @return
	 */
	@Step("???????????????????????????????????????????????????")
	public String getQueueExtNumWithRingStrategy(String queuenum, String ringStrategy, int index) {
		List<Quintet<String, String, String, String, String>> q =getQueueExtNumWithRingStrategy(queuenum,ringStrategy);
		if (q.size() > index){
			return q.get(index).getValue3();
		}
		return "";
	}
	/**
	 * ???????????????????????????????????????????????????
	 * @param queuenum
	 * @param ringStrategy : least_recent /
	 * @return
	 */
	@Step("?????????????????????????????????????????????????????????")
	public List<Quintet<String, String, String, String, String>> getQueueExtNumWithRingStrategy(String queuenum, String ringStrategy) {

		List<Quintet<String, String, String, String, String>> roleList = new ArrayList<Quintet<String,String, String, String, String>>();
		String queueInfo = execAsterisk("queue show queue-"+queuenum);
		String[] queueInfoList = queueInfo.substring(queueInfo.indexOf("0:"),queueInfo.indexOf("No Callers")).split("\n");

		for (int i = 0; i <queueInfoList.length-1; i++) {
			String str = queueInfoList[i];
			if (str.trim().isEmpty())
				continue;
			String extNum = "";
			String state = "";
			String cdrRecord = "";
			String hasTakenCall = "0";
			String lastCallInterval="9999999";
			if (str.contains("Unavailable"))
				state = "Unavailable";
			else if (str.contains("Not in use"))
				state = "Not in use";
			else if (str.contains("In use"))
				state = "In use";
			else if (str.contains("Ringing"))
				state = "Ringing";

			if (!state.equals("Not in use"))
				continue;

			if (!str.contains("has taken no calls yet")){
				lastCallInterval = str.substring(str.indexOf("last was ")+9, str.indexOf(" secs ago"));
			}

			if (str.substring(str.indexOf("Local/")+6, str.indexOf("Local/")+10).trim().equals("1020")){
				cdrRecord = findExtCdrRecordByExtNum("1020");
				extNum = "2000";
			}else{
				extNum = str.substring(str.indexOf("Local/")+6, str.indexOf("Local/")+10).trim();
				cdrRecord = findExtCdrRecordByExtNum(extNum);
			}

			if (!str.substring(str.indexOf("has taken ")+10, str.indexOf("has taken ")+12).equals("no") ){
				hasTakenCall = str.substring(str.indexOf("has taken ")+10, str.indexOf("has taken ")+12);
			}

			Quintet<String, String, String, String, String> t = TupleUtils.with(
					extNum, //extension number
					cdrRecord, //?????????????????????????????????
					hasTakenCall,//????????????
					lastCallInterval,//???????????????????????????
					str//???????????????
			);
			roleList.add(t);
		}

		for (int i=0; i < roleList.size(); i++){
			for (int j=i+1; j < roleList.size(); j++){
				if (ringStrategy.toLowerCase().equals("least_recent")){
					if (Integer.parseInt(roleList.get(i).getValue3().trim()) <= Integer.parseInt(roleList.get(j).getValue3().trim())){
						roleList.set(i, roleList.set(j, roleList.get(i)));
					}
				}
				if (ringStrategy.toLowerCase().equals("fewest_calls")){
					if (Integer.parseInt(roleList.get(i).getValue2().trim()) >= Integer.parseInt(roleList.get(j).getValue2().trim())){
						roleList.set(i, roleList.set(j, roleList.get(i)));
					}
				}
			}
		}
		log.debug(roleList);
		return roleList;
	}
	@Step("...??????/ysdisk/syslog/pbxlog.0??????")
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
	 * ????????????????????????
	 *
	 * @param username
	 */
	@Step("????????????????????????")
	public  int getExtensionStatus(int username, int expectStatus, int timeout) {
		UserAccount account;
		int time = 0;
		int status = -1;
		while (time <= timeout*20) {
			sleep(50);
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

			if (time == timeout*20) {
				status = account.status;
			}
			time++;
		}
		System.out.println("?????????????????????" + status);
		return status;
	}

	/**
	 * ????????????
	 */
	public void refresh(){
		getWebDriver().navigate().refresh();
	}


	/**
	 * ??????1000?????????: test A     ??????:Administrator
	 * ??????1001????????????test2 B    ?????????Supervisor
	 * ??????1002????????????testta C   ?????????Supervisor
	 * ??????1003????????????testa D    ?????????Supervisor
	 * ??????1004????????????test2X     ?????????Supervisor
	 * ??????1005????????????First Last    ?????????Supervisor
	 * fxs??????1020????????????1020 1020  ?????????Supervisor
	 * ??????0   ????????????0          ????????????
	 */
	public void initExtension(){
		step("????????????1000-1005???fxs 1020");
		apiUtil.deleteAllExtension().apply();
		sleep(WaitUntils.SHORT_WAIT);
		String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");
		apiUtil.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Administrator.toString()).replace("EXTENSIONFIRSTNAME", "test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Supervisor.toString()).replace("EXTENSIONFIRSTNAME", "test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Operator.toString()).replace("EXTENSIONFIRSTNAME", "testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Employee.toString()).replace("EXTENSIONFIRSTNAME", "testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.HumanResource.toString()).replace("EXTENSIONFIRSTNAME", "t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Accounting.toString()).replace("EXTENSIONFIRSTNAME", "First").replace("EXTENSIONLASTNAME", "Last").replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Administrator.toString()).replace("EXTENSIONFIRSTNAME", "0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
				.createExtension(reqDataCreateExtension.replace(ROLE + "",ROLE_ID.Administrator.toString()).replace("EXTENSIONFIRSTNAME", "1030").replace("EXTENSIONLASTNAME", "1030").replace("EXTENSIONNUM", "1030").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
				.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1030", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1000", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1001", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1002", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1003", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1004", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW)
				.loginWebClient("1005", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW).apply();
		if(!FXS_1.trim().equalsIgnoreCase("null")){
			apiUtil.createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", FXS_1).replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList)).apply();
		}
	}

	/**
	 * ??????????????????
	 * ExGroup1???1000 1001
	 * ExGroup2???1000 1002
	 */
	public void initExtensionGroup(){

		step("?????????????????? ExGroup1/ExGroup2");
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
	 * ?????????Trunk
	 */
	public void initTrunk(){
		step("????????? trunk");
		if(!FXO_1.trim().equalsIgnoreCase("null")){
			step("?????? FXO_1,DID:13001");
			apiUtil.editFXOTrunk(FXO_1,String.format("\"did\":\"13001\"")).apply();
		}

		if(!DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null") ||!DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") ||!GSM.trim().equalsIgnoreCase("null")){
			step("?????? GSM,DID:7"+ DEVICE_ASSIST_GSM);
			apiUtil.editGSMTrunk(GSM,String.format("\"did\":\"7"+DEVICE_ASSIST_GSM+"\"")).apply();
		}

		step("??????SPS1,SIPTrunk,account6700,SIP2(?????????)??????");
		apiUtil.deleteTrunk(SPS).deleteTrunk(SIPTrunk2).deleteTrunk(SIPTrunk).deleteTrunk(ACCOUNTTRUNK).
				createSIPTrunk(reqDataCreateSPS_2).
				createSIPTrunk(reqDataCreateS_SIPTrunk).
				createSIPTrunk(reqDataCreateS_SIPTrunk2).
				createSIPTrunk(reqDataCreateS_account).apply();
	}

	/**
	 * ?????????IVR
	 * ?????????6200
	 * ?????????IVR0
	 * ????????????0?????????A???
	 * ????????????Dial Outbound Routes?????????????????????
	 */
	public void initIVR(){
		step("??????IVR 6200");
		ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
		pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
		apiUtil.deleteAllIVR().createIVR("6200", "IVR0", pressKeyObjects_0)
				.editIVR("6200",String.format("\"enb_dial_outb_routes\":1,\"dial_outb_route_list\":[{\"text\":\"Out1\",\"value\":\"%s\"},{\"text\":\"Out2\",\"value\":\"%s\"},{\"text\":\"Out3\",\"value\":\"%s\"},{\"text\":\"Out4\",\"value\":\"%s\"},{\"text\":\"Out5\",\"value\":\"%s\"},{\"text\":\"Out6\",\"value\":\"%s\"},{\"text\":\"Out7\",\"value\":\"%s\"},{\"text\":\"Out8\",\"value\":\"%s\"},{\"text\":\"Out9\",\"value\":\"27\"}]",
						apiUtil.getOutBoundRouteSummary("Out1").id,apiUtil.getOutBoundRouteSummary("Out2").id,apiUtil.getOutBoundRouteSummary("Out3").id,apiUtil.getOutBoundRouteSummary("Out4").id,apiUtil.getOutBoundRouteSummary("Out5").id,apiUtil.getOutBoundRouteSummary("Out6").id,apiUtil.getOutBoundRouteSummary("Out7").id,apiUtil.getOutBoundRouteSummary("Out8").id)).apply();
	}

	/**
	 * ??????????????????
	 * ?????????6300
	 * ?????????RingGroup0
	 * ?????????ExGroup1???1003
	 * ??????????????????????????????????????????10??????Failover?????????1000?????????????????????
	 */
	public void initRingGroup(){
		step("???????????????6300 ");
		ArrayList<String> ringGroupMembers0 = new ArrayList<>();

		ringGroupMembers0.add("ExGroup1");
		ringGroupMembers0.add("1003");

		apiUtil.deleteAllRingGroup().createRingGroup("RingGroup0", "6300", ringGroupMembers0, 10, "extension", "", "1000").apply();

	}

	/**
	 * ??????????????????
	 * ?????????6500
	 * ?????????Conference0
	 * ?????????1000
	 * ??????????????????
	 */
	public void initConference(){
		step("???????????????");

		ArrayList<String> conferenceMember = new ArrayList<>();
		conferenceMember.add("1000");
		apiUtil.deleteAllConference().createConference("Conference0", "6500", conferenceMember).apply();

	}

	/**
	 * ???????????????
	 * ?????????6400
	 * ?????????Queue0
	 * ???????????????1000 1001
	 * ???????????????1003 1004
	 * ??????????????????60s???Failover?????????1000??????Key 0 ?????????1001???
	 * ??????????????????
	 */
	public void initQueue(){
		step("????????????6400");

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
	 * ?????????????????????
	 * ?????????In1
	 * ???????????????????????????????????????????????????1000???????????????
	 */
	public void initInbound(){
		step("??????????????????InRoute3,?????????????????????6300");

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
	 * ?????????????????????
	 * Out1??????????????????1.  ??????????????????1??????????????????sip1?????????????????????
	 * Out2??????????????????2. ??????????????????1????????????sps?????????????????????
	 * Out3??????????????????3. ??????????????????1????????????account?????????????????????
	 * Out4??????????????????4.??????????????????1????????????FXO?????????????????????
	 * Out5??????????????????5. ??????????????????1????????????BRI?????????????????????
	 * Out6??????????????????6.??????????????????1????????????E1?????????????????????
	 * Out7??????????????????7.??????????????????1????????????LTE/GSM?????????????????????
	 * Out8??????????????????X. ??????????????????????????????sps?????????????????????A
	 * Out9??????????????????X.???????????????????????????????????????????????????
	 */
	public void initOutbound(){

		step("??????????????????");

		List<String> extensionNum = new ArrayList<>();
		List<String> extensionNumA = new ArrayList<>();

		extensionNum.add("Default_Extension_Group");
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
		trunk8.add(SPS);
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

	/**
	 * ?????????????????????1000?????????????????????
	 */
	public void initFeatureCode(){
		step("Feature Code ?????????????????????1000?????????????????????");
		apiUtil.editFeatureCode(String.format("\"enb_office_time\":1,\"office_time_permit_list\":[{\"value\":\"%s\",\"type\":\"extension\",\"text\":\"test A\",\"text2\":\"1000\"}]",apiUtil.getExtensionSummary("1000").id)).apply();
	}

	/**
	 * Before
	 * 	System-???Storage-???????????????????????????-?????????????????????record
	 * 		?????????????????????????????????????????????????????????
	 * 		??????????????????????????????????????????
	 * 	????????????A-???Features-?????????Allow the extension to stop or restart call recording during a call ?????????????????????????????????
	 * 	??????????????????????????????????????????????????????
	 */
	public void initRecord(){
		//System-???Storage-???????????????????????????-?????????????????????record
		apiUtil.editStoragelocation(String.format("\"recording\":0")).deleteAllNetWorkStorage().
				createStorage(String.format("\"name\":\"record\",\"host\":\"%s\",\"share_name\":\"%s\",\"username\":\"%s\",\"password\":\"cQ==\",\"work_group\":\"\",\"samba_version\":\"auto\"",NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,NETWORK_DEVICE_USER_NAME, md5Hex(NETWORK_DEVICE_USER_PASSWORD))).apply();
//                createStorage("\"name\":\"record\",\"host\":\"192.168.3.5\",\"share_name\":\"record\",\"username\":\"\",\"password\":\"\",\"work_group\":\"\",\"samba_version\":\"auto\"").apply();
		String request = String.format("\"recording\":%d",apiUtil.getStorageObjectSummary("record"));
		apiUtil.editStoragelocation(request).apply();
		//????????????A-???Features-?????????Allow the extension to stop or restart call recording during a call ?????????????????????????????????
		apiUtil.editExtension("1000","\"enb_ctl_record\":1").apply();
	}

	public boolean registerAllExtensions() {
		log.debug("[prerequisite] init extension");
		pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(2001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(3001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(3002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(4000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(4001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
		pjsip.Pj_CreateAccount(4002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

		pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(1005, DEVICE_IP_LAN);
		pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);
		pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
		pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);
		pjsip.Pj_Register_Account_WithoutAssist(3002, DEVICE_ASSIST_1);
		pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
		pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_ASSIST_3);
		pjsip.Pj_Register_Account_WithoutAssist(4002, DEVICE_ASSIST_3);

		boolean reg = false;
		if (getExtensionStatus(1000, IDLE, 5) != IDLE) {
			reg = true;
			log.error("1000????????????");
		}
		if (getExtensionStatus(1001, IDLE, 5) != IDLE) {
			reg = true;
			log.error("1001????????????");
		}
		if (getExtensionStatus(1002, IDLE, 5) != IDLE) {
			reg = true;
			log.error("1002????????????");
		}
		if (getExtensionStatus(1003, IDLE, 5) != IDLE) {
			reg = true;
			log.error("1003????????????");
		}
		if (getExtensionStatus(1004, IDLE, 5) != IDLE) {
			reg = true;
			log.error("1004????????????");
		}
		if (getExtensionStatus(2000, IDLE, 5) != IDLE) {
			reg = true;
			log.error("2000????????????");
		}
		if (getExtensionStatus(2001, IDLE, 5) != IDLE) {
			reg = true;
			log.error("2001????????????");
		}
		if (getExtensionStatus(3001, IDLE, 5) != IDLE) {
			reg = true;
			log.error("3001????????????");
		}
		if (getExtensionStatus(4000, IDLE, 5) != IDLE) {
			reg = true;
			log.error("4000????????????");
		}
		if (getExtensionStatus(4001, IDLE, 5) != IDLE) {
			reg = true;
			log.error("4001????????????");
		}if (getExtensionStatus(4002, IDLE, 5) != IDLE) {
			reg = true;
			log.error("4002????????????");
		}
		if(reg){
			pjsip.Pj_Unregister_Accounts();
		}else{

			String queueInfo = execAsterisk("queue show queue-"+queueNum0);

			if (queueInfo != null && !queueInfo.contains("1003")){
				step("1003??????*76400?????????Queue0");
				pjsip.Pj_Make_Call_No_Answer(1003,  "*76400", DEVICE_IP_LAN, false);
				getExtensionStatus(1003, HUNGUP, 10);
			}

			if (queueInfo != null && !queueInfo.contains("1004")){
				step("1004??????*76400?????????Queue0");
				pjsip.Pj_Make_Call_No_Answer(1004,  "*76400", DEVICE_IP_LAN, false);
				getExtensionStatus(1004, HUNGUP, 10);
			}
		}
		return reg;
	}

	/**
	 * ????????? ?????????
	 * 	??????prompt1?????????prompt2?????????prompt3,??????prompt4,??????prompt5,??????prompt6
	 * 		2020.11.18 ???????????????6???????????????21???
	 * 	??????VoicemailMaxDuration30.wav
	 * 		2020-12-16 ??????
	 * 	?????????
	 * 	192.168.3.252 ?????????????????? ?????? /home/autotest/pseries/wav/record/?????????prompt1~prompt6.wav???VoicemailMaxDuration30.wav???
	 */
	public void initPrompt(){
		log.debug("[prerequisite] init prompt");
		String COMMAND = "sshpass -p '' scp -r -P "+SSH_PORT+" /home/autotest/pseries/wav/record/ "+USERNAEM_LS+"@"+DEVICE_IP_LAN+":/ysdisk/ysapps/pbxcenter/var/lib/asterisk/sounds/";
		try {
			SSHLinuxUntils.exeCommand(GRID_HUB_IP,22,"root","r@@t",COMMAND);
		} catch (JSchException e) {
			log.error("[ERROR initPrompt exception]"+e.getMessage());
		} catch (IOException e) {
			log.error("[ERROR initPrompt exception]"+e.getMessage());
		}
	}
}
