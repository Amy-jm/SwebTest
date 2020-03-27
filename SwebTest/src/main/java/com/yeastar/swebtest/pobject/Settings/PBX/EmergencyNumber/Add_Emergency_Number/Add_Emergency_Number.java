package com.yeastar.swebtest.pobject.Settings.PBX.EmergencyNumber.Add_Emergency_Number;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.SwebDriver.executeJs;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class Add_Emergency_Number {

    public String trunkSelect = "trunk0";
    public String selectExtension = "admin0";
    public SelenideElement addTrunk1 = $(By.xpath(".//div[starts-with(@id,'panel-')]//div[1]//div[6]//img"));
    public SelenideElement addTrunk2 = $(By.xpath(".//div[starts-with(@id,'panel-')]//div[2]//div[6]//img"));
    public SelenideElement deleteTrunk2 = $(By.xpath(".//div[starts-with(@id,'panel-')]//div[2]//div[5]//img"));
    public SelenideElement addNotification = $(By.xpath(".//div[starts-with(@id,'panel-')]//div[4]//img"));
    public SelenideElement deleteNotification1 = $(By.xpath(".//div[starts-with(@id,'panel-')]//div[2]//div[3]//img"));

    public SelenideElement emergencyNumber = $(By.id("st-emergency-number-inputEl"));
    public SelenideElement trunk = $(By.id("prefix0-inputEl"));
    public SelenideElement trunkPrepend = $(By.id("prefix0-inputEl"));
    public String notification = "admin0";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'emergency-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'emergency-edit-')]//span[text()='Cancel']"));

//   获取添加紧急号码页面中  多个Trunk/Admin情况下的data-componentid，除了第一个trunk/admin，其他的Trunk和Admin的data-componentid都是动态的，所以没办法写死
//   id从0开始
    public String getTrunkSelect(int id){
        return String.valueOf(executeJs("return  Ext.query(\"input[name='trunk']\")["+id+"].dataset.componentid"));
    }
    public String getAdminSelect(int id){
        return String.valueOf(executeJs("return  Ext.query(\"input[name='admin']\")["+id+"].dataset.componentid"));
    }

//    获取新增trunk的添加按钮
    public SelenideElement getAddTrunk(int row){
//        row表示第几行Trunk
        String path =  ".//div[starts-with(@id,'panel-')]//div["+ row +"]//div[6]//img";
        return $(By.xpath(path));
    }
//    获取trunk的删除按钮
    public SelenideElement getDeleteTrunk(int row){
//        row表示第几行Trunk
        String path =  ".//div[starts-with(@id,'panel-')]//div["+ row +"]//div[5]//img";
        return $(By.xpath(path));
    }
//    获取新增Notification的添加按钮
    public SelenideElement getAddNotification(int row){
//        row表示第几行notification
    String path =  ".//div[starts-with(@id,'panel-')]//div["+ row +"]//div[4]//img";
    return $(By.xpath(path));
}
//    获取notification的删除按钮
    public SelenideElement getDeleteNotification(int row){
//        row表示第几行notification
        String path =  ".//div[starts-with(@id,'panel-')]//div["+ row +"]//div[3]//img";
        return $(By.xpath(path));
    }
}
