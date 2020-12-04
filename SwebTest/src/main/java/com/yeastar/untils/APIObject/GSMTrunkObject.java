package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class GSMTrunkObject {
    public int id;
    public String name;

    public GSMTrunkObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.name = getValueByString(jsonObject,"name");
    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
