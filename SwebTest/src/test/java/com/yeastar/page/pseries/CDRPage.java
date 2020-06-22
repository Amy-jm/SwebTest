package com.yeastar.page.pseries;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import co.boorse.seleniumtable.SeleniumTableRow;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
import static com.yeastar.swebtest.driver.SwebDriverP.ys_waitingTime;

/**
 * @program: SwebTest
 * @description: CDR page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/16
 */
@Log4j2
public class CDRPage extends BasePage implements ICdrPageElement{

    public CDRPage assertCDRRecord(){
        //todo cdr 显示全选



        return this;
    }
}
