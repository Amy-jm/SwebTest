package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.*;

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



    /**
     * 通过标题输入，label定位对应的input
     * @param label 标题信息
     * @param input 输入的内容
     */
    public void inputComm(String label,String input){
        $(By.xpath(String.format(INPUT_COMM_XPATH,label))).shouldHave(Condition.visible).setValue(input);
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
    public void clickApply(){
        applyBtn.waitUntil(Condition.enabled,WaitUntils.TIME_OUT_SECOND).click();
        applyLoadingBtn.shouldBe(Condition.visible);
        //applyLoadedBtn.shouldBe(Condition.visible);//界面不一定会渲染出loaded
    }

    /**
     * 点击保存并应用
     */
    public void clickSaveAndApply(){
        saveBtn.shouldBe(Condition.enabled).click();
        clickApply();
    }

}
