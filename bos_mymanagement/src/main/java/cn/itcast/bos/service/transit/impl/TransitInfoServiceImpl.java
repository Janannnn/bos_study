package cn.itcast.bos.service.transit.impl;

import cn.itcast.bos.dao.WayBillReository;
import cn.itcast.bos.dao.transit.TransitRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.transit.TransitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransitInfoServiceImpl implements TransitInfoService {
    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;
    @Autowired
    private TransitRepository transitRepository;
    @Autowired
    private WayBillReository wayBillReository;
    @Override
    public void create(String wayBillIds) {
        String[] wayBillIdsArray = wayBillIds.split(",");
        for (String wayBillIdStr : wayBillIdsArray) {
            Integer wayBillId = Integer.parseInt(wayBillIdStr);
            WayBill wayBill = wayBillReository.findOne(wayBillId);
            if(wayBill.getSignStatus()==1){
                //待发货
                //生成transitInfo信息
                TransitInfo transitInfo = new TransitInfo();
                transitInfo.setWayBill(wayBill);
                transitInfo.setStatus("出入库中转");
                //保存，由于是自己创建，没有其他瞬时态
                transitRepository.save(transitInfo);

                //更改运单状态
                wayBill.setSignStatus(2);//派送中
                //更新索引库
                wayBillIndexRepository.save(wayBill);
            }else {
                throw new RuntimeException("运单已发货，无需重复配送");
            }
        }

    }

    @Override
    public Page<TransitInfo> pageQuery(Pageable pageable) {

        return transitRepository.findAll(pageable);
    }
}
