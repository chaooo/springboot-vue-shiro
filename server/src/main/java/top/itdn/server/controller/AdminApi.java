package top.itdn.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itdn.server.service.AdminService;
import top.itdn.server.utils.ResponseVo;

import javax.servlet.http.HttpServletRequest;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
@RestController
public class AdminApi {
	/**
	 * 注入服务类
	 */
    private AdminService adminService;
	@Autowired
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	/**
	 * 注册(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/register")
	public ResponseVo<String> register(String account, String password) {
		return adminService.register(account, password);
	}

	/**
	 * 登录(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/login")
	public ResponseVo<String> login(String account, String password) {
		return adminService.login(account, password);
	}

	/**
	 * 处理非法请求
	 */
	@GetMapping("/unauthorized")
	public ResponseVo unauthorized(HttpServletRequest request) {
		return new ResponseVo(-1, "Token失效请重新登录!");
	}
}
