package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserList {
    // 分页数据
    List<User> list;
    // 分页信息
    PageMeta pageData;
} 