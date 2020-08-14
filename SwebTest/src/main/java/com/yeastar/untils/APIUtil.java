package com.yeastar.untils;
import com.yeastar.untils.APIObject.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log.output.ServletOutputLogTarget;
import org.apache.xerces.xs.StringList;
import org.testng.Assert;
import top.jfunc.json.JsonArray;
import top.jfunc.json.JsonObject;
import top.jfunc.json.impl.JSONArray;
import top.jfunc.json.impl.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.swebtest.driver.DataReader2.*;
import static org.apache.log4j.spi.Configurator.NULL;


@SuppressWarnings("ALL")
@Log4j2
public class APIUtil {

    private  String webSession = NULL;

    /**
     * 登录aip，获取cookie
     * @return
     */
    public APIUtil loginWeb(){

        String req = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/login?username="+LOGIN_USERNAME+"&password="+enBase64(DigestUtils.md5Hex(LOGIN_PASSWORD));
        m_getRequest(req);
        return this ;
    }

    /**
     * 分机登录web client页面
     * @param num
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public APIUtil loginWebClient(String num, String oldPassword, String newPassword){

        //登录
        String request = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/login?username="+num+"&password="+enBase64(DigestUtils.md5Hex(oldPassword));
        String respone = m_getRequest(request);

        JSONObject jsonObject = new JSONObject(respone);
        Assert.assertEquals(String.valueOf(0),jsonObject.getString("errcode"),"[loginWebClient: ]分机"+num+"首次登录失败,errmsg:"+jsonObject.getString("errmsg"));

        //判断是否需要确认隐私协议
        if(jsonObject.containsKey("need_confirm_new_gdpr") && jsonObject.getInteger("need_confirm_new_gdpr") == 1){
            m_postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/user/confirmgdpr","{}");
        }

        //判断是否需要修改密码
        if(jsonObject.containsKey("need_modify_default_pwd") && jsonObject.getInteger("need_modify_default_pwd") == 1){
            JSONObject postJson = new JSONObject();
            postJson.put("old_password",enBase64(DigestUtils.md5Hex(oldPassword)));
            postJson.put("new_password",enBase64(DigestUtils.md5Hex(newPassword)));
            postJson.put("confirm_password",enBase64(DigestUtils.md5Hex(newPassword)));
            respone = m_postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/user/updatepassword",postJson.toString());

            JSONObject responeObject = new JSONObject(respone);
            if (!responeObject.getInteger("errcode").equals(0)){
                Assert.fail("[loginWebClient: ]"+"分机"+num+"默认密码修改失败,errormsg："+responeObject.getString("errmsg"));
            }else{
                //密码修改成功，重新登录
                request = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/login?username="+num+"&password="+enBase64(DigestUtils.md5Hex(newPassword));
                respone = m_getRequest(request);
                JSONObject jsonObject1 = new JSONObject(respone);
                if (!jsonObject1.getInteger("errcode").equals(0) || jsonObject1.containsKey("need_confirm_new_gdpr") || jsonObject1.containsKey("need_modify_default_pwd")){
                    Assert.fail("[loginWebClient: ] 分机"+num+"密码修改失败,errmsg:"+jsonObject1.getString("errmsg"));
                }
            }
        }
        return this;
    }

    /**
     * 更新WebClient页面的Preferences
     * @param request
     * @return
     */
    public APIUtil updatePersonal(String request){
        m_postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extension/updatepersonal",request);
        return this;
    }
    /**
     * 读CDR的API接口
     * @param num  要获取最新的num条CDR几率，从0开始
     * @return 返回List类型的CDRObject对象，List里的对象数量和num一致
     * @throws IOException
     */
    public  List<CDRObject> getCDRRecord(int num) throws IOException {
        String req = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/cdr/search?page=1&page_size="+(num+1)+"&sort_by=id&order_by=desc";
        String respondJson = getRequest(req);

        List<CDRObject> cdrList = new ArrayList<>();
        for (int k=0; k < num; k++){
            CDRObject p = new CDRObject(respondJson, k);
            cdrList.add(p);
        }

        return cdrList;
    }

    /**
     * apply 接口，apply发送后，判断设备apply状态，直到status参数消失，表示apply完成
     * @return
     */
    public boolean apply(){

        String response = m_getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/pbx/apply");
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInteger("errcode") == 0){
            while (true){
                response = m_getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/pbx/getapplystatus");
                jsonObject = new JSONObject(response);
                if(!jsonObject.containsKey("status")){
                    sleep(WaitUntils.SHORT_WAIT);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 创建时需要获取的初始化参数
     * @param arg ，根据需要创建的事件填写，分机相关：extension,队列相关：queue 以此类推
     *       有效参数：personal extension park vm_group ivr ring_group queue conference paging account random_pwd
     * @return
     */
    public JsonObject getInitialdata(String arg){

        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/initialdata/get?menu="+arg);
        JSONObject jsonObject = new JSONObject(jsonString);

        return jsonObject.getJsonObject("initial_data");
    }

    /**
     * 获取分机概要列表
     * 对应API：api/v1.0/extension/searchsummary
     */
    public List<ExtensionObject> getExtensionSummary(){

        List<ExtensionObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extension/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("extension_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("extension_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new ExtensionObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getExtensionSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 找到指定分机信息
     * @param num  分机号
     * @return
     */
    public ExtensionObject getExtensionSummary(String num){
        List<ExtensionObject> extensionObjectList = getExtensionSummary();
        for (ExtensionObject object : extensionObjectList){
            if(object.number.equals(num)){
                return object;
            }
        }
        return null;
    }

    /**
     * 删除当前存在的所有分机
     * */
    public APIUtil deleteAllExtension(){
        List<ExtensionObject> extensionObjectList = getExtensionSummary();

        List<Integer> list = new ArrayList<>();
        for(ExtensionObject object : extensionObjectList){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteExtension(list);
        }
        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/extension/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteExtension(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extension/batchdelete",jsonObject.toString());

        return this;
    }

    /**
     * 创建分机，
     * @param request  请求包中的完整body赋值给request
     */
    public APIUtil createExtension(String request){
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extension/create",request);
        return this;
    }

    /**
     * 获取分机组概要列表
     * 对应API：api/v1.0/extension/searchsummary
     */
    public List<ExtensionGroupObject> getExtensionGroupSummary(){

        List<ExtensionGroupObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extensiongroup/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("extension_group_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("extension_group_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new ExtensionGroupObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getExtensionSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 删除当前存在的所有分机组
     * */
    public APIUtil deleteAllExtensionGroup(){
        List<ExtensionGroupObject> extensionGroupObjects = getExtensionGroupSummary();

        List<Integer> list = new ArrayList<>();
        for(ExtensionGroupObject object : extensionGroupObjects){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteExtensionGroup(list);
        }
        return this;
    }

    /**
     * 通过分机组的ID删除指定分机组
     * 对应接口：/api/v1.0/extensiongroup/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteExtensionGroup(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extensiongroup/batchdelete",jsonObject.toString());

        return this;
    }

    /**
     * 创建分机，
     * @param request  请求包中的完整body赋值给request
     */
    public APIUtil createExtensionGroup(String request){
        //获取默认分机组
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/extensiongroup/create",request);
        return this;
    }

    /**
     * 获取Trunk概要列表
     * 对应API：api/v1.0/extension/searchsummary
     */
    public List<TrunkObject> getTrunkSummary(){

        List<TrunkObject> trunkObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/trunk/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("trunk_list"))
                return trunkObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("trunk_list");
            for (int i=0; i<jsonArray.size(); i++){
                trunkObjList.add(new TrunkObject((JSONObject) jsonArray.getJsonObject(i)));
            }
        }else {
            Assert.fail("[API getTrunkSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return trunkObjList;
    }

    /**
     * 通过Trunk的ID删除
     * 对应接口：/api/v1.0/trunk/batchdelete
     * @param trunkName  Trunk的名字
     */
    public APIUtil deleteTrunk(String trunkName){

        List<TrunkObject> list = getTrunkSummary();
        for (int i=0; i<list.size(); i++){
            if(list.get(i).name.equals(trunkName)){
                getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/trunk/delete?id="+list.get(i).id);
                break;
            }
        }
        return this;
    }

    /**
     * 创建SIP Trunk
     */
    public  APIUtil createSIPTrunk(String request){
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/siptrunk/create",request);
        return this;
    }


    /**
     * 获取呼出路由概要列表
     * 对应API：api/v1.0/inboundroute/searchsummary
     */
    public List<InboundRouteObject> getInboundSummary(){

        List<InboundRouteObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/inboundroute/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("inbound_route_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("inbound_route_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new InboundRouteObject((JSONObject) jsonArray.getJsonObject(i)));
            }
        }else {
            Assert.fail("[API getInboundSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 删除当前存在的所有分机
     * */
    public APIUtil deleteAllInbound(){
        List<InboundRouteObject> inboundObjectList = getInboundSummary();

        List<Integer> list = new ArrayList<>();
        for(InboundRouteObject object : inboundObjectList){
            list.add(object.id);
        }
        if (!list.isEmpty()) {
            deleteInbound(list);
        }
        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/inboundroute/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteInbound(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/inboundroute/batchdelete",jsonObject.toString());
        return this;
    }

    /**
     * 创建呼入路由
     * @param request
     * @param trunks
     */
    public  APIUtil createInbound(String name, List<String> trunks, String dest, String destValue){
        List<TrunkObject> trunkObjects = getTrunkSummary();
        JSONArray jsonArray = new JSONArray();
        String request = "";
        String id = "";

        for (int i=0; i<trunks.size(); i++){
            for (int j=0; j<trunkObjects.size(); j++){
                if (trunks.get(i).equals(trunkObjects.get(j).name)){
                    JSONObject a = new JSONObject();
                    a.put("text",trunkObjects.get(j).name);
                    a.put("value",String.valueOf(trunkObjects.get(j).id));
                    a.put("type",trunkObjects.get(j).type);
                    jsonArray.put(a);
                }
            }
        }
        if(dest.equalsIgnoreCase("extension")){
            ExtensionObject ext = getExtensionSummary(destValue);
            id = String.valueOf(ext.id);
        } else if(dest.equalsIgnoreCase("ring_group")){
            RingGroupObject ext = getRingGroupSummary(destValue);
            id = String.valueOf(ext.id);
        }else if(dest.equalsIgnoreCase("queue")){
            QueueObject ext = getQueueSummary(destValue);
            id = String.valueOf(ext.id);
        }
        request = String.format("{\"name\":\"%s\",\"did_option\":\"patterns\",\"did_pattern_to_ext\":\"\",\"did_to_ext_start\":\"\",\"did_to_ext_end\":\"\",\"cid_option\":\"patterns\",\"phonebook\":\"\",\"def_dest\":\"%s\",\"def_dest_prefix\":\"\",\"def_dest_value\":\"%s\",\"def_dest_ext_list\":[],\"enb_time_condition\":0,\"time_condition\":\"global\",\"office_time_dest\":\"end_call\",\"office_time_dest_ext_list\":[],\"office_time_dest_prefix\":\"\",\"office_time_dest_value\":\"\",\"outoffice_time_dest\":\"end_call\",\"outoffice_time_dest_prefix\":\"\",\"outoffice_time_dest_value\":\"\",\"outoffice_time_dest_ext_list\":[],\"holiday_dest\":\"end_call\",\"holiday_dest_ext_list\":[],\"holiday_dest_prefix\":\"\",\"holiday_dest_value\":\"\",\"enb_fax_detect\":0,\"fax_dest\":\"extension\",\"fax_dest_value\":\"\",\"trunk_list\":%s,\"did_pattern_list\":[],\"cid_pattern_list\":[],\"office_time_list\":[]}"
                ,name,dest.toLowerCase(),id ,jsonArray.toString());
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/inboundroute/create",request);
        return this;
    }

    /**
     * 获取概要列表
     * 对应API：api/v1.0/outboundroute/searchsummary
     */
    public List<OutBoundRouteObject> getOutboundSummary(){

        List<OutBoundRouteObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/outboundroute/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("outbound_route_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("outbound_route_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new OutBoundRouteObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getOutboundSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 删除当前存在的所有呼出路由
     * */
    public APIUtil deleteAllOutbound(){
        List<OutBoundRouteObject> OutboundObjectList = getOutboundSummary();

        List<Integer> list = new ArrayList<>();
        for(OutBoundRouteObject object : OutboundObjectList){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteOutbound(list);
        }
        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/outboundroute/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteOutbound(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/outboundroute/batchdelete",jsonObject.toString());
        return this;
    }

    /**
     * 创建呼出路由
     * @param request
     */
    public APIUtil createOutbound(String name, List<String> trunks, List<String> extensions){

        List<TrunkObject> trunkObjects = getTrunkSummary();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();

        for (int i=0; i<trunks.size(); i++){
            for (int j=0; j<trunkObjects.size(); j++){
                if (trunks.get(i).equals(trunkObjects.get(j).name)){
                    JSONObject a = new JSONObject();
                    a.put("text",trunkObjects.get(j).name);
                    a.put("value",String.valueOf(trunkObjects.get(j).id));
                    a.put("type",trunkObjects.get(j).type);
                    jsonArray.put(a);
                }
            }
        }


        List<ExtensionObject> extensionObjects = getExtensionSummary();
        for (String ext : extensions){
             for (ExtensionObject extensionObject: extensionObjects) {
                if (ext.equals(extensionObject.number)){
                    JSONObject a = new JSONObject();
                    a.put("text2",extensionObject.number);
                    a.put("value",String.valueOf(extensionObject.id));
                    a.put("type","extension");
                    jsonArray2.put(a);
                }
            }
        }

        String request = String.format("{\"name\":\"%s\",\"outb_cid\":\"\",\"enb_rrmemory_hunt\":0,\"pin_protect\":\"disable\",\"pin\":\"\",\"pin_list\":\"\",\"available_time\":\"always\",\"enb_office_time\":1,\"enb_out_of_office_time\":0,\"enb_holiday\":0,\"trunk_list\":%s,\"ext_list\":%s,\"role_list\":[],\"dial_pattern_list\":[],\"office_time_list\":[]}"
                ,name,jsonArray.toString() ,jsonArray2.toString());

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/outboundroute/create",request);
        return this;
    }

    /**
     * 获取概要列表
     * 对应API：api/v1.0/ringgroup/searchsummary
     */
    public List<RingGroupObject> getRingGroupSummary(){

        List<RingGroupObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/ringgroup/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("ring_group_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("ring_group_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new RingGroupObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getRingGroupSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }
    /**
     * 找到指定RingGroup
     * @param num  分机号
     * @return
     */
    public RingGroupObject getRingGroupSummary(String num){
        List<RingGroupObject> ringGroupObject = getRingGroupSummary();
        for (RingGroupObject object : ringGroupObject){
            if(object.number.equals(num)){
                return object;
            }
        }
        return null;
    }

    /**
     * 删除当前存在的所有响铃组
     * */
    public APIUtil deleteAllRingGroup(){
        List<RingGroupObject> ringGroupObjects = getRingGroupSummary();

        List<Integer> list = new ArrayList<>();
        for(RingGroupObject object : ringGroupObjects){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteRingGroup(list);
        }
        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/ringgroup/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteRingGroup(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/ringgroup/batchdelete",jsonObject.toString());
        return this;
    }

    /**
     * 创建呼出路由
     * @param request
     */
    public APIUtil createRingGroup(String name,String number, List<String> extensions){
        JSONArray jsonArray = new JSONArray();

        List<ExtensionObject> extensionObjects = getExtensionSummary();
        for (String ext : extensions){
            for (ExtensionObject extensionObject: extensionObjects) {
                if (ext.equals(extensionObject.number)){
                    JSONObject a = new JSONObject();
                    a.put("text",extensionObject.callerIdName);
                    a.put("text2",extensionObject.number);
                    a.put("value",String.valueOf(extensionObject.id));
                    a.put("type","extension");
                    jsonArray.put(a);
                }
            }
        }

        String request = String.format("{\"number\":\"%s\",\"name\":\"%s\",\"member_list\":%s,\"ring_strategy\":\"ring_all\",\"ring_timeout\":60,\"fail_dest\":\"end_call\",\"fail_dest_prefix\":\"\",\"fail_dest_value\":\"\"}"
                ,number,name,jsonArray.toString() );

        String respone = postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/ringgroup/create",request);

        JSONObject jsonObject = new JSONObject(respone);
        Assert.assertEquals( String.valueOf(0), jsonObject.getString("errcode"),"[createRingGroup: ]响铃组 num="+number+"创建失败,errmsg:"+jsonObject.getString("errmsg"));
        return this;
    }


    /**
     * 获取概要列表
     * 对应API：api/v1.0/conference/searchsummary
     */
    public List<ConferenceObject> getConferenceSummary(){

        List<ConferenceObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/conference/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("conference_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("conference_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new ConferenceObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getConferenceSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 删除当前存在的所有响铃组
     * */
    public APIUtil deleteAllConference(){
        List<ConferenceObject> conferenceObjects = getConferenceSummary();

        List<Integer> list = new ArrayList<>();
        for(ConferenceObject object : conferenceObjects){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteConference(list);
        }
        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/ringgroup/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteConference(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/conference/batchdelete",jsonObject.toString());
        return this;
    }

    /**
     * 创建呼出路由
     * @param request
     */
    public APIUtil createConference(String name,String number, List<String> extensions){
        JSONArray jsonArray = new JSONArray();

        List<ExtensionObject> extensionObjects = getExtensionSummary();
        for (String ext : extensions){
            for (ExtensionObject extensionObject: extensionObjects) {
                if (ext.equals(extensionObject.number)){
                    JSONObject a = new JSONObject();
                    a.put("text",extensionObject.callerIdName);
                    a.put("text2",extensionObject.number);
                    a.put("value",String.valueOf(extensionObject.id));
                    a.put("type","extension");
                    jsonArray.put(a);
                }
            }
        }

        String request = String.format("{\"number\":\"%s\",\"name\":\"%s\",\"partic_password\":\"\",\"moderator_password\":\"\",\"sound_prompt\":\"default\",\"enb_wait_moderator\":0,\"allow_partic_invite\":1,\"moderator_list\":%s}"
                ,number,name,jsonArray.toString() );

        String respone = postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/conference/create",request);

        JSONObject jsonObject = new JSONObject(respone);
        Assert.assertEquals( String.valueOf(0), jsonObject.getString("errcode"),"[createConference: ]响铃组 num="+number+"创建失败,errmsg:"+jsonObject.getString("errmsg"));
        return this;
    }


    /**
     * 获取概要列表
     * 对应API：api/v1.0/queue/searchsummary
     */
    public List<QueueObject> getQueueSummary(){

        List<QueueObject> extObjList = new ArrayList<>();
        String jsonString = getRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/queue/searchsummary?page=1&page_size=10&sort_by=id&order_by=asc");
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject.getString("errcode").equals("0")){

            if(!jsonObject.containsKey("queue_list"))
                return extObjList;

            JsonArray jsonArray = jsonObject.getJsonArray("queue_list");
            for (int i=0; i<jsonArray.size(); i++){
                extObjList.add(new QueueObject((JSONObject) jsonArray.getJsonObject(i)));
            }

        }else {
            Assert.fail("[API getQueueSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }
    /**
     * 找到指定坐席
     * @param num  分机号
     * @return
     */
    public QueueObject getQueueSummary(String num){
        List<QueueObject> queueObject = getQueueSummary();
        for (QueueObject object : queueObject){
            if(object.number.equals(num)){
                return object;
            }
        }
        return null;
    }


    /**
     * 删除当前存在的所有响铃组
     * */
    public APIUtil deleteAllQueue(){
        List<QueueObject> queueObjects = getQueueSummary();

        List<Integer> list = new ArrayList<>();
        for(QueueObject object : queueObjects){
            list.add(object.id);
        }
        if(list != null && !list.isEmpty()){
            deleteQueue(list);
        }

        return this;
    }

    /**
     * 通过分机的ID删除指定分机
     * 对应接口：/api/v1.0/queue/batchdelete
     * @param idLsit  int类型的id组成的list
     */
    public APIUtil deleteQueue(List<Integer> idLsit){

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id_list",idLsit);

        JSONObject jsonObject = (JSONObject) new JSONObject().fromMap(map);

        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/queue/batchdelete",jsonObject.toString());
        return this;
    }

    /**
     * 创建呼出路由
     * @param request
     */
    public APIUtil createQueue(String name,String number, List<String> dynamicAgentList, List<String> staticAgentList, List<String> managerList){
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONArray jsonArray3 = new JSONArray();

        List<ExtensionObject> extensionObjects = getExtensionSummary();

        for (ExtensionObject extensionObject: extensionObjects) {
            if(dynamicAgentList != null && !dynamicAgentList.isEmpty()){
                for (String ext : dynamicAgentList){
                    if (ext.equals(extensionObject.number)){
                        JSONObject a = new JSONObject();
                        a.put("text",extensionObject.callerIdName);
                        a.put("text2",extensionObject.number);
                        a.put("value",String.valueOf(extensionObject.id));
                        a.put("type","extension");
                        jsonArray1.put(a);
                    }
                }
            }
            if(staticAgentList != null && !staticAgentList.isEmpty()) {
                for (String ext : staticAgentList) {
                    if (ext.equals(extensionObject.number)) {
                        JSONObject a = new JSONObject();
                        a.put("text", extensionObject.callerIdName);
                        a.put("text2", extensionObject.number);
                        a.put("value", String.valueOf(extensionObject.id));
                        a.put("type", "extension");
                        jsonArray2.put(a);
                    }
                }
            }
            if(managerList != null && !managerList.isEmpty()) {
                for (String ext : managerList) {
                    if (ext.equals(extensionObject.number)) {
                        JSONObject a = new JSONObject();
                        a.put("text", extensionObject.callerIdName);
                        a.put("text2", extensionObject.number);
                        a.put("value", String.valueOf(extensionObject.id));
                        a.put("type", "extension");
                        jsonArray3.put(a);
                    }
                }
            }
        }

        String request = String.format("{\"number\":\"%s\",\"name\":\"%s\",\"ring_strategy\":\"ring_all\",\"moh\":\"default\",\"max_wait_time\":1800,\"fail_dest\":\"end_call\",\"fail_dest_prefix\":\"\",\"fail_dest_value\":\"\",\"enb_group_vm\":0,\"agent_timeout\":30,\"retry_time\":30,\"wrap_up_time\":30,\"agent_prompt\":\"\",\"enb_ring_in_use\":0,\"dynamic_agent_list\":%s,\"static_agent_list\":%s,\"manager_list\":%s,\"enb_email_miss_call\":1,\"enb_email_abandon_call\":1,\"enb_email_sla_alarm\":1,\"max_calls\":0,\"enb_leave_empty\":1,\"enb_join_empty\":0,\"sla_time\":60,\"sla_interval\":30,\"sla_alarm_threshold\":80,\"join_prompt\":\"\",\"enb_announce_agent_id\":0,\"enb_announce_pos\":1,\"enb_announce_hold_time\":1,\"caller_announce_freq\":30,\"sys_announce_prompt\":\"\",\"sys_announce_freq\":60,\"satisfa_survey_prompt\":\"\",\"press_key\":\"\",\"key_dest\":\"end_call\",\"key_dest_value\":\"\",\"key_dest_prefix\":\"\",\"enb_mgr_agent_status\":1,\"enb_mgr_call_dist\":1,\"enb_mgr_call_conn\":1,\"enb_mgr_monitor\":1,\"enb_mgr_call_park\":1,\"enb_mgr_record\":1,\"enb_agent_call_dist\":1,\"enb_agent_call_conn\":1,\"enb_agent_call_park\":1}"
                ,number,name,jsonArray1.toString(),jsonArray2.toString(),jsonArray3.toString() );

        String respone = postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/queue/create",request);
        JSONObject jsonObject = new JSONObject(respone);
        Assert.assertEquals( String.valueOf(0), jsonObject.getString("errcode"),"[createQueue: ]队列 num="+number+"创建失败,errmsg:"+jsonObject.getString("errmsg"));

        return this;
    }

    /**
     * Preference 更新
     * @param request
     * @return
     */
    public APIUtil preferencesUpdate(String request){
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/preferences/update",request);
        return this;
    }

    /**
     * linkusServer 更新
     * @param request
     * @return
     */
    public APIUtil linkusserverUpdate(String request){
        postRequest("https://"+DEVICE_IP_LAN+":8088/api/v1.0/linkusserver/update",request);
        return this;
    }


    /**
     * 发送get请求
     * @param urlpath
     * @return
     */
    public String getRequest(String urlpath){
        loginWeb();
        return m_getRequest(urlpath);
    }

    /**
     * 执行Get请求,此函数是私有函数，外部调用请用 getRequest，它包含登录信息
     * @param urlpath
     * @return
     */
    private String m_getRequest(String urlpath) {
        log.debug("deGetRequest cmd: "+urlpath);
        HttpsURLConnection.setDefaultHostnameVerifier(new APIUtil().new NullHostNameVerifier());

        String responeData = null;
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL(urlpath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // 设置访问提交模式，表单提交
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("cookie","websession="+webSession);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if(conn.getResponseCode() == 200){
                String cookies = conn.getHeaderField("Set-Cookie");
                log.debug("[获取Cookies] "+ cookies);
                if(cookies != null){
                    webSession = cookies.split(";")[0].replace("websession=","");
                }
                InputStream inStream = conn.getInputStream();
                int inStreamLength = inStream.available();
                byte[] bytes = new byte[inStreamLength+1];
                inStream.read(bytes, 0, inStream.available());
                responeData = new String(bytes, "utf-8").trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("[GET result: ] "+responeData);
        JSONObject jsonObject = new JSONObject(responeData);
        if(jsonObject.getInteger("errcode") != 0){
            Assert.fail("[GET Request err:] errcode:"+jsonObject.getInteger("errcode")+" errmsg: "+jsonObject.getString("errmsg"));
        }
        return responeData;
    }

    /**
     * 执行Post请求,此函数是私有函数，外部调用请用 postRequest，它包含登录信息
     * @param urlpath
     * @return
     */
    private String m_postRequest(String urlpath,String args1) {
        log.debug("postRequest cmd: "+urlpath + "  body: "+args1);
        HttpsURLConnection.setDefaultHostnameVerifier(new APIUtil().new NullHostNameVerifier());

        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder responeData = new StringBuilder();
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL(urlpath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            // 设置访问提交模式，表单提交
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("cookie","websession="+webSession);
            // 发送POST请求必须设置如下两行   否则会抛异常（java.net.ProtocolException: cannot write to a URLConnection if doOutput=false - call setDoOutput(true)）
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);

            //获取URLConnection对象对应的输出流并开始发送参数
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            //添加参数
            out.write(args1);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                responeData.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("[POST responeData: ] "+responeData);

        JSONObject jsonObject = new JSONObject(String.valueOf(responeData));
        if(jsonObject.getInteger("errcode") != 0){
            Assert.fail("[POST Request err:] errcode:"+jsonObject.getInteger("errcode")+" errmsg: "+jsonObject.getString("errmsg"));
        }
        return String.valueOf(responeData);
    }
    /**
     * 执行Post请求
     * @param urlpath
     * @param args1
     * @return
     */
    public String postRequest(String urlpath,String args1) {
        loginWeb();
        return m_postRequest(urlpath,args1);
    }

     TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    };

    public class NullHostNameVerifier implements HostnameVerifier {
        /*
         * (non-Javadoc)
         * 
         * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
         * javax.net.ssl.SSLSession)
         */
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    /**
     * Base64加密字符串
     * @param str
     * @return
     */
    public  String enBase64(String str) {

        byte[] bytes = str.getBytes();
        String encoded = Base64.getEncoder().encodeToString(bytes);
        return encoded;
    }
}
