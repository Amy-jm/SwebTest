package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class ExtensionObject {

    public int id;
    public String type;
    public String callerIdName;
    public int roleId;
    public String roleName;
    public String number;
    public String presenceStatus;
    public int enableMobileClient;
    public int enableDesktopClient;
    public int enableWebClient;

    public ExtensionObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.type = getValueByString(jsonObject,"type");
        this.callerIdName = getValueByString(jsonObject,"caller_id_name");
        this.roleId = Integer.parseInt(getValueByString(jsonObject,"role_id"));
        this.roleName = getValueByString(jsonObject,"role_name");
        this.number = getValueByString(jsonObject,"number");
        this.presenceStatus = getValueByString(jsonObject,"presence_status");
        this.enableMobileClient = Integer.parseInt(getValueByString(jsonObject,"enb_mobile_client"));
        this.enableDesktopClient = Integer.parseInt(getValueByString(jsonObject,"enb_desktop_client"));
        this.enableWebClient = Integer.parseInt(getValueByString(jsonObject,"enb_web_client"));
    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
