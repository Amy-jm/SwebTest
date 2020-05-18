package com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_Basic;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.setCheckBox;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_IVR_Basic {

    public SelenideElement basic = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Basic']"));

    public SelenideElement number = $(By.id("st-ivr-number-inputEl"));
    public SelenideElement name = $(By.id("st-ivr-name-inputEl"));
    public String prompt = "st-ivr-prompt0";
    public String promptRepeatCount ="st-ivr-playtimes";
//    public String responseTimeout ="st-ivr-waitexten";
//    public String dightTimeout ="st-ivr-digittimeout";
    public String dialExtensions ="st-ivr-enablenumber";
    public String dialOutboundRoutes ="st-ivr-enableoutrouter";
    public String listOutboundRoutes = "st-ivr-allowrouters";
    public String dialtoCheckVoicemail = "st-ivr-enablecheckvoicemail";
    public SelenideElement checkVoicemail_ok = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"OK\"]"));
    public SelenideElement responseTimeout =$(By.id("st-ivr-waitexten-inputEl"));
    public SelenideElement dightTimeout =$(By.id("st-ivr-digittimeout-inputEl"));

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'ivr-edit-')]//span[text()='Cancel']"));

    //允许所有分机 下拉列表
    public SelenideElement enablenumberTrigger =  $(By.xpath("//*[@id=\"st-ivr-enablenumber-trigger-picker\"]"));
    public SelenideElement allowExtension =  $(By.xpath("//li[contains(text(),\"Allow All Extensions\")]"));

    /**
     * 适配不同不同版本，小于30.14.0.A 为checkbox，大于30.14.0.A为list
     */
    public void choiceEnableNumber(Boolean boo){
        //大于30.14.0.A为list
        if(enablenumberTrigger.exists()){
            enablenumberTrigger.click();
            allowExtension.shouldBe(Condition.visible).click();
        }else{
            //小于30.14.0.A 为checkbox
            setCheckBox(dialExtensions,boo);
        }

    }

}
