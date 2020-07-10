package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.*;
import com.yeastar.page.pseries.CallControl.IInboundRoutePageElement;
import com.yeastar.page.pseries.CdrRecording.ICdrPageElement;
import com.yeastar.page.pseries.ExtensionTrunk.IExtensionPageElement;
import com.yeastar.page.pseries.PbxSettings.IPreferencesPageElement;
import com.yeastar.page.pseries.WebClient.Me_HomePage;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;


/**
 * @program: SwebTest
 * @description: 分机模块用例-voicemail
 * @author: linhaoran@yeastar.com
 * @create: 2020/06/18
 */
//@Listeners({AllureReporterListener.class, TestNGListenerP.class})
@Log4j2
public class TestExtensionVoicemail extends TestCaseBase {

//    @Test
    public void CaseName() throws InterruptedException {

//        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
//        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));
//
//        preparationSteps();
//        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        //todo 分机webclient页面检查留言
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        String me_time = TableUtils.getTableForHeader(getDriver(),"Time",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
//        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
//        if(TableUtils.clickTableDeletBtn(getDriver(),"Name",SPS)){
//            auto.trunkPage().OKAlertBtn.shouldBe(Condition.visible).click();
//        }
//
//        auto.trunkPage().createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

//        preparationSteps();
//        auto.homePage().logout();
//        auto.loginPage().login("0","Yeastar123");

    }



    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:login PBX" +
            "2:删除spstrunk -> 创建sps trunk" +
            "3:辅助设备分机2000通过sps trunk呼入，进入分机0的voicemial"+
            "4:cli确认播放提示音为vm-greeting-leave-after-tone.slin")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void test2000To0Voicemail() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认");
        preparationSteps();

        step("3:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(40000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        assertStep("4:cli确认播放提示音为vm-greeting-leave-after-tone.slin");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_ASSIST_1, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");

        assertStep("5:cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(), ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"2000<2000>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技<0>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"2000<2000> hung up");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Source_Trunk.getAlias(),0),SPS);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Communication_Type.getAlias(),0),communication_inbound);

        assertStep("6:分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        String me_time = TableUtils.getTableForHeader(getDriver(),"Time",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:login PBX" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:查看pbxlog.0，检查vm-received.gsm、vm-from-phonenumber、vm-duration.slin提示音字段")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void test0CallPin()  {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME, LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        //todo 特征码*2要重置设置
        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Make_Call_No_Answer(0,"*2",DEVICE_IP_LAN,false);
        sleep(2000);
        pjsip.Pj_Send_Dtmf(0,"1234");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(0,"1");
        sleep(3000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("查看pbxlog.0，检查vm-received.gsm、vm-from-phonenumber、vm-duration.slin提示音字段");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认voicemail提示音]");

        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-received.slin"),"[Assert,cli确认提示音]");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-from-phonenumber.slin"),"[Assert,cli确认提示音]");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-duration.slin"),"[Assert,cli确认提示音]");
        softAssert.assertAll();
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为available" +
            "1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "2:cli确认播放提示音为test2.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForAvailable(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Available ");
        auto.extensionPage().selectExtensionPresence("0","Available").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test2.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test2.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为away" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test3.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForAway(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Away ");
        auto.extensionPage().selectExtensionPresence("0","Away").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test3.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test3.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Bussiness trip" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test4.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForBusinessTrip(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Business Trip ");
        auto.extensionPage().selectExtensionPresence("0","Business Trip").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test4.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test4.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为dnd" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test5.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForDnd(){
        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Do Not Disturb");
        auto.extensionPage().selectExtensionPresence("0","Do Not Disturb").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test5.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test5.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Lunch" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test6.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForLunch(){
        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Lunch Break");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().selectExtensionPresence("0","Lunch Break").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test6.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test6.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Off Work" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailGreetingForOffWork(){
        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("设置分机0状态为Off Work");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        auto.extensionPage().selectExtensionPresence("0","Off Work").clickApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

        step("cli确认播放提示音为test1.wav");
        softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("test1.wav"),"[Assert,cli确认提示音]");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testNotification1(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("修改New Voicemail Notification设置为send email notification with attachment，afternotification设置为delete voicemail");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.SEND_EMAIL_NOTIFICATIONS_WITH_ATTACHMENT.getAlias())
                .selectCombobox(IExtensionPageElement.AFTER_NOTIFICATION.DELETE_VOICEMAIL.getAlias()).clickSaveAndApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();

        assertStep("cdr判断");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"2000<2000>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"2000<2000> hung up");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Source_Trunk.getAlias(),0),SPS);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Communication_Type.getAlias(),0),communication_inbound);

        //todo 检查分机页面
        assertStep("分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        String me_time = TableUtils.getTableForHeader(getDriver(),"Time",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Off Work" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testNotification2(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("修改New Voicemail Notification设置为send email notification without attachment，afternotification设置为mark as read");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.SEND_EMAIL_NOTIFICATIONS_WITHOUT_ATTACHMENT.getAlias())
                .selectCombobox(IExtensionPageElement.AFTER_NOTIFICATION.MARK_AS_READ.getAlias()).clickSaveAndApply();

        step("清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();

        assertStep("cdr判断");
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"2000<2000>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"2000<2000> hung up");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Source_Trunk.getAlias(),0),SPS);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Communication_Type.getAlias(),0),communication_inbound);

        assertStep("分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        String me_time = TableUtils.getTableForHeader(getDriver(),"Time",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Off Work" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testNotification3(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
                "修改分机0voicemail页面，启用voicemail，" +
                "启用voicemail pin Authentication，" +
                "pin码设置为1234，" +
                "New Voicemail Notification设置为send email notification with attachment，" +
                "afternotification设置为No action，" +
                "勾选play date  and time/caller id/durations，" +
                "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("修改New Voicemail Notification设置为do not send email notification");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .selectCombobox(IExtensionPageElement.NEW_VOICEMAIL_NOTIFICATION.DO_NOT_SEND_EMAIL_NOTIFICATIONS.getAlias()).clickSaveAndApply();

        step(":清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();

        assertStep("cdr判断");
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"2000<2000>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"2000<2000> hung up");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Source_Trunk.getAlias(),0),SPS);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Communication_Type.getAlias(),0),communication_inbound);

        assertStep("分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().loginWithExtension("0",EXTENSION_PASSWORD,EXTENSION_PASSWORD_NEW);
        auto.me_homePage().intoPage(Me_HomePage.Menu_Level_1.voicemails);
        String me_name = TableUtils.getTableForHeader(getDriver(),"Name",0);
        String me_time = TableUtils.getTableForHeader(getDriver(),"Time",0);
        Assert.assertEquals(me_name,"2000\n" +"External Number");
    }

    @Epic("P_Series")
    @Feature("Extension")
    @Story("Voicemail")
    @Description("Voicemail MODE 功能验证：" +
            "1:设置状态为Off Work" +
            "2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial" +
            "3:cli确认播放提示音为test1.wav")
    @Severity(SeverityLevel.BLOCKER)
    @Test(groups = "P0,TestExtensionVoicemail,testVoicemailModel,Regression,PSeries")
    public void testVoicemailDisable(){

        step("登录pbx");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("voicemail 环境准备" +
        "修改分机0voicemail页面，启用voicemail，" +
        "启用voicemail pin Authentication，" +
        "pin码设置为1234，" +
        "New Voicemail Notification设置为send email notification with attachment，" +
        "afternotification设置为No action，" +
        "勾选play date  and time/caller id/durations，" +
        "voicemail greeting 默认（default为follow system，available等presence状态保持默认none）");
        preparationSteps();

        step("分机0禁用voicemail，保存并应用");
        auto.extensionPage().switchToTab(IExtensionPageElement.TABLE_MENU.VOICEMAIL.getAlias())
                .isCheckBoxForSwitch(IExtensionPageElement.ele_extension_voicemail_enable,false).clickSaveAndApply();

        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();

    }

    /**
     * 创建0 9999999 1000分机
     */
    void createExts(){
        //todo 元素ok后开启
//        auto.extensionPage().deleAllExtension().createSipExtension("0","Yeastar Test0","朗视信息科技","(0591)-Ys.0","0",EXTENSION_PASSWORD).clickSave();
//        auto.extensionPage().createSipExtension("9999999","Yeastar Test9999999","朗视信息科技","(0591)-Ys.9999999","9999999",EXTENSION_PASSWORD).clickSave();
//        auto.extensionPage().createSipExtension("1000","F1000","朗视信息科技","(0591)-Ys.1000","1000",EXTENSION_PASSWORD).clickSave();
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
        auto.preferences().selectCombobox(IPreferencesPageElement.NAME_DISPLAY_FORMAT.FIRST_LAST_WITH_SPACE.getAlias()).clickSaveAndApply();

        //删除所有分机 创建分机0
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_extensions);
        createExts();

        auto.extensionPage().editExtension(getDriver(),"0")
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


        //todo 网页需要修改，暂不支持创建trunk
//        step("删除spstrunk -> 创建sps trunk");
//        sleep(3000);
//        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
//        if(TableUtils.clickTableDeletBtn(getDriver(),"Name",SPS)){
//            auto.trunkPage().OKAlertBtn.shouldBe(Condition.visible).click();
//        }
//        auto.trunkPage().createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

        //清空呼入路由，创建呼入路由
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);
        ArrayList<String> trunklist = new ArrayList<>();
        trunklist.add("SPS1");
        auto.inboundRoute().deleteAllInboundRoutes()
                .createInboundRoute("InRoute1",trunklist)
                .editInbound("InRoute1","Name")
                .selectDefaultDestination(IInboundRoutePageElement.DEFAULT_DESTIONATON.EXTENSION.getAlias(),"0-Yeastar Test0 朗视信息科技")
                .clickSaveAndApply();


    }

    /**
     * 外线2000通过sps呼入
     */
    public void ext2000CallVoicemail(){
        pjsip.Pj_Init();
//        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
//        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_No_Answer(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Destory();

    }
}
