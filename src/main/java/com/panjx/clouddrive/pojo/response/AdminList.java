package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminList {
    // 分页数据
    List<Admin> list;
    // 分页信息
    PageMeta pageData;
} 