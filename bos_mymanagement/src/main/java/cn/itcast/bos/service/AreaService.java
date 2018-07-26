package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;

public interface AreaService {

	void saveBatch(List<Area> array);


	Page<Area> findPageData(Specification<Area> specification, Pageable pageable);
	Area findByProvinceAndCityAndDistrict(String province,String city,String district);
}
