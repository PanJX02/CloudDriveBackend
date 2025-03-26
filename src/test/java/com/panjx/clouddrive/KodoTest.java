package com.panjx.clouddrive;

import com.panjx.clouddrive.utils.KodoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KodoTest {
    @Autowired
    private KodoUtil kodoUtil;

    @Test
    public void upTest(){
        String bucket = "myclouddrive";
        String upToken = KodoUtil.getUpToken(bucket);
        System.out.println(upToken);
    }

    @Test
    public void downTest() throws QiniuException {
        // domain   下载 domain, eg: qiniu.com【必须】
        // useHttps 是否使用 https【必须】
        // key      下载资源在七牛云存储的 key【必须】
        DownloadUrl url = new DownloadUrl("stl34gw6f.hd-bkt.clouddn.com", false, "铜坐龙.png");
        url.setAttname("铜坐龙.png"); // 配置 attname

        // 带有效期
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        long deadline = System.currentTimeMillis()/1000 + expireInSeconds;
        Auth auth = Auth.create("_5haiQyNJq1Z8ppbbvlIwzmbI5wX3lYd78wqaOJx", "yQqFpdZKu1kvnR3UUqk3hY6NNMe-skPQXtBTTOxQ");
        String urlString = url.buildURL(auth, deadline);
        System.out.println(urlString);


        String key = "铜坐龙.png";
        System.out.println(KodoUtil.getDownloadUrl(key, expireInSeconds));
    }
}
