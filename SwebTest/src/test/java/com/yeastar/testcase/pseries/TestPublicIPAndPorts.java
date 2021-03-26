package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import org.testng.annotations.Test;

public class TestPublicIPAndPorts extends TestCaseBaseNew {

    public void initTest(){
        apiUtil.tls("\"enb_tls\":0");
        apiUtil.rebootask().reboot();

    }
    @Test
    public void testPublicIPAndPorts(){
        initTest();
    }
}
