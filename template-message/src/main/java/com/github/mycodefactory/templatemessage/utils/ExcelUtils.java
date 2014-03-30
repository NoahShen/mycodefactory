package com.github.mycodefactory.templatemessage.utils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-3-5
 * Time: 下午6:25
 * To change this template use File | Settings | File Templates.
 */
public class ExcelUtils {

    public static List<Map<String, String>> readFromExcel(String f) throws IOException, BiffException {
        return readFromExcel(new File(f));
    }

    public static List<Map<String, String>> readFromExcel(File f) throws IOException, BiffException {
        //打开文件
        Workbook book = Workbook.getWorkbook(f);
        Sheet sheet = book.getSheet(0);
        Cell[] header = sheet.getRow(0);

        List<Map<String, String>> rowList = new ArrayList<Map<String, String>>();
        for (int row = 1; row < sheet.getRows(); ++row) {
            Cell[] oneRow = sheet.getRow(row);
            Map<String, String> rowMap = new HashMap<String, String>();
            for (int col = 0; col < oneRow.length; ++col) {
                rowMap.put(header[col].getContents(), oneRow[col].getContents());
            }
            rowList.add(rowMap);
        }
        return rowList;
    }
}
