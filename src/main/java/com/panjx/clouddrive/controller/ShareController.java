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
} 