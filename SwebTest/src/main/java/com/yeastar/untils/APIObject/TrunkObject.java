package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;

public class TrunkObject {
    public int id;
    public String name;
    public int enable;
    public String type;
    public String hostPort ;
    public String defOutBoundCid;
    public String status;

    public TrunkObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.name = getValueByString(jsonObject,"name");
        this.enable = Integer.parseInt(getValueByString(jsonObject,"enable"));
        this.type = getValueByString(jsonObject,"type");
        this.hostPort = getValueByString(jsonObject, "host_port");
        this.defOutBoundCid = getValueByString(jsonObject,"def_outbound_cid");
        this.status = getValueByString(jsonObject , "status");

    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
