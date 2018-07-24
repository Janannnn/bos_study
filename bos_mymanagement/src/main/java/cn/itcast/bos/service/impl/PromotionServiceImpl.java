package cn.itcast.bos.service.impl;

import cn.itcast.bos.dao.PromotionRepository;
import cn.itcast.bos.domain.pagebean.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public void promotionSave(Promotion promotion) {
        promotionRepository.save(promotion);
    }

    @Override
    public Page<Promotion> findAll(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    @Override
    public PageBean pageQuery(int page, int rows) {
        Pageable pageable = new PageRequest(page-1,rows);
        Page<Promotion> all = promotionRepository.findAll(pageable);

        PageBean<Promotion> pageBean = new PageBean();
        pageBean.setTotalCount((int) all.getTotalElements());
        pageBean.setDataPage(all.getContent());

        return pageBean;
    }

    @Override
    public Promotion findOne(int id) {
        return promotionRepository.findOne(id);
    }

    //根据当前时间判断截止时间是否过期，过期则修改status
    @Override
    public void updateStatus(Date date) {
        promotionRepository.updateStatus(date);
    }
}
