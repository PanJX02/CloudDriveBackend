package com.panjx.clouddrive.pojo.request;

import lombok.Data;

@Data
public class PageRequest {
    // 当前页码，默认为1
    private Integer page = 1;
    
    // 每页数量，默认为10
    private Integer pageSize = 10;
} 