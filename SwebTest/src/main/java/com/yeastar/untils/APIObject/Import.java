package com.yeastar.untils.APIObject;

import com.csvreader.CsvReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Import {

    /**
     * 读取CSV文件内容
     * @return
     */
//    public static ArrayList<String[]> readCsvFile(String filePath){
//        try {
//            ArrayList<String[]> csvList = new ArrayList<String[]>();
//            CsvReader reader = new CsvReader(filePath, ',', Charset.forName("GBK"));
//            reader.readHeaders();
//            while (reader.readRecord()){
//                csvList.add(reader.getValues());
//            }
//            reader.close();
//            return csvList;
//            System.out.println("读取的行数：" + csvList.size());
//
//            for (int row=0;row<csvList.size();row++){
//                System.out.println("--------------");
//                System.out.println(csvList.get(row)[0]+",");
//                System.out.println(csvList.get(row)[1]+",");
//                System.out.println(csvList.get(row)[2]+",");
//            }


//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        return null;
//        return null;
//    }
}
