package top.itdn.server.dao;

import org.apache.ibatis.annotations.Mapper;
import top.itdn.server.entity.User;
import top.itdn.server.entity.UserVo;

import java.util.Set;

@Mapper
public interface SysDao {
    int insertSelective(User record);

    User selectByAccount(String account);

    String getRoleByRoleid(Integer roleid);

    Set<String> getPermissionsByRoleid(Integer roleid);

    UserVo selectVoByPrimaryKey(int userId);

    Set<String> selectPermissionByRoleid(Integer roleid);
}