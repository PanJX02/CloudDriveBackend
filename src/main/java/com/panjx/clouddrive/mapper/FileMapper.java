package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.UserFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface FileMapper {

    // 通过文件sha256查找文件
    UserFile findByFileSHA256(String fileSHA256);

    // 添加文件
    void addUserFile(UserFile userFile);
    
    // 添加文件记录并返回生成的ID
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    Long addFile(UserFile file);

    // 通过文件id查找文件
    UserFile findByFileId(long fileId);

    //通过UserFileID查找文件
    UserFile findByUserFileId(long d);

    // 通过PID查找文件
    UserFile findByPid(long filePid);
    
    // 根据ID查找用户文件信息
    UserFile findUserFileById(long id);
    
    // 删除文件
    void deleteUserFile(long id);

    // 更新文件引用次数
    void increaseReferCount(long fileId,long lastReferTime);

    // 减少文件引用次数
    void decreaseReferCount(long fileId);

    // 获取指定父目录ID下的所有文件和文件夹
    List<UserFile> findByFilePid(long filePid);
    
    // 移动文件
    void moveFile(long fileId, long targetFolderId);
    
    // 检查是否是目标文件夹的子文件夹
    List<UserFile> checkIsChildFolder(long folderId, long targetFolderId);

    // 更新文件收藏状态
    void updateFavoriteFlag(long id, int favoriteFlag);
    
    // 获取用户收藏的文件列表
    List<UserFile> getFavoriteFiles(long userId);

}
