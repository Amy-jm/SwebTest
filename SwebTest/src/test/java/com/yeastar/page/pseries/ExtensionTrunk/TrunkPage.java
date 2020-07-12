package com.yeastar.page.pseries.ExtensionTrunk;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.page.pseries.CallFeatures.RingGroupPage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.page.pseries.ExtensionTrunk.ITrunkPageElement.TRUNK_TYPE.PeerTrunk;


public class TrunkPage extends BasePage implements ITrunkPageElement
{
    /**
     * 删除指定中继
     * @param trunkName 需要删除的中继名称
     * @return
     */
    @Step("删除指定中继")
    public TrunkPage deleteTrunk(WebDriver driver,String trunkName){

        addBtn.shouldBe(Condition.visible);
        if(TableUtils.clickTableDeletBtn(driver,"Name",trunkName)){
            OKAlertBtn.shouldBe(Condition.visible).click();
        }
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
        ele_trunk_basic_domain.setValue(domain);
        baseSwitchToTab(TRUNK_TAB.OUTBOUND_CALLER_ID.getAlias());
        ele_trunk_outbound_cid_list_def_outbound_cid.setValue("spsOutCallId");
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
    public TrunkPage selectTrunkType(TRUNK_TYPE trunk_type){
        $(By.xpath(String.format(SELECT_COMM_XPATH,trunk_type.getAlias()))).click();
        return this;
    }
}
