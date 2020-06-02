package com.yeastar.page.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

@Log4j2
public class BasePage implements IButton{



    public SelenideElement searchOnHome = $(By.id("search"));

    public String INPUT_COMM_XPATH = "//label[contains(text(),'%s')]/../following-sibling::div//input";

    /**
     * 通过标题输入，label定位对应的input
     * @param label 标题信息
     * @param input 输入的内容
     */
    public void inputComm(String label,String input){
        $(By.xpath(String.format(INPUT_COMM_XPATH,label))).shouldHave(Condition.visible).setValue(input);
    }

    public void saveSuccessAlertAppear(){
        $(By.xpath("//span[text()=\"添加成功\"]")).waitUntil(Condition.appear,10*1000);
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

}
