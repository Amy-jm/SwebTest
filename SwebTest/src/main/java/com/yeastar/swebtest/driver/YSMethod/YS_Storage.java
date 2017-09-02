package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import static com.yeastar.swebtest.driver.Config.addBulkExtensionsCallPermission;
import static com.yeastar.swebtest.driver.Config.extensions;
import static com.yeastar.swebtest.driver.Config.preference;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/19.
 */
public class YS_Storage {

    public void  isExistStorage(String storage) throws InterruptedException {
        preference.save.shouldBe(Condition.exist);
        ys_waitingLoading(preference.grid_Mask);
//        while(true){
//            Thread.sleep(50);
//            if(executeJs("return Ext.query('#'+Ext.getCmp('control-panel').down('storagepreference').down('loadmask').id)[0].style.display").equals("none"))
//                break;
//        }
        if(PRODUCT.equals(S300)){
            if(storage.equals("SD") || storage.equals("TF")){
                String SDTF=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,3)) ;
                if(SDTF.contains("%")){
                    YsAssert.assertEquals(SDTF,"NotInserted","SD\\TF未插入");
                }
            }
            else if(storage.equals("USB")){
                String USB=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,2)) ;
                if(USB.contains("%")){
                    YsAssert.assertEquals(USB,"NotInserted","USB未插入");
                }
            }
            else if(storage.equals("HD")){
                String HD=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,1)) ;
                if(HD.contains("%")){
                    YsAssert.assertEquals(HD,"NotInserted","HD未插入");
                }
            }
        }else if(PRODUCT.equals(S100)){
            if(storage.equals("SD") || storage.equals("TF")){
                String SDTF=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,2)) ;
                if(SDTF.contains("%")){
                    YsAssert.assertEquals(SDTF,"NotInserted","SD\\TF未插入");
                }
            }
            else if(storage.equals("USB")){
                String USB=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,1)) ;
                if(USB.contains("%")){
                    YsAssert.assertEquals(USB,"NotInserted","USB未插入");
                }
            }
        }else if(PRODUCT.equals(S50) || PRODUCT.equals(S20) || PRODUCT.equals(S412)){
            if(storage.equals("SD") || storage.equals("TF")){
                String SDTF=String.valueOf(gridContent(preference.grid,preference.gridColumn_Usage,1)) ;
                if(SDTF.contains("%")){
                    YsAssert.assertEquals(SDTF,"NotInserted","SD\\TF未插入");
                }
            }
        }

    }

    /**
     * 添加 NetworkDevice
     * @param name
     * @param hostip
     * @param shareName
     * @param username  可以 为 空
     * @param password  可以 为 空
     */
    public void AddNetworkDrive(String name,String hostip, String shareName, String username,String password) throws InterruptedException {
        preference.addNetworkDrive.click();
        preference.networkName.setValue(name);
        preference.networkHostIp.setValue(hostip);
        preference.networkShareName.setValue(shareName);
        if(!username.isEmpty()) {
            preference.networkAccessUsername.setValue(username);
        }
        if(!password.isEmpty()){
            preference.networkAccessPassword.setValue(password);
        }
        preference.networksave.click();
        ys_waitingLoading(preference.grid_Mask);
        ys_waitingTime(5000);
        String lineNum = String.valueOf(gridLineNum(preference.grid));
        String NetworkName=String.valueOf(gridContent(preference.grid,Integer.valueOf(lineNum),preference.gridColumn_Name));
        String Type =String.valueOf(gridContent(preference.grid,Integer.valueOf(lineNum),preference.gridColumn_Type));
        YsAssert.assertEquals(NetworkName,name);
        YsAssert.assertEquals(Type,"NETDISK");
    }

    /**
     *
     * @param index
     */
    public void StorageLocationsCDR(int index) throws InterruptedException {

        executeJs("Ext.getCmp('st-storage-slotcdr').setValue('"+preference.sdtf_CDR+"1')");
        executeJs("Ext.getCmp('st-storage-slotrecording').setValue('"+preference.sdtf_CDR+"1')");
        executeJs("Ext.getCmp('st-storage-slotlog').setValue('"+preference.sdtf_CDR+"1')");
        executeJs("Ext.getCmp('st-storage-slotvm').setValue('netdisk-2')");
        preference.save.click();
    }
}
