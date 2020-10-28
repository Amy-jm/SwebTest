package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.CallFeatures.IIVRPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD_DETAILS;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.APIObject.IVRObject.PressKey;
import com.yeastar.untils.APIObject.IVRObject.PressKeyObject;
import io.qameta.allure.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
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

/********* 用例与 prompt的时长密切相关*********
 *  prompt1.wav   9 秒
 *  prompt2.wav   2 秒
 *  prompt3.wav   3 秒
 *  prompt4.wav   4 秒
 *  prompt5.wav   3 秒
 */
@Log4j2
public class TestIVR extends TestCaseBaseNew{
    private boolean isRunRecoveryEnvFlag = true;

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();


    private String IVR_GREETING_DIAL_EXT= "ivr-greeting-dial-ext.slin";
    private String PROMPT_1 = "prompt1.slin";

    APIUtil apiUtil = new APIUtil();
    ArrayList<String> ringGroupNum_1;
    ArrayList<String> queueListNum_1;
    ArrayList<String> IVRList_0;
    ArrayList<String> IVRList_1;
    String queueListName_1 = "Q1:";
    String ringGroupName_1 = "RG1:";//6301
    String IVRListName_0 = "ivr_6200:";//6200
    String cdrIVR1_6201 ="IVR IVR1_6201<6201>";//6201
    String  invalidKey = "Invalid key";
    Object[][] routes = new Object[][]{

            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"},//sps   前缀 替换
            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "BRI"},//BRI   前缀 替换
            {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "FXO"},//FXO --77 不输   2005（FXS）
            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "E1"},//E1     前缀 替换
            {"",   3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_REGISTER"},//SIP  --55 REGISTER
            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_ACCOUNT"},
//            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
    };

    /**
     * 多线路测试数据
     * routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + vcpCaller（VCP列表中显示的主叫名称） + vcpDetail（VCP中显示的Detail信息） + testRouteTypeMessage（路由类型）
     *
     * @return
     */
    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        if(methodName.equals("testIVRBaseTrunk_1") || methodName.equals("testPPRD_1") || methodName.equals("testPPRD_2") || methodName.equals("testPPRD_3") || methodName.equals("testPPRD_4") || methodName.equals("testPPRD_5") ||
           methodName.equals("testIVRBaseTrunk_1") || methodName.equals("testPPRD_1_2")){
            return  new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(),SIPTrunk,"SIP_REGISTER"}};
        }
        if (methodName.equals("testIVRBaseTrunk_2") || methodName.equals("testDialExtension_2") || methodName.equals("testDialExtension_3") || methodName.equals("testDialExtension_4") || methodName.equals("testDialExtension_5") || methodName.equals("testDialExtension_9") ||
            methodName.equals("testDialExtension_10") || methodName.equals("testDialExtension_11") || methodName.equals("testDialExtension_13") || methodName.equals("testDialExtension_15") || methodName.equals("testDialExtension_16")){
            return  new Object[][]{{"99", 2000, "6201", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(),SPS, "SPS"}};
        }
        if(methodName.equals("testTrunk")){
            Object[][] routes = new Object[][]{
                    {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),BRI_1, "BRI"},//BRI   前缀 替换
                    {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),FXO_1,"FXO"},//FXO --77 不输   2005（FXS）
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),E1, "E1"},//E1     前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),ACCOUNTTRUNK ,"SIP_ACCOUNT"},
                    {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),GSM,"GSM"}
            };
            return routes;
        }
        else {
            for (String groups : c.getIncludedGroups()) {
                for (int i = 0; i < routes.length; i++) {
                    for (int j = 0; j < routes[i].length; j++) {
                        if (groups.equalsIgnoreCase("SPS")) {
                            group = new Object[][]{{"99", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"}};
                        } else if (groups.equalsIgnoreCase("BRI")) {
                            group = new Object[][]{{"88", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "BRI"}};
                        } else if (groups.equalsIgnoreCase("FXO")) {
                            group = new Object[][]{{"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "FXO"}};
                        } else if (groups.equalsIgnoreCase("E1")) {
                            group = new Object[][]{{"66", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "E1"}};
                        } else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                            group = new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_REGISTER"}};
                        } else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                            group = new Object[][]{{"44", 4000, "6200", DEVICE_ASSIST_3, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_ACCOUNT"}};
                        } else if (groups.equalsIgnoreCase("GSM")) {
                            group = new Object[][]{{"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM + " [" + DEVICE_ASSIST_GSM + "]", RECORD_DETAILS.EXTERNAL.getAlias(), "GSM"}};
                        } else {
                            group = routes;
                        }
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
    private boolean runRecoveryEnvFlag = true;
    private ArrayList<String> IVRMember = new ArrayList<>();
    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));



    private boolean registerAllExtension(){
        log.debug("[prerequisite] init extension");
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1012,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
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
        pjsip.Pj_Register_Account_WithoutAssist(1012,DEVICE_IP_LAN);
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
        return reg;
    }
    private boolean isDebugInitExtensionFlag = true;
    public void prerequisite() {
        //local debug
        if(isDebugInitExtensionFlag){
            log.debug("*****************init extension************");
            registerAllExtension();
            isRunRecoveryEnvFlag = false;
        }

        long startTime=System.currentTimeMillis();
        if (isRunRecoveryEnvFlag) {
//            apiUtil = new APIUtil();
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
            IVRList_0 = new ArrayList<>();
            IVRList_1 = new ArrayList<>();
            ringGroupNum_1 = new ArrayList<>();
            queueListNum_1 = new ArrayList<>();

            step("创建分机组 ExGroup1/ExGroup2");
            List<String> extensionExGroup1 = new ArrayList<>();
            List<String> extensionExGroup2 = new ArrayList<>();

            extensionExGroup1.add("1000");
            extensionExGroup1.add("1001");

            extensionExGroup2.add("1002");
            extensionExGroup2.add("1003");
            //todo 分机组ExGroup1，ExGroup2 没有号码
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
                    createExtensionGroup("ExGroup1",extensionExGroup1).
                    createExtensionGroup("ExGroup2",extensionExGroup2).apply();
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"Manager\"");

            extensionNum.add("0");
            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");

            extensionNumA.add("1000");


            step("创建分机1000-1010");
            apiUtil.deleteAllExtension().apply();
            sleep(WaitUntils.SHORT_WAIT);
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME","0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
//                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME","FXS").replace("EXTENSIONLASTNAME", "FXS").replace("FXSPORT","1-4").replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME","1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT","1-3").replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2);

            step("创建 IVR1_6201");
            ArrayList<PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new PressKeyObject(PressKey.press0, "extension", "", "1000", 0));
//            pressKeyObjects.add(new PressKeyObject(PressKey.press1,"end_call","","1002",0));

            ArrayList<PressKeyObject> pressKeyObjects_1 = new ArrayList<>();
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press0, "extension", "", "1001", 0));

            apiUtil.deleteAllIVR().createIVR("6201", "IVR1_6201", pressKeyObjects_0);

            step("创建呼入路由InRoute3,目的地到IVR 6200");
            apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "IVR", "6201");

            step("创建呼出路由");
            apiUtil.deleteAllOutbound().createOutbound("Out1", trunk1, extensionNum,"1.",1).
                                        createOutbound("Out2", trunk2, extensionNum,"2.",1).
                                        createOutbound("Out3", trunk3, extensionNum,"3.",1).
                                        createOutbound("Out4", trunk4, extensionNum,"4.",1).
                                        createOutbound("Out5", trunk5, extensionNum,"5.",1).
                                        createOutbound("Out6", trunk6, extensionNum,"6.",1).
                                        createOutbound("Out7", trunk7, extensionNum,"7.",1).
                                        createOutbound("Out8", trunk8, extensionNumA).
                                        createOutbound("Out9", trunk9, extensionNum);


            step("删除响铃组，队列");
            apiUtil.deleteAllRingGroup().deleteAllQueue().deleteAllConference();

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            isRunRecoveryEnvFlag = registerAllExtension();
        }
        log.debug("[prerequisite time]:"+(System.currentTimeMillis()-startTime)/1000+" Seconds");
    }

    public void restIVR(){
        ArrayList<PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
        pressKeyObjects_0.add(new PressKeyObject(PressKey.press0, "extension", "", "1000", 0));

        apiUtil.deleteIVR("IVR1_6201").createIVR("6201", "IVR1_6201", pressKeyObjects_0).apply();
        step("创建呼入路由InRoute3,目的地到IVR 6200");
        List<String> trunk9 = new ArrayList<>();
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
        apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "IVR", "6201").apply();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Trunk")
    @Description("1.通过外线呼入到IVR1按0到分机A\n" +
            "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","Trunk", "testTrunk", "SPS", "BRI", "FXO", "FXS", "E1", "SIP_REGISTER", "SIP_ACCOUNT"}, dataProvider = "routes")
    public void testTrunk(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail,String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:【2000 呼叫 6201】，press 0 ->1000 为Ringing状态，接听，挂断");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "0");
        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1000,false);
        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1000);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        if(message.equals("SPS")){
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
        }
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("1.通过sip外线呼入到IVR1按0到分机A")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P1", "IVR", "Basic", "Trunk", "testIVRBaseTrunk_1"}, dataProvider = "routes")
    public void testIVRBaseTrunk_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:通过sip外线呼入到IVR1按0到分机A,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "0");
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT );
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,5)).as("[通话状态校验]").isEqualTo(3);

        pjsip.Pj_hangupCall(1000);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("通过sip外线呼入到IVR1按0到分机A")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P1", "IVR", "Basic", "Trunk", "testIVRBaseTrunk_2"}, dataProvider = "routes")
    public void testIVRBaseTrunk_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.通过sps外线呼入到IVR1按分机号1001到分机B,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT );
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING,20)).as("[通话状态校验]").isEqualTo(3);

        pjsip.Pj_hangupCall(1001);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验_2] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("通过sip外线呼入到IVR1\n" +
            "1.进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin\n" +
            "2.进入asterisk检查播放提示音文件的间隔为3秒\n" +
            "3.进入asterisk检查播放3遍后再等待3秒通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P2", "IVR", "Prompt","testPPRD_1","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        new Thread(new AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT)).start(); //启动子线程，监控asterisk log

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() !=3 && tmp <= 600){
           sleep(50);
           tmp++;
           log.debug("[tmp]_"+tmp);
        }
        sleep(3000);

        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin 播放3遍提示音后挂断");
        softAssertPlus.assertThat(getExtensionStatus(caller,HUNGUP,10)).as("[Asterisk校验] 播放3遍提示音后挂断 "+ DataUtils.getCurrentTime()).isEqualTo(4);

        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin");
        softAssertPlus.assertThat(asteriskObjectList.get(0).getName()).as("[Asterisk校验] Time："+ DataUtils.getCurrentTime()).contains("ivr-greeting-dial-ext.slin");

//        log.debug("[asterisk object size] "+ asteriskObjectList.size());
//        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
//            log.debug(i+"_[asterisk object name] "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
//        }
//
        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin 3遍");
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[Asterisk校验] Time："+ DataUtils.getCurrentTime()).isEqualTo(3);
        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("通过sip外线呼入到IVR1\n" +
            "进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin第2遍时，按0到分机A")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P2", "IVR", "Prompt","testPPRD_2","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_1_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        //启动子线程，监控asterisk log
        List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
        Thread thread = new Thread(new AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT));
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 2 && tmp++ <= 600){
            sleep(50);
        }


        pjsip.Pj_Send_Dtmf(caller,"0");
        assertStep("[Asterisk校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,10)).as("[Asterisk校验] 播放2 遍提示音后，按0，转分机 "+ DataUtils.getCurrentTime()).isEqualTo(2);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);


        log.debug("[asterisk object size] "+ asteriskObjectList.size());
        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_[asterisk object name] "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }




    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("1.通过sip外线呼入到IVR1\n" +
                 "2.编辑IVR1,Prompt选择【None】，PromptRepeatCount 选择1，ResponseTimeout选择1，DigitTimeout选择1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testPPRD_2","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        new Thread(new AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT)).start();


        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1,Prompt选择【None】，PromptRepeatCount 选择1，ResponseTimeout选择1，DigitTimeout选择1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        auto.ivrPage().editIVR("IVR1_6201").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"None").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"1").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        //todo 时间太短无法校验，后续通过解析asterisk进行校验
        //softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,3)).as("[通话状态校验] Time："+ DataUtils.getCurrentTime()).isEqualTo(3);

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","callDuration","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "1","ANSWERED", "IVR IVR1_6201<6201> timed out, hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }
    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("1.通过sip外线呼入到IVR1\n" +
            "3.编辑IVR1,Prompt选择prompt1，PromptRepeatCount 选择2，ResponseTimeout选择5，DigitTimeout选择1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testPPRD_3","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        asteriskObjectList.clear();
        new Thread(new AsteriskThread(asteriskObjectList,PROMPT_1)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1,Prompt选择prompt1，PromptRepeatCount 选择2，ResponseTimeout选择5，DigitTimeout选择1");
        auto.ivrPage().editIVR("IVR1_6201").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"prompt1.wav").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"2").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"1").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep("[asterisk]播放第2遍prompt1时，等3秒按0");
        int tmp = 0;
        long startTime = System.currentTimeMillis();
        while (asteriskObjectList.size() != 2 && tmp++ <= 600){//30s
            sleep(50);
        }
        log.debug("[TestCase while] "+(System.currentTimeMillis() - startTime)/1000+" Seconds");
        if(tmp == 600){
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]");
        }

        step("[Sleep] 15s prompt1(9s) + ResponseTimeout(5s)+DigitTimeout(1s)");   //   9s(prompt1) < sleep <= 14s(timeout)
        sleep(9000); //send dtmf 响应 5s 左右  ；sleep 11 s 概率性可以

        assertStep("[通话状态校验]");
        pjsip.Pj_Send_Dtmf(caller,"0");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),2,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
//        softAssertPlus.assertThat(getExtensionStatus(1000,RING,5)).as("[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime()).isEqualTo(2);
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,5),3,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
//        softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,5)).as("[通话状态校验_通话] Time："+ DataUtils.getCurrentTime()).isEqualTo(3);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR显示]");
        assertStep("[asterisk]检查播放提示音文件prompt1");
        softAssertPlus.assertThat(asteriskObjectList.get(0).getName()).as("[检查播放提示音文件prompt1] Time："+ DataUtils.getCurrentTime()).contains("prompt1.slin");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201,"ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        log.debug("[asterisk object size 第二遍] " + tmp + "----------" + asteriskObjectList.size());
        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }
        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("1.通过sip外线呼入到IVR1\n" +
            "4.编辑IVR1,Prompt选择prompt1~prompt5，PromptRepeatCount 选择5，ResponseTimeout设置10，DigitTimeout设置10")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testPPRD_4","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        new Thread(new AsteriskThread(asteriskObjectList,"Playing 'record/prompt")).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1,Prompt选择prompt1~prompt5，PromptRepeatCount 选择5，ResponseTimeout设置10，DigitTimeout设置10");
        auto.ivrPage().editIVR("IVR1_6201").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"prompt1.wav,prompt2.wav,prompt3.wav,prompt4.wav,prompt5.wav").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"10").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"10").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ <= 600){
            sleep(50);
        }
        if(tmp == 600){
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]");
        }

        step("播放第5遍时按分机1002，每发送一个dtmf时间隔8秒");//send dtmf 5s
        pjsip.Pj_Send_Dtmf(caller,"1");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(caller,"0");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(caller,"0");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(caller,"2");
        sleep(4000);
        pjsip.Pj_Send_Dtmf(caller,"#");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002,RING,5),2,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());        pjsip.Pj_Answer_Call(1002,false);
        pjsip.Pj_Answer_Call(1002,false);
        Assert.assertEquals(getExtensionStatus(1002,TALKING,5),3,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(3000);
        pjsip.Pj_hangupCall(1002);

        assertStep("[Asterisk校验] 检查播放提示音文件prompt1~prompt5");
        softAssertPlus.assertThat(asteriskObjectList).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("keyword")
                .contains("record/prompt1.slin", "record/prompt2.slin", "record/prompt3.slin", "record/prompt4.slin", "record/prompt5.slin");

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "testta C<1002>", "ANSWERED", "testta C<1002> hung up",trunk,"","Inbound"));
        softAssertPlus.assertAll();

        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }
    }
    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("通过sip外线呼入到IVR1\n" +
            "5.编辑IVR1，Prompt选择【Default】,PromptRepeatCount 选择3，ResponseTimeout设置3，DigitTimeout设置3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testPPRD_5","PromptRepeatCount","ResponseTimeout","DigitTimeout"}, dataProvider = "routes")
    public void testPPRD_5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        new Thread(new AsteriskThread(asteriskObjectList,IVR_GREETING_DIAL_EXT)).start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1，Prompt选择【Default】,PromptRepeatCount 选择3，ResponseTimeout设置3，DigitTimeout设置3");
        auto.ivrPage().editIVR("IVR1_6201").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp++ <= 600){
            sleep(50);
        }
        if(tmp == 600){
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]");
        }
        pjsip.Pj_Send_Dtmf(caller,"0");
        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin");

        assertStep("[通话状态校验]");
        pjsip.Pj_Send_Dtmf(caller,"0");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),2,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,5),3,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin");
        softAssertPlus.assertThat(asteriskObjectList.get(0).getKeyword()).as("[Asterisk校验] Time："+ DataUtils.getCurrentTime()).contains("ivr-greeting-dial-ext.slin");

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.通过sip外线呼入到IVR1按1020到分机FXS\n" +
                 "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_1"}, dataProvider = "routes")
    public void testDialExtension_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:通过sip外线呼入到IVR1按1020到分机FXS");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","2","0","#");
        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1020,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1000,false);
        softAssertPlus.assertThat(getExtensionStatus(1020,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1000);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，禁用Dial Extensions，选择disable\n" +
                 "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
                 "3.分机B不会响铃，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_2"}, dataProvider = "routes")
    public void testDialExtension_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，禁用Dial Extensions，选择disable");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.DISABLE.getAlias()).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验]分机B不会响铃，通话被挂断");
        Assert.assertEquals(getExtensionStatus(1001, IDLE,5),0,"[通话校验]");
        sleep(10000);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", invalidKey ,trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1000\n" +
            "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_3"}, dataProvider = "routes")
    public void testDialExtension_3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        restIVR();  //重置ivr and inbound

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight(new String[]{"ExGroup1","1001","1003"}).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1000");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","0","#");
        step("[通话状态校验] 分机A响铃，可正常接听，分机1000挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,5)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1000,false);
        softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,5)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1000);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
                 "2.通过sps外线呼入到IVR1，按分机号1002\n" +
                 "3.分机1002不会响铃，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_4"}, dataProvider = "routes")
    public void testDialExtension_4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        restIVR();  //重置ivr and inbound

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight(new String[]{"ExGroup1","1001","1003"}).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1002");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","2","#");
        step("[通话状态校验] 分机1002不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1002,IDLE,60)).as("[通话校验]").isEqualTo(0);
        sleep(10000);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", invalidKey ,trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
                 "2.通过sps外线呼入到IVR1，按分机号1001\n" +
                 "3.分机1001响铃，接听，外线主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_5"}, dataProvider = "routes")
    public void testDialExtension_5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        restIVR();  //重置ivr and inbound

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight(new String[]{"ExGroup1","1001","1003"}).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验] 分机1001响铃，接听，外线主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(caller);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机1003响铃，接听，1004主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_6"})
    public void testDialExtension_6() {
        prerequisite();
        restIVR();  //重置ivr and inbound

        step("1:login with admin,trunk: 内部分机");
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight(new String[]{"ExGroup1","1001","1003"}).
                clickSaveAndApply();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "1001", DEVICE_IP_LAN, false);

        pjsip.Pj_Send_Dtmf(1004, "1","0","0","3","#");
        step("[通话状态校验] 分机1003响铃，接听，1004主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003,RING,5)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1003,false);
        softAssertPlus.assertThat(getExtensionStatus(1003,TALKING,5)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1004);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("1004<1004>", cdrIVR1_6201, "ANSWERED", "1004<1004> dialed Extension","E","","Inbound"))
                .contains(tuple("1004<1004>", "test A<1000>", "ANSWERED", "1004<1004> hung up","E","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
                 "编辑分机选择去掉1001、1003"+
                 "2.7.通过sps外线呼入到IVR1，按分机号1001\n" +
                 "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_7"}, dataProvider = "routes")
    public void testDialExtension_7(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();
        restIVR();  //重置ivr and inbound

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1_6201").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight(new String[]{"ExGroup1"}).
                clickSaveAndApply();

        step("3:7.通过sps外线呼入到IVR1，按分机号1001");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验] 分机B不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001,IDLE,10)).as("[通话校验]").isEqualTo(0);


        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1_6201, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
                 "编辑分机选择去掉1001、1003"+
                 "2.内部分机1004呼入到IVR1，按分机号1003\n" +
                 "3.分机D不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_2"}, dataProvider = "routes")
    public void testDialExtension_8(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:8.内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","3","#");
        step("[通话状态校验] 分机D不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1003,IDLE,10)).as("[通话校验]").isEqualTo(0);


        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过外线sps外线呼入到IVR1，按分机号1000\n" +
            "3.分机A不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_9"}, dataProvider = "routes")
    public void testDialExtension_9(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过外线sps外线呼入到IVR1，按分机号1000");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","0","#");
        step("[通话状态校验] 分机A不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,60)).as("[通话校验]").isEqualTo(2);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1002\n" +
            "3.分机C响铃，可正常接听，挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_10"}, dataProvider = "routes")
    public void testDialExtension_10(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1002");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","2","#");
        step("[通话状态校验] 分机C响铃，可正常接听，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1002,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1002);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1001\n" +
            "3.分机B不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_11"}, dataProvider = "routes")
    public void testDialExtension_11(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1001");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验] 分机B不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001,IDLE,5)).as("[通话校验]").isEqualTo(0);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机D不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_12"}, dataProvider = "routes")
    public void testDialExtension_12(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验] 分机D不会响铃，通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(1003,IDLE,60)).as("[通话校验]").isEqualTo(0);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003\n" +
            "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
            "3.分机1001响铃，接听，外线主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_13"}, dataProvider = "routes")
    public void testDialExtension_13(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1001");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验] 分机1001响铃，接听，外线主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(caller);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机1003响铃，接听，1004主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_14"}, dataProvider = "routes")
    public void testDialExtension_14(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "6201", deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","3","#");
        step("[通话状态校验] 分机1003响铃，接听，1004主叫挂断，检查cdr ");
        softAssertPlus.assertThat(getExtensionStatus(1003,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1003,false);
        softAssertPlus.assertThat(getExtensionStatus(1003,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1004);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAll Extensions\n" +
            "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
            "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialExtension_15"}, dataProvider = "routes")
    public void testDialExtension_15(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAll Extensions");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1","#");
        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,60)).as("[通话校验]").isEqualTo(2);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,60)).as("[通话校验]").isEqualTo(3);
        pjsip.Pj_hangupCall(1001);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAll Extensions\n" +
            "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
            "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialExtension_2"}, dataProvider = "routes")
    public void testDialExtension_16(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAll Extensions");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
//        auto.ivrPage().editIVR("IVR1_6201").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
//                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
//                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按queue0的号码6400");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "6","4","0","0","#");
        step("[通话状态校验] 通话被挂断");
        softAssertPlus.assertThat(getExtensionStatus(caller,HUNGUP,5)).as("[通话校验]").isEqualTo(4);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("1.\n" +
            "2.\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testDialOutboundRoutes_","Trunk","OutboundRoute"}, dataProvider = "routes")
    public void testDialOutboundRoutes_(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "6","4","0","0","#");
        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(caller,HUNGUP,5)).as("[通话校验]").isEqualTo(4);

        step("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple("2000<2000>".replace("2000",caller+""), cdr_IVR, "ANSWERED", "2000<2000> called Extension","SPS1","","Inbound"))
//                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up","SPS1","","Inbound"));

        softAssertPlus.assertAll();
    }



    /**
     * 子线程，监控 asterisk的日志输出
     */
    class AsteriskThread extends Thread {
        List<AsteriskObject> asteriskObject;
        String asteriskKey;

        AsteriskThread(List<AsteriskObject> asteriskObject,String asteriskKey) {
            this.asteriskObject=asteriskObject;
            this.asteriskKey=asteriskKey;
        }

        public void run() {
         log.debug("[PbxLog Thread]" + "正在执行...");
//         SSHLinuxUntils.getPbxlog("ivr-greeting-dial-ext.slin",1,60,asteriskObject);
            SSHLinuxUntils.getPbxlog(asteriskKey,1,12000,asteriskObject);//2分钟 1200 * 100
        }
    }
}

