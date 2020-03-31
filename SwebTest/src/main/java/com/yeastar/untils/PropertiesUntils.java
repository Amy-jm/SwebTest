package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by user on 2018/1/11.
 */
@Log4j2
public class PropertiesUntils {
    private static Properties prop;
    private static String PNG_KEY="png.path";
    private static String APP_KEY="app.path";
    private static String properiesName = "";
    public PropertiesUntils(){

    }
    public PropertiesUntils(String resourcePropetties){
        prop = new Properties();
        properiesName =  resourcePropetties;
        URL props = ClassLoader.getSystemResource(resourcePropetties);
        try {
            prop.load(props.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PropertiesUntils parseProperties = null;

    /**
     * Properties 实例
     * @return
     */
    public static PropertiesUntils getInstance(){
        if(parseProperties == null){
            parseProperties = new PropertiesUntils();
        }
        return parseProperties;
    }

    /**
     * 获取prop文件，prop需要在资源文件路径下
     * @param propPath
     * @return
     */
    public Properties getPropertie(String propPath) {
         prop = new Properties();
         BufferedReader br=null;
        try {
             br=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(propPath), "UTF-8"));
             prop.load(br);
        } catch (Exception e) {
            log.error("读取配置文件异常！"+e);
        }   finally {
            try{
                if(br!=null){
                    br.close();
                }
            }catch (IOException e){
                log.error("properties 文件关闭异常！"+e);
            }
        }
        return prop;
    }

    /**
     * 获取prop资源文件
     * @param propPath
     * @return
     */
    public static Properties getProperties(String propPath) {
        Properties prop = new Properties();
        try {
            FileInputStream file = new FileInputStream(propPath);
            prop.load(file);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }


    /**
     * 获取prop值，通过key
     * @param key
     * @return
     */
    public static String getPropertiesValue(String key){
        String value="";
        if(prop.containsKey(key)){
            value = prop.getProperty(key);
        }else{
            log.error("can not find the key");
        }
        return value;
    }

    /**
     * 获取value，通过路径和key
     * @param propPath
     * @param key
     * @return
     */
    public String getPropertiesforLocation(String propPath, String key){
        prop=  getPropertie(propPath);
        String value="";
        String keyTemp="";
        String newKeyTemp="";
        //遍历KEY
        Set strKey = prop.keySet();
        for(Iterator it = strKey.iterator(); it.hasNext();){
            keyTemp = (String)it.next();
            newKeyTemp = keyTemp.split("\\.")[0];
//            if(keyTemp.contains(key)){ //因现在四种案例的命名，太相近，固不能用包含的用法
            if(newKeyTemp.equals(key) || newKeyTemp == key){
                value =prop.getProperty(keyTemp);
                break;
            }
        }
        return keyTemp+"="+value;
    }

    /**
     * 获取完整的元素定位
     * @param propPath
     * @param key
     * @return
     */
    public String getFullKey(String propPath, String key){
        prop=  getPropertie(propPath);
        String keyTemp="";
        //遍历KEY
        Set strKey = prop.keySet();
        for(Iterator it = strKey.iterator(); it.hasNext();){
            keyTemp = (String)it.next();
            if(keyTemp.contains(key)){
                break;
            }
        }
        return keyTemp;
    }

    /**
     * 获取key值
     * @param propPath
     * @param key
     * @return
     */
    public String getPropertiesValue(String propPath, String key){
        prop=  getPropertie(propPath);
        //System.out.println("propPath:"+propPath);
        String value="";
        if(prop.containsKey(key)){
            value = prop.getProperty(key);
        }else{
            log.error("can not find the key");
        }
        return value;
    }

    /**
     * 读取配置文件config获取图片
     * @param CorPath
     * @return
     */
    public static String getpngPathforPropertesPngPath(String CorPath) {
        File directory = new File("");
        String courseFile = null;
        try {
            courseFile = directory.getCanonicalPath() + File.separator+ getPropertiesValue(PNG_KEY)+ File.separator+ CorPath;
           log.debug("[Get png path]"+courseFile);
        } catch (IOException e) {
            log.error("[ERROR] 读取失败"+e);
        }
        return courseFile;
    }

    /**
     * 获取图片
     * @param pngPath
     * @param pngName
     * @return
     */
    public static String getpngPath(String pngPath, String pngName) {
        File directory = new File("");
        String courseFile = null,temp= null,startString= null,endString = null;
        //ministrator\.jenkins\jobs\SinoTrans_3Times_Client_Verification\workspace\target\images\sino\pages\loginpage\login_name.png
        temp = System.getProperty("user.dir") + File.separator+ pngPath+ File.separator+pngName;
        if(temp.contains("target")){
            int startIndex = temp.indexOf("target");
            startString=temp.substring(0,startIndex);
            endString = temp.substring(startIndex+7);
            courseFile=startString+endString;
            log.debug("startString="+startString);
            log.debug("endString="+endString);
            log.debug("courseFile="+courseFile);
        }else{
            courseFile=temp;
        }
        log.debug("courseFile="+courseFile);
        return courseFile;
    }

    /**
     * 获取项目路径  user.dir
     * 文件路径过滤，在jenkins上执行会多target路径
     * 删除target路径
     */
    public String getUserDirPath(){
        String path = System.getProperty("user.dir");
        String temp = System.getProperty("user.dir");
        if(temp.contains("target")) {
            int endString = temp.indexOf("target");
            path=temp.substring(0,endString);
            log.debug("endString=" + endString);
        }
        return path;
    }

    public  void updatePropertiess(Map<String, String> keyValueMap, String path){
        //1.先实例化一个Properties对象
        Properties properties = new Properties();

        System.out.println(this.getClass().getResourceAsStream(path));//输出数据为:/E:/Idea_workspace/JFinal/TestSomething/out/production/TestSomething/test2.properties
        //注意:这里获取到的路径,为文件编译之后的路径,即class路径,如果用path来获取File文件的话,则修改的为classes中的文件,编译前的原文件是没有改变的,所以上服务器之后,应该使用path,才可以改变需要修改的文件

        //2.新建一个输入流和输出流,用来读取和写入文件
        InputStreamReader reader = null;
        OutputStreamWriter writer = null;
        try {
            //3.读取到配置文件,并加载在Properties中
            //reader = new InputStreamReader(new FileInputStream(path),"utf-8"); 读取的为classes中的配置文件
            reader = new InputStreamReader(this.getClass().getResourceAsStream(path),"utf-8");
            properties.load(reader);
            System.out.println("testWrite:" + properties.get("testWrite"));

            //4.将需要修改的键值对,或者新增的键值对,写入到properties中(此处与直接追加操作相同)

            writer = new OutputStreamWriter(new FileOutputStream(getUserDirPath()+ File.separator+"src"+ File.separator+"test"+ File.separator+"resources"+path),"utf-8");
            for (String key: keyValueMap.keySet()) {
                properties.setProperty(key,keyValueMap.get(key));
            }
            //5.调用properties中的存储方法
            properties.store(writer," update properties ");//注释信息为空，无法为每一个key-value提供注释

            //6.关闭资源
            reader.close();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
