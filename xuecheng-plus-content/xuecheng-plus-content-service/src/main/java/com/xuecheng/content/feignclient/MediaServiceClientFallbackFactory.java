package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            // 发生熔断上游服务调用此方法降级
            @Override
            public String upload(MultipartFile filedata, String objectName) {
                log.debug("远程调用上传文件接口发生熔断:{}",throwable.toString(),throwable);
                return null;
            }
        };
    }
}
