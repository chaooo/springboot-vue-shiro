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
	@Autowired
	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
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
        if (null == token || !JwtUtil.isVerify(token)) {
            throw new AuthenticationException("token无效!");
        }
        // 解密获得username，用于和数据库进行对比
        String account = JwtUtil.parseTokenAud(token);
        User user = adminService.selectByAccount(account);
        if (null == user) {
            throw new AuthenticationException("用户不存在!");
        }
        return new SimpleAuthenticationInfo(user, token,"MyRealm");
    }

    /**
     * 获取用户权限信息，包括角色以及权限。
     * 只有当触发检测用户权限时才会调用此方法，例如checkRole,checkPermission
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
     * JWTToken刷新生命周期 （解决用户一直在线操作，提供Token失效问题）
     * 1、登录成功后将用户的JWT生成的Token作为k、v存储到cache缓存里面(这时候k、v值一样)
     * 2、当该用户再次请求时，通过JWTFilter层层校验之后会进入到doGetAuthenticationInfo进行身份验证
     * 3、当该用户这次请求JWTToken值还在生命周期内，则会通过重新PUT的方式k、v都为Token值，
     *      缓存中的token值生命周期时间重新计算(这时候k、v值一样)
     * 4、当该用户这次请求jwt生成的token值已经超时，但该token对应cache中的k还是存在，
     *      则表示该用户一直在操作只是JWT的token失效了，程序会给token对应的k映射的v值
     *      重新生成JWTToken并覆盖v值，该缓存生命周期重新计算
     * 5、当该用户这次请求jwt在生成的token值已经超时，并在cache中不存在对应的k，
     *      则表示该用户账户空闲超时，返回用户信息已失效，请重新登录。
     * 6、每次当返回为true情况下，都会给Response的Header中设置Authorization，该Authorization映射的v为cache对应的v值。
     * 7、注：当前端接收到Response的Header中的Authorization值会存储起来，作为以后请求token使用
     * 参考方案：https://blog.csdn.net/qq394829044/article/details/82763936
     *
     */
    // ...

}
