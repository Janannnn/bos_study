package cn.itcast.bos.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.FixedArea;

public interface FixedAreaService {

	void add(FixedArea fixedArea);

	Page<FixedArea> findPageData(Specification<FixedArea> specification, Pageable pageable);


}
