package com.yeastar.swebtest.testcase;

import com.codeborne.selenide.Condition;

//import com.google.common.base.Verify;
import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import com.thoughtworks.selenium.webdriven.commands.WaitForCondition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.types.Assertions;
import org.testng.Assert;
import org.testng.annotations.*;

import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2018/2/9.
 */
public class test {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("执行的操作"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
//        m_extension.showCDRClounm();
    }
    @Test
    public void CaseName() {
        Reporter.infoExec("执行的操作"); //执行操作
        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
//        pjsip.Pj_CreateAccount(1000, "Yeastar202", "UDP", UDP_PORT, 1);
//        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);
//
//        Reporter.infoExec(" 辅助设备2注册分机1000"); //执行操作
//        pjsip.Pj_CreateAccount(1100, "Yeastar202", "UDP", UDP_PORT, 2);
//        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

//        pjsip.Pj_Make_Call_Auto_Answer(1000,"1100",DEVICE_IP_LAN);
//        ys_waitingTime(8000);
//        m_extension.checkCDR("1000","1100","");
        System.out.println("111111111111");

        Verify.verify(true,"cuowixinximoban",1);
        System.out.println("sdaaaffffffffffff");
         Verify.verify(false,"cuowixinximoban",1);
//        System.out.println("22222222222222222");
//        Verify.verify(true,"asdf");
        System.out.println("333333333333");
//        Verify.verifyNotNull("AA");
        Verify.verifyNotNull("");
        Assert.assertEquals(1,1);
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("执行的操作"); //执行操作
        Thread.sleep(15000);
        Reporter.infoAfterClass("执行完毕：======  InboundRoute  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);
    }

}
