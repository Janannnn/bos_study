package cn.itcast.bos.service.impl;

import cn.itcast.bos.dao.SubAreaRepository;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.SubAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SubAreaServiceImpl implements SubAreaService {
    @Autowired
    private SubAreaRepository subAreaRepository;
    @Override
    public void saveBatch(List<SubArea> subAreas) {

        subAreaRepository.save(subAreas);
    }
}
