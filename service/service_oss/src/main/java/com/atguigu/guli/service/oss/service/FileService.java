package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

public interface FileService {
    /**
     * oss文件上传
     * @param inputStream 输入流
     * @param module 文件夹名称
     * @param originalFilename 文件原始名称
     * @return 文件在oss服务器中url
     */
    String upload(InputStream inputStream, String module, String originalFilename);

    /**
     * 删除oss文件
     * @param url 文件url地址
     */
    void remove(String url);
}
