package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;


import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by AutoTest on 2017/9/27.
 */
public class YS_General {
    /**
     * 设置各个分机号的起始范围
     */
    public void setExPreferencesDefault(){
        preferences.preferences.click();

        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }
        preferences.userExtensions_start.clear();
        preferences.userExtensions_start.setValue("1000");
        preferences.userExtensions_end.clear();
        preferences.userExtensions_end.setValue("5999");
        preferences.ringGroupExtensions_start.clear();
        preferences.ringGroupExtensions_start.setValue("6200");
        preferences.ringGroupExtensions_end.clear();
        preferences.ringGroupExtensions_end.setValue("6299");
        preferences.pagingGroupExtensions_start.clear();
        preferences.pagingGroupExtensions_start.setValue("6300");
        preferences.pagingGroupExtensions_end.clear();
        preferences.pagingGroupExtensions_end.setValue("6399");
        preferences.conferenceExtensions_start.clear();
        preferences.conferenceExtensions_start.setValue("6400");
        preferences.conferenceExtensions_end.clear();
        preferences.conferenceExtensions_end.setValue("6499");
        preferences.IVRExtensions_start.clear();
        preferences.IVRExtensions_start.setValue("6500");
        preferences.IVRExtensions_end.clear();
        preferences.IVRExtensions_end.setValue("6599");
        preferences.queueExtensions_start.clear();
        preferences.queueExtensions_start.setValue("6700");
        preferences.queueExtensions_end.clear();
        preferences.queueExtensions_end.setValue("6799");
        preferences.save.click();
        ys_waitingMask();
    }

    /*设置允许拨打时间的特征码、分机
     */
   public void setExtensionPermission(boolean enable, String prefix ,String... extension ){
       featureCode.featureCode.click();
       featureCode.setExtensionPermission.should(Condition.exist);
       ys_waitingTime(5000);
        executeJs("Ext.getCmp('st-feature-enbforcetime').setValue("+String.valueOf(enable)+")");
        ys_waitingTime(1000);
        if(!prefix.isEmpty()){
            featureCode.timeConditionOverride.clear();
            featureCode.timeConditionOverride.setValue(prefix);
            featureCode.save.click();
        }
        featureCode.setExtensionPermission.click();
        if(extension[0].equals("all")){;
           listSelectAll(featureCode.list_setExtionsionPermission);
        }else {
           listSelect(featureCode.list_setExtionsionPermission,extensionList,extension);
        }
        featureCode.list_save.click();
        featureCode.save.click();
   }

    /**
     * 设置截答特征码
     * @param checkcallpickup
     * @param callpickup
     * @param checkextensionPickup
     * @param extensionPickup
     */
    public void setPickup(Boolean checkcallpickup,String callpickup,Boolean checkextensionPickup,String extensionPickup ){
        featureCode.featureCode.click();
        featureCode.callPickup.shouldBe(Condition.exist);
        ys_waitingTime(1000);

        setCheckBox(featureCode.callPickup_check,true);
        featureCode.callPickup.clear();
        featureCode.callPickup.setValue(callpickup);
        setCheckBox(featureCode.callPickup_check,checkcallpickup);

        setCheckBox(featureCode.extensionPickup_check,true);
        featureCode.extensionPickup.clear();
        featureCode.extensionPickup.setValue(extensionPickup);
        setCheckBox(featureCode.extensionPickup_check,checkextensionPickup);

        featureCode.save.click();

    }

    /**
     * 设置对讲的特征码
     * @param checkintercom
     * @param intercom
     */
    public void setIntercom(Boolean checkintercom,String intercom){
        featureCode.featureCode.click();
        featureCode.intercom.shouldBe(Condition.exist);
        ys_waitingTime(1000);
        setCheckBox(featureCode.intercom_check,checkintercom);
        featureCode.intercom.clear();
        featureCode.intercom.setValue(intercom);
        featureCode.save.click();
    }

}
