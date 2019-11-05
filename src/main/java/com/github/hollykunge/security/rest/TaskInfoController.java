package com.github.hollykunge.security.rest;

import com.github.hollykunge.security.biz.TaskInfoBiz;
import com.github.hollykunge.security.common.exception.BaseException;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.config.GitConfig;
import com.github.hollykunge.security.entity.TaskEntity;
import com.github.hollykunge.security.util.PermissionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author: zhhongyu
 * @description: 任务接口
 * @since: Create in 11:11 2019/9/5
 */
@RestController
@RequestMapping("taskInfo")
public class TaskInfoController extends BaseController<TaskInfoBiz, TaskEntity> {

    /**
     * 新建任务，同时创建仓库
     *
     * @param taskEntity
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<TaskEntity> addOne(@RequestBody TaskEntity taskEntity)throws Exception{
        baseBiz.addTaskAndGitRepo(taskEntity);
        return new ObjectRestResponse<>().rel(true);
    }

    /**
     * 往git仓库中导入文件
     * @param files
     * @param taskId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "fileUpload/{taskId}", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<Boolean> fileUpload(MultipartFile[] files,@PathVariable String taskId)throws Exception{
        TaskEntity taskEntity = baseBiz.selectById(taskId);
        if(taskEntity == null && StringUtils.isEmpty(taskEntity.getGitRepoName())){
            throw new BaseException("没有指定的资源库...");
        }
        //权限校验
        PermissionUtils.initGitPermission(request.getHeader("userId"),taskEntity.getTaskExecutorId());
        for (MultipartFile file : files) {
            //上传文件目录
            String uploadFolder = GitConfig.address+taskEntity.getGitRepoName();
            String fileName = file.getOriginalFilename();
            File uploadFile = new File(uploadFolder,fileName);
            //判断上传文件目录是否存在，如果不存在就创建
            if (!uploadFile.getParentFile().exists()) {
                uploadFile.getParentFile().mkdirs();
            }
            file.transferTo(uploadFile);
        }
        return new ObjectRestResponse<>().rel(true);
    }

    /**
     * 初始化任务中的文件
     * @param taskId 任务id
     * @return
     * @throws Exception
     */
    @GetMapping("/initFile")
    public ObjectRestResponse<Boolean> initRepo(String taskId)throws Exception{
        baseBiz.initRepoFile(taskId,request.getHeader("userId"));
        return new ObjectRestResponse<>().rel(true);
    }
}
