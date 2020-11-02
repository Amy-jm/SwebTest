package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.page.pseries.ExtensionTrunk.ExtensionPage;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.Collections;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.controllers.WebDriverFactory.getDriver;

/**
 * BasePage 通用方法
 */
@Log4j2
public class BasePage implements IButton{



    public SelenideElement searchOnHome = $(By.id("search"));
    //input 通用定位
    public String INPUT_COMM_XPATH = "//label[contains(text(),'%s')]/../following-sibling::div//input";
    //li 下拉选项通用定位
    public String SELECT_COMM_XPATH = "//li[contains(text(),'%s')]";
    //tab标签页定位
    public String TAB_COMM_XPATH = "//div[contains(@role,'tab') and contains(text(),\"%s\")]";
    //表格全选
    SelenideElement ele_delete_all_table = $(By.xpath("//table//thead//input[1]"));



    /**
     * 通过标题输入，label定位对应的input
     * @param label 标题信息
     * @param input 输入的内容
     */
    public void inputComm(String label,String input){
        $(By.xpath(String.format(INPUT_COMM_XPATH,label))).shouldHave(Condition.visible).setValue(input);
    }

    /** Tab 菜单切换 **/
    public void baseSwitchToTab(String enumTabMenu){
        $(By.xpath(String.format(TAB_COMM_XPATH,enumTabMenu))).shouldBe(Condition.visible).click();
    }
    /**
     * 选择下拉框
     *
     * @param arg 枚举类型中的参数别名
     * @return
     */
    public void selectComm(String arg) {
        $(By.xpath(String.format(SELECT_COMM_XPATH, arg))).click();
        return;
    }

    /**
     * 选择下拉框中内容
     * @param ele 下拉框元素
     * @param arg 需要选择的文本
     */
    public void selectComm(SelenideElement ele, String arg) {
        ele.shouldBe(Condition.visible).click();
        $(By.xpath(String.format(SELECT_COMM_XPATH, arg))).shouldBe(Condition.visible).click();
        return;
    }

    public Boolean isSaveSuccessAlertAppear(){
        return $(By.xpath("//span[text()=\"添加成功\"]")).waitUntil(Condition.appear,10*1000).isDisplayed();
    }

    public boolean waitElementDisplay(SelenideElement selenideElement,long waitTime){
        boolean boo = false;
        long startTime=System.currentTimeMillis();
        try{
            selenideElement.waitUntil(Condition.exist,waitTime);
            boo = true;
            System.out.println("[Wait Element Visibility Time]:"+(System.currentTimeMillis()-startTime)+" Millis");
        }catch (com.codeborne.selenide.ex.ElementNotFound ex){
            log.error(ex);
            System.out.println("[NoSuchElementException Time]:"+(System.currentTimeMillis()-startTime)/1000+" Seconds");
            boo = false;
        }catch (TimeoutException timeoutException){
            boo = false;
        }
        return boo;
    }

    /**
     * ExtJs 调用方法
     * @param js
     * @return
     */
    public  Object executeJs(String js) {
        log.debug("[executeJS] "+js);
        Object result = executeJavaScript(js);
        return result;
    }

    /**
     * 点击 apply button
     */
    public void clickApply() {
        sleep(1000);
        if(applyBtn.isDisplayed()){
            applyBtn.waitUntil(Condition.enabled, WaitUntils.TIME_OUT_SECOND).click();
            applyLoadingBtn.shouldBe(Condition.visible);
            applyLoadingBtn.shouldBe(Condition.disappear);
            if (applyLoadedBtn.isDisplayed()) {
                applyLoadedBtn.shouldBe(Condition.disappear);
            }
        }
    }


    /**
     * 点击保存并应用
     */
    public void clickSaveAndApply(){
        if(waitElementDisplay(saveBtn,WaitUntils.SHORT_WAIT*2) && saveBtn.has(Condition.visible)){
            saveBtn.click();
        }
//        saveBtn.shouldBe(Condition.enabled).click();
        sleep(WaitUntils.SHORT_WAIT);
        if(applyBtn.isDisplayed()){
            clickApply();
        }

    }
    /**
     * checkbox 设置
     * @param element
     * @param isChecked
     * @return
     */
    public BasePage isCheckbox(SelenideElement element,boolean isChecked){
        if(element.getAttribute("checked")==null){
            if(isChecked){
                new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
            }
        }else{
            if(!isChecked){
                new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
            }
        }
        return  this;
    }

    /**
     * 设置元素的值
     * @param strValue
     * @return
     */
    public BasePage setElementValue(SelenideElement element ,String strValue){
        element.shouldBe(Condition.visible).click();
        element.sendKeys(Keys.chord(Keys.CONTROL,"a"),strValue);
        return this;
    }

    /**
     * 点击保存
     * @return
     */
    public void clickSave() {
        saveBtn.shouldBe(Condition.enabled).click();
    }

    /**
     * 删除所有表格数据
     * @return
     */
    @Step("删除所有表格数据")
    public BasePage deleAllTableData() {
        if (ele_delete_all_table.isEnabled()) {
            Selenide.actions().click(ele_delete_all_table).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            clickApply();
        }
        return this;
    }

    /**
     * 获取最后一个元素位置，并通过偏移单击
     * @param strXpath
     * @param x
     * @param y
     */
    public void getLastElementOffsetAndClick(String strXpath,int x,int y){
        ElementsCollection elements_Test = $$(By.xpath(strXpath));
        try {
            actions().moveToElement(elements_Test.get(elements_Test.size() - 1), x, y).click().build().perform();
        } catch (org.openqa.selenium.WebDriverException ex) {
            log.info("[BastPage getLastElementOffsetAndClick exception And will retry] " + ex);
            new Actions(getDriver()).moveToElement(elements_Test.get(elements_Test.size() - 1), x, y).click().build().perform();
        }
    }



    @Step("{0}")
    public void reportMessage(String desc){
        log.debug("[Message] "+desc);
        sleep(5);
        try{
            Cookie cookie = new Cookie("zaleniumMessage", desc);
            getWebDriver().manage().addCookie(cookie);
        }catch (org.openqa.selenium.WebDriverException exception){
            log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
        }catch(Exception ex){
            log.error("[BasePage on reportMessage ] "+ex);
        }
    }


    /**
     * 界面刷新
     */
    public void refresh(){
        getWebDriver().navigate().refresh();
    }


    /**
     * 单击坐标
     * @param x
     * @param y
     */
    public void moveByOffsetAndClick(int x,int y){
        actions().moveByOffset(x,y).perform();
    }

    public void clickElementFromJS(WebElement element){
        try {
            if (element.isEnabled() && element.isDisplayed()) {
                System.out.println("Clicking on element with using java script click");

                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
            } else {
                log.error("Unable to click on element");
            }
        } catch (StaleElementReferenceException e) {
            log.error("Element is not attached to the page document "+ e.getStackTrace());
        } catch (NoSuchElementException e) {
            log.error("Element was not found in DOM "+ e.getStackTrace());
        } catch (Exception e) {
            log.error("Unable to click on element "+ e.getStackTrace());
        }
    }
    /**
     * 分机删除
     * extensionNumber 分机号
     * @return
     */
    public BasePage deleDataByDeleImage(String extensionNumber){
        $(By.xpath(String.format(DELETE_IMAGE_FOR_TABLE_FROM_TABLE_EXTENSION_NUMBER_XPATH
                ,extensionNumber))).click();
        OKAlertBtn.shouldBe(Condition.visible).click();
        return this;
    }

}
