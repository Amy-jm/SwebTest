package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.sleep;

/**
 * @program: SwebTest
 * @description: Operator Panel Test Case
 * @author: huangjx@yeastar.com
 * @create: 2020/07/30
 */
public class TestOperatorPanel extends TestCaseBase {

    @Epic("P_Series")
    @Feature("Operator Panel")
    @Story("外线号码A 呼入到")
    @Description("外线号码2000 呼入到--> Extension 呼入到分机1000-->分机10000 响铃中 -->呼入状态：ringing\n" +
            "1:分机0,login web client\n" +
            "2:外线号码[2000]呼叫[1000]\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P0","VCP","testIncomingRingStatus","Regression","PSeries"})
    public void testIncomingRingStatus() throws IOException, JSchException {
        //TODO 前提条件 1.新增分机0,1000   2.呼入路由到 1000
        step("1:login PBX");
        auto.loginPage().loginWithExtension("0","Yeastar202","New"+LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text("0"));

        step("2:进入Operator panel 界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.operator_panel);


        assertStep("3:[PJSIP注册]] ，2000 呼叫 1001 ");
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1001,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT,-1);

        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(1001,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist_For_PSeries(2000,DEVICE_ASSIST_2);

        pjsip.Pj_Make_Call_No_Answer(2000,"991001",DEVICE_IP_LAN,false);
        sleep(WaitUntils.SHORT_WAIT);

        assertStep("4:Ring显示状态 ");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"Ring");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Callee),"1000 A[1000]");
        softAssert.assertEquals(auto.operatorPanelPage().getRecordValue(OperatorPanelPage.TABLE_TYPE.INBOUND, RECORD.Caller,"2000",RECORD.Status),"External");
        pjsip.Pj_Hangup_All();
        softAssert.assertAll();

    }
}
