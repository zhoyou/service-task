package com.github.hollykunge.security.util;

import com.github.hollykunge.security.common.exception.BaseException;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author: zhhongyu
 * @description: 权限校验工具类
 * @since: Create in 16:39 2019/9/23
 */
public class PermissionUtils {
    public static void initGitPermission(String userId,String taskExecutorId){
        if(StringUtils.isEmpty(userId)||StringUtils.isEmpty(taskExecutorId)){
            throw new BaseException("权限对比人含有空值...");
        }
        if(!Objects.equals(userId,taskExecutorId)){
            throw new BaseException("你不是任务执行人，你没有权限做初始化文件的提交...");
        }
    }
}
