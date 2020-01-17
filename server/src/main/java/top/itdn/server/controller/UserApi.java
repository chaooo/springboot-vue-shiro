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
@CrossOrigin(origins="*",methods= {RequestMethod.GET,RequestMethod.POST})
public class UserApi {
	/**
	 * 注入服务类
	 */
	@Autowired
	private UserService userService;

	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
	@GetMapping("/user/get")
	public ResponseVo<User> load(int id) {
		return userService.loadUser(id);
	}

	/**
	 * 用户注册(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/user/regist")
	public ResponseVo<String> regist(String account, String password) {
		return userService.addUser(account, password);
	}

	/**
	 * 用户登录(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/user/login")
	public ResponseVo<String> login(String account, String password) {
		return userService.checkUser(account, password);
	}

	/**
	 * 检查Token
	 * @param token
	 * @return
	 */
	@PostMapping("/user/token")
	public ResponseVo verifyToken(String token) {
		return userService.checkToken(token);
	}

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
}
