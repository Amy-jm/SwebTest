package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class ExtensionGroupObject {

    public int id;
    public String name;


    public ExtensionGroupObject(JSONObject jsonObject){
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
