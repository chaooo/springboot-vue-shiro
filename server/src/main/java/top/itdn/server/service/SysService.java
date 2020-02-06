package top.itdn.server.service;

import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;
import top.itdn.server.utils.JwtUtil;
import top.itdn.server.utils.ResponseVo;

import java.util.List;
import java.util.Set;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
public interface SysService {
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
	 * 根据account查找用户，自定义Realm中调用
	 * @param account
	 * @return User
	 */
	User selectByAccount(String account);

	/**
	 * 根据roleid查找用户角色名，自定义Realm中调用
	 * @param roleid
	 * @return roles
	 */
	String getRoleByRoleid(Integer roleid);

	/**
	 * 根据roleid查找用户权限，自定义Realm中调用
	 * @param roleid
	 * @return  Set<permissions>
	 */
	Set<String> getPermissionsByRoleid(Integer roleid);

}
