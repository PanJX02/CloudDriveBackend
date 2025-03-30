package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.StorageMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.Storage;
import com.panjx.clouddrive.pojo.UploadResponse;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.FileService;
import com.panjx.clouddrive.utils.KodoUtil;
import com.panjx.clouddrive.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private UserMapper userMapper;

    
    private final Tika tika = new Tika();
    
    @Override
    public Result upload(String fileName,String fileExtension, String fileSHA256, Long file_pid) {
        log.info("根据文件SHA256查询文件");
        UserFile existingFile = fileMapper.findByFileSHA256(fileSHA256);
        log.info("查询结果：{}", existingFile);
        // 当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (existingFile != null) {
            log.info("查询到文件ID：{}", existingFile.getId());
            
            // 创建新的用户文件记录
            UserFile userFile = new UserFile();
            // 设置用户ID
            userFile.setUserId(userId);
            // 设置文件ID
            userFile.setFileId(existingFile.getFileId());
            // 设置文件名
            userFile.setFileName(fileName);
            // 设置文件扩展名
            userFile.setFileExtension(fileExtension);
            // 设置父文件夹ID
            userFile.setFilePid(file_pid);
            // 设置文件类型为
            userFile.setFolderType(0); // 0表示文件
            // 设置删除标志为
            userFile.setDeleteFlag(2); // 2表示正常
            // 设置创建时间为当前时间
            userFile.setCreateTime(System.currentTimeMillis());
            // 设置最后更新时间为当前时间
            userFile.setLastUpdateTime(System.currentTimeMillis());

            
            // 增加引用计数
            fileMapper.increaseReferCount(existingFile.getFileId(), System.currentTimeMillis());
            
            // 添加到用户文件表
            fileMapper.addUserFile(userFile);

            //更新用户使用空间
            userMapper.updateUserSpace(userId, existingFile.getFileSize());
            
            return Result.success(UploadResponse.fileExists());
        } else {
            log.info("未查询到文件，获取上传token");
            //查找默认存储配置
            Storage storage = storageMapper.findDefaultStorage();

            // 获取七牛云上传token
            String uploadToken = KodoUtil.getUpToken(storage.getBucket());
            String[] domain = {storage.getEndpoint()};
            // 获取域名
            // 返回上传token给前端
            return Result.success(UploadResponse.withToken(storage.getStorageId(),domain,uploadToken));
        }
    }
    
    @Override
    @Transactional
    public Result uploadComplete(UserFile userFile) {
        log.info("处理上传完成: {}", userFile.getFileName());
        
        // 获取当前用户ID并设置
        Long userId = SecurityUtils.getCurrentUserId();
        userFile.setUserId(userId);
        
        // 设置文件相关属性
        userFile.setFolderType(0); // 0表示文件
        userFile.setDeleteFlag(2); // 2表示正常
        
        // 设置时间信息
        long currentTime = System.currentTimeMillis();
        userFile.setCreateTime(currentTime);
        userFile.setLastUpdateTime(currentTime);
        userFile.setFileCreateTime(currentTime); // 设置文件创建时间
        
        // 设置引用计数为1 (初始值)
        userFile.setReferCount(1); 
        
        // 设置转码状态为未转码
        userFile.setTranscodeStatus(0);
        
        // 保存文件记录，fileId会在保存过程中被设置到userFile对象
        fileMapper.addFile(userFile);
        
        log.info("已保存文件记录，文件ID: {}", userFile.getFileId());
        
        // 添加到用户文件表
        fileMapper.addUserFile(userFile);
        
        // 更新用户使用空间
        userMapper.updateUserSpace(userId, userFile.getFileSize());
        
        log.info("文件上传完成，已保存记录。文件ID: {}", userFile.getFileId());
        
        return Result.success("文件上传成功",null);
    }
}
