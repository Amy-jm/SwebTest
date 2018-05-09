package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Add_Outbound_Routes;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class Add_Outbound_Routes {

    public SelenideElement mt_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-or-trunkinfo-innerCt')]//a[@data-qtip='Add All to Selected']"));
    public SelenideElement mt_RemoveAllFromSelected = $(By.xpath(".//div[starts-with(@id,'st-or-trunkinfo-innerCt')]//a[@data-qtip='Remove All from Selected']"));
    public SelenideElement me_AddAllToSelect = $(By.xpath(".//div[starts-with(@id,'st-or-exteninfo-innerCt')]//a[@data-qtip='Add All to Selected']"));
    public SelenideElement me_RemoveAllFromSelected = $(By.xpath(".//div[starts-with(@id,'st-or-exteninfo-innerCt')]//a[@data-qtip='Remove All from Selected']"));
    public SelenideElement add_patterns = $(By.xpath("//*[@id=\"st-ir-adddialpattern\"]/div/img"));

    public String grid = "Ext.getCmp('control-panel').down('outrouter-edit').down('grid')";         //呼出路由中patterns部分的表格名字
    public String grid_Mask = "Ext.getCmp('control-panel').down('outrouter-edit').down('grid').down('loadmask')";   //表格加载需要缓冲时间

    public int grid_gridcolumn_patterns = 0;

    public int gridEdit = 0; //仅搜索img
    public int gridDelete = 1; //仅搜索img

    public String list_Trunk = "st-or-trunkinfo";
    public String list_Extension = "st-or-exteninfo";
    public String list_TimeContion1 = "Ext.query('#st-or-timecondition-innerCt'+ ' tr td div')[0].id";

    public String Password = "st-or-pintype";
    public String Password_None = "none";
    public String Password_Pinset = "pinset";
    public String Password_Singlepin = "singlepin";
    public SelenideElement singlepin_edit=$(By.id("st-or-singlepin-inputEl"));

    public String combobox_PinsetPassword = "st-or-pinset";

    public SelenideElement name = $(By.id("st-or-name-inputEl"));
    public SelenideElement dialPattems = $(By.id("st-ir-adddialpattern"));


//    public SelenideElement password_button = $(By.id("st-or-pintype-trigger-picker"));
    public String rrmemoryHunt = "st-or-adjusttrunk";
    public String Workday = "st-or-1";


    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,'outrouter-edit-')]//span[text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'outrouter-edit-')]//span[text()='Cancel']"));

}
