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
	 * 获取当前用户信息，包括权限路径
	 * @param token
	 * @return UserVo
	 */
	ResponseVo<UserVo> userInfo(String token);

	/**
	 * 获取用户列表
	 * @return List<UserVo>
	 */
	ResponseVo<List<UserVo>> userList();

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
