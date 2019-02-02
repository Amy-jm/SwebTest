package com.yeastar.swebtest.testcase.Caroline_Practice;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Caroline on 2017/11/17.
 */
public class ExtensionPractice extends SwebDriver{
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开浏览器并登录设备_ExtensionTest"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(S300)){
            mySettings.close.click();
        }

    }

    //    创建分机1000、1100~1105
    @Test
    public void A1_addExtension() throws InterruptedException {
        pageDeskTop.settings.click();
        settings.extensions_panel.click();

//        deletes(" 删除所有分机",extensions.grid,extensions.delete,extensions.delete_yes,extensions.grid_Mask);
//        Reporter.infoExec(" 添加分机1000");
//        m_extension.addSipExtension(1000, EXTENSION_PASSWORD);
//        Reporter.infoExec(" 添加分机1017");
        Reporter.infoExec(" 主测设备注册分机1000，1100"); //执行操作
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);

        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);

//        pjsip.Pj_Register_Account(1001,DEVICE_IP_LAN);

//        pjsip.Pj_Answer_Call(1000,1108,false);
//        pjsip.Pj_Make_Call_No_Answer(1000,"1100",DEVICE_IP_LAN,false);
        pjsip.Pj_Make_Call_Auto_Answer(1000,"1100", DEVICE_IP_LAN,true);
        Thread.sleep(8000);
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1001 <1001>","1000 <1000>","Answered");
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  ExtensionPractice  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }
}
