package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.StorageMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.DownloadService;
import com.panjx.clouddrive.service.FileService;
import com.panjx.clouddrive.service.UploadService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private DownloadService downloadService;



    @Override
    public Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid) {
        return uploadService.upload(fileName,fileExtension,fileSHA256,file_pid);
    }

    @Override
    public Result uploadComplete(UserFile userFile) {
        return uploadService.uploadComplete(userFile);
    }


    /**
     * 处理文件下载的操作
     * @param userFile 用户文件对象
     * @return 下载结果
     */
    @Override
    public Result download(UserFile userFile) {
        return downloadService.download(userFile);
    }

}
