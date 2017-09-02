package com.yeastar.swebtest.driver.YSMethod;

import static com.yeastar.swebtest.driver.Config.extensionList;
import static com.yeastar.swebtest.driver.Config.grant_privilege_application;
import static com.yeastar.swebtest.driver.Config.userPermission;
import static com.yeastar.swebtest.driver.SwebDriver.*;
import static com.yeastar.swebtest.driver.SwebDriver.gridContent;

/**
 * Created by Yeastar on 2017/8/17.
 */
public class Ys_UserPermission {

    public void addUserPermission(int userNum , String privilegeAs)  {
        userPermission.add.click();
        ys_waitingMask();
        ys_waitingTime(8000);
        comboboxSelect(grant_privilege_settings.user_id,extensionList,String.valueOf(userNum));
        if(privilegeAs.equals(grant_privilege_settings.privilegeAs_Administrator)){
            setCombobox(grant_privilege_settings.setPrivilegeAs_id,grant_privilege_settings.privilegeAs_Administrator);
        }else if(privilegeAs.equals(grant_privilege_settings.privilegeAs_Custom)){
            setCombobox(grant_privilege_settings.setPrivilegeAs_id,grant_privilege_settings.privilegeAs_Custom);
        }

        grant_privilege_application.save.click();
        ys_waitingTime(5000);
//        String username= String.valueOf(gridContent(userPermission.grid,1,userPermission.gridColumn_User)) ;
//        String role = String.valueOf(gridContent(userPermission.grid,1,userPermission.gridColumn_Role));
    }
}
