package cn.itcast.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cn.itcast.bos.dao.StandardRepository;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;

@Service
public class StandardServiceImpl implements StandardService {
	
	@Autowired
	private StandardRepository standardRepository;

	@Override
	public void add(Standard standard) {
		//System.out.println("添加指派信息");
		standardRepository.save(standard);
	}

	@Override
	public Page<Standard> findPageData(Pageable pageable) {
		return standardRepository.findAll(pageable);
	}

	
}
