package com.github.hollykunge.security.util;

import com.github.hollykunge.security.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.Ref;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Git操作工具类
 */
@Slf4j
public class JGitUtil {
    public static boolean isExistBranch(List<Ref> refs,String branchName){
        if(StringUtils.isEmpty(branchName)){
            throw new BaseException("分支名称不能为null...");
        }
        for (Ref ref : refs) {
            System.out.println("Had branch: " + ref.getName());
            if (ref.getName().equals("refs/heads/" + branchName)) {
                return true;
            }
        }
        return false;
    }

}