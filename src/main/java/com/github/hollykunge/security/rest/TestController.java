package com.github.hollykunge.security.rest;

import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.config.GitConfig;
import com.github.hollykunge.security.constants.TaskContants;
import com.github.hollykunge.security.dto.GitCommitDTO;
import com.github.hollykunge.security.factory.GitFactory;
import com.github.hollykunge.security.strategy.RespositoryInterface;
import com.github.hollykunge.security.strategy.UserOptionJgitInterface;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author: zhhongyu
 * @description: 测试接口
 * @since: Create in 18:34 2019/9/4
 */
@RestController
public class TestController {
    @Autowired
    private RespositoryInterface repositoryProvider;

    @Autowired
    private UserOptionJgitInterface userOptionJgitInterface;

    @Autowired
    private GitFactory gitFactory;

    @GetMapping("clone")
    public String gitclone(String repo,String clonepath) throws IOException, GitAPIException {
        repositoryProvider.gitClone(GitConfig.address+clonepath,
                GitConfig.address+repo);
        return "success";
    }

    /**
     * 默认必须初始化到master分支，不然进行不下去了
     * @param repo
     * @param file
     * @param des
     * @return
     * @throws Exception
     */
    @GetMapping("initRepo")
    public String initRepo(String repo,String file,String des) throws Exception {
        Git git = gitFactory.getGit(repo);
        GitCommitDTO currentUser = this.getCurrentUser();
        currentUser.setFilePath(file);
        currentUser.setDecription(des);
        currentUser.setBranch("master");
        userOptionJgitInterface.setGitAndUser(currentUser,git);
        userOptionJgitInterface.initRepoFile();
        return "success";
    }

    @GetMapping("commit")
    public String commit(String repo,String branch,String file,String des) throws Exception {
        Git git = gitFactory.getGit(repo);
        GitCommitDTO currentUser = this.getCurrentUser();
        currentUser.setBranch(branch);
        currentUser.setFilePath(file);
        currentUser.setDecription(des);
        userOptionJgitInterface.setGitAndUser(currentUser,git);
        userOptionJgitInterface.gitCommit();
        return "success";
    }

    @GetMapping("checkout")
    public String checkout(String name,String repo) throws Exception {
        Git git = gitFactory.getGit(repo);
        userOptionJgitInterface.setGit(git);
        userOptionJgitInterface.checkout(name);
        return "success";
    }

    @GetMapping("init")
    public String init(String name) throws Exception {
        repositoryProvider.gitInit(name);
        return "success";
    }
    @GetMapping("showBranchDiff")
    public ObjectRestResponse<String> showBranchDiff(String repo, String branch1, String branch2) throws Exception {
        String[] branchs = new String[]{branch1,branch2};
        Git git = gitFactory.getGit(repo);
        userOptionJgitInterface.setGit(git);
        String result = new String(userOptionJgitInterface.showBranchDiff(branchs),"UTF-8");
        return new ObjectRestResponse<String>().data(result).rel(true);
    }

    @GetMapping("mergeChanges")
    public String mergeChanges(String repo, String toMerge, String fromMerge,String file,String des) throws Exception {
        Git git = gitFactory.getGit(repo);
        GitCommitDTO currentUser = this.getCurrentUser();
        currentUser.setBranch(fromMerge);
        currentUser.setFilePath(file);
        currentUser.setDecription(des);
        currentUser.setMergeBranchFlag(TaskContants.GIT_BRANCH_FROM_MERGE_BRANCH);
        GitCommitDTO gitCommitDTO = new GitCommitDTO();
        gitCommitDTO.setBranch(toMerge);
        gitCommitDTO.setMergeBranchFlag(TaskContants.GIT_BRANCH_TO_MERGE_BRANCH);
        userOptionJgitInterface.setGitAndUser(currentUser,git);
        userOptionJgitInterface.mergeChanges(gitCommitDTO);
        return "success";
    }
    @GetMapping("resolveConflicts")
    public String resolveConflicts(String repo,String conflictBranch) throws Exception {
        Git git = gitFactory.getGit(repo);
        GitCommitDTO currentUser = this.getCurrentUser();
        currentUser.setMergeBranchFlag(TaskContants.GIT_BRANCH_FROM_MERGE_BRANCH);
        userOptionJgitInterface.setGitAndUser(currentUser,git);
        userOptionJgitInterface.resolveConflicts(conflictBranch);
        return "success";
    }


    private GitCommitDTO getCurrentUser(){
        GitCommitDTO currentUser = new GitCommitDTO();
        currentUser.setUsername("zhhongyu");
        currentUser.setEmail("18525788842@163.com");
        return currentUser;
    }

}
