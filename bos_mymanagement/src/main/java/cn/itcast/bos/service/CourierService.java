package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;

public interface CourierService {

	void add(Courier courier);

	List<Courier> findAll();

	void updateTag(Integer id);

	// 分页查询
	public Page<Courier> findPageData(Specification<Courier> specification,
			Pageable pageable);

	void delByID(int id);
}
