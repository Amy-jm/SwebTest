package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class PerContactsObject {
    public String company;
    public String contact_name;
    public String email;
    public String first_name;
    public String last_name;
    public String number_list;
    public int id;
    public String phone_book;
    public String perType;

    public PerContactsObject(JSONObject jsonObject){
        this.id = Integer.parseInt(jsonObject.getString("id"));
        this.company = getStringValue(jsonObject,"company");
        this.email = getStringValue(jsonObject,"email");
        this.first_name = getStringValue(jsonObject,"first_name");
        this.last_name = getStringValue(jsonObject,"last_name");
        this.number_list = getStringValue(jsonObject,"number_list");
        this.phone_book = getStringValue(jsonObject,"phone_book");
        this.perType = getStringValue(jsonObject,"type");
    }

    public String getStringValue(JSONObject jsonObject, String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else
            return "";
    }
}
