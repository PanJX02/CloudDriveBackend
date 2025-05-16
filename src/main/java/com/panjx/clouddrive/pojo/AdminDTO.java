package com.panjx.clouddrive.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    @NotBlank(message = "管理员名不能为空")
    @Length(min = 4, max = 20, message = "管理员名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "管理员名只能包含字母、数字和下划线")
    private String adminName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少8位")
    @Size(max = 20, message = "密码长度至多为20位")
    private String password;
    
    /**
     * 身份
     * 0: 超级管理员
     * 1: 普通管理员
     */
    private Integer identity;
} 