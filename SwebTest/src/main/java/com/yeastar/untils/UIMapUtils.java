package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import top.jfunc.json.impl.JSONObject;

import java.io.File;
import java.io.IOException;

@Log4j2
public class UIMapUtils {
    private static String FILE_NAME = "en.ts";
    private static String FILE_PATH = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+"p_language"+File.separator;
    private static String CI_FILE_PATH = System.getProperty("user.dir")+File.separator+"test-classes"+File.separator+"p_language"+File.separator;


    /**
     * 从语言文件en.ts中 根据key提取对应的value，en.ts默认放在根目录
     * @param key
     * @return
     * @throws IOException
     */
    public static String getValueByKey(String key) throws IOException {

        File file = getSourceFile();
        String jsonString = FileUtils.readFileToString(file);
        JSONObject jsonObject = new JSONObject(jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}")+1));

        if(jsonObject.containsKey(key)){
//            log.debug("[UIMap:]: find key "+ key );
            return jsonObject.getString(key);
        }else{
            if(!key.isEmpty()) {
                log.info("[UIMap :] can not find key: " + key);
            }
            return key;
        }
    }

    public static JSONObject getUIMapHandle(){
        File file = getSourceFile();
        JSONObject jsonObject;

        String jsonString = null;
        try {
            jsonString = FileUtils.readFileToString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject = new JSONObject(jsonString.substring(jsonString.indexOf("{"), jsonString.lastIndexOf("}")+1));
        return  jsonObject;
    }


    public static File getSourceFile(){
        File file = null;
        file=new File(FILE_PATH+FILE_NAME);
        if(!file.exists()){
//            log.error("Local 文案文件en.ts不存在，请检查文件是否在项目根目录-->"+FILE_PATH+FILE_NAME);
            file = new File(CI_FILE_PATH+FILE_NAME);
            if(!file.exists()){
                log.error("CI 文案文件en.ts不存在，请检查文件是否在项目根目录-->"+CI_FILE_PATH+FILE_NAME);
            }
        }
        return file;
    }
}
