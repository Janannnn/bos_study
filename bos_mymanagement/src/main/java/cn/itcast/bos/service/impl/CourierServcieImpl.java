package cn.itcast.bos.service.impl;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.CourierService;

@Service
@Transactional
public class CourierServcieImpl implements CourierService {
	
	@Autowired
	private CourierRepository courierRepository;

	@Override
	@RequiresPermissions("courier_add")
	public void add(Courier courier) {
		courierRepository.save(courier);
	}

	@Override
	public List<Courier> findAll() {
		return courierRepository.findAll();
	}

	@Override
	public void updateTag(Integer id) {
		courierRepository.updateTag(id);
	}

	@Override
	public Page<Courier> findPageData(Specification<Courier> specification,
			Pageable pageable) {
		return courierRepository.findAll(specification, pageable);
	}

	@Override
	public void delByID(int id) {
		courierRepository.delete(id);
	}

}
