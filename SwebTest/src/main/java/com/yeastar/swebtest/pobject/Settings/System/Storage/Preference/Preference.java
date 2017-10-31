package com.yeastar.swebtest.pobject.Settings.System.Storage.Preference;

import com.codeborne.selenide.SelenideElement;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/6/30.
 */
public class Preference {

//    public int LOCAL = 0;
//    public int HD = 1;
//    public int USB =2;
//    public int SDTF = 3;


    public int gridColumn_Name = 0;
    public int gridColumn_Type = 1;
    public int gridColumn_Total = 2;
    public int gridColumn_Available = 3;
    public int gridColumn_Usage =4;
    public int gridConfigure = 0;
    public int gridUnmountNetDisk = 1;

    public String  grid = "Ext.getCmp('control-panel').down('storagepreference').down('tableview')";
    public String  grid_Mask = "Ext.getCmp('control-panel').down('storagepreference').down('loadmask')";


    public String sdtf_CDR = "tf/sd-1";
    public String local_CDR = "local-1";

    public SelenideElement preference = $(By.xpath(".//div[starts-with(@id,\"storage\")]//span[ text()=\"Preference\"]"));

    public SelenideElement recordingSettings = $(By.xpath(".//a[starts-with(@class,\"cp-link-before\") and text()=\"Recording Settings\"]"));
    /**
     * Storage Locations
     */
    public String CDR = ("st-storage-slotcdr");
    public String voicemail_OneTouchRecordings = ("st-storage-slotvm");
    public String recordings ="st-storage-slotrecording";
    public String logs = ("st-storage-slotlog");

    /**
     * Storage Devices
     */

    public SelenideElement addNetworkDrive = $(By.xpath(".//div[starts-with(@id,'storagepreference')]//span[text()='Add Network Drive']"));
    public SelenideElement networkName = $(By.id("st-storage-name-inputEl"));
    public SelenideElement networkHostIp = $(By.id(("st-storage-host-inputEl")));
    public SelenideElement networkShareName  =$(By.id(("st-storage-sharename-inputEl")));
    public SelenideElement networkAccessUsername = $(By.id("st-storage-username-inputEl"));
    public SelenideElement networkAccessPassword = $(By.id(("st-storage-password-inputEl")));
    public SelenideElement networksave = $(By.xpath(".//div[starts-with(@id,'storagepreference-netdisk-')]//span[text()='Save']"));
    public SelenideElement networkcancel = $(By.xpath(".//div[starts-with(@id,'storagepreference-netdisk-')]//span[text()='Cancel']"));


    public SelenideElement save = $(By.xpath(" .//div[starts-with(@id,'storagepreference')]//span[ text()='Save']"));
    public SelenideElement cancel = $(By.xpath(".//div[starts-with(@id,'storagepreference')]//span[text()='Cancel']"));

    /**
     * 删除挂载磁盘判断
     */
    public SelenideElement Unmount_NetDisk_Yes = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()='Yes']"));
    public SelenideElement Unmount_NetDisk_No = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()='No']"));
    public SelenideElement Unmount_NetDisk_OK = $(By.xpath(".//div[starts-with(@id,'messagebox')]//span[text()='OK']"));
}
