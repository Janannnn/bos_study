package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.base.Standard;

public interface StandardService {

	void add(Standard standrd);
	Page<Standard> findPageData(Pageable pageable);
	List<Standard> findAll();
}
