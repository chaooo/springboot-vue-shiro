package top.itdn.server.entity;

import lombok.Data;
import java.util.Date;
import java.util.Set;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/21
 */
@Data
public class UserVo {
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    private Integer id;
    /**
     * 用户名
     */
    private String account;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 角色ID
     */
    private Integer roleid;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updatetime;
    /**
     * 是否有效：1有效，2无效
     */
    private String deletestatus;
    /**
     * 角色列表['admin']
     */
    private Set<String> roles;
    /**
     * 权限列表['user:add', 'user:delete', 'user:update', 'user:list']
     */
    private Set<String> permissions;
}
