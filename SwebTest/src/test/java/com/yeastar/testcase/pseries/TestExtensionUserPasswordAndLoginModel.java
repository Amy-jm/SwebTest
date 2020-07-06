package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBase;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * @program: SwebTest
 * @description: Extension user password and login model
 * @author: huangjx@yeastar.com
 * @create: 2020/07/06
 */
@Log4j2
public class TestExtensionUserPasswordAndLoginModel extends TestCaseBase {



    public Boolean isEmailServerWork(){
        step("进入System Email界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.system, HomePage.Menu_Level_2.system_tree_email);
        auto.extensionPage().testBtn.shouldBe(Condition.enabled).click();

        sleep(WaitUntils.SHORT_WAIT);
        List<WebElement> elements_input = getWebDriver().findElements(By.xpath("//input"));
        elements_input.get(elements_input.size()-1).sendKeys("yeastarautotest@163.com");
        sleep(WaitUntils.SHORT_WAIT);
        actions().sendKeys(Keys.ENTER).perform();
        sleep(WaitUntils.RETRY_WAIT);

        auto.extensionPage().getLastElementOffsetAndClick(auto.emailPage().STR_EMAIL_XPATH,2,2);

        SelenideElement element_success = $(By.xpath("//span[contains(text(),'Success')]"));
        return auto.extensionPage().waitElementDisplay(element_success, WaitUntils.TIME_OUT_SECOND);
    }



    //1.创建分机 user role-->Admin

    //2.
    @Test
    public void testSelectTime() throws IOException, JSchException {
        step("1:login PBX");
        auto.loginPage().login(LOGIN_USERNAME,LOGIN_PASSWORD);
        auto.homePage().header_box_name.shouldHave(Condition.text(LOGIN_USERNAME));

        step("2:创建分机号1001,启用Allow Register Remotely");
        log.debug("[Email Server is work]{}",isEmailServerWork());

    }
}
