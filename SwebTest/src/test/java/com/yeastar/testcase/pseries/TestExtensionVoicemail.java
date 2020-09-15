package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.*;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.CdrRecording.ICdrPageElement;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import com.yeastar.swebtest.driver.SwebDriverP;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement.*;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;


/**
 * @program: SwebTest
 * @description: 分机模块用例-voicemail
 * @author: linhaoran@yeastar.com
 * @create: 2020/06/18
 */
@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionVoicemail extends TestCaseBase {

    private boolean runRecoveryEnvFlag = true;
    private String reqDataCreateExtension = String.format("" +
            "{\"type\":\"SIP\",\"first_name\":\"Yeastar Test0\",\"last_name\":\"朗视信息科技\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"0\",\"caller_id\":\"(0591)-Ys.0\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTIzNA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":1,\"enb_vm_play_caller_id\":1,\"enb_vm_play_duration\":1,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"0\",\"reg_password\":\"%s\",\"allow_reg_remotely\":0,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"            ,enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)),enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            ,SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2);
    /**
     * voicemail 环境准备
     * 修改分机0voicemail页面，启用voicemail，
     * 启用voicemail pin Authentication，
     * pin码设置为1234，
     * New Voicemail Notification设置为send email notification with attachment，
     * afternotification设置为No action，
     * 勾选play date  and time/caller id/durations，
     * voicemail greeting 默认（default为follow system，available等presence状态保持默认none），
     * 保存并应用
     */
    public void preparationSteps() {

        if(runRecoveryEnvFlag){
            APIUtil apiUtil = new APIUtil();
            List<String> trunks = new ArrayList<>();
            trunks.add(SPS);

            step("设置cdr名称显示格式:first_last,最大通话时长1800s");
            apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\",\"max_call_duration\":1800}")
                    .linkusserverUpdate("{\"enable_extension_login\":1,\"enable_email_login\":1}");

            step("创建分机组");
            apiUtil.deleteAllExtensionGroup().createExtensionGroup("{  \"name\": \"Default_Extension_Group\",  \"member_list\": [],  \"member_select\": \"sel_all_ext\",  \"share_group_info_to\": \"all_ext\",  \"specific_extensions\": [],  \"mgr_enb_widget_in_calls\": 1,  \"mgr_enb_widget_out_calls\": 1,  \"mgr_enb_widget_ext_list\": 1,  \"mgr_enb_widget_ring_group_list\": 1,  \"mgr_enb_widget_queue_list\": 1,  \"mgr_enb_widget_park_ext_list\": 1,  \"mgr_enb_widget_vm_group_list\": 1,  \"mgr_enb_chg_presence\": 1,  \"mgr_enb_call_distribution\": 1,  \"mgr_enb_call_conn\": 1,  \"mgr_enb_monitor\": 1,  \"mgr_enb_call_park\": 1,  \"mgr_enb_ctrl_ivr\": 1,  \"mgr_enb_office_time_switch\": 1,  \"mgr_enb_mgr_recording\": 1,  \"user_enb_widget_in_calls\": 0,  \"user_enb_widget_out_calls\": 0,  \"user_enb_widget_ext_list\": 0,  \"user_enb_widget_ring_group_list\": 0,  \"user_enb_widget_queue_list\": 0,  \"user_enb_widget_park_ext_list\": 0,  \"user_enb_widget_vm_group_list\": 0,  \"user_enb_chg_presence\": 0,  \"user_enb_call_distribution\": 0,  \"user_enb_call_conn\": 0,  \"user_enb_monitor\": 0,  \"user_enb_call_park\": 0,  \"user_enb_ctrl_ivr\": 0 }");
            String groupList = apiUtil.getInitialdata("extension").getString("group_list").replace("\"user\"", "\"manager\"");

            step("删除所有分机 -> 创建分机0");
            apiUtil.deleteAllExtension().createExtension(reqDataCreateExtension.replace("GROUPLIST",groupList));

            step("删除spstrunk -> 创建sps trunk");
            apiUtil.deleteTrunk(SPS).createSIPTrunk(reqDataCreateSPS);

            step("删除呼入路由 -> 创建呼入路由InRoute1");
            apiUtil.deleteAllInbound().createInbound("InRoute1",trunks,"Extension","0").apply();

            auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);

            //todo 26版本bug，删除分机提示音不会删除，此处手动兼容此问题

            step("录制voicemail greeting");
            SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, "rm -rf /ysdisk/ysapps/pbxcenter/var/lib/asterisk/sounds/record/0/*");
            pjsip.Pj_Init();
            pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
            pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
            auto.extensionPage().editExtension(getDriver(),"0").recordVoicemailGreeting("0-Yeastar Test0 朗视信息科技","test");
            softAssert.assertEquals(getExtensionStatus(0, RING, 8),RING,"预期分机0响铃");
            pjsip.Pj_Answer_Call(0,200,false);
            sleep(15000);
            pjsip.Pj_Hangup_All();
            pjsip.Pj_Destory();
            auto.extensionPage().clickSave();
            softAssert.assertAll();

            apiUtil.apply();
            apiUtil.loginWebClient("0", EXTENSION_PASSWORD, EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
        }

    }


    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "login PBX" +
            "删除spstrunk -> 创建sps trunk" +
            "辅助设备分机2000通过sps trunk呼入，进入分机0的voicemial"+
            "cli确认播放提示音为vm-greeting-leave-after-tone.slin")
    @Issue("V26分机voicemail页面消失 ")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void test2000To0Voicemail() throws IOException, JSchException {
        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(30000);
        pjsip.Pj_Hangup_All();

        assertStep("cli确认播放提示音为vm-greeting-leave-after-tone.slin");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2000<2000>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2000<2000> hung up",communication_inbound,SPS,"");
        softAssert.assertAll();

        //todo 特征码*2要重置设置
        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Make_Call_No_Answer(0,"*2",DEVICE_IP_LAN,false);
        sleep(2000);
        pjsip.Pj_Send_Dtmf(0,"1234#");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(0,"1");
        sleep(60000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();
        step("查看pbxlog.0，检查vm-received.gsm、vm-from-phonenumber、vm-duration.slin提示音字段");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-received"),"[Assert,cli确认提示音vm-received]");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-from-phonenumber"),"[Assert,cli确认提示音vm-from-phonenumber]");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-duration"),"[Assert,cli确认提示音vm-duration]");
        softAssert.assertAll();

        auto.homePage().logout();
        assertStep("分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
//        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
        
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为available" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForAvailable(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Available ");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,true)
                .selectComm(ele_extension_voicemail_available_combobox,"test.wav");
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.AVAILABLE.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("cli确认播放提示音为test.slin");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为away" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForAway(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Away ");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectComm(ele_extension_voicemail_away_combobox,"test.wav");
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.AWAY.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("cli确认播放提示音为test.slin");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为Bussiness trip" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForBusinessTrip(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Business Trip ");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectComm(ele_extension_voicemail_businessTrip_combobox,"test.wav");
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.BUSINESSTRIP.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("cli确认播放提示音为test.slin");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为dnd" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForDnd(){
        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Do Not Disturb");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectComm(ele_extension_voicemail_doNotDisturb_combobox,"test.wav");
        auto.extensionPage().clickSaveAndApply();
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(TABLE_MENU.PRESENCE.getAlias()).ele_extension_presence_doNotDisturb_tab.click();
        auto.extensionPage().isCheckbox(ele_extension_presence_forward_enb_ex_always_forward_checkBox,true).clickSaveAndApply();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.DONotDISTURB.getAlias()).clickApply();


        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();


        step("cli确认播放提示音为test.slin");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为Lunch" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForLunch(){
        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Lunch Break");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectComm(ele_extension_voicemail_lunchBreak_combobox,"test.wav");
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.LUNCHBREAK.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();


        step("cli确认播放提示音为test6.wav");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为Off Work" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailGreetingForOffWork(){
        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("设置分机0状态为Off Work");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectComm(ele_extension_voicemail_offWork_combobox,"test.wav");
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.OFFWORK.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();


        step("cli确认播放提示音为test.slin");
        Assert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test.slin"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testNotification1(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
//        preparationSteps();

        step("修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0")
                .setElementValue(ele_extension_user_email_addr,EMAIL)
                .switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,true)
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.SEND_EMAIL_NOTIFICATIONS_WITH_ATTACHMENT.getAlias())
                .selectCombobox(IExtensionPageElement.AFTER_NOTIFICATION.DELETE_VOICEMAIL.getAlias()).clickSaveAndApply();

        int emailUnreadCount_before = MailUtils.getEmailUnreadMessageCountFrom163();
        clearasteriskLog();

        step("辅助设备分机2001通过sps trunk呼入，进入voicemial");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2001,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
//

//        step("【验证邮箱服务器是否能正常】通过修改分机1001密码，验证是否能收到邮件");
//        sleep(30000);//等待接收邮件
//        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
//
//        log.debug("[邮箱服务器功能验证][测试前邮箱数量] "+emailUnreadCount_before+"-->>[验证邮箱功能，数量+1] "+emailUnreadCount_after);
//        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"邮箱服务器正常，邮件正常接收");

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2000<2000>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2000<2000> hung up",communication_inbound,SPS,"");//DEVICE_ASSIST_2 辅助设置SIP 呼出名称设置为 2000 （VCP多线路基础配置）

        //todo 检查分机页面
        assertStep("分机0登录webclient，voicemail页面未新增一条来自2001未读的留言记录");
        auto.homePage().logout();
//        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");//DEVICE_ASSIST_2 辅助设置SIP 呼出名称设置为 2000 （VCP多线路基础配置）
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为Off Work" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testNotification2(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();
        step("修改New Voicemail Notification设置为send email notification without attachment，afternotification设置为mark as read");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,true)
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.SEND_EMAIL_NOTIFICATIONS_WITHOUT_ATTACHMENT.getAlias())
                .selectCombobox(IExtensionPageElement.AFTER_NOTIFICATION.MARK_AS_READ.getAlias()).clickSaveAndApply();

        step("辅助设备分机2002通过sps trunk呼入，进入voicemial");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2002,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2002,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();


        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2000<2000>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2000<2000> hung up",communication_inbound,SPS,"");

        assertStep("分机0登录webclient，voicemail页面新增一条已读的来自2002的语音留言");
        auto.homePage().logout();
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");//DEVICE_ASSIST_2 辅助设置SIP 呼出名称设置为 2000
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "设置状态为Off Work" +
            "清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testNotification3(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("修改New Voicemail Notification设置为do not send email notification");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,true)
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.DO_NOT_SEND_EMAIL_NOTIFICATIONS.getAlias()).clickSaveAndApply();

        step("辅助设备分机2002通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2002,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2002,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();


        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2000<2000>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2000<2000> hung up",communication_inbound,SPS,"");

        assertStep("分机0登录webclient，voicemail页面新增一条未读的来自2002的语音留言");
        auto.homePage().logout();
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        log.debug("[me_name] "+me_name);
        Assert.assertTrue(me_name.contains("2000\n" +"External Number"));//DEVICE_ASSIST_2 辅助设置SIP 呼出名称设置为 2000
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "分机0禁用voicemail，保存并应用" +
            "辅助设备分机2000通过sps trunk呼入，未进入voicemial")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = {"P0","TestExtensionVoicemail","Extension","Regression","PSeries","Voicemail"})
    public void testVoicemailDisable(){

        step("登录 PBX");
        loginWithAdmin();

        step("环境准备");
        preparationSteps();

        step("分机0禁用voicemail，保存并应用");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0").switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,false).clickSaveAndApply();

        step("辅助设备分机2000通过sps trunk呼入，未进入voicemial");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);

        softAssert.assertEquals(getExtensionStatus(2000, HUNGUP, 8),HUNGUP,"预期分机2000直接被挂断");
        pjsip.Pj_Hangup_All();

        softAssert.assertAll();
    }

}
