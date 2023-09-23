package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 测试远程调用
 */
@SpringBootTest
public class FileUploadTest {

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void Test() {
        File file = new File("/Users/yw.hao/Documents/data/110.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);

        String upload = mediaServiceClient.upload(multipartFile, "course/110.html");
        System.out.println(upload);
    }
}
