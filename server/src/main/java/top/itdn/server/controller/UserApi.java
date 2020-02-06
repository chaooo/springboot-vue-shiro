package top.itdn.server.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;
import top.itdn.server.service.UserService;
import top.itdn.server.utils.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
	 * 获取当前用户信息，包括权限路径
	 * @param request
	 * @return UserVo
	 */
	@GetMapping("/user/info")
	public ResponseVo<UserVo> userInfo(HttpServletRequest request) {
		String token = request.getHeader("X-Token");
		return userService.userInfo(token);
	}

	/**
	 * 获取用户列表
	 * @return List<UserVo>
	 */
	@GetMapping("/user/list")
	public ResponseVo<List<UserVo>> userList() {
		return userService.userList();
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
