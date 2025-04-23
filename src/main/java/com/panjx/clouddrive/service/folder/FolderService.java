package com.panjx.clouddrive.service.folder;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.response.FileList;
import org.springframework.stereotype.Service;

@Service
public interface FolderService {
    FileList getFiles(Long folderId);
    
    /**
     * 创建文件夹
     * @param userId 用户ID
     * @param folderName 文件夹名称
     * @param parentId 父文件夹ID
     * @return 成功返回创建的文件夹记录，失败返回null
     */
    Result createFolder(Long userId, String folderName, Long parentId);
}
