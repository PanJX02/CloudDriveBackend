package com.panjx.clouddrive.service.admin.impl;

import com.panjx.clouddrive.mapper.AdminMapper;
import com.panjx.clouddrive.pojo.Admin;
import com.panjx.clouddrive.pojo.Result;
import com.panjx.clouddrive.pojo.request.PageRequest;
import com.panjx.clouddrive.pojo.response.AdminList;
import com.panjx.clouddrive.pojo.response.PageMeta;
import com.panjx.clouddrive.service.admin.AdminListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminListServiceImpl implements AdminListService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Result getAllAdmins(PageRequest pageRequest) {
        // 获取分页参数
        int page = pageRequest.getPage();
        int pageSize = pageRequest.getPageSize();
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询总数
        int total = adminMapper.countAll();
        
        // 计算总页数
        int totalPage = (total + pageSize - 1) / pageSize;
        
        // 查询管理员列表
        List<Admin> adminList = adminMapper.findAllByPage(offset, pageSize);
        
        // 组装分页信息
        PageMeta pageMeta = new PageMeta(total, totalPage, pageSize, page);
        
        // 组装响应结果
        AdminList result = new AdminList(adminList, pageMeta);
        
        return Result.success(result);
    }
} 