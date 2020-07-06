package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.actions;

/**
 * @program: SwebTest
 * @description: preferences page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/28
 */
public class PreferencesPage extends BasePage implements IPreferencesPage{
    /**
     * 设置元素的值
     * @param strValue
     * @return
     */
    @Override
    public PreferencesPage setElementValue(SelenideElement element , String strValue){
        element.shouldBe(Condition.visible).click();
        actions().sendKeys(Keys.DELETE).perform();//adapt linux
        element.setValue(strValue);
        return this;
    }
}
