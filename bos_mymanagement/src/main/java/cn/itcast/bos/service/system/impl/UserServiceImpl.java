package cn.itcast.bos.service.system.impl;

import cn.itcast.bos.dao.system.UserRepository;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.RoleService;
import cn.itcast.bos.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User user, String[] roleIds) {
        userRepository.save(user);
        for (String roleid : roleIds) {
            int roleId = Integer.parseInt(roleid);
            Role role = roleService.findOne(roleId);
            user.getRoles().add(role);
        }

    }
}
