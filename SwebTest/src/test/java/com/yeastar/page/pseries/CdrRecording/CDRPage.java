package com.yeastar.page.pseries.CdrRecording;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.swebtest.driver.ConfigP.*;
//import static com.yeastar.swebtest.driver.DataReader2.*;

/**
 * @program: SwebTest
 * @description: CDR page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/16
 */
@Log4j2
public class CDRPage extends BasePage implements ICdrPageElement {


    /**
     * 断言CDR记录
     * @param webDriver
     * @param row   在表格中的行数，从0开始计数
     * @param callFrom
     * @param callTo
     * @param status
     * @param reason
     * @param sourceTrunk
     * @param destTrunk
     * @param communicationType
     * @return
     */
    public CDRPage assertCDRRecord(WebDriver webDriver,int row,String callFrom,String callTo,String status,String reason,String communicationType,String sourceTrunk,String destTrunk){

        //todo cdr 显示全选  刷新按键
        SoftAssert softAssert = new SoftAssert();
        ele_download_cdr_btn.waitUntil(Condition.visible,WaitUntils.SHORT_WAIT);
        ele_refresh_cdr_btn.click();
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Call_From.getAlias(),row),callFrom);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Call_To.getAlias(),row),callTo);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Status.getAlias(),row),status);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Reason.getAlias(),row),reason);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Communication_Type.getAlias(),row),communicationType);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Source_Trunk.getAlias(),row),sourceTrunk);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Destination_Trunk.getAlias(),row),destTrunk);
        softAssert.assertAll();

        return this;
    }

    public CDRPage assertCDRRecord(WebDriver webDriver,int row,String callFrom,String callTo,String status,String reason,String communicationType) {
        assertCDRRecord(webDriver,row,callFrom,callTo,status,reason,communicationType,"","");
        return this;
    }

    /**
     * 删除当前页所有CDR
     * @return
     */
    public CDRPage deleteAllCDR(){
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
        }
        return  this;
    }

}
