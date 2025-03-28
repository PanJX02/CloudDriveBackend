package com.panjx.clouddrive;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.UserFile;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileTest {

    @Autowired
    FileMapper fileMapper;
    private final Tika tika = new Tika();

    @Test
    public void findByHSA256Test() {
        UserFile userFile = fileMapper.findByFileSHA256("aaaa1111bbbb2222cccc3333dddd4444eeee5555ffff6666");
        System.out.println(userFile);
    }
    
    @Test
    public void addUserFileTest() {
        UserFile userFile = new UserFile();
        userFile.setFileSHA256("test_sha256_value");
        userFile.setFileName("测试文件.txt");
        userFile.setFileSize(1024L);
        // 设置其他必要的属性
        
        fileMapper.addUserFile(userFile);
        
        // 验证添加是否成功
        UserFile result = fileMapper.findByFileSHA256("test_sha256_value");
        System.out.println(result);
    }
    
    @Test
    public void findByFileIdTest() {
        // 假设数据库中已存在ID为1的文件记录
        UserFile userFile = fileMapper.findByFileId(1L);
        System.out.println(userFile);
    }
    
    @Test
    public void deleteUserFileTest() {
        // 先添加一条记录
        UserFile userFile = new UserFile();
        userFile.setFileSHA256("delete_test_sha256");
        userFile.setFileName("删除测试文件.txt");
        userFile.setFileSize(2048L);
        // 设置其他必要属性
        
        fileMapper.addUserFile(userFile);
        
        // 通过SHA256查找刚添加的记录
        UserFile addedFile = fileMapper.findByFileSHA256("delete_test_sha256");
        
        // 删除该记录
        fileMapper.deleteUserFile(addedFile.getFileId());
        
        // 验证删除是否成功
        UserFile result = fileMapper.findByFileId(addedFile.getFileId());
        System.out.println("删除后查询结果：" + result); // 应为null
    }
    
    @Test
    public void increaseReferCountTest() {
        // 假设数据库中已存在ID为2的文件记录
        // 获取原始引用次数
        UserFile before = fileMapper.findByFileId(2L);
        System.out.println("增加前引用次数：" + before);
        
        // 增加引用次数
        long currentTime = System.currentTimeMillis();
        fileMapper.increaseReferCount(2L, currentTime);
        
        // 验证引用次数已增加
        UserFile after = fileMapper.findByFileId(2L);
        System.out.println("增加后引用次数：" + after);
    }
    
    @Test
    public void decreaseReferCountTest() {
        // 假设数据库中已存在ID为3的文件记录
        // 获取原始引用次数
        UserFile before = fileMapper.findByFileId(3L);
        System.out.println("减少前引用次数：" + before);
        
        // 减少引用次数
        fileMapper.decreaseReferCount(3L);
        
        // 验证引用次数已减少
        UserFile after = fileMapper.findByFileId(3L);
        System.out.println("减少后引用次数：" + after);
    }

    @Test
    public void mimeTypeTest(){
        // 设置MIME类型
        String mime = "txt";
        String mimeType = tika.detect("."+mime);
        System.out.println(mimeType);
    }
}
