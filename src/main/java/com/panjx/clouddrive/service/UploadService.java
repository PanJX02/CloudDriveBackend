package com.panjx.clouddrive.service;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;

public interface UploadService {

    /**
     * 处理文件上传的操作
     * @param fileName 文件名
     * @param fileExtension 文件扩展名
     * @param fileSHA256 文件的SHA256值
     * @param file_pid 文件的父目录ID
     * @return 上传结果
     */
    Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid);

    /**
     * 处理文件上传完成后的操作
     */
    Result uploadComplete(UserFile userFile);
}
