package com.github.hollykunge.security.biz;

import com.github.hollykunge.security.common.biz.BaseBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.util.UUIDUtils;
import com.github.hollykunge.security.entity.TaskEntity;
import com.github.hollykunge.security.entity.UserTaskMap;
import com.github.hollykunge.security.factory.GitFactory;
import com.github.hollykunge.security.mapper.TaskInfoMapper;
import com.github.hollykunge.security.mapper.UserTaskMapMapper;
import com.github.hollykunge.security.strategy.RespositoryInterface;
import com.github.hollykunge.security.util.PermissionUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author: zhhongyu
 * @description: 用户任务业务层接口
 * @since: Create in 11:07 2019/9/5
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserTaskBiz extends BaseBiz<UserTaskMapMapper, UserTaskMap> {
    @Resource
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private RespositoryInterface respo;
    @Autowired
    private GitFactory git;

    @Override
    protected String getPageName() {
        return "UserTaskBiz";
    }

    /**
     * 拉人进任务进行协同设计业务实现
     *
     * @param entity
     * @param userId 当前操作人
     */
    public void pullTaskUser(UserTaskMap entity,String userId) throws IOException, GitAPIException {
        if (entity == null) {
            throw new BaseException("参数不能为空...");
        }
        entity.setGitBranchName(UUIDUtils.generateShortUuid());
        if (jurgerUserTaskEntity(entity)) {
            TaskEntity taskEntity = taskInfoMapper.selectByPrimaryKey(entity.getTaskId());
            if (taskEntity == null) {
                throw new BaseException("这个taskid在主表信息中不存在...");
            }
            //权限校验
            PermissionUtils.initGitPermission(userId,taskEntity.getTaskExecutorId());
            entity.setTaskName(taskEntity.getName());
            UserTaskMap temUser = new UserTaskMap();
            temUser.setTaskId(entity.getTaskId());
            temUser.setUserId(entity.getUserId());
            List<UserTaskMap> entityList = mapper.select(temUser);
            if(entityList.size()>0){
                throw new BaseException("这个人已经拉取过了，不能重复拉取...");
            }
            super.insertSelective(entity);
            //创建git分支
            respo.createBrance(git.getGit(taskEntity.getGitRepoName()),entity.getGitBranchName());
        }
    }

    /**
     * 剔除协同人员
     * @param currentUser 当前操作人
     * @param taskUserId
     * @throws Exception
     */
    public void removeTaskUser(String currentUser,String taskUserId)throws Exception{
        if(StringUtils.isEmpty(taskUserId)){
            throw new BaseException("用户任务主键不能为空...");
        }
        UserTaskMap userTaskMap = mapper.selectByPrimaryKey(taskUserId);
        if(userTaskMap == null || StringUtils.isEmpty(userTaskMap.getGitBranchName())){
            throw new BaseException("不存在该用户分支...");
        }
        TaskEntity taskEntity = taskInfoMapper.selectByPrimaryKey(userTaskMap.getTaskId());
        //校验权限
        PermissionUtils.initGitPermission(currentUser,taskEntity.getTaskExecutorId());
        userTaskMap.setStatus("0");
        mapper.updateByPrimaryKeySelective(userTaskMap);
    }

    private boolean jurgerUserTaskEntity(UserTaskMap entity) {
        if (entity == null) {
            throw new BaseException("userTaskMap entity 不能为null或空...");
        }
        if (StringUtils.isEmpty(entity.getTaskId())) {
            throw new BaseException("taskId不能为空...");
        }
        if (StringUtils.isEmpty(entity.getUserId())) {
            throw new BaseException("userid不能为空...");
        }
        if (StringUtils.isEmpty(entity.getUserName())) {
            throw new BaseException("userName不能为空...");
        }
        if (StringUtils.isEmpty(entity.getGitBranchName())) {
            throw new BaseException("用户使用分支不能为空...");
        }
        return true;
    }

}
