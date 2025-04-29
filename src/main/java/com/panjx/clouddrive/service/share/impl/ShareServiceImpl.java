package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.pojo.response.ShareListResponse;
import com.panjx.clouddrive.service.share.ShareCreateService;
import com.panjx.clouddrive.service.share.ShareDetailService;
import com.panjx.clouddrive.service.share.ShareListService;
import com.panjx.clouddrive.service.share.ShareSaveService;
import com.panjx.clouddrive.service.share.ShareService;
import com.panjx.clouddrive.utils.ShareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShareCreateService shareCreateService;
    
    @Autowired
    private ShareListService shareListService;
    
    @Autowired
    private ShareDetailService shareDetailService;
    
    @Autowired
    private ShareSaveService shareSaveService;

    @Override
    @Transactional
    public Result createShare(CreateShareRequest createShareRequest) {
        return shareCreateService.createShare(createShareRequest);
    }
    
    /**
     * 生成分享名称
     * @param userFiles 用户文件列表
     * @return 分享名称
     */
    private String generateShareName(List<UserFile> userFiles) {
        if (userFiles == null || userFiles.isEmpty()) {
            return "未知文件";
        }
        
        // 获取第一个文件/文件夹的名称
        UserFile firstFile = userFiles.get(0);
        String firstName = firstFile.getFileName();
        if (firstFile.getFolderType() == 0 && firstFile.getFileExtension() != null && !firstFile.getFileExtension().isEmpty()) {
            firstName += "." + firstFile.getFileExtension();
        }
        
        // 如果只有一个文件/文件夹，直接返回名称
        if (userFiles.size() == 1) {
            return firstName;
        }
        
        // 多个文件/文件夹，返回"第一个名称 等XX个文件（夹）"
        String suffix = "文件";
        
        // 检查是否全是文件夹，或全是文件，或混合
        boolean hasFile = false;
        boolean hasFolder = false;
        
        for (UserFile userFile : userFiles) {
            if (userFile.getFolderType() == 0) { // 文件
                hasFile = true;
            } else { // 文件夹
                hasFolder = true;
            }
            
            // 如果已经确认既有文件又有文件夹，可以提前退出循环
            if (hasFile && hasFolder) {
                break;
            }
        }
        
        if (hasFile && hasFolder) {
            // 混合文件和文件夹
            suffix = "文件（夹）";
        } else if (hasFolder) {
            // 全是文件夹
            suffix = "文件夹";
        } // 否则默认是"文件"
        
        return firstName + " 等" + userFiles.size() + "个" + suffix;
    }
    
    @Override
    @Transactional
    public Result getUserShares(boolean showAll) {
        return shareListService.getUserShares(showAll);
    }
    
    @Override
    public FileList getShareFiles(Long shareId, String code, Long folderId) {
        return shareDetailService.getShareFiles(shareId, code, folderId);
    }
    
    /**
     * 将FileShare对象转换为ShareListResponse响应对象
     * @param share 分享信息
     * @return 分享列表响应对象
     */
    private ShareListResponse convertToShareListResponse(FileShare share) {
        ShareListResponse response = new ShareListResponse();
        
        // 设置基本信息
        response.setShareId(share.getShareId());
        response.setShareName(share.getShareName());
        response.setShareTime(share.getShareTime());
        response.setValidType(share.getValidType());
        response.setExpireTime(share.getExpireTime());
        response.setIsExpired(share.getIsExpired());
        response.setShowCount(share.getShowCount());
        response.setCode(share.getCode());
        
        // 设置加密标识
        response.setShareKey(ShareUtil.encryptShareId(share.getShareId()));
        response.setShareKeyWithCode(ShareUtil.encryptShareIdWithCode(share.getShareId(), share.getCode()));
        
        // 查询分享项
        List<ShareItem> shareItems = shareMapper.findShareItemsByShareId(share.getShareId());
        
        // 设置文件数量
        response.setFileCount(shareItems.size());
        
        return response;
    }

    @Override
    @Transactional
    public Result saveShareFiles(Long shareId, String code, List<Long> fileIds, Long targetFolderId) {
        return shareSaveService.saveShareFiles(shareId, code, fileIds, targetFolderId);
    }
} 
