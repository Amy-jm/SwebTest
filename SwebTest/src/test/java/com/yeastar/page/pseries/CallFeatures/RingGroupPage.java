package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
@Log4j2
public class RingGroupPage extends BasePage implements IRingGroupPageElement {

    /**
     * 删除所有响铃组
     *
     * @return
     */
    @Step("删除所有响铃组")
    public RingGroupPage deleAllRingGroup() {
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
//            applyBtn.shouldBe(Condition.visible).click();
//            sleep(WaitUntils.SHORT_WAIT*3);
        }

        return this;
    }

    /**
     * 编辑指定name的响铃组
     *
     * @param driver
     * @param name
     * @return
     */
    public RingGroupPage editRingGroup(WebDriver driver, String name) {
        TableUtils.clickTableEidtBtn(driver, "Name", name);
        return this;
    }

    public RingGroupPage createRingGroup(String number, String name, List<String> extList) {
        addBtn.click();
        ele_ring_group_name_input.shouldBe(Condition.enabled).setValue(number);
        try {
            actions().moveToElement(ele_ring_group_add_extension_member).perform();
        } catch (org.openqa.selenium.WebDriverException ex) {
            log.info("[createInboundRoute action exception And will retry] " + ex);
            new Actions(getDriver()).moveToElement(ele_ring_group_add_extension_member).build().perform();
        }
        for (String extname : extList) {
            $(By.xpath("//td[contains(text(),'" + extname + "')]")).click();
        }
        ele_ring_group_add_extension_member.shouldBe(Condition.enabled).click();
        return this;
    }
}
