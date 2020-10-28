package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.controllers.WebDriverFactory.getDriver;

public class IVRPage extends BasePage implements IIVRPageElement {
    /**
     * 删除所有IVR
     * @return
     */
    @Step("删除所有IVR")
    public IVRPage deleAllIVR() {
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
//            applyBtn.shouldBe(Condition.visible).click();
//            sleep(WaitUntils.SHORT_WAIT*3);
        }

        return this;
    }

    /**
     * 编辑指定name的IVR
     * @param driver
     * @param name
     * @return
     */
    public IVRPage editIVR(WebDriver driver, String name){
        TableUtils.clickTableEidtBtn(driver,"Name",name);
        return this;
    }

    /**
     * 编辑指定name的IVR
     * @param name
     * @return
     */
    public IVRPage editIVR(String name){
        TableUtils.clickTableEidtBtn(getWebDriver(),"Name",name);
        return this;
    }

    public IVRPage createIVR(String number, String name,String extname){
        addBtn.click();
        ele_ivr_basic_number.shouldBe(Condition.enabled).setValue(number);
        ele_ivr_basic_name.setValue(name);

        switchToTab(IVR_TAB.KEY_PRESS_EVENT.getAlias());
        selectComm(ele_ivr_key_press_event_press0_dest,IVR_KEY_PRESS_EVENT.EXTENSION.getAlias());
        selectComm(ele_ivr_key_press_event_press0_dest_value,extname);
        return this;
    }

    /** Tab 菜单切换 **/
    public IVRPage switchToTab(String enumTabMenu){
        baseSwitchToTab(enumTabMenu);
        return this;
    }

    /**
     * 设置元素的值
     * @param strValue
     * @return
     */
    public IVRPage setElementValueWithClean(SelenideElement element , String strValue){
        actions().moveToElement(element).click().build().perform();
        sleep(1000);
        actions().sendKeys(Keys.BACK_SPACE).build().perform();
        actions().sendKeys(Keys.BACK_SPACE).build().perform();
        actions().sendKeys(Keys.BACK_SPACE).build().perform();
        actions().sendKeys(Keys.BACK_SPACE).build().perform();
        actions().sendKeys(Keys.BACK_SPACE).build().perform();

        if(strValue.contains(",")){
            String[] split = strValue.split(",");
            for(int i = 0 ; i<split.length ; i++){
                actions().sendKeys(split[i]).build().perform();
                sleep(2000);
                actions().sendKeys(Keys.ENTER).build().perform();
                sleep(1000);
            }
        }else{
            actions().sendKeys(strValue).build().perform();
            sleep(1000);
            actions().sendKeys(Keys.ENTER).build().perform();
        }

        return this;
    }

    /**
     * 设置元素的值
     * @param strValue
     * @return
     */
    public IVRPage setElementDebug(WebElement element , String strValue){
        element.clear();
//        element.sendKeys(Keys.BACK_SPACE);
        getDriver().findElement(By.xpath("//*[@id=\"extra_prompt\"]//li")).sendKeys("None");
        element.sendKeys(Keys.chord(Keys.CONTROL,"a"),strValue);
        return this;
    }

    public IVRPage selectAllowExtensionsToRight(String[] callerIDName){
        for(String name:callerIDName) {
            SelenideElement element = $(By.xpath(String.format(allowed_extensions,name)));
            actions().moveToElement(element).click().build().perform();
            sleep(1000);
        }
        getDriver().findElements(By.xpath("//i[contains(@class,'anticon-right')]/..")).get(1).click();
        return this;
    }
}
