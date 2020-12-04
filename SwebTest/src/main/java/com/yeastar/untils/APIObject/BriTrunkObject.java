package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class BriTrunkObject {
    public int id;
    public String name;
    public String type;


    public BriTrunkObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.name = getValueByString(jsonObject,"name");
        this.type = getValueByString(jsonObject,"type");

    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
