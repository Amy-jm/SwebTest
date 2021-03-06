package com.yeastar.untils;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import top.jfunc.json.impl.JSONObject;

import java.io.IOException;

@Log4j2
@Getter
public class CDRObject {
    private String prefix="cdr_recording.cdr.";
    private JSONObject obj;
    public String errorcode;
    public String errmsg;
    public String uid;
    public String time;
    public String callFrom;
    public String callTo;
    public String callDuration;
    public String ringDuration;
    public String talkDuration;
    public String status;
    public String reason;
    public String sourceTrunk;
    public String destinationTrunk;
    public String communicatonType;
    public String did;
    public String dod;
    public String callerIpAddr;
    public String recordingFile;

    public CDRObject(String respondJson, int index) throws IOException {

        JSONObject object = new JSONObject(respondJson);
        errorcode = object.getString("errcode").trim();
        errmsg  = object.getString("errmsg");

        if(!errorcode.trim().equals("0")){
            log.error("[CDR API] errorcode: "+errorcode+" errmsg:"+errmsg);
            return;
        }

        obj = (JSONObject) object.getJsonArray("cdr_list").getJsonObject(index);
        uid = getValue("uid");
        time = getValue("time");
        callFrom = getValue("call_from").replace(prefix,"");
        callTo = getValue("call_to").replace(prefix,"");
        if(obj.containsKey("duration")){
            callDuration =  DateUtil.secondToTime(Integer.parseInt(obj.getString("duration")));
        }
        ringDuration = getValue("ring_duration");
        talkDuration = getValue("talk_duration");
        status = UIMapUtils.getValueByKey(getValue("disposition").toLowerCase().replace(" ","_"));
        sourceTrunk = getValue("src_trunk").replace(prefix,"");
        destinationTrunk = getValue("dst_trunk").replace(prefix,"");
        communicatonType = getValue("call_type").replace(prefix,"");
        did = getValue("did_number").replace(prefix,"");
        dod = getValue("dod_number").replace(prefix,"");
        callerIpAddr = getValue("src_addr");
        reason = getReason().replace(prefix,"");

    }

    /**
     * ??????????????????value???????????????????????????key???????????????value,??????????????????
     * @param key
     * @return
     */
    private String getValue(String key){
        if(obj.containsKey(key)){
            return prefix+obj.getString(key);
        }
        return "";
    }

    /**
     * ??????Reasn?????????????????????????????????src??????call_from?????????dst??????call_to??????
     * @param key
     * @return
     */
    private String getReasonValue(String key){
        if(obj.containsKey(key)){
            if(obj.getString(key).equals("src")) {
                return obj.getString("call_from");
            }else if(obj.getString(key).equals("dst")){
                return obj.getString("call_to");
            }else{
                return prefix+obj.getString(key);
            }
        }
        return "";
    }
    /**
     * ??????Reason
     * @return
     */
    private String getReason() throws IOException {

        String str = UIMapUtils.getValueByKey(getReasonValue("reason_partya")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyb")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyc")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyd")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partye")) +" " +
                UIMapUtils.getValueByKey(getReasonValue("reason_partyf")) ;

        return str.trim();
    }

    /**
     * cdr field call to
     */
    public enum CDRNAME {
        Extension_1000("test A<1000>"),
        Extension_1001("test2 B<1001>"),
        Extension_1002("testta C<1002>"),
        Extension_1003("testa D<1003>"),
        Extension_1004("t estX<1004>"),
        Extension_1005("First Last<1005>"),
        Extension_1020("1020 1020<1020>"),
        Extension_3001("3001<3001>"),
        Extension_2000("2000<2000>"),
        Extension_2001("2001<2001>"),
        Extension_4000("4000<4000>"),

        IVR0_6200("IVR IVR0<6200>"),
        IVR1_6201("IVR IVR1<6201>"),

        RINGGROUP0_6300("RingGroup RingGroup0<6300>"),
        RINGGROUP1_6301("RingGroup RingGroup1<6301>"),

        QUEUE0_6400("Queue Queue0<6400>"),
        QUEUE1_6401("Queue Queue1<6401>"),
        QUEUE2_6402("Queue Queue2<6402>"),
        QUEUE3_6403("Queue Queue3<6403>"),

        Extension_1000_VOICEMAIL("Voicemail test A<1000>"),
        Extension_1001_VOICEMAIL("Voicemail test2 B<1001>"),
        Extension_1004_VOICEMAIL("Voicemail t estX<1004>"),
        Extension_1005_VOICEMAIL("Voicemail First Last<1005>"),


        Conference0_6500("Conference Conference0<6500>"),

        Account_6700("6700<6700>");

        private final String alias;
        CDRNAME(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {

            return this.alias;
        }
    }


    /**
     * cdr field status
     */
    public enum STATUS {
        ANSWER("ANSWERED"),
        NO_ANSWER("NO ANSWER"),
        BUSY("BUSY"),
        VOICEMAIL("VOICEMAIL");


        private final String alias;
        STATUS(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field COMMUNICATION TYPE
     */
    public enum COMMUNICATION_TYPE {
        INBOUND("Inbound"),
        OUTBOUND("Outbound"),
        INTERNAL("Internal");

        private final String alias;
        COMMUNICATION_TYPE(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }
}
