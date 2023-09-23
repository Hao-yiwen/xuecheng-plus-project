package com.xuecheng.content.feignclient;


import com.alibaba.nacos.common.http.param.MediaType;
import com.xuecheng.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

// 使用fallback降级的类无法拿到异常
@FeignClient(value = "media-api", configuration = {MultipartSupportConfig.class}, fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @RequestMapping(value = "/media/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA)
    public String upload(@RequestPart("filedata") MultipartFile filedata, @RequestParam(value = "objectName", required = false) String objectName);
}
