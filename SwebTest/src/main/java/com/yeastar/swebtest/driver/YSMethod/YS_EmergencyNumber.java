package com.yeastar.swebtest.driver.YSMethod;

import com.yeastar.swebtest.tools.ysassert.YsAssert;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/25.
 */
public class YS_EmergencyNumber {

    public void addEmergencyNumber(int emergencyNum,String trunkname,int extension) {
        emergencyNumber.add.click();
        add_emergency_number.emergencyNumber.setValue(String.valueOf(emergencyNum));
        ys_waitingTime(2000);
        ArrayList<String> trunk = new ArrayList<>();
        trunk.add(trunkname);
        listSelect(add_emergency_number.trunkSelect,trunkList,trunk);

        ArrayList<String> ext = new ArrayList<>();
        ext.add(String.valueOf(extension));
        listSelect(add_emergency_number.selectExtension,extensionList,ext);

        add_emergency_number.save.click();
        ys_waitingLoading(emergencyNumber.grid_Mask);
        String aclname = String.valueOf(gridContent(emergencyNumber.grid,gridFindRowByColumn(emergencyNumber.grid,emergencyNumber.gridColumn_EmergencyNumber,String.valueOf(emergencyNum),sort_ascendingOrder)
                ,emergencyNumber.gridColumn_EmergencyNumber));
        YsAssert.assertEquals(aclname,String.valueOf(emergencyNum));
    }
}
