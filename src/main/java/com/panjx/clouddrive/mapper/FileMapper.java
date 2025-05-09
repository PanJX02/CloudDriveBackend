package com.panjx.clouddrive.mapper;

import com.panjx.clouddrive.pojo.UserFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    // 通过文件sha256查找文件
    UserFile findByFileSHA256(String fileSHA256);

    // 添加文件，并返回生成的ID
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUserFile(UserFile userFile);
    
    // 添加文件记录并返回生成的ID
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    Long addFile(UserFile file);

    // 通过文件id查找文件
    UserFile findByFileId(long fileId);

    //通过UserFileID查找文件
    UserFile findByUserFileId(long d);
    
    // 根据ID查找用户文件
    @Select("SELECT * FROM user_file WHERE id = #{id}")
    UserFile findById(Long id);

    // 根据多个ID查找用户文件列表
    List<UserFile> findUserFilesByIds(@Param("ids") List<Long> ids);

    // 通过PID查找文件
    UserFile findByPid(long filePid);
    
    // 根据ID查找用户文件信息
    UserFile findUserFileById(long id);
    
    // 删除文件
    void deleteUserFile(long id);

    // 将文件标记为回收站
    void moveToRecycleBin(@Param("id") long id, @Param("recoveryTime") long recoveryTime);

    // 从回收站恢复文件
    void restoreFromRecycleBin(long id);

    // 获取用户回收站中的文件
    List<UserFile> getRecycleBinFiles(long userId);

    // 获取过期的回收站文件
    List<UserFile> getExpiredRecycleBinFiles(@Param("currentTime") long currentTime);

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
    
    // 标记文件为待删除状态
    void markFileAsToBeDeleted(long fileId);

    //标记文件为未删除状态
    void markFileAsNotToBeDeleted(long fileId);
    
    // 获取指定文件夹下所有子文件和子文件夹
    List<UserFile> findAllByFilePidRecursive(long folderId);
    
    // 根据关键词搜索文件和扩展名
    List<UserFile> searchFiles(@Param("userId") long userId, 
                              @Param("keyword") String keyword, 
                              @Param("folderId") Long folderId);
                              
    // 更新文件名
    int updateFileName(UserFile userFile);
    
    // 获取所有文件的总数（管理员功能）
    @Select("SELECT COUNT(*) FROM file")
    int countAllFiles();
    
    // 分页获取所有文件（管理员功能）
    List<UserFile> getAllFiles(@Param("offset") int offset, @Param("limit") Integer limit);
}
