package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.Announcement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementList {
    // 分页数据
    List<Announcement> list;
    // 分页信息
    PageMeta pageData;
} 