package com.atguigu.guli.service.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.atguigu.guli.service.oss.service.FileService;
import com.atguigu.guli.service.oss.util.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {
        //读取配置信息
        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        ///创建阿里云实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        //判断bucket是否存在
        if (!ossClient.doesBucketExist(bucketname)){
            //不存在创建并设置权限
            ossClient.createBucket(bucketname);
            ossClient.setBucketAcl(bucketname, CannedAccessControlList.PublicRead);
        }
        //构建objectName,文件在oss的相对路径 avatar/2020/7/15/xxx.jpg
        String folder = new DateTime().toString("yyyy/MM/dd");
        String filename = UUID.randomUUID().toString();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String key = module + "/" + folder + "/" + filename + fileExtension;

        //上传文件
        ossClient.putObject(bucketname, key, inputStream);

        //关闭ossClient
        ossClient.shutdown();
        return "https://" + bucketname + "." + endpoint + "/" +key;
    }

    @Override
    public void remove(String url) {
        //读取配置信息
        String endpoint = ossProperties.getEndpoint();
        String keyid = ossProperties.getKeyid();
        String keysecret = ossProperties.getKeysecret();
        String bucketname = ossProperties.getBucketname();

        ///创建阿里云实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);

        //获取文件路径
        String host = "https://" + bucketname + "." + endpoint + "/";
        String key = url.substring(host.length());
        //删除文件
        ossClient.deleteObject(bucketname,key);

        //关闭ossClient
        ossClient.shutdown();
    }
}
