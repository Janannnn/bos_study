package cn.itcast.bos.service.system;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;

import java.util.List;

public interface RoleService {
    List<Role> findByUser(User user);

    List<Role> findAll();

    void save(Role model, String menuIds, String[] permissionIds);

    Role findOne(Integer roleId);
}
