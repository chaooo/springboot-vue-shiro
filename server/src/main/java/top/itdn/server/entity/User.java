package top.itdn.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String account;

    /**
     * 用户密码
     */
    @JsonIgnore
    private String password;

    /**
     * 随机盐
     */
    @JsonIgnore
    private String salt;

    /**
     * 用户昵称
     */
    private String nickname;

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

    private static final long serialVersionUID = 1L;
}
