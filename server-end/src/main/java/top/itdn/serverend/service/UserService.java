package top.itdn.serverend.service;

import top.itdn.serverend.entity.User;
import top.itdn.serverend.entity.vo.ResponseVo;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
public interface UserService {
    /**
     * 根据id查找用户
     * @param id
     * @return User
     */
    ResponseVo<User> loadUser(int id);

    /**
     * 用户注册(用户名，密码)
     * @param name
     * @param password
     * @return User
     */
    ResponseVo<User> addUser(String name, String password);

    /**
     * 用户登录(用户名，密码)
     * @param name
     * @param password
     * @return
     */
    ResponseVo<String> checkUser(String name, String password);

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
