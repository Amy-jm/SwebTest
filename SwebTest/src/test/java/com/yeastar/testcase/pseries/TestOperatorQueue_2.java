package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.APIObject.InboundRouteObject;
import com.yeastar.untils.APIObject.RingGroupObject;
import com.yeastar.untils.APIUtil;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;


public class TestOperatorQueue_2 extends TestCaseBase {
    private APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = false;

    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONNUM\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);

    private final String queueListName = "QU";
    private final String ringGroupName = "RG";//6300
    private final String conferenceName = "CO";
    private final String queueListName2 = "QE";
    private final String ringGroupName2 = "RI";//6300
    private final String conferenceName2 = "CF";

    private final ArrayList<String> queueMembers = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers = new ArrayList<>();
    private final ArrayList<String> conferenceMember = new ArrayList<>();
    private final ArrayList<String> queueMembers2 = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers2 = new ArrayList<>();
    private final ArrayList<String> conferenceMember2 = new ArrayList<>();

    private final String op_external = UI_MAP.getString("web_client.external").trim();
    private final String op_hangup = UI_MAP.getString("web_client.hangup").trim();
    private final String op_talking = UI_MAP.getString("web_client.talking").trim();
    private final String op_ringing = UI_MAP.getString("web_client.ringing").trim();
    private final String op_ringgroup = UI_MAP.getString("web_client.ringgroup").trim();
    private final String op_parked = UI_MAP.getString("web_client.parked").trim();
    private final String op_retrieve = UI_MAP.getString("web_client.unparked").trim();
    private final String op_queue = UI_MAP.getString("web_client.queue").trim();
    private final String opf_detial = "%s, %s";

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Presence")
    @Description("Presence->前置条件")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("")
    @Test(groups = {"P0","VCP","prerequisite","Regression","PSeries"},priority =0 )
    public void prerequisite() {

        if (runRecoveryEnvFlag){
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);
            List<String> extensionNum = new ArrayList<>();
            List<String> emptyList = new ArrayList<>();

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
            apiUtil.createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1000").replace("EXTENSIONLASTNAME","A").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1001").replace("EXTENSIONLASTNAME","B").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1002").replace("EXTENSIONLASTNAME","C").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1003").replace("EXTENSIONLASTNAME","D").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1004").replace("EXTENSIONLASTNAME","E").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1005").replace("EXTENSIONLASTNAME","F").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","0").replace("EXTENSIONLASTNAME","").replace("GROUPLIST",groupList))
                    .createExtension(reqDataCreateExtension.replace("EXTENSIONNUM","1").replace("EXTENSIONLASTNAME","").replace("GROUPLIST",groupList));

            step("创建SPS中继");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("创建呼入路由InRoute1,目的地到分机1000");
            apiUtil.deleteAllInbound().createInbound("InRoute1",trunks,"Extension","1000");

            step("创建呼出路由");
            apiUtil.deleteAllOutbound().createOutbound("Outbound1",trunks,extensionNum);

            step("创建响铃组6300");
            ringGroupMembers.add("1001");
            ringGroupMembers.add("1002");
            ringGroupMembers.add("1003");
            apiUtil.deleteAllRingGroup().createRingGroup(ringGroupName,"6300",ringGroupMembers);

            step("创建响铃组6301");
            ringGroupMembers2.add("1004");
            ringGroupMembers2.add("1005");
            apiUtil.createRingGroup(ringGroupName2,"6301",ringGroupMembers2);

            step("创建队列6400");
            queueMembers.add("1001");
            queueMembers.add("1002");
            queueMembers.add("1003");
            apiUtil.deleteAllQueue().createQueue(queueListName,"6400",null, queueMembers,null);

            step("创建队列6401");
            queueMembers2.add("1004");
            queueMembers2.add("1005");
            apiUtil.createQueue(queueListName2,"6401",null, queueMembers2,null);

            step("创建会议室");
            conferenceMember.add("1001");
            conferenceMember.add("1002");
            conferenceMember.add("1003");
            apiUtil.deleteAllConference().createConference(conferenceName, "6500", conferenceMember);

            step("创建会议室");
            conferenceMember2.add("1004");
            conferenceMember2.add("1005");
            apiUtil.createConference(conferenceName2, "6501", conferenceMember2);

            step("设置网络磁盘");
            extensionNum.add("0");
            extensionNum.add("1");
            apiUtil.deleteNetworkDrive().setNetworkDriver().updateAutoRecord(emptyList,extensionNum,emptyList,emptyList);

            List<InboundRouteObject> inboundObjectList = apiUtil.getInboundSummary();

            for(int i=0; i<inboundObjectList.size(); i++){
                InboundRouteObject object = inboundObjectList.get(i);
                if(object.name.equals("InRoute1")){
                    RingGroupObject rg = apiUtil.getRingGroupSummary("6400");
                    apiUtil.editInbound(String.format("{\"def_dest\":\"queue\",\"def_dest_value\":\"%s\",\"id\":%s}",rg.id,object.id));
                }
            }

            apiUtil.apply();

            apiUtil.loginWebClient("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            apiUtil.loginWebClient("1",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW).updatePersonal("{\"show_unregistered_extensions\":1}");
            runRecoveryEnvFlag = false;
        }

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 -->呼入状态：talking\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingTalkingStatus","Regression","PSeries"})
    public void testQueueIncomingTalkingStatus() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("3.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 -->右击不显示PickUp、Redirect、unpark\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingTalkingRightClickNotDisplay","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickNotDisplay() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        List rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000");

        assertStep("3.右击后不显示Pick Up、Redirect");
        softAssert.assertFalse(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.PICK_UP),"右击不显示Pick up");
        softAssert.assertFalse(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.REDIRECT),"右击不显示Redirect");
        softAssert.assertFalse(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.RETRIEVE),"右击不显示Retrieve");

        step("4.点击park后右击显示unpark");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(3000);
        rightEventList = auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000");

        softAssert.assertTrue(rightEventList.contains(OperatorPanelPage.RIGHT_EVENT.RETRIEVE.getAlias()),"右击有显示Retrieve");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击挂断" +
            "4.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingTalkingRightClickHangup","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickHangup() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        step("3.右击挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.HANG_UP,"2000");
        sleep(5000);

        //todo cdr校验
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击点击Listen" +
            "4.断言分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "6.断言VCP页面元素")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("Listen后VCP界面会多产生一条记录")
    @Test(groups = {"P0","VCP","Operator Panel","testQueueIncomingTalkingRightClickListen","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickListen() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        step("3.右击Listen");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.LISTEN,"2000");
        sleep(3000);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssert.assertEquals(pjsip.getUserAccountInfo(0).callerId,"Call Monitor","分机0的来显应为Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssert.assertEquals(getExtensionStatus(0, TALKING, 8),TALKING,"预期分机0通话中");

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        softAssert.assertAll();
        //todo cdr校验
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击点击 WHISPER" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickWhisper","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickWhisper() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        step("3.右击Whisper");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.WHISPER,"1000");
        sleep(3000);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssert.assertEquals(pjsip.getUserAccountInfo(0).callerId,"Call Monitor","分机0的来显应为Call Monitor");
        pjsip.Pj_Answer_Call(0,200,false);
        softAssert.assertEquals(getExtensionStatus(0, TALKING, 8),TALKING,"预期分机0通话中");

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        //todo cdr校验
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Barge" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickWhisper","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickBarge() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        step("3.右击Barge");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.Barge_IN,"1000");
        sleep(3000);

        assertStep("4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssert.assertEquals(pjsip.getUserAccountInfo(0).callerId,"Call Monitor","分机0的来显应为Call Monitor");
        softAssert.assertEquals(getExtensionStatus(0, TALKING, 8),TALKING,"预期分机0通话中");

        assertStep("5.断言：Inbound&Internal Call表格中只有一条记录");
        Assert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),1,"Inbound&Internal Call表格中只有一条记录");

        assertStep("6.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        //todo cdr校验
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickPark() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        clearasteriskLog();

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        assertStep("3.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        step("3.右击Park");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");

        assertStep("4.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"[6000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, Parked");

        sleep(5000);
        assertStep("5.Asterisk断言：分机1000听到停泊语音call-parked-at.slin，然后挂断");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("call-parked-at"),"[cli确认有停泊提示音]");
        softAssert.assertEquals(getExtensionStatus(1000, HUNGUP, 8),HUNGUP,"预期分机1000已挂断");

        //todo cdr校验
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickParkToUnPark() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        step("3.右击Park->右键点击Retrieve");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");

        assertStep("4.断言页面元素");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"0 [0]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickPark","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickRecord() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);


        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(5000);

        clearasteriskLog();

        step("3.右击Unrecording");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.PAUSE_RECORD,"");

        //todo 31版本bug，多余权限限制导致不能停止录音
        assertStep("4.[Asterisk断言]：cli打印停止录音");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("PAUSE MIXMON"),"[cli打印停止录音]");

        clearasteriskLog();
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"2000", OperatorPanelPage.RIGHT_EVENT.Resume_RECORD,"");

        assertStep("4.[Asterisk断言]：cli打印停止录音");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("UNPAUSE MIXMON"),"[cli打印开始录音]");

        assertStep("4.断言页面元素");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToRingGroup","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToRingGroup() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到响铃组6300");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6300");

        assertStep("【判断】：1001响铃");
        softAssert.assertEquals(getExtensionStatus(1001, RING, 5),RING,"预期响铃组6300的分机1001响铃");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External, Ring Group");

        assertStep("【判断】：1001 Talking");
        pjsip.Pj_Answer_Call(1001,200,false);
        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期响铃组6300的分机1001 Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External, Ring Group");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToQueue","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToQueue() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到响铃组6300");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6700");

        assertStep("【判断】：1001响铃");
        softAssert.assertEquals(getExtensionStatus(1001, RING, 5),RING,"预期分机1001响铃");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External, "+ OperatorPanelPage.RECORD_DETAILS.QUEUE_RING.getAlias());

        assertStep("【判断】：1001 Talking");
        pjsip.Pj_Answer_Call(1001,200,false);
        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期的分机1001 Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External, Queue");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToVoicemail","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToVoicemail() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到Voicemail");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");

        assertStep("【判断】：1001响铃");
        softAssert.assertEquals(getExtensionStatus(1001, RING, 5),RING,"预期分机1001响铃");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Ringing");

        assertStep("【判断】：预期响分机1001 挂断，进入Voicemail");
//        sleep(30000);
        pjsip.Pj_Answer_Call(1001,404,false);
        sleep(8000);
        softAssert.assertEquals(getExtensionStatus(1001, HUNGUP, 5),HUNGUP,"预期响分机1001 挂断，进入Voicemail ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External, Voicemail");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToIVR","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToIVR() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到IVR 6200");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6200");

        assertStep("4.【判断】：界面显示到IVR");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"IR [6200]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, IVR");

        step("5.IVR 呼叫1001");
        pjsip.Pj_Send_Dtmf(2000,"1001");
        softAssert.assertEquals(getExtensionStatus(1001, RING, 5),RING,"预期分机1001响铃");
        pjsip.Pj_Answer_Call(1001,200,false);
        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期的分机1001 Talking");

        assertStep("6.[判断] 界面仅显示External，无IVR相关的");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001", OperatorPanelPage.RECORD.Details),"External");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("停泊后VCP控制面吧无记录")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToParking","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToParking() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(2000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到停泊号码6000");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6000");

        assertStep("【判断】：控制面板显示");
        softAssert.assertEquals(getExtensionStatus(1001, RING, 5),RING,"预期分机1001响铃");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"6000");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, Parked");

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n" +
            "3.右击Park" +
            "4.断言：分机0的来显应为Call Monitor，分机0处于Talking状态" +
            "5.断言：Inbound&Internal Call表格中只有一条记录" +
            "6.校验CDR")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferToConference","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferToConference() {

        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到会议室6500");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6500");

        assertStep("【判断】：1001响铃->接听");
        pjsip.Pj_Answer_Call(1001,200,false);
        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期的分机1001 Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"CO [6500]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, Conference");

        //todo cdr校验
        softAssert.assertAll();

    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 -->转移到内部号码C -->外线号码先挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferInternalAHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到内部分机1001");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");

        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        assertStep("【判断】：1001响铃->接听");
        pjsip.Pj_Answer_Call(1001,200,false);

        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期的分机1001 Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        pjsip.Pj_hangupCall(2000);
        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机1000接听 -->转移到内部号码C -->被转移号码C先挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingTalkingRightClickTransferInternalAHangup","Regression","PSeries"})
    public void testQueueIncomingTalkingRightClickTransferInternalCHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
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
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(2000);

        step("3.右击Transfer到内部分机1001");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");

        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        assertStep("【判断】：1001响铃->接听");
        pjsip.Pj_Answer_Call(1001,200,false);

        softAssert.assertEquals(getExtensionStatus(1001, TALKING, 5),TALKING,"预期的分机1001 Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        pjsip.Pj_hangupCall(1001);
        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->主叫挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingCallerHangup","Regression","PSeries"})
    public void testQueueIncomingCallerHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(3000);

        assertStep("3.判断控制面板");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee ),"1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status ),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External");

        step("4.主叫挂断,控制面板没有记录");
        pjsip.Pj_hangupCall(2000);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(),0);

        //todo cdr校验
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->被叫挂断 \n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P0","VCP","OperatorPanel","testQueueIncomingCalleeHangup","Regression","PSeries"})
    public void testQueueIncomingCalleeHangup() {
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("2.外线号码[2000]呼叫[1000]");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996400", DEVICE_ASSIST_2, false);
        sleep(3000);

        assertStep("3.判断控制面板");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller, "2000", OperatorPanelPage.RECORD.Callee), "1000 A [1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller, "2000", OperatorPanelPage.RECORD.Status), "Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller, "2000", OperatorPanelPage.RECORD.Details), "External");

        step("4.被叫挂断,控制面板没有记录");
        pjsip.Pj_hangupCall(1000);
        softAssert.assertEquals(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size(), 0);

        //todo cdr校验
        softAssert.assertAll();

    }

    //======================================================Drag and Drop 拖拽 ==========================================//
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到1001（Talking）\n" +
            "1:分机0,login web client\n" +
            "2:内部分机[1002]-->[1001]通话\n+" +
            "3:[2000 呼叫 1000]，1000 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropWithCTalking","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropWithCTalking(){
        prerequisite();

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
        pjsip.Pj_Make_Call_No_Answer(1002,"1001",DEVICE_IP_LAN,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.SHORT_WAIT*3);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,486,false);
        sleep(WaitUntils.SHORT_WAIT*4);
        refresh();

        assertStep("4:VCP 第一条显示状态 A--C Talking external,voicemail");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"2000", OperatorPanelPage.RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, Voicemail");

        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到1001（idle）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->空闲状态\n+" +
            "3:[2000 呼叫 1001]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropWithCIdle","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropWithCIdle(){
        prerequisite();

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
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1001 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("7:显示状态 A--C ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status),"Ringing");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"2000", OperatorPanelPage.RECORD.Callee),"1001 B [1001]");

        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("8:显示状态 A--C talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"2000", OperatorPanelPage.RECORD.Callee),"1001 B [1001]");

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到1001（未注册）\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Extension]1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropWithCUnregistered","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropWithCUnregistered(){
        prerequisite();

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

        step("5:【2000 呼叫 1000】，1000 接听为Talking状态");
        pjsip.Pj_Make_Call_Auto_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[Extension]1001");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"2000",OperatorPanelPage.DOMAIN.EXTENSION,"1001");
        sleep(WaitUntils.SHORT_WAIT*2);//todo  30版本 6s左右后  被叫号码状态才更新过来

        assertStep("7:[VCP]显示状态 2000--1001 Talking ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status),"Talking");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"2000", OperatorPanelPage.RECORD.Callee),"1001 B [1001]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details),"External, Voicemail");

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
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropRingGroup","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropRingGroup(){
        prerequisite();

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

        step("5:【2000 呼叫 1000】，1000 接听为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[RingGroup]6300");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.RINGGROUP,"6300");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(ringGroupName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(ringGroupName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(ringGroupName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getDetails()).as("验证分机1002_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getDetails()).as("验证分机1003_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());

        softAssertPlus.assertThat(allRecordList).as("验证RingGroup数量").size().isEqualTo(ringGroupMembers.size());

        step("7:显示状态1001 接通");
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Answer_Call(1001,false);

        assertStep("[VCP验证]7:显示状态 A--B ring");
        sleep(WaitUntils.SHORT_WAIT);
        List allRecordListAfter = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter, OperatorPanelPage.RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(ringGroupName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter, OperatorPanelPage.RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordListAfter, OperatorPanelPage.RECORD.Callee,"1001").getDetails()).as("验证分机1001_Details").contains(UI_MAP.getString("web_client.external").trim(),UI_MAP.getString("web_client.ringgroup").trim());//External, Ring Agent
        softAssertPlus.assertThat(allRecordListAfter).size().isEqualTo(1);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Queue 6400\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Ring状态\n" +
            "4:[Inbound]1000 -->拖动到[Queue]6400")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropQueue","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropQueue(){
        prerequisite();

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

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Queue]6400");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.QUEUE,"6400");

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");
        List allRecordList = auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getCaller()).as("验证分机1000_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getCaller()).as("验证分机1001_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getCaller()).as("验证分机1002_Caller").contains(queueListName+":2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getCaller()).as("验证分机1003_Caller").contains(queueListName+":2000 [2000]");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getStatus()).as("验证分机1000_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getStatus()).as("验证分机1001_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getStatus()).as("验证分机1002_Status").contains("Ringing");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getStatus()).as("验证分机1003_Status").contains("Ringing");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1000").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));//External, Ring Agent
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1001").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1002").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecord(allRecordList, OperatorPanelPage.RECORD.Callee,"1003").getDetails()).as("验证分机1000_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing"));

        softAssertPlus.assertThat(allRecordList).as("验证Queue数量").size().isEqualTo(queueMembers.size());

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，分机D拨打6000取回park，D接起后D挂")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropParking","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropParking(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"6000");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Caller))
                .as("验证_Caller").contains("2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee))
                .as("验证_Caller").contains("6000");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status))
                .as("验证_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details))
                .as("验证_Details").contains(UI_MAP.getString("web_client.external"),UI_MAP.getString("web_client.parked"));//External, Parked

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        pjsip.Pj_Make_Call_Auto_Answer(1001,"6000",DEVICE_IP_LAN);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Caller))
                .as("验证_Caller").contains("2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee))
                .as("验证_Caller").contains("1001 B [1001]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status))
                .as("验证_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details))
                .as("验证_Details").contains(UI_MAP.getString("web_client.external"));//External

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键Transfer")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropParkingRightClickTransfer","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropParkingRightClickTransfer(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"6000");

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Caller))
                .as("验证_Caller").contains("2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee))
                .as("验证_Caller").contains("6000");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status))
                .as("验证_Status").contains("Talking");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details))
                .as("验证_Details").contains(op_external,op_parked);//External, Parked

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000",OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1001");
        pjsip.Pj_Answer_Call(1001,false);

        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Caller))
                .as("验证_Caller").contains("2000 [2000]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Callee))
                .as("验证_Caller").contains("1001 B [1001]");
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Status))
                .as("验证_Status").contains(op_talking);
        softAssertPlus.assertThat(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000", OperatorPanelPage.RECORD.Details))
                .as("验证_Details").contains(op_external);//External

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键UnPark")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropParkingRightClickUnpark","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropParkingRightClickUnpark(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"6000");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000",
                "2000 [2000]","[6000]",op_talking,String.format(opf_detial,op_external,op_parked));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000",OperatorPanelPage.RIGHT_EVENT.RETRIEVE,"");
        pjsip.Pj_Answer_Call(0,false);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"[0]",
                "2000 [2000]","0 [0]",op_talking,op_external);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时右键Hangup")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropParkingRightClickHangup","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropParkingRightClickHangup(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP验证]");
        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000",
                "2000 [2000]","[6000]",op_talking,String.format(opf_detial,op_external,op_parked));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "2000",OperatorPanelPage.RIGHT_EVENT.HANG_UP,"");

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 接听 -->DragAndDrop 到Park 6000\n" +
            "1:分机0,login web client\n" +
            "2:[1001(idle)]-->未注册\n+" +
            "3:[2000 呼叫 1000]，1001 为Talking状态\n" +
            "4:[Inbound]1000 -->拖动到[Park]6000" +
            "5:被park后，在park期间,通话未被取回时主叫挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testQueueIncomingDragAndDropParkingHangup","Regression","PSeries","VCP2"})
    public void testQueueIncomingDragAndDropParkingHangup(){
        prerequisite();

        step("1:login web client");
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        step("3:[PJSIP注册] 分机创建并注册");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        step("5:【2000 呼叫 1000】，1000接听 为Talking状态");
        pjsip.Pj_Make_Call_No_Answer(2000,"996400",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);

        step("6：[Inbound]1000 -->拖动到[到Parking]6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1000",OperatorPanelPage.DOMAIN.PARKING,"6000");

        assertStep("[VCP验证]");
        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Caller,"2000",
                "2000 [2000]","[6000]",op_talking,String.format(opf_detial,op_external,op_parked));

        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[VCP验证]");

        pjsip.Pj_hangupCall(2000);

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }
}
