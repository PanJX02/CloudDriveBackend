package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.file.FileFavoriteService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileFavoriteServiceImpl implements FileFavoriteService {
    
    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    @Override
    public Result favoriteFile(Long userFileId) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            UserFile userFile = fileMapper.findUserFileById(userFileId);
            
            if (userFile == null) {
                return Result.error("文件不存在");
            }
            
            if (!userFile.getUserId().equals(userId)) {
                return Result.error("无权限操作该文件");
            }
            
            fileMapper.updateFavoriteFlag(userFileId, 1);
            return Result.success("收藏成功", null);
        } catch (Exception e) {
            log.error("收藏文件出错", e);
            return Result.error("收藏文件失败");
        }
    }
    
    /**
     * 批量收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result favoriteFiles(List<Long> userFileIds) {
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("未指定要收藏的文件");
        }
        
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            List<String> errorMessages = new ArrayList<>();
            int successCount = 0;
            
            // 遍历处理每个文件/文件夹
            for (Long userFileId : userFileIds) {
                // 验证用户是否有权限操作该文件
                UserFile userFile = fileMapper.findUserFileById(userFileId);
                if (userFile == null) {
                    errorMessages.add("ID为" + userFileId + "的文件不存在");
                    continue;
                }
                
                if (!userFile.getUserId().equals(userId)) {
                    errorMessages.add("没有权限操作ID为" + userFileId + "的文件");
                    continue;
                }
                
                // 执行收藏操作
                try {
                    fileMapper.updateFavoriteFlag(userFileId, 1);
                    successCount++;
                } catch (Exception e) {
                    log.error("收藏ID为{}的文件失败: {}", userFileId, e.getMessage(), e);
                    errorMessages.add("收藏ID为" + userFileId + "的文件失败");
                }
            }
            
            // 构建结果消息
            if (successCount == 0) {
                return Result.error("收藏失败: " + String.join(", ", errorMessages));
            } else if (errorMessages.isEmpty()) {
                return Result.success("成功收藏" + successCount + "个文件");
            } else {
                return Result.success("成功收藏" + successCount + "个文件，但有以下错误: " + String.join(", ", errorMessages));
            }
        } catch (Exception e) {
            log.error("批量收藏文件出错", e);
            return Result.error("批量收藏文件失败");
        }
    }
    
    /**
     * 取消收藏文件
     * @param userFileId 用户文件ID
     * @return 操作结果
     */
    @Override
    public Result unfavoriteFile(Long userFileId) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            UserFile userFile = fileMapper.findUserFileById(userFileId);
            
            if (userFile == null) {
                return Result.error("文件不存在");
            }
            
            if (!userFile.getUserId().equals(userId)) {
                return Result.error("无权限操作该文件");
            }
            
            fileMapper.updateFavoriteFlag(userFileId, 0);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏文件出错", e);
            return Result.error("取消收藏文件失败");
        }
    }
    
    /**
     * 批量取消收藏文件
     * @param userFileIds 用户文件ID列表
     * @return 操作结果
     */
    @Override
    @Transactional
    public Result unfavoriteFiles(List<Long> userFileIds) {
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("未指定要取消收藏的文件");
        }
        
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            List<String> errorMessages = new ArrayList<>();
            int successCount = 0;
            
            // 遍历处理每个文件/文件夹
            for (Long userFileId : userFileIds) {
                // 验证用户是否有权限操作该文件
                UserFile userFile = fileMapper.findUserFileById(userFileId);
                if (userFile == null) {
                    errorMessages.add("ID为" + userFileId + "的文件不存在");
                    continue;
                }
                
                if (!userFile.getUserId().equals(userId)) {
                    errorMessages.add("没有权限操作ID为" + userFileId + "的文件");
                    continue;
                }
                
                // 执行取消收藏操作
                try {
                    fileMapper.updateFavoriteFlag(userFileId, 0);
                    successCount++;
                } catch (Exception e) {
                    log.error("取消收藏ID为{}的文件失败: {}", userFileId, e.getMessage(), e);
                    errorMessages.add("取消收藏ID为" + userFileId + "的文件失败");
                }
            }
            
            // 构建结果消息
            if (successCount == 0) {
                return Result.error("取消收藏失败: " + String.join(", ", errorMessages));
            } else if (errorMessages.isEmpty()) {
                return Result.success("成功取消收藏" + successCount + "个文件");
            } else {
                return Result.success("成功取消收藏" + successCount + "个文件，但有以下错误: " + String.join(", ", errorMessages));
            }
        } catch (Exception e) {
            log.error("批量取消收藏文件出错", e);
            return Result.error("批量取消收藏文件失败");
        }
    }
    
    /**
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    @Override
    public Result getFavoriteFiles() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            List<UserFile> favoriteFiles = fileMapper.getFavoriteFiles(userId);
            
            // 处理敏感信息
            for (UserFile file : favoriteFiles) {
                // 屏蔽敏感字段
                file.setUserId(null);
                file.setFileCategory(null);
                file.setDeleteFlag(null);
                file.setRecoveryTime(null);
                file.setFavoriteFlag(null);
                file.setFileMD5(null);
                file.setFileSHA1(null);
                file.setFileSHA256(null);
                file.setStorageId(null);
                file.setFileCover(null);
                file.setReferCount(null);
                file.setStatus(null);
                file.setTranscodeStatus(null);
                file.setFileCreateTime(null);
                file.setLastReferTime(null);
            }
            
            // 使用FileList格式封装数据
            FileList fileList = new FileList();
            PageMeta pageMeta = new PageMeta(favoriteFiles.size(), 1, favoriteFiles.size(), 1);
            fileList.setList(favoriteFiles);
            fileList.setPageData(pageMeta);
            
            return Result.success(fileList);
        } catch (Exception e) {
            log.error("获取收藏文件列表出错", e);
            return Result.error("获取收藏文件列表失败");
        }
    }
} 