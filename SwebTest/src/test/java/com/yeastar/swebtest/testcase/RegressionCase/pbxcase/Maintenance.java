package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.testng.annotations.*;

import java.lang.reflect.Method;

import static com.codeborne.selenide.Selenide.*;

/**
 * 回归测试--响铃组功能
 * Created by AutoTest on 2017/10/16.
 */
@Log4j2
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class Maintenance extends SwebDriver {
    private String RESTORE_POINT_IMG_XPAHT = "//table/tbody/tr/td[2]/div[contains(text(),'%s')]/../..//following-sibling::td[5]//img[contains(@src,'restore')]";
    final private String RESTORE_POINT_NAME_KEYWORD = "AutoTest";

    @BeforeMethod
    public void BeforeMethod(Method method) throws InterruptedException {
        Reporter.infoBeforeClass("开始执行：======  Maintenance  ======"); //执行操作
        log.info("[BeforeMethod] "+method.getName());
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/",method);
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }
    @Description("通过备份文件，还原测试环境")
    @Test(priority =0 )
    public void RestoreTestEnv(){
        step("1.选择还原点");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        cloiceRestoreByName(RESTORE_POINT_NAME_KEYWORD);

        step("2.重启");
        pageDeskTop.restore_alert_yes.shouldBe(Condition.visible).click();
        ys_waitingMask();
        pageDeskTop.reboot_Yes.should(Condition.visible).click();
        waitReboot();
        refresh();
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
    }
    @Step("选择还原的镜像：{0}")
    public void cloiceRestoreByName(String name){
        SelenideElement restoreElement = $(By.xpath(String.format(RESTORE_POINT_IMG_XPAHT,name)));
        restoreElement.click();
        sleep(2000);
    }


    @AfterMethod
    public void AfterClass() throws InterruptedException {
        quitDriver();
    }
    @Step("{0}")
    public void step(String desc){
        log.debug("[step] "+desc);
//        sleep(5);
//        Cookie cookie = new Cookie("zaleniumMessage", desc);
//        webDriver.manage().addCookie(cookie);
    }

}
