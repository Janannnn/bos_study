package cn.itcast.bos.service.system.impl;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.dao.system.PermissonRepository;
import cn.itcast.bos.dao.system.RoleRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private PermissonRepository permissonRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<Role> findByUser(User user) {
        if(user.getUsername().equals("admin")){
            return roleRepository.findAll();
        }else{
            return roleRepository.findByUser(user.getId());
        }

    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void save(Role model, String menuIds, String[] permissionIds) {
        //先保存角色：前台没有直接传递menu和permission的数据，不会产生对象，也就不会产生瞬时态
        roleRepository.save(model);
        //利用快照，关联其他进行外键维护
        String[] menuids = menuIds.split(",");
        for (String menuid : menuids) {
            //遍历菜单id查询出菜单
            int menuId = Integer.parseInt(menuid);
            Menu menu = menuRepository.findOne(menuId);
            model.getMenus().add(menu);
        }
        for (String permissionid : permissionIds) {
            int permissionId = Integer.parseInt(permissionid);
            Permission permission = permissonRepository.findOne(permissionId);
            model.getPermissions().add(permission);
        }
    }

    @Override
    public Role findOne(Integer roleId) {
        return roleRepository.findOne(roleId);
    }
}
