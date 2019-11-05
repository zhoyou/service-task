package com.github.hollykunge.security.strategy;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.ExceptionCommonUtil;
import com.github.hollykunge.security.dto.GitCommitDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author: zhhongyu
 * @description: jgit父级抽象接口
 * @since: Create in 14:36 2019/9/4
 */
@Slf4j
public abstract class BaseJgitManager {
    protected GitCommitDTO currentUser;

    protected Git git;

    /**
     * 需要登录远程仓库才能提交
     * @param remoterepouri
     * @param localpath
     * @param gitcommitdto
     * @return
     */
    public boolean setupRepo(String remoterepouri, String localpath, GitCommitDTO gitcommitdto) {
        boolean result = true;
        try {
            Git git = Git.cloneRepository().setURI(remoterepouri)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            gitcommitdto.getUsername(), gitcommitdto.getPassword()))
                    .setBranch(gitcommitdto.getBranch())
                    .setDirectory(new File(localpath)).call();
        } catch (Exception e) {
            result = false;
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
        }
        return result;
    }

    /**
     * 配置git
     */
    public void setGitBaseConfig() throws IOException {
        Repository repository = this.gitRepository();
        if(repository == null){
            repository = git.getRepository();
        }
        StoredConfig config = repository.getConfig();
        //设置差异对比时只显示变动文本
        config.setLong(ConfigConstants.CONFIG_DIFF_SECTION,null,"context",0);
        config.save();
    }


    /**
     * 设置当前使用git用户信息,策略执行第一步
     * @param currentUser
     */
    public void setGitAndUser(GitCommitDTO currentUser,Git git) throws IOException {
        if(currentUser == null){
            throw new BaseException("不能设置空的Git用户...");
        }
        if(git == null){
            throw new BaseException("Git仓库不能为空或null...");
        }
        this.currentUser = currentUser;
        this.git = git;
        this.setGitBaseConfig();
        return;
    }

    /**
     * 单独设置使用仓库,适用于只使用git仓库
     * @param git
     */
    public void setGit(Git git) throws IOException {
        if(git == null){
            throw new BaseException("Git仓库不能为空或null...");
        }
        this.git = git;
        this.setGitBaseConfig();
        return;
    }

    protected void judgeGitAndUser(){
        if(this.currentUser == null){
            throw new BaseException("Git使用人不能为空或者null...");
        }
        this.userRule();
        if(this.git == null){
            throw new BaseException("当前Git仓库为空或null...");
        }
    }
    protected void judgeGit(){
        if(this.git == null){
            throw new BaseException("当前Git仓库为空或null...");
        }
    }
    protected void judgeUser(){
        if(this.currentUser == null){
            throw new BaseException("Git当前使用人不能为空或者null...");
        }
        if(StringUtils.isEmpty(this.currentUser.getUsername())
                ||StringUtils.isEmpty(this.currentUser.getEmail())){
            throw new BaseException("当前git使用人的姓名，邮箱为空或null...");
        }
    }
    protected void userRule(){
        if(StringUtils.isEmpty(this.currentUser.getUsername())
                ||StringUtils.isEmpty(this.currentUser.getEmail())
                ||StringUtils.isEmpty(this.currentUser.getBranch())){
            throw new BaseException("当前git使用人的姓名，邮箱，分支为空或null...");
        }
    }
    protected Repository gitRepository (){
        if (git != null){
            return git.getRepository();
        }
        return null;
    }

    protected void gitClose(){
        if(this.git != null){
            git.close();
        }
        git = null;
    }
}
