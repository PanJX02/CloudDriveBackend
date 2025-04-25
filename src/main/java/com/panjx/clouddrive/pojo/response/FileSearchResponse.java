package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.UserFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchResponse {
    /**
     * 搜索结果列表
     */
    private List<UserFile> files;
    
    /**
     * 搜索结果总数
     */
    private Integer total;
} 