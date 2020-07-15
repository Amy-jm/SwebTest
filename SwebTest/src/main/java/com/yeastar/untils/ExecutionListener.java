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
public class ExecutionListener implements ITestListener {

    public void onTestStart(ITestResult iTestResult) {
        log.debug( "[ExecutionListener onTestStart] "+iTestResult.getTestClass()+"#"+iTestResult.getName());
    }

    public void onTestSuccess(ITestResult iTestResult) {
       log.debug( "[ExecutionListener Success] "+iTestResult.getTestClass()+"#"+iTestResult.getName());
       this.sendTestMethodStatus(iTestResult, "PASS");
    }

    public void onTestFailure(ITestResult iTestResult) {
        log.debug("[ExecutionListener Failure] "+iTestResult.getTestClass()+iTestResult.getName());
        this.sendTestMethodStatus(iTestResult, "FAIL");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        log.debug("[ExecutionListener SKIPPED] "+iTestResult.getTestClass()+iTestResult.getName());
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
}
