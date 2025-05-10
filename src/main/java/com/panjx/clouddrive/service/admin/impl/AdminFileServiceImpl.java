package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateFileRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.admin.AdminFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminFileServiceImpl implements AdminFileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Result getAllFiles(PageRequest pageRequest) {
        // 获取总记录数
        int total = fileMapper.countAllFiles();
        
        // 计算总页数
        int totalPage = (total + pageRequest.getPageSize() - 1) / pageRequest.getPageSize();
        
        // 计算分页的offset
        int offset = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        
        // 查询分页数据
        List<UserFile> files = fileMapper.getAllFiles(offset, pageRequest.getPageSize());
        
        // 封装分页元数据
        PageMeta pageMeta = new PageMeta(total, totalPage, pageRequest.getPageSize(), pageRequest.getPage());
        
        // 封装结果
        FileList fileList = new FileList(files, pageMeta);
        
        return Result.success(fileList);
    }
    
    @Override
    public Result updateFileInfo(UpdateFileRequest updateFileRequest) {
        // 检查文件是否存在
        UserFile existingFile = fileMapper.findByFileId(updateFileRequest.getFileId());
        if (existingFile == null) {
            return Result.error("文件不存在");
        }
        
        // 更新文件信息
        int rows = fileMapper.updateFileInfo(updateFileRequest);
        if (rows > 0) {
            return Result.success("文件信息更新成功");
        } else {
            return Result.error("文件信息更新失败");
        }
    }
} 