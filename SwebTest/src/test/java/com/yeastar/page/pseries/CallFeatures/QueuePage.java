package com.yeastar.page.pseries.CallFeatures;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.swebtest.testcase.RegressionCase.pbxcase.Queue;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class QueuePage extends BasePage implements IQueuePageElement {

    /**
     * 删除所有队列
     * @return
     */
    @Step("删除所有队列")
    public QueuePage deleAllQueue() {
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
     * 编辑指定name的队列
     * @param driver
     * @param name
     * @return
     */
    public QueuePage editQueue(WebDriver driver, String name){
        TableUtils.clickTableEidtBtn(driver,"Name",name);
        return this;
    }

    /** Tab 菜单切换 **/
    public QueuePage switchToTab(String enumTabMenu){
        baseSwitchToTab(enumTabMenu);
        return this;
    }

    public QueuePage createQueue(String number, String name){
        addBtn.click();
        ele_queue_basic_number_input.setValue(number);
        ele_queue_basic_name_input.setValue(name);

//        switchToTab(QUEUE_TAB.QUEUE_MEMBERS.getAlias());
//        for(String dynname: dynList){
//            $(By.xpath("//div[@id='"+ele_queue_dynamic_agents_left_table.getAttribute("id")+"']//td[text()='"+dynname+"']")).click();
//        }
//        ele_queue_add_dynamic_agents_btn.click();
//        for(String staname: staList){
//            $(By.xpath("//div[@id='"+ele_queue_static_agents_left_table.getAttribute("id")+"']//td[text()='"+staname+"']")).click();
//        }
//        ele_queue_add_static_agents_btn.click();
        return this;
    }

    public QueuePage editDynamicAgents(List<String> dynList){
        switchToTab(QUEUE_TAB.QUEUE_MEMBERS.getAlias());
        for(String dynname: dynList){
            $(By.xpath("//div[@id='"+ele_queue_dynamic_agents_left_table.getAttribute("id")+"']//td[text()='"+dynname+"']")).click();
        }
        ele_queue_add_dynamic_agents_btn.click();
        return this;
    }

    public QueuePage editStaticAgents(List<String> staList){
        switchToTab(QUEUE_TAB.QUEUE_MEMBERS.getAlias());
        for(String staname: staList){
            $(By.xpath("//div[@id='"+ele_queue_static_agents_left_table.getAttribute("id")+"']//td[text()='"+staname+"']")).click();
        }
        ele_queue_add_static_agents_btn.click();
        return this;
    }
}
