package com.yeastar.swebtest.pobject.Me.Me_Voicemail;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by Yeastar on 2017/7/21.
 */
public class Me_Voicemail {
    public String grid = "Ext.getCmp('mesetting').down('mevoicemail')";
    public String grid_Mask = "Ext.getCmp('mesetting').down('mevoicemail').down('loadmask')";

    public int gridColumn_Check = 0;
    public int gridColumn_Read = 1;
    public int gridColumn_Callerid = 2;
    public int gridColumn_Time = 3;
    public int gridColumn_Duration = 4 ;
    public int gridColumn_Size=5;

    public int gridPlay = 0;
    public int gridDownload = 1;
    public int gridDelete = 2;
}
