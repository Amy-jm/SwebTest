package com.yeastar.linkustest.RegressionCase;

import com.yeastar.linkustest.driver.AppDriver;
import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class Login extends AppDriver {

    @BeforeClass
    public void BeforeClass() throws MalformedURLException {
        initialDriver1();
        initialDriver2();
    }

//    @Test
    public void A_loginUser(){
        findElement1(login.userName).sendKeys("1000");
        findElement1(login.password).sendKeys("Yeastar202");
        findElement1(login.serverIp).sendKeys("369163454534");
        findElement1(login.login).click();
        ys_waitingTime(50000);
    }

    @Test
    public void B1_CustomloginWithWifi(){
        Reporter.infoExec("自定义登入");
        findElement1(login.userName).sendKeys(linkusA_num);
        findElement1(login.password).sendKeys(loginPassword);
        findElement1(login.configure_server).click();
        if(Boolean.parseBoolean(findElement1(login.custom_enable).getAttribute(attribute_checked)) == false){
            findElement1(login.custom_enable).click();
        }
        findElement1(login.custom_externalAddress).clear();
        findElement1(login.custom_externalPort).clear();
        findElement1(login.custom_localAddress).sendKeys(localAddress);
        findElement1(login.custom_localPort).sendKeys(localPort);
        findElement1(login.custom_confirm).click();
        if(Boolean.parseBoolean(findElement1(login.serverIp).getAttribute(attribute_editable)) == true){
            YsAssert.fail("域名输入框仍然可编辑");
        }
        findElement1(login.login).click();
        ys_waitingFor(findElement1(me.me_tab));
    }
    @Test
    public void B2_CustomloginWithWifi(){
        Reporter.infoExec("自定义登入");
        findElement2(login.userName).sendKeys(linkusB_num);
        findElement2(login.password).sendKeys(loginPassword);
        findElement2(login.configure_server).click();
        if(Boolean.parseBoolean(findElement2(login.custom_enable).getAttribute(attribute_checked)) == false){
            findElement2(login.custom_enable).click();
        }
        findElement2(login.custom_externalAddress).clear();
        findElement2(login.custom_externalPort).clear();
        findElement2(login.custom_localAddress).sendKeys(localAddress);
        findElement2(login.custom_localPort).sendKeys(localPort);
        findElement2(login.custom_confirm).click();
        if(Boolean.parseBoolean(findElement2(login.serverIp).getAttribute(attribute_editable)) == true){
            YsAssert.fail("域名输入框仍然可编辑");
        }
        findElement2(login.login).click();
        ys_waitingTime(5000);
        ys_waitingFor(findElement2(me.me_tab));
    }

//    @Test
    public void B3_CustomloginWith4G(){
        logout(driver1);
        Reporter.infoExec("自定义4G登入");
        findElement1(login.userName).sendKeys(linkusB_num);
        findElement1(login.password).sendKeys(loginPassword);
        findElement1(login.configure_server).click();
        if(Boolean.parseBoolean(findElement1(login.custom_enable).getAttribute(attribute_checked)) == false){
            findElement1(login.custom_enable).click();
        }
        findElement1(login.custom_localAddress).clear();
        findElement1(login.custom_localPort).clear();
        findElement1(login.custom_externalAddress).sendKeys(externalAddress);
        findElement1(login.custom_externalPort).sendKeys(externalAddress);
        findElement1(login.custom_confirm).click();
        if(Boolean.parseBoolean(findElement1(login.serverIp).getAttribute(attribute_editable)) == true){
            YsAssert.fail("域名输入框仍然可编辑");
        }
        findElement1(login.login).click();
        ys_waitingFor(findElement1(me.me_tab));
        logout(driver1);
    }


    @AfterClass
    public void AfterClass(){
        ys_waitingTime(2000);
        driver1.quit();
        driver2.quit();
    }
}
