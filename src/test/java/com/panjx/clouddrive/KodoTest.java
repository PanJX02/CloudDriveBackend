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
        String str = KodoUtil.getDownloadUrl(key, expireInSeconds);
        System.out.println(str);
        System.out.println(extractEAndToken(str));
        System.out.println(generateDownloadKey("f987ca5bf4a0082e31e796c84ca54b3329a81665b785b04806ffeba62b6d692b"));
    }

    public static String extractEAndToken(String url) {
        int index = url.indexOf("&e=");
        if (index != -1) {
            return url.substring(index+1); // 从 "e=" 开始截取到末尾
        }
        return null; // 如果未找到 "e="，返回 null
    }
    /**
     * 根据文件SHA256哈希值生成多级目录下载路径
     * 格式：files/前8字符/次8字符/再次8字符/再次8字符/完整SHA256值
     *
     * @param fileSHA256 文件的SHA256哈希值
     * @return 生成的下载路径key
     */
    private String generateDownloadKey(String fileSHA256) {
        if (fileSHA256 == null || fileSHA256.length() < 64) {
            throw new IllegalArgumentException("Invalid SHA256 hash");
        }

        // 按照指定规则分段生成目录
        String level1 = "files";
        String level2 = fileSHA256.substring(0, 8);
        String level3 = fileSHA256.substring(8, 16);
        String level4 = fileSHA256.substring(16, 24);
        String level5 = fileSHA256.substring(24, 32);

        // 组合路径
        return String.format("%s/%s/%s/%s/%s/%s",
                level1, level2, level3, level4, level5, fileSHA256);
    }
}
