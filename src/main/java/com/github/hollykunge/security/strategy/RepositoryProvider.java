package com.github.hollykunge.security.strategy;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.ExceptionCommonUtil;
import com.github.hollykunge.security.config.GitConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author: zhhongyu
 * @description: git仓库抽象接口
 * @since: Create in 15:11 2019/9/4
 */
@Slf4j
@Service
public class RepositoryProvider extends RespositoryInterface {

    /**
     * 初始化git仓库
     *
     * @param repositoryName
     * @throws IOException
     * @throws IllegalStateException
     * @throws GitAPIException
     */
    @Override
    public void gitInit(String repositoryName) throws IOException, IllegalStateException, GitAPIException {
        // prepare a new folder
        File localPath = new File(GitConfig.address + repositoryName);
        //文件夹路径不存在
        if (localPath.exists() && localPath.isDirectory()) {
            throw new BaseException("已创建了同名的仓库名称...");
        }
        // create the directory
        Git git = Git.init().setDirectory(localPath).call();
        System.out.println("成功创建仓库位置: " + git.getRepository().getDirectory());
        log.info("Having repository: " + git.getRepository().getDirectory());
        if (git != null) {
            git.close();
        }
    }

    @Override
    public boolean gitClone(String clonePath, String repoPath) throws GitAPIException {
        File client = new File(clonePath);
        client.mkdir();
        try (Git git = Git.cloneRepository()
                .setURI(repoPath)
                .setDirectory(client)
                .call()) {
            log.info("Cloning from " + repoPath + " to " + git.getRepository());
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw e;
        } finally {
            super.gitClose();
        }
    }

    @Override
    public void createBrance(Git git, String branchName) throws IOException, GitAPIException {
        try {
            judgeGitAndUser();
            List<Ref> refs = git.branchList().call();
            if (isExitBranch(refs, branchName)) {
                git.branchDelete()
                        .setBranchNames(branchName)
                        .setForce(true)
                        .call();
            }
            git.branchCreate()
                    .setName(branchName)
                    .call();
        } catch (Exception e) {
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
            throw e;
        } finally {
            gitClose();
        }
    }

    @Override
    public void deleteBrance(Git git, String branchName) throws Exception {
        try {
            judgeGitAndUser();
            List<Ref> refs = git.branchList().call();
            if (!isExitBranch(refs, branchName)) {
                throw new BaseException("仓库中不存在这个分支，不需要删除...");
            }
            git.branchDelete()
                    .setBranchNames(branchName)
                    .call();
        } catch (Exception e) {
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
            throw e;
        } finally {
            gitClose();
        }
    }

    private boolean isExitBranch(List<Ref> refs, String branchName) {
        if (StringUtils.isEmpty(branchName)) {
            throw new BaseException("分支不能为空...");
        }
        if (refs.size() == 0 || refs.size() < 0) {
            throw new BaseException("仓库中没有分支集...");
        }
        return refs.stream().anyMatch(new Predicate<Ref>() {
            @Override
            public boolean test(Ref ref) {
                if (ref.getName().equals("refs/heads/" + branchName)) {
                    return true;
                }
                return false;
            }
        });
    }
}
