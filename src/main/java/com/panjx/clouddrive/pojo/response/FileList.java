package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.UserFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileList {
    // 分页数据
    List<UserFile> list;
    // 分页信息
    PageMeta pageData;
}
