package cn.itcast.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.FixedAreaRepository;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.FixedAreaService;

@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {
	@Autowired
	private FixedAreaRepository fixedAreaRepository; 

	@Override
	public void add(FixedArea fixedArea) {
		fixedAreaRepository.save(fixedArea);
	}

}
