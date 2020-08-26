package com.yeastar.untils.APIObject;

import top.jfunc.json.impl.JSONObject;
import java.util.List;

public class IVRObject {
    public int id;
    public String number;
    public String name;
    public List<PressKeyObject> pressKeyObjectList;

    public enum PressKey {
        press0, press1, press2, press3, press4, press5, press6, press7, press8, press9, press_hash, press_star, timeout, invaild
    }

    public IVRObject() {}

    public IVRObject(JSONObject jsonObject) {
        this.id = Integer.parseInt(getValueByString(jsonObject, "id"));
        this.number = getValueByString(jsonObject, "number");
        this.name = getValueByString(jsonObject, "name");

    }


    private String getValueByString(JSONObject jsonObject, String key) {
        if (jsonObject.containsKey(key)) {
            return jsonObject.getString(key);
        } else {
            return "";
        }
    }

   public static class PressKeyObject{
       private String pressKeyNum;
       private String destPrefix;
       private String dest;
       private String destValue;
       private int isAllowOutRecord;

       public String getPressKeyNum() {
           return pressKeyNum;
       }

       public void setPressKeyNum(String pressKeyNum) {
           this.pressKeyNum = pressKeyNum;
       }

       public String getDestPrefix() {
           return destPrefix;
       }

       public void setDestPrefix(String destPrefix) {
           this.destPrefix = destPrefix;
       }

       public String getDestValue() {
           return destValue;
       }

       public void setDestValue(String destValue) {
           this.destValue = destValue;
       }

       public int getIsAllowOutRecord() {
           return isAllowOutRecord;
       }

       public void setIsAllowOutRecord(int isAllowOutRecord) {
           this.isAllowOutRecord = isAllowOutRecord;
       }

       public String getDest() {
           return dest;
       }

       public void setDest(String dest) {
           this.dest = dest;
       }

       public PressKeyObject(String pressKeyNum, String dest, String destPrefix, String destValue, int isAllowOutRecord) {
           this.pressKeyNum = pressKeyNum;
           this.dest = dest;
           this.destPrefix = destPrefix;
           this.destValue = destValue;
           this.isAllowOutRecord = isAllowOutRecord;
       }

       public PressKeyObject(Enum<IVRObject.PressKey> pressKeyEnum, String dest, String destPrefix, String destValue, int isAllowOutRecord) {
           this.pressKeyNum = pressKeyEnum.toString();
           this.dest = dest.toString();
           this.destPrefix = destPrefix;
           this.destValue = destValue;
           this.isAllowOutRecord = isAllowOutRecord;
       }
   }  }