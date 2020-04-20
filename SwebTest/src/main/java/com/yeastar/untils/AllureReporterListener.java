package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import io.qameta.allure.*;

import static com.yeastar.swebtest.driver.SwebDriver.webDriver;


@Log4j2
public class AllureReporterListener implements IHookable{
    @Override
    public  void run(IHookCallBack callBack, ITestResult testResult) {
        callBack.runTestMethod(testResult);
        if (testResult.getThrowable() != null) {
        try {
            final Throwable testResultThrowable = testResult.getThrowable();
            String message = testResultThrowable.getMessage() != null ? testResultThrowable.getMessage() :
                    testResultThrowable.getCause().getMessage();
            takeScreenShot(message+testResultThrowable.getMessage());
        } catch (Exception e) {
            log.error("Couldn't take screenshot. Error: " + e.getStackTrace());
         }
        }
    }

    @Attachment(value = "Failure in method {0}", type = "image/png")
    private byte[] takeScreenShot(String methodName) {

        return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);

    }
}