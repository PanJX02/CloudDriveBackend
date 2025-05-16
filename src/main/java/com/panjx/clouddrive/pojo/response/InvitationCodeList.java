package com.panjx.clouddrive.pojo.response;

import com.panjx.clouddrive.pojo.InvitationCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationCodeList {
    // 分页数据
    List<InvitationCode> list;
    // 分页信息
    PageMeta pageData;
} 