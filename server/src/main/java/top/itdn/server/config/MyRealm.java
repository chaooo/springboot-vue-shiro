package top.itdn.server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.itdn.server.entity.User;
import top.itdn.server.service.AdminService;
import top.itdn.server.utils.JwtUtil;
import top.itdn.server.utils.RedisUtil;

/**
 * Realm
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@Slf4j
@Component("MyRealm")
public class MyRealm extends AuthorizingRealm {

    private AdminService adminService;
    private RedisUtil redisUtil;
	@Autowired
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}
    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 用来进行身份认证，也就是说验证用户输入的账号和密码是否正确，
     * 获取身份验证信息，错误抛出异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        log.info("————————身份认证——————————");
        String token = (String) auth.getCredentials();
        if (null == token) {
            throw new AuthenticationException("token为空!");
        }
        // 解密获得username，用于和数据库进行对比
        String account = JwtUtil.parseTokenAud(token);
        User user = adminService.selectByAccount(account);
        if (null == user) {
            throw new AuthenticationException("用户不存在!");
        }
        // 校验token是否过期
        if (!tokenRefresh(token, user)) {
            throw new AuthenticationException("Token已过期!");
        }
        return new SimpleAuthenticationInfo(user, token,"MyRealm");
    }

    /**
     * 获取用户权限信息，包括角色以及权限。
     * 只有当触发检测用户权限时才会调用此方法，例如checkRole,checkPermissionJwtToken
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("————权限认证 [ roles、permissions]————");
        String account = JwtUtil.parseTokenAud(principals.toString());
        User user = adminService.selectByAccount(account);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        /* simpleAuthorizationInfo.addRole(user.getRole());
        Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
        simpleAuthorizationInfo.addStringPermissions(permission); */
        return simpleAuthorizationInfo;
    }

    /**
     * JWT Token续签：
     * 业务逻辑：登录成功后，用户在未过期时间内继续操作，续签token。
     *         登录成功后，空闲超过过期时间，返回token已失效，重新登录。
     * 实现逻辑：
     *    1.登录成功后将token存储到redis里面(这时候k、v值一样都为token)，并设置过期时间为token过期时间
     *    2.当用户请求时token值还未过期，则重新设置redis里token的过期时间。
     *    3.当用户请求时token值已过期，但redis中还在，则JWT重新生成token并覆盖v值(这时候k、v值不一样了)，然后设置redis过期时间。
     *    4.当用户请求时token值已过期，并且redis中也不存在，则用户空闲超时，返回token已失效，重新登录。
     */
    public boolean tokenRefresh(String token, User user) {
        String cacheToken = String.valueOf(redisUtil.get(token));
        // 过期后会得到"null"值，所以需判断字符串"null"
        if (cacheToken != null && cacheToken.length() != 0 && !"null".equals(cacheToken)) {
            // 校验token有效性
            if (!JwtUtil.isVerify(cacheToken)) {
                // 生成token
                String newToken = JwtUtil.createToken(user);
                // 将token存入redis,并设置超时时间
                redisUtil.set(token, newToken, JwtUtil.getExpireTime());
            } else {
                // 重新设置超时时间
                redisUtil.expire(token, JwtUtil.getExpireTime());
            }
            log.info("打印存入redis的过期时间："+redisUtil.getExpire(token));
            return true;
        }
        return false;
    }
}
