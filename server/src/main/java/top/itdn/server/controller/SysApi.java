package top.itdn.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itdn.server.service.SysService;
import top.itdn.server.utils.ResponseVo;

import javax.servlet.http.HttpServletRequest;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
@RestController
public class SysApi {
	/**
	 * 注入服务类
	 */
    private SysService sysService;
	@Autowired
	public void setSysService(SysService sysService) {
		this.sysService = sysService;
	}

	/**
	 * 注册(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/register")
	public ResponseVo<String> register(String account, String password) {
		return sysService.register(account, password);
	}

	/**
	 * 登录(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
	@PostMapping("/login")
	public ResponseVo<String> login(String account, String password) {
		return sysService.login(account, password);
	}

	/**
	 * 处理非法请求
	 */
	@GetMapping("/unauthorized")
	public ResponseVo unauthorized() {
		return new ResponseVo(-1, "Token失效请重新登录!");
	}
}
