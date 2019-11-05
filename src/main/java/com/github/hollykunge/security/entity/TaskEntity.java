package com.github.hollykunge.security.entity;

import com.github.hollykunge.security.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author: zhhongyu
 * @description: 任务实体类
 * @since: Create in 10:52 2019/9/5
 */
@Data
@Table(name = "LARK_TASK_INFO")
public class TaskEntity extends BaseEntity {

    /**
     * 任务描述
     */
    @Column(name = "TASK_DES")
    private String taskDes;

    /**
     * 任务执行人
     */
    @Column(name = "TASK_EXECUTOR_ID")
    private String taskExecutorId;
    /**
     * 任务名称
     */
    @Column(name = "NAME")
    private String name;
    /**
     * 存放的仓库名称
     */
    @Column(name = "GIT_REPO_NAME")
    private String gitRepoName;
    /**
     * 默认合并到的分支
     */
    @Column(name = "DEFULT_BRANCH")
    private String defultBranch;

    /**
     * 爹任务ID
     */
    @Column(name = "TASK_PARENT_ID")
    private String taskParentId;

    /**
     * 计划结束时间
     */
    @Column(name = "TASK_PLAN_END")
    private Date taskPlanEnd;

    /**
     * 任务进度
     */
    @Column(name = "TASK_PROCESS")
    private String taskProcess;


    /**
     * 任务资源ID
     */
    @Column(name = "TASK_RESOURCE_ID")
    private String taskResourceId;

    /**
     * 任务状态
     */
    @Column(name = "TASK_STATE")
    private String taskState;

    /**
     * 任务实际结束时间
     */
    @Column(name = "TASK_TIME_END")
    private Date taskTimeEnd;

    /**
     * 任务实际开始时间
     */
    @Column(name = "TASK_TIME_START")
    private Date taskTimeStart;

    /**
     * 任务类型
     */
    @Column(name = "TASK_TYPE")
    private String taskType;
}
