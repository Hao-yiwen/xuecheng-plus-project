package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.IoUtils;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        // 课程基本信息，营销信息
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        // 课程计划信息
        List<TeachplanDto> teachplanDto = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanDto);
        return coursePreviewDto;
    }

    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        // 如果课程的审核状态为已提交不容许提交
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {
            XueChengPlusException.cast("课程找不到");
        }
        // 审核状态
        String auditStatus = courseBaseInfo.getAuditStatus();
        if (auditStatus.equals("202003")) {
            XueChengPlusException.cast("课程已提交，请等待审核");
        }
        // 课程的图片、计划信息没有填写也不允许提交
        String pic = courseBaseInfo.getPic();
        if (StringUtils.isEmpty(pic)) {
            XueChengPlusException.cast("请求上传课程图片");
        }
        // 查询课程计划
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null || teachplanTree.size() == 0) {
            XueChengPlusException.cast("请编写课程计划");
        }

        // 本机构只能提交本机构的课程
        // todo...
        saveCoursePublishMessage(courseId);

        // 查询到课程的基本信息。营销信息。计划信息插入到课程发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        // 机构id
        coursePublishPre.setCompanyId(companyId);
        // 营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMaketJson = JSON.toJSONString(courseMarket);

        coursePublishPre.setMarket(courseMaketJson);
        //计划信息
        String teachplanTreeJson = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeJson);
        // 状态转化为已提交
        coursePublishPre.setStatus("202003");
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        // 更新课程基本信息表的审核状态为已提交
        if (coursePublishPre1 == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 更新基本信息表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");

        courseBaseMapper.updateById(courseBase);
    }

    private void saveCoursePublishMessage(Long courseId) {
        MqMessage coursePublish = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (coursePublish == null) {
            XueChengPlusException.cast(CommonError.UNKNOWN_ERROR);
        }
    }

    @Transactional
    @Override
    public void publish(Long comapnyId, Long courseId) {

        // 查询预发布表数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程没有提交审核");
        }
        //没有审核通过不容许发布
        String status = coursePublishPre.getStatus();
        if (!status.equals("202004")) {
            XueChengPlusException.cast("课程没有审核通过，不能发布~");
        }
        // 向课程发布表写数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        // 先查询课程发布表
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if (coursePublishObj == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        // 向消息表写入数据
        // todo...

        // 将预发布表数据删除
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File htmlFile = null;
        try {
            Configuration configuration = new Configuration(Configuration.getVersion());
            String path = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
            configuration.setDefaultEncoding("utf-8");
            Template template = configuration.getTemplate("course_template.ftl");
            CoursePreviewDto coursePreviewDto = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewDto);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            // 输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            htmlFile = File.createTempFile("coursePublish", ".html");
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IoUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.debug("页面静态化出现问题,课程id{}", courseId);
            e.printStackTrace();
        }
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);

            String upload = mediaServiceClient.upload(multipartFile, "course/" + courseId + ".html");
            if (upload == null) {
                log.debug("远程调用走降级");
                XueChengPlusException.cast("上传静态文件异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传静态文件异常");
        }
    }

    /**
     * 根据课程id查询课程id
     *
     * @param courseId
     * @return
     */
    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish;
    }

    // 解决缓存穿透
//    @Override
//    public CoursePublish getCoursePublishCache(Long courseId) {
//        // 查询布隆过滤器
//
//        Object jsonObj = redisTemplate.opsForValue().get("course" + courseId);
//        if (jsonObj != null) {
//            // 缓存有数据，直接返回
//            String jsonString = jsonObj.toString();
//            if ("null".equals(jsonString)) {
//                return null;
//            }
//            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
//            return coursePublish;
//        } else {
//            // 从数据库查询
//            System.out.println("查询数据库==========");
//            CoursePublish coursePublish = getCoursePublish(courseId);
////            if (coursePublish != null) {
//            redisTemplate.opsForValue().set("course" + courseId, JSON.toJSONString(coursePublish), 30 + new Random().nextInt(100), TimeUnit.SECONDS);
////            }
//            return coursePublish;
//        }
//    }

    // 使用同步锁解决缓存击穿
    // redisson
    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        // 查询布隆过滤器
        Object jsonObj = redisTemplate.opsForValue().get("course" + courseId);
        if (jsonObj != null) {
            // 缓存有数据，直接返回
            String jsonString = jsonObj.toString();
            if ("null".equals(jsonString)) {
                return null;
            }
            CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            RLock lock = redissonClient.getLock("coursequerylock:" + courseId);
            lock.lock();
            try {
                // 再次查一下缓存
                if (jsonObj != null) {
                    // 缓存有数据，直接返回
                    String jsonString = jsonObj.toString();
                    if ("null".equals(jsonString)) {
                        return null;
                    }
                    CoursePublish coursePublish = JSON.parseObject(jsonString, CoursePublish.class);
                    return coursePublish;
                }
                // 从数据库查询
                System.out.println("查询数据库==========");
                CoursePublish coursePublish = getCoursePublish(courseId);
                redisTemplate.opsForValue().set("course" + courseId, JSON.toJSONString(coursePublish), 300 + new Random().nextInt(100), TimeUnit.SECONDS);
                return coursePublish;
            } finally {
                // 手动释放锁
                lock.unlock();
            }
        }
    }
}
