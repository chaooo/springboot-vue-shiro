package top.itdn.serverend.entity;

import java.io.Serializable;
import java.util.Date;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

/**
 * sys_user
 *
 * @author : Charles
 * @date : 2020/1/12
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 用户名 */
    private String username;
    /** 密码 */
    @Ignore
    private String password;
    /** MD5随机盐 */
    private String salt;
    /** 昵称 */
    private String nickname;
    /** 角色ID */
    private Integer roleId;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date updateTime;
    /** 是否有效  1有效  2无效 */
    private String deleteStatus;

}