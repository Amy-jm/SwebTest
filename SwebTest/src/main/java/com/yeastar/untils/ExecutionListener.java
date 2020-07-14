package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.influxdb.dto.Point;
import org.openqa.selenium.Cookie;
import org.testng.*;

import java.util.concurrent.TimeUnit;

import static com.yeastar.controllers.WebDriverFactory.getDriver;

/**
 * @program: SwebTest
 * @description: listener for dashboard
 * @author: huangjx@yeastar.com
 * @create: 2020/06/23
 */
@Log4j2
public class ExecutionListener  extends TestListenerAdapter implements IInvokedMethodListener {
    @Override
    public void onTestStart(ITestResult iTestResult) {
        log.debug( "[ExecutionListener onTestStart] "+iTestResult.getTestClass()+"#"+iTestResult.getName());

    }
   @Override
    public void onTestSuccess(ITestResult iTestResult) {
       log.debug( "[ExecutionListener Success] "+iTestResult.getTestClass()+"#"+iTestResult.getName());
       try {
           Cookie cookie = new Cookie("zaleniumTestPassed", "true");
           getDriver().manage().addCookie(cookie);
       }catch(java.lang.NullPointerException ex){
           log.error("[ExecutionListener onTestSuccess add cookie to zalenium --> NullPointerException] {}",ex.getMessage());
       }
       this.sendTestMethodStatus(iTestResult, "PASS");
    }
    @Override
    public void onTestFailure(ITestResult iTestResult) {
        log.debug("[ExecutionListener Failure] "+iTestResult.getTestClass()+iTestResult.getName());
        //更新用例状态 zalenium
        try {
            Cookie cookie = new Cookie("zaleniumTestPassed", "false");
            getDriver().manage().addCookie(cookie);
        }catch(java.lang.NullPointerException ex){
            log.error("[ExecutionListener Failure add cookie to zalenium --> NullPointerException] {}",ex.getMessage());
        }
        this.sendTestMethodStatus(iTestResult, "FAIL");
    }
    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        log.debug("[ExecutionListener SKIPPED] "+iTestResult.getTestClass()+iTestResult.getName());
        //更新用例状态 zalenium
        try {
            Cookie cookie = new Cookie("zaleniumTestPassed", "false");
            getDriver().manage().addCookie(cookie);
        }catch(java.lang.NullPointerException ex){
            log.error("[ExecutionListener Skipped add cookie to zalenium --> NullPointerException] {}",ex.getMessage());
        }
        this.sendTestMethodStatus(iTestResult, "SKIPPED");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    public void onStart(ITestContext iTestContext) {

    }

    public void onFinish(ITestContext iTestContext) {
        this.sendTestClassStatus(iTestContext);
    }

    private void sendTestMethodStatus(ITestResult iTestResult, String status) {
        Point point = Point.measurement("testmethod")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("testclass", iTestResult.getTestClass().getName())
                .tag("name", iTestResult.getName())
//                .tag("description", iTestResult.getMethod().getDescription())
                .tag("result", status)
                .addField("duration", (iTestResult.getEndMillis() - iTestResult.getStartMillis()))
                .build();
        try {
            ResultSenderUtils.send(point);
        }catch(org.influxdb.InfluxDBIOException EX){
            log.error("[InfluxDB Server connection exception]");
        }
    }

    private void sendTestClassStatus(ITestContext iTestContext) {
        Point point = Point.measurement("testclass")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("name", iTestContext.getAllTestMethods()[0].getTestClass().getName())
                .addField("duration", (iTestContext.getEndDate().getTime() - iTestContext.getStartDate().getTime()))
                .build();
        try {
            ResultSenderUtils.send(point);
        }catch(org.influxdb.InfluxDBIOException EX){
            log.error("[InfluxDB Server connection exception]");
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
