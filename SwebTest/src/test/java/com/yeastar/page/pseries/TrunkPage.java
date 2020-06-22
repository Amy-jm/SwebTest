package com.yeastar.page.pseries;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
import static com.yeastar.page.pseries.ITrunkPageElement.Trunk_Type.PeerTrunk;
import static com.yeastar.untils.TableUtils.*;

public class TrunkPage extends BasePage implements ITrunkPageElement
{
    /**
     * 删除指定中继
     * @param trunkName 需要删除的中继名称
     * @return
     */
    @Step("删除指定中继")
    public TrunkPage deleTrunk(WebDriver driver,String trunkName){

        addBtn.shouldBe(Condition.visible);
//        Selenide.actions().click(ele_delete_all_checkbox).perform();
//        deleteBtn.shouldBe(Condition.visible).click();
//        OKAlertBtn.shouldBe(Condition.visible).click();
//        sleep(WaitUntils.RETRY_WAIT);
//        applyBtn.shouldBe(Condition.visible).click();
//        sleep(WaitUntils.SHORT_WAIT*3);

        TableUtils.clickTableEidtBtn(getDriver(),"Name",trunkName);
//        TableUtils.getCDRForHeader(getDriver(),"Communication Type",0);
        return this;
    }

    /**
     * 创建sps中继
     * @param trunkName
     * @return
     */
    @Step("创建sps中继")
    public TrunkPage createSpsTrunk(String trunkName,String hostname, String domain){
        addBtn.shouldBe(Condition.enabled).click();
        selectTrunkType(PeerTrunk);
        ele_trunk_basic_name.setValue(trunkName);
        ele_trunk_basic_hostname.setValue(hostname);
        ele_trunk_basic_domain.setValue(hostname);
        return this;
    }

    @Step("创建sip register中继")
    public TrunkPage createSipRegisterTrunk(String trunkName){
        return this;
    }

    @Step("创建Account Trunk中继")
    public TrunkPage createAccountTrunk(String trunkName){
        return this;
    }

    /**
     * 选择Trunk Type
     * @param trunk_type
     * @return
     */
    public TrunkPage selectTrunkType(Trunk_Type trunk_type){
        $(By.xpath(String.format(SELECT_COMM_XPATH,trunk_type.getAlias()))).click();
        return this;
    }
}
