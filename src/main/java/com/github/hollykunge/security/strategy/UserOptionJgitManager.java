package com.github.hollykunge.security.strategy;

import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.ExceptionCommonUtil;
import com.github.hollykunge.security.constants.TaskContants;
import com.github.hollykunge.security.dto.GitCommitDTO;
import com.github.hollykunge.security.helper.IDiffFormatter;
import com.github.hollykunge.security.util.JGitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author: zhhongyu
 * @description: git操作策略
 * @since: Create in 16:26 2019/9/4
 */
@Slf4j
@Service
public class UserOptionJgitManager extends UserOptionJgitInterface {

    @Override
    public boolean initRepoFile() throws Exception {
        //校验git用户
        super.judgeUser();
        try {
            this.gitCommitContent(git, currentUser, true);
            return true;
        } catch (Exception e) {
            throw e;
        } finally {
            super.gitClose();
        }
    }

    /**
     * 当前仓库使用人提交文件
     *
     * @return
     * @throws Exception
     */
    @Override
    public boolean gitCommit() throws Exception {
        //校验git用户
        super.judgeGitAndUser();
        try {
            this.gitCommitContent(git, currentUser, false);
            return true;
        } catch (Exception e) {
            throw e;
        } finally {
            super.gitClose();
        }
    }

    /**
     * 指定提交人提交文件，此方法不会关闭git
     *
     * @param gitCommitDTO
     * @return
     * @throws Exception
     */
    @Override
    public boolean gitCommit(GitCommitDTO gitCommitDTO) throws Exception {
        super.judgeGit();
        try {
            this.gitCommitContent(git, gitCommitDTO, false);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 提交详细私有方法
     *
     * @param git          git仓库
     * @param gitCommitDTO 提交人信息
     * @return
     */
    private Git gitCommitContent(Git git, GitCommitDTO gitCommitDTO, boolean isInitRepo) throws Exception {
        if (StringUtils.isEmpty(gitCommitDTO.getFilePath())) {
            throw new BaseException("提交的文件路径不能为空或null...");
        }
        checkRule(gitCommitDTO);
        if (StringUtils.isEmpty(gitCommitDTO.getDecription())) {
            throw new BaseException("提交文件必须要有描述...");
        }
        //切换分支
        try {
            if (!isInitRepo) {
                checkout(gitCommitDTO.getBranch());
            }
            git.add().addFilepattern(gitCommitDTO.getFilePath()).call();
            //提交
            git.commit()
                    .setAuthor(gitCommitDTO.getUsername(), gitCommitDTO.getEmail())
                    .setMessage(gitCommitDTO.getDecription())
                    .call();
            log.info("Commit And Push file " + gitCommitDTO.getFilePath() + " to repository at " + git.getRepository().getDirectory());
        } catch (Exception e) {
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
            throw e;
        }
        return git;
    }

    /**
     * 切换到指定分支名称
     *
     * @param branch
     * @return Git 由调用人选择是否关闭git
     * @throws Exception
     */
    @Override
    public Git checkout(String branch) throws Exception {
        try {
            List<Ref> refs = git.branchList().call();
            boolean existBranch = JGitUtil.isExistBranch(refs, branch);
            git.checkout()
                    .setCreateBranch(!existBranch)
                    .setName(branch)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .call();
            log.info("Pulled from remote repository to local repository at " + git.getRepository());
            return git;
        } catch (Exception e) {
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
            throw e;
        }
    }

    @Override
    public byte[] showBranchDiff(String... branchs) throws Exception {
        super.judgeGit();
        boolean isExitBranch = true;
        List<Ref> refs = git.branchList().call();
        for (String oneBranch : branchs) {
            isExitBranch = refs.stream().anyMatch((Ref ref) -> Objects.equals(ref.getName(), TaskContants.GIT_REF_HEAD + oneBranch));
        }
        if (!isExitBranch) {
            throw new BaseException("所输入的比对分支不存在...");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter formatter = new IDiffFormatter(out);
        try {
            AbstractTreeIterator firstTreeParser = null;
            AbstractTreeIterator secondTreeParser = null;
            for (int i = 0; i < branchs.length; i++) {
                firstTreeParser = prepareTreeParser(git.getRepository(), TaskContants.GIT_REF_HEAD + branchs[0]);
                secondTreeParser = prepareTreeParser(git.getRepository(), TaskContants.GIT_REF_HEAD + branchs[1]);
            }
            List<DiffEntry> diff = git
                    .diff()
                    .setOldTree(firstTreeParser)
                    .setNewTree(secondTreeParser)
                    .setSourcePrefix(branchs[0])
                    .setDestinationPrefix(branchs[1])
                    .call();
            for (DiffEntry entry : diff) {
                //设置比较器为忽略空白字符对比（Ignores all whitespace）
                formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
                formatter.setRepository(super.gitRepository());
                formatter.format(entry);
            }
            String result = out.toString("UTF-8");
            log.info("变更文件如下:\r\n" + result);
            return out.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            gitClose();
            formatter.close();
            out.close();
        }
    }

    @Override
    public boolean mergeChanges(GitCommitDTO mergeDTO) throws Exception {
        super.judgeGitAndUser();
        this.checkMergeDTO(mergeDTO);
        try {
            //如果当前仓库使用人中的提交filepath不为空，则先进行提交
            if (!StringUtils.isEmpty(currentUser.getFilePath())) {
                this.gitCommit();
            }
            String toMergeBranch = null;
            String fromMergeBranch = null;
            //判断当前使用人的分支和待合并的分支，哪个是合并到的分支
            if (StringUtils.isEmpty(currentUser.getMergeBranchFlag())
                    || StringUtils.isEmpty(mergeDTO.getMergeBranchFlag())) {
                throw new BaseException("要有两个合并分支...");
            }
            fromMergeBranch = selectMergeBranch(currentUser, TaskContants.GIT_BRANCH_FROM_MERGE_BRANCH);
            toMergeBranch = selectMergeBranch(mergeDTO, TaskContants.GIT_BRANCH_TO_MERGE_BRANCH);
            if (StringUtils.isEmpty(toMergeBranch) || StringUtils.isEmpty(fromMergeBranch)) {
                fromMergeBranch = selectMergeBranch(mergeDTO, TaskContants.GIT_BRANCH_FROM_MERGE_BRANCH);
                toMergeBranch = selectMergeBranch(currentUser, TaskContants.GIT_BRANCH_TO_MERGE_BRANCH);
            }
            if (StringUtils.isEmpty(toMergeBranch) || StringUtils.isEmpty(fromMergeBranch)) {
                throw new BaseException("没有合并到分支或待合并的分支...");
            }
            //切换到合并到的分支
            checkout(toMergeBranch);
            ObjectId mergeBase = gitRepository().resolve(fromMergeBranch);
            MergeResult merge = git.merge().
                    include(mergeBase).
                    setCommit(true).
                    setFastForward(MergeCommand.FastForwardMode.NO_FF).
                    setMessage("Merged changes").
                    call();
            log.info("合并分支状态码为："+merge.getMergeStatus());
            if(merge.getMergeStatus() == MergeResult.MergeStatus.CONFLICTING){
                merge.getConflicts().keySet().forEach( path -> {
//                    checkout(".ours" );
//                    checkout(".theirs" );
                } );
                //todo:回调函数保存到数据库或者调用git status
                throw new BaseException("合并失败...文件修改冲突");
            }
            if (merge.getMergeStatus().isSuccessful()) {
                log.info("Merge-Results for id: " + mergeBase + ": " + merge);
            }
            return true;
        } catch (Exception e) {
            log.error(ExceptionCommonUtil.getExceptionMessage(e));
            throw e;
        } finally {
            super.gitClose();
        }
    }

    @Override
    public boolean resolveConflicts(String conflictBranch) throws Exception {
        if(StringUtils.isEmpty(conflictBranch)){
            throw new BaseException("冲突分支名不能为空...");
        }
        judgeGitAndUser();
        GitCommitDTO conflictDto = new GitCommitDTO();
        conflictDto.setMergeBranchFlag(TaskContants.GIT_BRANCH_TO_MERGE_BRANCH);
        mergeChanges(conflictDto);
        return true;
    }

    private String selectMergeBranch(GitCommitDTO gitCommitDTO, String mergeFlag) {
        String result = null;
        if (mergeFlag.equals(gitCommitDTO.getMergeBranchFlag())) {
            result = gitCommitDTO.getBranch();
        }
        return result;
    }

    private AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
        Ref head = repository.exactRef(ref);
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

    private void checkRule(GitCommitDTO dto) {
        if (dto == null) {
            throw new BaseException("dto 不能为空...");
        }
        if (StringUtils.isEmpty(dto.getBranch())) {
            throw new BaseException("分支不能为空...");
        }
    }

    private void checkMergeDTO(GitCommitDTO dto) {
        if (dto == null || StringUtils.isEmpty(dto.getBranch())) {
            throw new BaseException("要合并的对象或分支为空...");
        }
    }

    private void mergeRule(List<GitCommitDTO> dtos) {
        if (dtos == null || dtos.size() == 0 || dtos.size() < 0) {
            throw new BaseException("要有合并的分支...");
        }
        if (dtos.size() == 1) {
            throw new BaseException("不能对一个分支进行合并...");
        }
        if (dtos.size() > 2) {
            throw new BaseException("不支持三个以上分支进行合并...");
        }
        boolean isMergedBranch = dtos.stream().anyMatch(new Predicate<GitCommitDTO>() {
            @Override
            public boolean test(GitCommitDTO gitCommitDTO) {
                if (Objects.equals(TaskContants.GIT_BRANCH_TO_MERGE_BRANCH, gitCommitDTO.getMergeBranchFlag())) {
                    return true;
                }
                return false;
            }
        });
        if (!isMergedBranch) {
            throw new BaseException("要有合并到的分支...");
        }
    }
}
