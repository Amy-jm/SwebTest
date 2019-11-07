package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.Maintenance.Upgrade.Upgrade;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

import java.util.ArrayList;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.Selenide.sleep;
public class UpdateVersion extends SwebDriver{

    @BeforeClass
    public void BeforeClass() {
        Reporter.infoBeforeClass("开始执行：======前置环境设置—BeforeTest======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

    }

    @Test
    public void update() {
        Reporter.infoExec("执行自动升级镜像"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.upgrade.click();

        comboboxSelect(upgrade.type,"http");
        upgrade.httpInput.setValue(HTTP_SERVER_IMAGE);
        upgrade.httpDownload.click();
        waitReboot();
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        refresh();
        if(!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        sleep(5000);
        Reporter.infoAfterClass("执行完毕：======升级镜像======"); //执行操作
        quitDriver();
        killChromePid();
        sleep(5000);

    }
}
