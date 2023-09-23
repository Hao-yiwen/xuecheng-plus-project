package com.xuecheng.content;

import com.alibaba.nacos.common.utils.IoUtils;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@SpringBootTest
public class FreeMarkerTest {
    @Autowired
    CoursePublishService coursePublishService;

    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        String path = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("course_template.ftl");
        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(1L);
        HashMap<String,Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        // 输入流
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        FileOutputStream outputStream = new FileOutputStream(new File("/Users/yw.hao/Documents/data/110.html"));
        IoUtils.copy(inputStream,outputStream);
    }
}
