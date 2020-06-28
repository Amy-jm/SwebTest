package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

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
        element.shouldBe(Condition.visible).setValue(strValue);
        return this;
    }
}
