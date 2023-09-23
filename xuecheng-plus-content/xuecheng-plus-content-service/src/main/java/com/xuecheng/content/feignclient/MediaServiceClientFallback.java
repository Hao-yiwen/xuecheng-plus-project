package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile filedata, String objectName) {
        return null;
    }
}
