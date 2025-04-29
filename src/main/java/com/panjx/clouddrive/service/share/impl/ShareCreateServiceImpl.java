package com.panjx.clouddrive.service.share.impl;

import com.panjx.clouddrive.mapper.FileMapper;
import com.panjx.clouddrive.mapper.ShareMapper;
import com.panjx.clouddrive.pojo.FileShare;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.ShareItem;
import com.panjx.clouddrive.pojo.UserFile;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.response.ShareResponse;
import com.panjx.clouddrive.service.share.ShareCreateService;
import com.panjx.clouddrive.utils.SecurityUtil;
import com.panjx.clouddrive.utils.ShareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShareCreateServiceImpl implements ShareCreateService {

    @Autowired
    private ShareMapper shareMapper;
    
    @Autowired
    private FileMapper fileMapper;

    @Override
    @Transactional
    public Result createShare(CreateShareRequest createShareRequest) {
        log.info("创建分享请求: {}", createShareRequest);
        
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 验证文件列表
        List<Long> userFileIds = createShareRequest.getUserFileIds();
        if (userFileIds == null || userFileIds.isEmpty()) {
            return Result.error("分享文件列表不能为空");
        }
        
        // 验证文件是否存在且属于当前用户
        List<UserFile> userFiles = new ArrayList<>();
        for (Long fileId : userFileIds) {
            UserFile userFile = fileMapper.findById(fileId);
            if (userFile == null) {
                return Result.error("文件不存在");
            }
            if (!userId.equals(userFile.getUserId())) {
                return Result.error("无权分享他人文件");
            }
            userFiles.add(userFile);
        }
        
        // 生成share_name
        String shareName = generateShareName(userFiles);
        
        // 生成提取码
        String code = ShareUtil.generateCode();
        
        // 计算过期时间
        Long expireTime = ShareUtil.calculateExpireTime(createShareRequest.getValidType());
        
        // 创建分享记录
        FileShare fileShare = new FileShare();
        fileShare.setUserId(userId);
        fileShare.setShareName(shareName);
        fileShare.setValidType(createShareRequest.getValidType());
        fileShare.setExpireTime(expireTime);
        fileShare.setShareTime(System.currentTimeMillis());
        fileShare.setCode(code);
        fileShare.setShowCount(0);
        fileShare.setIsExpired(0);
        
        // 添加分享记录（自动生成shareId并回填）
        shareMapper.addShare(fileShare);
        
        // 获取数据库自动生成的分享ID
        Long shareId = fileShare.getShareId();
        
        // 添加分享项
        for (Long fileId : userFileIds) {
            ShareItem shareItem = new ShareItem();
            shareItem.setShareId(shareId);
            shareItem.setUserFileId(fileId);
            shareMapper.addShareItem(shareItem);
        }
        
        // 生成不包含提取码的加密标识
        String encryptedShareId = ShareUtil.encryptShareId(shareId);
        
        // 生成包含提取码的加密标识
        String encryptedShareIdWithCode = ShareUtil.encryptShareIdWithCode(shareId, code);
        
        // 构建返回对象
        ShareResponse shareResponse = new ShareResponse();
        shareResponse.setShareKey(encryptedShareId);
        shareResponse.setShareKeyWithCode(encryptedShareIdWithCode);
        shareResponse.setCode(code);
        shareResponse.setShareName(shareName);
        
        return Result.success(shareResponse);
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
} 