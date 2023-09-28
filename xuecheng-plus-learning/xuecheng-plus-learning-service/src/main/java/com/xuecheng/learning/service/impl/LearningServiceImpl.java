package com.xuecheng.learning.service.impl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTableService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@Service
public class LearningServiceImpl implements LearningService {
    @Autowired
    MyCourseTableService myCourseTableService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        // 判断课程是否存在
        if(coursepublish==null) {
            return RestResponse.validfail("课程不存在");
        }

        // 根据课程计划id去查询课程计划信息，如果is_preview的值为1表示支持试学
        // 也可以从coursePublish解析出计划信息判断是否支持试学

        // 用户已登录
        if (StringUtils.isNotEmpty(userId)) {
            XcCourseTablesDto learningStatus = myCourseTableService.getLearningStatus(userId, courseId);
            String learnStatus = learningStatus.getLearnStatus();
            if ("702002".equals(learnStatus)) {
                return RestResponse.validfail("无法学习，因为没有选课");
            } else if ("702003".equals(learnStatus)) {
                return RestResponse.validfail("无法学习，因为课程已过期");
            } else {
                // 有资格学习，返回视频播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        } else {
            // 如果用户没有登录
            // 查询课程信息
            String charge = coursepublish.getCharge();
            if("20100".equals(charge)) {
                // 有资格学习，要返回视频的播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;
            }
        }
        return RestResponse.validfail("该课程没有选课");
    }
}
