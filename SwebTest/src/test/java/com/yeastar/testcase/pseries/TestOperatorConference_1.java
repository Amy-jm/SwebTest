package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD;
import com.yeastar.page.pseries.OperatorPanel.Record;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.apache.commons.codec.digest.DigestUtils;
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
public class TestOperatorConference_1 extends TestCaseBase {
    APIUtil apiUtil = new APIUtil();

    private boolean runRecoveryEnvFlag = true;
    ArrayList<String> ringGroupNum_1 ;
    ArrayList<String> conferenceList_0 ;
    ArrayList<String> conferenceList_1 ;
    private ArrayList<String> conferenceMember = new ArrayList<>();

    String queueListName = "Q0";
    String queueListName_1 = "Q1";
    String ringGroupName0 = "RG0";//6300
    String ringGroupName_1 = "RG1";//6301
    String conferenceListName_0 = "CONF0";//6500
    String conferenceListName_1 = "CONF1";//6501


    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,"DOD",DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    public void prerequisiteForAPI(boolean booRunRecoveryEnvFlag) {
        if (booRunRecoveryEnvFlag) {
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            conferenceList_0 = new ArrayList<>();
            conferenceList_1 = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");

            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");

            step("创建分机1000-1010");
            apiUtil.deleteAllExtension().apply();
            sleep(WaitUntils.SHORT_WAIT);
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1000").replace("EXTENSIONLASTNAME", "A").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "E").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "F").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("创建Conference_0");
            conferenceList_0.add("1000");
            conferenceList_0.add("1001");
            conferenceList_0.add("1002");
            conferenceList_0.add("1003");
            conferenceList_0.add("1004");

            step("创建Conference_1");
            conferenceList_1.add("1005");
            conferenceList_1.add("1006");
            conferenceList_1.add("1007");
            conferenceList_1.add("1008");
            conferenceList_1.add("1009");

            apiUtil.deleteAllConference().createConference(conferenceListName_0, "6500", conferenceList_0)
                                         .createConference(conferenceListName_1, "6501", conferenceList_1);

            step("创建呼入路由InRoute3,目的地到conference 6500");
            apiUtil.deleteAllInbound().createInbound("InRoute3", trunks, "conference", "6500");

            step("创建呼出路由");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
        }
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Conference-->Conference 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingStatus","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingStatus(){
        prerequisiteForAPI(runRecoveryEnvFlag);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500 -->DragAndDrop 1000 到分机 1010（Talking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1011]-->[1010]通话,1010-->Talking\n+" +
            "3:[2000 呼叫 Conference]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1010")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("bug 等待留言的时候才开始录音")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingDragAndDropWithCTalking","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingDragAndDropWithCTalking(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1011,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();

        step("4:【1011 与1010 通话】，【1010】 Talking状态");
        pjsip.Pj_Make_Call_Auto_Answer(1011,"1010",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("5:【2000 呼叫 1000】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);
        refresh();

        assertStep("7:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6300 -->DragAndDrop 1000 到分机 1010（idle）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 Conference]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（idle）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingDragAndDropWithCIdle","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingDragAndDropWithCIdle(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 1001】，1010 空闲状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
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
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500 -->DragAndDrop 1000 到分机 1010（未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 RingGroup 6300]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（未注册）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("1.勾选显示未注册分机，概率性出现 未注册分机不能显示 \n" +
            "2.拖动后，需要>=6秒后才会显示")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingDragAndDropWithCUnregistered","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingDragAndDropWithCUnregistered(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        step("4:【1010】 未注册");
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();//todo 概率性出现，未注册分机不能显示
        
        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:[VCP]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Conference 响铃中 -->[DragAndDrop]-->[RingGroup]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]" +
            "3.DragAndDrop RG\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingDragAndDropRG","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingDragAndDropRG(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);


        step("4：拖动到[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.RINGGROUP,"6301");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP显示]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+":2000 [2000]", "1005 F [1005]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1006 G [1006]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1007 H [1007]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1008 I [1008]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1009 J [1009]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());


        step("5:1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("6:[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+":2000 [2000]", "1005 F [1005]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));

        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Conference]-->DragAndDrop 到Parking\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 6401]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Parking]001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingDragAndDropParking","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRingDragAndDropParking(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("6：[Inbound]1001 -->拖动到[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"001");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_0 +":2000 [2000]", "1001 B [1001]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_0 +":2000 [2000]", "1002 C [1002]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_0 +":2000 [2000]", "1003 D [1003]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_0 +":2000 [2000]", "1004 E [1004]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));

        softAssertPlus.assertThat(allRecordList).as("验证数量").size().isEqualTo(conferenceList_0.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] -->右键Extension：C ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionRedirectC_AHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionRedirectC_AHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        step( "5:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("7:[接通]");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_talking= auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_talking).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "Conference CONF0<6500>","NO ANSWER","Redirected to 1010 K<1010>"),
                        tuple("2000<2000>", "1010 K<1010>","ANSWERED","2000<2000> hung up"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Conference]6500[响铃中]-->右键Extension：C ,C先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:c挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionRedirectC_CHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionRedirectC_CHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1010,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        step( "5:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        assertStep("7:[接通]");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:[VCP显示]");
        List<Record> resultSum_talking= auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_talking).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1010 K [1010]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        pjsip.Pj_hangupCall(1010);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "Conference CONF0<6500>","NO ANSWER","Redirected to 1010 K<1010>"),
                        tuple("2000<2000>", "1010 K<1010>","ANSWERED","1010 K<1010> hung up"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键Extension 到Ring Group 6301\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Ring Group]6301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRedirectRingGroup","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRedirectRingGroup(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[RingGroup]6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6301");
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+":2000 [2000]", "1005 F [1005]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1006 G [1006]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1007 H [1007]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1008 I [1008]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()),
                        tuple(ringGroupName_1+":2000 [2000]", "1009 J [1009]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());

        step("7:1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                .contains(tuple(ringGroupName_1+":2000 [2000]", "1005 F [1005]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_RING_GROUP.getAlias()));
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(1005);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "Conference CONF0<6500>","NO ANSWER","Redirected to RG1<6301>"),
                        tuple("2000<2000>", "RingGroup RG1<6301>","ANSWERED","RingGroup RG1<6301> connected"),
                        tuple("2000<2000>", "1005 F<1005>","ANSWERED","1005 F<1005> hung up"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键Extension  到Conference 6500\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Conference]6401")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRedirectConference","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRedirectConference(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(1006,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1007,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1008,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1009,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
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

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[Conference]6500");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6401");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP验证]");
        List<Record> allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordList).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_1+":2000 [2000]", "1005 F [1005]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_1+":2000 [2000]", "1006 G [1006]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_1+":2000 [2000]", "1007 H [1007]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_1+":2000 [2000]", "1008 I [1008]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()),
                        tuple(conferenceListName_1+":2000 [2000]", "1009 J [1009]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_AGENT_RING.getAlias()));
        softAssertPlus.assertThat(allRecordList).as("验证Conference数量").size().isEqualTo(conferenceList_1.size());

        step("7:显示状态1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("[VCP验证]");
        List<Record> allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(allRecordListAfter).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_1+":2000 [2000]", "1005 F [1005]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_hangupCall(1005);

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "Conference CONF0<6500>","NO ANSWER","Redirected to Q1<6401>"),
                        tuple("2000<2000>", "Conference Q1<6401>","ANSWERED","Conference Q1<6401> connected"),
                        tuple("2000<2000>", "1005 F<1005>","ANSWERED","1005 F<1005> hung up"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键Extension  Voicemail 图标\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Voicemail]小图标")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRedirectVoicemail","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRedirectVoicemail(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1000",true);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("5:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_VOICEMAIL.getAlias()));

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Hangup_All();

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "1000 A<1000>", "VOICEMAIL", "2000<2000> hung up"),
                        tuple ("2000<2000>", "Conference CONF0<6500>", "NO ANSWER", "Redirected to 1000 A<1000>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键Extension -->Redirect IVR \n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[IVR]6200")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRedirectIVR","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRedirectIVR(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("4：[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6200");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("5:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "6200 [6200]","Talking", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias()));

        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Hangup_All();//TODO IVR 接听

        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<2000>", "IVR 6200<6200>", "ANSWERED", "2000<2000> hung up"),
                        tuple("2000<2000>", "Conference CONF0<6500>", "NO ANSWER", "Redirected to 6200<6200>"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Conference] 呼入6500[响铃中]-->右键Extension-->右键Redirect：Y ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键Redirect Y(外线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionRedirectOffLineY_AHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionRedirectOffLineY_AHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

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
        pjsip.Pj_hangupCall(2000);

        assertStep("10:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2000<spsOuntCid> hung up"),
                        tuple ("2000<2000>", "Conference CONF0<6500>", "NO ANSWER", "Redirected to 0<2001>"));//todo check to 0<2001>?

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Conference] 呼入6500[响铃中]-->右键Extension-->右键Redirect：Y,Y先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[Redirect] Y(外线)" +
            "4:Y挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")//TODO make jenkins vm exception
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionRedirectOffLineY_YHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionRedirectOffLineY_YHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

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

        assertStep("10:[CDR显示]");//todo CDR显示
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason")
                .contains(tuple("2000<spsOuntCid>", "2001", "ANSWERED", "2001 hung up"),//todo check 2001 hung up ? 0<2001> hung up ?
                        tuple ("2000<2000>", "Conference CONF0<6500>", "NO ANSWER", "Redirected to 0<2001>"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Conference] 呼入6500[响铃中] -->右键HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键->HandUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

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
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键 悬停 HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->右键 悬停 HandUp" +
            "4:移开后 通话继续")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionHoverHandUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionHoverHandUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        step( "5:右键->[HandUp->悬停，移开]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.RETRY_WAIT);
        auto.operatorPanelPage().moveByOffsetAndClick(200,200);
        sleep(WaitUntils.RETRY_WAIT);

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中]-->右键PickUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键->PickUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionPickUp","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionPickUp(){
        prerequisiteForAPI(runRecoveryEnvFlag);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:[VCP显示]2000->1000 初始状态 Ring状态");
        List<Record> resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_before).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "1000 A [1000]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_CONFERENCE.getAlias()));

        step( "5:右键->[右键PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT*4);//todo BUG 等待>=6s 才会出现

        assertStep("6:[VCP显示]");
        List<Record> resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(resultSum_after).extracting("caller","callee","status","details")
                .contains(tuple(conferenceListName_0 +":2000 [2000]", "0 [0]","Ringing", OperatorPanelPage.RECORD_DETAILS.EXTERNAL.getAlias()));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Conference] 呼入6500[响铃中] -->右键不显示\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]6500\n" +
            "3:右键->查看显示的条目")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionUnDisplay","Regression","PSeries","VCP1","Conference"})
    public void testConferenceIncomingRightActionUnDisplay(){
        prerequisiteForAPI(runRecoveryEnvFlag);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示]");
        List<String> list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Recording","Unrecording");
        pjsip.Pj_Hangup_All();
    }
}

