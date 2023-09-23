package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 课程发布任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;


    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        int shardIndex = XxlJobHelper.getShardIndex(); // 执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal(); // 执行器的总数
        // 调用抽象类
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    // 如果此方法执行异常，任务失败
    @Override
    public boolean execute(MqMessage mqMessage) {
        // 从mqmessage拿到课程id
        long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        // 课程静态化上传到minio
        generateCourseHtml(mqMessage, courseId);
        // 向elasticsearch写索引
        saveCourseIndex(mqMessage, courseId);
        // 向redis写缓存
        //返回true表示任务完成
        return false;
    }

    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 任务幂等性
        //取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化完成，无需处理");
            return;
        }
        // 开始页面静态化
        File htmlFile = coursePublishService.generateCourseHtml(courseId);
        if(htmlFile==null) {
            XueChengPlusException.cast("生成的静态页面为空");
        }
        coursePublishService.uploadCourseHtml(courseId, htmlFile);


        mqMessageService.completedStageOne(taskId);
    }

    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        //任务id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 取出第二个阶段
        int stageTwo = mqMessageService.getStageTwo(taskId);
        // 任务幂等性判断
        if (stageTwo > 0) {
            log.debug("课程索引信息已写入，无需执行...");
        }

        mqMessageService.completedStageTwo(taskId);
    }
}
