package com.yeastar.swebtest.testcase.extendcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.Test;

/**
 * Created by GaGa on 2017-06-09.
 */
public class GridTest extends SwebDriver {
    @Test
    public void LogTest() throws InterruptedException {
        initialDriver(CHROME,"http://192.168.4.99","http://192.168.3.13:5555/wd/hub");
        //initialDriver(CHROME,"http://192.168.4.99");
        login("6205","GaGa6205");
        Thread.sleep(5000);
        logout();
        //需要判断是否driver为remote，然后手动quit，没法通过selenide进行quit
        webDriver.quit();
        Reporter.infoExec("1"); //执行操作
        YsAssert.assertEquals("Number3Test","Number3Test2","判断两个值");
    }
}
