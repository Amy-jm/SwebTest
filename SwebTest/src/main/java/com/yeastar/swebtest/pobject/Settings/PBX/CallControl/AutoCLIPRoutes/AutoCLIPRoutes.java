package com.yeastar.swebtest.pobject.Settings.PBX.CallControl.AutoCLIPRoutes;

import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import cucumber.api.java.cs.A;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/6/28.
 */
public class AutoCLIPRoutes {

//    public String grid = "Ext.getCmp('control-panel').down('cliplist').down('tableview')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('cliplist').down('loadmask')";
    public String grid = "Ext.getCmp('control-panel').down('cliplist').down('tableview')";
    public String grid_Page= "Ext.getCmp('control-panel').down('cliplist')";


    public int gridColumn_Checked = 0;
    public int gridColumn_ExtNumber = 1;
    public int gridColumn_CallerNumber = 2;
    public int gridColumn_Trunk = 3;
    public int gridColumn_ExpirationsTime = 4;

    public String list= "st-clip-trunkinfo";
    public SelenideElement autoCLIPRoutes = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"AutoCLIP Routes\"]"));

    public SelenideElement viewAutoCLIPList = $(By.xpath(".//div[starts-with(@id,'callcontrol-')]//span[text()='View AutoCLIP List']"));

    public String deleteUsedRecords = ("st-clip-useonce");
    public String onlyKeepMissedCallRecords = ("st-clip-onlynoanswer");
    public String matchOutgoingTrunk = ("st-clip-checkport");
    public String recordKeepTime_button = ("st-clip-keeptime");
    public String digitsMatch = "st-clip-matchdigit";

    public SelenideElement save = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"Save\"]"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,\"callcontrol\")]//span[ text()=\"Cancel\"]"));

    public SelenideElement mt_RemoveAllFromSelect = $(By.xpath(".//div[@id='st-clip-trunkinfo']//a[@data-qtip='Remove All from Selected']"));
    public SelenideElement mt_RemoveFromSelect = $(By.xpath(".//div[@id='st-clip-trunkinfo']//a[@data-qtip='Remove from Selected']"));
    public SelenideElement mt_AddToSelect = $(By.xpath(".//div[@id='st-clip-trunkinfo']//a[@data-qtip='Add to Selected']"));
    public SelenideElement mt_AddAllToSelect = $(By.xpath(".//div[@id='st-clip-trunkinfo']//a[@data-qtip='Add All to Selected']"));

    //AutoClipRoutes
    public SelenideElement delete = $(By.xpath(".//div[starts-with(@id,'cliplist-')]//span[text()='Delete']"));

    public SelenideElement delete_yes = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='Yes']"));
    public SelenideElement delete_ok = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='OK']"));
    public SelenideElement delete_no = $(By.xpath(".//div[starts-with(@id,'messagebox-')]//span[text()='No']"));

    public void closeAutoClIP_List(){
        executeJs("Ext.get(Ext.getCmp('control-panel').down('cliplist').down('tool').id).dom.click()");
    }

/*
    检查AutoCLIP List的数据
 */
    public void checkCliplist(String ex_num,String called_num,String trunk){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        viewAutoCLIPList.click();
        ys_waitingLoading(grid_Mask);
        YsAssert.assertEquals(gridContent(grid,1,1),ex_num,"AutoCLIP List--Extension Number检测");
        YsAssert.assertEquals(gridContent(grid,1,2),called_num,"AutoCLIP List--Called Number检测");
        YsAssert.assertEquals(String.valueOf(gridContent(grid,1,3)),trunk,"AutoCLIP List--Trunk检测");
        closeAutoClIP_List();
    }
}
