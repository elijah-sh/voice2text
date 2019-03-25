/**
 * 文件名: FileTest.java
 * 版权：Copyright 2017-2022 CMCC All Rights Reserved.
 * 描述:
 */
package voice2text.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @description:
 * @author: Shenshuaihu
 * @version: 1.0
 * @data: 2019-03-21 12:01
 */
public class FileTest {
    public static void test( ) {
        File file = new File("E:/2.lfasr/201903211157$3564.txt");
        FileOutputStream fileOutputStream = null;
        PrintWriter printWriter = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            printWriter = new PrintWriter(fileOutputStream);
            printWriter.write("ssssssss201903211157$3564");
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
        }

    }

}
