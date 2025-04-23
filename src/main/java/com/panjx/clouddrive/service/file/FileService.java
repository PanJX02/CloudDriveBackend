package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import org.springframework.stereotype.Service;

@Service
public interface FileService {

    /**
     * 处理文件上传的操作
     * @param fileName 文件名
     * @param fileExtension 文件扩展名
     * @param fileSHA256 文件的SHA256值
     * @param file_pid 文件的父目录ID
     * @return 上传结果
     */
    Result upload(String fileName, String fileExtension, String fileSHA256, Long file_pid);
    
    /**
     * 处理文件上传完成后的操作
     */
    Result uploadComplete(UserFile userFile);

    /**
     * 处理文件下载的操作
     */
    Result download(UserFile userFile);

    /**
     * 移动文件
     */
    Result moveFile(MoveFileRequest moveFileRequest);
    
    /**
     * 复制文件
     */
    Result copyFile(CopyFileRequest copyFileRequest);
    
    /**
     * 收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    Result favoriteFile(Long userFileId);
    
    /**
     * 取消收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    Result unfavoriteFile(Long userFileId);
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    Result getFavoriteFiles();
    
    /**
     * 获取文件或文件夹的详细信息
     * @param fileId 文件/文件夹ID
     * @return 详细信息结果
     */
    Result getFileDetail(Long fileId);
}
