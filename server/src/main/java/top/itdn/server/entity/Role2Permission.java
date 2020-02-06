package top.itdn.server.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * sys_role_permission
 * @author 
 */
@Data
public class Role2Permission implements Serializable {
    private Integer id;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 权限id
     */
    private Integer permissionId;

    private static final long serialVersionUID = 1L;
}