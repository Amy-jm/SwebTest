package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
@Log4j2
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
        sleep(WaitUntils.SHORT_WAIT*2);//todo version 30 bug,wait for fix and delete sleep
        ele_edit_name.setValue(name);
        ele_edit_default_destination.scrollTo();
        try {
            actions().moveToElement(ele_edit_trunk_toRight_btn).perform();
        }catch(org.openqa.selenium.WebDriverException ex){
            log.info("[createInboundRoute action exception And will retry] "+ex);
            new Actions(getDriver()).moveToElement(ele_edit_time_condition).perform();
        }
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

    /**
     * 新增 DID Matching Mode
     * @param row
     * @param pattern
     * @return
     */
    public InboundRoute addDIDPatternAnd(int row, String pattern){
        ele_inbound_routes_dial_pattern_add_btn.click();
        sleep(WaitUntils.SHORT_WAIT*2);//todo version 30 bug,wait for fix and delete sleep
        ele_inbound_routes_dial_pattern_input.get(row).pressEnter().sendKeys(Keys.chord(Keys.CONTROL,"a"),pattern);
        sleep(WaitUntils.RETRY_WAIT);
        return this;
    }
}
