package top.itdn.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;
import top.itdn.server.service.SysService;
import top.itdn.server.utils.ResponseVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
@RestController
@CrossOrigin(origins="*",methods= {RequestMethod.GET,RequestMethod.POST})
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
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	public ResponseVo<String> register(User user) {
		return sysService.register(user.getAccount(), user.getPassword());
	}

	/**
	 * 登录(用户名，密码)
	 * @param user
	 * @return
	 */
	@PostMapping("/login")
	public ResponseVo<String> login(@RequestBody User user) {
		return sysService.login(user.getAccount(), user.getPassword());
	}

	/**
	 * 处理非法请求
	 */
	@GetMapping("/unauthorized")
	public ResponseVo unauthorized() {
		return new ResponseVo(-1, "Token失效请重新登录!");
	}
}
