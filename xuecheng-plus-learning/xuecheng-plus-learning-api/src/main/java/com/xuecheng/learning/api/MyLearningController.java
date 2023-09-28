package com.xuecheng.learning.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的学习接口
 * @date 2022/10/27 8:59
 */
@Api(value = "学习过程管理接口", tags = "学习过程管理接口")
@Slf4j
@RestController
public class MyLearningController {
    @Autowired
    LearningService learningService;

    /**
     * 获取视频
     *
     * @param courseId
     * @param teachplanId
     * @param mediaId
     * @return
     */
    @ApiOperation("获取视频")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId, @PathVariable("teachplanId") Long teachplanId, @PathVariable("mediaId") String mediaId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();
        RestResponse<String> video = learningService.getVideo(userId, courseId, teachplanId, mediaId);
        // 判断学习资格
        return video;
    }

}
