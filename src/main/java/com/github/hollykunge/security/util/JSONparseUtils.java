package com.github.hollykunge.security.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.hollykunge.security.entity.UserTaskMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhhongyu
 * @description:
 * @since: Create in 14:52 2019/9/24
 */
public class JSONparseUtils {

    public static void writeToJson(String filePath, JSONArray object) throws IOException
    {
        File file = new File(filePath);
        char [] stack = new char[1024];
        int top=-1;
        String string = object.toString();
        StringBuffer sb = new StringBuffer();
        char [] charArray = string.toCharArray();
        for(int i=0;i<charArray.length;i++){
            char c= charArray[i];
            if ('{' == c || '[' == c) {
                stack[++top] = c;
                sb.append("\n"+charArray[i] + "\n");
                continue;
            }
            if ((i + 1) <= (charArray.length - 1)) {
                char d = charArray[i+1];
                if ('}' == d || ']' == d) {
                    top--;
                    sb.append(charArray[i] + "\n");
                    continue;
                }
            }
            if (',' == c) {
                sb.append(charArray[i] + "\n");
                for (int j = 0; j <= top; j++) {
                    sb.append("");
                }
                continue;
            }
            sb.append(c);
        }
        Writer write = new FileWriter(file);
        write.write(sb.toString());
        write.flush();
        write.close();
    }

    public static void main(String[] args) throws IOException {
        List<UserTaskMap> list = new ArrayList<UserTaskMap>();
        UserTaskMap userTaskMap1 = new UserTaskMap();
        userTaskMap1.setTaskName("sadfasf防守打法绿扩撒多军发奥奥奥奥奥" +
                "奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥奥");
        userTaskMap1.setUserName("李四");
        UserTaskMap userTaskMap3 = new UserTaskMap();
        userTaskMap3.setTaskName("张三qweqwe");
        userTaskMap3.setUserName("李四qweqwe");
        UserTaskMap userTaskMap2 = new UserTaskMap();
        userTaskMap2.setTaskName("张三");
        userTaskMap2.setUserName("李四");
        list.add(userTaskMap1);
        list.add(userTaskMap3);
        list.add(userTaskMap2);
        JSONArray jsonArray = JSON.parseArray(JSONObject.toJSONString(list));
        JSONparseUtils.writeToJson("C:\\lark_repo\\lark_csrw1\\wisehome.json",jsonArray);
    }
}
