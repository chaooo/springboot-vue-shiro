package top.itdn.server.controller;

import top.itdn.server.entity.User;
import top.itdn.server.service.UserService;
import top.itdn.server.utils.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
@RestController
public class UserApi {
	/**
	 * 注入服务类
	 */
	@Autowired
	private UserService userService;

	/**
	 * 用户更新资料
	 * @param user
	 * @return
	 */
	@PostMapping("/user/update")
	public ResponseVo update(User user, HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		return userService.modifyUser(token, user);
	}

	/**
	 * 用户修改密码
	 * @param password
	 * @param newPassword
	 * @return
	 */
	@PostMapping("/user/password")
	public ResponseVo modifyPassword(String password, String newPassword, HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		return userService.modifyPassword(token, password, newPassword);
	}

	@GetMapping("/user/test")
	public ResponseVo modifyPassword() {
		return new ResponseVo(1, "test");
	}


}
