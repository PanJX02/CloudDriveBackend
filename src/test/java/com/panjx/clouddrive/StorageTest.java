package com.panjx.clouddrive;

import com.panjx.clouddrive.mapper.StorageMapper;
import com.panjx.clouddrive.pojo.Storage;
import com.panjx.clouddrive.utils.KodoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StorageTest {
    @Autowired
    private StorageMapper storageMapper;

    @Test
    public void findByStorageId() {
        // 查找storage
        Storage storage = storageMapper.finByStorageId(1L);
        System.out.println(storage);
    }

    // 查找默认的storage
    @Test
    public void findDefaultStorage() {
        // 查找默认的storage
        Storage storage = storageMapper.findDefaultStorage();
        System.out.println(storage);
        // 获取七牛云上传token
        String uploadToken = KodoUtil.getUpToken(storage.getEndpoint());
        System.out.println(uploadToken);
    }
}
