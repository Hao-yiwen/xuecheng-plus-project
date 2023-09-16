package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BigFileTest {
    //    分块下载
    @Test
    public void testChunk() throws Exception {
        File sourceFile = new File("/Users/yw.hao/Desktop/2.mov");
//        分块文件存储路径
        String chunkFilePath = "/Users/yw.hao/Desktop/chunk/";
//        分开文件大小
        int chunkSize = 1024 * 1024 * 5;
//        分开文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        RandomAccessFile r = new RandomAccessFile(sourceFile, "r");
//        缓冲区
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            RandomAccessFile raf_r = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = r.read(bytes)) != -1) {
                raf_r.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_r.close();
        }
        r.close();
    }

    //    分块合并
    @Test
    public void testMerge() throws IOException {
        //        分块文件存储路径
        String chunkFilePath = "/Users/yw.hao/Desktop/chunk/";
        File chunkFolder = new File(chunkFilePath);
        File sourceFile = new File("/Users/yw.hao/Desktop/2.mov");
        File mergeFile = new File("/Users/yw.hao/Desktop/3.mov");
        File[] files = chunkFolder.listFiles();
        List<File> fileList = Arrays.asList(files);

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
//        向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];

//        便利分块文件，合并
        for (File file : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();
//        合并文件完成
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
        String md5_source = DigestUtils.md5Hex(fileInputStream_source);
        if (md5_merge.equals(md5_source)) {
            System.out.println("文件合并完成");
        }
    }
}
