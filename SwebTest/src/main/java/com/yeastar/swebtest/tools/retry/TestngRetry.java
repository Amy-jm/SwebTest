package com.yeastar.swebtest.tools.retry;

import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

import static com.yeastar.swebtest.driver.DataReader2.RETRY_COUNT;


/**
 * TestNG retry Analyzer.
 * @author kevinkong
 *
 */
public class TestngRetry implements IRetryAnalyzer {
	private static Logger logger = Logger.getLogger(TestngRetry.class);
	private int retryCount = 1;
	private static int maxRetryCount;

	static {
//		ConfigReader config = ConfigReader.getInstance();
		maxRetryCount = Integer.valueOf(RETRY_COUNT);
		logger.info("retrycount=" + maxRetryCount);
		logger.info("sourceCodeDir=" + "src");
		logger.info("sourceCodeEncoding=" + "UTF-8");
	}

	public boolean retry(ITestResult result) {
		if (retryCount <= maxRetryCount) {
			String message = "Retry for [" + result.getName() + "] on class [" + result.getTestClass().getName() + "] Retry "
					+ retryCount + " times";
			logger.info(message);
			Reporter.setCurrentTestResult(result);
			Reporter.log("RunCount=" + (retryCount + 1));
			retryCount++;
			return true;
		}
		return false;
	}

	public static int getMaxRetryCount() {
		return maxRetryCount;
	}
	
	public int getRetryCount() {
		return retryCount;
	}
	
}
