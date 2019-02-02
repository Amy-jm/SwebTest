package com.yeastar.linkustest.pobject;

import org.openqa.selenium.By;

import static com.yeastar.linkustest.driver.DataReader.*;

public class Contacts {

    public  By constacts_tab = By.id("com.yeastar.linkus:id/extension_layout");
    public  By extensions_page = By.id("com.yeastar.linkus:id/switch_left_tv");
    public  By contacts_page = By.id("com.yeastar.linkus:id/switch_right_tv");
    public  By search = By.id("com.yeastar.linkus:id/filter_cet");

    public By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public By ok = By.id("com.yeastar.linkus:id/actionbar_right_iv");
    //表格
    public By contactLayout = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alpha_lv']/android.widget.LinearLayout");
    //联系人
    public By name1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusA_name+"']");
    public By status1 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusA_name+"']/../android.widget.TextView[2]");
    public By name2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusB_name+"']");
    public By status2 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusB_name+"']/../android.widget.TextView[2]");
    public By name3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusC_name+"']");
    public By status3 = By.xpath("//*[@resource-id='com.yeastar.linkus:id/alphalist_name_tv'][@text='"+linkusC_name+"']/../android.widget.TextView[2]");

    //分机详细信息
    public By acatar = By.id("com.yeastar.linkus:id/tab_extension_avatar_civ");//头像
    public By detailName = By.id("com.yeastar.linkus:id/tab_extension_name_tv");

    public By detailExtension = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Extension']/../android.widget.TextView[2]");
    public By detailChat = By.id("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Extension']/../android.widget.ImageView[1]");
    public By detailCallExtension = By.id("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Extension']/../android.widget.ImageView[2]");
    public By detailMobile =By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Mobile']/../android.widget.TextView[2]");
    public By detailMsg =By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Mobile']/../android.widget.ImageView[1]");
    public By detailCallMobile = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Mobile']/../android.widget.ImageView[2]");
    public By detailEmial =By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Email']/../android.widget.TextView[2]");
    public By detailSendEmail = By.xpath("//*[@resource-id='com.yeastar.linkus:id/tab_extension_name_tv'][@text='Email']/../android.widget.ImageView[1]");

    public By selectNumExtension= By.xpath("//*[@resource-id='com.yeastar.linkus:id/lLayout_content']/android.widget.LinearLayout[1]/android.widget.TextView");;
    public By selectNumMobile = By.xpath("//*[@resource-id='com.yeastar.linkus:id/lLayout_content']/android.widget.LinearLayout[2]/android.widget.TextView");
    public By selectNumCancel = By.name("Cancel");



}
