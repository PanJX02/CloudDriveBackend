package com.panjx.clouddrive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageMeta {
    // 总数量
    int total;
    // 总页数
    int totalPage;
    // 每页数量
    int pageSize;
    // 当前页码
    int page;
}
