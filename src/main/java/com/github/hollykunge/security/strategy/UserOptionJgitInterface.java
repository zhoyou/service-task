package com.github.hollykunge.security.strategy;

import com.github.hollykunge.security.dto.GitCommitDTO;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

/**
 * @author: zhhongyu
 * @description: 用户操作抽象类接口
 * @since: Create in 9:12 2019/9/5
 */
public abstract class UserOptionJgitInterface extends BaseJgitManager {
    /**
     * 当前仓库人初始化库文件
     * @return
     * @throws Exception
     */
    public abstract boolean initRepoFile() throws Exception;
    /**
     * 当前仓库使用人，提交文件
     * @return
     * @throws GitAPIException
     */
    public abstract boolean gitCommit() throws Exception;

    /**
     * 指定提交人提交文件
     * @param gitCommitDTO
     * @return
     * @throws Exception
     */
    public abstract boolean gitCommit(GitCommitDTO gitCommitDTO) throws Exception;

    /**
     * 切换分支,如果分支名称不存在则新创建分支
     * @param branch
     * @return
     * @throws Exception
     */
    public abstract Git checkout(String branch) throws Exception;

    /**
     * 返回两个分支之间的差别
     *
     * @param branchs 分支集
     * @return 差异文件流
     * @throws Exception
     */
    public abstract byte[] showBranchDiff(String... branchs) throws Exception;

    /**
     * 合并分支操作，该方法为先切换分支
     * 再进行commit操作，之后合并分支代码
     * @param mergeDTO
     * @return
     * @throws Exception
     */
    public abstract boolean mergeChanges(GitCommitDTO mergeDTO) throws Exception;

    /**
     * 仓库创建人解决分支提交冲突
     * @param conflictBranch 冲突分支
     * @return
     * @throws Exception
     */
    public abstract boolean resolveConflicts(String conflictBranch) throws Exception;
}
