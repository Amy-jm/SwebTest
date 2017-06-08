package com.yeastar.swebtest.testcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Created by GaGa on 2017-05-12.
 */
public class CheckToTest extends SwebDriver {
    /**
     * data.properties里面的数据是否正确
     * 辅助设备的sip线路是否正确
     * 确定可测试后再将测试内容填充进来（xml文件连接进来）
     */
    @Test
    public void LogTest() throws InterruptedException {
        initialDriver("chrome","http://192.168.4.99");
        login("6205","GaGa6205");
        Thread.sleep(5000);
        logout();
        Reporter.infoExec("1"); //执行操作
        YsAssert.assertEquals("Number3Test","Number3Test2","判断两个值");
    }
}
