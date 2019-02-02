package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.linkustest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class Me extends AppDriver {
    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();
        A0_initPjsipExt();
    }
    public void A0_initPjsipExt(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(Integer.parseInt(linkusC_num),linkusC_Password,"UDP",udpPort);
        pjsip.Pj_Register_Account(Integer.parseInt(linkusC_num), localAddress);
        ys_waitingTime(2000);
    }
    @Test
    public void A1_() {
        Reporter.infoExec("Presence状态切换为出差"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.presenceStatus).click();
        findElement1(presenceStatus.businessTrip).click();
        findElement1(presenceStatus.back).click();
        YsAssert.assertEquals(findElement1(me.presenceStatus).getText(),presence_businessTrip,"A切换状态为出差");
        findElement2(contacts.constacts_tab).click();
        String A_Stauts = findElement2(contacts.status1).getText();
        YsAssert.assertInclude(A_Stauts,presence_businessTrip,"B查看A状态为"+A_Stauts);

    }

    @Test
    public void A2_(){
        Reporter.infoExec("Presence状态切换为离开"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.presenceStatus).click();
        findElement1(presenceStatus.away).click();
        findElement1(presenceStatus.back).click();
        YsAssert.assertEquals(findElement1(me.presenceStatus).getText(),presence_away,"A切换状态为离开");
        findElement2(contacts.constacts_tab).click();
        String A_Stauts = findElement2(contacts.status1).getText();
        YsAssert.assertInclude(A_Stauts,presence_away,"B查看A状态为"+A_Stauts);
    }

    @Test
    public void A3_(){
        Reporter.infoExec("Presence状态切换为免打扰"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.presenceStatus).click();
        findElement1(presenceStatus.dnd).click();
        findElement1(presenceStatus.back).click();
        YsAssert.assertEquals(findElement1(me.presenceStatus).getText(),presence_dnd,"A切换状态为免打扰");
        findElement2(contacts.constacts_tab).click();
        String A_Stauts = findElement2(contacts.status1).getText();
        YsAssert.assertInclude(A_Stauts,presence_dnd2,"B查看A状态为"+A_Stauts);
    }

    @Test
    public void A4_(){
        Reporter.infoExec("Presence状态切换为午餐中"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.presenceStatus).click();
        findElement1(presenceStatus.lunchBreak).click();
        findElement1(presenceStatus.back).click();
        YsAssert.assertEquals(findElement1(me.presenceStatus).getText(),presence_lunchBreak,"A切换状态为午餐中");
        findElement2(contacts.constacts_tab).click();
        String A_Stauts = findElement2(contacts.status1).getText();
        YsAssert.assertInclude(A_Stauts,presence_lunchBreak,"B查看A状态为"+A_Stauts);
    }

    @Test
    public void A5_(){
        Reporter.infoExec("Presence状态切换为空闲"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.presenceStatus).click();
        findElement1(presenceStatus.available).click();
        findElement1(presenceStatus.back).click();
        YsAssert.assertEquals(findElement1(me.presenceStatus).getText(),presence_available,"A切换状态为空闲");
        findElement2(contacts.constacts_tab).click();
        String A_Stauts = findElement2(contacts.status1).getText();
        YsAssert.assertInclude(A_Stauts,presence_available,"B查看A状态为"+A_Stauts);
    }


    @Test
    public void G1_AdvancedOptions(){
        Reporter.infoExec("响铃超时时间为15s");
        findElement1(me.me_tab).click();
        findElement1(me.settings).click();
        findElement1(settings.advancedOptions).click();
        findElement1(advancedOptions.ringTimeout).clear();
        findElement1(advancedOptions.ringTimeout).sendKeys("15");
        findElement1(advancedOptions.back).click();
        findElement1(settings.back).click();
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        toCall(driver2,linkusA_num);
        ys_waitingTime(18000);
        if(findElement1(me.me_tab).isDisplayed()){
            findElement2(calling.call_hangup).click();
            findElement1(dialpad.dialpad_tab).click();
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_missCall);
        }else{
            findElement2(calling.call_hangup).click();
            YsAssert.fail("A没有超时挂断");
        }
    }
    @Test
    public void G2_AdvancedOptions(){
        Reporter.infoExec("呼叫等待:A与B通话中，C呼入A");
        findElement1(me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,linkusB_num);
        findElement2(calling.calling_answer).click();
        pjsip.Pj_Make_Call_No_Answer(Integer.parseInt(linkusC_num),linkusA_num,localAddress);
        findElement1(calling.callWait_accpet).click();
        ys_waitingTime(5000);
        YsAssert.assertEquals(findElement1(calling.callWait_name).getText(),linkusB_name,"呼叫等待分机B名称显示错误");
        YsAssert.assertEquals(findElement1(calling.callWait_time).getText(),"Hold","呼叫等待分机B状态显示错误");
        YsAssert.assertEquals(findElement1(calling.caller_extensionName).getText(),linkusC_name,"A界面显示分机C名称错误");
        findElement1(calling.call_hangup).click();
        findElement1(calling.call_hangup).click();
        checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
    }
//    @Test   //暂时没办法判断图片是否是下载的图片
    public  void G3_AdvancedOptions(){
        Reporter.infoExec("清除缓存");
        findElement1(me.me_tab).click();
        findElement1(me.settings).click();
        findElement1(settings.advancedOptions).click();
        findElement1(advancedOptions.clearCache).click();
        findElement1(advancedOptions.clearCache_ok).click();
        ys_waitingTime(5000);
        findElement1(advancedOptions.back).click();
        findElement1(settings.back).click();
        findElement1(me.voicemail).click();
        findElement1(voicemail.all).click();
        if(findElement1(voicemail.nameExtension1) != null){
            Reporter.infoExec("存在至少一条语音留言");
            if(findElement1(voicemail.isRead1) != null){
                YsAssert.fail("缓存没清除");
            }
        }else {
            Reporter.infoExec("不存在语音留言");
        }
    }
    @Test
    public void H1_Quesition(){
        Reporter.infoExec("问题反馈");
        findElement1(me.me_tab).click();
        findElement1(me.settings).click();
        findElement1(settings.reportProblem).click();
        findElement1(reportProblem.appCrash).click();
        findElement1(reportProblem.appCrash_appCrash).click();
        findElement1(reportProblem.appCrash_email).clear();
        findElement1(reportProblem.appCrash_submit).click();
        if(findElement1(reportProblem.title).getText().contains("App not responding")){
            Reporter.infoExec("仍然停留在问题上报页面");
            findElement1(reportProblem.appCrash_email).sendKeys(linkusA_emailAddress);
            findElement1(reportProblem.appCrash_submit).click();
            ys_waitingTime(5000);
            YsAssert.assertEquals(findElement1(reportProblem.title).getText(),"Report a Problem","没有跳转至问题反馈页面，问题提交失败");
        }else if(findElement1(reportProblem.title).getText() == "Report a Problem"){
            YsAssert.fail("问题提交成功");
        }else {
            YsAssert.fail("跳转至其他页面");
        }
    }
    @Test
    public void H2_Quesition(){
        Reporter.infoExec("问题反馈");
        findElement1(me.me_tab).click();
        findElement1(me.settings).click();
        findElement1(settings.reportProblem).click();
        findElement1(reportProblem.others).click();
        findElement1(reportProblem.others_des).sendKeys("Automated Test submission problem");
        findElement1(reportProblem.others_emial).clear();
        findElement1(reportProblem.others_emial).sendKeys(linkusA_emailAddress);
        findElement1(reportProblem.others_submit).click();
        ys_waitingTime(5000);

        YsAssert.assertEquals(findElement1(reportProblem.title).getText(),"Report a Problem","没有跳转至问题反馈页面，问题提交失败");
    }

    @Test
    public void J1_About(){
        Reporter.infoExec("点击帮助、隐私协议 "); //执行操作
        findElement1(me.me_tab).click();
        findElement1(me.settings).click();
        findElement1(settings.about).click();
        findElement1(about.policy).click();
        ys_waitingTime(3000);
        YsAssert.assertInclude(findElement1(about.title).getText(),"Policy Agreement","跳转Help界面");
        findElement1(about.back).click();
        findElement1(about.help).click();
        ys_waitingTime(5000);
        YsAssert.assertEquals(findElement1(about.title).getText(),"Help","跳转Help界面");

        findElement1(about.back);
        findElement1(settings.back);
    }
    @AfterMethod
    public void AbackToMain(){
        pjsip.Pj_Hangup_All();
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
