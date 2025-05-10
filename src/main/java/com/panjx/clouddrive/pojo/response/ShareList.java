package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.FileShare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareList {
    // 分页数据
    List<FileShare> list;
    // 分页信息
    PageMeta pageData;
} 