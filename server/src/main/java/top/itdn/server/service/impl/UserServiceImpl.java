package top.itdn.server.service.impl;

import top.itdn.server.dao.UserDao;
import top.itdn.server.entity.User;
import top.itdn.server.service.UserService;
import top.itdn.server.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
@Service
public class UserServiceImpl implements UserService {


	private UserDao userDao;
	/**
	 * 注入DAO
	 */
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
     * 根据id查找用户
     * @param id 用户ID
     * @return user
     */
    @Override
    public ResponseVo<User> loadUser(int id) {
        User user = userDao.selectByPrimaryKey(id);
        if(user!=null) {
            return new ResponseVo<>(0, "sucess", user);
        }else {
            return new ResponseVo<>(-1, "查无此人");
        }
    }

	/**
     * 根据account查找用户
     * @param account 用户名
     * @return user
     */
    @Override
    public ResponseVo<User> loadUser(String account) {
        User user = userDao.selectByAccount(account);
        if(user!=null) {
            return new ResponseVo<>(0, "sucess", user);
        }else {
            return new ResponseVo<>(-1, "查无此人");
        }
    }

    /**
     * 用户更新资料
     */
    @Override
    public ResponseVo modifyUser(String token, User user) {
        //先检查token，若Token令牌错误，返回
        boolean ok = JwtUtil.isVerify(token);
        if(!ok) {
            return new ResponseVo(-1,"Token无效");
        }
        //根据token得到用户id,并查出用户数据
        int user_id = JwtUtil.parseTokenUid(token);
        user.setId(user_id);
        int rows = userDao.updateByPrimaryKeySelective(user);
        if(rows>0) {
            return new ResponseVo(0,"更新成功");
        }
        return new ResponseVo(-1,"更新失败");
    }

    /**
     * 用户修改密码
     */
    @Override
    public ResponseVo modifyPassword(String token, String password, String newPassword) {
        //先检查token，若Token令牌错误，返回
        boolean ok = JwtUtil.isVerify(token);
        if(!ok) {
            return new ResponseVo(-1,"Token无效");
        }
        //根据id查找用户
        int userId = JwtUtil.parseTokenUid(token);
        User user = userDao.selectByPrimaryKey(userId);
        //判断旧密码是否输入正确
        String oldPassword = user.getPassword();
        if(password.equals(oldPassword)) {
            return new ResponseVo(-1,"旧密码输入错误");
        }
        //新密码加密后再保存
        String salt = Md5Util.salt();
        String md5Password = Md5Util.md5(newPassword+salt);
        user.setPassword(md5Password);
        user.setSalt(salt);
        //更新到数据库
        int rows = userDao.updateByPrimaryKeySelective(user);
        if(rows>0) {
            return new ResponseVo(0,"更新成功");
        }
        return new ResponseVo(-1,"更新失败");
    }
}
