package top.itdn.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itdn.server.dao.SysDao;
import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;
import top.itdn.server.service.SysService;
import top.itdn.server.utils.JwtUtil;
import top.itdn.server.utils.Md5Util;
import top.itdn.server.utils.RedisUtil;
import top.itdn.server.utils.ResponseVo;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/20
 */
@Service
public class SysServiceImpl implements SysService {

	private SysDao sysDao;
    private RedisUtil redisUtil;
	/**
	 * 注入DAO
	 */
    @Resource
	public void setSysDao(SysDao sysDao) {
		this.sysDao = sysDao;
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
        User user = sysDao.selectByAccount(account);
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
        int row = sysDao.insertSelective(user);
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
        User user = sysDao.selectByAccount(account);
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
        // 为了过期续签，将token存入redis，并设置超时时间
        redisUtil.set(token, token, JwtUtil.getExpireTime());

        return token;
    }

    /**
     * 根据account查找用户，自定义Realm中调用
     *
     * @param account
     * @return User
     */
    @Override
    public User selectByAccount(String account) {
        return sysDao.selectByAccount(account);
    }

    /**
     * 根据roleid查找用户角色名，自定义Realm中调用
     *
     * @param roleid
     * @return roles
     */
    @Override
    public String getRoleByRoleid(Integer roleid) {
        return sysDao.getRoleByRoleid(roleid);
    }

    /**
     * 根据roleid查找用户权限，自定义Realm中调用
     *
     * @param roleid
     * @return Set<permissions>
     */
    @Override
    public Set<String> getPermissionsByRoleid(Integer roleid) {
        return sysDao.getPermissionsByRoleid(roleid);
    }

    /**
     * 获取当前用户信息，包括权限路径
     *
     * @param token
     * @return UserVo
     */
    @Override
    public ResponseVo<UserVo> userInfo(String token) {
        //根据id查找用户
        int userId = JwtUtil.parseTokenUid(token);
        UserVo userVo = sysDao.selectVoByPrimaryKey(userId);
        Set<String> menus = sysDao.selectPermissionByRoleid(userVo.getRoleid());
        userVo.setMenus(menus);
        return new ResponseVo<>(0,"获取成功", userVo);
    }
}
