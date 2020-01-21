package top.itdn.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itdn.server.dao.UserDao;
import top.itdn.server.entity.User;
import top.itdn.server.service.AdminService;
import top.itdn.server.utils.JwtUtil;
import top.itdn.server.utils.Md5Util;
import top.itdn.server.utils.RedisUtil;
import top.itdn.server.utils.ResponseVo;

import java.util.Date;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
@Service
public class AdminServiceImpl implements AdminService {

	private UserDao userDao;
    private RedisUtil redisUtil;
	/**
	 * 注入DAO
	 */
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }
    /**
     * 用户注册(用户名，密码)
     *
     * @param account 用户名
     * @param password 密码
     * @return token
     */
    @Override
    public ResponseVo<String> register(String account, String password) {
        //检查用户名是否被占用
        User user = userDao.selectByAccount(account);
        if(user!=null) {
            return new ResponseVo<>( -1, "用户名被占用");
        }
        //添加用户信息
        user = new User();
        //设置用户名
        user.setAccount(account);
        //密码加密后再保存
        String salt = Md5Util.salt();
        String md5Password = Md5Util.md5(password+salt);
        user.setPassword(md5Password);
        user.setSalt(salt);
        //设置注册时间
        user.setCreatetime(new Date());
        //添加到数据库
        int row = userDao.insertSelective(user);
        //返回信息
        if(row>0) {
			//生成token给用户
            String token = getToken(user);
            return new ResponseVo<>(0,"注册成功", token);
        }else {
            return new ResponseVo<>( -1, "注册失败");
        }
    }

    /**
     * 用户登录(用户名，密码)
     *
     * @param account 用户名
     * @param password 密码
     * @return token
     */
    @Override
    public ResponseVo<String> login(String account, String password) {
        //处理比对密码
        User user = userDao.selectByAccount(account);
        if(user!=null) {
            String  salt = user.getSalt();
            String md5Password = Md5Util.md5(password+salt);
            String dbPassword = user.getPassword();
            if(md5Password.equals(dbPassword)) {
                //生成token给用户
                String token = getToken(user);
                return new ResponseVo<>(0,"登录成功", token);
            }
        }
        return new ResponseVo<>( -1, "登录失败");
    }
    private String getToken(User user){
        // 生成token
        String token = JwtUtil.createToken(user);
        // 为了过期续签，将token存入redis
        redisUtil.set(token, token);
        // 设置超时时间
        redisUtil.expire(token, JwtUtil.getExpireTime());

        return token;
    }

    /**
     * 根据account查找用户
     *
     * @param account
     * @return User
     */
    @Override
    public User selectByAccount(String account) {
        return userDao.selectByAccount(account);
    }

}
