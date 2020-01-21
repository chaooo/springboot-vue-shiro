package top.itdn.server.service;

import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;
import top.itdn.server.utils.ResponseVo;

import java.util.List;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
public interface UserService {
	/**
	 * 查找所有用户
	 * @return List
	 */
	ResponseVo<List<User>> loadUser();

	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
    ResponseVo<User> loadUser(int id);

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
