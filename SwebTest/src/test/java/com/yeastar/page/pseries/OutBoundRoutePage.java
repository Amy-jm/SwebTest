package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.untils.WaitUntils;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

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
     * 通过路由名，通过编辑图标，编辑
     * @param routeName
     * @return
     */
    public OutBoundRoutePage editRoute(String routeName){
        $(By.xpath(String.format(ROUTE_LIST_TABLE_XAPTH,routeName))).click();
        return  this;
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

}
