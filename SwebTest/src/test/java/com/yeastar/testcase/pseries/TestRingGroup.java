package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.APIObject.IVRObject;
import com.yeastar.untils.*;
import com.yeastar.untils.APIObject.MenuOptionObject;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP;
import static org.assertj.core.api.Assertions.tuple;

@Log4j2
public class TestRingGroup extends TestCaseBaseNew {

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean runRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = true;

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

    private final String cdrRingGroup1 = String.format("RingGroup %s<%s>", ringGroupName1, ringGroupNum1);
    private final String ext_1000 = "test A<1000>";
    private final String ext_1001 = "test2 B<1001>";
    private final String ext_1002 = "testta C<1002>";
    private final String ext_1003 = "testa D<1003>";
    private final String ext_1004 = "t estX<1004>";

    APIUtil apiUtil = new APIUtil();
    ArrayList<String> queueStaticMembers;
    ArrayList<String> queueDynamicMembers;
    ArrayList<String> queueMembers;
    ArrayList<String> ringGroupMembers0;
    ArrayList<String> ringGroupMembers1;
    ArrayList<String> ringGroupExtGroupMembers0;
    ArrayList<String> ringGroupExtGroupMembers1;
    ArrayList<String> conferenceMember;

    Object[][] routes = new Object[][]{

            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), SPS, "SPS"},//sps   前缀 替换
//            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(),BRI_1, "BRI"},//BRI   前缀 替换
//            {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(),FXO_1, "FXO"},//FXO --77 不输   2005（FXS）
//            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(),E1, "E1"},//E1     前缀 替换
//            {"",   3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", EXTERNAL_RING_GROUP.getAlias(),SIPTrunk, "SIP_REGISTER"},//SIP  --55 REGISTER
//            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", EXTERNAL_RING_GROUP.getAlias(),ACCOUNTTRUNK, "SIP_ACCOUNT"},
//            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",EXTERNAL_RING_GROUP.getAlias(),GSM,"GSM"}
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
        if (methodName.equals("testIVRBaseTrunk_1") || methodName.equals("testPPRD_1") || methodName.equals("testPPRD_2") || methodName.equals("testPPRD_3") || methodName.equals("testPPRD_4") || methodName.equals("testPPRD_5") ||
                methodName.equals("testIVRBaseTrunk_1") || methodName.equals("testPPRD_1_2")) {
            return new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", EXTERNAL_RING_GROUP.getAlias(), SIPTrunk, "SIP_REGISTER"}};
        }
        if (methodName.equals("testIVRBaseTrunk_2") || methodName.equals("testDialExtension_2") || methodName.equals("testDialExtension_3") || methodName.equals("testDialExtension_4") || methodName.equals("testDialExtension_5") || methodName.equals("testDialExtension_9") ||
                methodName.equals("testDialExtension_10") || methodName.equals("testDialExtension_11") || methodName.equals("testDialExtension_13") || methodName.equals("testDialExtension_15") || methodName.equals("testDialExtension_16")) {
            return new Object[][]{{"99", 2000, "6201", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), SPS, "SPS"}};
        }
        if (methodName.equals("testTrunk")) {
            Object[][] routes = new Object[][]{
                    {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), BRI_1, "BRI"},//BRI   前缀 替换
                    {"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), FXO_1, "FXO"},//FXO --77 不输   2005（FXS）
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), E1, "E1"},//E1     前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", EXTERNAL_RING_GROUP.getAlias(), ACCOUNTTRUNK, "SIP_ACCOUNT"},
                    {"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM + " [" + DEVICE_ASSIST_GSM + "]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(), GSM, "GSM"},
            };
            return routes;
        } else {
            for (String groups : c.getIncludedGroups()) {
                for (int i = 0; i < routes.length; i++) {
                    for (int j = 0; j < routes[i].length; j++) {
                        if (groups.equalsIgnoreCase("SPS")) {
                            group = new Object[][]{{"99", 2000, "6301", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), SPS, "SPS"}};
                        } else if (groups.equalsIgnoreCase("BRI")) {
                            group = new Object[][]{{"88", 2000, "6301", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), BRI_1, "BRI"}};
                        } else if (groups.equalsIgnoreCase("FXO")) {
                            group = new Object[][]{{"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), FXO_1, "FXO"}};
                        } else if (groups.equalsIgnoreCase("E1")) {
                            group = new Object[][]{{"66", 2000, "6301", DEVICE_ASSIST_2, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), E1, "E1"}};
                        } else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                            group = new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", EXTERNAL_RING_GROUP.getAlias(), SIPTrunk, "SIP_REGISTER"}};
                        } else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                            group = new Object[][]{{"44", 4000, "6301", DEVICE_ASSIST_3, "2000 [2000]", EXTERNAL_RING_GROUP.getAlias(), ACCOUNTTRUNK, "SIP_ACCOUNT"}};
                        } else if (groups.equalsIgnoreCase("GSM")) {
                            group = new Object[][]{{"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM + " [" + DEVICE_ASSIST_GSM + "]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias(), GSM, "GSM"}};
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


    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));
    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private boolean registerAllExtension() {
        log.debug("[prerequisite] init extension");
//        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(3001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(4000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

//        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
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
        if (getExtensionStatus(2000, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("2000注册失败");
        }
        if (getExtensionStatus(4000, IDLE, 5) != IDLE) {
            reg = true;
            log.debug("4000注册失败");
        }
        return reg;
    }

    private void resetRingGroup1(){
        ringGroupMembers1 = new ArrayList<>();
        ringGroupExtGroupMembers1 = new ArrayList<>();

        ringGroupMembers1.add("1000");
        ringGroupMembers1.add("1001");
        ringGroupMembers1.add("1002");
        ringGroupMembers1.add("1003");
        ringGroupExtGroupMembers1.add("ExGroup1");

        JSONArray jsonArray = new JSONArray();

        MenuOptionObject menuOptionObject = apiUtil.getRingGroupMenuOption();
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
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":60",jsonArray.toString())).apply();
    }

    private String editRingGroup1Extension(List<String> extensions){

        JSONArray jsonArray = new JSONArray();

        MenuOptionObject menuOptionObject = apiUtil.getRingGroupMenuOption();
        for (String ext : extensions){
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
        return jsonArray.toString();
    }

    public void prerequisite() {
        //local debug
        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtension();
        }

        long startTime = System.currentTimeMillis();
        if (runRecoveryEnvFlag) {
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

            queueStaticMembers = new ArrayList<>();
            queueDynamicMembers = new ArrayList<>();
            queueMembers = new ArrayList<>();
            ringGroupMembers0 = new ArrayList<>();
            ringGroupMembers1 = new ArrayList<>();
            ringGroupExtGroupMembers0 = new ArrayList<>();
            ringGroupExtGroupMembers1 = new ArrayList<>();
            conferenceMember = new ArrayList<>();

            step("创建分机组 ExGroup1/ExGroup2");
            List<String> extensionExGroup1 = new ArrayList<>();
            List<String> extensionExGroup2 = new ArrayList<>();

            extensionExGroup1.add("1000");
            extensionExGroup1.add("1001");
            extensionExGroup2.add("1002");
            extensionExGroup2.add("1003");

            //todo 分机组ExGroup1，ExGroup2 没有号码
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }").
                    createExtensionGroup("ExGroup1", extensionExGroup1).
                    createExtensionGroup("ExGroup2", extensionExGroup2).apply();
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
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test").replace("EXTENSIONLASTNAME", "A").replace("EXTENSIONNUM", "1000").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "test2").replace("EXTENSIONLASTNAME", "B").replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testta").replace("EXTENSIONLASTNAME", "C").replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "testa").replace("EXTENSIONLASTNAME", "D").replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "t").replace("EXTENSIONLASTNAME", "estX").replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONFIRSTNAME", "0").replace("EXTENSIONLASTNAME", "0").replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtensionFXS.replace("EXTENSIONFIRSTNAME", "1020").replace("EXTENSIONLASTNAME", "1020").replace("FXSPORT", "1-3").replace("EXTENSIONNUM", "1020").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS_2);

            step("创建IVR 6301");
            ArrayList<IVRObject.PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
            pressKeyObjects_0.add(new IVRObject.PressKeyObject(IVRObject.PressKey.press0, "extension", "", "1000", 0));
            apiUtil.deleteAllIVR().createIVR(ivrNum0, ivrName0, pressKeyObjects_0);

            step("创建响铃组6300 6301");
            ringGroupMembers0.add("1003");
            ringGroupExtGroupMembers0.add("ExGroup1");
            ringGroupMembers1.add("1001");
            ringGroupMembers1.add("1002");
            ringGroupMembers1.add("1003");
            ringGroupExtGroupMembers1.add("ExGroup1");

            ExtensionObject extensionObject = apiUtil.getExtensionSummary("1000");
            ExtensionObject extensionObject2 = apiUtil.getExtensionSummary("1004");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName0, ringGroupNum0, ringGroupMembers0, ringGroupExtGroupMembers0)
                    .editRingGroup(ringGroupNum0, String.format("\"ring_strategy\":\"ring_all\",\"ring_timeout\":10,\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", extensionObject.id))
                    .createRingGroup(ringGroupName1, ringGroupNum1, ringGroupMembers1, ringGroupExtGroupMembers1)
                    .editRingGroup(ringGroupNum1, String.format("\"ring_strategy\":\"ring_all\",\"fail_dest\":\"extension\",\"fail_dest_value\":\"%s\"", extensionObject2.id));

            step("创建队列6400");
            queueStaticMembers.add("1000");
            queueStaticMembers.add("1001");
            queueDynamicMembers.add("1003");
            queueDynamicMembers.add("1004");
            apiUtil.deleteAllQueue().createQueue(queueName0, queueNum0, queueDynamicMembers, queueStaticMembers, null);

            step("创建会议室");
            apiUtil.deleteAllConference().createConference(conferenceName0, conferenceNum0, conferenceMember);

            step("创建呼入路由InRoute3,目的地到响铃组6300");
            apiUtil.deleteAllInbound().createInbound("In1", trunk9, "ring_group", ringGroupNum1);

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
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = registerAllExtension();
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @Epic("P_Series")
    @Feature("RingGroup")
    @Story("Basic,Trunk，RingStategry，RingTimeout,InboundRoute")
    @Description("1.通过外线呼入到RingGroup1\n" +
            "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute",  "SIP_REGISTER"}, dataProvider = "routes")
    public void testCallRingGroup1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

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

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, "ANSWERED", cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), "test A<1000>", "ANSWERED", "test A<1000> hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P2", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute", "SIP_REGISTER"}, dataProvider = "routes")
    public void testCallRingGroup2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1000应答可正常接听，其它分机停止响铃");
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
        if (message.equals("SPS")) {
            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                    .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, "ANSWERED", cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                    .contains(tuple("2000<2000>".replace("2000", caller + ""), "testa D<1003>", "ANSWERED", "testa D<1003> hung up", trunk, "", "Inbound"));

            softAssertPlus.assertAll();
        }
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
    @Test(groups = {"P1", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute", "SPS"}, dataProvider = "routes")
    public void testCallRingGroup3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

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

        step("分机1004正常响铃、接听、挂断；cdr正常；");
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
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, "NO ANSWER", cdrRingGroup1 + " timed out, failover", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), "t estX<1004>", "ANSWERED", "t estX<1004> hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P2", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute","SIP_REGISTER"}, dataProvider = "routes")
    public void testCallRingGroup4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1,RingTimeout时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000,1001,1002,1003同时响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
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
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, "NO ANSWER", cdrRingGroup1 + " timed out, failover", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), "t estX<1004>", "ANSWERED", "t estX<1004> hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute","SIP_REGISTER"}, dataProvider = "routes")
    public void testCallRingGroup5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite();

        step("编辑RingGroup1,RingTimeout时间为10s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":10").apply();

        step("网页admin登录,进入ringgroup界面 ");
        auto.loginPage().loginWithAdmin();

        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ring_group);

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        sleep(5000);

        step("分机1002在响铃5s后应答，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1002, false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1002, TALKING, 10)).as("[通话校验]:1000分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1,RingTimeout时间[恢复]为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1002, CDRObject.STATUS.ANSWER, ext_1002+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Basic,Trunk", "RingStategry", "RingTimeout", "InboundRoute"}, dataProvider = "routes")
    public void testCallRingGroup6(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
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
        softAssertPlus.assertThat(getExtensionStatus(1004, HUNGUP, 1)).as("[通话校验]:1004分机未响铃").isEqualTo(HUNGUP);
        sleep(3000);

        step("分机1003在响铃3s后应答，其它分机停止响铃");
        pjsip.Pj_Answer_Call(1003, false);
        sleep(WaitUntils.TALKING_WAIT);
        softAssertPlus.assertThat(getExtensionStatus(1003, TALKING, 10)).as("[通话校验]:1003分机10s内接听成功").isEqualTo(TALKING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机挂断").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机挂断").isEqualTo(HUNGUP);

        pjsip.Pj_hangupCall(1003);
        pjsip.Pj_Hangup_All();

        step("编辑RingGroup1,RingTimeout时间[恢复]为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(ext_1001, cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", "", "", CDRObject.COMMUNICATION_TYPE.INTERNAL.toString()))
                .contains(tuple(ext_1001, ext_1003, CDRObject.STATUS.ANSWER, ext_1003+" hung up", "", "", CDRObject.COMMUNICATION_TYPE.INTERNAL.toString()));

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
    @Test(groups = {"P3", "Trunk","InboundRoute", "SIP_ACCOUNT"}, dataProvider = "routes")
    public void testTrunkInboundRoute1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

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
                .contains(tuple("4000<4000>", cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("4000<4000>", ext_1001, CDRObject.STATUS.ANSWER, ext_1001+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Trunk","InboundRoute", "FXO"}, dataProvider = "routes")
    public void testTrunkInboundRoute2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

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
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1002, CDRObject.STATUS.ANSWER, ext_1002+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Trunk","InboundRoute", "BRI"}, dataProvider = "routes")
    public void testTrunkInboundRoute3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1002应答可正常接听，其它分机停止响铃");
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
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1003, CDRObject.STATUS.ANSWER, ext_1003+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Trunk","InboundRoute", "E1"}, dataProvider = "routes")
    public void testTrunkInboundRoute4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

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
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1003, CDRObject.STATUS.ANSWER, ext_1003+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "Trunk","InboundRoute", "GSM"}, dataProvider = "routes")
    public void testTrunkInboundRoute5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000,1001,1002,1003同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 1)).as("[通话校验]:1001分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 1)).as("[通话校验]:1003分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertAll();

        step("分机1003应答可正常接听，其它分机停止响铃");
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
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1001, CDRObject.STATUS.ANSWER, ext_1001+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃20秒后，分机1002响铃20秒");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1000, CDRObject.STATUS.ANSWER, ext_1000+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P3", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃20秒后，分机1002响铃20秒");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);


        pjsip.Pj_Answer_Call(1002,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1002, CDRObject.STATUS.ANSWER, ext_1002+" hung up", trunk, "", "Inbound"));
        
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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1000响铃20s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 10)).as("[通话校验]:20s后1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1001响铃20s后，分机1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(20000);
        step("分机1002响铃20s后，分机1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 10)).as("[通话校验]:20s后1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        step("分机1003响铃20s未接，1004响铃");
        sleep(20000);

        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验]:20s后1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1004);
        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " timed out, failover", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1004, CDRObject.STATUS.ANSWER, ext_1004+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":10").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 5)).as("[通话校验]:5s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃10秒后，分机1002");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验]:5s内1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout6(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"sequentially\",\"ring_timeout\":20").apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过" + trunk + "外线呼入到RingGroup1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("分机1000响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 10)).as("[通话校验]:1000分机10s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 5)).as("[通话校验]:5s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(1001);
        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);

        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>".replace("2000", caller + ""), cdrRingGroup1, CDRObject.STATUS.ANSWER, cdrRingGroup1 + " connected", trunk, "", "Inbound"))
                .contains(tuple("2000<2000>".replace("2000", caller + ""), ext_1001, CDRObject.STATUS.ANSWER, ext_1001+" hung up", trunk, "", "Inbound"));

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
    @Test(groups = {"P2", "RingStategry","RingTimeout", "SIP_REGISTER"}, dataProvider = "routes")
    public void testRingStategryRingTimeout7(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
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

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃，其他挂断");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 5)).as("[通话校验]:5s内1001分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1000响铃10s后，分机1001响铃10秒后，分机1002响铃");
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 5)).as("[通话校验]:5s内1002分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1000, HUNGUP, 1)).as("[通话校验]:1000分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);

        sleep(10000);
        step("分机1002响铃10s后，分机1003响铃");
        softAssertPlus.assertThat(getExtensionStatus(1003, RING, 5)).as("[通话校验]:5s内1003分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, HUNGUP, 1)).as("[通话校验]:1002分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1001, HUNGUP, 1)).as("[通话校验]:1001分机未响铃").isEqualTo(HUNGUP);
        softAssertPlus.assertThat(getExtensionStatus(1003, HUNGUP, 1)).as("[通话校验]:1003分机未响铃").isEqualTo(HUNGUP);
        pjsip.Pj_Hangup_All();

        step("[环境恢复]：编辑RingGroup1，RingStategry选择Ring All，响铃时间为60s");
        apiUtil.editRingGroup(ringGroupNum1,"\"ring_strategy\":\"ring_all\",\"ring_timeout\":60").apply();

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
    @Test(groups = {"P3", "Members", "SIP_REGISTER"}, dataProvider = "routes")
    public void testMembers1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择ring_all，响铃时间为10s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1002");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        sleep(10000);
        step("未接，响铃10s");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 10)).as("[通话校验]:1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<3001>", cdrRingGroup1, "NO ANSWER", cdrRingGroup1 + " timed out, failover", SIPTrunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "t estX<1004>", "ANSWERED", "3001<3001> hung up", SIPTrunk, "", "Inbound"));


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
    @Test(groups = {"P3", "Members", "SIP_REGISTER"}, dataProvider = "routes")
    public void testMembers2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {

        prerequisite();

        step("编辑RingGroup1，RingStategry选择Ring Sequentially，响铃时间为20s");
        ArrayList<String> extList = new ArrayList<>();
        extList.add("1000");
        extList.add("1002");
        apiUtil.editRingGroup(ringGroupNum1,String.format("\"member_list\":%s,\"ring_strategy\":\"sequentially\",\"ring_timeout\":10",editRingGroup1Extension(extList).toString()))
                .apply();

        step("网页admin登录 ");
        auto.loginPage().loginWithAdmin();

        step("通过sip外线呼入到RingGroup1");
        pjsip.Pj_Make_Call_No_Answer(3001,  "3000", DEVICE_ASSIST_1, false);

        step("分机1000,1002同时响铃");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 5)).as("[通话校验]:1000分机5s内响铃").isEqualTo(RING);
        softAssertPlus.assertThat(getExtensionStatus(1002, RING, 1)).as("[通话校验]:1002分机响铃").isEqualTo(RING);

        step("未接，响铃10s,1004接听，3001挂断");
        softAssertPlus.assertThat(getExtensionStatus(1004, RING, 1)).as("[通话校验]:1004分机响铃").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1004,false);
        sleep(WaitUntils.TALKING_WAIT);

        pjsip.Pj_hangupCall(3001);
        pjsip.Pj_Hangup_All();

        step("CDR校验");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("3001<3001>", cdrRingGroup1, "NO ANSWER", cdrRingGroup1 + " timed out, failover", SIPTrunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "t estX<1004>", "ANSWERED", "3001<3001> hung up", SIPTrunk, "", "Inbound"));


        step("[环境恢复]：编辑RingGroup1");
        resetRingGroup1();

        softAssertPlus.assertAll();
    }


}
