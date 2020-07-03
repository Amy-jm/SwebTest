package com.yeastar.page.pseries.PbxSettings;

import com.yeastar.page.pseries.BasePage;

public class Preferences extends BasePage implements IPreferencesPageElement {

    public Preferences selectCombobox(String arg){
        selectComm(arg);
        return this;
    }
}