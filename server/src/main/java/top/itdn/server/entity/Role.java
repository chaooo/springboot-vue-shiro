package top.itdn.server.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * sys_role
 * @author 
 */
@Data
public class Role implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}