package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.controllers.WebDriverFactory.getDriver;

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
        sleep(10000);
        ele_edit_name.setValue(name);
        ele_edit_default_destination.scrollTo();
        actions().moveToElement(ele_edit_trunk_toRight_btn).perform();
        for(String trunkname: trunklist){
            $(By.xpath("//td[contains(text(),'"+trunkname+"')]")).shouldBe(Condition.visible).click();
        }
        sleep(WaitUntils.RETRY_WAIT);
        ele_edit_trunk_toRight_btn.click();
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
    public InboundRoute deleteInboundRoute(String name){
        if(TableUtils.clickTableDeletBtn(WebDriverFactory.getDriver(),"Name",name)){
            OKAlertBtn.shouldBe(Condition.visible).click();
        }
        return this;
    }

    @Step("点击编辑呼入路由")
    public InboundRoute editInbound(String name,String title){
        TableUtils.clickTableEidtBtn(getDriver(),title,name);
        sleep(5000);
        return this;
    }

    public InboundRoute selectDefaultDestination(String type, String dest){
        selectCombobox(type);
        selectComm(ele_edit_default_destination2,dest);
        return this;
    }

    public InboundRoute selectCombobox(String arg){
        selectComm(arg);
        return this;
    }
}
