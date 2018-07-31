package cn.itcast.bos.service.system.impl;

import cn.itcast.bos.dao.system.PermissonRepository;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissonServiceImpl implements PermissionService {
    @Autowired
    private PermissonRepository permissonRepository;
    @Override
    public List<Permission> findByUser(User user) {
        if(user.getUsername().equals("admin")){
            return permissonRepository.findAll();
        }
        return permissonRepository.findByUser(user.getId());
    }

    @Override
    public List<Permission> findAll() {

        return permissonRepository.findAll();
    }

    @Override
    public void save(Permission model) {
        permissonRepository.save(model);
    }
}
