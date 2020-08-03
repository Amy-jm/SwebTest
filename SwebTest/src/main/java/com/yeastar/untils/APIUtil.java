package com.yeastar.untils;
import com.yeastar.untils.APIObject.ExtensionObject;
import com.yeastar.untils.APIObject.InboundRouteObject;
import com.yeastar.untils.APIObject.OutBoundRouteObject;
import com.yeastar.untils.APIObject.TrunkObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log.output.ServletOutputLogTarget;
import org.apache.xerces.xs.StringList;
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
        if (!jsonObject.getInteger("errcode").equals(0)){
            log.error("[loginWebClient: ]分机"+num+"首次登录失败,errmsg:"+jsonObject.getString("errmsg"));
        }
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
            System.out.println("DDDDDDDDDDD "+respone);
            JSONObject responeObject = new JSONObject(respone);
            if (!responeObject.getInteger("errcode").equals(0)){
                log.error("[loginWebClient: ]"+"分机"+num+"默认密码修改失败,errormsg："+responeObject.getString("errmsg"));
            }else{
                //密码修改成功，重新登录
                request = "https://"+DEVICE_IP_LAN+":8088/api/v1.0/login?username="+num+"&password="+enBase64(DigestUtils.md5Hex(newPassword));
                respone = m_getRequest(request);
                JSONObject jsonObject1 = new JSONObject(respone);
                if (!jsonObject1.getInteger("errcode").equals(0) || jsonObject1.containsKey("need_confirm_new_gdpr") || jsonObject1.containsKey("need_modify_default_pwd")){
                    log.error("[loginWebClient: ] 分机"+num+"密码修改失败,errmsg:"+jsonObject1.getString("errmsg"));
                }
            }
        }


        return this;
    }

    /**
     * 读CDR的API接口
     * @param num  要获取最新的几条CDR几率，从0开始
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
            log.error("[API getExtensionSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
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
        deleteExtension(list);
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
     * 获取分机概要列表
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
            log.error("[API getTrunkSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
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
            log.error("[API getInboundSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
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
        deleteInbound(list);
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

        ExtensionObject ext = getExtensionSummary(destValue);

        String request = String.format("{\"name\":\"%s\",\"did_option\":\"patterns\",\"did_pattern_to_ext\":\"\",\"did_to_ext_start\":\"\",\"did_to_ext_end\":\"\",\"cid_option\":\"patterns\",\"phonebook\":\"\",\"def_dest\":\"extension\",\"def_dest_prefix\":\"\",\"def_dest_value\":\"%s\",\"def_dest_ext_list\":[],\"enb_time_condition\":0,\"time_condition\":\"global\",\"office_time_dest\":\"end_call\",\"office_time_dest_ext_list\":[],\"office_time_dest_prefix\":\"\",\"office_time_dest_value\":\"\",\"outoffice_time_dest\":\"end_call\",\"outoffice_time_dest_prefix\":\"\",\"outoffice_time_dest_value\":\"\",\"outoffice_time_dest_ext_list\":[],\"holiday_dest\":\"end_call\",\"holiday_dest_ext_list\":[],\"holiday_dest_prefix\":\"\",\"holiday_dest_value\":\"\",\"enb_fax_detect\":0,\"fax_dest\":\"extension\",\"fax_dest_value\":\"\",\"trunk_list\":%s,\"did_pattern_list\":[],\"cid_pattern_list\":[],\"office_time_list\":[]}"
        ,name,String.valueOf(ext.id) ,jsonArray.toString());
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
            log.error("[API getOutboundSummary] ,errmsg: "+ jsonObject.getString("errmsg"));
        }
        return extObjList;
    }

    /**
     * 删除当前存在的所有分机
     * */
    public APIUtil deleteAllOutbound(){
        List<OutBoundRouteObject> OutboundObjectList = getOutboundSummary();

        List<Integer> list = new ArrayList<>();
        for(OutBoundRouteObject object : OutboundObjectList){
            list.add(object.id);
        }
        deleteOutbound(list);
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
    public  APIUtil createOutbound(String name, List<String> trunks, List<String> extensions){

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
        return responeData;
    }

    /**
     * 执行Post请求,此函数是私有函数，外部调用请用 postRequest，它包含登录信息
     * @param urlpath
     * @return
     */
    private String m_postRequest(String urlpath,String args1) {
        log.debug("postRequest cmd: "+urlpath + "body: "+args1);
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
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

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
