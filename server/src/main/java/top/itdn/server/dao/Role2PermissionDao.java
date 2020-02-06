package top.itdn.server.dao;

import org.apache.ibatis.annotations.Mapper;
import top.itdn.server.entity.Role2Permission;

@Mapper
public interface Role2PermissionDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Role2Permission record);

    int insertSelective(Role2Permission record);

    Role2Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role2Permission record);

    int updateByPrimaryKey(Role2Permission record);
}