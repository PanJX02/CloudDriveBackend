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
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;


    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少8位")
    @Size(max = 20, message = "密码长度至多为20位")
    private String password; //密码

//    @NotBlank(message = "邮箱不能为空")
//    @Email(message = "邮箱格式不正确")
    private String email; //邮箱

//    @NotBlank(message = "邮箱验证码不能为空")
//    @Size(min = 4, max = 4, message = "邮箱验证码长度为4位")
    private String emailCode; //邮箱验证码


}
