package top.itdn.server.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * sys_permission
 * @author 
 */
@Data
public class Permission implements Serializable {
    /**
     * 主键id
     */
    private String id;

    /**
     * 菜单标题
     */
    private String name;

    /**
     * 路径
     */
    private String url;

    /**
     * 菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)
     */
    private Integer menuType;

    /**
     * 菜单权限编码
     */
    private String perms;

    /**
     * 菜单排序
     */
    private Integer sortNo;

    /**
     * 删除状态 0正常 1已删除
     */
    private Integer delFlag;

    private static final long serialVersionUID = 1L;
}