package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Properties;

/**
 * @author huangjx
 **/
@Log4j2
public class TestNGRetry implements IRetryAnalyzer {
    private static int maxRetryCount = 0;

    static {
        Properties p = null;
        try {
            p = new PropertiesUntils().getInstance().getPropertie("/config.properties");
        } catch (Exception e) {
            log.error("can not find config properties file " +e);
        }
        try {
            maxRetryCount = Integer.valueOf(p.getProperty("maxRunCount"));
        } catch (java.lang.NumberFormatException e) {
            log.error("can not find retry key maxRunCount on " +e);
        }
        log.info("[maxRunCount] " + (maxRetryCount) + "\n");
    }

    private int count = 1;

    /**
     * Returns true if the test method has to be retried, false otherwise.
     *
     * @param result The result of the test method that just ran.
     * @return true if the test method has to be retried, false otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        if (count <= maxRetryCount) {
//            log.info("[maxRunCount -> retryCount] " + maxRetryCount +"->"+count+"\n");
            result.setAttribute("RETRY", new Integer(count));
            count++;
            return true;
        }
        return false;
    }
}
