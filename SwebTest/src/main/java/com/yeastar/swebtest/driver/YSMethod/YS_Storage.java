package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;


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
    public void AddNetworkDrive(String name,String hostip, String shareName, String username,String password) {
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
        ys_waitingTime(10000);
        String lineNum = String.valueOf(gridLineNum(preference.grid));
        String NetworkName=String.valueOf(gridContent(preference.grid,Integer.valueOf(lineNum),preference.gridColumn_Name));
        String Type =String.valueOf(gridContent(preference.grid,Integer.valueOf(lineNum),preference.gridColumn_Type));
        String Status = String.valueOf(gridContent(preference.grid,Integer.valueOf(lineNum),preference.gridColumn_Usage));

        YsAssert.assertEquals(NetworkName,name);
        YsAssert.assertEquals(Type,"NETDISK");
        YsAssert.assertInclude(Status,"%");
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

    /**
     * 选择进行全局录音的外线、分机、会议室
     * @param trunks：外线名称
     * @param extensions：分机名称
     * @param conference :会议室名称
     * @throws InterruptedException
     */
    public void selectRecord(ArrayList trunks,ArrayList extensions, ArrayList conference) {
        ys_waitingTime(10000);
        if(!trunks.isEmpty())
            if(trunks.get(0).equals("all")){
                recording.rt_AddAllToSelect.click();
                System.out.println("点击选择所有外线");
            }else {
                listSelect(recording.recordTrunks, trunkList, trunks);
            }
        if(!extensions.isEmpty())
            if(extensions.get(0).equals("all")){
                recording.re_AddAllToSelect.click();
                System.out.println("点击选择所有分机");
            }else{
                listSelect(recording.recordExtensions,extensionList,extensions);
             }
        if(!conference.isEmpty())
            if(conference.get(0).equals("all")){
                recording.rc_AddAllToSelect.click();
                System.out.println("点击选择所有会议室");
            }else
                listSelect(recording.recordConferences,nameList,conference);
        ys_waitingTime(1000);
        recording.save.click();
        ys_waitingMask();

    }
}
