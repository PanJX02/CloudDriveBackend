package com.panjx.clouddrive;

import com.panjx.clouddrive.mapper.UserMapper;
import com.panjx.clouddrive.pojo.User;
import com.panjx.clouddrive.pojo.UserDTO;
import com.panjx.clouddrive.utils.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CloudDriveApplicationTests {

	@Autowired
	private UserMapper userMapper;
	@Test
	void contextLoads() {
	}

	@Test
	void testRegister() {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("panjx");
		userDTO.setPassword("123456");
		//加密
		String password = PasswordUtil.encode(userDTO.getPassword());

		//插入数据库
		// 时间戳
		Long timestamp =  System.currentTimeMillis();
		// 不需要转换为Date对象，直接使用timestamp
		System.out.println(timestamp);
		User u = new User(null, userDTO.getUsername(), userDTO.getUsername(), userDTO.getEmail(), 0, null, password, timestamp, null, 0, 0L, null);
		userMapper.add(u);
	}

	@Test
	void testFindByUsername() {
		User user = userMapper.findByUsername("panjx");
		System.out.println(user);
	}

}
