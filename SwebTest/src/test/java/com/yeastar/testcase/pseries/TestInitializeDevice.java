package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import io.qameta.allure.*;
import org.testng.annotations.Test;

/**
 * @program: SwebTest
 * @description: 设备初始化后，设备环境配置
 * @author: huangjx@yeastar.com
 * @create: 2020/12/03
 */
public class TestInitializeDevice extends TestCaseBaseNew {

    @Epic("P_Series")
    @Feature("Initialize Device")
    @Story("")
    @Description("1.启用SSH Security-》Security Settings SSH Access 启用SSH ，端口默认为8022")
    @Test(groups = {"Init"})
    public void testInit_01_MaxCallDuration() {
        step("1.启用SSH Security-》Security Settings SSH Access 启用SSH ，端口默认为8022");
        apiUtil.editSSHAccess("\"enable_ssh\":1,\"ssh_port\":8022").apply();

    }
}
