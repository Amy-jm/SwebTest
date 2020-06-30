package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.swebtest.testcase.RegressionCase.pbxcase.Inbound;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
import static com.yeastar.swebtest.driver.DataReader2.SPS;

public class InboundRoute extends BasePage implements IInboundRoutePageElement {

    /**
     * 创建一条呼入路由
     * @param name
     * @param trunklist
     * @return
     */
    @Step("创建呼入路由")
    public InboundRoute createInboundRoute(String name, List<String> trunklist){
        ele_add_btn.click();

        return this;
    }

    /**
     * 删除所有呼入路由
     * @return
     */
    @Step("删除所有呼入路由")
    public InboundRoute deleteAllInboundRoutes(){
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
        }
        return this;
    }

    /**
     *
     * @return
     */
    @Step("删除指定呼入路由")
    public InboundRoute deleteInboundRoute(WebDriver devier, String name){
        if(TableUtils.clickTableDeletBtn(devier,"Name",name)){
            OKAlertBtn.shouldBe(Condition.visible).click();
        }
        return this;
    }

}
