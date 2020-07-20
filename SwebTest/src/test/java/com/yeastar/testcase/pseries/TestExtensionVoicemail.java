package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.*;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.CdrRecording.ICdrPageElement;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.ArrayList;

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

//    @Test
    public void CaseName() throws InterruptedException {

        step("登录 PBX");
        loginWithAdmin();
        Assert.fail();
        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        preparationSteps();

    }

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

        //设置cdr名称显示格式
        auto.homePage().intoPage(HomePage.Menu_Level_1.pbx_settings, HomePage.Menu_Level_2.pbx_settings_tree_preferences);
        auto.preferencesPage().selectCombobox(IPreferencesPageElement.NAME_DISPLAY_FORMAT.FIRST_LAST_WITH_SPACE.getAlias())
                .setElementValue(IPreferencesPageElement.ele_pbx_settings_preferences_max_call_duration_select,"1800")
                .clickSaveAndApply();

        step("删除所有分机 -> 创建分机0");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().choiceLinkusServer().
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_extension_login_checkbox,true).
                setCheckbox(IExtensionPageElement.ele_extension_server_enable_email_login_checkbox,true).
                clickSaveAndApply();

        auto.extensionPage().deleAllExtension()
                .createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD)
                .switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,true)
                .selectCombobox(IExtensionPageElement.VOICEMAIL_PIN_AUTH.ENABLED.getAlias())
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.DO_NOT_SEND_EMAIL_NOTIFICATIONS.getAlias())
                .isCheckbox(IExtensionPageElement.ele_extension_voicemail_play_date_time_checkbox,true)
                .isCheckbox(IExtensionPageElement.ele_extension_voicemail_play_caller_id_checkbox,true)
                .isCheckbox(IExtensionPageElement.ele_extension_voicemail_play_message_duration_checkbox,true)
                .selectCombobox(IExtensionPageElement.DEFAULT_GREETING.FOLLOW_SYSTEM.getAlias())
                .ele_extension_voicemail_access_pin.setValue("1234");
        auto.extensionPage().clickSaveAndApply();
        sleep(5000);

        //todo 26版本bug，删除分机提示音不会删除，此处手动兼容此问题
        step("录制voicemail greeting");
        SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, "rm /ysdisk/ysapps/pbxcenter/var/lib/asterisk/sounds/record/0/*");
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

        step("删除spstrunk -> 创建sps trunk");
        sleep(3000);
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        auto.trunkPage().deleteTrunk(getDriver(),SPS).createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

        step("删除呼入路由 -> 创建呼入路由InRoute1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        ArrayList<String> trunklist = new ArrayList<>();
        trunklist.add(SPS);
        auto.inboundRoute().deleteAllInboundRoutes()
                .createInboundRoute("InRoute1",trunklist)
                .editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技")
                .clickSaveAndApply();
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
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
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
        pjsip.Pj_Destory();

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
        pjsip.Pj_Destory();

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
        pjsip.Pj_Destory();

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
        auto.extensionPage().clickSave();
        auto.extensionPage().selectExtensionPresence("0",IExtensionPageElement.TABLE_PRESENCE_LIST.DONotDISTURB.getAlias()).clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

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
        pjsip.Pj_Destory();

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
        pjsip.Pj_Destory();

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
        preparationSteps();

        step("修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().editExtension(getDriver(),"0")
                .setElementValue(ele_extension_user_email_addr,"lhr@yeastar.com")
                .switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
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
        pjsip.Pj_Destory();

//        step("【验证邮箱服务器是否能正常】通过修改分机1001密码，验证是否能收到邮件");
//        sleep(30000);//等待接收邮件
//        int emailUnreadCount_after = MailUtils.getEmailUnreadMessageCountFrom163();
//
//        log.debug("[邮箱服务器功能验证][测试前邮箱数量] "+emailUnreadCount_before+"-->>[验证邮箱功能，数量+1] "+emailUnreadCount_after);
//        softAssert.assertEquals(emailUnreadCount_before+1,emailUnreadCount_after,"邮箱服务器正常，邮件正常接收");

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2001<2001>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2001<2001> hung up",communication_inbound,SPS,"");

        //todo 检查分机页面
        assertStep("分机0登录webclient，voicemail页面未新增一条来自2001未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertEquals(me_name,"2001\n" +"External Number");
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
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.SEND_EMAIL_NOTIFICATIONS_WITHOUT_ATTACHMENT.getAlias())
                .selectCombobox(IExtensionPageElement.AFTER_NOTIFICATION.MARK_AS_READ.getAlias()).clickSaveAndApply();

        step("辅助设备分机2002通过sps trunk呼入，进入voicemial");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2002,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2002,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2002<2002>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2002<2002> hung up",communication_inbound,SPS,"");

        assertStep("分机0登录webclient，voicemail页面新增一条已读的来自2002的语音留言");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertEquals(me_name,"2002\n" +"External Number");
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
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.DO_NOT_SEND_EMAIL_NOTIFICATIONS.getAlias()).clickSaveAndApply();

        step("辅助设备分机2002通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2002,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2002,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2002,"99550330",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().assertCDRRecord(getDriver(),0,"2002<2002>","Voicemail Yeastar Test0 朗视信息科技<0>","VOICEMAIL","2002<2002> hung up",communication_inbound,SPS,"");

        assertStep("分机0登录webclient，voicemail页面新增一条未读的来自2002的语音留言");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        Assert.assertNotEquals(me_name,"2002\n" +"External Number");
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
        pjsip.Pj_Destory();
        softAssert.assertAll();
    }

}
