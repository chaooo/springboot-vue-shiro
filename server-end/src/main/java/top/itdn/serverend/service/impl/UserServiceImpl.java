package top.itdn.serverend.service.impl;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itdn.serverend.dao.UserDao;
import top.itdn.serverend.entity.User;
import top.itdn.serverend.entity.vo.ResponseVo;
import top.itdn.serverend.service.UserService;
import top.itdn.serverend.util.JwtUtil;
import top.itdn.serverend.util.Md5Util;

import java.util.Date;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@Service
public class UserServiceImpl implements UserService {
    /**
     * 注入DAO
     */
    @Autowired
    private UserDao userDao;

    /**
     * 根据id查找用户
     * @param id
     * @return
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
     * 用户注册(用户名，密码)
     * @param name
     * @param password
     * @return
     */
    @Override
    public ResponseVo<User> addUser(String name, String password) {
        //检查用户名是否被占用
        User user = userDao.selectByName(name);
        if(user!=null) {
            return new ResponseVo<>( -1, "用户名被占用");
        }
        //添加用户信息
        user = new User();
        //设置用户名
        user.setUsername(name);
        //密码加密后再保存
        String salt = Md5Util.salt();
        String md5Password = Md5Util.md5(password+salt);
        user.setPassword(md5Password);
        user.setSalt(salt);
        //设置注册时间
        user.setCreateTime(new Date());
        //添加到数据库
        int row = userDao.insertSelective(user);
        //返回信息
        if(row>0) {
            return new ResponseVo<>(0,"注册成功", user);
        }else {
            return new ResponseVo<>( -1, "注册失败");
        }
    }
    /**
     * 用户登录(用户名，密码)
     * @param name
     * @param password
     * @return
     */
    @Override
    public ResponseVo<String> checkUser(String name, String password) {
        //处理比对密码
        User user = userDao.selectByName(name);
        if(user!=null) {
            String  salt = user.getSalt();
            String md5Password = Md5Util.md5(password+salt);
            String dbPassword = user.getPassword();
            if(md5Password.equals(dbPassword)) {
                //生成token给用户
                String token = JwtUtil.createToken(user);
                return new ResponseVo<>(0,"登录成功", token);
            }
        }
        // return new ResponseVo<>(-1,"登录失败");
        throw new UnauthorizedException();
    }

    /**
     * 检查Token
     * @param token
     * @return
     */
    @Override
    public ResponseVo checkToken(String token) {
        boolean ok = JwtUtil.isVerify(token);
        if(ok) {
            return new ResponseVo(0,"Token有效");
        }
        return new ResponseVo(-1,"Token无效");
    }

    /**
     * 用户更新资料
     * @param user
     * @return
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
     * @param password
     * @param newPassword
     * @return
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
