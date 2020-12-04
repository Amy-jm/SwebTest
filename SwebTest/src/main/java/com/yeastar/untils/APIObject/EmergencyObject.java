package com.yeastar.untils.APIObject;

import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmergencyObject {

    public class TrunkList {
        public String value;
        public String outbCid;
        public String text;
        public String type;
        public TrunkList(JSONObject jsonObject){
            this.value = getValueByString(jsonObject,"value");
            this.outbCid = getValueByString(jsonObject,"outb_cid");
            this.value = getValueByString(jsonObject,"text");
            this.type = getValueByString(jsonObject,"type");
        }
    }

    public int id ;
    public String number;
    public String name;
    public String outbCidOption;
    public List<TrunkList> trunkLists;
    public EmergencyObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.number = getValueByString(jsonObject,"number");
        this.name = getValueByString(jsonObject,"name");
        this.outbCidOption = getValueByString(jsonObject,"outb_cid_option");
        this.trunkLists = new ArrayList<>();
        if (jsonObject.containsKey("trunk_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("trunk_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.trunkLists.add(new TrunkList((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
    }

    private String getValueByString(JSONObject jsonObject,String key){
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return "";
        }
    }
}
