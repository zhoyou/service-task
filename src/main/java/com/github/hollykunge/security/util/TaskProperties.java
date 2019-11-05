package com.github.hollykunge.security.util;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author: zhhongyu
 * @description:
 * @since: Create in 15:59 2019/9/25
 */
public class TaskProperties {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\Administrator\\Desktop\\test.properties";
        String parameterName = "com.github.hollykunge.zh";
        String parameterValue = "zhhongyu";
        Properties prop = new Properties();
        try {
            InputStream fis = new FileInputStream(filePath);
            //从输入流中读取属性列表（键和元素对）
            prop.load(fis);
            //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
            //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(filePath);
            prop.setProperty(parameterName, parameterValue);
            //以适合使用 load 方法加载到 Properties 表中的格式，
            //将此 Properties 表中的属性列表（键和元素对）写入输出流
            prop.store(fos, "Update '" + parameterName + "' value");
        } catch (IOException e) {
            System.err.println("Visit "+filePath+" for updating "+parameterName+" value error");
        }
    }
}
