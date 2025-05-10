package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.FileShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.pojo.response.ShareList;
import com.panjx.clouddrive.service.admin.AdminShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminShareServiceImpl implements AdminShareService {

    @Autowired
    private FileShareMapper fileShareMapper;

    @Override
    public Result getAllShares(PageRequest pageRequest) {
        // 获取分页参数
        int page = pageRequest.getPage();
        int pageSize = pageRequest.getPageSize();
        
        // 计算分页起始位置
        int offset = (page - 1) * pageSize;
        
        // 查询总数量
        int total = fileShareMapper.countFileShares();
        
        // 计算总页数
        int totalPage = (total + pageSize - 1) / pageSize;
        
        // 获取分页数据
        List<FileShare> shares = fileShareMapper.getFileSharesByPage(offset, pageSize);
        
        // 构建分页元数据
        PageMeta pageMeta = new PageMeta(total, totalPage, pageSize, page);
        
        // 构建分享列表返回对象
        ShareList shareList = new ShareList(shares, pageMeta);
        
        // 使用Result包装结果
        return Result.success(shareList);
    }
    
    @Override
    public Result updateShare(UpdateShareRequest updateShareRequest) {
        // 参数校验
        if (updateShareRequest.getShareId() == null) {
            return Result.error("分享ID不能为空");
        }
        
        // 查询分享信息是否存在
        FileShare existingShare = fileShareMapper.getFileShareById(updateShareRequest.getShareId());
        if (existingShare == null) {
            return Result.error("分享信息不存在");
        }
        
        // 构建更新对象
        FileShare fileShare = new FileShare();
        fileShare.setShareId(updateShareRequest.getShareId());
        fileShare.setShareName(updateShareRequest.getShareName());
        fileShare.setUserId(updateShareRequest.getUserId());
        fileShare.setValidType(updateShareRequest.getValidType());
        fileShare.setExpireTime(updateShareRequest.getExpireTime());
        fileShare.setShareTime(updateShareRequest.getShareTime());
        fileShare.setCode(updateShareRequest.getCode());
        fileShare.setShowCount(updateShareRequest.getShowCount());
        fileShare.setIsExpired(updateShareRequest.getIsExpired());
        
        // 执行更新操作
        int rows = fileShareMapper.updateFileShare(fileShare);
        
        // 返回结果
        if (rows > 0) {
            return Result.success("更新分享信息成功");
        } else {
            return Result.error("更新分享信息失败");
        }
    }
} 