package com.yeastar.untils.APIObject;

import top.jfunc.json.JsonArray;
import top.jfunc.json.impl.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: SwebTest
 * @description: storage object
 * @author: huangjx@yeastar.com
 * @create: 2020/11/19
 */
public class StorageObject {
    public  StorageObject(){

    }
    public class Storage{
        public int id;
        public String name;
        public String type ;
        public String status;
        public String total;
        public String available;
        public String usage;
        public Storage(JSONObject jsonObject){
            this.id = Integer.parseInt(getValueByString(jsonObject, "id"));
            this.name = getValueByString(jsonObject, "name");
            this.status = getValueByString(jsonObject, "status");
            this.total = getValueByString(jsonObject, "total");
            this.type = getValueByString(jsonObject, "type");
            this.available = getValueByString(jsonObject, "available");
            this.usage = getValueByString(jsonObject, "usage");
        }
    }

    public List<Storage> storageList;
    public StorageObject(JSONObject jsonObject) {
        this.storageList = new ArrayList<>();

        if (jsonObject.containsKey("storage_list")){
            JsonArray m_memberist = jsonObject.getJsonArray("storage_list");
            for (int i=0; i<m_memberist.size(); i++){
                storageList.add(new StorageObject.Storage((JSONObject) m_memberist.getJsonObject(i)));
            }
        }
    }

    public class NetWorkStorageObject{
        public int id;
        public String name;
        public String host;
        public String shareName;
        public String userName;

        public NetWorkStorageObject(JSONObject jsonObject) {
            this.id = Integer.parseInt(getValueByString(jsonObject, "id"));
            this.name = getValueByString(jsonObject, "name");
            this.host = getValueByString(jsonObject, "host");
            this.shareName = getValueByString(jsonObject, "shareName");
            this.userName = getValueByString(jsonObject, "userName");
        }
    }
    private static String getValueByString(JSONObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            return jsonObject.getString(key);
        } else {
            return "";
        }
    }


}
