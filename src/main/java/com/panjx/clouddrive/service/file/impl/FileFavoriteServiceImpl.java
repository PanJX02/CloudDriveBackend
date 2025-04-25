package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.file.FileFavoriteService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * 获取收藏的文件列表
     * @return 收藏的文件列表
     */
    @Override
    public Result getFavoriteFiles() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            List<UserFile> favoriteFiles = fileMapper.getFavoriteFiles(userId);
            return Result.success(favoriteFiles);
        } catch (Exception e) {
            log.error("获取收藏文件列表出错", e);
            return Result.error("获取收藏文件列表失败");
        }
    }
} 