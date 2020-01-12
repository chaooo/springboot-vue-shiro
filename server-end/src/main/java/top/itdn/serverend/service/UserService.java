package top.itdn.serverend.service;

import org.springframework.stereotype.Component;
import top.itdn.serverend.entity.UserBean;

import java.util.Map;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@Component
public class UserService {

    public UserBean getUser(String username) {
        if (! DataMap.getData().containsKey(username)) {
            return null;
        }
        UserBean user = new UserBean();
        Map<String, String> detail = DataMap.getData().get(username);
        user.setUsername(username);
        user.setPassword(detail.get("password"));
        user.setRole(detail.get("role"));
        user.setPermission(detail.get("permission"));
        return user;
    }
}
