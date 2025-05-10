package com.panjx.clouddrive.service.admin;

import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.request.UpdateShareRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminShareService {
    /**
     * 获取所有分享信息
     * 
     * @param pageRequest 分页请求参数
     * @return 分享信息列表
     */
    Result getAllShares(PageRequest pageRequest);
    
    /**
     * 更新分享信息
     * 
     * @param updateShareRequest 更新分享请求参数
     * @return 更新结果
     */
    Result updateShare(UpdateShareRequest updateShareRequest);
} 