package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class CallIn extends AppDriver {
    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();
        initPjsipExt();
    }
    @Test
    public void A_(){
        Reporter.infoExec("分机B呼入Linkus A");
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        if(findElement1(calling.calling_extensionName).getText().equals(linkusB_name)){
            if(!findElement1(calling.calling_callTime).getText().equals(calling_ringing)) {
                Reporter.error("A状态显示错误为" + findElement1(calling.calling_callTime).getText());
            }
        }else{
            Reporter.error("A名称显示错误为"+findElement1(calling.calling_extensionName).getText());
        }
        ys_waitingTime(5000);
        findElement2(calling.call_hangup).click();
        //check cdr miss
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_missCall);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_NoAnswer);
    }
    @Test
    public void B_() {
        Reporter.infoExec("B打给A，A拒接"); //执行操作
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_hangup).click();
        findElement2(calling.call_hangup).click();
        if(!findElement1(dialpad.dialpad_tab).isDisplayed())
            Reporter.infoExec("A通话未界面消失");
        //ready to do
        //B进入A的忙转  用AMI？
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_NoAnswer);
        //ready to do
        //判断missed列表是否有1001  颜色可以通过css判断？
    }
    @Test
    public void C_() {
        Reporter.infoExec("B打给A，A接听，B挂断"); //执行操作
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_answer).click();
        ys_waitingTime(5000);
        if(!findElement1(calling.calling_callTime).getText().contains("00"))
            Reporter.error("计时未正常显示");

        findElement2(calling.call_hangup).click();
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
    }
    @Test
    public void D_() {
        Reporter.infoExec("B打给A，A接听，A挂断"); //执行操作
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_answer).click();
        ys_waitingTime(5000);
        if(!findElement1(calling.calling_callTime).getText().contains("00"))
            Reporter.error("计时未正常显示");

        findElement1(calling.call_hangup).click();
        ys_waitingTime(5000);
        if(!findElement1(dialpad.dialpad_tab).isDisplayed())
            Reporter.error("A挂断后界面没回到桌面");
        if(!findElement2(dialpad.dialpad_tab).isDisplayed())
            Reporter.error("B挂断后界面没回到桌面");
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
    }
    @Test
    public void E_() {
        Reporter.infoExec("锁屏时C通过SPS中继呼入到Linkus A"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(Integer.parseInt(linkusD_num),"991000",assistAddress);
        ys_waitingTime(2000);
        if(findElement1(calling.calling_answer).isDisplayed()){
            if(findElement1(calling.calling_trunk).getText().contains(spsTrunk)){
                findElement1(calling.calling_answer).click();
                ys_waitingTime(5000);
                findElement1(calling.call_hangup).click();
                checkCDR(driver1,linkusD_name,cdr_externalNumber,cdr_secs);
            }else{
                YsAssert.fail("呼入Trunk名称显示不正确");
            }
        }
    }

    @Test
    public void F1_(){
        Reporter.infoExec("A锁屏来电,B挂断"); //执行操作
        driver1.lockDevice();
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        if(findElement1(calling.calling_extensionName).getText().equals(linkusB_name)){
            if(!findElement1(calling.calling_callTime).getText().equals(calling_ringing)) {
                Reporter.error("A状态显示错误为" + findElement1(calling.calling_callTime).getText());
            }
        }else{
            Reporter.error("A名称显示错误为"+findElement1(calling.calling_extensionName).getText());
        }
        ys_waitingTime(5000);
        findElement2(calling.call_hangup).click();
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_missCall);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_NoAnswer);

    }
    @Test
    public void F2_() {
        Reporter.infoExec("A锁屏来电,A拒接"); //执行操作
        driver1.lockDevice();
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_hangup).click();

        driver1.unlockDevice();
        if(!findElement1(dialpad.dialpad_tab).isDisplayed())
            Reporter.infoExec("A通话未界面消失");
        //ready to do
        //B进入A的忙转  用AMI？
        findElement1(dialpad.dialpad_tab).click();
        findElement1(dialpad.missed_page).click();
        //ready to do
        //判断missed列表是否有1001  颜色可以通过css判断？
        //ready to do  判断CDR

    }

    @Test
    public void F3_() {
        Reporter.infoExec("B打给A，A接听，B挂断"); //执行操作
        driver1.lockDevice();
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_answer).click();
        driver1.unlockDevice();
        ys_waitingTime(5000);
        if(!findElement1(calling.calling_callTime).getText().contains("00"))
            Reporter.error("计时未正常显示");

        findElement2(calling.call_hangup).click();
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
    }
    @Test
    public void F4_() {
        Reporter.infoExec("B打给A，A接听，A挂断"); //执行操作
        driver1.lockDevice();
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        ys_waitingTime(3000);
        findElement1(calling.calling_answer).click();
        driver1.unlockDevice();
        ys_waitingTime(5000);
        if(!findElement1(calling.calling_callTime).getText().contains("00"))
            Reporter.error("计时未正常显示");

        findElement1(calling.call_hangup).click();
        ys_waitingTime(5000);
        if(!findElement1(dialpad.dialpad_tab).isDisplayed())
            Reporter.error("A挂断后界面没回到桌面");
        if(!findElement2(dialpad.dialpad_tab).isDisplayed())
            Reporter.error("B挂断后界面没回到桌面");
        checkCDR(driver1,linkusB_name,cdr_extension,cdr_secs);
        checkCDR(driver2,linkusA_name,cdr_extension,cdr_secs);
    }
    @Test
    public void F5_() {
        Reporter.infoExec("锁屏时C通过SPS中继呼入到Linkus A"); //执行操作
        driver1.lockDevice();

        pjsip.Pj_Make_Call_No_Answer(Integer.parseInt(linkusD_num),"991000",assistAddress);
        ys_waitingTime(2000);
        if(findElement1(calling.calling_answer).isDisplayed()){
            if(findElement1(calling.calling_trunk).getText().contains(spsTrunk)){
                findElement1(calling.calling_answer).click();
                ys_waitingTime(5000);
                findElement1(calling.call_hangup).click();
                checkCDR(driver1,linkusD_name,cdr_externalNumber,cdr_secs);
            }else{
                YsAssert.fail("呼入Trunk名称显示不正确");
            }
        }
    }

    public void initPjsipExt(){
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(Integer.parseInt(linkusD_num),linkusD_Password,"UDP",udpPort);
        pjsip.Pj_Register_Account(Integer.parseInt(linkusD_num), assistAddress);
        ys_waitingTime(2000);
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
        driver1.unlockDevice();
        driver2.unlockDevice();
        driver1.quit();
        driver2.quit();
    }
}
