package com.panjx.clouddrive.service.folder.impl;

import com.panjx.clouddrive.mapper.FolderMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.folder.FolderService;
import com.panjx.clouddrive.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderMapper folderMapper;

    @Override
    public FileList getFiles(Long folderId) {
        // 获取当前用户信息
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return null;
        }
        List<UserFile> files = folderMapper.getFileList(userId,folderId);
        if (files != null) {
            FileList fileList = new FileList();
            PageMeta pageMeta = new PageMeta(files.size(),1,files.size(),1);
            fileList.setList(files);
            fileList.setPageData(pageMeta);
            return fileList;
        }
        return null;
    }
    
    @Override
    public Result createFolder(Long userId, String folderName, Long parentId) {
        // 创建文件夹记录
        UserFile folder = new UserFile();
        folder.setUserId(userId);
        folder.setFileName(folderName);
        folder.setFilePid(parentId);
        folder.setFolderType(1); // 1表示文件夹
        folder.setDeleteFlag(0); // 0表示正常
        folder.setCreateTime(System.currentTimeMillis());
        folder.setLastUpdateTime(System.currentTimeMillis());
        
        // 插入数据库
        int result = folderMapper.createFolder(folder);
        if (result > 0) {
            return Result.success("成功创建文件夹");
        }
        return Result.error("创建文件夹失败");
    }
}
