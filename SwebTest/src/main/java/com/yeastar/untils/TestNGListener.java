package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.testng.*;


import java.util.Iterator;

import static com.yeastar.swebtest.driver.SwebDriver.webDriver;

/**
 * Created by user on 2017/11/3.
 */
@Log4j2
public class TestNGListener extends TestListenerAdapter implements IInvokedMethodListener {
    /**
     * onTestSuccess
     * @param tr
     */
    @Override
    public  void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        log.info( "[Success] "+tr.getName());

        Cookie cookie = new Cookie("zaleniumTestPassed", "true");
        webDriver.manage().addCookie(cookie);
    }

    /**
     * onTestFailure
     * @param tr
     */
    @Override
    public void onTestFailure(ITestResult tr) {
        /** 并发测试关闭
        log.error("Test Failure");
        super.onTestFailure(tr);
        new ReportUtils().step("[Test Failure Screenshot]",true);
         **/
        super.onTestFailure(tr);
        log.info("[Failure] "+tr.getName());

        //更新用例状态 zalenium
        Cookie cookie = new Cookie("zaleniumTestPassed", "false");
        webDriver.manage().addCookie(cookie);
    }


    /**
     * onTestSkipped
     * @param tr
     */
    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        log.info("[Skipped] "+tr.getName());
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
     * @param result
     */
    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);
        log.info( "[Start] " +result.getName());
    }

//    @Override
//    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
//        System.out.println( "********************* beforeInvocation ************************");
//        if (method.isTestMethod()) {
////            String browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browserName");
////            String gridURL = method.getTestMethod().getXmlTest().getLocalParameters().get("gridURL");
//            try {
//                RemoteWebDriver driver = DriverFactory.getRemoteBrowser();
//                DriverWrap.setWebDriver(driver);
//            } catch (Exception e) {
//                System.out.println( "********************* create RemoteWebDriver exception ************************");
//            }
//        }
//    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
//        if (method.isTestMethod()) {
//            RemoteWebDriver driver = DriverWrap.getDriver();
//            if (driver != null) {
//                driver.quit();
//            }
//        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
//        System.out.println( "********************* beforeInvocation ************************");
//        if (iInvokedMethod.isTestMethod()) {
//            try {
//                RemoteWebDriver driver = DriverFactory.getRemoteBrowser();
//                new DriverWrap().setWebDriver(driver);
//                System.out.println( "Session: "+driver.getSessionId());
//
//            } catch (Exception e) {
//                System.out.println( "********************* create RemoteWebDriver exception ************************");
//            }
//        }
    }
}
