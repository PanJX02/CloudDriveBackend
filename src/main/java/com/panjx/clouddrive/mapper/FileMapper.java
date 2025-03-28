package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.File;
import com.panjx.clouddrive.pojo.UserFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

    // 通过文件sha256查找文件
    UserFile findByFileSHA256(String fileSHA256);

    // 添加文件
    void addUserFile(UserFile userFile);

    // 通过文件id查找文件
    UserFile findByFileId(long fileId);

    // 删除文件
    void deleteUserFile(long id);

    // 更新文件引用次数
    void increaseReferCount(long fileId,long lastReferTime);

    // 减少文件引用次数
    void decreaseReferCount(long fileId);

}
