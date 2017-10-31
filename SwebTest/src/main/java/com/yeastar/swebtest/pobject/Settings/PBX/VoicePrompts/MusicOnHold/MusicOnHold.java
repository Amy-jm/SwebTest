package com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.MusicOnHold;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/29.
 */
public class MusicOnHold {


    public int gridcolumn_Check = 0; //仅搜索.x-grid-row-checker
    public int gridcolumn_MusicOnHoldFile = 1;
    public int gridPlay = 0;
    public int gridDelete =1;
    public String grid = "Ext.getCmp('control-panel').down('mohfiles')";
    public String grid_Mask = "Ext.getCmp('control-panel').down('mohfiles').down('loadmask')";

    public SelenideElement musicOnHold = $(By.xpath(".//div[starts-with(@id,'prompt')]//span[ text()='Music on Hold']"));
    public String chooseMOHPlaylist_id  = "st-moh-choosefolder";

    public SelenideElement createNewPlaylist = $(By.id("st-moh-createfolder-btnInnerEl"));
    public SelenideElement chooseMOHPlaylist_picker = $(By.id("st-moh-choosefolder-trigger-picker"));
    public SelenideElement chooseMOHPlaylist_input = $(By.id("st-moh-choosefolder-inputEl"));
    public SelenideElement chooseMOHPlaylist_edit = $(By.id("st-moh-editfolder"));
    public SelenideElement chooseMOHPlaylist_delete = $(By.id("st-moh-delfolder"));
    public SelenideElement uploadNewMusic = $(By.id("st-moh-mohfile-inputEl"));
    public SelenideElement browse = $(By.id("st-moh-mohfile-trigger-filebutton"));
    public SelenideElement upload = $(By.id("st-moh-upload-btnInnerEl"));
    public SelenideElement delete = $(By.id("st-moh-delfolder"));
    public SelenideElement deleteHoldFiles = $(By.xpath(".//div[starts-with(@id,'mohfiles')]//span[(text()=\"Delete\") and starts-with(@id,'button')]"));


    public SelenideElement playPrompt_PlayToExtension = $(By.id("st-moh-extension"));
    public SelenideElement playPrompt_Play = $(By.xpath(".//div[starts-with(@id,'moh-play')]//span[text()=\"Play\"]"));
    public SelenideElement playPrompt_Cancel = $(By.xpath(".//div[starts-with(@id,'moh-play')]//span[text()=\"Cancel\"]"));
    /**
     * 删除功能
     */
    public SelenideElement delete_yes = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"Yes\"]"));
    public SelenideElement delete_no = $(By.xpath(".//span[starts-with(@class,\"x-btn-inner x-btn-inner-default-small\") and text()=\"No\"]"));

    /**
     * 页码功能
     */
    public SelenideElement firstPage = $(By.xpath(".//div[starts-with(@id,'moh-play')]//a[@data-qtip='First Page']"));
    public SelenideElement previousPage = $(By.xpath(".//div[starts-with(@id,'moh-play')]//a[@data-qtip='Previous Page']"));
    public SelenideElement nextPage = $(By.xpath(".//div[starts-with(@id,'moh-play')]//a[@data-qtip='Next Page']"));
    public SelenideElement lastPage = $(By.xpath(".//div[starts-with(@id,'moh-play')]//a[@data-qtip='Last Page']"));
    public SelenideElement refresh = $(By.xpath(".//div[starts-with(@id,'moh-play')]//a[@data-qtip='Refresh']"));
    public SelenideElement gotoinput = $(By.xpath(".//div[starts-with(@id,'moh-play')]//input[@name='gotoinput']"));
    public SelenideElement go = $(By.xpath(".//div[starts-with(@id,'moh-play')]//span[text()='Go']"));
    public SelenideElement selectPage = $(By.id(".//*[(@data-ref='bodyEl') and (@role='presentation')]"));

    /**
     * play
     */
    public SelenideElement name = $(By.id("st-moh-name-inputEl"));
    public SelenideElement palyToExtension = $(By.id("st-moh-extension-trigger-picker"));
    public SelenideElement play = $(By.xpath(".//div[starts-with(@id,'moh-play-')]//span[text()='Play']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'moh-play-')]//span[text()='Cancel']"));
    public String  plauToExtension = "st-moh-extension";
}
