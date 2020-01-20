package top.itdn.server.service;

import top.itdn.server.entity.User;
import top.itdn.server.utils.ResponseVo;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
public interface AdminService {
	/**
	 * 注册(用户名，密码)
	 * @param account
	 * @param password
	 * @return String
	 */
    ResponseVo<String> register(String account, String password);

	/**
	 * 登录(用户名，密码)
	 * @param account
	 * @param password
	 * @return String
	 */
    ResponseVo<String> login(String account, String password);

	/**
	 * 根据account查找用户
	 * @param account
	 * @return User
	 */
	User selectByAccount(String account);
}
