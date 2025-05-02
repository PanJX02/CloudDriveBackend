package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.pojo.request.SaveShareFilesRequest;
import com.panjx.clouddrive.pojo.response.FileList;
import com.panjx.clouddrive.service.share.ShareService;
import com.panjx.clouddrive.utils.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文件分享控制器
 */
@RestController
@RequestMapping("/share")
public class ShareController {

    @Autowired
    private ShareService shareService;
    
    /**
     * 创建分享
     * @param createShareRequest 创建分享请求
     * @return 分享结果
     */
    @PostMapping
    public Result createShare(@RequestBody CreateShareRequest createShareRequest) {
        return shareService.createShare(createShareRequest);
    }
    
    /**
     * 获取当前用户的分享列表
     * @param showAll 是否显示全部（包括已过期），不传默认为true
     * @return 分享列表
     */
    @GetMapping("/list")
    public Result getUserShares(@RequestParam(required = false, defaultValue = "true") boolean showAll) {
        return shareService.getUserShares(showAll);
    }
    
    /**
     * 获取分享的文件列表
     * @param shareKey 加密的分享标识，可以是不包含提取码的shareKey或包含提取码的shareKeyWithCode
     * @param code 提取码(当使用不包含提取码的shareKey时必须提供)
     * @param folderId 文件夹ID(可选)，如果提供则获取该文件夹下的文件，否则获取分享根目录文件
     * @return 文件列表
     */
    @GetMapping("/files")
    public Result getShareFiles(
            @RequestParam String shareKey,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long folderId) {
        
        FileList fileList;
        
        // 检查shareKey是否包含提取码
        if (ShareUtil.isShareKeyWithCode(shareKey)) {
            // 从shareKeyWithCode提取分享ID和提取码
            Object[] shareInfo = ShareUtil.getShareInfoFromKeyWithCode(shareKey);
            if (shareInfo == null) {
                return Result.error("无效的分享链接");
            }
            
            Long shareId = (Long) shareInfo[0];
            String extractedCode = (String) shareInfo[1];
            
            // 使用解析出的shareId和提取码
            fileList = shareService.getShareFiles(shareId, extractedCode, folderId);
        } else {
            // 使用不包含提取码的shareKey
            Long shareId = ShareUtil.getShareIdFromKey(shareKey);
            if (shareId == null) {
                return Result.error("无效的分享链接");
            }
            
            // 使用传入的提取码
            fileList = shareService.getShareFiles(shareId, code, folderId);
        }
        
        if (fileList != null) {
            return Result.success(fileList);
        }
        return Result.error("获取分享文件列表失败");
    }
    
    /**
     * 保存分享的文件到自己的云盘
     * @param request 保存分享文件请求
     * @return 保存结果
     */
    @PostMapping("/save")
    public Result saveShareFiles(@RequestBody SaveShareFilesRequest request) {
        // 验证shareKey并获取shareId和code
        Long shareId;
        String code = request.getCode(); // 优先使用请求中单独提供的code
        
        // 检查shareKey是否包含提取码
        if (ShareUtil.isShareKeyWithCode(request.getShareKey())) {
            // 从shareKeyWithCode提取分享ID和提取码
            Object[] shareInfo = ShareUtil.getShareInfoFromKeyWithCode(request.getShareKey());
            if (shareInfo == null) {
                return Result.error("无效的分享链接");
            }
            
            shareId = (Long) shareInfo[0];
            
            // 如果请求中没有单独提供code，则使用从shareKey中解析出的code
            if (code == null) {
                code = (String) shareInfo[1];
            }
        } else {
            // 使用不包含提取码的shareKey
            shareId = ShareUtil.getShareIdFromKey(request.getShareKey());
            if (shareId == null) {
                return Result.error("无效的分享链接");
            }
            
            // 使用请求中单独提供的code
            // 如果此时code为null，由服务层进行验证
        }
        
        // 调用服务层执行保存操作
        return shareService.saveShareFiles(shareId, code, request.getIds(), request.getTargetFolderId());
    }
} 