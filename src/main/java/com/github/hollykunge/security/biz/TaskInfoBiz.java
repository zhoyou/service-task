package com.github.hollykunge.security.biz;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.config.GitConfig;
import com.github.hollykunge.security.constants.TaskContants;
import com.github.hollykunge.security.dto.GitCommitDTO;
import com.github.hollykunge.security.entity.TaskEntity;
import com.github.hollykunge.security.entity.UserTaskMap;
import com.github.hollykunge.security.factory.GitFactory;
import com.github.hollykunge.security.mapper.TaskInfoMapper;
import com.github.hollykunge.security.mapper.UserTaskMapMapper;
import com.github.hollykunge.security.strategy.RespositoryInterface;
import com.github.hollykunge.security.strategy.UserOptionJgitInterface;
import com.github.hollykunge.security.util.ChineseUtils;
import com.github.hollykunge.security.util.PermissionUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author: zhhongyu
 * @description: 任务业务处理层
 * @since: Create in 11:07 2019/9/5
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TaskInfoBiz extends BaseBiz<TaskInfoMapper, TaskEntity> {
    final String TASK_PRE = "lark_";
    @Autowired
    private UserOptionJgitInterface userOptionGit;
    @Autowired
    private RespositoryInterface respository;
    @Resource
    private UserTaskMapMapper userTaskMapMapper;

    @Autowired
    private GitFactory gitFactory;

    @Override
    protected String getPageName() {
        return "TaskInfoBiz";
    }

    /**
     * 创建task任务并生成git仓库
     *
     * @param entity
     */
    public void addTaskAndGitRepo(TaskEntity entity) throws IOException, GitAPIException {
        String taskName = entity.getName();
        if (StringUtils.isEmpty(taskName)) {
            throw new BaseException("任务名称不能为空...");
        }
        //仓库名称策略为按任务名称首字符缩写
        String gitRepoName = ChineseUtils.toFirstChar(TASK_PRE + taskName);
        entity.setGitRepoName(gitRepoName);
        entity.setDefultBranch(GitConfig.defultMergeBranch);
        //创建任务
        super.insertSelective(entity);
        //生成一个创建者的usertaskmap
        UserTaskMap userTaskMap = generateUserTaskMapEntity(entity);
        userTaskMapMapper.insertSelective(userTaskMap);
        //生成git仓库
        respository.gitInit(gitRepoName);
    }

    /**
     * 初始化仓库文件
     * @param taskId 任务id
     * @param userId 执行人
     * @throws Exception
     */
    public void initRepoFile(String taskId,String userId) throws Exception {
        if(StringUtils.isEmpty(taskId)){
            throw new BaseException("任务id不能为空...");
        }
        TaskEntity taskEntity = mapper.selectByPrimaryKey(taskId);
        if(taskEntity == null && StringUtils.isEmpty(taskEntity.getGitRepoName())){
            throw new BaseException("根据提供的任务id，不能获取到对应的仓库...");
        }
        //判断当前登录人是否为任务执行人，不然不能够初始化仓库文件
        PermissionUtils.initGitPermission(userId,taskEntity.getTaskExecutorId());
        //获取仓库
        Git git = gitFactory.getGit(taskEntity.getGitRepoName());
        //todo:获取当前仓库使用人，先用TaskExecutorId，后面改成当前登录人就行,邮箱也是
        GitCommitDTO currentUser = gitFactory.generateCurrentGitUser(taskEntity.getTaskExecutorId(),"1822578@163.com");
        currentUser.setFilePath(".");
        currentUser.setDecription("初始化提交文件");
        currentUser.setBranch(TaskContants.GIT_SURPER_BRANCH);
        userOptionGit.setGitAndUser(currentUser,git);
        userOptionGit.initRepoFile();
    }

    private UserTaskMap generateUserTaskMapEntity(TaskEntity entity) {
        if (StringUtils.isEmpty(entity.getId())) {
            throw new BaseException("任务主键不能为null...");
        }
        if (StringUtils.isEmpty(entity.getName())) {
            throw new BaseException("任务名称不能为空...");
        }
        UserTaskMap userTaskMap = new UserTaskMap();
        userTaskMap.setGitBranchName(GitConfig.defultMergeBranch);
        userTaskMap.setTaskId(entity.getId());
        userTaskMap.setTaskName(entity.getName());
        userTaskMap.setUserId(entity.getTaskExecutorId());
        userTaskMap.setUserName(entity.getCrtName());
        userTaskMap.setId(UUIDUtils.generateShortUuid());
        return userTaskMap;
    }
}
