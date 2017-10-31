package com.yeastar.swebtest.tools.retry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import static com.yeastar.swebtest.driver.SwebDriver.quitDriver;

/**
 * Test result Listener.
 *
 * @author kevinkong
 */
public class TestResultListener extends TestListenerAdapter {

    private static Logger logger = Logger.getLogger(TestResultListener.class);

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        //如果监听到失败的用例，截图重跑
//		ScreenShot.screenShots();
        System.out.println("onTestFailure....");
        logger.info(tr.getName() + " Failure");
//        quitDriver();
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
//		ScreenShot.screenShots();
        System.out.println("onTestSkipped.....");
        logger.info(tr.getName() + " Skipped");
//        quitDriver();
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        logger.info(tr.getName() + " Success");
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        logger.info(tr.getName() + " Start");
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        System.out.println("onFinish...");
// List of test results which we will delete later
        ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
// collect all id's from passed test
        Set<Integer> passedTestIds = new HashSet<Integer>();
        for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
            logger.info("PassedTests = " + passedTest.getName());
            passedTestIds.add(getId(passedTest));
        }

        Set<Integer> failedTestIds = new HashSet<Integer>();
        for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
            logger.info("failedTest = " + failedTest.getName());
// id = class + method + dataprovider
            int failedTestId = getId(failedTest);

// if we saw this test as a failed test before we mark as to be deleted
// or delete this failed test if there is at least one passed version
            if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestIds.add(failedTestId);
            }
        }

// finally delete all tests that are marked
        for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator(); iterator.hasNext();) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                logger.info("Remove repeat Fail Test: " + testResult.getName());
                iterator.remove();
            }
        }

    }

    private int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = id + result.getMethod().getMethodName().hashCode();
        id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
    }

}
