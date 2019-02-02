package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class Conference extends AppDriver {

    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();

        A0_initPjsipExt();
    }
    public void A0_initPjsipExt(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(Integer.parseInt(linkusC_num),linkusC_Password,"UDP",udpPort);
        pjsip.Pj_CreateAccount(Integer.parseInt(linkusD_num),linkusD_Password,"UDP",udpPort);
        pjsip.Pj_Register_Account(Integer.parseInt(linkusC_num), localAddress);
        pjsip.Pj_Register_Account(Integer.parseInt(linkusD_num), assistAddress);
        ys_waitingTime(2000);
    }
    @AfterMethod
    public void AbackToMain(){
        if(findElement1(calling.conference_exit) != null){
            findElement1(calling.conference_exit).click();
        }
        if(findElement1(calling.transfer_cancel) != null){
            findElement1(calling.transfer_cancel).click();
        }
        if(findElement1(calling.call_hangup) != null){
            findElement1(calling.call_hangup).click();
        }
        if(findElement1(calling.calling_hangup) != null){
            findElement1(calling.calling_hangup).click();
        }
        if(findElement1(settings.back) != null){//1
            findElement1(settings.back).click();
            if(findElement1(settings.back) != null){ //2
                findElement1(settings.back).click();
                if(findElement1(settings.back) != null){//3
                    findElement1(settings.back).click();
                    if(findElement1(settings.back) != null){//4
                        findElement1(settings.back).click();
                    }else{//4
                        findElement1(me.me_tab).click();
                    }
                }else{//3
                    findElement1(me.me_tab).click();
                }
            }else{//2
                findElement1(me.me_tab).click();
            }
        }else{//1
            findElement1(me.me_tab).click();
        }
    }
    @AfterMethod
    public void BbackToMain(){
        if(findElement2(calling.conference_exit) != null){
            findElement2(calling.conference_exit).click();
        }
        if(findElement2(calling.transfer_cancel) != null){
            findElement2(calling.transfer_cancel).click();
        }
        if(findElement2(calling.call_hangup) != null){
            findElement2(calling.call_hangup).click();
        }
        if(findElement2(calling.calling_hangup) != null){
            findElement2(calling.calling_hangup).click();
        }
        if(findElement2(settings.back) != null){//1
            findElement2(settings.back).click();
            if(findElement2(settings.back) != null){ //2
                findElement2(settings.back).click();
                if(findElement2(settings.back) != null){//3
                    findElement2(settings.back).click();
                    if(findElement2(settings.back) != null){//4
                        findElement2(settings.back).click();
                    }else{//4
                        findElement2(me.me_tab).click();
                    }
                }else{//3
                    findElement2(me.me_tab).click();
                }
            }else{//2
                findElement2(me.me_tab).click();
            }
        }else{//1
            findElement2(me.me_tab).click();
        }
    }

    @Test
    public void A1_Conference(){
        Reporter.infoExec("发起会议,A正常退出");
        callConference();
        findElement1(calling.conference_exit).click();
        checkCDR(driver2,linkusA_name+"<Conference>",cdr_extension,cdr_secs);
        checkConferenceRecord(driver1,"C1",linkusA_name,linkusB_name,linkusC_name);

    }
    @Test
    public void  B_RemoveMember(){
        Reporter.infoExec("移除成员B");
        callConference();
        findElement1(calling.conferece_avatar2).click();
        findElement1(calling.conference_delMember).click();
        if(findElement2(dialpad.dialpad_tab).isDisplayed()){
            findElement1(calling.conference_exit).click();
            checkCDR(driver2,linkusA_name+"<Conference>",cdr_extension,cdr_secs);
            findElement2(me.me_tab).click();
            findElement2(me.conference).click();
            checkConferenceRecord(driver2,"C1",linkusA_name,linkusB_name,linkusC_name);
        }
    }

    @Test
    public void C_Mute(){
        Reporter.infoExec("成员B静音图标显示");
        callConference();
        findElement1(calling.conferece_avatar2).click();
        findElement1(calling.conference_muteBtn).click();
        if(findElement1(calling.conference_mute2) == null){
            YsAssert.fail("B静音图标没有显示");
        }else {
            findElement1(calling.conferece_avatar2).click();
            findElement1(calling.conference_unmuteBtn).click();
            if(findElement1(calling.conference_mute2) != null){
                findElement1(calling.conference_exit).click();
            }else{
                YsAssert.fail("B静音图标没有消失");
            }
        }
    }
//    @Test
    public void D_ConferenceInCall(){
        Reporter.infoExec("会议室来电");
        findElement2(me.me_tab).click();
        findElement2(me.conference).click();
        findElement2(conference.add).click();
        findElement2(conference.details_ConferenceName).sendKeys("C2");
        findElement2(conference.details_add2).click();
        findElement2(contacts.extensions_page).click();
        findElement2(contacts.search).click();
        findElement2(contacts.name1).click();
        if(findElement2(contacts.selectNumExtension) != null){
            findElement2(contacts.selectNumExtension).click();
        }
        findElement2(contacts.ok).click();
        findElement2(conference.details_start).click();

        findElement1(calling.calling_answer).click();
        if(findElement2(calling.conference_exit).isDisplayed()){
            findElement1(calling.conferece_avatar3).click();
            findElement1(contacts.search).click();
            findElement1(contacts.name3).click();
            findElement1(contacts.ok).click();
            YsAssert.assertEquals(findElement1(calling.conference_name3).getText(),linkusC_name,"邀请C进会议室");
            ys_waitingTime(30000);

        }else{
            YsAssert.fail("A没有切换到会议室界面");
        }

    }

    public void callConference(){
        findElement1(me.me_tab).click();
        findElement1(me.conference).click();
        findElement1(conference.add).click();
        findElement1(conference.details_ConferenceName).sendKeys("C1");
        findElement1(conference.details_add2).click();
        findElement1(contacts.extensions_page).click();
        findElement1(contacts.search).click();
        findElement1(contacts.name2).click();
        if(findElement1(contacts.selectNumExtension) != null){
            findElement1(contacts.selectNumExtension).click();
        }
        findElement1(contacts.name3).click();
        if(findElement1(contacts.selectNumExtension) != null){
            findElement1(contacts.selectNumExtension).click();
        }
        findElement1(contacts.ok).click();
        findElement1(conference.details_start).click();
        findElement2(calling.calling_answer).click();
        pjsip.Pj_Answer_Call(linkusC_num,180);
        ys_waitingTime(3000);
        pjsip.Pj_Answer_Call(linkusC_num,200);
        ys_waitingTime(8000);
    }
    public void checkConferenceRecord(AndroidDriver driver, String conferenceName, String name1, String name2, String name3){
        Reporter.infoExec("判断会议室通话记录");
        YsAssert.assertEquals(findElement(driver, conference.conferenceName1).getText(),conferenceName,"本地会议室记录名称错误");
        findElement(driver, conference.conferenceName1).click();
        YsAssert.assertEquals(findElement(driver,conference.details_ConferenceName).getText(),conferenceName,"详细记录页面会议室名称错误");
        YsAssert.assertEquals(findElement(driver,conference.details_Name1).getText(),name1,"详细记录第一个分机名称显示错误");
        YsAssert.assertEquals(findElement(driver,conference.details_Name2).getText(),name2,"详细记录第二个分机名称显示错误");
        YsAssert.assertEquals(findElement(driver,conference.details_Name3).getText(),name3,"详细记录第三个分机名称显示错误");
    }


    @AfterClass
    public void AfterClass() {
        Reporter.infoAfterClass("Me页面结束"); //执行操作
        ys_waitingTime(5000);
        pjsip.Pj_Destory();
        driver1.unlockDevice();
        driver2.unlockDevice();
        driver1.quit();
        driver2.quit();
    }
}
