package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.linkustest.tools.ysassert.YsAssert;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import java.net.MalformedURLException;

public class CallOut extends AppDriver {
    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();
        A1_initPjsipExt();
    }
//    @Test
    public void a(){
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
    }

//    @Test
    public void A1_initPjsipExt(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(Integer.parseInt(linkusC_num),linkusC_Password,"UDP",udpPort);
        pjsip.Pj_Register_Account(Integer.parseInt(linkusC_num), localAddress);
        ys_waitingTime(2000);
    }
    @Test
    public void B1_A_Hangup(){
        Reporter.infoExec("A呼叫B，A挂断"); //执行操作
        findElement(driver1,me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,linkusB_num);
        //判断呼出页面
        if(findElement1(calling.caller_extensionName).getText().equals(linkusA_name)){
            Reporter.infoExec("呼出页面分机名称显示正确");
        }else{
            Reporter.error("呼出页面分机名称显示错误为"+findElement1(calling.caller_extensionName).getText());
        }
        if(findElement1(calling.caller_callTime).getText().equals(calling_ringing)){
            Reporter.infoExec("呼出页面分机状态显示正确");
        }else{
            Reporter.error("呼出页面分机状态显示错误为"+findElement1(calling.caller_callTime).getText());
        }
        //判断呼入页面
        if(findElement2(calling.calling_extensionName).getText().equals(linkusA_name)){
            if(!findElement2(calling.caller_callTime).getText().equals(calling_ringing)) {
                YsAssert.fail("接听方分机状态显示错误为" + findElement2(calling.caller_callTime).getText());
            }
        }else{
            YsAssert.fail("接听方分机名称显示错误为"+findElement2(calling.calling_extensionName).getText());
        }
        findElement1(calling.call_hangup).click();
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_NoAnswer);
    }
    @Test
    public void C_Talking() {
        Reporter.infoExec("A呼叫B，B接听"); //执行操作
        findElement(driver1,me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,linkusB_num);

        if(findElement2(calling.calling_extensionName).getText().equals(linkusA_name)){
            if(findElement2(calling.caller_callTime).getText().equals(calling_ringing)){
                findElement2(calling.calling_answer).click();
                ys_waitingTime(10000);
                if(findElement2(calling.calling_extensionName).getText().equals(linkusA_name)){
                    findElement2(calling.call_hangup).click();
                    ys_waitingTime(2000);
                    checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
                }
            }else{
                YsAssert.fail("接听方分机状态显示错误为"+findElement2(calling.caller_callTime).getText());
            }
        }else{
            YsAssert.fail("接听方分机名称显示错误为"+findElement2(calling.calling_extensionName).getText());
        }
    }
    @Test
    public void D_Record() {
        Reporter.infoExec("A打B，A点录音"); //执行操作
        findElement(driver1,me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,linkusB_num);

        if(findElement2(calling.calling_extensionName).getText().equals(linkusA_name)){
            if(findElement2(calling.caller_callTime).getText().equals(calling_ringing)){
                findElement2(calling.calling_answer).click();
                ys_waitingTime(10000);
                if(findElement2(calling.calling_extensionName).getText().equals(linkusA_name)){
                    findElement1(calling.record).click();
                    ys_waitingTime(8000);
                    findElement1(calling.record).click();
                    findElement2(calling.call_hangup).click();
                    ys_waitingTime(2000);
                    findElement1(me.me_tab).click();
                    findElement1(me.recording).click();
                    if(findElement1(recording.extension1).getText().equals(linkusB_num)){
                        findElement1(recording.back).click();
                    }else{
                        YsAssert.fail("录音界面分机号码显示错误为"+findElement1(recording.extension1).getText());
                    }
                }
            }else{
                YsAssert.fail("接听方分机状态显示错误为"+findElement2(calling.caller_callTime).getText());
            }
        }else{
            YsAssert.fail("接听方分机名称显示错误为"+findElement2(calling.calling_extensionName).getText());
        }
//        if(Boolean.parseBoolean(findElement1(calling.record).getAttribute(attribute_enable))){
//        }else{
//            YsAssert.fail("呼叫方录音按键不能点击");
//        }
    }
    @Test
    public void E1_Contacts(){
        Reporter.infoExec(linkusA_num+"点击联系人");
        findElement1(contacts.constacts_tab).click();
        if(findElement1(contacts.name1).isDisplayed()){

        }else{
            Reporter.error("第一个联系人不存在");
        }
    }
    @Test
    public void E2_Contacts(){
        Reporter.infoExec(linkusA_num+"点击联系人查看详细信息");
        findElement1(contacts.constacts_tab).click();
        findElement1(contacts.extensions_page).click();
        findElement1(contacts.name1).click();
        String name = findElement1(contacts.detailName).getText();
        String num = findElement1(contacts.detailExtension).getText();
        YsAssert.assertEquals(name,linkusA_name,"详细页面A分机名称错误为"+name);
        YsAssert.assertEquals(num,linkusA_num,"详细页面A分机号码错误为"+num);
    }
    @Test
    public void F_IVR() {
        Reporter.infoExec("IVR设置按键事件"); //执行操作
        findElement1(me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,"6","5","0","0");
        ys_waitingTime(3000);
        findElement1(calling.disaplad).click();
        findElement1(calling.num1).click();
        ys_waitingTime(5000);
        if(findElement1(dialpad.dialpad_tab).isDisplayed()){
            findElement1(dialpad.dialpad_tab).click();
            Reporter.infoExec("已通过IVR挂断电话");
            checkCDR(driver1,"6500",cdr_externalNumber,cdr_secs);
        }else{
            YsAssert.fail("A没有通过IVR挂断电话");
        }
    }
    @Test
    public void G01_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C拒接");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,486);
            ys_waitingTime(5000);
            findElement1(calling.transfer_cancel).click();
            findElement1(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else{
            YsAssert.fail("A转移界面出错");
        }
    }
    @Test
    public void G02_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C不接，超时前A取消转移");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(3000);
            findElement1(calling.transfer_cancel).click();
            findElement1(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else{
            YsAssert.fail("A转移界面出错");
        }
    }
    @Test
    public void G03_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C不接，C响铃超时");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(50000);
            findElement1(calling.transfer_cancel).click();
            findElement1(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else{
            YsAssert.fail("A转移界面出错");
        }
    }
    @Test
    public void G04_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C不接，A点击转移按钮");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(2000);
            findElement1(calling.transfer_confirm).click();
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A通话界面没有消失");
            }
            if(findElement2(calling.transfer).isDisplayed()){
                pjsip.Pj_Answer_Call(linkusC_num,200);
                ys_waitingTime(5000);
                findElement2(calling.call_hangup).click();
                checkCDR(driver2,linkusC_name,cdr_extension,cdr_secs,1);
                checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs,2);
                checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer,1);
                checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
            }else{
                YsAssert.fail("B已经退出通话界面");
            }
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else{
            YsAssert.fail("A转移界面出错");
        }
    }
    @Test
    public void G05_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C接听");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,200);
            String n1,s1,n2,n3,s3;
            n1 = findElement1(calling.transfer_name1).getText();
            s1 = findElement1(calling.transfer_status1).getText();
            n2 = findElement1(calling.transfer_name2).getText();
            n3 = findElement1(calling.transfer_name3).getText();
            s3 = findElement1(calling.transfer_status3).getText();
            findElement1(calling.transfer_cancel).click();
            findElement1(calling.call_hangup).click();
            Reporter.infoExec("A页面分机号码从做到右："+n1+" "+n2+" "+n3);
            Reporter.infoExec("A页面B分机状态："+s1+"  C分机状态："+s3);
            YsAssert.assertEquals(n1,linkusB_name,"B名称显示错误为"+n1);
            YsAssert.assertEquals(s1,calling_hold,"B状态显示错误为"+s1);
            YsAssert.assertEquals(n2,linkusA_num,"A名称显示错误为"+n2);
            YsAssert.assertEquals(n3,linkusC_num,"C名称显示错误为"+n3);
            YsAssert.assertInclude(s3,":","C状态显示错误为"+s3);
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else{
            YsAssert.fail("A转移失败");
        }
    }

    @Test
    public void G06_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C接听,A取消转移");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,200);
            ys_waitingTime(5000);
            findElement1(calling.transfer_cancel).click();
            ys_waitingTime(1000);
            int c_status = getExtensionStatus(linkusC_num, HUNGUP, 15);
            if(findElement1(calling.call_hangup).isDisplayed()){
                findElement1(calling.call_hangup).click();
                checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs,1);
                checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
                YsAssert.assertEquals(c_status,HUNGUP,"C在A取消转移后没有挂断");
            }else{
                YsAssert.fail("A取消转移失败");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void G07_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C接听,C挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,200);
            ys_waitingTime(5000);
            pjsip.Pj_Hangup_All();
            ys_waitingTime(3000);
            if(findElement1(calling.call_hangup).isDisplayed()){
                findElement1(calling.call_hangup).click();
                checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs,1);
                checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
            }else{
                YsAssert.fail("A取消转移失败");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void G08_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C接听,A确定转移");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,200);
            ys_waitingTime(5000);
            findElement1(calling.transfer_confirm).click();

            if(findElement1(dialpad.dialpad_tab).isDisplayed()){
                findElement2(calling.call_hangup).click();
                checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
                checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs,1);
                checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);

            }else{
                YsAssert.fail("A确定转移后没有自动挂断");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void G09_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C未接,B挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(5000);
            findElement2(calling.call_hangup).click();
            if(findElement1(calling.call_hangup).isDisplayed()){
                if(findElement1(calling.caller_extensionName).getText().equals(linkusC_num)){
                    findElement1(calling.call_hangup).click();
                    checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer,1);
                    checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
                }else
                    YsAssert.fail("B挂断后A通话界面没有显示C分机名称");
            }else{
                YsAssert.fail("A没有变成1对1通话界面");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void G10_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C已接,B挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,200);
            ys_waitingTime(5000);
            findElement2(calling.call_hangup).click();
            if(findElement1(calling.call_hangup).isDisplayed()){
                if(findElement1(calling.caller_extensionName).getText().equals(linkusC_num)){
                    findElement1(calling.call_hangup).click();
                    checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs,1);
                    checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
                }else
                    YsAssert.fail("B挂断后A通话界面没有显示C分机名称");
            }else{
                YsAssert.fail("A没有变成1对1通话界面");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void G11_CallForwarding(){
        Reporter.infoExec("AB通话，A转移给C，C拒接,B挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            pjsip.Pj_Answer_Call(linkusC_num,486);
            ys_waitingTime(5000);
            findElement2(calling.call_hangup).click();
            if(findElement1(calling.call_hangup).isDisplayed()){
                if(findElement1(calling.caller_extensionName).getText().equals(linkusC_num)){
                    findElement1(calling.call_hangup).click();
                    checkCDR(driver1,linkusC_name,cdr_extension,cdr_NoAnswer,1);
                    checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
                }else
                    YsAssert.fail("B挂断后A通话界面没有显示C分机名称");
            }else{
                YsAssert.fail("A没有变成1对1通话界面");
            }
        }else{
            YsAssert.fail("A转移失败");
        }
    }
    @Test
    public void H1_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,C拒接");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_blind)){
            findElement1(calling.transfer_attendedBlind).click();
        }

        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            pjsip.Pj_Answer_Call(linkusC_num,486);
            ys_waitingTime(3000);
            findElement2(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
            checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
        }else{
            YsAssert.fail("A转移界面出错");
        }
    }
    @Test
    public void H2_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,C不接 C响铃超时");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if(findElement1(calling.transfer_attendedBlind).getText().equals(transer_attended)){
            findElement1(calling.transfer_attendedBlind).click();
        }
        if(findElement1(calling.transfer_history).isDisplayed()){
            toCallingCall(driver1,linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num,180);
            ys_waitingTime(1000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            ys_waitingTime(20000);
            findElement2(calling.speaker).click();
            ys_waitingTime(30000);
            findElement2(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }
    }
    @Test
    public void H3_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,C接听");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if (findElement1(calling.transfer_attendedBlind).getText().equals(transer_attended)) {
            findElement1(calling.transfer_attendedBlind).click();
        }
        if (findElement1(calling.transfer_history).isDisplayed()) {
            toCallingCall(driver1, linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num, 180);
            ys_waitingTime(1000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            pjsip.Pj_Answer_Call(linkusC_num, 200);
            ys_waitingTime(8000);

            findElement2(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else {
            YsAssert.fail("A没有跳转到转移界面");
        }
    }
    @Test
    public void H4_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,C振铃中B主动挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if (findElement1(calling.transfer_attendedBlind).getText().equals(transer_attended)) {
            findElement1(calling.transfer_attendedBlind).click();
        }
        if (findElement1(calling.transfer_history).isDisplayed()) {
            toCallingCall(driver1, linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num, 180);
            ys_waitingTime(5000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            findElement2(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else {
            YsAssert.fail("A没有跳转到转移界面");
        }
    }
    @Test
    public void H5_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,C挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if (findElement1(calling.transfer_attendedBlind).getText().equals(transer_attended)) {
            findElement1(calling.transfer_attendedBlind).click();
        }
        if (findElement1(calling.transfer_history).isDisplayed()) {
            toCallingCall(driver1, linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num, 180);
            ys_waitingTime(5000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            pjsip.Pj_Hangup_All();
            ys_waitingTime(2000);
            findElement2(calling.call_hangup).click();
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
        }else {
            YsAssert.fail("A没有跳转到转移界面");
        }
    }
    @Test
    public void H6_CallForwarding() {
        Reporter.infoExec("AB通话，A盲转移给C,B挂断");
        ACallB();
        findElement2(calling.calling_answer).click();
        findElement1(calling.transfer).click();
        if (findElement1(calling.transfer_attendedBlind).getText().equals(transer_attended)) {
            findElement1(calling.transfer_attendedBlind).click();
        }
        if (findElement1(calling.transfer_history).isDisplayed()) {
            toCallingCall(driver1, linkusC_num);
            ys_waitingTime(3000);
            pjsip.Pj_Answer_Call(linkusC_num, 180);
            ys_waitingTime(5000);
            if(!findElement1(dialpad.dialpad_tab).isDisplayed()){
                YsAssert.fail("A没有正常挂断");
            }
            findElement2(calling.call_hangup).click();
            ys_waitingTime(2000);
            checkCDR(driver1,linkusC_name,cdr_extension,cdr_secs);
            checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs,2);
            checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
        }else {
            YsAssert.fail("A没有跳转到转移界面");
        }
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
        Reporter.infoAfterClass("呼出测试结束"); //执行操作
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        pjsip.Pj_Unregister_Accounts();
        driver1.quit();
        driver2.quit();
        pjsip.Pj_Destory();
    }

    public void ACallB(){
        findElement(driver1,me.me_tab).click();
        findElement1(dialpad.dialpad_tab).click();
        toCall(driver1,linkusB_num);
    }
}
