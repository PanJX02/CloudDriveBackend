package com.panjx.clouddrive.service.file;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CopyFileRequest;
import com.panjx.clouddrive.pojo.request.FileSearchRequest;
import com.panjx.clouddrive.pojo.request.MoveFileRequest;
import com.panjx.clouddrive.pojo.request.RenameFileRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 批量收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    Result favoriteFiles(List<Long> userFileIds);

    
    /**
     * 批量取消收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    Result unfavoriteFiles(List<Long> userFileIds);
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    Result getFavoriteFiles();

    
    /**
     * 批量获取文件或文件夹的详细信息
     * @param fileIds 文件/文件夹ID列表
     * @return 详细信息结果
     */
    Result getFileDetails(List<Long> fileIds);

    
    /**
     * 批量删除文件或文件夹
     * @param fileIds 文件/文件夹ID列表
     * @return 操作结果
     */
    Result deleteFiles(List<Long> fileIds);
    
    /**
     * 搜索文件
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    Result searchFiles(FileSearchRequest searchRequest);
    
    /**
     * 重命名文件或文件夹
     * @param renameFileRequest 重命名请求参数
     * @return 操作结果
     */
    Result renameFile(RenameFileRequest renameFileRequest);
}
