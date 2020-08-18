package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.annotations.Test;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @program: SwebTest
 * @description: Operator Panel Test Case
 * @author: huangjx@yeastar.com
 * @create: 2020/07/30
 */
public class TestOperatorPanel extends TestCaseBase {
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
    String ringGroupName0 = "RG0";//6300
    String ringGroupName_1 = "RG1";//6301
    String conferenceListName = "CONF1";



    /**
     * 前提条件
     * 1.添加0分机和sps中继到 路由AutoTest_Route
     */
    public void prerequisite(){
        //新增呼出路由 添加分机0 ，到路由AutoTest_Route
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        list.clear();
        list.add(SPS);
        list2.clear();
        list2.add("0");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        //创建分机
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1000",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1001",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        auto.extensionPage().createSipExtension("1002",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
        //创建trunk
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        auto.trunkPage().deleteTrunk(getDriver(),SPS).createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

        //创建Inbound
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        auto.inboundRoute().deleteAllInboundRoutes().createInboundRoute("InRoute1",list).addDIDPatternAnd(0,"X.").selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"1000").clickSaveAndApply();

        auto.homePage().logout();

    }
    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    public void prerequisiteForAPIExtension(boolean isRunRecoveryEnvFlag) {
        if(isRunRecoveryEnvFlag) {
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            queueListNum = new ArrayList<>();
            ringGroupNum = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");


            extensionNum.add("1000");
            extensionNum.add("1001");
            extensionNum.add("1002");
            extensionNum.add("1003");
            extensionNum.add("1004");
            extensionNum.add("1005");
            step("创建分机1000-1005");
            apiUtil.deleteAllExtension();
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1000").replace("EXTENSIONLASTNAME", "A").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1001").replace("EXTENSIONLASTNAME", "B").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1002").replace("EXTENSIONLASTNAME", "C").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1003").replace("EXTENSIONLASTNAME", "D").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1004").replace("EXTENSIONLASTNAME", "E").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "1005").replace("EXTENSIONLASTNAME", "F").replace("GROUPLIST", groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("创建呼入路由InRoute1,目的地到分机1000");
            apiUtil.deleteAllInbound().createInbound("InRoute1", trunks, "Extension", "1000");

//            step("创建呼出路由");//todo bug for 32
//            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);

            step("创建队列");
            queueListNum.add("1000");
            queueListNum.add("1001");
            queueListNum.add("1002");
            queueListNum.add("1003");
            apiUtil.deleteAllQueue().createQueue(queueListName, "6400", null, queueListNum, null);


            step("创建响铃组6300");
            ringGroupNum.add("1000");
            ringGroupNum.add("1001");
            ringGroupNum.add("1002");
            ringGroupNum.add("1003");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName_1, "6300", ringGroupNum);

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlagExtension = false;
        }
    }

    public void prerequisiteForAPIForRingGroup(boolean booRunRecoveryEnvFlag) {
        if (booRunRecoveryEnvFlag) {
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            queueListNum = new ArrayList<>();
            queueListNum_1 = new ArrayList<>();
            ringGroupNum = new ArrayList<>();
            ringGroupNum_1 = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");


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
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

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
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName0, "6300", ringGroupNum)
                    .createRingGroup(ringGroupName_1, "6301", ringGroupNum_1);

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


            step("创建呼入路由InRoute2,目的地到ring_group 6300");
            apiUtil.deleteAllInbound().createInbound("InRoute2", trunks, "ring_group", "6300");

//            step("创建呼出路由");//todo bug for 32
//            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);


            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlagRingGroup = false;
        }
    }

    public void prerequisiteForAPIForQueue(boolean booRunRecoveryEnvFlag) {
        if (booRunRecoveryEnvFlag) {
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            queueListNum = new ArrayList<>();
            queueListNum_1 = new ArrayList<>();
            ringGroupNum = new ArrayList<>();
            ringGroupNum_1 = new ArrayList<>();

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");


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
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM", "0").replace("EXTENSIONLASTNAME", "").replace("GROUPLIST", groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

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
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName0, "6300", ringGroupNum)
                    .createRingGroup(ringGroupName_1, "6301", ringGroupNum_1);

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


            step("创建呼入路由InRoute2,目的地到Queue 6400");
            apiUtil.deleteAllInbound().createInbound("InRoute2", trunks, "Queue", "6400");

//            step("创建呼出路由");//todo bug for 32
//            apiUtil.deleteAllOutbound().createOutbound("Outbound1", trunks, extensionNum);


            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlagQueue = false;
        }
    }

    public void prerequisiteForAPIForConference(boolean booRunRecoveryEnvFlag) {
        if (booRunRecoveryEnvFlag) {
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            conferenceList = new ArrayList<>();


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

            step("创建Conference");
            conferenceList.add("1000");
            conferenceList.add("1001");
            conferenceList.add("1002");
            conferenceList.add("1003");
            conferenceList.add("1004");

            apiUtil.deleteAllConference().createConference(conferenceListName, "6500", conferenceList);

            step("创建呼入路由InRoute3,目的地到conference 6500");
            apiUtil.deleteAllInbound().createInbound("InRoute3", trunks, "conference", "6500");

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlagConference = false;
        }
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries","VCP1"})
    public void testIncomingRingStatus(){
//        prerequisite();
//
//        step("1:login web client");
//        auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);

        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 1000 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1001（Talking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1002]-->[1001]通话\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropWithCTalking","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropWithCTalking(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();

        step("4:【1002 与1001 通话】，1001 为Talking状态");
        pjsip.Pj_Make_Call_Auto_Answer(1002,"1001",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("5:【2000 呼叫 1000】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT*12);//TODO 拖动后状态无法直接接听，通过30s timeout 转到voicemail
        refresh();


        assertStep("4:VCP 第一条显示状态 A--C Talking external,voicemail");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, Voicemail");

        softAssert.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1001（idle）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->空闲状态\n+" +
            "3:[2000 呼叫 1001]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropWithCIdle","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropWithCIdle(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        step("4:【1001】 空闲状态");
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1001】，1001 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:显示状态 A--C ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"2000",RECORD.Callee),"1001 B [1001]");

        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:显示状态 A--C talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"2000",RECORD.Callee),"1001 B [1001]");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到1001（未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropWithCUnregistered","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropWithCUnregistered(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        step("4:【1001】 未注册");
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        refresh();//todo extension概率性出现 未注册分机不显示

        step("5:【2000 呼叫 1000】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT);//todo  30版本 6s左右后  被叫号码状态才更新过来

        assertStep("7:[VCP]7:显示状态 A--C Talking ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, Voicemail");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到Ring Group 6300\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropRingGroup","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropRingGroup(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[RingGroup]6300");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.RINGGROUP,"6300");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(ringGroupName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1002_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1003_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum.size());

        step("7:显示状态1001 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,false);

        assertStep("[VCP验证]7:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到Parking\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Parking]1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropParking","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropParking(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1001】，1001 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"001");


        assertStep("[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External");
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1);

        softAssert.assertAll();

        pjsip.Pj_Hangup_All();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->DragAndDrop 到Queue 6400\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingDragAndDropQueue","Regression","PSeries","VCP1"})
    public void testIncomingDragAndDropQueue(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1001】，1001 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6400");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.QUEUE,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键Extension：C ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionRedirectC_AHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionRedirectC_AHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1001");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1001  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(2000,2000);

        assertStep("3:[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键Extension：C ,C先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:c挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionRedirectC_CHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionRedirectC_CHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1001");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1001  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(1001,1001);

        assertStep("3:[CDR显示]");//todo CDR显示

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->Redirect 到Ring Group 6300\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Ring Group]6300")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRedirectRingGroup","Regression","PSeries","VCP1"})
    public void testIncomingRedirectRingGroup(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[RingGroup]6300");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6300");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(ringGroupName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1002_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1003_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum.size());

        step("7:显示状态1001 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,false);

        assertStep("[VCP验证]7:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->Redirect 到Queue 6400\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRedirectQueue","Regression","PSeries","VCP1"})
    public void testIncomingRedirectQueue(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[Queue]6400");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->Redirect Voicemail 图标\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Voicemail]小图标")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRedirectVoicemail","Regression","PSeries","VCP1"})
    public void testIncomingRedirectVoicemail(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1000",true);


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, Voicemail");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->Redirect IVR \n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[IVR]6200")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRedirectIVR","Regression","PSeries","VCP1"})
    public void testIncomingRedirectIVR(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6200");


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"6200 [6200]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, IVR");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键不显示\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->查看显示的条目")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionUnDisplay","Regression","PSeries","VCP1"})
    public void testIncomingRightActionUnDisplay(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示] Ring 只显示 Redirect，pick up，hang up");
        List list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertThat(list).containsOnlyOnce("Redirect","Pick Up","Hang Up");//equals  assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Resume");
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键REDIRECT：C ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[REDIRECT] C(外线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionRedirectOffLineC_AHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionRedirectOffLineC_AHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2000,2000);//todo can not handup

        assertStep("3:[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键REDIRECT：C ,C先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[REDIRECT] C(外线)" +
            "4:c挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionRedirectOffLineC_CHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionRedirectOffLineC_CHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->2001 ");

        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2001,2001);

        assertStep("3:[CDR显示]");//todo CDR显示

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->HandUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        assertStep("5:[VCP显示]");
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);
        softAssert.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->右键 悬停 HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->右键 悬停 HandUp" +
            "4:移开后 通话继续")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionHoverHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionHoverHandUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[HandUp]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.operatorPanelPage().moveByOffsetAndClick(200,200);
        sleep(WaitUntils.SHORT_WAIT*2);
        assertStep("5:[VCP显示]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A [1000]");

        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->PickUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->PickUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testIncomingRightActionPickUp(){
        prerequisiteForAPIExtension(runRecoveryEnvFlagExtension);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP 创建/注册]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991000",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[右键PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT*2);//todo 等待个6s 才会出现
        assertStep("5:[VCP显示]");
        List resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getCallee()).contains("0 [0]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getDetails()).contains("External");

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRingStatus","Regression","PSeries","VCP1"})
    public void testRGIncomingRingStatus(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, Ring Group");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
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
    @Test(groups = {"P0","VCP","testRingGroupIncomingRingStatus","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropRG(){
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

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);


        step("4：[RingGroup]6300 -->拖动到[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.RINGGROUP,"6301");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(ringGroupName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");

        //todo 适配响铃状态
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());


        step("5:显示状态1001 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]6:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getCaller()).as("验证分机1000_Caller").contains(ringGroupName0+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getStatus()).as("验证分机1000_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
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
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropParking","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropParking(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("6：[Inbound]1001 -->拖动到[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"001");

        assertStep("[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(ringGroupName0+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName0+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(ringGroupName0+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(ringGroupName0+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        //todo 适配响铃状态
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1002_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1003_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum.size());

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
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropQueue","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropQueue(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1001】，1001 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6400");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.QUEUE,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        //todo 适配Details
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

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
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCTalking","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropWithCTalking(){
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
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*12);//TODO 拖动后状态无法直接接听，通过30s timeout 转到voicemail?? 手动不出现
        refresh();


        assertStep("4:[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Details),"External, Voicemail");

        softAssert.assertAll();

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
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCIdle","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropWithCIdle(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 1001】，1010 空闲状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");
        //TODO 适配显示状态
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
    @Test(groups = {"P0","VCP","testRGIncomingRingDragAndDropWithCUnregistered","Regression","PSeries","VCP1"})
    public void testRGIncomingRingDragAndDropWithCUnregistered(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        step("4:【1010】 未注册");
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 6300】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT);//todo  30版本 6s左右后  被叫号码状态才更新过来

        assertStep("7:[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Details),"External, Voicemail");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
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
    @Test(groups = {"P0","VCP","testRGIncomingRightActionUnDisplay","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionUnDisplay(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示] Ring 只显示 Redirect，pick up，hang up");
        List list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertThat(list).containsOnlyOnce("Redirect","Pick Up","Hang Up");//equals  assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Resume");
        pjsip.Pj_Hangup_All();
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
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionHandUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        List resultSum_after =auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        assertStep("5:[VCP显示]");
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
    @Test(groups = {"P0","VCP","testRGIncomingRightActionHoverHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionHoverHandUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[HandUp->悬停，移开]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT);
        auto.operatorPanelPage().moveByOffsetAndClick(200,200);
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]");
        List resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"1000").getStatus()).contains("Ringing");

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
    @Test(groups = {"P0","VCP","testIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionPickUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[右键PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT*2);//todo 等待个6s 才会出现
        assertStep("5:[VCP显示]");
        List resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getCallee()).contains("0 [0]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getDetails()).contains("External");

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
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectC_AHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionRedirectC_AHandUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External,Ring Group");


        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1010  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(2000,2000);

        assertStep("[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

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
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectC_CHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionRedirectC_CHandUp(){
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
        pjsip.Pj_CreateAccount(1010,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1010,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External, Ring Group");

        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1010  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(1010,1010);

        assertStep("[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
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
    @Test(groups = {"P0","VCP","testRGIncomingRedirectRingGroup","Regression","PSeries","VCP1"})
    public void testRGIncomingRedirectRingGroup(){
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
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[RingGroup]6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6301");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(ringGroupName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());

        step("7:显示状态1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]7:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Talking");
        //todo 适配details
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300[响铃中]-->右键Extension  到Queue 6400\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRedirectQueue","Regression","PSeries","VCP1"})
    public void testRGIncomingRedirectQueue(){
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
        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[Queue]6400");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
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
    @Test(groups = {"P0","VCP","testRGIncomingRedirectVoicemail","Regression","PSeries","VCP1"})
    public void testRGIncomingRedirectVoicemail(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1000",true);


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External, Voicemail");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
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
    @Test(groups = {"P0","VCP","testRGIncomingRedirectIVR","Regression","PSeries","VCP1"})
    public void testRGIncomingRedirectIVR(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6200");


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"6200 [6200]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External, IVR");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
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
    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectOffLineY_AHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionRedirectOffLineY_AHandUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2000,2000);//todo can not handup  分机2000处于hungup 【预期：3 实际：4】

        assertStep("3:[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

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
//    @Test(groups = {"P0","VCP","testRGIncomingRightActionRedirectOffLineY_YHandUp","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionRedirectOffLineY_YHandUp(){
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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C");
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->2001 ");

        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2001,2001);

        assertStep("3:[CDR显示]");//todo CDR显示

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Queue-->Queue 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingStatus","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingStatus(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6400 ");
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

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Details),"External, Ringing Agent");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400 -->DragAndDrop 1000 到分机 1010（Talking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1011]-->[1010]通话,1010-->Talking\n+" +
            "3:[2000 呼叫 Queue]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1010")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropWithCTalking","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropWithCTalking(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*12);//TODO 拖动后状态无法直接接听，通过30s timeout 转到voicemail?? 手动不出现
        refresh();

        assertStep("4:[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Details),"External, Voicemail");

        softAssert.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6300 -->DragAndDrop 1000 到分机 1010（idle）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 Queue]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（idle）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropWithCIdle","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropWithCIdle(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
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
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400 -->DragAndDrop 1000 到分机 1010（未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[2000 呼叫 RingGroup 6300]，1000 为Ring状态\n" +
            "3:[Inbound]1000 -->拖动到[Extension]1010（未注册）")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("勾选显示未注册分机，概率性出现 未注册分机不能显示")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropWithCUnregistered","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropWithCUnregistered(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        refresh();//分机无法自动更新

        step("5:【2000 呼叫 6400】，1000 为Ringing状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1010");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1010");
        sleep(WaitUntils.SHORT_WAIT*3);//todo  30版本 6s左右后  被叫号码状态才更新过来

        assertStep("7:[VCP]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Details),"External, Voicemail");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Queue 响铃中 -->[DragAndDrop]-->[RingGroup]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]" +
            "3.DragAndDrop RG\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropRG","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropRG(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);
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

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);


        step("4：[RingGroup]6300 -->拖动到[RingGroup]6301");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.RINGGROUP,"6301");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(ringGroupName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupNum_1.size());


        step("5:显示状态1001 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]6:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getCaller()).as("验证分机1000_Caller").contains(ringGroupName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getStatus()).as("验证分机1000_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue]-->DragAndDrop 到Parking\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 6401]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Parking]001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropParking","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropParking(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("6：[Inbound]1001 -->拖动到[Parking]001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"001");

        assertStep("[VCP验证]");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1002").getDetails()).as("验证分机1002_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1003").getDetails()).as("验证分机1003_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(queueListNum.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue]-->DragAndDrop Queue\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 6401]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Queue]6401")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRingDragAndDropQueue","Regression","PSeries","VCP1"})
    public void testQueueIncomingRingDragAndDropQueue(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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

        step("5:【2000 呼叫 1001】，1001 为Ring状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6400");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.QUEUE,"6401");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(queueListName_1+":2000 [2000]");


        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum_1.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] -->右键Extension：C ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionRedirectC_AHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionRedirectC_AHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),UI_MAP.getString("web_client.external")+", "+UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));


        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1010  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(2000,2000);

        assertStep("[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue]6400[响铃中]-->右键Extension：C ,C先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->[Redirect] C(内线)" +
            "4:c挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionRedirectC_CHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionRedirectC_CHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

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

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),UI_MAP.getString("web_client.external")+", "+UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        step( "4:右键->[Redirect] C");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1010");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->1001 来电  Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Ringing");


        assertStep("6:[VCP显示]2000->1010  接通后 Talking状态");
        pjsip.Pj_Answer_Call(1010,false);
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Callee),"1010 K [1010]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1010",RECORD.Status),"Talking");

        pjsip.Pj_hangupCall(1010,1010);

        assertStep("[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键Extension 到Ring Group 6301\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRedirectRingGroup","Regression","PSeries","VCP1"})
    public void testQueueIncomingRedirectRingGroup(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[RingGroup]6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6401");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(queueListName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(queueListName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(queueListName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(queueListName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(queueListName_1 +":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(queueListNum_1.size());

        step("7:显示状态1005 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1005,false);

        assertStep("[VCP验证]7:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(queueListName_1 +":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.queue").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键Extension  到Queue 6400\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Queue]6401")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRedirectQueue","Regression","PSeries","VCP1"})
    public void testQueueIncomingRedirectQueue(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->右键-->Redirect[Queue]6400");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6401");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getCaller()).as("验证分机1005_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getCaller()).as("验证分机1006_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getCaller()).as("验证分机1007_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getCaller()).as("验证分机1008_Caller").contains(queueListName_1+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getCaller()).as("验证分机1009_Caller").contains(queueListName_1+":2000 [2000]");


        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getStatus()).as("验证分机1005_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getStatus()).as("验证分机1006_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getStatus()).as("验证分机1007_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getStatus()).as("验证分机1008_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getStatus()).as("验证分机1009_Status").contains("Ringing");


        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1005").getDetails()).as("验证分机1005_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1006").getDetails()).as("验证分机1006_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1007").getDetails()).as("验证分机1007_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1008").getDetails()).as("验证分机1008_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList,RECORD.Callee,"1009").getDetails()).as("验证分机1009_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));


        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueListNum_1.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键Extension  Voicemail 图标\n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[Voicemail]小图标")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRedirectVoicemail","Regression","PSeries","VCP1"})
    public void testQueueIncomingRedirectVoicemail(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[Voicemail]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"1000",true);


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External, Voicemail");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键Extension -->Redirect IVR \n" +
            "1:分机0,login web client\n" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->Redirect[IVR]6200")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRedirectIVR","Regression","PSeries","VCP1"})
    public void testQueueIncomingRedirectIVR(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->Redirect[IVR]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"6200");


        assertStep("[VCP]7:显示状态 A--B Talking External,Voicemail");
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"6200 [6200]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Details),"External, IVR");

        sleep(WaitUntils.SHORT_WAIT);

        softAssert.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue] 呼入6400[响铃中]-->右键Extension-->右键Redirect：Y ,A先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键Redirect Y(外线)" +
            "4:A挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionRedirectOffLineY_AHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionRedirectOffLineY_AHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C(外线)");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2000,2000);//todo  can not handup  分机2000处于hungup 【预期：3 实际：4】 0814

        assertStep("3:[CDR显示]");//todo CDR显示
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue] 呼入6400[响铃中]-->右键Extension-->右键Redirect：Y,Y先挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->[Redirect] Y(外线)" +
            "4:Y挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")//TODO make jenkins vm exception
//    @Test(groups = {"P0","VCP","testQueueIncomingRightActionRedirectOffLineY_YHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionRedirectOffLineY_YHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Callee,"1000",RECORD.Callee),"1000 A [1000]");

        step( "4:右键->[Redirect] C");
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.REDIRECT,"2001");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]2000->2001 ");

        sleep(WaitUntils.SHORT_WAIT*2);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        pjsip.Pj_hangupCall(2001,2001);

        assertStep("3:[CDR显示]");//todo CDR显示

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue] 呼入6400[响铃中] -->右键HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->HandUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        List resultSum_after =auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        assertStep("5:[VCP显示]");
        softAssertPlus.assertThat(resultSum_before.size()).isEqualTo(resultSum_after.size()+1);
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键 悬停 HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3:右键->右键 悬停 HandUp" +
            "4:移开后 通话继续")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionHoverHandUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionHoverHandUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[HandUp->悬停，移开]");
        auto.operatorPanelPage().rightTableActionMouserHover(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT);
        auto.operatorPanelPage().moveByOffsetAndClick(200,200);
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("5:[VCP显示]");
        List resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中]-->右键PickUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->PickUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingRightActionPickUp","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionPickUp(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getCallee()).contains("1000 A [1000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Callee,"1000").getStatus()).contains("Ringing");

        step( "4:右键->[右键PickUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PICK_UP);
        sleep(WaitUntils.SHORT_WAIT*2);//todo 等待个6s 才会出现
        assertStep("5:[VCP显示]");
        List resultSum_after = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getCallee()).contains("0 [0]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_after, RECORD.Callee,"0 [0]").getDetails()).contains("External");

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[Queue] 呼入6400[响铃中] -->右键不显示\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->查看显示的条目")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionUnDisplay","Regression","PSeries","VCP1"})
    public void testQueueIncomingRightActionUnDisplay(){
        prerequisiteForAPIForQueue(runRecoveryEnvFlagQueue);

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
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示] Ring 只显示 Redirect，pick up，hang up");
        List list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertThat(list).containsOnlyOnce("Redirect","Pick Up","Hang Up");//equals  assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Resume");
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Conference响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRingStatus","Regression","PSeries","VCP1"})
    public void testConferenceIncomingRingStatus(){
//        prerequisiteForAPIForConference(runRecoveryEnvFlagConference);

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6500 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:Ring显示状态 ");
        List list =  auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssert.assertAll();
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(list, RECORD.Caller,"2000").getStatus()).contains("Talking").as("check vcp conference status");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(list, RECORD.Caller,"2000").getDetails()).contains("External, Conference").as("check vcp conference details");
        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->Conference响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Conference]\n" +
            "3:右键录音功能")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("bug，录音无法显示")
    @Test(groups = {"P0","VCP","testConferenceRingActionRecord","Regression","PSeries","VCP1"})
    public void testConferenceRingActionRecord(){
        prerequisiteForAPIForConference(runRecoveryEnvFlagConference);

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6500 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:右键录音");//todo  bug add vcp assert
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.PAUSE_RECORD,"");


        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> [Queue] 呼入6400[响铃中] -->右键HandUp\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[Queue]6400\n" +
            "3:右键->HandUp" +
            "4:通话结束")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testConferenceIncomingRightActionHandUp","Regression","PSeries","VCP1"})
    public void testConferenceIncomingRightActionHandUp(){
        prerequisiteForAPIForConference(runRecoveryEnvFlagConference);

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);// auto.loginPage().loginWithExtensionNewPassword("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW); //for prerequisite();

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6500 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996500",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3:[VCP显示]2000->1000 初始状态 Ring状态");
        List resultSum_before = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Caller,"2000").getCallee()).contains("CONF1 [6500]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(resultSum_before, RECORD.Caller,"2000").getStatus()).contains("Talking");

        step( "4:右键->[HandUp]");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);
        sleep(WaitUntils.SHORT_WAIT*2);
        List resultSum_after =auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        assertStep("5:[VCP显示]");
        softAssertPlus.assertThat(resultSum_before.size()).isEqualTo(resultSum_after.size()+1);
        softAssertPlus.assertAll();
    }

}
