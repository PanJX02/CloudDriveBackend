package com.panjx.clouddrive.service.impl;

import com.panjx.clouddrive.config.SecurityConfig;
import com.panjx.clouddrive.mapper.FolderMapper;
import com.panjx.clouddrive.pojo.FileList;
import com.panjx.clouddrive.pojo.PageMeta;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.service.FolderService;
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
}
