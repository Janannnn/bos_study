package cn.itcast.bos.service.system;

import cn.itcast.bos.domain.system.Menu;

import java.util.List;

public interface MenuService {
    List<Menu> findAll();

    void save(Menu model);

    List<Menu> findByUser();
}
