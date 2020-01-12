package top.itdn.serverend.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import top.itdn.serverend.entity.vo.ResponseVo;
import top.itdn.serverend.service.UserService;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@RestController
public class UserController {

    private UserService userService;
    @Autowired
    public void setService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseVo login(@RequestParam("username") String username,
                            @RequestParam("password") String password) {
        return userService.checkUser(username, password);
    }

    @GetMapping("/article")
    public ResponseVo article() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return new ResponseVo(200, "You are already logged in");
        } else {
            return new ResponseVo(200, "You are guest");
        }
    }

    @GetMapping("/require_auth")
    @RequiresAuthentication
    public ResponseVo requireAuth() {
        return new ResponseVo(200, "You are authenticated");
    }

    @GetMapping("/require_role")
    @RequiresRoles("admin")
    public ResponseVo requireRole() {
        return new ResponseVo(200, "You are visiting require_role");
    }

    @GetMapping("/require_permission")
    @RequiresPermissions(logical = Logical.AND, value = {"view", "edit"})
    public ResponseVo requirePermission() {
        return new ResponseVo(200, "You are visiting permission require edit,view");
    }

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseVo unauthorized() {
        return new ResponseVo(401, "Unauthorized");
    }
}
