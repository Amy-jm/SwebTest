package com.yeastar.linkustest.tools.file;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GaGa on 2017-03-29.
 */
public class ExcelUnit {
    public Workbook workbook;
    public Sheet sheet;
    public Cell cell;
    int rows;
    int columns;
    public String fileName;
    public String caseName;
    public ArrayList arrkey = new ArrayList<>();
    String sourceFile;

    /**
     * @param fileName   excel文件名
     * @param caseName   sheet名
     */
    public ExcelUnit(String fileName, String caseName) {
        super();
        this.fileName = fileName;
        this.caseName = caseName;
    }

    /**
     * 获得excel表中的数据
     */
    public Object[][] getExcelData() throws BiffException, IOException {
        workbook = Workbook.getWorkbook(new File(getPath()));
        sheet = workbook.getSheet(caseName);
        rows = sheet.getRows();
        columns = sheet.getColumns();
        // 为了返回值是Object[][],定义一个多行单列的二维数组
        HashMap<Object, String>[][] arrmap = new HashMap[rows - 1][1];
        // 对数组中所有元素hashmap进行初始化
        if (rows > 1) {
            for (int i = 0; i < rows - 1; i++) {
                arrmap[i][0] = new HashMap<>();
            }
        } else {
            System.out.println("excel中没有数据");
        }

        // 获得首行的列名，作为hashmap的key值
        for (int c = 0; c < columns; c++) {
            String cellvalue = sheet.getCell(c, 0).getContents();
            arrkey.add(cellvalue);
        }
        // 遍历所有的单元格的值添加到hashmap中
        for (int r = 1; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                String cellvalue = sheet.getCell(c, r).getContents();
                arrmap[r - 1][0].put(arrkey.get(c), cellvalue);
            }
        }
        return arrmap;
    }

//    获取指定行的值
    public Object[][] getExcelData(int row) throws BiffException, IOException {
        workbook = Workbook.getWorkbook(new File(getPath()));
        sheet = workbook.getSheet(caseName);
        rows = sheet.getRows();
        columns = sheet.getColumns();
        // 为了返回值是Object[][],定义一个多行单列的二维数组
        HashMap<Object, String>[][] arrmap = new HashMap[1][1];
        // 对数组中所有元素hashmap进行初始化
        if (row > 0 ) {
            arrmap[0][0] = new HashMap<>();
        } else {
            System.out.println("excel中没有数据");
        }

        // 获得首行的列名，作为hashmap的key值
        for (int c = 0; c < columns; c++) {
            String cellvalue = sheet.getCell(c, 0).getContents();
            arrkey.add(cellvalue);
        }
        // 遍历所有的单元格的值添加到hashmap中
        for (int c = 0; c < columns; c++) {
            String cellvalue = sheet.getCell(c, row).getContents();
            arrmap[0][0].put(arrkey.get(c), cellvalue);
        }
        return arrmap;
    }

    //    获取指定列的值
    public Object[][] getExcelDataColumn(int column) throws BiffException, IOException {
        workbook = Workbook.getWorkbook(new File(getPath()));
        sheet = workbook.getSheet(caseName);
        rows = sheet.getRows();
        columns = sheet.getColumns();
        // 为了返回值是Object[][],定义一个多行单列的二维数组
        HashMap<Object, String>[][] arrmap = new HashMap[rows -1][1];

        // 对数组中所有元素hashmap进行初始化
        if (rows > 1) {
            for (int i = 0; i < rows - 1; i++) {
                arrmap[i][0] = new HashMap<>();
            }
        } else {
            System.out.println("excel中没有数据");
        }

        // 获得首行的列名，作为hashmap的key值
        for (int r = 0; r < rows - 1; r++) {
            String cellvalue = sheet.getCell(column, 0).getContents();
            arrkey.add(cellvalue);
        }

        // 遍历所有的单元格的值添加到hashmap中
        for (int r = 1; r < rows; r++) {
            String cellvalue = sheet.getCell(column, r).getContents();
            arrmap[r -1][0].put(arrkey.get(column), cellvalue);
        }
        return arrmap;
    }

    //    获取指定单元格的值
    public Object[][] getExcelData(int row,int column) throws BiffException, IOException {
        workbook = Workbook.getWorkbook(new File(getPath()));
        sheet = workbook.getSheet(caseName);
        rows = sheet.getRows();
        columns = sheet.getColumns();
        // 为了返回值是Object[][],定义一个多行单列的二维数组
        HashMap<Object, String>[][] arrmap = new HashMap[1][1];

        // 对数组中所有元素hashmap进行初始化
        if (rows > 1) {
            arrmap[0][0] = new HashMap<>();

        } else {
            System.out.println("excel中没有数据");
        }

        // 获得首行的列名，作为hashmap的key值
        for (int r = 0; r < rows - 1; r++) {
            String cellvalue = sheet.getCell(column, 0).getContents();
            arrkey.add(cellvalue);
        }

        // 遍历所有的单元格的值添加到hashmap中
        String cellvalue = sheet.getCell(column, row).getContents();
        arrmap[0][0].put(arrkey.get(column), cellvalue);
        return arrmap;
    }


    /**
     * 获得excel文件的路径
     * @return
     * @throws IOException
     */
    public String getPath() throws IOException {
        File directory = new File(".");
        sourceFile = directory.getCanonicalPath() + "\\src\\database\\"
                + fileName + ".xls";
        return sourceFile;
    }


}
