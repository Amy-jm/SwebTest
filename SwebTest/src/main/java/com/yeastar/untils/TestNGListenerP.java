package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.testng.*;

import java.util.Iterator;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
//import static com.yeastar.controllers.WebDriverFactory.getWebDriver;
import static com.yeastar.swebtest.driver.SwebDriver.webDriver;

/**
 * Created by user on 2017/11/3.
 */
@Log4j2
public class TestNGListenerP extends TestListenerAdapter implements IInvokedMethodListener {
    /**
     * onTestSuccess
     * @param tr
     */
    @Override
    public  void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        log.debug( "[Success] "+tr.getTestClass()+tr.getName());

        Cookie cookie = new Cookie("zaleniumTestPassed", "true");
        getWebDriver().manage().addCookie(cookie);
    }

    /**
     * onTestFailure
     * @param tr
     */
    @Override
    public void onTestFailure(ITestResult tr) {

        super.onTestFailure(tr);
        log.debug("[Failure] "+tr.getTestClass()+tr.getName());

        //更新用例状态 zalenium
        Cookie cookie = new Cookie("zaleniumTestPassed", "false");
        getWebDriver().manage().addCookie(cookie);
    }


    /**
     * onTestSkipped
     * @param tr
     */
    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        log.debug("[Skipped] "+tr.getTestClass()+tr.getName());

        //更新用例状态 zalenium
        Cookie cookie = new Cookie("zaleniumTestPassed", "skip");
        getWebDriver().manage().addCookie(cookie);
    }

    /**
     * onFinish
     * @param testContext
     */
    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        Iterator<ITestResult> listOfFailedTests = testContext.getFailedTests().getAllResults().iterator();
        while (listOfFailedTests.hasNext()) {
            ITestResult failedTest = (ITestResult) listOfFailedTests.next();
            ITestNGMethod method = failedTest.getMethod();
            if (testContext.getFailedTests().getResults(method).size() > 1) {
                listOfFailedTests.remove();
            }
            else {
                if (testContext.getPassedTests().getResults(method).size() > 0) {
                    listOfFailedTests.remove();
                }

            }
        }
}

    /**
     * onTestStart
     * @param tr
     */
    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        log.debug( "[Start test] " +tr.getTestClass()+tr.getName());
        Cookie cookie = new Cookie("zaleniumMessage","[Start test] " + tr.getName());
        getWebDriver().manage().addCookie(cookie);
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }

    @Override
    public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {

    }


//    @Override
//    public void afterInvocation(IInvokedMethod method, ITestResult iTestResult) {
//       log.debug("[afterInvocation]"+iTestResult.getName());
//    }
//
//    @Override
//    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
//        log.debug("[beforeInvocation]"+iTestResult.getName());
//    }
}