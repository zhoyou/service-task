package com.github.hollykunge.security.strategy;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author: zhhongyu
 * @description: 仓库相关接口
 * @since: Create in 9:09 2019/9/5
 */
public abstract class RespositoryInterface extends BaseJgitManager {
    /**
     * 创建git仓库,并创建分支
     * @param filePath
     * @throws IOException
     * @throws IllegalStateException
     * @throws GitAPIException
     */
    public abstract void gitInit(String filePath) throws IOException, IllegalStateException, GitAPIException;

    /**
     * clone项目
     *
     * @param clonePath 克隆到地址
     * @param repoPath  资源库地址
     * @return
     * @throws GitAPIException
     */
    public abstract boolean gitClone(String clonePath, String repoPath) throws GitAPIException;

    /**
     * 创建分支
     * @param git
     * @param branchName
     * @throws IOException
     * @throws GitAPIException
     */
    public abstract void createBrance(Git git, String branchName) throws IOException, GitAPIException;

    /**
     * 删除分支
     * @param git
     * @param branchName
     * @throws Exception
     */
    public abstract void deleteBrance(Git git, String branchName) throws Exception;
}
