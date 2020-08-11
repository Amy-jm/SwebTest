package com.yeastar.untils.APIObject;

import org.bouncycastle.math.raw.Mod;
import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConferenceObject {

    public class ModeratorList{
        public String text;
        public String text2;
        public String value;
        public String type;
        public ModeratorList(JSONObject jsonObject){
            this.text = getValueByString(jsonObject,"text");
            this.text2 = getValueByString(jsonObject,"text2");
            this.value = getValueByString(jsonObject,"value");
            this.type = getValueByString(jsonObject,"type");
        }
    }

    public int id ;
    public String number;
    public String name;
    public List<ModeratorList> moderatorLists;
    public ConferenceObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.number = getValueByString(jsonObject,"number");
        this.name = getValueByString(jsonObject,"name");
        this.moderatorLists = new ArrayList<>();
        if (jsonObject.containsKey("moderator_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("moderator_list");
            for (int i=0; i<m_memberist.size(); i++){
                this.moderatorLists.add(new ConferenceObject.ModeratorList((JSONObject) m_memberist.getJsonObject(i)));
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
