package com.yeastar.testcase.pseries;

import com.yeastar.swebtest.pobject.Settings.PBX.General.API.API;
import com.yeastar.untils.APIUtil;
import org.testng.annotations.Test;

public class TestFQDN {
    APIUtil apiUtil = new APIUtil();
    @Test
    public void FQDNEnable(){
        apiUtil.FQDN("{\"enable_nat_fqdn\": 1}").apply();
    }
}
