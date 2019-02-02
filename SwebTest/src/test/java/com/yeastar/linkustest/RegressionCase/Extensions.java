package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class Extensions extends AppDriver {
    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();
    }

    @Test
    public void A1_(){
        Reporter.infoExec("B打给A，A接听，跳转到分机页面判断分机状态为Busy");
        findElement2(me.me_tab).click();
        findElement2(dialpad.dialpad_tab).click();
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<linkusA_num.length(); i++){
            numList.add(String.valueOf(linkusA_num.charAt(i)));
        }
        toCall(driver2,numList);
        findElement1(calling.calling_answer).click();
        findElement1(calling.contacts).click();
        String st = findElement1(contacts.status1).getText();
        String st2 = findElement1(contacts.status2).getText();
        findElement1(contacts.back).click();
        YsAssert.assertInclude(st,presence_busy,"分机A状态应为Busy,实际为"+st);
        YsAssert.assertInclude(st2,presence_busy,"分机B状态应为Busy,实际为"+st2);
    }
    @Test
    public void A2(){
        Reporter.infoExec("判断被叫方B分机状态为Busy");
        findElement2(calling.contacts).click();
        String st = findElement2(contacts.status1).getText();
        String st2 = findElement2(contacts.status2).getText();
        findElement2(contacts.back).click();
        YsAssert.assertInclude(st,presence_busy,"分机A状态应为Busy,实际为"+st);
        YsAssert.assertInclude(st2,presence_busy,"分机B状态应为Busy,实际为"+st2);
    }
    @Test
    public void A3_(){
        Reporter.infoExec("判断分机AB分机状态为空闲");
        findElement2(calling.call_hangup).click();
        if(findElement2(dialpad.dialpad_tab).isDisplayed()){
            findElement2(contacts.constacts_tab).click();
            String st = findElement2(contacts.status1).getText();
            String st2 = findElement2(contacts.status2).getText();
            YsAssert.assertInclude(st,presence_available,"分机A状态应为Busy,实际为"+st);
            YsAssert.assertInclude(st2,presence_available,"分机B状态应为Busy,实际为"+st2);
        }
        if(findElement1(dialpad.dialpad_tab).isDisplayed()){
            findElement1(contacts.constacts_tab).click();
            String st = findElement1(contacts.status1).getText();
            String st2 = findElement1(contacts.status2).getText();
            YsAssert.assertInclude(st,presence_available,"分机A状态应为Busy,实际为"+st);
            YsAssert.assertInclude(st2,presence_available,"分机B状态应为Busy,实际为"+st2);
        }
    }
    @Test
    public void B_(){
        Reporter.infoExec("A登出,B查看A状态为离线");
        logout(driver1);
        findElement2(contacts.constacts_tab).click();
        findElement2(contacts.extensions_page).click();
        String staus = findElement2(contacts.status1).getText();
        findElement1(login.login).click();
        YsAssert.assertInclude(staus,presence_offline,"分机A状态应为Busy,实际为"+staus);
    }

    @Test
    public void C_(){
        Reporter.infoExec("修改分机B的头像、姓名、手机号、邮箱并保存,A查看");
        boolean bNameRep=false;
        findElement2(me.me_tab).click();
        findElement2(me.name).click();
        findElement2(personalInformation.edit).click();
        if(findElement2(personalInformation.name).getText().equals(linkusB_name+"B"))
            bNameRep = true;
        findElement2(personalInformation.name).clear();
        findElement2(personalInformation.mobile).clear();
        findElement2(personalInformation.email).clear();
        findElement1(contacts.constacts_tab).click();
        if(bNameRep)
            findElement2(personalInformation.name).sendKeys(linkusB_name);
        else
            findElement2(personalInformation.name).sendKeys(linkusB_name+"B");
        findElement2(personalInformation.mobile).sendKeys(linkusB_phoneNum);
        findElement2(personalInformation.email).sendKeys(linkusB_emailAddress);
        findElement2(personalInformation.edit).click();

        Reporter.infoExec("分机A开始判断");
        findElement1(contacts.constacts_tab).click();
        findElement1(contacts.extensions_page).click();
        if(bNameRep)
            findElement1(contacts.name2).click();
        else
            driver1.findElement(By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusB_name+"B"+"']")).click();
        String name = findElement1(contacts.detailName).getText();
        String mobile = findElement1(contacts.detailMobile).getText();
        String emial = findElement1(contacts.detailEmial).getText();

        Reporter.infoExec("恢复B的名称");
        findElement2(personalInformation.edit).click();
        findElement2(personalInformation.name).clear();
        findElement2(personalInformation.name).sendKeys(linkusB_name);
        findElement2(personalInformation.edit).click();
        findElement2(personalInformation.back).click();
        if(bNameRep)
            YsAssert.assertEquals(linkusB_name,name,"B的名称显示错误："+linkusB_name);
        else
            YsAssert.assertEquals(linkusB_name+"B",name,"B的名称显示错误："+name);
        YsAssert.assertEquals(linkusB_phoneNum,mobile,"B的手机号码显示错误："+mobile);
        YsAssert.assertEquals(linkusB_emailAddress,emial,"B的Email显示错误："+emial);
    }

//    @Test
    public void D_(){
        Reporter.infoExec("网页增加分机");
    }
//    @Test
    public void E_(){
        Reporter.infoExec("网页删除分机");
    }
//    @Test
    public void F_(){
        Reporter.infoExec("android版本号验证");
        String ret=executedCmd("aapt dump badging "+androidApkPath);
        System.out.println(ret);
        if(ret != null){
            System.out.printf("@@@@@@@@@@@@@@@@@versionCode='"+androidVersionCode+"'");
            if(ret.contains("versionCode='"+androidVersionCode+"'"));
        }
    }
    @AfterClass
    public void AfterClass() {
        ys_waitingTime(5000);
        driver1.unlockDevice();
        driver2.unlockDevice();
        driver1.quit();
        driver2.quit();
    }
}
