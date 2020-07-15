package com.yeastar.page.pseries.CallControl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.controllers.WebDriverFactory.getDriver;

/**
 * @program: SwebTest
 * @description: OutboundRoute page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/30
 */
public class OutBoundRoutePage extends BasePage implements IOutBoundRoutePageElement{

    //根据路由名称选择->编辑
    public String ROUTE_LIST_TABLE_XAPTH = "//td[contains(text(),'AutoTest_Route')]/../../../../table/tbody/tr[1]//i[contains(@class,'edit')]";
    //Extension Extension Group 通用分机选择
    public String GROUP_EXTENSION_XPATH = "//td[contains(text(),'%s')]/..//input";
    //Extension Extension Group 通用分机选择-右移按钮
    public String Group_EXTENSION_RIGHT_BUTTON_XPATH="//td[contains(text(),'%s')]/../../../../../../../../../../../../../../../..//button[1]";

    /**
     * 删除所有呼入路由
     * @return
     */
    @Step("删除所有呼出路由")
    public OutBoundRoutePage deleteAllOutboundRoutes(){
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
        }
        return this;
    }


    @Step("删除指定呼chu路由")
    public OutBoundRoutePage deleteOutboundRoute(WebDriver devier, String name){
        if(TableUtils.clickTableDeletBtn(devier,"Name",name)){
            OKAlertBtn.shouldBe(Condition.visible).click();
        }
        return this;
    }


    public OutBoundRoutePage createOutbound(String name, String pattern, String strip, List<String> trunklist, List<String> extlist){
        ele_add_btn.click();
        ele_outbound_routes_name_input.setValue(name);
        ele_outbound_routes_dial_pattern_add_btn.click();
        sleep(2000);
        ele_outbound_routes_dial_pattern_input.get(0).click();
        ele_outbound_routes_dial_pattern_input.get(0).setValue(pattern);
        ele_outbound_routes_strip_input.get(0).setValue(strip);
        for(String trunkname: trunklist){
            $(By.xpath("//td[contains(text(),'"+trunkname+"')]")).click();
        }
        ele_outbound_routes_add_trunk_btn.click();

        for(String extname: extlist){
            $(By.xpath("//td[contains(text(),'"+extname+"')]")).click();
        }
        ele_outbound_routes_add_extension_btn.click();
        return this;
    }
    /**
     * 添加分机到路由中
     * @param extension
     * @return
     */
    public OutBoundRoutePage addExtensionOrExtensionGroup(String extension){
        sleep(WaitUntils.SHORT_WAIT);
        Selenide.actions().moveToElement($(By.xpath(String.format(GROUP_EXTENSION_XPATH,extension))),3,3).click().perform();
        sleep(WaitUntils.RETRY_WAIT);
        $(By.xpath(String.format(Group_EXTENSION_RIGHT_BUTTON_XPATH,extension))).click();
        return this;
    }

    @Step("点击编辑呼入路由")
    public OutBoundRoutePage editOutbound(String name,String title){
        TableUtils.clickTableEidtBtn(getDriver(),title,name);
        return this;
    }

}
