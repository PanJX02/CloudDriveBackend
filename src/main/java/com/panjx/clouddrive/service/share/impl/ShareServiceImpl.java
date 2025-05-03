package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.service.share.ShareCancelService;
import com.panjx.clouddrive.service.share.ShareCreateService;
import com.panjx.clouddrive.service.share.ShareDetailService;
import com.panjx.clouddrive.service.share.ShareListService;
import com.panjx.clouddrive.service.share.ShareSaveService;
import com.panjx.clouddrive.service.share.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件分享服务实现类
 */
@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareCreateService shareCreateService;
    
    @Autowired
    private ShareListService shareListService;
    
    @Autowired
    private ShareDetailService shareDetailService;
    
    @Autowired
    private ShareSaveService shareSaveService;
    
    @Autowired
    private ShareCancelService shareCancelService;

    /**
     * 创建分享
     * @param createShareRequest 创建分享请求
     * @return 创建结果
     */
    @Override
    @Transactional
    public Result createShare(CreateShareRequest createShareRequest) {
        return shareCreateService.createShare(createShareRequest);
    }
    
    /**
     * 获取当前用户的分享列表
     * @param showAll 是否显示全部（包括已过期），true表示显示全部，false表示只显示有效的
     * @return 分享列表
     */
    @Override
    @Transactional
    public Result getUserShares(boolean showAll) {
        return shareListService.getUserShares(showAll);
    }
    
    /**
     * 获取分享的文件列表
     * @param shareId 分享ID
     * @param code 提取码(可选)
     * @param folderId 文件夹ID(可选)，如果提供则获取该文件夹下的文件，否则获取分享根目录文件
     * @return 文件列表
     */
    @Override
    public FileList getShareFiles(Long shareId, String code, Long folderId) {
        return shareDetailService.getShareFiles(shareId, code, folderId);
    }

    /**
     * 保存分享的文件到自己的云盘
     * @param shareId 分享ID
     * @param code 提取码
     * @param fileIds 要保存的文件ID列表
     * @param targetFolderId 目标文件夹ID (保存到的位置)
     * @return 保存结果
     */
    @Override
    @Transactional
    public Result saveShareFiles(Long shareId, String code, List<Long> fileIds, Long targetFolderId) {
        return shareSaveService.saveShareFiles(shareId, code, fileIds, targetFolderId);
    }
    
    /**
     * 取消分享
     * @param shareId 分享ID
     * @param code 提取码(可选，用于验证)
     * @return 取消结果
     */
    @Override
    @Transactional
    public Result cancelShare(Long shareId, String code) {
        return shareCancelService.cancelShare(shareId, code);
    }
} 
