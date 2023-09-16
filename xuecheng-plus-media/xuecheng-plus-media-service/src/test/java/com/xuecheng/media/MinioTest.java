package com.xuecheng.media;

import com.alibaba.nacos.common.utils.IoUtils;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MinioTest {

    static MinioClient minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000").credentials("minio", "hyw650022").build();

    @Test
    public void test_upload() throws Exception {
//        通过扩展名称得到媒体资源类型 mimeType
        ContentInfo extensionMath = ContentInfoUtil.findExtensionMatch(".jpeg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMath != null) {
            mimeType = extensionMath.getMimeType();
        }

//        上传文件信息
        UploadObjectArgs yiwen = UploadObjectArgs.builder().bucket("yiwen").filename("/Users/yw.hao/Documents/WechatIMG1.jpeg").object("test/01/test.jpeg").contentType(mimeType).build();

//        上传文件参数信息
        minioClient.uploadObject(yiwen);
    }

    @Test
    public void test_delete() throws Exception {
        RemoveObjectArgs yiwen = RemoveObjectArgs.builder().bucket("yiwen").object("test.jpeg").build();
        //        删除文件
        minioClient.removeObject(yiwen);
    }

    @Test
    public void test_getFile() throws Exception {
        GetObjectArgs yiwen = GetObjectArgs.builder().bucket("yiwen").object("test/01/test.jpeg").build();

        FilterInputStream inputStream = minioClient.getObject(yiwen);

        FileOutputStream outputStream = new FileOutputStream(new File("/Users/yw.hao/Downloads/test.jpeg"));

        IoUtils.copy(inputStream, outputStream);

//        校验文件的完整性
        String source_md5 = DigestUtils.md5Hex(new FileInputStream(new File("/Users/yw.hao/Documents/WechatIMG1.jpeg")));
//        本地文件md5
        String local_md5 = DigestUtils.md5Hex(new FileInputStream(new File("/Users/yw.hao/Downloads/test.jpeg")));
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        }
    }

    //    将分块文件上传到minio
    @Test
    public void uploadChunk() throws Exception {
        for (int i = 0; i < 4; i++) {
            UploadObjectArgs yiwen = UploadObjectArgs.builder().bucket("yiwen").filename("/Users/yw.hao/Desktop/chunk/" + i).object("chunk/" + i).build();
            minioClient.uploadObject(yiwen);
            System.out.println("上传分块" + i + "成功");
        }
    }

    //    调用minio接口合并分块
    @Test
    public void testMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComposeSource> sources = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ComposeSource yiwen = ComposeSource.builder().bucket("yiwen").object("chunk/" + i).build();
            sources.add(yiwen);
        }
//        指定合并后的objectName
        ComposeObjectArgs yiwen = ComposeObjectArgs.builder().bucket("yiwen").object("1.mov").sources(sources).build();
        minioClient.composeObject(yiwen);
    }


//    批量清理分块文件

}
