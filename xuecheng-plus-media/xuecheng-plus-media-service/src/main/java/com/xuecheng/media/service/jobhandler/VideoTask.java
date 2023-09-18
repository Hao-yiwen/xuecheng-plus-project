package com.xuecheng.media.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 视频处理任务类
 */
@Slf4j
@Component
public class VideoTask {

    /**
     * 视频处理任务
     * @throws Exception
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); // 执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal(); // 执行器的总数

        System.out.println("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
    }

}
