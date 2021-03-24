package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class WebclientPersonObject {
    public String number;
    public String caller_id_name;
    public String first_name;
    public WebclientPersonObject(JSONObject jsonObject){
        this.number =getValueByString(jsonObject,"number");
        this.caller_id_name = getValueByString(jsonObject,"caller_id_name");
        this.first_name =getValueByString(jsonObject,"first_name");

    }
    private String getValueByString(JSONObject jsonObject, String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }


}
