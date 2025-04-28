package com.panjx.clouddrive.controller;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.CreateShareRequest;
import com.panjx.clouddrive.service.ShareService;
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
} 