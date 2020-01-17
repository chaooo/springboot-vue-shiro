package top.itdn.server.config;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.itdn.server.dao.UserDao;
import top.itdn.server.entity.User;
import top.itdn.server.utils.JwtUtil;

/**
 * Realm
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@Component("MyRealm")
public class MyRealm extends AuthorizingRealm {

    private UserDao userDao;
    @Autowired
    public MyRealm(UserDao userDao) {
        this.userDao = userDao;
    }
    public MyRealm(){}

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 检测用户权限时调用，例如checkRole,checkPermission
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String account = JwtUtil.parseTokenAud(principals.toString());
        User user = userDao.selectByAccount(account);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        /* simpleAuthorizationInfo.addRole(user.getRole());
        Set<String> permission = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
        simpleAuthorizationInfo.addStringPermissions(permission); */
        return simpleAuthorizationInfo;
    }

    /**
     * 验证用户名
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String account = JwtUtil.parseTokenAud(token);
        if (account == null) {
            throw new AuthenticationException("token invalid");
        }

        User user = userDao.selectByAccount(account);
        if (user == null) {
            throw new AuthenticationException("User didn't existed!");
        }

        if (!JwtUtil.isVerify(token)) {
            throw new AuthenticationException("Username or password error");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
