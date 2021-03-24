package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class WebclientExtensionObject {

    public int ext_id;
    public String ext_num;
    public int status;
    public String caller_id_name;
    public String email_addr;
    public String first_name;
    public String last_name;
    public String mobile_number;
    public String presence_status;
    public  WebclientExtensionObject(JSONObject jsonObject) {

        this.ext_id = Integer.parseInt(getValueByString(jsonObject,"ext_id"));
        this.ext_num = getValueByString(jsonObject,"ext_num");
        this.status =Integer.parseInt(getValueByString(jsonObject,"status"));
        this.caller_id_name = getValueByString(jsonObject,"caller_id_name");
        this.email_addr = getValueByString(jsonObject,"email_addr");
        this.first_name = getValueByString(jsonObject,"first_name");
        this.first_name = getValueByString(jsonObject,"last_name");
        this.mobile_number = getValueByString(jsonObject,"mobile_number");
        this.presence_status = getValueByString(jsonObject,"presence_status");


    }
    private String getValueByString(JSONObject jsonObject, String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
