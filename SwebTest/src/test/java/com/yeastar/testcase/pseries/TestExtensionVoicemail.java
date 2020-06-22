package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.ICdrPageElement;
import com.yeastar.page.pseries.IExtensionPageElement;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;


/**
 * @program: SwebTest
 * @description: 分机模块用例-voicemail
 * @author: linhaoran@yeastar.com
 * @create: 2020/06/18
 */
public class TestExtensionVoicemail extends TestCaseBase {

    @Test
    public void CaseName() {
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(60000);
        pjsip.Pj_Hangup_All();
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

        step("2:删除spstrunk -> 创建sps trunk");
        auto.homePage().intoPage(HomePage.Menu_Level_1.extension_trunk, HomePage.Menu_Level_2.extension_trunk_tree_trunks);
        sleep(3000);
        TableUtils.clickTableDeletBtn(getDriver(),"Name",SPS);
        auto.trunkPage().OKAlertBtn.shouldBe(Condition.visible).click();
        auto.trunkPage().createSpsTrunk(SPS,DEVICE_ASSIST_2,DEVICE_ASSIST_2).clickSaveAndApply();

        step("3:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(60000);
        pjsip.Pj_Hangup_All();

        assertStep("4:cli确认播放提示音为vm-greeting-leave-after-tone.slin");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("vm-greeting-leave-after-tone.slin"),"[Assert,cli确认提示音]");

        assertStep("5:cdr判断");
        auto.cdrPage().ele_download_cdr_btn.shouldBe(Condition.exist);
        ys_waitingTime(WaitUntils.SHORT_WAIT);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_From.getAlias(),0),"2000<2000>");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Call_To.getAlias(),0),"Voicemail Yeastar Test0 朗视信息科技");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Status.getAlias(),0),"VOICEMAIL");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Reason.getAlias(),0),"2000<2000> hung up");
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Source_Trunk.getAlias(),0),SPS);
        Assert.assertEquals(TableUtils.getTableForHeader(getDriver(),ICdrPageElement.CDR_HEADER.Communication_Type.getAlias(),0),communication_inbound);

        assertStep("6:分机0登录webclient，voicemail页面新增一条来自2000未读的留言记录");
        auto.homePage().logout();
        auto.loginPage().login("0","Yeastar123");
        //todo 分机webclient页面检查留言
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

        //todo 特征码*2是否要重置设置
        step("2:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(0,"*2",DEVICE_IP_LAN,false);
        sleep(2000);
        pjsip.Pj_Send_Dtmf(0,"1234");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(0,"1");
        sleep(3000);
        pjsip.Pj_Hangup_All();

        step("3:查看pbxlog.0，检查vm-received.gsm、vm-from-phonenumber、vm-duration.slin提示音字段");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("vm-received.slin"),"[Assert,cli确认提示音]");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("vm-from-phonenumber.slin"),"[Assert,cli确认提示音]");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("vm-duration.slin"),"[Assert,cli确认提示音]");
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
        //todo 设置状态为available
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test2.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test2.wav"),"[Assert,cli确认提示音]");
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
        //todo 上传提示音
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test3.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test3.wav"),"[Assert,cli确认提示音]");
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
        //todo 上传提示音
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test4.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test4.wav"),"[Assert,cli确认提示音]");
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
        //todo 上传提示音
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test5.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test5.wav"),"[Assert,cli确认提示音]");
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
        //todo 上传提示音
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test6.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test6.wav"),"[Assert,cli确认提示音]");
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
        //todo 上传提示音
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();

        step("2:cli确认播放提示音为test1.wav");
        softAssert.assertTrue(execAsterisk(SHOW_ASTERISK_LOG).contains("test1.wav"),"[Assert,cli确认提示音]");
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
    public void testNotification1(){
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();
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
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();
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
        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();
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

        step("1:清空asterisk log文件，辅助设备分机2000通过sps trunk呼入，进入voicemial");
        clearasteriskLog();
        ext2000CallVoicemail();

    }

    public void ext2000CallVoicemail(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(0,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(0,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);
        pjsip.Pj_Make_Call_Auto_Answer_For_PSeries(2000,"8200",DEVICE_ASSIST_2,false);
        sleep(20000);
        pjsip.Pj_Hangup_All();
    }
}
