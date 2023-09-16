package com.xuecheng.media.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UploadFileParamsDto {
    private String filename;

    private String fileType;

    private Long fileSize;

    private String tags;

    private String username;

    private String remark;

}
