package com.yeastar.page.pseries.PbxSettings;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.page.pseries.BasePage;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.actions;

/**
 * @program: SwebTest
 * @description: preferences page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/28
 */
public class PreferencesPage extends BasePage implements IPreferencesPageElement {
    /**
     * 设置元素的值
     * @param strValue
     * @return
     */
    @Override
    public PreferencesPage setElementValue(SelenideElement element , String strValue){
        element.shouldBe(Condition.visible).doubleClick();
        actions().sendKeys(Keys.DELETE).perform();//adapt linux
        element.setValue(strValue);
        return this;
    }

    public PreferencesPage selectCombobox(String arg){
        selectComm(arg);
        return this;
    }
}
