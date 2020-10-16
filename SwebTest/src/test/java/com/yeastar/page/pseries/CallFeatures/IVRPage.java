package com.yeastar.page.pseries.CallFeatures;

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
}
