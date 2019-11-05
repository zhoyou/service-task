package com.github.hollykunge.security.rest;

import com.github.hollykunge.security.biz.UserTaskBiz;
import com.github.hollykunge.security.common.msg.ObjectRestResponse;
import com.github.hollykunge.security.common.rest.BaseController;
import com.github.hollykunge.security.entity.UserTaskMap;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author: zhhongyu
 * @description: 用户任务接口
 * @since: Create in 11:11 2019/9/5
 */
@RestController
@RequestMapping("userTask")
public class UserTaskController extends BaseController<UserTaskBiz, UserTaskMap> {
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<UserTaskMap> pullTaskUser(@RequestBody UserTaskMap entity) throws IOException, GitAPIException {
        baseBiz.pullTaskUser(entity,request.getHeader("userId"));
        return new ObjectRestResponse<UserTaskMap>().rel(true);
    }
}
