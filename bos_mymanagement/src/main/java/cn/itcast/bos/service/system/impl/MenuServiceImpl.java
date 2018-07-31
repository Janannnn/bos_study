package cn.itcast.bos.service.system.impl;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.MenuService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuRepository menuRepository;
    @Override
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Override
    public void save(Menu model) {
        menuRepository.save(model);
    }

    @Override
    public List<Menu> findByUser() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        return menuRepository.findByUser(user.getId());
    }
}
