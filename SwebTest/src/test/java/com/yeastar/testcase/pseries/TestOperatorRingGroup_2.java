package com.yeastar.testcase.pseries;

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
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;

public class TestOperatorRingGroup_2 extends TestCaseBase {
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
    private final String ringGroupName2 = "RI";//6301
    private final String conferenceName2 = "CF";

    private final ArrayList<String> queueMembers = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers = new ArrayList<>();
    private final ArrayList<String> conferenceMember = new ArrayList<>();
    private final ArrayList<String> queueMembers2 = new ArrayList<>();
    private final ArrayList<String> ringGroupMembers2 = new ArrayList<>();
    private final ArrayList<String> conferenceMember2 = new ArrayList<>();

    private final String op_external = UI_MAP.getString("web_client.external").trim();
    private final String op_talking = UI_MAP.getString("web_client.talking").trim();
    private final String op_ringing = UI_MAP.getString("web_client.ringing").trim();
    private final String op_ringgroup = UI_MAP.getString("web_client.ringgroup").trim();
    private final String op_parked = UI_MAP.getString("web_client.parked").trim();
    private final String op_conference = UI_MAP.getString("web_client.conference").trim();
    private final String op_queue = UI_MAP.getString("web_client.queue").trim();
    private final String op_voicemail = UI_MAP.getString("web_client.voicemail").trim();
    private final String op_ivr = UI_MAP.getString("web_client.ivr").trim();
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

            step("创建IVR");
//            apiUtil.dela

            step("设置网络磁盘");
            extensionNum.add("0");
            extensionNum.add("1");
            apiUtil.deleteNetworkDrive().setNetworkDriver().updateAutoRecord(emptyList,extensionNum,emptyList,emptyList);

            List<InboundRouteObject> inboundObjectList = apiUtil.getInboundSummary();

            for(int i=0; i<inboundObjectList.size(); i++){
                InboundRouteObject object = inboundObjectList.get(i);
                if(object.name.equals("InRoute1")){
                    RingGroupObject rg = apiUtil.getRingGroupSummary("6300");
                    apiUtil.editInbound(String.format("{\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\",\"id\":%s}",rg.id,object.id));
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
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听后挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingStatus","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingStatus(){
        prerequisite();
        
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

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        assertStep("4:Talking显示状态 ");
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1000",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_ringgroup));
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有一条记录").isEqualTo(1);

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->[RingGroup] 呼入6300 1001接听-->右键不显示\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]6300" +
            "3.1000接听\n" +
            "4:右键->查看显示的条目")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingRightActionUnDisplay","Regression","PSeries","VCP1"})
    public void testRGIncomingRightActionUnDisplay(){
        prerequisite();
        

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

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:[VCP显示] 不显示 Redirect，pick up，hang up");
        List list =  auto.operatorPanelPage().getRightEvent(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001");

        softAssertPlus.assertThat(list).doesNotContain(OperatorPanelPage.RIGHT_EVENT.REDIRECT,OperatorPanelPage.RIGHT_EVENT.PICK_UP,OperatorPanelPage.RIGHT_EVENT.HANG_UP);//equals  assertThat(list).doesNotContain("Transfer","Listen","Whisper","Barge","Park","Unpark","Pause","Resume");
        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听后挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingHangup","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingHangup(){
        prerequisite();
        

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

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("4:右击选择挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.HANG_UP);

        assertStep("4:控制面板显示状态 ");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板没有记录").isEqualTo(0);

        pjsip.Pj_Hangup_All();
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听右击Linsten\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingRightClickListen","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingRightClickListen(){
        prerequisite();
        

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("4:右击选择挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.LISTEN);
        sleep(3000);

        assertStep("5.分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").contains("Call Monitor");

        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("预期分机0通话中").isEqualTo(TALKING);

        assertStep("6.Inbound&Internal Call表格中只有一条记录");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有1条记录").isEqualTo(1);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_ringgroup));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听右击Whisper\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingRightClickWhisper","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingRightClickWhisper(){
        prerequisite();
        

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("4:右击选择挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.WHISPER,"1001");
        sleep(3000);

        assertStep("5.分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").contains("Call Monitor");

        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("预期分机0通话中").isEqualTo(TALKING);

        assertStep("6.Inbound&Internal Call表格中只有一条记录");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有1条记录").isEqualTo(1);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_ringgroup));
        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听右击Barge\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingRightClickBarge","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingRightClickBarge(){
        prerequisite();
        

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        step("4:右击选择挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.Barge_IN,"1001");
        sleep(3000);

        assertStep("5.分机0的来显应为Call Monitor，分机0处于Talking状态");
        softAssertPlus.assertThat(pjsip.getUserAccountInfo(0).callerId).as("分机0的来显应为Call Monitor").contains("Call Monitor");

        pjsip.Pj_Answer_Call(0,200,false);
        softAssertPlus.assertThat(getExtensionStatus(0, TALKING, 8)).as("预期分机0通话中").isEqualTo(TALKING);

        assertStep("6.Inbound&Internal Call表格中只有一条记录");
        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有1条记录").isEqualTo(1);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_ringgroup));
        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听右击Parked\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingRightClickPark","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingRightClickPark(){
        prerequisite();

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_ringgroup));

        step("4:右击选择Parked");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.PARKED,"2000");
        sleep(3000);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","1001 B [1001]",op_talking, String.format(opf_detial,op_external,op_parked));

        //todo unparK后被叫分机显示不正确
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND,"1001", OperatorPanelPage.RIGHT_EVENT.RETRIEVE);
        pjsip.Pj_Answer_Call(0,200,false);
        sleep(3000);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus,OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee,"1001",
                "RG:2000 [2000]","0 [0]",op_talking, op_external);

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听右击Parked\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingRightClickRecord","Regression","PSeries","VCP1"})
    public void testRGIncomingTalkingRightClickRecord(){
        prerequisite();

        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT*2);

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

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听后挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到响铃组6301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToRG6301","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToRG6301() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到响铃组6301");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6301");

        assertStep("4:6301响铃组响铃，1004接听 ");
        sleep(1000);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RI:2000 [2000]", "1004 E [1004]", op_ringing, String.format(opf_detial, op_external, op_ringgroup));

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1005",
                "RI:2000 [2000]", "1005 F [1005]", op_ringing, String.format(opf_detial, op_external, op_ringgroup));

        pjsip.Pj_Answer_Call(1004,200,false);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RI:2000 [2000]", "1004 E [1004]", op_talking, String.format(opf_detial, op_external, op_ringgroup));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听后挂断\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到队列6401,1003接听")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToQE6401","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToQE6401() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6401队列 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1005, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到队列6401");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6401");

        assertStep("4:6401接听，1004接听 ");
        sleep(1000);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "QE:2000 [2000]", "1004 E [1004]", op_ringing, String.format(opf_detial, op_external, op_queue));

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1005",
                "QE:2000 [2000]", "1005 F [1005]", op_ringing, String.format(opf_detial, op_external, op_queue));

        pjsip.Pj_Answer_Call(1004,200,false);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "QE:2000 [2000]", "1004 E [1004]", op_talking, String.format(opf_detial, op_external, op_queue));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到分机1004，1004发486，进入1004语音留言")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToVoicemail","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToVoicemail() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到1004");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        assertStep("5:1004 发486进入语音留言");
        sleep(1000);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking, String.format(opf_detial, op_external, op_voicemail));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到分机ivr6201，按分机号转到1004，接听")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToIVR","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToIVR6201() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到ivr6201");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6201");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6201",
                "RG:2000 [2000]", "IV [6201]", op_talking, String.format(opf_detial, op_external, op_ivr));

        assertStep("5:2000按分机号1004,1004接听");
        sleep(1000);
        pjsip.Pj_Send_Dtmf(2000,"1004");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "IV:2000 [2000]", "1004 E [1004]", op_ringing, String.format(opf_detial, op_external, op_voicemail));

        pjsip.Pj_Answer_Call(1004,200,false);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "IV:2000 [2000]", "1004 E [1004]", op_talking, String.format(opf_detial, op_external, op_voicemail));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到park6000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToParking6000","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToParking6000() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到Park6000");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6000");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking, String.format(opf_detial, op_external, op_parked));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到分机会议室6501")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToConference6401","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToConference6401() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到会议室6501");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"6501");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6501",
                "RG:2000 [2000]", "CF [6501]", op_talking, String.format(opf_detial, op_external, op_conference));

        pjsip.Pj_Hangup_All();
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到分机内部分机1004" +
            "4:2000先挂")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToAHangup","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToAHangup() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);

        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到分机1004");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_ringing,op_external);

        pjsip.Pj_Answer_Call(1004,200,false);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking,op_external);

        pjsip.Pj_hangupCall(2000);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1000接听\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup] 1001接听\n" +
            "3:转移到分机内部分机1004" +
            "4:1004先挂")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingTransferToInternalBHangup","Regression","PSeries"})
    public void testRGIncomingTalkingTransferToInternalBHangup() {
        prerequisite();

        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300响铃组 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1001, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1002, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1003, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(1004, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);
        pjsip.Pj_CreateAccount(2000, EXTENSION_PASSWORD, "UDP", UDP_PORT, -1);


        pjsip.Pj_Register_Account_WithoutAssist(1000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000, "996300", DEVICE_ASSIST_2, false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("4:右击选择Transfer到分机1004");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "1001", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_ringing,op_external);

        pjsip.Pj_Answer_Call(1004,200,false);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking,op_external);

        pjsip.Pj_hangupCall(1004);

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到分机1004-\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDrop1004Busy","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDrop1004Busy(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(1004,"1005",DEVICE_IP_LAN);
        pjsip.Pj_Answer_Call(1005, 200, false);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.EXTENSION,"1004");
        sleep(WaitUntils.SHORT_WAIT);

        step("1004忙，转入voicemail");
        pjsip.Pj_Answer_Call(1004,486,false);
        sleep(WaitUntils.SHORT_WAIT*2);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking,String.format(opf_detial, op_external, op_voicemail));

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1005",
                "1004 E [1004]", "1005 F [1005]", op_talking,op_external);

        softAssertPlus.assertAll();

        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到分机1004-\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDrop1004Idle","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDrop1004Idle(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.EXTENSION,"1004");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_ringing,op_external);

        step("1004接听");
        pjsip.Pj_Answer_Call(1004,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking,op_external);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到分机1004-\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDrop1004UnRegister","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDrop1004UnRegister(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.EXTENSION,"1004");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_ringing,String.format(opf_detial, op_external, op_voicemail));

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到响铃组6301-\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropRG","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropRG(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.RINGGROUP,"6301");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RI:2000 [2000]", "1004 E [1004]", op_ringing,String.format(opf_detial, op_external, op_ringgroup));
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1005",
                "RI:2000 [2000]", "1005 F [1005]", op_ringing,String.format(opf_detial, op_external, op_ringgroup));

        pjsip.Pj_Answer_Call(1004,200,false);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RI:2000 [2000]", "1004 E [1004]", op_talking,String.format(opf_detial, op_external, op_ringgroup));

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("控制面板只有1条记录").isEqualTo(1);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到队列6401-\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropQE6401","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropQE6401(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.QUEUE,"6401");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "QE:2000 [2000]", "1004 E [1004]", op_ringing,String.format(opf_detial, op_external, op_queue));
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1005",
                "QE:2000 [2000]", "1005 F [1005]", op_ringing,String.format(opf_detial, op_external, op_queue));

        pjsip.Pj_Answer_Call(1004,200,false);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "QE:2000 [2000]", "1004 E [1004]", op_talking,String.format(opf_detial, op_external, op_queue));

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("控制面板只有1条记录").isEqualTo(1);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，分机D拨打6900取回park，D接起后D挂\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropPark6000Answer","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropPark6000_1(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到分机1004");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked));

        pjsip.Pj_Make_Call_Auto_Answer(1004,"6000",DEVICE_IP_LAN);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking ,op_external);

        pjsip.Pj_hangupCall(1004);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }


    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，通话未被取回时 邮件Transfer 转给1004\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropParking6000_2","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropParking6000_2(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到Park 6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked));

        step("5:右击转移");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "6000", OperatorPanelPage.RIGHT_EVENT.TRANSFER,"1004");

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_ringing ,op_external);

        pjsip.Pj_Make_Call_Auto_Answer(1004,"6000",DEVICE_IP_LAN);
        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1004",
                "RG:2000 [2000]", "1004 E [1004]", op_talking ,op_external);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，通话未被取回时 unpark\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropParking6000_3","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropParking6000_3(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到Park 6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked));

        step("5:右击取回");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "6000", OperatorPanelPage.RIGHT_EVENT.RETRIEVE);

        pjsip.Pj_Answer_Call(0,200,false);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "[0]",
                "RG:2000 [2000]", "0 [0]", op_talking ,op_external);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，2000挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropParking6000_4","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropParking6000_4(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到Park 6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked));

        step("5:右击挂断");
        auto.operatorPanelPage().rightTableAction(OperatorPanelPage.TABLE_TYPE.INBOUND, "6000", OperatorPanelPage.RIGHT_EVENT.HANG_UP);

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，未等取回，2000挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropParking6000_5","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropParking6000_5(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到Park 6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked));

        pjsip.Pj_hangupCall(2000);

        softAssertPlus.assertThat(auto.operatorPanelPage().getAllRecord(OperatorPanelPage.TABLE_TYPE.INBOUND).size()).as("操作面板只有没有记录").isEqualTo(0);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到-->RingGroup-->RingGroup 1001接听 -->]\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[RingGroup]" +
            "3.拖拽到Parking 6000-" +
            "4.被park后，park超时，回拨回B，B接起，A挂\n")
   @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testRGIncomingTalkingDragAndDropParking6000_6","Regression","PSeries"})
    public void testRGIncomingTalkingDragAndDropParking6000_6(){
        prerequisite();
        auto.loginPage().login("0",EXTENSION_PASSWORD_NEW);

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);

        assertStep("3:[PJSIP注册] ，2000 呼叫 6300 ,1001接听");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1003,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1004,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(1005,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1002,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1003,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1004,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(1005,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"996300",DEVICE_ASSIST_2,false);
        pjsip.Pj_Answer_Call(1001, 200, false);
        sleep(WaitUntils.SHORT_WAIT);

        step("4：分机1001-->拖动到Park 6000");
        auto.operatorPanelPage().dragAndDrop(OperatorPanelPage.DOMAIN.INBOUND,"1001",OperatorPanelPage.DOMAIN.PARKING,"6000");

        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "6000",
                "RG:2000 [2000]", "[6000]", op_talking ,String.format(opf_detial, op_external, op_parked),"拖拽至停泊6000");

        sleep(WaitUntils.SHORT_WAIT * 20);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1001",
                "RG:2000 [2000]", "1001 B [1001]", op_ringing ,op_external,"回拨至1001响铃");

        pjsip.Pj_Answer_Call(1001,200,false);
        sleep(WaitUntils.SHORT_WAIT);

        auto.operatorPanelPage().assertRecordValue(softAssertPlus, OperatorPanelPage.TABLE_TYPE.INBOUND, OperatorPanelPage.RECORD.Callee, "1001",
                "RG:2000 [2000]", "1001 B [1001]", op_talking ,op_external,"回拨至1001接听");

        pjsip.Pj_hangupCall(2000);

        softAssertPlus.assertAll();
        pjsip.Pj_Hangup_All();
    }
}
