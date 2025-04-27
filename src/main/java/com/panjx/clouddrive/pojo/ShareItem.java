package com.panjx.clouddrive.pojo;

import lombok.Data;

/**
 * 分享内容条目实体类
 */
@Data
public class ShareItem {
    /**
     * 关联file_share.share_id
     */
    private String shareId;
    
    /**
     * 关联user_file.id
     */
    private Long userFileId;
} 