package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;
/**
 * company_contacts的对象组成
 * @author huangst
 */
public class CompanyContactsObject {
    public int id;
    public String contact_name;
    public String company;
    public String business;
    public String mobile;
    public String home;
    public String business_fax;

    public CompanyContactsObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getStringValue(jsonObject,"id"));
        this.contact_name = getStringValue(jsonObject,"contact_name");
        this.company = getStringValue(jsonObject,"company");
        this.business = getStringValue(jsonObject,"business");
        this.mobile = getStringValue(jsonObject,"mobile");
        this.home = getStringValue(jsonObject,"home");
        this.business_fax = getStringValue(jsonObject,"business_fax");
    }

    private String getStringValue(JSONObject jsonObject, String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else {
            return "";
        }
    }
}
