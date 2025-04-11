package com.panjx.clouddrive.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.util.Auth;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component  // 必须添加此注解
public class KodoUtil {
    String bucket = "myclouddrive";

    private static final String accessKeyFromEnv = System.getenv("KODO_ACCESS_KEY");
    private static final String secretKeyFromEnv = System.getenv("KODO_SECRET_KEY");

    private static final Auth auth = Auth.create(accessKeyFromEnv, secretKeyFromEnv);

    private static String endpoint;

    private static String bucketName;

    private static String region;

    // 非静态字段接收配置
    @Value("${qiniu.kodo.endpoint}")
    private String tempEndpoint;

    @Value("${qiniu.kodo.bucketName}")
    private String tempBucketName;

    @Value("${qiniu.kodo.region}")
    private String tempRegion;

    // 在初始化方法中赋值给静态变量
    @PostConstruct
    public void init() {
        endpoint = this.tempEndpoint;
        bucketName = this.tempBucketName;
        region = this.tempRegion;
    }

    // 获取上传凭证
    public static String getUpToken(String bucket) {
        return auth.uploadToken(bucket);
    }

    // 获取下载链接
    public static String getDownloadUrl(String key,long expireInSeconds) throws QiniuException {
        DownloadUrl url = new DownloadUrl(endpoint, true, key);
        url.setAttname(key); // 配置 attname

        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        return url.buildURL(auth, deadline);
    }
}
