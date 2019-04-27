/**
 * 文件名: FileUtils.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-17 17:08
 */
public class FileUtils {
    public String ReadFile(String Path) {
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }


    public  static  String saveFile(String path,String str) {

        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        PrintWriter printWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            printWriter = new PrintWriter(fileOutputStream);
            printWriter.write(str);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            printWriter.close();
        }

        return "ok";
    }

    /**
     * @param byteArrayOutputStream 将文件内容写入ByteArrayOutputStream
     * @param response HttpServletResponse  写入response
     * @param returnName 返回的文件名
     */
    public static void download(ByteArrayOutputStream byteArrayOutputStream, HttpServletResponse response, String returnName) throws IOException{
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment; filename=" + returnName);
        response.setContentLength(byteArrayOutputStream.size());
        OutputStream outputstream = response.getOutputStream();         //取得输出流
        byteArrayOutputStream.writeTo(outputstream);                    //写到输出流
        byteArrayOutputStream.close();                                  //关闭
        outputstream.flush();                                           //刷数据
    }

    /**
     * 从文件路径中截取出来文件名字
     * 注意win与Linux环境
     * @param filePath
     * @return
     */
    public static String getFileNameInFilePath(String filePath){
        String fileName[] = filePath.split("\\/");
        return fileName[fileName.length-1];
    }
}
