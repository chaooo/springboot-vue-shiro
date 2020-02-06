package top.itdn.server.dao;

import org.apache.ibatis.annotations.Mapper;
import top.itdn.server.entity.Permission;

@Mapper
public interface PermissionDao {
    int deleteByPrimaryKey(String id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);
}