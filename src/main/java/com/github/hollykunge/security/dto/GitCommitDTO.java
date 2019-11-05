package com.github.hollykunge.security.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: zhhongyu
 * @description: git提交实体类
 * @since: Create in 16:37 2019/9/4
 */
@Data
public class GitCommitDTO {
    /**
     * 提交人
     */
    private String username;
    /**
     * 提交人密码
     */
    private String password;
    /**
     * 提交人邮箱
     */
    private String email;
    /**
     * 提交分支
     */
    @NotNull
    private String branch;
    /**
     * 提交人电话
     */
    private String tel;
    /**
     * 提交信息描述
     */
    private String decription;
    /**
     * 提交版本
     */
    private String version;
    /**
     * 要提交的文件，如果要提交所有则set(".");
     */
    @NotNull
    private String filePath;
    /**
     * 是否是新创建的文件
     */
    private Boolean isNewCreat;
    /**
     * 合并分支标识（from为要合并的分支，to为合并到的分支,参照常量类）
     */
    private String mergeBranchFlag;
}
