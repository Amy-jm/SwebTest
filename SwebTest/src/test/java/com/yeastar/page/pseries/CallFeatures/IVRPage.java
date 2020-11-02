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
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

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

    public IVRPage createIVR(String number, String name){
        addBtn.click();
        ele_ivr_basic_number.shouldBe(Condition.enabled).setValue(number);
        ele_ivr_basic_name.setValue(name);
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

    public IVRPage selectAllowExtensionsToRight(String... callerIDName){
        for(String name:callerIDName) {
            SelenideElement element = $(By.xpath(String.format(allowed_extensions,name)));
            actions().moveToElement(element).click().build().perform();
            sleep(1000);
        }
        getDriver().findElements(By.xpath("//i[contains(@class,'anticon-right')]/..")).get(1).click();
        return this;
    }
    /**
     * checkbox 设置
     * @param element
     * @param isChecked
     * @return
     */
    public IVRPage isCheckbox(SelenideElement element, boolean isChecked){
        if(element.getAttribute("checked") == null && isChecked){   //未勾选状态,isChecked为True，希望勾选
            new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
        }else if(element.getAttribute("checked") != null && !isChecked){  //勾选状态,isChecked为false，取消勾选
            new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
        }
        return this;
    }

    public enum KEY_PRESS_EVENT{
        EXTENSION(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        EXTENSION_VOICEMAIL(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        IVR(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        RING_GROUP(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        QUEUE(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        DIAL_BY_Name(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        EXTERNAL_NUMBER(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        PLAY_PROMPT_AND_EXIT(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim());
        private final String alias;
        KEY_PRESS_EVENT(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
    }
}
