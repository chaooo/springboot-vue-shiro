package top.itdn.server.service;

import top.itdn.server.entity.User;
import top.itdn.server.utils.ResponseVo;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
public interface UserService {

	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
    ResponseVo<User> loadUser(int id);

	/**
	 * 根据account查找用户
	 * @param account
	 * @return
	 */
    ResponseVo<User> loadUser(String account);

	/**
	 * 用户注册(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
    ResponseVo<String> addUser(String account, String password);

	/**
	 * 用户登录(用户名，密码)
	 * @param account
	 * @param password
	 * @return
	 */
    ResponseVo<String> checkUser(String account, String password);

	/**
	 * 检查Token
	 * @param token
	 * @return
	 */
    ResponseVo checkToken(String token);

	/**
	 * 用户更新资料
	 * @param token
	 * @param user
	 * @return
	 */
    ResponseVo modifyUser(String token, User user);

	/**
	 * 用户修改密码
	 * @param token
	 * @param password
	 * @param newPassword
	 * @return
	 */
    ResponseVo modifyPassword(String token, String password, String newPassword);
}
