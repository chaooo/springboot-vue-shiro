package top.itdn.server.config;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
public class JwtToken implements AuthenticationToken {

    private String token;
    JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
