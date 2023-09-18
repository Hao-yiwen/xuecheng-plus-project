package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * 视频处理任务类
 */
@Slf4j
@Component
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 视频处理任务
     *
     * @throws Exception
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); // 执行器的序号
        int shardTotal = XxlJobHelper.getShardTotal(); // 执行器的总数

        // 确定cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();

        // 查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);

        // 任务数量
        int size = mediaProcessList.size();
        log.debug("收到的视频处理任务书" + size);
        if (size <= 0) {
            return;
        }

        // 创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        // 使用一个计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            // 将任务加入线程池
            executorService.execute(() -> {
                try {
                    // 任务执行逻辑
                    Long taskId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    // 开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", taskId);
                        return;
                    }
                    // 执行视频转码
                    // 任务bucket
                    String bucket = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();
                    // 下载minio视频到本地
                    File file = mediaFileService.downloadFileFromMinIO(bucket, filePath);
                    if (file == null) {
                        log.debug("下载视频出错!!!");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频到本地失败");
                        return;
                    }
                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    //先创建一个临时文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("mp4", ".mp4");

                    } catch (IOException e) {
                        log.debug("创建临时文件异常");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败");
                        // 保存任务状态为成功
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;
                    }
                    //上传minio
                    String filePathByMd5 = getFilePathByMd5(fileId, ".mp4");
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, filePathByMd5);
                    if (!b1) {
                        log.debug("视频转码失败");
                        // 保存任务状态为成功
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频上传到minio失败");
                        return;
                    }
                    // mp4url
                    //保存任务处理结果
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, filePathByMd5, "处理成功");
                } finally {
                    // 技术器-1
                    countDownLatch.countDown();
                }
            });
        });
        //阻塞,指定最大阻塞时间
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    public String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }

}
