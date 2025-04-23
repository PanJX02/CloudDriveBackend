package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.UserFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper {

    List<UserFile> getFileList(long userId, long filePid);
    
    int createFolder(UserFile userFile);
}
