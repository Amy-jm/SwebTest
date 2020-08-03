package com.yeastar.untils.APIObject;

import com.alibaba.fastjson.JSON;
import top.jfunc.json.JsonArray;
import top.jfunc.json.JsonObject;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OutBoundRouteObject {

    public class TrunkList{
        public String text;
        public String value;
        public String type;
        public TrunkList(JSONObject jsonObject){
            this.text = getValueByString(jsonObject,"text");
            this.value = getValueByString(jsonObject,"value");
            this.type = getValueByString(jsonObject,"type");
        }
    }

    public class DialPatternList{

        public int id;
        public String dialPattern;
        public int strip;

        public DialPatternList(JSONObject jsonObject){
            this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
            this.dialPattern = getValueByString(jsonObject,"dialPattern");
            this.strip = Integer.parseInt(getValueByString(jsonObject,"strip"));
        }
    }

    public int id;
    public int pos;
    public String name;
    public List<TrunkList> trunkLists;
    public List<DialPatternList> dialPatternLists;

    public OutBoundRouteObject(JSONObject jsonObject){
        this.id = Integer.parseInt(getValueByString(jsonObject,"id"));
        this.pos = Integer.parseInt(getValueByString(jsonObject,"pos"));
        this.name = getValueByString(jsonObject,"name");

        this.trunkLists = new ArrayList<>();
        this.dialPatternLists = new ArrayList<>();

        if (jsonObject.containsKey("trunk_list")){
            JsonArray m_trunkList = jsonObject.getJsonArray("trunk_list");
            for (int i=0; i<m_trunkList.size(); i++){
                this.trunkLists.add(new TrunkList((JSONObject) m_trunkList.getJsonObject(i)));

            }
        }
        if (jsonObject.containsKey("dial_pattern_list")){
            JsonArray m_dialPatternList = jsonObject.getJsonArray("dial_pattern_list");

            for (int i=0; i<m_dialPatternList.size(); i++){
                this.dialPatternLists.add(new DialPatternList((JSONObject) m_dialPatternList.getJsonObject(i)));
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
