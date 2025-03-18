package com.panjx.clouddrive;

import com.panjx.clouddrive.mapper.FolderMapper;
import com.panjx.clouddrive.pojo.UserFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FileListTest {

    @Autowired
    private FolderMapper folderMapper;
    @Test
    public void test() {
        List<UserFile> userFiles = folderMapper.getFileList(12, 0);
        for (UserFile userFile : userFiles){
            System.out.println(userFile);
        }
    }
}
