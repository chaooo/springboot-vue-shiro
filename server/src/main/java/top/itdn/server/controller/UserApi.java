package top.itdn.server.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
	 * 获取所有用户信息
	 * @return
	 */
	@RequiresPermissions("user:list")
	@GetMapping("/user/list")
	public ResponseVo list() {
		return userService.loadUser();
	}

	/**
	 * 用户更新资料
	 * @param user
	 * @return
	 */
	@RequiresPermissions("user:update")
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
	@RequiresPermissions("user:update")
	@PostMapping("/user/password")
	public ResponseVo modifyPassword(String password, String newPassword, HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		return userService.modifyPassword(token, password, newPassword);
	}



}
