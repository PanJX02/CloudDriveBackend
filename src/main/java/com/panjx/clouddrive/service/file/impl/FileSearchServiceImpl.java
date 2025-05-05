package com.panjx.clouddrive.service.file.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.FileSearchRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.file.FileSearchService;
import com.panjx.clouddrive.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileSearchServiceImpl implements FileSearchService {

    @Autowired
    private FileMapper fileMapper;
    
    /**
     * 搜索文件
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    @Override
    public Result searchFiles(FileSearchRequest searchRequest) {
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        try {
            // 检查参数
            if (searchRequest.getKeyword() == null || searchRequest.getKeyword().trim().isEmpty()) {
                return Result.error("搜索关键词不能为空");
            }
            
            log.info("开始搜索文件，搜索关键词：{}, 文件夹ID：{}", 
                    searchRequest.getKeyword(), 
                    searchRequest.getFolderId());
            
            // 执行搜索
            List<UserFile> files = fileMapper.searchFiles(
                    userId, 
                    searchRequest.getKeyword(), 
                    searchRequest.getFolderId()
            );
            
            if (files == null) {
                files = new ArrayList<>();
            }
            
            log.info("搜索完成，共找到 {} 个结果", files.size());
            
            // 构建响应
            FileList response = new FileList();
            response.setList(files);
            
            // 设置分页信息
            PageMeta pageMeta = new PageMeta();
            pageMeta.setTotal(files.size());
            pageMeta.setPageSize(files.size());
            pageMeta.setPage(1);
            pageMeta.setTotalPage(1);
            response.setPageData(pageMeta);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索文件时发生错误", e);
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
} 