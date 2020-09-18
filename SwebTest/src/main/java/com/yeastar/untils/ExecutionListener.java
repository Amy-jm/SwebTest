package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.influxdb.dto.Point;
import org.testng.*;

import java.util.concurrent.TimeUnit;

/**
 * @program: SwebTest
 * @description: listener for dashboard
 * @author: huangjx@yeastar.com
 * @create: 2020/06/23
 */
@Log4j2
public class ExecutionListener  extends TestListenerAdapter implements IInvokedMethodListener {

    public void onTestStart(ITestResult iTestResult) {
//        log.debug( "[ExecutionListener onTestStart] "+iTestResult.getTestClass()+"#"+iTestResult.getName());
    }

    public void onTestSuccess(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
        log.debug( "[ExecutionListener Success] "+iTestResult.getTestClass()+"#"+iTestResult.getName());
        this.sendTestMethodStatus(iTestResult, "PASS");
    }

    public void onTestFailure(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
        log.debug("[ExecutionListener Failure] "+iTestResult.getTestClass()+iTestResult.getName());
        this.sendTestMethodStatus(iTestResult, "FAIL");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        super.onTestSuccess(iTestResult);
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
        try {
            Point point = Point.measurement("testmethod")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag("testclass", iTestResult.getTestClass().getName())
                    .tag("name", iTestResult.getName())
//                .tag("description", iTestResult.getMethod().getDescription())
                    .tag("result", status)
                    .addField("duration", (iTestResult.getEndMillis() - iTestResult.getStartMillis()))
                    .build();

            ResultSenderUtils.send(point);
        } catch (org.influxdb.InfluxDBIOException ex) {
            log.error("[InfluxDB Server connection exception]" + ex);
        } catch (Exception ex) {
            log.error("[ExecutionListener exception]" + ex);
        }
    }
    private void sendTestClassStatus(ITestContext iTestContext) {
        try {
        Point point = Point.measurement("testclass")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("name", iTestContext.getAllTestMethods()[0].getTestClass().getName())
                .addField("duration", (iTestContext.getEndDate().getTime() - iTestContext.getStartDate().getTime()))
                .build();

            ResultSenderUtils.send(point);
        }catch(org.influxdb.InfluxDBIOException ex){
            log.error("[InfluxDB Server connection exception]" + ex);
        }catch(Exception ex){
            log.error("[ExecutionListener exception]" + ex);
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }
}
